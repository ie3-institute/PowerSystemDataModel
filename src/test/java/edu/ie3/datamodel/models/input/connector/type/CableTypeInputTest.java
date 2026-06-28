/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import static edu.ie3.util.quantities.PowerSystemUnits.*;
import static org.junit.jupiter.api.Assertions.*;

import edu.ie3.util.quantities.interfaces.ThermalCapacitance;
import edu.ie3.util.quantities.interfaces.ThermalResistivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.measure.quantity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/** Unit tests for CableTypeInput class. */
@DisplayName("CableTypeInput Tests")
class CableTypeInputTest {

  private UUID testUuid;
  private ConductorInput conductor;
  private List<LayerInput> isolation;
  private List<LayerInput> filler;
  private List<LayerInput> armor;
  private List<LayerInput> jack;
  private ComparableQuantity<Temperature> limitTemperature;
  private ComparableQuantity<Frequency> frequency;
  private ComparableQuantity<ElectricCapacitance> electricalCapacitance;

  @BeforeEach
  void setUp() {
    testUuid = UUID.randomUUID();

    // conductor
    ComparableQuantity<Area> crossSection = Quantities.getQuantity(400.0e-6, Units.SQUARE_METRE);
    ComparableQuantity<Length> diameter = Quantities.getQuantity(0.0225, Units.METRE);
    ComparableQuantity<ThermalResistivity> thermalResistivity =
        Quantities.getQuantity(1.0 / 384.0, KELVIN_METRE_PER_WATT);
    ComparableQuantity<ThermalCapacitance> thermalCapacitance =
        Quantities.getQuantity(3449600.0, JOULE_PER_CUBIC_METRE_KELVIN);
    conductor =
        new ConductorInput(
            UUID.fromString("793da55e-6021-4468-af1c-5ec61576bb20"),
            "conductor",
            CableMaterial.COPPER,
            crossSection,
            diameter,
            false,
            thermalResistivity,
            thermalCapacitance,
            Optional.empty());

    // isolation
    isolation = new ArrayList<>();
    ComparableQuantity<Length> innerDiam = Quantities.getQuantity(0.0225, Units.METRE);
    ComparableQuantity<Length> outerDiam = Quantities.getQuantity(0.027, Units.METRE);
    ComparableQuantity<ThermalResistivity> layerThermalRes =
        Quantities.getQuantity(3.5, KELVIN_METRE_PER_WATT);
    ComparableQuantity<ThermalCapacitance> layerThermalCap =
        Quantities.getQuantity(2.4, JOULE_PER_CUBIC_METRE_KELVIN);

    LayerInput isolationLayer =
        new LayerInput(
            UUID.randomUUID(),
            "Main insulation",
            CableMaterial.XLPE,
            innerDiam,
            outerDiam,
            layerThermalRes,
            layerThermalCap,
            Optional.empty());
    isolation.add(isolationLayer);

    // other layers
    filler = new ArrayList<>();
    armor = new ArrayList<>();
    jack = new ArrayList<>();

    limitTemperature = Quantities.getQuantity(90.0, Units.CELSIUS);
    frequency = Quantities.getQuantity(50.0, Units.HERTZ);
    electricalCapacitance = Quantities.getQuantity(350e-9, Units.FARAD);
  }

  @Test
  @DisplayName("Test CableTypeInput creation with valid parameters")
  void testCableTypeInputCreation() {
    CableTypeInput cable =
        new CableTypeInput(
            testUuid,
            "NA2XS2Y 1x120 RM/25 12/20 kV",
            1,
            conductor,
            isolation,
            Optional.empty(),
            filler,
            armor,
            jack,
            limitTemperature,
            frequency,
            1.0,
            1.0,
            electricalCapacitance,
            0.1,
            0.0,
            0.0);
    assertNotNull(cable);
    assertEquals(testUuid, cable.getUuid());
    assertEquals("NA2XS2Y 1x120 RM/25 12/20 kV", cable.getId());
    assertEquals(1, cable.getCoreNumber());
  }

  @Test
  @DisplayName("Test CableTypeInput with three cores")
  void testCableTypeInputThreeCores() {
    CableTypeInput cable =
        new CableTypeInput(
            testUuid,
            "3-core cable",
            3,
            conductor,
            isolation,
            Optional.empty(),
            filler,
            armor,
            jack,
            limitTemperature,
            frequency,
            1.0,
            1.0,
            electricalCapacitance,
            0.1,
            0.0,
            0.0);
    assertEquals(3, cable.getCoreNumber());
  }

  @Test
  @DisplayName("Test CableTypeInput empty ID validation")
  void testCableTypeInputEmptyId() {
    assertThrows(IllegalArgumentException.class, this::createCableTypeInputWithEmptyId);
  }

