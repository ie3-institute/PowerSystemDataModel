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
        .setRSc(Quantities.getQuantity(1, Units.OHM))
        .setXSc(Quantities.getQuantity(52, Units.OHM))
        .setSRated(Quantities.getQuantity(50000, PowerSystemUnits.KILOWATT))
        .setVRatedA(Quantities.getQuantity(111, PowerSystemUnits.KILOVOLT))
        .setVRatedB(Quantities.getQuantity(11, PowerSystemUnits.KILOVOLT))
        .setGM(Quantities.getQuantity(1, PowerSystemUnits.NANOSIEMENS))
        .setBM(Quantities.getQuantity(1, PowerSystemUnits.NANOSIEMENS))
        .setDV(Quantities.getQuantity(2, PowerSystemUnits.PERCENT))
        .setDPhi(Quantities.getQuantity(1, PowerSystemUnits.DEGREE_GEOM))
        .setTapSide(true)
        .setTapNeutr(11)
        .setTapMin(2)
        .setTapMax(20)
        .build()

    then:
    alteredUnit.with {
      assert uuid == transformer2WTypeInput.uuid
      assert id == "transformer2WTypeInput_copy"
      assert rSc == Quantities.getQuantity(1, Units.OHM)
      assert xSc == Quantities.getQuantity(52, Units.OHM)
      assert sRated == Quantities.getQuantity(50000, PowerSystemUnits.KILOWATT)
      assert vRatedA == Quantities.getQuantity(111, PowerSystemUnits.KILOVOLT)
      assert vRatedB == Quantities.getQuantity(11, PowerSystemUnits.KILOVOLT)
      assert gM == Quantities.getQuantity(1, PowerSystemUnits.NANOSIEMENS)
      assert bM == Quantities.getQuantity(1, PowerSystemUnits.NANOSIEMENS)
      assert dV == Quantities.getQuantity(2, PowerSystemUnits.PERCENT)
      assert dPhi == Quantities.getQuantity(1, PowerSystemUnits.DEGREE_GEOM)
      assert tapSide
      assert tapNeutr == 11
      assert tapMin == 2
      assert tapMax == 20
    }
  }
}
