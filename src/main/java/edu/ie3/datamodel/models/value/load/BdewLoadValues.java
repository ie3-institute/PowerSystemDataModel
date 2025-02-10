/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value.load;

import static edu.ie3.datamodel.models.BdewSeason.*;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static tech.units.indriya.unit.Units.WATT;

import edu.ie3.datamodel.models.BdewSeason;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.value.PValue;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import tech.units.indriya.quantity.Quantities;

/** Load values for a {@link BdewStandardLoadProfile} */
public class BdewLoadValues implements LoadValues {
  private final double suSa;
  private final double suSu;
  private final double suWd;
  private final double trSa;
  private final double trSu;
  private final double trWd;
  private final double wiSa;
  private final double wiSu;
  private final double wiWd;

  public BdewLoadValues(
      double suSa,
      double suSu,
      double suWd,
      double trSa,
      double trSu,
      double trWd,
      double wiSa,
      double wiSu,
      double wiWd) {
    this.suSa = suSa;
    this.suSu = suSu;
    this.suWd = suWd;
    this.trSa = trSa;
    this.trSu = trSu;
    this.trWd = trWd;
    this.wiSa = wiSa;
    this.wiSu = wiSu;
    this.wiWd = wiWd;
  }

  @Override
  public PValue getValue(ZonedDateTime time, LoadProfile loadProfile) {
    Map<BdewSeason, Double> mapping =
        switch (time.getDayOfWeek()) {
          case SATURDAY -> Map.of(
              SUMMER, suSa,
              WINTER, wiSa,
              TRANSITION, trSa);
          case SUNDAY -> Map.of(
              SUMMER, suSu,
              WINTER, wiSu,
              TRANSITION, trSu);
          default -> Map.of(
              SUMMER, suWd,
              WINTER, wiWd,
              TRANSITION, trWd);
        };

    double power = mapping.get(getSeason(time));

    if (loadProfile == BdewStandardLoadProfile.H0) {
      /* For the residential average profile, a dynamization has to be taken into account */
      power = dynamization(power, time.getDayOfYear()); // leap years are ignored
    }

    return new PValue(Quantities.getQuantity(power, WATT));
  }

  /**
   * Calculates the dynamization factor for given day of year. Cf. <a
   * href="https://www.bdew.de/media/documents/2000131_Anwendung-repraesentativen_Lastprofile-Step-by-step.pdf">
   * Anwendung der repräsentativen Lastprofile - Step by step</a> page 19
   *
   * @param load load value
   * @param t day of year (1-366)
   * @return dynamization factor
   */
  public static double dynamization(double load, int t) {
    double factor =
        (-3.92e-10 * pow(t, 4) + 3.2e-7 * pow(t, 3) - 7.02e-5 * pow(t, 2) + 2.1e-3 * t + 1.24);
    double rndFactor = round(factor * 1e4) / 1e4; // round to 4 decimal places
    return round(load * rndFactor * 1e1) / 1e1; // rounded to 1 decimal place
  }

  public double getSuSa() {
    return suSa;
  }

  public double getSuSu() {
    return suSu;
  }

  public double getSuWd() {
    return suWd;
  }

  public double getTrSa() {
    return trSa;
  }

  public double getTrSu() {
    return trSu;
  }

  public double getTrWd() {
    return trWd;
  }

  public double getWiSa() {
    return wiSa;
  }

  public double getWiSu() {
    return wiSu;
  }

  public double getWiWd() {
    return wiWd;
  }

  public List<Double> values() {
    return List.of(suSa, suSu, suWd, trSa, trSu, trWd, wiSa, wiSu, wiWd);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BdewLoadValues that = (BdewLoadValues) o;
    return Objects.equals(suSa, that.suSa)
        && Objects.equals(suSu, that.suSu)
        && Objects.equals(suWd, that.suWd)
        && Objects.equals(trSa, that.trSa)
        && Objects.equals(trSu, that.trSu)
        && Objects.equals(trWd, that.trWd)
        && Objects.equals(wiSa, that.wiSa)
        && Objects.equals(wiSu, that.wiSu)
        && Objects.equals(wiWd, that.wiWd);
  }

  @Override
  public int hashCode() {
    return Objects.hash(suSa, suSu, suWd, trSa, trSu, trWd, wiSa, wiSu, wiWd);
  }

  @Override
  public String toString() {
    return "BDEWLoadValues{"
        + "suSa="
        + suSa
        + ", suSu="
        + suSu
        + ", suWd="
        + suWd
        + ", trSa="
        + trSa
        + ", trSu="
        + trSu
        + ", trWd="
        + trWd
        + ", wiSa="
        + wiSa
        + ", wiSu="
        + wiSu
        + ", wiWd="
        + wiWd
        + '}';
  }
}
