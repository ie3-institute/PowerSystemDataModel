/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input;

import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Model of a measuring unit attached to a certain {@link NodeInput}. */
public class MeasurementUnitInput extends AssetInput {
  /** Grid node, the asset is attached to */
  private NodeInput node;

  /** True, if the voltage magnitude is measured */
  private boolean vMag;

  /** True, if the voltage angle is measured */
  private boolean vAng;

  /** True, if the nodal residual active power is measured */
  private boolean p;

  /** True, if the reactive power is measured */
  private boolean q;

  /**
   * @param uuid of the input entity
   * @param operationInterval Empty for a non-operated asset, Interval of operation period else
   * @param operator of the asset
   * @param id of the asset
   * @param node Grid node, the asset is attached to
   * @param vMag True, if the voltage magnitude is measured
   * @param vAng True, if the voltage angle is measured
   * @param p True, if the nodal residual active power is measured
   * @param q True, if the reactive power is measured
   */
  public MeasurementUnitInput(
      UUID uuid,
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id,
      NodeInput node,
      boolean vMag,
      boolean vAng,
      boolean p,
      boolean q) {
    super(uuid, operationInterval, operator, id);
    this.node = node;
    this.vMag = vMag;
    this.vAng = vAng;
    this.p = p;
    this.q = q;
  }

  /**
   * If both operatesFrom and operatesUntil are Empty, it is assumed that the asset is non-operated.
   *
   * @param uuid of the input entity
   * @param operatesFrom start of operation period, will be replaced by LocalDateTime.MIN if Empty
   * @param operatesUntil end of operation period, will be replaced by LocalDateTime.MAX if Empty
   * @param operator of the asset
   * @param id of the asset
   * @param node Grid node, the asset is attached to
   * @param vMag True, if the voltage magnitude is measured
   * @param vAng True, if the voltage angle is measured
   * @param p True, if the nodal residual active power is measured
   * @param q True, if the reactive power is measured
   */
  public MeasurementUnitInput(
      UUID uuid,
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id,
      NodeInput node,
      boolean vMag,
      boolean vAng,
      boolean p,
      boolean q) {
    super(uuid, operatesFrom, operatesUntil, operator, id);
    this.node = node;
    this.vMag = vMag;
    this.vAng = vAng;
    this.p = p;
    this.q = q;
  }

  /**
   * Constructor for a non-operated asset
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node Grid node, the asset is attached to
   * @param vMag True, if the voltage magnitude is measured
   * @param vAng True, if the voltage angle is measured
   * @param p True, if the nodal residual active power is measured
   * @param q True, if the reactive power is measured
   */
  public MeasurementUnitInput(
      UUID uuid, String id, NodeInput node, boolean vMag, boolean vAng, boolean p, boolean q) {
    super(uuid, id);
    this.node = node;
    this.vMag = vMag;
    this.vAng = vAng;
    this.p = p;
    this.q = q;
  }

  public NodeInput getNode() {
    return node;
  }

  public void setNode(NodeInput node) {
    this.node = node;
  }

  public boolean getVMag() {
    return vMag;
  }

  public void setVMag(boolean vMag) {
    this.vMag = vMag;
  }

  public boolean getVAng() {
    return vAng;
  }

  public void setVAng(boolean vAng) {
    this.vAng = vAng;
  }

  public boolean getP() {
    return p;
  }

  public void setP(boolean p) {
    this.p = p;
  }

  public boolean getQ() {
    return q;
  }

  public void setQ(boolean q) {
    this.q = q;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    MeasurementUnitInput that = (MeasurementUnitInput) o;
    return Objects.equals(node, that.node)
        && Objects.equals(vMag, that.vMag)
        && Objects.equals(vAng, that.vAng)
        && Objects.equals(p, that.p)
        && Objects.equals(q, that.q);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), node, vMag, vAng, p, q);
  }
}
