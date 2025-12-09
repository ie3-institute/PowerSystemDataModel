/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import static edu.ie3.util.quantities.PowerSystemUnits.EURO_PER_MEGAWATTHOUR

import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.io.source.SourceValidator
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.SystemParticipantTestData
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class BmInputTest extends Specification implements FactoryTestHelper {

  def "A BmInput should throw an exception on incorrect fields correctly"() {
    given:
    def actualFields = SourceValidator.newSet("uuid")
    def validator = new SourceValidator()

    when:
    Try<Void, ValidationException> input = validator.validate(actualFields, BmInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [uuid] are invalid for instance of 'BmInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'BmInput' are possible (NOT case-sensitive!):\n" +
        "0: [controllingEm, costControlled, feedInTariff, id, marketReaction, node, qCharacteristics, type, uuid] or [controlling_em, cost_controlled, feed_in_tariff, id, market_reaction, node, q_characteristics, type, uuid]\n" +
        "1: [controllingEm, costControlled, feedInTariff, id, marketReaction, node, operator, qCharacteristics, type, uuid] or [controlling_em, cost_controlled, feed_in_tariff, id, market_reaction, node, operator, q_characteristics, type, uuid]\n" +
        "2: [controllingEm, costControlled, feedInTariff, id, marketReaction, node, operatesFrom, qCharacteristics, type, uuid] or [controlling_em, cost_controlled, feed_in_tariff, id, market_reaction, node, operates_from, q_characteristics, type, uuid]\n" +
        "3: [controllingEm, costControlled, feedInTariff, id, marketReaction, node, operatesFrom, operator, qCharacteristics, type, uuid] or [controlling_em, cost_controlled, feed_in_tariff, id, market_reaction, node, operates_from, operator, q_characteristics, type, uuid]\n" +
        "4: [controllingEm, costControlled, feedInTariff, id, marketReaction, node, operatesUntil, qCharacteristics, type, uuid] or [controlling_em, cost_controlled, feed_in_tariff, id, market_reaction, node, operates_until, q_characteristics, type, uuid]\n" +
        "5: [controllingEm, costControlled, feedInTariff, id, marketReaction, node, operatesUntil, operator, qCharacteristics, type, uuid] or [controlling_em, cost_controlled, feed_in_tariff, id, market_reaction, node, operates_until, operator, q_characteristics, type, uuid]\n" +
        "6: [controllingEm, costControlled, feedInTariff, id, marketReaction, node, operatesFrom, operatesUntil, qCharacteristics, type, uuid] or [controlling_em, cost_controlled, feed_in_tariff, id, market_reaction, node, operates_from, operates_until, q_characteristics, type, uuid]\n" +
        "7: [controllingEm, costControlled, feedInTariff, id, marketReaction, node, operatesFrom, operatesUntil, operator, qCharacteristics, type, uuid] or [controlling_em, cost_controlled, feed_in_tariff, id, market_reaction, node, operates_from, operates_until, operator, q_characteristics, type, uuid]\n"
  }

  def "A BmInput copy method should work as expected"() {
    given:
    def bmInput = SystemParticipantTestData.bmInput

    when:
    def alteredUnit = bmInput.copy().type(SystemParticipantTestData.bmTypeInput)
        .marketReaction(true)
        .costControlled(true).feedInTariff(Quantities.getQuantity(15, EURO_PER_MEGAWATTHOUR)).build()

    then:
    alteredUnit.with {
      assert uuid == bmInput.uuid
      assert operationTime == bmInput.operationTime
      assert operator == bmInput.operator
      assert id == bmInput.id
      assert marketReaction
      assert costControlled
      assert qCharacteristics == bmInput.qCharacteristics
      assert feedInTariff == Quantities.getQuantity(15, EURO_PER_MEGAWATTHOUR)
      assert type == SystemParticipantTestData.bmTypeInput
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling a BmInput via builder should work as expected"() {
    given:
    def bmInput = SystemParticipantTestData.bmInput

    when:
    def alteredUnit = bmInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == bmInput.uuid
      assert operationTime == bmInput.operationTime
      assert operator == bmInput.operator
      assert id == bmInput.id
      assert marketReaction == bmInput.marketReaction
      assert costControlled == bmInput.costControlled
      assert qCharacteristics == bmInput.qCharacteristics
      assert feedInTariff == bmInput.feedInTariff
      assert type.sRated == bmInput.type.sRated * 2d
      assert sRated() == bmInput.type.sRated * 2d
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
