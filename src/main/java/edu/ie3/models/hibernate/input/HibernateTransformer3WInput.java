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

@Entity(name = "transformers_three_windings")
public class HibernateTransformer3WInput implements Serializable {

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

  @Column(name = "node_c", nullable = false)
  private Integer nodeCId;

  @ManyToOne(targetEntity = HibernateNodeInput.class, optional = false)
  @JoinColumn(name = "node_c", referencedColumnName = "tid", updatable = false, insertable = false)
  private HibernateNodeInput nodeC;

  @Column(name = "in_operation", nullable = false)
  private boolean inOperation;

  @Column(name = "operates_from")
  private ZonedDateTime operatesFrom;

  @Column(name = "operates_until")
  private ZonedDateTime operatesUntil;

  @Column(name = "amount", nullable = false)
  private int amount;

  @Column(name = "tap_pos", nullable = false)
  private int tapPos;

  @Column(name = "auto_tap", nullable = false)
  private boolean autoTap;

  @Column(name = "scenario")
  private Integer scenario;

  @Column(name = "type")
  private Integer type;

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

  public Integer getNodeCId() {
    return nodeCId;
  }

  public void setNodeCId(Integer nodeCId) {
    this.nodeCId = nodeCId;
  }

  public HibernateNodeInput getNodeC() {
    return nodeC;
  }

  public void setNodeC(HibernateNodeInput nodeC) {
    this.nodeC = nodeC;
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

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public int getTapPos() {
    return tapPos;
  }

  public void setTapPos(int tapPos) {
    this.tapPos = tapPos;
  }

  public boolean isAutoTap() {
    return autoTap;
  }

  public void setAutoTap(boolean autoTap) {
    this.autoTap = autoTap;
  }

  public Integer getScenario() {
    return scenario;
  }

  public void setScenario(Integer scenario) {
    this.scenario = scenario;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }
}
