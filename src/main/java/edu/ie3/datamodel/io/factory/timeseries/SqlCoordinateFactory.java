/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

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

public class SqlCoordinateFactory extends IdCoordinateFactory {
  private static final String COORDINATE_ID = "id";
  private static final String COORDINATE = "coordinate";

  @Override
  protected Pair<Integer, Point> buildModel(SimpleFactoryData data) {
    int coordinateId = data.getInt(COORDINATE_ID);
    byte[] arr = WKBReader.hexToBytes(data.getField(COORDINATE));

    Coordinate coordinate;

    try {
      WKBReader reader = new WKBReader();
      coordinate = reader.read(arr).getCoordinate();
    } catch (ParseException e) {
      throw new RuntimeException(e);
    }

    Point point = GeoUtils.buildPoint(coordinate);
    return Pair.of(coordinateId, point);
  }

  @Override
  protected List<Set<String>> getFields(SimpleFactoryData data) {
    return Collections.singletonList(newSet(COORDINATE_ID, COORDINATE));
  }

  @Override
  public String getIdField() {
    return COORDINATE_ID;
  }

  @Override
  public String getLatField() {
    return null;
  }

  @Override
  public String getLonField() {
    return null;
  }

  /** @return the field id for the coordinates */
  public String getCoordinateField() {
    return COORDINATE;
  }
}
