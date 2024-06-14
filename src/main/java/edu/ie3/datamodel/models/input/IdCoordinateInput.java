/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import edu.ie3.util.geo.GeoUtils;
import org.locationtech.jts.geom.Point;

public record IdCoordinateInput(Integer id, Point point) implements InputEntity {
  /**
   * Constructor for an {@link IdCoordinateInput}.
   *
   * @param id of the pair
   * @param lat of the pair
   * @param lon of the pair
   */
  public IdCoordinateInput(Integer id, double lat, double lon) {
    this(id, GeoUtils.buildPoint(lat, lon));
  }
}
