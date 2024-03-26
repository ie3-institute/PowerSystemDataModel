/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.update;

import edu.ie3.datamodel.exceptions.MissingTypeException;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.ConnectorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.voltagelevels.VoltageLevel;
import edu.ie3.datamodel.utils.TypeUtils;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.Power;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.units.indriya.ComparableQuantity;

/** Utilities for updating {@link AssetInput}s. */
public class AssetUpdateUtils {
  private static final Logger log = LoggerFactory.getLogger(AssetUpdateUtils.class);

  private AssetUpdateUtils() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  public static final Function<NodeInput, ComparableQuantity<ElectricPotential>> rating =
      node -> node.getVoltLvl().getNominalVoltage();

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // methods for updating nodes

  /**
   * Method for changing the given {@link VoltageLevel} of a set of {@link NodeInput}s.
   *
   * <p>NOTE: This method will only change the voltage level without any checks.
   *
   * @param nodes given nodes
   * @param newLvl the new voltage level
   * @return a map: old to new nodes
   */
  public static Map<NodeInput, NodeInput> updateNodes(Set<NodeInput> nodes, VoltageLevel newLvl) {
    return nodes.parallelStream()
        .map(node -> Map.entry(node, node.copy().voltLvl(newLvl).build()))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // methods for updating connectors

  /**
   * Method for updating the rated current of the given {@link LineInput}. For changing the current
   * and voltage rating use {@link #updateLine(LineInput, ComparableQuantity, ComparableQuantity,
   * Collection)} instead.
   *
   * @param line given line
   * @param iMin minimum rated current
   * @param types all known line types
   * @return an updated line
   * @throws MissingTypeException if not suitable line type was found
   */
  public static LineInput updateLineCurrent(
      LineInput line, ComparableQuantity<ElectricCurrent> iMin, Collection<LineTypeInput> types)
      throws MissingTypeException {
    return updateLine(line, iMin, line.getNodeB().getVoltLvl().getNominalVoltage(), types);
  }

  /**
   * Method for updating the rated voltage of the given {@link LineInput}. For changing the current
   * and voltage rating use {@link #updateLine(LineInput, ComparableQuantity, ComparableQuantity,
   * Collection)} instead.
   *
   * @param line given line
   * @param vRated rated voltage
   * @param types all known line types
   * @return an updated line
   * @throws MissingTypeException if not suitable line type was found
   */
  public static LineInput updateLineVoltage(
      LineInput line, ComparableQuantity<ElectricPotential> vRated, Collection<LineTypeInput> types)
      throws MissingTypeException {
    return updateLine(line, line.getType().getiMax(), vRated, types);
  }

  /**
   * Method for updating the current and voltage rating of the given {@link LineInput}.
   *
   * <p>NOTE: This method automatically increase the number of {@link
   * ConnectorInput#getParallelDevices()} in order to fulfill the given current requirement.
   *
   * @param line given line
   * @param iMin minimum required current
   * @param vRated rated voltage
   * @param types all known line types
   * @return an updated line
   * @throws MissingTypeException if not suitable line type was found
   */
  public static LineInput updateLine(
      LineInput line,
      ComparableQuantity<ElectricCurrent> iMin,
      ComparableQuantity<ElectricPotential> vRated,
      Collection<LineTypeInput> types)
      throws MissingTypeException {
    List<LineTypeInput> suitableTypes = TypeUtils.findSuitableLineTypes(types, vRated);

    if (suitableTypes.isEmpty()) {
      // throws an exception if no line type is suitable for the required voltage rating
      throw new MissingTypeException("No suitable line type found for rating: " + vRated);
    }

    return TypeUtils.findSuitableLineType(suitableTypes, iMin)
        .map(suitableType -> line.copy().type(suitableType).build())
        .orElseGet(
            () -> {
              LineTypeInput type =
                  Collections.max(
                      suitableTypes,
                      Comparator.comparingDouble(o -> o.getiMax().getValue().doubleValue()));
              int parallelDevices = calculateNeededParallelDevices(type, iMin);

              if (line.getParallelDevices() != parallelDevices) {
                log.debug(
                    "Increased the number of parallel devices of line '{}' from {} to {} in order to carry a current of '{}'.",
                    line.getUuid(),
                    line.getParallelDevices(),
                    parallelDevices,
                    iMin);
              }

              return line.copy().type(type).parallelDevices(parallelDevices).build();
            });
  }

  /**
   * Method for updating rated power of the given {@link Transformer2WInput}.For changing the power
   * and voltage rating use {@link #updateTransformer(Transformer2WInput, ComparableQuantity,
   * ComparableQuantity, ComparableQuantity, Collection)} instead.
   *
   * <p>NOTE: This method automatically increase the number of {@link
   * ConnectorInput#getParallelDevices()} in order to fulfill the given power requirement.
   *
   * @param transformer given two winding transformer
   * @param sMin minimum rated power
   * @param types all known two winding transformer types
   * @return an updated transformer
   * @throws MissingTypeException if not suitable transformer type was found
   */
  public static Transformer2WInput updateTransformerPower(
      Transformer2WInput transformer,
      ComparableQuantity<Power> sMin,
      Collection<Transformer2WTypeInput> types)
      throws MissingTypeException {
    return updateTransformer(
        transformer,
        sMin,
        rating.apply(transformer.getNodeA()),
        rating.apply(transformer.getNodeB()),
        types);
  }

  /**
   * Method for updating rated voltage of the given {@link Transformer2WInput}.For changing the
   * power and voltage rating use {@link #updateTransformer(Transformer2WInput, ComparableQuantity,
   * ComparableQuantity, ComparableQuantity, Collection)} instead.
   *
   * <p>NOTE: This method automatically increase the number of {@link
   * ConnectorInput#getParallelDevices()} in order to fulfill the given power requirement.
   *
   * @param transformer given three winding transformer
   * @param vRatedA rated voltage at port A
   * @param vRatedB rated voltage at port B
   * @param types all known three winding transformer types
   * @return an updated transformer
   * @throws MissingTypeException if not suitable transformer type was found
   */
  public static Transformer2WInput updateTransformerVoltage(
      Transformer2WInput transformer,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      Collection<Transformer2WTypeInput> types)
      throws MissingTypeException {
    return updateTransformer(
        transformer, transformer.getType().getsRated(), vRatedA, vRatedB, types);
  }

  /**
   * Method for updating the power and voltage rating of the given {@link Transformer2WInput}.
   *
   * <p>NOTE: This method automatically increase the number of {@link
   * ConnectorInput#getParallelDevices()} in order to fulfill the given power requirement.
   *
   * @param transformer given two winding transformer
   * @param sMin minimum required power
   * @param vRatedA rated voltage at port A
   * @param vRatedB rated voltage at port B
   * @param types all known two winding transformer types
   * @return an updated transformer
   * @throws MissingTypeException if not suitable transformer type was found
   */
  public static Transformer2WInput updateTransformer(
      Transformer2WInput transformer,
      ComparableQuantity<Power> sMin,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      Collection<Transformer2WTypeInput> types)
      throws MissingTypeException {
    List<Transformer2WTypeInput> suitableTypes =
        TypeUtils.findSuitableTransformerTypes(types, vRatedA, vRatedB);

    if (suitableTypes.isEmpty()) {
      // throws an exception if no transformer type is suitable for the required voltage ratings
      throw new MissingTypeException(
          "No suitable two winding transformer type found for rating: "
              + vRatedA
              + " -> "
              + vRatedB);
    }

    return TypeUtils.findSuitableTransformerType(suitableTypes, sMin)
        .map(suitableType -> transformer.copy().type(suitableType).build())
        .orElseGet(
            () -> {
              Transformer2WTypeInput type =
                  Collections.max(
                      suitableTypes,
                      Comparator.comparingDouble(o -> o.getsRated().getValue().doubleValue()));
              int parallelDevices = calculateNeededParallelDevices(type, sMin);

              if (transformer.getParallelDevices() != parallelDevices) {
                log.debug(
                    "Increased the number of parallel devices of two winding transformer '{}' from {} to {} in order to carry a power of '{}'.",
                    transformer.getUuid(),
                    transformer.getParallelDevices(),
                    parallelDevices,
                    sMin);
              }

              return transformer.copy().type(type).parallelDevices(parallelDevices).build();
            });
  }

  /**
   * Method for updating rated power of the given {@link Transformer3WInput}.For changing the power
   * and voltage rating use {@link #updateTransformer(Transformer3WInput, ComparableQuantity,
   * ComparableQuantity, ComparableQuantity, ComparableQuantity, ComparableQuantity,
   * ComparableQuantity, Collection)} instead.
   *
   * <p>NOTE: This method automatically increase the number of {@link
   * ConnectorInput#getParallelDevices()} in order to fulfill the given power requirement.
   *
   * @param transformer given three winding transformer
   * @param sMinA minimum required power at port A
   * @param sMinB minimum required power at port B
   * @param sMinC minimum required power at port C
   * @param types all known three winding transformer types
   * @return an updated transformer
   * @throws MissingTypeException if not suitable transformer type was found
   */
  public static Transformer3WInput updateTransformerPower(
      Transformer3WInput transformer,
      ComparableQuantity<Power> sMinA,
      ComparableQuantity<Power> sMinB,
      ComparableQuantity<Power> sMinC,
      Collection<Transformer3WTypeInput> types)
      throws MissingTypeException {
    return updateTransformer(
        transformer,
        sMinA,
        sMinB,
        sMinC,
        rating.apply(transformer.getNodeA()),
        rating.apply(transformer.getNodeB()),
        rating.apply(transformer.getNodeC()),
        types);
  }

  /**
   * Method for updating rated voltage of the given {@link Transformer3WInput}.For changing the
   * power and voltage rating use {@link #updateTransformer(Transformer3WInput, ComparableQuantity,
   * ComparableQuantity, ComparableQuantity, ComparableQuantity, ComparableQuantity,
   * ComparableQuantity, Collection)} instead.
   *
   * <p>NOTE: This method automatically increase the number of {@link
   * ConnectorInput#getParallelDevices()} in order to fulfill the given power requirement.
   *
   * @param transformer given three winding transformer
   * @param vRatedA rated voltage at port A
   * @param vRatedB rated voltage at port B
   * @param vRatedC rated voltage at port C
   * @param types all known three winding transformer types
   * @return an updated transformer
   * @throws MissingTypeException if not suitable transformer type was found
   */
  public static Transformer3WInput updateTransformerVoltage(
      Transformer3WInput transformer,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      ComparableQuantity<ElectricPotential> vRatedC,
      Collection<Transformer3WTypeInput> types)
      throws MissingTypeException {
    Transformer3WTypeInput current = transformer.getType();

    return updateTransformer(
        transformer,
        current.getsRatedA(),
        current.getsRatedB(),
        current.getsRatedC(),
        vRatedA,
        vRatedB,
        vRatedC,
        types);
  }

  /**
   * Method for updating the power and voltage rating of the given {@link Transformer3WInput}.
   *
   * <p>NOTE: This method automatically increase the number of {@link
   * ConnectorInput#getParallelDevices()} in order to fulfill the given power requirement.
   *
   * @param transformer given three winding transformer
   * @param sMinA minimum required power at port A
   * @param sMinB minimum required power at port B
   * @param sMinC minimum required power at port C
   * @param vRatedA rated voltage at port A
   * @param vRatedB rated voltage at port B
   * @param vRatedC rated voltage at port C
   * @param types all known three winding transformer types
   * @return an updated transformer
   * @throws MissingTypeException if not suitable transformer type was found
   */
  public static Transformer3WInput updateTransformer(
      Transformer3WInput transformer,
      ComparableQuantity<Power> sMinA,
      ComparableQuantity<Power> sMinB,
      ComparableQuantity<Power> sMinC,
      ComparableQuantity<ElectricPotential> vRatedA,
      ComparableQuantity<ElectricPotential> vRatedB,
      ComparableQuantity<ElectricPotential> vRatedC,
      Collection<Transformer3WTypeInput> types)
      throws MissingTypeException {
    List<Transformer3WTypeInput> suitableTypes =
        TypeUtils.findSuitableTransformerTypes(types, vRatedA, vRatedB, vRatedC);

    if (suitableTypes.isEmpty()) {
      // throws an exception if no transformer type is suitable for the required voltage ratings
      throw new MissingTypeException(
          "No suitable three winding transformer type found for rating: "
              + vRatedA
              + " -> "
              + vRatedB
              + " -> "
              + vRatedC);
    }

    return TypeUtils.findSuitableTransformerType(suitableTypes, sMinA, sMinB, sMinC)
        .map(suitableType -> transformer.copy().type(suitableType).build())
        .orElseGet(
            () -> {
              Transformer3WTypeInput type =
                  Collections.max(
                      suitableTypes,
                      Comparator.comparingDouble(o -> o.getsRatedA().getValue().doubleValue()));
              int parallelDevices = calculateNeededParallelDevices(type, sMinA, sMinB, sMinC);

              if (transformer.getParallelDevices() != parallelDevices) {
                log.debug(
                    "Increased the number of parallel devices of three winding transformer '{}' from {} to {} in order to carry a power of '{}, {}, {}'.",
                    transformer.getUuid(),
                    transformer.getParallelDevices(),
                    parallelDevices,
                    sMinA,
                    sMinB,
                    sMinC);
              }

              return transformer.copy().type(type).parallelDevices(parallelDevices).build();
            });
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

    double min = Math.max(Math.max(quotientA, quotientB), quotientC);
    return (int) Math.ceil(min);
  }
}
