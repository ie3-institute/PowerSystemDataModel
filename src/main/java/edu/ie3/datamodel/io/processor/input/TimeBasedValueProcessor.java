/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor.input;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.io.processor.Processor;
import edu.ie3.datamodel.models.value.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.TimeTools;
import java.lang.reflect.Method;
import java.time.ZoneId;
import java.util.*;
import javax.measure.Quantity;

/**
 * Processor for time based values. It only handles the time based value itself. The included value
 * must be treated separately. Therefore using the raw type is okay here.
 */
@SuppressWarnings("rawtypes")
public class TimeBasedValueProcessor extends Processor<TimeBasedValue> {

  public static final List<Class<? extends TimeBasedValue>> eligibleClasses =
      Collections.unmodifiableList(Collections.singletonList(TimeBasedValue.class));

  private final SortedMap<String, Method> fieldNameToGetter;
  private final String[] headerElements;

  /** Constructs the processor and registers the foreseen class */
  public TimeBasedValueProcessor() {
    super(TimeBasedValue.class);

    /* Build a mapping from field name to getter method, disjoint for TimeBasedValue and the value itself */
    this.fieldNameToGetter =
        mapFieldNameToGetter(TimeBasedValue.class, Collections.singletonList("value"));

    /* Collect the field names */
    this.headerElements = fieldNameToGetter.keySet().toArray(new String[0]);

    TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss");
  }

  /**
   * Handles a given time based value and returns an option on a mapping from field name on field
   * value as String representation
   *
   * @param timeBasedValue The entity to handle
   * @return An option on a mapping from field name to field value as String representation
   */
  public Optional<LinkedHashMap<String, String>> handleEntity(
      TimeBasedValue<? extends Value> timeBasedValue) {
    /* Process both entities disjoint */
    LinkedHashMap<String, String> topLevelResult;
    try {
      topLevelResult = processObject(timeBasedValue, fieldNameToGetter);
    } catch (EntityProcessorException e) {
      logger.error("Cannot process the time based value {} itself.", timeBasedValue, e);
      return Optional.empty();
    }

    return Optional.of(topLevelResult);
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
  protected List<Class<? extends TimeBasedValue>> getEligibleEntityClasses() {
    return eligibleClasses;
  }
}
