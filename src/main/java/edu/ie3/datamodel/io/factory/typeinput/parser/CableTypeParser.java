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
import java.io.IOException;
import java.util.*;
import javax.measure.Unit;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

public class CableTypeParser {
  private final ObjectMapper mapper;

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
        UUID uuid = UUID.fromString(element.get("uuid").asText());

        String id = resolveId(element, new String[] {"id", "name"});
        if (id == null)
          throw new ParsingException("Cannot parse LayerInput: missing id in " + element);

        CableMaterial material;
        try {
          material = CableMaterial.valueOf(element.get("material").asText());
        } catch (Exception e) {
          String mat =
              (element.has("material") && !element.get("material").isNull())
                  ? element.get("material").asText()
                  : "null";
          throw new ParsingException("Cannot parse LayerInput: invalid material: " + mat, e);
        }

        ComparableQuantity<javax.measure.quantity.Length> innerDiameter =
            parseQuantityField(
                element,
                "innerDiameter",
                javax.measure.quantity.Length.class,
                edu.ie3.util.quantities.PowerSystemUnits.MILLIMETRE,
                "Cannot parse LayerInput: missing innerDiameter in " + element);

        ComparableQuantity<javax.measure.quantity.Length> outerDiameter =
            parseQuantityField(
                element,
                "outerDiameter",
                javax.measure.quantity.Length.class,
                edu.ie3.util.quantities.PowerSystemUnits.MILLIMETRE,
                "Cannot parse LayerInput: missing outerDiameter in " + element);

        ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalResistivity>
            thermalResistivity =
                parseQuantityField(
                    element,
                    "thermalResistivity",
                    edu.ie3.util.quantities.interfaces.ThermalResistivity.class,
                    edu.ie3.util.quantities.PowerSystemUnits.KELVIN_METRE_PER_WATT,
                    "Cannot parse LayerInput: missing thermalResistivity in " + element);

        ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalCapacitance>
            thermalCapacitance =
                parseQuantityField(
                    element,
                    "thermalCapacitance",
                    edu.ie3.util.quantities.interfaces.ThermalCapacitance.class,
                    edu.ie3.util.quantities.PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN,
                    "Cannot parse LayerInput: missing thermalCapacitance in " + element);

