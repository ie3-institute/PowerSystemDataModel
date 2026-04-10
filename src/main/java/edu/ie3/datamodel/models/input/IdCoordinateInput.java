/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import edu.ie3.util.geo.GeoUtils;
import java.util.Map;
import org.locationtech.jts.geom.Point;

public sealed class IdCoordinateInput implements InputEntity {

  private final int id;

  private final Point point;

  public IdCoordinateInput(int id, Point point) {
    this.id = id;
    this.point = point;
  }

  public int id() {
    return id;
  }

  public Point point() {
    return point;
  }

  @Override
  public Map<String, String> getAdditionalInformation() {
    return Map.of();
  }

  /**
   * Constructor for an {@link IdCoordinateInput}.
   *
   * @param id of the pair
   * @param lat of the pair
   * @param lon of the pair
   */
  public IdCoordinateInput(int id, double lat, double lon) {
    this(id, GeoUtils.buildPoint(lat, lon));
  }

  // for differencing between different sources
  private IdCoordinateInput() {
    this(-1, NodeInput.DEFAULT_GEO_POSITION);
  }

  public static final class CosmoIdCoordinateInput extends IdCoordinateInput {}

  public static final class IconIdCoordinateInput extends IdCoordinateInput {}

  public static final class SqlIdCoordinateInput extends IdCoordinateInput {}
}
