/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.json;

import com.couchbase.client.java.json.JsonObject;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Length;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

public class JsonMapper {
  private static Logger logger = LogManager.getLogger(JsonMapper.class);

  public static NodeInput toNodeInput(JsonObject object) {
    String uuidString = object.getString("uuid");
    Integer subnet = object.getInt("subnet");
    Double xCoord = object.getDouble("x_coord");
    Double yCoord = object.getDouble("y_coord");

    UUID uuid = UUID.fromString(uuidString);
    Point coordinate =
        (xCoord != null && yCoord != null) ? CoordinateUtils.xyCoordToPoint(xCoord, yCoord) : null;
    Quantity<Dimensionless> vTarget =
        Quantities.getQuantity(object.getInt("v_target"), StandardUnits.TARGET_VOLTAGE);
    ComparableQuantity<ElectricPotential> vRated =
        Quantities.getQuantity(object.getDouble("v_rated"), StandardUnits.V_RATED);
    ZonedDateTime operatesFrom = null; // how?
    ZonedDateTime operatesUntil = null; // how?
    Boolean isSlack = object.getBoolean("is_slack");
    String id = object.getString("id");
    VoltageLevel voltLvl = toVoltageLevel(object.getString("volt_lvl"));
    OperationTime operationTime =
        OperationTime.builder().withStart(operatesFrom).withEnd(operatesUntil).build();
    return new NodeInput(
        uuid, operationTime, null, id, vTarget, vRated, isSlack, coordinate, voltLvl, subnet);
  }

  public static Integer getTid(JsonObject jsonObject) {
    return jsonObject.getInt("tid");
  }

  public static VoltageLevel toVoltageLevel(String voltLevel) {
    switch (voltLevel) {
      case "NS":
        return GermanVoltageLevel.LV;
      case "MS":
        return GermanVoltageLevel.MV;
      case "HS":
        return GermanVoltageLevel.HV;
      case "HöS":
        return GermanVoltageLevel.EHV;
    }
    return null;
  }

  public static LineInput toLineInput(JsonObject object, NodeInput nodeA, NodeInput nodeB) {
    String uuidString = object.getString("uuid");
    Integer amount = object.getInt("amount");
    Double length = object.getDouble("length");
    String id = object.getString("id");
    ZonedDateTime operatesFrom = null; // how?
    ZonedDateTime operatesUntil = null; // how?
    Integer typeTid = object.getInt("type");
    String geoPosition = object.getString("st_astext");

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

  public static SwitchInput toSwitchInput(JsonObject object, NodeInput nodeA, NodeInput nodeB) {
    String uuidString = object.getString("uuid");
    String id = object.getString("id");
    Boolean closed = object.getBoolean("closed");
    ZonedDateTime operatesFrom = null; // how?
    ZonedDateTime operatesUntil = null; // how?

    UUID uuid = UUID.fromString(uuidString);
    OperationTime operationTime =
        OperationTime.builder().withStart(operatesFrom).withEnd(operatesUntil).build();

    SwitchInput switchInput = new SwitchInput(uuid, operationTime, null, id, nodeA, nodeB, closed);
    return null;
  }

  public static Transformer2WInput toTransformer2W(
      JsonObject object, NodeInput nodeA, NodeInput nodeB) {
    String uuidString = object.getString("uuid");
    Integer amount = object.getInt("amount");
    Boolean autoTap = object.getBoolean("auto_tap");
    String id = object.getString("id");
    ZonedDateTime operatesFrom = null; // how?
    ZonedDateTime operatesUntil = null; // how?
    Integer tapPos = object.getInt("tap_pos");
    Integer typeTid = object.getInt("type");

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
      JsonObject object, NodeInput nodeA, NodeInput nodeB, NodeInput nodeC) {
    String uuidString = object.getString("uuid");
    Integer amount = object.getInt("amount");
    Boolean autoTap = object.getBoolean("auto_tap");
    String id = object.getString("id");
    ZonedDateTime operatesFrom = null; // how?
    ZonedDateTime operatesUntil = null; // how?
    Integer tapPos = object.getInt("tap_pos");
    Integer typeTid = object.getInt("type");

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

  public static Integer identifyNodeA(JsonObject object) {
    return object.getInt("node_a");
  }

  public static Integer identifyNodeB(JsonObject object) {
    return object.getInt("node_b");
  }

  public static Integer identifyNodeC(JsonObject object) {
    return object.getInt("node_c");
  }
}
