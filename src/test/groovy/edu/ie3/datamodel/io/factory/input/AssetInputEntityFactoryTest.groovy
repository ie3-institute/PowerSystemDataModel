/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.AssetInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

/**
 * Tests a minimal extension of {@link AssetInputEntityFactory}
 */
class AssetInputEntityFactoryTest extends Specification implements FactoryTestHelper {

	def "An AssetInputFactory should contain exactly the expected class for parsing"() {
		given:
		def inputFactory = new TestAssetInputFactory()
		def expectedClasses = [TestAssetInput]

		expect:
		inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
	}

	def "An AssetInputFactory should parse a valid operated AssetInput correctly (no operation time provided)"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new TestAssetInputFactory()
		Map<String, String> parameter = [
			"uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"id"  : "TestID"
		]
		def inputClass = TestAssetInput
		def operatorInput = Mock(OperatorInput)

		when:
		Optional<TestAssetInput> input = inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass, operatorInput))

		then:
		input.present
		input.get().getClass() == inputClass
		((TestAssetInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert operationTime == OperationTime.notLimited()
			assert operator == operatorInput
			assert id == parameter["id"]
		}
	}

	def "An AssetInputFactory should parse a valid operated AssetInput correctly (operation start time provided)"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new TestAssetInputFactory()
		Map<String, String> parameter = [
			"uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"operatesfrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
			"operatesuntil": "",
			"id"           : "TestID"
		]
		def inputClass = TestAssetInput
		def operatorInput = Mock(OperatorInput)

		when:
		Optional<TestAssetInput> input = inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass, operatorInput))

		then:
		input.present
		input.get().getClass() == inputClass
		((TestAssetInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert operationTime.startDate.present
			assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
			assert !operationTime.endDate.present
			assert operator == operatorInput
			assert id == parameter["id"]
		}
	}

	def "An AssetInputFactory should parse a valid operated AssetInput correctly (operation end time provided)"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new TestAssetInputFactory()
		Map<String, String> parameter = [
			"uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"operatesuntil": "2019-01-01T00:00:00+01:00[Europe/Berlin]",
			"id"           : "TestID"
		]
		def inputClass = TestAssetInput
		def operatorInput = Mock(OperatorInput)

		when:
		Optional<TestAssetInput> input = inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass, operatorInput))

		then:
		input.present
		input.get().getClass() == inputClass
		((TestAssetInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert !operationTime.startDate.present
			assert operationTime.endDate.present
			assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
			assert operator == operatorInput
			assert id == parameter["id"]
		}
	}

	def "An AssetInputFactory should parse a valid operated AssetInput correctly (operation start and end time provided)"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new TestAssetInputFactory()
		Map<String, String> parameter = [
			"uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"operatesfrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
			"operatesuntil": "2019-12-31T00:00:00+01:00[Europe/Berlin]",
			"id"           : "TestID"
		]
		def inputClass = TestAssetInput
		def operatorInput = Mock(OperatorInput)

		when:
		Optional<TestAssetInput> input = inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass, operatorInput))

		then:
		input.present
		input.get().getClass() == inputClass
		((TestAssetInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert operationTime.startDate.present
			assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
			assert operationTime.endDate.present
			assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
			assert operator == operatorInput
			assert id == parameter["id"]
		}
	}

	def "An AssetInputFactory should parse a valid operated, always on AssetInput correctly (no operation time provided)"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new TestAssetInputFactory()
		Map<String, String> parameter = [
			"uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"id"  : "TestID"
		]
		def inputClass = TestAssetInput

		when:
		Optional<TestAssetInput> input = inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass))

		then:
		input.present
		input.get().getClass() == inputClass
		((TestAssetInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert operationTime == OperationTime.notLimited()
			assert operator == OperatorInput.NO_OPERATOR_ASSIGNED
			assert id == parameter["id"]
		}
	}

	def "An AssetInputFactory should parse a valid AssetInput correctly (operation start time provided)"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new TestAssetInputFactory()
		Map<String, String> parameter = [
			"uuid"        : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"operatesfrom": "2019-01-01T00:00:00+01:00[Europe/Berlin]",
			"id"          : "TestID"
		]
		def inputClass = TestAssetInput

		when:
		Optional<TestAssetInput> input = inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass))

		then:
		input.present
		input.get().getClass() == inputClass
		((TestAssetInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert operationTime.startDate.present
			assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
			assert !operationTime.endDate.present
			assert operator == OperatorInput.NO_OPERATOR_ASSIGNED
			assert id == parameter["id"]
		}
	}

	def "An AssetInputFactory should parse a valid AssetInput correctly (operation end time provided)"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new TestAssetInputFactory()
		Map<String, String> parameter = [
			"uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"operatesuntil": "2019-01-01T00:00:00+01:00[Europe/Berlin]",
			"id"           : "TestID"
		]
		def inputClass = TestAssetInput

		when:
		Optional<TestAssetInput> input = inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass))

		then:
		input.present
		input.get().getClass() == inputClass
		((TestAssetInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert !operationTime.startDate.present
			assert operationTime.endDate.present
			assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
			assert operator == OperatorInput.NO_OPERATOR_ASSIGNED
			assert id == parameter["id"]
		}
	}

	def "An AssetInputFactory should parse a valid AssetInput correctly (operation start and end time provided"() {
		given: "a system participant input type factory and model data"
		def inputFactory = new TestAssetInputFactory()
		Map<String, String> parameter = [
			"uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"operatesfrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
			"operatesuntil": "2019-12-31T00:00:00+01:00[Europe/Berlin]",
			"id"           : "TestID"
		]
		def inputClass = TestAssetInput

		when:
		Optional<TestAssetInput> input = inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass))

		then:
		input.present
		input.get().getClass() == inputClass
		((TestAssetInput) input.get()).with {
			assert uuid == UUID.fromString(parameter["uuid"])
			assert operationTime.startDate.present
			assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
			assert operationTime.endDate.present
			assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
			assert operator == OperatorInput.NO_OPERATOR_ASSIGNED
			assert id == parameter["id"]
		}
	}

	def "An AssetInputFactory should throw an exception on invalid or incomplete data "() {
		given: "a system participant input type factory and model data"
		def inputFactory = new TestAssetInputFactory()
		Map<String, String> parameter = [
			"uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
			"operatesfrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
			"operatesuntil": "2019-12-31T00:00:00+01:00[Europe/Berlin]"
		]
		def inputClass = TestAssetInput

		when:
		inputFactory.getEntity(new AssetInputEntityData(parameter, inputClass))

		then:
		FactoryException ex = thrown()
		ex.message ==
				"The provided fields [operatesfrom, operatesuntil, uuid] with data \n" +
				"{operatesfrom -> 2019-01-01T00:00:00+01:00[Europe/Berlin],\n" +
				"operatesuntil -> 2019-12-31T00:00:00+01:00[Europe/Berlin],\n" +
				"uuid -> 91ec3bcf-1777-4d38-af67-0bf7c9fa73c7} are invalid for instance of TestAssetInput. \n" +
				"The following fields to be passed to a constructor of 'TestAssetInput' are possible (NOT case-sensitive!):\n" +
				"0: [id, uuid]\n" +
				"1: [id, operatesfrom, uuid]\n" +
				"2: [id, operatesuntil, uuid]\n" +
				"3: [id, operatesfrom, operatesuntil, uuid]\n"
	}

	private class TestAssetInput extends AssetInput {
		TestAssetInput(UUID uuid, String id, OperatorInput operator, OperationTime operationTime) {
			super(uuid, id, operator, operationTime)
		}

		@Override
		UniqueEntityBuilder copy() {
			return null // todo JH
		}
	}

	private class TestAssetInputFactory extends AssetInputEntityFactory<TestAssetInput, AssetInputEntityData> {
		TestAssetInputFactory() {
			super(TestAssetInput)
		}

		@Override
		protected String[] getAdditionalFields() {
			return new String[0]
		}

		@Override
		protected TestAssetInput buildModel(AssetInputEntityData data, UUID uuid, String id, OperatorInput operator, OperationTime operationTime) {
			return new TestAssetInput(uuid, id, operator, operationTime)
		}
	}
}
