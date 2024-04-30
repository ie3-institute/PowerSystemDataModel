/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.*;
import edu.ie3.util.exceptions.QuantityException;
import java.lang.reflect.Method;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal API Interface for EntityProcessors. Main purpose is to 'serialize' models into a
 * fieldName to value representation to allow for an easy processing into a database or file sink
 * e.g. .csv
 *
 * @version 0.1
 * @since 31.01.20
 */
public abstract class EntityProcessor<T extends Entity> extends Processor<T> {

  public static final Logger log = LoggerFactory.getLogger(EntityProcessor.class);
  protected final String[] headerElements;
  private final SortedMap<String, Method> fieldNameToMethod;

  private static final String NODE_INTERNAL = "nodeInternal";

  /**
   * Create a new EntityProcessor
   *
   * @param registeredClass the class the entity processor should be able to handle
   */
  protected EntityProcessor(Class<? extends T> registeredClass) throws EntityProcessorException {
    super(registeredClass);
    this.fieldNameToMethod =
        mapFieldNameToGetter(registeredClass, Collections.singleton(NODE_INTERNAL));
    this.headerElements = fieldNameToMethod.keySet().toArray(new String[0]);
  }

  /**
   * Standard call to handle an entity
   *
   * @param entity the entity that should be 'serialized' into a map of fieldName to fieldValue
   * @return an optional Map with fieldName to fieldValue or an empty optional if an error occurred
   *     during processing
   */
  public LinkedHashMap<String, String> handleEntity(T entity) throws EntityProcessorException {
    if (!registeredClass.equals(entity.getClass()))
      throw new EntityProcessorException(
          "Cannot process "
              + entity.getClass().getSimpleName()
              + ".class with this EntityProcessor. Please either provide an element of "
              + registeredClass.getSimpleName()
              + ".class or create a new processor for "
              + entity.getClass().getSimpleName()
              + ".class!");

    return processObject(entity, fieldNameToMethod);
  }

  @Override
  protected Try<String, QuantityException> handleProcessorSpecificQuantity(
      Quantity<?> quantity, String fieldName) {
    return switch (fieldName) {
      case "energy", "eConsAnnual", "eStorage":
        yield Success.of(
            quantityValToOptionalString(quantity.asType(Energy.class).to(StandardUnits.ENERGY_IN)));
      case "q":
        yield Success.of(
            quantityValToOptionalString(
                quantity.asType(Power.class).to(StandardUnits.REACTIVE_POWER_IN)));
      case "p", "pMax", "pOwn", "pThermal":
        yield Success.of(
            quantityValToOptionalString(
                quantity.asType(Power.class).to(StandardUnits.ACTIVE_POWER_IN)));
      default:
        yield Failure.of(
            new QuantityException(
                "Cannot process quantity with value '"
                    + quantity
                    + "' for field with name "
                    + fieldName
                    + " in input entity processing!"));
    };
  }

  @Override
  public String[] getHeaderElements() {
    return headerElements;
  }
}
