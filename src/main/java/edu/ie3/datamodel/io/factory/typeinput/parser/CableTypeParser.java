/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.typeinput.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.input.connector.type.CableMaterial;
import edu.ie3.datamodel.models.input.connector.type.ConductorInput;
import edu.ie3.datamodel.models.input.connector.type.LayerInput;
import edu.ie3.datamodel.models.input.connector.type.ScreenLayerInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.io.IOException;
import java.util.*;
import javax.measure.Unit;
import javax.measure.quantity.Area;
import javax.measure.quantity.Length;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

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
      if (!node.isArray()) {
        throw new ParsingException("Expected array for LayerInput list: " + json);
      }

      List<LayerInput> layers = new ArrayList<>();
      for (JsonNode element : node) {
        if (!element.isObject()) {
          throw new ParsingException("Invalid LayerInput element (not object): " + element);
        }

        ensureUuid((ObjectNode) element);
        UUID uuid = UUID.fromString(element.get(FIELD_UUID).asText());

        String id = resolveId(element, new String[] {FIELD_ID, FIELD_NAME});
        if (id == null) {
          throw new ParsingException("Cannot parse LayerInput: missing id in " + element);
        }

        CableMaterial material = parseMaterial(element, "LayerInput");

        ComparableQuantity<Length> innerDiameter =
            parseQuantityField(
                element,
                "innerDiameter",
                Length.class,
                PowerSystemUnits.MILLIMETRE,
                "Cannot parse LayerInput: missing innerDiameter in " + element);

        ComparableQuantity<Length> outerDiameter =
            parseQuantityField(
                element,
                "outerDiameter",
                Length.class,
                PowerSystemUnits.MILLIMETRE,
                "Cannot parse LayerInput: missing outerDiameter in " + element);

        ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalResistivity>
            thermalResistivity =
                parseQuantityField(
                    element,
                    FIELD_THERMAL_RESISTIVITY,
                    edu.ie3.util.quantities.interfaces.ThermalResistivity.class,
                    PowerSystemUnits.KELVIN_METRE_PER_WATT,
                    "Cannot parse LayerInput: missing thermalResistivity in " + element);

        ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalCapacitance>
            thermalCapacitance =
                parseQuantityField(
                    element,
                    FIELD_THERMAL_CAPACITANCE,
                    edu.ie3.util.quantities.interfaces.ThermalCapacitance.class,
                    PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN,
                    "Cannot parse LayerInput: missing thermalCapacitance in " + element);

        ComparableQuantity<Area> area = null;
        String a =
            (element.has(FIELD_AREA) && !element.get(FIELD_AREA).isNull())
                ? element.get(FIELD_AREA).asText()
                : null;
        if (a != null) {
          a = a.trim();
          if (!a.isEmpty() && !"null".equalsIgnoreCase(a)) {
            try {
              area =
                  Quantities.getQuantity(Double.parseDouble(a), PowerSystemUnits.SQUARE_MILLIMETRE);
            } catch (NumberFormatException nfe) {
              throw new ParsingException(
                  "Cannot parse LayerInput: invalid area value in " + element, nfe);
            }
          }
        }

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
    } catch (IOException e) {
      throw new ParsingException(
          "Cannot parse LayerInput list: " + json + ". Cause: " + e.getMessage(), e);
    }
  }

  public ScreenLayerInput parseScreenLayer(String json) throws ParsingException {
    if (json == null || json.isBlank()) return null;

    try {
      JsonNode node = unwrapTextual(mapper.readTree(json));
      if (node.isObject()) ensureUuid((ObjectNode) node);

      JsonNode workingNode = node;
      JsonNode materialNode = workingNode.get(FIELD_MATERIAL);
      if (materialNode == null || materialNode.isNull()) {
        for (JsonNode child : node) {
          JsonNode childMaterialNode = child == null ? null : child.get(FIELD_MATERIAL);
          if (childMaterialNode != null && !childMaterialNode.isNull()) {
            workingNode = child;
            break;
          }
        }
      }

      UUID uuid = UUID.fromString(workingNode.get(FIELD_UUID).asText());
      String id = resolveId(workingNode, new String[] {FIELD_ID, FIELD_NAME});
      if (id == null)
        throw new ParsingException("Cannot parse ScreenLayerInput: missing id in " + json);

      CableMaterial material = parseMaterial(workingNode, "ScreenLayerInput");

      ComparableQuantity<Length> innerDiameter =
          parseQuantityField(
              workingNode,
              "innerDiameter",
              Length.class,
              PowerSystemUnits.MILLIMETRE,
              "Cannot parse ScreenLayerInput: missing innerDiameter in " + json);

      ComparableQuantity<Length> outerDiameter =
          parseQuantityField(
              workingNode,
              "outerDiameter",
              Length.class,
              PowerSystemUnits.MILLIMETRE,
              "Cannot parse ScreenLayerInput: missing outerDiameter in " + json);

      ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalResistivity> thermalResistivity =
          parseQuantityField(
              workingNode,
              FIELD_THERMAL_RESISTIVITY,
              edu.ie3.util.quantities.interfaces.ThermalResistivity.class,
              PowerSystemUnits.KELVIN_METRE_PER_WATT,
              "Cannot parse ScreenLayerInput: missing thermalResistivity in " + json);

      ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalCapacitance> thermalCapacitance =
          parseQuantityField(
              workingNode,
              FIELD_THERMAL_CAPACITANCE,
              edu.ie3.util.quantities.interfaces.ThermalCapacitance.class,
              PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN,
              "Cannot parse ScreenLayerInput: missing thermalCapacitance in " + json);

      ComparableQuantity<Area> area = null;
      JsonNode areaNode = node.get(FIELD_AREA);
      String areaText = areaNode == null || areaNode.isNull() ? null : areaNode.asText();
      if (areaText != null) {
        areaText = areaText.trim();
        if (!areaText.isEmpty() && !"null".equalsIgnoreCase(areaText)) {
          try {
            area =
                Quantities.getQuantity(
                    Double.parseDouble(areaText), PowerSystemUnits.SQUARE_MILLIMETRE);
          } catch (NumberFormatException nfe) {
            throw new ParsingException(
                "Cannot parse ScreenLayerInput: invalid area value in " + json, nfe);
          }
        }
      }

      JsonNode wiresNode = node.get(FIELD_WIRES_NUMBER);
      if (wiresNode == null || wiresNode.isNull()) {
        throw new ParsingException(
            "Cannot parse ScreenLayerInput: missing " + FIELD_WIRES_NUMBER + " in " + json);
      }
      int wiresNumber;
      try {
        wiresNumber = wiresNode.asInt();
      } catch (Exception e) {
        throw new ParsingException(
            "Cannot parse ScreenLayerInput: invalid " + FIELD_WIRES_NUMBER + " in " + json, e);
      }

      ComparableQuantity<Length> wireDiameter =
          parseQuantityField(
              node,
              "wireDiameter",
              Length.class,
              PowerSystemUnits.MILLIMETRE,
              "Cannot parse ScreenLayerInput: missing wireDiameter in " + json);

      ComparableQuantity<Length> lengthOfLay = null;
      JsonNode lengthOfLayNode = node.get(FIELD_LENGTH_OF_LAY);
      String lengthOfLayText =
          lengthOfLayNode == null || lengthOfLayNode.isNull() ? null : lengthOfLayNode.asText();
      if (lengthOfLayText != null) {
        lengthOfLayText = lengthOfLayText.trim();
        if (!lengthOfLayText.isEmpty() && !"null".equalsIgnoreCase(lengthOfLayText)) {
          try {
            lengthOfLay =
                Quantities.getQuantity(
                        Double.parseDouble(lengthOfLayText), PowerSystemUnits.MILLIMETRE)
                    .asType(Length.class);
          } catch (NumberFormatException nfe) {
            throw new ParsingException(
                "Cannot parse ScreenLayerInput: invalid lengthOfLay value in " + json, nfe);
          }
        }
      }

      ComparableQuantity<edu.ie3.util.quantities.interfaces.ElectricalResistivity>
          electricalResistivity =
              parseQuantityField(
                  node,
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
    } catch (IOException e) {
      throw new ParsingException(
          "Cannot parse ScreenLayerInput: " + json + ". Cause: " + e.getMessage(), e);
    }
  }

  public ConductorInput parseConductor(String json) throws ParsingException {
    if (json == null || json.isBlank()) return null;

    try {
      JsonNode node = unwrapTextual(mapper.readTree(json));

      if (node.isObject() && (!node.has(FIELD_UUID) || node.get(FIELD_UUID).isNull())) {
        ((ObjectNode) node).put(FIELD_UUID, UUID.randomUUID().toString());
      }

      UUID uuid = UUID.fromString(node.get(FIELD_UUID).asText());
      String id = resolveId(node, new String[] {FIELD_ID, FIELD_NAME});
      if (id == null)
        throw new ParsingException("Cannot parse ConductorInput: missing id in " + json);

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

      ComparableQuantity<Area> area = null;
      if (node.has(FIELD_AREA) && !node.get(FIELD_AREA).isNull()) {
        String a = node.get(FIELD_AREA).asText();
        if (a != null && !a.isBlank() && !"null".equalsIgnoreCase(a)) {
          try {
            area =
                Quantities.getQuantity(Double.parseDouble(a), PowerSystemUnits.SQUARE_MILLIMETRE);
          } catch (NumberFormatException nfe) {
            throw new ParsingException(
                "Cannot parse ConductorInput: invalid area value in " + json, nfe);
          }
        }
      }

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
    } catch (IOException e) {
      throw new ParsingException(
          "Cannot parse ConductorInput: " + json + ". Cause: " + e.getMessage(), e);
    }
  }

  private JsonNode unwrapTextual(JsonNode node) throws IOException {
    if (node.isTextual()) return mapper.readTree(node.asText());
    return node;
  }

  private void ensureUuid(ObjectNode node) {
    if (!node.has(FIELD_UUID) || node.get(FIELD_UUID).isNull())
      node.put(FIELD_UUID, UUID.randomUUID().toString());
  }

  private String resolveId(JsonNode node, String[] candidates) {
    for (String c : candidates) {
      if (node.has(c) && !node.get(c).isNull()) return node.get(c).asText();
    }
    return null;
  }

  private CableMaterial parseMaterial(JsonNode node, String context) throws ParsingException {
    try {
      String mat = null;
      if (node.has(FIELD_MATERIAL) && !node.get(FIELD_MATERIAL).isNull())
        mat = node.get(FIELD_MATERIAL).asText();
      return CableMaterial.valueOf(mat);
    } catch (Exception e) {
      String mat =
          (node.has(FIELD_MATERIAL) && !node.get(FIELD_MATERIAL).isNull())
              ? node.get(FIELD_MATERIAL).asText()
              : "null";
      throw new ParsingException("Cannot parse " + context + ": invalid material: " + mat, e);
    }
  }

  private <T extends javax.measure.Quantity<T>> ComparableQuantity<T> parseQuantityField(
      JsonNode node, String fieldName, Class<T> quantityClass, Unit<?> unit, String missingMessage)
      throws ParsingException {
    try {
      String s =
          node.has(fieldName) && !node.get(fieldName).isNull()
              ? node.get(fieldName).asText()
              : null;
      if (s == null || s.isBlank() || "null".equalsIgnoreCase(s))
        throw new ParsingException(missingMessage);
      return Quantities.getQuantity(Double.parseDouble(s), unit).asType(quantityClass);
    } catch (NumberFormatException nfe) {
      throw new ParsingException("Cannot parse " + fieldName + " value in " + node, nfe);
    }
  }
}
