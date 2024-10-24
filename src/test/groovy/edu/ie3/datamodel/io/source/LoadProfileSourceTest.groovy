/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.datamodel.models.profile.LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE

import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import spock.lang.Specification

class LoadProfileSourceTest extends Specification {

  def "A LoadProfileSourceTest should read in all build-in BDEWStandardLoadProfiles"() {
    when:
    def profiles = LoadProfileSource.getBDEWLoadProfiles()

    then:
    profiles.size() == 11
    BdewStandardLoadProfile.values().every { profiles.keySet().contains(it) }
    profiles.values().every { it.entries.size() == 96 }
  }

  def "A LoadProfileSourceTest should read in the build-in RandomLoadProfile"() {
    when:
    def random = LoadProfileSource.randomLoadProfile

    then:
    random.loadProfile == RANDOM_LOAD_PROFILE
    random.entries.size() == 96
  }
}
