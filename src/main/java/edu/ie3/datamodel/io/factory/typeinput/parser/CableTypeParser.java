/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput.parser;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.input.connector.type.CableMaterial;
import edu.ie3.datamodel.models.input.connector.type.ConductorInput;
import edu.ie3.datamodel.models.input.connector.type.LayerInput;
import edu.ie3.datamodel.models.input.connector.type.ScreenLayerInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.io.IOException;
import java.util.*;
import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

public class CableTypeParser {
  private final ObjectMapper mapper;

  // common field name constants
  private static final String FIELD_UUID = "uuid";
  private static final String FIELD_ID = "id";
  private static final String FIELD_NAME = "name";
  private static final String FIELD_MATERIAL = "material";
  private static final String FIELD_AREA = "area";
  private static final String FIELD_THERMAL_RESISTIVITY = "thermalResistivity";
  private static final String FIELD_THERMAL_CAPACITANCE = "thermalCapacitance";
  private static final String FIELD_WIRES_NUMBER = "wiresNumber";
  private static final String FIELD_LENGTH_OF_LAY = "lengthOfLay";
  private static final String FIELD_IS_COMPACTED = "isCompacted";

  public CableTypeParser(ObjectMapper mapper) {
    this.mapper = Objects.requireNonNull(mapper);
  }

  public List<LayerInput> parseLayerList(String json) throws ParsingException {
    if (json == null || json.isBlank()) return Collections.emptyList();

    try {
      JsonNode node = unwrapTextual(mapper.readTree(json));
      if (node == null || !node.isArray()) {
        throw new ParsingException("Expected array for LayerInput list: " + json);
      }

      List<LayerInput> layers = new ArrayList<>();
      for (JsonNode element : node) {
        ObjectNode layerNode = requireObject(element, "LayerInput", element);
        UUID uuid = parseUuid(layerNode, "LayerInput");
        String id = parseId(layerNode, "LayerInput");
        CableMaterial material = parseMaterial(layerNode, "LayerInput");
        ComparableQuantity<Length> innerDiameter =
            parseQuantityField(
                layerNode,
                "innerDiameter",
                Length.class,
                PowerSystemUnits.MILLIMETRE,
                "Cannot parse LayerInput: missing innerDiameter in " + element);
        ComparableQuantity<Length> outerDiameter =
            parseQuantityField(
                layerNode,
                "outerDiameter",
                Length.class,
                PowerSystemUnits.MILLIMETRE,
                "Cannot parse LayerInput: missing outerDiameter in " + element);
        ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalResistivity>
            thermalResistivity =
                parseQuantityField(
                    layerNode,
                    FIELD_THERMAL_RESISTIVITY,
                    edu.ie3.util.quantities.interfaces.ThermalResistivity.class,
                    PowerSystemUnits.KELVIN_METRE_PER_WATT,
                    "Cannot parse LayerInput: missing thermalResistivity in " + element);
        ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalCapacitance>
            thermalCapacitance =
                parseQuantityField(
                    layerNode,
                    FIELD_THERMAL_CAPACITANCE,
                    edu.ie3.util.quantities.interfaces.ThermalCapacitance.class,
                    PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN,
                    "Cannot parse LayerInput: missing thermalCapacitance in " + element);
        ComparableQuantity<Area> area =
            parseOptionalQuantityField(
                layerNode,
                FIELD_AREA,
                Area.class,
                PowerSystemUnits.SQUARE_MILLIMETRE,
                "LayerInput");

        layers.add(
            new LayerInput(
                uuid,
                id,
                material,
                innerDiameter,
                outerDiameter,
                thermalResistivity,
                thermalCapacitance,
                area));
      }

      return List.copyOf(layers);
    } catch (IOException | RuntimeException e) {
      throw new ParsingException(
          "Cannot parse LayerInput list: " + json + ". Cause: " + e.getMessage(), e);
    }
  }

