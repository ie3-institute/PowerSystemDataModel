/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import java.util.Objects;
import javax.measure.Quantity;
import org.jetbrains.annotations.NotNull;
import tec.uom.se.ComparableQuantity;

/** Class to describe one coordinate of a given {@link CharacteristicInput} */
public class CharacteristicCoordinate<A extends Quantity<A>, O extends Quantity<O>>
    implements Comparable<CharacteristicCoordinate<A, O>> {
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
