/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.type.evcslocation;

import edu.ie3.datamodel.exceptions.ParsingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class providing tools to retrieve {@link EvcsLocationType}s from string representation
 */
public class EvcsLocationTypeUtils {

  private static final HashMap<String, EvcsLocationType> nameToType = initMap();

  private static HashMap<String, EvcsLocationType> initMap() {
    final HashMap<String, EvcsLocationType> map = new HashMap<>(EvcsLocationType.values().length);
    for (EvcsLocationType type : EvcsLocationType.values()) map.put(toKey(type.name()), type);

    return map;
  }

  /** This is a static utility class. Do not instantiate! */
  private EvcsLocationTypeUtils() {
    throw new IllegalStateException("This is a factory class. Don't try to instantiate it.");
  }

  /**
   * Parsing a location type string into one {@link EvcsLocationType} or a list of
   * EvcsLocationTypes. Matching the string is case-insensitive and all - and _ are removed.
   *
   * @param parsableString string to parse
   * @return List<EvcsLocationType>
   * @throws ParsingException if string does not represent a location type
   */
  public static List<EvcsLocationType> parse(String parsableString) throws ParsingException {
    if (parsableString == null || parsableString.trim().isEmpty()) {
      throw new ParsingException("Location types string cannot be null or empty");
    }

    // Remove brackets if present
    parsableString = parsableString.replace("[", "").replace("]", "");

    // Check if it contains comma for multiple values
    if (parsableString.contains(",")) {
      return Arrays.stream(parsableString.split(","))
          .map(String::trim)
          .filter(s -> !s.isEmpty())
          .map(
              s -> {
                try {
                  return parseSingle(s);
                } catch (ParsingException e) {
                  throw new RuntimeException(e);
                }
              })
          .collect(Collectors.toList());
    } else {
      // Single value - wrap in List
      return List.of(parseSingle(parsableString.trim()));
    }
  }

  /**
   * Parsing a single location type string into one {@link EvcsLocationType}. Matching the string is
   * case-insensitive and all - and _ are removed.
   *
   * @param parsableString string to parse
   * @return corresponding EvcsLocationType
   * @throws ParsingException if string does not represent a location type
   */
  public static EvcsLocationType parseSingle(String parsableString) throws ParsingException {
    final String key = toKey(parsableString);
    if (nameToType.containsKey(key)) return nameToType.get(key);
    else throw new ParsingException("EvcsLocationType '" + key + "' does not exist.");
  }

  /**
   * Turns string to lower case and removes underscores and minuses.
   *
   * @param name name to turn into key
   * @return key
   */
  private static String toKey(String name) {
    return name.toLowerCase().replaceAll("[-_]*", "");
  }
}
