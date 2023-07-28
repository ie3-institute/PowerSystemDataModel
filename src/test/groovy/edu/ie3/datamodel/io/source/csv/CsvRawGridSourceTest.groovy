/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData
import edu.ie3.datamodel.io.factory.input.ConnectorInputEntityData
import edu.ie3.datamodel.io.factory.input.Transformer3WInputEntityData
import edu.ie3.datamodel.io.factory.input.TypedConnectorInputEntityData
import edu.ie3.datamodel.io.source.RawGridSource
import edu.ie3.datamodel.io.source.TypeSource
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.datamodel.models.input.connector.Transformer3WInput
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.GridTestData as rgtd
import spock.lang.Shared
import spock.lang.Specification

import java.util.stream.Collectors
import java.util.stream.Stream

class CsvRawGridSourceTest extends Specification implements CsvTestDataMeta {
  @Shared
  RawGridSource source

  def setupSpec() {
    TypeSource typeSource = new TypeSource(new CsvDataSource(csvSep, typeFolderPath, fileNamingStrategy))
    source = new RawGridSource(typeSource, new CsvDataSource(csvSep, gridDefaultFolderPath, fileNamingStrategy))
  }

  def "The CsvRawGridSource is able to convert single valid AssetInputEntityData to ConnectorInputEntityData"() {
    given: "valid input data"
    def fieldsToAttributes = [
      "uuid"			: "5dc88077-aeb6-4711-9142-db57287640b1",
      "id"			    : "test_switch_AtoB",
      "operator"		: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"	: "2020-03-24 15:11:31",
      "operatesUntil"	: "2020-03-24 15:11:31",
      "nodeA"			: "4ca90220-74c2-4369-9afa-a18bf068840d",
      "nodeB"			: "47d29df0-ba2d-4d23-8e75-c82229c5c758",
      "closed"		    : "true"
    ]

    def expectedFieldsToAttributes = [
      "uuid"			: "5dc88077-aeb6-4711-9142-db57287640b1",
      "id"			    : "test_switch_AtoB",
      "operator"		: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"	: "2020-03-24 15:11:31",
      "operatesUntil"	: "2020-03-24 15:11:31",
      "closed"		    : "true"
    ]

    def validAssetEntityInputData = new AssetInputEntityData(fieldsToAttributes, SwitchInput)

    def nodes = [rgtd.nodeA, rgtd.nodeB]

    when: "the source tries to convert it"
    def connectorDataOption = source.buildUntypedConnectorInputEntityData(validAssetEntityInputData, nodes)

    then: "everything is fine"
    connectorDataOption.success
    connectorDataOption.data.get().with {
      assert fieldsToValues == expectedFieldsToAttributes
      assert targetClass == SwitchInput
      assert nodeA == rgtd.nodeA
      assert nodeB == rgtd.nodeB
    }
  }

  def "The CsvRawGridSource is NOT able to convert single invalid AssetInputEntityData to ConnectorInputEntityData"() {
    given: "invalid input data"
    def fieldsToAttributes = [
      "uuid"			: "5dc88077-aeb6-4711-9142-db57287640b1",
      "id"			: "test_switch_AtoB",
      "operator"		: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"	: "2020-03-24 15:11:31",
      "operatesUntil"	: "2020-03-24 15:11:31",
      "nodeA"			: "4ca90220-74c2-4369-9afa-a18bf068840d",
      "nodeB"			: "620d35fc-34f8-48af-8020-3897fe75add7",
      "closed"		: "true"
    ]

    def validAssetEntityInputData = new AssetInputEntityData(fieldsToAttributes, SwitchInput)

    def nodes = [rgtd.nodeA, rgtd.nodeB]

    when: "the source tries to convert it"
    def connectorDataOption = source.buildUntypedConnectorInputEntityData(validAssetEntityInputData, nodes)

    then: "it returns en empty Optional"
    connectorDataOption.failure
  }


