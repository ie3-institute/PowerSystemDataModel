/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import org.locationtech.jts.geom.Point;

/** The type Coordinate value. */
public class CoordinateValue implements Value {
  /** The Id. */
  public final Integer id;

  /** The Coordinate. */
  public final Point coordinate;

  /**
   * Instantiates a new Coordinate value.
   *
   * @param id the id
   * @param coordinate the coordinate
   */
  public CoordinateValue(int id, Point coordinate) {
    this.id = id;
    this.coordinate = coordinate;
  }
}
