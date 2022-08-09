package edu.ie3.datamodel.models

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData
import edu.ie3.datamodel.io.factory.input.participant.FixedFeedInInputFactory
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import spock.lang.Specification

import java.util.stream.Collectors

class EmControlStrategyTest extends Specification {

    def "An em control strategy should be gotten by their key"() {
        given:
        EmControlStrategy actual = EmControlStrategy.get(key)

        expect:
        actual == expected

        where:
        key                                          || expected
        EmControlStrategy.SELF_OPTIMIZATION.getKey() || EmControlStrategy.SELF_OPTIMIZATION

    }

    def "An em control strategy should throw an exception when calling get on unkown key"() {
        given:
        String unknownKey = "nobody_knows_me"

        when:
        EmControlStrategy _ = EmControlStrategy.get(unknownKey)

        then:
        IllegalArgumentException ex = thrown()
        ex.message == "No predefined energy management control strategy '" +
                unknownKey +
                "' found. Please provide one of the following keys: " +
                Arrays.stream(EmControlStrategy.values()).map(EmControlStrategy::getKey).collect(Collectors.joining(", "))
    }

}
