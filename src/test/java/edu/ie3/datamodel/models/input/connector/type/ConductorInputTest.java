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

/** Unit tests for ConductorInput class. */
@DisplayName("ConductorInput Tests")
class ConductorInputTest {

  private UUID uuid;
  private ComparableQuantity<Area> crossSection;
  private ComparableQuantity<Length> diameter;
  private ComparableQuantity<ThermalResistivity> thermalResistivity;
  private ComparableQuantity<ThermalCapacitance> thermalCapacitance;
  private Optional<ComparableQuantity<Area>> emptyArea;

  @BeforeEach
  void setUp() {
    uuid = UUID.fromString("d2074b88-1b54-4032-b721-e71f96f6b3ac");
    crossSection = Quantities.getQuantity(400e-6, Units.SQUARE_METRE);
    diameter = Quantities.getQuantity(0.0225, Units.METRE);
    thermalResistivity = Quantities.getQuantity(1.0 / 384.0, KELVIN_METRE_PER_WATT);
    thermalCapacitance = Quantities.getQuantity(3449600.0, JOULE_PER_CUBIC_METRE_KELVIN);
    emptyArea = Optional.empty();
  }

  @Test
  @DisplayName("Test ConductorInput creation with valid parameters")
  void testConductorInputCreation() {
    ConductorInput conductor =
        new ConductorInput(
            uuid,
            "conductor",
            CableMaterial.COPPER,
            crossSection,
            diameter,
            false,
            thermalResistivity,
            thermalCapacitance,
            Optional.empty());
    assertNotNull(conductor);
    assertEquals(CableMaterial.COPPER, conductor.material());
    assertEquals(crossSection, conductor.crossSection());
    assertFalse(conductor.isCompacted());
  }

  @Test
  @DisplayName("Test ConductorInput with compacted")
  void testConductorInputCompacted() {
    ConductorInput conductor =
        new ConductorInput(
            uuid,
            "conductor",
            CableMaterial.COPPER,
            crossSection,
            diameter,
            true,
            thermalResistivity,
            thermalCapacitance,
            Optional.empty());
    assertTrue(conductor.isCompacted());
  }

  @Test
  @DisplayName("Test ConductorInput null material validation")
  void testConductorInputNullMaterial() {
    assertThrows(
        NullPointerException.class,
        () ->
            new ConductorInput(
                uuid,
                "conductor",
                null,
                crossSection,
                diameter,
                false,
                thermalResistivity,
                thermalCapacitance,
                emptyArea));
  }

