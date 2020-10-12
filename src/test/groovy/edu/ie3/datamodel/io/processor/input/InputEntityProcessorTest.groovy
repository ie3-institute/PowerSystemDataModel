/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.processor.input

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.RandomLoadParameters
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.Transformer3WInput
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput
import edu.ie3.datamodel.models.input.system.*
import edu.ie3.datamodel.models.input.system.type.*
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SystemParticipantTestData
import edu.ie3.test.common.TypeTestData
import edu.ie3.util.TimeTools
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZoneId
import java.time.ZonedDateTime

import static edu.ie3.util.quantities.PowerSystemUnits.PU

/**
 * Testing the function of processors
 *
 * @version 0.1
 * @since 24.03.20
 */
class InputEntityProcessorTest extends Specification {
	static {
		TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
	}

	def "A InputEntityProcessor should de-serialize a provided NodeInput correctly"() {
		given:
		def processor = new InputEntityProcessor(NodeInput)
		def validResult = GridTestData.nodeA

		Map expectedResults = [
			"uuid"         : "4ca90220-74c2-4369-9afa-a18bf068840d",
			"geoPosition"  : "{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"id"           : "node_a",
			"operatesUntil": "2020-03-25T15:11:31Z[UTC]",
			"operatesFrom" : "2020-03-24T15:11:31Z[UTC]",
			"operator"     : "f15105c4-a2de-4ab8-a621-4bc98e372d92",
			"slack"        : "true",
			"subnet"       : "1",
			"vTarget"      : "1.0",
			"voltLvl"      : "Höchstspannung",
			"vRated"       : "380.0"
		]

		when: "the entity is passed to the processor"
		def processingResult = processor.handleEntity(validResult)

		then: "make sure that the result is as expected "
		processingResult.present
		processingResult.get() == expectedResults
	}


	def "A InputEntityProcessor should de-serialize a provided ConnectorInput correctly"() {
		given:
		def processor = new InputEntityProcessor(modelClass)
		def validInput = modelInstance

		when: "the entity is passed to the processor"
		def processingResult = processor.handleEntity(validInput)

		then: "make sure that the result is as expected "
		processingResult.present

		processingResult.get() == expectedResult

		where:
		modelClass         | modelInstance                   || expectedResult
		Transformer3WInput | GridTestData.transformerAtoBtoC || [
			"uuid"           : "cc327469-7d56-472b-a0df-edbb64f90e8f",
			"autoTap"        : "true",
			"id"             : "3w_test",
			"parallelDevices": "1",
			"nodeA"          : "4ca90220-74c2-4369-9afa-a18bf068840d",
			"nodeB"          : "47d29df0-ba2d-4d23-8e75-c82229c5c758",
			"nodeC"          : "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2",
			"operatesUntil"  : "2020-03-25T15:11:31Z[UTC]",
			"operatesFrom"   : "2020-03-24T15:11:31Z[UTC]",
			"operator"       : "f15105c4-a2de-4ab8-a621-4bc98e372d92",
			"tapPos"         : "0",
			"type"           : "5b0ee546-21fb-4a7f-a801-5dbd3d7bb356"
		]
		Transformer2WInput | GridTestData.transformerCtoG    || [
			"uuid"           : "5dc88077-aeb6-4711-9142-db57292640b1",
			"autoTap"        : "true",
			"id"             : "2w_parallel_2",
			"parallelDevices": "1",
			"nodeA"          : "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2",
			"nodeB"          : "aaa74c1a-d07e-4615-99a5-e991f1d81cc4",
			"operatesUntil"  : "2020-03-25T15:11:31Z[UTC]",
			"operatesFrom"   : "2020-03-24T15:11:31Z[UTC]",
			"operator"       : "f15105c4-a2de-4ab8-a621-4bc98e372d92",
			"tapPos"         : "0",
			"type"           : "08559390-d7c0-4427-a2dc-97ba312ae0ac"
		]

		SwitchInput        | GridTestData.switchAtoB         || [
			"uuid"         : "5dc88077-aeb6-4711-9142-db57287640b1",
			"closed"       : "true",
			"id"           : "test_switch_AtoB",
			"nodeA"        : "4ca90220-74c2-4369-9afa-a18bf068840d",
			"nodeB"        : "47d29df0-ba2d-4d23-8e75-c82229c5c758",
			"operatesUntil": "2020-03-25T15:11:31Z[UTC]",
			"operatesFrom" : "2020-03-24T15:11:31Z[UTC]",
			"operator"     : "f15105c4-a2de-4ab8-a621-4bc98e372d92"
		]

		LineInput          | GridTestData.lineCtoD           || [
			"uuid"             : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"geoPosition"      : "{\"type\":\"LineString\",\"coordinates\":[[7.411111,51.492528],[7.414116,51.484136]],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"id"               : "test_line_CtoD",
			"length"           : "0.003",
			"parallelDevices"  : "2",
			"nodeA"            : "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2",
			"nodeB"            : "6e0980e0-10f2-4e18-862b-eb2b7c90509b",
			"olmCharacteristic": "olm:{(0.00,1.00)}",
			"operatesUntil"    : "2020-03-25T15:11:31Z[UTC]",
			"operatesFrom"     : "2020-03-24T15:11:31Z[UTC]",
			"operator"         : "f15105c4-a2de-4ab8-a621-4bc98e372d92",
			"type"             : "3bed3eb3-9790-4874-89b5-a5434d408088"
		]
	}

