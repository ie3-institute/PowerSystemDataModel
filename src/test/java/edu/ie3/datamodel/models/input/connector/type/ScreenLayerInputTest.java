/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import static edu.ie3.util.quantities.PowerSystemUnits.*;
import static org.junit.jupiter.api.Assertions.*;

import edu.ie3.util.quantities.interfaces.ElectricalResistivity;
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

/** Unit tests for ScreenLayerInput class. */
@DisplayName("ScreenLayerInput Tests")
class ScreenLayerInputTest {
  private UUID uuid;
  private ComparableQuantity<Length> innerDiameter;
  private ComparableQuantity<Length> outerDiameter;
  private ComparableQuantity<ThermalResistivity> thermalResistivity;
  private ComparableQuantity<ThermalCapacitance> thermalCapacitance;
  private ComparableQuantity<Length> wireDiameter;
  private ComparableQuantity<ElectricalResistivity> materialResistivity;

  @BeforeEach
  void setUp() {
    uuid = UUID.randomUUID();
    innerDiameter = Quantities.getQuantity(0.025, Units.METRE);
    outerDiameter = Quantities.getQuantity(0.027, Units.METRE);
    thermalResistivity = Quantities.getQuantity(2.5, KELVIN_METRE_PER_WATT);
    thermalCapacitance = Quantities.getQuantity(2.4, JOULE_PER_CUBIC_METRE_KELVIN);
    wireDiameter = Quantities.getQuantity(0.0005, Units.METRE);
    materialResistivity = Quantities.getQuantity(1.7e-7, OHM_METRE);
  }

  @Test
  @DisplayName("Test ScreenLayerInput creation with valid parameters")
  void testScreenLayerInputCreation() {
    ScreenLayerInput screen = createValidScreenLayer();

    assertNotNull(screen);
    assertEquals("Copper screen", screen.name());
    assertEquals(CableMaterial.COPPER, screen.material());
    assertEquals(20, screen.wiresNumber());
  }

  @Test
  @DisplayName("Test ScreenLayerInput null name validation")
  void testScreenLayerInputNullName() {
    assertThrows(NullPointerException.class, this::createScreenLayerInputWithNullName);
  }

  private void createScreenLayerInputWithNullName() {
    new ScreenLayerInput(
        uuid,
        null,
        CableMaterial.COPPER,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        thermalCapacitance,
        Optional.empty(),
        20,
        wireDiameter,
        Optional.empty(),
        materialResistivity);
  }

  @Test
  @DisplayName("Test ScreenLayerInput empty name validation")
  void testScreenLayerInputEmptyName() {
    assertThrows(IllegalArgumentException.class, this::createScreenLayerInputWithEmptyName);
  }

  private void createScreenLayerInputWithEmptyName() {
    new ScreenLayerInput(
        uuid,
        "",
        CableMaterial.COPPER,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        thermalCapacitance,
        Optional.empty(),
        20,
        wireDiameter,
        Optional.empty(),
        materialResistivity);
  }

  @Test
  @DisplayName("Test ScreenLayerInput outerDiameter < innerDiameter validation")
  void testScreenLayerInputOuterDiameterLessThanInner() {
    assertThrows(
        IllegalArgumentException.class, this::createScreenLayerInputWithInvalidOuterDiameter);
  }

  private void createScreenLayerInputWithInvalidOuterDiameter() {
    ComparableQuantity<Length> invalidOuter = Quantities.getQuantity(0.02, Units.METRE);

    new ScreenLayerInput(
        uuid,
        "Invalid screen",
        CableMaterial.COPPER,
        innerDiameter,
        invalidOuter,
        thermalResistivity,
        thermalCapacitance,
        Optional.empty(),
        20,
        wireDiameter,
        Optional.empty(),
        materialResistivity);
  }

  @Test
  @DisplayName("Test ScreenLayerInput zero or negative wiresNumber validation")
  void testScreenLayerInputNegativeWiresNumber() {
    assertThrows(
        IllegalArgumentException.class, this::createScreenLayerInputWithInvalidWiresNumber);
  }

  private void createScreenLayerInputWithInvalidWiresNumber() {
    new ScreenLayerInput(
        uuid,
        "Invalid screen",
        CableMaterial.COPPER,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        thermalCapacitance,
        Optional.empty(),
        0,
        wireDiameter,
        Optional.empty(),
        materialResistivity);
  }

  @Test
  @DisplayName("Test ScreenLayerInput negative wireDiameter validation")
  void testScreenLayerInputNegativeWireDiameter() {
    assertThrows(
        IllegalArgumentException.class, this::createScreenLayerInputWithNegativeWireDiameter);
  }

  private void createScreenLayerInputWithNegativeWireDiameter() {
    ComparableQuantity<Length> negativeWireDiameter = Quantities.getQuantity(-0.001, Units.METRE);

    new ScreenLayerInput(
        uuid,
        "Invalid screen",
        CableMaterial.COPPER,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        thermalCapacitance,
        Optional.empty(),
        20,
        negativeWireDiameter,
        Optional.empty(),
        materialResistivity);
  }

  @Test
  @DisplayName("Test ScreenLayerInput with optional lengthOfLay")
  void testScreenLayerInputWithLengthOfLay() {
    ComparableQuantity<Length> lengthOfLay = Quantities.getQuantity(0.02, Units.METRE);
    ScreenLayerInput screen =
        new ScreenLayerInput(
            uuid,
            "Copper screen",
            CableMaterial.COPPER,
            innerDiameter,
            outerDiameter,
            thermalResistivity,
            thermalCapacitance,
            Optional.empty(),
            20,
            wireDiameter,
            Optional.of(lengthOfLay),
            materialResistivity);
    assertTrue(screen.lengthOfLay().isPresent());
    assertEquals(lengthOfLay, screen.lengthOfLay().get());
  }

