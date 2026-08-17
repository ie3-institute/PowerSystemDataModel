/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.codegen;

import com.squareup.javapoet.ClassName;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

public final class ClassRegistry {
  private ClassRegistry() {}

  private static final Map<String, ClassName> registry = new LinkedHashMap<>();

  private static void add(Class<?> clazz) {
    registry.put(clazz.getSimpleName(), ClassName.get(clazz));
    registry.put(clazz.getName(), ClassName.get(clazz));
  }

  static {
    registerJavaClasses();
    registerOwnClasses();
    registerQuantities();

    registry.put("GeoUtils", ClassName.get("edu.ie3.util.geo", "GeoUtils"));
    registry.put("Quantities", ClassName.get("tech.units.indriya.quantity", "Quantities"));
    registry.put("PowerSystemUnits", ClassName.get("edu.ie3.util.quantities", "PowerSystemUnits"));
  }

  public static boolean containsKey(String name) {
    return registry.containsKey(name);
  }

  public static ClassName get(String name) {
    if (registry.containsKey(name)) {
      return registry.get(name);
    }

    throw new IllegalArgumentException("Couldn't find class path definition for name: " + name);
  }

  static void registerJavaClasses() {
    Stream.of(Serializable.class, String.class, Collections.class, UUID.class, List.class)
        .forEach(ClassRegistry::add);
  }

  static void registerOwnClasses() {
    // validations
    registry.put(
        "ConnectorValidationUtils",
        ClassName.get("edu.ie3.datamodel.utils.validation", "ConnectorValidationUtils"));

    // extractor interfaces
    Stream.of("HasEm", "HasLine", "HasNodes", "HasThermalBus", "HasThermalStorage", "HasType")
        .forEach(name -> registry.put(name, ClassName.get("edu.ie3.datamodel.io.extractor", name)));

    // model package
    Stream.of("Entity", "Operable", "OperationTime", "StandardUnits", "UniqueEntity", "Uniqueness")
        .forEach(name -> registry.put(name, ClassName.get("edu.ie3.datamodel.models", name)));

    // model.input package
    Stream.of(
            "AssetInput",
            "AssetTypeInput",
            "EmInput",
            "NodeInput",
            "OperatorInput",
            "UniqueInputEntity")
        .forEach(name -> registry.put(name, ClassName.get("edu.ie3.datamodel.models.input", name)));

    // model.input.conector package

    // model.input.conector.type package
    Stream.of("LineTypeInput", "Transformer2WTypeInput", "Transformer3WTypeInput")
        .forEach(
            name ->
                registry.put(
                    name, ClassName.get("edu.ie3.datamodel.models.input.conector.type", name)));
  }

  static void registerQuantities() {
    Stream.of("Dimensionless", "Percent")
        .forEach(
            name -> registry.put(name, ClassName.get("javax.measure.quantity", "Dimensionless")));

    Stream.of("DegreeGeom")
        .forEach(name -> registry.put(name, ClassName.get("javax.measure.quantity", "Angle")));

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
        .forEach(name -> registry.put(name, ClassName.get("javax.measure.quantity", name)));

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
            name -> registry.put(name, ClassName.get("edu.ie3.util.quantities.interfaces", name)));
  }
}
