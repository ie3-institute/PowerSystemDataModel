/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Stream;

public interface LoadProfile extends Serializable {
  /**
   * @return The identifying String
   */
  String getKey();

  /**
   * Parses the given key to {@link StandardLoadProfile}.
   *
   * @param key Key to parse
   * @return Matching {@link StandardLoadProfile}
   */
  static LoadProfile parse(String key) {
    if (key == null || key.isEmpty()) return LoadProfile.DefaultLoadProfiles.NO_LOAD_PROFILE;

    return LoadProfile.getProfile(getAllProfiles(), key);
  }

  static LoadProfile[] getAllProfiles() {
    return Stream.of(
            BdewStandardLoadProfile.values(),
            NbwTemperatureDependantLoadProfile.values(),
            (LoadProfile[]) RandomLoadProfile.values())
        .flatMap(Arrays::stream)
        .toArray(LoadProfile[]::new);
  }

  /**
   * Looks for load profile with given key and returns it. If no suitable profile is found, a {@link
   * CustomLoadProfile} with the given key is returned.
   *
   * @param profiles we search within
   * @param key to look for
   * @return the matching load profile
   */
  @SuppressWarnings("unchecked")
  static <T extends LoadProfile> T getProfile(T[] profiles, String key) {
    String uniformKey = getUniformKey(key);

    return Arrays.stream(profiles)
        .filter(loadProfile -> loadProfile.getKey().equalsIgnoreCase(uniformKey))
        .findFirst()
        .orElseGet(() -> (T) new CustomLoadProfile(uniformKey));
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

  record CustomLoadProfile(String key) implements LoadProfile {
    @Override
    public String getKey() {
      return key;
    }
  }

  enum RandomLoadProfile implements LoadProfile {
    RANDOM_LOAD_PROFILE;

    @Override
    public String getKey() {
      return "random";
    }
  }
}
