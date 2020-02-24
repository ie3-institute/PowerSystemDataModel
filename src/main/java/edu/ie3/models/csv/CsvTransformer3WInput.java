/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import edu.ie3.dataconnection.source.csv.CsvTypeSource;
import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.connector.Transformer3WInput;
import edu.ie3.models.input.connector.type.Transformer3WTypeInput;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CsvTransformer3WInput {
  //
  // tid;amount;auto_tap;id;in_operation;node_a;node_b;node_c;operates_from;operates_until;scenario;tap_pos;type;uuid

  @CsvBindByName String uuid;
  @CsvBindByName Integer tid;
  @CsvBindByName Integer amount;
  @CsvBindByName String auto_tap;
  @CsvBindByName String id;
  @CsvBindByName Integer node_a;
  @CsvBindByName Integer node_b;
  @CsvBindByName Integer node_c;

  @CsvBindByName
  @CsvDate("yyyy-MM-dd HH:mm:ss")
  LocalDateTime operates_from;

  @CsvBindByName
  @CsvDate("yyyy-MM-dd HH:mm:ss")
  LocalDateTime operates_until;

  @CsvBindByName Integer tap_pos;
  @CsvBindByName Integer type;

  public CsvTransformer3WInput() {}

  public CsvTransformer3WInput(
      String uuid,
      Integer tid,
      Integer amount,
      String auto_tap,
      String id,
      Integer node_a,
      Integer node_b,
      Integer node_c,
      LocalDateTime operates_from,
      LocalDateTime operates_until,
      Integer tap_pos,
      Integer type) {
    this.uuid = uuid;
    this.tid = tid;
    this.amount = amount;
    this.auto_tap = auto_tap;
    this.id = id;
    this.node_a = node_a;
    this.node_b = node_b;
    this.node_c = node_c;
    this.operates_from = operates_from;
    this.operates_until = operates_until;
    this.tap_pos = tap_pos;
    this.type = type;
  }

  public Transformer3WInput toTransformer3WInput(Map<Integer, NodeInput> tidToNode) {
    UUID uuid = UUID.fromString(getUuid());
    ZonedDateTime startDate = operates_from != null ? ZonedDateTime.of(operates_from, ZoneId.of("UTC")) : null;
    ZonedDateTime endDate = operates_until != null ? ZonedDateTime.of(operates_until, ZoneId.of("UTC")) : null;
    OperationTime operationTime =
            OperationTime.builder()
                    .withStart(startDate)
                    .withEnd(endDate)
                    .build();
    Transformer3WTypeInput transformer3WType = CsvTypeSource.getTrafo3WType(type);
    Boolean autoTapBool = null;
    if(auto_tap.equals("t")) autoTapBool = true;
    if(auto_tap.equals("f")) autoTapBool = false;

    Transformer3WInput transformer3WInput =
        new Transformer3WInput(
            uuid,
            operationTime,
            null,
            id,
            tidToNode.get(node_a),
            tidToNode.get(node_b),
            tidToNode.get(node_c),
            amount,
            transformer3WType,
            tap_pos,
                autoTapBool);
    return transformer3WInput;
  }

  public List<Integer> getRequiredNodes() {
    return Arrays.asList(node_a, node_b, node_c);
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

  public String getAuto_tap() {
    return auto_tap;
  }

  public void setAuto_tap(String auto_tap) {
    this.auto_tap = auto_tap;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public Integer getNode_c() {
    return node_c;
  }

  public void setNode_c(Integer node_c) {
    this.node_c = node_c;
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
