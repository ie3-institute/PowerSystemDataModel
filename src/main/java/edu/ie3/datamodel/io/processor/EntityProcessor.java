/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.UniqueEntity;
import java.lang.reflect.Method;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal API Interface for EntityProcessors. Main purpose is to 'de-serialize' models into a
 * fieldName to value representation to allow for an easy processing into a database or file sink
 * e.g. .csv
 *
 * @version 0.1
 * @since 31.01.20
 */
public abstract class EntityProcessor<T extends UniqueEntity> extends Processor<T> {

  public static final Logger log = LoggerFactory.getLogger(EntityProcessor.class);
  protected final String[] headerElements;
  private final SortedMap<String, Method> fieldNameToMethod;

  private static final String NODE_INTERNAL = "nodeInternal";

  /**
   * Create a new EntityProcessor
   *
   * @param registeredClass the class the entity processor should be able to handle
   */
  public EntityProcessor(Class<? extends T> registeredClass) {
    super(registeredClass);
    this.fieldNameToMethod =
        mapFieldNameToGetter(registeredClass, Collections.singleton(NODE_INTERNAL));
    this.headerElements = fieldNameToMethod.keySet().toArray(new String[0]);
  }

  /**
   * Standard call to handle an entity
   *
   * @param entity the entity that should be 'de-serialized' into a map of fieldName to fieldValue
   * @return an optional Map with fieldName to fieldValue or an empty optional if an error occurred
   *     during processing
   */
  public Optional<LinkedHashMap<String, String>> handleEntity(T entity) {
    if (!registeredClass.equals(entity.getClass()))
      throw new EntityProcessorException(
          "Cannot process "
              + entity.getClass().getSimpleName()
              + ".class with this EntityProcessor. Please either provide an element of "
              + registeredClass.getSimpleName()
              + ".class or create a new processor for "
              + entity.getClass().getSimpleName()
              + ".class!");

    try {
      return Optional.of(processObject(entity, fieldNameToMethod));
    } catch (EntityProcessorException e) {
      logger.error("Cannot process the entity{}.", entity, e);
      return Optional.empty();
    }
  }

  @Override
  protected Optional<String> handleProcessorSpecificQuantity(
      Quantity<?> quantity, String fieldName) {
    Optional<String> normalizedQuantityValue = Optional.empty();
    switch (fieldName) {
      case "energy":
      case "eConsAnnual":
      case "eStorage":
        normalizedQuantityValue =
            quantityValToOptionalString(quantity.asType(Energy.class).to(StandardUnits.ENERGY_IN));
        break;
      case "q":
        normalizedQuantityValue =
            quantityValToOptionalString(
                quantity.asType(Power.class).to(StandardUnits.REACTIVE_POWER_IN));
        break;
      case "p":
      case "pMax":
      case "pOwn":
      case "pThermal":
        normalizedQuantityValue =
            quantityValToOptionalString(
                quantity.asType(Power.class).to(StandardUnits.ACTIVE_POWER_IN));
        break;
      default:
        log.error(
            "Cannot process quantity with value '{}' for field with name {} in input entity processing!",
            quantity,
            fieldName);
        break;
    }
    return normalizedQuantityValue;
  }

  @Override
  public String[] getHeaderElements() {
    return headerElements;
  }
}