  private void createCableTypeInputWithEmptyId() {
    new CableTypeInput(
        testUuid,
        "",
        1,
        conductor,
        isolation,
        Optional.empty(),
        filler,
        armor,
        jack,
        limitTemperature,
        frequency,
        1.0,
        1.0,
        electricalCapacitance,
        0.1,
        0.0,
        0.0);
  }

  @Test
  @DisplayName("Test CableTypeInput invalid coreNumber validation")
  void testCableTypeInputInvalidCoreNumber() {
    assertThrows(IllegalArgumentException.class, this::createCableTypeInputWithInvalidCoreNumber);
  }

  private void createCableTypeInputWithInvalidCoreNumber() {
    new CableTypeInput(
        testUuid,
        "Test cable",
        0,
        conductor,
        isolation,
        Optional.empty(),
        filler,
        armor,
        jack,
        limitTemperature,
        frequency,
        1.0,
        1.0,
        electricalCapacitance,
        0.1,
        0.0,
        0.0);
  }

  @Test
  @DisplayName("Test CableTypeInput invalid frequency validation")
  void testCableTypeInputInvalidFrequency() {
    assertThrows(IllegalArgumentException.class, this::createCableTypeInputWithInvalidFrequency);
  }

  private void createCableTypeInputWithInvalidFrequency() {
    ComparableQuantity<Frequency> negativeFrequency = Quantities.getQuantity(-50.0, Units.HERTZ);

    new CableTypeInput(
        testUuid,
        "Test cable",
        1,
        conductor,
        isolation,
        Optional.empty(),
        filler,
        armor,
        jack,
        limitTemperature,
        negativeFrequency,
        1.0,
        1.0,
        electricalCapacitance,
        0.1,
        0.0,
        0.0);
  }

  @Test
  @DisplayName("Test CableTypeInput with screen layer")
  void testCableTypeInputWithScreen() {
    ComparableQuantity<Length> screenInner = Quantities.getQuantity(0.027, Units.METRE);
    ComparableQuantity<Length> screenOuter = Quantities.getQuantity(0.028, Units.METRE);
    ComparableQuantity<Length> wireDiameter = Quantities.getQuantity(0.0005, Units.METRE);
    ComparableQuantity<ThermalResistivity> screenThermalRes =
        Quantities.getQuantity(2.5, KELVIN_METRE_PER_WATT);
    ComparableQuantity<ThermalCapacitance> screenThermalCap =
        Quantities.getQuantity(2.4, JOULE_PER_CUBIC_METRE_KELVIN);
    var electricResistivity = Quantities.getQuantity(1.7e-7, OHM_METRE);

    ScreenLayerInput screen =
        new ScreenLayerInput(
            UUID.randomUUID(),
            "Copper screen",
            CableMaterial.COPPER,
            screenInner,
            screenOuter,
            screenThermalRes,
            screenThermalCap,
            Optional.empty(),
            20,
            wireDiameter,
            Optional.empty(),
            electricResistivity);

    CableTypeInput cable =
        new CableTypeInput(
            testUuid,
            "Cable with screen",
            1,
            conductor,
            isolation,
            Optional.of(screen),
            filler,
            armor,
            jack,
            limitTemperature,
            frequency,
            1.0,
            1.0,
            electricalCapacitance,
            0.1,
            0.0,
            0.0);
    assertTrue(cable.getScreen().isPresent());
    assertEquals(screen, cable.getScreen().get());
  }

  @Test
  @DisplayName("Test CableTypeInput equals method")
  void testCableTypeInputEquals() {
    CableTypeInput cable1 =
        new CableTypeInput(
            testUuid,
            "Test cable",
            1,
            conductor,
            isolation,
            Optional.empty(),
            filler,
            armor,
            jack,
            limitTemperature,
            frequency,
            1.0,
            1.0,
            electricalCapacitance,
            0.1,
            0.0,
            0.0);
    CableTypeInput cable2 =
        new CableTypeInput(
            testUuid,
            "Test cable",
            1,
            conductor,
            isolation,
            Optional.empty(),
            filler,
            armor,
            jack,
            limitTemperature,
            frequency,
            1.0,
            1.0,
            electricalCapacitance,
            0.1,
            0.0,
            0.0);
    assertEquals(cable1, cable2);
  }

  @Test
  @DisplayName("Test CableTypeInput hashCode consistency")
  void testCableTypeInputHashCode() {
    CableTypeInput cable1 =
        new CableTypeInput(
            testUuid,
            "Test cable",
            1,
            conductor,
            isolation,
            Optional.empty(),
            filler,
            armor,
            jack,
            limitTemperature,
            frequency,
            1.0,
            1.0,
            electricalCapacitance,
            0.1,
            0.0,
            0.0);
    CableTypeInput cable2 =
        new CableTypeInput(
            testUuid,
            "Test cable",
            1,
            conductor,
            isolation,
            Optional.empty(),
            filler,
            armor,
            jack,
            limitTemperature,
            frequency,
            1.0,
            1.0,
            electricalCapacitance,
            0.1,
            0.0,
            0.0);
    assertEquals(cable1.hashCode(), cable2.hashCode());
  }

