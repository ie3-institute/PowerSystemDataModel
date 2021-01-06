/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.RepetitiveTimeSeries;
import java.util.Arrays;

/**
 * Giving reference to a known standard load profile to apply to a {@link
 * edu.ie3.datamodel.models.input.system.LoadInput}. This interface does nothing more, than giving a
 * reference, the values have to be provided by the simulator using the models.
 *
 * <p>If you intend to provide distinct values, create either an {@link IndividualTimeSeries} or
 * {@link RepetitiveTimeSeries} and assign it to the model via mapping to the model.
 */
public interface StandardLoadProfile {
  /** @return The identifying String */
  String getKey();

  /**
   * Parses the given key to {@link StandardLoadProfile}.
   *
   * @param key Key to parse
   * @return Matching {@link StandardLoadProfile}
   * @throws ParsingException If key cannot be parsed
   */
  static StandardLoadProfile parse(String key) throws ParsingException {
    if (key == null || key.isEmpty()) return DefaultLoadProfiles.NO_STANDARD_LOAD_PROFILE;

    String filterKey = key.toLowerCase().replaceAll("[-_]*", "");
    return Arrays.stream(BdewLoadProfile.values())
        .filter(profile -> profile.getKey().equals(filterKey))
        .findFirst()
        .orElseThrow(
            () ->
                new ParsingException(
                    "Cannot parse \"" + key + "\" to a valid bdew standard load profile"));
  }

  enum DefaultLoadProfiles implements StandardLoadProfile {
    NO_STANDARD_LOAD_PROFILE;

    @Override
    public String getKey() {
      return "No standard load profile assigned";
    }
  }
}
