/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.result;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Dimensionless;

/** Represents calculation results of a {@link edu.ie3.models.input.NodeInput} */
public class NodeResult extends ResultEntity {

  /** Voltage magnitude @ this node in p.u. */
  private Quantity<Dimensionless> vMag;

  /** Voltage angle @ this node in degree */
  private Quantity<Angle> vAng;

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
      Quantity<Dimensionless> vMag,
      Quantity<Angle> vAng) {
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
   */
  public NodeResult(
      UUID uuid,
      ZonedDateTime timestamp,
      UUID inputModel,
      Quantity<Dimensionless> vMag,
      Quantity<Angle> vAng) {
    super(uuid, timestamp, inputModel);
    this.vMag = vMag;
    this.vAng = vAng;
  }

  public Quantity<Dimensionless> getvMag() {
    return vMag;
  }

  public void setvMag(Quantity<Dimensionless> vMag) {
    this.vMag = vMag;
  }

  public Quantity<Angle> getvAng() {
    return vAng;
  }

  public void setvAng(Quantity<Angle> vAng) {
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
}
