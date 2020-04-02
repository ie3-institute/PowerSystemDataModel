/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor.timeseries;

import static edu.ie3.datamodel.io.processor.timeseries.FieldSourceToMethod.FieldSource.*;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.io.processor.EntityProcessor;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.EnergyPriceValue;
import edu.ie3.datamodel.models.value.Value;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import javax.measure.Quantity;

public class TimeSeriesProcessor<
        T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
    extends EntityProcessor<TimeSeries> {
  /**
   * List of all combinations of time series class, entry class and value class, this processor is
   * able to handle
   */
  public static final List<TimeSeriesProcessorKey> eligibleKeys =
      Collections.unmodifiableList(
          Collections.singletonList(
              new TimeSeriesProcessorKey(
                  IndividualTimeSeries.class, TimeBasedValue.class, EnergyPriceValue.class)));

  /**
   * Specific combination of time series class, entry class and value class, this processor is
   * foreseen to handle.
   */
  private final TimeSeriesProcessorKey registeredKey;

  /**
   * Mapping from field name to the source, where to find the information and which getter method to
   * invoke
   */
  private final SortedMap<String, FieldSourceToMethod> fieldToSource;

  private final String[] flattenedHeaderElements;

  public TimeSeriesProcessor(Class<T> timeSeriesClass, Class<E> entryClass, Class<V> valueClass) {
    super(timeSeriesClass);

    /* Check, if this processor can handle the foreseen combination of time series, entry and value */
    TimeSeriesProcessorKey timeSeriesKey =
        new TimeSeriesProcessorKey(timeSeriesClass, entryClass, valueClass);
    if (!eligibleKeys.contains(timeSeriesKey))
      throw new EntityProcessorException(
          "Cannot register time series combination '"
              + timeSeriesKey.toString()
              + "' with entity processor '"
              + this.getClass().getSimpleName()
              + "'. Eligible combinations: "
              + eligibleKeys.stream()
                  .map(TimeSeriesProcessorKey::toString)
                  .collect(Collectors.joining(", ")));
    this.registeredKey = timeSeriesKey;

    /* Register, where to get which information from */
    this.fieldToSource = buildFieldToSource(timeSeriesClass, entryClass, valueClass);

    /* Collect all header elements */
    this.flattenedHeaderElements = fieldToSource.keySet().toArray(new String[0]);
  }

  /**
   * Collects the mapping, where to find which information and how to get them (in terms of getter
   * method).
   *
   * @param timeSeriesClass Class of the time series
   * @param entryClass Class of the entry in the time series for the "outer" fields
   * @param valueClass Class of the actual value in the entries for the "inner" fields
   * @return A mapping from field name to a tuple of source information and equivalent getter method
   */
  private SortedMap<String, FieldSourceToMethod> buildFieldToSource(
      Class<T> timeSeriesClass, Class<E> entryClass, Class<V> valueClass) {
    /* Get the mapping from field name to getter method ignoring the getter for returning all entries */
    Map<String, FieldSourceToMethod> timeSeriesMapping =
        super.mapFieldNameToGetter(timeSeriesClass, Arrays.asList("entries", "uuid")).entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> new FieldSourceToMethod(TIMESERIES, entry.getValue())));
    /* Get the mapping from field name to getter method for the entry, but ignoring the getter for the value */
    Map<String, FieldSourceToMethod> entryMapping =
        super.mapFieldNameToGetter(entryClass, Collections.singletonList("value")).entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry ->
                        new FieldSourceToMethod(ENTRY, entry.getValue())));
    Map<String, FieldSourceToMethod> valueMapping =
        super.mapFieldNameToGetter(valueClass).entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry ->
                        new FieldSourceToMethod(VALUE, entry.getValue())));

    /* Put everything together */
    HashMap<String, FieldSourceToMethod> jointMapping = new HashMap<>();
    jointMapping.putAll(timeSeriesMapping);
    jointMapping.putAll(entryMapping);
    jointMapping.putAll(valueMapping);

    /* Let uuid be the first entry */
    return Collections.unmodifiableSortedMap(putUuidFirst(jointMapping));
  }

  @Override
  public Optional<LinkedHashMap<String, String>> handleEntity(TimeSeries entity) {
    throw new UnsupportedOperationException(
        "Don't invoke this simple method, but TimeSeriesProcessor#handleTimeSeries(TimeSeries).");
  }

  /**
   * Handles the time series by processing each entry and collecting the results
   *
   * @param timeSeries Time series to handle
   * @return A set of mappings from field name to value
   */
  public Set<LinkedHashMap<String, String>> handleTimeSeries(T timeSeries) {
    TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(timeSeries);
    if (!registeredKey.equals(key))
      throw new EntityProcessorException(
          "Cannot a time series combination "
              + key
              + " with this EntityProcessor. Please either provide a time series combination of "
              + registeredKey
              + " or create a new processor for "
              + key
              + "!");

    Set<LinkedHashMap<String, String>> fieldToValueSet = new HashSet<>();

    for (E entry : timeSeries.getEntries()) {
      Map<String, String> entryResult = handleEntry(timeSeries, entry);

      /* Prepare the actual result and add them to the set of all results */
      fieldToValueSet.add(new LinkedHashMap<>(entryResult));
    }

    return fieldToValueSet;
  }

  /**
   * Processes a single entry to a mapping from field name to value as String representation. The
   * information from the time series are added as well.
   *
   * @param timeSeries Time series for additional information
   * @param entry Actual entry to handle
   * @return A sorted map from field name to value as String representation
   */
  private Map<String, String> handleEntry(T timeSeries, E entry) {
    /* Handle the information in the time series */
    Map<String, Method> timeSeriesFieldToMethod = extractFieldToMethod(TIMESERIES);
    LinkedHashMap<String, String> timeSeriesResults =
        processObject(timeSeries, timeSeriesFieldToMethod);

    /* Handle the information in the entry */
    Map<String, Method> entryFieldToMethod = extractFieldToMethod(ENTRY);
    LinkedHashMap<String, String> entryResults = processObject(entry, entryFieldToMethod);

    /* Handle the information in the value */
    Map<String, Method> valueFieldToMethod = extractFieldToMethod(VALUE);
    LinkedHashMap<String, String> valueResult = processObject(entry.getValue(), valueFieldToMethod);

    /* Join all information and sort them */
    Map<String, String> sortedResult = new HashMap<>();
    sortedResult.putAll(timeSeriesResults);
    sortedResult.putAll(entryResults);
    sortedResult.putAll(valueResult);
    return sortedResult;
  }

  /**
   * Extracts the field name to method map for the specific source
   *
   * @param source Source to extract field name to methods for
   * @return Field name to methods for the desired source
   */
  private Map<String, Method> extractFieldToMethod(FieldSourceToMethod.FieldSource source) {
    return fieldToSource.entrySet().stream()
        .filter(entry -> entry.getValue().getSource().equals(source))
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getMethod()));
  }

  @Override
  protected Optional<String> handleProcessorSpecificQuantity(
      Quantity<?> quantity, String fieldName) {
    throw new UnsupportedOperationException("No specific handling of quantities needed here.");
  }

  @Override
  public String[] getHeaderElements() {
    return flattenedHeaderElements;
  }

  @Override
  protected List<Class<? extends TimeSeries>> getEligibleEntityClasses() {
    return eligibleKeys.stream()
        .map(TimeSeriesProcessorKey::getTimeSeriesClass)
        .distinct()
        .collect(Collectors.toList());
  }
}
