/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.source.RawGridSource
import edu.ie3.datamodel.io.source.TypeSource
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.GridTestData as rgtd
import spock.lang.Shared
import spock.lang.Specification

class CsvRawGridSourceTest extends Specification implements CsvTestDataMeta {
  @Shared
  RawGridSource source

  def setupSpec() {
    TypeSource typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, fileNamingStrategy))
    source = new RawGridSource(typeSource, new CsvDataSource(csvSep, gridDefaultFolderPath, fileNamingStrategy))
  }

  def "The CsvRawGridSource is able to load all nodes from file"() {
    when: "loading all nodes from file"
    def actualSet = source.getNodes()
    def expectedSet = [
      rgtd.nodeA,
      rgtd.nodeB,
      rgtd.nodeC,
      rgtd.nodeD,
      rgtd.nodeE,
      rgtd.nodeF,
      rgtd.nodeG
    ]

    then: "all nodes are there"
    actualSet.size() == expectedSet.size()

    actualSet.each { entry ->
      def actual = entry.value
      def expected = expectedSet.find {
        it.uuid == actual.uuid
      }
      assert expected != null

      actual.with {
        assert uuid == expected.uuid
        assert id == expected.id
        assert operator == expected.operator
        assert operationTime == expected.operationTime
        assert vTarget == expected.vTarget
        assert slack == expected.slack
        assert geoPosition.coordinates == expected.geoPosition.coordinates
        assert voltLvl == expected.voltLvl
        assert subnet == expected.subnet
      }
    }
  }

  def "The CsvRawGridSource is able to load all measurement units from file"() {
    when: "loading all measurement units from file"
    def actualSet = source.getMeasurementUnits()
    def expectedSet = [
      rgtd.measurementUnitInput
    ]

    then: "all measurement units are there"
    actualSet.size() == expectedSet.size()
    actualSet.each {actual ->
      def expected = expectedSet.find {it.uuid == actual.uuid}
      assert expected != null

      actual.with {
        assert uuid == expected.uuid
        assert id == expected.id
        assert operator == expected.operator
        assert operationTime == expected.operationTime
        assert node.uuid == expected.node.uuid
        assert vMag == expected.vMag
        assert vAng == expected.vAng
        assert p == expected.p
        assert q == expected.q
      }
    }
  }

  def "The CsvRawGridSource is able to load all switches from file"() {
    when: "loading all switches from file"
    def actualSet = source.getSwitches()
    def expectedSet = [rgtd.switchAtoB]

    then: "all switches are there"
    actualSet.size() == expectedSet.size()
    actualSet.each {actual ->
      def expected = expectedSet.find {it.uuid == actual.uuid}
      assert expected != null

      actual.with {
        assert uuid == expected.uuid
        assert id == expected.id
        assert operator == expected.operator
        assert operationTime == expected.operationTime
        assert nodeA.uuid == expected.nodeA.uuid
        assert nodeB.uuid == expected.nodeB.uuid
        assert closed == expected.closed
      }
    }
  }

  def "The CsvRawGridSource is able to load all lines from file"() {
    when: "loading all lines from file"
    def actualSet = source.getLines()
    def expectedSet = [
      rgtd.lineAtoB,
      rgtd.lineCtoD
    ]

    then: "all lines are there"
    actualSet.size() == expectedSet.size()
    actualSet.each { entry ->
      def actual = entry.value
      def expected = expectedSet.find {it.uuid == actual.uuid}
      assert expected != null

      actual.with {
        assert uuid == expected.uuid
        assert id == expected.id
        assert operator == expected.operator
        assert operationTime == expected.operationTime
        assert nodeA.uuid == expected.nodeA.uuid
        assert nodeB.uuid == expected.nodeB.uuid
        assert parallelDevices == expected.parallelDevices
        assert type == expected.type
        assert length == expected.length
        assert geoPosition.coordinates == expected.geoPosition.coordinates
        assert olmCharacteristic == expected.olmCharacteristic
      }
    }
  }

  def "The CsvRawGridSource is able to load all two winding transformers from file"() {
    when: "loading all two winding transformers from file"
    def actualSet = source.get2WTransformers()
    def expectedSet = [
      GridTestData.transformerBtoD,
      GridTestData.transformerBtoE,
      GridTestData.transformerCtoE,
      GridTestData.transformerCtoF,
      GridTestData.transformerCtoG
    ]

    then: "all two winding transformers are there"
    actualSet.size() == expectedSet.size()
    actualSet.each {actual ->
      def expected = expectedSet.find {it.uuid == actual.uuid}
      assert expected != null

      actual.with {
        assert uuid == expected.uuid
        assert id == expected.id
        assert operator == expected.operator
        assert operationTime == expected.operationTime
        assert nodeA.uuid == expected.nodeA.uuid
        assert nodeB.uuid == expected.nodeB.uuid
        assert parallelDevices == expected.parallelDevices
        assert type == expected.type
        assert tapPos == expected.tapPos
        assert autoTap == expected.autoTap
      }
    }
  }

  def "The CsvRawGridSource is able to load all three winding transformers from file"() {
    when: "loading all three winding transformers from file"
    def actualSet = source.get3WTransformers()
    def expectedSet = [
      GridTestData.transformerAtoBtoC
    ]

    then: "all three winding transformers are there"
    actualSet.size() == expectedSet.size()
    actualSet.each {actual ->
      def expected = expectedSet.find {it.uuid == actual.uuid}
      assert expected != null

      actual.with {
        assert uuid == expected.uuid
        assert id == expected.id
        assert operator == expected.operator
        assert operationTime == expected.operationTime
        assert nodeA.uuid == expected.nodeA.uuid
        assert nodeB.uuid == expected.nodeB.uuid
        assert nodeC.uuid == expected.nodeC.uuid
        assert parallelDevices == expected.parallelDevices
        assert type == expected.type
        assert tapPos == expected.tapPos
        assert autoTap == expected.autoTap
      }
    }
  }

  def "The CsvRawGridSource is able to provide a correct RawGridElements"() {
    when: "loading a total grid structure from file"
    def actual = source.getGridData()
    def expected = new RawGridElements(
        [
          rgtd.nodeA,
          rgtd.nodeB,
          rgtd.nodeC,
          rgtd.nodeD,
          rgtd.nodeE,
          rgtd.nodeF,
          rgtd.nodeG
        ] as Set,
        [
          rgtd.lineAtoB,
          rgtd.lineCtoD
        ] as Set,
        [
          GridTestData.transformerBtoD,
          GridTestData.transformerBtoE,
          GridTestData.transformerCtoE,
          GridTestData.transformerCtoF,
          GridTestData.transformerCtoG
        ] as Set,
        [
          GridTestData.transformerAtoBtoC
        ] as Set,
        [rgtd.switchAtoB] as Set,
        [
          rgtd.measurementUnitInput
        ] as Set
        )

    then: "all elements are there"
    actual != null
    actual.with {
      /* It's okay, to only test the uuids, because content is tested with the other test methods */
      assert nodes.size() == expected.nodes.size()
      assert nodes.each {entry -> expected.nodes.contains({it.uuid == entry.uuid})}
      assert lines.size() == expected.lines.size()
      assert lines.each {entry -> expected.lines.contains({it.uuid == entry.uuid})}
      assert transformer2Ws.size() == expected.transformer2Ws.size()
      assert transformer2Ws.each {entry -> expected.transformer2Ws.contains({it.uuid == entry.uuid})}
      assert transformer3Ws.size() == expected.transformer3Ws.size()
      assert transformer3Ws.each {entry -> expected.transformer3Ws.contains({it.uuid == entry.uuid})}
      assert switches.size() == expected.switches.size()
      assert switches.each {entry -> expected.switches.contains({it.uuid == entry.uuid})}
      assert measurementUnits.size() == expected.measurementUnits.size()
      assert measurementUnits.each {entry -> expected.measurementUnits.contains({it.uuid == entry.uuid})}
    }
  }

  def "The CsvRawGridSource throws a rawInputDataException, if one mandatory element for the RawGridElements is missing"() {
    given: "a source pointing to malformed grid data"
    TypeSource typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, fileNamingStrategy))
    source = new RawGridSource(typeSource, new CsvDataSource(csvSep, gridMalformedFolderPath, fileNamingStrategy))

    when: "loading a total grid structure from file"
    def actual = source.getGridData()

    then: "the optional is empty"
    actual == null
    SourceException ex = thrown()
    ex.message == "edu.ie3.datamodel.exceptions.FailureException: 1 exception(s) occurred within \"NodeInput\" data, one is: edu.ie3.datamodel.exceptions.FactoryException: An error occurred when creating instance of NodeInput.class."
  }

  def "The CsvRawGridSource returns an empty grid, if the RawGridElements contain no single element"() {
    given: "a source pointing to malformed grid data"
    TypeSource typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, fileNamingStrategy))
    source = new RawGridSource(typeSource, new CsvDataSource(csvSep, gridEmptyFolderPath, fileNamingStrategy))

    when: "loading a total grid structure from file"
    def actual = source.getGridData()

    then: "the optional is empty"
    actual.allEntitiesAsList().empty
  }

  def "A CsvRawGridSource should process invalid input data as expected when requested to provide an instance of RawGridElements"() {
    given:
    def typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, fileNamingStrategy))
    def rawGridSource =
    new RawGridSource(typeSource, new CsvDataSource(csvSep, gridDefaultFolderPath, fileNamingStrategy)) {
      @Override
      Map<UUID, NodeInput> getNodes() {
        return Collections.emptyMap()
      }

      @Override
      Map<UUID, NodeInput> getNodes(Map<UUID, OperatorInput> operators) {
        return Collections.emptyMap()
      }
    }

    when:
    def rawGridElements = Try.of(() -> rawGridSource.gridData, SourceException)

    then:
    rawGridElements.failure
    rawGridElements.data == Optional.empty()

    Exception ex = rawGridElements.exception.get()
    ex.class == SourceException
    ex.message.startsWith("edu.ie3.datamodel.exceptions.FailureException: 2 exception(s) occurred within \"LineInput\" data, one is: edu.ie3.datamodel.exceptions.FactoryException: edu.ie3.datamodel.exceptions.SourceException: Entity with uuid ")
  }
}