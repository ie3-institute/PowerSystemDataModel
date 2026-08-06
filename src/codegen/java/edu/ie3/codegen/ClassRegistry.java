package edu.ie3.codegen;

import com.squareup.javapoet.ClassName;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Stream;

public final class ClassRegistry {
    private ClassRegistry() {}

    private static final Map<String, ClassName> registry = new LinkedHashMap<>();

    private static void add(Class<?> clazz) {
        registry.put(clazz.getSimpleName(), ClassName.get(clazz));
        registry.put(clazz.getName(), ClassName.get(clazz));
    }

    static {
        Stream.of(
                Serializable.class,
                String.class
        ).forEach(ClassRegistry::add);

        registry.put("UniqueEntity", ClassName.get("edu.ie3.datamodel.models2", "UniqueEntity"));
        registry.put("Entity", ClassName.get("edu.ie3.datamodel.models", "Entity"));
        registry.put("Uniqueness", ClassName.get("edu.ie3.datamodel.models", "Uniqueness"));
        registry.put("Dimensionless", ClassName.get("javax.measure.quantity", "Dimensionless"));
    }

    public static boolean containsKey(String name) {
        return registry.containsKey(name);
    }

    public static ClassName get(String name) {
        if (registry.containsKey(name)) {
            return registry.get(name);
        }

        throw new IllegalArgumentException("Couldn't find class path definition for name: "+ name);
    }

}
