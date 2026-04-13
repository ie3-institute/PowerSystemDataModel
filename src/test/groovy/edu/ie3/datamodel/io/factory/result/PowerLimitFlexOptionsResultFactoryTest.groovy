/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.result

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.io.source.DataSource
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.system.PowerLimitFlexOptionsResult
import edu.ie3.datamodel.utils.CollectionUtils
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class PowerLimitFlexOptionsResultFactoryTest extends Specification implements FactoryTestHelper {

  def "A PowerLimitFlexOptionsResultFactory should contain all expected classes for parsing"() {
    given:
    def resultFactory = new PowerLimitFlexOptionsResultFactory()
    def expectedClasses = [PowerLimitFlexOptionsResult]

    expect:
    resultFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A PowerLimitFlexOptionsResultFactory should parse a PowerLimitFlexOptionsResult correctly"() {
    given: "the relevant factory and model data"
    def resultFactory = new PowerLimitFlexOptionsResultFactory()
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
      "pref"      : "2",
      "pmin"      : "-1",
      "pmax"      : "10",
    ]

    when:
    Try<? extends PowerLimitFlexOptionsResult, FactoryException> result = resultFactory.get(new EntityData(parameter, PowerLimitFlexOptionsResult))

    then:
    result.success
    result.data.get().getClass() == PowerLimitFlexOptionsResult
    ((PowerLimitFlexOptionsResult) result.data.get()).with {
      pRef == getQuant(parameter["pref"], StandardUnits.ACTIVE_POWER_RESULT)
      pMin == getQuant(parameter["pmin"], StandardUnits.ACTIVE_POWER_RESULT)
      pMax == getQuant(parameter["pmax"], StandardUnits.ACTIVE_POWER_RESULT)
      time == TIME_UTIL.toZonedDateTime(parameter["time"])
      inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A PowerLimitFlexOptionsResultFactory should throw an exception on invalid or incomplete data"() {
    given: "a system participant factory and model data"
    def actualFields = CollectionUtils.newSet("time", "input_model", "p_ref", "p_min")

    when:
    Try<Void, FactoryException> input = DataSource.validate(actualFields, PowerLimitFlexOptionsResult)

    then:
    input.failure
    input.exception.get().message == "The provided fields [input_model, p_min, p_ref, time] are invalid for instance of 'PowerLimitFlexOptionsResult'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'PowerLimitFlexOptionsResult' are possible (NOT case-sensitive!):\n" +
        "0: [inputModel, pMax, pMin, pRef, time] or [input_model, p_max, p_min, p_ref, time]\n"
  }
}
