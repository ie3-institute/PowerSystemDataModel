/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input

import edu.ie3.test.common.GridTestData
import spock.lang.Specification


class MeasurementUnitInputTest extends Specification {

  def "A MeasurementUnitInput copy method should work as expected"() {
    given:
    def unit = GridTestData.measurementUnitInput

    when:
    def alteredUnit = unit.copy().node(GridTestData.nodeB).vMag(false).vAng(false).p(false).q(false).build()

    then:
    alteredUnit.with {
      assert uuid == unit.uuid
      assert operationTime == unit.operationTime
      assert operator == unit.operator
      assert id == unit.id
      assert node == GridTestData.nodeB
      assert !getVMag()
      assert !getVAng()
      assert !getP()
      assert !getQ()
    }
  }
}
