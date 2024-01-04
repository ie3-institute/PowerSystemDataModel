/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import static edu.ie3.util.quantities.PowerSystemUnits.DEGREE_GEOM
import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLTAMPERE
import static tech.units.indriya.unit.Units.PERCENT

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities


class PvInputTest extends Specification {

  def "A PvInput copy method should work as expected"() {
    given:
    def pvInput = SystemParticipantTestData.pvInput

    when:
    def alteredUnit = pvInput.copy().albedo(10).azimuth(Quantities.getQuantity(10, DEGREE_GEOM)).elevationAngle(Quantities.getQuantity(50, DEGREE_GEOM))
        .etaConv(Quantities.getQuantity(50d, PERCENT)).kG(10).kT(5).marketReaction(true).sRated(Quantities.getQuantity(0d, KILOVOLTAMPERE))
        .cosPhiRated(0.7d).build()
    then:
    alteredUnit.with {
      assert uuid == pvInput.uuid
      assert operationTime == pvInput.operationTime
      assert operator == pvInput.operator
      assert id == pvInput.id
      assert qCharacteristics == pvInput.qCharacteristics
      assert sRated == Quantities.getQuantity(0d, KILOVOLTAMPERE)
      assert cosPhiRated == 0.7d
      assert marketReaction
      assert albedo == 10
      assert azimuth == Quantities.getQuantity(10, DEGREE_GEOM)
      assert etaConv == Quantities.getQuantity(50, PERCENT)
      assert elevationAngle == Quantities.getQuantity(50, DEGREE_GEOM)
      assert kG == 10
      assert kT == 5
    }
  }
}
