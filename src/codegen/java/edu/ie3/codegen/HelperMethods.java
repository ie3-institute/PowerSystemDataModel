package edu.ie3.codegen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.*;
import java.util.function.Function;

public interface HelperMethods {
    ClassName OBJECTS = ClassName.get(Objects.class);
    ClassName MAP = ClassName.get(Map.class);
    ClassName hashMap = ClassName.get(HashMap.class);
    ClassName STRING = ClassName.get(String.class);
    ClassName UUID_CLASS = ClassName.get(UUID.class);
    ClassName FUNCTION = ClassName.get(Function.class);

    static CodeBlock indent(CodeBlock codeBlock) {
        return CodeBlock.builder()
                .indent()
                .add(codeBlock)
                .unindent()
                .build();
    }

    static boolean isPrimitive(String type) {
        return "Boolean".equals(type) || "Integer".equals(type);
    }

    static String defaultGetterName(ModelDefinition.ComponentDefinition component) {
        if (component.getter == null || component.getter.isBlank()) {
            if ("Boolean".equals(component.type)) {
                return "is" + capitalize(component.name);
            }

            return "get" + capitalize(component.name);
        } else {
            return component.getter;
        }
    }

    private static String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }


    static TypeName resolveType(String name, String currentPackage) {
        TypeRegistry.TypeDefinition type = TypeRegistry.get(name);

        ClassName rawType = className(type.javaName, currentPackage);

        if (type.genericArguments.isEmpty()) {
            return rawType;
        }

        TypeName[] genericArguments =
                type.genericArguments.stream()
                        .map(argument -> className(argument, currentPackage))
                        .toArray(TypeName[]::new);

        return ParameterizedTypeName.get(rawType, genericArguments);
    }

    static ClassName resolveClassName(String name, String currentPackage) {
        if (TypeRegistry.containsKey(name)) {
            return className(TypeRegistry.get(name).javaName, currentPackage);
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
                current = getModel(current.extendsName, models);
            }
        }

        Collections.reverse(hierarchy);

        return hierarchy;
    }

    private static ModelDefinition getModel(String name,  Map<String, ModelDefinition> models) {
        ModelDefinition result = models.get(name);

        if (result == null) {
            throw new IllegalArgumentException("Unknown model: " + name);
        }

        return result;
    }

}
