package edu.ie3.io.factory.result

import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.result.thermal.CylindricalStorageResult
import edu.ie3.models.result.thermal.ThermalHouseResult
import edu.ie3.models.result.thermal.ThermalUnitResult
import edu.ie3.util.TimeTools
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

class ThermalResultFactoryTest extends Specification {

    def "A ThermalResultFactory should contain all expected classes for parsing"() {
        given:
        def resultFactory = new ThermalResultFactory()
        def expectedClasses = [ThermalHouseResult, CylindricalStorageResult]

        expect:
        resultFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A ThermalResultFactory should parse a CylindricalStorageResult correctly"() {
        given: "a thermal result factory and model data"
        def resultFactory = new ThermalResultFactory()
        HashMap<String, String> parameterMap = [:]
        parameterMap.put("timestamp", "2020-01-30 17:26:44")
        parameterMap.put("inputModel", "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7")
        parameterMap.put("qDot", "2")
        parameterMap.put("energy", "3")
        parameterMap.put("fillLevel", "20")
        when:
        Optional<? extends ThermalUnitResult> result = resultFactory.getEntity(new SimpleEntityData(parameterMap, CylindricalStorageResult))

        then:
        result.present
        result.get().getClass() == CylindricalStorageResult
        CylindricalStorageResult castResult = (CylindricalStorageResult) result.get()
        castResult.timestamp == TimeTools.toZonedDateTime(parameterMap.get("timestamp"))
        castResult.inputModel == UUID.fromString(parameterMap.get("inputModel"))
        castResult.qDot == Quantities.getQuantity(Double.parseDouble(parameterMap.get("qDot")), StandardUnits.HEAT_DEMAND)
        castResult.energy == Quantities.getQuantity(Double.parseDouble(parameterMap.get("energy")), StandardUnits.ENERGY_RESULT)
        castResult.fillLevel == Quantities.getQuantity(Double.parseDouble(parameterMap.get("fillLevel")), StandardUnits.FILL_LEVEL)
    }

    def "A ThermalResultFactory should parse a ThermalHouseResult correctly"() {
        given: "a thermal result factory and model data"
        def resultFactory = new ThermalResultFactory()
        HashMap<String, String> parameterMap = [:]
        parameterMap.put("timestamp", "2020-01-30 17:26:44")
        parameterMap.put("inputModel", "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7")
        parameterMap.put("qDot", "2")
        parameterMap.put("indoorTemperature", "21")
        when:
        Optional<? extends ThermalUnitResult> result = resultFactory.getEntity(new SimpleEntityData(parameterMap, ThermalHouseResult))

        then:
        result.present
        result.get().getClass() == ThermalHouseResult
        ThermalHouseResult castResult = (ThermalHouseResult) result.get()
        castResult.timestamp == TimeTools.toZonedDateTime(parameterMap.get("timestamp"))
        castResult.inputModel == UUID.fromString(parameterMap.get("inputModel"))
        castResult.qDot == Quantities.getQuantity(Double.parseDouble(parameterMap.get("qDot")), StandardUnits.HEAT_DEMAND)
        castResult.indoorTemperature == Quantities.getQuantity(Double.parseDouble(parameterMap.get("indoorTemperature")), StandardUnits.TEMPERATURE)
    }
}
