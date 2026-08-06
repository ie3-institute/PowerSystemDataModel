package edu.ie3.codegen;

import com.squareup.javapoet.*;

import javax.lang.model.element.Modifier;
import java.util.*;

import static edu.ie3.codegen.HelperMethods.*;

public final class CopyBuilderGenerator implements HelperMethods {

    private final ModelDefinition model;
    private final GenerationConfig genConfig;
    private final Map<String, ModelDefinition> models;

    public CopyBuilderGenerator(
            ModelDefinition model,
            GenerationConfig genConfig,
            Map<String, ModelDefinition> models
    ) {
        this.model = model;
        this.genConfig = genConfig;
        this.models = models;
    }

    public TypeSpec generateCopyBuilder() {
        if (!model.isClass) {
            return generateAbstractCopyBuilder();
        }

        return generateConcreteCopyBuilder();
    }


    public MethodSpec generateCopyMethod() {
        ClassName builderClass = copyBuilderClassName(model);

        boolean hasParent = model.extendsName != null && !model.extendsName.isBlank();

        MethodSpec.Builder builder =
                MethodSpec.methodBuilder("copy")
                        .addModifiers(Modifier.PUBLIC);

        if (hasParent) {
            builder.addAnnotation(Override.class);
        }

        if (!model.isClass) {
            TypeName wildcardBuilder =
                    ParameterizedTypeName.get(
                            builderClass,
                            WildcardTypeName.subtypeOf(Object.class));

            return builder
                    .addModifiers(Modifier.ABSTRACT)
                    .returns(wildcardBuilder)
                    .build();
        }

        return builder
                .returns(builderClass)
                .addStatement("return new $T(this)", builderClass)
                .build();
    }


    private static String copyBuilderName(ModelDefinition model) {
        return model.name + "CopyBuilder";
    }

    private static ClassName modelClassName(ModelDefinition model) {
        return ClassName.get(model.packageName, model.name);
    }

    private static ClassName copyBuilderClassName(ModelDefinition model) {
        return ClassName.get(
                model.packageName,
                model.name,
                copyBuilderName(model));
    }

    private static String builderGetterName(ModelDefinition.ComponentDefinition component) {
        return "get" + capitalize(component.name);
    }

    private TypeSpec generateAbstractCopyBuilder() {
        ClassName modelClass = modelClassName(model);
        ClassName builderClass = copyBuilderClassName(model);

        TypeVariableName builderTypeVariable = TypeVariableName.get("B");

        TypeName ownParameterizedBuilder =
                ParameterizedTypeName.get(builderClass, builderTypeVariable);

        TypeSpec.Builder builder =
                TypeSpec.classBuilder(copyBuilderName(model))
                        .addModifiers(
                                Modifier.PUBLIC,
                                Modifier.STATIC,
                                Modifier.ABSTRACT)
                        .addTypeVariable(
                                TypeVariableName.get("B", ownParameterizedBuilder));

        ModelDefinition parent = getParent(model.extendsName, models);

        ClassName parentBuilderClass = copyBuilderClassName(parent);

        TypeName parentBuilderType =
                    ParameterizedTypeName.get(
                            parentBuilderClass,
                            builderTypeVariable);

        builder.superclass(parentBuilderType);


        for (ModelDefinition.ComponentDefinition component : model.components) {
            builder.addField(generateCopyBuilderField(component, model));
        }

        builder.addMethod(generateCopyBuilderConstructor(model, true));

        for (ModelDefinition.ComponentDefinition component : model.components) {
            builder.addMethod(generateCopyBuilderSetter(component, model, builderTypeVariable));
            builder.addMethod(generateCopyBuilderGetter(component, model));
        }

        builder.addMethod(
                MethodSpec.methodBuilder("build")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(modelClass)
                        .build());

        builder.addMethod(
                MethodSpec.methodBuilder("thisInstance")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PROTECTED, Modifier.ABSTRACT)
                        .returns(builderTypeVariable)
                        .build());

