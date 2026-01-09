/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.datamodel.models.profile.LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE

import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.datamodel.models.profile.LoadProfile
import edu.ie3.datamodel.models.profile.NbwTemperatureDependantLoadProfile
import edu.ie3.datamodel.models.profile.PowerProfileKey
import spock.lang.Specification

import java.util.stream.Stream

class LoadProfileSourceTest extends Specification {

  def "A LoadProfileSource should return the correct profile resolution for a given load profile"() {
    given:
    def allProfiles = Stream.of(BdewStandardLoadProfile.values(), NbwTemperatureDependantLoadProfile.values(), new LoadProfile[] {
      RANDOM_LOAD_PROFILE
    }).flatMap {
      Arrays.stream(it)
    }

    when:
    def resolutions = allProfiles.map {
      it -> LoadProfileSource.getResolution(it.key)
    }.toList()
    def resolutionForNoKeyAssigned = LoadProfileSource.getResolution(PowerProfileKey.NO_KEY_ASSIGNED)

    then:
    resolutions.every {
      resolution -> resolution == 900
    }
    resolutionForNoKeyAssigned == Long.MAX_VALUE
  }


  def "A LoadProfileSource should read in all build-in BDEWStandardLoadProfiles"() {
    when:
    def profiles = LoadProfileSource.bdewLoadProfiles

    then:
    profiles.size() == 16
    BdewStandardLoadProfile.values().every {
      profiles.keySet().contains(it.key)
    }
    profiles.values().every {
      it.timeSeries.entries.size() == 96
    }
  }

  def "A LoadProfileSource should read in the build-in RandomLoadProfile"() {
    when:
    def random = LoadProfileSource.randomLoadProfile.timeSeries

    then:
    random.powerProfileKey == RANDOM_LOAD_PROFILE.key
    random.entries.size() == 96
  }
}
