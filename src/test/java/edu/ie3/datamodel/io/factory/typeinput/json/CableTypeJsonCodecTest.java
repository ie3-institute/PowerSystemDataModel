/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput.json;

import static org.junit.jupiter.api.Assertions.*;

import edu.ie3.datamodel.io.factory.typeinput.parser.CableTypeParser;
import edu.ie3.datamodel.models.input.connector.type.CableMaterial;
import edu.ie3.datamodel.models.input.connector.type.ConductorInput;
import edu.ie3.datamodel.models.input.connector.type.LayerInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class CableTypeJsonCodecTest {

  @Test
  void conductorSerializationProducesCompactStrings() throws Exception {
    CableTypeJsonCodec codec = new CableTypeJsonCodec();
    ObjectMapper mapper = new ObjectMapper();

    ConductorInput conductor =
        new ConductorInput(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            "cond-1",
            CableMaterial.COPPER,
            Quantities.getQuantity(10.0, PowerSystemUnits.SQUARE_MILLIMETRE),
            Quantities.getQuantity(5.0, PowerSystemUnits.MILLIMETRE),
            false,
            Quantities.getQuantity(1.0, PowerSystemUnits.KELVIN_METRE_PER_WATT),
            Quantities.getQuantity(2.0, PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN),
            null);

    String json = codec.writeConductor(conductor);

    JsonNode root = mapper.readTree(json);

    assertTrue(json.contains("\"uuid\""));
    assertTrue(json.contains("\"name\":\"cond-1\""));

    JsonNode csNode = root.get("crossSection");
    assertNotNull(csNode);
    assertEquals(10.0, csNode.asDouble());

    JsonNode dNode = root.get("diameter");
    assertNotNull(dNode);
    assertEquals(5.0, dNode.asDouble());

    ConductorInput parsed = new CableTypeParser(new ObjectMapper()).parseConductor(json);
    assertEquals(10.0, parsed.crossSection().getValue().doubleValue());
    assertEquals(PowerSystemUnits.SQUARE_MILLIMETRE, parsed.crossSection().getUnit());
    assertEquals(5.0, parsed.diameter().getValue().doubleValue());
    assertEquals(PowerSystemUnits.MILLIMETRE, parsed.diameter().getUnit());
  }

  @Test
  void conductorSerializationUsesParserUnitsAndRoundTrips() throws Exception {
    CableTypeJsonCodec codec = new CableTypeJsonCodec();
    ObjectMapper mapper = new ObjectMapper();

    ConductorInput conductor =
        new ConductorInput(
            UUID.fromString("00000000-0000-0000-0000-000000000000"),
            "cond-1",
            CableMaterial.COPPER,
            Quantities.getQuantity(0.00001, Units.SQUARE_METRE),
            Quantities.getQuantity(0.005, Units.METRE),
            false,
            Quantities.getQuantity(1.0, PowerSystemUnits.KELVIN_METRE_PER_WATT),
            Quantities.getQuantity(2.0, PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN),
            null);

    String json = codec.writeConductor(conductor);

    JsonNode root = mapper.readTree(json);

    assertTrue(json.contains("\"uuid\""));
    assertTrue(json.contains("\"name\":\"cond-1\""));

    JsonNode csNode = root.get("crossSection");
    assertNotNull(csNode);
    assertEquals(10.0, csNode.asDouble());

    JsonNode dNode = root.get("diameter");
    assertNotNull(dNode);
    assertEquals(5.0, dNode.asDouble());

    ConductorInput parsed = new CableTypeParser(new ObjectMapper()).parseConductor(json);
    assertEquals(10.0, parsed.crossSection().getValue().doubleValue());
    assertEquals(PowerSystemUnits.SQUARE_MILLIMETRE, parsed.crossSection().getUnit());
    assertEquals(5.0, parsed.diameter().getValue().doubleValue());
    assertEquals(PowerSystemUnits.MILLIMETRE, parsed.diameter().getUnit());
  }

  @Test
  void layerSerializationProducesAnArrayInParserUnits() throws Exception {
    CableTypeJsonCodec codec = new CableTypeJsonCodec();
    ObjectMapper mapper = new ObjectMapper();

    LayerInput layer =
        new LayerInput(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "Main insulation",
            CableMaterial.XLPE,
            Quantities.getQuantity(0.0225, Units.METRE),
            Quantities.getQuantity(0.027, Units.METRE),
            Quantities.getQuantity(3.5, PowerSystemUnits.KELVIN_METRE_PER_WATT),
            Quantities.getQuantity(2.4, PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN),
            Quantities.getQuantity(1.0, Units.SQUARE_METRE));

    JsonNode serializedLayer = mapper.readTree(codec.writeLayers(java.util.List.of(layer))).get(0);

    assertEquals("Main insulation", serializedLayer.get("name").asString());
    assertEquals(22.5, serializedLayer.get("innerDiameter").asDouble());
    assertEquals(27.0, serializedLayer.get("outerDiameter").asDouble());
    assertEquals(1_000_000.0, serializedLayer.get("area").asDouble());
    assertEquals("[]", codec.writeLayers(java.util.List.of()));
  }

  @Test
  void screenLayerSerializationHandlesNull() throws Exception {
    assertEquals("null", new CableTypeJsonCodec().writeScreenLayer(null));
  }
}
