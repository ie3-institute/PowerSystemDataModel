/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.neo4j;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.DateString;

import java.util.Date;

@RelationshipEntity(type = "TRANSFORMER3W")
public class Neo4JTransformer3WInput {

  private Integer amount;
  private Integer tap_pos;
  private Boolean threewindings;
  private Boolean auto_tap;
  private String id;
  private Integer type;
  private String uuid;
  private Integer tid;
  private Boolean in_operation;
  @DateString private Date operates_from;
  @DateString private Date operates_until;
  @StartNode private Neo4JNodeInput nodeA;
  @EndNode private Neo4JNodeInput nodeB;

  public Neo4JTransformer3WInput() {}

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public Integer getTap_pos() {
    return tap_pos;
  }

  public void setTap_pos(Integer tap_pos) {
    this.tap_pos = tap_pos;
  }

  public Boolean getThreewindings() {
    return threewindings;
  }

  public void setThreewindings(Boolean threewindings) {
    this.threewindings = threewindings;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
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

  public Neo4JNodeInput getNodeA() {
    return nodeA;
  }

  public void setNodeA(Neo4JNodeInput nodeA) {
    this.nodeA = nodeA;
  }

  public Neo4JNodeInput getNodeB() {
    return nodeB;
  }

  public void setNodeB(Neo4JNodeInput nodeB) {
    this.nodeB = nodeB;
  }

  public Boolean getIn_operation() {
    return in_operation;
  }

  public void setIn_operation(Boolean in_operation) {
    this.in_operation = in_operation;
  }

  public Date getOperates_from() {
    return operates_from;
  }

  public void setOperates_from(Date operates_from) {
    this.operates_from = operates_from;
  }

  public Date getOperates_until() {
    return operates_until;
  }

  public void setOperates_until(Date operates_until) {
    this.operates_until = operates_until;
  }

  public Boolean getAuto_tap() {
    return auto_tap;
  }

  public void setAuto_tap(Boolean auto_tap) {
    this.auto_tap = auto_tap;
  }
}
