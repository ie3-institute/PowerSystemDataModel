/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result;

import edu.ie3.util.quantities.QuantityUtil;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;

/** Represents calculation results of a {@link edu.ie3.datamodel.models.input.NodeInput} */
public class NodeResult extends ResultEntity {

  /** Voltage magnitude @ this node in p.u. */
  private ComparableQuantity<Dimensionless> vMag;

  /** Voltage angle @ this node in degree */
  private ComparableQuantity<Angle> vAng;

  /**
   * Standard constructor which includes auto generation of the resulting output models uuid.
   *
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param vMag voltage magnitude @ this node in p.u.
   * @param vAng voltage angle @ this node in degree
   */
  public NodeResult(
      ZonedDateTime timestamp,
      UUID inputModel,
      ComparableQuantity<Dimensionless> vMag,
      ComparableQuantity<Angle> vAng) {
    super(timestamp, inputModel);
    this.vMag = vMag;
    this.vAng = vAng;
  }

  /**
   * Standard constructor without uuid generation.
   *
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param vMag Dimensionless voltage magnitude
   * @param vAng Voltage angle in degrees
   */
  public NodeResult(
      UUID uuid,
      ZonedDateTime timestamp,
      UUID inputModel,
      ComparableQuantity<Dimensionless> vMag,
      ComparableQuantity<Angle> vAng) {
    super(uuid, timestamp, inputModel);
    this.vMag = vMag;
    this.vAng = vAng;
  }

  public ComparableQuantity<Dimensionless> getvMag() {
    return vMag;
  }

  public void setvMag(ComparableQuantity<Dimensionless> vMag) {
    this.vMag = vMag;
  }

  public ComparableQuantity<Angle> getvAng() {
    return vAng;
  }

  public void setvAng(ComparableQuantity<Angle> vAng) {
    this.vAng = vAng;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    NodeResult that = (NodeResult) o;
    if (!QuantityUtil.quantityIsEmpty(vMag)) {
      if (QuantityUtil.quantityIsEmpty(that.vMag)) return false;
      if (!vMag.isEquivalentTo(that.vMag)) return false;
    } else if (!QuantityUtil.quantityIsEmpty(that.vMag)) return false;

    if (!QuantityUtil.quantityIsEmpty(vAng)) {
      if (QuantityUtil.quantityIsEmpty(that.vAng)) return false;
      return vAng.isEquivalentTo(that.vAng);
    } else return QuantityUtil.quantityIsEmpty(that.vAng);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), vMag, vAng);
  }

  @Override
  public String toString() {
    return "NodeResult{" + "vMag=" + vMag + ", vAng=" + vAng + '}';
  }
}
