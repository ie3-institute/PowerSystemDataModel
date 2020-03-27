package edu.ie3.datamodel.io.processor.input

import edu.ie3.util.TimeTools
import sun.awt.image.ImageWatched

import java.time.ZoneId
import java.time.ZonedDateTime

import static edu.ie3.util.quantities.PowerSystemUnits.*

import edu.ie3.datamodel.models.value.EnergyPriceValue
import edu.ie3.datamodel.models.value.TimeBasedValue
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

/**
 * Tests the behaviour of the processor for time based values. The time based value is a unique entity and therefore
 * has a uuid. In common case, the uuid will not be provided explicitly during model generation. Therefore, here only
 * the presence of that field ist tested and not the specific value.
 */
class TimeBasedValueProcessorTest extends Specification {
    static {
        TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
    }

    def "The TimeBasedValueProcessor should de-serialize a provided time based EnergyPriceValue correctly"() {
        given:
        TimeBasedValueProcessor processor = new TimeBasedValueProcessor<>(EnergyPriceValue.class)
        EnergyPriceValue value = new EnergyPriceValue(Quantities.getQuantity(43.21, EURO_PER_MEGAWATTHOUR))
        ZonedDateTime time = TimeTools.toZonedDateTime("2020-03-27 15:29:14")
        TimeBasedValue<EnergyPriceValue> timeBasedValue = new TimeBasedValue<>(time, value)
        Map expected = [
                "uuid"  : "has random uuid",
                "time"  : "2020-03-27 15:29:14",
                "price" : "43.21"
        ]

        when:
        Optional<LinkedHashMap<String, String>> actual = processor.handleEntity(timeBasedValue)

        then:
        actual.isPresent()
        LinkedHashMap<String, String> result = actual.get()
        expected.forEach { k, v ->
            if(k == "uuid")
                assert result.containsKey(k)
            else
                assert (v == result.get(k))
        }
    }
}
