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
        def expectedClasses = [LoadResult.class, FixedFeedInResult.class, BmResult.class, PvResult.class,
                               ChpResult.class, WecResult.class, StorageResult.class, EvcsResult.class, EvResult.class]

        expect:
        resultFactory.classes() == Arrays.asList(expectedClasses.toArray())
    }

    def "A SystemParticipantResultFactory should parse a valid result model correctly"() {
        given: "a system participant factory and model data"
        def resultFactory = new SystemParticipantResultFactory()
        HashMap<String, String> parameterMap = new HashMap<>();
        parameterMap.put("timestamp", "16/01/2010 17:27:46");
        parameterMap.put("inputModel", "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7");
        parameterMap.put("p", "2");
        parameterMap.put("q", "2");

        when:
        Optional<? extends SystemParticipantResult> result = resultFactory.getEntity(new SimpleEntityData(parameterMap, modelClass))

        then:
        result.isPresent()
        result.get().getClass() == resultingModelClass
        result.get().p == Quantities.getQuantity(Double.parseDouble(parameterMap.get("p")), StandardUnits.ACTIVE_POWER_OUT)
        result.get().q == Quantities.getQuantity(Double.parseDouble(parameterMap.get("q")), StandardUnits.REACTIVE_POWER_OUT)
        result.get().timestamp == TimeTools.toZonedDateTime(parameterMap.get("timestamp"))
        result.get().inputModel == UUID.fromString(parameterMap.get("inputModel"))

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
        HashMap<String, String> parameterMap = new HashMap<>();
        parameterMap.put("timestamp", "16/01/2010 17:27:46");
        parameterMap.put("inputModel", "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7");
        parameterMap.put("soc", "20")
        parameterMap.put("p", "2");
        parameterMap.put("q", "2");

        when:
        Optional<? extends SystemParticipantResult> result = resultFactory.getEntity(new SimpleEntityData(parameterMap, StorageResult.class))

        then:
        result.isPresent()
        result.get().getClass() == StorageResult.class
        result.get().p == Quantities.getQuantity(Double.parseDouble(parameterMap.get("p")), StandardUnits.ACTIVE_POWER_OUT)
        result.get().q == Quantities.getQuantity(Double.parseDouble(parameterMap.get("q")), StandardUnits.REACTIVE_POWER_OUT)
        ((StorageResult) result.get()).soc == Quantities.getQuantity(Double.parseDouble(parameterMap.get("soc")), Units.PERCENT)
        result.get().timestamp == TimeTools.toZonedDateTime(parameterMap.get("timestamp"))
        result.get().inputModel == UUID.fromString(parameterMap.get("inputModel"))

    }

    def "A SystemParticipantResultFactory should throw an exception on invalid or incomplete data"() {
        given: "a system participant factory and model data"
        def resultFactory = new SystemParticipantResultFactory()
        HashMap<String, String> parameterMap = new HashMap<>();
        parameterMap.put("timestamp", "16/01/2010 17:27:46");
        parameterMap.put("inputModel", "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7");
        parameterMap.put("q", "2");

        when:
        resultFactory.getEntity(new SimpleEntityData(parameterMap, WecResult.class))

        then:
        FactoryException ex = thrown()
        ex.message == "The provided fields [q, inputModel, timestamp] with data {q -> 2,inputModel -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7,timestamp -> 16/01/2010 17:27:46} are invalid for instance of WecResult. \n" +
                "The following fields to be passed to a constructor of WecResult are possible:\n" +
                "0: [p, q, inputModel, timestamp]\n" +
                "1: [p, q, inputModel, uuid, timestamp]\n"

    }

}
