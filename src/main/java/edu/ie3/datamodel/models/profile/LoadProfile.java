/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

public interface LoadProfile extends PowerProfile {

  enum RandomLoadProfile implements LoadProfile {
    RANDOM_LOAD_PROFILE;

    @Override
    public PowerProfileKey getKey() {
      return new PowerProfileKey("random");
    }
  }
}
