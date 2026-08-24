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
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public final class CableTypeObjectMapperProvider {

  private CableTypeObjectMapperProvider() {}

  public static ObjectMapper createObjectMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.registerModule(new Jdk8Module());
    objectMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);

    SimpleModule strictModule = new SimpleModule("StrictFieldUnitModule");

    strictModule.addDeserializer(
        (Class) ComparableQuantity.class,
        new JsonDeserializer() {
          @Override
          public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String currentField = p.currentName();
            javax.measure.Unit<?> unit;

            switch (currentField) {
              case "diameter",
                  "innerDiameter",
                  "inner_diameter",
                  "outerDiameter",
                  "outer_diameter",
                  "wireDiameter",
                  "wire_diameter" ->
                  unit = PowerSystemUnits.MILLIMETRE;

              case "electricalResistivity", "electrical_resistivity" ->
                  unit = PowerSystemUnits.OHM_METRE;

              case "thermalResistivity", "thermal_resistivity" ->
                  unit = PowerSystemUnits.KELVIN_METRE_PER_WATT;

              case "thermalCapacitance", "thermal_capacitance" ->
                  unit = PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN;

              case "area", "crossSection", "cross_section" ->
                  unit = PowerSystemUnits.SQUARE_MILLIMETRE;

              default ->
                  throw new IOException(
                      "Strict unit enforcement failed: Unknown target unit context for property field '"
                          + currentField
                          + "'");
            }

            JsonToken token = p.currentToken();
            if (token == JsonToken.VALUE_NUMBER_FLOAT || token == JsonToken.VALUE_NUMBER_INT) {
              BigDecimal bd = p.getDecimalValue();
              if (bd == null) return null;
              return Quantities.getQuantity(bd.doubleValue(), unit);
            }

            String text = p.getText();
            if (text == null) return null;
            text = text.trim();
            if (text.isEmpty() || "null".equalsIgnoreCase(text)) {
              return null;
            }

            double value = Double.parseDouble(text);
            return Quantities.getQuantity(value, unit);
          }
        });

    strictModule.addSerializer(
        (Class) ComparableQuantity.class,
        (JsonSerializer)
            new JsonSerializer<ComparableQuantity<?>>() {

              @Override
              public void serialize(
                  ComparableQuantity<?> value, JsonGenerator gen, SerializerProvider serializers)
                  throws IOException {
                if (value == null) {
                  gen.writeNull();
                } else {
                  double d = value.getValue().doubleValue();
                  String ds = Double.toString(d);
                  BigDecimal bd;
                  if (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0) {
                    bd = new BigDecimal(ds);
                  } else {
                    bd =
                        BigDecimal.valueOf(d)
                            .setScale(10, RoundingMode.HALF_UP)
                            .stripTrailingZeros();
                  }
                  gen.writeNumber(bd);
                }
              }
            });

    strictModule.addSerializer(
        ConductorInput.class,
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
            gen.writeStringField("uuid", value.getUuid().toString());
            gen.writeStringField("name", value.name());
            gen.writeStringField("material", value.material().name());

            if (value.crossSection() == null) gen.writeNullField("crossSection");
            else {
              double d = value.crossSection().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("crossSection", bd);
            }

            if (value.diameter() == null) gen.writeNullField("diameter");
            else {
              double d = value.diameter().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("diameter", bd);
            }

            gen.writeBooleanField("isCompacted", value.isCompacted());

            if (value.thermalResistivity() == null) gen.writeNullField("thermalResistivity");
            else {
              double d = value.thermalResistivity().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("thermalResistivity", bd);
            }

            if (value.thermalCapacitance() == null) gen.writeNullField("thermalCapacitance");
            else {
              double d = value.thermalCapacitance().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("thermalCapacitance", bd);
            }

            if (value.area().isEmpty()) gen.writeNullField("area");
            else {
              double d = value.area().get().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("area", bd);
            }

            gen.writeObjectFieldStart("additionalInformation");
            if (value.getAdditionalInformation() != null) {
              for (Map.Entry<String, String> e : value.getAdditionalInformation().entrySet()) {
                gen.writeStringField(e.getKey(), e.getValue());
              }
            }
            gen.writeEndObject();

            gen.writeEndObject();
          }
        });

    strictModule.addSerializer(
        LayerInput.class,
        new JsonSerializer<>() {
          @Override
          public void serialize(LayerInput value, JsonGenerator gen, SerializerProvider serializers)
              throws IOException {
            if (value == null) {
              gen.writeNull();
              return;
            }

            gen.writeStartObject();
            gen.writeStringField("uuid", value.getUuid().toString());
            gen.writeStringField("name", value.name());
            gen.writeStringField("material", value.material().name());

            if (value.innerDiameter() == null) gen.writeNullField("innerDiameter");
            else {
              double d = value.innerDiameter().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("innerDiameter", bd);
            }

            if (value.outerDiameter() == null) gen.writeNullField("outerDiameter");
            else {
              double d = value.outerDiameter().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("outerDiameter", bd);
            }

            if (value.thermalResistivity() == null) gen.writeNullField("thermalResistivity");
            else {
              double d = value.thermalResistivity().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("thermalResistivity", bd);
            }

            if (value.thermalCapacitance() == null) gen.writeNullField("thermalCapacitance");
            else {
              double d = value.thermalCapacitance().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("thermalCapacitance", bd);
            }

            if (value.area().isEmpty()) gen.writeNullField("area");
            else {
              double d = value.area().get().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("area", bd);
            }

            gen.writeObjectFieldStart("additionalInformation");
            if (value.getAdditionalInformation() != null) {
              for (Map.Entry<String, String> e : value.getAdditionalInformation().entrySet()) {
                gen.writeStringField(e.getKey(), e.getValue());
              }
            }
            gen.writeEndObject();

            gen.writeEndObject();
          }
        });

    strictModule.addSerializer(
        ScreenLayerInput.class,
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
            gen.writeStringField("uuid", value.getUuid().toString());
            gen.writeStringField("name", value.name());
            gen.writeStringField("material", value.material().name());

            if (value.innerDiameter() == null) gen.writeNullField("innerDiameter");
            else {
              double d = value.innerDiameter().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("innerDiameter", bd);
            }

            if (value.outerDiameter() == null) gen.writeNullField("outerDiameter");
            else {
              double d = value.outerDiameter().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("outerDiameter", bd);
            }

            if (value.thermalResistivity() == null) gen.writeNullField("thermalResistivity");
            else {
              double d = value.thermalResistivity().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("thermalResistivity", bd);
            }

            if (value.thermalCapacitance() == null) gen.writeNullField("thermalCapacitance");
            else {
              double d = value.thermalCapacitance().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("thermalCapacitance", bd);
            }

            if (value.area().isEmpty()) gen.writeNullField("area");
            else {
              double d = value.area().get().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("area", bd);
            }

            gen.writeNumberField("wiresNumber", value.wiresNumber());

            if (value.wireDiameter() == null) gen.writeNullField("wireDiameter");
            else {
              double d = value.wireDiameter().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("wireDiameter", bd);
            }

            if (value.lengthOfLay().isPresent()) {
              double d = value.lengthOfLay().get().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("lengthOfLay", bd);
            } else gen.writeNullField("lengthOfLay");

            if (value.electricalResistivity() == null) gen.writeNullField("electricalResistivity");
            else {
              double d = value.electricalResistivity().getValue().doubleValue();
              String ds = Double.toString(d);
              BigDecimal bd =
                  (ds.indexOf('E') >= 0 || ds.indexOf('e') >= 0)
                      ? new BigDecimal(ds)
                      : BigDecimal.valueOf(d)
                          .setScale(10, RoundingMode.HALF_UP)
                          .stripTrailingZeros();
              gen.writeNumberField("electricalResistivity", bd);
            }

            gen.writeObjectFieldStart("additionalInformation");
            if (value.getAdditionalInformation() != null) {
              for (Map.Entry<String, String> e : value.getAdditionalInformation().entrySet()) {
                gen.writeStringField(e.getKey(), e.getValue());
              }
            }
            gen.writeEndObject();

            gen.writeEndObject();
          }
        });

    objectMapper.registerModule(strictModule);
    return objectMapper;
  }

  public static final ObjectMapper OBJECT_MAPPER = createObjectMapper();
}
