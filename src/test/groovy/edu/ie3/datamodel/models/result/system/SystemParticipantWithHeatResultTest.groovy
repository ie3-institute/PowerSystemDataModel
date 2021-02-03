/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.result.system

import edu.ie3.test.common.SystemParticipantTestData
import spock.lang.Shared
import spock.lang.Specification

import javax.measure.Quantity
import javax.measure.quantity.Power
import java.time.ZonedDateTime

class SystemParticipantWithHeatResultTest extends Specification {
	// static fields
	@Shared
	UUID uuid = SystemParticipantTestData.uuid
	@Shared
	ZonedDateTime time = SystemParticipantTestData.time
	@Shared
	UUID inputModel = SystemParticipantTestData.inputModel
	@Shared
	Quantity<Power> p = SystemParticipantTestData.p
	@Shared
	Quantity<Power> q = SystemParticipantTestData.q
	@Shared
	Quantity<Power> qDot = SystemParticipantTestData.qDot

	def "A SystemParticipantWithHeatResult object should convert to a string correctly"() {
		given:
		def systemParticipantWithHeatResultObj = new SystemParticipantWithHeatResult(uuid, time, inputModel, p, q, qDot)

		when:
		def outputString = systemParticipantWithHeatResultObj.toString()

		then:
		def expectedString = "SystemParticipantWithHeatResult{uuid=" + uuid + ", time=" + time.toString() + ", inputModel=" +
				inputModel.toString() + ", p=" + p.toString() + ", q=" + q.toString() + ", qDot=" + qDot.toString() + "}"
		outputString == expectedString
	}
}