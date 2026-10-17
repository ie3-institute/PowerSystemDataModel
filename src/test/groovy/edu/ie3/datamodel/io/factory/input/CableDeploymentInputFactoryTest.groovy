/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.input.connector.CableDeploymentInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification
import tech.units.indriya.unit.Units

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
      "cableBedWidth" : "0.8",
      "cableBedHeight" : "0.3",
      "cableBedSoilType" : "ca5f58dd-e444-4daf-83a7-78529548fa77",
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
      depthCables == getQuant(parameter["depthCables"], Units.METRE)
      distanceCables == getQuant(parameter["distanceCables"], Units.METRE)
      cableBedWidth == getQuant(parameter["cableBedWidth"], Units.METRE)
      cableBedHeight == getQuant(parameter["cableBedHeight"], Units.METRE)
      cableBedSoilType == UUID.fromString(parameter["cableBedSoilType"])
    }
  }

  def "A CableDeploymentInputFactory throws FactoryException when a required field is missing"() {
    given:
    def inputFactory = new CableDeploymentInputFactory()
    Map<String, String> parameter = [
      "uuid" : "b8c873a4-e32b-4b10-879e-01c53375e578",
      "lineUuid" : "2a254a7b-9385-4d1a-bf5c-468f322e65fc",
      "layoutFormation" : "TREFOIL",
      // depthCables and distanceCables intentionally missing
    ]

    when:
    Try<CableDeploymentInput, FactoryException> input =
        inputFactory.get(new EntityData(parameter, CableDeploymentInput))

    then:
    input.failure
    input.exception.get() instanceof FactoryException
  }

  def "A CableDeploymentInputFactory throws FactoryException for invalid lineUuid"() {
    given:
    def inputFactory = new CableDeploymentInputFactory()
    Map<String, String> parameter = [
      "uuid" : "b8c873a4-e32b-4b10-879e-01c53375e578",
      "lineUuid" : "not-a-valid-uuid",
      "layoutFormation" : "TREFOIL",
      "depthCables" : "-0.5",
      "distanceCables" : "0.1",
    ]

    when:
    Try<CableDeploymentInput, FactoryException> input =
        inputFactory.get(new EntityData(parameter, CableDeploymentInput))

    then:
    input.failure
    input.exception.get() instanceof FactoryException
  }

  def "A CableDeploymentInputFactory accepts absent optional cable-bed fields"() {
    given:
    def inputFactory = new CableDeploymentInputFactory()
    Map<String, String> parameter = [
      "uuid" : "b8c873a4-e32b-4b10-879e-01c53375e578",
      "lineUuid" : "2a254a7b-9385-4d1a-bf5c-468f322e65fc",
      "layoutFormation" : "TREFOIL",
      "depthCables" : "-0.5",
      "distanceCables" : "0.1",
    ]

    when:
    def input = inputFactory.get(new EntityData(parameter, CableDeploymentInput))

    then:
    input.success
    input.data.get().with {
      cableBedWidth == null
      cableBedHeight == null
      cableBedSoilType == null
    }
  }

  def "A CableDeploymentInputFactory rejects incomplete cable-bed fields"() {
    given:
    def inputFactory = new CableDeploymentInputFactory()
    Map<String, String> parameter = [
      "uuid" : "b8c873a4-e32b-4b10-879e-01c53375e578",
      "lineUuid" : "2a254a7b-9385-4d1a-bf5c-468f322e65fc",
      "layoutFormation" : "TREFOIL",
      "depthCables" : "-0.5",
      "distanceCables" : "0.1",
      "cableBedWidth" : "0.8",
      "cableBedSoilType" : "ca5f58dd-e444-4daf-83a7-78529548fa77",
    ]

    when:
    def input = inputFactory.get(new EntityData(parameter, CableDeploymentInput))

    then:
    input.failure
    input.exception.get() instanceof FactoryException
  }

  def "A CableDeploymentInputFactory rejects an invalid cable-bed SoilType UUID"() {
    given:
    def inputFactory = new CableDeploymentInputFactory()
    Map<String, String> parameter = [
      "uuid" : "b8c873a4-e32b-4b10-879e-01c53375e578",
      "lineUuid" : "2a254a7b-9385-4d1a-bf5c-468f322e65fc",
      "layoutFormation" : "TREFOIL",
      "depthCables" : "-0.5",
      "distanceCables" : "0.1",
      "cableBedWidth" : "0.8",
      "cableBedHeight" : "0.3",
      "cableBedSoilType" : "not-a-valid-uuid",
    ]

    when:
    def input = inputFactory.get(new EntityData(parameter, CableDeploymentInput))

    then:
    input.failure
    input.exception.get() instanceof FactoryException
  }
}