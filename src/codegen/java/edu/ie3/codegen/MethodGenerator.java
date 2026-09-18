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

  private final boolean result;
  private final ModelDefinition model;
  private final GenerationConfig genConfig;
  private final Map<String, ModelDefinition.ComponentDefinition> allComponents;

  public MethodGenerator(
      String packageName,
      ModelDefinition model,
      GenerationConfig genConfig,
      Map<String, ModelDefinition.ComponentDefinition> allComponents) {
    this.result = packageName.contains("result");
    this.model = model;
    this.genConfig = genConfig;
    this.allComponents = allComponents;
  }

  public List<MethodSpec> getGetters() {
    List<MethodSpec> methodSpecs = new ArrayList<>();

    if (genConfig.getters) {
      for (ModelDefinition.ComponentDefinition component : model.components) {
        String getter = defaultGetterName(component, genConfig);
        TypeName returnType = resolveType(component.type);

        var builder = MethodSpec.methodBuilder(getter).addModifiers(Modifier.PUBLIC);

        if (isMap(component)) {
          builder.addStatement("return $T.unmodifiableMap($L)", Collections.class, component.name);
        } else if (!component.required && !ResolverUtils.hasDefaultExpression(component.type)) {
          returnType = ParameterizedTypeName.get(ClassName.get(Optional.class), returnType.box());

          builder.addStatement("return $T.ofNullable($L)", Optional.class, component.name);
        } else {
          builder.addStatement("return $L", component.name);
        }

        methodSpecs.add(builder.returns(returnType).build());
      }
    }

    if (model.components.stream().anyMatch(s -> s.name.equals(ADDITIONAL_INFORMATION))) {
      // type Map<String,String>
      TypeName mapStringString =
          ParameterizedTypeName.get(
              ClassName.get(Map.class), ClassName.get(String.class), ClassName.get(String.class));

      MethodSpec.Builder builder =
          MethodSpec.methodBuilder("setAdditionalInformation")
              .addModifiers(Modifier.PROTECTED)
              .returns(void.class)
              .addParameter(mapStringString, ADDITIONAL_INFORMATION);

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

  public List<MethodSpec> getSetters() {
    List<MethodSpec> methodSpecs = new ArrayList<>();

    for (ModelDefinition.ComponentDefinition component : model.components) {
      if (excludeFromMethods(component, genConfig.excludeFromMethods)) {
        continue;
      }

      String name = component.name;
      String setter = defaultSetterName(component, genConfig);

      var builder =
          MethodSpec.methodBuilder(setter)
              .addModifiers(Modifier.PUBLIC)
              .returns(TypeName.VOID)
              .addParameter(resolveType(component.type), name)
              .addStatement("this.$L = $L", name, name);

      methodSpecs.add(builder.build());
    }

    return methodSpecs;
  }

  public List<MethodSpec> getOtherMethods() {
    List<MethodSpec> methods = new ArrayList<>();

    for (GenerationConfig.MethodDefinition insert : genConfig.methods) {
      var methodBuilder = MethodSpec.methodBuilder(insert.name).returns(resolveType(insert.type));

      insert.modifiers.forEach(m -> methodBuilder.addModifiers(modifiers.get(m)));

      methods.add(enrichBuilder(methodBuilder, insert).build());
    }

    if (genConfig.toMap) {
      methods.add(generateToMap());
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

  private MethodSpec generateToMap() {
    MethodSpec.Builder builder =
        MethodSpec.methodBuilder("toMap")
            .addModifiers(Modifier.PUBLIC)
            .addAnnotation(Override.class)
            .returns(resolveType("SeqStringMap"));

    // add the initial statement
    if (model.extendsName == null || model.extendsName.isBlank()) {
      builder.addStatement(
          "$T<String, String> map = new $T<>()", SequencedMap.class, LinkedHashMap.class);
    } else {
      builder.addStatement("$T<String, String> map = super.toMap()", SequencedMap.class);
    }

    List<String> components = model.components.stream().map(c -> c.name).toList();

    // add the own fields of the model
    for (ModelDefinition.ComponentDefinition component : model.components) {
      String name = component.name;

      if (!name.equalsIgnoreCase(ADDITIONAL_INFORMATION)
          && !genConfig.excludeFromMethods.contains(name)) {

        if (component.keys.isEmpty()) {

          if (isQuantity(component.type)) {
            // we have a quantity and need to add a bit more handling

            String expression =
                component.unit != null
                    ? "QuantityUtils.toString(" + name + ", " + component.unit + ")"
                    : "Double.toString(" + name + ".getValue().doubleValue())";

            var modified = ResolverUtils.modifyExpression(expression);
            builder.addStatement(
                "map.put($S, $L)", name, CodeBlock.of(modified.expression(), modified.args()));

          } else {
            // add the value
            builder.addStatement(
                "map.put($S, $L)", name, toString(component, components, genConfig, true));
          }
        }

      } else {
        // we need some specialized calls here

        for (String key : component.keys) {
          String expression = genConfig.keyMapper.get(key);

          if (expression != null && !expression.isBlank()) {
            var modified = ResolverUtils.modifyExpression(expression);
            builder.addStatement(
                "map.put($S, $L)", key, CodeBlock.of(modified.expression(), modified.args()));
          }
        }
      }
    }

    // if the model is a non-abstract class, we need to add the additional information
    if (!model.isAbstract) {
      builder.addStatement("map.putAll(getAdditionalInformation())");
    }

    // add the return statement
    builder.addStatement("return map");
    return builder.build();
  }

  private MethodSpec generateEquals() {
    MethodSpec.Builder builder =
        MethodSpec.methodBuilder("equals")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(TypeName.BOOLEAN)
            .addParameter(Object.class, "o");

    builder.addStatement("if (this == o) return true");

    List<ModelDefinition.ComponentDefinition> filteredComponents = new ArrayList<>();
    for (ModelDefinition.ComponentDefinition component : model.components) {
      if (excludeFromMethods(component, genConfig.excludeFromMethods)) {
        continue;
      }
      filteredComponents.add(component);
    }

    boolean superStatement = model.extendsName != null && !model.extendsName.isBlank();

    if (filteredComponents.isEmpty()) {
      if (superStatement) {
        builder.addStatement("if (!(o instanceof $L that)) return false", model.name);
        builder.addStatement("return super.equals(o)");
      } else {
        builder.addStatement("return o instanceof $L that", model.name);
      }
    } else {
      builder.addStatement("if (!(o instanceof $L that)) return false", model.name);

      if (superStatement) {
        builder.addStatement("if (!super.equals(o)) return false");
      }

      CodeBlock.Builder expression = CodeBlock.builder();

      for (int index = 0; index < filteredComponents.size(); index++) {
        ModelDefinition.ComponentDefinition component = filteredComponents.get(index);

        if (index > 0) {
          expression.add("\n&& ");
        }

        String name = component.name;
        String type = component.type;

        if (isPrimitive(type)) {
          expression.add("$L == that.$L", name, name);
        } else if (isQuantity(type)) {
          expression.add("$T.equals($L, that.$L)", resolveClassName("QuantityUtils"), name, name);
        } else {
          expression.add("$T.equals($L, that.$L)", Objects.class, name, name);
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
      if (excludeFromMethods(component, genConfig.excludeFromMethods)) {
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
    Collection<ModelDefinition.ComponentDefinition> all = allComponents.values();
    List<String> allComponentNames = all.stream().map(c -> c.name).toList();

    int index = 0;
    for (ModelDefinition.ComponentDefinition component : all) {
      if (excludeFromMethods(component, genConfig.excludeFromMethods)) {
        continue;
      }

      String prefix = (index == 0) ? component.name + "=" : ", " + component.name + "=";

      builder.addCode(
          CodeBlock.of("    + $S + " + toString(component, components, genConfig, false), prefix)
              + "\n");

      index++;
    }

    if (allComponentNames.contains(ADDITIONAL_INFORMATION)) {
      builder.addStatement(
          "    + \", additionalInformation=\" + getAdditionalInformation()\n+ \"}\"");
    } else {
      builder.addStatement("    + \"}\"");
    }

    return builder.build();
  }
}
