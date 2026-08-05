package edu.ie3.codegen;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static edu.ie3.codegen.HelperMethods.*;

final class ModelGenerator implements HelperMethods {

    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println(
                    "Usage: ModelGenerator <output-directory>");
            System.exit(1);
        }

        Path resources = Path.of(".", "src", "codegen", "resources");
        Path modelsFile = resources.resolve("models.yaml");
        Path generationConfig = resources.resolve("generation.yaml");
        Path outputDirectory = Path.of(args[0]);

        // read in all information
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

        ModelFile modelFile = mapper.readValue(modelsFile.toFile(), ModelFile.class);
        GenerationConfigFile genConfig = mapper.readValue(generationConfig.toFile(), GenerationConfigFile.class);


        generateAll(modelFile.models, genConfig.models, outputDirectory);
    }

    public static void generateAll(Map<String, ModelDefinition> models, Map<String, GenerationConfig> generationConfigs, Path outputDirectory) throws IOException {
        for (ModelDefinition model : models.values()) {
            String name = model.name;

            if (!generationConfigs.containsKey(name)) {
               System.out.println("No configuration present for: " + name + " Skipping generation.");
            } else {
                generate(model, generationConfigs.get(name), models, outputDirectory);
            }
        }
    }

    public static void generate(ModelDefinition model, GenerationConfig genConfig, Map<String, ModelDefinition> models, Path outputDirectory) throws IOException {
        TypeSpec.Builder typeBuilder =
                TypeSpec.classBuilder(model.name)
                        .addModifiers(Modifier.PUBLIC);

        if ("abstractClass".equals(model.kind)) {
            typeBuilder.addModifiers(Modifier.ABSTRACT);
        }

        if (model.extendsName != null && !model.extendsName.isBlank()) {
            typeBuilder.superclass(resolveClassName(model.extendsName, model.packageName));
        }

        for (String interfaceName : model.inherits) {
            typeBuilder.addSuperinterface(resolveClassName(interfaceName, model.packageName));
        }

        typeBuilder.addFields(model.getStaticFields());
        typeBuilder.addFields(model.getPrivateFields());

        ConstructorGenerator constructorGenerator = new ConstructorGenerator(model, genConfig, models);


        typeBuilder.addMethods(constructorGenerator.getConstructors());


        typeBuilder.addMethods(model.getAllMethods(genConfig));


        if (genConfig.fromMap && "class".equals(model.kind)) {
            typeBuilder.addMethod(constructorGenerator.getFromMapConstructor());
        }

        if (genConfig.equals) {
            typeBuilder.addMethod(generateEquals(model));
        }

        if (genConfig.hashCode) {
            typeBuilder.addMethod(generateHashCode(model));
        }

        if (genConfig.toString) {
            typeBuilder.addMethod(generateToString(model, models));
        }

        JavaFile.builder(model.packageName, typeBuilder.build())
                .skipJavaLangImports(true)
                .build()
                .writeTo(outputDirectory);
    }

    private static MethodSpec generateEquals(ModelDefinition model) {
        MethodSpec.Builder builder =
                MethodSpec.methodBuilder("equals")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.BOOLEAN)
                        .addParameter(Object.class, "o");

        builder.beginControlFlow("if (this == o)")
                .addStatement("return true")
                .endControlFlow();

        builder.beginControlFlow("if (!(o instanceof $L that))", model.name)
                .addStatement("return false")
                .endControlFlow();

        if (model.extendsName != null && !model.extendsName.isBlank()) {
            builder.beginControlFlow("if (!super.equals(o))")
                    .addStatement("return false")
                    .endControlFlow();
        }

        if (model.components.isEmpty()) {
            builder.addStatement("return true");
            return builder.build();
        }

        CodeBlock.Builder expression = CodeBlock.builder();

        for (int index = 0; index < model.components.size(); index++) {
            ModelDefinition.ComponentDefinition component = model.components.get(index);

            if (index > 0) {
                expression.add("\n&& ");
            }

            if (isPrimitive(component.type)) {
                expression.add("$L == that.$L", component.name, component.name);
            } else {
                expression.add(
                        "$T.equals($L, that.$L)",
                        OBJECTS,
                        component.name,
                        component.name);
            }
        }

        builder.addStatement("return $L", expression.build());

        return builder.build();
    }

    private static MethodSpec generateHashCode(ModelDefinition model) {
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
            arguments.add(CodeBlock.of("$L", component.name));
        }

        if (arguments.isEmpty()) {
            builder.addStatement("return 0");
        } else {
            builder.addStatement(
                    "return $T.hash($L)",
                    OBJECTS,
                    CodeBlock.join(arguments, ", "));
        }

        return builder.build();
    }

    private static MethodSpec generateToString(ModelDefinition model, Map<String, ModelDefinition> models) {
        MethodSpec.Builder builder =
                MethodSpec.methodBuilder("toString")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(STRING);

        builder.addCode("return $S\n", model.name + "{");

        int index = 0;
        for (ModelDefinition.ComponentDefinition component: visibleComponents(model, models).values()) {
            String prefix = (index == 0) ? component.name + "=" : ", " + component.name + "=";

            String getter;
            if (component.getter != null && !component.getter.isBlank()) {
                getter = component.getter;
            } else {
                getter = defaultGetterName(component);
            }

            builder.addCode("    + $S + $L()\n", prefix, getter);

            index++;
        }

        builder.addStatement("    + '}'");

        return builder.build();
    }


    // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // helper classes

    public static final class ModelFile {
        public Map<String, ModelDefinition> models = new LinkedHashMap<>();
    }

    public static final class GenerationConfigFile {
        public Map<String, GenerationConfig> models = new LinkedHashMap<>();
    }
}
