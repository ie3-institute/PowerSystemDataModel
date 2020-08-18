package edu.ie3.datamodel.io.factory.deserializing;

import java.util.Map;
import java.util.Optional;

public interface DeserializingStrategy<T> {

    Optional<T> deserialize(Map<String, String> fieldMap);

    Optional<T> deserialize(Map<String, String> fieldMap, Map<String, Object> deserializedFields);

    Class<T> getType();

}
