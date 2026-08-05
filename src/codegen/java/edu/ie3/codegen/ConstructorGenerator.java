package edu.ie3.codegen;

import com.squareup.javapoet.*;
import edu.ie3.codegen.ModelDefinition.ComponentDefinition;

import javax.lang.model.element.Modifier;
import java.util.*;

import static edu.ie3.codegen.HelperMethods.*;

public final class ConstructorGenerator implements HelperMethods {

    private final ModelDefinition model;
    private final GenerationConfig genConfig;
    private final Map<String, ModelDefinition> models;

    public ConstructorGenerator(ModelDefinition model, GenerationConfig genConfig, Map<String, ModelDefinition> models) {
        this.model = model;
        this.genConfig = genConfig;
        this.models = models;
    }

    public MethodSpec getFromMapConstructor() {
        GenerationConfig.ConstructorDefinition targetConstructor =
                genConfig.constructors.stream()
                        .filter(
                                constructor ->
                                        constructor.name.equals(genConfig.fromMapConstructor))
                        .findFirst()
                        .orElseThrow(
                                () ->
                                        new IllegalArgumentException(
                                                "Unknown fromMapConstructor '"
                                                        + genConfig.fromMapConstructor
                                                        + "' for "
                                                        + model.name));

        Map<String, ComponentDefinition> allComponents = visibleComponents(model, models);

        List<ComponentDefinition> resolverComponents =
                orderedResolverComponents(model, allComponents);

        TypeName mapType =
                ParameterizedTypeName.get(MAP, STRING, STRING);

        MethodSpec.Builder builder =
                MethodSpec.methodBuilder("fromMap")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .returns(ClassName.get(model.packageName, model.name))
                        .addParameter(mapType, "values");

        for (ComponentDefinition resolverComponent : resolverComponents) {
            TypeName resolverType =
                    ParameterizedTypeName.get(
                            FUNCTION,
                            UUID_CLASS,
                            resolveType(resolverComponent.type, model.packageName));

            builder.addParameter(
                    resolverType,
                    resolverName(resolverComponent));
        }

        builder.addStatement("$T.requireNonNull(values, $S)", OBJECTS, "values");

        for (ComponentDefinition resolverComponent : resolverComponents) {
            builder.addStatement(
                    "$T.requireNonNull($L, $S)",
                    OBJECTS,
                    resolverName(resolverComponent),
                    resolverName(resolverComponent));
        }

        builder.addCode("\n");

        List<ComponentDefinition> constructorParameters =
                resolveParameters(
                        targetConstructor.components,
                        allComponents,
                        model.name + ".fromMap");

        for (ComponentDefinition component : constructorParameters) {
            generateFromMapLocalVariable(builder, component);
        }

        builder.addCode("\n");

        CodeBlock arguments =
                constructorParameters.stream()
                        .map(component -> CodeBlock.of("$L", component.name))
                        .collect(CodeBlock.joining(",\n"));

        builder.addStatement(
                "return new $T(\n$L\n)",
                ClassName.get(model.packageName, model.name),
                indent(arguments));

        return builder.build();
    }

    private void generateFromMapLocalVariable(
            MethodSpec.Builder builder,
            ComponentDefinition component) {

        if (component.resolve) {
            generateResolvedLocalVariable(builder, model, component);
            return;
        }

        List<CodeBlock> rawArguments = rawArguments(component);

        CodeBlock valueExpression =
                conversionExpression(model, component, rawArguments);

        builder.addStatement(
                "$T $L = $L",
                resolveType(component.type, model.packageName),
                component.name,
                valueExpression);
    }

    public List<MethodSpec> getConstructors() {
        return genConfig.constructors.stream().map(this::generateConstructor).toList();
    }


    // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // helper methods

