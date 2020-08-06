package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.annotations.FieldName;
import edu.ie3.datamodel.annotations.NestedFields;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;

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

    public static Collection<Field> getAllFields(Class<?> cls, Class<?> highestClassLvl){
            List<Field> fields = new ArrayList<>();

            for (Class<?> c = cls; c != null; c = c.getSuperclass()) {
                fields.addAll(Arrays.asList(c.getDeclaredFields()));
                if(c.equals(highestClassLvl)) break;
            }
            //exclude synthetic fields, like jacoco fields
            return fields.stream().filter(f -> !f.isSynthetic()).collect(Collectors.toSet());
        }

    public static Collection<Field> getAllFields(Class<?> cls){
        List<Field> fields = new ArrayList<>(Arrays.asList(cls.getDeclaredFields()));
            //exclude synthetic fields, like jacoco fields
            return fields.stream().filter(f -> !f.isSynthetic()).collect(Collectors.toSet());
        }

    //no nested names, but superclass names
    public static Map<Field, String> mapFieldToFieldName(Class<?> cls, Class<?> highestClassLvl){
        Collection<Field> fields = getAllFields(cls, highestClassLvl);
        return fields.stream().filter(field -> field.isAnnotationPresent(FieldName.class))
                .collect(Collectors.toMap(field -> field, field -> field.getAnnotation(FieldName.class).value()));
    }

    //no nested names, but superclass names
    public static Map<Field, String> mapFieldToFieldName(Class<?> cls){
        Collection<Field> fields = getAllFields(cls);
        return fields.stream().filter(field -> field.isAnnotationPresent(FieldName.class))
                .collect(Collectors.toMap(field -> field, field -> field.getAnnotation(FieldName.class).value()));
    }

    public static Map<String, String> mapFieldNames(Class<?> cls,  Class<?> highestClassLvl){
        Map<Field, String> fieldToFieldNames = mapFieldToFieldName(cls, highestClassLvl);
        return fieldToFieldNames.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getName(), Map.Entry::getValue));
    }

    public static Map<String, String> mapFieldNames(Class<?> cls){
        Map<Field, String> fieldToFieldNames = mapFieldToFieldName(cls);
        return fieldToFieldNames.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey().getName(), Map.Entry::getValue));
    }

//    public static Map<String, Method> mapFieldNameToGetter(Class<?> cls) throws IntrospectionException {
//        HashMap<String, Method> fieldNameToGetter = new HashMap<>();
//        Map<String, String> fieldNames = mapFieldNames(cls);
//        Arrays.stream(Introspector.getBeanInfo(cls, Object.class).getPropertyDescriptors())
//                .filter(pd -> Objects.nonNull(pd.getReadMethod()))
//                   .forEach(pd -> fieldNameToGetter.put(fieldNames.get(fieldNames.get(pd.getName())), pd.getReadMethod()));
//            fieldNameToGetter.remove(null);
//            return fieldNameToGetter;
//    }


    //also excludes regular stuff like operator.id
    public static <T> Map<String, Function<T, Optional<Object>>> mapFieldNameToFunctionWithExclusions(Class<T> cls) throws IntrospectionException {
        return mapFieldNameToFunctionWithExclusions(cls, new String[0]);
    }

    //also excludes regular stuff like operator.id
    public static <T> Map<String, Function<T, Optional<Object>>> mapFieldNameToFunctionWithExclusions(Class<T> cls, String... excludedFieldNames) throws IntrospectionException {
        Map<String, Function<T, Optional<Object>>> fieldNameToFunction = mapFieldNameToFunction(cls);
        Arrays.stream(excludedFieldNames)
                .forEach(fieldNameToFunction::remove);
        if(cls.equals(SwitchInput.class)) fieldNameToFunction.remove("parallel_devices");
        if(cls.equals(Transformer3WInput.class)) fieldNameToFunction.remove("node_internal");
        if(fieldNameToFunction.containsKey("operator_uuid")){
            fieldNameToFunction.remove("operator_id");
            fieldNameToFunction.put("operator", fieldNameToFunction.get("operator_uuid"));
            fieldNameToFunction.remove("operator_uuid");
        }
        return fieldNameToFunction;
    }

        public static <T> Map<String, Function<T, Optional<Object>>> mapFieldNameToFunction(Class<T> clsToMap) throws IntrospectionException {

        // map fields to getter function
            Map<String, Function<Object, Optional<Object>>> attributeNameToFunction = getMapFromPropertyDescriptors(clsToMap);

            // create a second map with FieldName.value() as key instead of Field.getName(), fill it only with non-nested fields for now
        Map<String, Function<Object, Optional<Object>>> fieldNameToFunction = mapFieldNames(clsToMap)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, entry -> attributeNameToFunction.get(entry.getKey())));

        // extract fields from superclass(es) and add them to our Map
            if(clsToMap.getSuperclass() != null && !clsToMap.getSuperclass().equals(Object.class)) {
                Map<String, Function<Object, Optional<Object>>> superFieldNameToFunction = mapFieldNameToFunction((Class<Object>) clsToMap.getSuperclass());
                fieldNameToFunction.putAll(superFieldNameToFunction);
            }

            // unwrap nested classes and map them to getter functions
        Collection<Field> nestedFields = getNestedFields(clsToMap);
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

    //no name mapping
    private static Map<String, Function<Object, Optional<Object>>> getMapFromPropertyDescriptors(Class<?> clsToMap, Class<?> highestClassLvl) throws IntrospectionException {
        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(clsToMap, highestClassLvl.getSuperclass()).getPropertyDescriptors();
        return Arrays.stream(propertyDescriptors)
                    .filter(pd -> Objects.nonNull(pd.getReadMethod()))
                    .collect(Collectors.toMap(FeatureDescriptor::getName, pd -> toFunction(pd.getReadMethod())));
    }

    //no name mapping
    private static Map<String, Function<Object, Optional<Object>>> getMapFromPropertyDescriptors(Class<?> clsToMap) throws IntrospectionException {
        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(clsToMap, clsToMap.getSuperclass()).getPropertyDescriptors();
        return Arrays.stream(propertyDescriptors)
                    .filter(pd -> Objects.nonNull(pd.getReadMethod()))
                    .collect(Collectors.toMap(FeatureDescriptor::getName, pd -> toFunction(pd.getReadMethod())));
    }

    public static Collection<Field> getNestedFields(Class<?> cls, Class<?> highestClassLvl) {
        Collection<Field> fields = getAllFields(cls, highestClassLvl);
        return fields.stream().filter(field -> field.isAnnotationPresent(NestedFields.class)).collect(Collectors.toSet());
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

    public static Optional<Function<Object, Optional<Object>>> getGetterFunction(Class<?> cls, String fieldName) {
        try {
            Map<String, Function<Object, Optional<Object>>> attributeNameToFunction = getMapFromPropertyDescriptors(cls, Object.class);
            return Optional.ofNullable(attributeNameToFunction.get(fieldName));
        } catch (IntrospectionException e) {
            return Optional.empty();
        }
    }

}