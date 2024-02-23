/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.SimpleFactoryData;
import edu.ie3.datamodel.models.input.IdCoordinatePair;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Factory, that is able to build coordinate id to coordinate mapping from German Federal Weather
 * Service's ICON model
 */
public class IconIdCoordinateFactory extends IdCoordinateFactory {
  private static final String COORDINATE_ID = "id";
  private static final String LONG = "longitude";
  private static final String LAT = "latitude";
  private static final String TYPE = "coordinateType";

  @Override
  protected IdCoordinatePair buildModel(SimpleFactoryData data) {
    int coordinateId = data.getInt(COORDINATE_ID);
    double lat = data.getDouble(LAT);
    double lon = data.getDouble(LONG);
    return IdCoordinatePair.of(coordinateId, lat, lon);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    return Collections.singletonList(newSet(COORDINATE_ID, LAT, LONG, TYPE));
  }

  @Override
  public String getIdField() {
    return COORDINATE_ID;
  }

  @Override
  public String getLatField() {
    return LAT;
  }

  @Override
  public String getLonField() {
    return LONG;
  }
}
