/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.SimpleFactoryData;
import edu.ie3.datamodel.models.input.IdCoordinateInput;

/**
 * Factory, that is able to build coordinate id to coordinate mapping from German Federal Weather
 * Service's ICON model
 */
public class IconIdCoordinateFactory extends IdCoordinateFactory {

  public IconIdCoordinateFactory() {
    super(IdCoordinateInput.IconIdCoordinateInput.class);
  }

  @Override
  protected IdCoordinateInput buildModel(SimpleFactoryData data) {
    int coordinateId = data.getInt(COORDINATE_ID);
    double lat = data.getDouble(LAT);
    double lon = data.getDouble(LONG);
    return new IdCoordinateInput(coordinateId, lat, lon);
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
