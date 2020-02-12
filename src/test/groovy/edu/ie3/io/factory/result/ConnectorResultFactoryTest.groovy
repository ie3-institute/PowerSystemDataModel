package edu.ie3.io.factory.result

import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.result.connector.*
import edu.ie3.util.TimeTools
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

class ConnectorResultFactoryTest extends Specification {

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
        Map<String, String> parameter = [:]
        parameter["timestamp"] = "2020-01-30 17:26:44"
        parameter["inputModel"] = "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"
        parameter["iamag"] = "1.0"
        parameter["iaang"] = "90"
        parameter["ibmag"] = "0.98123"
        parameter["ibang"] = "90"

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
        result.get().timestamp == TimeTools.toZonedDateTime(parameter["timestamp"])
        result.get().inputModel == UUID.fromString(parameter["inputModel"])
        result.get().iAAng == Quantities.getQuantity(Double.parseDouble(parameter["iaang"]), StandardUnits.ELECTRIC_CURRENT_ANGLE)
        result.get().iAMag == Quantities.getQuantity(Double.parseDouble(parameter["iamag"]), StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
        result.get().iBAng == Quantities.getQuantity(Double.parseDouble(parameter["ibang"]), StandardUnits.ELECTRIC_CURRENT_ANGLE)
        result.get().iBMag == Quantities.getQuantity(Double.parseDouble(parameter["ibmag"]), StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)

        if (result.get().getClass() == Transformer2WResult) {
            assert(((Transformer2WResult) result.get()).tapPos == Integer.parseInt(parameter["tappos"]))
        }

        if (result.get().getClass() == Transformer3WResult) {
            Transformer3WResult transformer3WResult = ((Transformer3WResult) result.get())
            assert( transformer3WResult.tapPos == Integer.parseInt(parameter["tappos"]))
            assert( transformer3WResult.iCAng == Quantities.getQuantity(Double.parseDouble(parameter["icang"]), StandardUnits.ELECTRIC_CURRENT_ANGLE))
            assert( transformer3WResult.iCMag == Quantities.getQuantity(Double.parseDouble(parameter["icmag"]), StandardUnits.ELECTRIC_CURRENT_MAGNITUDE))
        }

        if (result.get().getClass() == SwitchResult) {
            assert(((SwitchResult) result.get()).closed == Boolean.parseBoolean(parameter["closed"]))
        }


        where:
        modelClass          || resultingModelClass
        LineResult          || LineResult
        SwitchResult        || SwitchResult
        Transformer2WResult || Transformer2WResult
        Transformer3WResult || Transformer3WResult

    }

}
