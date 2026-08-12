/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.input.connector.CableDeploymentInput
import edu.ie3.datamodel.models.StandardUnits

import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper

import spock.lang.Specification


class CableDeploymentInputFactoryTest extends Specification implements FactoryTestHelper {
  def "A CableDeploymentInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new CableDeploymentInputFactory()
    def expectedClasses = [CableDeploymentInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A CableDeploymentInputFactory should parse a valid CableDeploymentInput correctly"() {
    given: "a system participant input type factory and model data"
    def inputFactory = new CableDeploymentInputFactory()
    Map<String, String> parameter = [
      "uuid" : "b8c873a4-e32b-4b10-879e-01c53375e578",
      "lineUuid" : "2a254a7b-9385-4d1a-bf5c-468f322e65fc",
      "layoutFormation" : "TREFOIL",
      "depthCables" : "3",
      "distanceCables" : "0.5",
    ]
    def inputClass = CableDeploymentInput

    when:
    Try<CableDeploymentInput, FactoryException> input =
        inputFactory.get(new EntityData(parameter, inputClass))

    then:
    input.success
    input.data.get().getClass() == inputClass
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      lineUuid == UUID.fromString(parameter["lineUuid"])
      layoutFormation == parameter["layoutFormation"]
      depthCables == getQuant(parameter["depthCables"], StandardUnits.LINE_LENGTH)
      distanceCables == getQuant(parameter["distanceCables"], StandardUnits.LINE_LENGTH)
    }
  }
}