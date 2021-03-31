/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.sink

import edu.ie3.datamodel.io.connectors.InfluxDbConnector
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.result.ResultEntity
import edu.ie3.datamodel.models.result.connector.LineResult
import edu.ie3.datamodel.models.result.system.ChpResult
import edu.ie3.datamodel.models.timeseries.TimeSeries
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.value.Value
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.util.TimeUtil
import org.influxdb.dto.Query
import org.testcontainers.containers.InfluxDBContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZoneId
import java.time.ZonedDateTime

@Testcontainers
class InfluxDbSinkIT extends Specification {

	@Shared
	InfluxDBContainer influxDbContainer = new InfluxDBContainer("1.8.4")
	.withAuthEnabled(false)
	.withDatabase("test_out")
	.withExposedPorts(8086) as InfluxDBContainer

	@Shared
	InfluxDbConnector connector

	@Shared
	EntityPersistenceNamingStrategy entityPersistenceNamingStrategy

	@Shared
	InfluxDbSink sink

	def setupSpec() {
		connector = new InfluxDbConnector(influxDbContainer.url,"test_out", "test_scenario")
		sink = new InfluxDbSink(connector)
		entityPersistenceNamingStrategy = new EntityPersistenceNamingStrategy()
	}


	def "The test container can establish a valid connection"() {
		when:
		def connector = new InfluxDbConnector(influxDbContainer.url,"test_weather", "test_scenario")
		then:
		connector.connectionValid
	}

