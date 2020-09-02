/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.*;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.container.GridContainer;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.input.thermal.ThermalUnitInput;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.measure.Quantity;

/** Basic Sanity validation tools for entities */
public class ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  protected ValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * This is a "distribution" method, that forwards the check request to specific implementations to
   * fulfill the checking task, based on the class of the given object. If an not yet know class is
   * handed in, a {@link ValidationException} is thrown.
   *
   * <p>TODO @ Niklas: Fill with other method calls, as collection of validation utils increases
   *
   * @param obj Object to check
   */
  public static void check(Object obj) {
    if (GridContainer.class.isAssignableFrom(obj.getClass())) {
      GridContainerValidationUtils.check((GridContainer) obj);
    } else if (NodeInput.class.isAssignableFrom(obj.getClass())) {
      NodeValidationUtils.check((NodeInput) obj);
    } else if (ConnectorInput.class.isAssignableFrom(obj.getClass())) {
      ConnectorValidationUtils.check((ConnectorInput) obj);
    } else if (MeasurementUnitInput.class.isAssignableFrom(obj.getClass())) {
      MeasurementUnitValidationUtils.check((MeasurementUnitInput) obj);
    } else if (GraphicInput.class.isAssignableFrom(obj.getClass())) {
      GraphicValidationUtils.check((GraphicInput) obj);
    } else if (SystemParticipantInput.class.isAssignableFrom(obj.getClass())) {
      SystemParticipantValidationUtils.check((SystemParticipantInput) obj);
    } else if (ThermalUnitInput.class.isAssignableFrom(obj.getClass())) {
      ThermalUnitValidationUtils.check((ThermalUnitInput) obj);
      //TODO NSteffan: Missing objects to check? -> OperatorInput, ...TypeInputs?
    } else {
      throw new ValidationException(
          "Cannot validate object of class '"
              + obj.getClass().getSimpleName()
              + "', as no routine is implemented.");
    }
  }

  /**
   * Checks, if the given object is null. If so, an {@link InvalidEntityException} is thrown.
   *
   * @param obj Object to check
   * @param expectedDescription Further description, of what has been expected.
   */
  protected static void checkNonNull(Object obj, String expectedDescription) {
    if (obj == null)
      throw new ValidationException(
          "Expected " + expectedDescription + ", but got nothing. :-(", new NullPointerException());
  }

  /**
   * Goes through the provided quantities and reports those, that have negative value via synoptic
   * {@link UnsafeEntityException}
   *
   * @param quantities Array of quantities to check
   * @param entity Unique entity holding the malformed quantities
   */
  protected static void detectNegativeQuantities(Quantity<?>[] quantities, UniqueEntity entity) {
    Predicate<Quantity<?>> predicate = quantity -> quantity.getValue().doubleValue() < 0;
    detectMalformedQuantities(
        quantities, entity, predicate, "The following quantities have to be zero or positive");
  }

  /**
   * Goes through the provided quantities and reports those, that are zero or have negative value
   * via synoptic {@link UnsafeEntityException}
   *
   * @param quantities Array of quantities to check
   * @param entity Unique entity holding the malformed quantities
   */
  protected static void detectZeroOrNegativeQuantities(
      Quantity<?>[] quantities, UniqueEntity entity) {
    Predicate<Quantity<?>> predicate = quantity -> quantity.getValue().doubleValue() <= 0;
    detectMalformedQuantities(
        quantities, entity, predicate, "The following quantities have to be positive");
  }

  /**
   * Goes through the provided quantities and reports those, that do fulfill the given predicate via
   * synoptic {@link UnsafeEntityException}
   *
   * @param quantities Array of quantities to check
   * @param entity Unique entity holding the malformed quantities
   * @param predicate Predicate to detect the malformed quantities
   * @param msg Message prefix to use for the exception message: [msg]: [malformedQuantities]
   */
  protected static void detectMalformedQuantities(
      Quantity<?>[] quantities, UniqueEntity entity, Predicate<Quantity<?>> predicate, String msg) {
    String malformedQuantities =
        Arrays.stream(quantities)
            .filter(predicate)
            .map(Quantity::toString)
            .collect(Collectors.joining(", "));
    if (!malformedQuantities.isEmpty()) {
      throw new UnsafeEntityException(msg + ": " + malformedQuantities, entity);
    }
  }

  /**
   * Determines if the provided set only contains elements with distinct UUIDs
   *
   * @param entities the set that should be checked
   * @return true if all UUIDs of the provided entities are unique, false otherwise
   */
  public static boolean distinctUuids(Set<? extends UniqueEntity> entities) {
    return entities.stream()
            .filter(distinctByKey(UniqueEntity::getUuid))
            .collect(Collectors.toSet())
            .size()
        == entities.size();
  }

  /**
   * Predicate that can be used to filter elements based on a given Function
   *
   * @param keyExtractor the function that should be used for the filter operations
   * @param <T> the type of the returning predicate
   * @return the filter predicate that filters based on the provided function
   */
  public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  /**
   * Checks if the provided set of unique entities only contains elements with distinct UUIDs and
   * either returns a string with duplicated UUIDs or an empty optional otherwise.
   *
   * @param entities the entities that should be checkd for UUID uniqueness
   * @return either a string wrapped in an optional with duplicate UUIDs or an empty optional
   */
  public static Optional<String> checkForDuplicateUuids(Set<UniqueEntity> entities) {
    if (distinctUuids(entities)) {
      return Optional.empty();
    }
    String duplicationsString =
        entities.stream()
            .collect(Collectors.groupingBy(UniqueEntity::getUuid, Collectors.counting()))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1)
            .map(
                entry -> {
                  String duplicateEntitiesString =
                      entities.stream()
                          .filter(entity -> entity.getUuid().equals(entry.getKey()))
                          .map(UniqueEntity::toString)
                          .collect(Collectors.joining("\n - "));

                  return entry.getKey()
                      + ": "
                      + entry.getValue()
                      + "\n - "
                      + duplicateEntitiesString;
                })
            .collect(Collectors.joining("\n\n"));

    return Optional.of(duplicationsString);
  }
}
