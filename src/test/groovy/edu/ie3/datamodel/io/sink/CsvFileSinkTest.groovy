/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.sink

import edu.ie3.datamodel.exceptions.SinkException
import edu.ie3.datamodel.io.CsvFileDefinition
import edu.ie3.datamodel.io.FileNamingStrategy
import edu.ie3.datamodel.io.processor.result.ResultEntityProcessor
import edu.ie3.datamodel.models.result.system.EvResult
import edu.ie3.datamodel.models.result.system.PvResult
import edu.ie3.datamodel.models.result.system.WecResult
import edu.ie3.util.io.FileIOUtils
import spock.lang.Shared
import spock.lang.Specification

class CsvFileSinkTest extends Specification {

	@Shared
	String testBaseFolderPath = "test"

	@Shared
	CsvFileDefinition pvResultFileDefinition

	@Shared
	CsvFileDefinition evResultFileDefinition

	@Shared
	CsvFileDefinition wecResultFileDefinition

	def setupSpec() {
		FileNamingStrategy fileNamingStrategy = new FileNamingStrategy()
		ResultEntityProcessor pvResultEntityProcessor = new ResultEntityProcessor(PvResult)
		ResultEntityProcessor evResultEntityProcessor = new ResultEntityProcessor(EvResult)
		ResultEntityProcessor wecResultEntityProcessor = new ResultEntityProcessor(WecResult)

		pvResultFileDefinition = new CsvFileDefinition(fileNamingStrategy.getFileName(PvResult).get(), pvResultEntityProcessor.getHeaderElements())
		evResultFileDefinition = new CsvFileDefinition(fileNamingStrategy.getFileName(EvResult).get(), evResultEntityProcessor.getHeaderElements())
		wecResultFileDefinition = new CsvFileDefinition(fileNamingStrategy.getFileName(WecResult).get(), wecResultEntityProcessor.getHeaderElements())
	}

	def cleanup() {
		// delete files after each test if they exist
		if (new File(testBaseFolderPath).exists()) {
			FileIOUtils.deleteRecursively(testBaseFolderPath)
		}
	}

	def "A valid CsvFileSink with 'initFiles' enabled should create files as expected"() {
		given:
		CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath, [
			pvResultFileDefinition,
			evResultFileDefinition
		], ",", true, false)
		csvFileSink.dataConnector.shutdown()

		expect:
		new File(testBaseFolderPath).exists()
		new File(testBaseFolderPath + File.separator + "ev_res.csv").exists()
		new File(testBaseFolderPath + File.separator + "pv_res.csv").exists()
	}

	def "A valid CsvFileSink without 'initFiles' enabled should create files as expected"() {
		given:
		CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath, [
			pvResultFileDefinition,
			evResultFileDefinition
		], ",", false, false)
		csvFileSink.dataConnector.shutdown()

		expect:
		!new File(testBaseFolderPath).exists()
		!new File(testBaseFolderPath + File.separator + "ev_res.csv").exists()
		!new File(testBaseFolderPath + File.separator + "pv_res.csv").exists()
	}

	def "A valid CsvFileSink without 'initFiles' should only persist provided elements correctly but not all files"() {
		given:
		CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath, [
			pvResultFileDefinition,
			evResultFileDefinition,
			wecResultFileDefinition
		], ",", false, false)

		LinkedHashMap<String, String> pvResult = [
			"uuid": "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
			"inputModel": "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
			"timestamp": "2020-01-30 17:26:44",
			"p": "0.01",
			"q": "0.01"
		]
		LinkedHashMap<String, String> wecResult = [
			"uuid": "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
			"inputModel": "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
			"timestamp": "2020-01-30 17:26:44",
			"p": "0.01",
			"q": "0.01"
		]

		when:
		csvFileSink.persist(pvResultFileDefinition, pvResult)
		csvFileSink.persist(wecResultFileDefinition, wecResult)
		csvFileSink.dataConnector.shutdown()

		then:
		new File(testBaseFolderPath).exists()
		new File(testBaseFolderPath + File.separator + "wec_res.csv").exists()
		new File(testBaseFolderPath + File.separator + "pv_res.csv").exists()

		!new File(testBaseFolderPath + File.separator + "ev_res.csv").exists()
	}

	def "A valid CsvFileSink throws a SinkException, if the data does not fit the header definition"() {
		given:
		CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath, [pvResultFileDefinition], ",", false, false)

		LinkedHashMap<String, String> pvResult = [
			"uuid": "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
			"lilaLauneBaer": "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
			"timestamp": "2020-01-30 17:26:44",
			"p": "0.01",
			"q": "0.01"
		]

		when:
		csvFileSink.persist(pvResultFileDefinition, pvResult)

		then:
		SinkException exception = thrown()
		csvFileSink.dataConnector.shutdown()

		exception.message == "The provided data does not match the head line definition!"
	}

	def "A valid CsvFileSink should throw an exception if the provided destination is not registered and later registration is prohibited"() {
		given:
		CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath, [pvResultFileDefinition], ",", false, false)
		LinkedHashMap<String, String> wecResult = [
			"uuid": "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
			"inputModel": "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
			"timestamp": "2020-01-30 17:26:44",
			"p": "0.01",
			"q": "0.01"
		]

		when:
		csvFileSink.persist(wecResultFileDefinition, wecResult)
		csvFileSink.dataConnector.shutdown()

		then:
		SinkException exception = thrown(SinkException)
		exception.getMessage() == "Cannot find a matching writer for file definition: \"CsvFileDefinition{fileName='wec_res', fileExtension='csv', headLineElements=[uuid, inputModel, p, q, timestamp]}\"."
	}

	def "A valid CsvFileSink registers a new destination if the provided destination is not registered and later registration is allowed"() {
		given:
		CsvFileSink csvFileSink = new CsvFileSink(testBaseFolderPath, [pvResultFileDefinition], ",", false, true)
		LinkedHashMap<String, String> wecResult = [
			"uuid": "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
			"inputModel": "22bea5fc-2cb2-4c61-beb9-b476e0107f52",
			"timestamp": "2020-01-30 17:26:44",
			"p": "0.01",
			"q": "0.01"
		]

		when:
		csvFileSink.persist(wecResultFileDefinition, wecResult)
		csvFileSink.dataConnector.shutdown()

		then:
		new File(testBaseFolderPath).exists()
		new File(testBaseFolderPath + File.separator + "wec_res.csv").exists()
		!new File(testBaseFolderPath + File.separator + "pv_res.csv").exists() // as it is not initialized
	}
}
