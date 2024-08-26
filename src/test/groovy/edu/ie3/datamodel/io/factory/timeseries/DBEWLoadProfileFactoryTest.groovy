/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import static java.time.DayOfWeek.*

import edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation
import edu.ie3.datamodel.models.Season
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.datamodel.models.timeseries.repetitive.BDEWLoadProfileEntry
import edu.ie3.datamodel.models.timeseries.repetitive.BDEWLoadProfileTimeSeries
import spock.lang.Shared
import spock.lang.Specification

class DBEWLoadProfileFactoryTest extends Specification {
  @Shared
  BDEWLoadProfileFactory factory

  @Shared
  private Set<BDEWLoadProfileEntry> allEntries

  def setupSpec() {
    factory = new BDEWLoadProfileFactory()

    def data0 = new LoadProfileData([
      "SuSa": "74.6",
      "SuSu": "68.8",
      "SuWd": "71.5",
      "TrSa": "75.8",
      "TrSu": "68.3",
      "TrWd": "73.0",
      "WiSa": "70.0",
      "WiSu": "63.2",
      "WiWd": "65.5",
      "quarterHour": "0"
    ] as Map, BDEWLoadProfileEntry)

    def data1 = new LoadProfileData([
      "SuSa": "76.2",
      "SuSu": "67.4",
      "SuWd": "69.0",
      "TrSa": "76.7",
      "TrSu": "66.5",
      "TrWd": "70.1",
      "WiSa": "73.0",
      "WiSu": "61.0",
      "WiWd": "62.6",
      "quarterHour": "1"
    ] as Map, BDEWLoadProfileEntry)

    def data2 = new LoadProfileData([
      "SuSa": "77.7",
      "SuSu": "65.7",
      "SuWd": "66.3",
      "TrSa": "77.7",
      "TrSu": "64.6",
      "TrWd": "67.1",
      "WiSa": "75.9",
      "WiSu": "58.9",
      "WiWd": "59.6",
      "quarterHour": "2"
    ] as Map, BDEWLoadProfileEntry)

    allEntries = [
      factory.buildModel(data0),
      factory.buildModel(data1),
      factory.buildModel(data2)
    ].flatten() as Set<BDEWLoadProfileEntry>
  }

  def "A BDEWLoadProfileFactory returns the correct fields"() {
    given:
    def expectedFields = [
      "SuSa",
      "SuSu",
      "SuWd",
      "TrSa",
      "TrSu",
      "TrWd",
      "WiSa",
      "WiSu",
      "WiWd",
      "quarterHour"
    ] as Set

    when:
    def actual = factory.getFields(BDEWLoadProfileEntry)

    then:
    actual.size() == 1
    actual.head() == expectedFields
  }

  def "A BDEWLoadProfileFactory refuses to build from invalid data"() {
    given:
    def actualFields = factory.newSet("Wd", "Sa", "Su")

    when:
    def actual = factory.validate(actualFields, BDEWLoadProfileEntry)

    then:
    actual.failure
    actual.exception.get().message == "The provided fields [Sa, Su, Wd] are invalid for instance of 'BDEWLoadProfileEntry'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'BDEWLoadProfileEntry' are possible (NOT case-sensitive!):\n" +
        "0: [quarterHour, SuSa, SuSu, SuWd, TrSa, TrSu, TrWd, WiSa, WiSu, WiWd] or [quarter_hour, su_sa, su_su, su_wd, tr_sa, tr_su, tr_wd, wi_sa, wi_su, wi_wd]\n"
  }

  def "A BDEWLoadProfileFactory builds model from valid data"() {
    given:
    def data = [
      "SuSa": "74.6",
      "SuSu": "68.8",
      "SuWd": "71.5",
      "TrSa": "75.8",
      "TrSu": "68.3",
      "TrWd": "73.0",
      "WiSa": "70.0",
      "WiSu": "63.2",
      "WiWd": "65.5",
      "quarterHour": "0"
    ] as Map

    when:
    def entries = factory.buildModel(new LoadProfileData<>(data, BDEWLoadProfileEntry))

    then:
    entries.size() == 9
  }

  def "A BDEWLoadProfileFactory builds time series from entries"() {
    given:
    UUID uuid = UUID.fromString("fa3894c1-25af-479c-8a40-1323bb9150a9")
    LoadProfileTimeSeriesMetaInformation metaInformation = new LoadProfileTimeSeriesMetaInformation(uuid, "g0")


    when:
    def lpts = factory.build(metaInformation, allEntries)

    then:
    lpts.loadProfile == BdewStandardLoadProfile.G0
    lpts.entries.size() == 27

    lpts.valueMapping.keySet() == [
      new BDEWLoadProfileTimeSeries.BdewKey(Season.SUMMER, SATURDAY),
      new BDEWLoadProfileTimeSeries.BdewKey(Season.SUMMER, SUNDAY),
      new BDEWLoadProfileTimeSeries.BdewKey(Season.SUMMER, MONDAY),
      new BDEWLoadProfileTimeSeries.BdewKey(Season.TRANSITION, SATURDAY),
      new BDEWLoadProfileTimeSeries.BdewKey(Season.TRANSITION, SUNDAY),
      new BDEWLoadProfileTimeSeries.BdewKey(Season.TRANSITION, MONDAY),
      new BDEWLoadProfileTimeSeries.BdewKey(Season.WINTER, SATURDAY),
      new BDEWLoadProfileTimeSeries.BdewKey(Season.WINTER, SUNDAY),
      new BDEWLoadProfileTimeSeries.BdewKey(Season.WINTER, MONDAY),
    ] as Set

    lpts.valueMapping.values().every { it.size() == 3}
  }
}
