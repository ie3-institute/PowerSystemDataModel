/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.util.geo.GeoUtils;
import java.util.Objects;
import javax.measure.quantity.Length;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;

/**
 * Wraps two coordinates with the distance between the first one and the second one, can be compared
 * by distance to another CoordinateDistance
 */
public class CoordinateDistance implements Comparable<CoordinateDistance> {
  private final Point coordinateA;
  private final Point coordinateB;
  private final ComparableQuantity<Length> distance;

  /**
   * @param coordinateA The first coordinate
   * @param coordinateB The second coordinate
   * @param distance The distance from A to B
   */
  public CoordinateDistance(
      Point coordinateA, Point coordinateB, ComparableQuantity<Length> distance) {
    this.coordinateA = coordinateA;
    this.coordinateB = coordinateB;
    this.distance = distance;
  }

  /**
   * Calculates the distance from the first to the second coordinate using {@link
   * GeoUtils#calcHaversine(double, double, double, double)}
   *
   * @param coordinateA The first coordinate
   * @param coordinateB The second coordinate
   */
  public CoordinateDistance(Point coordinateA, Point coordinateB) {
    this(
        coordinateA,
        coordinateB,
        GeoUtils.calcHaversine(
            coordinateA.getY(), coordinateA.getX(), coordinateB.getY(), coordinateB.getX()));
  }

  /** @return The first coordinate */
  public Point getCoordinateA() {
    return coordinateA;
  }

  /** @return The second coordinate */
  public Point getCoordinateB() {
    return coordinateB;
  }

  /** @return The distance from the first coordinate to the second coordinate in km */
  public ComparableQuantity<Length> getDistance() {
    return distance;
  }

  /**
   * Compares two coordinate distances on the length of the distance alone, thus having a natural
   * ordering that is inconsistent with equals
   *
   * @param that the distance to compare
   * @return a number lower than 0 if this has a lower distance than that, 0 if they are the same, a
   *     number higher than 0 if that has a lower distance
   */
  @Override
  public int compareTo(CoordinateDistance that) {
    return this.distance.compareTo(that.distance);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CoordinateDistance that = (CoordinateDistance) o;
    return coordinateA.equals(coordinateA)
        && coordinateB.equals(that.coordinateB)
        && distance.equals(that.distance);
  }

  @Override
  public int hashCode() {
    return Objects.hash(coordinateA, coordinateB, distance);
  }

  @Override
  public String toString() {
    return "CoordinateDistance{"
        + "coordinateA="
        + coordinateA
        + ", coordinateB="
        + coordinateB
        + ", distance="
        + distance
        + '}';
  }
}
