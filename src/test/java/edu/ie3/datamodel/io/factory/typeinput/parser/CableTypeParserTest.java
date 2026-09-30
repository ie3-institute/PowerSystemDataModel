/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput.parser;

import static org.junit.jupiter.api.Assertions.*;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.input.connector.type.ConductorInput;
import edu.ie3.datamodel.models.input.connector.type.LayerInput;
import edu.ie3.datamodel.models.input.connector.type.ScreenLayerInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import tech.units.indriya.quantity.Quantities;
import tools.jackson.databind.ObjectMapper;

/** Unit tests for CableTypeParser. */
@DisplayName("CableTypeParser Tests")
class CableTypeParserTest {
  ObjectMapper mapper = new ObjectMapper();
  CableTypeParser parser = new CableTypeParser(mapper);

  @Test
  @DisplayName("Test parseConductor with non-object node throws ParsingException")
  void testParseConductorWithNonObjectNode() {
    assertThrows(
        ParsingException.class, () -> parser.parseConductor("[1, 2, 3]"), "Expected object");
  }

  @Test
  @DisplayName("Test parseConductor with null node throws ParsingException")
  void testParseConductorWithNullNode() {
    assertThrows(ParsingException.class, () -> parser.parseConductor("null"), "Expected object");
  }

  @Test
  @DisplayName("Test parseLayerList with non-array node throws ParsingException")
  void testParseLayerListWithNonArrayNode() {
    assertThrows(
        ParsingException.class,
        () -> parser.parseLayerList("{\"key\":\"value\"}"),
        "Expected array");
  }

  @Test
  @DisplayName("Test parseConductor with missing id throws ParsingException")
  void testParseConductorWithMissingId() {
    String json =
        "{\"uuid\":\"00000000-0000-0000-0000-000000000001\",\"material\":\"COPPER\",\"crossSection\":\"10\",\"diameter\":\"5\",\"thermalResistivity\":\"1\",\"thermalCapacitance\":\"2\"}";
    ParsingException ex = assertThrows(ParsingException.class, () -> parser.parseConductor(json));
    assertTrue(ex.getMessage().contains("missing id"));
  }

  @Test
  @DisplayName("Test parseConductor with missing UUID throws ParsingException")
  void testParseConductorWithMissingUuid() {
    String json =
        "{\"name\":\"C1\",\"material\":\"COPPER\",\"crossSection\":\"10\",\"diameter\":\"5\",\"thermalResistivity\":\"1\",\"thermalCapacitance\":\"2\"}";
    ParsingException ex = assertThrows(ParsingException.class, () -> parser.parseConductor(json));
    assertTrue(ex.getMessage().contains("missing uuid"));
  }

  @Test
  @DisplayName("Test parseLayerList element with missing UUID throws ParsingException")
  void testParseLayerListElementWithMissingUuid() {
    String json =
        "[{\"name\":\"L1\",\"material\":\"XLPE\",\"innerDiameter\":\"10\",\"outerDiameter\":\"20\",\"thermalResistivity\":\"1\",\"thermalCapacitance\":\"2\"}]";
    ParsingException ex = assertThrows(ParsingException.class, () -> parser.parseLayerList(json));
    assertTrue(ex.getMessage().contains("missing uuid"));
  }

  @Test
  @DisplayName("Test parseScreenLayer with missing UUID throws ParsingException")
  void testParseScreenLayerWithMissingUuid() {
    String json =
        "{\"name\":\"S1\",\"material\":\"COPPER\",\"innerDiameter\":\"10\",\"outerDiameter\":\"20\",\"thermalResistivity\":\"1\",\"thermalCapacitance\":\"2\",\"wiresNumber\":\"56\",\"wireDiameter\":\"0.9\",\"electricalResistivity\":\"1.7\"}";
    ParsingException ex = assertThrows(ParsingException.class, () -> parser.parseScreenLayer(json));
    assertTrue(ex.getMessage().contains("missing uuid"));
  }

