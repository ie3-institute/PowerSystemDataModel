/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor.input;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.io.processor.Processor;
import edu.ie3.datamodel.models.value.EnergyPriceValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.TimeTools;
import java.lang.reflect.Method;
import java.time.ZoneId;
import java.util.*;
import javax.measure.Quantity;

/**
 * Processor for time based values. It "unboxes" the underlying value and joins the field name to
 * value mapping of the time based container and the value itself.
 *
 * @param <T> Type parameter of the contained {@link Value}
 */
public class ValueProcessor<T extends Value> extends Processor<Value> {

  public static final List<Class<? extends Value>> eligibleClasses =
      Collections.unmodifiableList(Collections.singletonList(EnergyPriceValue.class));

  private final SortedMap<String, Method> fieldNameToGetter;
  private final String[] headerElements;

  /**
   * Constructs the processor and registers the foreseen class of the {@link Value}
   *
   * @param foreSeenClass Foreseen class to be contained in this time based value
   */
  public ValueProcessor(Class<? extends T> foreSeenClass) {
    super(foreSeenClass);

    /* Build a mapping from field name to getter method, disjoint for TimeBasedValue and the value itself */
    this.fieldNameToGetter = mapFieldNameToGetter(foreSeenClass);

    /* Flatten the collected field name */
    this.headerElements = fieldNameToGetter.keySet().toArray(new String[0]);

    TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * Handles a given time value and returns an option on a mapping from field name on field value as
   * String representation
   *
   * @param value The entity to handle
   * @return An option on a mapping from field name to field value as String representation
   */
  public Optional<LinkedHashMap<String, String>> handleEntity(T value) {
    if (!registeredClass.equals(value.getClass()))
      throw new EntityProcessorException(
          "Cannot process "
              + value.getClass().getSimpleName()
              + ".class with this EntityProcessor. Please either provide an element of "
              + registeredClass.getSimpleName()
              + ".class or create a new factory for "
              + value.getClass().getSimpleName()
              + ".class!");

    /* Process both entities disjoint */
    LinkedHashMap<String, String> result;
    try {
      result = processObject(value, fieldNameToGetter);
    } catch (EntityProcessorException e) {
      logger.error("Cannot process the value {}.", value, e);
      return Optional.empty();
    }

    return Optional.of(result);
  }

  @Override
  protected Optional<String> handleProcessorSpecificQuantity(
      Quantity<?> quantity, String fieldName) {
    throw new UnsupportedOperationException("No specific quantity handling needed here!");
  }

  @Override
  public String[] getHeaderElements() {
    return headerElements;
  }

  @Override
  protected List<Class<? extends Value>> getEligibleEntityClasses() {
    return eligibleClasses;
  }
}