	def "An InfluxDbSink can persist a ResultEntity"() {
		given:
		def lineResult1 = new LineResult(ZonedDateTime.of(2020, 5, 3, 14, 18, 0, 0, ZoneId.of("UTC")),
				UUID.randomUUID(),
				Quantities.getQuantity(1.13d, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
				Quantities.getQuantity(1.23d, StandardUnits.ELECTRIC_CURRENT_ANGLE),
				Quantities.getQuantity(20.13d, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
				null)
		when:
		sink.persist(lineResult1)
		sink.flush()
		def key = entityPersistenceNamingStrategy.getEntityName(LineResult).get().trim().replaceAll("\\W", "_")
		def queryResult = connector.getSession().query(new Query("SELECT * FROM " + key))
		def parsedResults = InfluxDbConnector.parseQueryResult(queryResult)
		def fieldMap = parsedResults.get(key).first()
		then:
		parsedResults.size() == 1
		parsedResults.get(key).size() == 1
		mapMatchesLineResultEntity(fieldMap, lineResult1)
		cleanup:
		connector.getSession().query(new Query("DELETE FROM " + key))
	}

	def "An InfluxDbSink can persist multiple different ResultEntities"() {
		given:
		def lineResult1 = new LineResult(ZonedDateTime.of(2020, 5, 3, 14, 18, 0, 0, ZoneId.of("UTC")),
				UUID.randomUUID(),
				Quantities.getQuantity(1.13d, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
				Quantities.getQuantity(1.23d, StandardUnits.ELECTRIC_CURRENT_ANGLE),
				Quantities.getQuantity(20.13d, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
				null)
		def lineResult2 = new LineResult(ZonedDateTime.of(2020, 5, 3, 14, 18, 30, 0, ZoneId.of("UTC")),
				UUID.randomUUID(),
				Quantities.getQuantity(1.13d, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
				Quantities.getQuantity(1.23d, StandardUnits.ELECTRIC_CURRENT_ANGLE),
				Quantities.getQuantity(20.13d, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
				null)
		def lineResult3 = new LineResult(ZonedDateTime.of(2020, 5, 3, 14, 19, 0, 0, ZoneId.of("UTC")),
				UUID.randomUUID(),
				Quantities.getQuantity(1.13d, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
				Quantities.getQuantity(1.23d, StandardUnits.ELECTRIC_CURRENT_ANGLE),
				Quantities.getQuantity(20.13d, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
				null)
		def chpResult1 = new ChpResult(ZonedDateTime.of(2020, 5, 3, 14, 18, 0, 0, ZoneId.of("UTC")),
				UUID.randomUUID(),
				Quantities.getQuantity(42.24, StandardUnits.ACTIVE_POWER_RESULT),
				Quantities.getQuantity(-42.24, StandardUnits.REACTIVE_POWER_RESULT),
				Quantities.getQuantity(1.01, StandardUnits.Q_DOT_RESULT)
				)
		def chpResult2 = new ChpResult(ZonedDateTime.of(2020, 5, 3, 14, 19, 0, 0, ZoneId.of("UTC")),
				UUID.randomUUID(),
				Quantities.getQuantity(24.42, StandardUnits.ACTIVE_POWER_RESULT),
				Quantities.getQuantity(-24.42, StandardUnits.REACTIVE_POWER_RESULT),
				Quantities.getQuantity(1.01, StandardUnits.Q_DOT_RESULT))
		def entities = [
			lineResult1,
			lineResult2,
			lineResult3,
			chpResult1,
			chpResult2
		]
		when:
		sink.persistAll(entities)
		def key_line = entityPersistenceNamingStrategy.getEntityName(LineResult).get().trim().replaceAll("\\W", "_")
		def key_chp = entityPersistenceNamingStrategy.getEntityName(ChpResult).get().trim().replaceAll("\\W", "_")
		def queryResult = connector.getSession().query(new Query("SELECT * FROM " + key_line + ", " + key_chp))
		def parsedResults = InfluxDbConnector.parseQueryResult(queryResult)
		def lineResults = parsedResults.get(key_line)
		def chpResults = parsedResults.get(key_chp)
		then:
		lineResults.size() == 3
		lineResults.any{mapMatchesLineResultEntity(it, lineResult1)}
		lineResults.any{mapMatchesLineResultEntity(it, lineResult2)}
		lineResults.any{mapMatchesLineResultEntity(it, lineResult3)}
		chpResults.size() == 2
		chpResults.any{mapMatchesChpResultEntity(it, chpResult1)}
		chpResults.any{mapMatchesChpResultEntity(it, chpResult2)}
		cleanup:
		connector.getSession().query(new Query("DELETE FROM " + key_line + ", " + key_chp))
	}

	def "An InfluxDbSink can persist a TimeSeries"() {
		given:
		TimeBasedValue<PValue> p1 = new TimeBasedValue(ZonedDateTime.of(2020, 5, 3, 14, 18, 0, 0, ZoneId.of("UTC")),
				new PValue(Quantities.getQuantity(5d, StandardUnits.ACTIVE_POWER_IN)))
		TimeBasedValue<PValue> p2 = new TimeBasedValue(ZonedDateTime.of(2020, 5, 3, 14, 18, 30, 0, ZoneId.of("UTC")),
				new PValue(Quantities.getQuantity(10d, StandardUnits.ACTIVE_POWER_IN)))
		TimeBasedValue<PValue> p3 = new TimeBasedValue(ZonedDateTime.of(2020, 5, 3, 14, 19, 0, 0, ZoneId.of("UTC")),
				new PValue(Quantities.getQuantity(-5d, StandardUnits.ACTIVE_POWER_IN)))
		IndividualTimeSeries<PValue> timeSeries = new IndividualTimeSeries(UUID.randomUUID(), [p1, p2, p3] as Set<TimeBasedValue>)
		when:
		sink.persistTimeSeries(timeSeries)
		def key = entityPersistenceNamingStrategy.getEntityName(timeSeries).get().trim().replaceAll("\\W", "_")
		def queryResult = connector.getSession().query(new Query("SELECT * FROM " + key))
		def parsedResults = InfluxDbConnector.parseQueryResult(queryResult)
		def pValuesMap = parsedResults.get(key)
		then:
		parsedResults.size() == 1
		parsedResults.get(key).size() == 3
		pValuesMap.any{mapMatchesTimeBasedValue(it, p1)}
		pValuesMap.any{mapMatchesTimeBasedValue(it, p2)}
		pValuesMap.any{mapMatchesTimeBasedValue(it, p3)}
		cleanup:
		connector.getSession().query(new Query("DELETE FROM " + key))
	}

	def "An InfluxDbSink will use the class name if the NamingStrategy is failing"() {
		given:
		def lineResult1 = new LineResult(ZonedDateTime.of(2020, 5, 3, 14, 18, 0, 0, ZoneId.of("UTC")),
				UUID.randomUUID(),
				Quantities.getQuantity(1.13d, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
				Quantities.getQuantity(1.23d, StandardUnits.ELECTRIC_CURRENT_ANGLE),
				Quantities.getQuantity(20.13d, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
				null)
		TimeBasedValue<PValue> p1 = new TimeBasedValue(ZonedDateTime.of(2020, 5, 3, 14, 18, 0, 0, ZoneId.of("UTC")),
				new PValue(Quantities.getQuantity(5d, StandardUnits.ACTIVE_POWER_IN)))
		IndividualTimeSeries<PValue> timeSeries = new IndividualTimeSeries(UUID.randomUUID(), [p1] as Set<TimeBasedValue>)

		def sinkWithEmptyNamingStrategy = new InfluxDbSink(connector, new EmptyFileNamingStrategy())
		when:
		sinkWithEmptyNamingStrategy.persist(lineResult1)
		sinkWithEmptyNamingStrategy.persist(timeSeries)
		sinkWithEmptyNamingStrategy.flush()

		def key_lineresult = lineResult1.getClass().simpleName
		def key_timeseries = timeSeries.getEntries().iterator().next().getValue().getClass().simpleName
		def queryResult = connector.getSession().query(new Query("SELECT * FROM " + key_lineresult))
		def parsedResults_lineresult = InfluxDbConnector.parseQueryResult(queryResult)
		def fieldMap_lineresult = parsedResults_lineresult.get(key_lineresult).first()
		queryResult = connector.getSession().query(new Query("SELECT * FROM " + key_timeseries))
		def parsedResults_timeseries = InfluxDbConnector.parseQueryResult(queryResult)
		def fieldMap_timeseries = parsedResults_timeseries.get(key_timeseries)
		then:
		parsedResults_lineresult.size() == 1
		parsedResults_lineresult.get(key_lineresult).size() == 1
		mapMatchesLineResultEntity(fieldMap_lineresult, lineResult1)
		parsedResults_timeseries.size() == 1
		parsedResults_timeseries.get(key_timeseries).size() == 1
		fieldMap_timeseries.any{mapMatchesTimeBasedValue(it, p1)}
		cleanup:
		connector.getSession().query(new Query("DELETE FROM " + key_timeseries + ", " + key_lineresult))
	}

	def "An InfluxDbSink will not try to persist a InputEntity"() {
		given:
		def node = new NodeInput(UUID.randomUUID(), "node", Quantities.getQuantity(5d, StandardUnits.TARGET_VOLTAGE_MAGNITUDE),
				false, NodeInput.DEFAULT_GEO_POSITION, GermanVoltageLevelUtils.LV, 112)
		when:
		sink.persist(node)
		def queryResult = connector.getSession().query(new Query("SHOW MEASUREMENTS"))
		then:
		queryResult.getResults().get(0).getSeries() == null
	}

	def "An InfluxDbSink should terminate the corresponding session inside its connector correctly"() {
		when:
		sink.shutdown()

		then:
		// after shutdown the batch processor must be disabled and empty
		!sink.connector.getSession().batchEnabled
		sink.connector.getSession().batchProcessor.queue.isEmpty()
	}


	static def mapMatchesLineResultEntity(Map<String, String> fieldMap, LineResult lineResult) {
		def timeUtil = new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd'T'HH:mm:ss[.S[S][S]]'Z'")
		timeUtil.toZonedDateTime(fieldMap.get("time")) == lineResult.getTime()
		fieldMap.get("uuid") == lineResult.getUuid().toString()
		fieldMap.get("input_model") == lineResult.getInputModel().toString()
		def iAMagStr = fieldMap.get("iAMag")
		def iAAngStr = fieldMap.get("iAAng")
		def iBMagStr = fieldMap.get("iBMag")
		def iBAngStr = fieldMap.get("iBAng")
		if(iAMagStr== null || iAMagStr.empty) lineResult.getiAMag() == null
		else Double.parseDouble(iAMagStr) == lineResult.getiAMag().getValue()
		if(iAAngStr== null || iAAngStr.empty) lineResult.getiAAng() == null
		else Double.parseDouble(iAAngStr) == lineResult.getiAAng().getValue()
		if(iBMagStr== null || iBMagStr.empty) lineResult.getiBMag() == null
		else Double.parseDouble(iBMagStr) == lineResult.getiBMag().getValue()
		if(iBAngStr== null || iBAngStr.empty) lineResult.getiBAng() == null
		else Double.parseDouble(iBAngStr) == lineResult.getiBAng().getValue()
	}


	static def mapMatchesChpResultEntity(Map<String, String> fieldMap, ChpResult chpResult) {
		def timeUtil = new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd'T'HH:mm:ss[.S[S][S]]'Z'")
		timeUtil.toZonedDateTime(fieldMap.get("time")) == chpResult.getTime()
		fieldMap.get("uuid") == chpResult.getUuid().toString()
		fieldMap.get("input_model") == chpResult.getInputModel().toString()
		def pStr = fieldMap.get("p")
		def qStr = fieldMap.get("q")
		if(pStr== null || pStr.empty) chpResult.getP() == null
		else Double.parseDouble(pStr) == chpResult.getP().getValue()
		if(qStr== null || qStr.empty) chpResult.getQ() == null
		else Double.parseDouble(qStr) == chpResult.getQ().getValue()
	}

	static def mapMatchesTimeBasedValue(Map<String, String> fieldMap, TimeBasedValue<PValue> pVal) {
		def timeUtil = new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd'T'HH:mm:ss[.S[S][S]]'Z'")
		timeUtil.toZonedDateTime(fieldMap.get("time")) == pVal.getTime()
		fieldMap.get("uuid") == pVal.getUuid().toString()
		def pStr = fieldMap.get("p")
		if(pStr == null || pStr.empty) pVal.getValue().getP() == Optional.empty()
		else Double.parseDouble(pStr) == pVal.getValue().getP().get().getValue()
	}

	//Always return an empty Optional for results
	class EmptyFileNamingStrategy extends EntityPersistenceNamingStrategy {
		@Override
		Optional<String> getResultEntityName(Class<? extends ResultEntity> resultEntityClass) {
			return Optional.empty()
		}

		@Override
		<T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value> Optional<String> getEntityName(T timeSeries) {
			return Optional.empty()
		}
	}
}
