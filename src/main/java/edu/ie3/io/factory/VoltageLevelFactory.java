/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.models.GermanVoltageLevel;
import edu.ie3.models.VoltageLevel;

public class VoltageLevelFactory {
  private VoltageLevelFactory() {}

  public static VoltageLevel parseVoltageLvl(String value) {
    switch (value) {
      case "lv":
        return GermanVoltageLevel.LV;
      case "mv":
        return GermanVoltageLevel.MV;
      case "hv":
        return GermanVoltageLevel.HV;
      case "ehv":
        return GermanVoltageLevel.EHV;
      default:
        throw new FactoryException("Unrecognized voltage level: " + value);
    }
  }
}
