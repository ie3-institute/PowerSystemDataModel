/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput
import spock.lang.Specification

class ThermalUnitsTest extends Specification {

  def "A ThermalUnits object compiles the correct list of all entities"() {
    given:
    def thermalHouses = [
      Mock(ThermalHouseInput),
      Mock(ThermalHouseInput)
    ]
    def thermalStorages = [Mock(CylindricalStorageInput)]
    def thermalUnits = new ThermalUnits(thermalHouses, thermalStorages)

    when:
    def actualAllEntities = thermalUnits.allEntitiesAsList()

    then:
    actualAllEntities.size() == thermalHouses.size() + thermalStorages.size()
    actualAllEntities.containsAll(thermalHouses)
    actualAllEntities.containsAll(thermalStorages)
  }

  def "A ThermalUnits' copy method should work as expected"() {
    given:
    def thermalHouses = []
    def thermalStorages = []
    def thermalUnits = new ThermalUnits(thermalHouses, thermalStorages)

    def modifiedHouses = [Mock(ThermalHouseInput)]
    def modifiedStorages = [Mock(CylindricalStorageInput)]

    when:
    def modifiedThermalUnits = thermalUnits.copy()
        .houses(modifiedHouses as Set)
        .storages(modifiedStorages as Set)
        .build()

    then:
    modifiedThermalUnits.houses().first() == modifiedHouses.get(0)
    modifiedThermalUnits.storages().first() == modifiedStorages.get(0)
  }
}
