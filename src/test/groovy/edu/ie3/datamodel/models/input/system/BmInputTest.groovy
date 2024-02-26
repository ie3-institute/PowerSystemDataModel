/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import static edu.ie3.util.quantities.PowerSystemUnits.EURO_PER_MEGAWATTHOUR

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class BmInputTest extends Specification {

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
      assert em == Optional.of(SystemParticipantTestData.emInput)
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
      assert em == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
