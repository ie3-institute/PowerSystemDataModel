/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.utils.GridAndGeoUtils
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import org.locationtech.jts.geom.LineString
import spock.lang.Specification

class GraphicSourceTest extends Specification implements FactoryTestHelper{

  def "A LineGraphicInputFactory should be build correctly"() {
    given:
    def lineUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "path"         : "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "graphiclayer" : "test_graphic_layer",
      "line"         : lineUUID.toString()
    ]

    def lineInput = Mock(LineInput)
    def lines = [(lineUUID): lineInput]

    when:
    def input = GraphicSource.lineGraphicBuildFunction(lines).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert path == getGeometry(parameter["path"])
      assert graphicLayer == parameter["graphiclayer"]
      assert line == lineInput
    }
  }

  def "A LineGraphicInputFactory should be build with different geoPosition strings correctly"() {
    given:
    def lineUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "path"         : geoLineString,
      "graphiclayer" : "test_graphic_layer",
      "line"         : lineUUID.toString()
    ]

    def lineInput = Mock(LineInput)
    def lines = [(lineUUID): lineInput]

    when:
    def input = GraphicSource.lineGraphicBuildFunction(lines).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert path == GridAndGeoUtils.buildSafeLineString(getGeometry(parameter["path"]) as LineString)
    }

    where:
    geoLineString                                                                                                                         | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228]]}"                                           | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228]]}" | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.311111, 51.49228],[7.511111, 51.49228]]}" | _
  }

  def "A NodeGraphicInputFactory should be build correctly"() {
    given:
    def nodeUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"        : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "point"       : "{ \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] }",
      "path"        : "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "graphiclayer": "test_graphic_layer",
      "node"        : nodeUUID.toString()
    ]

    def nodeInput = Mock(NodeInput)
    def nodes = [(nodeUUID): nodeInput]

    when:
    def input = GraphicSource.nodeGraphicBuildFunction(nodes).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert point == getGeometry(parameter["point"])
      assert path == getGeometry(parameter["path"])
      assert graphicLayer == parameter["graphiclayer"]
      assert node == nodeInput
    }
  }

  def "A NodeGraphicInput should be build with different geoPosition strings correctly"() {
    given:
    def nodeUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"        : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "point"       : "{ \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] }",
      "path"        : geoLineString,
      "graphiclayer": "test_graphic_layer",
      "node"        : nodeUUID.toString()
    ]

    def nodeInput = Mock(NodeInput)
    def nodes = [(nodeUUID): nodeInput]

    when:
    def input = GraphicSource.nodeGraphicBuildFunction(nodes).apply(new Try.Success<>(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert path == GridAndGeoUtils.buildSafeLineString(getGeometry(parameter["path"]) as LineString)
    }
    where:
    geoLineString                                                                                                                         | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228]]}"                                           | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228]]}" | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.311111, 51.49228],[7.511111, 51.49228]]}" | _
  }
}
