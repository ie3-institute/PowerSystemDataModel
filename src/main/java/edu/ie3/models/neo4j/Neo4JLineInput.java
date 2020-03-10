/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.neo4j;

import java.util.Date;
import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;
import org.neo4j.ogm.annotation.typeconversion.DateString;

@RelationshipEntity(type = "LINE")
public class Neo4JLineInput {
  private String uuid;
  @DateString private Date operates_from;
  @DateString private Date operates_until;
  private String id;
  @StartNode private Neo4JNodeInput nodeA;
  @EndNode private Neo4JNodeInput nodeB;
  private Integer amount;
  private Integer tid;
  private Double length;
  private String geo_position;
  private String olm_characteristic;
  private Integer type;

  public Neo4JLineInput() {}

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
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

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
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

  public Integer getAmount() {
    return amount;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }

  public Integer getTid() {
    return tid;
  }

  public void setTid(Integer tid) {
    this.tid = tid;
  }

  public Double getLength() {
    return length;
  }

  public void setLength(Double length) {
    this.length = length;
  }

  public String getGeo_position() {
    return geo_position;
  }

  public void setGeo_position(String geo_position) {
    this.geo_position = geo_position;
  }

  public String getOlm_characteristic() {
    return olm_characteristic;
  }

  public void setOlm_characteristic(String olm_characteristic) {
    this.olm_characteristic = olm_characteristic;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }
}
