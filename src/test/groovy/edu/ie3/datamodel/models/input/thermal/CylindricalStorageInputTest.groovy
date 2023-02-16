/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.thermal

import edu.ie3.test.common.ThermalUnitInputTestData
import spock.lang.Specification


class CylindricalStorageInputTest extends Specification {

  def "A CylindricalStorageInput copy method should work as expected"() {
    given:
    def cylindricalStorageInput = ThermalUnitInputTestData.cylindricStorageInput

    when:
    def alteredUnit = cylindricalStorageInput.copy().storageVolumeLvl(ThermalUnitInputTestData.storageVolumeLvl)
        .storageVolumeLvlMin(ThermalUnitInputTestData.storageVolumeLvlMin).inletTemp(ThermalUnitInputTestData.inletTemp)
        .returnTemp(ThermalUnitInputTestData.returnTemp).c(ThermalUnitInputTestData.c)
        .thermalBus(ThermalUnitInputTestData.thermalBus).build()


    then:
    alteredUnit.with {
      assert uuid == cylindricalStorageInput.uuid
      assert id == cylindricalStorageInput.id
      assert operator == cylindricalStorageInput.operator
      assert operationTime == cylindricalStorageInput.operationTime
      assert thermalBus == cylindricalStorageInput.thermalBus
      assert storageVolumeLvl == ThermalUnitInputTestData.storageVolumeLvl
      assert storageVolumeLvlMin == ThermalUnitInputTestData.storageVolumeLvlMin
      assert inletTemp == ThermalUnitInputTestData.inletTemp
      assert returnTemp == ThermalUnitInputTestData.returnTemp
      assert c == ThermalUnitInputTestData.c
    }
  }
}
