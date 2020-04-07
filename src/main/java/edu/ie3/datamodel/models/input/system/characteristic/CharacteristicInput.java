/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import edu.ie3.datamodel.exceptions.ParsingException;
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
public abstract class CharacteristicInput<A extends Quantity<A>, O extends Quantity<O>> {
  protected final String characteristicPrefix;
  protected final int decimalPlaces;

  protected final SortedSet<CharacteristicCoordinate<A, O>> coordinates;

  /**
   * Constructor for the abstract class
   *
   * @param coordinates Set of coordinates that describe the characteristic
   * @param characteristicPrefix Prefix, that prepends the actual characteristic
   * @param decimalPlaces Desired amount of decimal places when de-serializing the characteristic
   */
  public CharacteristicInput(
      SortedSet<CharacteristicCoordinate<A, O>> coordinates,
      String characteristicPrefix,
      int decimalPlaces) {
    this.coordinates = Collections.unmodifiableSortedSet(coordinates);
    this.characteristicPrefix = characteristicPrefix;
    this.decimalPlaces = decimalPlaces;
  }

  public CharacteristicInput(
      String input,
      Unit<A> abscissaUnit,
      Unit<O> ordinateUnit,
      String characteristicPrefix,
      int decimalPlaces)
      throws ParsingException {
    this.characteristicPrefix = characteristicPrefix;
    this.decimalPlaces = decimalPlaces;

    if (!input.startsWith(characteristicPrefix + ":{") || !input.endsWith("}"))
      throw new ParsingException(
          "Cannot parse '"
              + input
              + "' to characteristic. It has to be of the form '"
              + characteristicPrefix
              + ":{"
              + CharacteristicCoordinate.REQUIRED_FORMAT
              + ",...}'");

    String coordinateList = extractCoordinateList(input);
    this.coordinates = buildCoordinatesFromString(coordinateList, abscissaUnit, ordinateUnit);
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
   * Extracts the coordinate list from the given input
   *
   * @param input Input string for the whole characteristic
   * @return The string list of coordinates
   */
  private String extractCoordinateList(String input) {
    return input.replaceAll(buildStartingRegex(characteristicPrefix), "").replaceAll("}$", "");
  }

  /**
   * Splits up a String of coordinate definition and parses them to {@link CharacteristicCoordinate}
   *
   * @param input Comma-separated list of coordinate definitions
   * @param abscissaUnit Unit to use on the abscissa
   * @param ordinateUnit Unit to use on the ordinate
   * @return An unmodifiable sorted set of {@link CharacteristicCoordinate}s
   * @throws ParsingException If one of the coordinates cannot be parsed
   */
  private SortedSet<CharacteristicCoordinate<A, O>> buildCoordinatesFromString(
      String input, Unit<A> abscissaUnit, Unit<O> ordinateUnit) throws ParsingException {
    /* Splits the coordinates only at those commas, that are preceded by a ')' */
    String[] entries = input.split("(?<=\\)),");

    SortedSet<CharacteristicCoordinate<A, O>> parsedCoordinates = new TreeSet<>();
    for (String entry : entries) {
      try {
        parsedCoordinates.add(new CharacteristicCoordinate<>(entry, abscissaUnit, ordinateUnit));
      } catch (ParsingException pe) {
        throw new ParsingException(
            "Cannot parse '"
                + input
                + "' to Set of coordinates as it contains a malformed coordinate.",
            pe);
      }
    }
    return Collections.unmodifiableSortedSet(parsedCoordinates);
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
    return characteristicPrefix
        + ":{"
        + coordinates.stream()
            .map(coordinate -> coordinate.deSerialize(decimalPlaces))
            .collect(Collectors.joining(","))
        + "}";
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
    return "CharacteristicInput{" + "coordinates=" + coordinates + '}';
  }
}
