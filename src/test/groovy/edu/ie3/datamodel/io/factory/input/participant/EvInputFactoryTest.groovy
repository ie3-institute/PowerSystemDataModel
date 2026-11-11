/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input.participant

import static edu.ie3.test.helper.EntityMap.*
import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.FieldMap
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.EvInput
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.models.input.system.type.EvTypeInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime
import javax.measure.quantity.Dimensionless

class EvInputFactoryTest extends Specification implements FactoryTestHelper {

  @Shared private UUID operatorUuid = UUID.randomUUID()
  @Shared private def operatorInput = Mock(OperatorInput)
  @Shared private UUID typeUuid = UUID.randomUUID()
  @Shared private def typeInput = Mock(EvTypeInput)

  @Shared private EvInputFactory inputFactory

  def setupSpec() {
    operatorInput.getUuid() >> operatorUuid
    typeInput.getUuid() >> typeUuid

    inputFactory = new EvInputFactory(map(operatorInput), map(typeInput))
  }

  def "An EvInputFactory should contain exactly the expected class for parsing"() {
    given:
    def expectedClasses = [EvInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "An EvInputFactory should parse a valid EvInput correctly without node or controlling EM"() {
    given: "an EV input file without node or controlling EM fields"
    Map<String, String> parameter = [
      "uuid"           : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom"   : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil"  : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"             : "TestID",
      "qCharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "operator"       : operatorUuid.toString(),
      "type"           : typeUuid.toString()
    ]

    when:
    Try<EvInput, FactoryException> input = inputFactory.get(new FieldMap(parameter))

    then:
    input.success
    input.data.get().getClass() == EvInput
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime.startDate.present
      operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesFrom"])
      operationTime.endDate.present
      operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesUntil"])
      operator == operatorInput
      id == parameter["id"]
      qCharacteristics.with {
        uuid != null
        points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(
              Quantities.getQuantity(0d, PU),
              Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
      type == typeInput
    }
  }
}