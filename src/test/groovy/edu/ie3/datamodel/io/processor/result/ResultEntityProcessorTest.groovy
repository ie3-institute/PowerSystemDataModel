/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.processor.result

import edu.ie3.datamodel.exceptions.EntityProcessorException
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.models.result.ResultEntity
import edu.ie3.datamodel.models.result.connector.LineResult
import edu.ie3.datamodel.models.result.connector.SwitchResult
import edu.ie3.datamodel.models.result.connector.Transformer2WResult
import edu.ie3.datamodel.models.result.connector.Transformer3WResult
import edu.ie3.datamodel.models.result.system.*
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units

import javax.measure.Quantity
import javax.measure.quantity.*
import java.time.ZonedDateTime

class ResultEntityProcessorTest extends Specification {

	// static fields
	@Shared
	UUID uuid = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
	@Shared
	UUID inputModel = UUID.fromString("22bea5fc-2cb2-4c61-beb9-b476e0107f52")
	@Shared
	Quantity<Power> p = Quantities.getQuantity(10, StandardUnits.ACTIVE_POWER_IN)
	@Shared
	Quantity<Power> q = Quantities.getQuantity(10, StandardUnits.REACTIVE_POWER_IN)
	@Shared
	Quantity<Dimensionless> soc = Quantities.getQuantity(50, Units.PERCENT)
	@Shared
	Quantity<Power> qDot = Quantities.getQuantity(1, StandardUnits.Q_DOT_RESULT)
	@Shared
	def expectedStandardResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
								   inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
								   p         : '0.01',
								   q         : '0.01',
								   time      : '2020-01-30T17:26:44Z[UTC]']

	@Shared
	def expectedSocResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
							  inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
							  p         : '0.01',
							  q         : '0.01',
							  soc       : '50.0',
							  time      : '2020-01-30T17:26:44Z[UTC]']

	@Shared
	def expectedHpResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
							 inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
							 p         : '0.01',
							 q         : '0.01',
							 time      : '2020-01-30T17:26:44Z[UTC]',
							 qDot      : '1.0']


	def "A ResultEntityProcessor should de-serialize a provided SystemParticipantResult correctly"() {
		given:
		def sysPartResProcessor = new ResultEntityProcessor(modelClass)
		def validResult = validSystemParticipantResult

		when:
		def validProcessedElement = sysPartResProcessor.handleEntity(validResult)

		then:
		validProcessedElement.present
		validProcessedElement.get() == expectedResults

		where:
		modelClass        | validSystemParticipantResult                                                                     || expectedResults
		LoadResult        | new LoadResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, p, q)         || expectedStandardResults
		FixedFeedInResult | new FixedFeedInResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, p, q)  || expectedStandardResults
		BmResult          | new BmResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, p, q)           || expectedStandardResults
		EvResult          | new EvResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, p, q, soc)      || expectedSocResults
		PvResult          | new PvResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, p, q)           || expectedStandardResults
		EvcsResult        | new EvcsResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, p, q)         || expectedStandardResults
		ChpResult         | new ChpResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, p, q)          || expectedStandardResults
		WecResult         | new WecResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, p, q)          || expectedStandardResults
		StorageResult     | new StorageResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, p, q, soc) || expectedSocResults
		HpResult          | new HpResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, p, q, qDot)           || expectedHpResults

	}

	def "A ResultEntityProcessor should de-serialize a provided SystemParticipantResult with null values correctly"() {
		given:
		def sysPartResProcessor = new ResultEntityProcessor(StorageResult)
		def storageResult = new StorageResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, p, q, null)


		when:
		def validProcessedElement = sysPartResProcessor.handleEntity(storageResult)

		then:
		validProcessedElement.present
		validProcessedElement.get() == [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
										inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
										p         : '0.01',
										q         : '0.01',
										soc       : '',
										time      : '2020-01-30T17:26:44Z[UTC]']

	}

	def "A ResultEntityProcessor should throw an exception if the provided class is not registered"() {
		given:
		def sysPartResProcessor = new ResultEntityProcessor(LoadResult)
		def storageResult = new StorageResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, p, q, null)

		when:
		sysPartResProcessor.handleEntity(storageResult)

		then:
		EntityProcessorException ex = thrown()
		ex.message == "Cannot process StorageResult.class with this EntityProcessor. Please either provide an element of LoadResult.class or create a new processor for StorageResult.class!"
	}

	def "A ResultEntityProcessor should de-serialize a NodeResult correctly"() {
		given:
		def sysPartResProcessor = new ResultEntityProcessor(NodeResult)

		Quantity<Dimensionless> vMag = Quantities.getQuantity(0.95, PowerSystemUnits.PU)
		Quantity<Angle> vAng = Quantities.getQuantity(45, StandardUnits.VOLTAGE_ANGLE)

		def validResult = new NodeResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, vMag, vAng)

		def expectedResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
							   inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
							   vAng      : '45.0',
							   vMag      : '0.95',
							   time      : '2020-01-30T17:26:44Z[UTC]']

		when:
		def validProcessedElement = sysPartResProcessor.handleEntity(validResult)

		then:
		validProcessedElement.present
		validProcessedElement.get() == expectedResults

	}

	@Shared
	def expectedLineResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
							   inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
							   iAMag     : '100.0',
							   iAAng     : '45.0',
							   iBMag     : '150.0',
							   iBAng     : '30.0',
							   time      : '2020-01-30T17:26:44Z[UTC]']

	@Shared
	def expectedTrafo2WResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
								  inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
								  iAMag     : '100.0',
								  iAAng     : '45.0',
								  iBMag     : '150.0',
								  iBAng     : '30.0',
								  tapPos    : '5',
								  time      : '2020-01-30T17:26:44Z[UTC]']


	@Shared
	def expectedTrafo3WResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
								  inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
								  iAMag     : '100.0',
								  iAAng     : '45.0',
								  iBMag     : '150.0',
								  iBAng     : '30.0',
								  iCMag     : '300.0',
								  iCAng     : '70.0',
								  tapPos    : '5',
								  time      : '2020-01-30T17:26:44Z[UTC]']

	@Shared
	def expectedSwitchResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
								 inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
								 closed    : 'true',
								 time      : '2020-01-30T17:26:44Z[UTC]']


	@Shared
	Quantity<ElectricCurrent> iAMag = Quantities.getQuantity(100, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
	@Shared
	Quantity<Angle> iAAng = Quantities.getQuantity(45, StandardUnits.ELECTRIC_CURRENT_ANGLE)
	@Shared
	Quantity<ElectricCurrent> iBMag = Quantities.getQuantity(150, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
	@Shared
	Quantity<Angle> iBAng = Quantities.getQuantity(30, StandardUnits.ELECTRIC_CURRENT_ANGLE)
	@Shared
	Quantity<ElectricCurrent> iCMag = Quantities.getQuantity(300, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
	@Shared
	Quantity<Angle> iCAng = Quantities.getQuantity(70, StandardUnits.ELECTRIC_CURRENT_ANGLE)
	@Shared
	int tapPos = 5
	@Shared
	boolean closed = true


	def "A ResultEntityProcessor should de-serialize all ConnectorResults correctly"() {
		given:
		def sysPartResProcessor = new ResultEntityProcessor(modelClass)

		def validResult = validConnectorResult

		when:
		def validProcessedElement = sysPartResProcessor.handleEntity(validResult)

		then:
		validProcessedElement.present
		validProcessedElement.get() == expectedResults

		where:
		modelClass          | validConnectorResult                                                                                                                          || expectedResults
		LineResult          | new LineResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, iAMag, iAAng, iBMag, iBAng)                                || expectedLineResults
		SwitchResult        | new SwitchResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, closed)                    								|| expectedSwitchResults
		Transformer2WResult | new Transformer2WResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, iAMag, iAAng, iBMag, iBAng, tapPos)               || expectedTrafo2WResults
		Transformer3WResult | new Transformer3WResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, iAMag, iAAng, iBMag, iBAng, iCMag, iCAng, tapPos) || expectedTrafo3WResults
	}

	def "A ResultEntityProcessor should de-serialize a CylindricalStorageResult correctly"() {
		given:
		def sysPartResProcessor = new ResultEntityProcessor(CylindricalStorageResult)

		Quantity<Power> qDot = Quantities.getQuantity(2, StandardUnits.Q_DOT_RESULT)
		Quantity<Energy> energy = Quantities.getQuantity(3, StandardUnits.ENERGY_RESULT)
		Quantity<Dimensionless> fillLevel = Quantities.getQuantity(20, Units.PERCENT)

		def validResult = new CylindricalStorageResult(uuid, ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), inputModel, energy, qDot, fillLevel)

		def expectedResults = [uuid      : '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
							   energy    : '3.0',
							   fillLevel : '20.0',
							   inputModel: '22bea5fc-2cb2-4c61-beb9-b476e0107f52',
							   qDot      : '2.0',
							   time      : '2020-01-30T17:26:44Z[UTC]']

		when:
		def validProcessedElement = sysPartResProcessor.handleEntity(validResult)

		then:
		validProcessedElement.present
		validProcessedElement.get() == expectedResults

	}

	def "A ResultEntityProcessor should throw an EntityProcessorException when it receives an entity result that is not eligible"() {

		given:
		def sysPartResProcessor = new ResultEntityProcessor(ResultEntityProcessor.eligibleEntityClasses.get(0))

		def invalidClassResult = new InvalidTestResult(ZonedDateTime.parse("2020-01-30T17:26:44Z[UTC]"), uuid)

		when:
		sysPartResProcessor.handleEntity(invalidClassResult)

		then:
		EntityProcessorException exception = thrown()
		exception.message == "Cannot process InvalidTestResult.class with this EntityProcessor. " +
				"Please either provide an element of LoadResult.class or create a new processor for InvalidTestResult.class!"

	}

	def "The list of eligible entity classes for a ResultEntityProcessor should be valid"() {
		given:
		int noOfElements = 17 // number of all currently implemented entity results

		expect:
		ResultEntityProcessor.eligibleEntityClasses.size() == noOfElements
	}

	def "ResultEntityProcessor should throw an exception if an invalid class is passed into the constructor"() {

		when:
		new ResultEntityProcessor(InvalidTestResult)

		then:
		thrown(EntityProcessorException)
	}

	private class InvalidTestResult extends ResultEntity {

		InvalidTestResult(ZonedDateTime time, UUID inputModel) {
			super(time, inputModel)
		}

		InvalidTestResult(UUID uuid, ZonedDateTime time, UUID inputModel) {
			super(uuid, time, inputModel)
		}
	}

}