  public ScreenLayerInput parseScreenLayer(String json) throws ParsingException {
    if (json == null || json.isBlank()) return null;

    try {
      ObjectNode node =
          requireObject(unwrapTextual(mapper.readTree(json)), "ScreenLayerInput", json);
      ObjectNode screenNode = findScreenNode(node, json);
      UUID uuid = parseUuid(screenNode, "ScreenLayerInput");
      String id = parseId(screenNode, "ScreenLayerInput");
      CableMaterial material = parseMaterial(screenNode, "ScreenLayerInput");
      ComparableQuantity<Length> innerDiameter =
          parseQuantityField(
              screenNode,
              "innerDiameter",
              Length.class,
              PowerSystemUnits.MILLIMETRE,
              "Cannot parse ScreenLayerInput: missing innerDiameter in " + json);
      ComparableQuantity<Length> outerDiameter =
          parseQuantityField(
              screenNode,
              "outerDiameter",
              Length.class,
              PowerSystemUnits.MILLIMETRE,
              "Cannot parse ScreenLayerInput: missing outerDiameter in " + json);
      ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalResistivity> thermalResistivity =
          parseQuantityField(
              screenNode,
              FIELD_THERMAL_RESISTIVITY,
              edu.ie3.util.quantities.interfaces.ThermalResistivity.class,
              PowerSystemUnits.KELVIN_METRE_PER_WATT,
              "Cannot parse ScreenLayerInput: missing thermalResistivity in " + json);
      ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalCapacitance> thermalCapacitance =
          parseQuantityField(
              screenNode,
              FIELD_THERMAL_CAPACITANCE,
              edu.ie3.util.quantities.interfaces.ThermalCapacitance.class,
              PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN,
              "Cannot parse ScreenLayerInput: missing thermalCapacitance in " + json);
      ComparableQuantity<Area> area =
          parseOptionalQuantityField(
              screenNode,
              FIELD_AREA,
              Area.class,
              PowerSystemUnits.SQUARE_MILLIMETRE,
              "ScreenLayerInput");
      String wiresNumberText = optionalText(screenNode, FIELD_WIRES_NUMBER);
      if (wiresNumberText == null) {
        throw new ParsingException(
            "Cannot parse ScreenLayerInput: missing " + FIELD_WIRES_NUMBER + " in " + json);
      }
      int wiresNumber;
      try {
        wiresNumber = Integer.parseInt(wiresNumberText);
      } catch (NumberFormatException e) {
        throw new ParsingException(
            "Cannot parse ScreenLayerInput: invalid " + FIELD_WIRES_NUMBER + " in " + json, e);
      }
      ComparableQuantity<Length> wireDiameter =
          parseQuantityField(
              screenNode,
              "wireDiameter",
              Length.class,
              PowerSystemUnits.MILLIMETRE,
              "Cannot parse ScreenLayerInput: missing wireDiameter in " + json);
      ComparableQuantity<Length> lengthOfLay =
          parseOptionalQuantityField(
              screenNode,
              FIELD_LENGTH_OF_LAY,
              Length.class,
              PowerSystemUnits.MILLIMETRE,
              "ScreenLayerInput");
      ComparableQuantity<edu.ie3.util.quantities.interfaces.ElectricalResistivity>
          electricalResistivity =
              parseQuantityField(
                  screenNode,
                  "electricalResistivity",
                  edu.ie3.util.quantities.interfaces.ElectricalResistivity.class,
                  PowerSystemUnits.OHM_METRE,
                  "Cannot parse ScreenLayerInput: missing electricalResistivity in " + json);

      return new ScreenLayerInput(
          uuid,
          id,
          material,
          innerDiameter,
          outerDiameter,
          thermalResistivity,
          thermalCapacitance,
          Optional.ofNullable(area),
          wiresNumber,
          wireDiameter,
          Optional.ofNullable(lengthOfLay),
          electricalResistivity);
    } catch (IOException | RuntimeException e) {
      throw new ParsingException(
          "Cannot parse ScreenLayerInput: " + json + ". Cause: " + e.getMessage(), e);
    }
  }

  public ConductorInput parseConductor(String json) throws ParsingException {
    if (json == null || json.isBlank()) return null;

    try {
      ObjectNode node = requireObject(unwrapTextual(mapper.readTree(json)), "ConductorInput", json);
      UUID uuid = parseUuid(node, "ConductorInput");
      String id = parseId(node, "ConductorInput");
      CableMaterial material = parseMaterial(node, "ConductorInput");
      ComparableQuantity<Area> crossSection =
          parseQuantityField(
              node,
              "crossSection",
              Area.class,
              PowerSystemUnits.SQUARE_MILLIMETRE,
              "Cannot parse ConductorInput: missing crossSection in " + json);
      ComparableQuantity<Length> diameter =
          parseQuantityField(
              node,
              "diameter",
              Length.class,
              PowerSystemUnits.MILLIMETRE,
              "Cannot parse ConductorInput: missing diameter in " + json);
      boolean isCompacted =
          node.has(FIELD_IS_COMPACTED)
              && !node.get(FIELD_IS_COMPACTED).isNull()
              && node.get(FIELD_IS_COMPACTED).asBoolean(false);
      ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalResistivity> thermalResistivity =
          parseQuantityField(
              node,
              FIELD_THERMAL_RESISTIVITY,
              edu.ie3.util.quantities.interfaces.ThermalResistivity.class,
              PowerSystemUnits.KELVIN_METRE_PER_WATT,
              "Cannot parse ConductorInput: missing thermalResistivity in " + json);
      ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalCapacitance> thermalCapacitance =
          parseQuantityField(
              node,
              FIELD_THERMAL_CAPACITANCE,
              edu.ie3.util.quantities.interfaces.ThermalCapacitance.class,
              PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN,
              "Cannot parse ConductorInput: missing thermalCapacitance in " + json);
      ComparableQuantity<Area> area =
          parseOptionalQuantityField(
              node, FIELD_AREA, Area.class, PowerSystemUnits.SQUARE_MILLIMETRE, "ConductorInput");

      return new ConductorInput(
          uuid,
          id,
          material,
          crossSection,
          diameter,
          isCompacted,
          thermalResistivity,
          thermalCapacitance,
          area);
    } catch (IOException | RuntimeException e) {
      throw new ParsingException(
          "Cannot parse ConductorInput: " + json + ". Cause: " + e.getMessage(), e);
    }
  }

