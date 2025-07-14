/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value.load;

import static edu.ie3.datamodel.models.value.load.BdewLoadValues.BdewSeason.*;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.time.Month.*;
import static java.util.Calendar.SATURDAY;
import static java.util.Calendar.SUNDAY;
import static tech.units.indriya.unit.Units.WATT;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.value.PValue;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import tech.units.indriya.quantity.Quantities;

/** Load values for a {@link BdewStandardLoadProfile} */
public abstract sealed class BdewLoadValues<S> implements LoadValues<BdewStandardLoadProfile>
    permits BdewLoadValues.BDEW1999, BdewLoadValues.BDEW2025 {
  private final Map<BDEWKey<S>, Double> values;

  protected BdewLoadValues(Map<BDEWKey<S>, Double> values) {
    this.values = values;
  }

  /**
   * Returns the actual power for the given time.
   *
   * @param time given time.
   * @return the power in kW as double
   */
  public abstract double getPower(ZonedDateTime time);

  /**
   * Getter for the actual power value in kW.
   *
   * @param key of the value, e.g. january saturday
   * @return the value
   */
  public double get(BDEWKey<S> key) {
    return values.get(key);
  }

  /**
   * Getter for the actual power value in kW.
   *
   * @param season either the {@link BdewSeason} for {@link BDEW1999} values or the {@link Month}
   *     for {@link BDEW2025} values
   * @param type day type of the value
   * @return the value
   */
  public double get(S season, DayType type) {
    return get(new BDEWKey<>(season, type));
  }

  /** Returns the values, that may contain the last day of the year, as a stream. */
  public abstract Stream<Double> lastDayOfYearValues();

  /** Returns the values as a stream. */
  public Stream<Double> values() {
    return values.values().stream();
  }

  @Override
  public PValue getValue(ZonedDateTime time, BdewStandardLoadProfile loadProfile) {
    double power =
        switch (loadProfile) {
          case H0, H25, P25, S25 ->
          /* For the residential average profile, a dynamization has to be taken into account */
          dynamization(getPower(time), time.getDayOfYear()); // leap years are ignored
          default -> getPower(time);
        };

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

  /** Scheme for old profiles from the year 1999. */
  public static final class BDEW1999 extends BdewLoadValues<BdewSeason> {

    public BDEW1999(Map<BDEWKey<BdewSeason>, Double> values) {
      super(values);
    }

    @Override
    public double getPower(ZonedDateTime time) {
      DayType type =
          switch (time.getDayOfWeek()) {
            case SATURDAY -> DayType.SATURDAY;
            case SUNDAY -> DayType.SUNDAY;
            default -> DayType.WEEKDAY;
          };

      return get(getSeason(time), type);
    }

    public double getSuSa() {
      return get(SUMMER, DayType.SATURDAY);
    }

    public double getSuSu() {
      return get(SUMMER, DayType.SUNDAY);
    }

    public double getSuWd() {
      return get(SUMMER, DayType.WEEKDAY);
    }

    public double getTrSa() {
      return get(TRANSITION, DayType.SATURDAY);
    }

    public double getTrSu() {
      return get(TRANSITION, DayType.SUNDAY);
    }

    public double getTrWd() {
      return get(TRANSITION, DayType.WEEKDAY);
    }

    public double getWiSa() {
      return get(WINTER, DayType.SATURDAY);
    }

    public double getWiSu() {
      return get(WINTER, DayType.SUNDAY);
    }

    public double getWiWd() {
      return get(WINTER, DayType.WEEKDAY);
    }

    public Stream<Double> lastDayOfYearValues() {
      return Stream.of(
              new BDEWKey<>(WINTER, DayType.SATURDAY),
              new BDEWKey<>(WINTER, DayType.SUNDAY),
              new BDEWKey<>(WINTER, DayType.WEEKDAY))
          .map(this::get);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
      return super.hashCode();
    }

    @Override
    public String toString() {
      String values =
          BDEWKey.getKeys(BdewSeason.values()).stream()
              .map(
                  key -> {
                    double value = get(key);
                    return key.getName() + "=" + value + ", ";
                  })
              .reduce("", (a, b) -> a + b);

      return "BDEW1999{" + values + '}';
    }
  }

  /** Scheme for updated profiles from the year 2025. */
  public static final class BDEW2025 extends BdewLoadValues<Month> {

    public BDEW2025(Map<BDEWKey<Month>, Double> values) {
      super(values);
    }

    @Override
    public double getPower(ZonedDateTime time) {
      DayType type =
          switch (time.getDayOfYear()) {
            case SATURDAY -> DayType.SATURDAY;
            case SUNDAY -> DayType.SUNDAY;
            default -> DayType.WEEKDAY;
          };

      return get(time.getMonth(), type);
    }

    @Override
    public Stream<Double> lastDayOfYearValues() {
      return Stream.of(
              new BDEWKey<>(DECEMBER, DayType.SATURDAY),
              new BDEWKey<>(DECEMBER, DayType.SUNDAY),
              new BDEWKey<>(DECEMBER, DayType.WEEKDAY))
          .map(this::get);
    }

    public double getJanSa() {
      return get(JANUARY, DayType.SATURDAY);
    }

    public double getJanSu() {
      return get(JANUARY, DayType.SUNDAY);
    }

    public double getJanWd() {
      return get(JANUARY, DayType.WEEKDAY);
    }

    public double getFebSa() {
      return get(FEBRUARY, DayType.SATURDAY);
    }

    public double getFebSu() {
      return get(FEBRUARY, DayType.SUNDAY);
    }

    public double getFebWd() {
      return get(FEBRUARY, DayType.WEEKDAY);
    }

    public double getMarSa() {
      return get(MARCH, DayType.SATURDAY);
    }

    public double getMarSu() {
      return get(MARCH, DayType.SUNDAY);
    }

    public double getMarWd() {
      return get(MARCH, DayType.WEEKDAY);
    }

    public double getAprSa() {
      return get(APRIL, DayType.SATURDAY);
    }

    public double getAprSu() {
      return get(APRIL, DayType.SUNDAY);
    }

    public double getAprWd() {
      return get(APRIL, DayType.WEEKDAY);
    }

    public double getMaySa() {
      return get(MAY, DayType.SATURDAY);
    }

    public double getMaySu() {
      return get(MAY, DayType.SUNDAY);
    }

    public double getMayWd() {
      return get(MAY, DayType.WEEKDAY);
    }

    public double getJunSa() {
      return get(JUNE, DayType.SATURDAY);
    }

    public double getJunSu() {
      return get(JUNE, DayType.SUNDAY);
    }

    public double getJunWd() {
      return get(JUNE, DayType.WEEKDAY);
    }

    public double getJulSa() {
      return get(JULY, DayType.SATURDAY);
    }

    public double getJulSu() {
      return get(JULY, DayType.SUNDAY);
    }

    public double getJulWd() {
      return get(JULY, DayType.WEEKDAY);
    }

    public double getAugSa() {
      return get(AUGUST, DayType.SATURDAY);
    }

    public double getAugSu() {
      return get(AUGUST, DayType.SUNDAY);
    }

    public double getAugWd() {
      return get(AUGUST, DayType.WEEKDAY);
    }

    public double getSepSa() {
      return get(SEPTEMBER, DayType.SATURDAY);
    }

    public double getSepSu() {
      return get(SEPTEMBER, DayType.SUNDAY);
    }

    public double getSepWd() {
      return get(SEPTEMBER, DayType.WEEKDAY);
    }

    public double getOctSa() {
      return get(OCTOBER, DayType.SATURDAY);
    }

    public double getOctSu() {
      return get(OCTOBER, DayType.SUNDAY);
    }

    public double getOctWd() {
      return get(OCTOBER, DayType.WEEKDAY);
    }

    public double getNovSa() {
      return get(NOVEMBER, DayType.SATURDAY);
    }

    public double getNovSu() {
      return get(NOVEMBER, DayType.SUNDAY);
    }

    public double getNovWd() {
      return get(NOVEMBER, DayType.WEEKDAY);
    }

    public double getDecSa() {
      return get(DECEMBER, DayType.SATURDAY);
    }

    public double getDecSu() {
      return get(DECEMBER, DayType.SUNDAY);
    }

    public double getDecWd() {
      return get(DECEMBER, DayType.WEEKDAY);
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      return o != null && getClass() == o.getClass();
    }

    @Override
    public int hashCode() {
      return super.hashCode();
    }

    @Override
    public String toString() {
      String values =
          BDEWKey.getKeys(Month.values()).stream()
              .map(
                  key -> {
                    double value = get(key);
                    return key.getName() + "=" + value + ", ";
                  })
              .reduce("", (a, b) -> a + b);

      return "BDEW2025{" + values + '}';
    }
  }

  public record BDEWKey<S>(S season, DayType type) {

    public String getName() {
      String seasonString = season.toString();

      String seasonName;

      if (seasonString.length() >= 3) {
        seasonName = seasonString.charAt(0) + seasonString.substring(1, 3).toLowerCase();
      } else {
        seasonName = seasonString;
      }

      return seasonName + type.name;
    }

    public static <S> Collection<BDEWKey<S>> getKeys(S[] seasons) {
      return Arrays.stream(seasons)
          .flatMap(
              m ->
                  Arrays.stream(BdewLoadValues.DayType.values())
                      .map(type -> new BDEWKey<>(m, type)))
          .toList();
    }

    public static <S> Map<BDEWKey<S>, String> getFields(S[] seasons) {
      return getKeys(seasons).stream().collect(Collectors.toMap(e -> e, BDEWKey::getName));
    }
  }

  public enum BdewSeason {
    SUMMER("Su"),
    TRANSITION("Tr"),
    WINTER("Wi");

    private final String key;

    BdewSeason(String key) {
      this.key = key;
    }

    public static BdewSeason parse(String key) throws ParsingException {
      return switch (key) {
        case "Wi", "Winter" -> WINTER;
        case "Su", "Summer" -> SUMMER;
        case "Tr", "Intermediate" -> TRANSITION;
        default -> throw new ParsingException(
            "There is no season for key:"
                + key
                + ". Permissible keys: 'Wi', 'Winter', 'Su', 'Summer', 'Tr', 'Intermediate'");
      };
    }

    /**
     * Creates a season from given time
     *
     * @param time the time
     * @return a season
     */
    public static BdewSeason getSeason(ZonedDateTime time) {
      int day = time.getDayOfMonth();

      // winter:      1.11.-20.03.
      // summer:     15.05.-14.09.
      // transition: 21.03.-14.05. and
      //             15.09.-31.10.
      // (VDEW handbook)

      return switch (time.getMonth()) {
        case NOVEMBER, DECEMBER, JANUARY, FEBRUARY -> WINTER;
        case MARCH -> {
          if (day <= 20) {
            yield WINTER;
          } else {
            yield TRANSITION;
          }
        }
        case MAY -> {
          if (day >= 15) {
            yield SUMMER;
          } else {
            yield TRANSITION;
          }
        }
        case JUNE, JULY, AUGUST -> SUMMER;
        case SEPTEMBER -> {
          if (day <= 14) {
            yield SUMMER;
          } else {
            yield TRANSITION;
          }
        }
        default -> TRANSITION;
      };
    }

    public String getKey() {
      return key;
    }

    @Override
    public String toString() {
      return key;
    }
  }

  public enum DayType {
    WEEKDAY("Wd"),
    SATURDAY("Sa"),
    SUNDAY("Su");

    public final String name;

    DayType(String name) {
      this.name = name;
    }
  }
}
