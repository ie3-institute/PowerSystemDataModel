/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import com.palantir.javapoet.ClassName;
import com.palantir.javapoet.ParameterizedTypeName;
import com.palantir.javapoet.TypeName;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

public class ResolverUtils {

  private ResolverUtils() {}

  private static final Map<String, ClassName> classes = new HashMap<>();
  private static final Map<String, String> defaultExpressions = new HashMap<>();
  private static final Map<String, CustomType> customTypes = new HashMap<>();

  public static ClassName resolveClassName(String name) {
    if (classes.containsKey(name)) {
      return classes.get(name);
    }

    throw new IllegalArgumentException("Couldn't find class path definition for name: " + name);
  }

  public static Optional<String> getDefaultExpression(String type) {
    return Optional.ofNullable(defaultExpressions.get(type));
  }

  public static TypeName resolveType(String name) {
    if (customTypes.containsKey(name)) {
      CustomType type = customTypes.get(name);

      if (type.genericArguments.isEmpty()) {
        return type.name;
      }

      TypeName[] genericArguments = type.genericArguments.toArray(TypeName[]::new);

      return ParameterizedTypeName.get(type.name, genericArguments);
    }

    return switch (name) {
      case "bool", "boolean" -> TypeName.BOOLEAN;
      case "int" -> TypeName.INT;
      case "double" -> TypeName.DOUBLE;
      default -> resolveClassName(name);
    };
  }

  private static void add(Class<?> clazz) {
    classes.put(clazz.getSimpleName(), ClassName.get(clazz));
    classes.put(clazz.getName(), ClassName.get(clazz));
  }

  static {
    registerJavaClasses();
    registerOwnClasses();
    registerOtherClasses();
    registerQuantities();
    registerCustomTypes();
    addDefaultExpressions();
  }

  static void registerJavaClasses() {
    Stream.of(
            Serializable.class, String.class, Collections.class, UUID.class, List.class, Map.class)
        .forEach(ResolverUtils::add);
  }

  static void registerOwnClasses() {
    classes.put("GeoUtils", ClassName.get("edu.ie3.util.geo", "GeoUtils"));
    classes.put("Quantities", ClassName.get("tech.units.indriya.quantity", "Quantities"));
    classes.put("PowerSystemUnits", ClassName.get("edu.ie3.util.quantities", "PowerSystemUnits"));

    // validations
    classes.put(
        "ConnectorValidationUtils",
        ClassName.get("edu.ie3.datamodel.utils.validation", "ConnectorValidationUtils"));

    // extractor interfaces
    Stream.of("HasEm", "HasLine", "HasNodes", "HasThermalBus", "HasThermalStorage", "HasType")
        .forEach(name -> classes.put(name, ClassName.get("edu.ie3.datamodel.io.extractor", name)));

    // model package
    Stream.of("Entity", "Operable", "OperationTime", "StandardUnits", "UniqueEntity", "Uniqueness")
        .forEach(name -> classes.put(name, ClassName.get("edu.ie3.datamodel.models", name)));

    classes.put(
        "VoltageLevel", ClassName.get("edu.ie3.datamodel.models.voltagelevels", "VoltageLevel"));

    // model.input package
    Stream.of(
            "AssetInput",
            "AssetTypeInput",
            "EmInput",
            "InputEntity",
            "NodeInput",
            "OperatorInput",
            "UniqueInputEntity")
        .forEach(name -> classes.put(name, ClassName.get("edu.ie3.datamodel.models.input", name)));

    classes.put(
        "OlmCharacteristicInput",
        ClassName.get(
            "edu.ie3.datamodel.models.input.system.characteristic", "OlmCharacteristicInput"));
    classes.put(
        "ReactivePowerCharacteristic",
        ClassName.get(
            "edu.ie3.datamodel.models.input.system.characteristic", "ReactivePowerCharacteristic"));

    // model.input.conector package
    Stream.of("ConnectorInput", "TransformerInput")
        .forEach(
            name ->
                classes.put(name, ClassName.get("edu.ie3.datamodel.models.input.connector", name)));

    // model.input.conector.type package
    Stream.of("LineTypeInput", "Transformer2WTypeInput", "Transformer3WTypeInput")
        .forEach(
            name ->
                classes.put(
                    name, ClassName.get("edu.ie3.datamodel.models.input.connector.type", name)));

    // participant
    Stream.of("SystemParticipantInput")
        .forEach(
            name ->
                classes.put(name, ClassName.get("edu.ie3.datamodel.models.input.system", name)));

    // participant types
    Stream.of(
            "AcTypeInput",
            "BmTypeInput",
            "ChpTypeInput",
            "EvTypeInput",
            "HpTypeInput",
            "StorageTypeInput",
            "SystemParticipantTypeInput",
            "WecTypeInput")
        .forEach(
            name ->
                classes.put(
                    name, ClassName.get("edu.ie3.datamodel.models.input.system.type", name)));

    classes.put(
        "ChargingPointType",
        ClassName.get(
            "edu.ie3.datamodel.models.input.system.type.chargingpoint", "ChargingPointType"));
    classes.put(
        "EvcsLocationType",
        ClassName.get(
            "edu.ie3.datamodel.models.input.system.type.evcslocation", "EvcsLocationType"));
    classes.put(
        "WecCharacteristicInput",
        ClassName.get(
            "edu.ie3.datamodel.models.input.system.characteristic", "WecCharacteristicInput"));
    classes.put(
        "PowerProfileKey", ClassName.get("edu.ie3.datamodel.models.profile", "PowerProfileKey"));

    // thermal
    Stream.of(
            "ThermalBusInput",
            "ThermalStorageInput",
            "ThermalInput",
            "ThermalUnitInput",
            "AbstractStorageInput",
            "ThermalSinkInput")
        .forEach(
            name ->
                classes.put(name, ClassName.get("edu.ie3.datamodel.models.input.thermal", name)));
  }

