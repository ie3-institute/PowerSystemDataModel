/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input.participant

import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.io.source.DataSource
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.EmInput
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.utils.CollectionUtils
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime
import javax.measure.quantity.Dimensionless

class FixedFeedInInputFactoryTest extends Specification implements FactoryTestHelper {

  @Shared private UUID nodeUuid = UUID.randomUUID()
  @Shared private def nodeInput = Mock(NodeInput)
  @Shared private UUID operatorUuid = UUID.randomUUID()
  @Shared private def operatorInput = Mock(OperatorInput)
  @Shared private UUID emUuid = UUID.randomUUID()
  @Shared private def emUnit = Mock(EmInput)

  @Shared private FixedFeedInInputFactory inputFactory

  def setupSpec() {
    nodeInput.getUuid() >> nodeUuid
    operatorInput.getUuid() >> operatorUuid
    emUnit.getUuid() >> emUuid

    inputFactory = new FixedFeedInInputFactory(map(operatorInput), map(nodeInput), map(emUnit))
  }

  def "A FixedFeedInInputFactory should contain exactly the expected class for parsing"() {
    given:
    def expectedClasses = [FixedFeedInInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A FixedFeedInInputFactory should parse a valid FixedFeedInInput correctly"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil" : "",
      "id" : "TestID",
      "qCharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "sRated" : "3",
      "cosPhiRated" : "4",
      "operator": operatorUuid.toString(),
      "node": nodeUuid.toString(),
      "controllingEm": emUuid.toString()
    ]

    when:
    Try<FixedFeedInInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.success
    input.data.get().getClass() == FixedFeedInInput
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime.startDate.present
      operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesFrom"])
      !operationTime.endDate.present
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
      sRated == getQuant(parameter["sRated"], StandardUnits.S_RATED)
      cosPhiRated == Double.parseDouble(parameter["cosPhiRated"])
    }
  }

  def "A FixedFeedInInputFactory should throw an exception on invalid or incomplete data fields"() {
    given:
    def actualFields = CollectionUtils.newSet("uuid", "id", "s_rated", "cosphi_rated")

    when:
    Try<Void, ValidationException> input = DataSource.validate(actualFields, FixedFeedInInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [cosphi_rated, id, s_rated, uuid] are invalid for instance of 'FixedFeedInInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'FixedFeedInInput' are possible (NOT case-sensitive!):\n" +
        "0: [controllingEm, cosPhiRated, id, node, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, q_characteristics, s_rated, uuid]\n" +
        "1: [controllingEm, cosPhiRated, id, node, operatesFrom, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, operates_from, q_characteristics, s_rated, uuid]\n" +
        "2: [controllingEm, cosPhiRated, id, node, operatesUntil, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, operates_until, q_characteristics, s_rated, uuid]\n" +
        "3: [controllingEm, cosPhiRated, id, node, operatesFrom, operatesUntil, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, operates_from, operates_until, q_characteristics, s_rated, uuid]\n" +
        "4: [controllingEm, cosPhiRated, id, node, operator, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, operator, q_characteristics, s_rated, uuid]\n" +
        "5: [controllingEm, cosPhiRated, id, node, operatesFrom, operator, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, operates_from, operator, q_characteristics, s_rated, uuid]\n" +
        "6: [controllingEm, cosPhiRated, id, node, operatesUntil, operator, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, operates_until, operator, q_characteristics, s_rated, uuid]\n" +
        "7: [controllingEm, cosPhiRated, id, node, operatesFrom, operatesUntil, operator, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, operates_from, operates_until, operator, q_characteristics, s_rated, uuid]\n"
  }
}
