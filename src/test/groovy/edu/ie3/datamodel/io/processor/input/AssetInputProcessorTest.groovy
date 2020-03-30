/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.processor.input

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.Transformer3WInput
import edu.ie3.datamodel.models.input.system.BmInput
import edu.ie3.datamodel.models.input.system.ChpInput
import edu.ie3.datamodel.models.input.system.EvInput
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.HpInput
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.StorageInput
import edu.ie3.datamodel.models.input.system.WecInput
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SystemParticipantTestData
import edu.ie3.util.TimeTools
import spock.lang.Specification

import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * //ToDo: Class Description
 *
 * @version 0.1* @since 24.03.20
 */
class AssetInputProcessorTest extends Specification {


	def "A AssetInputProcessor should de-serialize a provided NodeInput correctly"() {
		given:
		TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
		def assetInputProcessor = new AssetInputProcessor(NodeInput)
		def validResult = GridTestData.nodeA

        Map expectedResults = [
                "uuid"         : "5dc88077-aeb6-4711-9142-db57292640b1",
                "geoPosition"  : "{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
                "id"           : "node_a",
                "operatesUntil": "2020-03-25 15:11:31",
                "operatesFrom" : "2020-03-24 15:11:31",
                "operator"     : "8f9682df-0744-4b58-a122-f0dc730f6510",
                "slack"        : "true",
                "subnet"       : "1",
                "vTarget"      : "1.0",
                "voltlvl"      : "Höchstspannung",
                "vrated"       : "380.0"
        ]

		when: "the entity is passed to the processor"
		def processingResult = assetInputProcessor.handleEntity(validResult)


		then: "make sure that the result is as expected "
		processingResult.present
		processingResult.get() == expectedResults
	}


	def "A AssetInputProcessor should de-serialize a provided ConnectorInput correctly"() {
		given:
		TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
		def assetInputProcessor = new AssetInputProcessor(modelClass)
		def validInput = modelInstance

		when: "the entity is passed to the processor"
		def processingResult = assetInputProcessor.handleEntity(validInput)


		then: "make sure that the result is as expected "
		processingResult.present

		processingResult.get().forEach { k, v ->
			if (k != "nodeInternal")     // the internal 3w node is always randomly generated, hence we can skip to test on this
				assert (v == expectedResult.get(k))
		}


		where:
		modelClass         | modelInstance                   || expectedResult
		Transformer3WInput | GridTestData.transformerAtoBtoC || [
			"uuid"               : "5dc88077-aeb6-4711-9142-db57292640b1",
			"autoTap"            : "true",
			"id"                 : "3w_test",
			"noOfParallelDevices": "1",
			"nodeA"              : "5dc88077-aeb6-4711-9142-db57292640b1",
			"nodeB"              : "47d29df0-ba2d-4d23-8e75-c82229c5c758",
			"nodeC"              : "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2",
			"operatesUntil"      : "2020-03-25 15:11:31",
			"operatesFrom"       : "2020-03-24 15:11:31",
			"operator"           : "8f9682df-0744-4b58-a122-f0dc730f6510",
			"tapPos"             : "0",
			"type"               : "5b0ee546-21fb-4a7f-a801-5dbd3d7bb356"
		]
		Transformer2WInput | GridTestData.transformerCtoG    || [
			"uuid"               : "5dc88077-aeb6-4711-9142-db57292640b1",
			"autoTap"            : "true",
			"id"                 : "2w_parallel_2",
			"noOfParallelDevices": "1",
			"nodeA"              : "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2",
			"nodeB"              : "aaa74c1a-d07e-4615-99a5-e991f1d81cc4",
			"operatesUntil"      : "2020-03-25 15:11:31",
			"operatesFrom"       : "2020-03-24 15:11:31",
			"operator"           : "8f9682df-0744-4b58-a122-f0dc730f6510",
			"tapPos"             : "0",
			"type"               : "08559390-d7c0-4427-a2dc-97ba312ae0ac"
		]

		SwitchInput        | GridTestData.switchAtoB         || [
			"uuid"               : "5dc88077-aeb6-4711-9142-db57287640b1",
			"closed"             : "true",
			"id"                 : "test_switch_AtoB",
			"noOfParallelDevices": "1",
			"nodeA"              : "5dc88077-aeb6-4711-9142-db57292640b1",
			"nodeB"              : "47d29df0-ba2d-4d23-8e75-c82229c5c758",
			"operatesUntil"      : "2020-03-25 15:11:31",
			"operatesFrom"       : "2020-03-24 15:11:31",
			"operator"           : "8f9682df-0744-4b58-a122-f0dc730f6510"
		]

		LineInput          | GridTestData.lineCtoD           || [
			"uuid"               : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"geoPosition"        : "{\"type\":\"LineString\",\"coordinates\":[[7.411111,51.492528],[7.414116,51.484136]],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"id"                 : "test_line_AtoB",
			"length"             : "0.003",
			"noOfParallelDevices": "2",
			"nodeA"              : "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2",
			"nodeB"              : "6e0980e0-10f2-4e18-862b-eb2b7c90509b",
			"olmCharacteristic"  : "olm",
			"operatesUntil"      : "2020-03-25 15:11:31",
			"operatesFrom"       : "2020-03-24 15:11:31",
			"operator"           : "8f9682df-0744-4b58-a122-f0dc730f6510",
			"type"               : "3bed3eb3-9790-4874-89b5-a5434d408088"
		]
	}

