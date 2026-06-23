/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import static edu.ie3.util.quantities.PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN;
import static edu.ie3.util.quantities.PowerSystemUnits.KELVIN_METRE_PER_WATT;
import static org.junit.jupiter.api.Assertions.*;

import edu.ie3.util.quantities.interfaces.ThermalCapacitance;
import edu.ie3.util.quantities.interfaces.ThermalResistivity;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/** Unit tests for LayerInput class. */
@DisplayName("LayerInput Tests")
class LayerInputTest {
  private UUID uuid;
  private ComparableQuantity<Length> innerDiameter;
  private ComparableQuantity<Length> outerDiameter;
  private ComparableQuantity<ThermalResistivity> thermalResistivity;
  private ComparableQuantity<ThermalCapacitance> thermalCapacitance;

  @BeforeEach
  void setUp() {
    uuid = UUID.randomUUID();
    innerDiameter = Quantities.getQuantity(0.01, Units.METRE);
    outerDiameter = Quantities.getQuantity(0.015, Units.METRE);
    thermalResistivity = Quantities.getQuantity(3.5, KELVIN_METRE_PER_WATT);
    thermalCapacitance = Quantities.getQuantity(2.4, JOULE_PER_CUBIC_METRE_KELVIN);
  }

  @Test
  @DisplayName("Test LayerInput creation with valid parameters")
  void testLayerInputCreation() {
    LayerInput layer =
        new LayerInput(
            uuid,
            "Main insulation",
            CableMaterial.XLPE,
            innerDiameter,
            outerDiameter,
            thermalResistivity,
            thermalCapacitance,
            Optional.empty());
    assertNotNull(layer);
    assertEquals("Main insulation", layer.name());
    assertEquals(CableMaterial.XLPE, layer.material());
  }

  @Test
  @DisplayName("Test LayerInput null name validation")
  void testLayerInputNullName() {
    assertThrows(
        NullPointerException.class,
        () ->
            new LayerInput(
                uuid,
                null,
                CableMaterial.XLPE,
                innerDiameter,
                outerDiameter,
                thermalResistivity,
                thermalCapacitance,
                Optional.empty()));
  }