  def "The CsvRawGridSource is able to convert a stream of valid AssetInputEntityData to ConnectorInputEntityData"() {
    given: "valid input data"
    def validStream = Stream.of(
    new AssetInputEntityData([
      "uuid"			: "5dc88077-aeb6-4711-9142-db57287640b1",
      "id"			: "test_switch_AtoB",
      "operator"		: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"	: "2020-03-24 15:11:31",
      "operatesUntil"	: "2020-03-24 15:11:31",
      "nodeA"			: "4ca90220-74c2-4369-9afa-a18bf068840d",
      "nodeB"			: "47d29df0-ba2d-4d23-8e75-c82229c5c758",
      "closed"		: "true"
    ], SwitchInput),
    new AssetInputEntityData([
      "uuid"				: "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"				: "test_lineCtoD",
      "operator"			: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"		: "2020-03-24 15:11:31",
      "operatesUntil"		: "2020-03-24 15:11:31",
      "nodeA"				: "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2",
      "nodeB"				: "6e0980e0-10f2-4e18-862b-eb2b7c90509b",
      "parallelDevices"	: "2",
      "type"				: "3bed3eb3-9790-4874-89b5-a5434d408088",
      "length"			: "0.003",
      "geoPosition"		: "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "olmCharacteristic"	: "olm:{(0.0,1.0)}"
    ],
    LineInput)
    )

    def expectedSet = [
      new ConnectorInputEntityData([
        "uuid"			: "5dc88077-aeb6-4711-9142-db57287640b1",
        "id"			: "test_switch_AtoB",
        "operator"		: "8f9682df-0744-4b58-a122-f0dc730f6510",
        "operatesFrom"	: "2020-03-24 15:11:31",
        "operatesUntil"	: "2020-03-24 15:11:31",
        "closed"		: "true"
      ],
      SwitchInput,
      rgtd.nodeA,
      rgtd.nodeB
      ),
      new ConnectorInputEntityData([
        "uuid"				: "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
        "id"				: "test_lineCtoD",
        "operator"			: "8f9682df-0744-4b58-a122-f0dc730f6510",
        "operatesFrom"		: "2020-03-24 15:11:31",
        "operatesUntil"		: "2020-03-24 15:11:31",
        "parallelDevices"	: "2",
        "type"				: "3bed3eb3-9790-4874-89b5-a5434d408088",
        "length"			: "0.003",
        "geoPosition"		: "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
        "olmCharacteristic"	: "olm:{(0.0,1.0)}"
      ],
      LineInput,
      rgtd.nodeC,
      rgtd.nodeD
      )
    ] as Set

    def nodes = [
      rgtd.nodeA,
      rgtd.nodeB,
      rgtd.nodeC,
      rgtd.nodeD
    ]

    when: "the source tries to convert it"
    def actualSet = source.buildUntypedConnectorInputEntityData(validStream, nodes).collect(Collectors.toSet())

    then: "everything is fine"
    actualSet.size() == expectedSet.size()
    actualSet.every {
      it.success
    }

    actualSet.stream().map { it.data.get() }.toList().containsAll(expectedSet)
  }

  def "The CsvRawGridSource is able to add a type to untyped ConnectorInputEntityData correctly"() {
    given: "valid input data"
    def validConnectorEntityData = new ConnectorInputEntityData([
      "uuid"             	: "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"               	: "test_lineCtoD",
      "operator"         	: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"     	: "2020-03-24 15:11:31",
      "operatesUntil"		: "2020-03-24 15:11:31",
      "parallelDevices"  	: "2",
      "type"         		: "3bed3eb3-9790-4874-89b5-a5434d408088",
      "length"           	: "0.003",
      "geoPosition"      	: "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "olmCharacteristic"	: "olm:{(0.0,1.0)}"
    ],
    LineInput,
    rgtd.nodeC,
    rgtd.nodeD
    )

    def expectedTypedEntityData = new TypedConnectorInputEntityData([
      "uuid"             	: "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"               	: "test_lineCtoD",
      "operator"         	: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"     	: "2020-03-24 15:11:31",
      "operatesUntil"		: "2020-03-24 15:11:31",
      "parallelDevices"  	: "2",
      "length"           	: "0.003",
      "geoPosition"      	: "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "olmCharacteristic"	: "olm:{(0.0,1.0)}"
    ],
    LineInput,
    rgtd.nodeC,
    rgtd.nodeD,
    rgtd.lineTypeInputCtoD
    )

    when: "the source tries to convert it"
    def actual = source.addTypeToEntityData(validConnectorEntityData, rgtd.lineTypeInputCtoD)

    then: "everything is fine"
    actual == expectedTypedEntityData
  }

