package edu.ie3.datamodel.models.input.container

import edu.ie3.datamodel.models.input.MeasurementUnitInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.Transformer3WInput

import static  edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.exceptions.InvalidGridException
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

class SubGridContainerTest extends Specification {
    @Shared
    RawGridElements rawGridElements

    @Shared
    RawGridElements emptyRawGridElements = new RawGridElements(
            new HashSet<NodeInput>(),
            new HashSet<LineInput>(),
            new HashSet<Transformer2WInput>(),
            new HashSet<Transformer3WInput>(),
            new HashSet<SwitchInput>(),
            new HashSet<MeasurementUnitInput>())

    def setupSpec() {
        Set<NodeInput> nodes = new HashSet<>()
        nodes.add(new NodeInput(
                UUID.randomUUID(),
                OperationTime.notLimited(),
                OperatorInput.NO_OPERATOR_ASSIGNED,
                "test_node_1",
                Quantities.getQuantity(1d, PU),
                false,
                null,
                GermanVoltageLevelUtils.LV,
                1))
        nodes.add(new NodeInput(
                UUID.randomUUID(),
                OperationTime.notLimited(),
                OperatorInput.NO_OPERATOR_ASSIGNED,
                "test_node_2",
                Quantities.getQuantity(1d, PU),
                false,
                null,
                GermanVoltageLevelUtils.LV,
                1))
        nodes.add(new NodeInput(
                UUID.randomUUID(),
                OperationTime.notLimited(),
                OperatorInput.NO_OPERATOR_ASSIGNED,
                "test_node_3",
                Quantities.getQuantity(1d, PU),
                false,
                null,
                GermanVoltageLevelUtils.MV_10KV,
                0))

        rawGridElements = new RawGridElements(
                nodes,
                new HashSet<LineInput>(),
                new HashSet<Transformer2WInput>(),
                new HashSet<Transformer3WInput>(),
                new HashSet<SwitchInput>(),
                new HashSet<MeasurementUnitInput>())
    }

    def "The SingleGridContainer should determine the predominant voltage level correctly"() {
        when:
        VoltageLevel actual = SubGridContainer.determinePredominantVoltLvl(rawGridElements)

        then:
        actual == GermanVoltageLevelUtils.LV
    }

    def "The SingleGridContainer should throw an exception, when the there is no voltage level information at all"() {
        when:
        SubGridContainer.determinePredominantVoltLvl(emptyRawGridElements)

        then:
        InvalidGridException ex = thrown()
        ex.message == "Cannot determine the predominant voltage level."
    }
}
