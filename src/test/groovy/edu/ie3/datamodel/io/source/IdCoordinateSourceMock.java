/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.util.geo.CoordinateDistance;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javax.measure.quantity.Length;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;

public class IdCoordinateSourceMock implements IdCoordinateSource {
  @Override
  public Optional<Point> getCoordinate(int id) {
    return Optional.empty();
  }

  @Override
  public Collection<Point> getCoordinates(int... ids) {
    return Collections.emptyList();
  }

  @Override
  public Optional<Integer> getId(Point coordinate) {
    return Optional.empty();
  }

  @Override
  public Collection<Point> getAllCoordinates() {
    return Collections.emptyList();
  }

  @Override
  public List<CoordinateDistance> getNearestCoordinates(Point coordinate, int n) {
    return Collections.emptyList();
  }

  @Override
  public List<CoordinateDistance> getNearestCoordinates(
      Point coordinate, int n, ComparableQuantity<Length> distance) {
    return Collections.emptyList();
  }
}
