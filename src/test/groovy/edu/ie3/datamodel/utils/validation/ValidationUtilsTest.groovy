/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.exceptions.InvalidEntityException
import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.test.common.GridTestData
import edu.ie3.util.TimeUtil
import edu.ie3.util.quantities.interfaces.SpecificConductance
import org.locationtech.jts.geom.Coordinate
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.Quantity

import static edu.ie3.datamodel.models.StandardUnits.ADMITTANCE_PER_LENGTH
import static edu.ie3.datamodel.models.StandardUnits.ELECTRIC_CURRENT_MAGNITUDE
import static edu.ie3.datamodel.models.StandardUnits.RATED_VOLTAGE_MAGNITUDE
import static edu.ie3.util.quantities.PowerSystemUnits.OHM_PER_KILOMETRE
import static edu.ie3.util.quantities.PowerSystemUnits.PU

class ValidationUtilsTest extends Specification {

	def "Smoke Test: Correct asset throws no exception"() {
		given:
		def asset = GridTestData.nodeA

		when:
		ValidationUtils.check(asset)

		then:
		noExceptionThrown()
	}

	def "The validation utils should determine if a collection with UniqueEntity's is distinct by their uuid"() {

		expect:
		ValidationUtils.distinctUuids(collection) == distinct

		where:
		collection                         || distinct
		[
			GridTestData.nodeF,
			new NodeInput(
			UUID.fromString("9e37ce48-9650-44ec-b888-c2fd182aff01"), "node_g", OperatorInput.NO_OPERATOR_ASSIGNED,
			OperationTime.notLimited()
			,
			Quantities.getQuantity(1d, PU),
			false,
			null,
			GermanVoltageLevelUtils.LV,
			6)] as Set         || false
		[
			GridTestData.nodeD,
			GridTestData.nodeE] as Set || true
		[] as Set                          || true
	}

	def "The validation utils should check for duplicates as expected"() {

		expect:
		ValidationUtils.checkForDuplicateUuids(collection) == checkResult

		where:
		collection                         || checkResult
		[
			new NodeInput(
			UUID.fromString("9e37ce48-9650-44ec-b888-c2fd182aff01"), "node_f", GridTestData.profBroccoli,
			OperationTime.notLimited()
			,
			Quantities.getQuantity(1d, PU),
			false,
			null,
			GermanVoltageLevelUtils.LV,
			6),
			new NodeInput(
			UUID.fromString("9e37ce48-9650-44ec-b888-c2fd182aff01"), "node_g", GridTestData.profBroccoli,
			OperationTime.notLimited()
			,
			Quantities.getQuantity(1d, PU),
			false,
			null,
			GermanVoltageLevelUtils.LV,
			6)] as Set         || Optional.of("9e37ce48-9650-44ec-b888-c2fd182aff01: 2\n" +
		" - NodeInput{uuid=9e37ce48-9650-44ec-b888-c2fd182aff01, id='node_f', operator=f15105c4-a2de-4ab8-a621-4bc98e372d92, operationTime=OperationTime{startDate=null, endDate=null, isLimited=false}, vTarget=1 PU, slack=false, geoPosition=null, voltLvl=CommonVoltageLevel{id='Niederspannung', nominalVoltage=0.4 kV, synonymousIds=[Niederspannung, lv, ns], voltageRange=Interval [0 kV, 10 kV)}, subnet=6}\n" +
		" - NodeInput{uuid=9e37ce48-9650-44ec-b888-c2fd182aff01, id='node_g', operator=f15105c4-a2de-4ab8-a621-4bc98e372d92, operationTime=OperationTime{startDate=null, endDate=null, isLimited=false}, vTarget=1 PU, slack=false, geoPosition=null, voltLvl=CommonVoltageLevel{id='Niederspannung', nominalVoltage=0.4 kV, synonymousIds=[Niederspannung, lv, ns], voltageRange=Interval [0 kV, 10 kV)}, subnet=6}")
		[
			GridTestData.nodeD,
			GridTestData.nodeE] as Set || Optional.empty()
		[] as Set                          || Optional.empty()
	}

	def "If an object can't be identified, a ValidationException is thrown as expected"() {
		when:
		ValidationUtils.check(invalidObject)

		then:
		Exception ex = thrown()
		ex.class == expectedException.class
		ex.message == expectedException.message

		where:
		invalidObject          || expectedException
		new Coordinate(10, 10) || new ValidationException("Cannot validate object of class '" + invalidObject.getClass().getSimpleName() + "', as no routine is implemented.")
	}

