/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import javax.measure.Quantity;
import tech.units.indriya.ComparableQuantity;

// FIXME Class exists only for testing purposes, to be replaced by the class from PowerSystemUtils

public class QuantityUtil {

  public static <Q extends Quantity<Q>> boolean equals(
      ComparableQuantity<Q> a, ComparableQuantity<?> b) {
    if (a == null) return b == null;
    if (b == null) return false;
    if (!a.getUnit().isCompatible(b.getUnit())) return false;
    return a.isEquivalentTo((ComparableQuantity<Q>) b);
  }
}
