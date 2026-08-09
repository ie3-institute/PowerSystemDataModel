/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import static edu.ie3.codegen.HelperMethods.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.squareup.javapoet.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import javax.lang.model.element.Modifier;

final class ModelGenerator implements HelperMethods {

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("Usage: ModelGenerator <output-directory>");
      System.exit(1);
    }

    Path resources = Path.of(".", "src", "codegen", "resources");
    Path modelsFile = resources.resolve("models.yaml");
    Path generationConfig = resources.resolve("generation.yaml");
    Path outputDirectory = Path.of(args[0]);

    // read in all information
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    ModelFile modelFile = mapper.readValue(modelsFile.toFile(), ModelFile.class);
    GenerationConfigFile genConfig =
        mapper.readValue(generationConfig.toFile(), GenerationConfigFile.class);

    generateAll(modelFile.models, genConfig.models, outputDirectory);
  }

  public static void generateAll(
      Map<String, ModelDefinition> models,
      Map<String, GenerationConfig> generationConfigs,
      Path outputDirectory)
      throws IOException {
    for (ModelDefinition model : models.values()) {
      String name = model.name;

      if (!generationConfigs.containsKey(name)) {
        System.out.println("No configuration present for: " + name + " Skipping generation.");
      } else {
        generate(model, generationConfigs.get(name), models, outputDirectory);
      }
    }
  }

  public static void generate(
      ModelDefinition model,
      GenerationConfig genConfig,
      Map<String, ModelDefinition> models,
      Path outputDirectory)
      throws IOException {
    TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(model.name).addModifiers(Modifier.PUBLIC);

    if (!genConfig.classJavaDoc.isBlank()) {
      typeBuilder.addJavadoc(genConfig.classJavaDoc);
    }

    if (!model.isClass) {
      typeBuilder.addModifiers(Modifier.ABSTRACT);
    }

    if (model.extendsName != null && !model.extendsName.isBlank()) {
      typeBuilder.superclass(resolveClassName(model.extendsName, model.packageName));
    }

    for (String interfaceName : genConfig.inherits) {
      typeBuilder.addSuperinterface(resolveClassName(interfaceName, model.packageName));
    }

    typeBuilder.addFields(getStaticFields(model, genConfig));
    typeBuilder.addFields(model.getPrivateFields());

    ConstructorGenerator constructorGenerator = new ConstructorGenerator(model, genConfig, models);
    CopyBuilderGenerator copyBuilderGenerator = new CopyBuilderGenerator(model, genConfig, models);

    typeBuilder.addMethods(constructorGenerator.getConstructors());
    typeBuilder.addMethods(model.getAllMethods(genConfig));
    typeBuilder.addMethod(copyBuilderGenerator.generateCopyMethod());

    if (genConfig.fromMap && model.isClass) {
      typeBuilder.addMethod(constructorGenerator.getFromMapConstructor());
    }

    for (GenerationConfig.MethodOverride override : genConfig.methodOverrides) {
      var methodBuilder =
          MethodSpec.methodBuilder(override.name)
              .returns(resolveType(override.type, model.packageName))
              .addAnnotation(Override.class)
              .addModifiers(Modifier.PUBLIC);

      if (override.className != null && !override.className.isBlank()) {
        methodBuilder.addStatement(
            "return $T." + override.expression, getClassName(override.className));
      } else {
        methodBuilder.addStatement("return " + override.expression);
      }

      typeBuilder.addMethod(methodBuilder.build());
    }

    if (genConfig.equals) {
      typeBuilder.addMethod(generateEquals(model));
    }

    if (genConfig.hashCode) {
      typeBuilder.addMethod(generateHashCode(model));
    }

    if (genConfig.toString) {
      typeBuilder.addMethod(generateToString(model, models, genConfig));
    }

    typeBuilder.addType(copyBuilderGenerator.generateCopyBuilder());

    JavaFile.builder(model.packageName, typeBuilder.build())
        .skipJavaLangImports(true)
        .build()
        .writeTo(outputDirectory);
  }

  private static List<FieldSpec> getStaticFields(
      ModelDefinition model, GenerationConfig genConfig) {
    return genConfig.staticFields.stream()
        .map(
            staticField -> {
              FieldSpec.Builder builder =
                  FieldSpec.builder(
                      resolveType(staticField.type, model.packageName),
                      staticField.name,
                      Modifier.PUBLIC,
                      Modifier.STATIC,
                      Modifier.FINAL);

              if (!staticField.javaDoc.isBlank()) {
                builder.addJavadoc(staticField.javaDoc);
              }

              if (staticField.className != null) {
                builder.initializer(
                    "$T.$L", getClassName(staticField.className), staticField.expression);
              } else {
                builder.initializer("$L", staticField.expression);
              }

              return builder.build();
            })
        .toList();
  }

  private static MethodSpec generateEquals(ModelDefinition model) {
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
      builder.addStatement("if (!super.equals(o)) return false");

      CodeBlock.Builder expression = CodeBlock.builder();

      for (int index = 0; index < filteredComponents.size(); index++) {
        ModelDefinition.ComponentDefinition component = filteredComponents.get(index);

        if (index > 0) {
          expression.add("\n&& ");
        }

        if (isPrimitive(component.type)) {
          expression.add("$L == that.$L", component.name, component.name);
        } else {
          expression.add("$T.equals($L, that.$L)", OBJECTS, component.name, component.name);
        }
      }

      builder.addStatement("return $L", expression.build());
    }

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
      if (excludeFromMethods(component)) {
        continue;
      }

      arguments.add(CodeBlock.of("$L", component.name));
    }

    if (arguments.isEmpty()) {
      builder.addStatement("return 0");
    } else {
      builder.addStatement("return $T.hash($L)", OBJECTS, CodeBlock.join(arguments, ", "));
    }

    return builder.build();
  }

  private static MethodSpec generateToString(
      ModelDefinition model, Map<String, ModelDefinition> models, GenerationConfig genConfig) {
    MethodSpec.Builder builder =
        MethodSpec.methodBuilder("toString")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(STRING);

    builder.addCode("return $S\n", model.name + "{");

    List<String> components = model.components.stream().map(c -> c.name).toList();

    int index = 0;
    for (ModelDefinition.ComponentDefinition component :
        visibleComponents(model, models).values()) {
      if (excludeFromMethods(component)) {
        continue;
      }

      String prefix = (index == 0) ? component.name + "=" : ", " + component.name + "=";

      String componentName = component.name;

      if (component.nullable) {
        // we need some special calls here
        builder.addCode(
            "    + $S + $T.ofNullable($L).map($L::getUuid).map(UUID::toString).orElse(\"\")\n",
            prefix,
            Optional.class,
            componentName,
            model.name);

      } else if (components.contains(component.name)) {
        if (component.nested) {
          builder.addCode("    + $S + $L.getUuid()\n", prefix, component.name);
        } else {
          builder.addCode("    + $S + $L\n", prefix, component.name);
        }

      } else {
        String getter =
            defaultGetterName(
                component.name, component.type, genConfig.getterOptions.get(component.name));

        if (component.nested) {
          builder.addCode("    + $S + $L().getUuid()\n", prefix, getter);
        } else {
          builder.addCode("    + $S + $L()\n", prefix, getter);
        }
      }

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
