/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry
import edu.ie3.datamodel.models.value.load.BdewLoadValues
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class BdewLoadProfileFactoryTest extends Specification {
  @Shared
  BdewLoadProfileFactory factory

  @Shared
  private Set<LoadProfileEntry<BdewLoadValues>> allEntries

  def setupSpec() {
    factory = new BdewLoadProfileFactory()

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
    ] as Map, BdewLoadValues)

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
    ] as Map, BdewLoadValues)

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
    ] as Map, BdewLoadValues)

    allEntries = [
      factory.buildModel(data0),
      factory.buildModel(data1),
      factory.buildModel(data2)
    ].flatten() as Set<LoadProfileEntry<BdewLoadValues>>
  }

  def "A BDEWLoadProfileFactory returns the correct fields"() {
    given:
    def expectedScheme1999 = [
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

    def expectedScheme2025 = [
      "JanSa",
      "JanSu",
      "JanWd",
      "FebSa",
      "FebSu",
      "FebWd",
      "MarSa",
      "MarSu",
      "MarWd",
      "AprSa",
      "AprSu",
      "AprWd",
      "MaySa",
      "MaySu",
      "MayWd",
      "JunSa",
      "JunSu",
      "JunWd",
      "JulSa",
      "JulSu",
      "JulWd",
      "AugSa",
      "AugSu",
      "AugWd",
      "SepSa",
      "SepSu",
      "SepWd",
      "OctSa",
      "OctSu",
      "OctWd",
      "NovSa",
      "NovSu",
      "NovWd",
      "DecSa",
      "DecSu",
      "DecWd",
      "quarterHour"
    ] as Set

    when:
    def actual = factory.getFields(BdewLoadValues)

    then:
    actual.size() == 2
    actual.head() == expectedScheme1999
    actual.last() == expectedScheme2025
  }

  def "A BDEWLoadProfileFactory refuses to build from invalid data"() {
    given:
    def actualFields = factory.newSet("Sa", "Su", "Wd")

    when:
    def actual = factory.validate(actualFields, BdewLoadValues)

    then:
    actual.failure
    actual.exception.get().message == "The provided fields [Sa, Su, Wd] are invalid for instance of 'BdewLoadValues'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'BdewLoadValues' are possible (NOT case-sensitive!):\n" +
        "0: [quarterHour, SuSa, SuSu, SuWd, TrSa, TrSu, TrWd, WiSa, WiSu, WiWd] or [quarter_hour, su_sa, su_su, su_wd, tr_sa, tr_su, tr_wd, wi_sa, wi_su, wi_wd]\n" +
            "1: [AprSa, AprSu, AprWd, AugSa, AugSu, AugWd, DecSa, DecSu, DecWd, FebSa, FebSu, FebWd, JanSa, JanSu, JanWd, JulSa, JulSu, JulWd, JunSa, JunSu, JunWd, MarSa, MarSu, MarWd, MaySa, MaySu, MayWd, NovSa, NovSu, NovWd, OctSa, OctSu, OctWd, quarterHour, SepSa, SepSu, SepWd] or [apr_sa, apr_su, apr_wd, aug_sa, aug_su, aug_wd, dec_sa, dec_su, dec_wd, feb_sa, feb_su, feb_wd, jan_sa, jan_su, jan_wd, jul_sa, jul_su, jul_wd, jun_sa, jun_su, jun_wd, mar_sa, mar_su, mar_wd, may_sa, may_su, may_wd, nov_sa, nov_su, nov_wd, oct_sa, oct_su, oct_wd, quarter_hour, sep_sa, sep_su, sep_wd]\n"
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
    def entry = factory.buildModel(new LoadProfileData<>(data, BdewLoadValues))

    then:
    entry.value.class == BdewLoadValues.BDEW1999
  }

  def "A BDEWLoadProfileFactory builds time series from entries"() {
    given:
    UUID uuid = UUID.fromString("fa3894c1-25af-479c-8a40-1323bb9150a9")
    LoadProfileMetaInformation metaInformation = new LoadProfileMetaInformation(uuid, "g0")


    when:
    def lpts = factory.build(metaInformation, allEntries)

    then:
    lpts.loadProfile == BdewStandardLoadProfile.G0
    lpts.entries.size() == 3
  }

  def "A BDEWLoadProfileFactory does return the max power correctly"() {
    when:
    def maxPower = factory.calculateMaxPower(BdewStandardLoadProfile.G0, allEntries)

    then:
    maxPower == Quantities.getQuantity(77.7, PowerSystemUnits.WATT)
  }

  def "A BDEWLoadProfileFactory does return an energy scaling correctly"() {
    when:
    def energyScaling = factory.getLoadProfileEnergyScaling(BdewStandardLoadProfile.G0)

    then:
    energyScaling == Quantities.getQuantity(1000d, PowerSystemUnits.KILOWATTHOUR)
  }
}
