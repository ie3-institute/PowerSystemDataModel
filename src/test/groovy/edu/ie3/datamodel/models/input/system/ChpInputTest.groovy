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

class ChpInputTest extends Specification {

  def "A ChpInput should throw an exception on incorrect fields correctly"() {
    given:
    def actualFields = SourceValidator.newSet("uuid")
    def validator = new SourceValidator()

    when:
    Try<Void, ValidationException> input = validator.validate(actualFields, ChpInput)

    then:
    input.failure
    input.exception.get().message == "The provided fields [uuid] are invalid for instance of 'ChpInput'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'ChpInput' are possible (NOT case-sensitive!):\n" +
        "0: [controllingEm, id, marketReaction, node, qCharacteristics, thermalBus, thermalStorage, type, uuid] or [controlling_em, id, market_reaction, node, q_characteristics, thermal_bus, thermal_storage, type, uuid]\n" +
        "1: [controllingEm, id, marketReaction, node, operator, qCharacteristics, thermalBus, thermalStorage, type, uuid] or [controlling_em, id, market_reaction, node, operator, q_characteristics, thermal_bus, thermal_storage, type, uuid]\n" +
        "2: [controllingEm, id, marketReaction, node, operatesFrom, qCharacteristics, thermalBus, thermalStorage, type, uuid] or [controlling_em, id, market_reaction, node, operates_from, q_characteristics, thermal_bus, thermal_storage, type, uuid]\n" +
        "3: [controllingEm, id, marketReaction, node, operatesFrom, operator, qCharacteristics, thermalBus, thermalStorage, type, uuid] or [controlling_em, id, market_reaction, node, operates_from, operator, q_characteristics, thermal_bus, thermal_storage, type, uuid]\n" +
        "4: [controllingEm, id, marketReaction, node, operatesUntil, qCharacteristics, thermalBus, thermalStorage, type, uuid] or [controlling_em, id, market_reaction, node, operates_until, q_characteristics, thermal_bus, thermal_storage, type, uuid]\n" +
        "5: [controllingEm, id, marketReaction, node, operatesUntil, operator, qCharacteristics, thermalBus, thermalStorage, type, uuid] or [controlling_em, id, market_reaction, node, operates_until, operator, q_characteristics, thermal_bus, thermal_storage, type, uuid]\n" +
        "6: [controllingEm, id, marketReaction, node, operatesFrom, operatesUntil, qCharacteristics, thermalBus, thermalStorage, type, uuid] or [controlling_em, id, market_reaction, node, operates_from, operates_until, q_characteristics, thermal_bus, thermal_storage, type, uuid]\n" +
        "7: [controllingEm, id, marketReaction, node, operatesFrom, operatesUntil, operator, qCharacteristics, thermalBus, thermalStorage, type, uuid] or [controlling_em, id, market_reaction, node, operates_from, operates_until, operator, q_characteristics, thermal_bus, thermal_storage, type, uuid]\n"
  }

  def "A ChpInput copy method should work as expected"() {
    given:
    def chpInput = SystemParticipantTestData.chpInput

    when:
    def alteredUnit = chpInput.copy().thermalBus(SystemParticipantTestData.thermalBus)
        .type(SystemParticipantTestData.chpTypeInput).thermalStorage(SystemParticipantTestData.thermalStorage).marketReaction(true).build()

    then:
    alteredUnit.with {
      assert uuid == chpInput.uuid
      assert operationTime == chpInput.operationTime
      assert operator == chpInput.operator
      assert id == chpInput.id
      assert qCharacteristics == chpInput.qCharacteristics
      assert thermalBus == SystemParticipantTestData.thermalBus
      assert thermalStorage == SystemParticipantTestData.thermalStorage
      assert marketReaction
      assert type == SystemParticipantTestData.chpTypeInput
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling a ChpInput via builder should work as expected"() {
    given:
    def chpInput = SystemParticipantTestData.chpInput

    when:
    def alteredUnit = chpInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == chpInput.uuid
      assert operationTime == chpInput.operationTime
      assert operator == chpInput.operator
      assert id == chpInput.id
      assert qCharacteristics == chpInput.qCharacteristics
      assert thermalBus == chpInput.thermalBus
      assert thermalStorage == chpInput.thermalStorage
      assert marketReaction == chpInput.marketReaction
      assert type.sRated == chpInput.type.sRated * 2d
      assert sRated() == chpInput.type.sRated * 2d
      assert type.pThermal == chpInput.type.pThermal * 2d
      assert type.pOwn == chpInput.type.pOwn * 2d
      assert controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
