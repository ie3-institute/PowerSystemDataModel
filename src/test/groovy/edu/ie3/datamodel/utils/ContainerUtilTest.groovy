package edu.ie3.datamodel.utils

import edu.ie3.datamodel.exceptions.InvalidGridException
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.container.GraphicElements
import edu.ie3.datamodel.models.input.container.GridContainer
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.models.input.container.SubGridContainer
import edu.ie3.datamodel.models.input.container.SystemParticipants
import org.jgrapht.Graph
import tec.uom.se.quantity.Quantities

import javax.persistence.Subgraph

import static edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils.*
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel
import edu.ie3.test.common.ComplexTopology
import spock.lang.Shared
import spock.lang.Specification

import static edu.ie3.util.quantities.PowerSystemUnits.PU

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

    def "The container utils are able to derive the predominant voltage level" () {
        given:
        RawGridElements rawGrid = ContainerUtils.filterForSubnet(complexTopology.getRawGrid(), subnet)

        when:
        VoltageLevel actual = ContainerUtils.determinePredominantVoltLvl(rawGrid, subnet)

        then:
        actual == expected

        where:
        subnet  || expected
        1       || EHV_380KV
        2       || HV
        3       || MV_20KV
        4       || MV_20KV
        5       || MV_10KV
        6       || LV
    }

    def "The container utils throw an exception, when there is an ambiguous voltage level in the grid" () {
        given:
        RawGridElements rawGrid = ContainerUtils.filterForSubnet(complexTopology.getRawGrid(), 4)

        NodeInput corruptNode = new NodeInput(
                UUID.randomUUID(),
                OperationTime.notLimited(),
                OperatorInput.NO_OPERATOR_ASSIGNED,
                "node_e",
                Quantities.getQuantity(1d, PU),
                false,
                null,
                MV_10KV,
                4)

        Set<NodeInput> corruptNodes = [corruptNode] as Set
        corruptNodes.addAll(rawGrid.nodes)

        RawGridElements dut = new RawGridElements(corruptNodes, rawGrid.lines, rawGrid.transformer2Ws,
                rawGrid.transformer3Ws, rawGrid.switches, rawGrid.measurementUnits)

        when:
        ContainerUtils.determinePredominantVoltLvl(dut, 4)

        then:
        InvalidGridException ex = thrown()
        ex.message == "There are 2 voltage levels apparent, although only one is expected."
    }

    def "The container util determines the set of subnet number correctly" () {
        expect:
        ContainerUtils.determineSubnetNumbers(ComplexTopology.grid.getRawGrid().getNodes()) == [1, 2, 3, 4, 5, 6] as Set
    }

    def "The container util builds the sub grid containers correctly" () {
        given:
        String gridName = ComplexTopology.grid.getGridName()
        Set<Integer> subNetNumbers = ContainerUtils.determineSubnetNumbers(ComplexTopology.grid.getRawGrid().getNodes())
        RawGridElements rawGrid = ComplexTopology.grid.rawGrid
        SystemParticipants systemParticipants = ComplexTopology.grid.systemParticipants
        GraphicElements graphics = ComplexTopology.grid.graphics
        HashMap<Integer, SubGridContainer> expectedSubGrids = ComplexTopology.expectedSubGrids

        when:
        HashMap<Integer, SubGridContainer> actual = ContainerUtils.buildSubGridContainers(
                gridName,
                subNetNumbers,
                rawGrid,
                systemParticipants,
                graphics)

        then:
        actual.size() == 6
        for(Map.Entry<Integer, SubGridContainer> entry: actual){
            int subnetNo = entry.getKey()
            SubGridContainer actualSubGrid = entry.getValue()
            SubGridContainer expectedSubGrid = expectedSubGrids.get(subnetNo)

            assert actualSubGrid == expectedSubGrid
        }
    }

    /* TODO: Extend testing data so that,
     *   - filtering of system participants can be tested
     *   - filtering of graphic elements can be tested */
}
