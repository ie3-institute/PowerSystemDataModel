/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Shared
import spock.lang.Specification

import java.time.ZonedDateTime

class Transformer2WInputFactoryTest extends Specification implements FactoryTestHelper {

  @Shared private UUID operatorUuid = UUID.randomUUID()
  @Shared private OperatorInput operatorInput = Mock(OperatorInput)
  @Shared private UUID typeUuid = UUID.randomUUID()
  @Shared private Transformer2WTypeInput typeInput = Mock(Transformer2WTypeInput)

  @Shared private Transformer2WInputFactory inputFactory

  def setupSpec() {
    operatorInput.getUuid() >> operatorUuid
    typeInput.getUuid() >> typeUuid

    inputFactory = new Transformer2WInputFactory(map(operatorInput), map([
      GridTestData.nodeA,
      GridTestData.nodeB
    ]), map(typeInput))
  }

  def "A Transformer2WInputFactory should contain exactly the expected class for parsing"() {
    given:
    def expectedClasses = [Transformer2WInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A Transformer2WInputFactory should parse a valid Transformer2WInput correctly"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil" : "",
      "id" : "TestID",
      "parallelDevices": "2",
      "tapPos" : "3",
      "autoTap" : "true",
      "operator": operatorUuid.toString(),
      "nodeA": GridTestData.nodeA.getUuid().toString(),
      "nodeB": GridTestData.nodeB.getUuid().toString(),
      "type": typeUuid.toString()
    ]

    when:
    Try<Transformer2WInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.success
    input.data.get().getClass() == Transformer2WInput
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime.startDate.present
      operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesFrom"])
      !operationTime.endDate.present
      operator == operatorInput
      id == parameter["id"]
      nodeA == GridTestData.nodeA
      nodeB == GridTestData.nodeB
      type == typeInput
      parallelDevices == Integer.parseInt(parameter["parallelDevices"])
      tapPos == Integer.parseInt(parameter["tapPos"])
      autoTap
    }
  }

  def "A Transformer2WInputFactory should throw an IllegalArgumentException if nodeA is on the lower voltage side"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil" : "",
      "id" : "TestID",
      "parallelDevices": "2",
      "tapPos" : "3",
      "autoTap" : "true",
      "operator": operatorUuid.toString(),
      "nodeA": GridTestData.nodeB.getUuid().toString(),
      "nodeB": GridTestData.nodeA.getUuid().toString(),
      "type": typeUuid.toString()
    ]

    when:
    Try<Transformer2WInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.failure
    def e = input.exception.get()
    e.cause.class == IllegalArgumentException
    e.cause.message == "nodeA must be on the higher voltage side of the transformer"
  }
}
