/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.io.factory.input.NodeInputFactory;
import edu.ie3.datamodel.io.processor.result.ResultEntityProcessor;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardLoadProfile;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.StorageStrategy;
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.measure.Quantity;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Basic sketch and skeleton for a processors including all functions that apply for all needed
 * subtypes of processors
 *
 * @param <T> Type parameter of the class to handle
 */
public abstract class Processor<T> {
  protected static final Logger logger = LoggerFactory.getLogger(Processor.class);

  protected static UUID emptyUuid = UUID.fromString("00000000-0000-0000-0000-000000000000");

  protected final Class<? extends T> registeredClass;

  /* Quantities associated to those fields must be treated differently (e.g. input and result), all other quantity /
   * field combinations can be treated on a common basis and therefore need no further distinction */
  private static final Set<String> specificQuantityFieldNames =
      Collections.unmodifiableSet(
          new HashSet<>(
              Arrays.asList(
                  "eConsAnnual", "energy", "eStorage", "q", "p", "pMax", "pOwn", "pThermal")));

  private static final GeoJsonWriter geoJsonWriter = new GeoJsonWriter();

  private static final String UUID_FIELD_NAME = "uuid";

  private static final String OPERATION_TIME_FIELD_NAME = OperationTime.class.getSimpleName();
  private static final String OPERATES_FROM = "operatesFrom";
  private static final String OPERATES_UNTIL = "operatesUntil";

  private static final String VOLT_LVL_FIELD_NAME = "voltLvl";
  private static final String VOLT_LVL = NodeInputFactory.VOLT_LVL;
  private static final String V_RATED = NodeInputFactory.V_RATED;

  private static final String PARALLEL_DEVICES = "parallelDevices";

  /**
   * Instantiates a Processor for a foreseen class
   *
   * @param foreSeenClass Class and its children that are foreseen to be handled with this processor
   */
  protected Processor(Class<? extends T> foreSeenClass) {
    if (!getEligibleEntityClasses().contains(foreSeenClass))
      throw new EntityProcessorException(
          "Cannot register class '"
              + foreSeenClass.getSimpleName()
              + "' with entity processor '"
              + this.getClass().getSimpleName()
              + "'. Eligible classes: "
              + getEligibleEntityClasses().stream()
                  .map(Class::getSimpleName)
                  .collect(Collectors.joining(", ")));

    this.registeredClass = foreSeenClass;
  }

  /**
   * Comparator to sort a Map of field name to getter method, so that the first entry is the uuid
   * and the rest is sorted alphabetically.
   */
  private static class UuidFirstComparator implements Comparator<String> {
    @Override
    public int compare(String a, String b) {
      if (a.equalsIgnoreCase(UUID_FIELD_NAME)) return -1;
      else if (b.equalsIgnoreCase(UUID_FIELD_NAME)) return 1;
      else return a.compareTo(b);
    }
  }

  /**
   * Ensure, that the uuid field is put first. All other fields are sorted alphabetically.
   * Additionally, the map is immutable
   *
   * @param unsorted The unsorted map
   * @param <V> Type of the values in the map
   * @return The sorted map - what a surprise!
   */
  public static <V> SortedMap<String, V> putUuidFirst(Map<String, V> unsorted) {
    SortedMap<String, V> sortedMap = new TreeMap<>(new UuidFirstComparator());
    sortedMap.putAll(unsorted);
    return Collections.unmodifiableSortedMap(sortedMap);
  }

  /**
   * Processes the object to a map from field name to value as String representation
   *
   * @param object The object to process
   * @param fieldNameToGetter Mapping from field name to getter
   * @return Mapping from field name to value as String representation
   */
  protected LinkedHashMap<String, String> processObject(
      Object object, Map<String, Function<Object, Optional<Object>>> fieldNameToGetter) {
    LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
    for (Map.Entry<String, Function<Object, Optional<Object>>> entry : fieldNameToGetter.entrySet()) {
      String fieldName = entry.getKey();
      Function<Object, Optional<Object>> getterFunction = entry.getValue();
      Optional<Object> methodReturnObjectOpt = getterFunction.apply(object);

      if (methodReturnObjectOpt.isPresent()) {
        resultMap.put(
            fieldName, processMethodResult(methodReturnObjectOpt.get(), fieldName));
      } else {
        resultMap.put(fieldName, "");
      }
    }
    return resultMap;
  }

