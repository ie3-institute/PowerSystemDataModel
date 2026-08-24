/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import static edu.ie3.codegen.ResolverUtils.resolveClassName;
import static edu.ie3.codegen.ResolverUtils.resolveType;

import com.palantir.javapoet.*;
import java.util.*;

/** Interface containing some helper methods. */
public interface HelperMethods {

  /**
   * Method for returning the value used in the {@code toString()} method.
   *
   * @param component definition
   * @param components all component names of the current model.
   * @param genConfig generation config of the model
   * @param explicitConversion if {@code true} a {@code toString()} call is added
   * @return the resulting code block
   */
  default CodeBlock toString(
      ModelDefinition.ComponentDefinition component,
      List<String> components,
      GenerationConfig genConfig,
      boolean explicitConversion) {
    String componentName = component.name;

    // check if the return type is string
    if (component.type.equals("String")) {
      explicitConversion = false;
    }

    String valueGetterExpression;
    boolean optional = false;
    String getterName;

    if (component.nullable) {
      // we need some special calls here, since the
      valueGetterExpression = "$T.ofNullable($L)";
      optional = true;
      getterName =
          components.contains(componentName)
              ? componentName
              : defaultGetterName(component, genConfig);

      valueGetterExpression += ".map(e -> e.getUuid().toString()).orElse(\"\")";
      explicitConversion = false;

    } else {
      valueGetterExpression = component.nested ? "$L.getUuid()" : "$L";
      getterName =
          components.contains(componentName)
              ? componentName
              : defaultGetterName(component, genConfig) + "()";
    }

    if (explicitConversion) {
      // add an explicit toString call
      valueGetterExpression += ".toString()";
    }

    if (optional) {
      return CodeBlock.of(valueGetterExpression + "\n", Optional.class, getterName);
    } else {
      return CodeBlock.of(valueGetterExpression + "\n", getterName);
    }
  }

  /**
   * Checks if the provided type is primitive.
   *
   * @param type given type
   * @return true, if the type is primitive
   */
  default boolean isPrimitive(String type) {
    return switch (type) {
      case "bool", "int", "double", "float" -> true;
      default -> false;
    };
  }

  /**
   * Check if we should use an {@code equals()} call.
   *
   * @param type of the field
   * @return true, if an {@code equals()} call should be used
   */
  default boolean useEquals(String type) {
    String cn;

    if (resolveType(type) instanceof ParameterizedTypeName ptn) {
      cn = ptn.rawType().simpleName();
    } else {
      cn = resolveClassName(type).simpleName();
    }

    return cn.equals("Quantity") || cn.equals("ComparableQuantity");
  }

  /**
   * Method for getting the default getter name.
   *
   * @param name of the field
   * @param type of the field
   * @param genConfig generation config to use
   * @return the name of the getter method
   */
  default String defaultGetterName(String name, String type, GenerationConfig genConfig) {
    if (genConfig.fieldNameGetters) {
      // if we should use the field name directly
      return name;
    }

    // a list with getters that should not be capitalized
    List<String> nonCapitalizedGetters = genConfig.nonCapitalizedGetters;

    // a list of boolean getters that should use `get` instead of `is`
    List<String> booleanGetter = genConfig.booleanGetter;

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

  /**
   * Method for getting the default getter name.
   *
   * @param component definition to use
   * @param genConfig generation config to use
   * @return the name of the getter method
   */
  default String defaultGetterName(
      ModelDefinition.ComponentDefinition component, GenerationConfig genConfig) {
    return defaultGetterName(component.name, component.type, genConfig);
  }

  /**
   * Method for capitalizing the first character of a string.
   *
   * @param value to be capitalized
   * @return the capitalized string
   */
  default String capitalize(String value) {
    return Character.toUpperCase(value.charAt(0)) + value.substring(1);
  }

  /**
   * Checks if a given component should not be considered.
   *
   * @param component definition to use
   * @return true, if the component should not be used
   */
  default boolean excludeFromMethods(ModelDefinition.ComponentDefinition component) {
    return "additionalInformation".equals(component.name);
  }

  /**
   * Checks if the type of the given component is map.
   *
   * @param component definition to use
   * @return true, if the type is map
   */
  default boolean isMap(ModelDefinition.ComponentDefinition component) {
    return component.type.equals("StringMap");
  }

  /**
   * Method for finding all visible components in the hierarchy.
   *
   * @param model definition of the current model
   * @param models all available models
   * @return a map: model name to definition
   */
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

  /**
   * Method to walk the model hierarchy.
   *
   * @param model definition of the current model
   * @param models all available models
   * @return a list of all models in the hierarchy starting with the current model
   */
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

  /**
   * Method to return the parent model.
   *
   * @param name of the parent model
   * @param models all available models
   * @return the parent model definition
   */
  default ModelDefinition getParent(String name, Map<String, ModelDefinition> models) {
    return models.get(name);
  }

  /**
   * Method for resolving parameters.
   *
   * @param parameterNames names of the parameters
   * @param available all available models
   * @param context to use
   * @return a list of components
   */
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

  /**
   * Adds a statement to a method builder.
   *
   * @param builder current builder
   * @param componentName name of the component
   * @param modification modification to use
   */
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

  /**
   * Adds a statement to a method builder.
   *
   * @param builder current builder
   * @param modification modification to use
   */
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
