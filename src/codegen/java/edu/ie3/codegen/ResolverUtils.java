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

/** Utility class for resolving types and classes. */
public class ResolverUtils {

  private ResolverUtils() {}

  private static final Map<String, ClassName> classes = new HashMap<>();
  private static final Map<String, String> defaultExpressions = new HashMap<>();
  private static final Map<String, CustomType> customTypes = new HashMap<>();

  /**
   * Method for resolving the class name.
   *
   * @param name of the class
   * @return the class name object
   */
  public static ClassName resolveClassName(String name) {
    if (classes.containsKey(name)) {
      return classes.get(name);
    }

    throw new IllegalArgumentException("Couldn't find class path definition for name: " + name);
  }

  /**
   * Method for retrieving a default expression.
   *
   * @param type of the component
   * @return an option for a default expression
   */
  public static Optional<String> getDefaultExpression(String type) {
    return Optional.ofNullable(defaultExpressions.get(type));
  }

  /**
   * Method for resolving the type of field.
   *
   * @param name of the type
   * @return the type name
   */
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

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // helper method for registering class names and types

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
    classes.put("CollectionUtils", ClassName.get("edu.ie3.datamodel.utils", "CollectionUtils"));
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
            "IdCoordinateInput",
            "MeasurementUnitInput",
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
    Stream.of(
            "ConnectorInput",
            "TransformerInput",
            "LineInput",
            "SwitchInput",
            "Transformer2WInput",
            "Transformer3WInput")
        .forEach(
            name ->
                classes.put(name, ClassName.get("edu.ie3.datamodel.models.input.connector", name)));

    // model.input.conector.type package
    Stream.of(
            "CableMaterial",
            "CableTypeInput",
            "ConductorInput",
            "LayerInput",
            "LineTypeInput",
            "ScreenLayerInput",
            "Transformer2WTypeInput",
            "Transformer3WTypeInput")
        .forEach(
            name ->
                classes.put(
                    name, ClassName.get("edu.ie3.datamodel.models.input.connector.type", name)));

    // participant
    Stream.of("SystemParticipantInput")
        .forEach(
            name ->
                classes.put(name, ClassName.get("edu.ie3.datamodel.models.input.system", name)));

    // participant
    Stream.of(
            "AcInput",
            "BmInput",
            "ChpInput",
            "EvcsInput",
            "EvInput",
            "FixedFeedInInput",
            "HpInput",
            "LoadInput",
            "PvInput",
            "StorageInput",
            "SystemParticipantInput",
            "WecInput")
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
            "ThermalHouseInput",
            "ThermalUnitInput",
            "AbstractStorageInput",
            "CylindricalStorageInput",
            "DomesticHotWaterStorageInput",
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
            "ElectricCapacitance",
            "Power",
            "Energy",
            "Frequency",
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
    customTypes.put("LayerList", new CustomType("List", List.of("LayerInput")));

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
            "HeatCapacity",
            "ElectricCapacitance",
            "Frequency",
            "ThermalResistivity",
            "ThermalCapacitance",
            "ElectricalResistivity")
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

  /**
   * Record for custom types.
   *
   * @param name of the base class
   * @param genericArguments arguments to use
   */
  public record CustomType(ClassName name, List<ClassName> genericArguments) {
    public CustomType(String name, List<String> genericArguments) {
      this(
          resolveClassName(name),
          genericArguments.stream().map(ResolverUtils::resolveClassName).toList());
    }
  }
}
