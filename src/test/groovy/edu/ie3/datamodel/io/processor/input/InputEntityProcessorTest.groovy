package edu.ie3.datamodel.io.processor.input

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
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput
import edu.ie3.datamodel.models.input.system.type.EvTypeInput
import edu.ie3.datamodel.models.input.system.type.WecTypeInput
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SystemParticipantTestData
import edu.ie3.test.common.TypeTestData
import edu.ie3.util.TimeTools
import edu.ie3.util.quantities.interfaces.Currency
import edu.ie3.util.quantities.interfaces.EnergyPrice
import edu.ie3.util.quantities.interfaces.SpecificConductance
import edu.ie3.util.quantities.interfaces.SpecificEnergy
import edu.ie3.util.quantities.interfaces.SpecificResistance
import spock.lang.Specification

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.ElectricCurrent
import javax.measure.quantity.ElectricPotential
import javax.measure.quantity.Energy
import javax.measure.quantity.Power
import java.time.ZoneId
import java.time.ZonedDateTime

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
                "uuid"         : "5dc88077-aeb6-4711-9142-db57292640b1",
                "geoPosition"  : "{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
                "id"           : "node_a",
                "operatesUntil": "2020-03-25 15:11:31",
                "operatesFrom" : "2020-03-24 15:11:31",
                "operator"     : "8f9682df-0744-4b58-a122-f0dc730f6510",
                "slack"        : "true",
                "subnet"       : "1",
                "vTarget"      : "1.0",
                "voltLvl"      : "Höchstspannung (380 kV)",
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

    def "The InputEntityProcessor should de-serialize a provided NodeGraphicInput with point correctly"(){
        given:
        InputEntityProcessor processor = new InputEntityProcessor(NodeGraphicInput.class)
        NodeGraphicInput validNode = GridTestData.nodeGraphicC
        Map expected = [
                "uuid"          : "09aec636-791b-45aa-b981-b14edf171c4c",
                "graphicLayer"  : "main",
                "path"          : "",
                "point"         : "{\"type\":\"Point\",\"coordinates\":[0.0,10],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
                "node"          : "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2"
        ]

        when:
        Optional<LinkedHashMap<String, String>> actual = processor.handleEntity(validNode)

        then:
        actual.isPresent()
        actual.get() == expected
    }

    def "The InputEntityProcessor should de-serialize a provided NodeGraphicInput with path correctly"(){
        given:
        InputEntityProcessor processor = new InputEntityProcessor(NodeGraphicInput.class)
        NodeGraphicInput validNode = GridTestData.nodeGraphicD
        Map expected = [
                "uuid"          : "9ecad435-bd16-4797-a732-762c09d4af25",
                "graphicLayer"  : "main",
                "path"          : "{\"type\":\"LineString\",\"coordinates\":[[-1,0.0],[1,0.0]],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
                "point"         : "",
                "node"          : "6e0980e0-10f2-4e18-862b-eb2b7c90509b"
        ]

        when:
        Optional<LinkedHashMap<String, String>> actual = processor.handleEntity(validNode)

        then:
        actual.isPresent()
        actual.get() == expected
    }

    def "The InputEntityProcessor should de-serialize a provided LineGraphicInput correctly"(){
        given:
        InputEntityProcessor processor = new InputEntityProcessor(LineGraphicInput.class)
        LineGraphicInput validNode = GridTestData.lineGraphicCtoD
        Map expected = [
                "uuid"          : "ece86139-3238-4a35-9361-457ecb4258b0",
                "graphicLayer"  : "main",
                "path"          : "{\"type\":\"LineString\",\"coordinates\":[[0.0,0.0],[0.0,10]],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
                "line"          : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"
        ]

        when:
        Optional<LinkedHashMap<String, String>> actual = processor.handleEntity(validNode)

        then:
        actual.isPresent()
        actual.get() == expected
    }

    def "The InputEntityProcessor should de-serialize a provided OperatorInput correctly"() {
        given:
        InputEntityProcessor processor = new InputEntityProcessor(OperatorInput.class)
        OperatorInput operator = new OperatorInput(UUID.fromString("420ee39c-dd5a-4d9c-9156-23dbdef13e5e"), "Prof. Brokkoli")
        Map expected = [
                "uuid"  : "420ee39c-dd5a-4d9c-9156-23dbdef13e5e",
                "id"    : "Prof. Brokkoli"
        ]

        when:
        Optional<LinkedHashMap<String, String>> actual = processor.handleEntity(operator)

        then:
        actual.isPresent()
        actual.get() == expected
    }

    def "The InputEntityProcessor should de-serialize a provided RandomLoadParameters correctly"() {
        given:
        InputEntityProcessor processor = new InputEntityProcessor(RandomLoadParameters.class)
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
                "uuid"          : "a5b0f432-27b5-4b3e-b87a-61867b9edd79",
                "quarterHour"   : "4",
                "kWd"           : "1.2",
                "kSa"           : "2.3",
                "kSu"           : "3.4",
                "myWd"          : "4.5",
                "mySa"          : "5.6",
                "mySu"          : "6.7",
                "sigmaWd"       : "7.8",
                "sigmaSa"       : "8.9",
                "sigmaSu"       : "9.1"
        ]

        when:
        Optional<LinkedHashMap<String, String>> actual = processor.handleEntity(parameters)

        then:
        actual.isPresent()
        actual.get() == expected
    }

    def "The InputEntityProcessor should de-serialize a provided WecCharacteristicInput correctly"() {
        given:
        InputEntityProcessor processor = new InputEntityProcessor(WecCharacteristicInput.class)
        WecCharacteristicInput characteristic = TypeTestData.wecCharacteristic
        Map expected = [
                "uuid"              : "ab5ed9e4-62b5-4f40-adf1-286bda97569c",
                "type"              : "a24fc5b9-a26f-44de-96b8-c9f50b665cb3",
                "characteristic"    : "{(0.0,0.0), (8.0,0.2), (12.0,0.5), (14.0,1.0), (22.0,0.0)}"
        ]

        when:
        Optional<Map<String, String>> actual = processor.handleEntity(characteristic)

        then:
        actual.isPresent()
        actual.get() == expected
    }

    def "The InputEntityProcessor should de-serialize a provided WecTypeInput correctly"() {
        given:
        InputEntityProcessor processor = new InputEntityProcessor(WecTypeInput.class)
        WecTypeInput type = TypeTestData.wecType
        Map expected = [
                "uuid"          : "a24fc5b9-a26f-44de-96b8-c9f50b665cb3",
                "id"            : "Test wec type",
                "capex"         : "100.0",
                "opex"          : "101.0",
                "cosphiRated"   : "0.95",
                "etaConv"       : "90.0",
                "sRated"        : "2500.0",
                "rotorArea"     : "2000.0",
                "hubHeight"     : "130.0"
        ]

        when:
        Optional<Map<String, String>> actual = processor.handleEntity(type)

        then:
        actual.isPresent()
        actual.get() == expected
    }

    def "The InputEntityProcessor should de-serialize a provided Transformer2WTypeInput correctly"() {
        given:
        InputEntityProcessor processor = new InputEntityProcessor(Transformer2WTypeInput.class)
        Transformer2WTypeInput type = GridTestData.transformerTypeBtoD
        Map expected = [
                "uuid"      : "202069a7-bcf8-422c-837c-273575220c8a",
                "id"        : "HS-MS_1",
                "rSc"       : "45.375",
                "xSc"       : "102.759",
                "gM"        : "0.0",
                "bM"        : "0.0",
                "sRated"    : "20000.0",
                "vRatedA"   : "110.0",
                "vRatedB"   : "20.0",
                "dV"        : "1.5",
                "dPhi"      : "0.0",
                "tapSide"   : "false",
                "tapNeutr"  : "0",
                "tapMax"    : "10",
                "tapMin"    : "-10"
        ]

        when:
        Optional<Map<String, String>> actual = processor.handleEntity(type)

        then:
        actual.isPresent()
        actual.get() == expected
    }

    def "The InputEntityProcessor should de-serialize a provided Transformer3WTypeInput correctly"() {
        given:
        InputEntityProcessor processor = new InputEntityProcessor(Transformer3WTypeInput.class)
        Transformer3WTypeInput type = GridTestData.transformerTypeAtoBtoC
        Map expected = [
                "uuid"      : "5b0ee546-21fb-4a7f-a801-5dbd3d7bb356",
                "id"        : "HöS-HS-MS_1",
                "sRatedA"   : "120000.0",
                "sRatedB"   : "60000.0",
                "sRatedC"   : "40000.0",
                "vRatedA"   : "380.0",
                "vRatedB"   : "110.0",
                "vRatedC"   : "20.0",
                "rScA"      : "0.3",
                "rScB"      : "0.025",
                "rScC"      : "8.0E-4",
                "xScA"      : "1.0",
                "xScB"      : "0.08",
                "xScC"      : "0.003",
                "gM"        : "40000.0",
                "bM"        : "1000.0",
                "dV"        : "1.5",
                "dPhi"      : "0.0",
                "tapNeutr"  : "0",
                "tapMin"    : "-10",
                "tapMax"    : "10"
        ]

        when:
        Optional<Map<String, String>> actual = processor.handleEntity(type)

        then:
        actual.isPresent()
        actual.get() == expected
    }

    def "The InputEntityProcessor should de-serialize a provided LineTypeInput correctly"() {
        given:
        InputEntityProcessor processor = new InputEntityProcessor(LineTypeInput.class)
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
        actual.isPresent()
        actual.get() == expected
    }

    def "The InputEntityProcessor should de-serialize a provided EvTypeInput correctly"() {
        given:
        InputEntityProcessor processor = new InputEntityProcessor(EvTypeInput.class)
        EvTypeInput type = TypeTestData.evType
        Map expected = [
                "uuid"          : "66b0db5d-b2fb-41d0-a9bc-990d6b6a36db",
                "id"            : "ev type",
                "capex"         : "100.0",
                "opex"          : "101.0",
                "eStorage"      : "100.0",
                "eCons"         : "23.0",
                "sRated"        : "22.0",
                "cosphiRated"   : "0.9"
        ]

        when:
        Optional<Map<String, String>> actual = processor.handleEntity(type)

        then:
        actual.isPresent()
        actual.get() == expected
    }

    def "The InputEntityProcessor should de-serialize a provided ChpTypeInput correctly"() {
        given:
        InputEntityProcessor processor = new InputEntityProcessor(ChpTypeInput.class)
        ChpTypeInput type = TypeTestData.chpType
        Map expected = [
                "uuid"          : "1c027d3e-5409-4e52-a0e2-f8a23d5d0af0",
                "id"            : "chp type",
                "capex"         : "100.0",
                "opex"          : "101.0",
                "etaEl"         : "95.0",
                "etaThermal"    : "90.0",
                "sRated"        : "58.0",
                "cosphiRated"   : "0.98",
                "pThermal"      : "49.59",
                "pOwn"          : "5.0"
        ]

        when:
        Optional<Map<String, String>> actual = processor.handleEntity(type)

        then:
        actual.isPresent()
        actual.get() == expected
    }
}
