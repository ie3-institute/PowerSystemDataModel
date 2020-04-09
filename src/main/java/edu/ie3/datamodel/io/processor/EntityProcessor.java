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
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.system.StorageStrategy;
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.util.TimeTools;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.geojson.GeoJsonWriter;
import tec.uom.se.ComparableQuantity;

/**
 * Internal API Interface for EntityProcessors. Main purpose is to 'de-serialize' models into a
 * fieldName -> value representation to allow for an easy processing into a database or file sink
 * e.g. .csv
 *
 * @version 0.1
 * @since 31.01.20
 */
public abstract class EntityProcessor<T extends UniqueEntity> {

  public final Logger log = LogManager.getLogger(this.getClass());
  private final Class<? extends T> registeredClass;
  protected final String[] headerElements;
  private final Map<String, Method> fieldNameToMethod;

  private static final String OPERATION_TIME_FIELD_NAME = OperationTime.class.getSimpleName();
  private static final String OPERATES_FROM = "operatesFrom";
  private static final String OPERATES_UNTIL = "operatesUntil";

  private static final String VOLT_LVL_FIELD_NAME = "voltLvl";
  private static final String VOLT_LVL = NodeInputFactory.VOLT_LVL;
  private static final String V_RATED = NodeInputFactory.V_RATED;

  private static final String NODE_INTERNAL = "nodeInternal";
  private static final String PARALLEL_DEVICES = "parallelDevices";

  /* Quantities associated to those fields must be treated differently (e.g. input and result), all other quantity /
   * field combinations can be treated on a common basis and therefore need no further distinction */
  private static final Set<String> specificQuantityFieldNames =
      Collections.unmodifiableSet(
          new HashSet<>(
              Arrays.asList(
                  "eConsAnnual", "energy", "eStorage", "q", "p", "pMax", "pOwn", "pThermal")));

  private static final GeoJsonWriter geoJsonWriter = new GeoJsonWriter();

  /** Field name of {@link UniqueEntity} uuid */
  private static final String UUID_FIELD_NAME = "uuid";

