/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile.markov;

import edu.ie3.datamodel.models.profile.PowerProfile;
import edu.ie3.datamodel.models.profile.PowerProfileKey;
import java.util.Objects;

/** Simple {@link PowerProfile} implementation for Markov-based load models. */
public record MarkovPowerProfile(PowerProfileKey key) implements PowerProfile {

  public MarkovPowerProfile {
    Objects.requireNonNull(key, "key");
    if (key.noKeyAssigned) {
      throw new IllegalArgumentException("Profile key must not be blank.");
    }
  }

  public MarkovPowerProfile(String key) {
    this(buildKey(key));
  }

  private static PowerProfileKey buildKey(String key) {
    if (key == null || key.isBlank()) {
      throw new IllegalArgumentException("Profile key must not be blank.");
    }
    return new PowerProfileKey(key);
  }
}
