/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput.parser;

import static edu.ie3.datamodel.io.naming.FieldNamingStrategy.*;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.input.connector.type.CableMaterial;
import edu.ie3.datamodel.models.input.connector.type.ConductorInput;
import edu.ie3.datamodel.models.input.connector.type.LayerInput;
import edu.ie3.datamodel.models.input.connector.type.ScreenLayerInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import edu.ie3.util.quantities.interfaces.ThermalResistivity;
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

  public CableTypeParser(ObjectMapper mapper) {
    this.mapper = Objects.requireNonNull(mapper);
  }

  public List<LayerInput> parseLayerList(String json) throws ParsingException {
    if (json == null || json.isBlank()) return Collections.emptyList();

    try {
      JsonNode node = unwrapTextual(mapper.readTree(json));
      if (node == null || !node.isArray()) {
        throw new ParsingException("Expected array for " + LAYER + " list: " + json);
      }

      List<LayerInput> layers = new ArrayList<>();
      for (JsonNode element : node) {
        ObjectNode layerNode = requireObject(element, LAYER, element);
        UUID uuid = parseUuid(layerNode, LAYER);
        String id = parseId(layerNode, LAYER);
        CableMaterial material = parseMaterial(layerNode, LAYER);
        ComparableQuantity<Length> innerDiameter =
            parseQuantityField(
                layerNode,
                "innerDiameter",
                Length.class,
                PowerSystemUnits.MILLIMETRE,
                "Cannot parse " + LAYER + ": missing innerDiameter in " + element);
        ComparableQuantity<Length> outerDiameter =
            parseQuantityField(
                layerNode,
                "outerDiameter",
                Length.class,
                PowerSystemUnits.MILLIMETRE,
                "Cannot parse " + LAYER + ": missing outerDiameter in " + element);
        ComparableQuantity<ThermalResistivity> thermalResistivity =
            parseQuantityField(
                layerNode,
                THERMAL_RESISTIVITY,
                edu.ie3.util.quantities.interfaces.ThermalResistivity.class,
                PowerSystemUnits.KELVIN_METRE_PER_WATT,
                "Cannot parse " + LAYER + ": missing thermalResistivity in " + element);
        ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalCapacitance>
            thermalCapacitance =
                parseQuantityField(
                    layerNode,
                    THERMAL_CAPACITANCE,
                    edu.ie3.util.quantities.interfaces.ThermalCapacitance.class,
                    PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN,
                    "Cannot parse " + LAYER + ": missing thermalCapacitance in " + element);
        ComparableQuantity<Area> area =
            parseOptionalQuantityField(
                layerNode, AREA, Area.class, PowerSystemUnits.SQUARE_MILLIMETRE, LAYER);

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
    } catch (RuntimeException e) {
      throw new ParsingException(
          "Cannot parse " + LAYER + " list: " + json + ". Cause: " + e.getMessage(), e);
    }
  }

  public ScreenLayerInput parseScreenLayer(String json) throws ParsingException {
    if (json == null || json.isBlank()) return null;

    try {
      ObjectNode node = requireObject(unwrapTextual(mapper.readTree(json)), SCREEN_LAYER, json);
      ObjectNode screenNode = findScreenNode(node, json);
      UUID uuid = parseUuid(screenNode, SCREEN_LAYER);
      String id = parseId(screenNode, SCREEN_LAYER);
      CableMaterial material = parseMaterial(screenNode, SCREEN_LAYER);
      ComparableQuantity<Length> innerDiameter =
          parseQuantityField(
              screenNode,
              "innerDiameter",
              Length.class,
              PowerSystemUnits.MILLIMETRE,
              "Cannot parse " + SCREEN_LAYER + ": missing innerDiameter in " + json);
      ComparableQuantity<Length> outerDiameter =
          parseQuantityField(
              screenNode,
              "outerDiameter",
              Length.class,
              PowerSystemUnits.MILLIMETRE,
              "Cannot parse " + SCREEN_LAYER + ": missing outerDiameter in " + json);
      ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalResistivity> thermalResistivity =
          parseQuantityField(
              screenNode,
              THERMAL_RESISTIVITY,
              edu.ie3.util.quantities.interfaces.ThermalResistivity.class,
              PowerSystemUnits.KELVIN_METRE_PER_WATT,
              "Cannot parse " + SCREEN_LAYER + ": missing thermalResistivity in " + json);
      ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalCapacitance> thermalCapacitance =
          parseQuantityField(
              screenNode,
              THERMAL_CAPACITANCE,
              edu.ie3.util.quantities.interfaces.ThermalCapacitance.class,
              PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN,
              "Cannot parse " + SCREEN_LAYER + ": missing thermalCapacitance in " + json);
      ComparableQuantity<Area> area =
          parseOptionalQuantityField(
              screenNode, AREA, Area.class, PowerSystemUnits.SQUARE_MILLIMETRE, SCREEN_LAYER);
      String wiresNumberText = optionalText(screenNode, WIRES_NUMBER);
      if (wiresNumberText == null) {
        throw new ParsingException(
            "Cannot parse " + SCREEN_LAYER + ": missing " + WIRES_NUMBER + " in " + json);
      }
      int wiresNumber = parseIntegerField(wiresNumberText, WIRES_NUMBER, SCREEN_LAYER, json);
      ComparableQuantity<Length> wireDiameter =
          parseQuantityField(
              screenNode,
              "wireDiameter",
              Length.class,
              PowerSystemUnits.MILLIMETRE,
              "Cannot parse " + SCREEN_LAYER + ": missing wireDiameter in " + json);
      ComparableQuantity<Length> lengthOfLay =
          parseOptionalQuantityField(
              screenNode, LENGTH_OF_LAY, Length.class, PowerSystemUnits.MILLIMETRE, SCREEN_LAYER);
      ComparableQuantity<edu.ie3.util.quantities.interfaces.ElectricalResistivity>
          electricalResistivity =
              parseQuantityField(
                  screenNode,
                  "electricalResistivity",
                  edu.ie3.util.quantities.interfaces.ElectricalResistivity.class,
                  PowerSystemUnits.OHM_METRE,
                  "Cannot parse " + SCREEN_LAYER + ": missing electricalResistivity in " + json);

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
    } catch (RuntimeException e) {
      throw new ParsingException(
          "Cannot parse " + SCREEN_LAYER + ": " + json + ". Cause: " + e.getMessage(), e);
    }
  }

  public ConductorInput parseConductor(String json) throws ParsingException {
    if (json == null || json.isBlank()) return null;

    try {
      ObjectNode node = requireObject(unwrapTextual(mapper.readTree(json)), CONDUCTOR, json);
      UUID uuid = parseUuid(node, CONDUCTOR);
      String id = parseId(node, CONDUCTOR);
      CableMaterial material = parseMaterial(node, CONDUCTOR);
      ComparableQuantity<Area> crossSection =
          parseQuantityField(
              node,
              "crossSection",
              Area.class,
              PowerSystemUnits.SQUARE_MILLIMETRE,
              "Cannot parse " + CONDUCTOR + ": missing crossSection in " + json);
      ComparableQuantity<Length> diameter =
          parseQuantityField(
              node,
              "diameter",
              Length.class,
              PowerSystemUnits.MILLIMETRE,
              "Cannot parse " + CONDUCTOR + ": missing diameter in " + json);
      boolean isCompacted =
          node.has(IS_COMPACTED)
              && !node.get(IS_COMPACTED).isNull()
              && node.get(IS_COMPACTED).asBoolean(false);
      ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalResistivity> thermalResistivity =
          parseQuantityField(
              node,
              THERMAL_RESISTIVITY,
              edu.ie3.util.quantities.interfaces.ThermalResistivity.class,
              PowerSystemUnits.KELVIN_METRE_PER_WATT,
              "Cannot parse " + CONDUCTOR + ": missing thermalResistivity in " + json);
      ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalCapacitance> thermalCapacitance =
          parseQuantityField(
              node,
              THERMAL_CAPACITANCE,
              edu.ie3.util.quantities.interfaces.ThermalCapacitance.class,
              PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN,
              "Cannot parse " + CONDUCTOR + ": missing thermalCapacitance in " + json);
      ComparableQuantity<Area> area =
          parseOptionalQuantityField(
              node, AREA, Area.class, PowerSystemUnits.SQUARE_MILLIMETRE, CONDUCTOR);

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
    } catch (RuntimeException e) {
      throw new ParsingException(
          "Cannot parse " + CONDUCTOR + ": " + json + ". Cause: " + e.getMessage(), e);
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

    throw new ParsingException("Cannot parse " + SCREEN_LAYER + ": missing material in " + source);
  }

  private boolean hasMaterial(JsonNode node) {
    return node.has(MATERIAL) && !node.get(MATERIAL).isNull();
  }

  private UUID parseUuid(ObjectNode node, String context) throws ParsingException {
    ensureUuid(node);
    try {
      return java.util.UUID.fromString(node.get(UUID).asString());
    } catch (IllegalArgumentException e) {
      throw new ParsingException("Cannot parse " + context + ": invalid uuid in " + node, e);
    }
  }

  private String parseId(JsonNode node, String context) throws ParsingException {
    String id = resolveId(node, new String[] {ID, NAME});
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
      String mat = optionalText(node, MATERIAL);
      return CableMaterial.valueOf(mat);
    } catch (Exception e) {
      String mat = optionalText(node, MATERIAL);
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

  private JsonNode unwrapTextual(JsonNode node) {
    if (node != null && node.isString()) {
      try {
        return mapper.readTree(node.asString());
      } catch (Exception e) {
        throw new IllegalStateException(e);
      }
    }
    return node;
  }

  private void ensureUuid(ObjectNode node) {
    if (!node.has(UUID) || node.get(UUID).isNull()) {
      node.put(UUID, java.util.UUID.randomUUID().toString());
    }
  }

  private String resolveId(JsonNode node, String[] candidates) {
    for (String candidate : candidates) {
      String value = optionalText(node, candidate);
      if (value != null) return value;
    }
    return null;
  }

  private int parseIntegerField(String text, String fieldName, String context, String source)
      throws ParsingException {
    try {
      return Integer.parseInt(text);
    } catch (NumberFormatException e) {
      throw new ParsingException(
          "Cannot parse " + context + ": invalid " + fieldName + " in " + source, e);
    }
  }
}
