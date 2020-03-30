/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

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
  public static Point xyCoordToPoint(double x, double y) {
    Coordinate coordinate = new Coordinate(x, y, 0);
    return geometryFactory.createPoint(coordinate);
  }
}
