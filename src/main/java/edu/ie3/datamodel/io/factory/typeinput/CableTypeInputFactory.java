/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import static edu.ie3.util.quantities.PowerSystemUnits.*;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.connector.type.CableTypeInput;
import edu.ie3.datamodel.models.input.connector.type.ConductorInput;
import edu.ie3.datamodel.models.input.connector.type.LayerInput;
import edu.ie3.datamodel.models.input.connector.type.ScreenLayerInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.io.IOException;
import java.util.*;
import javax.measure.Unit;
import javax.measure.quantity.ElectricCapacitance;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Temperature;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public class CableTypeInputFactory extends AssetTypeInputEntityFactory<CableTypeInput> {

  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  static {
    OBJECT_MAPPER.registerModule(new Jdk8Module());
    SimpleModule strictModule = new SimpleModule("StrictFieldUnitModule");

    strictModule.addDeserializer(
        ComparableQuantity.class,
        new JsonDeserializer<>() {
          @Override
          public ComparableQuantity deserialize(JsonParser p, DeserializationContext ctxt)
              throws IOException {
            String text = p.getText().trim();
            if (text.isEmpty() || "null".equalsIgnoreCase(text)) {
              return null;
            }

            String currentField = p.currentName();
            double value = Double.parseDouble(text);
            Unit<?> unit;

            switch (currentField) {
              case "diameter",
                  "innerDiameter",
                  "inner_diameter",
                  "outerDiameter",
                  "outer_diameter",
                  "wireDiameter",
                  "wire_diameter" ->
                  unit = PowerSystemUnits.MILLIMETRE;

              case "electricalResistivity", "electrical_resistivity" -> unit = OHM_METRE;

              case "thermalResistivity", "thermal_resistivity" -> unit = KELVIN_METRE_PER_WATT;

              case "thermalCapacitance", "thermal_capacitance" ->
                  unit = JOULE_PER_CUBIC_METRE_KELVIN;

              case "area", "crossSection", "cross_section" -> unit = SQUARE_MILLIMETRE;

              default ->
                  throw new IOException(
                      "Strict unit enforcement failed: Unknown target unit context for property field '"
                          + currentField
                          + "'");
            }

            return Quantities.getQuantity(value, unit);
          }
        });

    strictModule.addSerializer(
        ComparableQuantity.class,
        new JsonSerializer<>() {
          @Override
          public void serialize(
              ComparableQuantity value, JsonGenerator gen, SerializerProvider serializers)
              throws IOException {
            if (value == null) {
              gen.writeNull();
            } else {
              gen.writeString(Double.toString(value.getValue().doubleValue()));
            }
          }
        });
    OBJECT_MAPPER.registerModule(strictModule);
  }

  private List<LayerInput> parseLayerList(String json) throws ParsingException {
    if (json == null || json.isBlank()) {
      return Collections.emptyList();
    }

    try {
      JsonNode node = OBJECT_MAPPER.readTree(json);

      if (node.isArray()) {
        for (JsonNode element : node) {
          if (element.isObject() && (!element.has("uuid") || element.get("uuid").isNull())) {
            ((ObjectNode) element).put("uuid", java.util.UUID.randomUUID().toString());
          }
        }
      }

      return OBJECT_MAPPER.readValue(
          OBJECT_MAPPER.treeAsTokens(node), new TypeReference<List<LayerInput>>() {});
    } catch (IOException e) {
      throw new ParsingException("Cannot parse LayerInput list: " + json, e);
    }
  }

  private ScreenLayerInput parseScreenLayer(String json) throws ParsingException {
    if (json == null || json.isBlank()) {
      return null;
    }

    try {
      JsonNode node = OBJECT_MAPPER.readTree(json);

      if (node.isObject() && (!node.has("uuid") || node.get("uuid").isNull())) {
        ((ObjectNode) node).put("uuid", java.util.UUID.randomUUID().toString());
      }

      return OBJECT_MAPPER.treeToValue(node, ScreenLayerInput.class);
    } catch (IOException e) {
      throw new ParsingException("Cannot parse ScreenLayerInput: " + json, e);
    }
  }

  private ConductorInput parseConductor(String json) throws ParsingException {
    if (json == null || json.isBlank()) {
      return null;
    }

    try {
      JsonNode node = OBJECT_MAPPER.readTree(json);

      if (node.isObject() && (!node.has("uuid") || node.get("uuid").isNull())) {
        ((ObjectNode) node).put("uuid", java.util.UUID.randomUUID().toString());
      }

      return OBJECT_MAPPER.treeToValue(node, ConductorInput.class);
    } catch (IOException e) {
      throw new ParsingException("Cannot parse ConductorInput: " + json, e);
    }
  }

  public CableTypeInputFactory() {
    super(CableTypeInput.class);
  }

  @Override
  protected CableTypeInput buildModel(EntityData data) {
    UUID uuid = data.getUUID(UUID);
    String id = data.getField(ID);
    int cores = data.getInt(CORE_NUMBER);

    final ConductorInput conductor;
    final List<LayerInput> isolation;
    final Optional<ScreenLayerInput> screen;
    final List<LayerInput> filler;
    final List<LayerInput> armor;
    final List<LayerInput> jack;

    try {
      conductor = parseConductor(data.getField(CONDUCTOR_STRING));
      isolation = parseLayerList(data.getField(ISOLATION_STRING));
      screen = Optional.ofNullable(parseScreenLayer(data.getField(SCREEN_STRING)));
      filler = parseLayerList(data.getField(FILLER_STRING));
      armor = parseLayerList(data.getField(ARMOR_STRING));
      jack = parseLayerList(data.getField(JACK_STRING));
    } catch (ParsingException e) {
      throw new IllegalArgumentException(
          "Cannot build CableTypeInput '" + id + "': invalid cable component JSON.", e);
    }

    ComparableQuantity<Temperature> limitTemp =
        data.getQuantity(LIMIT_TEMP, StandardUnits.TEMPERATURE);
    ComparableQuantity<Frequency> frequency = data.getQuantity(FREQUENCY, PowerSystemUnits.HERTZ);
    double skinEffectCoefficient = data.getDouble(SKIN_EFF_COEFF);
    double proxEffectCoefficient = data.getDouble(PROX_EFF_COEFF);
    ComparableQuantity<ElectricCapacitance> electricalCapacitance =
        data.getQuantity(ELECTR_CAPACITANCE, PowerSystemUnits.FARAD);
    double tanDelta = data.getDouble(TAN_DELTA);
    double circulatingLossFactor = data.getDouble(CIRCULATING_LOSS_FACTOR);
    double eddyCurrentLossFactor = data.getDouble(EDDY_CURRENT_LOSS_FACTOR);

    return new CableTypeInput(
        uuid,
        id,
        cores,
        conductor,
        isolation,
        screen,
        filler,
        armor,
        jack,
        limitTemp,
        frequency,
        skinEffectCoefficient,
        proxEffectCoefficient,
        electricalCapacitance,
        tanDelta,
        circulatingLossFactor,
        eddyCurrentLossFactor);
  }
}
