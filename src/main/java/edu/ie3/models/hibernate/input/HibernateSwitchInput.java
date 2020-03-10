/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.hibernate.input;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.*;

@Entity(name = "switches")
public class HibernateSwitchInput implements Serializable {

  @Id
  //    @GeneratedValue(strategy = GenerationType.SEQUENCE)
  private Integer tid;

  @Column(name = "id", length = 200)
  private String id;

  @Column(name = "uuid", columnDefinition = "uuid")
  private UUID uuid;

  @Column(name = "node_a", nullable = false)
  private Integer nodeAId;

  @ManyToOne(targetEntity = HibernateNodeInput.class, optional = false)
  @JoinColumn(name = "node_a", referencedColumnName = "tid", updatable = false, insertable = false)
  private HibernateNodeInput nodeA;

  @Column(name = "node_b", nullable = false)
  private Integer nodeBId;

  @ManyToOne(targetEntity = HibernateNodeInput.class, optional = false)
  @JoinColumn(name = "node_b", referencedColumnName = "tid", updatable = false, insertable = false)
  private HibernateNodeInput nodeB;

  @Column(name = "in_operation", nullable = false)
  private boolean inOperation;

  @Column(name = "operates_from")
  private ZonedDateTime operatesFrom;

  @Column(name = "operates_until")
  private ZonedDateTime operatesUntil;

  @Column(name = "scenario")
  private Integer scenario;

  @Column(name = "closed")
  private boolean closed;

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

  public Integer getNodeAId() {
    return nodeAId;
  }

  public void setNodeAId(Integer nodeAId) {
    this.nodeAId = nodeAId;
  }

  public HibernateNodeInput getNodeA() {
    return nodeA;
  }

  public void setNodeA(HibernateNodeInput nodeA) {
    this.nodeA = nodeA;
  }

  public Integer getNodeBId() {
    return nodeBId;
  }

  public void setNodeBId(Integer nodeBId) {
    this.nodeBId = nodeBId;
  }

  public HibernateNodeInput getNodeB() {
    return nodeB;
  }

  public void setNodeB(HibernateNodeInput nodeB) {
    this.nodeB = nodeB;
  }

  public boolean isClosed() {
    return closed;
  }

  public void setClosed(boolean closed) {
    this.closed = closed;
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

  public Integer getTid() {
    return tid;
  }

  public void setTid(Integer tid) {
    this.tid = tid;
  }
}
