/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.factory.input.AssetInputEntityData
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.test.common.GridTestData as rgtd

import spock.lang.Shared
import spock.lang.Specification

class CsvRawGridSourceTest extends Specification implements CsvTestDataMeta {
	@Shared
	CsvRawGridSource source = new CsvRawGridSource(csvSep, gridFolderPath, fileNamingStrategy, Mock(CsvTypeSource))

	def "The CsvRawGridSource is able to convert single valid AssetInputEntityData to ConnectorInputEntityData"() {
		given: "valid input data"
		def fieldsToAttributes = [
			"uuid"			: "5dc88077-aeb6-4711-9142-db57287640b1",
			"id"			: "test_switch_AtoB",
			"operator"		: "8f9682df-0744-4b58-a122-f0dc730f6510",
			"operationTime"	: "2020-03-24 15:11:31",
			"nodeA"			: "4ca90220-74c2-4369-9afa-a18bf068840d",
			"nodeB"			: "47d29df0-ba2d-4d23-8e75-c82229c5c758",
			"closed"		: "true"
		]

		def expectedFieldsToAttributes = [
			"uuid"			: "5dc88077-aeb6-4711-9142-db57287640b1",
			"id"			: "test_switch_AtoB",
			"operator"		: "8f9682df-0744-4b58-a122-f0dc730f6510",
			"operationTime"	: "2020-03-24 15:11:31",
			"closed"		: "true"
		]

		def validAssetEntityInputData = new AssetInputEntityData(fieldsToAttributes, SwitchInput.class)

		def nodes = [rgtd.nodeA, rgtd.nodeB]

		when: "the source tries to convert it"
		def connectorDataOption = source.buildUntypedConnectorInputEntityData(validAssetEntityInputData, nodes)

		then: "everything is fine"
		connectorDataOption.isPresent()
		connectorDataOption.get().with {
			assert fieldsToValues == expectedFieldsToAttributes
			assert entityClass == SwitchInput.class
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
			"operationTime"	: "2020-03-24 15:11:31",
			"nodeA"			: "4ca90220-74c2-4369-9afa-a18bf068840d",
			"nodeB"			: "620d35fc-34f8-48af-8020-3897fe75add7",
			"closed"		: "true"
		]

		def validAssetEntityInputData = new AssetInputEntityData(fieldsToAttributes, SwitchInput.class)

		def nodes = [rgtd.nodeA, rgtd.nodeB]

		when: "the source tries to convert it"
		def connectorDataOption = source.buildUntypedConnectorInputEntityData(validAssetEntityInputData, nodes)

		then: "it returns en empty Optional"
		!connectorDataOption.isPresent()
	}
}
