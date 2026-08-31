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

public class MethodGenerator implements HelperMethods {

  private final ModelDefinition model;
  private final GenerationConfig genConfig;
  private final Map<String, ModelDefinition> models;

  public MethodGenerator(
      ModelDefinition model, GenerationConfig genConfig, Map<String, ModelDefinition> models) {
    this.model = model;
    this.genConfig = genConfig;
    this.models = models;
  }

  public List<MethodSpec> getGetters() {
    List<MethodSpec> methodSpecs = new ArrayList<>();

    if (genConfig.getters) {
      for (ModelDefinition.ComponentDefinition component : model.components) {
        if (!genConfig.noGetters.contains(component.name)) {
          List<String> optionalGetter = genConfig.optionalGetters;

          String getter = defaultGetterName(component, genConfig);
          TypeName returnType = resolveType(component.type);

          var builder = MethodSpec.methodBuilder(getter).addModifiers(Modifier.PUBLIC);

          if (isMap(component)) {
            builder.addStatement(
                "return $T.unmodifiableMap($L)", Collections.class, component.name);
          } else if (optionalGetter != null && optionalGetter.contains(component.name)) {
            // TODO: options != null && options.optional -> component.required
            returnType = ParameterizedTypeName.get(ClassName.get(Optional.class), returnType.box());

            builder.addStatement("return $T.ofNullable($L)", Optional.class, component.name);
          } else {
            builder.addStatement("return $L", component.name);
          }

          methodSpecs.add(builder.returns(returnType).build());
        }
      }
    }

    if (model.components.stream().anyMatch(s -> s.name.equals("additionalInformation"))) {
      // type Map<String,String>
      TypeName mapStringString =
          ParameterizedTypeName.get(
              ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(String.class));

      MethodSpec.Builder builder =
          MethodSpec.methodBuilder("setAdditionalInformation")
              .addModifiers(Modifier.PROTECTED)
              .returns(void.class)
              .addParameter(mapStringString, "additionalInformation");

      // if (additionalInformation == null) return;
      builder
          .beginControlFlow("if (additionalInformation == null)")
          .addStatement("return")
          .endControlFlow();

      // this.additionalInformation.putAll(additionalInformation);
      builder.addStatement("this.additionalInformation.putAll(additionalInformation)");

      methodSpecs.add(builder.build());
    }

    return methodSpecs;
  }

  public List<MethodSpec> getOtherMethods() {
    List<MethodSpec> methods = new ArrayList<>();

    for (GenerationConfig.MethodOverride override : genConfig.methodOverrides) {
      var methodBuilder =
          MethodSpec.methodBuilder(override.name)
              .returns(resolveType(override.type))
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PUBLIC);

      if (override.className != null && !override.className.isBlank()) {
        methodBuilder.addStatement(
            "return $T." + override.expression, resolveClassName(override.className));
      } else {
        methodBuilder.addStatement("return " + override.expression);
      }

      methods.add(methodBuilder.build());
    }

    for (GenerationConfig.MethodInsert insert : genConfig.methodInserts) {
      var methodBuilder =
          MethodSpec.methodBuilder(insert.name)
              .returns(resolveType(insert.type))
              .addModifiers(Modifier.PUBLIC);

      if (!insert.javaDoc.isBlank()) {
        methodBuilder.addJavadoc(insert.javaDoc);
      }

      if (insert.isAbstract) {
        methodBuilder.addModifiers(Modifier.ABSTRACT);

      } else {
        if (insert.className != null && !insert.className.isBlank()) {
          methodBuilder.addStatement(
              "return $T." + insert.expression, resolveClassName(insert.className));
        } else {
          methodBuilder.addStatement("return " + insert.expression);
        }
      }

      methods.add(methodBuilder.build());
    }

    if (genConfig.equals) {
      methods.add(generateEquals());
    }

    if (genConfig.hashCode) {
      methods.add(generateHashCode());
    }

    if (genConfig.toString) {
      methods.add(generateToString());
    }

    return methods;
  }

  private MethodSpec generateEquals() {
    MethodSpec.Builder builder =
        MethodSpec.methodBuilder("equals")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.BOOLEAN)
            .addParameter(Object.class, "o");

    builder.addStatement("if (this == o) return true");
    builder.addStatement("if (!(o instanceof $L that)) return false", model.name);

    List<ModelDefinition.ComponentDefinition> filteredComponents = new ArrayList<>();
    for (ModelDefinition.ComponentDefinition component : model.components) {
      if (excludeFromMethods(component)) {
        continue;
      }
      filteredComponents.add(component);
    }

    boolean superStatement = model.extendsName != null && !model.extendsName.isBlank();

    if (filteredComponents.isEmpty()) {
      if (superStatement) {
        builder.addStatement("return super.equals(o)");
      } else {
        builder.addStatement("if (!super.equals(o)) return false");
        builder.addStatement("return true");
      }
    } else {
      if (superStatement) {
        builder.addStatement("if (!super.equals(o)) return false");
      }

      CodeBlock.Builder expression = CodeBlock.builder();

      for (int index = 0; index < filteredComponents.size(); index++) {
        ModelDefinition.ComponentDefinition component = filteredComponents.get(index);

        if (index > 0) {
          expression.add("\n&& ");
        }

        String type = component.type;

        if (isPrimitive(type)) {
          expression.add("$L == that.$L", component.name, component.name);
        } else if (useEquals(type)) {
          expression.add("$L.equals(that.$L)", component.name, component.name);
        } else {
          expression.add("$T.equals($L, that.$L)", Objects.class, component.name, component.name);
        }
      }

      builder.addStatement("return $L", expression.build());
    }

    return builder.build();
  }

  private MethodSpec generateHashCode() {
    MethodSpec.Builder builder =
        MethodSpec.methodBuilder("hashCode")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.INT);

    List<CodeBlock> arguments = new ArrayList<>();

    if (model.extendsName != null && !model.extendsName.isBlank()) {
      arguments.add(CodeBlock.of("super.hashCode()"));
    }

    for (ModelDefinition.ComponentDefinition component : model.components) {
      if (excludeFromMethods(component)) {
        continue;
      }

      arguments.add(CodeBlock.of("$L", component.name));
    }

    if (arguments.isEmpty()) {
      builder.addStatement("return 0");
    } else {
      builder.addStatement("return $T.hash($L)", Objects.class, CodeBlock.join(arguments, ", "));
    }

    return builder.build();
  }

  private MethodSpec generateToString() {
    MethodSpec.Builder builder =
        MethodSpec.methodBuilder("toString")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(String.class);

    builder.addCode("return $S\n", model.name + "{");

    List<String> components = model.components.stream().map(c -> c.name).toList();
    Collection<ModelDefinition.ComponentDefinition> allComponents =
        visibleComponents(model, models).values();
    List<String> allComponentNames = allComponents.stream().map(c -> c.name).toList();

    int index = 0;
    for (ModelDefinition.ComponentDefinition component : allComponents) {
      if (excludeFromMethods(component)) {
        continue;
      }

      String prefix = (index == 0) ? component.name + "=" : ", " + component.name + "=";

      builder.addCode(
          CodeBlock.of("    + $S + " + toString(component, components, genConfig, false), prefix));

      index++;
    }

    if (allComponentNames.contains("additionalInformation")) {
      builder.addStatement(
          "    + \", additionalInformation=\" + getAdditionalInformation()\n+ \"}\"");
    } else {
      builder.addStatement("    + \"}\"");
    }

    return builder.build();
  }
}
