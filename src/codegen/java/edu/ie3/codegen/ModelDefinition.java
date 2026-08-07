/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import static edu.ie3.codegen.HelperMethods.*;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.squareup.javapoet.*;
import java.util.*;
import javax.lang.model.element.Modifier;

public final class ModelDefinition implements HelperMethods {
  public String name;

  @JsonProperty("package")
  public String packageName;

  @JsonProperty("class")
  public boolean isClass = false;

  @JsonProperty("extends")
  public String extendsName;

  public List<ComponentDefinition> components = new ArrayList<>();

  public List<FieldSpec> getPrivateFields() {
    return components.stream()
        .map(
            component -> {
              var builder =
                  FieldSpec.builder(
                      resolveType(component.type, packageName),
                      component.name,
                      Modifier.PRIVATE,
                      Modifier.FINAL);

              if (!component.javaDoc.isBlank()) {
                builder.addJavadoc(component.javaDoc);
              }

              return builder.build();
            })
        .toList();
  }

  public List<MethodSpec> getAllMethods(GenerationConfig genConfig) {
    List<MethodSpec> methodSpecs = new ArrayList<>();

    if (genConfig.getters) {
      for (ComponentDefinition component : components) {
        GenerationConfig.GetterOptions options = genConfig.getterOptions.get(component.name);

        String getter = defaultGetterName(component.name, component.type, options);
        TypeName returnType = resolveType(component.type, packageName);

        var builder = MethodSpec.methodBuilder(getter).addModifiers(Modifier.PUBLIC);

        if (options != null && !options.javaDoc.isBlank()) {
          builder.addJavadoc(options.javaDoc);
        }

        if (isMap(component)) {
          builder.addStatement("return $T.unmodifiableMap($L)", Collections.class, component.name);
        } else if (options != null && options.optional) {
          returnType = ParameterizedTypeName.get(ClassName.get(Optional.class), returnType.box());

          builder.addStatement("return $T.ofNullable($L)", Optional.class, component.name);
        } else {
          builder.addStatement("return $L", component.name);
        }

        methodSpecs.add(builder.returns(returnType).build());
      }
    }

    if (components.stream().anyMatch(s -> s.name.equals("additionalInformation"))) {
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

  public static final class ComponentDefinition {
    public String name;
    public String type;
    public List<String> keys = new ArrayList<>();
    public boolean required = true;
    public String javaDoc = "";
  }
}