	def "A AssetInputProcessor should de-serialize a provided SystemParticipantInput correctly"() {

		given:
		def assetInputProcessor = new AssetInputProcessor(modelClass)
		def validInput = modelInstance

		when: "the entity is passed to the processor"
		def processingResult = assetInputProcessor.handleEntity(validInput)


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
			"cosphiRated"     : SystemParticipantTestData.fixedFeedInInput.cosphiRated.toString(),
			"id"              : SystemParticipantTestData.fixedFeedInInput.id,
			"node"            : SystemParticipantTestData.fixedFeedInInput.node.uuid.toString(),
			"operatesUntil"   : TimeTools.toString(SystemParticipantTestData.fixedFeedInInput.operationTime.endDate.orElse(ZonedDateTime.now())),
			"operatesFrom"    : TimeTools.toString(SystemParticipantTestData.fixedFeedInInput.operationTime.startDate.orElse(ZonedDateTime.now())),
			"operator"        : SystemParticipantTestData.fixedFeedInInput.operator.getUuid().toString(),
			"qCharacteristics": SystemParticipantTestData.fixedFeedInInput.qCharacteristics,
			"sRated"          : SystemParticipantTestData.fixedFeedInInput.sRated.to(StandardUnits.S_RATED).getValue().doubleValue().toString()
		]
		PvInput          | SystemParticipantTestData.pvInput          || [
			"uuid"            : SystemParticipantTestData.pvInput.uuid.toString(),
			"albedo"          : SystemParticipantTestData.pvInput.albedo.toString(),
			"azimuth"         : SystemParticipantTestData.pvInput.azimuth.to(StandardUnits.AZIMUTH).getValue().doubleValue().toString(),
			"cosphiRated"     : SystemParticipantTestData.pvInput.cosphiRated.toString(),
			"etaConv"         : SystemParticipantTestData.pvInput.etaConv.getValue().doubleValue().toString(),
			"height"          : SystemParticipantTestData.pvInput.height.getValue().doubleValue().toString(),
			"id"              : SystemParticipantTestData.pvInput.id,
			"kG"              : SystemParticipantTestData.pvInput.kG.toString(),
			"kT"              : SystemParticipantTestData.pvInput.kT.toString(),
			"marketReaction"  : SystemParticipantTestData.pvInput.marketReaction.toString(),
			"node"            : SystemParticipantTestData.pvInput.node.uuid.toString(),
			"operatesUntil"   : TimeTools.toString(SystemParticipantTestData.pvInput.operationTime.endDate.orElse(ZonedDateTime.now())),
			"operatesFrom"    : TimeTools.toString(SystemParticipantTestData.pvInput.operationTime.startDate.orElse(ZonedDateTime.now())),
			"operator"        : SystemParticipantTestData.pvInput.operator.getUuid().toString(),
			"qCharacteristics": SystemParticipantTestData.pvInput.qCharacteristics,
			"sRated"          : SystemParticipantTestData.pvInput.sRated.to(StandardUnits.S_RATED).getValue().doubleValue().toString()
		]
		WecInput         | SystemParticipantTestData.wecInput         || [
			"uuid"            : SystemParticipantTestData.wecInput.uuid.toString(),
			"id"              : SystemParticipantTestData.wecInput.id,
			"marketReaction"  : SystemParticipantTestData.wecInput.marketReaction.toString(),
			"node"            : SystemParticipantTestData.wecInput.node.uuid.toString(),
			"operatesUntil"   : TimeTools.toString(SystemParticipantTestData.wecInput.operationTime.endDate.orElse(ZonedDateTime.now())),
			"operatesFrom"    : TimeTools.toString(SystemParticipantTestData.wecInput.operationTime.startDate.orElse(ZonedDateTime.now())),
			"operator"        : SystemParticipantTestData.wecInput.operator.getUuid().toString(),
			"qCharacteristics": SystemParticipantTestData.wecInput.qCharacteristics,
			"type"            : SystemParticipantTestData.wecInput.type.getUuid().toString()
		]
		ChpInput         | SystemParticipantTestData.chpInput         || [
			"uuid"            : SystemParticipantTestData.chpInput.uuid.toString(),
			"id"              : SystemParticipantTestData.chpInput.id,
			"marketReaction"  : SystemParticipantTestData.chpInput.marketReaction.toString(),
			"node"            : SystemParticipantTestData.chpInput.node.getUuid().toString(),
			"operatesUntil"   : TimeTools.toString(SystemParticipantTestData.chpInput.operationTime.endDate.orElse(ZonedDateTime.now())),
			"operatesFrom"    : TimeTools.toString(SystemParticipantTestData.chpInput.operationTime.startDate.orElse(ZonedDateTime.now())),
			"operator"        : SystemParticipantTestData.chpInput.operator.getUuid().toString(),
			"qCharacteristics": SystemParticipantTestData.chpInput.qCharacteristics,
			"thermalBus"      : SystemParticipantTestData.chpInput.thermalBus.getUuid().toString(),
			"thermalStorage"  : SystemParticipantTestData.chpInput.thermalStorage.getUuid().toString(),
			"type"            : SystemParticipantTestData.chpInput.type.getUuid().toString(),
		]
		BmInput          | SystemParticipantTestData.bmInput          || [
			"uuid"            : SystemParticipantTestData.bmInput.uuid.toString(),
			"costControlled"  : SystemParticipantTestData.bmInput.costControlled.toString(),
			"feedInTariff"    : SystemParticipantTestData.bmInput.feedInTariff.to(StandardUnits.ENERGY_PRICE).getValue().doubleValue().toString(),
			"id"              : SystemParticipantTestData.bmInput.id,
			"marketReaction"  : SystemParticipantTestData.bmInput.marketReaction.toString(),
			"node"            : SystemParticipantTestData.bmInput.node.uuid.toString(),
			"operatesUntil"   : TimeTools.toString(SystemParticipantTestData.bmInput.operationTime.endDate.orElse(ZonedDateTime.now())),
			"operatesFrom"    : TimeTools.toString(SystemParticipantTestData.bmInput.operationTime.startDate.orElse(ZonedDateTime.now())),
			"operator"        : SystemParticipantTestData.bmInput.operator.getUuid().toString(),
			"qCharacteristics": SystemParticipantTestData.bmInput.qCharacteristics,
			"type"            : SystemParticipantTestData.bmInput.type.getUuid().toString()
		]
		EvInput          | SystemParticipantTestData.evInput          || [
			"uuid"            : SystemParticipantTestData.evInput.uuid.toString(),
			"id"              : SystemParticipantTestData.evInput.id,
			"node"            : SystemParticipantTestData.evInput.node.uuid.toString(),
			"operatesUntil"   : TimeTools.toString(SystemParticipantTestData.evInput.operationTime.endDate.orElse(ZonedDateTime.now())),
			"operatesFrom"    : TimeTools.toString(SystemParticipantTestData.evInput.operationTime.startDate.orElse(ZonedDateTime.now())),
			"operator"        : SystemParticipantTestData.evInput.operator.getUuid().toString(),
			"qCharacteristics": SystemParticipantTestData.evInput.qCharacteristics,
			"type"            : SystemParticipantTestData.evInput.type.getUuid().toString()
		]

