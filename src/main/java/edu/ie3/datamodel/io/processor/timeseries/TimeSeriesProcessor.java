/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.processor.timeseries;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.io.processor.EntityProcessor;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput;
import edu.ie3.datamodel.models.value.*;

import javax.measure.Quantity;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static edu.ie3.datamodel.io.processor.timeseries.FieldSourceToMethod.FieldSource.*;

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
                  IndividualTimeSeries.class, TimeBasedValue.class, IrradiationValue.class),
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

  private final Map<String, Function<Object, Optional<Object>>> entryFieldNamesToMethod;

  private final Map<String, Function<Object, Optional<Object>>> valueFieldNamesToMethod;

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

    //MIA
    entryFieldNamesToMethod = buildFunctionMap(entryClass);

    valueFieldNamesToMethod = buildFunctionMap(valueClass);

    /* Collect all header elements */
    this.flattenedHeaderElements = Stream.of(fieldNameToFunction.keySet(), entryFieldNamesToMethod.keySet(), valueFieldNamesToMethod.keySet()).flatMap(Collection::stream).toArray(String[]::new);
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
    LinkedHashMap<String, String> timeSeriesResults = processObject(timeSeries, fieldNameToFunction);

    /* Handle the information in the entry */
    LinkedHashMap<String, String> entryResults = processObject(entry, entryFieldNamesToMethod);

//    /* Handle the information in the value */
    LinkedHashMap<String, String> valueResults = processObject(entry.getValue(), valueFieldNamesToMethod);

    /* Join all information and sort them */
    Map<String, String> combinedResult = new HashMap<>();
    combinedResult.putAll(timeSeriesResults);
    combinedResult.putAll(entryResults);
    combinedResult.putAll(valueResults);
    return putUuidFirst(combinedResult);
  }

  //TODO: develop a strategy to get rid of these hard coded fieldNames
  @Override
  protected Optional<String> handleProcessorSpecificQuantity(
          Quantity<?> quantity, String fieldName) {
    Optional<String> normalizedQuantityValue = Optional.empty();
    switch (fieldName) {
      case "energy":
      case "e_cons_annual":
      case "e_storage":
        normalizedQuantityValue =
                quantityValToOptionalString(quantity.asType(Energy.class).to(StandardUnits.ENERGY_IN));
        break;
      case "q":
        normalizedQuantityValue =
                quantityValToOptionalString(
                        quantity.asType(Power.class).to(StandardUnits.REACTIVE_POWER_IN));
        break;
      case "p":
      case "p_max":
      case "p_own":
      case "p_thermal":
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
