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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

/**
 * Parses the JSON representations of the individual cable components ({@link LayerInput}, {@link
 * ScreenLayerInput} and {@link ConductorInput}) that are stored as embedded JSON strings within a
 * {@link edu.ie3.datamodel.models.input.connector.type.CableTypeInput}.
 */
public class CableTypeParser {
  private static final Logger log = LoggerFactory.getLogger(CableTypeParser.class);

  private final ObjectMapper mapper;

  /**
   * Creates a new parser using the given JSON mapper.
   *
   * @param mapper JSON mapper used to read the embedded JSON strings; must not be {@code null}
   */
  public CableTypeParser(ObjectMapper mapper) {
    this.mapper = Objects.requireNonNull(mapper);
  }

  /**
   * Parses a JSON array of {@link LayerInput} objects.
   *
   * @param json the JSON array string, or {@code null} / blank to produce an empty list
   * @return an immutable list of parsed layers (never {@code null})
   * @throws ParsingException if the JSON cannot be interpreted as a list of layers
   */
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

  /**
   * Parses a JSON object into a {@link ScreenLayerInput}.
   *
   * @param json the JSON string; {@code null} or blank returns {@code null}
   * @return the parsed screen layer, or {@code null} when the input is empty
   * @throws ParsingException if the JSON cannot be interpreted as a screen layer
   */
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

  /**
   * Parses a JSON object into a {@link ConductorInput}.
   *
   * @param json the JSON string; {@code null} or blank returns {@code null}
   * @return the parsed conductor, or {@code null} when the input is empty
   * @throws ParsingException if the JSON cannot be interpreted as a conductor
   */
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

  /**
   * Ensures the given node is a JSON object, otherwise a parsing error is reported.
   *
   * @param node node to validate
   * @param context logical name used in the error message
   * @param source original source used in the error message
   * @return the node cast to {@link ObjectNode}
   * @throws ParsingException if the node is {@code null} or not an object
   */
  private ObjectNode requireObject(JsonNode node, String context, Object source)
      throws ParsingException {
    if (node == null || !node.isObject()) {
      throw new ParsingException("Cannot parse " + context + ": expected object in " + source);
    }
    return (ObjectNode) node;
  }

  /**
   * Locates the screen-layer node inside a possibly wrapped object by looking for the first node
   * that carries a non-null material field.
   *
   * @param node candidate root node
   * @param source original source used in the error message
   * @return the screen-layer node
   * @throws ParsingException if no child carries a material field
   */
  private ObjectNode findScreenNode(ObjectNode node, String source) throws ParsingException {
    if (hasMaterial(node)) return node;

    for (JsonNode child : node) {
      if (child != null && child.isObject() && hasMaterial(child)) {
        return (ObjectNode) child;
      }
    }

    throw new ParsingException("Cannot parse " + SCREEN_LAYER + ": missing material in " + source);
  }

  /**
   * Returns whether the given node contains a non-null {@code material} field.
   *
   * @param node node to inspect
   * @return {@code true} when a usable material field is present
   */
  private boolean hasMaterial(JsonNode node) {
    return node.has(MATERIAL) && !node.get(MATERIAL).isNull();
  }

  /**
   * Reads and parses the {@code uuid} field of a node, generating and inserting a fresh UUID when
   * the field is absent or null.
   *
   * @param node node to read from
   * @param context logical name used in the error message
   * @return the parsed (or newly generated) UUID
   * @throws ParsingException if the stored value is not a valid UUID
   */
  private UUID parseUuid(ObjectNode node, String context) throws ParsingException {
    ensureUuid(node);
    try {
      return java.util.UUID.fromString(node.get(UUID).asString());
    } catch (IllegalArgumentException e) {
      throw new ParsingException("Cannot parse " + context + ": invalid uuid in " + node, e);
    }
  }

