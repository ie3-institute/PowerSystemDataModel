/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.input.container

import static edu.ie3.util.quantities.PowerSystemUnits.METRE

import edu.ie3.datamodel.models.input.AssetInput
import edu.ie3.datamodel.models.input.UniqueInputEntity
import edu.ie3.datamodel.models.input.connector.CableDeploymentInput
import edu.ie3.test.common.ComplexTopology
import edu.ie3.test.common.GridTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities


class RawGridElementsTest extends Specification {

  def "A valid collection of asset entities can be used to build a valid instance of RawGridElements"() {
    given:
    def rawGrid = ComplexTopology.grid.rawGrid

    when:
    def newlyCreatedRawGrid = new RawGridElements(rawGrid.allEntitiesAsList())

    then:
    newlyCreatedRawGrid == rawGrid
  }

  def "A RawGridElements' copy method should work as expected"() {
    given:
    def emptyRawGrid = new RawGridElements([] as Set, [] as Set, [] as Set, [] as Set, [] as Set, [] as Set)

    when:
    def modifiedRawGrid = emptyRawGrid.copy()
        .nodes(Set.of(GridTestData.nodeA))
        .lines(Set.of(GridTestData.lineAtoB))
        .transformers2Ws(Set.of(GridTestData.transformerBtoD))
        .transformer3Ws(Set.of(GridTestData.transformerAtoBtoC))
        .switches(Set.of(GridTestData.switchAtoB))
        .measurementUnits(Set.of(GridTestData.measurementUnitInput))
        .build()

    then:
    modifiedRawGrid.nodes.first() == GridTestData.nodeA
    modifiedRawGrid.lines.first() == GridTestData.lineAtoB
    modifiedRawGrid.transformer2Ws.first() == GridTestData.transformerBtoD
    modifiedRawGrid.transformer3Ws.first() == GridTestData.transformerAtoBtoC
    modifiedRawGrid.switches.first() == GridTestData.switchAtoB
    modifiedRawGrid.measurementUnits.first() == GridTestData.measurementUnitInput

    and:
    UUID lineUuid = UUID.randomUUID()
    def deployment = new Object()
    def deploymentsByLineRaw = new HashMap()
    RawGridElements base = new RawGridElements(new ArrayList<AssetInput>())

    when:
    deploymentsByLineRaw.put(lineUuid, new ArrayList(Collections.singletonList(deployment)))
    RawGridElements elements = base.copy().cableDeploymentsByLine((Map) deploymentsByLineRaw).build()

    then:
    elements.getCableDeploymentsByLine().containsKey(lineUuid)
    elements.getCableDeploymentsByLine().get(lineUuid).size() == 1

    when:
    def rawMap = (Map) elements.getCableDeploymentsByLine()
    rawMap.put(UUID.randomUUID(), Collections.singletonList(deployment))

    then:
    thrown(UnsupportedOperationException)

    when:
    def entryList = (List) rawMap.get(lineUuid)
    entryList.add(deployment)

    then:
    thrown(UnsupportedOperationException)
  }
  def "The List constructor should extract CableDeploymentInput instances and group them by line UUID"() {
    given:
    def baseGrid = ComplexTopology.grid.rawGrid
    def lineUuid = UUID.randomUUID()
    def deployment1 = new CableDeploymentInput(
        UUID.randomUUID(),
        lineUuid,
        "TREFOIL",
        Quantities.getQuantity(-0.8, METRE),
        Quantities.getQuantity(0.05, METRE))
    def deployment2 = new CableDeploymentInput(
        UUID.randomUUID(),
        lineUuid,
        "FLAT",
        Quantities.getQuantity(-1.0, METRE),
        Quantities.getQuantity(0.1, METRE))

    def entities = new ArrayList<UniqueInputEntity>(baseGrid.allEntitiesAsList())
    entities.add(deployment1)
    entities.add(deployment2)

    when:
    def rawGrid = new RawGridElements(entities)

    then:
    rawGrid.cableDeploymentsByLine.size() == 1
    rawGrid.cableDeploymentsByLine.containsKey(lineUuid)
    rawGrid.cableDeploymentsByLine.get(lineUuid).size() == 2
    rawGrid.cableDeploymentsByLine.get(lineUuid).contains(deployment1)
    rawGrid.cableDeploymentsByLine.get(lineUuid).contains(deployment2)
  }
}