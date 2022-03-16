/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.csv.CsvIndividualTimeSeriesMetaInformation
import edu.ie3.datamodel.io.naming.FileNamingStrategy
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme
import spock.lang.Shared
import spock.lang.Specification

class CsvTimeSeriesTypeSourceIT extends Specification implements CsvTestDataMeta {
	@Shared
	CsvTimeSeriesTypeSource source

	def setupSpec() {
		source = new CsvTimeSeriesTypeSource(";", timeSeriesFolderPath, new FileNamingStrategy())
	}

	def "A csv time series type source returns correct mapping of time series"() {
		given:
		def expectedTimeSeries = Set.of(
				new CsvIndividualTimeSeriesMetaInformation(UUID.fromString("2fcb3e53-b94a-4b96-bea4-c469e499f1a1"), ColumnScheme.ENERGY_PRICE, 'its_c_2fcb3e53-b94a-4b96-bea4-c469e499f1a1'),
				new CsvIndividualTimeSeriesMetaInformation(UUID.fromString("76c9d846-797c-4f07-b7ec-2245f679f5c7"), ColumnScheme.ACTIVE_POWER_AND_HEAT_DEMAND, 'its_ph_76c9d846-797c-4f07-b7ec-2245f679f5c7'),
				new CsvIndividualTimeSeriesMetaInformation(UUID.fromString("c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0"), ColumnScheme.HEAT_DEMAND, 'its_h_c8fe6547-fd85-4fdf-a169-e4da6ce5c3d0'),
				new CsvIndividualTimeSeriesMetaInformation(UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5"), ColumnScheme.ACTIVE_POWER, 'its_p_9185b8c1-86ba-4a16-8dea-5ac898e8caa5'),
				new CsvIndividualTimeSeriesMetaInformation(UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26"), ColumnScheme.APPARENT_POWER, 'its_pq_3fbfaa97-cff4-46d4-95ba-a95665e87c26'),
				new CsvIndividualTimeSeriesMetaInformation(UUID.fromString("46be1e57-e4ed-4ef7-95f1-b2b321cb2047"), ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND, 'its_pqh_46be1e57-e4ed-4ef7-95f1-b2b321cb2047'),
				new CsvIndividualTimeSeriesMetaInformation(UUID.fromString("1061af70-1c03-46e1-b960-940b956c429f"), ColumnScheme.APPARENT_POWER, 'its_pq_1061af70-1c03-46e1-b960-940b956c429f')
		)

		when:
		def actual = source.timeSeriesMetaInformation

		then:
		actual.size() == 7
		actual.every {
			it.key == it.value.uuid &&
					expectedTimeSeries.contains(it.value)
		}
	}
}
