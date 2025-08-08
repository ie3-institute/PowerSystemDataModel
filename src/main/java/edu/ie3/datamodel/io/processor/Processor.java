/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.io.factory.input.NodeInputFactory;
import edu.ie3.datamodel.io.processor.result.ResultEntityProcessor;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicInput;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.result.CongestionResult;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Failure;
import edu.ie3.datamodel.utils.Try.Success;
import edu.ie3.util.TimeUtil;
import edu.ie3.util.exceptions.QuantityException;
import java.beans.Introspector;
import java.lang.reflect.InvocationTargetException;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import javax.measure.Quantity;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Basic sketch and skeleton for a processors including all functions that apply for all needed
 * subtypes of processors
 *
 * @param <T> Type parameter of the class to handle
 */
public abstract class Processor<T> {
  protected static final Logger logger = LoggerFactory.getLogger(Processor.class);

  protected final Class<? extends T> registeredClass;

  /* Quantities associated to those fields must be treated differently (e.g. input and result), all other quantity /
   * field combinations can be treated on a common basis and therefore need no further distinction */
  private static final Set<String> specificQuantityFieldNames =
      Set.of(
          "eConsAnnual",
          "energy",
          "eStorage",
          "q",
          "p",
          "pMax",
          "pOwn",
          "pThermal",
          "pRef",
          "pMin");

