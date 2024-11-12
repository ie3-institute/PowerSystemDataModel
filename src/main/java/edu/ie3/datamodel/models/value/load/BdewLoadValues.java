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
  private final double SuSa;
  private final double SuSu;
  private final double SuWd;
  private final double TrSa;
  private final double TrSu;
  private final double TrWd;
  private final double WiSa;
  private final double WiSu;
  private final double WiWd;

  public BdewLoadValues(
      double SuSa,
      double SuSu,
      double SuWd,
      double TrSa,
      double TrSu,
      double TrWd,
      double WiSa,
      double WiSu,
      double WiWd) {
    this.SuSa = SuSa;
    this.SuSu = SuSu;
    this.SuWd = SuWd;
    this.TrSa = TrSa;
    this.TrSu = TrSu;
    this.TrWd = TrWd;
    this.WiSa = WiSa;
    this.WiSu = WiSu;
    this.WiWd = WiWd;
  }

  @Override
  public PValue getValue(ZonedDateTime time, LoadProfile loadProfile) {
    Map<BdewSeason, Double> mapping =
        switch (time.getDayOfWeek()) {
          case SATURDAY -> Map.of(
              SUMMER, SuSa,
              WINTER, WiSa,
              TRANSITION, TrSa);
          case SUNDAY -> Map.of(
              SUMMER, SuSu,
              WINTER, WiSu,
              TRANSITION, TrSu);
          default -> Map.of(
              SUMMER, SuWd,
              WINTER, WiWd,
              TRANSITION, TrWd);
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
    return SuSa;
  }

  public double getSuSu() {
    return SuSu;
  }

  public double getSuWd() {
    return SuWd;
  }

  public double getTrSa() {
    return TrSa;
  }

  public double getTrSu() {
    return TrSu;
  }

  public double getTrWd() {
    return TrWd;
  }

  public double getWiSa() {
    return WiSa;
  }

  public double getWiSu() {
    return WiSu;
  }

  public double getWiWd() {
    return WiWd;
  }

  public List<Double> values() {
    return List.of(SuSa, SuSu, SuWd, TrSa, TrSu, TrWd, WiSa, WiSu, WiWd);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    BdewLoadValues that = (BdewLoadValues) o;
    return Objects.equals(SuSa, that.SuSa)
        && Objects.equals(SuSu, that.SuSu)
        && Objects.equals(SuWd, that.SuWd)
        && Objects.equals(TrSa, that.TrSa)
        && Objects.equals(TrSu, that.TrSu)
        && Objects.equals(TrWd, that.TrWd)
        && Objects.equals(WiSa, that.WiSa)
        && Objects.equals(WiSu, that.WiSu)
        && Objects.equals(WiWd, that.WiWd);
  }

  @Override
  public int hashCode() {
    return Objects.hash(SuWd, SuSa, SuSu, WiWd, WiSa, WiSu, TrWd, TrSa, TrSu);
  }

  @Override
  public String toString() {
    return "BDEWLoadValues{"
        + "SuWd="
        + SuWd
        + ", SuSa="
        + SuSa
        + ", SuSu="
        + SuSu
        + ", WiWd="
        + WiWd
        + ", WiSa="
        + WiSa
        + ", WiSu="
        + WiSu
        + ", TrWd="
        + TrWd
        + ", TrSa="
        + TrSa
        + ", TrSu="
        + TrSu
        + '}';
  }
}
