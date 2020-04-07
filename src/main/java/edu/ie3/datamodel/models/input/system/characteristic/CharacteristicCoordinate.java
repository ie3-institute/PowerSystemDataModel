/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.measure.Quantity;
import org.jetbrains.annotations.NotNull;
import tec.uom.se.ComparableQuantity;

/** Class to describe one coordinate of a given {@link CharacteristicInput} */
public class CharacteristicCoordinate<A extends Quantity<A>, O extends Quantity<O>>
    implements Comparable<CharacteristicCoordinate<A, O>> {
  private static final Pattern DOUBLE_PATTERN = Pattern.compile("[+-]?\\d+\\.?\\d*");
  public static final Pattern MATCHING_PATTERN =
      Pattern.compile(
          "\\((" + DOUBLE_PATTERN.pattern() + "),(" + DOUBLE_PATTERN.pattern() + ")\\)");

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

  /**
   * Returns the abscissa value from a properly formatted String
   *
   * @param input Properly formatted input string
   * @return The abscissa value
   */
  protected static double getXFromString(String input) {
    Matcher matcher = MATCHING_PATTERN.matcher(input);
    if (!matcher.matches())
      throw new IllegalArgumentException(
          "The given input '"
              + input
              + "' is not a valid representation of a CharacteristicCoordinate.");
    return Double.parseDouble(matcher.group(1));
  }

  /**
   * Returns the ordinate value from a properly formatted String
   *
   * @param input Properly formatted input string
   * @return The ordinate value
   */
  protected static double getYFromString(String input) {
    Matcher matcher = MATCHING_PATTERN.matcher(input);
    if (!matcher.matches())
      throw new IllegalArgumentException(
          "The given input '"
              + input
              + "' is not a valid representation of a CharacteristicCoordinate.");
    return Double.parseDouble(matcher.group(2));
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
  public int compareTo(@NotNull CharacteristicCoordinate<A, O> b) {
    int abscissaCompare = x.compareTo(b.getX());
    return abscissaCompare != 0 ? abscissaCompare : y.compareTo(b.getY());
  }
}
