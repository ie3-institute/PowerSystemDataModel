package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.exceptions.InvalidEntityException
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
        // GridTestData.measurementUnitInput.copy().node(null).build()                                || new InvalidEntityException("Node of measurement unit is null", invalidMeasurementUnit)
        GridTestData.measurementUnitInput.copy().vMag(false).vAng(false).p(false).q(false).build() || new UnsafeEntityException("Measurement Unit does not measure any values", invalidMeasurementUnit)
    }
    // TODO NSteffan: if node is null, causes NullPointerException in toString function,
    //  can't create string for UnsafeEntityException

}