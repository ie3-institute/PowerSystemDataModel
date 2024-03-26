/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.helper

import static edu.ie3.datamodel.models.StandardUnits.ELECTRIC_CURRENT_MAGNITUDE
import static edu.ie3.datamodel.models.StandardUnits.RATED_VOLTAGE_MAGNITUDE
import static edu.ie3.datamodel.models.StandardUnits.S_RATED

import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.ElectricCurrent
import javax.measure.quantity.ElectricPotential
import javax.measure.quantity.Power

class QuantityHelper {
  static ComparableQuantity<ElectricPotential> potential(double value) {
    return Quantities.getQuantity(value, RATED_VOLTAGE_MAGNITUDE)
  }

  static ComparableQuantity<ElectricCurrent> current(double value) {
    return Quantities.getQuantity(value, ELECTRIC_CURRENT_MAGNITUDE)
  }

  static ComparableQuantity<Power> power(double value) {
    return Quantities.getQuantity(value, S_RATED)
  }
}
