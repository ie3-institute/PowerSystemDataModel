/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.grid;

import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Class for updating assets and asset inputs. */
public class AssetUpdateUtils {
  private AssetUpdateUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // asset enhancing methods

  /**
   * Method for enhancing the given {@link LineInput}.
   *
   * <p>This method works as follows:
   * <li>Tries to retrieve a type by using {@link #enhanceLineType}
   * <li>If no type can be found increasing number of parallel devices
   *
   * @param input given line input
   * @param types all known {@link LineTypeInput}s
   * @return an option for an enhanced line
   */
  public static LineInput enhanceLine(LineInput input, Set<LineTypeInput> types) {
    LineTypeInput current = input.getType();
    return enhanceLineType(types, current.getiMax(), current.getvRated())
        .map(type -> input.copy().type(type).build())
        .orElse(enhanceParallelDevices(input));
  }

  /**
   * Method for enhancing the given {@link Transformer2WInput}.
   *
   * <p>This method works as follows:
   * <li>Tries to retrieve a type by using {@link #enhanceTransformer2WType}
   * <li>If no type can be found increasing number of parallel devices
   *
   * @param input given transformer input
   * @param types all known {@link Transformer2WTypeInput}s
   * @return an option for an enhanced transformer
   */
  public static Transformer2WInput enhanceTransformer2W(
      Transformer2WInput input, Set<Transformer2WTypeInput> types) {
    Transformer2WTypeInput current = input.getType();
    return enhanceTransformer2WType(
            types, current.getsRated(), current.getvRatedA(), current.getvRatedB())
        .map(type -> input.copy().type(type).build())
        .orElse(enhanceParallelDevices(input));
  }

  /**
   * Method for enhancing the given {@link Transformer3WInput}.
   *
   * <p>This method works as follows:
   * <li>Tries to retrieve a type by using {@link #enhanceTransformer3WType}
   * <li>If no type can be found increasing number of parallel devices
   *
   * @param input given transformer input
   * @param types all known {@link Transformer3WTypeInput}s
   * @return an option for an enhanced transformer
   */
  public static Transformer3WInput enhanceTransformer3W(
      Transformer3WInput input, Set<Transformer3WTypeInput> types) {
    Transformer3WTypeInput current = input.getType();
    return enhanceTransformer3WType(
            types,
            current.getsRatedA(),
            current.getsRatedB(),
            current.getsRatedC(),
            current.getvRatedA(),
            current.getvRatedB(),
            current.getvRatedC())
        .map(type -> input.copy().type(type).build())
        .orElse(enhanceParallelDevices(input));
  }

