/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import static edu.ie3.codegen.ResolverUtils.resolveType;

import com.palantir.javapoet.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.lang.model.element.Modifier;

/** Class for generating the copy method and copy builder. */
public final class CopyBuilderGenerator implements HelperMethods {

  private final ModelDefinition model;
  private final GenerationConfig genConfig;
  private final Map<String, ModelDefinition> models;
  private final ClassName builderClass;
  private final TypeVariableName builderTypeVariable = TypeVariableName.get("B");
  private final boolean hasParent;
  private ModelDefinition parent;
  private ClassName parentBuilderClass;

  public CopyBuilderGenerator(
      ModelDefinition model, GenerationConfig genConfig, Map<String, ModelDefinition> models) {
    this.model = model;
    this.genConfig = genConfig;
    this.models = models;

    this.builderClass = copyBuilderClassName(model);

    this.hasParent = model.extendsName != null && !model.extendsName.isBlank();

    if (hasParent) {
      this.parent = getParent(model.extendsName, models);
      this.parentBuilderClass = copyBuilderClassName(parent);
    }
  }

  /** Returns the generated copy builder. */
  public TypeSpec generateCopyBuilder() {
    if (model.isAbstract) {
      // if the model is not a class, we generate an abstract copy builder
      return generateAbstractCopyBuilder();
    }

    // else we generate a concrete copy builder
    return generateConcreteCopyBuilder();
  }

  /** Returns the generated copy method. */
  public MethodSpec generateCopyMethod() {
    MethodSpec.Builder builder = MethodSpec.methodBuilder("copy").addModifiers(Modifier.PUBLIC);

    if (hasParent) {
      // add an annotation, if we extend another model class
      builder.addAnnotation(Override.class);
    }

    if (model.isAbstract) {
      // if the copy builder is abstract, we add a wildcard
      TypeName wildcardBuilder =
          ParameterizedTypeName.get(builderClass, WildcardTypeName.subtypeOf(Object.class));

      // we add no return statement
      return builder.addModifiers(Modifier.ABSTRACT).returns(wildcardBuilder).build();
    }

    // if we generate a concrete copy builder, we add a return statement
    return builder.returns(builderClass).addStatement("return new $T(this)", builderClass).build();
  }

  /** Generates and returns an abstract copy builder. */
  private TypeSpec generateAbstractCopyBuilder() {
    ClassName modelClass = modelClassName(model, genConfig);

    TypeName ownParameterizedBuilder = ParameterizedTypeName.get(builderClass, builderTypeVariable);

    TypeSpec.Builder builder =
        TypeSpec.classBuilder(copyBuilderName(model))
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT)
            .addTypeVariable(TypeVariableName.get("B", ownParameterizedBuilder));

    // add the private fields to the copy builder
    for (ModelDefinition.ComponentDefinition component : model.components) {
      builder.addField(generateCopyBuilderField(component));
    }

    // generate the constructor of the copy builder
    builder.addMethod(generateCopyBuilderConstructor(model));

    // generate getters and setters for the copy builder
    for (ModelDefinition.ComponentDefinition component : model.components) {
      builder.addMethod(generateCopyBuilderSetter(component, builderTypeVariable));
      builder.addMethod(generateCopyBuilderGetter(component));
    }

    // check if we need to add additional methods
    for (GenerationConfig.MethodInsert insert : genConfig.copyBuilderAdditionalMethods) {
      var methodBuilder =
          MethodSpec.methodBuilder(insert.name)
              .returns(builderTypeVariable)
              .addModifiers(Modifier.PUBLIC);

      // add the parameters to the method
      for (ModelDefinition.Parameter parameter : insert.parameters) {
        methodBuilder.addParameter(resolveType(parameter.type), parameter.name);
      }

      // add Javadoc if present
      if (!insert.javaDoc.isBlank()) {
        methodBuilder.addJavadoc(insert.javaDoc);
      }

      if (insert.isAbstract) {
        // if the method should be abstract, we need to add the modifier
        methodBuilder.addModifiers(Modifier.ABSTRACT);

      } else {
        if (insert.annotation) {
          // add an annotation if needed
          methodBuilder.addAnnotation(Override.class);
        }

        addStatement(methodBuilder, insert);
        methodBuilder.addStatement("return thisInstance()");
      }

      builder.addMethod(methodBuilder.build());
    }

