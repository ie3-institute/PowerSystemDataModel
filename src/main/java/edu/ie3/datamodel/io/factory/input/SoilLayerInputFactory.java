/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import static edu.ie3.util.quantities.PowerSystemUnits.METRE;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.UniqueEntityFactory;
import edu.ie3.datamodel.models.input.SoilLayer;
import java.util.UUID;
import javax.measure.quantity.Length;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import tech.units.indriya.ComparableQuantity;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

/** Factory for building {@link SoilLayer} instances from raw entity data. */
public class SoilLayerInputFactory extends UniqueEntityFactory<SoilLayer, EntityData> {
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
  private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

  public SoilLayerInputFactory() {
    super(SoilLayer.class);
  }

  @Override
  protected SoilLayer buildModel(EntityData data) {
    UUID uuid = data.getUUID(UUID);
    String geoJson = data.getField(GEOMETRY);
    Geometry geometry = parseGeoJsonToPolygon(geoJson);
    ComparableQuantity<Length> zFrom = data.getQuantity(Z_FROM, METRE);
    ComparableQuantity<Length> zTo = data.getQuantity(Z_TO, METRE);
    UUID soilType = data.getUUID(SOIL_TYPE);

    return new SoilLayer(uuid, geometry, zFrom, zTo, soilType, data.getFieldsToValues());
  }

  /**
   * Parses a GeoJSON polygon string into a {@link Geometry}.
   *
   * @param geoJson a GeoJSON document describing a closed polygon
   * @return the parsed geometry
   * @throws FactoryException if the GeoJSON is missing a type, is not a polygon, has malformed
   *     coordinates or is not closed
   */
  static Geometry parseGeoJsonToPolygon(String geoJson) {
    try {
      JsonNode document = JSON_MAPPER.readTree(geoJson);
      JsonNode type = document == null ? null : document.get("type");
      if (type == null || !type.isString()) {
        throw new FactoryException("Invalid GeoJSON: missing type: " + geoJson);
      }
      if (!"polygon".equalsIgnoreCase(type.asString())) {
        throw new FactoryException("Unsupported GeoJSON type: " + type.asString());
      }
      JsonNode coordinates = document.get("coordinates");
      JsonNode exteriorRing = coordinates == null ? null : coordinates.get(0);
      if (exteriorRing == null || !exteriorRing.isArray() || exteriorRing.isEmpty()) {
        throw new FactoryException("Expected polygon coordinates: " + geoJson);
      }
      Coordinate[] points = new Coordinate[exteriorRing.size()];
      for (int i = 0; i < exteriorRing.size(); i++) {
        JsonNode point = exteriorRing.get(i);
        if (point == null
            || !point.isArray()
            || point.size() < 2
            || !point.get(0).isNumber()
            || !point.get(1).isNumber()) {
          throw new FactoryException("Invalid polygon coordinate: " + geoJson);
        }
        points[i] = new Coordinate(point.get(0).asDouble(), point.get(1).asDouble());
      }
      if (!points[0].equals2D(points[points.length - 1])) {
        throw new FactoryException("Expected closed polygon of soil layer: " + geoJson);
      }
      return GEOMETRY_FACTORY.createPolygon(points);
    } catch (FactoryException e) {
      throw e;
    } catch (Exception e) {
      throw new FactoryException("Unable to parse geometry from GeoJSON: " + geoJson, e);
    }
  }
}
