/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import edu.ie3.test.common.SystemParticipantTestData
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities


class FixedFeedInInputTest extends Specification {

  def "A FixedFeedInInput copy method should work as expected"() {
    given:
    def ffIn = SystemParticipantTestData.fixedFeedInInput

    when:
    def alteredUnit = ffIn.copy().sRated(Quantities.getQuantity(10d, PowerSystemUnits.VOLTAMPERE)).cosPhiRated(0.8d).build()

    then:
    alteredUnit.with {
      uuid == ffIn.uuid
      operationTime == ffIn.operationTime
      operator == ffIn.operator
      id == ffIn.id
      qCharacteristics == ffIn.qCharacteristics
      sRated == Quantities.getQuantity(10d, PowerSystemUnits.VOLTAMPERE)
      cosPhiRated == 0.8d
      controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling a FixedFeedInInput via builder should work as expected"() {
    given:
    def ffIn = SystemParticipantTestData.fixedFeedInInput

    when:
    def alteredUnit = ffIn.copy().scale(2d).build()

    then:
    alteredUnit.with {
      uuid == ffIn.uuid
      operationTime == ffIn.operationTime
      operator == ffIn.operator
      id == ffIn.id
      qCharacteristics == ffIn.qCharacteristics
      sRated == ffIn.sRated * 2d
      cosPhiRated == ffIn.cosPhiRated
      controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
