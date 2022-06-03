/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

import edu.ie3.datamodel.exceptions.ParsingException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Collectors;
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

    String filterKey = getUniformKey(key);
    return Stream.concat(
            Arrays.stream(BdewStandardLoadProfile.values()),
            Arrays.stream(NbwTemperatureDependantLoadProfile.values()))
        .filter(profile -> profile.getKey().equals(filterKey))
        .findFirst()
        .orElseThrow(
            () ->
                new ParsingException("Cannot parse \"" + key + "\" to a valid known load profile"));
  }

  /** Looks for load profile with given key and returns it.
   *
   * @param profiles we search within
   * @param key to look for
   * @return the matching load profile
   */
  static LoadProfile getProfile(LoadProfile[] profiles, String key) {
    return Arrays.stream(profiles)
        .filter(loadProfile -> loadProfile.getKey().equalsIgnoreCase(getUniformKey(key)))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "No predefined load profile with key '"
                        + key
                        + "' found. Please provide one of the following keys:"
                        + Arrays.stream(profiles)
                            .map(LoadProfile::getKey)
                            .collect(Collectors.joining(", "))));
  }

  private static String getUniformKey(String key) {
    return key.toLowerCase().replaceAll("[-_]*", "");
  }

  enum DefaultLoadProfiles implements LoadProfile {
    NO_LOAD_PROFILE;

    @Override
    public String getKey() {
      return "No load profile assigned";
    }
  }
}
