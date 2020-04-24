/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.test.common.GridTestData
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.io.geojson.GeoJsonReader
import spock.lang.Specification
import tec.uom.se.quantity.Quantities
import tec.uom.se.unit.Units

import java.awt.GraphicsDevice

import static edu.ie3.util.quantities.PowerSystemUnits.PU

class LineInputTest extends Specification {

	def "A valid LineInput class equality and hashCode check must be ensured if all fields are the same, but the objects are different"(){
		given:
		def entity1 = new LineInput(
				UUID.fromString("91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
				"test_line_CtoD",
				OperatorInput.NO_OPERATOR_ASSIGNED,
				OperationTime.notLimited(),
				GridTestData.nodeC,
				GridTestData.nodeD,
				2,
				GridTestData.lineTypeInputCtoD,
				Quantities.getQuantity(3, Units.METRE),
				new GeoJsonReader().read("{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}") as LineString,
				OlmCharacteristicInput.CONSTANT_CHARACTERISTIC
				)

		def entity2 =
				new LineInput(
				UUID.fromString("91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
				"test_line_CtoD",
				OperatorInput.NO_OPERATOR_ASSIGNED,
				OperationTime.notLimited(),
				GridTestData.nodeC,
				GridTestData.nodeD,
				2,
				GridTestData.lineTypeInputCtoD,
				Quantities.getQuantity(3, Units.METRE),
				new GeoJsonReader().read("{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}") as LineString,
				OlmCharacteristicInput.CONSTANT_CHARACTERISTIC
				)

		expect:
		entity1 == entity2
		entity1.hashCode() == entity2.hashCode()
	}
}
