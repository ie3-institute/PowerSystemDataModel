package edu.ie3.datamodel.io.factory.deserializing;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PrimitiveDeserializingStrategies {
    
    public static final String VALUE_FIELD_KEY = "value";

    public static final SingleValueDeserializingStrategy<String> STRING_STRATEGY = new SingleValueDeserializingStrategy<String>() {
        @Override
        public Optional<String> deserialize(String valueToDeserialize) {
            return Optional.ofNullable(valueToDeserialize);
        }

        @Override
        public Class<String> getType() {
            return String.class;
        }
    };

    public static final SingleValueDeserializingStrategy<Boolean> BOOLEAN_STRATEGY = new SingleValueDeserializingStrategy<Boolean>() {
        @Override
        public Optional<Boolean> deserialize(String valueToDeserialize) {
            return Optional.of(Boolean.parseBoolean(valueToDeserialize));
        }

        @Override
        public Class<Boolean> getType() {
            return Boolean.class;
        }
    };

    public static final SingleValueDeserializingStrategy<Integer> INTEGER_STRATEGY = new SingleValueDeserializingStrategy<Integer>() {
        @Override
        public Optional<Integer> deserialize(String valueToDeserialize) {
            return Optional.of(Integer.parseInt(valueToDeserialize));
        }

        @Override
        public Class<Integer> getType() {
            return Integer.class;
        }
    };

    public static final Map<Class, SingleValueDeserializingStrategy> PRIMITIVE_STRATEGIES = Stream.of(STRING_STRATEGY, BOOLEAN_STRATEGY, INTEGER_STRATEGY).collect(Collectors.toMap(DeserializingStrategy::getType, strategy -> strategy));



}
