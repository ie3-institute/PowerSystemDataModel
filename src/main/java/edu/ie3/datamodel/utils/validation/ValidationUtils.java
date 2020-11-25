/*
 * © 2020. TU Dortmund University,
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
   * fulfill the checking task, based on the class of the given object. If an unknown class is
   * handed in, a {@link ValidationException} is thrown.
   *
   * @param obj Object to check
   */
  public static void check(Object obj) {
    checkNonNull(obj, "an object");
    if (AssetInput.class.isAssignableFrom(obj.getClass())) checkAsset((AssetInput) obj);
    else if (GridContainer.class.isAssignableFrom(obj.getClass()))
      GridContainerValidationUtils.check((GridContainer) obj);
    else if (GraphicInput.class.isAssignableFrom(obj.getClass()))
      GraphicValidationUtils.check((GraphicInput) obj);
    else if (AssetTypeInput.class.isAssignableFrom(obj.getClass()))
      checkAssetType((AssetTypeInput) obj);
    else {
      throw new ValidationException(
          "Cannot validate object of class '"
              + obj.getClass().getSimpleName()
              + "', as no routine is implemented.");
    }
  }

  /**
   * Validates an asset if: <br>
   * - it is not null <br>
   * - its id is not null <br>
   * - its operation time is not null <br>
   * - in case operation time is limited, its start time and end time are not null and start time is
   * before end time <br>
   * A "distribution" method, that forwards the check request to specific implementations to fulfill
   * the checking task, based on the class of the given object. If an unknown class is handed in, a
   * {@link ValidationException} is thrown.
   *
   * @param assetInput AssetInput to check
   */
  public static void checkAsset(AssetInput assetInput) {
    // Check if asset is not null
    checkNonNull(assetInput, "an asset");
    // Check if ID is not null
    if (assetInput.getId() == null) throw new InvalidEntityException("No ID assigned", assetInput);
    // Check if operation time is not null
    if (assetInput.getOperationTime() == null)
      throw new InvalidEntityException("Operation time of the asset is not defined", assetInput);
    // Check if start time and end time are not null and start time is before end time
    if (assetInput.getOperationTime().isLimited()) {
      if (assetInput.getOperationTime().getEndDate().isPresent()
          && assetInput.getOperationTime().getStartDate().isPresent()) {
        if (assetInput
            .getOperationTime()
            .getEndDate()
            .get()
            .isBefore(assetInput.getOperationTime().getStartDate().get()))
          throw new InvalidEntityException(
              "Operation start time of the asset has to be before end time", assetInput);
      } else
        throw new InvalidEntityException(
            "Start and/or end time of operation time is null, although operation should be limited",
            assetInput);
    }

    // Further checks for subclasses
    if (NodeInput.class.isAssignableFrom(assetInput.getClass()))
      NodeValidationUtils.check((NodeInput) assetInput);
    else if (ConnectorInput.class.isAssignableFrom(assetInput.getClass()))
      ConnectorValidationUtils.check((ConnectorInput) assetInput);
    else if (MeasurementUnitInput.class.isAssignableFrom(assetInput.getClass()))
      MeasurementUnitValidationUtils.check((MeasurementUnitInput) assetInput);
    else if (SystemParticipantInput.class.isAssignableFrom(assetInput.getClass()))
      SystemParticipantValidationUtils.check((SystemParticipantInput) assetInput);
    else if (ThermalUnitInput.class.isAssignableFrom(assetInput.getClass()))
      ThermalUnitValidationUtils.check((ThermalUnitInput) assetInput);
    else {
      throw new ValidationException(
          "Cannot validate object of class '"
              + assetInput.getClass().getSimpleName()
              + "', as no routine is implemented.");
    }
  }

  /**
   * Validates an asset type if: <br>
   * - it is not null <br>
   * A "distribution" method, that forwards the check request to specific implementations to fulfill
   * the checking task, based on the class of the given object. If an unknown class is handed in, a
   * {@link ValidationException} is thrown.
   *
   * @param assetTypeInput AssetTypeInput to check
   */
  public static void checkAssetType(AssetTypeInput assetTypeInput) {
    // Check if asset type is null
    checkNonNull(assetTypeInput, "an asset type");
    // Check if UUID is not null
    if (assetTypeInput.getUuid() == null)
      throw new InvalidEntityException("No UUID assigned", assetTypeInput);
    // Check if ID is not null
    if (assetTypeInput.getId() == null)
      throw new InvalidEntityException("No ID assigned", assetTypeInput);

    // Further checks for subclasses
    if (LineTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
      ConnectorValidationUtils.checkLineType((LineTypeInput) assetTypeInput);
    else if (Transformer2WTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
      ConnectorValidationUtils.checkTransformer2WType((Transformer2WTypeInput) assetTypeInput);
    else if (Transformer3WTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
      ConnectorValidationUtils.checkTransformer3WType((Transformer3WTypeInput) assetTypeInput);
    else if (SystemParticipantTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
      if (BmTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
        SystemParticipantValidationUtils.checkBmType((BmTypeInput) assetTypeInput);
      else if (ChpTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
        SystemParticipantValidationUtils.checkChpType((ChpTypeInput) assetTypeInput);
      else if (EvTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
        SystemParticipantValidationUtils.checkEvType((EvTypeInput) assetTypeInput);
      else if (HpTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
        SystemParticipantValidationUtils.checkHpType((HpTypeInput) assetTypeInput);
      else if (StorageTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
        SystemParticipantValidationUtils.checkStorageType((StorageTypeInput) assetTypeInput);
      else if (WecTypeInput.class.isAssignableFrom(assetTypeInput.getClass()))
        SystemParticipantValidationUtils.checkWecType((WecTypeInput) assetTypeInput);
      else {
        throw new ValidationException(
            "Cannot validate object of class '"
                + assetTypeInput.getClass().getSimpleName()
                + "', as no routine is implemented.");
      }
    else {
      throw new ValidationException(
          "Cannot validate object of class '"
              + assetTypeInput.getClass().getSimpleName()
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
