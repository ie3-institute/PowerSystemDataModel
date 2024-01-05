/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.io.factory.SimpleFactoryData;
import edu.ie3.util.geo.GeoUtils;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKBReader;

public class SqlIdCoordinateFactory extends IdCoordinateFactory {
  private static final String COORDINATE_ID = "id";
  private static final String COORDINATE = "coordinate";
  private final WKBReader reader = new WKBReader();

  @Override
  protected Pair<Integer, Point> buildModel(SimpleFactoryData data) {
    try {
      int coordinateId = data.getInt(COORDINATE_ID);
      byte[] byteArr = WKBReader.hexToBytes(data.getField(COORDINATE));

      Coordinate coordinate = reader.read(byteArr).getCoordinate();

      Point point = GeoUtils.buildPoint(coordinate);
      return Pair.of(coordinateId, point);

    } catch (ParseException e) {
      throw new FactoryException(e);
    }
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    return Collections.singletonList(newSet(COORDINATE_ID, COORDINATE));
  }

  @Override
  public String getIdField() {
    return COORDINATE_ID;
  }

  @Override
  public String getLatField() {
    throw new UnsupportedOperationException(
        "This is not supported by " + SqlIdCoordinateFactory.class + "!");
  }

  @Override
  public String getLonField() {
    throw new UnsupportedOperationException(
        "this is not supported by " + SqlIdCoordinateFactory.class + "!");
  }

  public String getCoordinateField() {
    return COORDINATE;
  }
}
