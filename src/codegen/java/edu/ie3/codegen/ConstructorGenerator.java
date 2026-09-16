/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import static edu.ie3.codegen.ResolverUtils.resolveType;

import com.palantir.javapoet.CodeBlock;
import com.palantir.javapoet.MethodSpec;
import java.util.*;
import java.util.function.Function;
import javax.lang.model.element.Modifier;

/** Class for generating constructors. */
public final class ConstructorGenerator implements HelperMethods {

  private final ModelDefinition model;
  private final GenerationConfig genConfig;
  private final Map<String, ModelDefinition.ComponentDefinition> allComponents;

  private final List<String> ownComponents = new ArrayList<>();
  public final List<String> fullConstructor;
  private final Set<List<String>> allConstructors = new HashSet<>();

  public ConstructorGenerator(
      ModelDefinition model,
      GenerationConfig genConfig,
      Map<String, ModelDefinition.ComponentDefinition> allComponents) {
    this.model = model;
    this.genConfig = genConfig;
    this.allComponents = allComponents;

    model.components.forEach(c -> this.ownComponents.add(c.name));

    List<String> components = new ArrayList<>(allComponents.keySet());

    if (model.isAbstract) {
      components.remove(ADDITIONAL_INFORMATION);
    }

    this.fullConstructor = new ArrayList<>(components);

    allConstructors.add(fullConstructor);

    List<String> minParams = new ArrayList<>();
    List<String> withoutAdditionalInformation = new ArrayList<>(components);
    withoutAdditionalInformation.remove(ADDITIONAL_INFORMATION);

    for (String name : components) {
      if (allComponents.get(name).required) {
        minParams.add(name);
      }
    }

    allConstructors.add(minParams);
    allConstructors.add(withoutAdditionalInformation);
  }

