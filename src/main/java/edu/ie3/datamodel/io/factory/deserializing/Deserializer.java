package edu.ie3.datamodel.io.factory.deserializing;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.UniqueEntity;
import org.locationtech.jts.geom.Point;

import javax.measure.Quantity;
import java.time.ZonedDateTime;
import java.util.*;

public class Deserializer {

    // TODO pre-fill with mappings like Quantity:QuantityDeserializingStrategy
    private static Map<Class, DeserializingStrategy> deserializingStrategies = new HashMap<>();
    static {
        deserializingStrategies.putAll(PrimitiveDeserializingStrategies.PRIMITIVE_STRATEGIES);
        deserializingStrategies.put(Point.class, new PointDeserializingStrategy());
        deserializingStrategies.put(Quantity.class, new QuantityDeserializingStrategy());
        deserializingStrategies.put(UUID.class, new UuidDeserializingStrategy());
        deserializingStrategies.put(ZonedDateTime.class, new ZonedDateTimeDeserializingStrategy());
        deserializingStrategies.put(OperationTime.class, new OperationTimeDeserializingStrategy());
    }


    public static <T> Optional<T> deserialize(Class<T> cls, Map<String, String> fieldMap) {
        return deserialize(cls, fieldMap, Collections.emptyMap());
    }

    public static <T> Optional<T> deserialize(Class<T> cls, Map<String, String> fieldMap, Map<String, Object> deserializedFields) {
        DeserializingStrategy<T> deserializingStrategy = getDeserializingStrategy(cls);
        if(deserializingStrategy == null) return Optional.empty();
        return deserializingStrategy.deserialize(fieldMap, deserializedFields);
    }

    private static <T> DeserializingStrategy<T> getDeserializingStrategy(Class<T> cls) {
        if(UniqueEntity.class.isAssignableFrom(cls)) {
            if (!deserializingStrategies.containsKey(cls)) registerClass((Class<? extends UniqueEntity>) cls);
            return deserializingStrategies.get(cls);
        }
        if(Quantity.class.isAssignableFrom(cls)) {
            return deserializingStrategies.get(Quantity.class);
        }
        if(boolean.class.isAssignableFrom(cls)) {
            return deserializingStrategies.get(Boolean.class);
        }
        if(int.class.isAssignableFrom(cls)) {
            return deserializingStrategies.get(Integer.class);
        }
        else return deserializingStrategies.get(cls);
    }

    private static void registerClass(Class<? extends UniqueEntity> cls) {
        deserializingStrategies.put(cls, new SimpleEntityDeserializingStrategy<>(cls));
    }

}
