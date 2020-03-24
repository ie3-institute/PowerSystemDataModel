package edu.ie3.datamodel.io.factory.result

import edu.ie3.datamodel.io.factory.SimpleEntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.connector.*
import edu.ie3.test.helper.FactoryTestHelper
import edu.ie3.util.TimeTools
import spock.lang.Specification

class ConnectorResultFactoryTest extends Specification implements FactoryTestHelper {

    def "A ConnectorResultFactory should contain all expected classes for parsing"() {
        given:
        def resultFactory = new ConnectorResultFactory()
        def expectedClasses = [LineResult, SwitchResult, Transformer2WResult, Transformer3WResult]

        expect:
        resultFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A ConnectorResultFactory should parse a valid result model correctly"() {
        given: "a system participant factory and model data"
        def resultFactory = new ConnectorResultFactory()
        Map<String, String> parameter = [
            "timestamp":    "2020-01-30 17:26:44",
            "inputModel":   "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
            "iamag":        "1.0",
            "iaang":        "90",
            "ibmag":        "0.98123",
            "ibang":        "90"
        ]

        if (modelClass == Transformer2WResult) {
            parameter["tappos"] = "3"
        }
        if (modelClass == Transformer3WResult) {
            parameter["tappos"] = "3"
            parameter["icmag"] = "1.0"
            parameter["icang"] = "90"
        }
        if (modelClass == SwitchResult) {
            parameter["closed"] = "true"
        }

        when:
        Optional<? extends ConnectorResult> result = resultFactory.getEntity(new SimpleEntityData(parameter, modelClass))

        then:
        result.present
        result.get().getClass() == resultingModelClass
        ((ConnectorResult) result.get()).with {
            assert timestamp == TimeTools.toZonedDateTime(parameter["timestamp"])
            assert inputModel == UUID.fromString(parameter["inputModel"])
            assert iAAng == getQuant(parameter["iaang"], StandardUnits.ELECTRIC_CURRENT_ANGLE)
            assert iAMag == getQuant(parameter["iamag"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
            assert iBAng == getQuant(parameter["ibang"], StandardUnits.ELECTRIC_CURRENT_ANGLE)
            assert iBMag == getQuant(parameter["ibmag"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
        }

        if (result.get().getClass() == Transformer2WResult) {
            assert ((Transformer2WResult) result.get()).tapPos == Integer.parseInt(parameter["tappos"])
        }

        if (result.get().getClass() == Transformer3WResult) {
            Transformer3WResult transformer3WResult = ((Transformer3WResult) result.get())
            assert transformer3WResult.tapPos == Integer.parseInt(parameter["tappos"])
            assert transformer3WResult.iCAng == getQuant(parameter["icang"], StandardUnits.ELECTRIC_CURRENT_ANGLE)
            assert transformer3WResult.iCMag == getQuant(parameter["icmag"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
        }

        if (result.get().getClass() == SwitchResult) {
            assert ((SwitchResult) result.get()).closed == Boolean.parseBoolean(parameter["closed"])
        }


        where:
        modelClass          || resultingModelClass
        LineResult          || LineResult
        SwitchResult        || SwitchResult
        Transformer2WResult || Transformer2WResult
        Transformer3WResult || Transformer3WResult

    }

}
