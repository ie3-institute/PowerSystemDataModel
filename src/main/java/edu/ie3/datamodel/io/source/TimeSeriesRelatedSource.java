package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.timeseries.SimpleTimeBasedValueData;
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.Value;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public abstract class TimeSeriesRelatedSource<V extends Value> {


    /*
    public abstract <V extends Value> IndividualTimeSeries<V> buildIndividualTimeSeries (
            UUID timeSeriesUuid,
            String specialPlace,
            Function<Map<String, String>, Optional<TimeBasedValue<V>>> fieldToValueFunction
    ) throws SourceException;


     */
    public Optional<TimeBasedValue<V>> buildTimeBasedValue(
            Map<String, String> fieldToValues,
            Class<V> valueClass,
            TimeBasedSimpleValueFactory<V> factory) {
        SimpleTimeBasedValueData<V> factoryData =
                new SimpleTimeBasedValueData<>(fieldToValues, valueClass);
        return factory.get(factoryData);
    }







}
