/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result.connector;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Angle;
import javax.measure.quantity.ElectricCurrent;
import tech.units.indriya.ComparableQuantity;

/**
 * Abstract class that adds a tap changer position attribute to the {@link ConnectorResult} which
 * actually creates a transformer representation.
 */
public abstract class TransformerResult extends ConnectorResult {

  /** Current tapping position if a transformer has a tap changer */
  private int tapPos;

  /**
   * Standard constructor with automatic uuid generation.
   *
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param iAMag electric current magnitude @ port A, normally provided in Ampere
   * @param iAAng electric current angle @ Port A in degree
   * @param iBMag electric current magnitude @ port B, normally provided in Ampere
   * @param iBAng electric current angle @ Port B in degree
   * @param tapPos the current position of the transformers tap changer
   */
  public TransformerResult(
      ZonedDateTime timestamp,
      UUID inputModel,
      ComparableQuantity<ElectricCurrent> iAMag,
      ComparableQuantity<Angle> iAAng,
      ComparableQuantity<ElectricCurrent> iBMag,
      ComparableQuantity<Angle> iBAng,
      int tapPos) {
    super(timestamp, inputModel, iAMag, iAAng, iBMag, iBAng);
    this.tapPos = tapPos;
  }

  /**
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   * @param iAMag electric current magnitude @ port A, normally provided in Ampere
   * @param iAAng electric current angle @ Port A in degree
   * @param iBMag electric current magnitude @ port B, normally provided in Ampere
   * @param iBAng electric current angle @ Port B in degree
   * @param tapPos the current position of the transformers tap changer
   */
  public TransformerResult(
      UUID uuid,
      ZonedDateTime timestamp,
      UUID inputModel,
      ComparableQuantity<ElectricCurrent> iAMag,
      ComparableQuantity<Angle> iAAng,
      ComparableQuantity<ElectricCurrent> iBMag,
      ComparableQuantity<Angle> iBAng,
      int tapPos) {
    super(uuid, timestamp, inputModel, iAMag, iAAng, iBMag, iBAng);
    this.tapPos = tapPos;
  }

  public int getTapPos() {
    return tapPos;
  }

  public void setTapPos(int tapPos) {
    this.tapPos = tapPos;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    TransformerResult that = (TransformerResult) o;
    return tapPos == that.tapPos;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), tapPos);
  }

  @Override
  public String toString() {
    return "TransformerResult{" + "tapPos=" + tapPos + '}';
  }
}
