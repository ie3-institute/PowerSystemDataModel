package edu.ie3.datamodel.io.factory.deserializing

import edu.ie3.datamodel.io.factory.input.AssetInputEntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification
import tec.uom.se.ComparableQuantity

import javax.measure.quantity.ElectricPotential
import java.time.ZonedDateTime

class DeserializerTest extends Specification implements FactoryTestHelper {

    def "A Deserializer can deserialize a NodeInput from a FieldMap" () {
        given:
        Map<String, String> parameter = [
                "uuid"          : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
                "operates_from" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
                "operates_until": "",
                "id"            : "TestID",
                "v_target"      : "1",
                "v_rated"       : "",
                "slack"         : "true",
                "geo_position"  : "{ \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] }",
                "volt_lvl"      : "",
                "subnet"        : "7"
        ]
        def operatorInput = Mock(OperatorInput)
        def inputClass = NodeInput

        when:
        Optional<NodeInput> input = Deserializer.deserialize(inputClass, parameter, ["operator":operatorInput])

        then:
        input.present
        input.get().getClass() == inputClass
        ((NodeInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime.startDate.present
            assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operates_from"])
            assert !operationTime.endDate.present
            assert operator == operatorInput
            assert id == parameter["id"]
            assert vTarget == getQuant(parameter["v_target"], StandardUnits.TARGET_VOLTAGE_MAGNITUDE)
            assert slack
            assert geoPosition == getGeometry(parameter["geo_position"])
//            assert voltLvl == GermanVoltageLevelUtils.parse(parameter["volt_lvl"], getQuant(parameter["v_rated"], StandardUnits.RATED_VOLTAGE_MAGNITUDE) as ComparableQuantity<ElectricPotential>)
            assert subnet == Integer.parseInt(parameter["subnet"])

        }
    }
}
