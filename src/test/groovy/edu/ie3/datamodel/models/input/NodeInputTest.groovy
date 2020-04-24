/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.test.common.GridTestData
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.GeometryFactory
import org.locationtech.jts.geom.PrecisionModel
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import static edu.ie3.util.quantities.PowerSystemUnits.PU


class NodeInputTest extends Specification {

	def "A valid NodeInput class equality and hashCode check must be ensured if all fields are the same, but the objects are different"(){
		given:
		def entity1 = new NodeInput(
				UUID.fromString("aaa74c1a-d07e-4615-99a5-e991f1d81cc4"),
				"node_entity",
				OperatorInput.NO_OPERATOR_ASSIGNED,
				OperationTime.notLimited(),
				Quantities.getQuantity(1d, PU),
				false,
				new GeometryFactory(new PrecisionModel(), 4326).createPoint(new Coordinate(7.4116482, 51.4843281)),
				GermanVoltageLevelUtils.LV,
				6)
		def entity2 = new NodeInput(
				UUID.fromString("aaa74c1a-d07e-4615-99a5-e991f1d81cc4"),
				"node_entity",
				OperatorInput.NO_OPERATOR_ASSIGNED,
				OperationTime.notLimited(),
				Quantities.getQuantity(1d, PU),
				false,
				new GeometryFactory(new PrecisionModel(), 4326).createPoint(new Coordinate(7.4116482, 51.4843281)),
				GermanVoltageLevelUtils.LV,
				6)

		expect:
		entity1 == entity2
		entity1.hashCode() == entity2.hashCode()
	}
}
