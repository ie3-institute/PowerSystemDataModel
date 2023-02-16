/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils


import edu.ie3.test.common.ComplexTopology
import edu.ie3.test.common.SampleJointGrid
import edu.ie3.util.quantities.PowerSystemUnits
import org.locationtech.jts.geom.Point
import org.locationtech.jts.io.geojson.GeoJsonReader
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.util.function.Function
import java.util.stream.Collectors
import java.util.stream.Stream


class ContainerNodeUpdateUtilTest extends Specification {

  @Shared
  GeoJsonReader geoJsonReader = new GeoJsonReader()

  def "A ContainerUpdateUtil should update chained 2w transformers when a single node update is provided as expected"() {

    given:
    def sampleGrid = SampleJointGrid.grid()

    def oldToNew = [:]
    oldToNew.put(oldNode, newNode)

    when:
    ContainerNodeUpdateUtil.TransformerNodeUpdateResult trafoNodeUpdateResult =
        ContainerNodeUpdateUtil.updateTransformers(sampleGrid.rawGrid.transformer2Ws, sampleGrid.rawGrid.transformer3Ws,
        oldToNew)

    then:
    trafoNodeUpdateResult.with {
      assert updatedOldToNewNodes.size() == 3
      // all nodes should have the expected geoPosition
      assert updatedOldToNewNodes.values().stream().map({ node -> node.geoPosition }).collect(Collectors.toSet()) == Collections.singleton(expectedGeoPosition)


      assert updatedTransformer2WInputs.size() == 2
      // transformers nodes should end up to be the updated nodes
      assert updatedTransformer2WInputs.stream().flatMap({ trafo2w -> Stream.of(trafo2w.getNodeA(), trafo2w.getNodeB()) })
      .collect(Collectors.toSet()) == updatedOldToNewNodes.values() as Set
    }

    where:
    oldNode               | newNode                                                                                                                                                           || expectedGeoPosition
    SampleJointGrid.nodeA | SampleJointGrid.nodeA.copy().geoPosition((Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [16.592276813887139, 49.37770599548332] }")).build() || (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [16.592276813887139, 49.37770599548332] }")
    SampleJointGrid.nodeD | SampleJointGrid.nodeD.copy().geoPosition((Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [16.592276813887139, 50.37770599548332] }")).build() || (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [16.592276813887139, 50.37770599548332] }")
    SampleJointGrid.nodeG | SampleJointGrid.nodeG.copy().geoPosition((Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [16.592276813887139, 25.37770599548332] }")).build() || (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [16.592276813887139, 25.37770599548332] }")

  }

  def "A ContainerUpdateUtil should update chained 2w transformers correctly when multiple node updates are provided"() {

    given:
    def sampleGrid = SampleJointGrid.grid()

    // trafo2w hv node
    def oldNodeG = SampleJointGrid.nodeG

    // trafo2w mv node
    def oldNodeD = SampleJointGrid.nodeD
    def newNodeD = SampleJointGrid.nodeD.copy().geoPosition((Point) geoJsonReader
        .read("{ \"type\": \"Point\", \"coordinates\": [16.592276813887139, 49.37770599548332] }"))
        .build()

    // trafo2w lv node
    def oldNodeA = SampleJointGrid.nodeA
    def newNodeA = SampleJointGrid.nodeA.copy().geoPosition((Point) geoJsonReader
        .read("{ \"type\": \"Point\", \"coordinates\": [6.592276813887139, 49.37770599548332] }"))
        .vTarget(Quantities.getQuantity(0.7, PowerSystemUnits.PU)).build()

    def oldToNew = [:]

    oldToNew.put(oldNodeD, newNodeD)
    oldToNew.put(oldNodeA, newNodeA)

    when:
    ContainerNodeUpdateUtil.TransformerNodeUpdateResult trafoNodeUpdateResult =
        ContainerNodeUpdateUtil.updateTransformers(sampleGrid.rawGrid.transformer2Ws, sampleGrid.rawGrid.transformer3Ws,
        oldToNew)

    then:
    trafoNodeUpdateResult.with {
      assert updatedOldToNewNodes.size() == 3
      // nodeA and nodeD are set to geoPosition of high voltage node of leading transformer (= nodeG geoPosition)
      assert updatedOldToNewNodes.get(oldNodeA) == newNodeA.copy().geoPosition(oldNodeG.geoPosition).build()
      assert updatedOldToNewNodes.get(oldNodeD) == newNodeD.copy().geoPosition(oldNodeG.geoPosition).build()
      assert updatedOldToNewNodes.get(oldNodeG) == oldNodeG

      assert updatedTransformer2WInputs.size() == 2
      // transformer nodes should end up to be the updated nodes
      assert updatedTransformer2WInputs.stream().flatMap({ trafo2w -> Stream.of(trafo2w.getNodeA(), trafo2w.getNodeB()) })
      .collect(Collectors.toSet()) == updatedOldToNewNodes.values() as Set
    }

  }

