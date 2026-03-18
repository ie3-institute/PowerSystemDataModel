/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity

import java.time.ZonedDateTime
import javax.measure.quantity.ElectricPotential

class NodeInputFactoryTest extends Specification implements FactoryTestHelper {
  def "A NodeInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new NodeInputFactory()
    def expectedClasses = [NodeInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
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
    Try<NodeInput, FactoryException> input = inputFactory.get(new AssetInputEntityData(parameter, inputClass, operatorInput))

    then:
    input.success
    input.data.get().getClass() == inputClass
    ((NodeInput) input.data.get()).with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime.startDate.present
      operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      !operationTime.endDate.present
      operator == operatorInput
      id == parameter["id"]
      vTarget == getQuant(parameter["vtarget"], StandardUnits.TARGET_VOLTAGE_MAGNITUDE)
      slack
      geoPosition == getGeometry(parameter["geoposition"])
      voltLvl == GermanVoltageLevelUtils.parse(parameter["voltlvl"], getQuant(parameter["vrated"], StandardUnits.RATED_VOLTAGE_MAGNITUDE) as ComparableQuantity<ElectricPotential>)
      subnet == Integer.parseInt(parameter["subnet"])
    }
  }

  def "A NodeInputFactory should thrown an exception on invalid NodeInput correctly"() {
    given:
    def inputFactory = new NodeInputFactory()
    Map<String, String> parameter = [
            "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
            "operatesfrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
            "operatesuntil": "",
            "id"           : "TestID",
            "vtarget"      : "",
            "vrated"       : "3",
            "slack"        : "true",
            "geoposition"  : "{ \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] }",
            "voltlvl"      : "lv",
            "subnet"       : "7"
    ]
    def inputClass = NodeInput
    def operatorInput = Mock(OperatorInput)

    when:
    inputFactory.buildModel(new AssetInputEntityData(parameter, inputClass, operatorInput))

    then:
    Exception ex = thrown()
    ex.class == FactoryException
    ex.message == "Exception while trying to parse field \"vTarget\" with supposed double value \"\""
  }
}
