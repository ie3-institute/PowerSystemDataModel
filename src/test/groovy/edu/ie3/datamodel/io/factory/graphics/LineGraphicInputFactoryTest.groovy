/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.graphics

import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputEntityData
import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputFactory
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class LineGraphicInputFactoryTest extends Specification implements FactoryTestHelper {

	def "A LineGraphicInputFactory contain exactly the expected class for parsing"() {
		given:
		def inputFactory = new LineGraphicInputFactory()
		def expectedClasses = [LineGraphicInput]

		expect:
		inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
	}

	def "A LineGraphicInputFactory should parse a valid NodeGraphicInput correctly"() {
		given:
		def inputFactory = new LineGraphicInputFactory()
		Map<String, String> parameter = [
			"uuid"          : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"path": "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
			"graphiclayer"  : "test_graphic_layer"
		]

		def inputClass = LineGraphicInput
		def lineInput = Mock(LineInput)

		when:
		Optional<LineGraphicInput> input = inputFactory.getEntity(
				new LineGraphicInputEntityData(parameter, lineInput))

		then:
		input.present
		input.get().getClass() == inputClass
		((LineGraphicInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert path == getGeometry(parameter["path"])
			assert graphicLayer == parameter["graphiclayer"]
			assert line == lineInput
		}
	}
}
