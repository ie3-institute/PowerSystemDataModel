/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.typeInput

import edu.ie3.test.common.GridTestData
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units

class LineTypeInputTest extends Specification {

  def "A LineTypeInput copy method should work as expected"(){
    given:

    def lineTypeInput = GridTestData.lineTypeInputCtoD

    when:
    def alteredUnit = lineTypeInput.copy()
        .id("lineTypeInput_copy")
        .b(Quantities.getQuantity(0.1d, PowerSystemUnits.MICRO_SIEMENS_PER_KILOMETRE))
        .g(Quantities.getQuantity(0.1d, PowerSystemUnits.MICRO_SIEMENS_PER_KILOMETRE))
        .r(Quantities.getQuantity(0.5d, PowerSystemUnits.OHM_PER_KILOMETRE))
        .x(Quantities.getQuantity(0.4d, PowerSystemUnits.OHM_PER_KILOMETRE))
        .iMax(Quantities.getQuantity(310d, Units.AMPERE))
        .vRated(Quantities.getQuantity(30d, Units.VOLT))
        .build()

    then:
    alteredUnit.with {
      assert uuid == lineTypeInput.uuid
      assert id == "lineTypeInput_copy"
      assert b == Quantities.getQuantity(0.1d, PowerSystemUnits.MICRO_SIEMENS_PER_KILOMETRE)
      assert g == Quantities.getQuantity(0.1d, PowerSystemUnits.MICRO_SIEMENS_PER_KILOMETRE)
      assert r == Quantities.getQuantity(0.5d, PowerSystemUnits.OHM_PER_KILOMETRE)
      assert x == Quantities.getQuantity(0.4d, PowerSystemUnits.OHM_PER_KILOMETRE)
      assert iMax == Quantities.getQuantity(310d, Units.AMPERE)
      assert vRated == Quantities.getQuantity(30d, Units.VOLT)
    }
  }
}