  def "A ContainerUpdateUtil should update chained 2w and 3w transformers correctly when only one node update is provided"() {

    given:
    def sampleGrid = ComplexTopology.grid

    def newNode = oldNode.copy().geoPosition(expectedGeoPosition).build() // alter geoPosition of node

    def oldToNew = [:]
    oldToNew.put(oldNode, newNode)

    when:

    ContainerNodeUpdateUtil.TransformerNodeUpdateResult trafoNodeUpdateResult =
        ContainerNodeUpdateUtil.updateTransformers(sampleGrid.rawGrid.transformer2Ws, sampleGrid.rawGrid.transformer3Ws,
        oldToNew)

    then:
    trafoNodeUpdateResult.with {
      assert updatedOldToNewNodes.size() == 7
      // all nodes should have the expected geoPosition
      assert updatedOldToNewNodes.values().stream().map({ node -> node.geoPosition }).collect(Collectors.toSet()) == Collections.singleton(expectedGeoPosition)


      assert updatedTransformer2WInputs.size() == 5
      // transformer nodes should end up to be the updated nodes
      assert Stream.of(updatedTransformer2WInputs.stream().flatMap({ trafo2w -> Stream.of(trafo2w.getNodeA(), trafo2w.getNodeB()) }),
      updatedTransformer3WInputs.stream().flatMap({ trafo3w -> Stream.of(trafo3w.getNodeA(), trafo3w.getNodeB(), trafo3w.getNodeC()) })).flatMap(Function.identity())
      .collect(Collectors.toSet()) == updatedOldToNewNodes.values() as Set
    }

    where:
    oldNode               || expectedGeoPosition
    ComplexTopology.nodeA || (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [6.592276813887139, 10.37770599548332] }")
    ComplexTopology.nodeB || (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [7.592276813887139, 10.37770599548332] }")
    ComplexTopology.nodeC || (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [9.592276813887139, 10.37770599548332] }")
    ComplexTopology.nodeD || (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [8.592276813887139, 10.37770599548332] }")
    ComplexTopology.nodeE || (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [2.592276813887139, 10.37770599548332] }")
    ComplexTopology.nodeF || (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [4.592276813887139, 10.37770599548332] }")
    ComplexTopology.nodeG || (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [3.592276813887139, 10.37770599548332] }")
  }

