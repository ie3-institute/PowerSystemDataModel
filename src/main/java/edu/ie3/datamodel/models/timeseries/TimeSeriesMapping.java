package edu.ie3.datamodel.models.timeseries;

import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.value.Value;

import java.util.HashMap;

public class TimeSeriesMapping<S extends SystemParticipantInput, V extends Value> extends HashMap<S, TimeSeries<V>> {
}
