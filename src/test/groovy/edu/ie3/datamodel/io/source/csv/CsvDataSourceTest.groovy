/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source.csv

import edu.ie3.datamodel.io.FileNamingStrategy
import edu.ie3.datamodel.models.UniqueEntity
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.test.common.SystemParticipantTestData as sptd
import edu.ie3.test.common.GridTestData as gtd
import spock.lang.Shared
import spock.lang.Specification

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.LongAdder
import java.util.stream.Collectors


class CsvDataSourceTest extends Specification {

	// Using a groovy bug to gain access to private methods in superclass:
	// by default, we cannot access private methods with parameters from abstract parent classes, introducing a
	// class that extends the abstract parent class and unveils the private methods by calling the parents private
	// methods in a public or protected method makes them available for testing
	private final class DummyCsvSource extends CsvDataSource {

		DummyCsvSource(String csvSep, String folderPath, FileNamingStrategy fileNamingStrategy) {
			super(csvSep, folderPath, fileNamingStrategy)
		}

		Map<String, String> buildFieldsToAttributes(
				final String csvRow, final String[] headline) {
			return super.buildFieldsToAttributes(csvRow, headline)
		}

		OperatorInput getFirstOrDefaultOperator(
				Collection<OperatorInput> operators, String operatorUuid, String entityClassName, String requestEntityUuid) {
			return super.getFirstOrDefaultOperator(operators, operatorUuid, entityClassName, requestEntityUuid)
		}

		def <T extends UniqueEntity> Set<Map<String, String>> distinctRowsWithLog(
				Class<T> entityClass, Collection<Map<String, String>> allRows) {
			return super.distinctRowsWithLog(entityClass, allRows)
		}

		String[] fieldVals(
				String csvSep, String csvRow) {
			return super.fieldVals(csvSep, csvRow)
		}
	}

	@Shared
	String csvSep = ","
	String testBaseFolderPath = new File(getClass().getResource('/testGridFiles').toURI()).getAbsolutePath()
	FileNamingStrategy fileNamingStrategy = new FileNamingStrategy()

	DummyCsvSource dummyCsvSource = new DummyCsvSource(csvSep, testBaseFolderPath, fileNamingStrategy)

	def "A DataSource should contain a valid connector after initialization"() {
		expect:
		dummyCsvSource.connector != null
		dummyCsvSource.connector.baseFolderName == testBaseFolderPath
		dummyCsvSource.connector.fileNamingStrategy == fileNamingStrategy
		dummyCsvSource.connector.entityWriters.isEmpty()

	}

	def "A CsvDataSource should build a valid fields to attributes map with valid data as expected"() {
		given:
		def validHeadline = [
			"uuid",
			"active_power_gradient",
			"capex",
			"cosphi_rated",
			"eta_conv",
			"id",
			"opex",
			"s_rated",
			"olmcharacteristic",
			"cosPhiFixed"] as String[]
		def validCsvRow = "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8,25.0,100.0,0.95,98.0,test_bmTypeInput,50.0,25.0,olm:{(0.0,1.0)},cosPhiFixed:{(0.0,1.0)}"

		expect:
		dummyCsvSource.buildFieldsToAttributes(validCsvRow, validHeadline) == [
			activePowerGradient: "25.0",
			capex              : "100.0",
			cosphiRated        : "0.95",
			etaConv            : "98.0",
			id                 : "test_bmTypeInput",
			opex               : "50.0",
			sRated             : "25.0",
			uuid               : "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8",
			olmcharacteristic  : "olm:{(0.0,1.0)}",
			cosPhiFixed        : "cosPhiFixed:{(0.0,1.0)}"
		]

	}

