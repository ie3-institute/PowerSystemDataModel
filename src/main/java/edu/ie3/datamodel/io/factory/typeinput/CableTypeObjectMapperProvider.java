/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput;

import edu.ie3.datamodel.models.input.connector.type.ConductorInput;
import edu.ie3.datamodel.models.input.connector.type.LayerInput;
import edu.ie3.datamodel.models.input.connector.type.ScreenLayerInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;

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
  private static final String FIELD_LENGTH_OF_LAY = "lengthOfLay";

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
          FIELD_WIRE_DIAMETER_ALT,
          FIELD_LENGTH_OF_LAY);

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
  @SuppressWarnings({"rawtypes", "unchecked"})
  private static BigDecimal toBigDecimalFromQuantity(
      ComparableQuantity<?> quantity, String fieldName) {
    if (quantity == null) return null;
    javax.measure.Unit<?> unit = unitForField(fieldName);
    // Localized raw cast: required because ComparableQuantity.to(Unit) is generic and
    // cannot be expressed with an unknown quantity type at compile time. The cast is
    // intentionally narrowly scoped and suppressed above.
    ComparableQuantity<?> quantityInFieldUnit =
        unit == null ? quantity : ((ComparableQuantity) quantity).to(unit);

    Number n = (Number) quantityInFieldUnit.getValue();
    if (n == null) throw new RuntimeException("Cannot serialize quantity without numeric value");

    String ns = n.toString();
    // Preserve exponential representation if present
    if (ns.indexOf('E') >= 0 || ns.indexOf('e') >= 0) {
      return new BigDecimal(ns);
    }

    BigDecimal bd;
    if (n instanceof BigDecimal) {
      bd = (BigDecimal) n;
    } else if (n instanceof Long
        || n instanceof Integer
        || n instanceof Short
        || n instanceof Byte) {
      bd = BigDecimal.valueOf(n.longValue());
    } else if (n instanceof Double || n instanceof Float) {
      // Construct from string to avoid binary double artifacts
      bd = new BigDecimal(ns);
    } else {
      bd = new BigDecimal(ns);
    }

    return bd.setScale(10, RoundingMode.HALF_UP).stripTrailingZeros();
  }

  private static void writeCommonHeader(
      JsonGenerator gen, String uuid, String name, String material) {
    gen.writeName(FIELD_UUID);
    gen.writeString(uuid);
    gen.writeName(FIELD_NAME);
    gen.writeString(name);
    gen.writeName(FIELD_MATERIAL);
    gen.writeString(material);
  }

  private static void writeAdditionalInformationObject(
      JsonGenerator gen, Map<String, String> additional) {
    gen.writeName(FIELD_ADDITIONAL_INFORMATION);
    gen.writeStartObject();
    if (additional != null) {
      for (Map.Entry<String, String> e : additional.entrySet()) {
        gen.writeName(e.getKey());
        gen.writeString(e.getValue());
      }
    }
    gen.writeEndObject();
  }

  private static void writeQuantityNumberField(
      JsonGenerator gen, String fieldName, ComparableQuantity<?> value) {
    if (value == null) {
      gen.writeName(fieldName);
      gen.writeNull();
    } else {
      gen.writeName(fieldName);
      gen.writeNumber(toBigDecimalFromQuantity(value, fieldName));
    }
  }

  @SuppressWarnings("unchecked")
  public static ObjectMapper createObjectMapper() {
    JsonMapper objectMapper =
        JsonMapper.builder().enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS).build();

    SimpleModule strictModule = new SimpleModule("StrictFieldUnitModule");

    ValueDeserializer<ComparableQuantity<?>> quantityDeserializer =
        new ValueDeserializer<>() {
          @Override
          public ComparableQuantity<?> deserialize(JsonParser p, DeserializationContext ctxt) {
            String currentField = p.currentName();
            javax.measure.Unit<?> unit = unitForField(currentField);

            if (unit == null) {
              throw new RuntimeException(
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

            String text = p.getString();
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

    ValueSerializer<ComparableQuantity<?>> quantitySerializer =
        new ValueSerializer<>() {
          @Override
          public void serialize(
              ComparableQuantity<?> value, JsonGenerator gen, SerializationContext serializers) {
            try {
              if (value == null) {
                gen.writeNull();
              } else {
                gen.writeNumber(toBigDecimalFromQuantity(value, null));
              }
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        };

    @SuppressWarnings("unchecked")
    Class<ComparableQuantity<?>> comparableQuantityClass =
        (Class<ComparableQuantity<?>>) (Class<?>) ComparableQuantity.class;

    strictModule.addDeserializer(comparableQuantityClass, quantityDeserializer);
    strictModule.addSerializer(comparableQuantityClass, quantitySerializer);

    ValueSerializer<ConductorInput> conductorSerializer =
        new ValueSerializer<>() {
          @Override
          public void serialize(
              ConductorInput value, JsonGenerator gen, SerializationContext serializers) {
            try {
              if (value == null) {
                gen.writeNull();
                return;
              }

              gen.writeStartObject();
              writeCommonHeader(
                  gen, value.getUuid().toString(), value.name(), value.material().name());
              writeQuantityNumberField(gen, FIELD_CROSS_SECTION, value.crossSection());
              writeQuantityNumberField(gen, FIELD_DIAMETER, value.diameter());
              gen.writeName("isCompacted");
              gen.writeBoolean(value.isCompacted());
              writeQuantityNumberField(gen, FIELD_THERMAL_RESISTIVITY, value.thermalResistivity());
              writeQuantityNumberField(gen, FIELD_THERMAL_CAPACITANCE, value.thermalCapacitance());
              writeQuantityNumberField(gen, FIELD_AREA, value.area().orElse(null));

              writeAdditionalInformationObject(gen, value.getAdditionalInformation());

              gen.writeEndObject();
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        };

    strictModule.addSerializer(ConductorInput.class, conductorSerializer);

    ValueSerializer<LayerInput> layerSerializer =
        new ValueSerializer<>() {
          @Override
          public void serialize(
              LayerInput value, JsonGenerator gen, SerializationContext serializers) {
            try {
              if (value == null) {
                gen.writeNull();
                return;
              }

              gen.writeStartObject();
              writeCommonHeader(
                  gen, value.getUuid().toString(), value.name(), value.material().name());
              writeQuantityNumberField(gen, FIELD_INNER_DIAMETER, value.innerDiameter());
              writeQuantityNumberField(gen, FIELD_OUTER_DIAMETER, value.outerDiameter());
              writeQuantityNumberField(gen, FIELD_THERMAL_RESISTIVITY, value.thermalResistivity());
              writeQuantityNumberField(gen, FIELD_THERMAL_CAPACITANCE, value.thermalCapacitance());
              writeQuantityNumberField(gen, FIELD_AREA, value.area().orElse(null));

              writeAdditionalInformationObject(gen, value.getAdditionalInformation());

              gen.writeEndObject();
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        };

    strictModule.addSerializer(LayerInput.class, layerSerializer);

    ValueSerializer<ScreenLayerInput> screenLayerSerializer =
        new ValueSerializer<>() {
          @Override
          public void serialize(
              ScreenLayerInput value, JsonGenerator gen, SerializationContext serializers) {
            try {
              if (value == null) {
                gen.writeNull();
                return;
              }

              gen.writeStartObject();
              writeCommonHeader(
                  gen, value.getUuid().toString(), value.name(), value.material().name());
              writeQuantityNumberField(gen, FIELD_INNER_DIAMETER, value.innerDiameter());
              writeQuantityNumberField(gen, FIELD_OUTER_DIAMETER, value.outerDiameter());
              writeQuantityNumberField(gen, FIELD_THERMAL_RESISTIVITY, value.thermalResistivity());
              writeQuantityNumberField(gen, FIELD_THERMAL_CAPACITANCE, value.thermalCapacitance());
              writeQuantityNumberField(gen, FIELD_AREA, value.area().orElse(null));

              gen.writeName("wiresNumber");
              gen.writeNumber(value.wiresNumber());
              writeQuantityNumberField(gen, FIELD_WIRE_DIAMETER, value.wireDiameter());
              writeQuantityNumberField(gen, FIELD_LENGTH_OF_LAY, value.lengthOfLay().orElse(null));
              writeQuantityNumberField(
                  gen, FIELD_ELECTRICAL_RESISTIVITY, value.electricalResistivity());

              writeAdditionalInformationObject(gen, value.getAdditionalInformation());

              gen.writeEndObject();
            } catch (Exception e) {
              throw new RuntimeException(e);
            }
          }
        };

    strictModule.addSerializer(ScreenLayerInput.class, screenLayerSerializer);

    objectMapper = objectMapper.rebuild().addModule(strictModule).build();

    return objectMapper;
  }

  public static final ObjectMapper OBJECT_MAPPER = createObjectMapper();
}
