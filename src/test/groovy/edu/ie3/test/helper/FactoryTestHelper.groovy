/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.helper

import edu.ie3.util.TimeUtil
import org.locationtech.jts.io.geojson.GeoJsonReader
import tech.units.indriya.quantity.Quantities

import javax.measure.Unit

trait FactoryTestHelper {
	private static final GeoJsonReader GEOJSON_READER = new GeoJsonReader()
	static final TimeUtil TIME_UTIL = TimeUtil.withDefaults

	static getQuant(String parameter, Unit unit) {
		return Quantities.getQuantity(Double.parseDouble(parameter), unit)
	}

	static getGeometry(String value) {
		return GEOJSON_READER.read(value)
	}

}
