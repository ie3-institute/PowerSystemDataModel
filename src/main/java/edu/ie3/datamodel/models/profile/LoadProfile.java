/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

import edu.ie3.datamodel.exceptions.ParsingException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Stream;

public interface LoadProfile extends Serializable {
  /** @return The identifying String */
  String getKey();

  /**
   * Parses the given key to {@link StandardLoadProfile}.
   *
   * @param key Key to parse
   * @return Matching {@link StandardLoadProfile}
   * @throws ParsingException If key cannot be parsed
   */
  static LoadProfile parse(String key) throws ParsingException {
    if (key == null || key.isEmpty()) return LoadProfile.DefaultLoadProfiles.NO_LOAD_PROFILE;

    String filterKey = key.toLowerCase().replaceAll("[-_]*", "");
    return Stream.concat(
            Arrays.stream(BdewStandardLoadProfile.values()),
            Arrays.stream(NbwTemperatureDependantLoadProfile.values()))
        .filter(profile -> profile.getKey().equals(filterKey))
        .findFirst()
        .orElseThrow(
            () ->
                new ParsingException("Cannot parse \"" + key + "\" to a valid known load profile"));
  }

  enum DefaultLoadProfiles implements StandardLoadProfile {
    NO_LOAD_PROFILE;

    @Override
    public String getKey() {
      return "No load profile assigned";
    }
  }
}