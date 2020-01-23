/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models;

import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT;
import static edu.ie3.util.quantities.PowerSystemUnits.VOLT;

import edu.ie3.util.interval.ClosedInterval;
import javax.measure.quantity.ElectricPotential;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

public enum GermanVoltageLevel implements VoltageLevel {
  LV(
      "Low voltage level",
      new ClosedInterval<>(Quantities.getQuantity(230, VOLT), Quantities.getQuantity(500, VOLT))),
  MV(
      "Medium voltage level",
      new ClosedInterval<>(
          Quantities.getQuantity(1, KILOVOLT), Quantities.getQuantity(30, KILOVOLT))),
  HV(
      "High voltage level",
      new ClosedInterval<>(
          Quantities.getQuantity(60, KILOVOLT), Quantities.getQuantity(110, KILOVOLT))),
  EHV(
      "Extra high voltage level",
      new ClosedInterval<>(
          Quantities.getQuantity(220, KILOVOLT), Quantities.getQuantity(380, KILOVOLT)));

  private String name;
  private ClosedInterval<ComparableQuantity<ElectricPotential>> voltageRange;

  public ClosedInterval<ComparableQuantity<ElectricPotential>> getVoltageRange() {
    return voltageRange;
  }

  GermanVoltageLevel(
      String name, ClosedInterval<ComparableQuantity<ElectricPotential>> voltageRange) {
    this.name = name;
    this.voltageRange = voltageRange;
  }

  public String getName() {
    return name;
  }
}
