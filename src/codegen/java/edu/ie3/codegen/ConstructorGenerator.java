/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import static edu.ie3.codegen.HelperMethods.*;

import com.squareup.javapoet.*;
import edu.ie3.codegen.ModelDefinition.ComponentDefinition;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Modifier;

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

  public List<MethodSpec> getConstructors() {
    return genConfig.constructors.stream().map(this::generateConstructor).toList();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // helper methods

  private List<ComponentDefinition> resolveParameters(
      List<String> parameterNames, Map<String, ComponentDefinition> available, String context) {

    List<ComponentDefinition> result = new ArrayList<>();

    for (String parameterName : parameterNames) {
      ComponentDefinition parameter = available.get(parameterName);

      if (parameter == null) {
        throw new IllegalArgumentException(
            "Unknown parameter '" + parameterName + "' in " + context);
      }

      result.add(parameter);
    }

    return result;
  }

  private MethodSpec generateConstructor(GenerationConfig.ConstructorDefinition constructor) {

    Map<String, ModelDefinition.ComponentDefinition> visibleComponents =
        visibleComponents(model, models);

    List<ModelDefinition.ComponentDefinition> parameters =
        resolveParameters(
            constructor.components, visibleComponents, model.name + "." + constructor.name);

    Modifier modifier;

    if (model.isClass) {
      modifier = Modifier.PUBLIC;
    } else {
      modifier = Modifier.PROTECTED;
    }

    MethodSpec.Builder builder = MethodSpec.constructorBuilder().addModifiers(modifier);

    if (!constructor.javaDoc.isBlank()) {
      builder.addJavadoc(constructor.javaDoc);
    }

    for (ModelDefinition.ComponentDefinition parameter : parameters) {
      builder.addParameter(resolveType(parameter.type, model.packageName), parameter.name);
    }

    CodeBlock superArgs;
    if (model.extendsName != null && constructor.superArgs != null) {
      List<CodeBlock> argBlocks = new ArrayList<>();
      for (String arg : constructor.superArgs) {
        // If arg equals a parameter name, use variable; else if it's a visible component name, use
        // variable;
        // otherwise treat as raw Java expression
        if (visibleComponents.containsKey(arg)) {
          argBlocks.add(CodeBlock.of("$L", arg)); // variable reference
        } else {
          // raw expression (e.g. "OperatorInput.NO_OPERATOR_ASSIGNED")
          argBlocks.add(CodeBlock.of("$L", arg));
        }
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

    for (ModelDefinition.ComponentDefinition localField : model.components) {
      String componentName = localField.name;

      boolean isConstructorParameter =
          parameters.stream().anyMatch(parameter -> parameter.name.equals(componentName));

      TypeRegistry.TypeDefinition type = TypeRegistry.get(localField.type);

      if (!isConstructorParameter) {
        if (type.defaultExpression != null && !type.defaultExpression.isBlank()) {
          builder.addStatement("this.$L = $L", componentName, type.defaultExpression);

        } else if (!componentName.equals("additionalInformation")
            && !constructor.constructorModifications.containsKey(componentName)) {
          throw new IllegalArgumentException(
              "Constructor '"
                  + constructor.name
                  + "' of "
                  + model.name
                  + " does not initialize local field '"
                  + componentName
                  + "'.");
        } else {
          GenerationConfig.ConstructorModification modification =
              constructor.constructorModifications.get(componentName);

          if (modification.className != null && !modification.className.isBlank()) {

            if (modification.insert) {

              if (modification.unitClass != null && !modification.unitClass.isBlank()) {
                builder.addStatement(
                    "this.$L = " + modification.expression,
                    componentName,
                    getClassName(modification.className),
                    getClassName(modification.unitClass));

              } else {
                builder.addStatement(
                    "this.$L = " + modification.expression,
                    componentName,
                    getClassName(modification.className));
              }

            } else {
              builder.addStatement(
                  "this.$L = $T." + modification.expression,
                  componentName,
                  getClassName(modification.className));
            }

          } else {
            builder.addStatement("this.$L = " + modification.expression, componentName);
          }
        }

      } else {
        if (constructor.constructorModifications.containsKey(componentName)) {
          GenerationConfig.ConstructorModification modification =
              constructor.constructorModifications.get(componentName);

          if (modification.className != null && !modification.className.isBlank()) {

            if (modification.insert) {

              if (modification.unitClass != null && !modification.unitClass.isBlank()) {
                builder.addStatement(
                    "this.$L = " + modification.expression,
                    componentName,
                    getClassName(modification.className),
                    getClassName(modification.unitClass));

              } else {
                builder.addStatement(
                    "this.$L = " + modification.expression,
                    componentName,
                    getClassName(modification.className));
              }

            } else {
              builder.addStatement(
                  "this.$L = $T." + modification.expression,
                  componentName,
                  getClassName(modification.className));
            }

          } else {
            builder.addStatement("this.$L = " + modification.expression, componentName);
          }

        } else {
          builder.addStatement("this.$L = $L", componentName, componentName);
        }
      }
    }

    for (GenerationConfig.ConstructorCheck check : constructor.constructorChecks) {

      if (check.className != null && !check.className.isBlank()) {
        builder.addStatement("$T." + check.expression, getClassName(check.className));
      } else {
        builder.addStatement(check.expression);
      }
    }

    boolean hasAdditionalInfoParam =
        parameters.stream().anyMatch(p -> "additionalInformation".equals(p.name));

    if (hasAdditionalInfoParam) {
      builder.addStatement("setAdditionalInformation(additionalInformation)");
    }

    return builder.build();
  }
}
