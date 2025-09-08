/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.datamodel.models.profile.LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE

import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.datamodel.models.profile.LoadProfile
import spock.lang.Specification

class LoadProfileSourceTest extends Specification {

  def "A LoadProfileSource should return the correct profile resolution for a given load profile"() {
    given:
    def allProfiles = LoadProfile.getAllProfiles()


    when:
    def resolutions = Arrays.stream(allProfiles).map { it -> LoadProfileSource.getResolution(it) }.toList()

    then:
    resolutions.every { resolution -> resolution == 900 }
  }


  def "A LoadProfileSource should read in all build-in BDEWStandardLoadProfiles"() {
    when:
    def profiles = LoadProfileSource.bdewLoadProfiles

    then:
    profiles.size() == 16
    BdewStandardLoadProfile.values().every { profiles.keySet().contains(it) }
    profiles.values().every { it.timeSeries.entries.size() == 96 }
  }

  def "A LoadProfileSource should read in the build-in RandomLoadProfile"() {
    when:
    def random = LoadProfileSource.randomLoadProfile.timeSeries

    then:
    random.loadProfile == RANDOM_LOAD_PROFILE
    random.entries.size() == 96
  }
}
