/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.csv.CsvIndividualTimeSeriesMetaInformation
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme

import java.nio.file.Path

import static edu.ie3.datamodel.models.StandardUnits.ENERGY_PRICE

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.*
import edu.ie3.util.TimeUtil
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZoneId

class CsvTimeSeriesSourceTest extends Specification implements CsvTestDataMeta {

  def "The csv time series source is able to build time based values from simple data"() {
    given:
    def factory = new TimeBasedSimpleValueFactory(EnergyPriceValue)
    def source = new CsvTimeSeriesSource(";", timeSeriesFolderPath, new FileNamingStrategy(), UUID.fromString("2fcb3e53-b94a-4b96-bea4-c469e499f1a1"), Path.of("its_c_2fcb3e53-b94a-4b96-bea4-c469e499f1a1"), EnergyPriceValue, factory)
    def time = TimeUtil.withDefaults.toZonedDateTime("2019-01-01 00:00:00")
    def timeUtil = new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd'T'HH:mm:ss[.S[S][S]]'Z'")
    def fieldToValue = [
      "uuid" : "78ca078a-e6e9-4972-a58d-b2cadbc2df2c",
      "time" : timeUtil.toString(time),
      "price": "52.4"
    ]
    def expected = new TimeBasedValue(
        UUID.fromString("78ca078a-e6e9-4972-a58d-b2cadbc2df2c"),
        time,
        new EnergyPriceValue(Quantities.getQuantity(52.4, ENERGY_PRICE))
        )

    when:
    def actual = source.createTimeBasedValue(fieldToValue)

    then:
    actual.success
    actual.data.get() == expected
  }

  def "The factory method in csv time series source refuses to build time series with unsupported column type"() {
    given:
    def metaInformation = new CsvIndividualTimeSeriesMetaInformation(UUID.fromString("8bc9120d-fb9b-4484-b4e3-0cdadf0feea9"), ColumnScheme.WEATHER, Path.of("its_weather_8bc9120d-fb9b-4484-b4e3-0cdadf0feea9"))

    when:
    CsvTimeSeriesSource.getSource(";", timeSeriesFolderPath, fileNamingStrategy, metaInformation)

    then:
    def e = thrown(SourceException)
    e.message == "Unsupported column scheme '" + ColumnScheme.WEATHER + "'."
  }

  def "The factory method in csv time series source builds a time series source for all supported column types"() {
    given:
    def metaInformation = new CsvIndividualTimeSeriesMetaInformation(uuid, columnScheme, path)

    when:
    def actual = CsvTimeSeriesSource.getSource(";", timeSeriesFolderPath, fileNamingStrategy, metaInformation)

    then:
    actual.timeSeries.entries.size() == amountOfEntries
    actual.timeSeries.entries[0].value.class == valueClass

    where:
    uuid                                                    | columnScheme                                | path                                                    || amountOfEntries | valueClass
    UUID.fromString("2fcb3e53-b94a-4b96-bea4-c469e499f1a1") | ColumnScheme.ENERGY_PRICE                   | Path.of("its_c_2fcb3e53-b94a-4b96-bea4-c469e499f1a1")   || 2               | EnergyPriceValue
    UUID.fromString("c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0") | ColumnScheme.HEAT_DEMAND                    | Path.of("its_h_c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0")   || 2               | HeatDemandValue
    UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5") | ColumnScheme.ACTIVE_POWER                   | Path.of("its_p_9185b8c1-86ba-4a16-8dea-5ac898e8caa5")   || 2               | PValue
    UUID.fromString("76c9d846-797c-4f07-b7ec-2245f679f5c7") | ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND   | Path.of("its_ph_76c9d846-797c-4f07-b7ec-2245f679f5c7")  || 2               | HeatAndPValue
    UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26") | ColumnScheme.APPARENT_POWER                 | Path.of("its_pq_3fbfaa97-cff4-46d4-95ba-a95665e87c26")  || 2               | SValue
    UUID.fromString("46be1e57-e4ed-4ef7-95f1-b2b321cb2047") | ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND | Path.of("its_pqh_46be1e57-e4ed-4ef7-95f1-b2b321cb2047") || 2               | HeatAndSValue
  }
}