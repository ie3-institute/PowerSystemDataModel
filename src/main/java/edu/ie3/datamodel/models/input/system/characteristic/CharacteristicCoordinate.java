/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import edu.ie3.datamodel.exceptions.ParsingException;
import java.util.Locale;
import java.util.Objects;
import javax.measure.Quantity;
import javax.measure.Unit;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

/** Class to describe one coordinate of a given {@link CharacteristicInput} */
public class CharacteristicCoordinate<A extends Quantity<A>, O extends Quantity<O>>
    implements Comparable<CharacteristicCoordinate<A, O>> {
  public static final String REQUIRED_FORMAT = "(%d,%d)";

  private final ComparableQuantity<A> x;
  private final ComparableQuantity<O> y;

  /**
   * Build a new coordinate
   *
   * @param x Value on the abscissa
   * @param y Value on the ordinate
   */
  public CharacteristicCoordinate(ComparableQuantity<A> x, ComparableQuantity<O> y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Builds a coordinate from a given input string. The string has to be of format '(%d,%d)'. Spaces
   * are tolerated
   *
   * @param input Input string to parse
   * @param abscissaUnit Unit to use on the abscissa
   * @param ordinateUnit Unit to use on the ordinate
   */
  public CharacteristicCoordinate(String input, Unit<A> abscissaUnit, Unit<O> ordinateUnit)
      throws ParsingException {
    String trimmed = input.trim();
    if (!trimmed.startsWith("(") || !trimmed.endsWith(")"))
      throw new ParsingException(buildExceptionMessage(input));
    String[] entries = trimmed.replaceAll("^\\(|\\)$", "").split(",");
    if (entries.length != 2) throw new ParsingException(buildExceptionMessage(input));

    try {
      this.x = Quantities.getQuantity(Double.parseDouble(entries[0]), abscissaUnit);
    } catch (NumberFormatException nfe) {
      throw new ParsingException(
          buildExceptionMessage(input, "Abscissa value cannot be parsed to double."), nfe);
    }

    try {
      this.y = Quantities.getQuantity(Double.parseDouble(entries[1]), ordinateUnit);
    } catch (NumberFormatException nfe) {
      throw new ParsingException(
          buildExceptionMessage(input, "Abscissa value cannot be parsed to double."), nfe);
    }
  }

  /**
   * Builds a message for an exception to throw.
   *
   * @param input Malformed input string
   */
  private static String buildExceptionMessage(String input) {
    return buildExceptionMessage(
        input, "It doesn't comply with the required format '" + REQUIRED_FORMAT + "'.");
  }

  /**
   * Builds a custom message for an exception to throw.
   *
   * @param input Malformed input string
   * @param message Additional, custom message
   */
  private static String buildExceptionMessage(String input, String message) {
    return "Cannot parse " + input + " to CharacteristicCoordinate. " + message;
  }

  /** @return the position on the abscissa */
  public ComparableQuantity<A> getX() {
    return x;
  }

  /** @return the position on the ordinate */
  public ComparableQuantity<O> getY() {
    return y;
  }

  /**
   * De-serializes the given coordinate to a string
   *
   * @param decimalPlaces Desired amount of decimal places
   * @return The de-serialized coordinate
   */
  public String deSerialize(int decimalPlaces) {
    String formattingString = String.format("(%%.%sf,%%.%sf)", decimalPlaces, decimalPlaces);
    return String.format(
        Locale.ENGLISH, formattingString, x.getValue().doubleValue(), y.getValue().doubleValue());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CharacteristicCoordinate<?, ?> that = (CharacteristicCoordinate<?, ?>) o;
    return Objects.equals(x, that.x) && Objects.equals(y, that.y);
  }

  @Override
  public int hashCode() {
    return Objects.hash(x, y);
  }

  @Override
  public String toString() {
    return "CharacteristicCoordinate{" + "x=" + x + ", y=" + y + '}';
  }

  /**
   * Compares this instance against another coordinate. They are compared on the abscissa first. If
   * they are on the same location there, the ordinate is taken into account.
   *
   * @param b The other coordinate
   * @return The comparision result
   */
  @Override
  public int compareTo(CharacteristicCoordinate<A, O> b) {
    int abscissaCompare = x.compareTo(b.getX());
    return abscissaCompare != 0 ? abscissaCompare : y.compareTo(b.getY());
  }
}
