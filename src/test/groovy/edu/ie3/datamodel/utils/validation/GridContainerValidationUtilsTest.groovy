/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import static tech.units.indriya.unit.Units.METRE

import edu.ie3.datamodel.exceptions.InvalidGridException
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.connector.CableDeploymentInput
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData as GTD
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime

class GridContainerValidationUtilsTest extends Specification {
  @Shared
  private ZonedDateTime start

  @Shared
  private RawGridElements limitedElements

  def setupSpec() {
    start = ZonedDateTime.now()

    def operationTimeFrame1 = OperationTime.builder().withStart(start).withEnd(start.plusHours(2)).build()
    def operationTimeFrame2 = OperationTime.builder().withStart(start.plusHours(1)).withEnd(start.plusHours(3)).build()

    def nodes = [
      GTD.nodeC,
      GTD.nodeD,
      GTD.nodeE,
      GTD.nodeF,
      GTD.nodeG
    ] as Set

    def lines = [
      GTD.lineCtoD.copy().operationTime(operationTimeFrame1).build(),
      GTD.lineFtoG.copy().operationTime(operationTimeFrame2).build()
    ] as Set

    def transformers = [
      GTD.transformerCtoF.copy().operationTime(operationTimeFrame1).build(),
      GTD.transformerCtoE.copy().operationTime(operationTimeFrame2).build()
    ] as Set

    limitedElements = new RawGridElements(nodes, lines, transformers, [] as Set, [] as Set, [] as Set)
  }

  def "The GridContainerValidationUtils should check the connectivity for all operation intervals correctly"() {
    when:
    def actual = GridContainerValidationUtils.checkConnectivity(limitedElements)

    then:
    actual.size() == 4
    actual.get(0).failure
    actual.get(1).success
    actual.get(2).success
    actual.get(3).failure

    actual.get(0).exception.get().message == "The grid contains unconnected elements for time " + start + ": " + [
      GTD.nodeE.uuid,
      GTD.nodeG.uuid
    ]
    actual.get(3).exception.get().message == "The grid contains unconnected elements for time " + start.plusHours(3) + ": " + [
      GTD.nodeD.uuid,
      GTD.nodeF.uuid,
      GTD.nodeG.uuid
    ]
  }

  def "The GridContainerValidationUtils should check the connectivity correctly"() {
    when:
    def actual = GridContainerValidationUtils.checkConnectivity(limitedElements, time as Optional<ZonedDateTime>)

    then:
    actual == expectedResult

    where:
    time || expectedResult
    Optional.empty() || Try.Success.empty()
    Optional.of(start.plusHours(1)) || Try.Success.empty()
  }

  def "The GridContainerValidationUtils should return an exception if the grid is not properly connected"() {
    when:
    def actual = GridContainerValidationUtils.checkConnectivity(limitedElements, time as Optional<ZonedDateTime>)

    then:
    actual.exception.get().message == expectedException.message

    where:
    time || expectedException
    Optional.of(start) || new InvalidGridException("The grid contains unconnected elements for time " + start + ": " + [
      GTD.nodeE.uuid,
      GTD.nodeG.uuid
    ])
    Optional.of(start.plusHours(3)) || new InvalidGridException("The grid contains unconnected elements for time " + start.plusHours(3) + ": " + [
      GTD.nodeD.uuid,
      GTD.nodeF.uuid,
      GTD.nodeG.uuid
    ])
  }

  def "The GridContainerValidationUtils should return an exception if the grid is not properly connected, because a switch is open"() {
    given:
    def nodeA = GTD.nodeA.copy().operationTime(OperationTime.notLimited()).build()
    def nodeB = GTD.nodeB.copy().operationTime(OperationTime.notLimited()).build()

    def switchAtoB = GTD.switchAtoB.copy()
        .nodeA(nodeA)
        .nodeB(nodeB)
        .operationTime(OperationTime.notLimited())
        .closed(false)
        .build()

    def rawGrid = new RawGridElements([nodeA, nodeB] as Set, [] as Set, [] as Set, [] as Set, [switchAtoB] as Set, [] as Set)

    when:
    def actual = GridContainerValidationUtils.checkConnectivity(rawGrid, Optional.of(start) as Optional<ZonedDateTime>)

    then:
    actual.exception.get().message == "The grid contains unconnected elements for time "+start+": [47d29df0-ba2d-4d23-8e75-c82229c5c758]"
  }

  def "The GridContainerValidationUtils validates cable deployments and their line references"() {
    given:
    def line = GTD.lineAtoB
    def deployment = new CableDeploymentInput(
        UUID.randomUUID(),
        line.uuid,
        layoutFormation,
        Quantities.getQuantity(-0.5d, METRE),
        Quantities.getQuantity(0.1d, METRE))
    def rawGrid = new RawGridElements(
        [line.nodeA, line.nodeB] as Set,
        [line] as Set,
        [] as Set,
        [] as Set,
        [] as Set,
        [] as Set,
        [(lineUuid): [deployment]])

    when:
    def results = GridContainerValidationUtils.checkRawGridElements(rawGrid)

    then:
    results.findAll {
      it.failure
    }.any {
      it.exception.get().message.contains(expectedMessage)
    } == failureExpected

    where:
    lineUuid || layoutFormation || failureExpected || expectedMessage
    GTD.lineAtoB.uuid || "TREFOIL" || false || "Cable deployment"
    GTD.lineAtoB.uuid || "" || true || "Layout formation cannot be empty"
    UUID.randomUUID() || "TREFOIL" || true || "Cable deployment references unknown line"
  }
}