  /**
   * Resolves the identifier of a node from the first non-empty candidate among the given field
   * names (typically {@code id}, then {@code name}).
   *
   * @param node node to read from
   * @param context logical name used in the error message
   * @return the resolved identifier
   * @throws ParsingException if none of the candidates is set
   */
  private String parseId(JsonNode node, String context) throws ParsingException {
    String id = resolveId(node, new String[] {ID, NAME});
    if (id == null) {
      throw new ParsingException("Cannot parse " + context + ": missing id in " + node);
    }
    return id;
  }

  /**
   * Reads an optional quantity field from a node, returning {@code null} when the field is absent,
   * null, blank, or the literal string {@code "null"}.
   *
   * @param <T> the {@link Quantity} sub-type to build
   * @param node node to read from
   * @param fieldName name of the JSON field
   * @param quantityClass concrete quantity class to build
   * @param unit unit used to build the quantity
   * @param context logical name used in the error message
   * @return the parsed quantity, or {@code null} when the field is not present
   * @throws ParsingException if the value cannot be parsed as a number
   */
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

  /**
   * Reads an optional text field from a node, returning {@code null} when the field is absent,
   * null, blank, or the literal string {@code "null"}.
   *
   * @param node node to read from
   * @param fieldName name of the JSON field
   * @return the raw text value, or {@code null} when not meaningfully present
   */
  private String optionalText(JsonNode node, String fieldName) {
    if (!node.has(fieldName) || node.get(fieldName).isNull()) return null;

    String value = node.get(fieldName).asString();
    return value == null || value.isBlank() || "null".equalsIgnoreCase(value) ? null : value;
  }

  /**
   * Parses the {@code material} field into a {@link CableMaterial}.
   *
   * @param node node to read from
   * @param context logical name used in the error message
   * @return the parsed cable material
   * @throws ParsingException if the material is missing or unknown
   */
  private CableMaterial parseMaterial(JsonNode node, String context) throws ParsingException {
    try {
      String mat = optionalText(node, MATERIAL);
      return CableMaterial.valueOf(mat);
    } catch (Exception e) {
      String mat = optionalText(node, MATERIAL);
      throw new ParsingException("Cannot parse " + context + ": invalid material: " + mat, e);
    }
  }

  /**
   * Reads a required quantity field from a node.
   *
   * @param <T> the {@link Quantity} sub-type to build
   * @param node node to read from
   * @param fieldName name of the JSON field
   * @param quantityClass concrete quantity class to build
   * @param unit unit used to build the quantity
   * @param missingMessage message used when the field is missing
   * @return the parsed quantity
   * @throws ParsingException if the field is missing or not a valid number
   */
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

  /**
   * Unwraps a JSON string that itself encodes another JSON document; non-string nodes are returned
   * unchanged.
   *
   * @param node node to possibly unwrap
   * @return the (possibly unwrapped) node
   */
  private JsonNode unwrapTextual(JsonNode node) {
    if (node != null && node.isString()) {
      try {
        return mapper.readTree(node.asString());
      } catch (Exception e) {
        log.error("Failed to unwrap textual JSON node: {}", node.asString(), e);
        throw new IllegalStateException("Failed to unwrap textual JSON node: " + node.asString(), e);
      }
    }
    return node;
  }

  /**
   * Ensures the node carries a {@code uuid} field, generating and storing a random UUID when the
   * field is absent or null.
   *
   * @param node node to inspect (mutated when a UUID must be generated)
   */
  private void ensureUuid(ObjectNode node) {
    if (!node.has(UUID) || node.get(UUID).isNull()) {
      node.put(UUID, java.util.UUID.randomUUID().toString());
    }
  }

  /**
   * Resolves the first non-empty text field among the given candidate names.
   *
   * @param node node to read from
   * @param candidates candidate field names, in the order to try
   * @return the first non-empty value, or {@code null} when none is present
   */
  private String resolveId(JsonNode node, String[] candidates) {
    for (String candidate : candidates) {
      String value = optionalText(node, candidate);
      if (value != null) return value;
    }
    return null;
  }

  /**
   * Parses a required integer field.
   *
   * @param text raw text value
   * @param fieldName field name used in the error message
   * @param context logical name used in the error message
   * @param source original source used in the error message
   * @return the parsed integer
   * @throws ParsingException if the text is not a valid integer
   */
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
