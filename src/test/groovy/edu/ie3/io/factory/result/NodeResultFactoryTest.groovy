package edu.ie3.io.factory.result

import edu.ie3.exceptions.FactoryException
import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.result.NodeResult
import edu.ie3.util.TimeTools
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

class NodeResultFactoryTest extends Specification {

    def "A NodeResultFactory should contain all expected classes for parsing"() {
        given:
        def resultFactory = new NodeResultFactory()
        def expectedClasses = [NodeResult]

        expect:
        resultFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A NodeResultFactory should parse a WecResult correctly"() {
        given: "a system participant factory and model data"
        def resultFactory = new NodeResultFactory()
        HashMap<String, String> parameter = [:]
        parameter["timestamp"] = "16/01/2010 17:27:46"
        parameter["inputModel"] = "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7"
        parameter["vmag"] = "2"
        parameter["vang"] = "2"

        when:
        Optional<? extends NodeResult> result = resultFactory.getEntity(new SimpleEntityData(parameter, NodeResult))

        then:
        result.present
        result.get().getClass() == NodeResult
        result.get().vMag == Quantities.getQuantity(Double.parseDouble(parameter["vmag"]), StandardUnits.TARGET_VOLTAGE)
        result.get().vAng == Quantities.getQuantity(Double.parseDouble(parameter["vang"]), StandardUnits.DPHI_TAP) //TODO
        result.get().timestamp == TimeTools.toZonedDateTime(parameter["timestamp"])
        result.get().inputModel == UUID.fromString(parameter["inputModel"])

    }

    def "A NodeResultFactory should throw an exception on invalid or incomplete data"() {
        given: "a system participant factory and model data"
        def resultFactory = new NodeResultFactory()
        Map<String, String> parameter = [:]
        parameter["timestamp"] = "16/01/2010 17:27:46"
        parameter["inputModel"] = "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7"
        parameter["vmag"] = "2"

        when:
        resultFactory.getEntity(new SimpleEntityData(parameter, NodeResult.class))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [inputModel, timestamp, vmag] with data {inputModel -> 91ec3bcf-1897-4d38-af67-0bf7c9fa73c7,timestamp -> 16/01/2010 17:27:46,vmag -> 2} are invalid for instance of NodeResult. \n" +
                "The following fields to be passed to a constructor of NodeResult are possible:\n" +
                "0: [inputModel, timestamp, vang, vmag]\n" +
                "1: [inputModel, timestamp, uuid, vang, vmag]\n"

    }
}