  @Test
  @DisplayName("Test ConductorInput negative crossSection validation")
  void testConductorInputNegativeCrossSection() {
    ComparableQuantity<Area> negativeCrossSection =
        Quantities.getQuantity(-1e-6, Units.SQUARE_METRE);
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ConductorInput(
                uuid,
                "conductor",
                CableMaterial.COPPER,
                negativeCrossSection,
                diameter,
                false,
                thermalResistivity,
                thermalCapacitance,
                emptyArea));
  }

  @Test
  @DisplayName("Test ConductorInput negative diameter validation")
  void testConductorInputNegativeDiameter() {
    ComparableQuantity<Length> negativeDiameter = Quantities.getQuantity(-0.001, Units.METRE);
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ConductorInput(
                uuid,
                "conductor",
                CableMaterial.COPPER,
                crossSection,
                negativeDiameter,
                false,
                thermalResistivity,
                thermalCapacitance,
                emptyArea));
  }

  @Test
  @DisplayName("Test ConductorInput with optional area")
  void testConductorInputWithArea() {
    ComparableQuantity<Area> area = Quantities.getQuantity(380e-6, Units.SQUARE_METRE);
    Optional<ComparableQuantity<Area>> optionalArea = Optional.of(area);

    ConductorInput conductor =
        new ConductorInput(
            uuid,
            "conductor",
            CableMaterial.COPPER,
            crossSection,
            diameter,
            false,
            thermalResistivity,
            thermalCapacitance,
            optionalArea);

    assertEquals(area, conductor.area());
  }

  @Test
  @DisplayName("Test ConductorInput equals method")
  void testConductorInputEquals() {
    ConductorInput conductor1 = createValidConductor();
    ConductorInput conductor2 = createValidConductor();

    assertEquals(conductor1, conductor2);
  }

  @Test
  @DisplayName("Test ConductorInput hashCode consistency")
  void testConductorInputHashCode() {
    ConductorInput conductor1 = createValidConductor();
    ConductorInput conductor2 = createValidConductor();

    assertEquals(conductor1.hashCode(), conductor2.hashCode());
  }

  @Test
  @DisplayName("Test ConductorInput toString")
  void testConductorInputToString() {
    ConductorInput conductor = createValidConductor();

    String str = conductor.toString();
    assertNotNull(str);
    assertTrue(str.contains("COPPER"));
  }

  @Test
  void testAreaOptionalEmptyByDefault() {
    ConductorInput conductor = createValidConductor();

    assertNull(conductor.area());
  }

  @Test
  void testConductorInputNullOptionalArea() {
    Optional<ComparableQuantity<Area>> nullArea = null;

    assertThrows(
        NullPointerException.class,
        () ->
            new ConductorInput(
                uuid,
                "conductor",
                CableMaterial.COPPER,
                crossSection,
                diameter,
                false,
                thermalResistivity,
                thermalCapacitance,
                nullArea));
  }

  @Test
  void testConductorInputNullThermalResistivity() {
    assertThrows(
        NullPointerException.class,
        () ->
            new ConductorInput(
                uuid,
                "conductor",
                CableMaterial.COPPER,
                crossSection,
                diameter,
                false,
                null,
                thermalCapacitance,
                emptyArea));
  }

  @Test
  void testConductorInputNullThermalCapacitance() {
    assertThrows(
        NullPointerException.class,
        () ->
            new ConductorInput(
                uuid,
                "conductor",
                CableMaterial.COPPER,
                crossSection,
                diameter,
                false,
                thermalResistivity,
                null,
                emptyArea));
  }

  @Test
  void testConductorInputNullName() {
    assertThrows(
        NullPointerException.class,
        () ->
            new ConductorInput(
                uuid,
                null,
                CableMaterial.COPPER,
                crossSection,
                diameter,
                false,
                thermalResistivity,
                thermalCapacitance,
                emptyArea));
  }

  @Test
  void testConductorInputEmptyName() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ConductorInput(
                uuid,
                "",
                CableMaterial.COPPER,
                crossSection,
                diameter,
                false,
                thermalResistivity,
                thermalCapacitance,
                emptyArea));
  }

  @Test
  @DisplayName("Test ConductorInput with ALUMINIUM material")
  void testConductorInputWithAluminium() {
    ConductorInput conductor =
        new ConductorInput(
            uuid,
            "conductor",
            CableMaterial.ALUMINIUM,
            crossSection,
            diameter,
            false,
            thermalResistivity,
            thermalCapacitance,
            emptyArea);
    assertEquals(CableMaterial.ALUMINIUM, conductor.material());
  }

  @Test
  @DisplayName("Test ConductorInput null crossSection validation")
  void testConductorInputNullCrossSection() {
    assertThrows(
        NullPointerException.class,
        () ->
            new ConductorInput(
                uuid,
                "conductor",
                CableMaterial.COPPER,
                null,
                diameter,
                false,
                thermalResistivity,
                thermalCapacitance,
                emptyArea));
  }

  @Test
  @DisplayName("Test ConductorInput null diameter validation")
  void testConductorInputNullDiameter() {
    assertThrows(
        NullPointerException.class,
        () ->
            new ConductorInput(
                uuid,
                "conductor",
                CableMaterial.COPPER,
                crossSection,
                null,
                false,
                thermalResistivity,
                thermalCapacitance,
                emptyArea));
  }

  @Test
  @DisplayName("Test ConductorInput negative thermalCapacitance validation")
  void testConductorInputNegativeThermalCapacitance() {
    ComparableQuantity<ThermalCapacitance> negativeThermalCap =
        Quantities.getQuantity(-1.0, JOULE_PER_CUBIC_METRE_KELVIN);
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new ConductorInput(
                uuid,
                "conductor",
                CableMaterial.COPPER,
                crossSection,
                diameter,
                false,
                thermalResistivity,
                negativeThermalCap,
                emptyArea));
  }

  @Test
  @DisplayName("Test ConductorInput not equals for different materials")
  void testConductorInputNotEquals() {
    ConductorInput conductor1 =
        new ConductorInput(
            uuid,
            "conductor",
            CableMaterial.COPPER,
            crossSection,
            diameter,
            false,
            thermalResistivity,
            thermalCapacitance,
            emptyArea);
    ConductorInput conductor2 =
        new ConductorInput(
            uuid,
            "conductor",
            CableMaterial.ALUMINIUM,
            crossSection,
            diameter,
            false,
            thermalResistivity,
            thermalCapacitance,
            emptyArea);
    assertNotEquals(conductor1, conductor2);
  }

  @Test
  @DisplayName("Test ConductorInput not equals for different compacted state")
  void testConductorInputNotEqualsCompacted() {
    ConductorInput conductor1 =
        new ConductorInput(
            uuid,
            "conductor",
            CableMaterial.COPPER,
            crossSection,
            diameter,
            false,
            thermalResistivity,
            thermalCapacitance,
            emptyArea);
    ConductorInput conductor2 =
        new ConductorInput(
            uuid,
            "conductor",
            CableMaterial.COPPER,
            crossSection,
            diameter,
            true,
            thermalResistivity,
            thermalCapacitance,
            emptyArea);
    assertNotEquals(conductor1, conductor2);
  }

  private ConductorInput createValidConductor() {
    return new ConductorInput(
        uuid,
        "conductor",
        CableMaterial.COPPER,
        crossSection,
        diameter,
        false,
        thermalResistivity,
        thermalCapacitance,
        emptyArea);
  }
}
