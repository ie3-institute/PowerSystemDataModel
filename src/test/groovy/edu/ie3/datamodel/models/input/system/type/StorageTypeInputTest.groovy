/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system.type

import static edu.ie3.datamodel.models.StandardUnits.ACTIVE_POWER_IN
import static edu.ie3.datamodel.models.StandardUnits.ENERGY_IN

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class StorageTypeInputTest extends Specification {

  def "A StorageTypeInput copy method should work as expected"() {
    given:
    def storageTypeInput = SystemParticipantTestData.storageTypeInput

    when:
    def alteredUnit = storageTypeInput.copy()
        .seteStorage(Quantities.getQuantity(90, ENERGY_IN))
        .setpMax(Quantities.getQuantity(15, ACTIVE_POWER_IN))
        .build()

    then:
    alteredUnit.with {
      uuid == storageTypeInput.uuid
      id == storageTypeInput.id
      capex == storageTypeInput.capex
      opex == storageTypeInput.opex
      eStorage == Quantities.getQuantity(90, ENERGY_IN)
      sRated == storageTypeInput.sRated
      cosPhiRated == storageTypeInput.cosPhiRated
      pMax == Quantities.getQuantity(15, ACTIVE_POWER_IN)
      activePowerGradient == storageTypeInput.activePowerGradient
      eta == storageTypeInput.eta
    }
  }

  def "Scaling a StorageTypeInput via builder should work as expected"() {
    given:
    def storageTypeInput = SystemParticipantTestData.storageTypeInput

    when:
    def alteredUnit = storageTypeInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      uuid == storageTypeInput.uuid
      id == storageTypeInput.id
      capex == storageTypeInput.capex * 2d
      opex == storageTypeInput.opex
      eStorage == storageTypeInput.eStorage * 2d
      sRated == storageTypeInput.sRated * 2d
      cosPhiRated == storageTypeInput.cosPhiRated
      pMax == storageTypeInput.pMax * 2d
      activePowerGradient == storageTypeInput.activePowerGradient
      eta == storageTypeInput.eta
    }
  }
}
