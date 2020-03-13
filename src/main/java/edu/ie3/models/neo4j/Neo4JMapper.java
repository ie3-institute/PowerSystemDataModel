/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.neo4j;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import edu.ie3.dataconnection.source.csv.CsvTypeSource;
import edu.ie3.models.GermanVoltageLevel;
import edu.ie3.models.OperationTime;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.VoltageLevel;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.connector.LineInput;
import edu.ie3.models.input.connector.SwitchInput;
import edu.ie3.models.input.connector.Transformer2WInput;
import edu.ie3.models.input.connector.Transformer3WInput;
import edu.ie3.models.input.connector.type.LineTypeInput;
import edu.ie3.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import edu.ie3.utils.CoordinateUtils;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Length;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

public class Neo4JMapper {

  public static NodeInput toNodeInput(Neo4JNodeInput neo4JNode) {
    UUID uuid = UUID.fromString(neo4JNode.getUuid());
    Integer subnet = neo4JNode.getSubnet();
    Point coordinate =
        (neo4JNode.getX_coord() != null && neo4JNode.getY_coord() != null)
            ? CoordinateUtils.xyCoordToPoint(neo4JNode.getX_coord(), neo4JNode.getY_coord())
            : null;
    Quantity<Dimensionless> vTarget =
        Quantities.getQuantity(neo4JNode.getV_target(), StandardUnits.TARGET_VOLTAGE);
    ComparableQuantity<ElectricPotential> vRated =
        Quantities.getQuantity(neo4JNode.getV_rated(), StandardUnits.V_RATED);

    ZonedDateTime operatesFrom =
        neo4JNode.getOperates_from() != null
            ? ZonedDateTime.ofInstant(neo4JNode.getOperates_from().toInstant(), ZoneId.of("UTC"))
            : null;
    ZonedDateTime operatesUntil =
        neo4JNode.getOperates_until() != null
            ? ZonedDateTime.ofInstant(neo4JNode.getOperates_until().toInstant(), ZoneId.of("UTC"))
            : null;
    Boolean isSlack = neo4JNode.getIs_slack();
    String id = neo4JNode.getId();
    VoltageLevel voltLvl = GermanVoltageLevel.of(neo4JNode.getVolt_lvl());
    OperationTime operationTime =
        OperationTime.builder().withStart(operatesFrom).withEnd(operatesUntil).build();
    return new NodeInput(
        uuid, operationTime, null, id, vTarget, vRated, isSlack, coordinate, voltLvl, subnet);
  }

  public static LineInput toLineInput(Neo4JLineInput neo4JLine, NodeInput nodeA, NodeInput nodeB) {
    String uuidString = neo4JLine.getUuid();
    String id = neo4JLine.getId();
    ZonedDateTime operatesFrom =
        neo4JLine.getOperates_from() != null
            ? ZonedDateTime.ofInstant(neo4JLine.getOperates_from().toInstant(), ZoneId.of("UTC"))
            : null;
    ZonedDateTime operatesUntil =
        neo4JLine.getOperates_until() != null
            ? ZonedDateTime.ofInstant(neo4JLine.getOperates_until().toInstant(), ZoneId.of("UTC"))
            : null;
    Integer amount = neo4JLine.getAmount();
    Integer typeTid = neo4JLine.getType();
    LineTypeInput lineTypeInput = CsvTypeSource.getLineType(typeTid);
    Double length = neo4JLine.getLength();
    String geoPosition = neo4JLine.getGeo_position();

    UUID uuid = UUID.fromString(uuidString);
    OperationTime operationTime =
        OperationTime.builder().withStart(operatesFrom).withEnd(operatesUntil).build();
    Quantity<Length> lengthQuantity = Quantities.getQuantity(length, PowerSystemUnits.KILOMETRE);
    LineString lineString = CoordinateUtils.stringToLineString(geoPosition);
    Optional<String> olmCharacteristic = Optional.empty();

    LineTypeInput type = CsvTypeSource.getLineType(typeTid);

    LineInput line =
        new LineInput(
            uuid,
            operationTime,
            null,
            id,
            nodeA,
            nodeB,
            amount,
            type,
            lengthQuantity,
            lineString,
            olmCharacteristic);
    return line;
  }

