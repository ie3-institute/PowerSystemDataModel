/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Shared
import spock.lang.Specification

import java.time.ZonedDateTime

class SwitchInputFactoryTest extends Specification implements FactoryTestHelper {

  @Shared private UUID operatorUuid = UUID.randomUUID()
  @Shared private OperatorInput operatorInput = Mock(OperatorInput)
  @Shared private UUID uuidNodeA = UUID.randomUUID()
  @Shared private NodeInput nodeInputA = Mock(NodeInput)
  @Shared private UUID uuidNodeB = UUID.randomUUID()
  @Shared private NodeInput nodeInputB = Mock(NodeInput)
  @Shared private SwitchInputFactory inputFactory

  def setupSpec() {
    operatorInput.getUuid() >> operatorUuid

    nodeInputA.getUuid() >> uuidNodeA
    nodeInputA.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION

    nodeInputB.getUuid() >> uuidNodeB
    nodeInputB.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION

    inputFactory = new SwitchInputFactory(map(operatorInput), map(nodeInputA, nodeInputB))
  }

  def "A SwitchInputFactory should contain exactly the expected class for parsing"() {
    given:
    def expectedClasses = [SwitchInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A SwitchInputFactory should parse a valid SwitchInput correctly"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil": "",
      "id" : "TestID",
      "closed" : "true",
      "operator": operatorUuid.toString(),
      "nodeA": uuidNodeA.toString(),
      "nodeB": uuidNodeB.toString()
    ]

    when:
    Try<SwitchInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.success
    input.data.get().getClass() == SwitchInput
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime.startDate.present
      operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesFrom"])
      !operationTime.endDate.present
      operator == operatorInput
      id == parameter["id"]
      nodeA == nodeInputA
      nodeB == nodeInputB
      closed
    }
  }

  def "A SwitchInputFactory should parse a valid SwitchInput with parallelDevices parameter correctly"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil": "",
      "id" : "TestID",
      "closed" : "true",
      "parallelDevices": "2",
      "operator": operatorUuid.toString(),
      "nodeA": uuidNodeA.toString(),
      "nodeB": uuidNodeB.toString()
    ]

    expect:
    Try<SwitchInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))
    input.success
    input.data.get().getClass() == SwitchInput
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime.startDate.present
      operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesFrom"])
      !operationTime.endDate.present
      operator == operatorInput
      id == parameter["id"]
      nodeA == nodeInputA
      nodeB == nodeInputB
      closed
      parallelDevices == 1
    }
  }
}
