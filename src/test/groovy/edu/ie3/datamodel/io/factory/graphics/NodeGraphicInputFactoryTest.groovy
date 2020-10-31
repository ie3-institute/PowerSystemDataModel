/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.graphics

import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputEntityData
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputFactory
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput
import edu.ie3.datamodel.utils.GridAndGeoUtils
import edu.ie3.test.helper.FactoryTestHelper
import org.locationtech.jts.geom.LineString
import spock.lang.Specification

class NodeGraphicInputFactoryTest extends Specification implements FactoryTestHelper {

	def "A NodeGraphicInputFactory contain exactly the expected class for parsing"() {
		given:
		def inputFactory = new NodeGraphicInputFactory()
		def expectedClasses = [NodeGraphicInput]

		expect:
		inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
	}

	def "A NodeGraphicInputFactory should parse a valid NodeGraphicInput correctly"() {
		given:
		def inputFactory = new NodeGraphicInputFactory()
		Map<String, String> parameter = [
			"uuid"        : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"point"       : "{ \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] }",
			"path"        : "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
			"graphiclayer": "test_graphic_layer"
		]

		def inputClass = NodeGraphicInput
		def nodeInput = Mock(NodeInput)

		when:
		Optional<NodeGraphicInput> input = inputFactory.get(
				new NodeGraphicInputEntityData(parameter, nodeInput))

		then:
		input.present
		input.get().getClass() == inputClass
		((NodeGraphicInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert point == getGeometry(parameter["point"])
			assert path == getGeometry(parameter["path"])
			assert graphicLayer == parameter["graphiclayer"]
			assert node == nodeInput
		}
	}

	def "A NodeGraphicInputFactoryshould parse a valid NodeGraphicInput with different geoPosition strings correctly"() {
		given:
		def inputFactory = new NodeGraphicInputFactory()
		Map<String, String> parameter = [
			"uuid"        : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"point"       : "{ \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] }",
			"path"        : geoLineString,
			"graphiclayer": "test_graphic_layer"
		]

		def inputClass = NodeGraphicInput
		def nodeInput = Mock(NodeInput)

		when:
		Optional<NodeGraphicInput> input = inputFactory.get(
				new NodeGraphicInputEntityData(parameter, nodeInput))

		then:
		input.present
		input.get().getClass() == inputClass
		((NodeGraphicInput) input.get()).with {
			assert path == GridAndGeoUtils.buildSafeLineString(getGeometry(parameter["path"]) as LineString)
		}
		where:
		geoLineString                                                                                                                         | _
		"{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228]]}"                                           | _
		"{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228]]}" | _
		"{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.311111, 51.49228],[7.511111, 51.49228]]}" | _
	}
}
