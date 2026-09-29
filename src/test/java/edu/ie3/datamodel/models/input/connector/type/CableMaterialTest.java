/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/** Unit tests for CableMaterial enum and its thermal/electrical property methods. */
@DisplayName("CableMaterial Tests")
class CableMaterialTest {

  static Stream<Arguments> thermalPropertiesArgs() {
    return Stream.of(
        Arguments.of(CableMaterial.COPPER, 0.002604166667, 3449600.0, 1e-12, 1e-5),
        Arguments.of(CableMaterial.ALUMINIUM, 0.0042194092827, 2420913.3, 1e-12, 1e-5),
        Arguments.of(CableMaterial.XLPE, 3.5, 2.4e6, 1e-3, 1e-3),
        Arguments.of(CableMaterial.PVC, 5.0, 1.7e6, 1e-3, 1e-3),
        Arguments.of(CableMaterial.SEMI_COND_SCREEN, 2.5, 2.4e6, 1e-3, 1e-3),
        Arguments.of(CableMaterial.SC_TAPE, 6.0, 2.4e6, 1e-3, 1e-3));
  }

  @ParameterizedTest
  @MethodSource("thermalPropertiesArgs")
  @DisplayName("Test thermal properties")
  void testThermalProperties(
      CableMaterial material,
      double expectedResistivity,
      double expectedCapacitance,
      double resistivityDelta,
      double capacitanceDelta) {
    CableMaterial.ThermalProperties props = material.getThermalProperties();
    assertNotNull(props);
    assertNotNull(props.resistivity());
    assertNotNull(props.capacitance());
    assertEquals(
        expectedResistivity, props.resistivity().getValue().doubleValue(), resistivityDelta);
    assertEquals(
        expectedCapacitance, props.capacitance().getValue().doubleValue(), capacitanceDelta);
  }

  @Test
  @DisplayName("Test COPPER electrical resistivity")
  void testCopperElectricalResistivity() {
    var resistivity = CableMaterial.COPPER.getElectricalResistivity();
    assertNotNull(resistivity);
    assertEquals(1.7241e-8, resistivity.getValue().doubleValue(), 1e-12);
  }

  @Test
  @DisplayName("Test ALUMINIUM electrical resistivity")
  void testAluminiumElectricalResistivity() {
    var resistivity = CableMaterial.ALUMINIUM.getElectricalResistivity();
    assertNotNull(resistivity);
    assertEquals(2.8264e-8, resistivity.getValue().doubleValue(), 1e-12);
  }

  @Test
  @DisplayName("Test STEEL electrical resistivity")
  void testSteelElectricalResistivity() {
    var resistivity = CableMaterial.STEEL.getElectricalResistivity();
    assertNotNull(resistivity);
    assertEquals(13.8e-8, resistivity.getValue().doubleValue(), 1e-12);
  }

  @Test
  @DisplayName("Test insulation materials throw exception for electrical resistivity")
  void testInsulationElectricalResistivity() {
    assertThrows(IllegalArgumentException.class, CableMaterial.XLPE::getElectricalResistivity);
    assertThrows(IllegalArgumentException.class, CableMaterial.PE::getElectricalResistivity);
    assertThrows(IllegalArgumentException.class, CableMaterial.PVC::getElectricalResistivity);
    assertThrows(
        IllegalArgumentException.class, CableMaterial.SEMI_COND_SCREEN::getElectricalResistivity);
    assertThrows(IllegalArgumentException.class, CableMaterial.SC_TAPE::getElectricalResistivity);
    assertThrows(
        IllegalArgumentException.class, CableMaterial.POLYPROPYLENE::getElectricalResistivity);
  }

  @Test
  @DisplayName("Test COPPER temperature coefficient")
  void testCopperTemperatureCoefficient() {
    double coeff = CableMaterial.COPPER.getElectricalResistivityTemperatureCoefficient();
    assertEquals(3.93e-3, coeff, 1e-8);
  }

  @Test
  @DisplayName("Test ALUMINIUM temperature coefficient")
  void testAluminiumTemperatureCoefficient() {
    double coeff = CableMaterial.ALUMINIUM.getElectricalResistivityTemperatureCoefficient();
    assertEquals(4.03e-3, coeff, 1e-8);
  }

  @Test
  @DisplayName("Test LEAD temperature coefficient")
  void testLeadTemperatureCoefficient() {
    double coeff = CableMaterial.LEAD.getElectricalResistivityTemperatureCoefficient();
    assertEquals(4.0e-3, coeff, 1e-8);
  }

  @Test
  @DisplayName("Test STEEL temperature coefficient")
  void testSteelTemperatureCoefficient() {
    double coeff = CableMaterial.STEEL.getElectricalResistivityTemperatureCoefficient();
    assertEquals(4.5e-3, coeff, 1e-8);
  }

  @Test
  @DisplayName("Test insulation materials throw exception for temperature coefficient")
  void testInsulationTemperatureCoefficient() {
    assertThrows(
        IllegalArgumentException.class,
        CableMaterial.XLPE::getElectricalResistivityTemperatureCoefficient);
    assertThrows(
        IllegalArgumentException.class,
        CableMaterial.PE::getElectricalResistivityTemperatureCoefficient);
    assertThrows(
        IllegalArgumentException.class,
        CableMaterial.PVC::getElectricalResistivityTemperatureCoefficient);
    assertThrows(
        IllegalArgumentException.class,
        CableMaterial.SEMI_COND_SCREEN::getElectricalResistivityTemperatureCoefficient);
    assertThrows(
        IllegalArgumentException.class,
        CableMaterial.SC_TAPE::getElectricalResistivityTemperatureCoefficient);
    assertThrows(
        IllegalArgumentException.class,
        CableMaterial.POLYPROPYLENE::getElectricalResistivityTemperatureCoefficient);
  }

  @Test
  @DisplayName("Test all materials have thermal properties")
  void testAllMaterialsHaveThermalProperties() {
    for (CableMaterial material : CableMaterial.values()) {
      assertDoesNotThrow(material::getThermalProperties);
    }
  }
}
