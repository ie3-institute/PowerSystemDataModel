/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput.json;

import static edu.ie3.datamodel.io.naming.FieldNamingStrategy.*;

import edu.ie3.datamodel.models.input.connector.type.ConductorInput;
import edu.ie3.datamodel.models.input.connector.type.LayerInput;
import edu.ie3.datamodel.models.input.connector.type.ScreenLayerInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.measure.Unit;
import tech.units.indriya.ComparableQuantity;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.json.JsonMapper;

public final class CableTypeJsonCodec {

  private static final Set<String> DIAMETER_FIELDS =
      Set.of(DIAMETER, INNER_DIAMETER, OUTER_DIAMETER, WIRE_DIAMETER, LENGTH_OF_LAY);
  private static final Set<String> AREA_FIELDS = Set.of(AREA, CROSS_SECTION);
  private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

  public String writeConductor(ConductorInput conductor) throws JacksonException {
    return write(generator -> writeConductor(generator, conductor));
  }

  public String writeLayers(List<LayerInput> layers) throws JacksonException {
    return write(
        generator -> {
          generator.writeStartArray();
          for (LayerInput layer : layers) writeLayer(generator, layer);
          generator.writeEndArray();
        });
  }

  public String writeScreenLayer(ScreenLayerInput screenLayer) throws JacksonException {
    return write(generator -> writeScreenLayer(generator, screenLayer));
  }

  private String write(JsonWriter writer) throws JacksonException {
    StringWriter output = new StringWriter();
    try (JsonGenerator generator = JSON_MAPPER.createGenerator(output)) {
      writer.write(generator);
    }
    return output.toString();
  }

  private void writeConductor(JsonGenerator generator, ConductorInput conductor)
      throws JacksonException {
    if (conductor == null) {
      generator.writeNull();
      return;
    }
    generator.writeStartObject();
    writeCommonHeader(
        generator, conductor.getUuid().toString(), conductor.name(), conductor.material().name());
    writeQuantityField(generator, CROSS_SECTION, conductor.crossSection());
    writeQuantityField(generator, DIAMETER, conductor.diameter());
    generator.writeName("isCompacted");
    generator.writeBoolean(conductor.isCompacted());
    writeQuantityField(generator, THERMAL_RESISTIVITY, conductor.thermalResistivity());
    writeQuantityField(generator, THERMAL_CAPACITANCE, conductor.thermalCapacitance());
    writeQuantityField(generator, AREA, conductor.area().orElse(null));
    writeAdditionalInformation(generator, conductor.getAdditionalInformation());
    generator.writeEndObject();
  }

  private void writeLayer(JsonGenerator generator, LayerInput layer) throws JacksonException {
    if (layer == null) {
      generator.writeNull();
      return;
    }
    generator.writeStartObject();
    writeCommonHeader(generator, layer.getUuid().toString(), layer.name(), layer.material().name());
    writeQuantityField(generator, INNER_DIAMETER, layer.innerDiameter());
    writeQuantityField(generator, OUTER_DIAMETER, layer.outerDiameter());
    writeQuantityField(generator, THERMAL_RESISTIVITY, layer.thermalResistivity());
    writeQuantityField(generator, THERMAL_CAPACITANCE, layer.thermalCapacitance());
    writeQuantityField(generator, AREA, layer.area().orElse(null));
    writeAdditionalInformation(generator, layer.getAdditionalInformation());
    generator.writeEndObject();
  }

  private void writeScreenLayer(JsonGenerator generator, ScreenLayerInput screenLayer)
      throws JacksonException {
    if (screenLayer == null) {
      generator.writeNull();
      return;
    }
    generator.writeStartObject();
    writeCommonHeader(
        generator,
        screenLayer.getUuid().toString(),
        screenLayer.name(),
        screenLayer.material().name());
    writeQuantityField(generator, INNER_DIAMETER, screenLayer.innerDiameter());
    writeQuantityField(generator, OUTER_DIAMETER, screenLayer.outerDiameter());
    writeQuantityField(generator, THERMAL_RESISTIVITY, screenLayer.thermalResistivity());
    writeQuantityField(generator, THERMAL_CAPACITANCE, screenLayer.thermalCapacitance());
    writeQuantityField(generator, AREA, screenLayer.area().orElse(null));
    generator.writeName("wiresNumber");
    generator.writeNumber(screenLayer.wiresNumber());
    writeQuantityField(generator, WIRE_DIAMETER, screenLayer.wireDiameter());
    writeQuantityField(generator, LENGTH_OF_LAY, screenLayer.lengthOfLay().orElse(null));
    writeQuantityField(generator, ELECTRICAL_RESISTIVITY, screenLayer.electricalResistivity());
    writeAdditionalInformation(generator, screenLayer.getAdditionalInformation());
    generator.writeEndObject();
  }

  private void writeCommonHeader(JsonGenerator generator, String uuid, String name, String material)
      throws JacksonException {
    generator.writeName(UUID);
    generator.writeString(uuid);
    generator.writeName(NAME);
    generator.writeString(name);
    generator.writeName(MATERIAL);
    generator.writeString(material);
  }

  private void writeAdditionalInformation(JsonGenerator generator, Map<String, String> additional)
      throws JacksonException {
    generator.writeName(ADDITIONAL_INFORMATION);
    generator.writeStartObject();
    if (additional != null) {
      for (Map.Entry<String, String> entry : additional.entrySet()) {
        generator.writeName(entry.getKey());
        generator.writeString(entry.getValue());
      }
    }
    generator.writeEndObject();
  }

  private void writeQuantityField(
      JsonGenerator generator, String fieldName, ComparableQuantity<?> quantity)
      throws JacksonException {
    generator.writeName(fieldName);
    if (quantity == null) generator.writeNull();
    else generator.writeNumber(quantityAsBigDecimal(quantity, fieldName));
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  private BigDecimal quantityAsBigDecimal(ComparableQuantity<?> quantity, String fieldName) {
    Unit<?> unit = unitForField(fieldName);
    ComparableQuantity<?> quantityInFieldUnit =
        unit == null ? quantity : ((ComparableQuantity) quantity).to(unit);
    Object value = quantityInFieldUnit.getValue();
    if (value == null)
      throw new IllegalArgumentException("Cannot serialize quantity without numeric value");
    BigDecimal decimal = new BigDecimal(value.toString());
    return decimal.stripTrailingZeros();
  }

  private Unit<?> unitForField(String fieldName) {
    if (DIAMETER_FIELDS.contains(fieldName)) return PowerSystemUnits.MILLIMETRE;
    if (AREA_FIELDS.contains(fieldName)) return PowerSystemUnits.SQUARE_MILLIMETRE;
    return null;
  }

  @FunctionalInterface
  private interface JsonWriter {
    void write(JsonGenerator generator) throws JacksonException;
  }
}
