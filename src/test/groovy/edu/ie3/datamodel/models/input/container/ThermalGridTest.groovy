/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput
import spock.lang.Specification

class ThermalGridTest extends Specification {

  def "A ThermalGrid object compiles the correct list of all entities"() {
    given:
    def thermalBus = Mock(ThermalBusInput)
    def thermalHouses = [
      Mock(ThermalHouseInput),
      Mock(ThermalHouseInput)
    ]
    def thermalStorages = [Mock(CylindricalStorageInput)]
    def thermalUnits = new ThermalGrid(thermalBus, thermalHouses, thermalStorages)

    when:
    def actualAllEntities = thermalUnits.allEntitiesAsList()

    then:
    actualAllEntities.size() == 1 + thermalHouses.size() + thermalStorages.size()
    actualAllEntities.contains(thermalBus)
    actualAllEntities.containsAll(thermalHouses)
    actualAllEntities.containsAll(thermalStorages)
  }
}
