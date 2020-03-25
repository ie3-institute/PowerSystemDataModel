package edu.ie3.datamodel.io.processor.input

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

/**
 * //ToDo: Class Description
 *
 * @version 0.1* @since 24.03.20
 */
class AssetInputProcessorTest extends Specification {

    def "A ResultEntityProcessor should de-serialize a provided NodeInput correctly"() {
        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new AssetInputProcessor(NodeInput)
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
                "voltlvl"      : "Höchstspannung (380 kV)",
                "vrated"       : "380.0",
        ]

        when: "the entity is passed to the processor"
        def processingResult = sysPartResProcessor.handleEntity(validResult)


        then: "make sure that the result is as expected "
        processingResult.present
        processingResult.get() == expectedResults

    }


    def "A ResultEntityProcessor should de-serialize a provided ConnectorInput correctly"() {
        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new AssetInputProcessor(modelClass)
        def validInput = modelInstance

        when: "the entity is passed to the processor"
        def processingResult = sysPartResProcessor.handleEntity(validInput)


        then: "make sure that the result is as expected "
        processingResult.present

        println "["
        processingResult.get().each { k, v -> println "\"${k}\":\"${v.replaceAll("\"", "\"")}\"," }
        println "]"

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
                "type"               : "5b0ee546-21fb-4a7f-a801-5dbd3d7bb356",
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
                "type"               : "08559390-d7c0-4427-a2dc-97ba312ae0ac",
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
                "operator"           : "8f9682df-0744-4b58-a122-f0dc730f6510",
        ]

        LineInput          | GridTestData.lineCtoD           || [
                "uuid"               : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "geoPosition"        : "{\"type\":\"LineString\",\"coordinates\":[[7.411111,51.492528],[7.414116,51.484136]],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
                "id"                 : "test_line_AtoB",
                "length"             : "0.003",
                "noOfParallelDevices": "2",
                "nodeA"              : "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2",
                "nodeB"              : "bd865a25-58f3-44ac-aa90-c6b6e3cd91b2",
                "olmCharacteristic"  : "olm",
                "operatesUntil"      : "2020-03-25 15:11:31",
                "operatesFrom"       : "2020-03-24 15:11:31",
                "operator"           : "8f9682df-0744-4b58-a122-f0dc730f6510",
                "type"               : "3bed3eb3-9790-4874-89b5-a5434d408088",
        ]

    }

    def "A ResultEntityProcessor should de-serialize a provided SystemParticipantInput correctly"() {

        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new AssetInputProcessor(modelClass)
        def validInput = modelInstance

        when: "the entity is passed to the processor"
        def processingResult = sysPartResProcessor.handleEntity(validInput)


        then: "make sure that the result is as expected "
        processingResult.present

        println "["
        processingResult.get().each { k, v -> println "\"${k}\":\"${v.replaceAll("\"", "\"")}\"," }
        println "]"

        processingResult.get().forEach { k, v ->
            if (k != "nodeInternal")     // the internal 3w node is always randomly generated, hence we can skip to test on this
                assert (v == expectedResult.get(k))
        }


        where:
        modelClass       | modelInstance                              || expectedResult
        FixedFeedInInput | SystemParticipantTestData.fixedFeedInInput || [:]
        PvInput          | SystemParticipantTestData.pvInput          || [:]
        WecInput         | SystemParticipantTestData.wecInput         || [:]
//        ChpInput         |                                            ||
//                BmInput | ||
//                EvInput | ||
//                LoadInput | ||
//                StorageInput | ||
//                HpInput | ||


    }


}
