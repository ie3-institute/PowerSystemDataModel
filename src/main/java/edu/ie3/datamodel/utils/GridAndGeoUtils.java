/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.util.geo.GeoUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.measure.quantity.Length;
import org.apache.commons.lang3.ArrayUtils;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;
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
  @Deprecated
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
   * Build an instance of {@link LineString} between two points that is safe to be compared even if
   * the provided two points consist of exactly the same coordinates. This is done by increasing the
   * coordinate of the provided Point {@code p1} by a small amount to make it different from Point
   * {@code p2}. For details on the bug inside {@link LineString} that is addressed here, see
   * https://github.com/locationtech/jts/issues/531
   *
   * @param p1 start point of the linestring
   * @param p2 end point of the linestring
   * @return a {@link LineString} between the provided points
   */
  public static LineString buildSafeLineStringBetweenPoints(final Point p1, final Point p2) {
    final Point safePoint1 = p1.equals(p2) ? buildSafePoint(p1) : p1;
    return DEFAULT_GEOMETRY_FACTORY.createLineString(
        ArrayUtils.addAll(safePoint1.getCoordinates(), p2.getCoordinates()));
  }

  /**
   * Build an instance of {@link LineString} between two coordinates that is safe to be compared
   * even if the provided two coordinates are exactly the same coordinates. This is done by
   * increasing the coordinate of the provided Point {@code c1} by a small amount to make it
   * different from Point {@code c2}. For details on the bug inside {@link LineString} that is
   * addressed here, see https://github.com/locationtech/jts/issues/531
   *
   * @param c1 start coordinate of the linestring
   * @param c2 end coordinate of the linestring
   * @return A safely build line string
   */
  public static LineString buildSafeLineStringBetweenCoords(
      final Coordinate c1, final Coordinate c2) {
    final Coordinate safeCoord1 = c1.equals(c2) ? buildSafeCoord(c1) : c1;
    return DEFAULT_GEOMETRY_FACTORY.createLineString(
        ArrayUtils.addAll(new Coordinate[] {safeCoord1}, c2));
  }

  /**
   * Convert a given {@link LineString} with at least two points into a 'safe to be compared' {@link
   * LineString} This is done by removing duplicates in the points in the provided linestring as
   * well as a small change of the start coordinate if the linestring only consists of two
   * coordinates. For details on the bug inside {@link LineString} that is addressed here, see
   * https://github.com/locationtech/jts/issues/531
   *
   * @param lineString the linestring that should be checked and maybe converted to a 'safe to be
   *     compared' linestring
   * @return a 'safe to be compared' linestring
   */
  public static LineString buildSafeLineString(LineString lineString) {
    if (lineString.getCoordinates().length == 2) {
      return buildSafeLineStringBetweenPoints(lineString.getStartPoint(), lineString.getEndPoint());
    } else {
      // rebuild line with unique points
      Set<Coordinate> uniqueCoords = new HashSet<>(Arrays.asList(lineString.getCoordinates()));
      return uniqueCoords.size() == 1
          ? buildSafeLineStringBetweenPoints(lineString.getStartPoint(), lineString.getEndPoint())
          : DEFAULT_GEOMETRY_FACTORY.createLineString(uniqueCoords.toArray(new Coordinate[0]));
    }
  }

  /**
   * Adapted {@link Coordinate#x}, {@link Coordinate#y} and {@link Coordinate#z} of the provided
   * {@link Coordinate} by 1e-13 and return a new, adapted instance of {@link Coordinate}
   *
   * @param coord the coordinate that should be adapted
   * @return the adapted coordinate with slightly changed x,y,z values
   */
  private static Coordinate buildSafeCoord(Coordinate coord) {

    double modVal = 1e-13;
    double p1X = coord.getX() + modVal;
    double p1Y = coord.getY() + modVal;
    double p1Z = coord.getZ() + modVal;

    return new Coordinate(p1X, p1Y, p1Z);
  }

  /**
   * Adapt the provided point as described in {@link #buildSafeCoord(Coordinate)} and return a new,
   * adapted instance of {@link Point}
   *
   * @param p1 the point that should be adapted
   * @return the adapted point with a slightly changed coordinate
   */
  private static Point buildSafePoint(Point p1) {

    Coordinate[] safeCoord = new Coordinate[] {buildSafeCoord(p1.getCoordinate())};
    CoordinateArraySequence safeCoordSeq = new CoordinateArraySequence(safeCoord);

    return new Point(safeCoordSeq, p1.getFactory());
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
