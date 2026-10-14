/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.io.naming.FieldNamingStrategy.*;
import static edu.ie3.datamodel.models.StandardUnits.TEMPERATURE;
import static edu.ie3.util.quantities.PowerSystemUnits.*;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.SimpleFactoryData;
import edu.ie3.datamodel.models.input.grid.SoilLayer;
import edu.ie3.datamodel.models.input.grid.SoilType;
import edu.ie3.util.quantities.interfaces.ThermalCapacitance;
import edu.ie3.util.quantities.interfaces.ThermalResistivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/**
 * Parses soil related input entities (soil types and soil layers) from raw row data as provided by
 * the CSV and SQL sources. This class cannot be instantiated.
 */
public final class SoilDataParser {
  private static final Logger log = LoggerFactory.getLogger(SoilDataParser.class);
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
  private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

  private SoilDataParser() {}

  /**
   * Reads soil types from a stream of raw rows.
   *
   * @param rows a stream of maps mapping field names to their string values
   * @return an unmodifiable list of parsed soil types
   * @throws SourceException if any row cannot be parsed
   */
  public static List<SoilType> readSoilTypes(Stream<Map<String, String>> rows)
      throws SourceException {
    return readRows(rows, SoilDataParser::parseSoilType, "soil types");
  }

  /**
   * Reads soil layers from a stream of raw rows.
   *
   * @param rows a stream of maps mapping field names to their string values
   * @return an unmodifiable list of parsed soil layers
   * @throws SourceException if any row cannot be parsed
   */
  public static List<SoilLayer> readSoilLayers(Stream<Map<String, String>> rows)
      throws SourceException {
    return readRows(rows, SoilDataParser::parseSoilLayer, "soil layers");
  }

  /**
   * Consumes the given stream and parses every row with the provided row parser.
   *
   * @param <T> the type of entity to parse
   * @param rows a stream of raw rows
   * @param rowParser the parser applied to each row
   * @param sourceName a human readable name of the source used in error messages
   * @return an unmodifiable list of parsed entities
   * @throws SourceException if any row cannot be parsed
   */
  private static <T> List<T> readRows(
      Stream<Map<String, String>> rows, SoilRowParser<T> rowParser, String sourceName)
      throws SourceException {
    List<T> parsed = new ArrayList<>();
    try (rows) {
      var iterator = rows.iterator();
      int rowNumber = 0;
      while (iterator.hasNext()) {
        rowNumber++;
        try {
          parsed.add(rowParser.parse(iterator.next()));
        } catch (ParsingException e) {
          log.error("Failed to parse {} row {}", sourceName, rowNumber, e);
          throw new SourceException("Failed to parse " + sourceName + " row " + rowNumber, e);
        }
      }
      return List.copyOf(parsed);
    } catch (RuntimeException e) {
      log.error("Failed to read {}", sourceName, e);
      throw new SourceException("Failed to read " + sourceName, e);
    }
  }

  /**
   * Parses a single soil type from a raw row.
   *
   * @param data a map mapping field names to their string values
   * @return the parsed soil type
   * @throws ParsingException if a required field is missing, blank or malformed
   */
  public static SoilType parseSoilType(Map<String, String> data) throws ParsingException {
    SimpleFactoryData row = new SimpleFactoryData(data, SoilType.class);
    try {
      return new SoilType(
          row.getUUID(UUID),
          required(row, ID),
          row.getQuantity(THERMAL_RESISTIVITY_WET, KELVIN_METRE_PER_WATT)
              .asType(ThermalResistivity.class),
          row.getQuantity(THERMAL_RESISTIVITY_DRY, KELVIN_METRE_PER_WATT)
              .asType(ThermalResistivity.class),
          row.getQuantity(SPECIFIC_HEAT_CAPACITY, KILOWATTHOUR_PER_KELVIN_TIMES_CUBICMETRE)
              .asType(ThermalCapacitance.class),
          row.getQuantity(CRITICAL_TEMPERATURE_DIFFERENCE, TEMPERATURE));
    } catch (FactoryException e) {
      throw factoryParsingException("soil type", e);
    }
  }

