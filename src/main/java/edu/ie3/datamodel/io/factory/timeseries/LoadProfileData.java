/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.io.factory.FactoryData;
import edu.ie3.datamodel.models.value.load.LoadValues;
import java.util.Map;

public class LoadProfileData<V extends LoadValues> extends FactoryData {
  public LoadProfileData(Map<String, String> fieldsToAttributes, Class<V> targetClass) {
    super(fieldsToAttributes, targetClass);
  }
}
