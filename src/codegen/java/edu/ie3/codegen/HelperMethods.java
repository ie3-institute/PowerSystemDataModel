/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import static edu.ie3.codegen.ResolverUtils.resolveClassName;
import static edu.ie3.codegen.ResolverUtils.resolveType;

import com.squareup.javapoet.*;
import java.util.*;

public interface HelperMethods {

  default CodeBlock toString(
      ModelDefinition.ComponentDefinition component,
      List<String> components,
      GenerationConfig genConfig) {
    String componentName = component.name;

    String valueGetterExpression;
    boolean optional = false;
    String getterName;

    if (component.nullable) {
      // we need some special calls here
      if (!component.required) {
        valueGetterExpression = "$L()";
        getterName = defaultGetterName(component, genConfig);

      } else {
        valueGetterExpression = "$T.ofNullable($L)";
        optional = true;
        getterName =
            components.contains(componentName)
                ? componentName
                : defaultGetterName(component, genConfig);
      }

      valueGetterExpression += ".map(e -> e.getUuid().toString()).orElse(\"\")";

    } else {
      valueGetterExpression = component.nested ? "$L.getUuid()" : "$L";
      getterName =
          components.contains(componentName)
              ? componentName
              : defaultGetterName(component, genConfig) + "()";
    }

    if (optional) {
      return CodeBlock.of(valueGetterExpression + "\n", Optional.class, getterName);
    } else {
      return CodeBlock.of(valueGetterExpression + "\n", getterName);
    }
  }

  default boolean isPrimitive(String type) {
    return switch (type) {
      case "bool", "int", "double", "float" -> true;
      default -> false;
    };
  }

  default boolean useEquals(String type) {
    String cn;

    if (resolveType(type) instanceof ParameterizedTypeName ptn) {
      cn = ptn.rawType.simpleName();
    } else {
      cn = resolveClassName(type).simpleName();
    }

    return cn.equals("Quantity") || cn.equals("ComparableQuantity");
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

  default String defaultGetterName(
      ModelDefinition.ComponentDefinition component, GenerationConfig genConfig) {
    return defaultGetterName(
        component.name, component.type, genConfig.booleanGetter, genConfig.nonCapitalizedGetters);
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

  default List<ModelDefinition.ComponentDefinition> resolveParameters(
      List<String> parameterNames,
      Map<String, ModelDefinition.ComponentDefinition> available,
      String context) {

    List<ModelDefinition.ComponentDefinition> result = new ArrayList<>();

    for (String parameterName : parameterNames) {
      ModelDefinition.ComponentDefinition parameter = available.get(parameterName);

      if (parameter == null) {
        throw new IllegalArgumentException(
            "Unknown parameter '" + parameterName + "' in: " + context);
      }

      result.add(parameter);
    }

    return result;
  }

  default void addStatement(
      MethodSpec.Builder builder,
      String componentName,
      GenerationConfig.ConstructorModification modification) {
    if (modification == null) {
      builder.addStatement("this.$L = $L", componentName, componentName);
    } else if (modification.usableClassName()) {
      ClassName className = resolveClassName(modification.className);

      if (modification.insert) {

        if (modification.usableUnitClass()) {
          builder.addStatement(
              "this.$L = " + modification.expression,
              componentName,
              className,
              resolveClassName(modification.unitClass));

        } else {
          builder.addStatement("this.$L = " + modification.expression, componentName, className);
        }

      } else {
        builder.addStatement("this.$L = $T." + modification.expression, componentName, className);
      }

    } else {
      builder.addStatement("this.$L = " + modification.expression, componentName);
    }
  }

  default void addStatement(
      MethodSpec.Builder builder, GenerationConfig.BasicExpression modification) {
    if (modification instanceof GenerationConfig.StandardFields sf) {

      if (!sf.javaDoc.isBlank()) {
        builder.addJavadoc(sf.javaDoc);
      }
    }

    if (modification.usableClassName()) {
      ClassName className = resolveClassName(modification.className);

      if (modification instanceof GenerationConfig.ConstructorModification m && m.insert) {
        if (m.usableUnitClass()) {
          builder.addStatement(modification.expression, className, resolveClassName(m.unitClass));

        } else {
          builder.addStatement(modification.expression, className);
        }

      } else {
        builder.addStatement("$T." + modification.expression, className);
      }

    } else {
      builder.addStatement(modification.expression);
    }
  }
}