		LoadInput        | SystemParticipantTestData.loadInput        || [
			"uuid"               : SystemParticipantTestData.loadInput.uuid.toString(),
			"cosphiRated"        : SystemParticipantTestData.loadInput.cosphiRated.toString(),
			"dsm"                : SystemParticipantTestData.loadInput.dsm.toString(),
			"eConsAnnual"        : SystemParticipantTestData.loadInput.eConsAnnual.getValue().doubleValue().toString(),
			"id"                 : SystemParticipantTestData.loadInput.id,
			"node"               : SystemParticipantTestData.loadInput.node.uuid.toString(),
			"operatesUntil"      : TimeTools.toString(SystemParticipantTestData.loadInput.operationTime.endDate.orElse(ZonedDateTime.now())),
			"operatesFrom"       : TimeTools.toString(SystemParticipantTestData.loadInput.operationTime.startDate.orElse(ZonedDateTime.now())),
			"operator"           : SystemParticipantTestData.loadInput.operator.getUuid().toString(),
			"qCharacteristics"   : SystemParticipantTestData.loadInput.qCharacteristics,
			"sRated"             : SystemParticipantTestData.loadInput.sRated.getValue().doubleValue().toString(),
			"standardLoadProfile": SystemParticipantTestData.loadInput.standardLoadProfile.key
		]
		StorageInput     | SystemParticipantTestData.storageInput     || [
			"uuid"            : SystemParticipantTestData.storageInput.uuid.toString(),
			"behaviour"       : SystemParticipantTestData.storageInput.behaviour.token,
			"id"              : SystemParticipantTestData.storageInput.id,
			"node"            : SystemParticipantTestData.storageInput.node.uuid.toString(),
			"operatesUntil"   : TimeTools.toString(SystemParticipantTestData.storageInput.operationTime.endDate.orElse(ZonedDateTime.now())),
			"operatesFrom"    : TimeTools.toString(SystemParticipantTestData.storageInput.operationTime.startDate.orElse(ZonedDateTime.now())),
			"operator"        : SystemParticipantTestData.storageInput.operator.getUuid().toString(),
			"qCharacteristics": SystemParticipantTestData.storageInput.qCharacteristics,
			"type"            : SystemParticipantTestData.storageInput.type.getUuid().toString()
		]
		HpInput          | SystemParticipantTestData.hpInput          || [
			"uuid"            : SystemParticipantTestData.hpInput.uuid.toString(),
			"id"              : SystemParticipantTestData.hpInput.id,
			"node"            : SystemParticipantTestData.hpInput.node.uuid.toString(),
			"operatesUntil"   : TimeTools.toString(SystemParticipantTestData.hpInput.operationTime.endDate.orElse(ZonedDateTime.now())),
			"operatesFrom"    : TimeTools.toString(SystemParticipantTestData.hpInput.operationTime.startDate.orElse(ZonedDateTime.now())),
			"operator"        : SystemParticipantTestData.hpInput.operator.getUuid().toString(),
			"qCharacteristics": SystemParticipantTestData.hpInput.qCharacteristics,
			"thermalBus"      : SystemParticipantTestData.hpInput.thermalBus.uuid.toString(),
			"type"            : SystemParticipantTestData.hpInput.type.getUuid().toString()
		]
	}
}
