/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static tech.units.indriya.unit.Units.METRE

import edu.ie3.datamodel.exceptions.SourceException
import edu.ie3.datamodel.models.input.connector.CableDeploymentInput
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class RawGridSourceJoinCableDeploymentsTest extends Specification {

  def "joinCableDeploymentsByLine groups deployments by line UUID"() {
    given:
    def lineUuid1 = UUID.randomUUID()
    def lineUuid2 = UUID.randomUUID()
    def lines = new java.util.HashMap<UUID, Object>()
    lines.put(lineUuid1, null)
    lines.put(lineUuid2, null)
    def deployment1 = new CableDeploymentInput(UUID.randomUUID(), lineUuid1, "TREFOIL",
        Quantities.getQuantity(-0.5, METRE), Quantities.getQuantity(0.1, METRE))
    def deployment2 = new CableDeploymentInput(UUID.randomUUID(), lineUuid1, "FLAT",
        Quantities.getQuantity(-0.6, METRE), Quantities.getQuantity(0.1, METRE))
    def deployment3 = new CableDeploymentInput(UUID.randomUUID(), lineUuid2, "TREFOIL",
        Quantities.getQuantity(-0.5, METRE), Quantities.getQuantity(0.2, METRE))

    when:
    def result = RawGridSource.joinCableDeploymentsByLine(lines, [
      deployment1,
      deployment2,
      deployment3
    ])

    then:
    result.size() == 2
    result.get(lineUuid1).size() == 2
    result.get(lineUuid1).contains(deployment1)
    result.get(lineUuid1).contains(deployment2)
    result.get(lineUuid2).size() == 1
    result.get(lineUuid2).contains(deployment3)
  }

  def "joinCableDeploymentsByLine returns empty map for no deployments"() {
    given:
    def lineUuid = UUID.randomUUID()
    def lines = new java.util.HashMap<UUID, Object>()
    lines.put(lineUuid, null)

    when:
    def result = RawGridSource.joinCableDeploymentsByLine(lines, [])

    then:
    result.isEmpty()
  }

  def "joinCableDeploymentsByLine throws SourceException for unknown line UUID"() {
    given:
    def knownLineUuid = UUID.randomUUID()
    def unknownLineUuid = UUID.randomUUID()
    def lines = new java.util.HashMap<UUID, Object>()
    lines.put(knownLineUuid, null)
    def deployment = new CableDeploymentInput(UUID.randomUUID(), unknownLineUuid, "TREFOIL",
        Quantities.getQuantity(-0.5, METRE), Quantities.getQuantity(0.1, METRE))

    when:
    RawGridSource.joinCableDeploymentsByLine(lines, [deployment])

    then:
    SourceException ex = thrown()
    ex.message.contains("Cable deployment references unknown line with uuid " + unknownLineUuid)
  }
}
