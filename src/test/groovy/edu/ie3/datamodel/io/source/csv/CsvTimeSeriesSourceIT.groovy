/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.connectors.CsvFileConnector
import edu.ie3.datamodel.io.csv.FileNamingStrategy
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme
import edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation
import edu.ie3.datamodel.models.value.HeatAndPValue
import spock.lang.Shared
import spock.lang.Specification


class CsvTimeSeriesSourceIT extends Specification implements CsvTestDataMeta {

	@Shared
	CsvTimeSeriesSource source

	def setup() {
		source = new CsvTimeSeriesSource(";", timeSeriesFolderPath, new FileNamingStrategy())
	}

	def "A csv time series source throw an Exception, if the file cannot be found"() {
		given:
		def filePath = "file/not/found.csv"

		when:
		source.buildIndividualTimeSeries(UUID.fromString("fbc59b5b-9307-4fb4-a406-c1f08f26fee5"), filePath, { null })

		then:
		def ex = thrown(SourceException)
		ex.message == "Unable to find a file with path '" + filePath + "'."
		ex.cause.class == FileNotFoundException
	}

	def "A csv time series source is able to read in a proper file correctly"() {
		given:
		def filePath = "its_ph_76c9d846-797c-4f07-b7ec-2245f679f5c7"
		def tsUuid = UUID.fromString("76c9d846-797c-4f07-b7ec-2245f679f5c7")

		when:
		def actual = source.buildIndividualTimeSeries(tsUuid, filePath, { source.buildTimeBasedValue(it, HeatAndPValue.class, source.heatAndPValueFactory) })

		then:
		noExceptionThrown()
		actual.getEntries().size() == 2
	}

	def "A csv time series source returns empty optional, if there is no time series for a given model"() {
		given:
		def modelUuid = UUID.fromString("da01c973-46d0-458d-8bd3-c06831208e19")

		when:
		def actual = source.getTimeSeriesUuid(modelUuid)

		then:
		!actual.present
	}

	def "A csv time series source is able to get correct UUID mapping for a given model"() {
		given:
		def modelUuid = UUID.fromString("c7ebcc6c-55fc-479b-aa6b-6fa82ccac6b8")
		def expected = UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26")

		when:
		def actual = source.getTimeSeriesUuid(modelUuid)

		then:
		actual.present
		actual.get() == expected
	}

	def "A csv time series source returns empty optional on meta information for non existing time series"() {
		given:
		def timeSeriesUuid = UUID.fromString("f5eb3be5-98db-40de-85b0-243507636cd5")

		when:
		def actual = source.getTimeSeriesMetaInformation(timeSeriesUuid)

		then:
		!actual.present
	}

	def "A csv time series source returns correct meta information for an existing time series"() {
		given:
		def timeSeriesUuid = UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26")
		def expected = new CsvFileConnector.CsvIndividualTimeSeriesMetaInformation(
				timeSeriesUuid,
				ColumnScheme.APPARENT_POWER,
				"its_pq_3fbfaa97-cff4-46d4-95ba-a95665e87c26")

		when:
		def actual = source.getTimeSeriesMetaInformation(timeSeriesUuid)

		then:
		actual.present
		actual.get() == expected
	}

	def "A csv time series source returns empty optional on attempt to read time series with malformed meta information"() {
		given:
		def metaInformation = new IndividualTimeSeriesMetaInformation(UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26"), ColumnScheme.APPARENT_POWER)

		when:
		def actual = source.getTimeSeries(metaInformation)

		then:
		!actual.present
	}

	def "A csv time series source returns empty optional on attempt to read non supported time series"() {
		given:
		def metaInformation = new CsvFileConnector.CsvIndividualTimeSeriesMetaInformation(
				UUID.fromString("8bc9120d-fb9b-4484-b4e3-0cdadf0feea9"),
				ColumnScheme.WEATHER,
				"its_weather_8bc9120d-fb9b-4484-b4e3-0cdadf0feea9.csv"
				)

		when:
		def actual = source.getTimeSeries(metaInformation)

		then:
		!actual.present
	}

	def "A csv time series source is able to read time series of different types properly"() {
		given:
		def metaInformation = new CsvFileConnector.CsvIndividualTimeSeriesMetaInformation(
				uuid,
				columnScheme,
				path
				)

		when:
		def actual = source.getTimeSeries(metaInformation)

		then:
		actual.present
		actual.get().getEntries().size() == amountOfEntries

		where:
		uuid                                                    | columnScheme                                | path                                           || amountOfEntries
		UUID.fromString("2fcb3e53-b94a-4b96-bea4-c469e499f1a1") | ColumnScheme.ENERGY_PRICE                   | "its_c_2fcb3e53-b94a-4b96-bea4-c469e499f1a1"   || 2
		UUID.fromString("c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0") | ColumnScheme.HEAT_DEMAND                    | "its_h_c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0"   || 2
		UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5") | ColumnScheme.ACTIVE_POWER                   | "its_p_9185b8c1-86ba-4a16-8dea-5ac898e8caa5"   || 2
		UUID.fromString("76c9d846-797c-4f07-b7ec-2245f679f5c7") | ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND   | "its_ph_76c9d846-797c-4f07-b7ec-2245f679f5c7"  || 2
		UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26") | ColumnScheme.APPARENT_POWER                 | "its_pq_3fbfaa97-cff4-46d4-95ba-a95665e87c26"  || 2
		UUID.fromString("46be1e57-e4ed-4ef7-95f1-b2b321cb2047") | ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND | "its_pqh_46be1e57-e4ed-4ef7-95f1-b2b321cb2047" || 2
	}
}
