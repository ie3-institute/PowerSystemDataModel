/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
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
    Stream.of(Serializable.class, String.class).forEach(ClassRegistry::add);
    registry.put("InputEntity", ClassName.get("edu.ie3.datamodel.models.input", "InputEntity"));
    registry.put("UniqueEntity", ClassName.get("edu.ie3.datamodel.models", "UniqueEntity"));
    registry.put("Entity", ClassName.get("edu.ie3.datamodel.models", "Entity"));
    registry.put("Uniqueness", ClassName.get("edu.ie3.datamodel.models", "Uniqueness"));
    registry.put("Operable", ClassName.get("edu.ie3.datamodel.models", "Operable"));
    registry.put("Dimensionless", ClassName.get("javax.measure.quantity", "Dimensionless"));
  }

  public static boolean containsKey(String name) {
    return registry.containsKey(name);
  }

  public static ClassName get(String name) {
    if (registry.containsKey(name)) {
      return registry.get(name);
    }

    throw new IllegalArgumentException("Couldn't find class path definition for name: " + name);
  }
}