  def "The CsvRawGridSource is able to find and add a type to untyped ConnectorInputEntityData correctly"() {
    given: "valid input data"
    def validConnectorEntityData = new ConnectorInputEntityData([
      "uuid"             	: "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"               	: "test_lineCtoD",
      "operator"         	: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"     	: "2020-03-24 15:11:31",
      "operatesUntil"		: "2020-03-24 15:11:31",
      "parallelDevices"  	: "2",
      "type"             	: "3bed3eb3-9790-4874-89b5-a5434d408088",
      "length"           	: "0.003",
      "geoPosition"      	: "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "olmCharacteristic"	: "olm:{(0.0,1.0)}"
    ],
    LineInput,
    rgtd.nodeC,
    rgtd.nodeD
    )

    def expectedTypedEntityData = new TypedConnectorInputEntityData([
      "uuid"             	: "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"               	: "test_lineCtoD",
      "operator"         	: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"     	: "2020-03-24 15:11:31",
      "operatesUntil"		: "2020-03-24 15:11:31",
      "parallelDevices"  	: "2",
      "length"           	: "0.003",
      "geoPosition"      	: "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "olmCharacteristic"	: "olm:{(0.0,1.0)}"
    ],
    LineInput,
    rgtd.nodeC,
    rgtd.nodeD,
    rgtd.lineTypeInputCtoD
    )

    def availableTypes = [rgtd.lineTypeInputCtoD]

    when: "the source tries to convert it"
    def actual = source.findAndAddType(validConnectorEntityData, availableTypes)

    then: "everything is fine"
    actual.success
    actual.data.get() == expectedTypedEntityData
  }

  def "The CsvRawGridSource is able to identify ConnectorInputEntityData data with non matching type requirements correctly"() {
    given: "valid input data"
    def validConnectorEntityData = new ConnectorInputEntityData([
      "uuid"             	: "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"               	: "test_lineCtoD",
      "operator"         	: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"     	: "2020-03-24 15:11:31",
      "operatesUntil"		: "2020-03-24 15:11:31",
      "parallelDevices"  	: "2",
      "type"             	: "fd5b128d-ed35-4355-94b6-7518c55425fe",
      "length"           	: "0.003",
      "geoPosition"      	: "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "olmCharacteristic"	: "olm:{(0.0,1.0)}"
    ],
    LineInput,
    rgtd.nodeC,
    rgtd.nodeD
    )

    def availableTypes = [rgtd.lineTypeInputCtoD]

    when: "the source tries to convert it"
    def actual = source.findAndAddType(validConnectorEntityData, availableTypes)

    then: "everything is fine"
    actual.failure
  }

