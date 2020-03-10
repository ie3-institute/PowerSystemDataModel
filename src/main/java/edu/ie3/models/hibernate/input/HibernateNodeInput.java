/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.hibernate.input;

import com.vividsolutions.jts.geom.Point;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity(name = "nodes")
public class HibernateNodeInput implements Serializable {

  @Id
  //    @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Integer tid;

  @Column(name = "id", nullable = false, length = 200)
  private String id;

  @Column(name = "uuid", columnDefinition = "uuid")
  private UUID uuid;

  @Column(name = "is_slack", nullable = false)
  private boolean isSlack;

  @Column(name = "v_rated", nullable = false)
  private double vRated;

  @Column(name = "v_target", nullable = false)
  private double vTarget;

  @Column(name = "geo_position", columnDefinition = "geometry")
  private Point geoPosition;

  @Column(name = "volt_lvl", nullable = false)
  private String voltLvl;

  @Column(name = "subnet", nullable = false)
  private int subnet;

  @Column(name = "in_operation", nullable = false)
  private boolean inOperation;

  @Column(name = "operates_from")
  private ZonedDateTime operatesFrom;

  @Column(name = "operates_until")
  private ZonedDateTime operatesUntil;

  @Column(name = "scenario")
  private Integer scenario;

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

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }

  public boolean isSlack() {
    return isSlack;
  }

  public void setSlack(boolean slack) {
    isSlack = slack;
  }

  public double getvRated() {
    return vRated;
  }

  public void setvRated(double vRated) {
    this.vRated = vRated;
  }

  public double getvTarget() {
    return vTarget;
  }

  public void setvTarget(double vTarget) {
    this.vTarget = vTarget;
  }

  public Point getGeoPosition() {
    return geoPosition;
  }

  public void setGeoPosition(Point geoPosition) {
    this.geoPosition = geoPosition;
  }

  public String getVoltLvl() {
    return voltLvl;
  }

  public void setVoltLvl(String voltLvl) {
    this.voltLvl = voltLvl;
  }

  public int getSubnet() {
    return subnet;
  }

  public void setSubnet(int subnet) {
    this.subnet = subnet;
  }

  public boolean isInOperation() {
    return inOperation;
  }

  public void setInOperation(boolean inOperation) {
    this.inOperation = inOperation;
  }

  public ZonedDateTime getOperatesFrom() {
    return operatesFrom;
  }

  public void setOperatesFrom(ZonedDateTime operatesFrom) {
    this.operatesFrom = operatesFrom;
  }

  public ZonedDateTime getOperatesUntil() {
    return operatesUntil;
  }

  public void setOperatesUntil(ZonedDateTime operatesUntil) {
    this.operatesUntil = operatesUntil;
  }

  public Integer getScenario() {
    return scenario;
  }

  public void setScenario(Integer scenario) {
    this.scenario = scenario;
  }
}
