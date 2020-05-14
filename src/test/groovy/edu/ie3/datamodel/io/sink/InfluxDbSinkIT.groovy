/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.sink

import edu.ie3.datamodel.io.FileNamingStrategy
import edu.ie3.datamodel.io.connectors.InfluxDbConnector
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.result.connector.LineResult
import edu.ie3.datamodel.models.result.system.ChpResult
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.PValue
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.util.TimeUtil
import org.influxdb.dto.Query
import org.testcontainers.containers.InfluxDBContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Testcontainers
class InfluxDbSinkIT extends Specification {

	@Shared
	InfluxDBContainer influxDbContainer = new InfluxDBContainer()
	.withAuthEnabled(false)
	.withDatabase("test_out")
	.withExposedPorts(8086)

	@Shared
	InfluxDbConnector connector

	@Shared
	FileNamingStrategy fileNamingStrategy

	@Shared
	InfluxDbSink sink

	def setupSpec() {
		connector = new InfluxDbConnector(influxDbContainer.url,"test_out", "test_scenario")
		fileNamingStrategy = new FileNamingStrategy();
		sink = new InfluxDbSink(connector, fileNamingStrategy)
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
		def key = fileNamingStrategy.getFileName(LineResult.class).get().trim().replaceAll("\\W", "_");
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
				Quantities.getQuantity(-42.24, StandardUnits.REACTIVE_POWER_RESULT))
		def chpResult2 = new ChpResult(ZonedDateTime.of(2020, 5, 3, 14, 19, 0, 0, ZoneId.of("UTC")),
				UUID.randomUUID(),
				Quantities.getQuantity(24.42, StandardUnits.ACTIVE_POWER_RESULT),
				Quantities.getQuantity(-24.42, StandardUnits.REACTIVE_POWER_RESULT))
		def entities = [
			lineResult1,
			lineResult2,
			lineResult3,
			chpResult1,
			chpResult2
		]
		when:
		sink.persistAll(entities)
		def key_line = fileNamingStrategy.getFileName(LineResult.class).get().trim().replaceAll("\\W", "_");
		def key_chp = fileNamingStrategy.getFileName(ChpResult.class).get().trim().replaceAll("\\W", "_");
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
		sink.persist(timeSeries)
		def key = fileNamingStrategy.getFileName(timeSeries).get().trim().replaceAll("\\W", "_");
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


	static def mapMatchesLineResultEntity(Map<String, String> fieldMap, LineResult lineResult) {
		def timeUtil = new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd'T'HH:mm:ss[.S[S][S]]'Z'")
		timeUtil.toZonedDateTime(fieldMap.get("time")) == lineResult.getTimestamp()
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
		timeUtil.toZonedDateTime(fieldMap.get("time")) == chpResult.getTimestamp()
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
		if(pStr== null || pStr.empty) pVal.getValue().getP() == null
		else Double.parseDouble(pStr) == pVal.getValue().getP().getValue()
	}
}