  def "The CsvRawGridSource is able to convert a stream of valid ConnectorInputEntityData to TypedConnectorInputEntityData"() {
    given: "valid input data"
    def validStream = Stream.of(new Try.Success<>(
    new ConnectorInputEntityData([
      "uuid"             	: "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"               	: "test_lineCtoD",
      "operator"         	: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"     	: "2020-03-24 15:11:31",
      "operatesUntil"		: "2020-03-24 15:11:31",
      "parallelDevices"  	: "2",
      "type"             	: "3bed3eb3-9790-4874-89b5-a5434d408088",
      "length"           	: "0.003",
      "geoPosition"      	: "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "olmCharacteristic"	: "olm:{(0.0,1.0)}"
    ],
    LineInput,
    rgtd.nodeC,
    rgtd.nodeD
    )),
    new Try.Success<>(new ConnectorInputEntityData([
      "uuid"             	: "92ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"               	: "test_line_AtoB",
      "operator"         	: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"     	: "2020-03-24 15:11:31",
      "operatesUntil"		: "2020-03-24 15:11:31",
      "parallelDevices"  	: "2",
      "type"             	: "3bed3eb3-9790-4874-89b5-a5434d408088",
      "length"           	: "0.003",
      "geoPosition"      	: "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "olmCharacteristic"	: "olm:{(0.0,1.0)}"
    ], LineInput,
    rgtd.nodeA,
    rgtd.nodeB
    ))) as Stream<Try<ConnectorInputEntityData, Exception>>

    def expectedSet = [
      new TypedConnectorInputEntityData<>([
        "uuid"             	: "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
        "id"               	: "test_lineCtoD",
        "operator"         	: "8f9682df-0744-4b58-a122-f0dc730f6510",
        "operatesFrom"     	: "2020-03-24 15:11:31",
        "operatesUntil"		: "2020-03-24 15:11:31",
        "parallelDevices"  	: "2",
        "length"           	: "0.003",
        "geoPosition"      	: "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
        "olmCharacteristic"	: "olm:{(0.0,1.0)}"
      ],
      LineInput,
      rgtd.nodeC,
      rgtd.nodeD,
      rgtd.lineTypeInputCtoD
      ),
      new TypedConnectorInputEntityData<>([
        "uuid"             	: "92ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
        "id"               	: "test_line_AtoB",
        "operator"         	: "8f9682df-0744-4b58-a122-f0dc730f6510",
        "operatesFrom"     	: "2020-03-24 15:11:31",
        "operatesUntil"		: "2020-03-24 15:11:31",
        "parallelDevices"  	: "2",
        "length"           	: "0.003",
        "geoPosition"      	: "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
        "olmCharacteristic"	: "olm:{(0.0,1.0)}"
      ], LineInput,
      rgtd.nodeA,
      rgtd.nodeB,
      rgtd.lineTypeInputCtoD
      )
    ]

    def availableTypes = [rgtd.lineTypeInputCtoD]

    when: "the source tries to convert it"
    def actualSet = source.buildTypedConnectorEntityData(validStream, availableTypes).collect(Collectors.toSet())

    then: "everything is fine"
    actualSet.size() == expectedSet.size()
    actualSet.every {
      it.success
    }
    actualSet.stream().map {
      it.data.get()
    }.toList().containsAll(expectedSet)
  }

  def "The CsvRawGridSource is able to add the third node for a three winding transformer correctly"() {
    given: "valid input data"
    def typedEntityData = new TypedConnectorInputEntityData([
      "uuid"				: "cc327469-7d56-472b-a0df-edbb64f90e8f",
      "id"				: "3w_test",
      "operator"			: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"		: "2020-03-24 15:11:31",
      "operatesUntil"		: "2020-03-24 15:11:31",
      "nodeC"				: "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2",
      "parallelDevices"	: "1",
      "tapPos"			: "0",
      "autoTap"			: "true"
    ],
    Transformer3WInput,
    rgtd.nodeA,
    rgtd.nodeB,
    rgtd.transformerTypeAtoBtoC)

    def expected = new Transformer3WInputEntityData([
      "uuid"				: "cc327469-7d56-472b-a0df-edbb64f90e8f",
      "id"				: "3w_test",
      "operator"			: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"		: "2020-03-24 15:11:31",
      "operatesUntil"		: "2020-03-24 15:11:31",
      "parallelDevices"	: "1",
      "tapPos"			: "0",
      "autoTap"			: "true"
    ],
    Transformer3WInput,
    rgtd.nodeA,
    rgtd.nodeB,
    rgtd.nodeC,
    rgtd.transformerTypeAtoBtoC)

    def availableNodes = [
      rgtd.nodeA,
      rgtd.nodeB,
      rgtd.nodeC
    ]

    when: "the sources tries to add the node"
    def actual = source.addThirdNode(typedEntityData, availableNodes)

    then: "everything is fine"
    actual.success
    actual.data.get() == expected
  }

