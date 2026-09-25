/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import java.util.Objects;
import javax.measure.Quantity;
import javax.measure.Unit;

public final class QuantityUtils {

  private QuantityUtils() {}

  /**
   * Improved version of {@link Objects#equals(Object, Object)}.
   *
   * @param a first quantity
   * @param b second quantity
   * @return true, if both quantities are equal
   * @param <Q> type of the quantity
   */
  public static <Q extends Quantity<Q>> boolean equals(Quantity<Q> a, Quantity<Q> b) {
    if (a == b) {
      return true;
    }

    if (a != null && b != null) {
      // convert the second quantity
      return a.equals(b.to(a.getUnit()));
    }

    return false;
  }

  public static <Q extends Quantity<Q>> String toString(Quantity<Q> quantity, Unit<Q> unit) {
    return Double.toString(quantity.to(unit).getValue().doubleValue());
  }
}
