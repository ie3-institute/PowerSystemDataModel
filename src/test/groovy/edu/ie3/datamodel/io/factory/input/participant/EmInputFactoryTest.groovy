/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input.participant

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.EmInput
import edu.ie3.datamodel.utils.Try
import spock.lang.Specification

import java.time.ZonedDateTime

class EmInputFactoryTest extends Specification {

  def "A EmInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new EmInputFactory()
    def expectedClasses = [EmInput] as List

    expect:
    inputFactory.supportedClasses == expectedClasses
  }

  def "A EmInputFactory should parse a valid EmInput correctly"() {
    given:
    def inputFactory = new EmInputFactory()
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "controlstrategy" : "no_control"
    ]
    def inputClass = EmInput
    def operatorInput = Mock(OperatorInput)

    when:
    Try<EmInput, FactoryException> input = inputFactory.get(
        new AssetInputEntityData(parameter, inputClass, operatorInput))

    then:
    input.success
    input.data.get().getClass() == inputClass
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert operationTime.endDate.present
      assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
      assert operator == operatorInput
      assert id == parameter["id"]
      assert controlStrategy == parameter["controlstrategy"]
    }
  }

  def "A EmInputFactory should fail when passing an invalid UUID"() {
    given:
    def inputFactory = new EmInputFactory()
    Map<String, String> parameter = [
      "uuid"            : "- broken -",
      "id"              : "TestID",
      "controlstrategy" : "no_control"
    ]
    def inputClass = EmInput
    def operatorInput = Mock(OperatorInput)

    when:
    Try<EmInput, FactoryException> input = inputFactory.get(
        new AssetInputEntityData(parameter, inputClass, operatorInput))

    then:
    input.failure
    input.exception.get().cause.message == "Exception while trying to parse UUID of field \"uuid\" with value \"- broken -\""
  }
}
