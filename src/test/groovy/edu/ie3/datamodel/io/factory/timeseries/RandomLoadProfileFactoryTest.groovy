/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import static java.time.DayOfWeek.*

import edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation
import edu.ie3.datamodel.models.profile.LoadProfile
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries
import edu.ie3.datamodel.models.timeseries.repetitive.RandomLoadProfileEntry
import spock.lang.Shared
import spock.lang.Specification

class RandomLoadProfileFactoryTest extends Specification {
  @Shared
  RandomLoadProfileFactory factory

  @Shared
  private Set<RandomLoadProfileEntry> allEntries

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
    ] as Map, RandomLoadProfileEntry)

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
    ] as Map, RandomLoadProfileEntry)

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
    ] as Map, RandomLoadProfileEntry)

    allEntries = [
      factory.buildModel(data0),
      factory.buildModel(data1),
      factory.buildModel(data2)
    ].flatten() as Set<RandomLoadProfileEntry>
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
    def actual = factory.getFields(RandomLoadProfileEntry)

    then:
    actual.size() == 1
    actual.head() == expectedFields
  }

  def "A RandomLoadProfileFactory refuses to build from invalid data"() {
    given:
    def actualFields = factory.newSet("Wd", "Sa", "Su")

    when:
    def actual = factory.validate(actualFields, RandomLoadProfileEntry)

    then:
    actual.failure
    actual.exception.get().message == "The provided fields [Sa, Su, Wd] are invalid for instance of 'RandomLoadProfileEntry'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'RandomLoadProfileEntry' are possible (NOT case-sensitive!):\n" +
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
    def entries = factory.buildModel(new LoadProfileData<>(data, RandomLoadProfileEntry))

    then:
    entries.size() == 3
  }

  def "A RandomLoadProfileFactory builds time series from entries"() {
    given:
    UUID uuid = UUID.fromString("fa3894c1-25af-479c-8a40-1323bb9150a9")
    LoadProfileTimeSeriesMetaInformation metaInformation = new LoadProfileTimeSeriesMetaInformation(uuid, "random")


    when:
    def lpts = factory.build(metaInformation, allEntries)

    then:
    lpts.loadProfile == LoadProfile.RandomLoadProfile.RANDOM_LOAD_PROFILE
    lpts.entries.size() == 9

    lpts.valueMapping.keySet() == [
      new LoadProfileTimeSeries.WeekDayKey(SATURDAY),
      new LoadProfileTimeSeries.WeekDayKey(SUNDAY),
      new LoadProfileTimeSeries.WeekDayKey(MONDAY),
    ] as Set

    lpts.valueMapping.values().every { it.size() == 3}
  }
}
