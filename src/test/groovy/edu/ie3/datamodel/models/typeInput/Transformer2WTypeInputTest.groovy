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

class Transformer2WTypeInputTest extends Specification{

  def "A Transformer2WTypeInput copy method should work as expected"() {
    given:
    def transformer2WTypeInput = GridTestData.transformerTypeBtoD

    when:
    def alteredUnit = transformer2WTypeInput.copy()
        .id("transformer2WTypeInput_copy")
        .rSc(Quantities.getQuantity(1, Units.OHM))
        .xSc(Quantities.getQuantity(52, Units.OHM))
        .sRated(Quantities.getQuantity(50000, PowerSystemUnits.KILOWATT))
        .vRatedA(Quantities.getQuantity(111, PowerSystemUnits.KILOVOLT))
        .vRatedB(Quantities.getQuantity(11, PowerSystemUnits.KILOVOLT))
        .gM(Quantities.getQuantity(1, PowerSystemUnits.NANOSIEMENS))
        .bM(Quantities.getQuantity(1, PowerSystemUnits.NANOSIEMENS))
        .dV(Quantities.getQuantity(2, PowerSystemUnits.PERCENT))
        .dPhi(Quantities.getQuantity(1, PowerSystemUnits.DEGREE_GEOM))
        .tapSide(true)
        .tapNeutr(11)
        .tapMin(2)
        .tapMax(20)
        .build()

    then:
    alteredUnit.with {
      uuid == transformer2WTypeInput.uuid
      id == "transformer2WTypeInput_copy"
      rSc == Quantities.getQuantity(1, Units.OHM)
      xSc == Quantities.getQuantity(52, Units.OHM)
      sRated == Quantities.getQuantity(50000, PowerSystemUnits.KILOWATT)
      vRatedA == Quantities.getQuantity(111, PowerSystemUnits.KILOVOLT)
      vRatedB == Quantities.getQuantity(11, PowerSystemUnits.KILOVOLT)
      gM == Quantities.getQuantity(1, PowerSystemUnits.NANOSIEMENS)
      bM == Quantities.getQuantity(1, PowerSystemUnits.NANOSIEMENS)
      dV == Quantities.getQuantity(2, PowerSystemUnits.PERCENT)
      dPhi == Quantities.getQuantity(1, PowerSystemUnits.DEGREE_GEOM)
      tapSide
      tapNeutr == 11
      tapMin == 2
      tapMax == 20
    }
  }
}
