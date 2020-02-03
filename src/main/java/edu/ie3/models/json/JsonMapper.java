package edu.ie3.models.json;

import com.couchbase.client.java.json.JsonObject;
import com.vividsolutions.jts.geom.Point;
import edu.ie3.models.GermanVoltageLevel;
import edu.ie3.models.OperationTime;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.VoltageLevel;
import edu.ie3.models.input.NodeInput;
import edu.ie3.utils.CoordinateUtils;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricPotential;
import java.time.ZonedDateTime;

public class JsonMapper {

    public static NodeInput toNodeInput(JsonObject jsonObj) {
        Integer subnet = jsonObj.getInt("subnet");
        Double xCoord = jsonObj.getDouble("x_coord");
        Double yCoord = jsonObj.getDouble("y_coord");

        Point coordinate = (xCoord != null && yCoord != null) ? CoordinateUtils.xyCoordToPoint(xCoord, yCoord) : null;
        Quantity<Dimensionless> vTarget = Quantities.getQuantity(jsonObj.getInt("v_target"), StandardUnits.TARGET_VOLTAGE);
        ComparableQuantity<ElectricPotential> vRated = Quantities.getQuantity(jsonObj.getDouble("v_target"), StandardUnits.V_RATED);
        ZonedDateTime operatesFrom = null; //how?
        ZonedDateTime operatesUntil = null; //how?
        Boolean isSlack = jsonObj.getBoolean("is_slack");
        String id = jsonObj.getString("id");
        VoltageLevel voltLvl = toVoltageLevel(jsonObj.getString("volt_lvl"));
        OperationTime operationTime = OperationTime.builder().withStart(operatesFrom).withEnd(operatesUntil).build();
        return new NodeInput(null, operationTime, null, id, vTarget, vRated, isSlack, coordinate, voltLvl, subnet);
    }

    public static Integer getTid(JsonObject jsonObject) {
        return jsonObject.getInt("tid");
    }

    public static VoltageLevel toVoltageLevel(String voltLevel) {
        switch (voltLevel) {
            case "NS": return GermanVoltageLevel.LV;
            case "MS": return GermanVoltageLevel.MV;
            case "HS": return GermanVoltageLevel.HV;
            case "HöS": return GermanVoltageLevel.EHV;
        }
        return null;
    }
}