  /** Returns all constructors that should be added to the generated class. */
  public List<MethodSpec> getConstructors() {
    List<MethodSpec> constructors = new ArrayList<>();

    // add default constructors
    for (List<String> components : allConstructors) {
      constructors.add(generateConstructor(components));
    }

    // add additional constructors
    for (GenerationConfig.ConstructorDefinition def : genConfig.constructors) {
      constructors.add(generateConstructor(def));
    }

    return constructors;
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // helper methods

  /**
   * Generates a constructor base on the given definition.
   *
   * @param componentNames names of the components to use.
   * @return a constructor method.
   */
  private MethodSpec generateConstructor(SequencedCollection<String> componentNames) {
    // creates the builder
    MethodSpec.Builder builder = MethodSpec.constructorBuilder();

    // to have a modifiable list
    List<String> orderedComponents = new ArrayList<>(componentNames);

    // selects the modifier of the constructor
    if (model.isAbstract) {
      builder.addModifiers(Modifier.PROTECTED);
    } else {
      builder.addModifiers(Modifier.PUBLIC);
    }

    // stores the ordered components that are used for this constructor
    List<CodeBlock> orderedSuperArgs = new ArrayList<>();
    List<CodeBlock> initStatements = new ArrayList<>();

    // Javadoc builder
    StringBuilder javaDocBuilder = new StringBuilder();

    for (String name : orderedComponents) {
      ModelDefinition.ComponentDefinition def = allComponents.get(name);

      // adds the parameter and the initialization
      addParameter(builder, def);
      addStatement(orderedSuperArgs, initStatements, name);

      // extends the Javadoc
      javaDocBuilder
          .append(" @param ")
          .append(def.name)
          .append(" ")
          .append(def.description)
          .append("\n");
    }

    // adds everything to the builder
    addMissingStatement(initStatements, orderedComponents, allComponents::get);
    addCode(builder, orderedSuperArgs, initStatements);

    // add Javadoc
    builder.addJavadoc(javaDocBuilder.toString());

    // we build the constructor and return it
    return builder.build();
  }

  /**
   * Method for generating additional constructors.
   *
   * @param def the definition to use
   * @return a constructor method.
   */
  private MethodSpec generateConstructor(GenerationConfig.ConstructorDefinition def) {
    List<String> orderedComponents = new ArrayList<>();
    Map<String, ModelDefinition.Parameter> parameters = new HashMap<>();

    def.parameters.forEach(
        c -> {
          orderedComponents.add(c.name);
          parameters.put(c.name, c);
        });

    // creates the builder
    MethodSpec.Builder builder = MethodSpec.constructorBuilder();

    // selects the modifier of the constructor
    if (def.isPrivate) {
      builder.addModifiers(Modifier.PRIVATE);
    } else if (model.isAbstract) {
      builder.addModifiers(Modifier.PROTECTED);
    } else {
      builder.addModifiers(Modifier.PUBLIC);
    }

    // stores the ordered components that are used for this constructor
    List<CodeBlock> orderedSuperArgs = new ArrayList<>();
    List<CodeBlock> initStatements = new ArrayList<>();

    // Javadoc builder
    StringBuilder javaDocBuilder = new StringBuilder();

    for (String name : orderedComponents) {
      ModelDefinition.Parameter parameter = parameters.get(name);

      // adds the parameter and the initialization
      addParameter(builder, parameter);

      if (def.codeBlock.isBlank()) {
        addStatement(orderedSuperArgs, initStatements, name);
      } else {
        // we may need to import additional classes
        var modified = ResolverUtils.modifyExpression(def.codeBlock);
        initStatements.add(CodeBlock.of(modified.expression(), modified.args()));
      }

      // extends the Javadoc
      javaDocBuilder
          .append(" @param ")
          .append(parameter.name)
          .append(" ")
          .append(parameter.description)
          .append("\n");
    }

    // adds everything to the builder
    addMissingStatement(initStatements, orderedComponents, parameters::get);
    addCode(builder, orderedSuperArgs, initStatements);

    // add Javadoc
    builder.addJavadoc(javaDocBuilder.toString());

    // we build the constructor and return it
    return builder.build();
  }

  /**
   * Method for adding the code blocks to the builder
   *
   * @param builder of the constructor
   * @param orderedSuperArgs super arguments
   * @param initStatements initialization statements
   */
  private void addCode(
      MethodSpec.Builder builder,
      List<CodeBlock> orderedSuperArgs,
      List<CodeBlock> initStatements) {
    // check if we need to add super args
    if (!orderedSuperArgs.isEmpty()) {
      builder.addStatement("super($L)", CodeBlock.join(orderedSuperArgs, ", "));
    }

    // add field init code
    initStatements.forEach(c -> builder.addStatement("$L", c));
  }

  /**
   * Method for adding a parameter to the constructor.
   *
   * @param builder of the constructor
   * @param def the parameter definition to use
   */
  private void addParameter(MethodSpec.Builder builder, ModelDefinition.Parameter def) {
    // add the component to the constructor
    builder.addParameter(resolveType(def.type), def.name);
  }

  /**
   * Adds a component to the arguments.
   *
   * @param superArgs list of super arguments
   * @param initStatements list of initialization statements
   * @param name of the field
   */
  private void addStatement(
      List<CodeBlock> superArgs, List<CodeBlock> initStatements, String name) {
    // initialize the component
    if (!ownComponents.contains(name) && !name.equals(ADDITIONAL_INFORMATION)) {
      // we need to add this component to the super args
      superArgs.add(CodeBlock.of("$L", name));
    } else {

      if (name.equals(ADDITIONAL_INFORMATION)) {
        // if the constructor contains additional information, we call the setter
        initStatements.add(CodeBlock.of("setAdditionalInformation(additionalInformation)"));
      } else {
        // init the field
        initStatements.add(CodeBlock.of("this.$L = " + name, name));
      }
    }
  }

  /**
   * Method for checking and adding missing initialization statements.
   *
   * @param initStatements list of initialization statements
   * @param orderedComponents list of added components
   * @param fcn to retrieve a parameter definition
   */
  private void addMissingStatement(
      List<CodeBlock> initStatements,
      List<String> orderedComponents,
      Function<String, ModelDefinition.Parameter> fcn) {
    for (String c : ownComponents) {
      if (!orderedComponents.contains(c)) {
        initStatements.add(
            CodeBlock.of("this.$L = " + ResolverUtils.getDefaultExpression(fcn.apply(c).type), c));
      }
    }
  }
}