        ComparableQuantity<javax.measure.quantity.Area> area = null;
        if (element.has("area") && !element.get("area").isNull()) {
          String a = element.get("area").asText();
          if (a != null && !a.isBlank() && !"null".equalsIgnoreCase(a)) {
            try {
              area =
                  Quantities.getQuantity(
                          Double.parseDouble(a),
                          edu.ie3.util.quantities.PowerSystemUnits.SQUARE_MILLIMETRE)
                      .asType(javax.measure.quantity.Area.class);
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

      if (node.isObject() && (!node.has("uuid") || node.get("uuid").isNull())) {
        ((ObjectNode) node).put("uuid", java.util.UUID.randomUUID().toString());
      }

      JsonNode workingNode = node;
      if (!workingNode.has("material") || workingNode.get("material").isNull()) {
        if (node.isArray()) {
          for (JsonNode child : node) {
            if (child != null && child.has("material") && !child.get("material").isNull()) {
              workingNode = child;
              break;
            }
          }
        } else if (node.isObject()) {
          Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
          while (fields.hasNext()) {
            JsonNode child = fields.next().getValue();
            if (child != null && child.has("material") && !child.get("material").isNull()) {
              workingNode = child;
              break;
            }
          }
        }
      }

      java.util.UUID uuid = java.util.UUID.fromString(workingNode.get("uuid").asText());
      String id = resolveId(workingNode, new String[] {"id", "name"});
      if (id == null)
        throw new ParsingException("Cannot parse ScreenLayerInput: missing id in " + json);

      CableMaterial material;
      try {
        material =
            CableMaterial.valueOf(
                workingNode.has("material") && !workingNode.get("material").isNull()
                    ? workingNode.get("material").asText()
                    : null);
      } catch (Exception e) {
        String mat =
            (workingNode.has("material") && !workingNode.get("material").isNull())
                ? workingNode.get("material").asText()
                : "null";
        throw new ParsingException("Cannot parse ScreenLayerInput: invalid material: " + mat, e);
      }

      ComparableQuantity<javax.measure.quantity.Length> innerDiameter =
          parseQuantityField(
              workingNode,
              "innerDiameter",
              javax.measure.quantity.Length.class,
              edu.ie3.util.quantities.PowerSystemUnits.MILLIMETRE,
              "Cannot parse ScreenLayerInput: missing innerDiameter in " + json);

      ComparableQuantity<javax.measure.quantity.Length> outerDiameter =
          parseQuantityField(
              workingNode,
              "outerDiameter",
              javax.measure.quantity.Length.class,
              edu.ie3.util.quantities.PowerSystemUnits.MILLIMETRE,
              "Cannot parse ScreenLayerInput: missing outerDiameter in " + json);

      ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalResistivity> thermalResistivity =
          parseQuantityField(
              workingNode,
              "thermalResistivity",
              edu.ie3.util.quantities.interfaces.ThermalResistivity.class,
              edu.ie3.util.quantities.PowerSystemUnits.KELVIN_METRE_PER_WATT,
              "Cannot parse ScreenLayerInput: missing thermalResistivity in " + json);

      ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalCapacitance> thermalCapacitance =
          parseQuantityField(
              workingNode,
              "thermalCapacitance",
              edu.ie3.util.quantities.interfaces.ThermalCapacitance.class,
              edu.ie3.util.quantities.PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN,
              "Cannot parse ScreenLayerInput: missing thermalCapacitance in " + json);

      ComparableQuantity<javax.measure.quantity.Area> area = null;
      if (node.has("area") && !node.get("area").isNull()) {
        String a = node.get("area").asText();
        if (a != null && !a.isBlank() && !"null".equalsIgnoreCase(a)) {
          try {
            area =
                Quantities.getQuantity(
                        Double.parseDouble(a),
                        edu.ie3.util.quantities.PowerSystemUnits.SQUARE_MILLIMETRE)
                    .asType(javax.measure.quantity.Area.class);
          } catch (NumberFormatException nfe) {
            throw new ParsingException(
                "Cannot parse ScreenLayerInput: invalid area value in " + json, nfe);
          }
        }
      }

      int wiresNumber;
      if (node.has("wiresNumber") && !node.get("wiresNumber").isNull()) {
        try {
          wiresNumber = node.get("wiresNumber").asInt();
        } catch (Exception e) {
          throw new ParsingException(
              "Cannot parse ScreenLayerInput: invalid wiresNumber in " + json, e);
        }
      } else {
        throw new ParsingException("Cannot parse ScreenLayerInput: missing wiresNumber in " + json);
      }

      ComparableQuantity<javax.measure.quantity.Length> wireDiameter =
          parseQuantityField(
              node,
              "wireDiameter",
              javax.measure.quantity.Length.class,
              edu.ie3.util.quantities.PowerSystemUnits.MILLIMETRE,
              "Cannot parse ScreenLayerInput: missing wireDiameter in " + json);

      ComparableQuantity<javax.measure.quantity.Length> lengthOfLay = null;
      if (node.has("lengthOfLay") && !node.get("lengthOfLay").isNull()) {
        String ll = node.get("lengthOfLay").asText();
        if (ll != null && !ll.isBlank() && !"null".equalsIgnoreCase(ll)) {
          try {
            lengthOfLay =
                Quantities.getQuantity(
                        Double.parseDouble(ll), edu.ie3.util.quantities.PowerSystemUnits.MILLIMETRE)
                    .asType(javax.measure.quantity.Length.class);
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
                  edu.ie3.util.quantities.PowerSystemUnits.OHM_METRE,
                  "Cannot parse ScreenLayerInput: missing electricalResistivity in " + json);

      return new ScreenLayerInput(
          uuid,
          id,
          material,
          innerDiameter,
          outerDiameter,
          thermalResistivity,
          thermalCapacitance,
          java.util.Optional.ofNullable(area),
          wiresNumber,
          wireDiameter,
          java.util.Optional.ofNullable(lengthOfLay),
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

      if (node.isObject() && (!node.has("uuid") || node.get("uuid").isNull())) {
        ((ObjectNode) node).put("uuid", java.util.UUID.randomUUID().toString());
      }

      java.util.UUID uuid = java.util.UUID.fromString(node.get("uuid").asText());
      String id = resolveId(node, new String[] {"id", "name"});
      if (id == null)
        throw new ParsingException("Cannot parse ConductorInput: missing id in " + json);

      CableMaterial material;
      try {
        material =
            CableMaterial.valueOf(
                node.has("material") && !node.get("material").isNull()
                    ? node.get("material").asText()
                    : null);
      } catch (Exception e) {
        String mat =
            (node.has("material") && !node.get("material").isNull())
                ? node.get("material").asText()
                : "null";
        throw new ParsingException("Cannot parse ConductorInput: invalid material: " + mat, e);
      }

      ComparableQuantity<javax.measure.quantity.Area> crossSection =
          parseQuantityField(
              node,
              "crossSection",
              javax.measure.quantity.Area.class,
              edu.ie3.util.quantities.PowerSystemUnits.SQUARE_MILLIMETRE,
              "Cannot parse ConductorInput: missing crossSection in " + json);

      ComparableQuantity<javax.measure.quantity.Length> diameter =
          parseQuantityField(
              node,
              "diameter",
              javax.measure.quantity.Length.class,
              edu.ie3.util.quantities.PowerSystemUnits.MILLIMETRE,
              "Cannot parse ConductorInput: missing diameter in " + json);

      boolean isCompacted =
          node.has("isCompacted")
              && !node.get("isCompacted").isNull()
              && node.get("isCompacted").asBoolean(false);

      ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalResistivity> thermalResistivity =
          parseQuantityField(
              node,
              "thermalResistivity",
              edu.ie3.util.quantities.interfaces.ThermalResistivity.class,
              edu.ie3.util.quantities.PowerSystemUnits.KELVIN_METRE_PER_WATT,
              "Cannot parse ConductorInput: missing thermalResistivity in " + json);

      ComparableQuantity<edu.ie3.util.quantities.interfaces.ThermalCapacitance> thermalCapacitance =
          parseQuantityField(
              node,
              "thermalCapacitance",
              edu.ie3.util.quantities.interfaces.ThermalCapacitance.class,
              edu.ie3.util.quantities.PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN,
              "Cannot parse ConductorInput: missing thermalCapacitance in " + json);

      ComparableQuantity<javax.measure.quantity.Area> area = null;
      if (node.has("area") && !node.get("area").isNull()) {
        String a = node.get("area").asText();
        if (a != null && !a.isBlank() && !"null".equalsIgnoreCase(a)) {
          try {
            area =
                Quantities.getQuantity(
                        Double.parseDouble(a),
                        edu.ie3.util.quantities.PowerSystemUnits.SQUARE_MILLIMETRE)
                    .asType(javax.measure.quantity.Area.class);
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
    if (!node.has("uuid") || node.get("uuid").isNull())
      node.put("uuid", java.util.UUID.randomUUID().toString());
  }

  private String resolveId(JsonNode node, String[] candidates) {
    for (String c : candidates) {
      if (node.has(c) && !node.get(c).isNull()) return node.get(c).asText();
    }
    return null;
  }

  @SuppressWarnings("unchecked")
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
