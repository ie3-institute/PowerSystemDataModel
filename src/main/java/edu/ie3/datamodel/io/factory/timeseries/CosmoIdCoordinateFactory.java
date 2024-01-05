/*
 * © 2021. TU Dortmund University,
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
import org.locationtech.jts.geom.Point;

/**
 * Factory, that is able to build coordinate id to coordinate mapping from German Federal Weather
 * Service's COSMO model
 */
public class CosmoIdCoordinateFactory extends IdCoordinateFactory {
  private static final String TID = "tid";
  private static final String COORDINATE_ID = "id";
  private static final String LONG_GEO = "longGeo";
  private static final String LAT_GEO = "latGeo";
  private static final String LONG_ROT = "longRot";
  private static final String LAT_ROT = "latRot";

  @Override
  protected Pair<Integer, Point> buildModel(SimpleFactoryData data) {
    int coordinateId = data.getInt(COORDINATE_ID);
    double lat = data.getDouble(LAT_GEO);
    double lon = data.getDouble(LONG_GEO);
    return Pair.of(coordinateId, GeoUtils.buildPoint(lat, lon));
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    return Collections.singletonList(
        newSet(TID, COORDINATE_ID, LONG_GEO, LAT_GEO, LONG_ROT, LAT_ROT));
  }

  @Override
  public String getIdField() {
    return COORDINATE_ID;
  }

  @Override
  public String getLatField() {
    return LAT_GEO;
  }

  @Override
  public String getLonField() {
    return LONG_GEO;
  }
}
