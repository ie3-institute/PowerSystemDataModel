/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.DomesticHotWaterStorageInput
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
    def heatStorages = [Mock(CylindricalStorageInput)]
    def domesticHotWaterStorages = [
      Mock(DomesticHotWaterStorageInput)
    ]
    def thermalUnits = new ThermalGrid(thermalBus, thermalHouses, heatStorages, domesticHotWaterStorages)

    when:
    def actualAllEntities = thermalUnits.allEntitiesAsList()

    then:
    actualAllEntities.size() == 1 + thermalHouses.size() + heatStorages.size() + domesticHotWaterStorages.size()
    actualAllEntities.contains(thermalBus)
    actualAllEntities.containsAll(thermalHouses)
    actualAllEntities.containsAll(heatStorages)
  }

  def "A ThermalGrid's copy method should work as expected"() {
    given:
    def thermalBus = Mock(ThermalBusInput)
    def thermalHouses = []
    def heatStorages = []
    def domesticHotWaterStorages = []
    def thermalGrid = new ThermalGrid(thermalBus, thermalHouses, heatStorages, domesticHotWaterStorages)

    def modifiedHouses = [Mock(ThermalHouseInput)]
    def modifiedHeatStorages = [Mock(CylindricalStorageInput)]
    def modifiedDomesticHotWaterStorages = [Mock(CylindricalStorageInput)]

    when:
    def modifiedThermalGrid = thermalGrid.copy()
        .houses(modifiedHouses as Set)
        .heatStorages(modifiedHeatStorages as Set)
        .domesticHotWaterStorages(modifiedDomesticHotWaterStorages as Set)
        .build()

    then:
    modifiedThermalGrid.houses().first() == modifiedHouses.get(0)
    modifiedThermalGrid.heatStorages().first() == modifiedHeatStorages.get(0)
    modifiedThermalGrid.domesticHotWaterStorages().first() == modifiedDomesticHotWaterStorages.get(0)
  }
}
