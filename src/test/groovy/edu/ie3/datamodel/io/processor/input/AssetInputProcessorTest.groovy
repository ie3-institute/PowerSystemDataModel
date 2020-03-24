package edu.ie3.datamodel.io.processor.input

import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.Transformer3WInput
import edu.ie3.test.common.GridTestData
import edu.ie3.util.TimeTools
import spock.lang.Specification

import java.time.ZoneId

/**
 * //ToDo: Class Description
 *
 * @version 0.1* @since 24.03.20
 */
class AssetInputProcessorTest extends Specification {

    def "A ResultEntityProcessor should de-serialize a provided SystemParticipantInput correctly"() {


    }

    def "A ResultEntityProcessor should de-serialize a provided NodeInput correctly"() {
        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new AssetInputProcessor(NodeInput)
        def validResult = GridTestData.nodeA

        Map expectedResults = [
                "uuid"         : "5dc88077-aeb6-4711-9142-db57292640b1",
                "geoPosition"  : "{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
                "id"           : "node_a",
                "operatesuntil": "2020-03-25 15:11:31",
                "operatesfrom" : "2020-03-24 15:11:31",
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

//        println "["
//        processingResult.get().each { k, v -> println "\"${k}\":\"${v.replaceAll("\"", \"\\"\")}\"," }
//        println "]"

    }


    def "A ResultEntityProcessor should de-serialize a provided ConnectorInput correctly"() {
        given:
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
        def sysPartResProcessor = new AssetInputProcessor(modelClass)
        def validInput = modelInstance


        Map expectedResults = [
                "uuid"         : "5dc88077-aeb6-4711-9142-db57292640b1",
                "geoPosition"  : "{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
                "id"           : "node_a",
                "operatesuntil": "2020-03-25 15:11:31",
                "operatesfrom" : "2020-03-24 15:11:31",
                "operator"     : "8f9682df-0744-4b58-a122-f0dc730f6510",
                "slack"        : "true",
                "subnet"       : "1",
                "vTarget"      : "1.0",
                "voltlvl"      : "Höchstspannung (380 kV)",
                "vrated"       : "380.0",
        ]

        when: "the entity is passed to the processor"
        def processingResult = sysPartResProcessor.handleEntity(validInput)


        then: "make sure that the result is as expected "
        processingResult.present
        processingResult.get() == expectedResults

        where:
        modelClass         | modelInstance                   || expectedResult
        Transformer3WInput | GridTestData.transformerAtoBtoC || "// todo "
        Transformer2WInput | GridTestData.transformerCtoG    || "// todo "
        // todo JH Line, Switch, Transformer2, Transformer3,

//        println "["
//        processingResult.get().each { k, v -> println "\"${k}\":\"${v.replaceAll("\"", \"\\"\")}\"," }
//        println "]"

    }


}
