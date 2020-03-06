package edu.ie3.models.aggregated

import static  edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.exceptions.AggregationException
import edu.ie3.models.input.aggregated.AggregatedGridInput
import edu.ie3.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.models.voltagelevels.VoltageLevel

import edu.ie3.models.OperationTime
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.aggregated.RawGridElements
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

class AggregatedGridInputTest extends Specification {
    @Shared
    RawGridElements rawGridElements = new RawGridElements()

    @Shared
    RawGridElements emptyRawGridElements = new RawGridElements()

    def setupSpec() {
        rawGridElements.add(new NodeInput(
                UUID.randomUUID(),
                OperationTime.notLimited(),
                OperatorInput.NO_OPERATOR_ASSIGNED,
                "test_node_1",
                Quantities.getQuantity(1d, PU),
                false,
                null,
                GermanVoltageLevelUtils.LV,
                1))
        rawGridElements.add(new NodeInput(
                UUID.randomUUID(),
                OperationTime.notLimited(),
                OperatorInput.NO_OPERATOR_ASSIGNED,
                "test_node_2",
                Quantities.getQuantity(1d, PU),
                false,
                null,
                GermanVoltageLevelUtils.LV,
                1))
        rawGridElements.add(new NodeInput(
                UUID.randomUUID(),
                OperationTime.notLimited(),
                OperatorInput.NO_OPERATOR_ASSIGNED,
                "test_node_3",
                Quantities.getQuantity(1d, PU),
                false,
                null,
                GermanVoltageLevelUtils.MV_10KV,
                0))
    }

    def "The AggregatedGridInput should determine the predominant voltage level correctly"() {
        when:
        VoltageLevel actual = AggregatedGridInput.determinePredominantVoltLvl(rawGridElements)

        then:
        actual == GermanVoltageLevelUtils.LV
    }

    def "The AggregatedGridInput should throw an exception, when the there is no voltage level information at all"() {
        when:
        AggregatedGridInput.determinePredominantVoltLvl(emptyRawGridElements)

        then:
        AggregationException ex = thrown()
        ex.message == "Cannot determine the predominant voltage level."
    }
}
