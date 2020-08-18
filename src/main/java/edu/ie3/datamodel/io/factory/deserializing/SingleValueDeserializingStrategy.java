package edu.ie3.datamodel.io.factory.deserializing;

import java.util.Map;
import java.util.Optional;

public interface SingleValueDeserializingStrategy<T> extends DeserializingStrategy<T> {

    String VALUE_FIELD_KEY = "value_field";

    Optional<T> deserialize(String valueToDeserialize);


    default Optional<T> deserialize(Map<String, String> fieldMap, Map<String, Object> deserializedFields) {
        deserializedFields.keySet().forEach(fieldMap::remove);
        if(!fieldMap.isEmpty()) return deserialize(fieldMap);
        Class<T> type = getType();
        return deserializedFields.values().stream().filter(type::isInstance).map(value -> (T) value).findFirst();
    }

    default Optional<T> deserialize(Map<String, String> fieldMap) {
        String valueToDeserialize = fieldMap.containsKey(VALUE_FIELD_KEY) ?
                fieldMap.get(VALUE_FIELD_KEY) : fieldMap.values().stream().findFirst().orElse("");
        if (valueToDeserialize.isEmpty()) return Optional.empty();
        return deserialize(valueToDeserialize);
    }
}
