/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.result

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.NodeResult
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class NodeResultFactoryTest extends Specification implements FactoryTestHelper {

  def "A NodeResultFactory should contain all expected classes for parsing"() {
    given:
    def resultFactory = new NodeResultFactory()
    def expectedClasses = [NodeResult]

    expect:
    resultFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A NodeResultFactory should parse a NodeResult correctly"() {
    given: "a system participant factory and model data"
    def resultFactory = new NodeResultFactory()
    Map<String, String> parameter = [
      "time"      : "2020-01-30 17:26:44",
      "inputModel": "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
      "vmag"      : "2",
      "vang"      : "2"
    ]

    when:
    Try<? extends NodeResult, FactoryException> result = resultFactory.get(new EntityData(parameter, NodeResult))

    then:
    result.success
    result.data.get().getClass() == NodeResult
    ((NodeResult) result.data.get()).with {
      assert vMag == getQuant(parameter["vmag"], StandardUnits.VOLTAGE_MAGNITUDE)
      assert vAng == getQuant(parameter["vang"], StandardUnits.VOLTAGE_ANGLE)
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
    }
  }

  def "A NodeResultFactory should throw an exception on invalid or incomplete data"() {
    given: "a system participant factory and model data"
    def resultFactory = new NodeResultFactory()
    def actualFields = NodeResultFactory.newSet("time", "input_model", "v_mag")

    when:
    Try<Void, FactoryException> input = resultFactory.validate(actualFields, NodeResult)

    then:
    input.failure
    input.exception.get().message == "The provided fields [input_model, time, v_mag] are invalid for instance of 'NodeResult'. \n" +
        "The following fields (without complex objects e.g. nodes, operators, ...) to be passed to a constructor of 'NodeResult' are possible (NOT case-sensitive!):\n" +
        "0: [inputModel, time, vAng, vMag] or [input_model, time, v_ang, v_mag]\n" +
        "1: [inputModel, time, uuid, vAng, vMag] or [input_model, time, uuid, v_ang, v_mag]\n"
  }
}
