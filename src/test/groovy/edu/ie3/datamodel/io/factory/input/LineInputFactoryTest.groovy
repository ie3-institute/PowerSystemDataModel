/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import static edu.ie3.util.quantities.PowerSystemUnits.METRE_PER_SECOND
import static edu.ie3.util.quantities.PowerSystemUnits.PU

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
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime
import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Speed

class LineInputFactoryTest extends Specification implements FactoryTestHelper {

  @Shared private UUID operatorUuid = UUID.randomUUID()
  @Shared private OperatorInput operatorInput = Mock(OperatorInput)
  @Shared private UUID uuidNodeA = UUID.randomUUID()
  @Shared private NodeInput nodeInputA = Mock(NodeInput)
  @Shared private UUID uuidNodeB = UUID.randomUUID()
  @Shared private NodeInput nodeInputB = Mock(NodeInput)
  @Shared private UUID typeUuid = UUID.randomUUID()
  @Shared private LineTypeInput typeInput = Mock(LineTypeInput)
  @Shared private LineInputFactory inputFactory

  def setupSpec() {
    operatorInput.getUuid() >> operatorUuid

    nodeInputA.getUuid() >> uuidNodeA
    nodeInputA.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION

    nodeInputB.getUuid() >> uuidNodeB
    nodeInputB.getGeoPosition() >> NodeInput.DEFAULT_GEO_POSITION

    typeInput.getUuid() >> typeUuid
    inputFactory = new LineInputFactory(map(operatorInput), map(nodeInputA, nodeInputB), map(typeInput))
  }


  def "A LineInputFactory should contain exactly the expected class for parsing"() {
    given:
    def expectedClasses = [LineInput]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A LineInputFactory should parse a valid LineInput correctly"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil" : "",
      "id" : "TestID",
      "parallelDevices" : "2",
      "length" : "3",
      "geoPosition" : "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "olmCharacteristic": "olm:{(0.0,1.0)}",
      "operator": operatorUuid.toString(),
      "nodeA": uuidNodeA.toString(),
      "nodeB": uuidNodeB.toString(),
      "type": typeUuid.toString()
    ]

    when:
    Try<LineInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.success
    input.data.get().getClass() == LineInput
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime.startDate.present
      operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesFrom"])
      !operationTime.endDate.present
      operator == operatorInput
      id == parameter["id"]
      nodeA == nodeInputA
      nodeB == nodeInputB
      type == typeInput
      parallelDevices == Integer.parseInt(parameter["parallelDevices"])
      length == getQuant(parameter["length"], StandardUnits.LINE_LENGTH)
      geoPosition == getGeometry(parameter["geoPosition"])
      olmCharacteristic.with {
        uuid != null
        points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Speed, Dimensionless>(
              Quantities.getQuantity(0d, METRE_PER_SECOND),
              Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
    }
  }

  def "A LineInputFactory should parse a valid LineInput without olm characteristic correctly"() {
    given: "a system participant input type factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil" : "",
      "id" : "TestID",
      "parallelDevices" : "2",
      "length" : "3",
      "geoPosition" : "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}",
      "olmCharacteristic": "",
      "operator": operatorUuid.toString(),
      "nodeA": uuidNodeA.toString(),
      "nodeB": uuidNodeB.toString(),
      "type": typeUuid.toString()
    ]

    when:
    Try<LineInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.success
    input.data.get().getClass() == LineInput
    input.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      operationTime.startDate.present
      operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesFrom"])
      !operationTime.endDate.present
      operator == operatorInput
      id == parameter["id"]
      nodeA == nodeInputA
      nodeB == nodeInputB
      type == typeInput
      parallelDevices == Integer.parseInt(parameter["parallelDevices"])
      length == getQuant(parameter["length"], StandardUnits.LINE_LENGTH)
      geoPosition == getGeometry(parameter["geoPosition"])
      olmCharacteristic.with {
        uuid != null
        points == Collections.unmodifiableSortedSet([
          new CharacteristicPoint<Speed, Dimensionless>(
              Quantities.getQuantity(0d, METRE_PER_SECOND),
              Quantities.getQuantity(1d, PU))
        ] as TreeSet)
      }
    }
  }

  def "A LineInputFactory should parse a valid LineInput with different geoPosition strings correctly"() {
    given: "a line input factory and model data"
    Map<String, String> parameter = [
      "uuid" : "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "operatesFrom" : "2019-01-01T00:00:00+01:00[Europe/Berlin]",
      "operatesUntil" : "",
      "id" : "TestID",
      "parallelDevices" : "2",
      "length" : "3",
      "geoPosition" : geoLineString,
      "olmCharacteristic": "olm:{(0.0,1.0)}",
      "operator": operatorUuid.toString(),
      "nodeA": uuidNodeA.toString(),
      "nodeB": uuidNodeB.toString(),
      "type": typeUuid.toString()
    ]

    when:
    Try<LineInput, FactoryException> input = inputFactory.get(new HashMap<>(parameter))

    then:
    input.success
    input.data.get().getClass() == LineInput
    input.data.get().with {
      geoPosition == GridAndGeoUtils.buildSafeLineString(getGeometry(parameter["geoPosition"]) as LineString)
    }

    where:
    geoLineString | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228]]}" | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228],[7.411111, 51.49228]]}" | _
    "{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.49228],[7.311111, 51.49228],[7.511111, 51.49228]]}" | _
  }
}