    // build the needed methods
    var thisInstanceBuilder =
        MethodSpec.methodBuilder("thisInstance")
            .returns(builderTypeVariable)
            .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT);
    var buildBuilder =
        MethodSpec.methodBuilder("build")
            .returns(modelClass)
            .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT);

    if (!hasParent) {
      // if we have no parent model, we just build the methods
      builder.addMethod(thisInstanceBuilder.build());
      builder.addMethod(buildBuilder.build());

    } else {
      // if a parent model class is present, we need to add annotations to the methods.
      TypeName parentBuilderType =
          ParameterizedTypeName.get(parentBuilderClass, builderTypeVariable);

      builder.superclass(parentBuilderType);
      builder.addMethod(buildBuilder.addAnnotation(Override.class).build());
      builder.addMethod(thisInstanceBuilder.addAnnotation(Override.class).build());
    }

    return builder.build();
  }

  /** Generates and returns a concrete copy builder. */
  private TypeSpec generateConcreteCopyBuilder() {
    TypeSpec.Builder builder =
        TypeSpec.classBuilder(copyBuilderName(model))
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

    if (hasParent) {

      if (parent.isAbstract) {
        TypeName parentBuilderType = ParameterizedTypeName.get(parentBuilderClass, builderClass);
        builder.superclass(parentBuilderType);
      } else {
        builder.superclass(parentBuilderClass);
      }
    }

    // add the private fields to the copy builder
    for (ModelDefinition.ComponentDefinition component : model.components) {
      builder.addField(generateCopyBuilderField(component));
    }

    // generate the constructor of the copy builder
    builder.addMethod(generateCopyBuilderConstructor(model));

    // generate getters and setters for the copy builder
    for (ModelDefinition.ComponentDefinition component : model.components) {
      builder.addMethod(generateCopyBuilderSetter(component, builderClass));
      builder.addMethod(generateCopyBuilderGetter(component));
    }

    // check if we need to add additional methods
    for (GenerationConfig.MethodInsert insert : genConfig.copyBuilderAdditionalMethods) {
      var methodBuilder =
          MethodSpec.methodBuilder(insert.name).returns(builderClass).addModifiers(Modifier.PUBLIC);

      if (insert.annotation) {
        // add an annotation if needed
        methodBuilder.addAnnotation(Override.class);
      }

      // add the parameters to the method
      for (ModelDefinition.Parameter parameter : insert.parameters) {
        methodBuilder.addParameter(resolveType(parameter.type), parameter.name);
      }

      // add Javadoc if present
      if (!insert.comment.isBlank()) {
        methodBuilder.addComment(insert.comment);
      }

      addStatement(methodBuilder, insert);
      methodBuilder.addStatement("return thisInstance()");

      builder.addMethod(methodBuilder.build());
    }

    // add the model constructor call
    builder.addMethod(generateConcreteBuilderBuildMethod(model));

    // implement the method
    builder.addMethod(
        MethodSpec.methodBuilder("thisInstance")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PROTECTED)
            .returns(builderClass)
            .addStatement("return this")
            .build());

    // build and return the copy builder
    return builder.build();
  }

  /**
   * Method for generating a copy builder field.
   *
   * @param component for which a field needs to be added
   * @return the field
   */
  private FieldSpec generateCopyBuilderField(ModelDefinition.ComponentDefinition component) {
    TypeName type = resolveType(component.type);
    return FieldSpec.builder(type, component.name, Modifier.PRIVATE).build();
  }

  /**
   * Method for generating the copy builder constructor.
   *
   * @param model for which the copy builder will be generated
   * @return the generated method
   */
  private MethodSpec generateCopyBuilderConstructor(ModelDefinition model) {
    ClassName modelClass = modelClassName(model, genConfig);

    MethodSpec.Builder builder =
        MethodSpec.constructorBuilder()
            .addModifiers(Modifier.PROTECTED)
            .addParameter(modelClass, "entity");

    if (hasParent) {
      // if we have a parent model, we insert a super call
      builder.addStatement("super(entity)");
    }

    for (ModelDefinition.ComponentDefinition component : model.components) {
      String entityGetter = defaultGetterName(component, genConfig);

      if (isMap(component)) {
        // different map initialization
        builder.addStatement(
            "this.$L = new $T<>(entity.$L())", component.name, HashMap.class, entityGetter);
      } else {
        // initializing the field with the current value
        builder.addStatement("this.$L = entity.$L", component.name, component.name);
      }
    }

    // builds the method
    return builder.build();
  }

  /**
   * Method for generating a setter method.
   *
   * @param component for which the setter should be generated
   * @param builderReturnType the return type of the setter
   * @return the generated method
   */
  private MethodSpec generateCopyBuilderSetter(
      ModelDefinition.ComponentDefinition component, TypeName builderReturnType) {

    // get the type of the parameter
    TypeName componentType = resolveType(component.type);

    // create the method handle
    MethodSpec.Builder builder =
        MethodSpec.methodBuilder(component.name)
            .addModifiers(Modifier.PUBLIC)
            .returns(builderReturnType)
            .addParameter(componentType, component.name);

    if (isMap(component)) {
      // if we have a map, we simply add the new value (may override old ones)
      builder.addStatement("this.$L.putAll($L)", component.name, component.name);
    } else {
      // else we simply override the old value
      builder.addStatement("this.$L = $L", component.name, component.name);
    }

    // add a return statement and build the method
    return builder.addStatement("return thisInstance()").build();
  }

  /**
   * Method for generating a getter method.
   *
   * @param component for which the getter should be generated
   * @return the generated method
   */
  private MethodSpec generateCopyBuilderGetter(ModelDefinition.ComponentDefinition component) {
    TypeName componentType = resolveType(component.type);

    return MethodSpec.methodBuilder(defaultGetterName(component, genConfig))
        .addModifiers(Modifier.PROTECTED)
        .returns(componentType)
        .addStatement("return $L", component.name)
        .build();
  }

  /**
   * Method for selecting the concrete model constructor.
   *
   * @param model definition to use
   * @return the selected constructor
   */
  private GenerationConfig.ConstructorDefinition selectCopyConstructor(
      ModelDefinition model, Map<String, ModelDefinition.ComponentDefinition> visible) {
    List<String> requiredComponentNames =
        visible.values().stream()
            .map(c -> c.name)
            .filter(name -> !"additionalInformation".equals(name))
            .filter(name -> !model.unsupported.contains(name))
            .toList();

    // selecting the constructor
    return genConfig.constructors.stream()
        .filter(constructor -> constructor.components.containsAll(requiredComponentNames))
        .max(java.util.Comparator.comparingInt(constructor -> constructor.components.size()))
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "No constructor of "
                        + model.name
                        + " can be used by its CopyBuilder. "
                        + "Expected a constructor containing at least: "
                        + requiredComponentNames));
  }

  /**
   * Method for generating the build method implementation.
   *
   * @param model definition to use
   * @return the implementation
   */
  private MethodSpec generateConcreteBuilderBuildMethod(ModelDefinition model) {
    // get the class of the model
    ClassName modelClass = modelClassName(model, genConfig);

    // get a list of all components
    List<String> components = model.components.stream().map(c -> c.name).toList();

    // looking for visible components.
    Map<String, ModelDefinition.ComponentDefinition> visible = visibleComponents(model, models);

    // get the constructor definition
    GenerationConfig.ConstructorDefinition constructor = selectCopyConstructor(model, visible);

    // create the method builder
    MethodSpec.Builder builder =
        MethodSpec.methodBuilder("build")
            .addAnnotation(Override.class)
            .addModifiers(Modifier.PUBLIC)
            .returns(modelClass);

    List<CodeBlock> constructorArguments = new ArrayList<>();

    // add the constructor arguments
    for (String parameterName : constructor.components) {
      ModelDefinition.ComponentDefinition component = visible.get(parameterName);

      if (component == null) {
        // throw exception if the parament is not visible
        throw new IllegalArgumentException(
            "Constructor parameter '"
                + parameterName
                + "' of "
                + model.name
                + " does not reference a known component.");
      }

      if (components.contains(component.name)) {
        // since the field is in the own copy build, we can insert it directly
        constructorArguments.add(CodeBlock.of("$L", component.name));
      } else {
        // since the field is present in a super class, we need to use a getter here
        constructorArguments.add(CodeBlock.of("$L()", defaultGetterName(component, genConfig)));
      }
    }

    // add the return statement
    builder.addStatement(
        "return new $T($L)", modelClass, CodeBlock.join(constructorArguments, ", "));

    // build and return the method
    return builder.build();
  }

  /**
   * Method for generating the copy builder name.
   *
   * @param model definition to use
   * @return the name of the copy builder
   */
  private static String copyBuilderName(ModelDefinition model) {
    return model.name + "CopyBuilder";
  }

  private static ClassName modelClassName(ModelDefinition model, GenerationConfig genConfig) {
    return ClassName.get(genConfig.packageName, model.name);
  }

  /**
   * Method for generating the copy builder class name.
   *
   * @param model definition to use
   * @return the class name of the copy builder
   */
  private static ClassName copyBuilderClassName(ModelDefinition model) {
    return ClassName.get("", copyBuilderName(model));
  }
}
