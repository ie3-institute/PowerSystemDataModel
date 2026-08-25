/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import edu.ie3.datamodel.models.input.connector.type.ConductorInput;
import edu.ie3.datamodel.models.input.connector.type.LayerInput;
import edu.ie3.datamodel.models.input.connector.type.ScreenLayerInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public final class CableTypeObjectMapperProvider {

  private CableTypeObjectMapperProvider() {}

  private static final String FIELD_UUID = "uuid";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_MATERIAL = "material";

  private static final String FIELD_DIAMETER = "diameter";
  private static final String FIELD_INNER_DIAMETER = "innerDiameter";
  private static final String FIELD_INNER_DIAMETER_ALT = "inner_diameter";
  private static final String FIELD_OUTER_DIAMETER = "outerDiameter";
  private static final String FIELD_OUTER_DIAMETER_ALT = "outer_diameter";
  private static final String FIELD_WIRE_DIAMETER = "wireDiameter";
  private static final String FIELD_WIRE_DIAMETER_ALT = "wire_diameter";

  private static final String FIELD_ELECTRICAL_RESISTIVITY = "electricalResistivity";
  private static final String FIELD_ELECTRICAL_RESISTIVITY_ALT = "electrical_resistivity";

  private static final String FIELD_THERMAL_RESISTIVITY = "thermalResistivity";
  private static final String FIELD_THERMAL_RESISTIVITY_ALT = "thermal_resistivity";

  private static final String FIELD_THERMAL_CAPACITANCE = "thermalCapacitance";
  private static final String FIELD_THERMAL_CAPACITANCE_ALT = "thermal_capacitance";

  private static final String FIELD_AREA = "area";
  private static final String FIELD_CROSS_SECTION = "crossSection";
  private static final String FIELD_CROSS_SECTION_ALT = "cross_section";

  private static final String FIELD_ADDITIONAL_INFORMATION = "additionalInformation";

  private static final Set<String> DIAMETER_FIELDS =
      Set.of(
          FIELD_DIAMETER,
          FIELD_INNER_DIAMETER,
          FIELD_INNER_DIAMETER_ALT,
          FIELD_OUTER_DIAMETER,
          FIELD_OUTER_DIAMETER_ALT,
          FIELD_WIRE_DIAMETER,
          FIELD_WIRE_DIAMETER_ALT);

  private static final Set<String> ELECTRICAL_RESISTIVITY_FIELDS =
      Set.of(FIELD_ELECTRICAL_RESISTIVITY, FIELD_ELECTRICAL_RESISTIVITY_ALT);

  private static final Set<String> THERMAL_RESISTIVITY_FIELDS =
      Set.of(FIELD_THERMAL_RESISTIVITY, FIELD_THERMAL_RESISTIVITY_ALT);

  private static final Set<String> THERMAL_CAPACITANCE_FIELDS =
      Set.of(FIELD_THERMAL_CAPACITANCE, FIELD_THERMAL_CAPACITANCE_ALT);

  private static final Set<String> AREA_FIELDS =
      Set.of(FIELD_AREA, FIELD_CROSS_SECTION, FIELD_CROSS_SECTION_ALT);

  // Helper to map field names to units
  private static javax.measure.Unit<?> unitForField(String fieldName) {
    if (fieldName == null) return null;
    if (DIAMETER_FIELDS.contains(fieldName)) return PowerSystemUnits.MILLIMETRE;
    if (ELECTRICAL_RESISTIVITY_FIELDS.contains(fieldName)) return PowerSystemUnits.OHM_METRE;
    if (THERMAL_RESISTIVITY_FIELDS.contains(fieldName))
      return PowerSystemUnits.KELVIN_METRE_PER_WATT;
    if (THERMAL_CAPACITANCE_FIELDS.contains(fieldName))
      return PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN;
    if (AREA_FIELDS.contains(fieldName)) return PowerSystemUnits.SQUARE_MILLIMETRE;
    return null;
  }

  // Helper methods
  private static BigDecimal toBigDecimalFromQuantity(ComparableQuantity<?> quantity) {
    double d = quantity.getValue().doubleValue();
    String ds = Double.toString(d);
    if (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0) {
      return new BigDecimal(ds);
    } else {
      return BigDecimal.valueOf(d).setScale(10, RoundingMode.HALF_UP).stripTrailingZeros();
    }
  }

  private static void writeQuantityNumberField(
      JsonGenerator gen, String fieldName, ComparableQuantity<?> value) throws IOException {
    if (value == null) gen.writeNullField(fieldName);
    else gen.writeNumberField(fieldName, toBigDecimalFromQuantity(value));
  }

  private static void writeOptionalQuantityNumberField(
      JsonGenerator gen, String fieldName, Optional<? extends ComparableQuantity<?>> opt)
      throws IOException {
    if (opt.isEmpty()) gen.writeNullField(fieldName);
    else gen.writeNumberField(fieldName, toBigDecimalFromQuantity(opt.get()));
  }

  public static ObjectMapper createObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new Jdk8Module());
    objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

    SimpleModule strictModule = new SimpleModule("StrictFieldUnitModule");

    JsonDeserializer<ComparableQuantity<?>> quantityDeserializer =
        new JsonDeserializer<>() {
          @Override
          public ComparableQuantity<?> deserialize(JsonParser p, DeserializationContext ctxt)
              throws IOException {
            String currentField = p.currentName();
            javax.measure.Unit<?> unit = unitForField(currentField);

            if (unit == null) {
              throw new IOException(
                  "Strict unit enforcement failed: Unknown target unit context for property field '"
                      + currentField
                      + "'");
            }

            JsonToken token = p.currentToken();
            if (token != null && token.isNumeric()) {
              BigDecimal bd = p.getDecimalValue();
              if (bd == null) return null;
              return Quantities.getQuantity(bd.doubleValue(), unit);
            }

            String text = p.getText();
            boolean isNullish = text == null;
            if (!isNullish) {
              text = text.trim();
              isNullish = text.isEmpty() || "null".equalsIgnoreCase(text);
            }
            if (isNullish) return null;

            double value = Double.parseDouble(text);
            return Quantities.getQuantity(value, unit);
          }
        };

    JsonSerializer<ComparableQuantity<?>> quantitySerializer =
        new JsonSerializer<>() {
          @Override
          public void serialize(
              ComparableQuantity<?> value, JsonGenerator gen, SerializerProvider serializers)
              throws IOException {
            if (value == null) {
              gen.writeNull();
            } else {
              gen.writeNumber(toBigDecimalFromQuantity(value));
            }
          }
        };

    strictModule.addDeserializer(
        (Class<ComparableQuantity<?>>) (Class<?>) ComparableQuantity.class, quantityDeserializer);

    strictModule.addSerializer(
        (Class<ComparableQuantity<?>>) (Class<?>) ComparableQuantity.class, quantitySerializer);

    JsonSerializer<ConductorInput> conductorSerializer =
        new JsonSerializer<>() {
          @Override
          public void serialize(
              ConductorInput value, JsonGenerator gen, SerializerProvider serializers)
              throws IOException {
            if (value == null) {
              gen.writeNull();
              return;
            }

            gen.writeStartObject();
            gen.writeStringField(FIELD_UUID, value.getUuid().toString());
            gen.writeStringField(FIELD_NAME, value.name());
            gen.writeStringField(FIELD_MATERIAL, value.material().name());
            writeQuantityNumberField(gen, FIELD_CROSS_SECTION, value.crossSection());
            writeQuantityNumberField(gen, FIELD_DIAMETER, value.diameter());
            gen.writeBooleanField("isCompacted", value.isCompacted());
            writeQuantityNumberField(gen, FIELD_THERMAL_RESISTIVITY, value.thermalResistivity());
            writeQuantityNumberField(gen, FIELD_THERMAL_CAPACITANCE, value.thermalCapacitance());
            writeOptionalQuantityNumberField(gen, FIELD_AREA, value.area());

            gen.writeObjectFieldStart(FIELD_ADDITIONAL_INFORMATION);
            if (value.getAdditionalInformation() != null) {
              for (Map.Entry<String, String> e : value.getAdditionalInformation().entrySet()) {
                gen.writeStringField(e.getKey(), e.getValue());
              }
            }
            gen.writeEndObject();

            gen.writeEndObject();
          }
        };

    strictModule.addSerializer(ConductorInput.class, conductorSerializer);

    JsonSerializer<LayerInput> layerSerializer =
        new JsonSerializer<>() {
          @Override
          public void serialize(LayerInput value, JsonGenerator gen, SerializerProvider serializers)
              throws IOException {
            if (value == null) {
              gen.writeNull();
              return;
            }

            gen.writeStartObject();
            gen.writeStringField(FIELD_UUID, value.getUuid().toString());
            gen.writeStringField(FIELD_NAME, value.name());
            gen.writeStringField(FIELD_MATERIAL, value.material().name());
            writeQuantityNumberField(gen, FIELD_INNER_DIAMETER, value.innerDiameter());
            writeQuantityNumberField(gen, FIELD_OUTER_DIAMETER, value.outerDiameter());
            writeQuantityNumberField(gen, FIELD_THERMAL_RESISTIVITY, value.thermalResistivity());
            writeQuantityNumberField(gen, FIELD_THERMAL_CAPACITANCE, value.thermalCapacitance());
            writeOptionalQuantityNumberField(gen, FIELD_AREA, value.area());

            gen.writeObjectFieldStart(FIELD_ADDITIONAL_INFORMATION);
            if (value.getAdditionalInformation() != null) {
              for (Map.Entry<String, String> e : value.getAdditionalInformation().entrySet()) {
                gen.writeStringField(e.getKey(), e.getValue());
              }
            }
            gen.writeEndObject();

            gen.writeEndObject();
          }
        };

    strictModule.addSerializer(LayerInput.class, layerSerializer);

    JsonSerializer<ScreenLayerInput> screenLayerSerializer =
        new JsonSerializer<>() {
          @Override
          public void serialize(
              ScreenLayerInput value, JsonGenerator gen, SerializerProvider serializers)
              throws IOException {
            if (value == null) {
              gen.writeNull();
              return;
            }

            gen.writeStartObject();
            gen.writeStringField(FIELD_UUID, value.getUuid().toString());
            gen.writeStringField(FIELD_NAME, value.name());
            gen.writeStringField(FIELD_MATERIAL, value.material().name());
            writeQuantityNumberField(gen, FIELD_INNER_DIAMETER, value.innerDiameter());
            writeQuantityNumberField(gen, FIELD_OUTER_DIAMETER, value.outerDiameter());
            writeQuantityNumberField(gen, FIELD_THERMAL_RESISTIVITY, value.thermalResistivity());
            writeQuantityNumberField(gen, FIELD_THERMAL_CAPACITANCE, value.thermalCapacitance());
            writeOptionalQuantityNumberField(gen, FIELD_AREA, value.area());

            gen.writeNumberField("wiresNumber", value.wiresNumber());
            writeQuantityNumberField(gen, FIELD_WIRE_DIAMETER, value.wireDiameter());
            writeOptionalQuantityNumberField(gen, "lengthOfLay", value.lengthOfLay());
            writeQuantityNumberField(
                gen, FIELD_ELECTRICAL_RESISTIVITY, value.electricalResistivity());

            gen.writeObjectFieldStart(FIELD_ADDITIONAL_INFORMATION);
            if (value.getAdditionalInformation() != null) {
              for (Map.Entry<String, String> e : value.getAdditionalInformation().entrySet()) {
                gen.writeStringField(e.getKey(), e.getValue());
              }
            }
            gen.writeEndObject();

            gen.writeEndObject();
          }
        };

    strictModule.addSerializer(ScreenLayerInput.class, screenLayerSerializer);

    objectMapper.registerModule(strictModule);
    return objectMapper;
  }

  public static final ObjectMapper OBJECT_MAPPER = createObjectMapper();
}
