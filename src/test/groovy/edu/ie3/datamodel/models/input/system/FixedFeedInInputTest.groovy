/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.io.source.SourceValidator
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.SystemParticipantTestData
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class FixedFeedInInputTest extends Specification {

  def "An EvInput should throw an exception on incorrect fields correctly"() {
    given:
    def actualFields = SourceValidator.newSet("uuid")
    def validator = new SourceValidator()

    when:
    Try<Void, ValidationException> input = validator.validate(actualFields, FixedFeedInInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [uuid] are invalid for instance of 'FixedFeedInInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'FixedFeedInInput' are possible (NOT case-sensitive!):\n" +
        "0: [controllingEm, cosPhiRated, id, node, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, q_characteristics, s_rated, uuid]\n" +
        "1: [controllingEm, cosPhiRated, id, node, operator, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, operator, q_characteristics, s_rated, uuid]\n" +
        "2: [controllingEm, cosPhiRated, id, node, operatesFrom, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, operates_from, q_characteristics, s_rated, uuid]\n" +
        "3: [controllingEm, cosPhiRated, id, node, operatesFrom, operator, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, operates_from, operator, q_characteristics, s_rated, uuid]\n" +
        "4: [controllingEm, cosPhiRated, id, node, operatesUntil, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, operates_until, q_characteristics, s_rated, uuid]\n" +
        "5: [controllingEm, cosPhiRated, id, node, operatesUntil, operator, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, operates_until, operator, q_characteristics, s_rated, uuid]\n" +
        "6: [controllingEm, cosPhiRated, id, node, operatesFrom, operatesUntil, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, operates_from, operates_until, q_characteristics, s_rated, uuid]\n" +
        "7: [controllingEm, cosPhiRated, id, node, operatesFrom, operatesUntil, operator, qCharacteristics, sRated, uuid] or [controlling_em, cos_phi_rated, id, node, operates_from, operates_until, operator, q_characteristics, s_rated, uuid]\n"
  }

  def "A FixedFeedInInput copy method should work as expected"() {
    given:
    def ffIn = SystemParticipantTestData.fixedFeedInInput

    when:
    def alteredUnit = ffIn.copy().sRated(Quantities.getQuantity(10d, PowerSystemUnits.VOLTAMPERE)).cosPhiRated(0.8d).build()

    then:
    alteredUnit.with {
      assert uuid == ffIn.uuid
      assert operationTime == ffIn.operationTime
      assert operator == ffIn.operator
      assert id == ffIn.id
      assert qCharacteristics == ffIn.qCharacteristics
      assert sRated == Quantities.getQuantity(10d, PowerSystemUnits.VOLTAMPERE)
      assert cosPhiRated == 0.8d
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling a FixedFeedInInput via builder should work as expected"() {
    given:
    def ffIn = SystemParticipantTestData.fixedFeedInInput

    when:
    def alteredUnit = ffIn.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == ffIn.uuid
      assert operationTime == ffIn.operationTime
      assert operator == ffIn.operator
      assert id == ffIn.id
      assert qCharacteristics == ffIn.qCharacteristics
      assert sRated == ffIn.sRated * 2d
      assert cosPhiRated == ffIn.cosPhiRated
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
