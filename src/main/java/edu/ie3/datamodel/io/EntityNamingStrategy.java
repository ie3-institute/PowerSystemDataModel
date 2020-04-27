package edu.ie3.datamodel.io;

import edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessorKey;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;

import java.util.Optional;

public class EntityNamingStrategy {

    //TODO constructor

    private static final String RES_ENTITY_SUFFIX = "_res";

    public static Optional<String> getEntityName(Class<? extends UniqueEntity> cls){
        if(cls.isAssignableFrom(ResultEntity.class)) return Optional.of(getResultEntityName(cls.asSubclass(ResultEntity.class)));
        //TODO //MIA
        else return Optional.empty();
    }

    public static <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
    Optional<String> getEntityName(T timeSeries) {
            TimeSeriesProcessorKey key = new TimeSeriesProcessorKey(timeSeries);
            return Optional.of(key.getValueClass().getSimpleName());
    }

    private static String getResultEntityName(Class<? extends ResultEntity> resultEntityClass) {
        String resultEntityString =
                resultEntityClass.getSimpleName().replace("Result", "").toLowerCase();
        return resultEntityString.concat(RES_ENTITY_SUFFIX);
    }

}
