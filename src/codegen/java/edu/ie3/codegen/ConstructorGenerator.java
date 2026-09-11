/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import static edu.ie3.codegen.ResolverUtils.resolveType;

import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.MethodSpec;
import java.util.*;
import javax.lang.model.element.Modifier;

/** Class for generating constructors. */
public final class ConstructorGenerator implements HelperMethods {

  private final ModelDefinition model;
  private final GenerationConfig genConfig;
  private final Map<String, ModelDefinition> models;

  public ConstructorGenerator(
      ModelDefinition model, GenerationConfig genConfig, Map<String, ModelDefinition> models) {
    this.model = model;
    this.genConfig = genConfig;
    this.models = models;
  }

  /** Returns all constructors that should be added to the generated class. */
  public List<MethodSpec> getConstructors() {
    return genConfig.constructors.stream().map(this::generateConstructor).toList();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // helper methods

  /**
   * Generates a constructor base on the given definition.
   *
   * @param constructor definition to use.
   * @return a constructor method.
   */
  private MethodSpec generateConstructor(GenerationConfig.ConstructorDefinition constructor) {
    List<String> components = constructor.components;
    String[] ordered = components.toArray(String[]::new);

    // looking for all visible components
    Map<String, ModelDefinition.ComponentDefinition> visibleComponents =
        visibleComponents(model, models);

    Map<String, ModelDefinition.ComponentDefinition> parameters = new HashMap<>();

    if (constructor.additionalComponents.isEmpty()) {
      // since no modification to the parameters was given, we only use the visible components
      // if a field is not initialized by either a component or a default expression, then an
      // exception will be thrown
      resolveParameters(components, visibleComponents, model.name + "." + constructor.name)
          .forEach(c -> parameters.put(c.name, c));
    } else {
      List<String> additional = constructor.additionalComponents.stream().map(c -> c.name).toList();

      // only add the specified components, all other fields need to be initialized by the
      // additional components and
      // a constructor modification
      visibleComponents.values().stream()
          .filter(c -> components.contains(c.name) && !additional.contains(c.name))
          .forEach(c -> parameters.put(c.name, c));

      constructor.additionalComponents.forEach(c -> parameters.put(c.name, c));
    }

    List<ModelDefinition.ComponentDefinition> orderedComponents = new ArrayList<>();

    Modifier modifier;

    // selects the modifier of the constructor
    if (constructor.isPrivate) {
      modifier = Modifier.PRIVATE;
    } else if (!model.isAbstract) {
      modifier = Modifier.PUBLIC;
    } else {
      modifier = Modifier.PROTECTED;
    }

    // creates the builder
    MethodSpec.Builder builder = MethodSpec.constructorBuilder().addModifiers(modifier);

    // add Javadoc if present
    if (!constructor.javaDoc.isBlank()) {
      builder.addJavadoc(constructor.javaDoc);
    }

    // add the components with their type to the parameter list
    for (String parameterName : ordered) {
      ModelDefinition.ComponentDefinition parameter = parameters.get(parameterName);
      builder.addParameter(resolveType(parameter.type), parameter.name);

      if (model.components.contains(parameter)) {
        orderedComponents.add(parameter);
      }
    }

    // check if we need a super block
    CodeBlock superArgs;
    if (model.extendsName != null && constructor.superArgs != null) {
      List<CodeBlock> argBlocks = new ArrayList<>();
      for (String arg : constructor.superArgs) {
        argBlocks.add(CodeBlock.of("$L", arg));
      }
      superArgs = CodeBlock.join(argBlocks, ", ");
      builder.addStatement("super($L)", superArgs);
    } else if (model.extendsName != null) {
      // no superCall specified -> require explicit mapping or throw
      throw new IllegalArgumentException(
          "Model "
              + model.name
              + " requires a superCall config for constructor "
              + constructor.name);
    }

    for (ModelDefinition.ComponentDefinition componentDefinition : model.components) {
      if (!orderedComponents.contains(componentDefinition)) {
        orderedComponents.add(componentDefinition);
      }
    }

    // initializing all necessary fields
    for (ModelDefinition.ComponentDefinition localField : orderedComponents) {
      String componentName = localField.name;

      boolean isConstructorParameter =
          parameters.values().stream().anyMatch(parameter -> parameter.name.equals(componentName));

      if (!isConstructorParameter) {
        // if a parameter cannot be initialized by the provided constructor arguments, we try to use
        // a default expression
        Optional<String> defaultExpression = ResolverUtils.getDefaultExpression(localField.type);

        if (defaultExpression.isPresent() && !defaultExpression.get().isBlank()) {
          // add the default expression
          builder.addStatement("this.$L = $L", componentName, defaultExpression.get());

        } else if (!componentName.equals("additionalInformation")
            && !constructor.constructorModifications.containsKey(componentName)) {
          // if no default expression was defined and no modification was defined, an exception is
          // thrown
          throw new IllegalArgumentException(
              "Constructor '"
                  + constructor.name
                  + "' of "
                  + model.name
                  + " does not initialize local field '"
                  + componentName
                  + "'.");
        } else {
          // since we have a modification, we will insert it here
          addStatement(
              builder, componentName, constructor.constructorModifications.get(componentName));
        }

      } else {
        // if we have a modification, we will insert it here, else we initialized the field with the
        // provided value
        addStatement(
            builder, componentName, constructor.constructorModifications.get(componentName));
      }
    }

    // we add all defined constructor checks
    constructor.constructorChecks.forEach(c -> addStatement(builder, c));

    boolean hasAdditionalInfoParam =
        parameters.values().stream().anyMatch(p -> "additionalInformation".equals(p.name));

    if (!constructor.valuesMap.isBlank()) {
      for (ModelDefinition.ComponentDefinition localField : orderedComponents) {
        builder.addStatement(
            "$L.put($S, $L)", constructor.valuesMap, localField.name, localField.name);
      }
    }

    if (hasAdditionalInfoParam) {
      // if the constructor contains additional information, we call the setter
      builder.addStatement("setAdditionalInformation(additionalInformation)");
    }

    // we build the constructor and return it
    return builder.build();
  }
}
