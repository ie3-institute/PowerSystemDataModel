package edu.ie3.test.common

import edu.ie3.datamodel.models.input.container.GraphicElements
import edu.ie3.datamodel.models.input.container.JointGridContainer
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.models.input.container.SystemParticipants

class ComplexTopology extends GridTestData {

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
            "complex_topology",
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
}
