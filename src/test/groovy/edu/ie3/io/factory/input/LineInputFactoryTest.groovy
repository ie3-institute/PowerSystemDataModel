package edu.ie3.io.factory.input

import edu.ie3.models.StandardUnits
import edu.ie3.models.input.NodeInput
import edu.ie3.models.input.OperatorInput
import edu.ie3.models.input.connector.LineInput
import edu.ie3.models.input.connector.type.LineTypeInput
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

import java.time.ZonedDateTime

class LineInputFactoryTest extends Specification implements FactoryTestHelper {
    def "A LineInputFactory should contain exactly the expected class for parsing"() {
        given:
        def inputFactory = new LineInputFactory()
        def expectedClasses = [LineInput]

        expect:
        inputFactory.classes() == Arrays.asList(expectedClasses.toArray())
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
                "olmcharacteristic": "blub"
        ]
        def inputClass = LineInput
        def operatorInput = Mock(OperatorInput)
        def nodeInputA = Mock(NodeInput)
        def nodeInputB = Mock(NodeInput)
        def typeInput = Mock(LineTypeInput)

        when:
        Optional<LineInput> input = inputFactory.getEntity(new LineInputEntityData(parameter, inputClass, operatorInput, nodeInputA, nodeInputB, typeInput))

        then:
        input.present
        input.get().getClass() == inputClass
        ((LineInput) input.get()).with {
            assert uuid == UUID.fromString(parameter["uuid"])
            assert operationTime.startDate.present
            assert operationTime.startDate.get() == ZonedDateTime.parse(parameter["operatesfrom"])
            assert !operationTime.endDate.present
            assert operator == operatorInput
            assert id == parameter["id"]
            assert nodeA == nodeInputA
            assert nodeB == nodeInputB
            assert type == typeInput
            assert noOfParallelDevices == Integer.parseInt(parameter["paralleldevices"])
            assert length == getQuant(parameter["length"], StandardUnits.LINE_LENGTH)
            assert geoPosition == getGeometry(parameter["geoposition"])
            assert olmCharacteristic == Optional.of(parameter["olmcharacteristic"])
        }
    }
}
