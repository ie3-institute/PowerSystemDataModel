/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value.load;

import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT;

import de.lmu.ifi.dbs.elki.math.statistics.distribution.GeneralizedExtremeValueDistribution;
import de.lmu.ifi.dbs.elki.utilities.random.RandomFactory;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.value.PValue;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Random;
import tech.units.indriya.quantity.Quantities;

/**
 * Data model to describe the parameters of a probability density function to draw random power
 * consumptions. This model represents a generalized extreme value distribution (GEV), that has been
 * sampled for each quarter hour of a day, subdivided into workdays, Saturdays and Sundays. In
 * general the GEV is described by the three parameters "location", "scale" and "shape"
 */
public class RandomLoadValues implements LoadValues<LoadProfile> {
  /** Shape parameter for a Saturday */
  private final double kSa;

  /** Shape parameter for a Sunday */
  private final double kSu;

  /** Shape parameter for a working day */
  private final double kWd;

  /** Location parameter for a Saturday */
  private final double mySa;

  /** Location parameter for a Sunday */
  private final double mySu;

  /** Location parameter for a working day */
  private final double myWd;

  /** Scale parameter for a Saturday */
  private final double sigmaSa;

  /** Scale parameter for a Sunday */
  private final double sigmaSu;

  /** Scale parameter for a working day */
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
    this.kWd = kWd;
    this.kSa = kSa;
    this.kSu = kSu;
    this.myWd = myWd;
    this.mySa = mySa;
    this.mySu = mySu;
    this.sigmaWd = sigmaWd;
    this.sigmaSa = sigmaSa;
    this.sigmaSu = sigmaSu;

    RandomFactory factory = RandomFactory.get(new Random().nextLong());

    this.gevWd = new GeneralizedExtremeValueDistribution(myWd, sigmaWd, kWd, factory.getRandom());

    this.gevSa = new GeneralizedExtremeValueDistribution(mySa, sigmaSa, kSa, factory.getRandom());
    this.gevSu = new GeneralizedExtremeValueDistribution(mySu, sigmaSu, kSu, factory.getRandom());
  }

  @Override
  public PValue getValue(ZonedDateTime time, LoadProfile loadProfile) {
    return new PValue(Quantities.getQuantity(getValue(time.getDayOfWeek()), KILOWATT));
  }

  /**
   * Method to get the next random double value.
   *
   * @param day of the week
   * @return a suitable random double
   */
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
    if (!(o instanceof RandomLoadValues that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(kSa, that.kSa)
        && Objects.equals(kSu, that.kSu)
        && Objects.equals(kWd, that.kWd)
        && Objects.equals(mySa, that.mySa)
        && Objects.equals(mySu, that.mySu)
        && Objects.equals(myWd, that.myWd)
        && Objects.equals(sigmaSa, that.sigmaSa)
        && Objects.equals(sigmaSu, that.sigmaSu)
        && Objects.equals(sigmaWd, that.sigmaWd);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), kSa, kSu, kWd, mySa, mySu, myWd, sigmaSa, sigmaSu, sigmaWd);
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
        + '}';
  }
}
