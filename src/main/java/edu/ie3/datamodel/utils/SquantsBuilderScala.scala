package edu.ie3.datamodel.utils

import squants.Quantity
import tech.units.indriya.ComparableQuantity

import java.util.function.BiFunction

private object SquantsBuilderScala {

  private[utils] def build[A <: Quantity[A]](value: Double, unit: BiFunction[Double, Numeric [Double], A]): A =
    unit.apply(value, Numeric.DoubleIsFractional)


  private[utils] def from[A <: Quantity[A]](value: ComparableQuantity[?], unit: BiFunction[Double, Numeric [Double], A]): A =
    build(value.getValue.doubleValue, unit)


}
