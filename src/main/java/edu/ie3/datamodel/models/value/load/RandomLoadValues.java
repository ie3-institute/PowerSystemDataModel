/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value.load;

import de.lmu.ifi.dbs.elki.math.statistics.distribution.GeneralizedExtremeValueDistribution;
import edu.ie3.datamodel.models.profile.PowerProfileKey;
import edu.ie3.datamodel.models.value.PValue;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.Objects;
import tech.units.indriya.quantity.Quantities;

/**
 * Data model to describe the parameters of a probability density function to draw random power
 * consumptions. This model represents a generalized extreme value distribution (GEV), that has been
 * sampled for each quarter-hour of a day, subdivided into workdays, Saturdays and Sundays. In
 * general the GEV is described by the three parameters "location", "scale" and "shape".
 */
public class RandomLoadValues implements LoadValues, RandomNumberProvider {
  /** Shape parameter for a Saturday. */
  private final double kSa;

  /** Shape parameter for a Sunday. */
  private final double kSu;

  /** Shape parameter for a working day. */
  private final double kWd;

  /** Shape parameter for a Saturday. */
  private final double mySa;

  /** Shape parameter for a Sunday. */
  private final double mySu;

  /** Shape parameter for a working day. */
  private final double myWd;

  /** Shape parameter for a Saturday. */
  private final double sigmaSa;

  /** Shape parameter for a Sunday. */
  private final double sigmaSu;

  /** Shape parameter for a working day. */
  private final double sigmaWd;

  private final transient GeneralizedExtremeValueDistribution gevWd;

  private final transient GeneralizedExtremeValueDistribution gevSa;

  private final transient GeneralizedExtremeValueDistribution gevSu;

  /**
   * @param kSa Shape parameter for a Saturday
   * @param kSu Shape parameter for a Sunday
   * @param kWd Shape parameter for a working day
   * @param mySa Location parameter for a Saturday
   * @param mySu Location parameter for a Sunday
   * @param myWd Location parameter for a working day
   * @param sigmaSa Scale parameter for a Saturday
   * @param sigmaSu Scale parameter for a Sunday
   * @param sigmaWd Scale parameter for a working day
   */
  public RandomLoadValues(
      double kSa,
      double kSu,
      double kWd,
      double mySa,
      double mySu,
      double myWd,
      double sigmaSa,
      double sigmaSu,
      double sigmaWd) {
    this.kSa = kSa;
    this.kSu = kSu;
    this.kWd = kWd;
    this.mySa = mySa;
    this.mySu = mySu;
    this.myWd = myWd;
    this.sigmaSa = sigmaSa;
    this.sigmaSu = sigmaSu;
    this.sigmaWd = sigmaWd;
    this.gevWd = new GeneralizedExtremeValueDistribution(myWd, sigmaWd, kWd, factory.getRandom());
    this.gevSa = new GeneralizedExtremeValueDistribution(mySa, sigmaSa, kSa, factory.getRandom());
    this.gevSu = new GeneralizedExtremeValueDistribution(mySu, sigmaSu, kSu, factory.getRandom());
  }

  public double getKSa() {
    return kSa;
  }

  public double getKSu() {
    return kSu;
  }

  public double getKWd() {
    return kWd;
  }

  public double getMySa() {
    return mySa;
  }

  public double getMySu() {
    return mySu;
  }

  public double getMyWd() {
    return myWd;
  }

  public double getSigmaSa() {
    return sigmaSa;
  }

  public double getSigmaSu() {
    return sigmaSu;
  }

  public double getSigmaWd() {
    return sigmaWd;
  }

  public GeneralizedExtremeValueDistribution getGevWd() {
    return gevWd;
  }

  public GeneralizedExtremeValueDistribution getGevSa() {
    return gevSa;
  }

  public GeneralizedExtremeValueDistribution getGevSu() {
    return gevSu;
  }

  @Override
  public PValue getValue(ZonedDateTime time, PowerProfileKey powerProfileKey) {
    return new PValue(
        Quantities.getQuantity(getValue(time.getDayOfWeek()), PowerSystemUnits.KILOWATT));
  }

  private double getValue(DayOfWeek day) {
    double randomValue =
        switch (day) {
          case SATURDAY -> gevSa.nextRandom();
          case SUNDAY -> gevSu.nextRandom();
          default -> gevWd.nextRandom();
        };

    while (randomValue < 0) {
      randomValue = getValue(day);
    }
    return randomValue;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof RandomLoadValues that)) return false;
    return kSa == that.kSa
        && kSu == that.kSu
        && kWd == that.kWd
        && mySa == that.mySa
        && mySu == that.mySu
        && myWd == that.myWd
        && sigmaSa == that.sigmaSa
        && sigmaSu == that.sigmaSu
        && sigmaWd == that.sigmaWd;
  }

  @Override
  public int hashCode() {
    return Objects.hash(kSa, kSu, kWd, mySa, mySu, myWd, sigmaSa, sigmaSu, sigmaWd);
  }

  @Override
  public String toString() {
    return "RandomLoadValues{"
        + "kSa="
        + kSa
        + ", kSu="
        + kSu
        + ", kWd="
        + kWd
        + ", mySa="
        + mySa
        + ", mySu="
        + mySu
        + ", myWd="
        + myWd
        + ", sigmaSa="
        + sigmaSa
        + ", sigmaSu="
        + sigmaSu
        + ", sigmaWd="
        + sigmaWd
        + "}";
  }
}