	def "A InputEntityProcessor should de-serialize a provided SystemParticipantInput correctly"() {
		given:
		def processor = new InputEntityProcessor(modelClass)
		def validInput = modelInstance

		when: "the entity is passed to the processor"
		def processingResult = processor.handleEntity(validInput)

		then: "make sure that the result is as expected "
		processingResult.present

		processingResult.get().forEach { k, v ->
			if (k != "nodeInternal")     // the internal 3w node is always randomly generated, hence we can skip to test on this
				assert (v == expectedResult.get(k))
		}

		where:
		modelClass       | modelInstance                              || expectedResult
		FixedFeedInInput | SystemParticipantTestData.fixedFeedInInput || [
			"uuid"            : SystemParticipantTestData.fixedFeedInInput.uuid.toString(),
			"cosPhiRated"     : SystemParticipantTestData.fixedFeedInInput.cosPhiRated.toString(),
			"id"              : SystemParticipantTestData.fixedFeedInInput.id,
			"node"            : SystemParticipantTestData.fixedFeedInInput.node.uuid.toString(),
			"operatesUntil"   : SystemParticipantTestData.fixedFeedInInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operatesFrom"    : SystemParticipantTestData.fixedFeedInInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.fixedFeedInInput.operator.getUuid().toString(),
			"qCharacteristics": SystemParticipantTestData.cosPhiFixedDeSerialized,
			"sRated"          : SystemParticipantTestData.fixedFeedInInput.sRated.to(StandardUnits.S_RATED).getValue().doubleValue().toString()
		]
		PvInput          | SystemParticipantTestData.pvInput          || [
			"uuid"            : SystemParticipantTestData.pvInput.uuid.toString(),
			"albedo"          : SystemParticipantTestData.pvInput.albedo.toString(),
			"azimuth"         : SystemParticipantTestData.pvInput.azimuth.to(StandardUnits.AZIMUTH).getValue().doubleValue().toString(),
			"cosPhiRated"     : SystemParticipantTestData.pvInput.cosPhiRated.toString(),
			"etaConv"         : SystemParticipantTestData.pvInput.etaConv.getValue().doubleValue().toString(),
			"height"          : SystemParticipantTestData.pvInput.height.getValue().doubleValue().toString(),
			"id"              : SystemParticipantTestData.pvInput.id,
			"kG"              : SystemParticipantTestData.pvInput.kG.toString(),
			"kT"              : SystemParticipantTestData.pvInput.kT.toString(),
			"marketReaction"  : SystemParticipantTestData.pvInput.marketReaction.toString(),
			"node"            : SystemParticipantTestData.pvInput.node.uuid.toString(),
			"operatesUntil"   : SystemParticipantTestData.pvInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operatesFrom"    : SystemParticipantTestData.pvInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.pvInput.operator.getUuid().toString(),
			"qCharacteristics": SystemParticipantTestData.cosPhiFixedDeSerialized,
			"sRated"          : SystemParticipantTestData.pvInput.sRated.to(StandardUnits.S_RATED).getValue().doubleValue().toString()
		]
		WecInput         | SystemParticipantTestData.wecInput         || [
			"uuid"            : SystemParticipantTestData.wecInput.uuid.toString(),
			"id"              : SystemParticipantTestData.wecInput.id,
			"marketReaction"  : SystemParticipantTestData.wecInput.marketReaction.toString(),
			"node"            : SystemParticipantTestData.wecInput.node.uuid.toString(),
			"operatesUntil"   : SystemParticipantTestData.wecInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operatesFrom"    : SystemParticipantTestData.wecInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.wecInput.operator.uuid.toString(),
			"qCharacteristics": SystemParticipantTestData.cosPhiPDeSerialized,
			"type"            : SystemParticipantTestData.wecInput.type.uuid.toString()
		]
		ChpInput         | SystemParticipantTestData.chpInput         || [
			"uuid"            : SystemParticipantTestData.chpInput.uuid.toString(),
			"id"              : SystemParticipantTestData.chpInput.id,
			"marketReaction"  : SystemParticipantTestData.chpInput.marketReaction.toString(),
			"node"            : SystemParticipantTestData.chpInput.node.uuid.toString(),
			"operatesUntil"   : SystemParticipantTestData.chpInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operatesFrom"    : SystemParticipantTestData.chpInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.chpInput.operator.uuid.toString(),
			"qCharacteristics": SystemParticipantTestData.cosPhiFixedDeSerialized,
			"thermalBus"      : SystemParticipantTestData.chpInput.thermalBus.uuid.toString(),
			"thermalStorage"  : SystemParticipantTestData.chpInput.thermalStorage.uuid.toString(),
			"type"            : SystemParticipantTestData.chpInput.type.uuid.toString(),
		]
		BmInput          | SystemParticipantTestData.bmInput          || [
			"uuid"            : SystemParticipantTestData.bmInput.uuid.toString(),
			"costControlled"  : SystemParticipantTestData.bmInput.costControlled.toString(),
			"feedInTariff"    : SystemParticipantTestData.bmInput.feedInTariff.to(StandardUnits.ENERGY_PRICE).getValue().doubleValue().toString(),
			"id"              : SystemParticipantTestData.bmInput.id,
			"marketReaction"  : SystemParticipantTestData.bmInput.marketReaction.toString(),
			"node"            : SystemParticipantTestData.bmInput.node.uuid.toString(),
			"operatesUntil"   : SystemParticipantTestData.bmInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operatesFrom"    : SystemParticipantTestData.bmInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.bmInput.operator.uuid.toString(),
			"qCharacteristics": SystemParticipantTestData.qVDeSerialized,
			"type"            : SystemParticipantTestData.bmInput.type.uuid.toString()
		]
		EvInput          | SystemParticipantTestData.evInput          || [
			"uuid"            : SystemParticipantTestData.evInput.uuid.toString(),
			"id"              : SystemParticipantTestData.evInput.id,
			"node"            : SystemParticipantTestData.evInput.node.uuid.toString(),
			"operatesUntil"   : SystemParticipantTestData.evInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operatesFrom"    : SystemParticipantTestData.evInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.evInput.operator.getUuid().toString(),
			"qCharacteristics": SystemParticipantTestData.cosPhiFixedDeSerialized,
			"type"            : SystemParticipantTestData.evInput.type.getUuid().toString()
		]

		LoadInput        | SystemParticipantTestData.loadInput        || [
			"uuid"               : SystemParticipantTestData.loadInput.uuid.toString(),
			"cosPhiRated"        : SystemParticipantTestData.loadInput.cosPhiRated.toString(),
			"dsm"                : SystemParticipantTestData.loadInput.dsm.toString(),
			"eConsAnnual"        : SystemParticipantTestData.loadInput.eConsAnnual.getValue().doubleValue().toString(),
			"id"                 : SystemParticipantTestData.loadInput.id,
			"node"               : SystemParticipantTestData.loadInput.node.uuid.toString(),
			"operatesUntil"      : SystemParticipantTestData.loadInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operatesFrom"       : SystemParticipantTestData.loadInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"           : SystemParticipantTestData.loadInput.operator.uuid.toString(),
			"qCharacteristics"   : SystemParticipantTestData.cosPhiFixedDeSerialized,
			"sRated"             : SystemParticipantTestData.loadInput.sRated.getValue().doubleValue().toString(),
			"standardLoadProfile": SystemParticipantTestData.loadInput.standardLoadProfile.key
		]
		StorageInput     | SystemParticipantTestData.storageInput     || [
			"uuid"            : SystemParticipantTestData.storageInput.uuid.toString(),
			"id"              : SystemParticipantTestData.storageInput.id,
			"node"            : SystemParticipantTestData.storageInput.node.uuid.toString(),
			"operatesUntil"   : SystemParticipantTestData.storageInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operatesFrom"    : SystemParticipantTestData.storageInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.storageInput.operator.uuid.toString(),
			"qCharacteristics": SystemParticipantTestData.cosPhiFixedDeSerialized,
			"type"            : SystemParticipantTestData.storageInput.type.uuid.toString()
		]
		HpInput          | SystemParticipantTestData.hpInput          || [
			"uuid"            : SystemParticipantTestData.hpInput.uuid.toString(),
			"id"              : SystemParticipantTestData.hpInput.id,
			"node"            : SystemParticipantTestData.hpInput.node.uuid.toString(),
			"operatesUntil"   : SystemParticipantTestData.hpInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operatesFrom"    : SystemParticipantTestData.hpInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.hpInput.operator.uuid.toString(),
			"qCharacteristics": SystemParticipantTestData.cosPhiFixedDeSerialized,
			"thermalBus"      : SystemParticipantTestData.hpInput.thermalBus.uuid.toString(),
			"type"            : SystemParticipantTestData.hpInput.type.uuid.toString()
		]
	}

