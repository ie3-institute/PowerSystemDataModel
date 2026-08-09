/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class TypeRegistry {
  private TypeRegistry() {}

  private static final Map<String, TypeDefinition> registry = new LinkedHashMap<>();

  static {
    registry.put("UUID", new TypeDefinition("java.util.UUID", "getUUID", "UUID.randomUUID()"));
    registry.put("String", new TypeDefinition("java.lang.String", "getField"));
    registry.put("bool", new TypeDefinition("boolean", "getBoolean"));
    registry.put("int", new TypeDefinition("int", "getInt"));
    registry.put(
        "StringMap",
        new TypeDefinition(
            "java.util.Map",
            List.of("java.lang.String", "java.lang.String"),
            null,
            "new HashMap<>()"));
    registry.put(
        "NodeList",
        new TypeDefinition("java.util.List", List.of("NodeInput"), null, "new ArrayList<>()"));
    registry.put(
        "OperatorInput",
        new TypeDefinition(
            "edu.ie3.datamodel.models.input.OperatorInput",
            "getEntity",
            "OperatorInput.NO_OPERATOR_ASSIGNED"));
    registry.put(
        "NodeInput", new TypeDefinition("edu.ie3.datamodel.models.input.NodeInput", "getEntity"));
    registry.put("AssetInput", new TypeDefinition("edu.ie3.datamodel.models.input.AssetInput"));
    registry.put(
        "LineTypeInput",
        new TypeDefinition("edu.ie3.datamodel.models.input.connector.type.LineTypeInput"));
    registry.put(
        "OperationTime",
        new TypeDefinition(
            "edu.ie3.datamodel.models.OperationTime",
            List.of(),
            "buildOperationTime",
            "OperationTime.notLimited()"));
    registry.put("Point", new TypeDefinition("org.locationtech.jts.geom.Point", "getNodePoint"));
    registry.put(
        "Dimensionless",
        new TypeDefinition(
            "tech.units.indriya.ComparableQuantity", List.of("Dimensionless"), "getDimensionless"));
    registry.put(
        "Length", new TypeDefinition("tech.units.indriya.ComparableQuantity", List.of("Length")));

    registry.put(
        "VoltageLevel",
        new TypeDefinition(
            "edu.ie3.datamodel.models.voltagelevels.VoltageLevel", List.of(), "getVoltageLvl"));
    registry.put("EmInput", new TypeDefinition("edu.ie3.datamodel.models.input.EmInput"));
    registry.put("LineString", new TypeDefinition("org.locationtech.jts.geom.LineString"));
    registry.put(
        "OlmCharacteristicInput",
        new TypeDefinition(
            "edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput"));
  }

  public static boolean containsKey(String name) {
    return registry.containsKey(name);
  }

  public static TypeDefinition get(String name) {
    if (registry.containsKey(name)) {
      return registry.get(name);
    }

    throw new IllegalArgumentException("Couldn't find type definition for name: " + name);
  }

  public static final class TypeDefinition {
    public final String javaName;
    public final List<String> genericArguments;
    public final String converter; // name of method on ConversionUtils, nullable
    public final String defaultExpression; // optional for default mapping

    public TypeDefinition(String javaName) {
      this(javaName, List.of(), null);
    }

    public TypeDefinition(String javaName, String converter) {
      this(javaName, List.of(), converter);
    }

    public TypeDefinition(String javaName, String converter, String defaultExpression) {
      this.javaName = javaName;
      this.genericArguments = List.of();
      this.converter = converter;
      this.defaultExpression = defaultExpression;
    }

    public TypeDefinition(String javaName, List<String> genericArguments) {
      this.javaName = javaName;
      this.genericArguments = List.copyOf(genericArguments);
      this.converter = null;
      this.defaultExpression = null;
    }

    public TypeDefinition(String javaName, List<String> genericArguments, String converter) {
      this.javaName = javaName;
      this.genericArguments = List.copyOf(genericArguments);
      this.converter = converter;
      this.defaultExpression = null;
    }

    public TypeDefinition(
        String javaName,
        List<String> genericArguments,
        String converter,
        String defaultExpression) {
      this.javaName = javaName;
      this.genericArguments = List.copyOf(genericArguments);
      this.converter = converter;
      this.defaultExpression = defaultExpression;
    }
  }
}
