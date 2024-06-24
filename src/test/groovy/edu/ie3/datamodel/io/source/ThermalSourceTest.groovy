/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source


import static edu.ie3.test.helper.EntityMap.map

import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput
import edu.ie3.datamodel.utils.Try
import spock.lang.Specification

class ThermalSourceTest extends Specification {

  def "A ThermalSource thermalUnitEnricher should work as expected"() {
    given:
    def bus = new ThermalBusInput(UUID.fromString("0d95d7f2-49fb-4d49-8636-383a5220384e"), "test_thermal_bus")
    def entityData = new EntityData(["operators": "", "thermalbus": "0d95d7f2-49fb-4d49-8636-383a5220384e"], ThermalHouseInput)
    def operators = map([OperatorInput.NO_OPERATOR_ASSIGNED])
    def buses = map([bus])

    when:
    def actual = ThermalSource.thermalUnitEnricher.apply(new Try.Success<>(entityData), operators, buses)

    then:
    actual.success
    actual.data.get().with {
      assert it.operatorInput == OperatorInput.NO_OPERATOR_ASSIGNED
      assert it.busInput == bus
    }
  }
}
