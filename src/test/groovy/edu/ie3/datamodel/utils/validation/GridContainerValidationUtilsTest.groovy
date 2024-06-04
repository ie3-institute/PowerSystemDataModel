/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.exceptions.InvalidGridException
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData as GTD
import spock.lang.Shared
import spock.lang.Specification

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
    time                            || expectedResult
    Optional.empty()                || Try.Success.empty()
    Optional.of(start.plusHours(1)) || Try.Success.empty()
  }

  def "The GridContainerValidationUtils should return an exception if the grid is not properly connected"() {
    when:
    def actual = GridContainerValidationUtils.checkConnectivity(limitedElements, time as Optional<ZonedDateTime>)

    then:
    actual.exception.get().message == expectedException.message

    where:
    time                            || expectedException
    Optional.of(start)              || new InvalidGridException("The grid contains unconnected elements for time " + start + ": " + [
      GTD.nodeE.uuid,
      GTD.nodeG.uuid
    ])
    Optional.of(start.plusHours(3)) || new InvalidGridException("The grid contains unconnected elements for time " + start.plusHours(3) + ": " + [
      GTD.nodeD.uuid,
      GTD.nodeF.uuid,
      GTD.nodeG.uuid
    ])
  }
}
