package edu.ie3.io.factory.input

import edu.ie3.models.StandardUnits
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification
import tec.uom.se.ComparableQuantity

import javax.measure.quantity.ElectricPotential
import java.time.ZonedDateTime

class NodeInputFactoryTest extends Specification implements FactoryTestHelper {
  def "A NodeInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new NodeInputFactory()
    def expectedClasses = [NodeInput]

    expect:
    inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
  }

  def "A NodeInputFactory should parse a valid NodeInput correctly"() {
    given: "a system participant input type factory and model data"
    def inputFactory = new NodeInputFactory()
    Map<String, String> parameter = [
            "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
            "operatesfrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
            "operatesuntil": "",
            "id"           : "TestID",
            "vtarget"      : "2",
            "vrated"       : "3",
            "slack"        : "true",
            "geoposition"  : "{ \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] }",
            "voltlvl"      : "lv",
            "subnet"       : "7"
        ]
    def inputClass = NodeInput
    def operatorInput = Mock(OperatorInput)

    when:
    Optional<NodeInput> input = inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass, operatorInput))

    then:
    input.present
    input.get().getClass() == inputClass
    ((NodeInput) input.get()).with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert !operationTime.endDate.present
      assert operator == operatorInput
      assert id == parameter["id"]
      assert vTarget == getQuant(parameter["vtarget"], StandardUnits.TARGET_VOLTAGE_MAGNITUDE)
      assert slack
      assert geoPosition == getGeometry(parameter["geoposition"])
      assert voltLvl == GermanVoltageLevelUtils.parse(parameter["voltlvl"], getQuant(parameter["vrated"], StandardUnits.RATED_VOLTAGE_MAGNITUDE) as ComparableQuantity<ElectricPotential>)
      assert subnet == Integer.parseInt(parameter["subnet"])
    }
  }
}
