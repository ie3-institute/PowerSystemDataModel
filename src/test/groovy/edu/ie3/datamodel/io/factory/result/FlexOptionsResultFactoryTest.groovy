/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.result

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.SimpleEntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.system.FlexOptionsResult
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
      "preference": "2",
      "pmin"      : "-1",
      "pmax"      : "10",
    ]

    when:
    Optional<? extends FlexOptionsResult> result = resultFactory.get(new SimpleEntityData(parameter, FlexOptionsResult))

    then:
    result.present
    result.get().getClass() == FlexOptionsResult
    ((FlexOptionsResult) result.get()).with {
      assert pReference == getQuant(parameter["preference"], StandardUnits.ACTIVE_POWER_RESULT)
      assert pMin == getQuant(parameter["pmin"], StandardUnits.ACTIVE_POWER_RESULT)
      assert pMax == getQuant(parameter["pmax"], StandardUnits.ACTIVE_POWER_RESULT)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A FlexOptionsResultFactory should throw an exception on invalid or incomplete data"() {
    given: "a system participant factory and model data"
    def resultFactory = new FlexOptionsResultFactory()
    Map<String, String> parameter = [
      "time"      : "2020-01-30 17:26:44",
      "inputModel": "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
      "preference": "2",
      "pmin"      : "-1",
    ]

    when:
    resultFactory.get(new SimpleEntityData(parameter, FlexOptionsResult))

    then:
    FactoryException ex = thrown()
    ex.message == "The provided fields [inputModel, pmin, preference, time] with data \n" +
        "{inputModel -> 91ec3bcf-1897-4d38-af67-0bf7c9fa73c7,\n" +
        "pmin -> -1,\n" +
        "preference -> 2,\n" +
        "time -> 2020-01-30 17:26:44} are invalid for instance of FlexOptionsResult. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'FlexOptionsResult' are possible (NOT case-sensitive!):\n" +
        "0: [inputModel, pmax, pmin, preference, time]\n" +
        "1: [inputModel, pmax, pmin, preference, time, uuid]\n"
  }
}
