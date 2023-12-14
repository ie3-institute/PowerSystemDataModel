/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import edu.ie3.test.common.EnergyManagementTestData
import spock.lang.Specification

class EnergyManagementUnitsTest extends Specification {

  def "An EnergyManagementUnits' copy method should work as expected"() {
    given:
    def energyManagementUnits = new EnergyManagementUnits(
        Collections.singleton(EnergyManagementTestData.emInput)
        )

    def modifiedEmInput = EnergyManagementTestData.emInput.copy().id("modified").build()

    when:
    def modifiedEnergyManagementUnits = energyManagementUnits.copy()
        .emUnits(Set.of(modifiedEmInput))
        .build()

    then:
    modifiedEnergyManagementUnits.emUnits.first() == modifiedEmInput
  }
}
