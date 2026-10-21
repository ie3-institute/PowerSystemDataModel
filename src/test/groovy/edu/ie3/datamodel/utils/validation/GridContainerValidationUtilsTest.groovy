/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import static edu.ie3.datamodel.models.StandardUnits.TEMPERATURE
import static edu.ie3.util.quantities.PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN
import static edu.ie3.util.quantities.PowerSystemUnits.KELVIN_METRE_PER_WATT
import static tech.units.indriya.unit.Units.METRE

import edu.ie3.datamodel.exceptions.InvalidGridException
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.SoilLayer
import edu.ie3.datamodel.models.input.SoilType
import edu.ie3.datamodel.models.input.connector.CableDeploymentInput
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.models.input.container.SoilData
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData as GTD
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity
import edu.ie3.util.quantities.interfaces.ThermalResistivity
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.GeometryFactory
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime
import javax.measure.quantity.Temperature

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

  def "checkSoilData accepts consistent, gap-free and non-overlapping soil data"() {
    given:
    def type = validSoilType()
    def layer1 = soilLayer(type.uuid, 0d, -2d)
    def layer2 = soilLayer(type.uuid, -2d, -4d)
    def soilData = new SoilData([type] as Set, [layer1, layer2] as Set)

    when:
    def results = GridContainerValidationUtils.checkSoilData(soilData)

    then:
    results.size() == 1
    !results.any { it.failure }
  }

  def "checkSoilData reports overlapping soil layers at the same coordinate"() {
    given:
    def type = validSoilType()
    def layer1 = soilLayer(type.uuid, 0d, -2d)
    def layer2 = soilLayer(type.uuid, -1d, -3d)
    def soilData = new SoilData([type] as Set, [layer1, layer2] as Set)

    when:
    def results = GridContainerValidationUtils.checkSoilData(soilData)

    then:
    def failure = results.find { it.failure }
    failure.exception.get().message.contains("Overlapping soil layers")
  }

  def "checkSoilData reports gaps in soil coverage at the same coordinate"() {
    given:
    def type = validSoilType()
    def layer1 = soilLayer(type.uuid, 0d, -2d)
    def layer2 = soilLayer(type.uuid, -3d, -5d)
    def soilData = new SoilData([type] as Set, [layer1, layer2] as Set)

    when:
    def results = GridContainerValidationUtils.checkSoilData(soilData)

    then:
    def failure = results.find { it.failure }
    failure.exception.get().message.contains("Gap in soil coverage")
  }

  def "checkSoilData reports invalid soil types"() {
    given:
    def type = validSoilType().copy().id(null).build()
    def soilData = new SoilData([type] as Set, [] as Set)

    when:
    def results = GridContainerValidationUtils.checkSoilData(soilData)

    then:
    def failure = results.find { it.failure }
    failure.exception.get().message.contains("No ID assigned")
  }

  def "checkSoilData reports null soil data"() {
    when:
    def results = GridContainerValidationUtils.checkSoilData(null)

    then:
    results.size() == 1
    results.first().failure
  }

  private static Geometry soilGeometry() {
    return new GeometryFactory().createPolygon(
        ([
          new Coordinate(0d, 0d),
          new Coordinate(1d, 0d),
          new Coordinate(1d, 1d),
          new Coordinate(0d, 1d),
          new Coordinate(0d, 0d)
        ] as Coordinate[]))
  }

  private static SoilLayer soilLayer(UUID soilType, double zFrom, double zTo) {
    return new SoilLayer(
        UUID.randomUUID(),
        soilGeometry(),
        Quantities.getQuantity(zFrom, METRE),
        Quantities.getQuantity(zTo, METRE),
        soilType)
  }

  private static SoilType validSoilType() {
    ComparableQuantity<ThermalResistivity> wet = Quantities.getQuantity(0.30d, KELVIN_METRE_PER_WATT)
    ComparableQuantity<ThermalResistivity> dry = Quantities.getQuantity(0.40d, KELVIN_METRE_PER_WATT)
    ComparableQuantity<SpecificHeatCapacity> capacity = Quantities.getQuantity(1.5d, JOULE_PER_CUBIC_METRE_KELVIN)
    ComparableQuantity<Temperature> critical = Quantities.getQuantity(15d, TEMPERATURE)
    return new SoilType(UUID.randomUUID(), "loam", wet, dry, capacity, critical)
  }
}
