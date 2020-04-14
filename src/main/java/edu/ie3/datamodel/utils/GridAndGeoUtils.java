/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.util.geo.GeoUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.locationtech.jts.geom.*;

/** This class offers some useful methods for handling geographical problems related to grids */
public class GridAndGeoUtils extends GeoUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private GridAndGeoUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  /**
   * Builds a straight line string between the both nodes
   *
   * @param a Starting point of the line string
   * @param b Ending point of the line string
   * @return The equivalent straight line string
   */
  public static LineString buildLineStringBetweenNodes(NodeInput a, NodeInput b) {
    return DEFAULT_GEOMETRY_FACTORY.createLineString(
        ArrayUtils.addAll(
            a.getGeoPosition().getCoordinates(), b.getGeoPosition().getCoordinates()));
  }
}
