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
  Integer quarterHour;

  /** Shape parameter for a working day */
  Double kWd;

  /** Shape parameter for a Saturday */
  Double kSa;

  /** Shape parameter for a Sunday */
  Double kSu;

  /** Location parameter for a working day */
  Double myWd;

  /** Location parameter for a Saturday */
  Double mySa;

  /** Location parameter for a Sunday */
  Double mySu;

  /** Scale parameter for a working day */
  Double sigmaWd;

  /** Scale parameter for a Saturday */
  Double sigmaSa;

  /** Scale parameter for a Sunday */
  Double sigmaSu;

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
      Integer quarterHour,
      Double kWd,
      Double kSa,
      Double kSu,
      Double myWd,
      Double mySa,
      Double mySu,
      Double sigmaWd,
      Double sigmaSa,
      Double sigmaSu) {
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

  public Integer getQuarterHour() {
    return quarterHour;
  }

  public void setQuarterHour(Integer quarterHour) {
    this.quarterHour = quarterHour;
  }

  public Double getKWd() {
    return kWd;
  }

  public void setKWd(Double kWd) {
    this.kWd = kWd;
  }

  public Double getKSa() {
    return kSa;
  }

  public void setKSa(Double kSa) {
    this.kSa = kSa;
  }

  public Double getKSu() {
    return kSu;
  }

  public void setKSu(Double kSu) {
    this.kSu = kSu;
  }

  public Double getMyWd() {
    return myWd;
  }

  public void setMyWd(Double myWd) {
    this.myWd = myWd;
  }

  public Double getMySa() {
    return mySa;
  }

  public void setMySa(Double mySa) {
    this.mySa = mySa;
  }

  public Double getMySu() {
    return mySu;
  }

  public void setMySu(Double mySu) {
    this.mySu = mySu;
  }

  public Double getSigmaWd() {
    return sigmaWd;
  }

  public void setSigmaWd(Double sigmaWd) {
    this.sigmaWd = sigmaWd;
  }

  public Double getSigmaSa() {
    return sigmaSa;
  }

  public void setSigmaSa(Double sigmaSa) {
    this.sigmaSa = sigmaSa;
  }

  public Double getSigmaSu() {
    return sigmaSu;
  }

  public void setSigmaSu(Double sigmaSu) {
    this.sigmaSu = sigmaSu;
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
