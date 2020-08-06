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
import edu.ie3.datamodel.models.input.system.BmInput
import edu.ie3.datamodel.models.input.system.ChpInput
import edu.ie3.datamodel.models.input.system.EvInput
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.HpInput
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.StorageInput
import edu.ie3.datamodel.models.input.system.WecInput
import edu.ie3.datamodel.models.input.system.type.BmTypeInput
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput
import edu.ie3.datamodel.models.input.system.type.EvTypeInput
import edu.ie3.datamodel.models.input.system.type.HpTypeInput
import edu.ie3.datamodel.models.input.system.type.StorageTypeInput
import edu.ie3.datamodel.models.input.system.type.WecTypeInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SystemParticipantTestData
import edu.ie3.test.common.TypeTestData
import edu.ie3.util.TimeTools
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

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
			"geo_position"  : "{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"id"           : "node_a",
			"operates_until": "2020-03-25T15:11:31Z[UTC]",
			"operates_from" : "2020-03-24T15:11:31Z[UTC]",
			"operator"     : "f15105c4-a2de-4ab8-a621-4bc98e372d92",
			"slack"        : "true",
			"subnet"       : "1",
			"v_target"      : "1.0",
			"volt_lvl"      : "Höchstspannung",
			"v_rated"       : "380.0"
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
			"auto_tap"        : "true",
			"id"             : "3w_test",
			"parallel_devices": "1",
			"node_a"          : "4ca90220-74c2-4369-9afa-a18bf068840d",
			"node_b"          : "47d29df0-ba2d-4d23-8e75-c82229c5c758",
			"node_c"          : "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2",
			"operates_until"  : "2020-03-25T15:11:31Z[UTC]",
			"operates_from"   : "2020-03-24T15:11:31Z[UTC]",
			"operator"       : "f15105c4-a2de-4ab8-a621-4bc98e372d92",
			"tap_pos"         : "0",
			"type"           : "5b0ee546-21fb-4a7f-a801-5dbd3d7bb356"
		]
		Transformer2WInput | GridTestData.transformerCtoG    || [
			"uuid"           : "5dc88077-aeb6-4711-9142-db57292640b1",
			"auto_tap"        : "true",
			"id"             : "2w_parallel_2",
			"parallel_devices": "1",
			"node_a"          : "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2",
			"node_b"          : "aaa74c1a-d07e-4615-99a5-e991f1d81cc4",
			"operates_until"  : "2020-03-25T15:11:31Z[UTC]",
			"operates_from"   : "2020-03-24T15:11:31Z[UTC]",
			"operator"       : "f15105c4-a2de-4ab8-a621-4bc98e372d92",
			"tap_pos"         : "0",
			"type"           : "08559390-d7c0-4427-a2dc-97ba312ae0ac"
		]

		SwitchInput        | GridTestData.switchAtoB         || [
			"uuid"         : "5dc88077-aeb6-4711-9142-db57287640b1",
			"closed"       : "true",
			"id"           : "test_switch_AtoB",
			"node_a"        : "4ca90220-74c2-4369-9afa-a18bf068840d",
			"node_b"        : "47d29df0-ba2d-4d23-8e75-c82229c5c758",
			"operates_until": "2020-03-25T15:11:31Z[UTC]",
			"operates_from" : "2020-03-24T15:11:31Z[UTC]",
			"operator"     : "f15105c4-a2de-4ab8-a621-4bc98e372d92"
		]

		LineInput          | GridTestData.lineCtoD           || [
			"uuid"             : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"geo_position"      : "{\"type\":\"LineString\",\"coordinates\":[[7.411111,51.492528],[7.414116,51.484136]],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"id"               : "test_line_CtoD",
			"length"           : "0.003",
			"parallel_devices"  : "2",
			"node_a"            : "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2",
			"node_b"            : "6e0980e0-10f2-4e18-862b-eb2b7c90509b",
			"olm_characteristic": "olm:{(0.00,1.00)}",
			"operates_until"    : "2020-03-25T15:11:31Z[UTC]",
			"operates_from"     : "2020-03-24T15:11:31Z[UTC]",
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
			"cosphi_rated"     : SystemParticipantTestData.fixedFeedInInput.cosPhiRated.toString(),
			"id"              : SystemParticipantTestData.fixedFeedInInput.id,
			"node"            : SystemParticipantTestData.fixedFeedInInput.node.uuid.toString(),
			"operates_until"   : SystemParticipantTestData.fixedFeedInInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operates_from"    : SystemParticipantTestData.fixedFeedInInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.fixedFeedInInput.operator.getUuid().toString(),
			"q_characteristics": SystemParticipantTestData.cosPhiFixedDeSerialized,
			"s_rated"          : SystemParticipantTestData.fixedFeedInInput.sRated.to(StandardUnits.S_RATED).getValue().doubleValue().toString()
		]
		PvInput          | SystemParticipantTestData.pvInput          || [
			"uuid"            : SystemParticipantTestData.pvInput.uuid.toString(),
			"albedo"          : SystemParticipantTestData.pvInput.albedo.toString(),
			"azimuth"         : SystemParticipantTestData.pvInput.azimuth.to(StandardUnits.AZIMUTH).getValue().doubleValue().toString(),
			"cosphi_rated"     : SystemParticipantTestData.pvInput.cosPhiRated.toString(),
			"eta_conv"         : SystemParticipantTestData.pvInput.etaConv.getValue().doubleValue().toString(),
			"height"          : SystemParticipantTestData.pvInput.height.getValue().doubleValue().toString(),
			"id"              : SystemParticipantTestData.pvInput.id,
			"k_g"              : SystemParticipantTestData.pvInput.kG.toString(),
			"k_t"              : SystemParticipantTestData.pvInput.kT.toString(),
			"market_reaction"  : SystemParticipantTestData.pvInput.marketReaction.toString(),
			"node"            : SystemParticipantTestData.pvInput.node.uuid.toString(),
			"operates_until"   : SystemParticipantTestData.pvInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operates_from"    : SystemParticipantTestData.pvInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.pvInput.operator.getUuid().toString(),
			"q_characteristics": SystemParticipantTestData.cosPhiFixedDeSerialized,
			"s_rated"          : SystemParticipantTestData.pvInput.sRated.to(StandardUnits.S_RATED).getValue().doubleValue().toString()
		]
		WecInput         | SystemParticipantTestData.wecInput         || [
			"uuid"            : SystemParticipantTestData.wecInput.uuid.toString(),
			"id"              : SystemParticipantTestData.wecInput.id,
			"market_reaction"  : SystemParticipantTestData.wecInput.marketReaction.toString(),
			"node"            : SystemParticipantTestData.wecInput.node.uuid.toString(),
			"operates_until"   : SystemParticipantTestData.wecInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operates_from"    : SystemParticipantTestData.wecInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.wecInput.operator.uuid.toString(),
			"q_characteristics": SystemParticipantTestData.cosPhiPDeSerialized,
			"type"            : SystemParticipantTestData.wecInput.type.uuid.toString()
		]
		ChpInput         | SystemParticipantTestData.chpInput         || [
			"uuid"            : SystemParticipantTestData.chpInput.uuid.toString(),
			"id"              : SystemParticipantTestData.chpInput.id,
			"market_reaction"  : SystemParticipantTestData.chpInput.marketReaction.toString(),
			"node"            : SystemParticipantTestData.chpInput.node.uuid.toString(),
			"operates_until"   : SystemParticipantTestData.chpInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operates_from"    : SystemParticipantTestData.chpInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.chpInput.operator.uuid.toString(),
			"q_characteristics": SystemParticipantTestData.cosPhiFixedDeSerialized,
			"thermal_bus"      : SystemParticipantTestData.chpInput.thermalBus.uuid.toString(),
			"thermal_storage"  : SystemParticipantTestData.chpInput.thermalStorage.uuid.toString(),
			"type"            : SystemParticipantTestData.chpInput.type.uuid.toString(),
		]
		BmInput          | SystemParticipantTestData.bmInput          || [
			"uuid"            : SystemParticipantTestData.bmInput.uuid.toString(),
			"cost_controlled"  : SystemParticipantTestData.bmInput.costControlled.toString(),
			"feed_in_tariff"    : SystemParticipantTestData.bmInput.feedInTariff.to(StandardUnits.ENERGY_PRICE).getValue().doubleValue().toString(),
			"id"              : SystemParticipantTestData.bmInput.id,
			"market_reaction"  : SystemParticipantTestData.bmInput.marketReaction.toString(),
			"node"            : SystemParticipantTestData.bmInput.node.uuid.toString(),
			"operates_until"   : SystemParticipantTestData.bmInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operates_from"    : SystemParticipantTestData.bmInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.bmInput.operator.uuid.toString(),
			"q_characteristics": SystemParticipantTestData.qVDeSerialized,
			"type"            : SystemParticipantTestData.bmInput.type.uuid.toString()
		]
		EvInput          | SystemParticipantTestData.evInput          || [
			"uuid"            : SystemParticipantTestData.evInput.uuid.toString(),
			"id"              : SystemParticipantTestData.evInput.id,
			"node"            : SystemParticipantTestData.evInput.node.uuid.toString(),
			"operates_until"   : SystemParticipantTestData.evInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operates_from"    : SystemParticipantTestData.evInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.evInput.operator.getUuid().toString(),
			"q_characteristics": SystemParticipantTestData.cosPhiFixedDeSerialized,
			"type"            : SystemParticipantTestData.evInput.type.getUuid().toString()
		]

		LoadInput        | SystemParticipantTestData.loadInput        || [
			"uuid"               : SystemParticipantTestData.loadInput.uuid.toString(),
			"cosphi_rated"        : SystemParticipantTestData.loadInput.cosPhiRated.toString(),
			"dsm"                : SystemParticipantTestData.loadInput.dsm.toString(),
			"e_cons_annual"        : SystemParticipantTestData.loadInput.eConsAnnual.getValue().doubleValue().toString(),
			"id"                 : SystemParticipantTestData.loadInput.id,
			"node"               : SystemParticipantTestData.loadInput.node.uuid.toString(),
			"operates_until"      : SystemParticipantTestData.loadInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operates_from"       : SystemParticipantTestData.loadInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"           : SystemParticipantTestData.loadInput.operator.uuid.toString(),
			"q_characteristics"   : SystemParticipantTestData.cosPhiFixedDeSerialized,
			"s_rated"             : SystemParticipantTestData.loadInput.sRated.getValue().doubleValue().toString(),
			"standard_load_profile": SystemParticipantTestData.loadInput.standardLoadProfile.key
		]
		StorageInput     | SystemParticipantTestData.storageInput     || [
			"uuid"            : SystemParticipantTestData.storageInput.uuid.toString(),
			"behaviour"       : SystemParticipantTestData.storageInput.behaviour.token,
			"id"              : SystemParticipantTestData.storageInput.id,
			"node"            : SystemParticipantTestData.storageInput.node.uuid.toString(),
			"operates_until"   : SystemParticipantTestData.storageInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operates_from"    : SystemParticipantTestData.storageInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.storageInput.operator.uuid.toString(),
			"q_characteristics": SystemParticipantTestData.cosPhiFixedDeSerialized,
			"type"            : SystemParticipantTestData.storageInput.type.uuid.toString()
		]
		HpInput          | SystemParticipantTestData.hpInput          || [
			"uuid"            : SystemParticipantTestData.hpInput.uuid.toString(),
			"id"              : SystemParticipantTestData.hpInput.id,
			"node"            : SystemParticipantTestData.hpInput.node.uuid.toString(),
			"operates_until"   : SystemParticipantTestData.hpInput.operationTime.endDate.orElse(ZonedDateTime.now()).toString(),
			"operates_from"    : SystemParticipantTestData.hpInput.operationTime.startDate.orElse(ZonedDateTime.now()).toString(),
			"operator"        : SystemParticipantTestData.hpInput.operator.uuid.toString(),
			"q_characteristics": SystemParticipantTestData.cosPhiFixedDeSerialized,
			"thermal_bus"      : SystemParticipantTestData.hpInput.thermalBus.uuid.toString(),
			"type"            : SystemParticipantTestData.hpInput.type.uuid.toString()
		]
	}

	def "The InputEntityProcessor should de-serialize a provided NodeGraphicInput with point correctly"() {
		given:
		InputEntityProcessor processor = new InputEntityProcessor(NodeGraphicInput)
		NodeGraphicInput validNode = GridTestData.nodeGraphicC
		Map expected = [
			"uuid"        : "09aec636-791b-45aa-b981-b14edf171c4c",
			"graphic_layer": "main",
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
			"graphic_layer": "main",
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
			"graphic_layer": "main",
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
			"quarter_hour": "4",
			"k_wd"        : "1.2",
			"k_sa"        : "2.3",
			"k_su"        : "3.4",
			"my_wd"       : "4.5",
			"my_sa"       : "5.6",
			"my_su"       : "6.7",
			"sigma_wd"    : "7.8",
			"sigma_sa"    : "8.9",
			"sigma_su"    : "9.1"
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
			"cosphi_rated"     : "0.95",
			"cp_characteristic": "cP:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}",
			"eta_conv"         : "90.0",
			"s_rated"          : "2500.0",
			"rotor_area"       : "2000.0",
			"hub_height"       : "130.0"
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
			"r_sc"     : "45.375",
			"x_sc"     : "102.759",
			"g_m"      : "0.0",
			"b_m"      : "0.0",
			"s_rated"  : "20000.0",
			"v_rated_a" : "110.0",
			"v_rated_b" : "20.0",
			"d_v"      : "1.5",
			"d_phi"    : "0.0",
			"tap_side" : "false",
			"tap_neutr": "0",
			"tap_max"  : "10",
			"tap_min"  : "-10"
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
			"s_rated_a" : "120000.0",
			"s_rated_b" : "60000.0",
			"s_rated_c" : "40000.0",
			"v_rated_a" : "380.0",
			"v_rated_b" : "110.0",
			"v_rated_c" : "20.0",
			"r_sc_a"    : "0.3",
			"r_sc_b"    : "0.025",
			"r_sc_c"    : "8.0E-4",
			"x_sc_a"    : "1.0",
			"x_sc_b"    : "0.08",
			"x_sc_c"    : "0.003",
			"g_m"      : "40000.0",
			"b_m"      : "1000.0",
			"d_v"      : "1.5",
			"d_phi"    : "0.0",
			"tap_neutr": "0",
			"tap_min"  : "-10",
			"tap_max"  : "10"
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
			"i_max"  : "300.0",
			"v_rated": "20.0"
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
			"e_storage"   : "100.0",
			"e_cons"      : "23.0",
			"s_rated"     : "22.0",
			"cosphi_rated": "0.9"
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
			"eta_el"      : "95.0",
			"eta_thermal" : "90.0",
			"s_rated"     : "58.0",
			"cosphi_rated": "0.98",
			"p_thermal"   : "49.59",
			"p_own"       : "5.0"
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
			"s_rated"     : "45.0",
			"cosphi_rated": "0.975",
			"p_thermal"   : "26.3"
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
			"active_power_gradient": "5.0",
			"s_rated"             : "800.0",
			"cosphi_rated"        : "0.965",
			"eta_conv"            : "89.0"
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
			"e_storage"           : "200.0",
			"s_rated"             : "13.0",
			"cosphi_rated"        : "0.997",
			"p_max"               : "12.961",
			"active_power_gradient": "3.0",
			"eta"                : "92.0",
			"dod"                : "20.0",
			"life_time"           : "43800.0",
			"life_cycle"          : "100000"
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
			"geo_position"  : "",
			"id"           : "node_d",
			"operates_from" : "",
			"operates_until": "",
			"operator"     : "",
			"slack"        : "false",
			"subnet"       : "4",
			"uuid"         : "6e0980e0-10f2-4e18-862b-eb2b7c90509b",
			"v_rated"       : "20.0",
			"v_target"      : "1.0",
			"volt_lvl"      : "Mittelspannung"
		]

		when:
		Optional<Map<String, String>> actual = processor.handleEntity(nodeWithOutOperator)

		then:
		actual.present
		actual.get() == expected
	}
}
