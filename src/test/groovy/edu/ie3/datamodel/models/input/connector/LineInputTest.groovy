/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.connector

import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.test.common.GridTestData
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.io.geojson.GeoJsonReader
import spock.lang.Shared
import spock.lang.Specification
import tec.uom.se.quantity.Quantities
import tec.uom.se.unit.Units

class LineInputTest extends Specification {

	@Shared
	private static final GeoJsonReader geoJsonReader = new GeoJsonReader()

	def "Two LineInputs should be equal if all attributes are equal"() {
		given:
		def line1 = new LineInput(
				UUID.fromString("91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
				"test_line_CtoD",
				GridTestData.profBroccoli,
				GridTestData.defaultOperationTime,
				GridTestData.nodeC,
				GridTestData.nodeD,
				2,
				GridTestData.lineTypeInputCtoD,
				Quantities.getQuantity(3, Units.METRE),
				geoJsonReader.read(lineString) as LineString,
				OlmCharacteristicInput.CONSTANT_CHARACTERISTIC)

		def line2 = new LineInput(
				UUID.fromString("91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
				"test_line_CtoD",
				GridTestData.profBroccoli,
				GridTestData.defaultOperationTime,
				GridTestData.nodeC,
				GridTestData.nodeD,
				2,
				GridTestData.lineTypeInputCtoD,
				Quantities.getQuantity(3, Units.METRE),
				geoJsonReader.read(lineString) as LineString,
				OlmCharacteristicInput.CONSTANT_CHARACTERISTIC)

		expect:
		// do NOT change this equals to '==' as this yields true even if it's false
		line1.equals(line2)

		where:
		lineString                                                                                                                            | _
		"{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228]]}"                                           | _
		"{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228]]}" | _
		"{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.311111, 51.49228],[7.511111, 51.49228]]}" | _

	}
}
