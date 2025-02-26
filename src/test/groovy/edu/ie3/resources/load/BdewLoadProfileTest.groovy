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

  @Shared
  private List<String> keys = ['su', 'tr', 'wi']

  @Shared
  private Map results = [:]

  def setupSpec() {
    Path resourcePath = Path.of(".", "src", "main", "resources", "load")
    source = new CsvDataSource(",", resourcePath, new FileNamingStrategy())
  }

  def "The BDEW profile G0 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G0)

    when:
    keys.each {
      key ->
      results["${key}Sa"] = sumValues(data, v -> v."${key}Sa")
      results["${key}Su"] = sumValues(data, v -> v."${key}Su")
      results["${key}Wd"] = sumValues(data, v -> v."${key}Wd")
    }

    then:
    results["suSa"] == 9994.0
    results["suSu"] == 6187.2
    results["suWd"] == 11784.4

    results["trSa"] == 10434.2
    results["trSu"] == 6293.7
    results["trWd"] == 12239.9

    results["wiSa"] == 10693.2
    results["wiSu"] == 6227.4
    results["wiWd"] == 12827.2
  }

  def "The BDEW profile G1 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G1)

    when:
    keys.each {
      key ->
      results["${key}Sa"] = sumValues(data, v -> v."${key}Sa")
      results["${key}Su"] = sumValues(data, v -> v."${key}Su")
      results["${key}Wd"] = sumValues(data, v -> v."${key}Wd")
    }

    then:
    results["suSa"] == 2613.7
    results["suSu"] == 2127.5
    results["suWd"] == 12499.4

    results["trSa"] == 3048.3
    results["trSu"] == 1955.6
    results["trWd"] == 14523.8

    results["wiSa"] == 3294.2
    results["wiSu"] == 2808.7
    results["wiWd"] == 17431.7
  }

  def "The BDEW profile G2 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G2)

    when:
    keys.each {
      key ->
      results["${key}Sa"] = sumValues(data, v -> v."${key}Sa")
      results["${key}Su"] = sumValues(data, v -> v."${key}Su")
      results["${key}Wd"] = sumValues(data, v -> v."${key}Wd")
    }

    then:
    results["suSa"] == 9221.1
    results["suSu"] == 7924.7
    results["suWd"] == 9954.1

    results["trSa"] == 10706.3
    results["trSu"] == 8897.2
    results["trWd"] == 11272.5

    results["wiSa"] == 12456
    results["wiSu"] == 10596.0
    results["wiWd"] == 12837.4
  }

  def "The BDEW profile G3 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G3)

    when:
    keys.each {
      key ->
      results["${key}Sa"] = sumValues(data, v -> v."${key}Sa")
      results["${key}Su"] = sumValues(data, v -> v."${key}Su")
      results["${key}Wd"] = sumValues(data, v -> v."${key}Wd")
    }

    then:
    results["suSa"] == 10834.0
    results["suSu"] == 9656.0
    results["suWd"] == 11544.3

    results["trSa"] == 10544.1
    results["trSu"] == 9160.9
    results["trWd"] == 10978.1

    results["wiSa"] == 10645.9
    results["wiSu"] == 9216.2
    results["wiWd"] == 11679.7
  }

  def "The BDEW profile G4 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G4)

    when:
    keys.each {
      key ->
      results["${key}Sa"] = sumValues(data, v -> v."${key}Sa")
      results["${key}Su"] = sumValues(data, v -> v."${key}Su")
      results["${key}Wd"] = sumValues(data, v -> v."${key}Wd")
    }

    then:
    results["suSa"] == 10513.0
    results["suSu"] == 6640.3
    results["suWd"] == 11968.2

    results["trSa"] == 10120.5
    results["trSu"] == 6166.7
    results["trWd"] == 11947

    results["wiSa"] == 10733.4
    results["wiSu"] == 6202.3
    results["wiWd"] == 12749.4
  }

  def "The BDEW profile G5 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G5)

    when:
    keys.each {
      key ->
      results["${key}Sa"] = sumValues(data, v -> v."${key}Sa")
      results["${key}Su"] = sumValues(data, v -> v."${key}Su")
      results["${key}Wd"] = sumValues(data, v -> v."${key}Wd")
    }

    then:
    results["suSa"] == 12107.1
    results["suSu"] == 5401.0
    results["suWd"] == 12042.8

    results["trSa"] == 11861.1
    results["trSu"] == 5111.0
    results["trWd"] == 11969.3

    results["wiSa"] == 12337.1
    results["wiSu"] == 5165.2
    results["wiWd"] == 12477.4
  }

  def "The BDEW profile G6 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G6)

    when:
    keys.each {
      key ->
      results["${key}Sa"] = sumValues(data, v -> v."${key}Sa")
      results["${key}Su"] = sumValues(data, v -> v."${key}Su")
      results["${key}Wd"] = sumValues(data, v -> v."${key}Wd")
    }

    then:
    results["suSa"] == 11793.6
    results["suSu"] == 12017.4
    results["suWd"] == 9053.4

    results["trSa"] == 12718.5
    results["trSu"] == 13591.8
    results["trWd"] == 10111.4

    results["wiSa"] == 13647.2
    results["wiSu"] == 13741.2
    results["wiWd"] == 10748.5
  }

  def "The BDEW profile H0 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.H0)

    when:
    keys.each {
      key ->
      results["${key}Sa"] = sumValues(data, v -> v."${key}Sa")
      results["${key}Su"] = sumValues(data, v -> v."${key}Su")
      results["${key}Wd"] = sumValues(data, v -> v."${key}Wd")
    }

    then:
    results["suSa"] == 12132.0
    results["suSu"] == 11416.0
    results["suWd"] == 11255.9

    results["trSa"] == 12054.9
    results["trSu"] == 11079.4
    results["trWd"] == 10783.3

    results["wiSa"] == 11546.0
    results["wiSu"] == 10742.0
    results["wiWd"] == 10223.7
  }

  def "The BDEW dynamization function for the profile H0 should work as expected"() {
    when:
    def dynamizedValue = BdewLoadValues.dynamization(value, dayOfTheYear)

    then:
    dynamizedValue == expectedValue

    where:
    dayOfTheYear | value  | expectedValue
    153          | 89.8d  | 76.3d // suSa, time: 00:15
    262          | 47.9d  | 42.1d // trWd, time: 01:45
    343          | 146.8d | 174.5d // wiSu, time: 18:15
  }

  def "The BDEW profile L0 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.L0)

    when:
    keys.each {
      key ->
      results["${key}Sa"] = sumValues(data, v -> v."${key}Sa")
      results["${key}Su"] = sumValues(data, v -> v."${key}Su")
      results["${key}Wd"] = sumValues(data, v -> v."${key}Wd")
    }

    then:
    results["suSa"] == 9536.1
    results["suSu"] == 10243.0
    results["suWd"] == 9985.2

    results["trSa"] == 10662.1
    results["trSu"] == 11012.7
    results["trWd"] == 10929.7

    results["wiSa"] == 11452.7
    results["wiSu"] == 12006.8
    results["wiWd"] == 11934.3
  }

  def "The BDEW profile L1 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.L1)

    when:
    keys.each {
      key ->
      results["${key}Sa"] = sumValues(data, v -> v."${key}Sa")
      results["${key}Su"] = sumValues(data, v -> v."${key}Su")
      results["${key}Wd"] = sumValues(data, v -> v."${key}Wd")
    }

    then:
    results["suSa"] == 9320.5
    results["suSu"] == 10011.8
    results["suWd"] == 9963.3

    results["trSa"] == 10484.5
    results["trSu"] == 10913.8
    results["trWd"] == 10874.8

    results["wiSa"] == 11717.6
    results["wiSu"] == 12241.9
    results["wiWd"] == 12010.0
  }

  def "The BDEW profile L2 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.L2)

    when:
    keys.each {
      key ->
      results["${key}Sa"] = sumValues(data, v -> v."${key}Sa")
      results["${key}Su"] = sumValues(data, v -> v."${key}Su")
      results["${key}Wd"] = sumValues(data, v -> v."${key}Wd")
    }

    then:
    results["suSa"] == 9645.7
    results["suSu"] == 10408.9
    results["suWd"] == 10090.1

    results["trSa"] == 10652.4
    results["trSu"] == 10980.3
    results["trWd"] == 10927.8

    results["wiSa"] == 11326.9
    results["wiSu"] == 11908.2
    results["wiWd"] == 11847.5
  }


  // helper methods

  private List<BdewLoadValues> read(BdewStandardLoadProfile profile) {
    source.getSourceData(Path.of("lpts_"+profile.key)).map { it -> factory.buildModel(new LoadProfileData<>(it, BdewLoadValues)).value }.toList()
  }

  private static double sumValues(List<BdewLoadValues> values, Function<BdewLoadValues, Double> extractor) {
    values.stream().map { extractor.apply(it) }.mapToDouble { it.doubleValue() }.sum()
  }
}
