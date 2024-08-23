/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.FactoryData;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry;
import java.util.Map;

public class LoadProfileData<E extends LoadProfileEntry> extends FactoryData {
  public LoadProfileData(Map<String, String> fieldsToAttributes, Class<E> targetClass) {
    super(fieldsToAttributes, targetClass);
  }
}
