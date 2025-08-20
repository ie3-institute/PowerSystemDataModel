/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import edu.ie3.datamodel.exceptions.ParsingException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import javax.measure.Quantity;
import javax.measure.Unit;

/**
 * Describes characteristics of assets
 *
 * @param <A> Type of quantity, that applies to the abscissa
 * @param <O> Type of quantity, that applies to the ordinate
 */
public abstract class CharacteristicInput<A extends Quantity<A>, O extends Quantity<O>>
    implements Serializable {

  /** Prefix used to identify this characteristic input. */
  protected final String characteristicPrefix;

  /** A sorted set of points defining the characteristic curve. */
  private final SortedSet<CharacteristicPoint<A, O>> points;

  /**
   * Constructor for the abstract class
   *
   * @param points Set of points that describe the characteristic
   * @param characteristicPrefix Prefix, that prepends the actual characteristic
   */
  protected CharacteristicInput(
      SortedSet<CharacteristicPoint<A, O>> points, String characteristicPrefix) {
    this.points = Collections.unmodifiableSortedSet(points);
    /** Prefix used to identify this characteristic input. */
    this.characteristicPrefix = characteristicPrefix;
  }

  /**
   * Instantiates a new Characteristic input.
   *
   * @param input the input
   * @param abscissaUnit the abscissa unit
   * @param ordinateUnit the ordinate unit
   * @param characteristicPrefix the characteristic prefix
   * @throws ParsingException the parsing exception
   */
  protected CharacteristicInput(
      String input, Unit<A> abscissaUnit, Unit<O> ordinateUnit, String characteristicPrefix)
      throws ParsingException {
    this.characteristicPrefix = characteristicPrefix;

    if (!input.startsWith(characteristicPrefix + ":{") || !input.endsWith("}"))
      throw new ParsingException(
          "Cannot parse '"
              + input
              + "' to characteristic. It has to be of the form '"
              + characteristicPrefix
              + ":{"
              + CharacteristicPoint.REQUIRED_FORMAT
              + ",...}'");

    String coordinateList = extractCoordinateList(input);
    this.points = buildCoordinatesFromString(coordinateList, abscissaUnit, ordinateUnit);
  }

  /**
   * Builds a regex, that is suitable to match '[prefix]:{'
   *
   * @param prefix Unique prefix to an instance of the Characteristic
   * @return The suitable regex
   */
  public static String buildStartingRegex(String prefix) {
    return "^" + prefix + ":\\{";
  }

  /**
   * Extracts the point list from the given input
   *
   * @param input Input string for the whole characteristic
   * @return The string list of points
   */
  private String extractCoordinateList(String input) {
    return input.replaceAll(buildStartingRegex(characteristicPrefix), "").replaceAll("}$", "");
  }

  /**
   * Splits up a String of point definition and parses them to {@link CharacteristicPoint}
   *
   * @param input Comma-separated list of point definitions
   * @param abscissaUnit Unit to use on the abscissa
   * @param ordinateUnit Unit to use on the ordinate
   * @return An unmodifiable sorted set of {@link CharacteristicPoint}s
   * @throws ParsingException If one of the points cannot be parsed
   */
  private SortedSet<CharacteristicPoint<A, O>> buildCoordinatesFromString(
      String input, Unit<A> abscissaUnit, Unit<O> ordinateUnit) throws ParsingException {
    /* Splits the points only at those commas, that are preceded by a ')' */
    String[] entries = input.split("(?<=\\)),");

    SortedSet<CharacteristicPoint<A, O>> parsedCoordinates = new TreeSet<>();
    for (String entry : entries) {
      try {
        parsedCoordinates.add(new CharacteristicPoint<>(entry, abscissaUnit, ordinateUnit));
      } catch (ParsingException pe) {
        throw new ParsingException(
            "Cannot parse '" + input + "' to Set of points as it contains a malformed point.", pe);
      }
    }
    return Collections.unmodifiableSortedSet(parsedCoordinates);
  }

  /**
   * Returns all points that define this characteristic.
   *
   * @return An unmodifiable sorted set containing all {@link CharacteristicPoint}s associated with
   *     this characterisitc input.
   */
  public SortedSet<CharacteristicPoint<A, O>> getPoints() {
    return points;
  }

  /**
   * Serialize the characteristic to a commonly understood string
   *
   * @return the characteristic as serialized string
   */
  public String serialize() {
    return characteristicPrefix
        + ":{"
        + points.stream().map(CharacteristicPoint::serialize).collect(Collectors.joining(","))
        + "}";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof CharacteristicInput<?, ?> that)) return false;

    return characteristicPrefix.equals(that.characteristicPrefix) && points.equals(that.points);
  }

  @Override
  public int hashCode() {
    return Objects.hash(characteristicPrefix, points);
  }

  @Override
  public String toString() {
    return "CharacteristicInput{" + "points=" + points + '}';
  }
}
