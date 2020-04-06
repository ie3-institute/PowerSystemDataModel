/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system.characteristic;

import edu.ie3.datamodel.models.input.InputEntity;
import java.util.Objects;
import java.util.SortedSet;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.measure.Quantity;

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
    this.coordinates = coordinates;
    this.prefix = prefix;
    this.decimalPlaces = decimalPlaces;
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
