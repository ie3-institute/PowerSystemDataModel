/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import java.util.Collection;
import org.locationtech.jts.geom.Point;

public interface IdCoordinateSource extends DataSource {

  Point getCoordinate(Integer id);

  Collection<Point> getCoordinates(Integer... ids);

  Integer getId(Point coordinate);
}