  static void registerQuantities() {
    classes.put("ComparableQuantity", ClassName.get("tech.units.indriya", "ComparableQuantity"));

    Stream.of("Dimensionless", "Percent")
        .forEach(
            name -> classes.put(name, ClassName.get("javax.measure.quantity", "Dimensionless")));

    Stream.of("DegreeGeom")
        .forEach(name -> classes.put(name, ClassName.get("javax.measure.quantity", "Angle")));

    Stream.of(
            "Length",
            "ElectricCurrent",
            "ElectricPotential",
            "ElectricResistance",
            "ElectricConductance",
            "Power",
            "Energy",
            "Angle",
            "Area",
            "Volume",
            "Temperature",
            "SpecificHeatCapacity")
        .forEach(name -> classes.put(name, ClassName.get("javax.measure.quantity", name)));

    Stream.of(
            "Currency",
            "Density",
            "DimensionlessRate",
            "ElectricalResistivity",
            "EnergyDensity",
            "EnergyPrice",
            "HeatCapacity",
            "Irradiance",
            "Irradiation",
            "PowerDensity",
            "PricePerLength",
            "SpecificCapacitance",
            "SpecificConductance",
            "SpecificEnergy",
            "SpecificHeatCapacity",
            "SpecificResistance",
            "ThermalCapacitance",
            "ThermalConductance",
            "ThermalResistivity",
            "VolumetricFlowRate")
        .forEach(
            name -> classes.put(name, ClassName.get("edu.ie3.util.quantities.interfaces", name)));
  }

  static void registerOtherClasses() {
    Stream.of("Point", "LineString")
        .forEach(name -> classes.put(name, ClassName.get("org.locationtech.jts.geom", name)));
  }

  static void registerCustomTypes() {
    customTypes.put("StringMap", new CustomType("Map", List.of("String", "String")));
    customTypes.put("NodeList", new CustomType("List", List.of("NodeInput")));

    Stream.of(
            "SpecificConductance",
            "SpecificResistance",
            "Length",
            "ElectricCurrent",
            "ElectricPotential",
            "ElectricResistance",
            "ElectricConductance",
            "Power",
            "EnergyPrice",
            "Energy",
            "Currency",
            "DimensionlessRate",
            "SpecificEnergy",
            "Area",
            "Volume",
            "Temperature",
            "SpecificHeatCapacity",
            "ThermalConductance",
            "HeatCapacity")
        .forEach(
            name -> customTypes.put(name, new CustomType("ComparableQuantity", List.of(name))));

    Stream.of("DegreeGeom")
        .forEach(
            name -> customTypes.put(name, new CustomType("ComparableQuantity", List.of("Angle"))));

    Stream.of("Dimensionless", "Percent")
        .forEach(
            name ->
                customTypes.put(
                    name, new CustomType("ComparableQuantity", List.of("Dimensionless"))));
  }

  static void addDefaultExpressions() {
    defaultExpressions.put("UUID", "UUID.randomUUID()");
    defaultExpressions.put("Map", "new HashMap<>()");
    defaultExpressions.put("StringMap", "new HashMap<>()");
    defaultExpressions.put("List", "new ArrayList<>()");
    defaultExpressions.put("OperatorInput", "OperatorInput.NO_OPERATOR_ASSIGNED");
    defaultExpressions.put("OperationTime", "OperationTime.notLimited()");
  }

  public record CustomType(ClassName name, List<ClassName> genericArguments) {
    public CustomType(String name, List<String> genericArguments) {
      this(
          resolveClassName(name),
          genericArguments.stream().map(ResolverUtils::resolveClassName).toList());
    }
  }
}
