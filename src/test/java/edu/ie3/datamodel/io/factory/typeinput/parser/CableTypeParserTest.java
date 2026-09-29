/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput.parser;

import static org.junit.jupiter.api.Assertions.*;

import edu.ie3.datamodel.exceptions.ParsingException;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class CableTypeParserTest {

  private final ObjectMapper mapper = new ObjectMapper();
  private final CableTypeParser parser = new CableTypeParser(mapper);

  @Test
  void parseConductorWithNonObjectNodeThrowsParsingException() {
    assertThrows(
        ParsingException.class, () -> parser.parseConductor("[1, 2, 3]"), "Expected object");
  }

  @Test
  void parseConductorWithNullNodeThrowsParsingException() {
    assertThrows(ParsingException.class, () -> parser.parseConductor("null"), "Expected object");
  }

  @Test
  void parseLayerListWithNonArrayNodeThrowsParsingException() {
    assertThrows(
        ParsingException.class,
        () -> parser.parseLayerList("{\"key\":\"value\"}"),
        "Expected array");
  }

  @Test
  void parseLayerListElementWithInvalidMaterialThrowsParsingException() {
    String json =
        "[{\"uuid\":\"00000000-0000-0000-0000-000000000001\",\"name\":\"L1\",\"material\":\"INVALID_MATTERIAL\",\"innerDiameter\":\"10\",\"outerDiameter\":\"20\",\"thermalResistivity\":\"1\",\"thermalCapacitance\":\"2\"}]";
    ParsingException ex = assertThrows(ParsingException.class, () -> parser.parseLayerList(json));
    assertTrue(ex.getMessage().contains("invalid material"));
  }

  @Test
  void parseScreenLayerWithMissingMaterialThrowsParsingException() {
    String json =
        "{\"uuid\":\"00000000-0000-0000-0000-000000000001\",\"name\":\"S1\",\"innerDiameter\":\"10\",\"outerDiameter\":\"20\",\"thermalResistivity\":\"1\",\"thermalCapacitance\":\"2\",\"wiresNumber\":\"56\",\"wireDiameter\":\"0.9\",\"electricalResistivity\":\"1.7\"}";
    ParsingException ex = assertThrows(ParsingException.class, () -> parser.parseScreenLayer(json));
    assertTrue(ex.getMessage().contains("missing material"));
  }

  @Test
  void parseScreenLayerWithNonObjectNodeThrowsParsingException() {
    assertThrows(
        ParsingException.class, () -> parser.parseScreenLayer("[1,2,3]"), "Expected object");
  }

  @Test
  void parseConductorWithInvalidUuidThrowsParsingException() {
    String json =
        "{\"uuid\":\"not-a-valid-uuid\",\"name\":\"C1\",\"material\":\"COPPER\",\"crossSection\":\"10\",\"diameter\":\"5\",\"thermalResistivity\":\"1\",\"thermalCapacitance\":\"2\"}";
    ParsingException ex = assertThrows(ParsingException.class, () -> parser.parseConductor(json));
    assertTrue(ex.getMessage().contains("invalid uuid"));
  }

  @Test
  void parseConductorWithMissingIdThrowsParsingException() {
    String json =
        "{\"uuid\":\"00000000-0000-0000-0000-000000000001\",\"material\":\"COPPER\",\"crossSection\":\"10\",\"diameter\":\"5\",\"thermalResistivity\":\"1\",\"thermalCapacitance\":\"2\"}";
    ParsingException ex = assertThrows(ParsingException.class, () -> parser.parseConductor(json));
    assertTrue(ex.getMessage().contains("missing id"));
  }
}