  /**
   * Processes the returned object to String by taking care of different conventions.
   *
   * @param methodReturnObject Return object to process
   * @param fieldName Name of the foreseen field
   * @return A String representation of the result
   */
  protected String processMethodResult(Object methodReturnObject, String fieldName) {
    if(methodReturnObject == null) return "";
    StringBuilder resultStringBuilder = new StringBuilder();
    switch (methodReturnObject.getClass().getSimpleName()) {
      case "UUID":
        resultStringBuilder.append(emptyUuid.equals(methodReturnObject)
                        ? ""
                        : methodReturnObject.toString());
        break;
      // primitives (Boolean, Character, Byte, Short, Integer, Long, Float, Double, String)
      case "boolean":
      case "Boolean":
      case "int":
      case "Integer":
      case "double":
      case "Double":
      case "String":
      case "DayOfWeek":
        resultStringBuilder.append(methodReturnObject.toString());
        break;
        // Quantities
      case "Quantity":
      case "DoubleQuantity":
      case "DecimalQuantity":
      case "NumberQuantity":
      case "ComparableQuantity":
        resultStringBuilder.append(
            handleQuantity((Quantity<?>) methodReturnObject, fieldName)
                .orElseThrow(
                    () ->
                        new EntityProcessorException(
                            "Unable to process quantity value for attribute '"
                                + fieldName
                                + "' in result entity "
                                + getRegisteredClass().getSimpleName()
                                + ".class.")));
        break;
      case "Optional":
        resultStringBuilder.append(processMethodResult(((Optional) methodReturnObject).orElse(null), fieldName));
        break;
      case "ZonedDateTime":
        resultStringBuilder.append(processZonedDateTime((ZonedDateTime) methodReturnObject));
        break;
      case "OperationTime":
        resultStringBuilder.append(
            processOperationTime((OperationTime) methodReturnObject, fieldName));
        break;
      case "VoltageLevel":
        resultStringBuilder.append(
            processVoltageLevel((VoltageLevel) methodReturnObject, fieldName));
        break;
      case "Point":
      case "LineString":
        resultStringBuilder.append(geoJsonWriter.write((Geometry) methodReturnObject));
        break;
      case "StandardLoadProfile":
      case "BdewLoadProfile":
        resultStringBuilder.append(((StandardLoadProfile) methodReturnObject).getKey());
        break;
      case "StorageStrategy":
        resultStringBuilder.append(((StorageStrategy) methodReturnObject).getToken());
        break;
      case "AssetTypeInput":
      case "BmTypeInput":
      case "ChpTypeInput":
      case "EvTypeInput":
      case "HpTypeInput":
      case "LineTypeInput":
      case "LineInput":
      case "NodeInput":
      case "StorageTypeInput":
      case "SystemParticipantInput":
      case "ThermalBusInput":
      case "ThermalStorageInput":
      case "TimeSeries":
      case "Transformer2WTypeInput":
      case "Transformer3WTypeInput":
      case "WecTypeInput":
        resultStringBuilder.append(((UniqueEntity) methodReturnObject).getUuid());
        break;
      case "OperatorInput":
        resultStringBuilder.append(
            ((OperatorInput) methodReturnObject).getId().equalsIgnoreCase("NO_OPERATOR_ASSIGNED")
                ? ""
                : ((OperatorInput) methodReturnObject).getUuid());
        break;
      case "EvCharacteristicInput":
      case "OlmCharacteristicInput":
      case "WecCharacteristicInput":
      case "CosPhiFixed":
      case "CosPhiP":
      case "QV":
      case "ReactivePowerCharacteristic":
      case "CharacteristicInput":
        resultStringBuilder.append(((CharacteristicInput<?, ?>) methodReturnObject).deSerialize());
        break;
      default:
        throw new EntityProcessorException(
            "Unable to process value for attribute/field '"
                + fieldName
                + "' and method return type '"
                + methodReturnObject.getClass().getSimpleName()
                + "' in in entity model "
                + getRegisteredClass().getSimpleName()
                + ".class.");
    }

    return resultStringBuilder.toString();
  }

