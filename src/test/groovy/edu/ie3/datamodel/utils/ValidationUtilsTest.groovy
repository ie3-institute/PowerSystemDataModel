/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.test.common.GridTestData
import edu.ie3.util.TimeTools
import spock.lang.Specification
import tec.uom.se.quantity.Quantities

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
}
