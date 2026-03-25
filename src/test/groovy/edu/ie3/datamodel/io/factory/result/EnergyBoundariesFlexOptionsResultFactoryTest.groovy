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
import edu.ie3.datamodel.models.result.system.EnergyBoundariesFlexOptionsResult
import edu.ie3.datamodel.utils.CollectionUtils
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class EnergyBoundariesFlexOptionsResultFactoryTest extends Specification implements FactoryTestHelper {

  def "A EnergyBoundariesFlexOptionsResultFactory should contain all expected classes for parsing"() {
    given:
    def resultFactory = new EnergyBoundariesFlexOptionsResultFactory()
    def expectedClasses = [
      EnergyBoundariesFlexOptionsResult
    ]

    expect:
    resultFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A EnergyBoundariesFlexOptionsResultFactory should parse a EnergyBoundariesFlexOptionsResult correctly"() {
    given: "the relevant factory and model data"
    def resultFactory = new EnergyBoundariesFlexOptionsResultFactory()
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44Z",
      "inputModel": "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
      "emin"      : "-0.05",
      "emax"      : "0.05",
      "pmin"      : "-1",
      "pmax"      : "10",
    ]

    when:
    Try<? extends EnergyBoundariesFlexOptionsResult, FactoryException> result = resultFactory.get(new EntityData(parameter, EnergyBoundariesFlexOptionsResult))

    then:
    result.success
    result.data.get().getClass() == EnergyBoundariesFlexOptionsResult
    ((EnergyBoundariesFlexOptionsResult) result.data.get()).with {
      eMin == getQuant(parameter["emin"], StandardUnits.ENERGY_RESULT)
      eMax == getQuant(parameter["emax"], StandardUnits.ENERGY_RESULT)
      pMin == getQuant(parameter["pmin"], StandardUnits.ACTIVE_POWER_RESULT)
      pMax == getQuant(parameter["pmax"], StandardUnits.ACTIVE_POWER_RESULT)
      time == TIME_UTIL.toZonedDateTime(parameter["time"])
      inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A EnergyBoundariesFlexOptionsResultFactory should throw an exception on invalid or incomplete data"() {
    given: "a system participant factory and model data"
    def actualFields = CollectionUtils.newSet("time", "input_model", "e_max", "p_min", "p_max")

    when:
    Try<Void, FactoryException> input = DataSource.validate(actualFields, EnergyBoundariesFlexOptionsResult)

    then:
    input.failure
    input.exception.get().message == "The provided fields [e_max, input_model, p_max, p_min, time] are invalid for instance of 'EnergyBoundariesFlexOptionsResult'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'EnergyBoundariesFlexOptionsResult' are possible (NOT case-sensitive!):\n" +
        "0: [eMax, eMin, inputModel, pMax, pMin, time] or [e_max, e_min, input_model, p_max, p_min, time]\n"
  }
}
