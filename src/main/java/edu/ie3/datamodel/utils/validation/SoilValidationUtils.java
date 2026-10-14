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
import edu.ie3.datamodel.models.input.grid.SoilLayer;
import edu.ie3.datamodel.models.input.grid.SoilType;
import edu.ie3.datamodel.utils.Try;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.measure.quantity.Length;
import org.locationtech.jts.geom.Geometry;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

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
                && soilLayer.thickness().getValue().doubleValue() <= 0d,
            () ->
                new InvalidEntityException("Soil layer has a non-positive thickness", soilLayer)));
    return exceptions;
  }

  /**
   * Runs coverage and per-entity soil validations and throws a {@link ValidationException} with
   * aggregated issues when failures are found.
   */
  public static void validateAll(List<SoilLayer> layers, List<SoilType> types)
      throws ValidationException {
    List<Exception> problems = new ArrayList<>();

    // Per-type and per-layer checks
    problems.addAll(collectCheckProblems(types, SoilValidationUtils::check));
    problems.addAll(collectCheckProblems(layers, SoilValidationUtils::check));

    if (layers != null) {
      problems.addAll(validateNonOverlappingPerCoordinate(layers));
      problems.addAll(validateNoGapsPerCoordinate(layers));
    }

    if (!problems.isEmpty()) {
      throw new FailedValidationException(problems);
    }
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
   * Checks that no two soil layers with the same geometry overlap in depth.
   *
   * @param layers the soil layers to check
   * @return the detected overlaps as exceptions; an empty list if none were found
   */
  private static List<Exception> validateNonOverlappingPerCoordinate(List<SoilLayer> layers) {
    List<Exception> problems = new ArrayList<>();

    Map<String, List<SoilLayer>> grouped =
        layers.stream()
            .collect(
                Collectors.groupingBy(
                    l -> geometryKey(l.getGeometry()), Collectors.toUnmodifiableList()));

    for (Map.Entry<String, List<SoilLayer>> e : grouped.entrySet()) {
      List<SoilLayer> group = e.getValue();
      for (int i = 0; i < group.size(); i++) {
        SoilLayer a = group.get(i);
        if (a.getZFrom() == null || a.getZTo() == null) continue;
        SoilLayer.ZInterval aInterval = a.zInterval();
        for (int j = i + 1; j < group.size(); j++) {
          SoilLayer b = group.get(j);
          if (b.getZFrom() == null || b.getZTo() == null) continue;
          SoilLayer.ZInterval bInterval = b.zInterval();

          if (aInterval.overlaps(bInterval)) {
            problems.add(
                new InvalidEntityException(
                    String.format(
                        "Overlapping soil layers detected at coordinate '%s': %s ([%fm,%fm]) vs %s ([%fm,%fm])",
                        e.getKey(),
                        a.getUuid(),
                        aInterval.lower(),
                        aInterval.upper(),
                        b.getUuid(),
                        bInterval.lower(),
                        bInterval.upper()),
                    a));
          }
        }
      }
    }
    return problems;
  }

  /**
   * Checks that the soil layers with the same geometry cover their depth range without gaps.
   *
   * @param layers the soil layers to check
   * @return the detected gaps as exceptions; an empty list if none were found
   */
  private static List<Exception> validateNoGapsPerCoordinate(List<SoilLayer> layers) {
    List<Exception> problems = new ArrayList<>();

    Map<String, List<SoilLayer>> grouped =
        layers.stream()
            .collect(
                Collectors.groupingBy(
                    l -> geometryKey(l.getGeometry()), Collectors.toUnmodifiableList()));

    for (Map.Entry<String, List<SoilLayer>> e : grouped.entrySet()) {
      List<SoilLayer> group =
          e.getValue().stream()
              .filter(l -> l.getZFrom() != null && l.getZTo() != null)
              .sorted(Comparator.comparingDouble(l -> l.zInterval().lower()))
              .toList();

      if (group.isEmpty()) continue;

      for (int i = 1; i < group.size(); i++) {
        SoilLayer cur = group.get(i);
        SoilLayer prev = group.get(i - 1);

        if (cur.zInterval().gapAbove(prev.zInterval())) {
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

  /**
   * Sums the thickness of all soil layers per soil type.
   *
   * @param layers the soil layers to aggregate; may be {@code null}
   * @return a map from soil type uuid to the summed layer thickness in metres; empty if {@code
   *     layers} is {@code null}
   */
  public static Map<UUID, ComparableQuantity<Length>> totalThicknessBySoilType(
      List<SoilLayer> layers) {
    Map<UUID, Double> sums = new HashMap<>();
    if (layers == null) return Map.of();

    for (SoilLayer l : layers) {
      if (l.getSoilType() == null) continue;
      double thickness = l.thickness().to(METRE).getValue().doubleValue();
      sums.merge(l.getSoilType(), thickness, Double::sum);
    }

    Map<UUID, ComparableQuantity<Length>> result = new HashMap<>();
    sums.forEach((k, v) -> result.put(k, Quantities.getQuantity(v, METRE)));
    return result;
  }

  /**
   * Associates each soil layer with the soil type referenced by its {@code soilType} uuid.
   *
   * @param layers the soil layers to associate; may be {@code null}
   * @param types the soil types to look up; may be {@code null}
   * @return a map from soil layer to its soil type; the value is an empty {@link Optional} if the
   *     layer does not reference a soil type or the reference cannot be resolved
   */
  public static Map<SoilLayer, Optional<SoilType>> associateLayersWithTypes(
      List<SoilLayer> layers, List<SoilType> types) {
    Map<UUID, SoilType> typeIndex = new HashMap<>();
    if (types != null) {
      for (SoilType t : types) typeIndex.put(t.getUuid(), t);
    }

    Map<SoilLayer, Optional<SoilType>> mapping = new HashMap<>();
    if (layers == null) return mapping;

    for (SoilLayer l : layers) {
      if (l.getSoilType() == null) {
        mapping.put(l, Optional.empty());
      } else {
        mapping.put(l, Optional.ofNullable(typeIndex.get(l.getSoilType())));
      }
    }

    return mapping;
  }

  /**
   * Derives a stable string key from a geometry to group soil layers by their horizontal footprint.
   *
   * @param g the geometry, may be {@code null}
   * @return a string representation of the geometry used as a grouping key
   */
  private static String geometryKey(Geometry g) {
    if (g == null) return "<null>";
    try {
      return g.toText();
    } catch (Exception ex) {
      return g.getGeometryType() + "@" + System.identityHashCode(g);
    }
  }
}
