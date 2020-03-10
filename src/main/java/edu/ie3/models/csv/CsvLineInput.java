/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import com.vividsolutions.jts.geom.LineString;
import edu.ie3.dataconnection.source.csv.CsvTypeSource;
import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.connector.LineInput;
import edu.ie3.models.input.connector.type.LineTypeInput;
import edu.ie3.util.quantities.PowerSystemUnits;
import edu.ie3.utils.CoordinateUtils;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import javax.measure.quantity.Length;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

public class CsvLineInput {
  //
  // uuid;tid;amount;st_astext;id;in_operation;length;node_a;node_b;operates_from;operates_until;scenario;type

  @CsvBindByName String uuid;
  @CsvBindByName Integer tid;
  @CsvBindByName Integer amount;
  @CsvBindByName String st_astext;
  @CsvBindByName String id;
  @CsvBindByName Double length;
  @CsvBindByName Integer node_a;
  @CsvBindByName Integer node_b;

  @CsvBindByName
  @CsvDate("yyyy-MM-dd HH:mm:ss")
  LocalDateTime operates_from;

  @CsvBindByName
  @CsvDate("yyyy-MM-dd HH:mm:ss")
  LocalDateTime operates_until;

  @CsvBindByName Integer type;

  public CsvLineInput() {}

  public CsvLineInput(
      String uuid,
      Integer tid,
      Integer amount,
      String st_astext,
      String id,
      Double length,
      Integer node_a,
      Integer node_b,
      LocalDateTime operates_from,
      LocalDateTime operates_until,
      Integer type) {
    this.uuid = uuid;
    this.tid = tid;
    this.amount = amount;
    this.st_astext = st_astext;
    this.id = id;
    this.length = length;
    this.node_a = node_a;
    this.node_b = node_b;
    this.operates_from = operates_from;
    this.operates_until = operates_until;
    this.type = type;
  }

  public LineInput toLineInput(Map<Integer, NodeInput> tidToNode) {
    UUID uuid = UUID.fromString(getUuid());
    LineString lineString = CoordinateUtils.stringToLineString(st_astext);
    ComparableQuantity<Length> lengthQuantity =
        Quantities.getQuantity(length, PowerSystemUnits.KILOMETRE);
    Optional olmCharacterisitcs = Optional.empty();
    ZonedDateTime startDate =
        operates_from != null ? ZonedDateTime.of(operates_from, ZoneId.of("UTC")) : null;
    ZonedDateTime endDate =
        operates_until != null ? ZonedDateTime.of(operates_until, ZoneId.of("UTC")) : null;
    OperationTime operationTime =
        OperationTime.builder().withStart(startDate).withEnd(endDate).build();
    LineTypeInput lineType = CsvTypeSource.getLineType(type);

    LineInput line =
        new LineInput(
            uuid,
            operationTime,
            null,
            id,
            tidToNode.get(node_a),
            tidToNode.get(node_b),
            amount,
            lineType,
            lengthQuantity,
            lineString,
            olmCharacterisitcs);
    return line;
  }

  public List<Integer> getRequiredNodes() {
    return Arrays.asList(node_a, node_b);
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Integer getTid() {
    return tid;
  }

  public void setTid(Integer tid) {
    this.tid = tid;
  }

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public String getSt_astext() {
    return st_astext;
  }

  public void setSt_astext(String st_astext) {
    this.st_astext = st_astext;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Double getLength() {
    return length;
  }

  public void setLength(Double length) {
    this.length = length;
  }

  public Integer getNode_a() {
    return node_a;
  }

  public void setNode_a(Integer node_a) {
    this.node_a = node_a;
  }

  public Integer getNode_b() {
    return node_b;
  }

  public void setNode_b(Integer node_b) {
    this.node_b = node_b;
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

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }
}
