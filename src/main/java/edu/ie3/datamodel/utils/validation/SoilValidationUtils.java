/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import static edu.ie3.util.quantities.PowerSystemUnits.METRE;

import edu.ie3.datamodel.exceptions.FailedValidationException;
import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.models.input.SoilLayer;
import edu.ie3.datamodel.models.input.SoilType;
import edu.ie3.datamodel.utils.Try;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Geometry;

/**
 * Utility class holding validation helpers for soil related input entities. This class cannot be
 * instantiated.
 */
public class SoilValidationUtils extends ValidationUtils {
  private SoilValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Checks a soil type for invalid or missing data.
   *
   * @param soilType the soil type to check
   * @return a list of validation results; an entry is a failure if the respective check failed
   */
  public static List<Try<Void, ? extends ValidationException>> check(SoilType soilType) {
    Try<Void, InvalidEntityException> isNull = checkNonNull(soilType, "a soil type");
    if (isNull.isFailure()) return List.of(isNull);

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();
    exceptions.add(
        Try.ofVoid(
            soilType.getId() == null,
            () -> new InvalidEntityException("No ID assigned", soilType)));
    exceptions.add(
        Try.ofVoid(
            soilType.getThermalResistivityWet() == null
                || soilType.getThermalResistivityDry() == null
                || soilType.getSpecificHeatCapacity() == null
                || soilType.getCriticalTemperatureDifference() == null,
            () ->
                new InvalidEntityException(
                    "Soil type has incomplete thermal properties", soilType)));

    if (exceptions.stream().noneMatch(Try::isFailure)) {
      try {
        detectZeroOrNegativeQuantities(
            quantities(
                "thermalResistivityWet", soilType.getThermalResistivityWet(),
                "thermalResistivityDry", soilType.getThermalResistivityDry(),
                "specificHeatCapacity", soilType.getSpecificHeatCapacity()),
            soilType);
      } catch (InvalidEntityException e) {
        exceptions.add(new Try.Failure<>(e));
      }
    }
    return exceptions;
  }

  /**
   * Checks a soil layer for invalid or missing data.
   *
   * @param soilLayer the soil layer to check
   * @return a list of validation results; an entry is a failure if the respective check failed
   */
  public static List<Try<Void, ? extends ValidationException>> check(SoilLayer soilLayer) {
    Try<Void, InvalidEntityException> isNull = checkNonNull(soilLayer, "a soil layer");
    if (isNull.isFailure()) return List.of(isNull);

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();
    exceptions.add(
        Try.ofVoid(
            soilLayer.getGeometry() == null
                || soilLayer.getZFrom() == null
                || soilLayer.getZTo() == null
                || soilLayer.getSoilType() == null,
            () -> new InvalidEntityException("Soil layer has incomplete data", soilLayer)));
    exceptions.add(
        Try.ofVoid(
            soilLayer.getZFrom() != null
                && soilLayer.getZTo() != null
                && soilLayer.getZFrom().to(METRE).getValue().doubleValue()
                    <= soilLayer.getZTo().to(METRE).getValue().doubleValue(),
            () ->
                new InvalidEntityException(
                    "Soil layer zFrom must be greater than zTo", soilLayer)));
    return exceptions;
  }

  /**
   * Runs coverage and per-entity soil validations and returns aggregated issues when failures are
   * found.
   *
   * @return a success if all soil validations pass, or a failure containing aggregated issues
   */
  public static Try<Void, ValidationException> validateAll(
      List<SoilLayer> layers, List<SoilType> types) {
    List<Exception> problems = new ArrayList<>();

    // Per-type and per-layer checks
    problems.addAll(collectCheckProblems(types, SoilValidationUtils::check));
    problems.addAll(collectCheckProblems(layers, SoilValidationUtils::check));

    if (layers != null) {
      problems.addAll(validatePerCoordinate(layers));
    }

    if (!problems.isEmpty()) {
      return Try.Failure.ofVoid(new FailedValidationException(problems));
    }
    return Try.Success.empty();
  }

  /**
   * Collects all validation problems of the given entities into a list.
   *
   * @param <T> the type of entity to check
   * @param entities the entities to check; may be {@code null}
   * @param check a function that returns the validation results for a single entity
   * @return the exceptions of all failed checks
   */
  private static <T> List<Exception> collectCheckProblems(
      List<T> entities, Function<T, List<Try<Void, ? extends ValidationException>>> check) {
    if (entities == null) {
      return List.of();
    }
    List<Exception> problems = new ArrayList<>();
    for (T entity : entities) {
      check.apply(entity).stream()
          .filter(Try::isFailure)
          .forEach(t -> problems.add(((Try.Failure<?, ? extends ValidationException>) t).get()));
    }
    return problems;
  }

  /**
   * Checks that soil layers sharing the same geometry neither overlap nor leave gaps in their depth
   * intervals.
   *
   * @param layers the soil layers to check
   * @return the detected overlaps and gaps as exceptions; an empty list if none were found
   */
  private static List<Exception> validatePerCoordinate(List<SoilLayer> layers) {
    List<Exception> problems = new ArrayList<>();

    Map<Geometry, List<SoilLayer>> grouped =
        layers.stream()
            .filter(l -> l.getGeometry() != null)
            .collect(
                Collectors.groupingBy(SoilLayer::getGeometry, Collectors.toUnmodifiableList()));

    for (Map.Entry<Geometry, List<SoilLayer>> e : grouped.entrySet()) {
      List<SoilLayer> sorted =
          e.getValue().stream()
              .filter(l -> l.getZFrom() != null && l.getZTo() != null)
              .sorted(Comparator.comparingDouble(l -> l.zInterval().lower()))
              .toList();

      if (sorted.isEmpty()) continue;

      for (int i = 1; i < sorted.size(); i++) {
        SoilLayer cur = sorted.get(i);
        SoilLayer prev = sorted.get(i - 1);
        SoilLayer.ZInterval curInterval = cur.zInterval();
        SoilLayer.ZInterval prevInterval = prev.zInterval();

        if (prevInterval.overlaps(curInterval)) {
          problems.add(
              new InvalidEntityException(
                  String.format(
                      "Overlapping soil layers detected at coordinate '%s': %s ([%fm,%fm]) vs %s ([%fm,%fm])",
                      e.getKey(),
                      prev.getUuid(),
                      prevInterval.lower(),
                      prevInterval.upper(),
                      cur.getUuid(),
                      curInterval.lower(),
                      curInterval.upper()),
                  prev));
        } else if (curInterval.gapAbove(prevInterval)) {
          problems.add(
              new InvalidEntityException(
                  String.format(
                      "Gap in soil coverage at coordinate '%s' between layers %s and %s",
                      e.getKey(), prev.getUuid(), cur.getUuid()),
                  cur));
        }
      }
    }
    return problems;
  }
}
