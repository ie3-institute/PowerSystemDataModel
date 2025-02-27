/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import static edu.ie3.test.common.TimeSeriesSourceTestData.*

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.models.value.HeatAndPValue
import edu.ie3.util.interval.ClosedInterval
import spock.lang.Shared
import spock.lang.Specification

import java.nio.file.Path

class CsvTimeSeriesSourceIT extends Specification implements CsvTestDataMeta {

  @Shared
  CsvTimeSeriesSource source

  @Shared
  TimeBasedSimpleValueFactory<HeatAndPValue> factory

  def setup() {
    factory = new TimeBasedSimpleValueFactory<>(HeatAndPValue)
    source = new CsvTimeSeriesSource(";", timeSeriesFolderPath, new FileNamingStrategy(), UUID.fromString("76c9d846-797c-4f07-b7ec-2245f679f5c7"), Path.of("its_ph_76c9d846-797c-4f07-b7ec-2245f679f5c7"), HeatAndPValue, factory)
  }

  def "A csv time series source throw an Exception, if the file cannot be found"() {
    given:
    def filePath = Path.of("file/not/found.csv")

    when:
    source.buildIndividualTimeSeries(UUID.fromString("fbc59b5b-9307-4fb4-a406-c1f08f26fee5"), filePath, { null })

    then:
    def ex = thrown(SourceException)
    ex.message == "Unable to find file '" + filePath + "'."
    ex.cause.class == FileNotFoundException
  }

  def "A csv time series source is able to read in a proper file correctly"() {
    given:
    def filePath = Path.of("its_ph_76c9d846-797c-4f07-b7ec-2245f679f5c7")
    def tsUuid = UUID.fromString("76c9d846-797c-4f07-b7ec-2245f679f5c7")

    when:
    def actual = source.buildIndividualTimeSeries(tsUuid, filePath, { source.createTimeBasedValue(it) })

    then:
    noExceptionThrown()
    actual.entries.size() == 2
  }

  def "Construction a csv time series source with malicious parameters, leads to IllegalArgumentException"() {
    when:
    new CsvTimeSeriesSource(";", timeSeriesFolderPath, new FileNamingStrategy(), UUID.fromString("fbc59b5b-9307-4fb4-a406-c1f08f26fee5"), Path.of("file/not/found"), HeatAndPValue, factory)

    then:
    def e = thrown(IllegalArgumentException)
    e.message == "Unable to obtain time series with UUID 'fbc59b5b-9307-4fb4-a406-c1f08f26fee5'. Please check arguments!"
    e.cause.class == SourceException
  }

  def "A csv time series source is able to return a time series for a period of interest"() {
    given:
    def interval = new ClosedInterval(TIME_15MIN, TIME_15MIN)

    when:
    def actual = source.getTimeSeries(interval)

    then:
    actual.entries.size() == 1
  }

  def "A csv time series source is able to return a single value, if it is covered"() {
    when:
    def actual = source.getValue(TIME_15MIN)

    then:
    actual.present
    actual.get() == PH_VALUE_15MIN
  }

  def "A csv time series source is able to return the previous value for a given time"() {
    when:
    def actual = source.getPreviousTimeBasedValue(TIME_15MIN)

    then:
    actual.isPresent()
    actual.get().time == TIME_00MIN
    actual.get().value == PH_VALUE_00MIN
  }

  def "A csv time series source is able to return the next value for a given time"() {
    when:
    def actual = source.getNextTimeBasedValue(TIME_00MIN)

    then:
    actual.isPresent()
    actual.get().time == TIME_15MIN
    actual.get().value == PH_VALUE_15MIN
  }
}