  private static final GeoJsonWriter geoJsonWriter = new GeoJsonWriter();

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
  protected Processor(Class<? extends T> foreSeenClass) throws EntityProcessorException {
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
      if (a.equalsIgnoreCase(UniqueEntity.UUID_FIELD_NAME)) return -1;
      else if (b.equalsIgnoreCase(UniqueEntity.UUID_FIELD_NAME)) return 1;
      else return a.compareTo(b);
    }
  }

  /**
   * Maps the foreseen table fields to the objects getters
   *
   * @param cls class to use for mapping
   * @return a map of all field values of the class
   */
  protected SortedMap<String, GetterMethod> mapFieldNameToGetter(Class<?> cls)
      throws EntityProcessorException {
    return mapFieldNameToGetter(cls, Collections.emptyList());
  }

  /**
   * Maps the foreseen table fields to the objects getters and ignores the specified fields
   *
   * @param cls class to use for mapping
   * @param ignoreFields A collection of all field names to ignore during process
   * @return a map of all field values of the class
   */
  protected SortedMap<String, GetterMethod> mapFieldNameToGetter(
      Class<?> cls, Collection<String> ignoreFields) throws EntityProcessorException {
    try {
      final LinkedHashMap<String, GetterMethod> resFieldNameToMethod = new LinkedHashMap<>();
      Arrays.stream(Introspector.getBeanInfo(cls, Object.class).getPropertyDescriptors())
          // filter out properties with setters only
          .filter(pd -> Objects.nonNull(pd.getReadMethod()))
          .filter(pd -> !ignoreFields.contains(pd.getName()))
          .filter(
              pd ->
                  // switches can never be parallel but have this field due to inheritance -> filter
                  // it out as it cannot be passed into the constructor
                  !(registeredClass.equals(SwitchInput.class)
                      && pd.getName().equalsIgnoreCase(PARALLEL_DEVICES)))
          .forEach(
              pd -> {
                String fieldName = pd.getName();
                GetterMethod method = new GetterMethod(pd.getReadMethod());

                // OperationTime needs to be replaced by operatesFrom and operatesUntil
                if (fieldName.equalsIgnoreCase(OPERATION_TIME_FIELD_NAME)) {
                  fieldName = OPERATES_FROM;
                  resFieldNameToMethod.put(OPERATES_UNTIL, method);
                }

                // VoltageLevel needs to be replaced by id and nominalVoltage
                if (fieldName.equalsIgnoreCase(VOLT_LVL_FIELD_NAME)) {
                  fieldName = V_RATED;
                  resFieldNameToMethod.put(VOLT_LVL, method);
                }

                resFieldNameToMethod.put(fieldName, method);
              });

      return putUuidFirst(resFieldNameToMethod);
    } catch (Exception e) {
      throw new EntityProcessorException(
          "Error during EntityProcessor class registration process.", e);
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
      Object object, Map<String, GetterMethod> fieldNameToGetter) throws EntityProcessorException {
    try {
      LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
      for (Map.Entry<String, GetterMethod> entry : fieldNameToGetter.entrySet()) {
        String fieldName = entry.getKey();
        GetterMethod getter = entry.getValue();
        Optional<Object> methodReturnObjectOpt = Optional.ofNullable(getter.invoke(object));

        if (methodReturnObjectOpt.isPresent()) {
          resultMap.put(
              fieldName, processMethodResult(methodReturnObjectOpt.get(), getter, fieldName));
        } else {
          resultMap.put(fieldName, "");
        }
      }
      return resultMap;
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new EntityProcessorException("Processing of object " + object + "failed.", e);
    }
  }

  /**
   * Processes the returned object to String by taking care of different conventions.
   *
   * @param methodReturnObject Return object to process
   * @param method The method, that is invoked
   * @param fieldName Name of the foreseen field
   * @return A String representation of the result
   */
  protected String processMethodResult(
      Object methodReturnObject, GetterMethod method, String fieldName)
      throws EntityProcessorException {

    StringBuilder resultStringBuilder = new StringBuilder();

    switch (method.returnType()) {
      // primitives (Boolean, Character, Byte, Short, Integer, Long, Float, Double, String,
      case "UUID",
          "boolean",
          "int",
          "double",
          "String",
          "DayOfWeek",
          "Season",
          "ChargingPointType",
          "EvcsLocationType" ->
          resultStringBuilder.append(methodReturnObject.toString());
      case "Quantity", "ComparableQuantity" ->
          resultStringBuilder.append(handleQuantity((Quantity<?>) methodReturnObject, fieldName));
      case "Optional" ->
          // only quantity optionals are expected here!
          // if optional and present, unpack value and call this method again, if not present return
          // an empty string as by convention null == missing value == "" when persisting data
          resultStringBuilder.append(
              ((Optional<?>) methodReturnObject)
                  .map(
                      o -> {
                        if (o instanceof Quantity<?> quantity) {
                          return Try.of(
                              () -> handleQuantity(quantity, fieldName),
                              EntityProcessorException.class);
                        } else if (o instanceof UniqueEntity entity) {
                          return Try.of(entity::getUuid, EntityProcessorException.class);
                        } else {
                          return Failure.of(
                              new EntityProcessorException(
                                  "Handling of "
                                      + o.getClass().getSimpleName()
                                      + ".class instance wrapped into Optional is currently not supported by entity processors!"));
                        }
                      })
                  .orElse(Success.of("")) // (in case of empty optional)
                  .getOrThrow());
      case "ZonedDateTime" ->
          resultStringBuilder.append(processZonedDateTime((ZonedDateTime) methodReturnObject));
      case "OperationTime" ->
          resultStringBuilder.append(
              processOperationTime((OperationTime) methodReturnObject, fieldName));
      case "VoltageLevel" ->
          resultStringBuilder.append(
              processVoltageLevel((VoltageLevel) methodReturnObject, fieldName));
      case "Point", "LineString" ->
          resultStringBuilder.append(geoJsonWriter.write((Geometry) methodReturnObject));
      case "LoadProfile", "BdewStandardLoadProfile", "RandomLoadProfile" ->
          resultStringBuilder.append(((LoadProfile) methodReturnObject).getKey());
      case "AssetTypeInput",
          "BmTypeInput",
          "ChpTypeInput",
          "EvTypeInput",
          "HpTypeInput",
          "LineTypeInput",
          "LineInput",
          "NodeInput",
          "StorageTypeInput",
          "SystemParticipantInput",
          "ThermalBusInput",
          "ThermalStorageInput",
          "TimeSeries",
          "Transformer2WTypeInput",
          "Transformer3WTypeInput",
          "WecTypeInput",
          "EmInput" ->
          resultStringBuilder.append(((UniqueEntity) methodReturnObject).getUuid());
      case "OperatorInput" ->
          resultStringBuilder.append(
              ((OperatorInput) methodReturnObject).getId().equalsIgnoreCase("NO_OPERATOR_ASSIGNED")
                  ? ""
                  : ((OperatorInput) methodReturnObject).getUuid());
      case "EvCharacteristicInput",
          "OlmCharacteristicInput",
          "WecCharacteristicInput",
          "CosPhiFixed",
          "CosPhiP",
          "QV",
          "ReactivePowerCharacteristic",
          "CharacteristicInput" ->
          resultStringBuilder.append(((CharacteristicInput<?, ?>) methodReturnObject).serialize());
      case "InputModelType" ->
          resultStringBuilder.append(((CongestionResult.InputModelType) methodReturnObject).type);
      default ->
          throw new EntityProcessorException(
              "Unable to process value for attribute/field '"
                  + fieldName
                  + "' and method return type '"
                  + method.returnType()
                  + "' for method with name '"
                  + method.name()
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
  protected String processVoltageLevel(VoltageLevel voltageLevel, String fieldName)
      throws EntityProcessorException {

    StringBuilder resultStringBuilder = new StringBuilder();
    if (fieldName.equalsIgnoreCase(VOLT_LVL)) resultStringBuilder.append(voltageLevel.getId());

    if (fieldName.equalsIgnoreCase(V_RATED))
      resultStringBuilder.append(handleQuantity(voltageLevel.getNominalVoltage(), fieldName));
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
  protected String handleQuantity(Quantity<?> quantity, String fieldName)
      throws EntityProcessorException {
    Try<String, QuantityException> optQuant;
    if (specificQuantityFieldNames.contains(fieldName)) {
      optQuant = handleProcessorSpecificQuantity(quantity, fieldName);
    } else {
      optQuant = Success.of(quantityValToOptionalString(quantity));
    }

    return optQuant
        .transformF(
            e ->
                new EntityProcessorException(
                    "Unable to process quantity value for attribute '"
                        + fieldName
                        + "' in entity "
                        + getRegisteredClass().getSimpleName()
                        + ".class.",
                    e))
        .getOrThrow();
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
  protected abstract Try<String, QuantityException> handleProcessorSpecificQuantity(
      Quantity<?> quantity, String fieldName);

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
   * @return ISO 8601 conform string representation of the ZonedDateTime
   */
  protected String processZonedDateTime(ZonedDateTime zonedDateTime) {
    return TimeUtil.withDefaults.toString(zonedDateTime);
  }

  /**
   * Converts a given quantity to String by extracting the value and applying the toString method to
   * it
   *
   * @param quantity Quantity to convert
   * @return A string of the quantity's value
   */
  protected String quantityValToOptionalString(Quantity<?> quantity) {
    return Double.toString(quantity.getValue().doubleValue());
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
