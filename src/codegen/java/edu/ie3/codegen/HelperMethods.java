package edu.ie3.codegen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.*;

public interface HelperMethods {
    ClassName OBJECTS = ClassName.get(Objects.class);
    ClassName MAP = ClassName.get(Map.class);
    ClassName STRING = ClassName.get(String.class);
    ClassName UUID_CLASS = ClassName.get(UUID.class);
    ClassName CONVERSION_UTILS = ClassName.get("edu.ie3.datamodel.utils", "ConversionUtils");
    ClassName GEO_UTILS = ClassName.get("edu.ie3.util.geo", "GeoUtils");

    static ClassName getClassName(String name) {
        return switch (name) {
            case "GeoUtils" -> GEO_UTILS;
            case "UUID" -> UUID_CLASS;
            default -> throw new IllegalArgumentException("Could not resolve class: " + name);
        };
    }


    static CodeBlock indent(CodeBlock codeBlock) {
        return CodeBlock.builder()
                .indent()
                .add(codeBlock)
                .unindent()
                .build();
    }

    static boolean isPrimitive(String type) {
        return "bool".equals(type) || "int".equals(type);
    }

    static String defaultGetterName(ModelDefinition.ComponentDefinition component) {
        if (isPrimitive(component.type) || component.getter == null || component.getter.isBlank()) {
            if ("bool".equals(component.type)) {
                return "is" + capitalize(component.name);
            }

            return "get" + capitalize(component.name);
        } else {
            return component.getter;
        }
    }

    static String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }


    static boolean excludeFromMethods(ModelDefinition.ComponentDefinition component) {
        return "additionalInformation".equals(component.name);
    }

    default boolean isMap(ModelDefinition.ComponentDefinition component) {
        return component.type.equals("StringMap");
    }

    default boolean isOptional(ModelDefinition.ComponentDefinition component, GenerationConfig config) {
        //return !component.required; // will break the current code -> TODO: Adapt PSDM to use optional parameters

        return config.optionalGetters.contains(component.name);
    }

    static TypeName resolveType(String name, String currentPackage) {
        TypeRegistry.TypeDefinition type = TypeRegistry.get(name);

        ClassName rawType = className(type.javaName, currentPackage);

        if (type.genericArguments.isEmpty()) {
            return rawType;
        }

        TypeName[] genericArguments =
                type.genericArguments.stream()
                        .map(argument -> resolveClassName(argument, currentPackage))
                        .toArray(TypeName[]::new);

        return ParameterizedTypeName.get(rawType, genericArguments);
    }

    static ClassName resolveClassName(String name, String currentPackage) {
        if (ClassRegistry.containsKey(name)) {
            return ClassRegistry.get(name);
        }

        return className(name, currentPackage);
    }

    static ClassName className(String name, String currentPackage) {
        if (!name.contains(".")) {
            return ClassName.get(currentPackage, name);
        }

        int lastDot = name.lastIndexOf('.');

        return ClassName.get(
                name.substring(0, lastDot),
                name.substring(lastDot + 1));
    }


    static Map<String, ModelDefinition.ComponentDefinition> visibleComponents(ModelDefinition model, Map<String, ModelDefinition> models) {

        LinkedHashMap<String, ModelDefinition.ComponentDefinition> result =
                new LinkedHashMap<>();

        for (ModelDefinition level : hierarchy(model, models)) {
            for (ModelDefinition.ComponentDefinition component : level.components) {
                if (result.put(component.name, component) != null) {
                    throw new IllegalArgumentException(
                            "Duplicate component '"
                                    + component.name
                                    + "' in hierarchy of "
                                    + model.name);
                }
            }
        }

        return result;
    }

    private static List<ModelDefinition> hierarchy(ModelDefinition model, Map<String, ModelDefinition> models) {
        List<ModelDefinition> hierarchy = new ArrayList<>();

        ModelDefinition current = model;

        while (current != null) {
            hierarchy.add(current);

            if (current.extendsName == null || current.extendsName.isBlank()) {
                current = null;
            } else {
                current = getParent(current.extendsName, models);
            }
        }

        Collections.reverse(hierarchy);

        return hierarchy;
    }

    static ModelDefinition getParent(String name, Map<String, ModelDefinition> models) {
        return models.get(name);
    }

}
