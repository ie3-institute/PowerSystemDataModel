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
import edu.ie3.util.TimeTools
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZoneId

import static edu.ie3.util.quantities.PowerSystemUnits.PU

class ValidationUtilsTest extends Specification {

	static {
		TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
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

	def "The validation utils should throw a validation exception if the provided type is null"() {
		when:
		ValidationUtils.check(null)

		then:
		ValidationException ex = thrown()
		ex.message == "Expected an object, but got nothing. :-("
	}

	def "The checkAsset validation recognizes a missing id"() {
		given:
		def asset = GridTestData.nodeA.copy().id(null).build()
		when:
		ValidationUtils.check(asset)
		then:
		InvalidEntityException ex = thrown()
		ex.message == "Entity is invalid because of: No ID assigned [NodeInput{uuid=4ca90220-74c2-4369-9afa-a18bf068840d, id='null', operator=f15105c4-a2de-4ab8-a621-4bc98e372d92, operationTime=OperationTime{startDate=2020-03-24T15:11:31Z[UTC], endDate=2020-03-25T15:11:31Z[UTC], isLimited=true}, vTarget=1 PU, slack=true, geoPosition=POINT (7.411111 51.492528), voltLvl=CommonVoltageLevel{id='Höchstspannung', nominalVoltage=380 kV, synonymousIds=[Höchstspannung, ehv, ehv_380kv, hoes, hoes_380kv], voltageRange=Interval [380 kV, 560 kV)}, subnet=1}]"
	}

	def "The checkAsset validation recognizes a missing operator"() {
		given:
		def asset = GridTestData.nodeA.copy().operator(null).build()
		when:
		ValidationUtils.check(asset)
		then:
		InvalidEntityException ex = thrown()
		ex.message == "Entity is invalid because of: No operator assigned [NodeInput{uuid=4ca90220-74c2-4369-9afa-a18bf068840d, id='node_A', operator=null, operationTime=OperationTime{startDate=2020-03-24T15:11:31Z[UTC], endDate=2020-03-25T15:11:31Z[UTC], isLimited=true}, vTarget=1 PU, slack=true, geoPosition=POINT (7.411111 51.492528), voltLvl=CommonVoltageLevel{id='Höchstspannung', nominalVoltage=380 kV, synonymousIds=[Höchstspannung, ehv, ehv_380kv, hoes, hoes_380kv], voltageRange=Interval [380 kV, 560 kV)}, subnet=1}]"
	}
	// TODO NSteffan: in NodeInput wird in toString Methode getOperator().getUuid() aufgerufen, erzeugt NullPointerException
	//  -> Text für InvalidEntityException kann nicht erstellt werden




	// TODO NSteffan: Move to right place
	def "A LineType should throw a NullPointerException if the provided field values are null"() {
		when:
		new LineTypeInput(null, null, null,null,null,null,null,null)

		then:
		NullPointerException ex = thrown()
	}

}
