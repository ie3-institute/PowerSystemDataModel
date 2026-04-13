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
 * Service's COSMO model
 */
public class CosmoIdCoordinateFactory extends IdCoordinateFactory {

  public CosmoIdCoordinateFactory() {
    super(IdCoordinateInput.CosmoIdCoordinateInput.class);
  }

  @Override
  protected IdCoordinateInput buildModel(SimpleFactoryData data) {
    int coordinateId = data.getInt(COORDINATE_ID);
    double lat = data.getDouble(LAT_GEO);
    double lon = data.getDouble(LONG_GEO);
    return new IdCoordinateInput(coordinateId, lat, lon);
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
