/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.resources.load

import edu.ie3.datamodel.io.factory.timeseries.BdewLoadProfileFactory
import edu.ie3.datamodel.io.factory.timeseries.LoadProfileData
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.source.csv.CsvDataSource
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.datamodel.models.value.load.BdewLoadValues
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path
import java.util.function.Function

class BdewLoadProfileTest extends Specification {

  @Shared
  private CsvDataSource source

  @Shared
  private BdewLoadProfileFactory factory = new BdewLoadProfileFactory()

  def setupSpec() {
    Path resourcePath = Path.of(".", "src", "main", "resources", "load")
    source = new CsvDataSource(",", resourcePath, new FileNamingStrategy())
  }

  def "The bdew profile g0 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G0)

    when:
    def suSa = sumValues(data, v -> v.suSa)
    def suSu = sumValues(data, v -> v.suSu)
    def suWd = sumValues(data, v -> v.suWd)

    def trSa = sumValues(data, v -> v.trSa)
    def trSu = sumValues(data, v -> v.trSu)
    def trWd = sumValues(data, v -> v.trWd)

    def wiSa = sumValues(data, v -> v.wiSa)
    def wiSu = sumValues(data, v -> v.wiSu)
    def wiWd = sumValues(data, v -> v.wiWd)

    then:
    suSa == 9994d
    suSu == 6187.2d
    suWd == 11784.4d

    trSa == 10434.2d
    trSu == 6293.7d
    trWd == 12239.9d

