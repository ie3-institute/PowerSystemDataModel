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
        .storageVolumeLvlMin(ThermalUnitInputTestData.storageVolumeLvlMin).inletTemp(ThermalUnitInputTestData.inletTemp)
        .returnTemp(ThermalUnitInputTestData.returnTemp).c(ThermalUnitInputTestData.c)
        .thermalBus(ThermalUnitInputTestData.thermalBus).build()


    then:
    alteredUnit.with {
      assert uuid == domesticHotWaterStorageInput.uuid
      assert id == domesticHotWaterStorageInput.id
      assert operator == domesticHotWaterStorageInput.operator
      assert operationTime == domesticHotWaterStorageInput.operationTime
      assert thermalBus == domesticHotWaterStorageInput.thermalBus
      assert storageVolumeLvl == ThermalUnitInputTestData.storageVolumeLvl
      assert storageVolumeLvlMin == ThermalUnitInputTestData.storageVolumeLvlMin
      assert inletTemp == ThermalUnitInputTestData.inletTemp
      assert returnTemp == ThermalUnitInputTestData.returnTemp
      assert c == ThermalUnitInputTestData.c
    }
  }

  def "Scaling a CylindricalStorageInput via builder should work as expected"() {
    given:
    def domesticHotWaterStorageInput = ThermalUnitInputTestData.domesticHotWaterStorageInput

    when:
    def alteredUnit = domesticHotWaterStorageInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert uuid == domesticHotWaterStorageInput.uuid
      assert id == domesticHotWaterStorageInput.id
      assert operator == domesticHotWaterStorageInput.operator
      assert operationTime == domesticHotWaterStorageInput.operationTime
      assert thermalBus == domesticHotWaterStorageInput.thermalBus
      assert storageVolumeLvl == domesticHotWaterStorageInput.storageVolumeLvl * 2d
      assert storageVolumeLvlMin == domesticHotWaterStorageInput.storageVolumeLvlMin * 2d
      assert inletTemp == domesticHotWaterStorageInput.inletTemp
      assert returnTemp == domesticHotWaterStorageInput.returnTemp
      assert c == domesticHotWaterStorageInput.c
      assert pThermalMax == domesticHotWaterStorageInput.pThermalMax * 2d
    }
  }
}
