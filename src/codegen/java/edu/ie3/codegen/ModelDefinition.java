package edu.ie3.codegen;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static edu.ie3.codegen.HelperMethods.defaultGetterName;
import static edu.ie3.codegen.HelperMethods.resolveType;

public final class ModelDefinition implements HelperMethods {
    public String name;

    @JsonProperty("package")
    public String packageName;

    public String kind = "class"; // or abstractClass

    @JsonProperty("extends")
    public String extendsName;

    public List<String> inherits = new ArrayList<>();

    public String conversionUtils;

    public List<StaticFieldDefinition> staticFields = new ArrayList<>();

    public List<ComponentDefinition> components = new ArrayList<>();

    public List<FieldSpec> getStaticFields() {
        return staticFields.stream().map(staticField -> {
            if ("additionalInformation".equals(staticField.name)) {
                return FieldSpec.builder(resolveType(staticField.type, packageName), staticField.name, Modifier.PRIVATE, Modifier.FINAL)
                        .initializer("new $T<>()", hashMap)
                        .build();
            } else {
                return FieldSpec.builder(
                                resolveType(staticField.type, packageName),
                                staticField.name,
                                Modifier.PUBLIC,
                                Modifier.STATIC,
                                Modifier.FINAL)
                        .initializer("$L", staticField.expression)
                        .build();
            }
        }).toList();
    }

    public List<FieldSpec> getPrivateFields() {
        return components.stream().map(component ->
                FieldSpec.builder(
                                resolveType(component.type, packageName),
                                component.name,
                                Modifier.PRIVATE,
                                Modifier.FINAL)
                        .build()
        ).toList();
    }

    public List<MethodSpec> getAllMethods(GenerationConfig genConfig) {
        List<MethodSpec> methodSpecs = new ArrayList<>();

        if (genConfig.getters) {
            for (ComponentDefinition component : components) {
                String getter = defaultGetterName(component);

                methodSpecs.add(MethodSpec.methodBuilder(getter)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(resolveType(component.type, packageName))
                        .addStatement("return $L", component.name)
                        .build());
            }
        }

        if (components.stream().anyMatch(s -> s.name.equals("additionalInformation"))) {
            // type Map<String,String>
            TypeName mapStringString = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(String.class)
            );

            MethodSpec.Builder builder = MethodSpec.methodBuilder("setAdditionalInformation")
                    .addModifiers(Modifier.PROTECTED)
                    .returns(void.class)
                    .addParameter(mapStringString, "additionalInformation");

            // if (additionalInformation == null) return;
            builder.beginControlFlow("if (additionalInformation == null)")
                    .addStatement("return")
                    .endControlFlow();

            // this.additionalInformation.putAll(additionalInformation);
            builder.addStatement("this.additionalInformation.putAll(additionalInformation)");

            methodSpecs.add(builder.build());
        }

        return methodSpecs;
    }

    public static final class StaticFieldDefinition {
        public String name;
        public String type;
        public String expression;
    }

    public static final class ComponentDefinition {
        public String name;
        public String type;

        public List<String> keys = new ArrayList<>();

        public String keyMode = "ALL";

        public boolean resolve;

        public boolean required = true;

        @JsonProperty("default")
        public String defaultValue;

        public String getter;
    }
}
