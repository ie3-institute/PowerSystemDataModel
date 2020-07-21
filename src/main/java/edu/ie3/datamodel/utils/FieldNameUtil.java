package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.annotations.FieldName;
import edu.ie3.datamodel.annotations.NestedFields;

import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

//In this class, FieldName means the name indicated by the annotation (-> Field.getAnnotation(FieldName.class).value)),
// attribute name means the actual name of the Field (-> Field.getName())
public class FieldNameUtil {


    private FieldNameUtil() {
            throw new IllegalStateException("Utility classes cannot be instantiated");
    }

    public static Collection<Field> getAllFields(Class<?> cls){
            List<Field> fields = new ArrayList<>();
            for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
                fields.addAll(Arrays.asList(c.getDeclaredFields()));
            }
            return fields.stream().filter(f -> !f.isSynthetic()).collect(Collectors.toSet());
        }

    //no nested names, but superclass names
    public static Map<Field, String> mapFieldToFieldName(Class<?> cls){
        Collection<Field> fields = getAllFields(cls);
        return fields.stream().filter(field -> field.isAnnotationPresent(FieldName.class))
                .collect(Collectors.toMap(field -> field, field -> field.getAnnotation(FieldName.class).value()));
    }

    public static Map<String, String> mapFieldNames(Class<?> cls){
        Map<Field, String> fieldToFieldNames = mapFieldToFieldName(cls);
        return fieldToFieldNames.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getName(), Map.Entry::getValue));
    }

    public static Map<String, Method> mapFieldNameToGetter(Class<?> cls) throws IntrospectionException {
        HashMap<String, Method> fieldNameToGetter = new HashMap<>();
        Map<String, String> fieldNames = mapFieldNames(cls);
        Arrays.stream(Introspector.getBeanInfo(cls, Object.class).getPropertyDescriptors())
                .filter(pd -> Objects.nonNull(pd.getReadMethod()))
                   .forEach(pd -> fieldNameToGetter.put(fieldNames.get(fieldNames.get(pd.getName())), pd.getReadMethod()));
            fieldNameToGetter.remove(null);
            return fieldNameToGetter;
    }


    public static <T> Map<String, Function<T, Optional<Object>>> mapFieldNameToFunction(Class<T> cls) throws IntrospectionException {
        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(cls, Object.class).getPropertyDescriptors();

        // map fields to getter function
        Map<String, Function<Object, Optional<Object>>> attributeNameToFunction = Arrays.stream(propertyDescriptors)
                .filter(pd -> Objects.nonNull(pd.getReadMethod()))
                .collect(Collectors.toMap(FeatureDescriptor::getName, pd -> toFunction(pd.getReadMethod())));

        // create a second map with FieldName.value() as key instead of Field.getName(), fill it only with non-nested fields for now
        Map<String, Function<Object, Optional<Object>>> fieldNameToFunction = mapFieldNames(cls).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getValue, entry -> attributeNameToFunction.get(entry.getKey())));

        // unwrap nested classes and map them to getter functions
        Collection<Field> nestedFields = getNestedFields(cls);
        Map<Field, Function<Object, Optional<Object>>> nestedFieldGetterFunctions = nestedFields.stream()
                .collect(Collectors.toMap(field -> field, field -> attributeNameToFunction.get(field.getName())));

        // map nested fields
        for (Field outerField : nestedFields) {
            // recursively extract inner getter functions
            // (this is also the reason we can't use type parameters, as the inner Function's input parameter
            // could have different types for every loop iteration and java doesn't have in-body type parameters)
            Map<String, ? extends Function<?, Optional<Object>>> innerGetterFunctions = mapFieldNameToFunction(outerField.getType());

            Function<Object, Optional<Object>> outerGetterFunction = nestedFieldGetterFunctions.get(outerField);
            String outerGetterPrefix = outerField.getAnnotation(NestedFields.class).prefix();

            // compose the outer getter and the inner getter functions as well as the field name and the prefix of the outer field
            Map<String, Function<Object, Optional<Object>>> composedGetterFunctions = innerGetterFunctions.entrySet().stream()
                    .collect(Collectors.toMap(entry -> outerGetterPrefix + entry.getKey(), entry -> composeFunctions(outerGetterFunction, (Function<Object, Optional<Object>>) entry.getValue()))); //MIA cast okay!

            fieldNameToFunction.putAll(composedGetterFunctions);
        }

        // cast now, because it wasn't possible before as the composition would've needed two generic types,
        // but we can't access the inner type
        return fieldNameToFunction.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entry -> (Function<T, Optional<Object>>) entry.getValue()));
    }

    public static Collection<Field> getNestedFields(Class<?> cls) {
        Collection<Field> fields = getAllFields(cls);
        return fields.stream().filter(field -> field.isAnnotationPresent(NestedFields.class)).collect(Collectors.toSet());
    }


    private static <T> Function<T, Optional<Object>> toFunction(Method m) {
        return (T t ) -> {
            try {
                return Optional.ofNullable(m.invoke(t));
            } catch (IllegalAccessException | InvocationTargetException e) {
                return Optional.empty();
            }
        };
    }

    //wrapper: getVoltLevel, nested: getVRated
    private static Function<Object, Optional<Object>> composeFunctions(Function<Object, Optional<Object>> outerFunction, Function<Object, Optional<Object>> innerFunction) {
        return (Object outerObject) -> outerFunction.apply(outerObject).flatMap(innerFunction);
    }
}