  @Test
  @DisplayName("Test ScreenLayerInput with optional area")
  void testScreenLayerInputWithArea() {
    ComparableQuantity<Area> area = Quantities.getQuantity(5e-6, Units.SQUARE_METRE);
    ScreenLayerInput screen =
        new ScreenLayerInput(
            uuid,
            "Copper screen",
            CableMaterial.COPPER,
            innerDiameter,
            outerDiameter,
            thermalResistivity,
            thermalCapacitance,
            Optional.of(area),
            20,
            wireDiameter,
            Optional.empty(),
            materialResistivity);
    assertTrue(screen.area().isPresent());
    assertEquals(area, screen.area().get());
  }

  @Test
  @DisplayName("Test ScreenLayerInput equals method")
  void testScreenLayerInputEquals() {
    ScreenLayerInput screen1 = createValidScreenLayer();
    ScreenLayerInput screen2 = createValidScreenLayer();

    assertEquals(screen1, screen2);
  }

  @Test
  @DisplayName("Test ScreenLayerInput hashCode consistency")
  void testScreenLayerInputHashCode() {
    ScreenLayerInput screen1 = createValidScreenLayer();
    ScreenLayerInput screen2 = createValidScreenLayer();

    assertEquals(screen1.hashCode(), screen2.hashCode());
  }

  @Test
  @DisplayName("Test ScreenLayerInput toString")
  void testScreenLayerInputToString() {
    ScreenLayerInput screen = createValidScreenLayer();

    String str = screen.toString();
    assertNotNull(str);
    assertTrue(str.contains("Copper screen"));
    assertTrue(str.contains("COPPER"));
  }

  @Test
  @DisplayName("Test ScreenLayerInput with STEEL material")
  void testScreenLayerInputWithSteel() {
    ScreenLayerInput screen =
        new ScreenLayerInput(
            uuid,
            "Steel screen",
            CableMaterial.STEEL,
            innerDiameter,
            outerDiameter,
            thermalResistivity,
            thermalCapacitance,
            Optional.empty(),
            20,
            wireDiameter,
            Optional.empty(),
            materialResistivity);
    assertEquals(CableMaterial.STEEL, screen.material());
  }

  @Test
  @DisplayName("Test ScreenLayerInput null material validation")
  void testScreenLayerInputNullMaterial() {
    assertThrows(NullPointerException.class, this::createScreenLayerInputWithNullMaterial);
  }

  private void createScreenLayerInputWithNullMaterial() {
    new ScreenLayerInput(
        uuid,
        "Screen",
        null,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        thermalCapacitance,
        Optional.empty(),
        20,
        wireDiameter,
        Optional.empty(),
        materialResistivity);
  }

  @Test
  @DisplayName("Test ScreenLayerInput null materialResistivity validation")
  void testScreenLayerInputNullMaterialResistivity() {
    assertThrows(
        NullPointerException.class, this::createScreenLayerInputWithNullMaterialResistivity);
  }

  private void createScreenLayerInputWithNullMaterialResistivity() {
    new ScreenLayerInput(
        uuid,
        "Screen",
        CableMaterial.COPPER,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        thermalCapacitance,
        Optional.empty(),
        20,
        wireDiameter,
        Optional.empty(),
        null);
  }

  @Test
  @DisplayName("Test ScreenLayerInput negative materialResistivity validation")
  void testScreenLayerInputNegativeMaterialResistivity() {
    ComparableQuantity<ElectricalResistivity> negativeResistivity =
        Quantities.getQuantity(-1.0, OHM_METRE);
    assertThrows(
        IllegalArgumentException.class,
        this::createScreenLayerInputWithNegativeMaterialResistivity);
  }

  private void createScreenLayerInputWithNegativeMaterialResistivity() {
    ComparableQuantity<ElectricalResistivity> negativeResistivity =
        Quantities.getQuantity(-1.0, OHM_METRE);

    new ScreenLayerInput(
        uuid,
        "Screen",
        CableMaterial.COPPER,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        thermalCapacitance,
        Optional.empty(),
        20,
        wireDiameter,
        Optional.empty(),
        negativeResistivity);
  }

  @Test
  @DisplayName("Test ScreenLayerInput not equals for different wires number")
  void testScreenLayerInputNotEqualsWires() {
    ScreenLayerInput screen1 = createValidScreenLayer();

    ScreenLayerInput screen2 =
        new ScreenLayerInput(
            uuid,
            "Copper screen",
            CableMaterial.COPPER,
            innerDiameter,
            outerDiameter,
            thermalResistivity,
            thermalCapacitance,
            Optional.empty(),
            30, // changed
            wireDiameter,
            Optional.empty(),
            materialResistivity);

    assertNotEquals(screen1, screen2);
  }

  private ScreenLayerInput createValidScreenLayer() {
    return new ScreenLayerInput(
        uuid,
        "Copper screen",
        CableMaterial.COPPER,
        innerDiameter,
        outerDiameter,
        thermalResistivity,
        thermalCapacitance,
        Optional.empty(),
        20,
        wireDiameter,
        Optional.empty(),
        materialResistivity);
  }
}
