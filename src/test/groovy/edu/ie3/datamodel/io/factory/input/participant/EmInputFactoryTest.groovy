/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input.participant

import edu.ie3.datamodel.io.factory.input.NodeAssetInputEntityData
import edu.ie3.datamodel.models.ControlStrategy
import edu.ie3.datamodel.models.EmControlStrategy
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.EmInput
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.utils.Try
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Dimensionless
import java.time.ZonedDateTime

class EmInputFactoryTest extends Specification {

  def "A EmInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new EmInputFactory()
    def expectedClasses = [EmInput] as List

    expect:
    inputFactory.supportedClasses == expectedClasses
  }

  def "A EmInputFactory should parse a valid EmInput correctly"() {
    given:
    def inputFactory = new EmInputFactory()
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "connectedassets" : "4e840ea0-fb72-422e-942f-4111312e9914 a17aa6f0-e663-4186-ac34-a7b68573938b",
      "controlstrategy" : "self_optimization"
    ]
    def inputClass = EmInput
    def nodeInput = Mock(NodeInput)
    def operatorInput = Mock(OperatorInput)

    when:
    Try<EmInput> input = inputFactory.get(
        new NodeAssetInputEntityData(parameter, inputClass, operatorInput, nodeInput))

    then:
    input.success
    input.data().getClass() == inputClass
    input.data().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert operationTime.endDate.present
      assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PowerSystemUnits.PU), Quantities.getQuantity(1d, PowerSystemUnits.PU))
        ] as TreeSet)
      }
      assert connectedAssets == [
        UUID.fromString("4e840ea0-fb72-422e-942f-4111312e9914"),
        UUID.fromString("a17aa6f0-e663-4186-ac34-a7b68573938b")
      ] as UUID[]
      assert controlStrategy == EmControlStrategy.SELF_OPTIMIZATION
    }
  }

  def "A EmInputFactory should parse a valid EmInput with zero connected assets correctly"() {
    given:
    def inputFactory = new EmInputFactory()
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"    : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"   : "2019-12-31T23:59:00+01:00[Europe/Berlin]",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "connectedassets" : "",
      "controlstrategy" : "self_optimization"
    ]
    def inputClass = EmInput
    def nodeInput = Mock(NodeInput)
    def operatorInput = Mock(OperatorInput)

    when:
    Try<EmInput> input = inputFactory.get(
        new NodeAssetInputEntityData(parameter, inputClass, operatorInput, nodeInput))

    then:
    input.success
    input.data().getClass() == inputClass
    input.data().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert operationTime.endDate.present
      assert operationTime.endDate.get() == ZonedDateTime.parse(parameter["operatesuntil"])
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PowerSystemUnits.PU), Quantities.getQuantity(1d, PowerSystemUnits.PU))
        ] as TreeSet)
      }
      assert connectedAssets == [] as UUID[]
      assert controlStrategy == EmControlStrategy.SELF_OPTIMIZATION
    }
  }

  def "A EmInputFactory should use a default control strategy if value cannot be parsed"() {
    given:
    def inputFactory = new EmInputFactory()
    Map<String, String> parameter = [
      "uuid"            : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"              : "TestID",
      "qcharacteristics": "cosPhiFixed:{(0.0,1.0)}",
      "connectedassets" : "4e840ea0-fb72-422e-942f-4111312e9914",
      "controlstrategy" : " -- invalid --"
    ]
    def inputClass = EmInput
    def nodeInput = Mock(NodeInput)
    def operatorInput = Mock(OperatorInput)

    when:
    Try<EmInput> input = inputFactory.get(
        new NodeAssetInputEntityData(parameter, inputClass, operatorInput, nodeInput))

    then:
    input.success
    input.data().getClass() == inputClass
    input.data().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.empty
      assert operationTime.endDate.empty
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert qCharacteristics.with {
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Dimensionless, Dimensionless>(Quantities.getQuantity(0d, PowerSystemUnits.PU), Quantities.getQuantity(1d, PowerSystemUnits.PU))
        ] as TreeSet)
      }
      assert connectedAssets == [
        UUID.fromString("4e840ea0-fb72-422e-942f-4111312e9914")
      ] as UUID[]
      assert controlStrategy == ControlStrategy.DefaultControlStrategies.NO_CONTROL_STRATEGY
    }
  }
}
