package edu.ie3.io.factory

import edu.ie3.models.input.NodeInput
import edu.ie3.models.result.system.PvResult
import spock.lang.Specification


class EntityFactoryUtilsTest extends Specification {
    def "EntityFactorUtils should list exactly all known factories when requested to do so"() {
        given: "all known factory enums as we as all classes we have factories for"
        def knownClasses = [NodeInput.class, PvResult.class]
        def knownFactoryEnums = [SimpleEntityFactory.class, OperatorEntityFactory.class]

        expect:
        knownClasses.forEach({ x ->
            assert (EntityFactoryUtils.getFactory(x, knownFactoryEnums.toArray(new Class<? extends EntityFactory>[knownFactoryEnums.size()])).isPresent())
        })

    }
}
