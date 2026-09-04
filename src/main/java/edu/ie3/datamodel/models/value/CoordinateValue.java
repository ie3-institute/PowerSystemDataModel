/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import org.locationtech.jts.geom.Point;

public class CoordinateValue implements Value {
  public int id;

  public Point coordinate;

  public CoordinateValue(int id, Point coordinate) {
    this.id = id;
    this.coordinate = coordinate;
  }
}
