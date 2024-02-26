/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.Transformer3WInput
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class Transformer3WInputFactoryTest  extends Specification implements FactoryTestHelper {
  def "A Transformer3WInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new Transformer3WInputFactory()
    def expectedClasses = [Transformer3WInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A Transformer3WInputFactory should parse a valid Transformer3WInput correctly"() {
    given: "a system participant input type factory and model data"
    def inputFactory = new Transformer3WInputFactory()
    Map<String, String> parameter = [
      "uuid"           : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"             : "TestID",
      "paralleldevices": "2",
      "tappos"         : "3",
      "autotap"        : "true"
    ]
    def inputClass = Transformer3WInput
    def nodeInputA = GridTestData.nodeA
    def nodeInputB = GridTestData.nodeB
    def nodeInputC = GridTestData.nodeC
    def typeInput = Mock(Transformer3WTypeInput)

    when:
    Try<Transformer3WInput, FactoryException> input = inputFactory.get(new Transformer3WInputEntityData(parameter, inputClass, nodeInputA, nodeInputB, nodeInputC, typeInput))

    then:
    input.success
    input.data.get().getClass() == inputClass
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime == OperationTime.notLimited()
      assert operator == OperatorInput.NO_OPERATOR_ASSIGNED
      assert id == parameter["id"]
      assert nodeA == nodeInputA
      assert nodeB == nodeInputB
      assert nodeC == nodeInputC
      assert type == typeInput
      assert parallelDevices == Integer.parseInt(parameter["paralleldevices"])
      assert tapPos == Integer.parseInt(parameter["tappos"])
      assert autoTap
    }
  }
  def "A Transformer3WInputFactory should throw an IllegalArgumentException if nodeB is greater than nodeA or nodeC is greater than nodeB"() {
    given: "a system participant input type factory and model data"
    def inputFactory = new Transformer3WInputFactory()
    Map<String, String> parameter = [
      "uuid"           : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"             : "TestID",
      "paralleldevices": "2",
      "tappos"         : "3",
      "autotap"        : "true"
    ]
    def inputClass = Transformer3WInput
    def nodeInputA = GridTestData.nodeC
    def nodeInputB = GridTestData.nodeB
    def nodeInputC = GridTestData.nodeA
    def typeInput = Mock(Transformer3WTypeInput)

    when:
    Try<Transformer2WInput, FactoryException> input = inputFactory.get(new Transformer3WInputEntityData(parameter, inputClass, nodeInputA, nodeInputB, nodeInputC, typeInput))

    then:
    input.failure
    def e = input.exception.get()
    e.cause.class == IllegalArgumentException
    e.cause.message == "Voltage level of node a must be greater than voltage level of node b and voltage level of node b must be greater than voltage level of node c"
  }
}
