/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.FileNamingStrategy
import edu.ie3.datamodel.io.connectors.CsvFileConnector
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource
import spock.lang.Shared
import spock.lang.Specification

class CsvTimeSeriesMappingSourceIT extends Specification implements CsvTestDataMeta {
	@Shared
	TimeSeriesMappingSource source

	def setupSpec() {
		source = new CsvTimeSeriesMappingSource(";", timeSeriesFolderPath, new FileNamingStrategy())
	}

	def "The csv time series mapping source is able to provide a valid time series mapping from files"() {
		given:
		def expectedMapping = [
			(UUID.fromString("b86e95b0-e579-4a80-a534-37c7a470a409")) : UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5"),
			(UUID.fromString("c7ebcc6c-55fc-479b-aa6b-6fa82ccac6b8")) : UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26"),
			(UUID.fromString("90a96daa-012b-4fea-82dc-24ba7a7ab81c")) : UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26")
		]

		when:
		def actualMapping = source.mapping

		then:
		actualMapping.size() == expectedMapping.size()

		expectedMapping.entrySet().stream().allMatch { entry ->
			actualMapping.containsKey(entry.key) && actualMapping.get(entry.key) == entry.value
		}
	}

	def "The csv time series mapping source returns empty optional on not covered model"() {
		given:
		def modelUuid = UUID.fromString("60b9a3da-e56c-40ff-ace7-8060cea84baf")

		when:
		def actual = source.getTimeSeriesUuid(modelUuid)

		then:
		!actual.present
	}

	def "The csv time series mapping source is able to return the correct time series uuid"() {
		given:
		def modelUuid = UUID.fromString("c7ebcc6c-55fc-479b-aa6b-6fa82ccac6b8")
		def expectedUuid = UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26")

		when:
		def actual = source.getTimeSeriesUuid(modelUuid)

		then:
		actual.present
		actual.get() == expectedUuid
	}

	def "A csv time series mapping source returns empty optional on meta information for non existing time series"() {
		given:
		def timeSeriesUuid = UUID.fromString("f5eb3be5-98db-40de-85b0-243507636cd5")

		when:
		def actual = source.getTimeSeriesMetaInformation(timeSeriesUuid)

		then:
		!actual.present
	}

	def "A csv time series mapping source returns correct meta information for an existing time series"() {
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
}
