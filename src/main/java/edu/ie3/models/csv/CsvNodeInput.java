/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.models.GermanVoltageLevel;
import edu.ie3.util.geo.GeoUtils;
import org.locationtech.jts.geom.Point;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.ElectricPotential;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

public class CsvNodeInput {
  // id;in_operation;is_slack;operates_from;operates_until;scenario;subnet;v_rated;v_target;volt_lvl
  @CsvBindByName String uuid;
  @CsvBindByName int tid;
  @CsvBindByName Double x_coord;
  @CsvBindByName Double y_coord;
  @CsvBindByName String id;
  @CsvBindByName String is_slack;

  @CsvBindByName
  @CsvDate("yyyy-MM-dd HH:mm:ss")
  LocalDateTime operates_from;

  @CsvBindByName
  @CsvDate("yyyy-MM-dd HH:mm:ss")
  LocalDateTime operates_until;

  @CsvBindByName int subnet;
  @CsvBindByName Double v_rated;
  @CsvBindByName Integer v_target;
  @CsvBindByName String volt_lvl;

  public CsvNodeInput() {}

  public CsvNodeInput(
      String uuid,
      int tid,
      Double x_coord,
      Double y_coords,
      String id,
      String is_slack,
      LocalDateTime operates_from,
      LocalDateTime operates_until,
      int subnet,
      Double v_rated,
      Integer v_target,
      String volt_lvl) {
    this.uuid = uuid;
    this.tid = tid;
    this.x_coord = x_coord;
    this.y_coord = y_coords;
    this.id = id;
    this.is_slack = is_slack;
    this.operates_from = operates_from;
    this.operates_until = operates_until;
    this.subnet = subnet;
    this.v_rated = v_rated;
    this.v_target = v_target;
    this.volt_lvl = volt_lvl;
  }

  public NodeInput toNodeInput() {
    Point coordinate = GeoUtils.xyToPoint(x_coord, y_coord);
    UUID uuid = UUID.fromString(getUuid());
    ComparableQuantity<ElectricPotential> vRatedQuantity =
        Quantities.getQuantity(v_rated, StandardUnits.RATED_VOLTAGE_MAGNITUDE);
    ComparableQuantity<Dimensionless> vTargetQuantity =
        Quantities.getQuantity(v_target, StandardUnits.TARGET_VOLTAGE_MAGNITUDE);
    ZonedDateTime startDate =
        operates_from != null ? ZonedDateTime.of(operates_from, ZoneId.of("UTC")) : null;
    ZonedDateTime endDate =
        operates_until != null ? ZonedDateTime.of(operates_until, ZoneId.of("UTC")) : null;
    OperationTime operationTime =
        OperationTime.builder().withStart(startDate).withEnd(endDate).build();
    VoltageLevel voltageLevel = GermanVoltageLevel.of(volt_lvl);
    Boolean isSlackBool = null;
    if (is_slack.equals("t")) isSlackBool = true;
    if (is_slack.equals("f")) isSlackBool = false;

    NodeInput node =
        new NodeInput(
            uuid,
            operationTime,
            null,
            id,
            vTargetQuantity,
            vRatedQuantity,
            isSlackBool,
            coordinate,
            voltageLevel,
            subnet);
    return node;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public int getTid() {
    return tid;
  }

  public void setTid(int tid) {
    this.tid = tid;
  }

  public Double getX_coord() {
    return x_coord;
  }

  public void setX_coord(Double x_coord) {
    this.x_coord = x_coord;
  }

  public Double getY_coord() {
    return y_coord;
  }

  public void setY_coord(Double y_coord) {
    this.y_coord = y_coord;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String isIs_slack() {
    return is_slack;
  }

  public void setIs_slack(String is_slack) {
    this.is_slack = is_slack;
  }

  public LocalDateTime getOperates_from() {
    return operates_from;
  }

  public void setOperates_from(LocalDateTime operates_from) {
    this.operates_from = operates_from;
  }

  public LocalDateTime getOperates_until() {
    return operates_until;
  }

  public void setOperates_until(LocalDateTime operates_until) {
    this.operates_until = operates_until;
  }

  public int getSubnet() {
    return subnet;
  }

  public void setSubnet(int subnet) {
    this.subnet = subnet;
  }

  public Double getV_rated() {
    return v_rated;
  }

  public void setV_rated(Double v_rated) {
    this.v_rated = v_rated;
  }

  public Integer getV_target() {
    return v_target;
  }

  public void setV_target(Integer v_target) {
    this.v_target = v_target;
  }

  public String getVolt_lvl() {
    return volt_lvl;
  }

  public void setVolt_lvl(String volt_lvl) {
    this.volt_lvl = volt_lvl;
  }
}
