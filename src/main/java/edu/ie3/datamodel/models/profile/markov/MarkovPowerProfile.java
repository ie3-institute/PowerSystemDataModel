/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile.markov;

import edu.ie3.datamodel.models.profile.PowerProfile;
import java.util.Objects;

/** Simple {@link PowerProfile} implementation for Markov-based load models. */
public record MarkovPowerProfile(String key) implements PowerProfile {

  public MarkovPowerProfile {
    Objects.requireNonNull(key, "key");
    if (key.isBlank()) {
      throw new IllegalArgumentException("Profile key must not be blank.");
    }
  }

  @Override
  public String getKey() {
    return key;
  }
}
