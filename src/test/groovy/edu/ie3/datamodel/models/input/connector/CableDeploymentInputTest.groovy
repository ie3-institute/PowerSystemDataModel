/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.connector

import static tech.units.indriya.unit.Units.METRE

import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class CableDeploymentInputTest extends Specification {

  def "Cable deployments with identical values are equal and expose their values"() {
    given:
    def depth = Quantities.getQuantity(-0.5, METRE)
    def distance = Quantities.getQuantity(0.1, METRE)
    def uuid = UUID.randomUUID()
    def lineUuid = UUID.randomUUID()
    def firstDeployment = new CableDeploymentInput(uuid, lineUuid, "TREFOIL", depth, distance)
    def secondDeployment = new CableDeploymentInput(uuid, lineUuid, "TREFOIL", depth, distance)

    expect:
    firstDeployment == secondDeployment
    firstDeployment.layoutFormation == "TREFOIL"
    firstDeployment.depthCables == depth
    firstDeployment.distanceCables == distance
    firstDeployment.lineUuid == lineUuid
  }

  def "Cable deployments with different values are not equal"() {
    given:
    def firstDeployment = new CableDeploymentInput(
        UUID.randomUUID(), UUID.randomUUID(), "TREFOIL",
        Quantities.getQuantity(-0.5, METRE), Quantities.getQuantity(0.1, METRE))
    def secondDeployment = new CableDeploymentInput(
        UUID.randomUUID(), UUID.randomUUID(), "FLAT",
        Quantities.getQuantity(-0.6, METRE), Quantities.getQuantity(0.12, METRE))

    expect:
    firstDeployment != secondDeployment
    firstDeployment != null
    !firstDeployment.equals("some string")
  }

  def "Cable deployment string representation contains field values"() {
    given:
    def deployment = new CableDeploymentInput(
        UUID.randomUUID(), UUID.randomUUID(), "TREFOIL",
        Quantities.getQuantity(-0.5, METRE), Quantities.getQuantity(0.1, METRE))

    when:
    def result = deployment.toString()

    then:
    result
    result.contains("TREFOIL")
    result.contains("depthCables") || result.contains("-0.5") || result.contains("lineUuid")
  }
}