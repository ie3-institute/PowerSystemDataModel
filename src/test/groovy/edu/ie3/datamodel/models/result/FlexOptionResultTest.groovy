/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.result

import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.datamodel.io.source.SourceValidator
import edu.ie3.datamodel.models.result.system.FlexOptionsResult
import edu.ie3.datamodel.utils.Try
import spock.lang.Specification

class FlexOptionResultTest extends Specification {
  def "A FlexOptionResult should throw an exception on invalid fields"() {
    given:
    def actualFields = SourceValidator.newSet("uuid")
    def validator = new SourceValidator()

    when:
    Try<Void, ValidationException> input = validator.validate(actualFields, FlexOptionsResult)

    then:
    input.failure
    input.exception.get().message == "The provided fields [input_model, p_min, p_ref, time] are invalid for instance of 'FlexOptionsResult'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'FlexOptionsResult' are possible (NOT case-sensitive!):\n" +
        "0: [inputModel, pMax, pMin, pRef, time] or [input_model, p_max, p_min, p_ref, time]\n"
  }
}
