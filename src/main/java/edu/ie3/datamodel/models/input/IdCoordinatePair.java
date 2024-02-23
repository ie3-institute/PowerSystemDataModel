/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import edu.ie3.util.geo.GeoUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.locationtech.jts.geom.Point;

public class IdCoordinatePair extends ImmutablePair<Integer, Point> implements InputEntity {
  /**
   * Create a new pair instance.
   *
   * @param left the left value, may be null
   * @param right the right value, may be null
   */
  private IdCoordinatePair(Integer left, Point right) {
    super(left, right);
  }

  /**
   * Creates an {@link IdCoordinatePair}.
   *
   * @param id of the pair
   * @param lat of the pair
   * @param lon of the pair
   * @return a new {@link IdCoordinatePair}
   */
  public static IdCoordinatePair of(Integer id, double lat, double lon) {
    return new IdCoordinatePair(id, GeoUtils.buildPoint(lat, lon));
  }

  /**
   * Creates an {@link IdCoordinatePair}.
   *
   * @param id of the pair
   * @param point of the pair
   * @return a new {@link IdCoordinatePair}
   */
  public static IdCoordinatePair of(Integer id, Point point) {
    return new IdCoordinatePair(id, point);
  }
}
