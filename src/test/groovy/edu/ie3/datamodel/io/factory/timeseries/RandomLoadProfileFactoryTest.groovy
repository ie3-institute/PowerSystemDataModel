/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation
import edu.ie3.datamodel.models.profile.LoadProfile
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry
import edu.ie3.datamodel.models.value.load.RandomLoadValues
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class RandomLoadProfileFactoryTest extends Specification {
  @Shared
  RandomLoadProfileFactory factory

  @Shared
  private Set<LoadProfileEntry<RandomLoadValues>> allEntries

  def setupSpec() {
    factory = new RandomLoadProfileFactory()

    def data0 = new LoadProfileData([
      "kSa": "0.266806721687317",
      "kSu": "0.295997023582459",
      "kWd": "0.279087692499161",
      "mySa": "0.0610353946685791",
      "mySu": "0.0630703344941139",
      "myWd": "0.053140863776207",
      "sigmaSa": "0.0357091873884201",
      "sigmaSu": "0.0370676517486572",
      "sigmaWd": "0.0293692331761122",
      "quarterHour": "0"
    ] as Map, RandomLoadValues)

    def data1 = new LoadProfileData([
      "kSa": "0.281179457902908",
      "kSu": "0.299608528614044",
      "kWd": "0.275292456150055",
      "mySa": "0.0560021996498108",
      "mySu": "0.058424074202776",
      "myWd": "0.0498424917459488",
      "sigmaSa": "0.0319067053496838",
      "sigmaSu": "0.0334825366735458",
      "sigmaWd": "0.0265011098235846",
      "quarterHour": "1"
    ] as Map, RandomLoadValues)

    def data2 = new LoadProfileData([
      "kSa": "0.275563269853592",
      "kSu": "0.29670587182045",
      "kWd": "0.252942383289337",
      "mySa": "0.0528385005891323",
      "mySu": "0.0547995530068874",
      "myWd": "0.0472154095768929",
      "sigmaSa": "0.0286294519901276",
      "sigmaSu": "0.0310499873012304",
      "sigmaWd": "0.0245211906731129",
      "quarterHour": "2"
    ] as Map, RandomLoadValues)

    allEntries = [
      factory.buildModel(data0),
      factory.buildModel(data1),
      factory.buildModel(data2)
    ].flatten() as Set<LoadProfileEntry<RandomLoadValues>>
  }

  def "A RandomLoadProfileFactory returns the correct fields"() {
    given:
    def expectedFields = [
      "kSa",
      "kSu",
      "kWd",
      "mySa",
      "mySu",
      "myWd",
      "sigmaSa",
      "sigmaSu",
      "sigmaWd",
      "quarterHour"
    ] as Set

    when:
    def actual = factory.getFields(RandomLoadValues)

    then:
    actual.size() == 1
    actual.head() == expectedFields
  }

  def "A RandomLoadProfileFactory refuses to build from invalid data"() {
    given:
    def actualFields = factory.newSet("Wd", "Sa", "Su")

    when:
    def actual = factory.validate(actualFields, RandomLoadValues)

    then:
    actual.failure
    actual.exception.get().message == "The provided fields [Sa, Su, Wd] are invalid for instance of 'RandomLoadValues'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'RandomLoadValues' are possible (NOT case-sensitive!):\n" +
        "0: [kSa, kSu, kWd, mySa, mySu, myWd, quarterHour, sigmaSa, sigmaSu, sigmaWd] or [k_sa, k_su, k_wd, my_sa, my_su, my_wd, quarter_hour, sigma_sa, sigma_su, sigma_wd]\n"
  }

  def "A RandomLoadProfileFactory builds model from valid data"() {
    given:
    def data = [
      "kSa": "0.266806721687317",
      "kSu": "0.295997023582459",
      "kWd": "0.279087692499161",
      "mySa": "0.0610353946685791",
      "mySu": "0.0630703344941139",
      "myWd": "0.053140863776207",
      "sigmaSa": "0.0357091873884201",
      "sigmaSu": "0.0370676517486572",
      "sigmaWd": "0.0293692331761122",
      "quarterHour": "0"
    ] as Map

    when:
    def entry = factory.buildModel(new LoadProfileData<>(data, RandomLoadValues))

    then:
    entry.value.class == RandomLoadValues
  }

  def "A RandomLoadProfileFactory builds time series from entries"() {
    given:
    UUID uuid = UUID.fromString("fa3894c1-25af-479c-8a40-1323bb9150a9")
    LoadProfileMetaInformation metaInformation = new LoadProfileMetaInformation(uuid, "random")


    when:
    def lpts = factory.build(metaInformation, allEntries)

    then:
    lpts.loadProfile == LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE
    lpts.entries.size() == 3
  }

  def "A RandomLoadProfileFactory does return the max power correctly"() {
    when:
    def maxPower = factory.calculateMaxPower(LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE, allEntries)

    then:
    maxPower.isPresent()
    maxPower.get() == Quantities.getQuantity(159d, PowerSystemUnits.WATT)
  }

  def "A RandomLoadProfileFactory does return an energy scaling correctly"() {
    when:
    def energyScaling = factory.getLoadProfileEnergyScaling(LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE)

    then:
    energyScaling.isPresent()
    energyScaling.get() == Quantities.getQuantity(716.5416966513656, PowerSystemUnits.KILOWATTHOUR)
  }
}