  def "A ContainerUpdateUtil should update chained 2w and 3w transformers correctly when multiple updates are provided"() {

    given:
    def sampleGrid = ComplexTopology.grid

    def newNode = oldNode.copy().geoPosition(updatedGeoPosition).build() // alter geoPosition of node

    def oldToNew = [:]
    oldToNew.put(oldNode, newNode)
    oldToNew.put(ComplexTopology.nodeA, ComplexTopology.nodeA.copy().vTarget(Quantities.getQuantity(0.7, PowerSystemUnits.PU)).build())

    when:
    ContainerNodeUpdateUtil.TransformerNodeUpdateResult trafoNodeUpdateResult =
        ContainerNodeUpdateUtil.updateTransformers(sampleGrid.rawGrid.transformer2Ws, sampleGrid.rawGrid.transformer3Ws,
        oldToNew)

    then:
    trafoNodeUpdateResult.with {
      assert updatedOldToNewNodes.size() == 7
      // all nodes should have the expected geoPosition
      assert updatedOldToNewNodes.values().stream().map({ node -> node.geoPosition }).collect(Collectors.toSet()) == Collections.singleton(expectedGeoPosition)

      assert updatedTransformer2WInputs.size() == 5
      // transformer nodes should end up to be the updated nodes
      assert Stream.of(updatedTransformer2WInputs.stream().flatMap({ trafo2w -> Stream.of(trafo2w.getNodeA(), trafo2w.getNodeB()) }),
      updatedTransformer3WInputs.stream().flatMap({ trafo3w -> Stream.of(trafo3w.getNodeA(), trafo3w.getNodeB(), trafo3w.getNodeC()) })).flatMap(Function.identity())
      .collect(Collectors.toSet()) == updatedOldToNewNodes.values() as Set
    }

    where:
    oldNode               | updatedGeoPosition                                                                                             || expectedGeoPosition
    ComplexTopology.nodeB | (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [7.592276813887139, 10.37770599548332] }") || ComplexTopology.nodeA.geoPosition
    ComplexTopology.nodeC | (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [9.592276813887139, 10.37770599548332] }") || ComplexTopology.nodeA.geoPosition
    ComplexTopology.nodeD | (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [8.592276813887139, 10.37770599548332] }") || ComplexTopology.nodeA.geoPosition
    ComplexTopology.nodeE | (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [2.592276813887139, 10.37770599548332] }") || ComplexTopology.nodeA.geoPosition
    ComplexTopology.nodeF | (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [4.592276813887139, 10.37770599548332] }") || ComplexTopology.nodeA.geoPosition
    ComplexTopology.nodeG | (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [3.592276813887139, 10.37770599548332] }") || ComplexTopology.nodeA.geoPosition
  }

  def "A ContainerUpdateUtil should update a provided joint grid with one node update as expected"() {

    given:
    def sampleGrid = SampleJointGrid.grid()

    def alteredGeoPos = (Point) geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [2.592276813887139, 10.37770599548332] }")
    def newNode = SampleJointGrid.nodeA.copy().geoPosition(alteredGeoPos).build()

    def oldToNew = new HashMap()
    oldToNew.put(SampleJointGrid.nodeA, newNode)

    when:
    def resultingGrid = ContainerNodeUpdateUtil.updateGridWithNodes(sampleGrid, oldToNew)

    then:
    // all assets are connected to nodeA -> all geoPositions should be the same
    resultingGrid.systemParticipants.allEntitiesAsList().stream().map({ x -> x.getNode().geoPosition }).collect(Collectors.toSet()) == [alteredGeoPos] as Set

    // all transformers are connected to nodeA -> all geoPositions should be the same
    resultingGrid.rawGrid.transformer2Ws.stream().flatMap({ trafo2w -> Stream.of(trafo2w.getNodeA().geoPosition, trafo2w.getNodeB().geoPosition)}).collect(Collectors.toSet()) == [alteredGeoPos] as Set

    // some lines also needed to get an update
    resultingGrid.rawGrid.lines.find {line -> line.getId().equals("lineAtoC")}.nodeA.geoPosition == alteredGeoPos
    resultingGrid.rawGrid.lines.find {line -> line.getId().equals("lineAtoC")}.geoPosition.coordinates.contains(alteredGeoPos.coordinate)

    resultingGrid.rawGrid.lines.find {line -> line.getId().equals("lineAtoB")}.nodeA.geoPosition == alteredGeoPos
    resultingGrid.rawGrid.lines.find {line -> line.getId().equals("lineAtoB")}.geoPosition.coordinates.contains(alteredGeoPos.coordinate)

    // lines got an update because their transformer node D got an update
    resultingGrid.rawGrid.lines.find {line -> line.getId().equals("lineDtoE")}.nodeA.geoPosition == alteredGeoPos
    resultingGrid.rawGrid.lines.find {line -> line.getId().equals("lineDtoE")}.geoPosition.coordinates.contains(alteredGeoPos.coordinate)

    resultingGrid.rawGrid.lines.find {line -> line.getId().equals("lineDtoF")}.nodeA.geoPosition == alteredGeoPos
    resultingGrid.rawGrid.lines.find {line -> line.getId().equals("lineDtoF")}.geoPosition.coordinates.contains(alteredGeoPos.coordinate)

  }

}
