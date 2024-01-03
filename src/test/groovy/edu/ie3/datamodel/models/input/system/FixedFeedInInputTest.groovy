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
      assert uuid == ffIn.uuid
      assert operationTime == ffIn.operationTime
      assert operator == ffIn.operator
      assert id == ffIn.id
      assert qCharacteristics == ffIn.qCharacteristics
      assert sRated == Quantities.getQuantity(10d, PowerSystemUnits.VOLTAMPERE)
      assert cosPhiRated == 0.8d
    }
  }
}
