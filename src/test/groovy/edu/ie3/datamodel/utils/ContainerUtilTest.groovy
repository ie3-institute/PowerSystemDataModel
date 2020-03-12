package edu.ie3.datamodel.utils

import edu.ie3.datamodel.models.input.container.GridContainer
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.test.common.ComplexTopology
import spock.lang.Shared
import spock.lang.Specification

class ContainerUtilTest extends Specification {
    @Shared
    GridContainer complexTopology = ComplexTopology.grid

    def "The container utils filter raw grid elements correctly for a given subnet" () {
        when:
        RawGridElements actual = ContainerUtils.filterForSubnet(complexTopology.getRawGrid(), subnet)

        then:
        actual.getNodes() == expectedNodes
        actual.getTransformer2Ws() == expectedTransformers2W
        actual.getTransformer3Ws() == expectedTransformers3W
        /* TODO: Add lines, switches etc. to testing data */

        where:
        subnet  || expectedNodes                                                                || expectedTransformers2W                                                       || expectedTransformers3W
        1       || [ComplexTopology.nodeA, ComplexTopology.nodeB, ComplexTopology.nodeC] as Set || [] as Set                                                                    || [ComplexTopology.transformerAtoBtoC] as Set
        2       || [ComplexTopology.nodeA, ComplexTopology.nodeB, ComplexTopology.nodeC] as Set || [] as Set                                                                    || [ComplexTopology.transformerAtoBtoC] as Set
        3       || [ComplexTopology.nodeA, ComplexTopology.nodeB, ComplexTopology.nodeC] as Set || [] as Set                                                                    || [ComplexTopology.transformerAtoBtoC] as Set
        4       || [ComplexTopology.nodeB, ComplexTopology.nodeD] as Set                        || [ComplexTopology.transformerBtoD] as Set                                     || [] as Set
        5       || [ComplexTopology.nodeB, ComplexTopology.nodeC, ComplexTopology.nodeE] as Set || [ComplexTopology.transformerBtoE, ComplexTopology.transformerCtoE] as Set    || [] as Set
        6       || [ComplexTopology.nodeC, ComplexTopology.nodeF, ComplexTopology.nodeG] as Set || [ComplexTopology.transformerCtoF, ComplexTopology.transformerCtoG] as Set    || [] as Set
    }

    /* TODO: Extend testing data so that,
     *   - filtering of system participants can be tested
     *   - filtering of graphic elements can be tested */
}
