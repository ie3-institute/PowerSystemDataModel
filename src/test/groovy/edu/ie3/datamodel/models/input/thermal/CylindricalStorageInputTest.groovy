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
    def cylindricalStorageInput = ThermalUnitInputTestData.cylindricalStorageInput

    when:
    def alteredUnit = cylindricalStorageInput.copy().storageVolumeLvl(ThermalUnitInputTestData.storageVolumeLvl)
        .inletTemp(ThermalUnitInputTestData.inletTemp)
        .returnTemp(ThermalUnitInputTestData.returnTemp).c(ThermalUnitInputTestData.c)
        .thermalBus(ThermalUnitInputTestData.thermalBus).build()


    then:
    alteredUnit.with {
      uuid == cylindricalStorageInput.uuid
      id == cylindricalStorageInput.id
      operator == cylindricalStorageInput.operator
      operationTime == cylindricalStorageInput.operationTime
      thermalBus == cylindricalStorageInput.thermalBus
      storageVolumeLvl == ThermalUnitInputTestData.storageVolumeLvl
      inletTemp == ThermalUnitInputTestData.inletTemp
      returnTemp == ThermalUnitInputTestData.returnTemp
      c == ThermalUnitInputTestData.c
    }
  }

  def "Scaling a CylindricalStorageInput via builder should work as expected"() {
    given:
    def cylindricalStorageInput = ThermalUnitInputTestData.cylindricalStorageInput

    when:
    def alteredUnit = cylindricalStorageInput.copy().scale(2d).build()

    then:
    alteredUnit.with {
      uuid == cylindricalStorageInput.uuid
      id == cylindricalStorageInput.id
      operator == cylindricalStorageInput.operator
      operationTime == cylindricalStorageInput.operationTime
      thermalBus == cylindricalStorageInput.thermalBus
      storageVolumeLvl == cylindricalStorageInput.storageVolumeLvl * 2d
      inletTemp == cylindricalStorageInput.inletTemp
      returnTemp == cylindricalStorageInput.returnTemp
      c == cylindricalStorageInput.c
      pThermalMax == cylindricalStorageInput.pThermalMax * 2d
    }
  }
}