  def "The CsvRawGridSource is NOT able to add the third node for a three winding transformer, if it is not available"() {
    given: "valid input data"
    def typedEntityData = new TypedConnectorInputEntityData([
      "uuid"				: "cc327469-7d56-472b-a0df-edbb64f90e8f",
      "id"				: "3w_test",
      "operator"			: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"		: "2020-03-24 15:11:31",
      "operatesUntil"		: "2020-03-24 15:11:31",
      "nodeC"				: "bd8927b4-0ca9-4dd3-b645-468e6e433160",
      "parallelDevices"	: "1",
      "tapPos"			: "0",
      "autoTap"			: "true"
    ],
    Transformer3WInput,
    rgtd.nodeA,
    rgtd.nodeB,
    rgtd.transformerTypeAtoBtoC)

    def availableNodes = [
      rgtd.nodeA,
      rgtd.nodeB,
      rgtd.nodeC
    ]

    when: "the sources tries to add the node"
    def actual = source.addThirdNode(typedEntityData, availableNodes)

    then: "everything is fine"
    actual.failure
  }

  def "The CsvRawGridSource is able to add the third node for a three winding transformer to a stream of candidates"() {
    given: "suitable input data"
    def inputStream = Stream.of(Try.of(() -> new TypedConnectorInputEntityData([
      "uuid"				: "cc327469-7d56-472b-a0df-edbb64f90e8f",
      "id"				: "3w_test",
      "operator"			: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"		: "2020-03-24 15:11:31",
      "operatesUntil"		: "2020-03-24 15:11:31",
      "nodeC"				: "bd837a25-58f3-44ac-aa90-c6b6e3cd91b2",
      "parallelDevices"	: "1",
      "tapPos"			: "0",
      "autoTap"			: "true"
    ],
    Transformer3WInput,
    rgtd.nodeA,
    rgtd.nodeB,
    rgtd.transformerTypeAtoBtoC), SourceException),
    Try.of(() -> new TypedConnectorInputEntityData([
      "uuid"				: "cc327469-7d56-472b-a0df-edbb64f90e8f",
      "id"				: "3w_test",
      "operator"			: "8f9682df-0744-4b58-a122-f0dc730f6510",
      "operatesFrom"		: "2020-03-24 15:11:31",
      "operatesUntil"		: "2020-03-24 15:11:31",
      "nodeC"				: "bd8927b4-0ca9-4dd3-b645-468e6e433160",
      "parallelDevices"	: "1",
      "tapPos"			: "0",
      "autoTap"			: "true"
    ],
    Transformer3WInput,
    rgtd.nodeA,
    rgtd.nodeB,
    rgtd.transformerTypeAtoBtoC), SourceException))

    def availableNodes = [
      rgtd.nodeA,
      rgtd.nodeB,
      rgtd.nodeC
    ]

    def expectedSet = [
      new Transformer3WInputEntityData([
        "uuid"				: "cc327469-7d56-472b-a0df-edbb64f90e8f",
        "id"				: "3w_test",
        "operator"			: "8f9682df-0744-4b58-a122-f0dc730f6510",
        "operatesFrom"		: "2020-03-24 15:11:31",
        "operatesUntil"		: "2020-03-24 15:11:31",
        "parallelDevices"	: "1",
        "tapPos"			: "0",
        "autoTap"			: "true"
      ],
      Transformer3WInput,
      rgtd.nodeA,
      rgtd.nodeB,
      rgtd.nodeC,
      rgtd.transformerTypeAtoBtoC),
      null
    ]

    when: "the sources tries to add nodes"
    def actualSet = source.buildTransformer3WEntityData(inputStream, availableNodes).collect(Collectors.toSet())

    then: "everything is fine"
    actualSet.size() == expectedSet.size()
    actualSet.stream().map {
      it.data.get()
    }.toList().containsAll(expectedSet)
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

    actualSet.each {actual ->
      def expected = expectedSet.find {it.uuid == actual.uuid}
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
      /* It's okay, to only test the uuids, because content is tested with the other test mehtods */
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
}