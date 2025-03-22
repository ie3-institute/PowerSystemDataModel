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

class Transformer3WTypeInputTest extends Specification{

  def "A Transformer2WTypeInput copy method should work as expected"() {
    given:
    def transformer3WTypeInput = GridTestData.transformerTypeAtoBtoC

    when:
    def alteredUnit = transformer3WTypeInput.copy()
        .id("transformer3WTypeInput_copy")
        .setSRatedA(Quantities.getQuantity(100000, PowerSystemUnits.KILOWATT))
        .setSRatedB(Quantities.getQuantity(70000, PowerSystemUnits.KILOWATT))
        .setSRatedC(Quantities.getQuantity(50000, PowerSystemUnits.KILOWATT))
        .setVRatedA(Quantities.getQuantity(400, PowerSystemUnits.KILOVOLT))
        .setVRatedB(Quantities.getQuantity(120, PowerSystemUnits.KILOVOLT))
        .setVRatedC(Quantities.getQuantity(30, PowerSystemUnits.KILOVOLT))
        .setRScA(Quantities.getQuantity(3, Units.OHM))
        .setRScB(Quantities.getQuantity(2, Units.OHM))
        .setRScC(Quantities.getQuantity(1, Units.OHM))
        .setXScA(Quantities.getQuantity(3, Units.OHM))
        .setXScB(Quantities.getQuantity(2, Units.OHM))
        .setXScC(Quantities.getQuantity(1, Units.OHM))
        .setGM(Quantities.getQuantity(50000, PowerSystemUnits.NANOSIEMENS))
        .setBM(Quantities.getQuantity(-2000, PowerSystemUnits.NANOSIEMENS))
        .setDV(Quantities.getQuantity(2, PowerSystemUnits.PERCENT))
        .setDPhi(Quantities.getQuantity(1, PowerSystemUnits.DEGREE_GEOM))
        .setTapNeutr(1)
        .setTapMin(-11)
        .setTapMax(20)
        .build()

    then:
    alteredUnit.with {
      assert uuid == transformer3WTypeInput.uuid
      assert id == "transformer3WTypeInput_copy"
      assert rScA == Quantities.getQuantity(3, Units.OHM)
      assert rScB == Quantities.getQuantity(2, Units.OHM)
      assert rScC == Quantities.getQuantity(1, Units.OHM)
      assert xScA == Quantities.getQuantity(3, Units.OHM)
      assert xScB == Quantities.getQuantity(2, Units.OHM)
      assert xScC == Quantities.getQuantity(1, Units.OHM)
      assert sRatedA == Quantities.getQuantity(100000, PowerSystemUnits.KILOWATT)
      assert sRatedB == Quantities.getQuantity(70000, PowerSystemUnits.KILOWATT)
      assert sRatedC == Quantities.getQuantity(50000, PowerSystemUnits.KILOWATT)
      assert vRatedA == Quantities.getQuantity(400, PowerSystemUnits.KILOVOLT)
      assert vRatedB == Quantities.getQuantity(120, PowerSystemUnits.KILOVOLT)
      assert vRatedC == Quantities.getQuantity(30, PowerSystemUnits.KILOVOLT)
      assert gM == Quantities.getQuantity(50000, PowerSystemUnits.NANOSIEMENS)
      assert bM == Quantities.getQuantity(-2000, PowerSystemUnits.NANOSIEMENS)
      assert dV == Quantities.getQuantity(2, PowerSystemUnits.PERCENT)
      assert dPhi == Quantities.getQuantity(1, PowerSystemUnits.DEGREE_GEOM)
      assert tapNeutr == 1
      assert tapMin == -11
      assert tapMax == 20
    }
  }
}
