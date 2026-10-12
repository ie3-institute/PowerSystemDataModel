/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.Transformer3WInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Shared
import spock.lang.Specification

class Transformer3WInputFactoryTest extends Specification implements FactoryTestHelper {

  @Shared private UUID typeUuid = UUID.randomUUID()
  @Shared private Transformer3WTypeInput typeInput = Mock(Transformer3WTypeInput)

  @Shared private Transformer3WInputFactory inputFactory

  def setupSpec() {
    typeInput.getUuid() >> typeUuid

    inputFactory = new Transformer3WInputFactory(map(OperatorInput.NO_OPERATOR_ASSIGNED), map([
      GridTestData.nodeA,
      GridTestData.nodeB,
      GridTestData.nodeC
    ]), map(typeInput))
  }

  def "A Transformer3WInputFactory should contain exactly the expected class for parsing"() {
    given:
    def expectedClasses = [Transformer3WInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A Transformer3WInputFactory should parse a valid Transformer3WInput correctly"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id" : "TestID",
      "parallelDevices": "2",
      "tapPos" : "3",
      "autoTap" : "true",
      "operator": "",
      "nodeA": GridTestData.nodeA.getUuid().toString(),
      "nodeB": GridTestData.nodeB.getUuid().toString(),
      "nodeC": GridTestData.nodeC.getUuid().toString(),
      "type": typeUuid.toString()
    ]

    when:
    Try<Transformer3WInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.success
    input.data.get().getClass() == Transformer3WInput
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime == OperationTime.notLimited()
      operator == OperatorInput.NO_OPERATOR_ASSIGNED
      id == parameter["id"]
      nodeA == GridTestData.nodeA
      nodeB == GridTestData.nodeB
      nodeC == GridTestData.nodeC
      type == typeInput
      parallelDevices == Integer.parseInt(parameter["parallelDevices"])
      tapPos == Integer.parseInt(parameter["tapPos"])
      autoTap
    }
  }
  def "A Transformer3WInputFactory should throw an IllegalArgumentException if nodeB is greater than nodeA or nodeC is greater than nodeB"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id" : "TestID",
      "parallelDevices": "2",
      "tapPos" : "3",
      "autoTap" : "true",
      "operator": "",
      "nodeA": GridTestData.nodeC.getUuid().toString(),
      "nodeB": GridTestData.nodeB.getUuid().toString(),
      "nodeC": GridTestData.nodeA.getUuid().toString(),
      "type": typeUuid.toString()
    ]

    when:
    Try<Transformer3WInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.failure
    def e = input.exception.get()
    e.cause.class == IllegalArgumentException
    e.cause.message == "Voltage level of node a must be greater than voltage level of node b and voltage level of node b must be greater than voltage level of node c"
  }
}
