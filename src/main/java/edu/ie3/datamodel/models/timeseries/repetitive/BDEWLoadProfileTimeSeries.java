/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.repetitive;

import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATT;
import static java.lang.Math.pow;
import static java.lang.Math.round;

import edu.ie3.datamodel.models.Season;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.value.PValue;
import java.time.DayOfWeek;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * Describes a bdew load profile time series with repetitive values that can be calculated from a
 * pattern
 */
public class BDEWLoadProfileTimeSeries extends LoadProfileTimeSeries<BDEWLoadProfileEntry> {

  public BDEWLoadProfileTimeSeries(
      UUID uuid, BdewStandardLoadProfile loadProfile, Set<BDEWLoadProfileEntry> values) {
    super(uuid, loadProfile, values, e -> new BdewKey(e.getSeason(), e.getDayOfWeek()));
  }

  @Override
  public BdewStandardLoadProfile getLoadProfile() {
    return (BdewStandardLoadProfile) super.getLoadProfile();
  }

  @Override
  public PValue calc(ZonedDateTime time) {
    if (getLoadProfile() == BdewStandardLoadProfile.H0) {
      /* For the residential average profile, a dynamization has to be taken into account */
      return dynamization(super.calc(time), time.getDayOfYear()); // leap years are ignored
    } else {
      return super.calc(time);
    }
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
  private PValue dynamization(PValue load, int t) {
    double factor =
        (-3.92e-10 * pow(t, 4) + 3.2e-7 * pow(t, 3) - 7.02e-5 * pow(t, 2) + 2.1e-3 * t + 1.24);
    double rndFactor = round(factor * 1e4) / 1e4; // round to 4 decimal places
    Function<Double, Double> round =
        l -> round(l * rndFactor * 1e1) / 1e1; // rounded to 1 decimal place

    ComparableQuantity<Power> value =
        load.getP()
            .map(v -> v.getValue().doubleValue())
            .map(round)
            .map(v -> Quantities.getQuantity(v, KILOWATT))
            .orElse(null);

    return new PValue(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String toString() {
    return "BDEWLoadProfileTimeSeries{"
        + "uuid="
        + getUuid()
        + "loadProfile="
        + getLoadProfile()
        + ", valueMapping="
        + getValueMapping()
        + '}';
  }

  @Override
  protected Key fromTime(ZonedDateTime time) {
    Season season = Season.get(time);

    DayOfWeek day =
        switch (time.getDayOfWeek()) {
          case SATURDAY -> DayOfWeek.SATURDAY;
          case SUNDAY -> DayOfWeek.SUNDAY;
          default -> DayOfWeek.MONDAY;
        };

    return new BdewKey(season, day);
  }

  private record BdewKey(Season season, DayOfWeek dayOfWeek) implements Key {}
}
