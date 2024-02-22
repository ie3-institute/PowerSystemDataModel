/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.system.type.chargingpoint

import edu.ie3.datamodel.models.ElectricCurrentType
import edu.ie3.datamodel.models.StandardUnits
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class ChargingPointTypeTest extends Specification {

  def "A ChargingPointType copy method should work as expected"() {
    given:
    def cpt = ChargingPointTypeUtils.HouseholdSocket

    when:
    def alteredUnit = cpt.copy()
        .setElectricCurrentType(ElectricCurrentType.DC)
        .setsRated(Quantities.getQuantity(2.4, StandardUnits.S_RATED))
        .build()

    then:
    alteredUnit.with {
      assert id == cpt.id
      assert sRated == Quantities.getQuantity(2.4, StandardUnits.S_RATED)
      assert electricCurrentType == ElectricCurrentType.DC
      assert synonymousIds == cpt.synonymousIds
    }
  }

  def "Scaling a ChargingPointType via builder should work as expected"() {
    given:
    def cpt = ChargingPointTypeUtils.HouseholdSocket

    when:
    def alteredUnit = cpt.copy().scale(2d).build()

    then:
    alteredUnit.with {
      assert id == cpt.id
      assert sRated == cpt.sRated * 2d
      assert electricCurrentType == cpt.electricCurrentType
      assert synonymousIds == cpt.synonymousIds
    }
  }
}
