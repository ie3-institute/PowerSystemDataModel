/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input;

import java.util.Objects;
import java.util.UUID;

/**
 * Data model to describe the parameters of a probability density function to draw random power
 * consumptions. This model represents a generalized extreme value distribution (GEV), that has been
 * sampled for each quarter hour of a day, subdivided into workdays, Saturdays and Sundays. In
 * general the GEV is described by the three parameters "location", "scale" and "shape"
 */
public class RandomLoadParameters extends InputEntity {

  /** The respective quarter hour of the day */
  private final int quarterHour;

  /** Shape parameter for a working day */
  private final double kWd;

  /** Shape parameter for a Saturday */
  private final double kSa;

  /** Shape parameter for a Sunday */
  private final double kSu;

  /** Location parameter for a working day */
  private final double myWd;

  /** Location parameter for a Saturday */
  private final double mySa;

  /** Location parameter for a Sunday */
  private final double mySu;

  /** Scale parameter for a working day */
  private final double sigmaWd;

  /** Scale parameter for a Saturday */
  private final double sigmaSa;

  /** Scale parameter for a Sunday */
  private final double sigmaSu;

  /**
   * @param uuid of the input entity
   * @param quarterHour The respective quarter hour of the day
   * @param kWd Shape parameter for a working day
   * @param kSa Shape parameter for a Saturday
   * @param kSu Shape parameter for a Sunday
   * @param myWd Location parameter for a working day
   * @param mySa Location parameter for a Saturday
   * @param mySu Location parameter for a Sunday
   * @param sigmaWd Scale parameter for a working day
   * @param sigmaSa Scale parameter for a Saturday
   * @param sigmaSu Scale parameter for a Sunday
   */
  public RandomLoadParameters(
      UUID uuid,
      int quarterHour,
      double kWd,
      double kSa,
      double kSu,
      double myWd,
      double mySa,
      double mySu,
      double sigmaWd,
      double sigmaSa,
      double sigmaSu) {
    super(uuid);
    this.quarterHour = quarterHour;
    this.kWd = kWd;
    this.kSa = kSa;
    this.kSu = kSu;
    this.myWd = myWd;
    this.mySa = mySa;
    this.mySu = mySu;
    this.sigmaWd = sigmaWd;
    this.sigmaSa = sigmaSa;
    this.sigmaSu = sigmaSu;
  }

  public int getQuarterHour() {
    return quarterHour;
  }

  public double getMyWd() {
    return myWd;
  }

  public double getMySa() {
    return mySa;
  }

  public double getMySu() {
    return mySu;
  }

  public double getSigmaWd() {
    return sigmaWd;
  }

  public double getSigmaSa() {
    return sigmaSa;
  }

  public double getSigmaSu() {
    return sigmaSu;
  }

  public double getkWd() {
    return kWd;
  }

  public double getkSa() {
    return kSa;
  }

  public double getkSu() {
    return kSu;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    RandomLoadParameters that = (RandomLoadParameters) o;
    return Objects.equals(quarterHour, that.quarterHour)
        && Objects.equals(kWd, that.kWd)
        && Objects.equals(kSa, that.kSa)
        && Objects.equals(kSu, that.kSu)
        && Objects.equals(myWd, that.myWd)
        && Objects.equals(mySa, that.mySa)
        && Objects.equals(mySu, that.mySu)
        && Objects.equals(sigmaWd, that.sigmaWd)
        && Objects.equals(sigmaSa, that.sigmaSa)
        && Objects.equals(sigmaSu, that.sigmaSu);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), quarterHour, kWd, kSa, kSu, myWd, mySa, mySu, sigmaWd, sigmaSa, sigmaSu);
  }
}
