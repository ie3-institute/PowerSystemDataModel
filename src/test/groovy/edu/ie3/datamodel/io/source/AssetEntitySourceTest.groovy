/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.test.helper.EntityMap.map

import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.io.factory.input.ConnectorInputEntityData
import edu.ie3.datamodel.io.factory.input.LineInputFactory
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import spock.lang.Specification

class AssetEntitySourceTest extends Specification {


  def "An AssetEntitySource assetEnricher should work as expected"() {
    given:
    def entityData = new EntityData(["operator": ""], LineInput)
    def operators = map([GridTestData.profBroccoli])

    when:
    def actual = AssetEntitySource.assetEnricher.apply(new Try.Success<>(entityData), operators)

    then:
    actual.success
    actual.data.get().operatorInput == OperatorInput.NO_OPERATOR_ASSIGNED
  }

  def "An AssetEntitySource nodeAssetEnricher should work as expected"() {
    given:
    def entityData = new EntityData(["operator": "", "node": GridTestData.nodeA.uuid.toString()], LineInput)
    def operators = map([GridTestData.profBroccoli])
    def nodes = map([GridTestData.nodeA, GridTestData.nodeB])

    when:
    def actual = AssetEntitySource.nodeAssetEnricher.apply(new Try.Success<>(entityData), operators, nodes)

    then:
    actual.success
    actual.data.get().node == GridTestData.nodeA
  }

  def "An AssetEntitySource connectorEnricher should work as expected"() {
    given:
    def entityData = new EntityData(["operator": "", "nodeA": GridTestData.nodeA.uuid.toString(), "nodeB": GridTestData.nodeB.uuid.toString()], LineInput)
    def operators = map([GridTestData.profBroccoli])
    def nodes = map([GridTestData.nodeA, GridTestData.nodeB])

    when:
    def actual = AssetEntitySource.connectorEnricher.apply(new Try.Success<>(entityData), operators, nodes)

    then:
    actual.success
    actual.data.get().nodeA == GridTestData.nodeA
    actual.data.get().nodeB == GridTestData.nodeB
  }

  def "An AssetEntitySource can return a stream of typed connector entities"() {
    given:
    def parameters = [
      "uuid": "92ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id": "test_line_AtoB",
      "operator": GridTestData.profBroccoli.uuid.toString(),
      "parallelDevices": "2",
      "length": "0.003",
      "geoPosition": "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "nodeA": GridTestData.nodeA.uuid.toString(),
      "nodeB": GridTestData.nodeB.uuid.toString(),
      "type": GridTestData.lineTypeInputCtoD.uuid.toString()
    ]
    def source = DummyDataSource.of(parameters)
    def operators = map([GridTestData.profBroccoli])
    def nodes = map([
      GridTestData.nodeA,
      GridTestData.nodeB
    ])
    def types = map([
      GridTestData.lineTypeInputCtoD
    ])

    when:
    def actual = AssetEntitySource.getTypedConnectorEntities(LineInput, source, new LineInputFactory(), operators, nodes, types).toList()

    then:
    actual.size() == 1
    actual.get(0).with {
      // we only want to test of enriching with nodes and type worked as expected
      assert it.nodeA == GridTestData.nodeA
      assert it.nodeB == GridTestData.nodeB
      assert it.type == GridTestData.lineTypeInputCtoD
    }
  }

  def "An AssetEntitySource can enrich ConnectorInputEntityData with AssetTypeInput correctly"() {
    given:
    def entityData = new ConnectorInputEntityData(["type": GridTestData.lineTypeInputCtoD.uuid.toString()], LineInput, GridTestData.nodeA, GridTestData.nodeB)
    def types = map([GridTestData.lineTypeInputCtoD])

    when:
    def actual = AssetEntitySource.enrich(types).apply(new Try.Success<>(entityData))

    then:
    actual.success
    actual.data.get().type == GridTestData.lineTypeInputCtoD
  }
}