	def "The InputEntityProcessor should de-serialize a provided NodeGraphicInput with point correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(NodeGraphicInput)
		NodeGraphicInput validNode = GridTestData.nodeGraphicC
		Map expected = [
			"uuid"        : "09aec636-791b-45aa-b981-b14edf171c4c",
			"graphicLayer": "main",
			"path"        : "",
			"point"       : "{\"type\":\"Point\",\"coordinates\":[0.0,10],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"node"        : "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2"
		]

		when:
		Optional<LinkedHashMap<String, String>> actual = processor.handleEntity(validNode)

		then:
		actual.present
		actual.get() == expected
	}

	def "The InputEntityProcessor should de-serialize a provided NodeGraphicInput with path correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(NodeGraphicInput)
		NodeGraphicInput validNode = GridTestData.nodeGraphicD
		Map expected = [
			"uuid"        : "9ecad435-bd16-4797-a732-762c09d4af25",
			"graphicLayer": "main",
			"path"        : "{\"type\":\"LineString\",\"coordinates\":[[-1,0.0],[1,0.0]],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"point"       : "",
			"node"        : "6e0980e0-10f2-4e18-862b-eb2b7c90509b"
		]

		when:
		Optional<LinkedHashMap<String, String>> actual = processor.handleEntity(validNode)

		then:
		actual.present
		actual.get() == expected
	}

	def "The InputEntityProcessor should de-serialize a provided LineGraphicInput correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(LineGraphicInput)
		LineGraphicInput validNode = GridTestData.lineGraphicCtoD
		Map expected = [
			"uuid"        : "ece86139-3238-4a35-9361-457ecb4258b0",
			"graphicLayer": "main",
			"path"        : "{\"type\":\"LineString\",\"coordinates\":[[0.0,0.0],[0.0,10]],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"line"        : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"
		]

		when:
		Optional<LinkedHashMap<String, String>> actual = processor.handleEntity(validNode)

		then:
		actual.present
		actual.get() == expected
	}

	def "The InputEntityProcessor should de-serialize a provided OperatorInput correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(OperatorInput)
		OperatorInput operator = new OperatorInput(UUID.fromString("420ee39c-dd5a-4d9c-9156-23dbdef13e5e"), "Prof. Brokkoli")
		Map expected = [
			"uuid": "420ee39c-dd5a-4d9c-9156-23dbdef13e5e",
			"id"  : "Prof. Brokkoli"
		]

		when:
		Optional<LinkedHashMap<String, String>> actual = processor.handleEntity(operator)

		then:
		actual.present
		actual.get() == expected
	}

	def "The InputEntityProcessor should de-serialize a provided RandomLoadParameters correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(RandomLoadParameters)
		RandomLoadParameters parameters = new RandomLoadParameters(
				UUID.fromString("a5b0f432-27b5-4b3e-b87a-61867b9edd79"),
				4,
				1.2,
				2.3,
				3.4,
				4.5,
				5.6,
				6.7,
				7.8,
				8.9,
				9.10
				)
		Map expected = [
			"uuid"       : "a5b0f432-27b5-4b3e-b87a-61867b9edd79",
			"quarterHour": "4",
			"kWd"        : "1.2",
			"kSa"        : "2.3",
			"kSu"        : "3.4",
			"myWd"       : "4.5",
			"mySa"       : "5.6",
			"mySu"       : "6.7",
			"sigmaWd"    : "7.8",
			"sigmaSa"    : "8.9",
			"sigmaSu"    : "9.1"
		]

		when:
		Optional<LinkedHashMap<String, String>> actual = processor.handleEntity(parameters)

		then:
		actual.present
		actual.get() == expected
	}

	def "The InputEntityProcessor should de-serialize a provided WecTypeInput correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(WecTypeInput)
		WecTypeInput type = TypeTestData.wecType
		Map expected = [
			"uuid"            : "a24fc5b9-a26f-44de-96b8-c9f50b665cb3",
			"id"              : "Test wec type",
			"capex"           : "100.0",
			"opex"            : "101.0",
			"cosPhiRated"     : "0.95",
			"cpCharacteristic": "cP:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}",
			"etaConv"         : "90.0",
			"sRated"          : "2500.0",
			"rotorArea"       : "2000.0",
			"hubHeight"       : "130.0"
		]

		when:
		Optional<Map<String, String>> actual = processor.handleEntity(type)

		then:
		actual.present
		actual.get() == expected
	}

	def "The InputEntityProcessor should de-serialize a provided Transformer2WTypeInput correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(Transformer2WTypeInput)
		Transformer2WTypeInput type = GridTestData.transformerTypeBtoD
		Map expected = [
			"uuid"    : "202069a7-bcf8-422c-837c-273575220c8a",
			"id"      : "HS-MS_1",
			"rSc"     : "45.375",
			"xSc"     : "102.759",
			"gM"      : "0.0",
			"bM"      : "0.0",
			"sRated"  : "20000.0",
			"vRatedA" : "110.0",
			"vRatedB" : "20.0",
			"dV"      : "1.5",
			"dPhi"    : "0.0",
			"tapSide" : "false",
			"tapNeutr": "0",
			"tapMax"  : "10",
			"tapMin"  : "-10"
		]

		when:
		Optional<Map<String, String>> actual = processor.handleEntity(type)

		then:
		actual.present
		actual.get() == expected
	}

	def "The InputEntityProcessor should de-serialize a provided Transformer3WTypeInput correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(Transformer3WTypeInput)
		Transformer3WTypeInput type = GridTestData.transformerTypeAtoBtoC
		Map expected = [
			"uuid"    : "5b0ee546-21fb-4a7f-a801-5dbd3d7bb356",
			"id"      : "HöS-HS-MS_1",
			"sRatedA" : "120000.0",
			"sRatedB" : "60000.0",
			"sRatedC" : "40000.0",
			"vRatedA" : "380.0",
			"vRatedB" : "110.0",
			"vRatedC" : "20.0",
			"rScA"    : "0.3",
			"rScB"    : "0.025",
			"rScC"    : "8.0E-4",
			"xScA"    : "1.0",
			"xScB"    : "0.08",
			"xScC"    : "0.003",
			"gM"      : "40000.0",
			"bM"      : "1000.0",
			"dV"      : "1.5",
			"dPhi"    : "0.0",
			"tapNeutr": "0",
			"tapMin"  : "-10",
			"tapMax"  : "10"
		]

		when:
		Optional<Map<String, String>> actual = processor.handleEntity(type)

		then:
		actual.present
		actual.get() == expected
	}

	def "The InputEntityProcessor should de-serialize a provided LineTypeInput correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(LineTypeInput)
		LineTypeInput type = GridTestData.lineTypeInputCtoD
		Map expected = [
			"uuid"  : "3bed3eb3-9790-4874-89b5-a5434d408088",
			"id"    : "lineType_AtoB",
			"b"     : "0.00322",
			"g"     : "0.0",
			"r"     : "0.437",
			"x"     : "0.356",
			"iMax"  : "300.0",
			"vRated": "20.0"
		]

		when:
		Optional<Map<String, String>> actual = processor.handleEntity(type)

		then:
		actual.present
		actual.get() == expected
	}

	def "The InputEntityProcessor should de-serialize a provided EvTypeInput correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(EvTypeInput)
		EvTypeInput type = TypeTestData.evType
		Map expected = [
			"uuid"       : "66b0db5d-b2fb-41d0-a9bc-990d6b6a36db",
			"id"         : "ev type",
			"capex"      : "100.0",
			"opex"       : "101.0",
			"eStorage"   : "100.0",
			"eCons"      : "23.0",
			"sRated"     : "22.0",
			"cosPhiRated": "0.9"
		]

		when:
		Optional<Map<String, String>> actual = processor.handleEntity(type)

		then:
		actual.present
		actual.get() == expected
	}

	def "The InputEntityProcessor should de-serialize a provided ChpTypeInput correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(ChpTypeInput)
		ChpTypeInput type = TypeTestData.chpType
		Map expected = [
			"uuid"       : "1c027d3e-5409-4e52-a0e2-f8a23d5d0af0",
			"id"         : "chp type",
			"capex"      : "100.0",
			"opex"       : "101.0",
			"etaEl"      : "95.0",
			"etaThermal" : "90.0",
			"sRated"     : "58.0",
			"cosPhiRated": "0.98",
			"pThermal"   : "49.59",
			"pOwn"       : "5.0"
		]

		when:
		Optional<Map<String, String>> actual = processor.handleEntity(type)

		then:
		actual.present
		actual.get() == expected
	}

	def "The InputEntityProcessor should de-serialize a provided HpTypeInput correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(HpTypeInput)
		HpTypeInput type = TypeTestData.hpType
		Map expected = [
			"uuid"       : "1059ef51-9e17-4c13-928c-7c1c716d4ee6",
			"id"         : "hp type",
			"capex"      : "100.0",
			"opex"       : "101.0",
			"sRated"     : "45.0",
			"cosPhiRated": "0.975",
			"pThermal"   : "26.3"
		]

		when:
		Optional<Map<String, String>> actual = processor.handleEntity(type)

		then:
		actual.present
		actual.get() == expected
	}

	def "The InputEntityProcessor should de-serialize a provided BmTypeInput correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(BmTypeInput)
		BmTypeInput type = TypeTestData.bmType
		Map expected = [
			"uuid"               : "c3bd30f5-1a62-4a37-86e3-074040d965a4",
			"id"                 : "bm type",
			"capex"              : "100.0",
			"opex"               : "101.0",
			"activePowerGradient": "5.0",
			"sRated"             : "800.0",
			"cosPhiRated"        : "0.965",
			"etaConv"            : "89.0"
		]

		when:
		Optional<Map<String, String>> actual = processor.handleEntity(type)

		then:
		actual.present
		actual.get() == expected
	}

	def "The InputEntityProcessor should de-serialize a provided StorageTypeInput correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(StorageTypeInput)
		StorageTypeInput type = TypeTestData.storageType
		Map expected = [
			"uuid"               : "fbee4995-24dd-45e4-9c85-7d986fe99ff3",
			"id"                 : "storage type",
			"capex"              : "100.0",
			"opex"               : "101.0",
			"eStorage"           : "200.0",
			"sRated"             : "13.0",
			"cosPhiRated"        : "0.997",
			"pMax"               : "12.961",
			"activePowerGradient": "3.0",
			"eta"                : "92.0",
			"dod"                : "20.0",
			"lifeTime"           : "43800.0",
			"lifeCycle"          : "100000"
		]

		when:
		Optional<Map<String, String>> actual = processor.handleEntity(type)

		then:
		actual.present
		actual.get() == expected
	}

	def "The InputEntityProcessor should deserialize an entity but ignore the operator field when OperatorInput is equal to NO_OPERATOR_ASSIGNED"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(NodeInput)
		def nodeWithOutOperator = new NodeInput(
				UUID.fromString("6e0980e0-10f2-4e18-862b-eb2b7c90509b"), "node_d", OperatorInput.NO_OPERATOR_ASSIGNED,
				OperationTime.notLimited()
				,
				Quantities.getQuantity(1d, PU),
				false,
				null,
				GermanVoltageLevelUtils.MV_20KV,
				4)

		Map expected = [
			"geoPosition"  : "",
			"id"           : "node_d",
			"operatesFrom" : "",
			"operatesUntil": "",
			"operator"     : "",
			"slack"        : "false",
			"subnet"       : "4",
			"uuid"         : "6e0980e0-10f2-4e18-862b-eb2b7c90509b",
			"vRated"       : "20.0",
			"vTarget"      : "1.0",
			"voltLvl"      : "Mittelspannung"
		]

		when:
		Optional<Map<String, String>> actual = processor.handleEntity(nodeWithOutOperator)

		then:
		actual.present
		actual.get() == expected
	}
}
