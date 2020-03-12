package edu.ie3.test.common

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.datamodel.models.input.container.GraphicElements
import edu.ie3.datamodel.models.input.container.JointGridContainer
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.models.input.container.SubGridContainer
import edu.ie3.datamodel.models.input.container.SystemParticipants

class ComplexTopology extends GridTestData {
    public static gridName = "complex_topology"

    private static final RawGridElements rawGrid = new RawGridElements(
            [nodeA, nodeB, nodeC, nodeD, nodeE, nodeF, nodeG] as Set,
            [] as Set,
            [transformerBtoD,
             transformerBtoE,
             transformerCtoE,
             transformerCtoF,
             transformerCtoG] as Set,
            [transformerAtoBtoC] as Set,
            [] as Set,
            [] as Set)

    public static grid = new JointGridContainer(
            gridName,
            rawGrid,
            new SystemParticipants(
                    [] as Set,
                    [] as Set,
                    [] as Set,
                    [] as Set,
                    [] as Set,
                    [] as Set,
                    [] as Set,
                    [] as Set,
                    [] as Set),
            new GraphicElements(
                    [] as Set,
                    [] as Set))

    public static HashMap<Integer, SubGridContainer> expectedSubGrids = new HashMap<>()

    static {
        expectedSubGrids.put(1, new SubGridContainer(
                gridName,
                1,
                new RawGridElements(
                        [nodeA, nodeB, nodeC] as Set,
                        [] as Set,
                        [] as Set,
                        [transformerAtoBtoC] as Set,
                        [] as Set,
                        [] as Set),
                new SystemParticipants(
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set),
                new GraphicElements(
                        [] as Set,
                        [] as Set)
            )
        )
        expectedSubGrids.put(2, new SubGridContainer(
                gridName,
                2,
                new RawGridElements(
                        [nodeA, nodeB, nodeC] as Set,
                        [] as Set,
                        [] as Set,
                        [transformerAtoBtoC] as Set,
                        [] as Set,
                        [] as Set),
                new SystemParticipants(
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set),
                new GraphicElements(
                        [] as Set,
                        [] as Set)
        )
        )
        expectedSubGrids.put(3, new SubGridContainer(
                gridName,
                3,
                new RawGridElements(
                        [nodeA, nodeB, nodeC] as Set,
                        [] as Set,
                        [] as Set,
                        [transformerAtoBtoC] as Set,
                        [] as Set,
                        [] as Set),
                new SystemParticipants(
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set),
                new GraphicElements(
                        [] as Set,
                        [] as Set)
        )
        )
        expectedSubGrids.put(4, new SubGridContainer(
                gridName,
                4,
                new RawGridElements(
                        [nodeB, nodeD] as Set,
                        [] as Set,
                        [transformerBtoD] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set),
                new SystemParticipants(
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set),
                new GraphicElements(
                        [] as Set,
                        [] as Set)
        )
        )
        expectedSubGrids.put(5, new SubGridContainer(
                gridName,
                5,
                new RawGridElements(
                        [nodeB, nodeC, nodeE] as Set,
                        [] as Set,
                        [transformerBtoE, transformerCtoE] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set),
                new SystemParticipants(
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set),
                new GraphicElements(
                        [] as Set,
                        [] as Set)
        )
        )
        expectedSubGrids.put(6, new SubGridContainer(
                gridName,
                6,
                new RawGridElements(
                        [nodeC, nodeF, nodeG] as Set,
                        [] as Set,
                        [transformerCtoF, transformerCtoG] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set),
                new SystemParticipants(
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set,
                        [] as Set),
                new GraphicElements(
                        [] as Set,
                        [] as Set)
        )
        )
    }
}
