/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import java.util.*;

public interface HelperMethods {
  ClassName OBJECTS = ClassName.get(Objects.class);
  ClassName STRING = ClassName.get(String.class);

  default CodeBlock indent(CodeBlock codeBlock) {
    return CodeBlock.builder().indent().add(codeBlock).unindent().build();
  }

  default boolean isPrimitive(String type) {
    return "bool".equals(type) || "int".equals(type);
  }

  default String defaultGetterName(
      String name, String type, List<String> booleanGetter, List<String> nonCapitalizedGetters) {
    if (isPrimitive(type) || nonCapitalizedGetters == null || nonCapitalizedGetters.isEmpty()) {
      if ("bool".equals(type) && !booleanGetter.contains(name)) {
        return "is" + capitalize(name);
      }

      return "get" + capitalize(name);
    } else {
      if (!nonCapitalizedGetters.contains(name)) {
        return "get" + capitalize(name);
      } else {
        return "get" + name;
      }
    }
  }

  default String capitalize(String value) {
    return Character.toUpperCase(value.charAt(0)) + value.substring(1);
  }

  default boolean excludeFromMethods(ModelDefinition.ComponentDefinition component) {
    return "additionalInformation".equals(component.name);
  }

  default boolean isMap(ModelDefinition.ComponentDefinition component) {
    return component.type.equals("StringMap");
  }

  default Map<String, ModelDefinition.ComponentDefinition> visibleComponents(
      ModelDefinition model, Map<String, ModelDefinition> models) {

    LinkedHashMap<String, ModelDefinition.ComponentDefinition> result = new LinkedHashMap<>();

    for (ModelDefinition level : hierarchy(model, models)) {
      for (ModelDefinition.ComponentDefinition component : level.components) {
        if (result.put(component.name, component) != null) {
          throw new IllegalArgumentException(
              "Duplicate component '" + component.name + "' in hierarchy of " + model.name);
        }
      }
    }

    return result;
  }

  private List<ModelDefinition> hierarchy(
      ModelDefinition model, Map<String, ModelDefinition> models) {
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

  default ModelDefinition getParent(String name, Map<String, ModelDefinition> models) {
    return models.get(name);
  }
}
