/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory;

import java.util.Map;

/** Simple class, that holds a mapping from key to value. */
public class SimpleFactoryData extends FactoryData {
  public SimpleFactoryData(Map<String, String> fieldsToAttributes, Class<?> targetClass) {
    super(fieldsToAttributes, targetClass);
  }
}
