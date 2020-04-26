package edu.ie3.datamodel.utils

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.test.common.GridTestData
import org.geolatte.geom.M
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.PrecisionModel
import org.locationtech.jts.io.geojson.GeoJsonReader
import spock.lang.Specification
import tec.uom.se.quantity.Quantities
import tec.uom.se.unit.Units

import static edu.ie3.util.quantities.PowerSystemUnits.PU

// EQUALS IS IMPORTANT! DO NOT CHANGE EQUALS TO == !!!!


class GridAndGeoUtilsTest extends Specification {

    def "The GridAndGeoUtils should build a line string with two exact equal geo coordinates correctly avoiding the known bug in jts geometry"() {
        // for bug details see https://github.com/locationtech/jts/issues/531 for details

//        given:


        expect:
        def p1 = GridTestData.geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [7.39249992,51.81000137] }") as Point
        def p2 = GridTestData.geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [7.39249992,51.81000137] }") as Point

        println p1.equals(p2)
        println p1.equalsExact(p2)
        println p1.equalsTopo(p2)

        PrecisionModel precisionModel = p1.getPrecisionModel()
        int SRID = p1.getSRID()
        Coordinate p1Coord = p1.getCoordinate()
        double increaseValue = 1e-13
        double p1X = p1Coord.getX() + increaseValue
        double p1Y = p1Coord.getY() + increaseValue
        double p1Z = p1Coord.getZ() + increaseValue
        Coordinate safeCoordinate = new Coordinate(p1X, p1Y, p1Z)


        def p3 = new Point(safeCoordinate, precisionModel, SRID)

        println p2.equals(p3)
        println p2.equalsExact(p3)
        println p2.equalsTopo(p3)

        Coordinate[] c12 = [
                p1.getCoordinate(),
                p2.getCoordinate()]

        Coordinate[] c23 = [
                p2.getCoordinate(),
                p3.getCoordinate()]

        def lineString12_1 = new LineString(c12, precisionModel, SRID)
        def lineString12_2 = new LineString(c12, precisionModel, SRID)

        println lineString12_1.equals(lineString12_2)

        def lineString23_1 = new LineString(c23, precisionModel, SRID)
        def lineString23_2 = new LineString(c23, precisionModel, SRID)

        println lineString23_1.equals(lineString23_2)

        println Double.MIN_VALUE

//
//        LineString line1 = (LineString) new GeoJsonReader()
//                .read("{ \"type\": \"LineString\", \"coordinates\": [[7.39249992,51.81000137], [7.39249992,51.81000137]]}") as LineString
//
//        LineString line2 = (LineString) new GeoJsonReader()
//                .read("{ \"type\": \"LineString\", \"coordinates\": [[7.39249992,51.81000137], [7.39249992,51.81000137]]}") as LineString
//
//        line1 == line2


    }

    def "Test"() {
        expect:

        def nodeA = new NodeInput(
                UUID.fromString("9e37ce48-9650-44ec-b888-c2fd182aff01"),
                "node_f",
                OperatorInput.NO_OPERATOR_ASSIGNED,
                OperationTime.notLimited(),
                Quantities.getQuantity(1d, PU),
                false,
                NodeInput.DEFAULT_GEO_POSITION,
                GermanVoltageLevelUtils.LV,
                6)

        def nodeB = new NodeInput(
                UUID.fromString("9e37ce48-9650-44ec-b888-c2fd182aff01"),
                "node_f",
                OperatorInput.NO_OPERATOR_ASSIGNED,
                OperationTime.notLimited(),
                Quantities.getQuantity(1d, PU),
                false,
                NodeInput.DEFAULT_GEO_POSITION,
                GermanVoltageLevelUtils.LV,
                6)

        def line1 = GridAndGeoUtils.buildLineStringBetweenNodes(nodeA, nodeB)
        def line2 = GridAndGeoUtils.buildLineStringBetweenNodes(nodeA, nodeB)

        println line1.equals(line2)
    }


    def "Test12"() {
        expect:
        LineInput lineCtoD_1 = new LineInput(
                UUID.fromString("91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
                "test_line_CtoD",
                GridTestData.profBroccoli,
                GridTestData.defaultOperationTime,
                GridTestData.nodeC,
                GridTestData.nodeD,
                2,
                GridTestData.lineTypeInputCtoD,
                Quantities.getQuantity(3, Units.METRE),
                GridTestData.geoJsonReader.read("{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.492528], [7.5511111, 51.492528],[7.411111, 51.492528],[7.411111, 51.492528]]}") as LineString,
                OlmCharacteristicInput.CONSTANT_CHARACTERISTIC)

        LineInput lineCtoD_2 = new LineInput(
                UUID.fromString("91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
                "test_line_CtoD",
                GridTestData.profBroccoli,
                GridTestData.defaultOperationTime,
                GridTestData.nodeC,
                GridTestData.nodeD,
                2,
                GridTestData.lineTypeInputCtoD,
                Quantities.getQuantity(3, Units.METRE),
                GridTestData.geoJsonReader.read("{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.49228],[7.411111, 51.492528], [7.5511111, 51.492528],[7.411111, 51.492528],[7.411111, 51.492528]]}") as LineString,
                OlmCharacteristicInput.CONSTANT_CHARACTERISTIC)

        lineCtoD_1.equals(lineCtoD_2)
        lineCtoD_2 == lineCtoD_1

    }

    def "test123"() {
        expect:

        def lineString = GridTestData.geoJsonReader.read("{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.411111, 51.492528]]}") as LineString
        List<Coordinate> s = new ArrayList<>(Arrays.asList(lineString.getCoordinates()))
        def k = new HashSet<>(s)
        println "stop"

    }

}
