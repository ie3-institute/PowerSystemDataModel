/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.grid;

import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.connector.ConnectorInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.system.type.SystemParticipantTypeInput;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.measure.Quantity;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Utilities for {@link AssetTypeInput}s and {@link SystemParticipantTypeInput}s. */
public class TypeUtils {
  private TypeUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // methods for filtering types

  /**
   * Method for finding an option for a suitable type. This method uses the given minimum required
   * value to filter find all possible types. After that {@link Stream#findAny()} is used to return
   * an option.
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
   * value to filter find all possible types. After that {@link Stream#findAny()} is used to return
   * an option.
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
   * value to filter find all possible types. After that {@link Stream#findAny()} is used to return
   * an option.
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
                type.getsRatedA().isGreaterThan(sMinA)
                    && type.getsRatedB().isGreaterThan(sMinB)
                    && type.getsRatedC().isGreaterThan(sMinC))
        .findAny();
  }

  /**
   * Method for find all types that fulfills the required voltage rating.
   *
   * @param types all line types
   * @param vRated required voltage rating
   * @return a list of all suitable line types
   */
  public static List<LineTypeInput> findSuitableTypes(
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
  public static List<Transformer2WTypeInput> findSuitableTypes(
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
  public static List<Transformer3WTypeInput> findSuitableTypes(
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

  /**
   * Method for finding an option for a suitable type. This method uses the given minimum required
   * value to filter find all possible types. After that {@link Stream#findAny()} is used to return
   * an option.
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
    return types.stream().filter(type -> extractor.apply(type).isGreaterThan(min)).findAny();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // methods for calculating parallel devices count

  /**
   * Method for calculating the needed amount of {@link ConnectorInput#getParallelDevices()} in
   * order to carry the required minimum current.
   *
   * @param type line type containing a maximum current that can be carried
   * @param iMin minimal required current
   * @return an amount of parallel devices
   */
  public static int calculateNeededParallelDevices(
      LineTypeInput type, ComparableQuantity<ElectricCurrent> iMin) {
    double quotient = iMin.divide(type.getiMax()).getValue().doubleValue();
    return (int) Math.ceil(quotient);
  }

  /**
   * Method for calculating the needed amount of {@link ConnectorInput#getParallelDevices()} in
   * order to carry the required minimum power.
   *
   * @param type transformer type with a power rating
   * @param sMin minimal required power
   * @return an amount of parallel devices
   */
  public static int calculateNeededParallelDevices(
      Transformer2WTypeInput type, ComparableQuantity<Power> sMin) {
    double quotient = sMin.divide(type.getsRated()).getValue().doubleValue();
    return (int) Math.ceil(quotient);
  }

  /**
   * Method for calculating the needed amount of {@link ConnectorInput#getParallelDevices()} in
   * order to carry the required minimum power for all ports.
   *
   * @param type transformer type with a power ratings for all ports
   * @param sMinA minimal required power of port A
   * @param sMinB minimal required power of port B
   * @param sMinC minimal required power of port C
   * @return an amount of parallel devices
   */
  public static int calculateNeededParallelDevices(
      Transformer3WTypeInput type,
      ComparableQuantity<Power> sMinA,
      ComparableQuantity<Power> sMinB,
      ComparableQuantity<Power> sMinC) {
    double quotientA = sMinA.divide(type.getsRatedA()).getValue().doubleValue();
    double quotientB = sMinB.divide(type.getsRatedB()).getValue().doubleValue();
    double quotientC = sMinC.divide(type.getsRatedC()).getValue().doubleValue();

    double min = Math.min(Math.min(quotientA, quotientB), quotientC);
    return (int) Math.ceil(min);
  }
}
