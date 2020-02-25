package edu.ie3.test.helper

import org.locationtech.jts.io.geojson.GeoJsonReader
import tec.uom.se.quantity.Quantities

import javax.measure.Unit

trait FactoryTestHelper {
    private static final GeoJsonReader geoJsonReader = new GeoJsonReader();

    static getQuant(String parameter, Unit unit) {
        return Quantities.getQuantity(Double.parseDouble(parameter), unit)
    }

    static getGeometry(String value) {
        return geoJsonReader.read(value)
    }
}