  @Test
  @DisplayName("Test LayerInput empty name validation")
  void testLayerInputEmptyName() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new LayerInput(
                uuid,
                "",
                CableMaterial.XLPE,
                innerDiameter,
                outerDiameter,
                thermalResistivity,
                thermalCapacitance,
                Optional.empty()));
  }

  @Test
  @DisplayName("Test LayerInput outerDiameter < innerDiameter validation")
  void testLayerInputOuterDiameterLessThanInner() {
    ComparableQuantity<Length> invalidOuter = Quantities.getQuantity(0.005, Units.METRE);
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new LayerInput(
                uuid,
                "Invalid layer",
                CableMaterial.XLPE,
                innerDiameter,
                invalidOuter,
                thermalResistivity,
                thermalCapacitance,
                Optional.empty()));
  }

  @Test
  @DisplayName("Test LayerInput negative innerDiameter validation")
  void testLayerInputNegativeInnerDiameter() {
    ComparableQuantity<Length> negativeInner = Quantities.getQuantity(-0.001, Units.METRE);
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new LayerInput(
                uuid,
                "Invalid layer",
                CableMaterial.XLPE,
                negativeInner,
                outerDiameter,
                thermalResistivity,
                thermalCapacitance,
                Optional.empty()));
  }

  @Test
  @DisplayName("Test LayerInput negative thermalResistivity validation")
  void testLayerInputNegativeThermalResistivity() {
    ComparableQuantity<ThermalResistivity> negativeThermalRes =
        Quantities.getQuantity(-1.0, KELVIN_METRE_PER_WATT);
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new LayerInput(
                uuid,
                "Invalid layer",
                CableMaterial.XLPE,
                innerDiameter,
                outerDiameter,
                negativeThermalRes,
                thermalCapacitance,
                Optional.empty()));
  }

  @Test
  @DisplayName("Test LayerInput with optional area")
  void testLayerInputWithArea() {
    ComparableQuantity<Area> area = Quantities.getQuantity(50e-6, Units.SQUARE_METRE);
    LayerInput layer =
        new LayerInput(
            uuid,
            "Layer with area",
            CableMaterial.PE,
            innerDiameter,
            outerDiameter,
            thermalResistivity,
            thermalCapacitance,
            Optional.of(area));
    assertTrue(layer.area().isPresent());
    assertEquals(area, layer.area().get());
  }

  @Test
  @DisplayName("Test LayerInput equals method")
  void testLayerInputEquals() {
    LayerInput layer1 =
        new LayerInput(
            uuid,
            "Main insulation",
            CableMaterial.XLPE,
            innerDiameter,
            outerDiameter,
            thermalResistivity,
            thermalCapacitance,
            Optional.empty());
    LayerInput layer2 =
        new LayerInput(
            uuid,
            "Main insulation",
            CableMaterial.XLPE,
            innerDiameter,
            outerDiameter,
            thermalResistivity,
            thermalCapacitance,
            Optional.empty());
    assertEquals(layer1, layer2);
  }

  @Test
  @DisplayName("Test LayerInput hashCode consistency")
  void testLayerInputHashCode() {
    LayerInput layer1 =
        new LayerInput(
            uuid,
            "Main insulation",
            CableMaterial.XLPE,
            innerDiameter,
            outerDiameter,
            thermalResistivity,
            thermalCapacitance,
            Optional.empty());
    LayerInput layer2 =
        new LayerInput(
            uuid,
            "Main insulation",
            CableMaterial.XLPE,
            innerDiameter,
            outerDiameter,
            thermalResistivity,
            thermalCapacitance,
            Optional.empty());
    assertEquals(layer1.hashCode(), layer2.hashCode());
  }

  @Test
  @DisplayName("Test LayerInput toString")
  void testLayerInputToString() {
    LayerInput layer =
        new LayerInput(
            uuid,
            "Main insulation",
            CableMaterial.XLPE,
            innerDiameter,
            outerDiameter,
            thermalResistivity,
            thermalCapacitance,
            Optional.empty());
    String str = layer.toString();
    assertNotNull(str);
    assertTrue(str.contains("Main insulation"));
    assertTrue(str.contains("XLPE"));
  }

  @Test
  @DisplayName("Test LayerInput with PVC material")
  void testLayerInputWithPVC() {
    LayerInput layer =
        new LayerInput(
            uuid,
            "PVC jacket",
            CableMaterial.PVC,
            innerDiameter,
            outerDiameter,
            thermalResistivity,
            thermalCapacitance,
            Optional.empty());
    assertEquals(CableMaterial.PVC, layer.material());
  }

  @Test
  @DisplayName("Test LayerInput null material validation")
  void testLayerInputNullMaterial() {
    assertThrows(
        NullPointerException.class,
        () ->
            new LayerInput(
                uuid,
                "Layer",
                null,
                innerDiameter,
                outerDiameter,
                thermalResistivity,
                thermalCapacitance,
                Optional.empty()));
  }

  @Test
  @DisplayName("Test LayerInput null innerDiameter validation")
  void testLayerInputNullInnerDiameter() {
    assertThrows(
        NullPointerException.class,
        () ->
            new LayerInput(
                uuid,
                "Layer",
                CableMaterial.XLPE,
                null,
                outerDiameter,
                thermalResistivity,
                thermalCapacitance,
                Optional.empty()));
  }

  @Test
  @DisplayName("Test LayerInput null Optional parameter validation")
  void testLayerInputNullOptional() {
    assertThrows(
        NullPointerException.class,
        () ->
            new LayerInput(
                uuid,
                "Layer",
                CableMaterial.XLPE,
                innerDiameter,
                outerDiameter,
                thermalResistivity,
                thermalCapacitance,
                null));
  }

  @Test
  @DisplayName("Test LayerInput zero diameter validation")
  void testLayerInputZeroDiameters() {
    ComparableQuantity<Length> zeroDiameter = Quantities.getQuantity(0.0, Units.METRE);
    LayerInput layer =
        new LayerInput(
            uuid,
            "Zero layer",
            CableMaterial.XLPE,
            zeroDiameter,
            zeroDiameter,
            thermalResistivity,
            thermalCapacitance,
            Optional.empty());
    assertNotNull(layer);
  }
}