  /**
   * Handling of elements of type {@link VoltageLevel}
   *
   * @param voltageLevel the voltage level that should be processed
   * @param fieldName the field name that should be generated (either v_rated or volt_lvl)
   * @return the resulting string of a VoltageLevel attribute value for the provided field or an
   *     empty string when an invalid field name is provided
   */
  protected String processVoltageLevel(VoltageLevel voltageLevel, String fieldName) {

    StringBuilder resultStringBuilder = new StringBuilder();
    if (fieldName.equalsIgnoreCase(VOLT_LVL)) resultStringBuilder.append(voltageLevel.getId());

    if (fieldName.equalsIgnoreCase(V_RATED))
      resultStringBuilder.append(
          handleQuantity(voltageLevel.getNominalVoltage(), fieldName)
              .orElseThrow(
                  () ->
                      new EntityProcessorException(
                          "Unable to process quantity value for attribute '"
                              + fieldName
                              + "' in result entity "
                              + getRegisteredClass().getSimpleName()
                              + ".class.")));
    return resultStringBuilder.toString();
  }

  /**
   * Standard method to process a Quantity to a String based on a method return object
   *
   * @param quantity the quantity that should be processed
   * @param fieldName the field name the quantity is set to
   * @return an optional string with the normalized to {@link StandardUnits} value of the quantity
   *     or empty if an error occurred during processing
   */
  protected Optional<String> handleQuantity(Quantity<?> quantity, String fieldName) {
    if (specificQuantityFieldNames.contains(fieldName)) {
      return handleProcessorSpecificQuantity(quantity, fieldName);
    } else {
      return quantityValToOptionalString(quantity);
    }
  }

  /**
   * Handling of elements of type {@link OperationTime}
   *
   * @param operationTime the operation time that should be processed
   * @param fieldName the field name that should be generated (either operatesFrom or operatesUntil)
   * @return the resulting string of a OperationTime attribute value for the provided field or an
   *     empty string when an invalid field name is provided
   */
  protected String processOperationTime(OperationTime operationTime, String fieldName) {
    StringBuilder resultStringBuilder = new StringBuilder();

    if (fieldName.equalsIgnoreCase(OPERATES_FROM))
      operationTime
          .getStartDate()
          .ifPresent(startDate -> resultStringBuilder.append(processZonedDateTime(startDate)));

    if (fieldName.equalsIgnoreCase(OPERATES_UNTIL))
      operationTime
          .getEndDate()
          .ifPresent(endDate -> resultStringBuilder.append(processZonedDateTime(endDate)));

    return resultStringBuilder.toString();
  }

  /**
   * Standard method to process a ZonedDateTime to a String based on a method return object NOTE:
   * this method does NOT check if the provided object is of type ZonedDateTime. This has to be done
   * manually BEFORE calling this method!
   *
   * @param zonedDateTime representation of the ZonedDateTime
   * @return string representation of the ZonedDateTime
   */
  protected String processZonedDateTime(ZonedDateTime zonedDateTime) {
    return zonedDateTime.toString();
  }

  /**
   * This method should handle all quantities that are model processor specific e.g. we need to
   * handle active power p different for {@link edu.ie3.datamodel.models.result.ResultEntity}s and
   * {@link edu.ie3.datamodel.models.input.system.SystemParticipantInput}s Hence from the
   * generalized method {@link #handleQuantity(Quantity, String)}, this allows for the specific
   * handling of child implementations. See the implementation @ {@link ResultEntityProcessor} for
   * details.
   *
   * @param quantity the quantity that should be processed
   * @param fieldName the field name the quantity is set to
   * @return an optional string with the normalized to {@link StandardUnits} value of the quantity
   *     or empty if an error occurred during processing
   */
  protected abstract Optional<String> handleProcessorSpecificQuantity(
      Quantity<?> quantity, String fieldName);

  /**
   * Converts a given quantity to String by extracting the value and applying the toString method to
   * it
   *
   * @param quantity Quantity to convert
   * @return A string of the quantity's value
   */
  protected Optional<String> quantityValToOptionalString(Quantity<?> quantity) {
    return Optional.of(Double.toString(quantity.getValue().doubleValue()));
  }

  /**
   * Return all header elements of the table
   *
   * @return all header elements of the table
   */
  public abstract String[] getHeaderElements();

  /**
   * Reveal the registered class
   *
   * @return the registered class
   */
  protected Class<? extends T> getRegisteredClass() {
    return registeredClass;
  }

  /**
   * Returns a (unmodifiable) {@link List} of classes that this Processors is capable of processing
   *
   * @return The unmodifiable {@link List} of eligible classes
   */
  protected abstract List<Class<? extends T>> getEligibleEntityClasses();
}
