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
import javax.lang.model.element.Modifier;

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

    String valueGetterExpression = "$L";
    String getterName =
        components.contains(componentName)
            ? componentName
            : defaultGetterName(component, genConfig) + "()";

    if (!component.required && component.nested) {
      // we need some special calls here
      valueGetterExpression += ".map(e -> e.getUuid().toString()).orElse(\"\")";
      getterName = defaultGetterName(component, genConfig) + "()";
      explicitConversion = false;
    } else if (component.nested) {
      valueGetterExpression += ".getUuid()";
    }

    if (explicitConversion) {
      valueGetterExpression += ".toString()";
    }

    return CodeBlock.of(valueGetterExpression + "\n", getterName);
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
   * Check if the class is a quantity.
   *
   * @param type of the field
   * @return true, if the type is a quantity
   */
  default boolean isQuantity(String type) {
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
    if (genConfig.fieldNameGetters.contains(name)) {
      // if we should use the field name directly
      return name;
    }

    // a list with getters that should not be capitalized
    List<String> nonCapitalized = genConfig.nonCapitalized;

    // a list of boolean getters that should use `get` instead of `is`
    List<String> booleanGetter = genConfig.booleanGetter;

    String methodName;

    if (nonCapitalized.contains(name)) {
      methodName = name;
    } else {
      methodName = capitalize(name);
    }

    if (isPrimitive(type)) {
      if ("bool".equals(type) && !booleanGetter.contains(name)) {
        return "is" + methodName;
      }
    }

    return "get" + methodName;
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
   * Method for getting the default setter name.
   *
   * @param component definition to use
   * @param genConfig generation config to use
   * @return the name of the setter method
   */
  default String defaultSetterName(
      ModelDefinition.ComponentDefinition component, GenerationConfig genConfig) {
    String methodName;
    String name = component.name;

    if (genConfig.nonCapitalized.contains(name)) {
      methodName = name;
    } else {
      methodName = capitalize(name);
    }

    return "set" + methodName;
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
  default boolean excludeFromMethods(
      ModelDefinition.ComponentDefinition component, List<String> excludeFromMethods) {
    return "additionalInformation".equals(component.name)
        || excludeFromMethods.contains(component.name);
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

  default MethodSpec.Builder enrichBuilder(
      MethodSpec.Builder builder, GenerationConfig.MethodFields insert) {
    // add the parameters to the method
    for (ModelDefinition.Parameter parameter : insert.parameters) {
      builder.addParameter(resolveType(parameter.type), parameter.name);
    }

    // add Javadoc if present
    if (!insert.javaDoc.isBlank()) {
      builder.addJavadoc(insert.javaDoc);
    }

    if (insert.isAbstract) {
      // if the method should be abstract, we need to add the modifier
      builder.addModifiers(Modifier.ABSTRACT);

    } else {
      if (insert.annotation) {
        // add an annotation if needed
        builder.addAnnotation(Override.class);
      }

      addStatement(builder, insert);
    }

    return builder;
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

    } else if (modification.usableUnitClass()) {
      builder.addStatement(
          "this.$L = " + modification.expression,
          componentName,
          resolveClassName(modification.unitClass));
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

    String expression = modification.expression;
    String prefix = "";

    if (modification instanceof GenerationConfig.MethodFields mf && mf.addReturn) {
      prefix = "return ";
    }

    if (expression != null && expression.contains("\n")) {
      if (modification.usableClassName()) {
        builder.addStatement(prefix + expression, resolveClassName(modification.className));
      } else {
        builder.addStatement(prefix + expression);
      }

    } else if (modification.usableClassName()) {
      ClassName className = resolveClassName(modification.className);

      if (modification.usableUnitClass()) {
        builder.addStatement(
            prefix + expression, className, resolveClassName(modification.unitClass));

      } else {
        builder.addStatement(prefix + "$T." + expression, className);
      }

    } else {
      builder.addStatement(prefix + expression);
    }
  }
}
