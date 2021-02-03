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

class ChpResultTest extends Specification {
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

	def "A ChpResult object should convert to a string correctly"() {
		given:
		def chpResultObj = new ChpResult(uuid, time, inputModel, p, q, qDot)

		when:
		def outputString = chpResultObj.toString()

		then:
		def expectedString = "ChpResult{uuid=" + uuid + ", time=" + time.toString() + ", inputModel=" +
				inputModel.toString() + ", p=" + p.toString() + ", q=" + q.toString() + ", qDot=" + qDot.toString() + "}"
		outputString == expectedString
	}

	def "A ChpResult object can be compared to another object correctly"() {
		given:
		def chpResultObj = new ChpResult(uuid, time, inputModel, p, q, qDot)

		when:
		def equivalentChpResultObj = new ChpResult(uuid, time, inputModel, p, q, qDot)
		def differentChpResultObj = new ChpResult(uuid, time, inputModel, p, q, qDot.subtract(qDot))

		then:
		chpResultObj.equals(equivalentChpResultObj)
		!chpResultObj.equals(differentChpResultObj)
	}
}