/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class Transformer2WInputFactoryTest extends Specification implements FactoryTestHelper {
  def "A Transformer2WInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new Transformer2WInputFactory()
    def expectedClasses = [Transformer2WInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A Transformer2WInputFactory should parse a valid Transformer2WInput correctly"() {
    given: "a system participant input type factory and model data"
    def inputFactory = new Transformer2WInputFactory()
    Map<String, String> parameter = [
      "uuid"           : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"   : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"  : "",
      "id"             : "TestID",
      "paralleldevices": "2",
      "tappos"         : "3",
      "autotap"        : "true"
    ]
    def inputClass = Transformer2WInput
    def operatorInput = Mock(OperatorInput)
    def nodeInputA = Mock(NodeInput)
    def nodeInputB = Mock(NodeInput)
    def typeInput = Mock(Transformer2WTypeInput)

    when:
    Try<Transformer2WInput, FactoryException> input = inputFactory.get(new TypedConnectorInputEntityData<Transformer2WTypeInput>(parameter, inputClass, operatorInput, nodeInputA, nodeInputB, typeInput))

    then:
    input.success
    input.data.get().getClass() == inputClass
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert !operationTime.endDate.present
      assert operator == operatorInput
      assert id == parameter["id"]
      assert nodeA == nodeInputA
      assert nodeB == nodeInputB
      assert type == typeInput
      assert parallelDevices == Integer.parseInt(parameter["paralleldevices"])
      assert tapPos == Integer.parseInt(parameter["tappos"])
      assert autoTap
    }
  }
}