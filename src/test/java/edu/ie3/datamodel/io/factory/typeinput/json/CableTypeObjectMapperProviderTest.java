/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput.json;

import static org.junit.jupiter.api.Assertions.*;

import edu.ie3.datamodel.io.factory.typeinput.CableTypeObjectMapperProvider;
import edu.ie3.datamodel.io.factory.typeinput.parser.CableTypeParser;
import edu.ie3.datamodel.models.input.connector.type.CableMaterial;
import edu.ie3.datamodel.models.input.connector.type.ConductorInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.UUID;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import org.junit.jupiter.api.Test;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

class CableTypeObjectMapperProviderTest {

  public static class Holder {
    public tech.units.indriya.ComparableQuantity<Area> crossSection;
    public tech.units.indriya.ComparableQuantity<Length> diameter;
    public tech.units.indriya.ComparableQuantity<Length> lengthOfLay;
  }

  @Test
  void conductorSerializationProducesCompactStrings() throws Exception {
    ObjectMapper mapper = CableTypeObjectMapperProvider.createObjectMapper();

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

    String json = mapper.writeValueAsString(conductor);

    JsonNode root = mapper.readTree(json);

    assertTrue(json.contains("\"uuid\""));
    assertTrue(json.contains("\"name\":\"cond-1\""));

    JsonNode csNode = root.get("crossSection");
    assertNotNull(csNode);
    assertEquals(10.0, csNode.asDouble());

    JsonNode dNode = root.get("diameter");
    assertNotNull(dNode);
    assertEquals(5.0, dNode.asDouble());

    ConductorInput parsed = new CableTypeParser(mapper).parseConductor(json);
    assertEquals(10.0, parsed.crossSection().getValue().doubleValue());
    assertEquals(PowerSystemUnits.SQUARE_MILLIMETRE, parsed.crossSection().getUnit());
    assertEquals(5.0, parsed.diameter().getValue().doubleValue());
    assertEquals(PowerSystemUnits.MILLIMETRE, parsed.diameter().getUnit());
  }

  @Test
  void conductorSerializationUsesParserUnitsAndRoundTrips() throws Exception {
    ObjectMapper mapper = CableTypeObjectMapperProvider.createObjectMapper();

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

    String json = mapper.writeValueAsString(conductor);

    JsonNode root = mapper.readTree(json);

    assertTrue(json.contains("\"uuid\""));
    assertTrue(json.contains("\"name\":\"cond-1\""));

    JsonNode csNode = root.get("crossSection");
    assertNotNull(csNode);
    assertEquals(10.0, csNode.asDouble());

    JsonNode dNode = root.get("diameter");
    assertNotNull(dNode);
    assertEquals(5.0, dNode.asDouble());

    ConductorInput parsed = new CableTypeParser(mapper).parseConductor(json);
    assertEquals(10.0, parsed.crossSection().getValue().doubleValue());
    assertEquals(PowerSystemUnits.SQUARE_MILLIMETRE, parsed.crossSection().getUnit());
    assertEquals(5.0, parsed.diameter().getValue().doubleValue());
    assertEquals(PowerSystemUnits.MILLIMETRE, parsed.diameter().getUnit());
  }

  @Test
  void comparableQuantityDeserializerRespectsFieldNameUnitMapping() {
    ObjectMapper mapper = CableTypeObjectMapperProvider.createObjectMapper();

    String json = "{\"diameter\": \"5\", \"crossSection\": \"10\", \"lengthOfLay\": \"30\"}";

    Holder h = mapper.readValue(json, Holder.class);

    assertNotNull(h.diameter);
    assertNotNull(h.crossSection);
    assertNotNull(h.lengthOfLay);
    assertEquals(PowerSystemUnits.MILLIMETRE, h.diameter.getUnit());
    assertEquals(PowerSystemUnits.SQUARE_MILLIMETRE, h.crossSection.getUnit());
    assertEquals(5.0, h.diameter.getValue().doubleValue());
    assertEquals(10.0, h.crossSection.getValue().doubleValue());
    assertEquals(PowerSystemUnits.MILLIMETRE, h.lengthOfLay.getUnit());
    assertEquals(30.0, h.lengthOfLay.getValue().doubleValue());
  }
}