  /**
   * Create a new EntityProcessor
   *
   * @param registeredClass the class the entity processor should be able to handle
   */
  public EntityProcessor(Class<? extends T> registeredClass) {
    this.registeredClass = registeredClass;
    this.fieldNameToMethod = registerClass(registeredClass, getAllEligibleClasses());
    this.headerElements =
        ArrayUtils
            .addAll( // ensures that uuid is always the first entry in the header elements array
                new String[] {UUID_FIELD_NAME},
                fieldNameToMethod.keySet().stream()
                    .filter(x -> !x.toLowerCase().contains(UUID_FIELD_NAME))
                    .toArray(String[]::new));

    TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * Register the class provided in the constructor
   *
   * @param cls class to be registered
   * @return an array of strings of all field values of the registered class
   */
  private Map<String, Method> registerClass(
      Class<? extends T> cls, List<Class<? extends T>> eligibleClasses) {

    final LinkedHashMap<String, Method> resFieldNameToMethod = new LinkedHashMap<>();

    if (!eligibleClasses.contains(cls))
      throw new EntityProcessorException(
          "Cannot register class '"
              + cls.getSimpleName()
              + "' with entity processor '"
              + this.getClass().getSimpleName()
              + "'. Eligible classes: "
              + eligibleClasses.stream()
                  .map(Class::getSimpleName)
                  .collect(Collectors.joining(", ")));
    try {
      Arrays.stream(Introspector.getBeanInfo(cls, Object.class).getPropertyDescriptors())
          // filter out properties with setters only
          .filter(pd -> Objects.nonNull(pd.getReadMethod()))
          .filter(
              pd ->
                  !pd.getName()
                      .equalsIgnoreCase(
                          NODE_INTERNAL)) // filter internal node for 3 winding transformer
          .filter(
              pd ->
                  // switches can never be parallel but have this field due to inheritance -> filter
                  // it out as it cannot be passed into the constructor
                  !(registeredClass.equals(SwitchInput.class)
                      && pd.getName().equalsIgnoreCase(PARALLEL_DEVICES)))
          .forEach(
              pd -> { // invoke method to get value
                if (pd.getReadMethod() != null) {

                  // OperationTime needs to be replaced by operatesFrom and operatesUntil
                  String fieldName = pd.getName();
                  if (fieldName.equalsIgnoreCase(OPERATION_TIME_FIELD_NAME)) {
                    fieldName = OPERATES_FROM;
                    resFieldNameToMethod.put(OPERATES_UNTIL, pd.getReadMethod());
                  }

                  // VoltageLevel needs to be replaced by id and nominalVoltage
                  if (fieldName.equalsIgnoreCase(VOLT_LVL_FIELD_NAME)) {
                    fieldName = V_RATED;
                    resFieldNameToMethod.put(VOLT_LVL, pd.getReadMethod());
                  }
                  resFieldNameToMethod.put(fieldName, pd.getReadMethod());
                }
              });

    } catch (Exception e) {
      throw new EntityProcessorException(
          "Error during EntityProcessor class registration process. Exception was:" + e);
    }
    return Collections.unmodifiableMap(resFieldNameToMethod);
  }

  /**
   * Standard call to handle an entity
   *
   * @param entity the entity that should be 'de-serialized' into a map of fieldName -> fieldValue
   * @return an optional Map with fieldName -> fieldValue or an empty optional if an error occurred
   *     during processing
   */
  public Optional<LinkedHashMap<String, String>> handleEntity(T entity) {
    if (!registeredClass.equals(entity.getClass()))
      throw new EntityProcessorException(
          "Cannot process "
              + entity.getClass().getSimpleName()
              + ".class with this EntityProcessor. Please either provide an element of "
              + registeredClass.getSimpleName()
              + ".class or create a new factory for "
              + entity.getClass().getSimpleName()
              + ".class!");
    return processEntity(entity);
  }

  /**
   * Actual implementation of the entity handling process
   *
   * @param entity the entity that should be 'de-serialized' into a map of fieldName -> fieldValue
   * @return an optional Map with fieldName -> fieldValue or an empty optional if an error occurred
   *     during processing
   */
  private Optional<LinkedHashMap<String, String>> processEntity(T entity) {

    Optional<LinkedHashMap<String, String>> resultMapOpt;

    try {
      LinkedHashMap<String, String> resultMap = new LinkedHashMap<>();
      for (String fieldName : headerElements) {
        Method method = fieldNameToMethod.get(fieldName);
        Optional<Object> methodReturnObjectOpt = Optional.ofNullable(method.invoke(entity));

        if (methodReturnObjectOpt.isPresent()) {
          resultMap.put(
              fieldName, processMethodResult(methodReturnObjectOpt.get(), method, fieldName));
        } else {
          resultMap.put(fieldName, "");
        }
      }
      resultMapOpt = Optional.of(resultMap);
    } catch (Exception e) {
      log.error("Error during entity processing:", e);
      resultMapOpt = Optional.empty();
    }
    return resultMapOpt;
  }

  /**
   * Processes the returned object to String by taking care of different conventions.
   *
   * @param methodReturnObject Return object to process
   * @param method The method, that is invoked
   * @param fieldName Name of the foreseen field
   * @return A String representation of the result
   */
  private String processMethodResult(Object methodReturnObject, Method method, String fieldName) {

    StringBuilder resultStringBuilder = new StringBuilder();

    switch (method.getReturnType().getSimpleName()) {
        // primitives (Boolean, Character, Byte, Short, Integer, Long, Float, Double, String,
      case "UUID":
      case "boolean":
      case "int":
      case "double":
      case "String":
        resultStringBuilder.append(methodReturnObject.toString());
        break;
      case "ComparableQuantity":
        resultStringBuilder.append(
            handleQuantity((ComparableQuantity<?>) methodReturnObject, fieldName)
                .orElseThrow(
                    () ->
                        new EntityProcessorException(
                            "Unable to process quantity value for attribute '"
                                + fieldName
                                + "' in result entity "
                                + getRegisteredClass().getSimpleName()
                                + ".class.")));
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
        resultStringBuilder.append(((StandardLoadProfile) methodReturnObject).getKey());
        break;
      case "StorageStrategy":
        resultStringBuilder.append(((StorageStrategy) methodReturnObject).getToken());
        break;
      case "NodeInput":
      case "AssetTypeInput":
      case "Transformer3WTypeInput":
      case "Transformer2WTypeInput":
      case "LineTypeInput":
      case "LineInput":
      case "OperatorInput":
      case "WecTypeInput":
      case "ThermalBusInput":
      case "ThermalStorageInput":
      case "ChpTypeInput":
      case "BmTypeInput":
      case "EvTypeInput":
      case "StorageTypeInput":
      case "HpTypeInput":
        resultStringBuilder.append(((UniqueEntity) methodReturnObject).getUuid());
        break;
      case "EvCharacteristicInput":
      case "OlmCharacteristicInput":
      case "WecCharacteristicInput":
      case "CosPhiFixed":
      case "CosPhiP":
      case "QV":
      case "ReactivePowerCharacteristic":
      case "CharacteristicInput":
        resultStringBuilder.append(((CharacteristicInput) methodReturnObject).deSerialize());
        break;
      default:
        throw new EntityProcessorException(
            "Unable to process value for attribute/field '"
                + fieldName
                + "' and method return type '"
                + method.getReturnType().getSimpleName()
                + "' for method with name '"
                + method.getName()
                + "' in in entity model "
                + getRegisteredClass().getSimpleName()
                + ".class.");
    }

    return resultStringBuilder.toString();
  }

  /**
   * Standard method to process a ZonedDateTime to a String based on a method return object
   *
   * @param zonedDateTime representation of the ZonedDateTime
   * @return string representation of the ZonedDateTime
   */
  protected String processZonedDateTime(ZonedDateTime zonedDateTime) {
    return zonedDateTime.toString();
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
  protected Optional<String> handleQuantity(ComparableQuantity<?> quantity, String fieldName) {
    if (specificQuantityFieldNames.contains(fieldName)) {
      return handleProcessorSpecificQuantity(quantity, fieldName);
    } else {
      return quantityValToOptionalString(quantity);
    }
  }

  /**
   * This method should handle all quantities that are model processor specific e.g. we need to
   * handle active power p different for {@link edu.ie3.datamodel.models.result.ResultEntity}s and
   * {@link edu.ie3.datamodel.models.input.system.SystemParticipantInput}s Hence from the
   * generalized method {@link this.handleQuantity()}, this allows for the specific handling of
   * child implementations. See the implementation @ {@link ResultEntityProcessor} for details.
   *
   * @param quantity the quantity that should be processed
   * @param fieldName the field name the quantity is set to
   * @return an optional string with the normalized to {@link StandardUnits} value of the quantity
   *     or empty if an error occurred during processing
   */
  protected abstract Optional<String> handleProcessorSpecificQuantity(
      ComparableQuantity<?> quantity, String fieldName);

  protected Optional<String> quantityValToOptionalString(ComparableQuantity<?> quantity) {
    return Optional.of(Double.toString(quantity.getValue().doubleValue()));
  }

  public Class<? extends T> getRegisteredClass() {
    return registeredClass;
  }

  public String[] getHeaderElements() {
    return headerElements;
  }

  protected abstract List<Class<? extends T>> getAllEligibleClasses();
}