  @Test
  @DisplayName("Test parseScreenLayer with non-object node throws ParsingException")
  void testParseScreenLayerWithNonObjectNode() {
    assertThrows(
        ParsingException.class, () -> parser.parseScreenLayer("[1,2,3]"), "Expected object");
  }

  @Test
  @DisplayName("Test parseScreenLayer with wrapped screen object throws ParsingException")
  void testParseScreenLayerWithWrappedScreenObject() {
    String json =
        "{\"screen\":{\"uuid\":\"00000000-0000-0000-0000-000000000001\",\"name\":\"S1\",\"material\":\"COPPER\",\"innerDiameter\":\"10\",\"outerDiameter\":\"20\",\"thermalResistivity\":\"1\",\"thermalCapacitance\":\"2\",\"wiresNumber\":\"56\",\"wireDiameter\":\"0.9\",\"electricalResistivity\":\"1.7\"}}";
    ParsingException ex = assertThrows(ParsingException.class, () -> parser.parseScreenLayer(json));
    assertTrue(ex.getMessage().contains("missing material"));
  }

  @Test
  @DisplayName("Test parseConductor resolves common layer fields")
  void testParseConductorResolvesCommonLayerFields() throws ParsingException {
    String json =
        "{\"uuid\":\"00000000-0000-0000-0000-000000000001\",\"name\":\"C1\",\"material\":\"COPPER\",\"crossSection\":\"10\",\"diameter\":\"5\",\"innerDiameter\":\"5\",\"outerDiameter\":\"7\",\"thermalResistivity\":\"0.0026\",\"thermalCapacitance\":\"3449600\",\"area\":\"240\"}";
    ConductorInput conductor = parser.parseConductor(json);

    assertNotNull(conductor);
    assertEquals("C1", conductor.name());
    assertEquals(
        Quantities.getQuantity(0.0026, PowerSystemUnits.KELVIN_METRE_PER_WATT),
        conductor.thermalResistivity(),
        "thermalResistivity is resolved from the common layer fields");
    assertEquals(
        Quantities.getQuantity(3449600.0, PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN),
        conductor.thermalCapacitance(),
        "thermalCapacitance is resolved from the common layer fields");
    assertEquals(
        Quantities.getQuantity(240.0, PowerSystemUnits.SQUARE_MILLIMETRE),
        conductor.area().orElse(null),
        "area is resolved from the common layer fields");
  }

  @Test
  @DisplayName("Test parseLayerList resolves common layer fields per element")
  void testParseLayerListResolvesCommonLayerFieldsPerElement() throws ParsingException {
    String json =
        "[{\"uuid\":\"00000000-0000-0000-0000-000000000001\",\"name\":\"L1\",\"material\":\"XLPE\",\"innerDiameter\":\"18.4\",\"outerDiameter\":\"19.4\",\"thermalResistivity\":\"4.0\",\"thermalCapacitance\":\"2.0E6\",\"area\":\"30\"}]";
    List<LayerInput> layers = parser.parseLayerList(json);

    assertEquals(1, layers.size());
    LayerInput layer = layers.getFirst();
    assertEquals(Quantities.getQuantity(18.4, PowerSystemUnits.MILLIMETRE), layer.innerDiameter());
    assertEquals(Quantities.getQuantity(19.4, PowerSystemUnits.MILLIMETRE), layer.outerDiameter());
    assertEquals(
        Quantities.getQuantity(4.0, PowerSystemUnits.KELVIN_METRE_PER_WATT),
        layer.thermalResistivity());
    assertEquals(
        Quantities.getQuantity(2.0E6, PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN),
        layer.thermalCapacitance());
    assertEquals(
        Quantities.getQuantity(30.0, PowerSystemUnits.SQUARE_MILLIMETRE),
        layer.area().orElse(null));
  }

