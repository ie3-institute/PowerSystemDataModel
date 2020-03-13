/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.neo4j;

import java.util.Date;
import java.util.Set;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.DateString;

@NodeEntity(label = "Node")
public class Neo4JNodeInput {

  private Integer subnet;
  private Double v_target;
  private Double v_rated;
  private String id;
  private String uuid;
  private String volt_lvl;
  private Integer tid;
  private Boolean is_slack;
  private Double x_coord;
  private Double y_coord;
  @DateString private Date operates_from;
  @DateString private Date operates_until;
  private boolean in_operation;

  @Relationship(type = "LINE")
  Set<Neo4JLineInput> outgoingLines;

  @Relationship(type = "TRANSFORMER")
  Set<Neo4JTransformer2WInput> outgoingTransformers2W;

  @Relationship(type = "TRANSFORMER3W")
  Set<Neo4JTransformer3WInput> outgoingTransformers3W;

  @Relationship(type = "SWITCH")
  Set<Neo4JSwitchInput> outgoingSwitches;

  public Neo4JNodeInput() {}

  public Integer getSubnet() {
    return subnet;
  }

  public void setSubnet(Integer subnet) {
    this.subnet = subnet;
  }

  public Double getV_target() {
    return v_target;
  }

  public void setV_target(Double v_target) {
    this.v_target = v_target;
  }

  public Double getV_rated() {
    return v_rated;
  }

  public void setV_rated(Double v_rated) {
    this.v_rated = v_rated;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getVolt_lvl() {
    return volt_lvl;
  }

  public void setVolt_lvl(String volt_lvl) {
    this.volt_lvl = volt_lvl;
  }

  public Integer getTid() {
    return tid;
  }

  public void setTid(Integer tid) {
    this.tid = tid;
  }

  public Set<Neo4JLineInput> getOutgoingLines() {
    return outgoingLines;
  }

  public void setOutgoingLines(Set<Neo4JLineInput> outgoingLines) {
    this.outgoingLines = outgoingLines;
  }

  public Boolean getIs_slack() {
    return is_slack;
  }

  public void setIs_slack(Boolean is_slack) {
    this.is_slack = is_slack;
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

  public boolean isIn_operation() {
    return in_operation;
  }

  public void setIn_operation(boolean in_operation) {
    this.in_operation = in_operation;
  }

  public void setOutgoingTransformers2W(Set<Neo4JTransformer2WInput> outgoingTransformers2W) {
    this.outgoingTransformers2W = outgoingTransformers2W;
  }

  public Set<Neo4JTransformer3WInput> getOutgoingTransformers3W() {
    return outgoingTransformers3W;
  }

  public void setOutgoingTransformers3W(Set<Neo4JTransformer3WInput> outgoingTransformers3W) {
    this.outgoingTransformers3W = outgoingTransformers3W;
  }

  public Set<Neo4JSwitchInput> getOutgoingSwitches() {
    return outgoingSwitches;
  }

  public void setOutgoingSwitches(Set<Neo4JSwitchInput> outgoingSwitches) {
    this.outgoingSwitches = outgoingSwitches;
  }
}
