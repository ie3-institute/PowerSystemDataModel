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

/** Main class for the model generator. */
final class ModelGenerator implements HelperMethods {

  /**
   * Main method to generate models.
   *
   * @param args program arguments
   * @throws IOException throws an I/O exception if writing a class file fails.
   */
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

    // check for versions
    if (!modelFile.version.equals(genConfig.version)) {
      throw new IllegalStateException("Versions don't match!");
    } else {
      System.out.println("Generating models with version: " + modelFile.version);
    }

    // generate the models
    generateAll(modelFile.models.all(), genConfig.models.all(), outputDirectory);
  }

  /**
   * Method for generating the models.
   *
   * @param models to generate
   * @param generationConfigs to use
   * @param outputDirectory directory for the classes
   * @throws IOException throws an I/O exception if writing a class file fails.
   */
  private static void generateAll(
      Map<String, ModelDefinition> models,
      Map<String, GenerationConfig> generationConfigs,
      Path outputDirectory)
      throws IOException {
    for (ModelDefinition model : models.values()) {
      String name = model.name;

      // only generate a model, if a generation config is defined
      if (!generationConfigs.containsKey(name)) {
        System.out.println("No configuration present for: " + name + " Skipping generation.");
      } else {
        generate(model, generationConfigs.get(name), models, outputDirectory);
      }
    }
  }

  /**
   * Method for generating a model.
   *
   * @param model to generate
   * @param genConfig to use
   * @param models all available models
   * @param outputDirectory directory for the classes
   * @throws IOException throws an I/O exception if writing a class file fails.
   */
  private static void generate(
      ModelDefinition model,
      GenerationConfig genConfig,
      Map<String, ModelDefinition> models,
      Path outputDirectory)
      throws IOException {
    TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(model.name).addModifiers(Modifier.PUBLIC);

    // check if we can add Javadoc to the class.
    if (!genConfig.classJavaDoc.isBlank()) {
      typeBuilder.addJavadoc(genConfig.classJavaDoc);
    }

    if (model.isAbstract) {
      // if the model is abstract, we add the modifier
      typeBuilder.addModifiers(Modifier.ABSTRACT);
    }

    if (genConfig.isSealed) {
      // if the model is sealed, we add the modifier
      typeBuilder.addModifiers(Modifier.SEALED);
    }

    if (model.extendsName != null && !model.extendsName.isBlank()) {
      // if the class extends another model, we add the super class
      typeBuilder.superclass(resolveClassName(model.extendsName));
    }

    // adding all inherited interfaces
    for (String interfaceName : genConfig.inherits) {
      typeBuilder.addSuperinterface(resolveClassName(interfaceName));
    }

    // add all the fields
    typeBuilder.addFields(getStaticFields(genConfig));
    typeBuilder.addFields(getPrivateFields(model, genConfig));

    MethodGenerator methodGenerator = new MethodGenerator(model, genConfig, models);
    ConstructorGenerator constructorGenerator = new ConstructorGenerator(model, genConfig, models);

    // add all the methods
    typeBuilder.addMethods(constructorGenerator.getConstructors());
    typeBuilder.addMethods(methodGenerator.getGetters());

    if (genConfig.setters) {
      typeBuilder.addMethods(methodGenerator.getSetters());
    }

    typeBuilder.addMethods(methodGenerator.getOtherMethods());

    // check if we need to add a copy method and copy builder
    if (genConfig.copy && !genConfig.setters) {
      CopyBuilderGenerator copyBuilderGenerator =
          new CopyBuilderGenerator(model, genConfig, models);

      // add the method and the copy builder
      typeBuilder.addMethod(copyBuilderGenerator.generateCopyMethod());
      typeBuilder.addType(copyBuilderGenerator.generateCopyBuilder());
    }

    // insert all nested classes
    for (String nestedClass : genConfig.nestedClasses) {
      TypeSpec nested =
          TypeSpec.classBuilder(nestedClass)
              .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
              .superclass(resolveClassName(model.name))
              .build();

      typeBuilder.addType(nested);
    }

    // build the class file and write to it
    JavaFile.builder(genConfig.packageName, typeBuilder.build())
        .skipJavaLangImports(true)
        .build()
        .writeTo(outputDirectory);
  }

  /**
   * Method for getting all the static fields.
   *
   * @param genConfig generation config to use
   * @return a list of static field definitions
   */
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

  /**
   * Method for getting all the static fields.
   *
   * @param model definition to use
   * @return a list of private field definitions
   */
  private static List<FieldSpec> getPrivateFields(
      ModelDefinition model, GenerationConfig genConfig) {
    return model.components.stream()
        .map(
            component -> {
              var builder =
                  FieldSpec.builder(resolveType(component.type), component.name, Modifier.PRIVATE);

              if (!component.javaDoc.isBlank()) {
                builder.addJavadoc(component.javaDoc);
              }

              if (!genConfig.setters) {
                builder.addModifiers(Modifier.FINAL);
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
