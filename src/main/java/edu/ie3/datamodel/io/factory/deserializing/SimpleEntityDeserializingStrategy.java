package edu.ie3.datamodel.io.factory.deserializing;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.utils.FieldNameUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleEntityDeserializingStrategy<T extends UniqueEntity> implements DeserializingStrategy<T> {

    private final Class<T> type;

    private final String[] constructorFields;

    private final Map<Field, String> fieldToFieldName;

    private final Collection<Field> nonNestedFields;

    private final Map<Field, Collection<String>> innerFieldsOfNestedFields;

    private final Constructor<T> constructor;


    public SimpleEntityDeserializingStrategy(Class<T> type) {
        this.type = type;
        constructorFields = FieldNameUtil.getConstructorFields(type);
        fieldToFieldName = FieldNameUtil.mapFieldToFieldName(type, Object.class);
        fieldToFieldName.putAll(FieldNameUtil.mapNestedFieldsToReferenceName(type));
        nonNestedFields = FieldNameUtil.getFields(type, Object.class);
        innerFieldsOfNestedFields = FieldNameUtil.getInnerFieldNamesForNestedFields(type);
        constructor = (Constructor<T>) FieldNameUtil.getAnnotatedConstructor(type).orElseThrow(() -> new IllegalArgumentException("No constructor found"));
    }

    private Optional<T> construct(Object[] constructorParameters) {
        try {
            return Optional.of(constructor.newInstance(constructorParameters));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return Optional.empty();
        }
    }


    @Override
    public Optional<T> deserialize(Map<String, String> fieldMap) {
        return deserialize(fieldMap, Collections.emptyMap());
    }

    @Override
    public Optional<T> deserialize(Map<String, String> fieldMap, Map<String, Object> previouslyDeserializedFields) {
        Map<String, Object> fieldNameToDeserializedObject = new HashMap<>();
        Map<String, Field> fieldNameToField = fieldToFieldName.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        // Non-Nested Fields

        Map<Field, Optional<?>> deserializedFields = fieldMap.entrySet().stream().filter(entry -> nonNestedFields.contains(fieldNameToField.get(entry.getKey())))
                .collect(Collectors.toMap(entry -> fieldNameToField.get(entry.getKey()), entry -> Deserializer.deserialize(fieldNameToField.get(entry.getKey()).getType(), Collections.singletonMap(entry.getKey(), entry.getValue()))));
        deserializedFields.forEach((key, value) -> fieldNameToDeserializedObject.put(fieldToFieldName.get(key), value.orElse(null)));

        // Nested Fields
        Map<Field, Map<String, String>> fieldMapsForNestedFields = innerFieldsOfNestedFields.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> retrieveKeysFromMap(entry.getValue(), fieldMap)));
        Map<Field, Optional<?>> deserializedNestedFields = fieldMapsForNestedFields.entrySet().stream().filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> Deserializer.deserialize(entry.getKey().getType(), entry.getValue())));
        deserializedNestedFields.forEach((key, value) -> fieldNameToDeserializedObject.put(fieldToFieldName.get(key), value.orElse(null)));

        // If any object is redundant, use the previously deserialized object
        fieldNameToDeserializedObject.putAll(previouslyDeserializedFields);

        Object[] constructorParameters =  Stream.of(constructorFields).map(fieldNameToDeserializedObject::get).toArray();

        return construct(constructorParameters);
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    private static Map<String, String> retrieveKeysFromMap(Collection<String> keys, Map<String, String> map) {
        return map.entrySet().stream().filter(entry -> keys.contains(entry.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

}
