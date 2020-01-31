/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.processor;

import edu.ie3.exceptions.EntityProcessorException;
import edu.ie3.exceptions.FactoryException;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.UniqueEntity;
import edu.ie3.models.result.ResultEntity;
import edu.ie3.util.TimeTools;
import edu.ie3.util.quantities.interfaces.HeatCapacity;
import java.beans.Introspector;
import java.lang.reflect.Method;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.Power;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
  protected final LinkedHashMap<String, Method> fieldNameToMethod = new LinkedHashMap<>();
  protected final boolean resultModel;

  /** Field name of {@link UniqueEntity} uuid */
  private final String uuidString = "uuid";

  /**
   * Create a new EntityProcessor
   *
   * @param registeredClass the class the entity processor should be able to handle
   */
  public EntityProcessor(Class<? extends T> registeredClass) {
    this.registeredClass = registeredClass;
    this.resultModel = isResultModel(registeredClass);
    this.headerElements = registerClass(registeredClass);
    TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * Determines if the provided class is a subclass of ResultEntity
   *
   * @param cls the provided class
   * @return true if it is a subclass of ResultEntity, false otherwise
   */
  private boolean isResultModel(Class<?> cls) {
    return ResultEntity.class.isAssignableFrom(cls);
  }

  /**
   * Register the class provided in the constructor
   *
   * @param cls class to be registered
   * @return an array of strings of all field values of the registered class
   */
  private String[] registerClass(Class<?> cls) {

    try {
      Arrays.stream(Introspector.getBeanInfo(cls, Object.class).getPropertyDescriptors())
          // filter out properties with setters only
          .filter(pd -> Objects.nonNull(pd.getReadMethod()))
          .forEach(
              pd -> { // invoke method to get value
                if (pd.getReadMethod() != null) {
                  fieldNameToMethod.put(pd.getName(), pd.getReadMethod());
                }
              });

    } catch (Exception e) {
      throw new EntityProcessorException(
          "Error during EntityProcessor class registration process. Exception was:" + e);
    }

    // uuid should always be the first element in the map
    String[] filteredArray =
        fieldNameToMethod.keySet().stream()
            .filter(x -> !x.toLowerCase().contains(uuidString))
            .toArray(String[]::new);
    return ArrayUtils.addAll(new String[] {uuidString}, filteredArray);
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
      throw new FactoryException(
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
   * Actual implementation of the handling process. Depends on the entity that should be processed
   * and hence needs to be implemented individually
   *
   * @param entity the entity that should be 'de-serialized' into a map of fieldName -> fieldValue
   * @return an optional Map with fieldName -> fieldValue or an empty optional if an error occurred
   *     during processing
   */
  protected abstract Optional<LinkedHashMap<String, String>> processEntity(T entity);

  /**
   * Standard method to process a ZonedDateTime to a String based on a method return object NOTE:
   * this method does NOT check if the provided object is of type ZonedDateTime. This has to be done
   * manually BEFORE calling this method!
   *
   * @param zonedDateTime representation of the ZonedDateTime
   * @return string representation of the ZonedDateTime
   */
  protected String processZonedDateTime(ZonedDateTime zonedDateTime) {
    return TimeTools.toString(zonedDateTime);
  }

  /**
   * Standard method to process a Quantity to a String based on a method return object
   *
   * @param quantity the quantity that should be processed
   * @param fieldName the field name the quantity is set to
   * @param resultModel true if the processed model is of type ResultEntity, false otherwise
   * @return an optional string with the normalized to {@link StandardUnits} value of the quantity
   *     or empty if an error occurred during processing
   */
  protected Optional<String> handleQuantity(
      Quantity<?> quantity, String fieldName, boolean resultModel) {

    Optional<String> normalizedQuantityValue = Optional.empty();
    // result models
    if (resultModel) {
      normalizedQuantityValue = handleResultEntityQuantity(quantity, fieldName);
      // input models, not complete yet!
      // might make sense to move this to another place as well in the future
    } else {
      switch (fieldName) {
        case "p":
          normalizedQuantityValue =
              quantityValToOptionalString(
                  quantity.asType(Power.class).to(StandardUnits.ACTIVE_POWER_IN));
          break;
        case "q":
          normalizedQuantityValue =
              quantityValToOptionalString(
                  quantity.asType(Power.class).to(StandardUnits.REACTIVE_POWER_IN));
          break;
        case "soc":
        case "vAng":
        case "vMag":
        case "iAAng":
        case "iBAng":
        case "iCAng":
          normalizedQuantityValue = quantityValToOptionalString(quantity);
          break;
        case "iAMag":
        case "iBMag":
        case "iCMag":
          normalizedQuantityValue =
              quantityValToOptionalString(
                  quantity.asType(ElectricCurrent.class).to(StandardUnits.CURRENT));
          break;
        case "qDemand":
          normalizedQuantityValue =
              quantityValToOptionalString(
                  quantity.asType(HeatCapacity.class).to(StandardUnits.HEAT_CAPACITY));
          break;
        default:
          log.error(
              "Cannot process quantity {} for field with name {} in input model processing!",
              quantity,
              fieldName);
          break;
      }
    }
    return normalizedQuantityValue;
  }

  private Optional<String> handleResultEntityQuantity(Quantity<?> quantity, String fieldName) {
    Optional<String> normalizedQuantityValue = Optional.empty();
    switch (fieldName) {
      case "p":
        normalizedQuantityValue =
            quantityValToOptionalString(
                quantity.asType(Power.class).to(StandardUnits.ACTIVE_POWER_OUT));
        break;
      case "q":
        normalizedQuantityValue =
            quantityValToOptionalString(
                quantity.asType(Power.class).to(StandardUnits.REACTIVE_POWER_OUT));
        break;
      case "soc":
      case "vAng":
      case "vMag":
      case "iAAng":
      case "iBAng":
      case "iCAng":
        normalizedQuantityValue = quantityValToOptionalString(quantity);
        break;
      case "iAMag":
      case "iBMag":
      case "iCMag":
        normalizedQuantityValue =
            quantityValToOptionalString(
                quantity.asType(ElectricCurrent.class).to(StandardUnits.CURRENT));
        break;
      case "qDemand":
        normalizedQuantityValue =
            quantityValToOptionalString(
                quantity.asType(HeatCapacity.class).to(StandardUnits.HEAT_CAPACITY));
        break;
      default:
        log.error(
            "Cannot process quantity {} for field with name {} in result model processing!",
            quantity,
            fieldName);
        break;
    }
    return normalizedQuantityValue;
  }

  private Optional<String> quantityValToOptionalString(Quantity<?> quantity) {
    return Optional.of(Double.toString(quantity.getValue().doubleValue()));
  }

  public Class<? extends T> getRegisteredClass() {
    return registeredClass;
  }

  public String[] getHeaderElements() {
    return headerElements;
  }
}
