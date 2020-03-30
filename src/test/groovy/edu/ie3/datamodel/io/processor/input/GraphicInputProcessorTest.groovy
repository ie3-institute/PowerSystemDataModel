/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.processor.input

import edu.ie3.datamodel.models.input.graphics.LineGraphicInput
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput
import edu.ie3.test.common.GridTestData
import edu.ie3.util.TimeTools
import spock.lang.Specification

import java.time.ZoneId

class GraphicInputProcessorTest extends Specification {
	static {
		TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
	}

	def "The GraphicInputProcessor should de-serialize a provided NodeGraphicInput with point correctly"(){
		given:
		GraphicInputProcessor processor = new GraphicInputProcessor(NodeGraphicInput.class)
		NodeGraphicInput validNode = GridTestData.nodeGraphicC
		Map expected = [
			"uuid"          : "09aec636-791b-45aa-b981-b14edf171c4c",
			"graphicLayer"  : "main",
			"path"          : "",
			"point"         : "{\"type\":\"Point\",\"coordinates\":[0.0,10],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"node"          : "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2"
		]

		when:
		Optional<LinkedHashMap<String, String>> actual = processor.handleEntity(validNode)

		then:
		actual.isPresent()
		actual.get() == expected
	}

	def "The GraphicInputProcessor should de-serialize a provided NodeGraphicInput with path correctly"(){
		given:
		GraphicInputProcessor processor = new GraphicInputProcessor(NodeGraphicInput.class)
		NodeGraphicInput validNode = GridTestData.nodeGraphicD
		Map expected = [
			"uuid"          : "9ecad435-bd16-4797-a732-762c09d4af25",
			"graphicLayer"  : "main",
			"path"          : "{\"type\":\"LineString\",\"coordinates\":[[-1,0.0],[1,0.0]],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"point"         : "",
			"node"          : "6e0980e0-10f2-4e18-862b-eb2b7c90509b"
		]

		when:
		Optional<LinkedHashMap<String, String>> actual = processor.handleEntity(validNode)

		then:
		actual.isPresent()
		actual.get() == expected
	}

	def "The GraphicInputProcessor should de-serialize a provided LineGraphicInput correctly"(){
		given:
		GraphicInputProcessor processor = new GraphicInputProcessor(LineGraphicInput.class)
		LineGraphicInput validNode = GridTestData.lineGraphicCtoD
		Map expected = [
			"uuid"          : "ece86139-3238-4a35-9361-457ecb4258b0",
			"graphicLayer"  : "main",
			"path"          : "{\"type\":\"LineString\",\"coordinates\":[[0.0,0.0],[0.0,10]],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"line"          : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"
		]

		when:
		Optional<LinkedHashMap<String, String>> actual = processor.handleEntity(validNode)

		then:
		actual.isPresent()
		actual.get() == expected
	}
}