	def "The validation check method recognizes all potential errors for an asset"() {
		when:
		ValidationUtils.check(invalidAsset)

		then:
		Exception ex = thrown()
		ex.class == expectedException.class
		ex.message == expectedException.message

		where:
		invalidAsset                                                            	    || expectedException
		null 																			|| new ValidationException("Expected an object, but got nothing. :-(")
		GridTestData.nodeA.copy().id(null).build()										|| new InvalidEntityException("No ID assigned", invalidAsset)
		GridTestData.nodeA.copy().operationTime(null).build()							|| new InvalidEntityException("Operation time of the asset is not defined", invalidAsset)
		GridTestData.nodeA.copy().operationTime(OperationTime.builder().
				withStart(TimeUtil.withDefaults.toZonedDateTime("2020-03-26 15:11:31")).
				withEnd(TimeUtil.withDefaults.toZonedDateTime("2020-03-25 15:11:31")).build()).build() || new InvalidEntityException("Operation start time of the asset has to be before end time", invalidAsset)
		GridTestData.nodeA.copy().operationTime(OperationTime.builder().
				withStart(null).
				withEnd(TimeUtil.withDefaults.toZonedDateTime("2020-03-25 15:11:31")).build()).build() || new InvalidEntityException("Start and/or end time of operation time is null, although operation should be limited", invalidAsset)
		GridTestData.nodeA.copy().operationTime(OperationTime.builder().
				withStart(TimeUtil.withDefaults.toZonedDateTime("2020-03-26 15:11:31")).
				withEnd(null).build()).build() || new InvalidEntityException("Start and/or end time of operation time is null, although operation should be limited", invalidAsset)
	}

	def "The check for negative entities should work as expected"() {
		given:
		def asset = new LineTypeInput(
				UUID.fromString("3bed3eb3-9790-4874-89b5-a5434d408088"),
				"lineType_AtoB",
				Quantities.getQuantity(0d, ADMITTANCE_PER_LENGTH),
				Quantities.getQuantity(0d, ADMITTANCE_PER_LENGTH),
				Quantities.getQuantity(0.437d, OHM_PER_KILOMETRE),
				Quantities.getQuantity(0.356d, OHM_PER_KILOMETRE),
				Quantities.getQuantity(300d, ELECTRIC_CURRENT_MAGNITUDE),
				Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE)
				)
		def invalidAsset = new LineTypeInput(
				UUID.fromString("3bed3eb3-9790-4874-89b5-a5434d408088"),
				"lineType_AtoB",
				Quantities.getQuantity(-1d, ADMITTANCE_PER_LENGTH), // invalid value
				Quantities.getQuantity(0d, ADMITTANCE_PER_LENGTH),
				Quantities.getQuantity(0.437d, OHM_PER_KILOMETRE),
				Quantities.getQuantity(0.356d, OHM_PER_KILOMETRE),
				Quantities.getQuantity(300d, ELECTRIC_CURRENT_MAGNITUDE),
				Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE)
				)

		when:
		ValidationUtils.detectNegativeQuantities([asset.getB()] as Quantity<SpecificConductance>[], asset)

		then:
		noExceptionThrown()

		when:
		ValidationUtils.detectNegativeQuantities([invalidAsset.getB()] as Quantity<SpecificConductance>[], invalidAsset)

		then:
		InvalidEntityException ex = thrown()
		ex.message == "Entity is invalid because of: The following quantities have to be zero or positive: -1 μS/km [LineTypeInput{uuid=3bed3eb3-9790-4874-89b5-a5434d408088, id=lineType_AtoB, b=-1 μS/km, g=0 μS/km, r=0.437 Ω/km, x=0.356 Ω/km, iMax=300 A, vRated=20 kV}]"
	}

	def "The check for zero or negative entities should work as expected"() {
		given:
		def asset = new LineTypeInput(
				UUID.fromString("3bed3eb3-9790-4874-89b5-a5434d408088"),
				"lineType_AtoB",
				Quantities.getQuantity(1d, ADMITTANCE_PER_LENGTH),
				Quantities.getQuantity(0d, ADMITTANCE_PER_LENGTH),
				Quantities.getQuantity(0.437d, OHM_PER_KILOMETRE),
				Quantities.getQuantity(0.356d, OHM_PER_KILOMETRE),
				Quantities.getQuantity(300d, ELECTRIC_CURRENT_MAGNITUDE),
				Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE)
				)
		def invalidAsset = new LineTypeInput(
				UUID.fromString("3bed3eb3-9790-4874-89b5-a5434d408088"),
				"lineType_AtoB",
				Quantities.getQuantity(0d, ADMITTANCE_PER_LENGTH), // invalid value
				Quantities.getQuantity(0d, ADMITTANCE_PER_LENGTH),
				Quantities.getQuantity(0.437d, OHM_PER_KILOMETRE),
				Quantities.getQuantity(0.356d, OHM_PER_KILOMETRE),
				Quantities.getQuantity(300d, ELECTRIC_CURRENT_MAGNITUDE),
				Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE)
				)

		when:
		ValidationUtils.detectZeroOrNegativeQuantities([asset.getB()] as Quantity<SpecificConductance>[], asset)

		then:
		noExceptionThrown()

		when:
		ValidationUtils.detectZeroOrNegativeQuantities([invalidAsset.getB()] as Quantity<SpecificConductance>[], invalidAsset)

		then:
		InvalidEntityException ex = thrown()
		ex.message == "Entity is invalid because of: The following quantities have to be positive: 0 μS/km [LineTypeInput{uuid=3bed3eb3-9790-4874-89b5-a5434d408088, id=lineType_AtoB, b=0 μS/km, g=0 μS/km, r=0.437 Ω/km, x=0.356 Ω/km, iMax=300 A, vRated=20 kV}]"
	}
}
