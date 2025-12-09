/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.source

import static edu.ie3.util.quantities.PowerSystemUnits.PU
import static tech.units.indriya.unit.Units.METRE_PER_SECOND

import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.datamodel.utils.GridAndGeoUtils
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import edu.ie3.test.helper.FactoryTestHelper
import org.locationtech.jts.geom.LineString
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime
import javax.measure.quantity.Dimensionless
import javax.measure.quantity.ElectricPotential
import javax.measure.quantity.Speed

class RawGridSourceTest extends Specification implements FactoryTestHelper {

  def "A NodeInput can be build correctly"() {
    given:
    def operatorUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operator"     : operatorUUID.toString(),
      "operatesfrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil": "",
      "id"           : "TestID",
      "vtarget"      : "2",
      "vrated"       : "3",
      "slack"        : "true",
      "geoposition"  : "{ \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] }",
      "voltlvl"      : "lv",
      "subnet"       : "7"
    ]

    def operatorInput = Mock(OperatorInput)
    def operators = [(operatorUUID) : operatorInput]

    when:
    def input = RawGridSource.nodeBuildFunction(operators).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert !operationTime.endDate.present
      assert operator == operatorInput
      assert id == parameter["id"]
      assert vTarget == getQuant(parameter["vtarget"], StandardUnits.TARGET_VOLTAGE_MAGNITUDE)
      assert slack
      assert geoPosition == getGeometry(parameter["geoposition"])
      assert voltLvl == GermanVoltageLevelUtils.parse(parameter["voltlvl"], getQuant(parameter["vrated"], StandardUnits.RATED_VOLTAGE_MAGNITUDE) as ComparableQuantity<ElectricPotential>)
      assert subnet == Integer.parseInt(parameter["subnet"])
    }
  }

  def "A LineInput can be build correctly"() {
    given:
    def operatorUUID = UUID.randomUUID()
    def nodeUuidA = UUID.randomUUID()
    def nodeUuidB = UUID.randomUUID()
    def typeUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"             : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operator"         : operatorUUID.toString(),
      "operatesfrom"     : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"    : "",
      "nodeA"            : nodeUuidA.toString(),
      "nodeB"            : nodeUuidB.toString(),
      "id"               : "TestID",
      "paralleldevices"  : "2",
      "length"           : "3",
      "geoposition"      : "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "olmcharacteristic": "olm:{(0.0,1.0)}",
      "type"             : typeUUID.toString()
    ]

    def operatorInput = Mock(OperatorInput)
    def operators = [(operatorUUID) : operatorInput]
    def nodeInputA = Mock(NodeInput)
    nodeInputA.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION
    def nodeInputB = Mock(NodeInput)
    nodeInputB.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION
    def nodes = [(nodeUuidA): nodeInputA, (nodeUuidB): nodeInputB]
    def typeInput = Mock(LineTypeInput)
    def types = [(typeUUID): typeInput]


    when:
    def input = RawGridSource.lineBuildFunction(operators, nodes, types).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert !operationTime.endDate.present
      assert operator == operatorInput
      assert id == parameter["id"]
      assert nodeA == nodeInputA
      assert nodeB == nodeInputB
      assert type == typeInput
      assert parallelDevices == Integer.parseInt(parameter["paralleldevices"])
      assert length == getQuant(parameter["length"], StandardUnits.LINE_LENGTH)
      assert geoPosition == getGeometry(parameter["geoposition"])
      olmCharacteristic.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Speed, Dimensionless>(
          Quantities.getQuantity(0d, METRE_PER_SECOND),
          Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
    }
  }

  def "A LineInput build function should parse a valid LineInput without olm characteristic correctly"() {
    given:
    def operatorUUID = UUID.randomUUID()
    def nodeUuidA = UUID.randomUUID()
    def nodeUuidB = UUID.randomUUID()
    def typeUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"             : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operator"         : operatorUUID.toString(),
      "operatesfrom"     : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"    : "",
      "nodeA"            : nodeUuidA.toString(),
      "nodeB"            : nodeUuidB.toString(),
      "id"               : "TestID",
      "paralleldevices"  : "2",
      "length"           : "3",
      "geoposition"      : "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "olmcharacteristic": "",
      "type"             : typeUUID.toString()
    ]

    def operatorInput = Mock(OperatorInput)
    def nodeInputA = Mock(NodeInput)
    nodeInputA.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION
    def nodeInputB = Mock(NodeInput)
    nodeInputB.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION
    def typeInput = Mock(LineTypeInput)

    def operators = [(operatorUUID) : operatorInput]
    def nodes = [(nodeUuidA): nodeInputA, (nodeUuidB): nodeInputB]
    def types = [(typeUUID): typeInput]

    when:
    def input = RawGridSource.lineBuildFunction(operators, nodes, types).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert !operationTime.endDate.present
      assert operator == operatorInput
      assert id == parameter["id"]
      assert nodeA == nodeInputA
      assert nodeB == nodeInputB
      assert type == typeInput
      assert parallelDevices == Integer.parseInt(parameter["paralleldevices"])
      assert length == getQuant(parameter["length"], StandardUnits.LINE_LENGTH)
      assert geoPosition == getGeometry(parameter["geoposition"])
      olmCharacteristic.with {
        assert uuid != null
        assert points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Speed, Dimensionless>(
          Quantities.getQuantity(0d, METRE_PER_SECOND),
          Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
    }
  }

  def "A LineInput build function should parse a valid LineInput with different geoPosition strings correctly"() {
    given:
    def operatorUUID = UUID.randomUUID()
    def nodeUuidA = UUID.randomUUID()
    def nodeUuidB = UUID.randomUUID()
    def typeUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"             : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operator"         : operatorUUID.toString(),
      "operatesfrom"     : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"    : "",
      "nodeA"            : nodeUuidA.toString(),
      "nodeB"            : nodeUuidB.toString(),
      "id"               : "TestID",
      "paralleldevices"  : "2",
      "length"           : "3",
      "geoposition"      : geoLineString,
      "olmcharacteristic": "olm:{(0.0,1.0)}",
      "type"             : typeUUID.toString()
    ]

    def inputClass = LineInput
    def operatorInput = Mock(OperatorInput)
    def nodeInputA = Mock(NodeInput)
    nodeInputA.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION
    def nodeInputB = Mock(NodeInput)
    nodeInputB.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION
    def typeInput = Mock(LineTypeInput)

    def operators = [(operatorUUID) : operatorInput]
    def nodes = [(nodeUuidA): nodeInputA, (nodeUuidB): nodeInputB]
    def types = [(typeUUID): typeInput]

    when:
    def input = RawGridSource.lineBuildFunction(operators, nodes, types).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().getClass() == inputClass
    input.data.get().with {
      assert geoPosition == GridAndGeoUtils.buildSafeLineString(getGeometry(parameter["geoposition"]) as LineString)
    }

    where:
    geoLineString                                                                                                                         | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228]]}"                                           | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228]]}" | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.311111, 51.49228],[7.511111, 51.49228]]}" | _
  }

  def "A Transformer2WInput can be build correctly"() {
    given:
    def operatorUUID = UUID.randomUUID()
    def nodeUuidA = UUID.randomUUID()
    def nodeUuidB = UUID.randomUUID()
    def typeUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"             : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operator"         : operatorUUID.toString(),
      "operatesfrom"     : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"    : "",
      "nodeA"            : nodeUuidA.toString(),
      "nodeB"            : nodeUuidB.toString(),
      "id"               : "TestID",
      "paralleldevices"  : "2",
      "tappos"           : "3",
      "autotap"          : "true",
      "type"             : typeUUID.toString()
    ]

    def operatorInput = Mock(OperatorInput)
    def operators = [(operatorUUID) : operatorInput]
    def nodeInputA = GridTestData.nodeA
    def nodeInputB = GridTestData.nodeB
    def nodes = [(nodeUuidA): nodeInputA, (nodeUuidB): nodeInputB]
    def typeInput = Mock(Transformer2WTypeInput)
    def types = [(typeUUID): typeInput]

    when:
    def input = RawGridSource.transformer2WBuildFunction(operators, nodes, types).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert !operationTime.endDate.present
      assert operator == operatorInput
      assert id == parameter["id"]
      assert nodeA == nodeInputA
      assert nodeB == nodeInputB
      assert type == typeInput
      assert parallelDevices == Integer.parseInt(parameter["paralleldevices"])
      assert tapPos == Integer.parseInt(parameter["tappos"])
      assert autoTap
    }
  }

  def "A Transformer2WInput build function should throw an IllegalArgumentException if nodeA is on the lower voltage side"() {
    given:
    def operatorUUID = UUID.randomUUID()
    def nodeUuidA = UUID.randomUUID()
    def nodeUuidB = UUID.randomUUID()
    def typeUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"           : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operator"       : operatorUUID.toString(),
      "operatesfrom"   : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"  : "",
      "nodeA"          : nodeUuidA.toString(),
      "nodeB"          : nodeUuidB.toString(),
      "id"             : "TestID",
      "paralleldevices": "2",
      "tappos"         : "3",
      "autotap"        : "true",
      "type"           : typeUUID.toString()
    ]
    def operatorInput = Mock(OperatorInput)
    def nodeInputA = GridTestData.nodeB
    def nodeInputB = GridTestData.nodeA
    def typeInput = Mock(Transformer2WTypeInput)

    def operators = [(operatorUUID) : operatorInput]
    def nodes = [(nodeUuidA): nodeInputA, (nodeUuidB): nodeInputB]
    def types = [(typeUUID): typeInput]

    when:
    def input = RawGridSource.transformer2WBuildFunction(operators, nodes, types).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.failure
    def e = input.exception.get()
    e.cause.class == IllegalArgumentException
    e.cause.message == "nodeA must be on the higher voltage side of the transformer"
  }

  def "A Transformer3WInput can be build correctly"() {
    given:
    def operatorUUID = UUID.randomUUID()
    def nodeInputA = GridTestData.nodeA
    def nodeInputB = GridTestData.nodeB
    def nodeInputC = GridTestData.nodeC
    def typeUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"             : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operator"         : operatorUUID.toString(),
      "operatesfrom"     : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"    : "",
      "nodeA"            : nodeInputA.uuid.toString(),
      "nodeB"            : nodeInputB.uuid.toString(),
      "nodeC"            : nodeInputC.uuid.toString(),
      "id"               : "TestID",
      "paralleldevices"  : "2",
      "tappos"           : "3",
      "autotap"          : "true",
      "type"             : typeUUID.toString()
    ]

    def operatorInput = Mock(OperatorInput)
    def operators = [(operatorUUID) : operatorInput]
    def nodes = [(nodeInputA.uuid): nodeInputA, (nodeInputB.uuid): nodeInputB, (nodeInputC.uuid): nodeInputC]
    def typeInput = Mock(Transformer3WTypeInput)
    def types = [(typeUUID): typeInput]

    when:
    def input = RawGridSource.transformer3WBuildFunction(operators, nodes, types).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert !operationTime.endDate.present
      assert operator == operatorInput
      assert id == parameter["id"]
      assert nodeA == nodeInputA
      assert nodeB == nodeInputB
      assert type == typeInput
      assert parallelDevices == Integer.parseInt(parameter["paralleldevices"])
      assert tapPos == Integer.parseInt(parameter["tappos"])
      assert autoTap
    }
  }

  def "A Transformer3WInput build function should throw an IllegalArgumentException if nodeA is on the lower voltage side"() {
    given:
    def operatorUUID = UUID.randomUUID()
    def nodeInputA = GridTestData.nodeC
    def nodeInputB = GridTestData.nodeB
    def nodeInputC = GridTestData.nodeA
    def typeUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"           : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operator"       : operatorUUID.toString(),
      "operatesfrom"   : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"  : "",
      "nodeA"          : nodeInputA.uuid.toString(),
      "nodeB"          : nodeInputB.uuid.toString(),
      "nodeC"          : nodeInputC.uuid.toString(),
      "id"             : "TestID",
      "paralleldevices": "2",
      "tappos"         : "3",
      "autotap"        : "true",
      "type"           : typeUUID.toString()
    ]
    def operatorInput = Mock(OperatorInput)
    def typeInput = Mock(Transformer3WTypeInput)

    def operators = [(operatorUUID) : operatorInput]
    def nodes = [(nodeInputA.uuid): nodeInputA, (nodeInputB.uuid): nodeInputB, (nodeInputC.uuid): nodeInputC]
    def types = [(typeUUID): typeInput]

    when:
    def input = RawGridSource.transformer3WBuildFunction(operators, nodes, types).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.failure
    def e = input.exception.get()
    e.cause.class == IllegalArgumentException
    e.cause.message == "Voltage level of node a must be greater than voltage level of node b and voltage level of node b must be greater than voltage level of node c"
  }

  def "A SwitchInput can be build correctly"() {
    given:
    def operatorUUID = UUID.randomUUID()
    def nodeUuidA = UUID.randomUUID()
    def nodeUuidB = UUID.randomUUID()
    def typeUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"             : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operator"         : operatorUUID.toString(),
      "operatesfrom"     : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"    : "",
      "nodeA"            : nodeUuidA.toString(),
      "nodeB"            : nodeUuidB.toString(),
      "id"               : "TestID",
      "closed"           : "true",
      "type"             : typeUUID.toString()
    ]

    def operatorInput = Mock(OperatorInput)
    def operators = [(operatorUUID) : operatorInput]
    def nodeInputA = Mock(NodeInput)
    def nodeInputB = Mock(NodeInput)
    def nodes = [(nodeUuidA): nodeInputA, (nodeUuidB): nodeInputB]

    when:
    def input = RawGridSource.switchBuildFunction(operators, nodes).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime.startDate.present
      assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
      assert !operationTime.endDate.present
      assert operator == operatorInput
      assert id == parameter["id"]
      assert nodeA == nodeInputA
      assert nodeB == nodeInputB
      assert closed
      assert parallelDevices == 1 // required, since a switch is a connector
    }
  }

  def "A MeasurementUnitInput can be build correctly"() {
    given:
    def operatorUUID = UUID.randomUUID()
    def nodeUUID = UUID.randomUUID()

    Map<String, String> parameter = [
      "uuid"         : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id"           : "TestID",
      "vmag"         : "true",
      "vang"         : "false",
      "p"            : "true",
      "q"            : "true",
      "operator"     : operatorUUID.toString(),
      "node"         : nodeUUID.toString()
    ]

    def operatorInput = Mock(OperatorInput)
    def operators = [(operatorUUID) : operatorInput]
    def nodeInput = Mock(NodeInput)
    def nodes = [(nodeUUID) : nodeInput]

    when:
    def input = RawGridSource.measurementBuildFunction(operators, nodes).apply(Try.Success.of(new EntityData(parameter)))

    then:
    input.success
    input.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert operationTime == OperationTime.notLimited()
      assert operator == operatorInput
      assert id == parameter["id"]
      assert node == nodeInput
      assert VMag
      assert !VAng
      assert p
      assert q
    }
  }
}
