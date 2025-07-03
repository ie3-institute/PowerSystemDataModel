/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.exceptions.InvalidEntityException
import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import spock.lang.Specification

class EmValidationUtilsTest extends Specification {

  def "Smoke Test: Correct energy management system throws no exception"() {
    given:
    def em = GridTestData.energyManagementInput

    when:
    List<Try<Void, ? extends ValidationException>> tries = EnergyManagementValidationUtils.check(em)

    then:
    tries.every { it.success }
  }

  def "The check method recognizes all potential errors for an energy management input"() {
    when:
    List<Try<Void, ? extends ValidationException>> exceptions = EnergyManagementValidationUtils.check(invalidEm).stream().filter { it -> it.failure }.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception.get()
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidEm                                                            	      || expectedSize || expectedException
    GridTestData.energyManagementInput.copy().controlStrategy("invalid").build()  || 1            || new InvalidEntityException("Control strategy of energy management system must be one of the following: PRIORITIZED, PROPORTIONAL.", invalidEm)
  }
}
