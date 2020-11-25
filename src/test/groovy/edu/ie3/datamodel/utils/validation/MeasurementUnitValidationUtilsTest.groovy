/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.exceptions.UnsafeEntityException
import edu.ie3.test.common.GridTestData
import spock.lang.Specification

class MeasurementUnitValidationUtilsTest extends Specification {

	def "Smoke Test: Correct measurement unit throws no exception"() {
		given:
		def measurementUnit = GridTestData.measurementUnitInput

		when:
		ValidationUtils.check(measurementUnit)

		then:
		noExceptionThrown()
	}

	def "MeasurementUnitValidationUtils.check() recognizes all potential errors for a measurement unit"() {
		when:
		MeasurementUnitValidationUtils.check(invalidMeasurementUnit)

		then:
		Exception ex = thrown()
		ex.class == expectedException.class
		ex.message == expectedException.message

		where:
		invalidMeasurementUnit                                                                     || expectedException
		GridTestData.measurementUnitInput.copy().vMag(false).vAng(false).p(false).q(false).build() || new UnsafeEntityException("Measurement Unit does not measure any values", invalidMeasurementUnit)
	}
}