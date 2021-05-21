/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result;

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
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param vMag voltage magnitude @ this node in p.u.
   * @param vAng voltage angle @ this node in degree
   */
  public NodeResult(
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Dimensionless> vMag,
      ComparableQuantity<Angle> vAng) {
    super(time, inputModel);
    this.vMag = vMag;
    this.vAng = vAng;
  }

  /**
   * Standard constructor without uuid generation.
   *
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param vMag Dimensionless voltage magnitude
   * @param vAng Voltage angle in degrees
   */
  public NodeResult(
      UUID uuid,
      ZonedDateTime time,
      UUID inputModel,
      ComparableQuantity<Dimensionless> vMag,
      ComparableQuantity<Angle> vAng) {
    super(uuid, time, inputModel);
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
    return vMag.equals(that.vMag) && vAng.equals(that.vAng);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), vMag, vAng);
  }

  @Override
  public String toString() {
    return "NodeResult{"
        + "uuid="
        + getUuid()
        + ", time="
        + getTime()
        + ", inputModel="
        + getInputModel()
        + ", vMag="
        + vMag
        + ", vAng="
        + vAng
        + '}';
  }
}
