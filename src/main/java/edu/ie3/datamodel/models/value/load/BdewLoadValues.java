/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value.load;

import static edu.ie3.datamodel.models.value.load.BdewLoadValues.BdewSeason.WINTER;
import static edu.ie3.datamodel.models.value.load.BdewLoadValues.BdewSeason.getSeason;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.time.Month.DECEMBER;
import static tech.units.indriya.unit.Units.WATT;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile;
import edu.ie3.datamodel.models.value.PValue;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import tech.units.indriya.quantity.Quantities;

/** Load values for a {@link BdewStandardLoadProfile} */
public final class BdewLoadValues implements LoadValues<BdewStandardLoadProfile> {
  public final BdewScheme scheme;
  private final Map<BdewKey, Double> values;

  public BdewLoadValues(BdewScheme scheme, Map<BdewKey, Double> values) {
    this.scheme = scheme;
    this.values = values;
  }

  /**
   * Returns the actual power for the given time.
   *
   * @param time given time.
   * @return the power in kW as double
   */
  public double getPower(ZonedDateTime time) {
    DayType type =
        switch (time.getDayOfWeek()) {
          case SATURDAY -> DayType.SATURDAY;
          case SUNDAY -> DayType.SUNDAY;
          default -> DayType.WEEKDAY;
        };

    return switch (scheme) {
      case BDEW1999 -> get(new Bdew1999Key(getSeason(time), type));
      case BDEW2025 -> get(new Bdew2025Key(time.getMonth(), type));
    };
  }

  /**
   * Getter for the actual power value in kW.
   *
   * @param key of the value, e.g. january saturday
   * @return the value
   */
  public double get(BdewKey key) {
    if (!scheme.isAccepted(key)) {
      throw new IllegalArgumentException(
          "The given key '" + key + "' is not accepted by the scheme: " + scheme);
    }

    return values.get(key);
  }

  /** Returns the values, that may contain the last day of the year, as a stream. */
  public Stream<Double> lastDayOfYearValues() {
    Stream<BdewKey> keys =
        switch (scheme) {
          case BDEW1999 -> Stream.of(
              new Bdew1999Key(WINTER, DayType.SATURDAY),
              new Bdew1999Key(WINTER, DayType.SUNDAY),
              new Bdew1999Key(WINTER, DayType.WEEKDAY));
          case BDEW2025 -> Stream.of(
              new Bdew2025Key(DECEMBER, DayType.SATURDAY),
              new Bdew2025Key(DECEMBER, DayType.SUNDAY),
              new Bdew2025Key(DECEMBER, DayType.WEEKDAY));
        };

    return keys.map(values::get);
  }

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

  @Override
  public Optional<Scheme> getScheme() {
    return Optional.ofNullable(scheme);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    BdewLoadValues that = (BdewLoadValues) o;
    return Objects.equals(scheme, that.scheme) && Objects.equals(values, that.values);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scheme, values);
  }

  private String valueString() {
    return values.entrySet().stream()
        .map(
            entry -> {
              double value = entry.getValue();
              return entry.getKey().getName() + "=" + value + ", ";
            })
        .reduce("", (a, b) -> a + b);
  }

  @Override
  public String toString() {
    return "BdewLoadValues{" + "scheme=" + scheme + valueString() + '}';
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

  public sealed interface BdewKey {

    /**
     * Returns the abbreviation of either a {@link BdewSeason} for {@link BdewScheme#BDEW1999} or
     * the {@link Month} for {@link BdewScheme#BDEW2025}.
     */
    String getSeasonName();

    /** Returns the name of the {@link DayType}. */
    String getDayTypeName();

    /** Returns the name of the key. */
    default String getName() {
      return getSeasonName() + getDayTypeName();
    }

    /** Returns the name of the field. */
    default String getFieldName() {
      return getSeasonName().toLowerCase() + getDayTypeName();
    }

    /**
     * Maps all keys of a given {@link BdewScheme} to their field names
     *
     * @param scheme given scheme
     * @return a map: key to field name
     */
    static Map<BdewKey, String> toMap(BdewScheme scheme) {
      return scheme.keys.stream().collect(Collectors.toMap(e -> e, BdewKey::getName));
    }

    private static <S, K extends BdewKey> Collection<K> getKeys(
        S[] seasons, BiFunction<S, DayType, K> function) {
      return Arrays.stream(seasons)
          .flatMap(
              m ->
                  Arrays.stream(BdewLoadValues.DayType.values())
                      .map(type -> function.apply(m, type)))
          .toList();
    }
  }

  private record Bdew1999Key(BdewSeason season, DayType type) implements BdewKey {

    @Override
    public String getSeasonName() {
      return season.toString().substring(0, 2);
    }

    @Override
    public String getDayTypeName() {
      return type.name;
    }
  }

  private record Bdew2025Key(Month season, DayType type) implements BdewKey {
    @Override
    public String getSeasonName() {
      String seasonString = season.toString();
      return seasonString.charAt(0) + seasonString.substring(1, 3).toLowerCase();
    }

    @Override
    public String getDayTypeName() {
      return type.name;
    }
  }

  /** Season defined for {@link BdewScheme#BDEW1999}. */
  public enum BdewSeason {
    SUMMER("Summer"),
    TRANSITION("Transition"),
    WINTER("Winter");

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

  /** Day type used for {@link BdewLoadValues}. */
  public enum DayType {
    WEEKDAY("Wd"),
    SATURDAY("Sa"),
    SUNDAY("Su");

    public final String name;

    DayType(String name) {
      this.name = name;
    }
  }

  /** Scheme for underlying values of a {@link BdewLoadValues}. */
  public enum BdewScheme implements Scheme {
    BDEW1999(BdewKey.getKeys(BdewSeason.values(), Bdew1999Key::new)),
    BDEW2025(BdewKey.getKeys(Month.values(), Bdew2025Key::new));

    public final Collection<BdewKey> keys;

    BdewScheme(Collection<BdewKey> keys) {
      this.keys = keys;
    }

    public boolean isAccepted(BdewKey key) {
      return keys.contains(key);
    }
  }
}