	def "A CsvDataSource should be able to handle a variety of different csvRows correctly"() {
		expect:
		dummyCsvSource.fieldVals(csvSep, csvRow) as List == resultingArray

		where:
		csvSep | csvRow                                                                                                                                                                                                                                                                                                                                                                                                              || resultingArray
		","    | "4ca90220-74c2-4369-9afa-a18bf068840d,{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}},node_a,2020-03-25T15:11:31Z[UTC],2020-03-24T15:11:31Z[UTC],8f9682df-0744-4b58-a122-f0dc730f6510,true,1,1.0,Höchstspannung,380.0,olm:{(0.00,1.00)},cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}"                                                     || [
			"4ca90220-74c2-4369-9afa-a18bf068840d",
			"{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"node_a",
			"2020-03-25T15:11:31Z[UTC]",
			"2020-03-24T15:11:31Z[UTC]",
			"8f9682df-0744-4b58-a122-f0dc730f6510",
			"true",
			"1",
			"1.0",
			"Höchstspannung",
			"380.0",
			"olm:{(0.00,1.00)}",
			"cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}"
		]
		","    | "\"4ca90220-74c2-4369-9afa-a18bf068840d\",\"{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}\",\"node_a\",\"2020-03-25T15:11:31Z[UTC]\",\"2020-03-24T15:11:31Z[UTC]\",\"8f9682df-0744-4b58-a122-f0dc730f6510\",\"true\",\"1\",\"1.0\",\"Höchstspannung\",\"380.0\",\"olm:{(0.00,1.00)}\",\"cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}\"" || [
			"4ca90220-74c2-4369-9afa-a18bf068840d",
			"{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"node_a",
			"2020-03-25T15:11:31Z[UTC]",
			"2020-03-24T15:11:31Z[UTC]",
			"8f9682df-0744-4b58-a122-f0dc730f6510",
			"true",
			"1",
			"1.0",
			"Höchstspannung",
			"380.0",
			"olm:{(0.00,1.00)}",
			"cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}"
		]
		";"    | "4ca90220-74c2-4369-9afa-a18bf068840d;cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)};{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}};node_a;2020-03-25T15:11:31Z[UTC];2020-03-24T15:11:31Z[UTC];8f9682df-0744-4b58-a122-f0dc730f6510;true;1;1.0;Höchstspannung;380.0;olm:{(0.00,1.00)};cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}"            || [
			"4ca90220-74c2-4369-9afa-a18bf068840d",
			"cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}",
			"{(0.0,1.0),(0.9,1.0),(1.2,-0.3)};{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"node_a",
			"2020-03-25T15:11:31Z[UTC]",
			"2020-03-24T15:11:31Z[UTC]",
			"8f9682df-0744-4b58-a122-f0dc730f6510",
			"true",
			"1",
			"1.0",
			"Höchstspannung",
			"380.0",
			"olm:{(0.00,1.00)}",
			"cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}"
		]
		";"    | "\"4ca90220-74c2-4369-9afa-a18bf068840d\";\"{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}\";\"node_a\";\"2020-03-25T15:11:31Z[UTC]\";\"2020-03-24T15:11:31Z[UTC]\";\"8f9682df-0744-4b58-a122-f0dc730f6510\";\"true\";\"1\";\"1.0\";\"Höchstspannung\";\"380.0\";\"olm:{(0.00,1.00)}\";\"cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}\"" || [
			"4ca90220-74c2-4369-9afa-a18bf068840d",
			"{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"node_a",
			"2020-03-25T15:11:31Z[UTC]",
			"2020-03-24T15:11:31Z[UTC]",
			"8f9682df-0744-4b58-a122-f0dc730f6510",
			"true",
			"1",
			"1.0",
			"Höchstspannung",
			"380.0",
			"olm:{(0.00,1.00)}",
			"cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}"
		]
		","    | "66275bfd-978b-4974-9f73-f270165a6351,Standard,f18a5a9b-6d45-4843-be12-be6d12de0e6b,{\"type\":\"LineString\",\"coordinates\":[[7.4116482,51.4843281],[7.4116482,51.4843281]],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}},{\"type\":\"Point\",\"coordinates\":[0.25423729,0.75409836],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:0\"}}}\""                                  || [
			"66275bfd-978b-4974-9f73-f270165a6351",
			"Standard",
			"f18a5a9b-6d45-4843-be12-be6d12de0e6b",
			"{\"type\":\"LineString\",\"coordinates\":[[7.4116482,51.4843281],[7.4116482,51.4843281]],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"{\"type\":\"Point\",\"coordinates\":[0.25423729,0.75409836],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:0\"}}}"
		]
		","    | "4ca90220-74c2-4369-9afa-a18bf068840d,{\"\"type\"\":\"\"Point\"\",\"\"coordinates\"\":[7.411111,51.492528],\"\"crs\"\":{\"\"type\"\":\"\"name\"\",\"\"properties\"\":{\"\"name\"\":\"\"EPSG:4326\"\"}}},node_a,2020-03-25T15:11:31Z[UTC],2020-03-24T15:11:31Z[UTC],8f9682df-0744-4b58-a122-f0dc730f6510,true,1,1.0,Höchstspannung,380.0,\"olm:{(0.00,1.00)}\",\"cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}\""                                                     || [
			"4ca90220-74c2-4369-9afa-a18bf068840d",
			"{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"node_a",
			"2020-03-25T15:11:31Z[UTC]",
			"2020-03-24T15:11:31Z[UTC]",
			"8f9682df-0744-4b58-a122-f0dc730f6510",
			"true",
			"1",
			"1.0",
			"Höchstspannung",
			"380.0",
			"olm:{(0.00,1.00)}",
			"cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}"
		]
	}


	def "A CsvDataSource should build a valid fields to attributes map with valid data and empty value fields as expected"() {
		given:
		def validHeadline = [
			"uuid",
			"active_power_gradient",
			"capex",
			"cosphi_rated",
			"eta_conv",
			"id",
			"opex",
			"s_rated",
			"olmcharacteristic",
			"cosPhiFixed"] as String[]
		def validCsvRow = "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8,25.0,100.0,0.95,98.0,test_bmTypeInput,50.0,25.0,\"olm:{(0.0,1.0)}\","

		expect:
		dummyCsvSource.buildFieldsToAttributes(validCsvRow, validHeadline) == [
			activePowerGradient: "25.0",
			capex              : "100.0",
			cosphiRated        : "0.95",
			etaConv            : "98.0",
			id                 : "test_bmTypeInput",
			opex               : "50.0",
			sRated             : "25.0",
			uuid               : "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8",
			olmcharacteristic  : "olm:{(0.0,1.0)}",
			cosPhiFixed        : ""
		]

	}

	def "A CsvDataSource should be able to handle several errors when the csvRow is invalid or cannot be processed"() {
		given:
		def validHeadline = [
			"uuid",
			"active_power_gradient",
			"capex",
			"cosphi_rated",
			"eta_conv",
			"id",
			"opex",
			"s_rated"] as String[]

		expect:
		dummyCsvSource.buildFieldsToAttributes(invalidCsvRow, validHeadline) == [:]

		where:
		invalidCsvRow                                                                          || explaination
		"5ebd8f7e-dedb-4017-bb86-6373c4b68eb8;25.0;100.0;0.95;98.0;test_bmTypeInput;50.0;25.0" || "wrong separator"
		"5ebd8f7e-dedb-4017-bb86-6373c4b68eb8,25.0,100.0,0.95,98.0,test_bmTypeInput"           || "too less columns"
		"5ebd8f7e-dedb-4017-bb86-6373c4b68eb8,25.0,100.0,0.95,98.0,test_bmTypeInput,,,,"       || "too much columns"

	}

	def "A CsvDataSource should always return an operator. Either the found one (if any) or OperatorInput.NO_OPERATOR_ASSIGNED"() {

		expect:
		dummyCsvSource.getFirstOrDefaultOperator(operators, operatorUuid, entityClassName, requestEntityUuid) == expectedOperator

		where:
		operatorUuid                           | operators      | entityClassName | requestEntityUuid         || expectedOperator
		"8f9682df-0744-4b58-a122-f0dc730f6510" | [sptd.hpInput.operator]| "TestEntityClass" | "8f9682df-0744-4b58-a122-f0dc730f6511" || sptd.hpInput.operator
		"8f9682df-0744-4b58-a122-f0dc730f6520" | [sptd.hpInput.operator]| "TestEntityClass" | "8f9682df-0744-4b58-a122-f0dc730f6511" || OperatorInput.NO_OPERATOR_ASSIGNED
		"8f9682df-0744-4b58-a122-f0dc730f6510" | []| "TestEntityClass"|"8f9682df-0744-4b58-a122-f0dc730f6511" || OperatorInput.NO_OPERATOR_ASSIGNED

	}

	def "A CsvDataSource should collect be able to collect empty optionals when asked to do so"() {

		given:
		ConcurrentHashMap<Class<? extends UniqueEntity>, LongAdder> emptyCollector = new ConcurrentHashMap<>()
		def nodeInputOptionals = [
			Optional.of(sptd.hpInput.node),
			Optional.empty(),
			Optional.of(sptd.chpInput.node)
		]

		when:
		def resultingList = nodeInputOptionals.stream().filter(dummyCsvSource.isPresentCollectIfNot(NodeInput, emptyCollector)).collect(Collectors.toList())

		then:
		emptyCollector.size() == 1
		emptyCollector.get(NodeInput).toInteger() == 1

		resultingList.size() == 2
		resultingList.get(0) == Optional.of(sptd.hpInput.node)
		resultingList.get(1) == Optional.of(sptd.chpInput.node)
	}

	def "A CsvDataSource should return a given collection of csv row mappings as distinct rows collection correctly"() {

		given:
		def nodeInputRow = [
			"uuid"          : "4ca90220-74c2-4369-9afa-a18bf068840d",
			"geo_position"  : "{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"id"            : "node_a",
			"operates_until": "2020-03-25T15:11:31Z[UTC]",
			"operates_from" : "2020-03-24T15:11:31Z[UTC]",
			"operator"      : "8f9682df-0744-4b58-a122-f0dc730f6510",
			"slack"         : "true",
			"subnet"        : "1",
			"v_target"      : "1.0",
			"volt_lvl"      : "Höchstspannung",
			"v_rated"       : "380"
		]

		when:
		def allRows = [nodeInputRow]* noOfEntities
		def distinctRows = dummyCsvSource.distinctRowsWithLog(NodeInput, allRows)

		then:
		distinctRows.size() == distinctSize
		distinctRows[0] == firstElement

		where:
		noOfEntities || distinctSize || firstElement
		0            || 0            || null
		10           || 1            || ["uuid"          : "4ca90220-74c2-4369-9afa-a18bf068840d",
			"geo_position"  : "{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"id"            : "node_a",
			"operates_until": "2020-03-25T15:11:31Z[UTC]",
			"operates_from" : "2020-03-24T15:11:31Z[UTC]",
			"operator"      : "8f9682df-0744-4b58-a122-f0dc730f6510",
			"slack"         : "true",
			"subnet"        : "1",
			"v_target"      : "1.0",
			"volt_lvl"      : "Höchstspannung",
			"v_rated"       : "380"]

	}

	def "A CsvDataSource should return an empty set of csv row mappings if the provided collection of mappings contains duplicated UUIDs with different data"() {

		given:
		def nodeInputRow1 = [
			"uuid"          : "4ca90220-74c2-4369-9afa-a18bf068840d",
			"geo_position"  : "{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"id"            : "node_a",
			"operates_until": "2020-03-25T15:11:31Z[UTC]",
			"operates_from" : "2020-03-24T15:11:31Z[UTC]",
			"operator"      : "8f9682df-0744-4b58-a122-f0dc730f6510",
			"slack"         : "true",
			"subnet"        : "1",
			"v_target"      : "1.0",
			"volt_lvl"      : "Höchstspannung",
			"v_rated"       : "380"
		]
		def nodeInputRow2 = [
			"uuid"          : "4ca90220-74c2-4369-9afa-a18bf068840d",
			"geo_position"  : "{\"type\":\"Point\",\"coordinates\":[7.411111,51.492528],\"crs\":{\"type\":\"name\",\"properties\":{\"name\":\"EPSG:4326\"}}}",
			"id"            : "node_b",
			"operates_until": "2020-03-25T15:11:31Z[UTC]",
			"operates_from" : "2020-03-24T15:11:31Z[UTC]",
			"operator"      : "8f9682df-0744-4b58-a122-f0dc730f6510",
			"slack"         : "true",
			"subnet"        : "1",
			"v_target"      : "1.0",
			"volt_lvl"      : "Höchstspannung",
			"v_rated"       : "380"
		]

		when:
		def allRows = [nodeInputRow1, nodeInputRow2]*10
		def distinctRows = dummyCsvSource.distinctRowsWithLog(NodeInput, allRows)

		then:
		distinctRows.size() == 0
	}

	def "A CsvDataSource should be able to handle the extraction process of an asset type correctly"() {

		when:
		def assetTypeOpt = dummyCsvSource.getAssetType(types, fieldsToAttributes, "TestClassName")

		then:
		assetTypeOpt.present == resultIsPresent
		assetTypeOpt.ifPresent({ assetType ->
			assert (assetType == resultData)
		})

		where:
		types                     | fieldsToAttributes                               || resultIsPresent || resultData
		[]| ["type": "202069a7-bcf8-422c-837c-273575220c8a"] || false           || null
		[]| ["bla": "foo"]                                   || false           || null
		[gtd.transformerTypeBtoD]| ["type": "202069a7-bcf8-422c-837c-273575220c8a"] || true            || gtd.transformerTypeBtoD
		[sptd.chpTypeInput]| ["type": "5ebd8f7e-dedb-4017-bb86-6373c4b68eb8"] || true            || sptd.chpTypeInput
	}

}
