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
import java.io.*;
import java.time.Month;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import tech.units.indriya.quantity.Quantities;

/** Load values for a {@link BdewStandardLoadProfile} */
public final class BdewLoadValues implements LoadValues<BdewStandardLoadProfile> {
  public final BdewScheme scheme;
  private transient Map<BdewKey, Double> values;

  public BdewLoadValues(BdewScheme scheme, Map<BdewKey, Double> values) {
    this.scheme = scheme;
    this.values = Collections.unmodifiableMap(values);
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

  // custom serialization (needed for the values)

  @Serial
  private void writeObject(ObjectOutputStream out) throws IOException {
    out.defaultWriteObject();
    out.writeObject(values);
  }

  @Serial
  @SuppressWarnings("unchecked")
  private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    this.values = Collections.unmodifiableMap((Map<BdewKey, Double>) in.readObject());
  }

  public sealed interface BdewKey extends Serializable {

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
    static BdewMap<String> toMap(BdewScheme scheme) {
      return new BdewMap<>(
          scheme.keys.stream().collect(Collectors.toMap(e -> e, BdewKey::getName)));
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
      return type.abbreviation;
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
      return type.abbreviation;
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

    public final String abbreviation;

    DayType(String abbreviation) {
      this.abbreviation = abbreviation;
    }
  }

  /** Scheme for underlying values of a {@link BdewLoadValues}. */
  public enum BdewScheme implements Scheme, Serializable {
    BDEW1999(BdewKey.getKeys(BdewSeason.values(), Bdew1999Key::new)),
    BDEW2025(BdewKey.getKeys(Month.values(), Bdew2025Key::new));

    private final Collection<BdewKey> keys;

    BdewScheme(Collection<BdewKey> keys) {
      this.keys = keys;
    }

    public Collection<BdewKey> getKeys() {
      return keys;
    }

    public boolean isAccepted(BdewKey key) {
      return keys.contains(key);
    }
  }

  /**
   * Immutable map that take {@link BdewKey}.
   *
   * @param <V> type of value
   */
  public static final class BdewMap<V> {
    private final Map<BdewKey, V> values;

    public BdewMap(Map<BdewKey, V> values) {
      this.values = values;
    }

    /**
     * Returns the number of key-value mappings in this map. If the map contains more than {@code
     * Integer.MAX_VALUE} elements, returns {@code Integer.MAX_VALUE}.
     *
     * @return the number of key-value mappings in this map
     */
    public int size() {
      return values.size();
    }

    /**
     * Returns {@code true} if this map contains no key-value mappings.
     *
     * @return {@code true} if this map contains no key-value mappings
     */
    public boolean isEmpty() {
      return values.isEmpty();
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified key. More formally,
     * returns {@code true} if and only if this map contains a mapping for a key {@code k} such that
     * {@code Objects.equals(key, k)}. (There can be at most one such mapping.)
     *
     * @param key key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified key
     * @throws NullPointerException if the specified key is null and this map does not permit null
     *     keys (<a
     *     href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    public boolean containsKey(BdewKey key) {
      return values.containsKey(key);
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null} if this map contains
     * no mapping for the key.
     *
     * <p>More formally, if this map contains a mapping from a key {@code k} to a value {@code v}
     * such that {@code Objects.equals(key, k)}, then this method returns {@code v}; otherwise it
     * returns {@code null}. (There can be at most one such mapping.)
     *
     * <p>If this map permits null values, then a return value of {@code null} does not
     * <i>necessarily</i> indicate that the map contains no mapping for the key; it's also possible
     * that the map explicitly maps the key to {@code null}. The {@link #containsKey containsKey}
     * operation may be used to distinguish these two cases.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or {@code null} if this map contains
     *     no mapping for the key
     * @throws NullPointerException if the specified key is null and this map does not permit null
     *     keys (<a
     *     href="{@docRoot}/java.base/java/util/Collection.html#optional-restrictions">optional</a>)
     */
    public V get(BdewKey key) {
      return values.get(key);
    }

    /**
     * Returns a {@link Set} view of the keys contained in this map. The set is backed by the map,
     * so changes to the map are reflected in the set, and vice-versa. If the map is modified while
     * an iteration over the set is in progress (except through the iterator's own {@code remove}
     * operation), the results of the iteration are undefined. The set supports element removal,
     * which removes the corresponding mapping from the map, via the {@code Iterator.remove}, {@code
     * Set.remove}, {@code removeAll}, {@code retainAll}, and {@code clear} operations. It does not
     * support the {@code add} or {@code addAll} operations.
     *
     * @return a set view of the keys contained in this map
     */
    public Set<BdewKey> keySet() {
      return values.keySet();
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map. The collection is
     * backed by the map, so changes to the map are reflected in the collection, and vice-versa. If
     * the map is modified while an iteration over the collection is in progress (except through the
     * iterator's own {@code remove} operation), the results of the iteration are undefined. The
     * collection supports element removal, which removes the corresponding mapping from the map,
     * via the {@code Iterator.remove}, {@code Collection.remove}, {@code removeAll}, {@code
     * retainAll} and {@code clear} operations. It does not support the {@code add} or {@code
     * addAll} operations.
     *
     * @return a collection view of the values contained in this map
     */
    public Collection<V> values() {
      return values.values();
    }

    /**
     * Maps the {@link BdewKey} to a new value.
     *
     * @param mapper function
     * @return the new {@link BdewMap}
     * @param <R> type of new values
     */
    public <R> Map<BdewKey, R> map(Function<V, R> mapper) {
      HashMap<BdewKey, R> map = new HashMap<>();
      values.forEach((key, value) -> map.put(key, mapper.apply(value)));
      return map;
    }
  }
}
