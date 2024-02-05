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
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.*;
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
   * Logs a warning, if there is no check available for the class of the given object.
   *
   * @param obj object, that cannot be checked
   */
  protected static void logNotImplemented(Object obj) {
    logger.warn(
        "Cannot validate object of class '{}', as no routine is implemented.",
        obj.getClass().getSimpleName());
  }

  /**
   * This is a "distribution" method, that forwards the check request to specific implementations to
   * fulfill the checking task, based on the class of the given object.
   *
   * @param obj Object to check
   */
  public static void check(Object obj) throws ValidationException {
    checkNonNull(obj, "an object").getOrThrow();

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    if (AssetInput.class.isAssignableFrom(obj.getClass())) {
      exceptions.addAll(checkAsset((AssetInput) obj));
    } else if (GridContainer.class.isAssignableFrom(obj.getClass())) {
      exceptions.addAll(GridContainerValidationUtils.check((GridContainer) obj));
    } else if (GraphicInput.class.isAssignableFrom(obj.getClass())) {
      exceptions.addAll(GraphicValidationUtils.check((GraphicInput) obj));
    } else if (AssetTypeInput.class.isAssignableFrom(obj.getClass())) {
      exceptions.addAll(checkAssetType((AssetTypeInput) obj));
    } else {
      logNotImplemented(obj);
    }

    List<? extends ValidationException> list =
        exceptions.stream()
            .filter(Try::isFailure)
            .map(t -> ((Failure<?, ? extends ValidationException>) t).get())
            .toList();

    Try.ofVoid(!list.isEmpty(), () -> new FailedValidationException(list)).getOrThrow();
  }

  /**
   * Validates an asset if:
   *
   * <ul>
   *   <li>it is not null
   *   <li>its id is not null
   *   <li>its operation time is not null
   *   <li>in case operation time is limited, start time is before end time
   * </ul>
   *
   * A "distribution" method, that forwards the check request to specific implementations to fulfill
   * the checking task, based on the class of the given object.
   *
   * @param assetInput AssetInput to check
   * @return a list of try objects either containing a {@link ValidationException} or an empty
   *     Success
   */
  private static List<Try<Void, ? extends ValidationException>> checkAsset(AssetInput assetInput) {
    Try<Void, InvalidEntityException> isNull = checkNonNull(assetInput, "an asset");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    exceptions.add(
        Try.ofVoid(
            assetInput.getId() == null,
            () -> new InvalidEntityException("No ID assigned", assetInput)));

    if (assetInput.getOperationTime() == null) {
      exceptions.add(
          Failure.ofVoid(
              new InvalidEntityException(
                  "Operation time of the asset is not defined", assetInput)));
    } else {
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
                                    new Failure<>(
                                        new InvalidEntityException(
                                            "Operation start time of the asset has to be before end time",
                                            assetInput)));
                            }));
      }
    }

    // Further checks for subclasses
    if (NodeInput.class.isAssignableFrom(assetInput.getClass()))
      exceptions.addAll(NodeValidationUtils.check((NodeInput) assetInput));
    else if (ConnectorInput.class.isAssignableFrom(assetInput.getClass()))
      exceptions.addAll(ConnectorValidationUtils.check((ConnectorInput) assetInput));
    else if (MeasurementUnitInput.class.isAssignableFrom(assetInput.getClass()))
      exceptions.add(MeasurementUnitValidationUtils.check((MeasurementUnitInput) assetInput));
    else if (SystemParticipantInput.class.isAssignableFrom(assetInput.getClass()))
      exceptions.addAll(
          SystemParticipantValidationUtils.check((SystemParticipantInput) assetInput));
    else if (ThermalUnitInput.class.isAssignableFrom(assetInput.getClass()))
      exceptions.addAll(ThermalUnitValidationUtils.check((ThermalUnitInput) assetInput));
    else {
      logNotImplemented(assetInput);
    }

    return exceptions;
  }

  /**
   * Validates an asset type if:
   *
   * <ul>
   *   <li>it is not null
   * </ul>
   *
   * A "distribution" method, that forwards the check request to specific implementations to fulfill
   * the checking task, based on the class of the given object.
   *
   * @param assetTypeInput AssetTypeInput to check
   * @return a list of try objects either containing a {@link ValidationException} or an empty
   *     Success
   */
  private static List<Try<Void, ? extends ValidationException>> checkAssetType(
      AssetTypeInput assetTypeInput) {
    Try<Void, InvalidEntityException> isNull = checkNonNull(assetTypeInput, "an asset type");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    exceptions.add(
        Try.ofVoid(
            assetTypeInput.getUuid() == null,
            () -> new InvalidEntityException("No UUID assigned", assetTypeInput)));
    exceptions.add(
        Try.ofVoid(
            assetTypeInput.getId() == null,
            () -> new InvalidEntityException("No ID assigned", assetTypeInput)));

    // Further checks for subclasses
    if (LineTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
      exceptions.addAll(ConnectorValidationUtils.checkLineType((LineTypeInput) assetTypeInput));
    else if (Transformer2WTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
      exceptions.addAll(
          ConnectorValidationUtils.checkTransformer2WType((Transformer2WTypeInput) assetTypeInput));
    else if (Transformer3WTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
      exceptions.addAll(
          ConnectorValidationUtils.checkTransformer3WType((Transformer3WTypeInput) assetTypeInput));
    else if (SystemParticipantTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
      exceptions.addAll(
          SystemParticipantValidationUtils.checkType((SystemParticipantTypeInput) assetTypeInput));
    else {
      logNotImplemented(assetTypeInput);
    }

    return exceptions;
  }

  /**
   * Checks, if the given object is null. If so, an {@link InvalidEntityException} wrapped in a
   * {@link Failure} is returned.
   *
   * @param obj Object to check
   * @param expectedDescription Further description, of what has been expected.
   * @return either an {@link InvalidEntityException} wrapped in a {@link Failure} or an empty
   *     {@link Success}
   */
  protected static Try<Void, InvalidEntityException> checkNonNull(
      Object obj, String expectedDescription) {
    return Try.ofVoid(
        obj == null,
        () ->
            new InvalidEntityException(
                "Validation not possible because received object was null. Expected "
                    + expectedDescription
                    + ", but got nothing. :-(",
                new NullPointerException()));
  }

  /**
   * Goes through the provided quantities and reports those, that have negative value via synoptic
   * {@link UnsafeEntityException}
   *
   * @param quantities Array of quantities to check
   * @param entity Unique entity holding the malformed quantities
   */
  protected static void detectNegativeQuantities(Quantity<?>[] quantities, UniqueEntity entity)
      throws InvalidEntityException {
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
      Quantity<?>[] quantities, UniqueEntity entity) throws InvalidEntityException {
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
  protected static void detectPositiveQuantities(Quantity<?>[] quantities, UniqueEntity entity)
      throws InvalidEntityException {
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
      Quantity<?>[] quantities, UniqueEntity entity, Predicate<Quantity<?>> predicate, String msg)
      throws InvalidEntityException {
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
   * Method to check for duplicate fields in a set of {@link UniqueEntity}.
   *
   * @param entities to be checked
   * @param supplier for the field
   * @return a list of {@link Try}.
   * @param <E> type of the {@link UniqueEntity}
   * @param <F> type of the field
   */
  protected static <E extends UniqueEntity, F>
      List<Try<Void, DuplicateEntitiesException>> checkForDuplicates(
          Collection<E> entities, FieldSupplier<E, F> supplier) {
    Map<F, List<E>> duplicates =
        entities.stream().collect(Collectors.groupingBy(supplier::getField));

    return duplicates.entrySet().stream()
        .filter(e -> e.getValue().size() > 1)
        .map(
            duplicate ->
                Failure.ofVoid(
                    new DuplicateEntitiesException(
                        duplicate.getKey().getClass().getSimpleName(), duplicate.getValue())))
        .collect(Collectors.toList());
  }

  /**
   * Supplier for unique entity fields that returns a field of type F given an entity of type E.
   *
   * @param <E> type of unique entity
   * @param <F> type of field
   */
  @FunctionalInterface
  protected interface FieldSupplier<E extends UniqueEntity, F> {
    F getField(E entity);
  }
}
