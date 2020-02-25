package edu.ie3.io.factory.input

import edu.ie3.exceptions.FactoryException
import edu.ie3.models.GermanVoltageLevel
import edu.ie3.models.OperationTime
import edu.ie3.models.StandardUnits
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class NodeInputFactoryTest extends Specification implements FactoryTestHelper {
  def "A NodeInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new NodeInputFactory()
    def expectedClasses = [NodeInput]

    expect:
    inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
  }

  def "A NodeInputFactory should parse a valid operated NodeInput correctly"() {
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
      assert vRated == getQuant(parameter["vrated"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      assert slack
      assert geoPosition == getGeometry(parameter["geoposition"])
      assert voltLvl == GermanVoltageLevel.parseVoltageLvl(parameter["voltlvl"])
      assert subnet == Integer.parseInt(parameter["subnet"])
    }
  }

  def "A NodeInputFactory should parse a valid non-operated NodeInput correctly"() {
    given: "a system participant input type factory and model data"
    def inputFactory = new NodeInputFactory()
    Map<String, String> parameter = [
            "uuid"       : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
            "id"         : "TestID",
            "vtarget"    : "2",
            "vrated"     : "3",
            "slack"      : "true",
            "geoposition": "{ \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] }",
            "voltlvl"    : "lv",
            "subnet"     : "7"
    ]
    def inputClass = NodeInput

    when:
    Optional<NodeInput> input = inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass))

    then:
    input.present
    input.get().getClass() == inputClass
    ((NodeInput) input.get()).with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime == OperationTime.notLimited()
      assert operator == null
      assert id == parameter["id"]
      assert vTarget == getQuant(parameter["vtarget"], StandardUnits.TARGET_VOLTAGE_MAGNITUDE)
      assert vRated == getQuant(parameter["vrated"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      assert slack
      assert geoPosition == getGeometry(parameter["geoposition"])
      assert voltLvl == GermanVoltageLevel.parseVoltageLvl(parameter["voltlvl"])
      assert subnet == Integer.parseInt(parameter["subnet"])
    }
  }

  def "A NodeInputFactory should throw an exception on invalid or incomplete data (parameter missing)"() {
    given: "a system participant input type factory and model data"
    def inputFactory = new NodeInputFactory()
    Map<String, String> parameter = [
            "uuid"       : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
            "id"         : "TestID",
            "vtarget"    : "2",
            "vrated"     : "3",
            "slack"      : "true",
            "geoposition": "{ \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] }",
            "subnet"     : "7"
    ]
    def inputClass = NodeInput

    when:
    inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass))

    then:
    FactoryException ex = thrown()
    ex.message == "The provided fields [geoposition, id, slack, subnet, uuid, vrated, vtarget] with data {geoposition -> { \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] },id -> TestID,slack -> true,subnet -> 7,uuid -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7,vrated -> 3,vtarget -> 2} are invalid for instance of NodeInput. \n" +
            "The following fields to be passed to a constructor of NodeInput are possible:\n" +
            "0: [geoposition, id, slack, subnet, uuid, voltlvl, vrated, vtarget]\n" +
            "1: [geoposition, id, operatesfrom, operatesuntil, slack, subnet, uuid, voltlvl, vrated, vtarget]\n"
  }

  def "A NodeInputFactory should throw an exception on invalid or incomplete data (operator missing)"() {
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

    when:
    inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass))

    then:
    FactoryException ex = thrown()
    ex.message == "Operation time (fields 'operatesfrom' and 'operatesuntil') are passed, but operator input is not."
  }

  def "A NodeInputFactory should throw an exception on invalid or incomplete data (operation time missing)"() {
    given: "a system participant input type factory and model data"
    def inputFactory = new NodeInputFactory()
    Map<String, String> parameter = [
            "uuid"       : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
            "id"         : "TestID",
            "vtarget"    : "2",
            "vrated"     : "3",
            "slack"      : "true",
            "geoposition": "{ \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] }",
            "voltlvl"    : "lv",
            "subnet"     : "7"
        ]
    def inputClass = NodeInput
    def operatorInput = Mock(OperatorInput)

    when:
    inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass, operatorInput))

    then:
    FactoryException ex = thrown()
    ex.message == "Operator input is passed, but operation time (fields 'operatesfrom' and 'operatesuntil') is not."
  }
}