    private CodeBlock conversionExpression(
            ModelDefinition model,
            ComponentDefinition component,
            List<CodeBlock> rawArguments) {

        if ("String".equals(component.type)) {
            return rawArguments.getFirst();
        }

        TypeRegistry.TypeDefinition type = TypeRegistry.get(component.type);

        if (type.converter == null || type.converter.isBlank()) {
            throw new IllegalArgumentException(
                    "No converter configured for type: " + component.type);
        }

        CodeBlock arguments = CodeBlock.join(rawArguments, ", ");

        CodeBlock converted =
                CodeBlock.of(
                        "$T.$L($L)",
                        conversionUtils(model),
                        type.converter,
                        arguments);

        if (!component.required && component.defaultValue != null) {
            return CodeBlock.of(
                    "$L == null ? $L : $L",
                    rawArguments.getFirst(),
                    defaultExpression(component),
                    converted);
        }

        return converted;
    }

    private String resolverName(ComponentDefinition component) {
        return component.name + "s";
    }

    private List<ComponentDefinition> resolveParameters(
            List<String> parameterNames,
            Map<String, ComponentDefinition> available,
            String context) {

        List<ComponentDefinition> result = new ArrayList<>();

        for (String parameterName : parameterNames) {
            ComponentDefinition parameter = available.get(parameterName);

            if (parameter == null) {
                throw new IllegalArgumentException(
                        "Unknown parameter '"
                                + parameterName
                                + "' in "
                                + context);
            }

            result.add(parameter);
        }

        return result;
    }

    private void generateResolvedLocalVariable(
            MethodSpec.Builder builder,
            ModelDefinition model,
            ComponentDefinition component) {

        List<CodeBlock> rawArguments = rawArguments(component);

        String resolverIdName = component.name + "Id";

        CodeBlock idExpression =
                conversionUtils(model) == null
                        ? CodeBlock.of("null")
                        : CodeBlock.of(
                        "$T.toUUID($L)",
                        conversionUtils(model),
                        rawArguments.getFirst());

        builder.addStatement(
                "$T $L = $L",
                UUID_CLASS,
                resolverIdName,
                idExpression);

        if (!component.required && component.defaultValue != null) {
            builder.addStatement(
                    "$T $L = $L == null ? $L : $L.apply($L)",
                    resolveType(component.type, model.packageName),
                    component.name,
                    resolverIdName,
                    defaultExpression(component),
                    resolverName(component),
                    resolverIdName);
        } else {
            builder.addStatement(
                    "$T $L = $L.apply($L)",
                    resolveType(component.type, model.packageName),
                    component.name,
                    resolverName(component),
                    resolverIdName);
        }
    }

    private CodeBlock defaultExpression(ModelDefinition.ComponentDefinition component) {

        if (component.defaultValue == null || component.defaultValue.isBlank()) {
            return CodeBlock.of("null");
        }

        String expression = component.defaultValue;
        return CodeBlock.of("$L", expression);
    }

    private List<CodeBlock> rawArguments(ComponentDefinition component) {
        if (component.keys.isEmpty()) {
            throw new IllegalArgumentException(
                    "Component '" + component.name + "' has no input keys.");
        }

        if ("FIRST_PRESENT".equals(component.keyMode)) {
            CodeBlock keys =
                    component.keys.stream()
                            .map(key -> CodeBlock.of("$S", key))
                            .collect(CodeBlock.joining(", "));

            return List.of(
                    component.required
                            ? CodeBlock.of("requiredFirstPresent(values, $L)", keys)
                            : CodeBlock.of("firstPresent(values, $L)", keys));
        }

        List<CodeBlock> result = new ArrayList<>();

        for (String key : component.keys) {
            result.add(
                    component.required
                            ? CodeBlock.of("required(values, $S)", key)
                            : CodeBlock.of("optional(values, $S)", key));
        }

        return result;
    }

    private ClassName conversionUtils(ModelDefinition model) {
        if (model.conversionUtils == null || model.conversionUtils.isBlank()) {
            throw new IllegalArgumentException(
                    "Model "
                            + model.name
                            + " uses fromMap but has no conversionUtils.");
        }

        return className(model.conversionUtils, model.packageName);
    }

