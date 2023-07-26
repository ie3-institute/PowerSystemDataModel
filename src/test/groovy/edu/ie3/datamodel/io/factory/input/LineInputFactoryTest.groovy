/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.CharacteristicPoint
import edu.ie3.datamodel.utils.GridAndGeoUtils
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import org.locationtech.jts.geom.LineString
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Speed
import java.time.ZonedDateTime

import static edu.ie3.util.quantities.PowerSystemUnits.METRE_PER_SECOND
import static edu.ie3.util.quantities.PowerSystemUnits.PU

class LineInputFactoryTest extends Specification implements FactoryTestHelper {
  def "A LineInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new LineInputFactory()
    def expectedClasses = [LineInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A LineInputFactory should parse a valid LineInput correctly"() {
    given: "a system participant input type factory and model data"
    def inputFactory = new LineInputFactory()
    Map<String, String> parameter = [
      "uuid"             : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"     : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"    : "",
      "id"               : "TestID",
      "paralleldevices"  : "2",
      "length"           : "3",
      "geoposition"      : "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "olmcharacteristic": "olm:{(0.0,1.0)}"
    ]
    def inputClass = LineInput
    def operatorInput = Mock(OperatorInput)
    def nodeInputA = Mock(NodeInput)
    nodeInputA.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION
    def nodeInputB = Mock(NodeInput)
    nodeInputB.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION
    def typeInput = Mock(LineTypeInput)

    when:
    Try<LineInput, FactoryException> input = inputFactory.get(new TypedConnectorInputEntityData<LineTypeInput>(parameter, inputClass, operatorInput, nodeInputA, nodeInputB, typeInput))

    then:
    input.success
    input.data().getClass() == inputClass
    input.data().with {
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

  def "A LineInputFactory should parse a valid LineInput without olm characteristic correctly"() {
    given: "a system participant input type factory and model data"
    def inputFactory = new LineInputFactory()
    Map<String, String> parameter = [
      "uuid"             : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"     : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"    : "",
      "id"               : "TestID",
      "paralleldevices"  : "2",
      "length"           : "3",
      "geoposition"      : "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "olmcharacteristic": ""
    ]
    def inputClass = LineInput
    def operatorInput = Mock(OperatorInput)
    def nodeInputA = Mock(NodeInput)
    nodeInputA.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION
    def nodeInputB = Mock(NodeInput)
    nodeInputB.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION
    def typeInput = Mock(LineTypeInput)

    when:
    Try<LineInput, FactoryException> input = inputFactory.get(new TypedConnectorInputEntityData<LineTypeInput>(parameter, inputClass, operatorInput, nodeInputA, nodeInputB, typeInput))

    then:
    input.success
    input.data().getClass() == inputClass
    input.data().with {
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

  def "A LineInputFactory should parse a valid LineInput with different geoPosition strings correctly"() {
    given: "a line input factory and model data"
    def inputFactory = new LineInputFactory()
    Map<String, String> parameter = [
      "uuid"             : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesfrom"     : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesuntil"    : "",
      "id"               : "TestID",
      "paralleldevices"  : "2",
      "length"           : "3",
      "geoposition"      : geoLineString,
      "olmcharacteristic": "olm:{(0.0,1.0)}"
    ]
    def inputClass = LineInput
    def operatorInput = Mock(OperatorInput)
    def nodeInputA = Mock(NodeInput)
    nodeInputA.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION
    def nodeInputB = Mock(NodeInput)
    nodeInputB.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION
    def typeInput = Mock(LineTypeInput)

    when:
    Try<LineInput, FactoryException> input = inputFactory.get(new TypedConnectorInputEntityData<LineTypeInput>(parameter, inputClass, operatorInput, nodeInputA, nodeInputB, typeInput))

    then:
    input.success
    input.data().getClass() == inputClass
    input.data().with {
      assert geoPosition == GridAndGeoUtils.buildSafeLineString(getGeometry(parameter["geoposition"]) as LineString)
    }

    where:
    geoLineString                                                                                                                         | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228]]}"                                           | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228]]}" | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.311111, 51.49228],[7.511111, 51.49228]]}" | _
  }
}
