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
  private Map results = [:]

  def setupSpec() {
    Path resourcePath = Path.of(".", "src", "main", "resources", "load")
    source = new CsvDataSource(",", resourcePath, new FileNamingStrategy())
  }

  // profiles with the 1999 scheme

  def "The BDEW profile G0 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G0)

    when:
    BdewLoadValues.BdewScheme.BDEW1999.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
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
    BdewLoadValues.BdewScheme.BDEW1999.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
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
    BdewLoadValues.BdewScheme.BDEW1999.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
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
    BdewLoadValues.BdewScheme.BDEW1999.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
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
    BdewLoadValues.BdewScheme.BDEW1999.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
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
    BdewLoadValues.BdewScheme.BDEW1999.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
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
    BdewLoadValues.BdewScheme.BDEW1999.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
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
    BdewLoadValues.BdewScheme.BDEW1999.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
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
    BdewLoadValues.BdewScheme.BDEW1999.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
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
    BdewLoadValues.BdewScheme.BDEW1999.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
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
    BdewLoadValues.BdewScheme.BDEW1999.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
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

  // profiles with the 2025 scheme

  def "The BDEW profile G25 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.G25)
    Map results = [:]

    when:
    BdewLoadValues.BdewScheme.BDEW2025.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
    }

    then:
    results["janSa"] == 2138.953
    results["janSu"] == 1606.713
    results["janWd"] == 3554.476

    results["febSa"] == 2109.825
    results["febSu"] == 1627.338
    results["febWd"] == 3510.431

    results["marSa"] == 2062.480
    results["marSu"] == 1588.284
    results["marWd"] == 3406.751

    results["aprSa"] == 1957.4089999999999 // 1957.409
    results["aprSu"] == 1513.385
    results["aprWd"] == 3178.702

    results["maySa"] == 1864.490
    results["maySu"] == 1439.740
    results["mayWd"] == 3004.823

    results["junSa"] == 1867.560
    results["junSu"] == 1569.091
    results["junWd"] == 2983.980

    results["julSa"] == 1859.625
    results["julSu"] == 1434.583
    results["julWd"] == 2818.939

    results["augSa"] == 1865.794
    results["augSu"] == 1440.782
    results["augWd"] == 2880.367

    results["sepSa"] ==1844.127
    results["sepSu"] ==1434.353
    results["sepWd"] ==2989.369

    results["octSa"] ==	1945.162
    results["octSu"] ==	1481.594
    results["octWd"] == 3086.210

    results["novSa"] == 2087.148
    results["novSu"] == 1721.967
    results["novWd"] == 3515.913

    results["decSa"] == 2187.194
    results["decSu"] == 1652.092
    results["decWd"] ==	3479.094
  }

  def "The BDEW profile H25 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.H25)
    Map results = [:]

    when:
    BdewLoadValues.BdewScheme.BDEW2025.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
    }

    then:
    results["janSa"] == 2842.961
    results["janSu"] == 2903.033
    results["janWd"] == 2476.450

    results["febSa"] == 2844.567
    results["febSu"] == 2944.478
    results["febWd"] == 2448.516

    results["marSa"] == 2784.877
    results["marSu"] == 2866.433
    results["marWd"] == 2398.8849999999998 // 2398.885

    results["aprSa"] == 2961.768
    results["aprSu"] == 3047.309
    results["aprWd"] == 2554.9519999999998 // 2554.952

    results["maySa"] == 3024.437
    results["maySu"] == 3087.454
    results["mayWd"] == 2632.023

    results["junSa"] == 3139.621
    results["junSu"] == 3216.223
    results["junWd"] == 2773.430

    results["julSa"] == 3277.933
    results["julSu"] == 3361.232
    results["julWd"] == 2915.474

    results["augSa"] == 3170.155
    results["augSu"] == 3254.218
    results["augWd"] == 2820.521

    results["sepSa"] == 3040.361
    results["sepSu"] == 3190.438
    results["sepWd"] == 2656.074

    results["octSa"] ==	2972.852
    results["octSu"] ==	3127.245
    results["octWd"] == 2633.577

    results["novSa"] == 2944.428
    results["novSu"] == 3042.968
    results["novWd"] == 2541.863

    results["decSa"] == 2816.414
    results["decSu"] ==	2936.746
    results["decWd"] ==	2536.519
  }

  def "The BDEW profile L25 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.L25)
    Map results = [:]

    when:
    BdewLoadValues.BdewScheme.BDEW2025.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
    }

    then:
    results["janSa"] == 2863.175
    results["janSu"] == 3001.700
    results["janWd"] == 2983.575

    results["febSa"] == 2863.175
    results["febSu"] == 3001.700
    results["febWd"] == 2983.575

    results["marSa"] == 2797.291
    results["marSu"] == 2918.857
    results["marWd"] == 2899.859

    results["aprSa"] == 2665.525
    results["aprSu"] == 2753.175
    results["aprWd"] == 2732.425

    results["maySa"] == 2524.8
    results["maySu"] == 2656.983
    results["mayWd"] ==	2614.388

    results["junSa"] == 2384.025
    results["junSu"] ==	2560.750
    results["junWd"] == 2496.300

    results["julSa"] == 2384.025
    results["julSu"] ==	2560.750
    results["julWd"] == 2496.300

    results["augSa"] == 2384.025
    results["augSu"] == 2560.750
    results["augWd"] == 2496.300

    results["sepSa"] == 2524.8
    results["sepSu"] == 2656.983
    results["sepWd"] == 2614.388

    results["octSa"] ==	2665.525
    results["octSu"] ==	2753.175
    results["octWd"] ==	2732.425

    results["novSa"] == 2863.175
    results["novSu"] ==	3001.700
    results["novWd"] ==	2983.575

    results["decSa"] == 2863.175
    results["decSu"] ==	3001.700
    results["decWd"] ==	2983.575
  }

  def "The BDEW profile P25 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.P25)
    Map results = [:]

    when:
    BdewLoadValues.BdewScheme.BDEW2025.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
    }
    then:
    results["janSa"] == 3937.182
    results["janSu"] ==	3826.787
    results["janWd"] == 3676.576

    results["febSa"] == 3221.188
    results["febSu"] ==	3352.444
    results["febWd"] == 3022.281

    results["marSa"] == 2776.773
    results["marSu"] ==	2837.057
    results["marWd"] ==	2680.670

    results["aprSa"] == 2521.392
    results["aprSu"] ==	2465.958
    results["aprWd"] ==	2316.198

    results["maySa"] == 1593.953
    results["maySu"] == 1640.973
    results["mayWd"] == 1612.535

    results["junSa"] == 1562.425
    results["junSu"] ==	1632.361
    results["junWd"] ==	1653.580

    results["julSa"] == 1858.290
    results["julSu"] == 1755.261
    results["julWd"] == 1685.281

    results["augSa"] == 1934.920
    results["augSu"] == 1961.067
    results["augWd"] ==	1842.870

    results["sepSa"] == 1960.462
    results["sepSu"] ==	2120.285
    results["sepWd"] ==	2042.071

    results["octSa"] ==	2762.563
    results["octSu"] ==	2610.414
    results["octWd"] ==	2570.650

    results["novSa"] == 3595.329
    results["novSu"] ==	3570.977
    results["novWd"] == 3463.9900000000002

    results["decSa"] == 4358.588
    results["decSu"] ==	4474.190
    results["decWd"] ==	4233.233
  }

  def "The BDEW profile S25 should be correct"() {
    given:
    def data = read(BdewStandardLoadProfile.S25)
    Map results = [:]

    when:
    BdewLoadValues.BdewScheme.BDEW2025.keys.each {
      key -> results["${key.fieldName}"] = sumValues(data, v -> v.get(key))
    }

    then:
    results["janSa"] == 5772.512
    results["janSu"] == 5675.793
    results["janWd"] == 5178.520

    results["febSa"] == 3873.464
    results["febSu"] == 3982.030
    results["febWd"] == 3526.293

    results["marSa"] == 2320.319
    results["marSu"] ==	2281.753
    results["marWd"] == 2231.144

    results["aprSa"] == 1925.482
    results["aprSu"] ==	1602.650
    results["aprWd"] == 1242.171

    results["maySa"] == 521.384
    results["maySu"] ==	517.552
    results["mayWd"] ==	528.821

    results["junSa"] == 469.686
    results["junSu"] ==	402.993
    results["junWd"] == 437.111

    results["julSa"] == 660.072
    results["julSu"] == 567.734
    results["julWd"] ==	549.828

    results["augSa"] == 789.160
    results["augSu"] == 784.639
    results["augWd"] ==	729.175

    results["sepSa"] == 846.852
    results["sepSu"] ==	810.538
    results["sepWd"] == 841.354

    results["octSa"] == 2532.381
    results["octSu"] ==	2158.282
    results["octWd"] == 2322.616

    results["novSa"] == 4641.930
    results["novSu"] ==	4351.746
    results["novWd"] ==	4624.885

    results["decSa"] == 6788.41
    results["decSu"] == 6777.526
    results["decWd"] ==	6455.487
  }

  // helper methods

  private List<BdewLoadValues> read(BdewStandardLoadProfile profile) {
    source.getSourceData(Path.of("lpts_"+profile.key)).map { it -> factory.buildModel(new LoadProfileData<>(it, BdewLoadValues)).value }.toList()
  }

  private static double sumValues(List<BdewLoadValues> values, Function<BdewLoadValues, Double> extractor) {
    values.stream().map { extractor.apply(it) }.mapToDouble { it.doubleValue() }.sum()
  }
}
