package edu.ie3.test.helper

import tec.uom.se.quantity.Quantities

import javax.measure.Unit

trait FactoryTestHelper {
    static getQuant(String parameter, Unit unit) {
        return Quantities.getQuantity(Double.parseDouble(parameter), unit)
    }
}
