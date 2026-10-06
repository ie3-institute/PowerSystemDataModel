/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input.participant

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.input.EmInput
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.EvcsInput
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointTypeUtils
import edu.ie3.datamodel.models.input.system.type.evcslocation.EvcsLocationType
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime
import javax.measure.quantity.Dimensionless

/**
 * Testing EvcsInputFactory
 *
 * @version 0.1* @since 26.07.20
 */
class EvcsInputFactoryTest extends Specification implements FactoryTestHelper {

  @Shared private UUID nodeUuid = UUID.randomUUID()
  @Shared  private def nodeInput = Mock(NodeInput)
  @Shared  private UUID operatorUuid = UUID.randomUUID()
  @Shared   private def operatorInput = Mock(OperatorInput)
  @Shared   private UUID emUuid = UUID.randomUUID()
  @Shared   private def emUnit = Mock(EmInput)

  @Shared private EvcsInputFactory inputFactory

  def setupSpec() {
    nodeInput.getUuid() >> nodeUuid
    operatorInput.getUuid() >> operatorUuid
    emUnit.getUuid() >> emUuid

    inputFactory = new EvcsInputFactory(map(operatorInput), map(nodeInput), map(emUnit))
  }

  def "A EvcsInputFactory should contain exactly the expected class for parsing"() {
    given:
    def expectedClasses = [EvcsInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A EvcsInputFactory should parse a valid EvcsInput correctly"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil" : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id" : "TestID",
      "qCharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "type" : "Household",
      "chargingPoints" : "4",
      "cosPhiRated" : "0.95",
      "locationType" : "CHARGING_HUB_TOWN",
      "v2gSupport" : "false",
      "operator": operatorUuid.toString(),
      "node": nodeUuid.toString(),
      "controllingEm": emUuid.toString()
    ]

    when:
    Try<EvcsInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.success
    input.data.get().getClass() == EvcsInput
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime.startDate.present
      operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesFrom"])
      operationTime.endDate.present
      operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesUntil"])
      operator == operatorInput
      id == parameter["id"]
      node == nodeInput
      qCharacteristics.with {
        uuid != null
        points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PowerSystemUnits.PU), Quantities.getQuantity(1d, PowerSystemUnits.PU))
        ] as TreeSet)
      }
      controllingEm == Optional.of(emUnit)
      type == ChargingPointTypeUtils.HouseholdSocket
      chargingPoints == Integer.parseInt(parameter["chargingPoints"])
      cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])
      locationType == EvcsLocationType.CHARGING_HUB_TOWN
      !v2gSupport
    }
  }

  def "A EvcsInputFactory should fail when passing an invalid ChargingPointType"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil" : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id" : "TestID",
      "qCharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "type" : "-- invalid --",
      "chargingPoints" : "4",
      "cosPhiRated" : "0.95",
      "locationType" : "CHARGING_HUB_TOWN",
      "v2gSupport" : "false",
      "operator": operatorUuid.toString(),
      "node": nodeUuid.toString(),
      "controllingEm": emUuid.toString()
    ]

    when:
    Try<EvcsInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.failure
    input.exception.get().cause.message == "Exception while trying to parse field \"type\" with supposed int value \"-- invalid --\""
  }

  def "A EvcsInputFactory should fail when passing an invalid EvcsLocationType"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil" : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id" : "TestID",
      "qCharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "type" : "Household",
      "chargingPoints" : "4",
      "cosPhiRated" : "0.95",
      "locationType" : "-- invalid --",
      "v2gSupport" : "false",
      "operator": operatorUuid.toString(),
      "node": nodeUuid.toString(),
      "controllingEm": emUuid.toString()
    ]

    when:
    Try<EvcsInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.failure
    input.exception.get().cause.message == "Exception while trying to parse field \"locationType\" with supposed int value \"-- invalid --\""
  }
}
