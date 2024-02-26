/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import static edu.ie3.test.helper.EntityMap.map

import edu.ie3.datamodel.exceptions.FailureException
import edu.ie3.datamodel.exceptions.GraphicSourceException
import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.source.GraphicSource
import edu.ie3.datamodel.io.source.RawGridSource
import edu.ie3.datamodel.io.source.TypeSource
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData as gtd
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import spock.lang.Specification

class CsvGraphicSourceTest extends Specification implements CsvTestDataMeta {

  def "A CsvGraphicSource should provide an instance of GraphicElements based on valid input data correctly"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, fileNamingStrategy))
    def rawGridSource = new RawGridSource(typeSource, new CsvDataSource(csvSep, gridDefaultFolderPath, fileNamingStrategy))
    def graphicSource = new GraphicSource(typeSource, rawGridSource, new CsvDataSource(csvSep, graphicsFolderPath, fileNamingStrategy))

    when:
    def graphicElements = graphicSource.graphicElements

    then:
    graphicElements.allEntitiesAsList().size() == 3
    graphicElements.nodeGraphics.size() == 2
    graphicElements.lineGraphics.size() == 1
  }

  def "A CsvGraphicSource should process invalid input data as expected when requested to provide an instance of GraphicElements"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, fileNamingStrategy))
    def rawGridSource =
    new RawGridSource(typeSource, new CsvDataSource(csvSep, gridDefaultFolderPath, fileNamingStrategy)) {
      @Override
      Map<UUID, LineInput> getLines() {
        return Collections.emptyMap()
      }

      @Override
      Map<UUID, LineInput> getLines(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, LineTypeInput> lineTypeInputs) {
        return Collections.emptyMap()
      }
    }

    def graphicSource = new GraphicSource(typeSource, rawGridSource, new CsvDataSource(csvSep, graphicsFolderPath, fileNamingStrategy))

    when:
    def graphicElements = Try.of(() -> graphicSource.graphicElements, GraphicSourceException)

    then:
    graphicElements.failure
    graphicElements.data == Optional.empty()

    Exception ex = graphicElements.exception.get()
    ex.class == GraphicSourceException
    ex.message.startsWith("1error(s) occurred while initializing graphic elements.  edu.ie3.datamodel.exceptions.FailureException: 1 exception(s) occurred within \"LineGraphicInput\" data, one is: edu.ie3.datamodel.exceptions.FactoryException: edu.ie3.datamodel.exceptions.SourceException: Linked line with UUID 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7 was not found for entity")
  }


  def "A CsvGraphicSource should read and handle a valid node graphics file as expected"() {
    given:
    def graphicSource = new GraphicSource(
    Mock(TypeSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, graphicsFolderPath, fileNamingStrategy))
    def expectedNodeGraphicD = new NodeGraphicInput(
    gtd.nodeGraphicD.uuid,
    gtd.nodeGraphicD.graphicLayer,
    gtd.nodeGraphicD.path,
    gtd.nodeD,
    gtd.geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [7.4116482, 51.4843281] }") as Point
    )
    def expectedNodeGraphicC = new NodeGraphicInput(
    gtd.nodeGraphicC.uuid,
    gtd.nodeGraphicC.graphicLayer,
    gtd.geoJsonReader.read("{ \"type\": \"LineString\", \"coordinates\": [[7.4116482, 51.4843281], [7.4116482, 51.4843281]]}") as LineString,
    gtd.nodeC,
    gtd.nodeGraphicC.point
    )

    Map<UUID, NodeInput>  nodeMap = [
      (gtd.nodeC.getUuid()) : gtd.nodeC,
      (gtd.nodeD.getUuid()) : gtd.nodeD
    ]

    when:
    def nodeGraphics = graphicSource.getNodeGraphicInput(nodeMap)

    then:
    nodeGraphics.size() == 2
    nodeGraphics == [
      expectedNodeGraphicC,
      expectedNodeGraphicD
    ] as Set
  }

  def "A GraphicSource should read and handle a valid line graphics file as expected"() {
    given:
    def graphicSource = new GraphicSource(
    Mock(TypeSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, graphicsFolderPath, fileNamingStrategy))

    Map<UUID, LineInput> lineMap = [
      (gtd.lineCtoD.getUuid()) : gtd.lineCtoD
    ]

    when:
    def lineGraphics = graphicSource.getLineGraphicInput(lineMap)

    then:
    lineGraphics.size() == 1
    lineGraphics.first() == gtd.lineGraphicCtoD
  }

  def "A GraphicSource when building node graphic data should fail when required node data is not provided"() {
    given:
    def graphicSource = new GraphicSource(
    Mock(TypeSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, graphicsFolderPath, fileNamingStrategy))
    Map<UUID, NodeInput> nodeMap = map(nodeCollection)

    when:
    graphicSource.getNodeGraphicInput(nodeMap)

    then:
    def e = thrown(SourceException)
    e.cause.class == FailureException
    e.cause.message.startsWith(expectedFailures + " exception(s) occurred")

    where:
    nodeCollection         || expectedFailures
    []                     || 2     // no nodes provided
    [gtd.nodeA, gtd.nodeB] || 2     // wrongs nodes provided
    [gtd.nodeC]            || 1     // one node provided
    [gtd.nodeD]            || 1     // one node provided
  }

  def "A GraphicSource when building line graphic data should fail when required line data is not provided"() {
    given:
    def graphicSource = new GraphicSource(
    Mock(TypeSource),
    Mock(RawGridSource),
    new CsvDataSource(csvSep, graphicsFolderPath, fileNamingStrategy))
    Map<UUID, LineInput> lineMap = map(lineCollection)

    when:
    graphicSource.getLineGraphicInput(lineMap)

    then:
    def e = thrown(SourceException)
    e.cause.class == FailureException
    e.cause.message.startsWith(expectedFailures + " exception(s) occurred")

    where:
    lineCollection || expectedFailures
    []             || 1     // no lines provided
    [gtd.lineAtoB] || 1     // line cannot be found
  }
}