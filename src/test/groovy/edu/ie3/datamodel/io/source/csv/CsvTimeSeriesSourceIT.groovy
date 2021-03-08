/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import static edu.ie3.datamodel.models.StandardUnits.*
import edu.ie3.datamodel.io.connectors.CsvFileConnector
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme
import edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.timeseries.mapping.TimeSeriesMapping
import edu.ie3.datamodel.models.value.*
import edu.ie3.util.TimeUtil
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.nio.charset.StandardCharsets


class CsvTimeSeriesSourceIT extends Specification implements CsvTestDataMeta {

	@Shared
	CsvTimeSeriesSource source

	def setup() {
		source = new CsvTimeSeriesSource(";", timeSeriesFolderPath, new EntityPersistenceNamingStrategy())
	}

	def "The csv time series source is able to provide an individual time series from given field to object function"() {
		given:
		def heatAndSValueFunction = { fieldToValues -> source.buildTimeBasedValue(fieldToValues, HeatAndSValue, new TimeBasedSimpleValueFactory<>(HeatAndSValue)) }
		def tsUuid = UUID.fromString("46be1e57-e4ed-4ef7-95f1-b2b321cb2047")
		def filePath = new File(this.getClass().getResource( File.separator + "testTimeSeriesFiles" + File.separator + "its_pqh_46be1e57-e4ed-4ef7-95f1-b2b321cb2047.csv").toURI())
		def readingData = new CsvFileConnector.TimeSeriesReadingData(
				tsUuid,
				ColumnScheme.APPARENT_POWER_AND_HEAT_DEMAND,
				new BufferedReader(
				new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8), 16384)
				)
		def expected = new IndividualTimeSeries(
				tsUuid,
				[
					new TimeBasedValue(
					UUID.fromString("661ac594-47f0-4442-8d82-bbeede5661f7"),
					TimeUtil.withDefaults.toZonedDateTime("2020-01-01 00:00:00"),
					new HeatAndSValue(
					Quantities.getQuantity(1000.0, ACTIVE_POWER_IN),
					Quantities.getQuantity(329.0, REACTIVE_POWER_IN),
					Quantities.getQuantity(8.0, HEAT_DEMAND)

					)),
					new TimeBasedValue(
					UUID.fromString("5adcd6c5-a903-433f-b7b5-5fe669a3ed30"),
					TimeUtil.withDefaults.toZonedDateTime("2020-01-01 00:15:00"),
					new HeatAndSValue(
					Quantities.getQuantity(1250.0, ACTIVE_POWER_IN),
					Quantities.getQuantity(411.0, REACTIVE_POWER_IN),
					Quantities.getQuantity(12.0, HEAT_DEMAND)

					))] as Set
				)

		when:
		def actual = source.buildIndividualTimeSeries(readingData, heatAndSValueFunction)

		then:
		actual.with {
			assert uuid == tsUuid
			assert entries.size() == expected.entries.size()
			assert entries.containsAll(expected.entries)
		}
		/* Close the reader */
		readingData.reader.close()
	}

	def "The csv time series source is able to acquire all time series of a given type"() {
		given:
		def filePath0 = new File(this.getClass().getResource( File.separator + "testTimeSeriesFiles" + File.separator + "its_pq_1061af70-1c03-46e1-b960-940b956c429f.csv").toURI())
		def tsUuid0 = UUID.fromString("1061af70-1c03-46e1-b960-940b956c429f")
		def filePath1 = new File(this.getClass().getResource( File.separator + "testTimeSeriesFiles" + File.separator + "its_pq_3fbfaa97-cff4-46d4-95ba-a95665e87c26.csv").toURI())
		def tsUuid1 = UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26")
		def readingData = [
			new CsvFileConnector.TimeSeriesReadingData(
			tsUuid0,
			ColumnScheme.APPARENT_POWER,
			new BufferedReader(
			new InputStreamReader(new FileInputStream(filePath0), StandardCharsets.UTF_8), 16384)
			),
			new CsvFileConnector.TimeSeriesReadingData(
			tsUuid1,
			ColumnScheme.APPARENT_POWER,
			new BufferedReader(
			new InputStreamReader(new FileInputStream(filePath1), StandardCharsets.UTF_8), 16384)
			)
		] as Set
		def factory = new TimeBasedSimpleValueFactory<>(SValue)

		when:
		def actual = source.read(readingData, SValue, factory)

		then:
		Objects.nonNull(actual)
		actual.size() == 2
	}

	def "The csv time series source is able to acquire all time series"() {
		when:
		def actual = source.timeSeries

		then:
		Objects.nonNull(actual)
		actual.with {
			assert energyPrice.size() == 1
			assert heatAndApparentPower.size() == 1
			assert heatAndActivePower.size() == 1
			assert heat.size() == 1
			assert apparentPower.size() == 2
			assert activePower.size() == 1
		}
	}

	def "The csv time series source is able to provide either mapping an time series, that can be put together"() {
		when:
		def mappingEntries = source.mapping
		def timeSeries = source.timeSeries
		def mapping = new TimeSeriesMapping(mappingEntries, timeSeries.all)

		then:
		mapping.with {
			assert it.mapping.size() == 3
			assert it.mapping.containsKey(UUID.fromString("b86e95b0-e579-4a80-a534-37c7a470a409"))
			assert it.mapping.get(UUID.fromString("b86e95b0-e579-4a80-a534-37c7a470a409")).uuid == UUID.fromString("9185b8c1-86ba-4a16-8dea-5ac898e8caa5")
			assert it.mapping.containsKey(UUID.fromString("c7ebcc6c-55fc-479b-aa6b-6fa82ccac6b8"))
			assert it.mapping.get(UUID.fromString("c7ebcc6c-55fc-479b-aa6b-6fa82ccac6b8")).uuid == UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26")
			assert it.mapping.containsKey(UUID.fromString("90a96daa-012b-4fea-82dc-24ba7a7ab81c"))
			assert it.mapping.get(UUID.fromString("90a96daa-012b-4fea-82dc-24ba7a7ab81c")).uuid == UUID.fromString("3fbfaa97-cff4-46d4-95ba-a95665e87c26")
		}
	}
}
