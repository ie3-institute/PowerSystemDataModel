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
import spock.lang.Specification

class EvInputTest extends Specification {

  def "An EvInput should throw an exception on incorrect fields correctly"() {
    given:
    def actualFields = SourceValidator.newSet("uuid")
    def validator = new SourceValidator()

    when:
    Try<Void, ValidationException> input = validator.validate(actualFields, EvInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [uuid] are invalid for instance of 'EvInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'EvInput' are possible (NOT case-sensitive!):\n" +
        "0: [controllingEm, id, node, qCharacteristics, type, uuid] or [controlling_em, id, node, q_characteristics, type, uuid]\n" +
        "1: [controllingEm, id, node, operator, qCharacteristics, type, uuid] or [controlling_em, id, node, operator, q_characteristics, type, uuid]\n" +
        "2: [controllingEm, id, node, operatesFrom, qCharacteristics, type, uuid] or [controlling_em, id, node, operates_from, q_characteristics, type, uuid]\n" +
        "3: [controllingEm, id, node, operatesFrom, operator, qCharacteristics, type, uuid] or [controlling_em, id, node, operates_from, operator, q_characteristics, type, uuid]\n" +
        "4: [controllingEm, id, node, operatesUntil, qCharacteristics, type, uuid] or [controlling_em, id, node, operates_until, q_characteristics, type, uuid]\n" +
        "5: [controllingEm, id, node, operatesUntil, operator, qCharacteristics, type, uuid] or [controlling_em, id, node, operates_until, operator, q_characteristics, type, uuid]\n" +
        "6: [controllingEm, id, node, operatesFrom, operatesUntil, qCharacteristics, type, uuid] or [controlling_em, id, node, operates_from, operates_until, q_characteristics, type, uuid]\n" +
        "7: [controllingEm, id, node, operatesFrom, operatesUntil, operator, qCharacteristics, type, uuid] or [controlling_em, id, node, operates_from, operates_until, operator, q_characteristics, type, uuid]\n"
  }

  def "An EvInput copy method should work as expected"() {
    given:
    def ev = SystemParticipantTestData.evInput

    when:
    def alteredUnit = ev.copy().type(SystemParticipantTestData.evTypeInput).build()

    then:
    alteredUnit.with {
      assert uuid == ev.uuid
      assert operationTime == ev.operationTime
      assert operator == ev.operator
      assert id == ev.id
      assert qCharacteristics == ev.qCharacteristics
      assert type == SystemParticipantTestData.evTypeInput
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling an EvInput via builder should work as expected"() {
    given:
    def ev = SystemParticipantTestData.evInput

    when:
    def alteredUnit = ev.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == ev.uuid
      assert operationTime == ev.operationTime
      assert operator == ev.operator
      assert id == ev.id
      assert qCharacteristics == ev.qCharacteristics
      assert type.sRated == ev.type.sRated * 2d
      assert sRated() == ev.type.sRated * 2d
      assert type.sRatedDC == ev.type.sRatedDC * 2d
      assert type.eStorage == ev.type.eStorage * 2d
      assert type.eCons == ev.type.eCons * 2d
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
