/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import static edu.ie3.codegen.ResolverUtils.resolveClassName;
import static edu.ie3.codegen.ResolverUtils.resolveType;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.palantir.javapoet.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import javax.lang.model.element.Modifier;

final class ModelGenerator implements HelperMethods {

  public static void main(String[] args) throws IOException {
    Path resources = Path.of(".", "src", "codegen", "resources");
    Path modelsFile = resources.resolve("models.yaml");
    Path generationConfig = resources.resolve("generation.yaml");
    Path outputDirectory = Path.of(".", "src", "main", "java");

    // read in all information
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    ModelFile modelFile = mapper.readValue(modelsFile.toFile(), ModelFile.class);
    GenerationConfigFile genConfig =
        mapper.readValue(generationConfig.toFile(), GenerationConfigFile.class);

    if (!modelFile.version.equals(genConfig.version)) {
      throw new IllegalStateException("Versions don't match!");
    } else {
      System.out.println("Generating models with version: " + modelFile.version);
    }

    generateAll(modelFile.models.all(), genConfig.models.all(), outputDirectory);
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

    if (genConfig.isSealed) {
      typeBuilder.addModifiers(Modifier.SEALED);
    }

    if (model.extendsName != null && !model.extendsName.isBlank()) {
      typeBuilder.superclass(resolveClassName(model.extendsName));
    }

    for (String interfaceName : genConfig.inherits) {
      typeBuilder.addSuperinterface(resolveClassName(interfaceName));
    }

    typeBuilder.addFields(getStaticFields(genConfig));
    typeBuilder.addFields(getPrivateFields(model));

    MethodGenerator methodGenerator = new MethodGenerator(model, genConfig, models);
    ConstructorGenerator constructorGenerator = new ConstructorGenerator(model, genConfig, models);

    typeBuilder.addMethods(constructorGenerator.getConstructors());
    typeBuilder.addMethods(methodGenerator.getGetters());
    typeBuilder.addMethods(methodGenerator.getOtherMethods());

    if (genConfig.copy) {
      CopyBuilderGenerator copyBuilderGenerator =
          new CopyBuilderGenerator(model, genConfig, models);
      typeBuilder.addMethod(copyBuilderGenerator.generateCopyMethod());
      typeBuilder.addType(copyBuilderGenerator.generateCopyBuilder());
    }

    for (String nestedClass : genConfig.nestedClasses) {
      TypeSpec nested =
          TypeSpec.classBuilder(nestedClass)
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .superclass(resolveClassName(model.name))
              .build();

      typeBuilder.addType(nested);
    }

    JavaFile.builder(genConfig.packageName, typeBuilder.build())
        .skipJavaLangImports(true)
        .build()
        .writeTo(outputDirectory);
  }

  private static List<FieldSpec> getStaticFields(GenerationConfig genConfig) {
    return genConfig.staticFields.stream()
        .map(
            staticField -> {
              FieldSpec.Builder builder =
                  FieldSpec.builder(
                      resolveType(staticField.type),
                      staticField.name,
                      Modifier.PUBLIC,
                      Modifier.STATIC,
                      Modifier.FINAL);

              if (!staticField.javaDoc.isBlank()) {
                builder.addJavadoc(staticField.javaDoc);
              }

              if (staticField.className != null) {
                builder.initializer(
                    "$T.$L", resolveClassName(staticField.className), staticField.expression);
              } else {
                builder.initializer("$L", staticField.expression);
              }

              return builder.build();
            })
        .toList();
  }

  private static List<FieldSpec> getPrivateFields(ModelDefinition model) {
    return model.components.stream()
        .map(
            component -> {
              var builder =
                  FieldSpec.builder(
                      resolveType(component.type),
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

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // helper classes

  public static final class ModelFile {
    public String version;
    public Models<ModelDefinition> models;
  }

  public static final class GenerationConfigFile {
    public String version;
    public Models<GenerationConfig> models;
  }

  public static final class Models<C> {
    public Map<String, C> input = new LinkedHashMap<>();
    public Map<String, C> typeInput = new LinkedHashMap<>();

    public Map<String, C> results = new LinkedHashMap<>();

    public Map<String, C> all() {
      Map<String, C> res = new HashMap<>();
      res.putAll(input);
      res.putAll(typeInput);
      res.putAll(results);

      return res;
    }
  }
}
