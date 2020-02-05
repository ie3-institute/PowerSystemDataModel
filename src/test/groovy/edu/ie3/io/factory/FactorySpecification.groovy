package edu.ie3.io.factory

import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import javax.measure.Unit

class FactorySpecification extends Specification {
    static def getQuant(String parameter, Unit unit) {
        return Quantities.getQuantity(Double.parseDouble(parameter), unit)
    }
}
