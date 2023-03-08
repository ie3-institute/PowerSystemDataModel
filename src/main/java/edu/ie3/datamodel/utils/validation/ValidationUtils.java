/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.*;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.GridContainer;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.thermal.ThermalUnitInput;
import edu.ie3.datamodel.utils.ExceptionUtils;
import edu.ie3.datamodel.utils.options.Failure;
import edu.ie3.datamodel.utils.options.Success;
import edu.ie3.datamodel.utils.options.Try;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.measure.Quantity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Basic Sanity validation tools for entities */
public class ValidationUtils {
  protected static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class);

  /** Private Constructor as this class is not meant to be instantiated */
  protected ValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Creates a new {@link NotImplementedException}, if there is no check available for the class of
   * the given object
   *
   * @param obj Object, that cannot be checked
   * @return Exception with predefined error string
   */
  protected static NotImplementedException checkNotImplementedException(Object obj) {
    return new NotImplementedException(
        String.format(
            "Cannot validate object of class '%s', as no routine is implemented.",
            obj.getClass().getSimpleName()));
  }

  /**
   * This is a "distribution" method, that forwards the check request to specific implementations to
   * fulfill the checking task, based on the class of the given object.
   *
   * @param obj Object to check
   * @return a try object either containing a {@link ValidationException} or an empty Success
   */
  public static Try<Void, ValidationException> check(Object obj) {
    try {
      checkNonNull(obj, "an object");
    } catch (InvalidEntityException e) {
      return new Failure<>(
          new InvalidEntityException(
              "Validation not possible because received object {" + obj + "} was null", e));
    }

    Try<Void, ? extends ValidationException> check;

    if (AssetInput.class.isAssignableFrom(obj.getClass())) {
      check = checkAsset((AssetInput) obj);
    } else if (GridContainer.class.isAssignableFrom(obj.getClass())) {
      check = GridContainerValidationUtils.check((GridContainer) obj);
    } else if (GraphicInput.class.isAssignableFrom(obj.getClass())) {
      check = GraphicValidationUtils.check((GraphicInput) obj);
    } else if (AssetTypeInput.class.isAssignableFrom(obj.getClass())) {
      check = checkAssetType((AssetTypeInput) obj);
    } else {
      check =
          new Failure<>(
              new InvalidEntityException(
                  "Validation failed due to: ", checkNotImplementedException(obj)));
    }

    if (check.isFailure()) {
      return new Failure<>(
          new FailedValidationException("Validation failed due to: ", check.getException()));
    } else {
      return Success.empty();
    }
  }

  /**
   * Validates an asset if: <br>
   * - it is not null <br>
   * - its id is not null <br>
   * - its operation time is not null <br>
   * - in case operation time is limited, start time is before end time <br>
   * A "distribution" method, that forwards the check request to specific implementations to fulfill
   * the checking task, based on the class of the given object.
   *
   * @param assetInput AssetInput to check
   * @return a try object either containing a {@link ValidationException} or an empty Success
   */
  private static Try<Void, ValidationException> checkAsset(AssetInput assetInput) {
    try {
      checkNonNull(assetInput, "an asset");
    } catch (InvalidEntityException e) {
      return new Failure<>(
          new InvalidEntityException(
              "Validation not possible because received object {" + assetInput + "} was null", e));
    }

    List<ValidationException> exceptions = new ArrayList<>();

    if (assetInput.getId() == null) {
      exceptions.add(new InvalidEntityException("No ID assigned", assetInput));
    }
    if (assetInput.getOperationTime() == null) {
      exceptions.add(
          new InvalidEntityException("Operation time of the asset is not defined", assetInput));
    }
    // Check if start time and end time are not null and start time is before end time
    if (assetInput.getOperationTime().isLimited()) {
      assetInput
          .getOperationTime()
          .getEndDate()
          .ifPresent(
              endDate ->
                  assetInput
                      .getOperationTime()
                      .getStartDate()
                      .ifPresent(
                          startDate -> {
                            if (endDate.isBefore(startDate))
                              exceptions.add(
                                  new InvalidEntityException(
                                      "Operation start time of the asset has to be before end time",
                                      assetInput));
                          }));
    }

    Try<Void, ? extends ValidationException> check;

    // Further checks for subclasses
    if (NodeInput.class.isAssignableFrom(assetInput.getClass()))
      check = NodeValidationUtils.check((NodeInput) assetInput);
    else if (ConnectorInput.class.isAssignableFrom(assetInput.getClass()))
      check = ConnectorValidationUtils.check((ConnectorInput) assetInput);
    else if (MeasurementUnitInput.class.isAssignableFrom(assetInput.getClass()))
      check = MeasurementUnitValidationUtils.check((MeasurementUnitInput) assetInput);
    else if (SystemParticipantInput.class.isAssignableFrom(assetInput.getClass()))
      check = SystemParticipantValidationUtils.check((SystemParticipantInput) assetInput);
    else if (ThermalUnitInput.class.isAssignableFrom(assetInput.getClass()))
      check = ThermalUnitValidationUtils.check((ThermalUnitInput) assetInput);
    else {
      check =
          new Failure<>(
              new InvalidEntityException(
                  "Validation failed due to: ", checkNotImplementedException(assetInput)));
    }

    if (check.isFailure()) {
      exceptions.add(check.getException());
    }

    if (exceptions.size() > 0) {
      return new Failure<>(
          new FailedValidationException(
              "Validation failed due to the following exception(s): ",
              new Throwable(ExceptionUtils.getMessages(exceptions))));
    } else {
      return Success.empty();
    }
  }

  /**
   * Validates an asset type if: <br>
   * - it is not null <br>
   * A "distribution" method, that forwards the check request to specific implementations to fulfill
   * the checking task, based on the class of the given object.
   *
   * @param assetTypeInput AssetTypeInput to check
   * @return a try object either containing a {@link InvalidEntityException} or an empty Success
   */
  private static Try<Void, InvalidEntityException> checkAssetType(AssetTypeInput assetTypeInput) {
    try {
      checkNonNull(assetTypeInput, "an asset type");
    } catch (InvalidEntityException e) {
      return new Failure<>(
          new InvalidEntityException(
              "Validation not possible because received object {" + assetTypeInput + "} was null",
              e));
    }

    List<InvalidEntityException> exceptions = new ArrayList<>();

    if (assetTypeInput.getUuid() == null)
      exceptions.add(new InvalidEntityException("No UUID assigned", assetTypeInput));
    if (assetTypeInput.getId() == null)
      exceptions.add(new InvalidEntityException("No ID assigned", assetTypeInput));

    Try<Void, InvalidEntityException> check;

    // Further checks for subclasses
    if (LineTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
      check = ConnectorValidationUtils.checkLineType((LineTypeInput) assetTypeInput);
    else if (Transformer2WTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
      check =
          ConnectorValidationUtils.checkTransformer2WType((Transformer2WTypeInput) assetTypeInput);
    else if (Transformer3WTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
      check =
          ConnectorValidationUtils.checkTransformer3WType((Transformer3WTypeInput) assetTypeInput);
    else if (SystemParticipantTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
      check =
          SystemParticipantValidationUtils.checkType((SystemParticipantTypeInput) assetTypeInput);
    else {
      check =
          new Failure<>(
              new InvalidEntityException(
                  "Validation failed due to: ", checkNotImplementedException(assetTypeInput)));
    }

    if (check.isFailure()) {
      exceptions.add(check.getException());
    }

    if (exceptions.size() > 0) {
      return new Failure<>(
          new InvalidEntityException(
              "Validation failed due to the following exception(s): ",
              new Throwable(ExceptionUtils.getMessages(exceptions))));
    } else {
      return Success.empty();
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
      throw new InvalidEntityException(
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
    Predicate<Quantity<?>> predicate = quantity -> quantity.getValue().doubleValue() < 0d;
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
    Predicate<Quantity<?>> predicate = quantity -> quantity.getValue().doubleValue() <= 0d;
    detectMalformedQuantities(
        quantities, entity, predicate, "The following quantities have to be positive");
  }
  /**
   * Goes through the provided quantities and reports those, that have positive value via
   *
   * @param quantities Array of quantities to check
   * @param entity Unique entity holding the malformed quantities
   */
  protected static void detectPositiveQuantities(Quantity<?>[] quantities, UniqueEntity entity) {
    Predicate<Quantity<?>> predicate = quantity -> quantity.getValue().doubleValue() > 0d;
    detectMalformedQuantities(
        quantities, entity, predicate, "The following quantities have to be negative");
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
      throw new InvalidEntityException(msg + ": " + malformedQuantities, entity);
    }
  }

  /**
   * Determines if the provided set only contains elements with distinct UUIDs
   *
   * @param entities the set that should be checked
   * @return true if all UUIDs of the provided entities are unique, false otherwise
   */
  private static boolean distinctUuids(Set<? extends UniqueEntity> entities) {
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
  protected static Optional<String> checkForDuplicateUuids(Set<UniqueEntity> entities) {
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