        return builder.build();
    }

    private TypeSpec generateConcreteCopyBuilder() {
        ClassName builderClass = copyBuilderClassName(model);

        TypeSpec.Builder builder =
                TypeSpec.classBuilder(copyBuilderName(model))
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        ModelDefinition parent = findParentModel(model);

        if (parent != null) {
            ClassName parentBuilderClass = copyBuilderClassName(parent);

            TypeName parentBuilderType =
                    ParameterizedTypeName.get(
                            parentBuilderClass,
                            builderClass);

            builder.superclass(parentBuilderType);
        }

        for (ModelDefinition.ComponentDefinition component : model.components) {
            builder.addField(generateCopyBuilderField(component, model));
        }

        builder.addMethod(generateCopyBuilderConstructor(model, false));

        for (ModelDefinition.ComponentDefinition component : model.components) {
            builder.addMethod(generateCopyBuilderSetter(component, model, builderClass));
            builder.addMethod(generateCopyBuilderGetter(component, model));
        }

        builder.addMethod(generateConcreteBuilderBuildMethod(model));

        builder.addMethod(
                MethodSpec.methodBuilder("thisInstance")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PROTECTED)
                        .returns(builderClass)
                        .addStatement("return this")
                        .build());

        return builder.build();
    }

    private FieldSpec generateCopyBuilderField(
            ModelDefinition.ComponentDefinition component,
            ModelDefinition model) {

        TypeName type = resolveType(component.type, model.packageName);

        return FieldSpec.builder(
                        type,
                        component.name,
                        Modifier.PRIVATE)
                .build();
    }

    private MethodSpec generateCopyBuilderConstructor(
            ModelDefinition model,
            boolean abstractBuilder) {

        ClassName modelClass = modelClassName(model);

        MethodSpec.Builder builder =
                MethodSpec.constructorBuilder()
                        .addModifiers(
                                abstractBuilder ? Modifier.PROTECTED : Modifier.PRIVATE)
                        .addParameter(modelClass, "entity");

        ModelDefinition parent = findParentModel(model);

        if (parent != null) {
            builder.addStatement("super(entity)");
        }

        for (ModelDefinition.ComponentDefinition component : model.components) {
            String entityGetter =
                    component.getter != null && !component.getter.isBlank()
                            ? component.getter
                            : defaultGetterName(component);

            if (isMap(component)) {
                builder.addStatement(
                        "this.$L = new $T<>(entity.$L())",
                        component.name,
                        HashMap.class,
                        entityGetter);
            } else {
                builder.addStatement(
                        "this.$L = entity.$L()",
                        component.name,
                        entityGetter);
            }
        }

        return builder.build();
    }

    private MethodSpec generateCopyBuilderSetter(
            ModelDefinition.ComponentDefinition component,
            ModelDefinition model,
            TypeName builderReturnType) {

        TypeName componentType =
                resolveType(component.type, model.packageName);

        MethodSpec.Builder builder =
                MethodSpec.methodBuilder(component.name)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(builderReturnType)
                        .addParameter(componentType, component.name);

        if (isMap(component)) {
            builder.addStatement(
                    "this.$L.putAll($L)",
                    component.name,
                    component.name);
        } else {
            builder.addStatement(
                    "this.$L = $L",
                    component.name,
                    component.name);
        }

        builder.addStatement("return thisInstance()");

        return builder.build();
    }

    private MethodSpec generateCopyBuilderGetter(
            ModelDefinition.ComponentDefinition component,
            ModelDefinition model) {

        TypeName componentType =
                resolveType(component.type, model.packageName);

        return MethodSpec.methodBuilder(builderGetterName(component))
                .addModifiers(Modifier.PROTECTED)
                .returns(componentType)
                .addStatement("return $L", component.name)
                .build();
    }

    private GenerationConfig.ConstructorDefinition selectCopyConstructor(ModelDefinition model) {

        Map<String, ModelDefinition.ComponentDefinition> visible =
                visibleComponents(model);

        List<String> requiredComponentNames =
                visible.keySet().stream()
                        .filter(name -> !"additionalInformation".equals(name))
                        .toList();

        return genConfig.constructors.stream()
                .filter(
                        constructor ->
                                constructor.components.containsAll(requiredComponentNames))
                .max(
                        java.util.Comparator.comparingInt(
                                constructor -> constructor.components.size()))
                .orElseThrow(
                        () ->
                                new IllegalArgumentException(
                                        "No constructor of "
                                                + model.name
                                                + " can be used by its CopyBuilder. "
                                                + "Expected a constructor containing at least: "
                                                + requiredComponentNames));
    }

    private MethodSpec generateConcreteBuilderBuildMethod(
            ModelDefinition model) {

        ClassName modelClass = modelClassName(model);

        GenerationConfig.ConstructorDefinition constructor = selectCopyConstructor(model);

        Map<String, ModelDefinition.ComponentDefinition> visible =
                visibleComponents(model);

        MethodSpec.Builder builder =
                MethodSpec.methodBuilder("build")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(modelClass);

        List<CodeBlock> constructorArguments = new ArrayList<>();

        for (String parameterName : constructor.components) {
            ModelDefinition.ComponentDefinition component =
                    visible.get(parameterName);

            if (component == null) {
                throw new IllegalArgumentException(
                        "Constructor parameter '"
                                + parameterName
                                + "' of "
                                + model.name
                                + " does not reference a known component.");
            }

            constructorArguments.add(
                    CodeBlock.of("$L()", builderGetterName(component)));
        }

        builder.addStatement(
                "$T result = new $T($L)",
                modelClass,
                modelClass,
                CodeBlock.join(constructorArguments, ", "));


        builder.addStatement("result.setAdditionalInformation(getAdditionalInformation())");
        builder.addStatement("return result");

        return builder.build();
    }

    private ModelDefinition findParentModel(ModelDefinition model) {
        if (model.extendsName == null || model.extendsName.isBlank()) {
            return null;
        }

        ModelDefinition parent = models.get(model.extendsName);

        if (parent == null) {
            throw new IllegalArgumentException(
                    "Unknown parent model '"
                            + model.extendsName
                            + "' for model '"
                            + model.name
                            + "'.");
        }

        return parent;
    }

    private Map<String, ModelDefinition.ComponentDefinition> visibleComponents(
            ModelDefinition model) {

        LinkedHashMap<String, ModelDefinition.ComponentDefinition> result =
                new LinkedHashMap<>();

        ModelDefinition parent = findParentModel(model);

        if (parent != null) {
            result.putAll(visibleComponents(parent));
        }

        for (ModelDefinition.ComponentDefinition component : model.components) {
            if (result.put(component.name, component) != null) {
                throw new IllegalArgumentException(
                        "Component '"
                                + component.name
                                + "' occurs multiple times in the hierarchy of "
                                + model.name);
            }
        }

        return result;
    }







}
