/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import java.util.Collection;
import org.locationtech.jts.geom.Point;

public interface CoordinateSource extends DataSource {

  Point getCoordinate(Integer id);

  Collection<Point> getCoordinates(Integer... ids);

  Collection<Point> getCoordinatesBetween(Integer fromId, Integer toId);

  Integer getId(Point coordinate);
}
