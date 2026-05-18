/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.helper

class DoubleTestHelper {
  static def equalsWithTolerance(double lhs, double rhs, double tolerance) {
    return lhs >= rhs - tolerance && lhs <= rhs + tolerance
  }
}