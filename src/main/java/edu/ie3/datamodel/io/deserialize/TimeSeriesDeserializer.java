/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.deserialize;

import edu.ie3.datamodel.exceptions.DeserializationException;
import edu.ie3.datamodel.io.processor.input.TimeBasedValueProcessor;
import edu.ie3.datamodel.io.processor.input.ValueProcessor;
import edu.ie3.datamodel.models.input.LoadProfileInput;
import edu.ie3.datamodel.models.timeseries.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.value.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeSeriesDeserializer {
  private static final Logger logger = LoggerFactory.getLogger(TimeSeriesDeserializer.class);

  public <T extends Value> void deserialize(TimeSeries<T> timeSeries)
      throws DeserializationException {
    /* Distinguish between individual and repetitive time series */
    if (timeSeries instanceof IndividualTimeSeries) {
      IndividualTimeSeries<T> individualTimeSeries = (IndividualTimeSeries<T>) timeSeries;

      /* Get all entries */
      TimeBasedValueProcessor timeBasedValueProcessor = new TimeBasedValueProcessor();
      SortedSet<TimeBasedValue<T>> entries = individualTimeSeries.getAllEntries();
      Set<Map<String, String>> result =
          Collections.unmodifiableSet(
              entries.stream()
                  .map(
                      timeBasedValue -> {
                        /* Build the mapping from field name to value for the containing class */
                        Optional<LinkedHashMap<String, String>> outerResult =
                            timeBasedValueProcessor.handleEntity(timeBasedValue);
                        if (!outerResult.isPresent()) {
                          logger.error(
                              "Cannot deserialize a time based value \"{}\".", timeBasedValue);
                          return new HashMap<String, String>();
                        }

                        ValueProcessor<T> valueProcessor =
                            new ValueProcessor<T>(
                                (Class<? extends T>) timeBasedValue.getValue().getClass());
                        Optional<LinkedHashMap<String, String>> innerResult =
                            valueProcessor.handleEntity(timeBasedValue.getValue());
                        if (!innerResult.isPresent()) {
                          logger.error(
                              "Cannot deserialize a time value \"{}\".", timeBasedValue.getValue());
                          return new HashMap<String, String>();
                        }

                        LinkedHashMap<String, String> interMediateResult = outerResult.get();
                        interMediateResult.putAll(innerResult.get());
                        return Collections.unmodifiableMap(interMediateResult);
                      })
                  .collect(Collectors.toSet()));

      // TODO: Writing the result
    } else {
      /* As repetitive time series as only abstract, determine the concrete type */
      if (timeSeries instanceof LoadProfileInput) {
        LoadProfileInput loadProfile = (LoadProfileInput) timeSeries;
        throw new DeserializationException(
            "The deserialisation of LoadProleInput is not implemented, yet.", loadProfile);

        /*
         * Steps to implement
         *   1) Determine the "unique" table entries as a combination of "credentials"
         *      and edu.ie3.datamodel.models.value.Value
         *   2) Build field name to value mapping for credentials and values independently
         *   3) Combine the mapping
         *   4) Write the result
         */
      } else {
        throw new DeserializationException(
            "There is no deserialization routine defined for a time series of type "
                + timeSeries.getClass().getSimpleName(),
            timeSeries);
      }
    }
  }
}