  /**
   * Method for increasing the number of {@link ConnectorInput#getParallelDevices()} by one.
   *
   * <p>NOTE: The number of parallel devices of {@link SwitchInput}s cannot be changed with this
   * method. A given switch is just returned!
   *
   * @param input given line
   * @return line with increased number of parallel devices
   */
  @SuppressWarnings("unchecked")
  public static <T extends ConnectorInput> T enhanceParallelDevices(T input) {
    if (input instanceof SwitchInput) {
      return input;
    } else {
      return (T) input.copy().parallelDevices(input.getParallelDevices() + 1).build();
    }
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // voltage enhancing methods

  /**
   * Method for changing the given {@link VoltageLevel} of a set of {@link NodeInput}s.
   *
   * <p>NOTE: This method will only change the voltage level without any checks.
   *
   * @param nodes given nodes
   * @param newLvl the new voltage level
   * @return a map: old to new nodes
   */
  public static Map<NodeInput, NodeInput> enhanceVoltage(
      Set<NodeInput> nodes, VoltageLevel newLvl) {
    return nodes.parallelStream()
        .map(node -> Map.entry(node, node.copy().voltLvl(newLvl).build()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  /**
   * Method to check if a given set of types contains type for a given voltage rating.
   *
   * @param types set of types
   * @param vRated rated voltage
   * @return true, if at least one type is suitable
   */
  public static boolean checkLineTypes(
      Set<LineTypeInput> types, ComparableQuantity<ElectricPotential> vRated) {
    return !types.stream()
        .filter(type -> type.getvRated().isEquivalentTo(vRated))
        .collect(Collectors.toSet())
        .isEmpty();
  }

  /**
   * Method to check if a given set of types contains type for a given voltage rating.
   *
   * @param types set of types
   * @param vRatedA rated voltage at port A
   * @param vRatedB rated voltage at port B
   * @return true, if at least one type is suitable
   */
  public static boolean checkTransformer2WTypes(
      Set<Transformer2WTypeInput> types,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB) {
    // checks that only types are considered that have the required voltage rating
    Predicate<Transformer2WTypeInput> vRated =
        type ->
            type.getvRatedA().isEquivalentTo(vRatedA) && type.getvRatedB().isEquivalentTo(vRatedB);

    return !types.stream().filter(vRated).collect(Collectors.toSet()).isEmpty();
  }

  /**
   * Method to check if a given set of types contains type for a given voltage rating.
   *
   * @param types set of types
   * @param vRatedA rated voltage at port A
   * @param vRatedB rated voltage at port B
   * @param vRatedC rated voltage at port C
   * @return true, if at least one type is suitable
   */
  public static boolean checkTransformer3WTypes(
      Set<Transformer3WTypeInput> types,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      ComparableQuantity<ElectricPotential> vRatedC) {
    // checks that only types are considered that have the required voltage rating
    Predicate<Transformer3WTypeInput> vRated =
        type ->
            type.getvRatedA().isEquivalentTo(vRatedA)
                && type.getvRatedB().isEquivalentTo(vRatedB)
                && type.getvRatedC().isEquivalentTo(vRatedC);

    return !types.stream().filter(vRated).collect(Collectors.toSet()).isEmpty();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // methods for enhancing types

  /**
   * Method for finding the next possible {@link LineTypeInput}. The new type needs to fulfill the
   * requirement that are given. If no type that fulfills these requirements is found, an {@link
   * Optional#empty()} is returned instead.
   *
   * @param types all known {@link LineTypeInput}s
   * @param iMin the minimum current of the new type
   * @param vRated the required electric potential of the type
   * @return an option of a line type
   */
  public static Optional<LineTypeInput> enhanceLineType(
      Set<LineTypeInput> types,
      ComparableQuantity<ElectricCurrent> iMin,
      ComparableQuantity<ElectricPotential> vRated) {
    return types.stream()
        .filter(
            type -> type.getvRated().isEquivalentTo(vRated) && type.getiMax().isGreaterThan(iMin))
        .min(Comparator.comparingDouble(o -> o.getiMax().getValue().doubleValue()));
  }

  /**
   * Method for finding the next possible {@link Transformer2WTypeInput}. The new type needs to
   * fulfill the requirement that are given. If no type that fulfills these requirements is found,
   * an {@link Optional#empty()} is returned instead.
   *
   * @param types all known {@link Transformer2WTypeInput}s
   * @param sMin the minimum rated power
   * @param vRatedA the required electric potential at the port A
   * @param vRatedB the required electric potential at the port B
   * @return an option of a line type
   */
  public static Optional<Transformer2WTypeInput> enhanceTransformer2WType(
      Set<Transformer2WTypeInput> types,
      ComparableQuantity<Power> sMin,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB) {

    // checks that only types are considered that have the required voltage rating
    Predicate<Transformer2WTypeInput> vRated =
        type ->
            type.getvRatedA().isEquivalentTo(vRatedA) && type.getvRatedB().isEquivalentTo(vRatedB);

    return types.stream()
        .filter(vRated)
        .filter(type -> type.getsRated().isGreaterThan(sMin))
        .min(Comparator.comparingDouble(o -> o.getsRated().getValue().doubleValue()));
  }

  /**
   * Method for finding the next possible {@link Transformer3WTypeInput}. The new type needs to
   * fulfill the requirement that are given. If no type that fulfills these requirements is found,
   * an {@link Optional#empty()} is returned instead.
   *
   * @param types all known {@link Transformer3WTypeInput}s
   * @param sMinA the minimum rated power at the port A
   * @param sMinB the minimum rated power at the port B
   * @param sMinC the minimum rated power at the port C
   * @param vRatedA the required electric potential at the port A
   * @param vRatedB the required electric potential at the port B
   * @param vRatedC the required electric potential at the port C
   * @return an option of a line type
   */
  public static Optional<Transformer3WTypeInput> enhanceTransformer3WType(
      Set<Transformer3WTypeInput> types,
      ComparableQuantity<Power> sMinA,
      ComparableQuantity<Power> sMinB,
      ComparableQuantity<Power> sMinC,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      ComparableQuantity<ElectricPotential> vRatedC) {

    // checks that only types are considered that have the required voltage rating
    Predicate<Transformer3WTypeInput> vRated =
        type ->
            type.getvRatedA().isEquivalentTo(vRatedA)
                && type.getvRatedB().isEquivalentTo(vRatedB)
                && type.getvRatedC().isEquivalentTo(vRatedC);

    // checks that only types are considered that have the required power rating
    Predicate<Transformer3WTypeInput> sRated =
        type ->
            type.getsRatedA().isGreaterThan(sMinA)
                && type.getsRatedB().isGreaterThan(sMinB)
                && type.getsRatedC().isGreaterThan(sMinC);

    return types.stream()
        .filter(vRated)
        .filter(sRated)
        .min(Comparator.comparingDouble(o -> o.getsRatedA().getValue().doubleValue()));
  }
}