  @Test
  @DisplayName("Test CableTypeInput toString")
  void testCableTypeInputToString() {
    CableTypeInput cable =
        new CableTypeInput(
            testUuid,
            "Test cable",
            1,
            conductor,
            isolation,
            Optional.empty(),
            filler,
            armor,
            jack,
            limitTemperature,
            frequency,
            1.0,
            1.0,
            electricalCapacitance,
            0.1,
            0.0,
            0.0);
    String str = cable.toString();
    assertNotNull(str);
    assertTrue(str.contains("Test cable"));
  }

  @Test
  @DisplayName("Test CableTypeInput invalid skinEffectCoefficient validation")
  void testCableTypeInputInvalidSkinEffectCoefficient() {
    assertThrows(
        IllegalArgumentException.class, this::createCableTypeInputWithInvalidSkinEffectCoefficient);
  }

  private void createCableTypeInputWithInvalidSkinEffectCoefficient() {
    new CableTypeInput(
        testUuid,
        "Test cable",
        1,
        conductor,
        isolation,
        Optional.empty(),
        filler,
        armor,
        jack,
        limitTemperature,
        frequency,
        -1.0,
        1.0,
        electricalCapacitance,
        0.1,
        0.0,
        0.0);
  }

  @Test
  @DisplayName("Test CableTypeInput invalid tanDelta validation")
  void testCableTypeInputInvalidTanDelta() {
    assertThrows(IllegalArgumentException.class, this::createCableTypeInputWithInvalidTanDelta);
  }

  private void createCableTypeInputWithInvalidTanDelta() {
    new CableTypeInput(
        testUuid,
        "Test cable",
        1,
        conductor,
        isolation,
        Optional.empty(),
        filler,
        armor,
        jack,
        limitTemperature,
        frequency,
        1.0,
        1.0,
        electricalCapacitance,
        -0.1,
        0.0,
        0.0);
  }

  @Test
  @DisplayName("Test CableTypeInput getters")
  void testCableTypeInputGetters() {
    CableTypeInput cable =
        new CableTypeInput(
            testUuid,
            "Test cable",
            2,
            conductor,
            isolation,
            Optional.empty(),
            filler,
            armor,
            jack,
            limitTemperature,
            frequency,
            1.5,
            2.0,
            electricalCapacitance,
            0.15,
            0.05,
            0.02);

    assertEquals(testUuid, cable.getUuid());
    assertEquals("Test cable", cable.getId());
    assertEquals(2, cable.getCoreNumber());
    assertEquals(conductor, cable.getConductor());
    assertEquals(isolation, cable.getIsolation());
    assertEquals(filler, cable.getFiller());
    assertEquals(armor, cable.getArmor());
    assertEquals(jack, cable.getJack());
    assertEquals(1.5, cable.getSkinEffectCoefficient(), 0.001);
    assertEquals(2.0, cable.getProximityEffectCoefficient(), 0.001);
    assertEquals(0.15, cable.getTanDelta(), 0.001);
    assertEquals(0.05, cable.getCirculatingLossFactor(), 0.001);
    assertEquals(0.02, cable.getEddyCurrentLossFactor(), 0.001);
  }

  @Test
  @DisplayName("Test CableTypeInput not equals for different UUIDs")
  void testCableTypeInputNotEqualsUuid() {
    UUID differentUuid = UUID.randomUUID();
    CableTypeInput cable1 =
        new CableTypeInput(
            testUuid,
            "Test cable",
            1,
            conductor,
            isolation,
            Optional.empty(),
            filler,
            armor,
            jack,
            limitTemperature,
            frequency,
            1.0,
            1.0,
            electricalCapacitance,
            0.1,
            0.0,
            0.0);
    CableTypeInput cable2 =
        new CableTypeInput(
            differentUuid,
            "Test cable",
            1,
            conductor,
            isolation,
            Optional.empty(),
            filler,
            armor,
            jack,
            limitTemperature,
            frequency,
            1.0,
            1.0,
            electricalCapacitance,
            0.1,
            0.0,
            0.0);
    assertNotEquals(cable1, cable2);
  }

  @Test
  @DisplayName("Test CableTypeInput isolationElements list is immutable")
  void testCableTypeInputIsolationElementsImmutable() {
    CableTypeInput cable =
        new CableTypeInput(
            testUuid,
            "Test cable",
            1,
            conductor,
            isolation,
            Optional.empty(),
            filler,
            armor,
            jack,
            limitTemperature,
            frequency,
            1.0,
            1.0,
            electricalCapacitance,
            0.1,
            0.0,
            0.0);

    List<LayerInput> retrievedLayers = cable.getIsolation();
    assertThrows(UnsupportedOperationException.class, () -> retrievedLayers.add(null));
  }
}
