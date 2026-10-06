/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input.participant

import spock.lang.Shared

import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.EmInput
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime
import javax.measure.quantity.Dimensionless

class PvInputFactoryTest extends Specification implements FactoryTestHelper {

  @Shared private UUID nodeUuid = UUID.randomUUID()
  @Shared private def nodeInput = Mock(NodeInput)
  @Shared private UUID operatorUuid = UUID.randomUUID()
  @Shared private def operatorInput = Mock(OperatorInput)
  @Shared  private UUID emUuid = UUID.randomUUID()
  @Shared private def emUnit = Mock(EmInput)

  @Shared  private PvInputFactory inputFactory

  def setupSpec() {
    nodeInput.getUuid() >> nodeUuid
    operatorInput.getUuid() >> operatorUuid
    emUnit.getUuid() >> emUuid

    inputFactory = new PvInputFactory(map(operatorInput), map(nodeInput), map(emUnit))
  }

  def "A PvInputFactory should contain exactly the expected class for parsing"() {
    given:
    def expectedClasses = [PvInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A PvInputFactory should parse a valid PvInput correctly"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil" : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id" : "TestID",
      "qCharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "albedo" : "3",
      "azimuth" : "4",
      "etaConv" : "5",
      "elevationAngle" : "6",
      "kG" : "7",
      "kT" : "8",
      "sRated" : "9",
      "cosPhiRated" : "10",
      "operator": operatorUuid.toString(),
      "node": nodeUuid.toString(),
      "controllingEm": emUuid.toString()
    ]

    when:
    Try<PvInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.success
    input.data.get().getClass() == PvInput
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
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PU), Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      controllingEm == Optional.of(emUnit)
      albedo == Double.parseDouble(parameter["albedo"])
      azimuth == getQuant(parameter["azimuth"], StandardUnits.AZIMUTH)
      etaConv == getQuant(parameter["etaConv"], StandardUnits.EFFICIENCY)
      elevationAngle == getQuant(parameter["elevationAngle"], StandardUnits.SOLAR_ELEVATION_ANGLE)
      kG == Double.parseDouble(parameter["kG"])
      kT == Double.parseDouble(parameter["kT"])
      sRated == getQuant(parameter["sRated"], StandardUnits.S_RATED)
      cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])
    }
  }
}