    wiSa == 10693.2d
    wiSu == 6227.4d
    wiWd == 12827.2d
  }

  def "The bdew profile g1 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G1)

    when:
    def suSa = sumValues(data, v -> v.suSa)
    def suSu = sumValues(data, v -> v.suSu)
    def suWd = sumValues(data, v -> v.suWd)

    def trSa = sumValues(data, v -> v.trSa)
    def trSu = sumValues(data, v -> v.trSu)
    def trWd = sumValues(data, v -> v.trWd)

    def wiSa = sumValues(data, v -> v.wiSa)
    def wiSu = sumValues(data, v -> v.wiSu)
    def wiWd = sumValues(data, v -> v.wiWd)

    then:
    suSa == 2613.7d
    suSu == 2127.5d
    suWd == 12499.4d

    trSa == 3048.3d
    trSu == 1955.6d
    trWd == 14523.8d

    wiSa == 3294.2d
    wiSu == 2808.7d
    wiWd == 17431.7d
  }

  def "The bdew profile g2 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G2)

    when:
    def suSa = sumValues(data, v -> v.suSa)
    def suSu = sumValues(data, v -> v.suSu)
    def suWd = sumValues(data, v -> v.suWd)

    def trSa = sumValues(data, v -> v.trSa)
    def trSu = sumValues(data, v -> v.trSu)
    def trWd = sumValues(data, v -> v.trWd)

    def wiSa = sumValues(data, v -> v.wiSa)
    def wiSu = sumValues(data, v -> v.wiSu)
    def wiWd = sumValues(data, v -> v.wiWd)

    then:
    suSa == 9221.1d
    suSu == 7924.7d
    suWd == 9954.1d

    trSa == 10706.3d
    trSu == 8897.2d
    trWd == 11272.5d

    wiSa == 12456d
    wiSu == 10596
    wiWd == 12837.4d
  }

  def "The bdew profile g3 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G3)

    when:
    def suSa = sumValues(data, v -> v.suSa)
    def suSu = sumValues(data, v -> v.suSu)
    def suWd = sumValues(data, v -> v.suWd)

    def trSa = sumValues(data, v -> v.trSa)
    def trSu = sumValues(data, v -> v.trSu)
    def trWd = sumValues(data, v -> v.trWd)

    def wiSa = sumValues(data, v -> v.wiSa)
    def wiSu = sumValues(data, v -> v.wiSu)
    def wiWd = sumValues(data, v -> v.wiWd)

    then:
    suSa == 10834d
    suSu == 9656
    suWd == 11544.3d

    trSa == 10544.1d
    trSu == 9160.9d
    trWd == 10978.1d

    wiSa == 10645.9d
    wiSu == 9216.2d
    wiWd == 11679.7d
  }

  def "The bdew profile g4 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G4)

    when:
    def suSa = sumValues(data, v -> v.suSa)
    def suSu = sumValues(data, v -> v.suSu)
    def suWd = sumValues(data, v -> v.suWd)

    def trSa = sumValues(data, v -> v.trSa)
    def trSu = sumValues(data, v -> v.trSu)
    def trWd = sumValues(data, v -> v.trWd)

    def wiSa = sumValues(data, v -> v.wiSa)
    def wiSu = sumValues(data, v -> v.wiSu)
    def wiWd = sumValues(data, v -> v.wiWd)

    then:
    suSa == 10513d
    suSu == 6640.3d
    suWd == 11968.2d

    trSa == 10120.5d
    trSu == 6166.7d
    trWd == 11947d

    wiSa == 10733.4d
    wiSu == 6202.3d
    wiWd == 12749.4d
  }

  def "The bdew profile g5 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G5)

    when:
    def suSa = sumValues(data, v -> v.suSa)
    def suSu = sumValues(data, v -> v.suSu)
    def suWd = sumValues(data, v -> v.suWd)

    def trSa = sumValues(data, v -> v.trSa)
    def trSu = sumValues(data, v -> v.trSu)
    def trWd = sumValues(data, v -> v.trWd)

    def wiSa = sumValues(data, v -> v.wiSa)
    def wiSu = sumValues(data, v -> v.wiSu)
    def wiWd = sumValues(data, v -> v.wiWd)

    then:
    suSa == 12107.1d
    suSu == 5401d
    suWd == 12042.8d

    trSa == 11861.1d
    trSu == 5111d
    trWd == 11969.3d

    wiSa == 12337.1d
    wiSu == 5165.2d
    wiWd == 12477.4d
  }

  def "The bdew profile g6 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G6)

    when:
    def suSa = sumValues(data, v -> v.suSa)
    def suSu = sumValues(data, v -> v.suSu)
    def suWd = sumValues(data, v -> v.suWd)

    def trSa = sumValues(data, v -> v.trSa)
    def trSu = sumValues(data, v -> v.trSu)
    def trWd = sumValues(data, v -> v.trWd)

    def wiSa = sumValues(data, v -> v.wiSa)
    def wiSu = sumValues(data, v -> v.wiSu)
    def wiWd = sumValues(data, v -> v.wiWd)

    then:
    suSa == 11793.6d
    suSu == 12017.4d
    suWd == 9053.4d

    trSa == 12718.5d
    trSu == 13591.8d
    trWd == 10111.4d

    wiSa == 13647.2d
    wiSu == 13741.2d
    wiWd == 10748.5d
  }

  def "The bdew profile h0 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.H0)

    when:
    def suSa = sumValues(data, v -> v.suSa)
    def suSu = sumValues(data, v -> v.suSu)
    def suWd = sumValues(data, v -> v.suWd)

    def trSa = sumValues(data, v -> v.trSa)
    def trSu = sumValues(data, v -> v.trSu)
    def trWd = sumValues(data, v -> v.trWd)

    def wiSa = sumValues(data, v -> v.wiSa)
    def wiSu = sumValues(data, v -> v.wiSu)
    def wiWd = sumValues(data, v -> v.wiWd)

    then:
    suSa == 12132d
    suSu == 11416d
    suWd == 11255.9d

    trSa == 12054.9d
    trSu == 11079.4d
    trWd == 10783.3d

    wiSa == 11546d
    wiSu == 10742d
    wiWd == 10223.7d
  }

  def "The bdew profile L0 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.L0)

    when:
    def suSa = sumValues(data, v -> v.suSa)
    def suSu = sumValues(data, v -> v.suSu)
    def suWd = sumValues(data, v -> v.suWd)

    def trSa = sumValues(data, v -> v.trSa)
    def trSu = sumValues(data, v -> v.trSu)
    def trWd = sumValues(data, v -> v.trWd)

    def wiSa = sumValues(data, v -> v.wiSa)
    def wiSu = sumValues(data, v -> v.wiSu)
    def wiWd = sumValues(data, v -> v.wiWd)

    then:
    suSa == 9536.1d
    suSu == 10243d
    suWd == 9985.2d

    trSa == 10662.1d
    trSu == 11012.7d
    trWd == 10929.7d

    wiSa == 11452.7d
    wiSu == 12006.8d
    wiWd == 11934.3d
  }

  def "The bdew profile L1 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.L1)

    when:
    def suSa = sumValues(data, v -> v.suSa)
    def suSu = sumValues(data, v -> v.suSu)
    def suWd = sumValues(data, v -> v.suWd)

    def trSa = sumValues(data, v -> v.trSa)
    def trSu = sumValues(data, v -> v.trSu)
    def trWd = sumValues(data, v -> v.trWd)

    def wiSa = sumValues(data, v -> v.wiSa)
    def wiSu = sumValues(data, v -> v.wiSu)
    def wiWd = sumValues(data, v -> v.wiWd)

    then:
    suSa == 9320.5d
    suSu == 10011.8d
    suWd == 9963.3d

    trSa == 10484.5d
    trSu == 10913.8d
    trWd == 10874.8d

    wiSa == 11717.6d
    wiSu == 12241.9d
    wiWd == 12010d
  }

  def "The bdew profile L2 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.L2)

    when:
    def suSa = sumValues(data, v -> v.suSa)
    def suSu = sumValues(data, v -> v.suSu)
    def suWd = sumValues(data, v -> v.suWd)

    def trSa = sumValues(data, v -> v.trSa)
    def trSu = sumValues(data, v -> v.trSu)
    def trWd = sumValues(data, v -> v.trWd)

    def wiSa = sumValues(data, v -> v.wiSa)
    def wiSu = sumValues(data, v -> v.wiSu)
    def wiWd = sumValues(data, v -> v.wiWd)

    then:
    suSa == 9645.7d
    suSu == 10408.9d
    suWd == 10090.1d

    trSa == 10652.4d
    trSu == 10980.3d
    trWd == 10927.8d

    wiSa == 11326.9d
    wiSu == 11908.2d
    wiWd == 11847.5d
  }


  // helper methods

  private List<BdewLoadValues> read(BdewStandardLoadProfile profile) {
    source.getSourceData(Path.of("lpts_"+profile.key)).map { it -> factory.buildModel(new LoadProfileData<>(it, BdewLoadValues)).value }.toList()
  }

  private static double sumValues(List<BdewLoadValues> values, Function<BdewLoadValues, Double> extractor) {
    values.stream().map { extractor.apply(it) }.mapToDouble { it.doubleValue() }.sum()
  }
}
