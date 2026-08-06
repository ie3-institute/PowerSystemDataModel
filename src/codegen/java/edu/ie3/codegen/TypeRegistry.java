package edu.ie3.codegen;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TypeRegistry {
    private TypeRegistry() {}

    private static final Map<String, TypeDefinition> registry = new LinkedHashMap<>();

    static {
        registry.put("UUID", new TypeDefinition("java.util.UUID", "toUUID"));
        registry.put("String", TypeDefinition.primitive("java.lang.String"));
        registry.put("bool", TypeDefinition.primitive("boolean"));
        registry.put("int", TypeDefinition.primitive("int"));
        registry.put("StringMap", new TypeDefinition("java.util.Map", List.of("java.lang.String", "java.lang.String"), null, "Map.of()"));
        registry.put("OperatorInput", TypeDefinition.withDefault("edu.ie3.datamodel.models.input.OperatorInput", "OperatorInput.NO_OPERATOR_ASSIGNED"));
        registry.put("OperationTime", new TypeDefinition("edu.ie3.datamodel.models.OperationTime", List.of(), "buildOperationTime", "OperationTime.notLimited()"));
        registry.put("Point", new TypeDefinition("org.locationtech.jts.geom.Point", "toPointOrDefault"));
    }

    public static boolean containsKey(String name) {
        return registry.containsKey(name);
    }

    public static TypeDefinition get(String name) {
        if (registry.containsKey(name)) {
            return registry.get(name);
        }

        throw new IllegalArgumentException("Couldn't find type definition for name: "+ name);
    }

    public static final class TypeDefinition {
        public final String javaName;
        public final List<String> genericArguments;
        public final String converter; // name of method on ConversionUtils, nullable
        public final String defaultExpression; // optional for default mapping

        public TypeDefinition(String javaName, String converter) {
            this(javaName, List.of(), converter, null);
        }

        public TypeDefinition(String javaName, List<String> genericArguments, String converter, String defaultExpression) {
            this.javaName = javaName;
            this.genericArguments = List.copyOf(genericArguments);
            this.converter = converter;
            this.defaultExpression = defaultExpression;
        }

        public static TypeDefinition primitive(String javaName) {
            return new TypeDefinition(javaName, List.of(), null, null);
        }

        public static TypeDefinition withDefault(String javaName, String defaultExpr) {
            return new TypeDefinition(javaName, List.of(), null, defaultExpr);
        }
    }

}
