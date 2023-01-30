/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.graphics

import edu.ie3.test.common.GridTestData
import org.locationtech.jts.geom.LineString
import spock.lang.Specification


class GraphicInputTest extends Specification {

  static final class DummyGraphicObject extends GraphicInput {

    DummyGraphicObject(UUID uuid, String graphicLayer, LineString path) {
      super(uuid, graphicLayer, path)
    }
  }


  def "Two GraphicInput elements should be equal for different structures of LineStrings"() {

    given:
    UUID uuid = UUID.fromString("91ec3bcf-1777-4d38-af67-0bf7c9fa73c7")
    def dummyGraphic1 = new DummyGraphicObject(uuid, "testlayer", GridTestData.geoJsonReader.read(lineString) as LineString)
    def dummyGraphic2 = new DummyGraphicObject(uuid, "testlayer", GridTestData.geoJsonReader.read(lineString) as LineString)

    expect:
    // do NOT change this equals to '==' as this yields true even if it's false
    dummyGraphic1.equals(dummyGraphic2)

    where:
    lineString                                                                                                                            | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228]]}"                                           | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228]]}" | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.311111, 51.49228],[7.511111, 51.49228]]}" | _

  }
}
