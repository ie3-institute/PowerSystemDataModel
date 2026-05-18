/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system

import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLTAMPERE
import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATTHOUR

import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities


class LoadInputTest extends Specification {

  def "A LoadInput copy method should work as expected"() {
    given:
    def loadInput = SystemParticipantTestData.loadInput

    when:
    def alteredUnit = loadInput.copy().loadProfile(BdewStandardLoadProfile.G0.key)
        .eConsAnnual(Quantities.getQuantity(6000, KILOWATTHOUR)).sRated(Quantities.getQuantity(0d, KILOVOLTAMPERE))
        .cosPhiRated(0.8).node(GridTestData.nodeG)
        .qCharacteristics(CosPhiFixed.CONSTANT_CHARACTERISTIC).build()

    then:
    alteredUnit.with {
      uuid == loadInput.uuid
      operationTime == loadInput.operationTime
      operator == loadInput.operator
      id == loadInput.id
      loadProfile == BdewStandardLoadProfile.G0.key
      node == GridTestData.nodeG
      qCharacteristics == CosPhiFixed.CONSTANT_CHARACTERISTIC
      eConsAnnual == Quantities.getQuantity(6000, KILOWATTHOUR)
      sRated == Quantities.getQuantity(0d, KILOVOLTAMPERE)
      cosPhiRated == 0.8d
      controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }

  def "Scaling a LoadInput via builder should work as expected"() {
    given:
    def loadInput = SystemParticipantTestData.loadInput

    when:
    def alteredUnit = loadInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      uuid == loadInput.uuid
      operationTime == loadInput.operationTime
      operator == loadInput.operator
      id == loadInput.id
      loadProfile == loadInput.loadProfile
      node == loadInput.node
      qCharacteristics == loadInput.qCharacteristics
      eConsAnnual == loadInput.eConsAnnual * 2d
      sRated == loadInput.sRated * 2d
      cosPhiRated == loadInput.cosPhiRated
      controllingEm == Optional.of(SystemParticipantTestData.emInput)
    }
  }
}
