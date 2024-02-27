/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.result

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.system.FlexOptionsResult
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class FlexOptionsResultFactoryTest extends Specification implements FactoryTestHelper {

  def "A FlexOptionsResultFactory should contain all expected classes for parsing"() {
    given:
    def resultFactory = new FlexOptionsResultFactory()
    def expectedClasses = [FlexOptionsResult]

    expect:
    resultFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A FlexOptionsResultFactory should parse a FlexOptionsResult correctly"() {
    given: "a system participant factory and model data"
    def resultFactory = new FlexOptionsResultFactory()
    Map<String, String> parameter = [
      "time"      : "2020-01-30 17:26:44",
      "inputModel": "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
      "pref"      : "2",
      "pmin"      : "-1",
      "pmax"      : "10",
    ]

    when:
    Try<? extends FlexOptionsResult, FactoryException> result = resultFactory.get(new EntityData(parameter, FlexOptionsResult))

    then:
    result.success
    result.data.get().getClass() == FlexOptionsResult
    ((FlexOptionsResult) result.data.get()).with {
      assert pRef == getQuant(parameter["pref"], StandardUnits.ACTIVE_POWER_RESULT)
      assert pMin == getQuant(parameter["pmin"], StandardUnits.ACTIVE_POWER_RESULT)
      assert pMax == getQuant(parameter["pmax"], StandardUnits.ACTIVE_POWER_RESULT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A FlexOptionsResultFactory should throw an exception on invalid or incomplete data"() {
    given: "a system participant factory and model data"
    def resultFactory = new FlexOptionsResultFactory()
    def actualFields = FlexOptionsResultFactory.newSet("time", "input_model", "p_ref", "p_min")

    when:
    Try<Void, FactoryException> input = resultFactory.validate(actualFields, FlexOptionsResult)

    then:
    input.failure
    input.exception.get().message == "The provided fields [input_model, p_min, p_ref, time] are invalid for instance of 'FlexOptionsResult'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'FlexOptionsResult' are possible (NOT case-sensitive!):\n" +
        "0: [inputModel, pMax, pMin, pRef, time] or [input_model, p_max, p_min, p_ref, time]\n"
  }
}
