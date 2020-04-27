/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import edu.ie3.dataconnection.source.csv.CsvTypeSource;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CsvTransformer2WInput {
  //
  // tid;amount;auto_tap;id;in_operation;node_a;node_b;operates_from;operates_until;scenario;tap_pos;type;uuid

  @CsvBindByName String uuid;
  @CsvBindByName Integer tid;
  @CsvBindByName Integer amount;
  @CsvBindByName String id;
  @CsvBindByName String auto_tap;
  @CsvBindByName Integer node_a;
  @CsvBindByName Integer node_b;

  @CsvBindByName
  @CsvDate("yyyy-MM-dd HH:mm:ss")
  LocalDateTime operates_from;

  @CsvBindByName
  @CsvDate("yyyy-MM-dd HH:mm:ss")
  LocalDateTime operates_until;

  @CsvBindByName Integer tap_pos;
  @CsvBindByName Integer type;

  public CsvTransformer2WInput() {}

  public CsvTransformer2WInput(
      String uuid,
      Integer tid,
      Integer amount,
      String id,
      String auto_tap,
      Integer node_a,
      Integer node_b,
      LocalDateTime operates_from,
      LocalDateTime operates_until,
      Integer tap_pos,
      Integer type) {
    this.uuid = uuid;
    this.tid = tid;
    this.amount = amount;
    this.id = id;
    this.auto_tap = auto_tap;
    this.node_a = node_a;
    this.node_b = node_b;
    this.operates_from = operates_from;
    this.operates_until = operates_until;
    this.tap_pos = tap_pos;
    this.type = type;
  }

  public Transformer2WInput toTransformer2WInput(Map<Integer, NodeInput> tidToNode) {
    UUID uuid = UUID.fromString(getUuid());
    ZonedDateTime startDate =
        operates_from != null ? ZonedDateTime.of(operates_from, ZoneId.of("UTC")) : null;
    ZonedDateTime endDate =
        operates_until != null ? ZonedDateTime.of(operates_until, ZoneId.of("UTC")) : null;
    OperationTime operationTime =
        OperationTime.builder().withStart(startDate).withEnd(endDate).build();
    Transformer2WTypeInput transformer2WType = CsvTypeSource.getTrafo2WType(type);
    Boolean autoTapBool = null;
    if (auto_tap.equals("t")) autoTapBool = true;
    if (auto_tap.equals("f")) autoTapBool = false;

    Transformer2WInput transformer2WInput =
        new Transformer2WInput(
            uuid,
            operationTime,
            null,
            id,
            tidToNode.get(node_a),
            tidToNode.get(node_b),
            amount,
            transformer2WType,
            tap_pos,
            autoTapBool);
    return transformer2WInput;
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAuto_tap() {
    return auto_tap;
  }

  public void setAuto_tap(String auto_tap) {
    this.auto_tap = auto_tap;
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

  public Integer getTap_pos() {
    return tap_pos;
  }

  public void setTap_pos(Integer tap_pos) {
    this.tap_pos = tap_pos;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }
}
