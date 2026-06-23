/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.connector.type;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Unit tests for CableMaterial enum and its thermal/electrical property methods. */
@DisplayName("CableMaterial Tests")
class CableMaterialTest {

  @Test
  @DisplayName("Test COPPER thermal properties")
  void testCopperThermalProperties() {
    CableMaterial.ThermalProperties props = CableMaterial.COPPER.getThermalProperties();
    assertNotNull(props);
    assertNotNull(props.resistivity());
    assertNotNull(props.capacitance());
    assertEquals(0.002604166667, props.resistivity().getValue().doubleValue(), 1e-12);
    assertEquals(3449600.0, props.capacitance().getValue().doubleValue(), 1e-5);
  }

  @Test
  @DisplayName("Test ALUMINIUM thermal properties")
  void testAluminiumThermalProperties() {
    CableMaterial.ThermalProperties props = CableMaterial.ALUMINIUM.getThermalProperties();
    assertNotNull(props);
    assertEquals(0.0042194092827, props.resistivity().getValue().doubleValue(), 1e-12);
    assertEquals(2420913.3, props.capacitance().getValue().doubleValue(), 1e-5);
  }

  @Test
  @DisplayName("Test XLPE thermal properties")
  void testXlpeThermalProperties() {
    CableMaterial.ThermalProperties props = CableMaterial.XLPE.getThermalProperties();
    assertNotNull(props);
    assertEquals(3.5, props.resistivity().getValue().doubleValue(), 1e-3);
    assertEquals(2.4, props.capacitance().getValue().doubleValue(), 1e-3);
  }

  @Test
  @DisplayName("Test UNKNOWN material throws exception for thermal properties")
  void testUnknownThermalProperties() {
    assertThrows(IllegalArgumentException.class, CableMaterial.UNKNOWN::getThermalProperties);
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
  @DisplayName("Test UNKNOWN material throws exception for electrical resistivity")
  void testUnknownElectricalResistivity() {
    assertThrows(IllegalArgumentException.class, CableMaterial.UNKNOWN::getElectricalResistivity);
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
  @DisplayName("Test UNKNOWN material throws exception for temperature coefficient")
  void testUnknownTemperatureCoefficient() {
    assertThrows(
        IllegalArgumentException.class,
        CableMaterial.UNKNOWN::getElectricalResistivityTemperatureCoefficient);
  }

  @Test
  @DisplayName("Test all non-UNKNOWN materials have thermal properties")
  void testAllMaterialsHaveThermalProperties() {
    for (CableMaterial material : CableMaterial.values()) {
      if (material != CableMaterial.UNKNOWN) {
        assertDoesNotThrow(material::getThermalProperties);
      }
    }
  }

  @Test
  @DisplayName("Test PVC thermal properties")
  void testPvcThermalProperties() {
    CableMaterial.ThermalProperties props = CableMaterial.PVC.getThermalProperties();
    assertNotNull(props);
    assertEquals(5.0, props.resistivity().getValue().doubleValue(), 1e-3);
    assertEquals(1.7, props.capacitance().getValue().doubleValue(), 1e-3);
  }

  @Test
  @DisplayName("Test SEMI_COND_SCREEN thermal properties")
  void testSemiCondScreenThermalProperties() {
    CableMaterial.ThermalProperties props = CableMaterial.SEMI_COND_SCREEN.getThermalProperties();
    assertNotNull(props);
    assertEquals(2.5, props.resistivity().getValue().doubleValue(), 1e-3);
    assertEquals(2.4, props.capacitance().getValue().doubleValue(), 1e-3);
  }

  @Test
  @DisplayName("Test SC_TAPE thermal properties")
  void testScTapeThermalProperties() {
    CableMaterial.ThermalProperties props = CableMaterial.SC_TAPE.getThermalProperties();
    assertNotNull(props);
    assertEquals(6.0, props.resistivity().getValue().doubleValue(), 1e-3);
    assertEquals(2.4, props.capacitance().getValue().doubleValue(), 1e-3);
  }
}
