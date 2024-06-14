/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.test.helper.EntityMap.map

import edu.ie3.datamodel.io.factory.input.ConnectorInputEntityData
import edu.ie3.datamodel.io.factory.input.participant.SystemParticipantEntityData
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.system.EvInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import edu.ie3.test.common.SystemParticipantTestData as sptd
import spock.lang.Specification

class SystemParticipantSourceTest extends Specification {

  def "An SystemParticipantSource participantEnricher should work as expected"() {
    given:
    def entityData = new ConnectorInputEntityData(["operators": "", "node": sptd.participantNode.uuid.toString(), "em": sptd.emInput.uuid.toString()], LineInput, GridTestData.nodeA, GridTestData.nodeB)
    def operators = map([OperatorInput.NO_OPERATOR_ASSIGNED])
    def nodes = map([sptd.participantNode])
    def emUnits = map([sptd.emInput])

    when:
    def actual = SystemParticipantSource.participantEnricher.apply(new Try.Success<>(entityData), operators, nodes, emUnits)

    then:
    actual.success
    actual.data.get().operatorInput == OperatorInput.NO_OPERATOR_ASSIGNED
    actual.data.get().node == sptd.participantNode
    actual.data.get().em == Optional.of(sptd.emInput)
  }

  def "An SystemParticipantSource can enrich SystemParticipantEntityData with SystemParticipantTypeInput correctly"() {
    given:
    def entityData = new SystemParticipantEntityData(["type": sptd.evTypeInput.uuid.toString()], EvInput, sptd.evInput.node, sptd.emInput)
    def types = map([sptd.evTypeInput])

    when:
    def actual = SystemParticipantSource.enrichTypes(types).apply(new Try.Success<>(entityData))

    then:
    actual.success
    actual.data.get().typeInput == sptd.evTypeInput
  }
}
