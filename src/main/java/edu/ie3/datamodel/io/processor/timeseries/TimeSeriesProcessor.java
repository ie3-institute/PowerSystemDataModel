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
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput;
import edu.ie3.datamodel.models.value.*;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TimeSeriesProcessor<
        T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
    extends EntityProcessor<TimeSeries> {
  /**
   * List of all combinations of time series class, entry class and value class, this processor is
   * able to handle
   */
  public static final List<TimeSeriesProcessorKey> eligibleKeys =
      Collections.unmodifiableList(
          Arrays.asList(
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
                  LoadProfileInput.class, LoadProfileEntry.class, PValue.class)));

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
              + timeSeriesKey
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
        mapFieldNameToGetter(timeSeriesClass, Arrays.asList("entries", "uuid", "type"))
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    entry -> new FieldSourceToMethod(TIMESERIES, entry.getValue())));
    /* Get the mapping from field name to getter method for the entry, but ignoring the getter for the value */
    Map<String, FieldSourceToMethod> entryMapping =
        mapFieldNameToGetter(entryClass, Collections.singletonList("value")).entrySet().stream()
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey, entry -> new FieldSourceToMethod(ENTRY, entry.getValue())));
    Map<String, FieldSourceToMethod> valueMapping;
    if (!valueClass.equals(WeatherValue.class)) {
      valueMapping =
          mapFieldNameToGetter(valueClass).entrySet().stream()
              .collect(
                  Collectors.toMap(
                      Map.Entry::getKey,
                      entry -> new FieldSourceToMethod(VALUE, entry.getValue())));
    } else {
      /* Treat the nested weather values specially. */
      /* Flatten the nested structure of Weather value */
      valueMapping =
          Stream.concat(
                  Stream.concat(
                      Stream.concat(
                          mapFieldNameToGetter(
                                  valueClass,
                                  Arrays.asList("solarIrradiance", "temperature", "wind"))
                              .entrySet()
                              .stream()
                              .map(
                                  entry ->
                                      new AbstractMap.SimpleEntry<>(
                                          entry.getKey(),
                                          new FieldSourceToMethod(VALUE, entry.getValue()))),
                          mapFieldNameToGetter(SolarIrradianceValue.class).entrySet().stream()
                              .map(
                                  entry ->
                                      new AbstractMap.SimpleEntry<>(
                                          entry.getKey(),
                                          new FieldSourceToMethod(
                                              WEATHER_IRRADIANCE, entry.getValue())))),
                      mapFieldNameToGetter(TemperatureValue.class).entrySet().stream()
                          .map(
                              entry ->
                                  new AbstractMap.SimpleEntry<>(
                                      entry.getKey(),
                                      new FieldSourceToMethod(
                                          WEATHER_TEMPERATURE, entry.getValue())))),
                  mapFieldNameToGetter(WindValue.class).entrySet().stream()
                      .map(
                          entry ->
                              new AbstractMap.SimpleEntry<>(
                                  entry.getKey(),
                                  new FieldSourceToMethod(WEATHER_WIND, entry.getValue()))))
              .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

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
    /* Treat WeatherValues specially, as they are nested ones */
    if (entry.getValue() instanceof WeatherValue) {
      WeatherValue weatherValue = (WeatherValue) entry.getValue();

      Map<String, Method> irradianceFieldToMethod = extractFieldToMethod(WEATHER_IRRADIANCE);
      valueResult.putAll(processObject(weatherValue.getSolarIrradiance(), irradianceFieldToMethod));

      Map<String, Method> temperatureFieldToMethod = extractFieldToMethod(WEATHER_TEMPERATURE);
      valueResult.putAll(processObject(weatherValue.getTemperature(), temperatureFieldToMethod));

      Map<String, Method> windFieldToMethod = extractFieldToMethod(WEATHER_WIND);
      valueResult.putAll(processObject(weatherValue.getWind(), windFieldToMethod));
    }

    /* Join all information and sort them */
    Map<String, String> combinedResult = new HashMap<>();
    combinedResult.putAll(timeSeriesResults);
    combinedResult.putAll(entryResults);
    combinedResult.putAll(valueResult);
    return putUuidFirst(combinedResult);
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
