/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import edu.ie3.datamodel.models.input.InputEntity;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.measure.Quantity;
import javax.measure.Unit;
import tec.uom.se.quantity.Quantities;

/** Describes characteristics of assets */
public abstract class CharacteristicInput<A extends Quantity<A>, O extends Quantity<O>>
    extends InputEntity {
  protected final String prefix;
  protected final int decimalPlaces;

  protected final SortedSet<CharacteristicCoordinate<A, O>> coordinates;

  /**
   * Constructor for the abstract class
   *
   * @param uuid Unique identifier
   * @param coordinates Set of coordinates that describe the characteristic
   * @param prefix Prefix, that prepends the actual characteristic
   * @param decimalPlaces Desired amount of decimal places when de-serializing the characteristic
   */
  public CharacteristicInput(
      UUID uuid,
      SortedSet<CharacteristicCoordinate<A, O>> coordinates,
      String prefix,
      int decimalPlaces) {
    super(uuid);
    this.coordinates = Collections.unmodifiableSortedSet(coordinates);
    this.prefix = prefix;
    this.decimalPlaces = decimalPlaces;
  }

  public CharacteristicInput(
      UUID uuid,
      String input,
      Pattern matchingPattern,
      Unit<A> abscissaUnit,
      Unit<O> ordinateUnit,
      String prefix,
      int decimalPlaces) {
    super(uuid);
    this.prefix = prefix;
    this.decimalPlaces = decimalPlaces;
    String coordinateList = extractCoordinateList(input, matchingPattern);
    this.coordinates = buildCoordinatesFromString(coordinateList, abscissaUnit, ordinateUnit);
  }

  /**
   * Extracts the coordinate list from the given input
   *
   * @param input Input string for the whole characteristic
   * @param matchingPattern Pattern to match the underlying characteristic
   * @return The string list of coordinates
   */
  private String extractCoordinateList(String input, Pattern matchingPattern) {
    Matcher matcher = matchingPattern.matcher(input);
    if (!matcher.matches())
      throw new IllegalArgumentException(
          "The given input '" + input + "' is not a valid representation.");
    return matcher.group(1);
  }

  /**
   * Extracts the coordinates from the input string
   *
   * @param input string list of coordinates
   * @param abscissaUnit Unit for the abscissa
   * @param ordinateUnit Unit for the ordinate
   * @return an unmodifiable sorted set of coordinates
   */
  private SortedSet<CharacteristicCoordinate<A, O>> buildCoordinatesFromString(
      String input, Unit<A> abscissaUnit, Unit<O> ordinateUnit) {
    Matcher coordinateMatcher = CharacteristicCoordinate.MATCHING_PATTERN.matcher(input);
    SortedSet<CharacteristicCoordinate<A, O>> parsedCoordinate = new TreeSet<>();
    while (coordinateMatcher.find()) {
      double xValue = CharacteristicCoordinate.getXFromString(coordinateMatcher.group(0));
      double yValue = CharacteristicCoordinate.getYFromString(coordinateMatcher.group(0));

      parsedCoordinate.add(
          new CharacteristicCoordinate<>(
              Quantities.getQuantity(xValue, abscissaUnit),
              Quantities.getQuantity(yValue, ordinateUnit)));
    }
    return Collections.unmodifiableSortedSet(parsedCoordinate);
  }

  public SortedSet<CharacteristicCoordinate<A, O>> getCoordinates() {
    return coordinates;
  }

  /**
   * De-serialize the characteristic to a commonly understood string
   *
   * @return the characteristic as de-serialized string
   */
  public String deSerialize() {
    return prefix
        + ":{"
        + coordinates.stream()
            .map(coordinate -> coordinate.deSerialize(decimalPlaces))
            .collect(Collectors.joining(","))
        + "}";
  }

  /**
   * Builds a matching pattern that is able to recognize a specific characteristic
   *
   * @param prefix The concrete prefix
   * @return A pattern, that matches the characteristic
   */
  public static Pattern buildMatchingPattern(String prefix) {
    return Pattern.compile(
        prefix + ":\\{((" + CharacteristicCoordinate.MATCHING_PATTERN.pattern() + ",?)+)}");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CharacteristicInput<?, ?> that = (CharacteristicInput<?, ?>) o;
    return coordinates.equals(that.coordinates);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), coordinates);
  }

  @Override
  public String toString() {
    return "CharacteristicInput{" + "uuid=" + uuid + ", coordinates=" + coordinates + '}';
  }
}
