package edu.ie3.io.factory.result

import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.result.ThermalSinkResult
import edu.ie3.test.helper.FactoryTestHelper
import edu.ie3.util.TimeTools
import spock.lang.Specification

class ThermalSinkResultFactoryTest extends Specification implements FactoryTestHelper {

    def "A ThermalSinkResultFactory should contain all expected classes for parsing"() {
        given:
        def resultFactory = new ThermalSinkResultFactory()
        def expectedClasses = [ThermalSinkResult]

        expect:
        resultFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A ThermalSinkResultFactory should parse a WecResult correctly"() {
        given: "a system participant factory and model data"
        def resultFactory = new ThermalSinkResultFactory()

        Map<String, String> parameter = [
            "timestamp":    "2020-01-30 17:26:44",
            "inputModel":   "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
            "qdemand":      "2"
        ]

        when:
        Optional<? extends ThermalSinkResult> result = resultFactory.getEntity(new SimpleEntityData(parameter, ThermalSinkResult))

        then:
        result.present
        result.get().getClass() == ThermalSinkResult
        ((ThermalSinkResult) result.get()).with {
            assert qDemand == getQuant(parameter["qdemand"], StandardUnits.HEAT_DEMAND)
            assert timestamp == TimeTools.toZonedDateTime(parameter["timestamp"])
            assert inputModel == UUID.fromString(parameter["inputModel"])
        }
    }
}
