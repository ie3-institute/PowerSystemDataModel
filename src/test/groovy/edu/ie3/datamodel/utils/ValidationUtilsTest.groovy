/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import static edu.ie3.util.quantities.PowerSystemUnits.PU
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.test.common.GridTestData
import edu.ie3.util.TimeTools
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

import java.time.ZoneId

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
		" - NodeInput{uuid=9e37ce48-9650-44ec-b888-c2fd182aff01, id='node_f', operator=OperatorInput{uuid=f15105c4-a2de-4ab8-a621-4bc98e372d92, id='Univ.-Prof. Dr. rer. hort. Klaus-Dieter Brokkoli'}, operationTime=OperationTime{startDate=null, endDate=null, isLimited=false}, vTarget=1.0 PU, slack=false, geoPosition=null, voltLvl=CommonVoltageLevel{id='Niederspannung', nominalVoltage=0.4 kV, synonymousIds=[Niederspannung, lv, ns], voltageRange=Interval [0.0 kV, 10.0 kV)}, subnet=6}\n" +
		" - NodeInput{uuid=9e37ce48-9650-44ec-b888-c2fd182aff01, id='node_g', operator=OperatorInput{uuid=f15105c4-a2de-4ab8-a621-4bc98e372d92, id='Univ.-Prof. Dr. rer. hort. Klaus-Dieter Brokkoli'}, operationTime=OperationTime{startDate=null, endDate=null, isLimited=false}, vTarget=1.0 PU, slack=false, geoPosition=null, voltLvl=CommonVoltageLevel{id='Niederspannung', nominalVoltage=0.4 kV, synonymousIds=[Niederspannung, lv, ns], voltageRange=Interval [0.0 kV, 10.0 kV)}, subnet=6}")
		[
			GridTestData.nodeD,
			GridTestData.nodeE] as Set || Optional.empty()
		[] as Set                          || Optional.empty()
	}
}
