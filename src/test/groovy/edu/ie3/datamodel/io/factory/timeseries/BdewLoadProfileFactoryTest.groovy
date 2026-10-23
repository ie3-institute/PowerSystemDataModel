/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import edu.ie3.datamodel.io.factory.FieldMap
import edu.ie3.datamodel.io.source.DataSource
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileEntry
import edu.ie3.datamodel.models.value.load.BdewLoadValues
import edu.ie3.datamodel.utils.CollectionUtils
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

    def data0 = [
      "suSa": "74.6",
      "suSu": "68.8",
      "suWd": "71.5",
      "trSa": "75.8",
      "trSu": "68.3",
      "trWd": "73.0",
      "wiSa": "70.0",
      "wiSu": "63.2",
      "wiWd": "65.5",
      "quarterHour": "0"
    ]

    def data1 = [
      "suSa": "76.2",
      "suSu": "67.4",
      "suWd": "69.0",
      "trSa": "76.7",
      "trSu": "66.5",
      "trWd": "70.1",
      "wiSa": "73.0",
      "wiSu": "61.0",
      "wiWd": "62.6",
      "quarterHour": "1"
    ]

    def data2 = [
      "suSa": "77.7",
      "suSu": "65.7",
      "suWd": "66.3",
      "trSa": "77.7",
      "trSu": "64.6",
      "trWd": "67.1",
      "wiSa": "75.9",
      "wiSu": "58.9",
      "wiWd": "59.6",
      "quarterHour": "2"
    ]

    allEntries = [
      factory.buildModel(new FieldMap(data0)),
      factory.buildModel(new FieldMap(data1)),
      factory.buildModel(new FieldMap(data2))
    ].flatten() as Set<LoadProfileEntry<BdewLoadValues>>
  }

  def "A BDEWLoadProfileFactory returns the correct fields"() {
    given:
    def expectedScheme1999 = (BdewLoadValues.BdewKey.toMap(BdewLoadValues.BdewScheme.BDEW1999).values() + "quarterHour") as Set

    def expectedScheme2025 = (BdewLoadValues.BdewKey.toMap(BdewLoadValues.BdewScheme.BDEW2025).values() + "quarterHour") as Set

    when:
    def actual = factory.getFields(BdewLoadValues)

    then:
    actual.size() == 2
    actual.head() == expectedScheme1999
    actual.last() == expectedScheme2025
  }

  def "A BDEWLoadProfileFactory refuses to build from invalid data"() {
    given:
    def actualFields = CollectionUtils.newSet("Sa", "Su", "Wd")

    when:
    def actual = DataSource.validate(actualFields, BdewLoadValues)

    then:
    actual.failure
    actual.exception.get().message == "The provided fields [Sa, Su, Wd] are invalid for instance of 'BdewLoadValues'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'BdewLoadValues' are possible (NOT case-sensitive!):\n" +
        "0: [quarterHour, suSa, suSu, suWd, trSa, trSu, trWd, wiSa, wiSu, wiWd] or [quarter_hour, su_sa, su_su, su_wd, tr_sa, tr_su, tr_wd, wi_sa, wi_su, wi_wd]\n" +
        "1: [aprSa, aprSu, aprWd, augSa, augSu, augWd, decSa, decSu, decWd, febSa, febSu, febWd, janSa, janSu, janWd, julSa, julSu, julWd, junSa, junSu, junWd, marSa, marSu, marWd, maySa, maySu, mayWd, novSa, novSu, novWd, octSa, octSu, octWd, quarterHour, sepSa, sepSu, sepWd] or [apr_sa, apr_su, apr_wd, aug_sa, aug_su, aug_wd, dec_sa, dec_su, dec_wd, feb_sa, feb_su, feb_wd, jan_sa, jan_su, jan_wd, jul_sa, jul_su, jul_wd, jun_sa, jun_su, jun_wd, mar_sa, mar_su, mar_wd, may_sa, may_su, may_wd, nov_sa, nov_su, nov_wd, oct_sa, oct_su, oct_wd, quarter_hour, sep_sa, sep_su, sep_wd]\n"
  }

  def "A BDEWLoadProfileFactory builds model from valid data"() {
    given:
    def data = [
      "suSa": "74.6",
      "suSu": "68.8",
      "suWd": "71.5",
      "trSa": "75.8",
      "trSu": "68.3",
      "trWd": "73.0",
      "wiSa": "70.0",
      "wiSu": "63.2",
      "wiWd": "65.5",
      "quarterHour": "0"
    ] as Map

    when:
    def entry = factory.buildModel(new FieldMap(data))

    then:
    entry.value.class == BdewLoadValues
    entry.value.scheme.get() == BdewLoadValues.BdewScheme.BDEW1999
  }

  def "A BDEWLoadProfileFactory builds time series from entries"() {
    when:
    def lpts = factory.build(BdewStandardLoadProfile.G0.key, allEntries)

    then:
    lpts.powerProfileKey == BdewStandardLoadProfile.G0.key
    lpts.entries.size() == 3
  }

  def "A BDEWLoadProfileFactory does return the max power correctly"() {
    when:
    def maxPower = factory.calculateMaxPower(BdewStandardLoadProfile.G0.key, allEntries)

    then:
    maxPower == Quantities.getQuantity(77.7, PowerSystemUnits.WATT)
  }

  def "A BDEWLoadProfileFactory does return an energy scaling correctly"() {
    when:
    def energyScaling = factory.getLoadProfileEnergyScaling(BdewStandardLoadProfile.G0.key)

    then:
    energyScaling == Quantities.getQuantity(1000d, PowerSystemUnits.KILOWATTHOUR)
  }
}
