/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.Factory;
import edu.ie3.datamodel.io.factory.SimpleFactoryData;
import edu.ie3.datamodel.models.input.IdCoordinateInput;

/**
 * Abstract class definition for a factory, that is able to build single mapping entries from
 * coordinate identifier to actual coordinate
 */
public abstract class IdCoordinateFactory
    extends Factory<IdCoordinateInput, SimpleFactoryData, IdCoordinateInput> {
  protected IdCoordinateFactory() {
    super(IdCoordinateInput.class);
  }

  /** @return the field id for the coordinate id */
  public abstract String getIdField();

  /** @return the field id for the coordinate latitude */
  public abstract String getLatField();

  /** @return the field id for the coordinate longitude */
  public abstract String getLonField();
}