  @Test
  @DisplayName(
      "Test parseLayerList element with missing thermalCapacitance throws ParsingException")
  void testParseLayerListElementWithMissingThermalCapacitance() {
    String json =
        "[{\"uuid\":\"00000000-0000-0000-0000-000000000001\",\"name\":\"L1\",\"material\":\"XLPE\",\"innerDiameter\":\"18.4\",\"outerDiameter\":\"19.4\",\"thermalResistivity\":\"4.0\"}]";
    ParsingException ex = assertThrows(ParsingException.class, () -> parser.parseLayerList(json));
    assertTrue(ex.getMessage().contains("missing thermalCapacitance"));
  }

  @Test
  @DisplayName("Test parseScreenLayer resolves common layer fields")
  void testParseScreenLayerResolvesCommonLayerFields() throws ParsingException {
    String json =
        "{\"uuid\":\"00000000-0000-0000-0000-000000000001\",\"name\":\"S1\",\"material\":\"COPPER\",\"innerDiameter\":\"36.8\",\"outerDiameter\":\"38.6\",\"thermalResistivity\":\"0.0026\",\"thermalCapacitance\":\"3449600\",\"area\":\"35.6\",\"wiresNumber\":\"56\",\"wireDiameter\":\"0.9\",\"electricalResistivity\":\"1.7241E-8\"}";
    ScreenLayerInput screen = parser.parseScreenLayer(json);

    assertNotNull(screen);
    assertEquals(Quantities.getQuantity(36.8, PowerSystemUnits.MILLIMETRE), screen.innerDiameter());
    assertEquals(Quantities.getQuantity(38.6, PowerSystemUnits.MILLIMETRE), screen.outerDiameter());
    assertEquals(
        Quantities.getQuantity(0.0026, PowerSystemUnits.KELVIN_METRE_PER_WATT),
        screen.thermalResistivity());
    assertEquals(
        Quantities.getQuantity(3449600.0, PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN),
        screen.thermalCapacitance());
    assertEquals(
        Quantities.getQuantity(35.6, PowerSystemUnits.SQUARE_MILLIMETRE),
        screen.area().orElse(null));
  }

  @ParameterizedTest(name = "{0} with invalid data throws ParsingException")
  @MethodSource("invalidCableComponentInputs")
  @DisplayName("Test invalid cable components throw ParsingException")
  void testInvalidCableComponents(String component, String json, String expectedMessage) {
    ParsingException ex =
        assertThrows(ParsingException.class, () -> parseComponent(component, json));
    assertTrue(ex.getMessage().contains(expectedMessage));
  }

  private void parseComponent(String component, String json) throws ParsingException {
    switch (component) {
      case "layer" -> parser.parseLayerList(json);
      case "screen layer" -> parser.parseScreenLayer(json);
      case "conductor" -> parser.parseConductor(json);
      default -> throw new IllegalArgumentException("Unsupported component: " + component);
    }
  }

  private static Stream<Arguments> invalidCableComponentInputs() {
    return Stream.of(
        Arguments.of(
            "layer",
            "[{\"uuid\":\"00000000-0000-0000-0000-000000000001\",\"name\":\"L1\",\"material\":\"INVALID_MATTERIAL\",\"innerDiameter\":\"10\",\"outerDiameter\":\"20\",\"thermalResistivity\":\"1\",\"thermalCapacitance\":\"2\"}]",
            "invalid material"),
        Arguments.of(
            "screen layer",
            "{\"uuid\":\"00000000-0000-0000-0000-000000000001\",\"name\":\"S1\",\"innerDiameter\":\"10\",\"outerDiameter\":\"20\",\"thermalResistivity\":\"1\",\"thermalCapacitance\":\"2\",\"wiresNumber\":\"56\",\"wireDiameter\":\"0.9\",\"electricalResistivity\":\"1.7\"}",
            "missing material"),
        Arguments.of(
            "conductor",
            "{\"uuid\":\"not-a-valid-uuid\",\"name\":\"C1\",\"material\":\"COPPER\",\"crossSection\":\"10\",\"diameter\":\"5\",\"thermalResistivity\":\"1\",\"thermalCapacitance\":\"2\"}",
            "invalid uuid"));
  }
}
