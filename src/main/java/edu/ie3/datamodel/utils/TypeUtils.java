/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils;

import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.measure.Quantity;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Utilities for types. */
public class TypeUtils {
  private TypeUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // methods for filtering types

  /**
   * Method for finding an option for a suitable type. This method uses the given minimum required
   * value to filter find all possible types. After that {@link Stream#findFirst()} is used to
   * return an option.
   *
   * @param types all known types
   * @param iMin minimum required current
   * @return an option for one type or an empty option if no type is found
   */
  public static Optional<LineTypeInput> findSuitableLineType(
      Collection<LineTypeInput> types, ComparableQuantity<ElectricCurrent> iMin) {
    return findSuitableType(types, iMin, LineTypeInput::getiMax);
  }

  /**
   * Method for finding an option for a suitable type. This method uses the given minimum required
   * value to filter find all possible types. After that {@link Stream#findFirst()} is used to
   * return an option.
   *
   * @param types all known types
   * @param sMin minimum required power
   * @return an option for one type or an empty option if no type is found
   */
  public static Optional<Transformer2WTypeInput> findSuitableTransformerType(
      Collection<Transformer2WTypeInput> types, ComparableQuantity<Power> sMin) {
    return findSuitableType(types, sMin, Transformer2WTypeInput::getsRated);
  }

  /**
   * Method for finding an option for a suitable type. This method uses the given minimum required
   * value to filter find all possible types. After that {@link Stream#findFirst()} is used to
   * return an option.
   *
   * @param types all known types
   * @param sMinA minimum power required at port A
   * @param sMinB minimum power required at port B
   * @param sMinC minimum power required at port C
   * @return an option for one type or an empty option if no type is found
   */
  public static Optional<Transformer3WTypeInput> findSuitableTransformerType(
      Collection<Transformer3WTypeInput> types,
      ComparableQuantity<Power> sMinA,
      ComparableQuantity<Power> sMinB,
      ComparableQuantity<Power> sMinC) {
    return types.stream()
        .filter(
            type ->
                type.getsRatedA().isGreaterThanOrEqualTo(sMinA)
                    && type.getsRatedB().isGreaterThanOrEqualTo(sMinB)
                    && type.getsRatedC().isGreaterThanOrEqualTo(sMinC))
        .findFirst();
  }

  /**
   * Method for find all types that fulfills the required voltage rating.
   *
   * @param types all line types
   * @param vRated required voltage rating
   * @return a list of all suitable line types
   */
  public static List<LineTypeInput> findSuitableLineTypes(
      Collection<LineTypeInput> types, ComparableQuantity<ElectricPotential> vRated) {
    return types.stream().filter(type -> type.getvRated().isEquivalentTo(vRated)).toList();
  }

  /**
   * Method for find all types that fulfills the required voltage rating.
   *
   * @param types all two winding transformer types
   * @param vRatedA required voltage rating at port A
   * @param vRatedB required voltage rating at port B
   * @return a list of all suitable two winding types
   */
  public static List<Transformer2WTypeInput> findSuitableTransformerTypes(
      Collection<Transformer2WTypeInput> types,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB) {
    return types.stream()
        .filter(
            type ->
                type.getvRatedA().isEquivalentTo(vRatedA)
                    && type.getvRatedB().isEquivalentTo(vRatedB))
        .toList();
  }

  /**
   * Method for find all types that fulfills the required voltage rating.
   *
   * @param types all three winding transformer types
   * @param vRatedA required voltage rating at port A
   * @param vRatedB required voltage rating at port B
   * @param vRatedC required voltage rating at port C
   * @return a list of all suitable three winding types
   */
  public static List<Transformer3WTypeInput> findSuitableTransformerTypes(
      Collection<Transformer3WTypeInput> types,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      ComparableQuantity<ElectricPotential> vRatedC) {
    return types.stream()
        .filter(
            type ->
                type.getvRatedA().isEquivalentTo(vRatedA)
                    && type.getvRatedB().isEquivalentTo(vRatedB)
                    && type.getvRatedC().isEquivalentTo(vRatedC))
        .toList();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // non public methods

  /**
   * Method for finding an option for a suitable type. This method uses the given minimum required
   * value to filter find all possible types. After that {@link Stream#findFirst()} is used to
   * return an option.
   *
   * @param types all known types
   * @param min minimum required quantity
   * @param extractor for extracting a quantity
   * @return an option for one type or an empty option if no type is found
   * @param <T> type of the types
   * @param <V> type of quantity
   */
  protected static <T extends AssetTypeInput, V extends Quantity<V>> Optional<T> findSuitableType(
      Collection<T> types,
      ComparableQuantity<V> min,
      Function<T, ComparableQuantity<V>> extractor) {
    return types.stream()
        .filter(type -> extractor.apply(type).isGreaterThanOrEqualTo(min))
        .findFirst();
  }
}