  public static SwitchInput toSwitchInput(
      Neo4JSwitchInput neo4JSwitch, NodeInput nodeA, NodeInput nodeB) {
    String uuidString = neo4JSwitch.getUuid();
    String id = neo4JSwitch.getId();
    Boolean closed = neo4JSwitch.getClosed();
    ZonedDateTime operatesFrom =
        neo4JSwitch.getOperates_from() != null
            ? ZonedDateTime.ofInstant(neo4JSwitch.getOperates_from().toInstant(), ZoneId.of("UTC"))
            : null;
    ZonedDateTime operatesUntil =
        neo4JSwitch.getOperates_until() != null
            ? ZonedDateTime.ofInstant(neo4JSwitch.getOperates_until().toInstant(), ZoneId.of("UTC"))
            : null;

    UUID uuid = UUID.fromString(uuidString);
    OperationTime operationTime =
        OperationTime.builder().withStart(operatesFrom).withEnd(operatesUntil).build();

    return new SwitchInput(uuid, operationTime, null, id, nodeA, nodeB, closed);
  }

  public static Transformer2WInput toTransformer2W(
      Neo4JTransformer2WInput neo4JTransformer, NodeInput nodeA, NodeInput nodeB) {
    String uuidString = neo4JTransformer.getUuid();
    String id = neo4JTransformer.getId();
    ZonedDateTime operatesFrom =
        neo4JTransformer.getOperates_from() != null
            ? ZonedDateTime.ofInstant(
                neo4JTransformer.getOperates_from().toInstant(), ZoneId.of("UTC"))
            : null;
    ZonedDateTime operatesUntil =
        neo4JTransformer.getOperates_until() != null
            ? ZonedDateTime.ofInstant(
                neo4JTransformer.getOperates_until().toInstant(), ZoneId.of("UTC"))
            : null;
    Integer amount = neo4JTransformer.getAmount();
    Boolean autoTap = neo4JTransformer.getAuto_tap();
    Integer tapPos = neo4JTransformer.getTap_pos();
    Integer typeTid = neo4JTransformer.getType();

    UUID uuid = UUID.fromString(uuidString);
    OperationTime operationTime =
        OperationTime.builder().withStart(operatesFrom).withEnd(operatesUntil).build();
    Transformer2WTypeInput transformer2WTypeInput = CsvTypeSource.getTrafo2WType(typeTid);

    Transformer2WInput transformer2WInput =
        new Transformer2WInput(
            uuid,
            operationTime,
            null,
            id,
            nodeA,
            nodeB,
            amount,
            transformer2WTypeInput,
            tapPos,
            autoTap);
    return transformer2WInput;
  }

  public static Transformer3WInput toTransformer3W(
      Neo4JTransformer3WInput neo4JTransformer, NodeInput nodeA, NodeInput nodeB, NodeInput nodeC) {
    String uuidString = neo4JTransformer.getUuid();
    String id = neo4JTransformer.getId();
    ZonedDateTime operatesFrom =
        neo4JTransformer.getOperates_from() != null
            ? ZonedDateTime.ofInstant(
                neo4JTransformer.getOperates_from().toInstant(), ZoneId.of("UTC"))
            : null;
    ZonedDateTime operatesUntil =
        neo4JTransformer.getOperates_until() != null
            ? ZonedDateTime.ofInstant(
                neo4JTransformer.getOperates_until().toInstant(), ZoneId.of("UTC"))
            : null;
    Integer amount = neo4JTransformer.getAmount();
    Boolean autoTap = neo4JTransformer.getAuto_tap();
    Integer tapPos = neo4JTransformer.getTap_pos();
    Integer typeTid = neo4JTransformer.getType();

    UUID uuid = UUID.fromString(uuidString);
    OperationTime operationTime =
        OperationTime.builder().withStart(operatesFrom).withEnd(operatesUntil).build();
    Transformer3WTypeInput transformer3WTypeInput = CsvTypeSource.getTrafo3WType(typeTid);

    Transformer3WInput transformer3WInput =
        new Transformer3WInput(
            uuid,
            operationTime,
            null,
            id,
            nodeA,
            nodeB,
            nodeC,
            amount,
            transformer3WTypeInput,
            tapPos,
            autoTap);
    return transformer3WInput;
  }

  public static Integer[] getNodeTids(Neo4JTransformer3WInput trafo1, Neo4JTransformer3WInput trafo2) {
    Integer node1A = trafo1.getNodeA().getTid();
    Integer node1B = trafo1.getNodeB().getTid();
    Integer node2A = trafo2.getNodeA().getTid();
    Integer node2B = trafo2.getNodeB().getTid();

    if (node1A.equals(node2B)) return new Integer[] {node2A, node2B, node1B};
    if (node1B.equals(node2A)) return new Integer[] {node1A, node1B, node2B};
    return null;
  }

  public static Transformer2WInput getBoundaryInjectionTransformer(){
    Transformer2WInput transformer2WInput =
            new Transformer2WInput(
                    UUID.fromString("01fc415b-8909-3d9e-a4c6-505155d95c32"),
                    OperationTime.notLimited(),
                    null,
                    "1000_Boundary_Injection",
                    null,
                    null,
                    1,
                    null,
                    0,
                    false);
    return transformer2WInput;
  }
}
