/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.thermal

import edu.ie3.test.common.ThermalUnitInputTestData
import spock.lang.Specification


class DomesticHotWaterStorageInputTest extends Specification {

  def "A DomesticHotWaterStorageInput copy method should work as expected"() {
    given:
    def domesticHotWaterStorageInput = ThermalUnitInputTestData.domesticHotWaterStorageInput

    when:
    def alteredUnit = domesticHotWaterStorageInput.copy().storageVolumeLvl(ThermalUnitInputTestData.storageVolumeLvl)
        .inletTemp(ThermalUnitInputTestData.inletTemp)
        .returnTemp(ThermalUnitInputTestData.returnTemp).c(ThermalUnitInputTestData.c)
        .thermalBus(ThermalUnitInputTestData.thermalBus).build()


    then:
    alteredUnit.with {
      uuid == domesticHotWaterStorageInput.uuid
      id == domesticHotWaterStorageInput.id
      operator == domesticHotWaterStorageInput.operator
      operationTime == domesticHotWaterStorageInput.operationTime
      thermalBus == domesticHotWaterStorageInput.thermalBus
      storageVolumeLvl == ThermalUnitInputTestData.storageVolumeLvl
      inletTemp == ThermalUnitInputTestData.inletTemp
      returnTemp == ThermalUnitInputTestData.returnTemp
      c == ThermalUnitInputTestData.c
    }
  }

  def "Scaling a DomesticHotWaterStorageInput via builder should work as expected"() {
    given:
    def domesticHotWaterStorageInput = ThermalUnitInputTestData.domesticHotWaterStorageInput

    when:
    def alteredUnit = domesticHotWaterStorageInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      uuid == domesticHotWaterStorageInput.uuid
      id == domesticHotWaterStorageInput.id
      operator == domesticHotWaterStorageInput.operator
      operationTime == domesticHotWaterStorageInput.operationTime
      thermalBus == domesticHotWaterStorageInput.thermalBus
      storageVolumeLvl == domesticHotWaterStorageInput.storageVolumeLvl * 2d
      inletTemp == domesticHotWaterStorageInput.inletTemp
      returnTemp == domesticHotWaterStorageInput.returnTemp
      c == domesticHotWaterStorageInput.c
      pThermalMax == domesticHotWaterStorageInput.pThermalMax * 2d
    }
  }
}
