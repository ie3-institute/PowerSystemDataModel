/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.result

import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.io.source.SourceValidator
import edu.ie3.datamodel.utils.Try
import spock.lang.Specification

class NodeResultTest extends Specification {

  def "A NodeResultInput should throw an exception on invalid fields"() {
    given:
    def actualFields = SourceValidator.newSet("uuid")
    def validator = new SourceValidator()

    when:
    Try<Void, ValidationException> input = validator.validate(actualFields, NodeResult)

    then:
    input.failure
    input.exception.get().message == "The provided fields [input_model, time, v_mag] are invalid for instance of 'NodeResult'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'NodeResult' are possible (NOT case-sensitive!):\n" +
        "0: [inputModel, time, vAng, vMag] or [input_model, time, v_ang, v_mag]\n"
  }
}
