/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type.evcslocation;

import edu.ie3.datamodel.exceptions.ParsingException;
import java.util.HashMap;

public class EvcsLocationTypeUtils {

  private static final HashMap<String, EvcsLocationType> nameToType = initMap();

  private static HashMap<String, EvcsLocationType> initMap() {
    final HashMap<String, EvcsLocationType> map = new HashMap<>(EvcsLocationType.values().length);
    for (EvcsLocationType type : EvcsLocationType.values()) map.put(type.name(), type);

    return map;
  }

  private EvcsLocationTypeUtils() {
    throw new IllegalStateException("This is a factory class. Don't try to instantiate it.");
  }

  public static EvcsLocationType parse(String parsableString) throws ParsingException {
    if (nameToType.containsKey(parsableString)) return nameToType.get(parsableString);
    else throw new ParsingException("EvcsLocationType '" + parsableString + "' does not exist.");
  }
}
