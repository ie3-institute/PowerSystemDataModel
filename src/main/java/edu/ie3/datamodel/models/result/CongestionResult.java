/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result;

import java.time.ZonedDateTime;
import java.util.Objects;
import javax.measure.quantity.Dimensionless;
import tech.units.indriya.ComparableQuantity;

public class CongestionResult extends ResultEntity {
  /** Values */
  private final Integer subnet;

  private final ComparableQuantity<Dimensionless> vMin;
  private final ComparableQuantity<Dimensionless> vMax;
  private final boolean voltage;
  private final boolean line;
  private final boolean transformer;

  /**
   * Standard constructor which includes auto generation of the resulting output models uuid.
   *
   * @param time date and time when the result is produced
   * @param subnet the subnet
   * @param vMin minimum voltage in pu
   * @param vMax maximal voltage in pu
   * @param voltage {@code true} if a voltage congestion occurred in the subnet
   * @param line {@code true} if a line congestion occurred in the subnet
   * @param transformer {@code true} if a transformer congestion occurred in the subnet
   */
  public CongestionResult(
      ZonedDateTime time,
      int subnet,
      ComparableQuantity<Dimensionless> vMin,
      ComparableQuantity<Dimensionless> vMax,
      boolean voltage,
      boolean line,
      boolean transformer) {
    super(time);
    this.subnet = subnet;
    this.vMin = vMin;
    this.vMax = vMax;
    this.voltage = voltage;
    this.line = line;
    this.transformer = transformer;
  }

  public int getSubnet() {
    return subnet;
  }

  public boolean getVoltage() {
    return voltage;
  }

  public boolean getLine() {
    return line;
  }

  public boolean getTransformer() {
    return transformer;
  }

  public ComparableQuantity<Dimensionless> getVMin() {
    return vMin;
  }

  public ComparableQuantity<Dimensionless> getVMax() {
    return vMax;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CongestionResult that = (CongestionResult) o;
    return getTime().equals(that.getTime())
        && vMin.equals(that.vMin)
        && vMax.equals(that.vMax)
        && voltage == that.voltage
        && line == that.line
        && transformer == that.transformer;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getTime(), vMin, vMax, voltage, line, transformer);
  }

  @Override
  public String toString() {
    return "InputResultEntity{time="
        + getTime()
        + ", vMin="
        + vMin
        + ", vMan="
        + vMax
        + ", voltage="
        + voltage
        + ", line="
        + line
        + ", transformer="
        + transformer
        + '}';
  }
}
