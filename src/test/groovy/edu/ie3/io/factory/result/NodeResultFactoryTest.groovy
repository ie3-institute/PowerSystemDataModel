package edu.ie3.io.factory.result

import edu.ie3.exceptions.FactoryException
import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.result.NodeResult
import edu.ie3.models.result.system.SystemParticipantResult
import edu.ie3.util.TimeTools
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

class NodeResultFactoryTest extends Specification {

    def "A NodeResultFactory should contain all expected classes for parsing"() {
        given:
        def resultFactory = new NodeResultFactory()
        def expectedClasses = [NodeResult.class]

        expect:
        resultFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A NodeResultFactory should parse a WecResult correctly"() {
        given: "a system participant factory and model data"
        def resultFactory = new NodeResultFactory()
        HashMap<String, String> parameterMap = new HashMap<>();
        parameterMap.put("timestamp", "16/01/2010 17:27:46");
        parameterMap.put("inputModel", "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7");
        parameterMap.put("vmag", "2");
        parameterMap.put("vang", "2");

        when:
        Optional<? extends NodeResult> result = resultFactory.getEntity(new SimpleEntityData(parameterMap, NodeResult.class))

        then:
        result.isPresent()
        result.get().getClass() == NodeResult.class
        result.get().vMag == Quantities.getQuantity(Double.parseDouble(parameterMap.get("vmag")), StandardUnits.TARGET_VOLTAGE)
        result.get().vAng == Quantities.getQuantity(Double.parseDouble(parameterMap.get("vang")), StandardUnits.DPHI_TAP) //TODO
        result.get().timestamp == TimeTools.toZonedDateTime(parameterMap.get("timestamp"))
        result.get().inputModel == UUID.fromString(parameterMap.get("inputModel"))

    }

    def "A NodeResultFactory should throw an exception on invalid or incomplete data"() {
        given: "a system participant factory and model data"
        def resultFactory = new NodeResultFactory()
        HashMap<String, String> parameterMap = new HashMap<>();
        parameterMap.put("timestamp", "16/01/2010 17:27:46");
        parameterMap.put("inputModel", "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7");
        parameterMap.put("vmag", "2");

        when:
        resultFactory.getEntity(new SimpleEntityData(parameterMap, NodeResult.class))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [inputModel, vmag, timestamp] with data {inputModel -> 91ec3bcf-1897-4d38-af67-0bf7c9fa73c7,vmag -> 2,timestamp -> 16/01/2010 17:27:46} are invalid for instance of NodeResult. \n" +
                "The following fields to be passed to a constructor of NodeResult are possible:\n" +
                "0: [vang, inputModel, vmag, timestamp]\n" +
                "1: [vang, inputModel, vmag, uuid, timestamp]\n"

    }
}
