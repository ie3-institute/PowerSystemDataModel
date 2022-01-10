/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.util.geo.GeoUtils;
import javax.measure.quantity.Length;
import org.locationtech.jts.geom.LineString;
import tech.units.indriya.ComparableQuantity;

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
   * @deprecated Use {@link #buildSafeLineStringBetweenNodes(NodeInput, NodeInput)} instead
   */
  @Deprecated(since = "1.1.0", forRemoval = true)
  public static LineString buildLineStringBetweenNodes(NodeInput a, NodeInput b) {
    return buildSafeLineStringBetweenPoints(a.getGeoPosition(), b.getGeoPosition());
  }

  /**
   * Builds a straight line string between the both nodes that can be compared safely even if the
   * two provided nodes contain exactly equal coordinates
   *
   * @param a Starting point of the line string
   * @param b Ending point of the line string
   * @return The equivalent straight line string
   */
  public static LineString buildSafeLineStringBetweenNodes(NodeInput a, NodeInput b) {
    return buildSafeLineStringBetweenPoints(a.getGeoPosition(), b.getGeoPosition());
  }

  /**
   * Calculates the distance between two {@link NodeInput} entities using {@link
   * #calcHaversine(double, double, double, double)}
   *
   * @param nodeA start node
   * @param nodeB end node
   * @return distance between start node and end node
   */
  public static ComparableQuantity<Length> distanceBetweenNodes(NodeInput nodeA, NodeInput nodeB) {
    return calcHaversine(
        nodeA.getGeoPosition().getY(),
        nodeA.getGeoPosition().getX(),
        nodeB.getGeoPosition().getY(),
        nodeB.getGeoPosition().getX());
  }
}