    private List<ComponentDefinition> orderedResolverComponents(
            ModelDefinition model,
            Map<String, ComponentDefinition> allComponents) {

        List<ComponentDefinition> resolvers =
                allComponents.values().stream()
                        .filter(component -> component.resolve)
                        .toList();

        if (genConfig.resolverOrder.isEmpty()) {
            return resolvers;
        }

        List<ComponentDefinition> ordered = new ArrayList<>();

        for (String resolverName : genConfig.resolverOrder) {
            ComponentDefinition component = allComponents.get(resolverName);

            if (component == null || !component.resolve) {
                throw new IllegalArgumentException(
                        "Unknown resolver component '"
                                + resolverName
                                + "' in "
                                + model.name);
            }

            ordered.add(component);
        }

        if (ordered.size() != resolvers.size()) {
            throw new IllegalArgumentException(
                    "resolverOrder of "
                            + model.name
                            + " must contain every resolve: true component exactly once.");
        }

        return ordered;
    }

    private MethodSpec generateConstructor(GenerationConfig.ConstructorDefinition constructor) {

        Map<String, ModelDefinition.ComponentDefinition> visibleComponents =
                visibleComponents(model, models);

        List<ModelDefinition.ComponentDefinition> parameters =
                resolveParameters(
                        constructor.components,
                        visibleComponents,
                        model.name + "." + constructor.name);

        MethodSpec.Builder builder =
                MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC);

        for (ModelDefinition.ComponentDefinition parameter : parameters) {
            builder.addParameter(
                    resolveType(parameter.type, model.packageName),
                    parameter.name);
        }

        CodeBlock superArgs;
        if (model.extendsName != null && constructor.superArgs != null) {
            List<CodeBlock> argBlocks = new ArrayList<>();
            for (String arg : constructor.superArgs) {
                // If arg equals a parameter name, use variable; else if it's a visible component name, use variable;
                // otherwise treat as raw Java expression
                if (visibleComponents.containsKey(arg)) {
                    argBlocks.add(CodeBlock.of("$L", arg)); // variable reference
                } else {
                    // raw expression (e.g. "OperatorInput.NO_OPERATOR_ASSIGNED")
                    argBlocks.add(CodeBlock.of("$L", arg));
                }
            }
            superArgs = CodeBlock.join(argBlocks, ", ");
            builder.addStatement("super($L)", superArgs);
        } else if (model.extendsName != null) {
            // no superCall specified -> require explicit mapping or throw
            throw new IllegalArgumentException("Model " + model.name + " requires a superCall config for constructor " + constructor.name);
        }

        for (ModelDefinition.ComponentDefinition localField : model.components) {
            boolean isConstructorParameter =
                    parameters.stream()
                            .anyMatch(parameter -> parameter.name.equals(localField.name));

            TypeRegistry.TypeDefinition type = TypeRegistry.get(localField.type);

            if (!isConstructorParameter) {
                if (type.defaultExpression != null && !type.defaultExpression.isBlank()) {
                    CodeBlock expression = CodeBlock.of("$L", type.defaultExpression);
                    builder.addStatement("this.$L = $L", localField.name, expression);

                } else if (!localField.name.equals("additionalInformation")) {
                    throw new IllegalArgumentException(
                            "Constructor '"
                                    + constructor.name
                                    + "' of "
                                    + model.name
                                    + " does not initialize local field '"
                                    + localField.name
                                    + "'.");
                }

            } else {
                CodeBlock expression = CodeBlock.of("$L", localField.name);
                builder.addStatement("this.$L = $L", localField.name, expression);
            }

            boolean hasAdditionalInfoParam = parameters.stream()
                    .anyMatch(p -> "additionalInformation".equals(p.name));

            if (hasAdditionalInfoParam) {
                builder.addStatement("setAdditionalInformation(additionalInformation)");
            }
        }

        return builder.build();
    }

}
