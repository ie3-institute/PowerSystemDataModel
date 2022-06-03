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

    return LoadProfile.getProfile(getAllProfiles(), key);
  }

  static LoadProfile[] getAllProfiles() {
    final LoadProfile[][] all =
        new LoadProfile[][] {
          BdewStandardLoadProfile.values(), NbwTemperatureDependantLoadProfile.values()
        };

    return Arrays.stream(all).flatMap(Arrays::stream).toArray(LoadProfile[]::new);
  }

  /**
   * Looks for load profile with given key and returns it.
   *
   * @param profiles we search within
   * @param key to look for
   * @return the matching load profile
   */
  static <T extends LoadProfile> T getProfile(T[] profiles, String key) throws ParsingException {
    return Arrays.stream(profiles)
        .filter(loadProfile -> loadProfile.getKey().equalsIgnoreCase(getUniformKey(key)))
        .findFirst()
        .orElseThrow(
            () ->
                new ParsingException(
                    "No predefined load profile with key '"
                        + key
                        + "' found. Please provide one of the following keys: "
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
