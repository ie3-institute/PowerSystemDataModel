/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import edu.ie3.datamodel.exceptions.ParsingException;
import java.io.Serializable;
import java.util.Arrays;

public interface ControlStrategy extends Serializable {
  String getKey();

  static ControlStrategy parse(String key) throws ParsingException {
    if (key == null || key.isEmpty())
      return ControlStrategy.DefaultControlStrategies.NO_CONTROL_STRATEGY;

    String filterKey = key.toLowerCase().replaceAll("-", "_");
    return Arrays.stream(EmControlStrategy.values())
        .filter(profile -> profile.getKey().equals(filterKey))
        .findFirst()
        .orElseThrow(
            () -> new ParsingException("Cannot parse \"" + key + "\" to a valid control strategy"));
  }

  enum DefaultControlStrategies implements ControlStrategy {
    NO_CONTROL_STRATEGY;

    @Override
    public String getKey() {
      return "No control strategy assigned";
    }
  }
}
