package edu.ie3.io.factory.result

import edu.ie3.exceptions.FactoryException
import edu.ie3.io.factory.SimpleEntityData
import edu.ie3.models.StandardUnits
import edu.ie3.models.result.system.*
import edu.ie3.util.TimeTools
import spock.lang.Specification
import tec.uom.se.quantity.Quantities
import tec.uom.se.unit.Units

class SystemParticipantResultFactoryTest extends Specification {

    def "A SystemParticipantResultFactory should contain all expected classes for parsing"() {
        given:
        def resultFactory = new SystemParticipantResultFactory()
        def expectedClasses = [LoadResult, FixedFeedInResult, BmResult, PvResult,
                               ChpResult, WecResult, StorageResult, EvcsResult, EvResult]

        expect:
        resultFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A SystemParticipantResultFactory should parse a valid result model correctly"() {
        given: "a system participant factory and model data"
        def resultFactory = new SystemParticipantResultFactory()
        Map<String, String> parameter = [:]
        parameter["timestamp"] = "16/01/2010 17:27:46"
        parameter["inputModel"] = "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"
        parameter["p"] = "2"
        parameter["q"] = "2"
        if (modelClass == EvResult)
          {  parameter["soc"] = "10"}

        when:
        Optional<? extends SystemParticipantResult> result = resultFactory.getEntity(new SimpleEntityData(parameter, modelClass))

        then:
        result.present
        result.get().getClass() == resultingModelClass
        result.get().p == Quantities.getQuantity(Double.parseDouble(parameter["p"]), StandardUnits.ACTIVE_POWER_OUT)
        result.get().q == Quantities.getQuantity(Double.parseDouble(parameter["q"]), StandardUnits.REACTIVE_POWER_OUT)
        result.get().timestamp == TimeTools.toZonedDateTime(parameter["timestamp"])
        result.get().inputModel == UUID.fromString(parameter["inputModel"])

        if (modelClass == EvResult) {
            assert (((EvResult) result.get()).soc == Quantities.getQuantity(Double.parseDouble(parameter["soc"]), Units.PERCENT))
        }

        where:
        modelClass        || resultingModelClass
        LoadResult        || LoadResult
        FixedFeedInResult || FixedFeedInResult
        BmResult          || BmResult
        EvResult          || EvResult
        PvResult          || PvResult
        EvcsResult        || EvcsResult
        ChpResult         || ChpResult
        WecResult         || WecResult

    }

    def "A SystemParticipantResultFactory should parse a StorageResult correctly"() {
        given: "a system participant factory and model data"
        def resultFactory = new SystemParticipantResultFactory()
        Map<String, String> parameter = [:]
        parameter["timestamp"] = "16/01/2010 17:27:46"
        parameter["inputModel"] = "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"
        parameter["soc"] = "20"
        parameter["p"] = "2"
        parameter["q"] = "2"

        when:
        Optional<? extends SystemParticipantResult> result = resultFactory.getEntity(new SimpleEntityData(parameter, StorageResult))

        then:
        result.present
        result.get().getClass() == StorageResult
        result.get().p == Quantities.getQuantity(Double.parseDouble(parameter["p"]), StandardUnits.ACTIVE_POWER_OUT)
        result.get().q == Quantities.getQuantity(Double.parseDouble(parameter["q"]), StandardUnits.REACTIVE_POWER_OUT)
        ((StorageResult) result.get()).soc == Quantities.getQuantity(Double.parseDouble(parameter["soc"]), Units.PERCENT)
        result.get().timestamp == TimeTools.toZonedDateTime(parameter["timestamp"])
        result.get().inputModel == UUID.fromString(parameter["inputModel"])

    }

    def "A SystemParticipantResultFactory should throw an exception on invalid or incomplete data"() {
        given: "a system participant factory and model data"
        def resultFactory = new SystemParticipantResultFactory()
        Map<String, String> parameter = [:]
        parameter["timestamp"] = "16/01/2010 17:27:46"
        parameter["inputModel"] = "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"
        parameter["q"] = "2"

        when:
        resultFactory.getEntity(new SimpleEntityData(parameter, WecResult))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [inputModel, q, timestamp] with data {inputModel -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7,q -> 2,timestamp -> 16/01/2010 17:27:46} are invalid for instance of WecResult. \n" +
                "The following fields to be passed to a constructor of WecResult are possible:\n" +
                "0: [inputModel, p, q, timestamp]\n" +
                "1: [inputModel, p, q, timestamp, uuid]\n"

    }

}
