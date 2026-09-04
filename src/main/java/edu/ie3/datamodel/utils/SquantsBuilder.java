/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import java.util.function.BiFunction;
import scala.math.Numeric;
import squants.Quantity;
import tech.units.indriya.ComparableQuantity;

public final class SquantsBuilder {

  public static <A extends Quantity<A>> A build(
      double value, BiFunction<Object, Numeric<Object>, A> unit) {
    return SquantsBuilderScala$.MODULE$.build(value, unit);
  }

  public static <A extends Quantity<A>> A from(
      ComparableQuantity<?> value, BiFunction<Object, Numeric<Object>, A> unit) {
    return SquantsBuilderScala$.MODULE$.from(value, unit);
  }
}
