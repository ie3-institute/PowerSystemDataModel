/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import javax.measure.Quantity;

public record ValueWithWeight<V extends Quantity<V>>(Quantity<V> value, long weight) {

  @Override
  public String toString() {
    return "ValueWithWeight{" + "value=" + value + ", weight=" + weight + '}';
  }
}
