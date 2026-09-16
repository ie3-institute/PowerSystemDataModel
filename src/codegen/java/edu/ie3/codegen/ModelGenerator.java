/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import static edu.ie3.codegen.ResolverUtils.*;

import com.palantir.javapoet.*;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import javax.lang.model.element.Modifier;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.dataformat.yaml.YAMLFactory;

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
    Path modelsFile = Path.of(".", "src", "main", "resources").resolve("datamodel.yaml");
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

    // generate the data model
    generateAll(
        modelFile.modelToPackage(), modelFile.flatten(), genConfig.flatten(), outputDirectory);
  }

  /**
   * Method for generating the models.
   *
   * @param modelToPackage map: model name to package name
   * @param models to generate
   * @param generationConfigs to use
   * @param outputDirectory directory for the classes
   * @throws IOException throws an I/O exception if writing a class file fails.
   */
  private static void generateAll(
      Map<String, String> modelToPackage,
      Map<String, ModelDefinition> models,
      Map<String, GenerationConfig> generationConfigs,
      Path outputDirectory)
      throws IOException {
    for (ModelDefinition model : models.values()) {
      String name = model.name;

      Map<String, ModelDefinition.ComponentDefinition> allComponents =
          HelperMethods.visibleComponents(model, models);

      // only generate a model, if a generation config is defined
      if (!generationConfigs.containsKey(name)) {
        System.out.println("No configuration present for: " + name + " Skipping generation.");
      } else {
        String packageName = modelToPackage.get(name);

        TypeSpec cl = generate(packageName, model, allComponents, models, generationConfigs);

        // build the class file and write to it
        JavaFile.builder(packageName, cl)
            .skipJavaLangImports(true)
            .build()
            .writeTo(outputDirectory);
      }
    }
  }

  /**
   * Method for generating a model.
   *
   * @param packageName name of the package
   * @param model to generate
   * @param allComponents all available components
   * @param generationConfigs all available generation configs
   */
  private static TypeSpec generate(
      String packageName,
      ModelDefinition model,
      Map<String, ModelDefinition.ComponentDefinition> allComponents,
      Map<String, ModelDefinition> models,
      Map<String, GenerationConfig> generationConfigs) {
    GenerationConfig genConfig = generationConfigs.get(model.name);

    if (genConfig == null) {
      genConfig = new GenerationConfig();
    }

    TypeSpec.Builder typeBuilder = TypeSpec.classBuilder(model.name);

    // check if we can add Javadoc to the class.
    if (!model.description.isBlank()) {
      typeBuilder.addJavadoc(model.description);
    }

    if (model.isAbstract) {
      // if the model is abstract, we add the modifier
      typeBuilder.addModifiers(Modifier.ABSTRACT);
    }

    genConfig.modifiers.forEach(m -> typeBuilder.addModifiers(modifiers.get(m)));

    if (model.extendsName != null && !model.extendsName.isBlank()) {
      // if the class extends another model, we add the super class
      typeBuilder.superclass(resolveClassName(model.extendsName));
    }

    // adding all inherited interfaces
    for (String interfaceName : genConfig.inherits) {
      typeBuilder.addSuperinterface(resolveClassName(interfaceName));
    }

    // add all the fields
    typeBuilder.addFields(getFields(model, genConfig.setters));
    typeBuilder.addFields(getAdditionalFields(genConfig));

    MethodGenerator methodGenerator = new MethodGenerator(model, genConfig, allComponents);
    ConstructorGenerator constructorGenerator =
        new ConstructorGenerator(model, genConfig, allComponents);

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
          new CopyBuilderGenerator(packageName, model, genConfig, models, allComponents);

      // add the method and the copy builder
      typeBuilder.addMethod(copyBuilderGenerator.generateCopyMethod());
      typeBuilder.addType(copyBuilderGenerator.generateCopyBuilder());
    }

    for (ModelDefinition nested : model.nested) {
      if (generationConfigs.containsKey(nested.name)) {
        typeBuilder.addType(
            generate(
                packageName + "." + model.name, nested, allComponents, models, generationConfigs));
      }
    }

    return typeBuilder.build();
  }

  /**
   * Method for getting all the additional fields.
   *
   * @param genConfig generation config to use
   * @return a list of static field definitions
   */
  private static List<FieldSpec> getAdditionalFields(GenerationConfig genConfig) {
    return genConfig.fields.stream()
        .map(
            field -> {
              FieldSpec.Builder builder = FieldSpec.builder(resolveType(field.type), field.name);

              if (!field.description.isBlank()) {
                builder.addJavadoc(field.description);
              }

              field.modifiers.forEach(m -> builder.addModifiers(modifiers.get(m)));

              var modified = modifyExpression(field.expression);
              builder.initializer(modified.expression(), modified.args());

              return builder.build();
            })
        .toList();
  }

  /**
   * Method for getting all the fields.
   *
   * @param model definition to use
   * @return a list of private field definitions
   */
  private static List<FieldSpec> getFields(ModelDefinition model, boolean setter) {
    return model.components.stream()
        .map(
            component -> {
              var builder = FieldSpec.builder(resolveType(component.type), component.name);

              if (setter) {
                builder.addModifiers(Modifier.PRIVATE);
              } else {
                builder.addModifiers(Modifier.PRIVATE, Modifier.FINAL);
              }

              if (!component.description.isBlank()) {
                builder.addJavadoc(component.description);
              }

              if (component.isTransient) {
                builder.addModifiers(Modifier.TRANSIENT);
              }

              return builder.build();
            })
        .toList();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // helper classes

  public static final class ModelFile extends Models<ModelDefinition> {
    public String version;
  }

  public static final class GenerationConfigFile extends Models<GenerationConfig> {
    public String version;
  }

  public static sealed class Models<C> {
    public Map<String, Map<String, C>> datamodel = new HashMap<>();

    public Map<String, String> modelToPackage() {
      Map<String, String> res = new HashMap<>();

      datamodel.forEach(
          (packageName, models) -> {
            String fullPackageName = "edu.ie3.datamodel." + packageName;
            models.keySet().forEach(name -> res.put(name, fullPackageName));
          });

      return res;
    }

    public Map<String, C> flatten() {
      Map<String, C> res = new HashMap<>();
      datamodel.values().forEach(res::putAll);
      return res;
    }
  }
}
