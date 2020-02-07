/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.utils;

import com.vividsolutions.jts.geom.*;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class CoordinateUtils {

  public static final int SPATIAL_REFERENCE_ID = 4326;
  private static GeometryFactory geometryFactory;

  /** Private Constructor as this class is not meant to be instantiated */
  private CoordinateUtils() {
    throw new IllegalStateException("This is an Utility Class and not meant to be instantiated");
  }

  static {
    geometryFactory =
        new GeometryFactory(
            new PrecisionModel(PrecisionModel.FLOATING_SINGLE), SPATIAL_REFERENCE_ID);
  }

  /**
   * Wraps XY values in a JTS geometry point
   *
   * @param x latitude value
   * @param y longitude value
   * @return JTS geometry Point
   */
  public static Point xyCoordToPoint(Double x, Double y) {
    if(x==null || y==null) return null;
    Coordinate coordinate = new Coordinate(x, y, 0);
    return geometryFactory.createPoint(coordinate);
  }

  public static LineString stringToLineString(String str) {
    if(str == null || str.isEmpty()) return null;
    WKTReader reader = new WKTReader();
    Geometry geometry;
    try {
      geometry = reader.read(str);
    } catch (ParseException e) {
      e.printStackTrace();
      return null;
    }
    CoordinateSequence coordinateSequence = geometryFactory.getCoordinateSequenceFactory().create(geometry.getCoordinates());
    return new LineString(coordinateSequence, geometryFactory);
  }
}
