/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.hibernate;

import edu.ie3.dataconnection.source.csv.CsvTypeSource;
import edu.ie3.models.GermanVoltageLevel;
import edu.ie3.models.OperationTime;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.VoltageLevel;
import edu.ie3.models.hibernate.input.*;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.connector.LineInput;
import edu.ie3.models.input.connector.SwitchInput;
import edu.ie3.models.input.connector.Transformer2WInput;
import edu.ie3.models.input.connector.Transformer3WInput;
import edu.ie3.models.input.connector.type.LineTypeInput;
import edu.ie3.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.util.HashMap;
import java.util.Optional;
import javax.measure.Quantity;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Length;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

public class HibernateMapper {

  public static final HashMap<Integer, NodeInput> tidToNode = new HashMap<>();

  public static NodeInput toNodeInput(HibernateNodeInput hibernateNode) {
    if (tidToNode.containsKey(hibernateNode.getTid())) return tidToNode.get(hibernateNode.getTid());
    int subnet = hibernateNode.getSubnet();
    Quantity<Dimensionless> vTarget =
        Quantities.getQuantity(hibernateNode.getvTarget(), StandardUnits.TARGET_VOLTAGE);
    ComparableQuantity<ElectricPotential> vRated =
        Quantities.getQuantity(hibernateNode.getvRated(), StandardUnits.V_RATED);

    String id = hibernateNode.getId();
    VoltageLevel voltLvl = toVoltageLevel(hibernateNode.getVoltLvl());
    OperationTime operationTime =
        OperationTime.builder()
            .withStart(hibernateNode.getOperatesFrom())
            .withEnd(hibernateNode.getOperatesUntil())
            .build();
    NodeInput nodeInput =
        new NodeInput(
            hibernateNode.getUuid(),
            operationTime,
            null,
            id,
            vTarget,
            vRated,
            hibernateNode.isSlack(),
            hibernateNode.getGeoPosition(),
            voltLvl,
            subnet);
    tidToNode.put(hibernateNode.getTid(), nodeInput);
    return nodeInput;
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
      default:
        return null;
    }
  }

  public static LineInput toLineInput(HibernateLineInput hibernateLine) {
    String id = hibernateLine.getId();
    int amount = hibernateLine.getAmount();
    Integer typeTid = hibernateLine.getType();
    LineTypeInput lineTypeInput = CsvTypeSource.getLineType(typeTid);
    Double length = hibernateLine.getLength();

    OperationTime operationTime =
        OperationTime.builder()
            .withStart(hibernateLine.getOperatesFrom())
            .withEnd(hibernateLine.getOperatesUntil())
            .build();
    Quantity<Length> lengthQuantity = Quantities.getQuantity(length, PowerSystemUnits.KILOMETRE);
    Optional<String> olmCharacteristic = Optional.empty();

    LineTypeInput type = CsvTypeSource.getLineType(typeTid);

    LineInput line =
        new LineInput(
            hibernateLine.getUuid(),
            operationTime,
            null,
            id,
            toNodeInput(hibernateLine.getNodeA()),
            toNodeInput(hibernateLine.getNodeB()),
            amount,
            type,
            lengthQuantity,
            hibernateLine.getGeoPosition(),
            olmCharacteristic);
    return line;
  }

  public static SwitchInput toSwitchInput(HibernateSwitchInput hibernateSwitch) {
    String id = hibernateSwitch.getId();
    Boolean closed = hibernateSwitch.isClosed();

    OperationTime operationTime =
        OperationTime.builder()
            .withStart(hibernateSwitch.getOperatesFrom())
            .withEnd(hibernateSwitch.getOperatesUntil())
            .build();

    return new SwitchInput(
        hibernateSwitch.getUuid(),
        operationTime,
        null,
        id,
        toNodeInput(hibernateSwitch.getNodeA()),
        toNodeInput(hibernateSwitch.getNodeB()),
        closed);
  }

  public static Transformer2WInput toTransformer2W(
      HibernateTransformer2WInput hibernateTransformer) {
    String id = hibernateTransformer.getId();
    int amount = hibernateTransformer.getAmount();
    boolean autoTap = hibernateTransformer.isAutoTap();
    int tapPos = hibernateTransformer.getTapPos();
    Integer typeTid = hibernateTransformer.getType();

    OperationTime operationTime =
        OperationTime.builder()
            .withStart(hibernateTransformer.getOperatesFrom())
            .withEnd(hibernateTransformer.getOperatesUntil())
            .build();
    Transformer2WTypeInput transformer2WTypeInput = CsvTypeSource.getTrafo2WType(typeTid);

    Transformer2WInput transformer2WInput =
        new Transformer2WInput(
            hibernateTransformer.getUuid(),
            operationTime,
            null,
            id,
            toNodeInput(hibernateTransformer.getNodeA()),
            toNodeInput(hibernateTransformer.getNodeB()),
            amount,
            transformer2WTypeInput,
            tapPos,
            autoTap);
    return transformer2WInput;
  }

  public static Transformer3WInput toTransformer3W(
      HibernateTransformer3WInput hibernateTransformer) {
    String id = hibernateTransformer.getId();
    int amount = hibernateTransformer.getAmount();
    boolean autoTap = hibernateTransformer.isAutoTap();
    int tapPos = hibernateTransformer.getTapPos();
    Integer typeTid = hibernateTransformer.getType();

    OperationTime operationTime =
        OperationTime.builder()
            .withStart(hibernateTransformer.getOperatesFrom())
            .withEnd(hibernateTransformer.getOperatesUntil())
            .build();
    Transformer3WTypeInput transformer3WTypeInput = CsvTypeSource.getTrafo3WType(typeTid);

    Transformer3WInput transformer3WInput =
        new Transformer3WInput(
            hibernateTransformer.getUuid(),
            operationTime,
            null,
            id,
            toNodeInput(hibernateTransformer.getNodeA()),
            toNodeInput(hibernateTransformer.getNodeB()),
            toNodeInput(hibernateTransformer.getNodeC()),
            amount,
            transformer3WTypeInput,
            tapPos,
            autoTap);
    return transformer3WInput;
  }
}
