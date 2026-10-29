/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import scala.math.Numeric;
import squants.Quantity;

@FunctionalInterface
public interface QuantityFunction<U extends Quantity<U>> {

  U apply(double value, Numeric<Object> n);
}
