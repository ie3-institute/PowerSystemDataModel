package edu.ie3.test.helper

class DoubleTestHelper {
    static def equalsWithTolerance(double lhs, double rhs, double tolerance) {
        return lhs >= rhs - tolerance && lhs <= rhs + tolerance
    }
}