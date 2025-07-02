/*
 * © 2021. TU Dortmund University,
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
import edu.ie3.datamodel.models.timeseries.repetitive.BdewLoadProfileTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.RandomLoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.*;
import edu.ie3.datamodel.models.value.load.BdewLoadValues;
import edu.ie3.datamodel.models.value.load.RandomLoadValues;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class TimeSeriesProcessor<
        T extends TimeSeries<E, V, R>,
        E extends TimeSeriesEntry<V>,
        V extends Value,
        R extends Value>
    extends EntityProcessor<TimeSeries> {
  public static final List<TimeSeriesProcessorKey> eligibleKeys =
      List.of(
          new TimeSeriesProcessorKey(
              IndividualTimeSeries.class, TimeBasedValue.class, EnergyPriceValue.class),
          new TimeSeriesProcessorKey(
              IndividualTimeSeries.class, TimeBasedValue.class, TemperatureValue.class),
          new TimeSeriesProcessorKey(
              IndividualTimeSeries.class, TimeBasedValue.class, WindValue.class),
          new TimeSeriesProcessorKey(
              IndividualTimeSeries.class, TimeBasedValue.class, SolarIrradianceValue.class),
          new TimeSeriesProcessorKey(
              IndividualTimeSeries.class, TimeBasedValue.class, WeatherValue.class),
          new TimeSeriesProcessorKey(
              IndividualTimeSeries.class, TimeBasedValue.class, HeatDemandValue.class),
          new TimeSeriesProcessorKey(
              IndividualTimeSeries.class, TimeBasedValue.class, PValue.class),
          new TimeSeriesProcessorKey(
              IndividualTimeSeries.class, TimeBasedValue.class, HeatAndPValue.class),
          new TimeSeriesProcessorKey(
              IndividualTimeSeries.class, TimeBasedValue.class, SValue.class),
          new TimeSeriesProcessorKey(
              IndividualTimeSeries.class, TimeBasedValue.class, HeatAndSValue.class),
          new TimeSeriesProcessorKey(
              BdewLoadProfileTimeSeries.class, LoadProfileEntry.class, BdewLoadValues.class),
          new TimeSeriesProcessorKey(
              RandomLoadProfileTimeSeries.class, LoadProfileEntry.class, RandomLoadValues.class));

  private final TimeSeriesProcessorKey registeredKey;
  private final SortedMap<String, FieldSourceToMethod> fieldToSource;
  private final String[] flattenedHeaderElements;

  public TimeSeriesProcessor(Class<T> timeSeriesClass, Class<E> entryClass, Class<V> valueClass)
      throws EntityProcessorException {
    super(timeSeriesClass);

    TimeSeriesProcessorKey timeSeriesKey =
        new TimeSeriesProcessorKey(timeSeriesClass, entryClass, valueClass);
    if (!eligibleKeys.contains(timeSeriesKey))
      throw new EntityProcessorException(
          "Cannot register time series combination '"
              + timeSeriesKey
              + "' with entity processor '"
              + this.getClass().getSimpleName()
              + "'. Eligible combinations: "
              + eligibleKeys.stream()
                  .map(TimeSeriesProcessorKey::toString)
                  .collect(Collectors.joining(", ")));
    this.registeredKey = timeSeriesKey;

    this.fieldToSource = buildFieldToSource(timeSeriesClass, entryClass, valueClass);
    this.flattenedHeaderElements = fieldToSource.keySet().toArray(new String[0]);
  }

  public TimeSeriesProcessorKey getRegisteredKey() {
    return registeredKey;
  }

  private SortedMap<String, FieldSourceToMethod> buildFieldToSource(
      Class<T> timeSeriesClass, Class<E> entryClass, Class<V> valueClass)
      throws EntityProcessorException {
    Map<String, FieldSourceToMethod> timeSeriesMapping =
        mapFieldNameToGetter(
                timeSeriesClass, Arrays.asList("entries", "uuid", "type", "loadProfile"))
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> new FieldSourceToMethod(TIMESERIES, entry.getValue())));

    Map<String, FieldSourceToMethod> entryMapping =
        mapFieldNameToGetter(entryClass, Collections.singletonList("value")).entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey, entry -> new FieldSourceToMethod(ENTRY, entry.getValue())));

    Map<String, FieldSourceToMethod> valueMapping;
    if (valueClass.equals(WeatherValue.class)) {
      valueMapping =
          mapFieldNameToGetter(valueClass, Collections.singletonList("groundTemperatures"))
              .entrySet()
              .stream()
              .collect(
                  Collectors.toMap(
                      Map.Entry::getKey,
                      entry -> new FieldSourceToMethod(VALUE, entry.getValue())));
    } else {
      valueMapping =
          mapFieldNameToGetter(valueClass).entrySet().stream()
              .collect(
                  Collectors.toMap(
                      Map.Entry::getKey,
                      entry -> new FieldSourceToMethod(VALUE, entry.getValue())));
    }

    HashMap<String, FieldSourceToMethod> jointMapping = new HashMap<>();
    jointMapping.putAll(timeSeriesMapping);
    jointMapping.putAll(entryMapping);
    jointMapping.putAll(valueMapping);

    return putUuidFirst(jointMapping);
  }

  @Override
  public LinkedHashMap<String, String> handleEntity(TimeSeries entity) {
    throw new UnsupportedOperationException(
        "Don't invoke this simple method, but TimeSeriesProcessor#handleTimeSeries(TimeSeries).");
  }

  public Set<LinkedHashMap<String, String>> handleTimeSeries(T timeSeries)
      throws EntityProcessorException {
    TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(timeSeries);
    if (!registeredKey.equals(key))
      throw new EntityProcessorException(
          "Cannot handle a time series combination "
              + key
              + " with this EntityProcessor. Please either provide a time series combination of "
              + registeredKey
              + " or create a new processor for "
              + key
              + "!");

    Set<LinkedHashMap<String, String>> fieldToValueSet = new LinkedHashSet<>();

    for (E entry : timeSeries.getEntries()) {
      Map<String, String> entryResult = handleEntry(timeSeries, entry);
      fieldToValueSet.add(new LinkedHashMap<>(entryResult));
    }

    return fieldToValueSet;
  }

  private Map<String, String> handleEntry(T timeSeries, E entry) throws EntityProcessorException {
    Map<String, Method> timeSeriesFieldToMethod = extractFieldToMethod(TIMESERIES);
    LinkedHashMap<String, String> timeSeriesResults =
        processObject(timeSeries, timeSeriesFieldToMethod);

    Map<String, Method> entryFieldToMethod = extractFieldToMethod(ENTRY);
    LinkedHashMap<String, String> entryResults = processObject(entry, entryFieldToMethod);

    Map<String, Method> valueFieldToMethod = extractFieldToMethod(VALUE);
    LinkedHashMap<String, String> valueResult = processObject(entry.getValue(), valueFieldToMethod);

    Map<String, String> combinedResult = new HashMap<>();
    combinedResult.putAll(timeSeriesResults);
    combinedResult.putAll(entryResults);
    combinedResult.putAll(valueResult);
    return putUuidFirst(combinedResult);
  }

  private Map<String, Method> extractFieldToMethod(FieldSourceToMethod.FieldSource source) {
    return fieldToSource.entrySet().stream()
        .filter(entry -> entry.getValue().source().equals(source))
        .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().method()));
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
