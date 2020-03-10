/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.csv;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.connector.SwitchInput;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class CsvSwitchInput {
  // tid;closed;id;in_operation;node_a;node_b;operates_from;operates_until;scenario;uuid
  @CsvBindByName String uuid;
  @CsvBindByName Integer tid;
  @CsvBindByName String id;
  @CsvBindByName String closed;
  @CsvBindByName Integer node_a;
  @CsvBindByName Integer node_b;

  @CsvBindByName
  @CsvDate("yyyy-MM-dd HH:mm:ss")
  LocalDateTime operates_from;

  @CsvBindByName
  @CsvDate("yyyy-MM-dd HH:mm:ss")
  LocalDateTime operates_until;

  public CsvSwitchInput() {}

  public CsvSwitchInput(
      String uuid,
      Integer tid,
      String id,
      String closed,
      Integer node_a,
      Integer node_b,
      LocalDateTime operates_from,
      LocalDateTime operates_until) {
    this.uuid = uuid;
    this.tid = tid;
    this.id = id;
    this.closed = closed;
    this.node_a = node_a;
    this.node_b = node_b;
    this.operates_from = operates_from;
    this.operates_until = operates_until;
  }

  public SwitchInput toSwitchInput(Map<Integer, NodeInput> tidToNode) {
    UUID uuid = UUID.fromString(getUuid());
    ZonedDateTime startDate =
        operates_from != null ? ZonedDateTime.of(operates_from, ZoneId.of("UTC")) : null;
    ZonedDateTime endDate =
        operates_until != null ? ZonedDateTime.of(operates_until, ZoneId.of("UTC")) : null;
    OperationTime operationTime =
        OperationTime.builder().withStart(startDate).withEnd(endDate).build();
    Boolean closedBool = null;
    if (closed.equals("t")) closedBool = true;
    if (closed.equals("f")) closedBool = false;

    SwitchInput switchInput =
        new SwitchInput(
            uuid,
            operationTime,
            null,
            id,
            tidToNode.get(node_a),
            tidToNode.get(node_b),
            closedBool);
    return switchInput;
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getClosed() {
    return closed;
  }

  public void setClosed(String closed) {
    this.closed = closed;
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
}