  private ObjectNode requireObject(JsonNode node, String context, Object source)
      throws ParsingException {
    if (node == null || !node.isObject()) {
      throw new ParsingException("Cannot parse " + context + ": expected object in " + source);
    }
    return (ObjectNode) node;
  }

  private ObjectNode findScreenNode(ObjectNode node, String source) throws ParsingException {
    if (hasMaterial(node)) return node;

    for (JsonNode child : node) {
      if (child != null && child.isObject() && hasMaterial(child)) {
        return (ObjectNode) child;
      }
    }

    throw new ParsingException("Cannot parse ScreenLayerInput: missing material in " + source);
  }

  private boolean hasMaterial(JsonNode node) {
    return node.has(FIELD_MATERIAL) && !node.get(FIELD_MATERIAL).isNull();
  }

  private UUID parseUuid(ObjectNode node, String context) throws ParsingException {
    ensureUuid(node);
    try {
      return UUID.fromString(node.get(FIELD_UUID).asString());
    } catch (IllegalArgumentException e) {
      throw new ParsingException("Cannot parse " + context + ": invalid uuid in " + node, e);
    }
  }

  private String parseId(JsonNode node, String context) throws ParsingException {
    String id = resolveId(node, new String[] {FIELD_ID, FIELD_NAME});
    if (id == null) {
      throw new ParsingException("Cannot parse " + context + ": missing id in " + node);
    }
    return id;
  }

  private <T extends Quantity<T>> ComparableQuantity<T> parseOptionalQuantityField(
      JsonNode node, String fieldName, Class<T> quantityClass, Unit<?> unit, String context)
      throws ParsingException {
    String value = optionalText(node, fieldName);
    if (value == null) return null;

    try {
      return Quantities.getQuantity(Double.parseDouble(value), unit).asType(quantityClass);
    } catch (NumberFormatException nfe) {
      throw new ParsingException(
          "Cannot parse " + context + ": invalid " + fieldName + " value in " + node, nfe);
    }
  }

  private String optionalText(JsonNode node, String fieldName) {
    if (!node.has(fieldName) || node.get(fieldName).isNull()) return null;

    String value = node.get(fieldName).asString();
    return value == null || value.isBlank() || "null".equalsIgnoreCase(value) ? null : value;
  }

  private CableMaterial parseMaterial(JsonNode node, String context) throws ParsingException {
    try {
      String mat = optionalText(node, FIELD_MATERIAL);
      return CableMaterial.valueOf(mat);
    } catch (Exception e) {
      String mat = optionalText(node, FIELD_MATERIAL);
      throw new ParsingException("Cannot parse " + context + ": invalid material: " + mat, e);
    }
  }

  private <T extends Quantity<T>> ComparableQuantity<T> parseQuantityField(
      JsonNode node, String fieldName, Class<T> quantityClass, Unit<?> unit, String missingMessage)
      throws ParsingException {
    String value = optionalText(node, fieldName);
    if (value == null) {
      throw new ParsingException(missingMessage);
    }

    try {
      return Quantities.getQuantity(Double.parseDouble(value), unit).asType(quantityClass);
    } catch (NumberFormatException nfe) {
      throw new ParsingException("Cannot parse " + fieldName + " value in " + node, nfe);
    }
  }

  private JsonNode unwrapTextual(JsonNode node) throws IOException {
    if (node != null && node.isString()) return mapper.readTree(node.asString());
    return node;
  }

  private void ensureUuid(ObjectNode node) {
    if (!node.has(FIELD_UUID) || node.get(FIELD_UUID).isNull()) {
      node.put(FIELD_UUID, UUID.randomUUID().toString());
    }
  }

  private String resolveId(JsonNode node, String[] candidates) {
    for (String candidate : candidates) {
      String value = optionalText(node, candidate);
      if (value != null) return value;
    }
    return null;
  }
}