  /**
   * Parses a single soil layer from a raw row.
   *
   * @param data a map mapping field names to their string values
   * @return the parsed soil layer
   * @throws ParsingException if a required field is missing, blank or malformed
   */
  public static SoilLayer parseSoilLayer(Map<String, String> data) throws ParsingException {
    SimpleFactoryData row = new SimpleFactoryData(data, SoilLayer.class);
    try {
      return new SoilLayer(
          row.getUUID(UUID),
          parseGeoJsonToGeometry(required(row, GEOMETRY)),
          row.getQuantity(Z_FROM, METRE),
          row.getQuantity(Z_TO, METRE),
          row.getUUID(SOIL_TYPE));
    } catch (FactoryException e) {
      throw factoryParsingException("soil layer", e);
    }
  }

  /**
   * Parses a GeoJSON polygon string into a {@link Geometry}.
   *
   * @param geoJson a GeoJSON document describing a closed polygon
   * @return the parsed geometry
   * @throws ParsingException if the GeoJSON is missing a type, is not a polygon, has malformed
   *     coordinates or is not closed
   */
  private static Geometry parseGeoJsonToGeometry(String geoJson) throws ParsingException {
    try {
      JsonNode document = JSON_MAPPER.readTree(geoJson);
      JsonNode type = document == null ? null : document.get("type");
      if (type == null || !type.isString()) {
        throw new ParsingException("Invalid GeoJSON: missing type: " + geoJson);
      }
      if (!"polygon".equalsIgnoreCase(type.asString())) {
        throw new ParsingException("Unsupported GeoJSON type: " + type.asString());
      }
      JsonNode coordinates = document.get("coordinates");
      JsonNode exteriorRing = coordinates == null ? null : coordinates.get(0);
      if (exteriorRing == null || !exteriorRing.isArray() || exteriorRing.isEmpty()) {
        throw new ParsingException("Expected polygon coordinates: " + geoJson);
      }
      Coordinate[] points = new Coordinate[exteriorRing.size()];
      for (int i = 0; i < exteriorRing.size(); i++) {
        JsonNode point = exteriorRing.get(i);
        if (point == null
            || !point.isArray()
            || point.size() < 2
            || !point.get(0).isNumber()
            || !point.get(1).isNumber()) {
          throw new ParsingException("Invalid polygon coordinate: " + geoJson);
        }
        points[i] = new Coordinate(point.get(0).asDouble(), point.get(1).asDouble());
      }
      if (!points[0].equals2D(points[points.length - 1])) {
        throw new ParsingException("Expected closed polygon of soil layer: " + geoJson);
      }
      return GEOMETRY_FACTORY.createPolygon(points);
    } catch (ParsingException e) {
      log.error("Unable to parse geometry from GeoJSON: {}", geoJson, e);
      throw e;
    } catch (Exception e) {
      log.error("Unable to parse geometry from GeoJSON: {}", geoJson, e);
      throw new ParsingException("Unable to parse geometry from GeoJSON: " + geoJson, e);
    }
  }

  /**
   * Reads a required, non-blank text field from a raw row.
   *
   * @param row the raw row
   * @param field the field name to read
   * @return the field value
   * @throws ParsingException if the field is missing or blank
   */
  private static String required(SimpleFactoryData row, String field) throws ParsingException {
    if (row.isFieldBlank(field)) {
      throw new ParsingException("Missing or empty field " + field + ".");
    }
    return row.getField(field);
  }

  /**
   * Builds a {@link ParsingException} from a {@link FactoryException}.
   *
   * @param entityName a human readable name of the entity that could not be parsed
   * @param e the underlying factory exception
   * @return a parsing exception carrying the factory exception as cause
   */
  private static ParsingException factoryParsingException(String entityName, FactoryException e) {
    return new ParsingException("Unable to parse " + entityName + ": " + e.getMessage(), e);
  }

  /** Parser for a single soil related raw row. */
  @FunctionalInterface
  private interface SoilRowParser<T> {
    T parse(Map<String, String> row) throws ParsingException;
  }
}
