/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor.timeseries;

import static edu.ie3.datamodel.io.processor.timeseries.FieldSourceToMethod.FieldSource.*;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.io.processor.EntityProcessor;
import edu.ie3.datamodel.io.processor.GetterMethod;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.timeseries.repetitive.BdewLoadProfileTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.RandomLoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.*;
import edu.ie3.datamodel.models.value.load.BdewLoadValues;
import edu.ie3.datamodel.models.value.load.LoadValues;
import edu.ie3.datamodel.models.value.load.RandomLoadValues;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The type Time series processor.
 *
 * @param <T> the type parameter
 * @param <E> the type parameter
 * @param <V> the type parameter
 * @param <R> the type parameter
 */
public class TimeSeriesProcessor<
        T extends TimeSeries<E, V, R>,
        E extends TimeSeriesEntry<V>,
        V extends Value,
        R extends Value>
    extends EntityProcessor<TimeSeries> {
  /**
   * List of all combinations of time series class, entry class and value class, this processor is
   * able to handle
   */
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
              BdewLoadProfileTimeSeries.class,
              LoadProfileEntry.class,
              BdewLoadValues.class,
              BdewLoadValues.BdewScheme.BDEW1999),
          new TimeSeriesProcessorKey(
              BdewLoadProfileTimeSeries.class,
              LoadProfileEntry.class,
              BdewLoadValues.class,
              BdewLoadValues.BdewScheme.BDEW2025),
          new TimeSeriesProcessorKey(
              RandomLoadProfileTimeSeries.class, LoadProfileEntry.class, RandomLoadValues.class));

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

  /**
   * Instantiates a new Time series processor.
   *
   * @param timeSeriesClass the time series class
   * @param entryClass the entry class
   * @param valueClass the value class
   * @throws EntityProcessorException the entity processor exception
   */
  public TimeSeriesProcessor(Class<T> timeSeriesClass, Class<E> entryClass, Class<V> valueClass)
      throws EntityProcessorException {
    this(timeSeriesClass, entryClass, valueClass, Optional.empty());
  }

  /**
   * Instantiates a new Time series processor.
   *
   * @param timeSeriesClass the time series class
   * @param entryClass the entry class
   * @param valueClass the value class
   * @param scheme the scheme
   * @throws EntityProcessorException the entity processor exception
   */
  public TimeSeriesProcessor(
      Class<T> timeSeriesClass, Class<E> entryClass, Class<V> valueClass, LoadValues.Scheme scheme)
      throws EntityProcessorException {
    this(timeSeriesClass, entryClass, valueClass, Optional.ofNullable(scheme));
  }

  /**
   * Instantiates a new Time series processor.
   *
   * @param timeSeriesClass the time series class
   * @param entryClass the entry class
   * @param valueClass the value class
   * @param scheme the scheme
   * @throws EntityProcessorException the entity processor exception
   */
  public TimeSeriesProcessor(
      Class<T> timeSeriesClass,
      Class<E> entryClass,
      Class<V> valueClass,
      Optional<LoadValues.Scheme> scheme)
      throws EntityProcessorException {
    super(timeSeriesClass);

    /* Check, if this processor can handle the foreseen combination of time series, entry and value */
    TimeSeriesProcessorKey timeSeriesKey =
        new TimeSeriesProcessorKey(timeSeriesClass, entryClass, valueClass, scheme);
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
    this.fieldToSource = buildFieldToSource(timeSeriesClass, entryClass, valueClass, scheme);

    /* Collect all header elements */
    this.flattenedHeaderElements = fieldToSource.keySet().toArray(new String[0]);
  }

  /**
   * Gets registered key.
   *
   * @return the registered key
   */
  public TimeSeriesProcessorKey getRegisteredKey() {
    return registeredKey;
  }

  /**
   * Collects the mapping, where to find which information and how to get them (in terms of getter
   * method).
   *
   * @param timeSeriesClass Class of the time series
   * @param entryClass Class of the entry in the time series for the "outer" fields
   * @param valueClass Class of the actual value in the entries for the "inner" fields
   * @param scheme option for a scheme (used for load values)
   * @return A mapping from field name to a tuple of source information and equivalent getter method
   */
  private SortedMap<String, FieldSourceToMethod> buildFieldToSource(
      Class<T> timeSeriesClass,
      Class<E> entryClass,
      Class<V> valueClass,
      Optional<? extends LoadValues.Scheme> scheme)
      throws EntityProcessorException {
    /* Joined mapping */
    HashMap<String, FieldSourceToMethod> jointMapping = new HashMap<>();

    Function<FieldSourceToMethod.FieldSource, BiConsumer<String, GetterMethod>> addFunction =
        source ->
            (fieldName, getter) ->
                jointMapping.put(fieldName, new FieldSourceToMethod(source, getter));

    /* Get the mapping from field name to getter method ignoring the getter for returning all entries */
    mapFieldNameToGetter(timeSeriesClass, Arrays.asList("entries", "uuid", "type", "loadProfile"))
        .forEach(addFunction.apply(TIMESERIES));

    /* Get the mapping from field name to getter method for the entry, but ignoring the getter for the value */
    mapFieldNameToGetter(entryClass, Collections.singletonList("value"))
        .forEach(addFunction.apply(ENTRY));

    if (valueClass.equals(WeatherValue.class)) {
      /* Treat the nested weather values specially. */
      /* Flatten the nested structure of Weather value */
      mapFieldNameToGetter(valueClass, Arrays.asList("solarIrradiance", "temperature", "wind"))
          .forEach(addFunction.apply(VALUE));

      mapFieldNameToGetter(SolarIrradianceValue.class)
          .forEach(addFunction.apply(WEATHER_IRRADIANCE));
      mapFieldNameToGetter(TemperatureValue.class).forEach(addFunction.apply(WEATHER_TEMPERATURE));
      mapFieldNameToGetter(WindValue.class).forEach(addFunction.apply(WEATHER_WIND));

    } else if (valueClass.equals(BdewLoadValues.class)) {

      Collection<BdewLoadValues.BdewKey> keys;

      if (scheme.isPresent() && scheme.get() instanceof BdewLoadValues.BdewScheme bdewScheme) {
        keys = bdewScheme.getKeys();
      } else {
        keys = Collections.emptySet();
      }

      keys.stream()
          .collect(
              Collectors.toMap(
                  BdewLoadValues.BdewKey::getFieldName,
                  key ->
                      new GetterMethod(
                          "get" + key.getName(),
                          value -> ((BdewLoadValues) value).get(key),
                          "double")))
          .forEach(addFunction.apply(VALUE));

    } else {
      mapFieldNameToGetter(valueClass).forEach(addFunction.apply(VALUE));
    }

    /* Let uuid be the first entry */
    return putUuidFirst(jointMapping);
  }

  @Override
  public LinkedHashMap<String, String> handleEntity(TimeSeries entity) {
    throw new UnsupportedOperationException(
        "Don't invoke this simple method, but TimeSeriesProcessor#handleTimeSeries(TimeSeries).");
  }

  /**
   * Handles the time series by processing each entry and collecting the results
   *
   * @param timeSeries Time series to handle
   * @return A set of mappings from field name to value
   * @throws EntityProcessorException the entity processor exception
   */
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
  private Map<String, String> handleEntry(T timeSeries, E entry) throws EntityProcessorException {
    /* Handle the information in the time series */
    Map<String, GetterMethod> timeSeriesFieldToMethod = extractFieldToMethod(TIMESERIES);
    LinkedHashMap<String, String> timeSeriesResults =
        processObject(timeSeries, timeSeriesFieldToMethod);

    /* Handle the information in the entry */
    Map<String, GetterMethod> entryFieldToMethod = extractFieldToMethod(ENTRY);
    LinkedHashMap<String, String> entryResults = processObject(entry, entryFieldToMethod);

    /* Handle the information in the value */
    Map<String, GetterMethod> valueFieldToMethod = extractFieldToMethod(VALUE);
    LinkedHashMap<String, String> valueResult = processObject(entry.getValue(), valueFieldToMethod);

    /* Treat WeatherValues specially, as they are nested ones */
    if (entry.getValue() instanceof WeatherValue weatherValue) {
      Map<String, GetterMethod> irradianceFieldToMethod = extractFieldToMethod(WEATHER_IRRADIANCE);
      valueResult.putAll(processObject(weatherValue.getSolarIrradiance(), irradianceFieldToMethod));

      Map<String, GetterMethod> temperatureFieldToMethod =
          extractFieldToMethod(WEATHER_TEMPERATURE);
      valueResult.putAll(processObject(weatherValue.getTemperature(), temperatureFieldToMethod));

      Map<String, GetterMethod> windFieldToMethod = extractFieldToMethod(WEATHER_WIND);
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
  private Map<String, GetterMethod> extractFieldToMethod(FieldSourceToMethod.FieldSource source) {
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
