/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.*;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.GridContainer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.measure.Quantity;

/** Basic Sanity validation tools for entities */
public class ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  protected ValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * This is a "distribution" method, that forwards the check request to specific implementations to
   * fulfill the checking task, based on the class of the given object. If an not yet know class is
   * handed in, a {@link ValidationException} is thrown.
   *
   * TODO @ Niklas: Fill with other method calls, as collection of validation utils increases
   *
   * @param obj Object to check
   */
  public static void check(Object obj) {
    if (GridContainer.class.isAssignableFrom(obj.getClass())) {
      GridContainerValidationUtils.check((GridContainer) obj);
    } else if (NodeInput.class.isAssignableFrom(obj.getClass())) {
      NodeValidationUtils.check((NodeInput) obj);
    } else {
      throw new ValidationException(
          "Cannot validate object of class '"
              + obj.getClass().getSimpleName()
              + "', as no routine is implemented.");
    }
  }

  /**
   * Checks, if the given object is null. If so, an {@link InvalidEntityException} is thrown.
   *
   * @param obj Object to check
   * @param expectedDescription Further description, of what has been expected.
   */
  protected static void checkNonNull(Object obj, String expectedDescription) {
    if (obj == null)
      throw new ValidationException(
          "Expected " + expectedDescription + ", but got nothing. :-(", new NullPointerException());
  }

  /**
   * Validates a connector if: <br>
   * - it is not null <br>
   * - both of its nodes are not null
   *
   * @param connector Connector to validate
   */
  public static void checkConnector(ConnectorInput connector) {
    checkNonNull(connector, "a connector");
    if (connector.getNodeA() == null || connector.getNodeB() == null)
      throw new InvalidEntityException("at least one node of this connector is null ", connector);
  }

  /**
   * Validates a line if: <br>
   * - it is not null <br>
   * - line type is not null <br>
   * - {@link ValidationUtils#checkLineType(LineTypeInput)} and {@link
   * ValidationUtils#checkConnector(ConnectorInput)} confirm a valid type and valid connector
   * properties
   *
   * @param line Line to validate
   */
  public static void checkLine(LineInput line) {
    checkNonNull(line, "a line");
    checkConnector(line);
    checkLineType(line.getType());
    if (line.getNodeA().getSubnet() != line.getNodeB().getSubnet())
      throw new InvalidEntityException("the line {} connects to different subnets", line);
    if (line.getNodeA().getVoltLvl() != line.getNodeB().getVoltLvl())
      throw new InvalidEntityException("the line {} connects to different voltage levels", line);
  }

  /**
   * Validates a line type if: <br>
   * - it is not null <br>
   * - none of its values are null or 0 <br>
   *
   * @param lineType Line type to validate
   */
  public static void checkLineType(LineTypeInput lineType) {
    checkNonNull(lineType, "a line type");

    detectNegativeQuantities(new Quantity<?>[] {lineType.getB(), lineType.getG()}, lineType);
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {
          lineType.getvRated(), lineType.getiMax(), lineType.getX(), lineType.getR()
        },
        lineType);
  }

  /**
   * Validates a transformer if: <br>
   * - it is not null <br>
   * - transformer type is not null <br>
   * - {@link ValidationUtils#checkTransformer2WType(Transformer2WTypeInput)} and {@link
   * ValidationUtils#checkConnector(ConnectorInput)} confirm a valid type and valid connector
   * properties
   *
   * @param trafo Transformer to validate
   */
  public static void checkTransformer2W(Transformer2WInput trafo) {
    checkNonNull(trafo, "a two winding transformer");
    checkConnector(trafo);
    checkTransformer2WType(trafo.getType());
  }

  /**
   * Validates a transformer type if: <br>
   * - it is not null <br>
   * - none of its values are null or 0 <br>
   *
   * @param trafoType Transformer type to validate
   */
  public static void checkTransformer2WType(Transformer2WTypeInput trafoType) {
    checkNonNull(trafoType, "a two winding transformer type");
    if ((trafoType.getsRated() == null)
        || (trafoType.getvRatedA() == null)
        || (trafoType.getvRatedB() == null)
        || (trafoType.getrSc() == null)
        || (trafoType.getxSc() == null)
        || (trafoType.getgM() == null)
        || (trafoType.getbM() == null)
        || (trafoType.getdV() == null)
        || (trafoType.getdPhi() == null))
      throw new InvalidEntityException("at least one value of trafo2w type is null", trafoType);

    detectNegativeQuantities(
        new Quantity<?>[] {trafoType.getgM(), trafoType.getbM(), trafoType.getdPhi()}, trafoType);
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {
          trafoType.getsRated(),
          trafoType.getvRatedA(),
          trafoType.getvRatedB(),
          trafoType.getxSc(),
          trafoType.getdV()
        },
        trafoType);
  }

  /**
   * Validates a transformer if: <br>
   * - it is not null <br>
   * - transformer type is not null <br>
   * - {@link ValidationUtils#checkTransformer3WType(Transformer3WTypeInput)} and {@link
   * ValidationUtils#checkConnector(ConnectorInput)} confirm a valid type and valid connector
   * properties
   *
   * @param trafo Transformer to validate
   */
  public static void checkTransformer3W(Transformer3WInput trafo) {
    checkNonNull(trafo, "a three winding transformer");
    checkConnector(trafo);
    if (trafo.getNodeC() == null)
      throw new InvalidEntityException("at least one node of this connector is null", trafo);
    checkTransformer3WType(trafo.getType());
  }

  /**
   * Validates a transformer type if: <br>
   * - it is not null <br>
   * - none of its values are null or 0 <br>
   *
   * @param trafoType Transformer type to validate
   */
  public static void checkTransformer3WType(Transformer3WTypeInput trafoType) {
    checkNonNull(trafoType, "a three winding transformer type");
    if ((trafoType.getsRatedA() == null)
        || (trafoType.getsRatedB() == null)
        || (trafoType.getsRatedC() == null)
        || (trafoType.getvRatedA() == null)
        || (trafoType.getvRatedB() == null)
        || (trafoType.getvRatedC() == null)
        || (trafoType.getrScA() == null)
        || (trafoType.getrScB() == null)
        || (trafoType.getrScC() == null)
        || (trafoType.getxScA() == null)
        || (trafoType.getxScB() == null)
        || (trafoType.getxScC() == null)
        || (trafoType.getgM() == null)
        || (trafoType.getbM() == null)
        || (trafoType.getdV() == null)
        || (trafoType.getdPhi() == null))
      throw new InvalidEntityException("at least one value of trafo3w type is null", trafoType);

    detectNegativeQuantities(
        new Quantity<?>[] {trafoType.getgM(), trafoType.getbM(), trafoType.getdPhi()}, trafoType);
    detectZeroOrNegativeQuantities(
        new Quantity<?>[] {
          trafoType.getsRatedA(), trafoType.getsRatedB(), trafoType.getsRatedC(),
          trafoType.getvRatedA(), trafoType.getvRatedB(), trafoType.getvRatedC(),
          trafoType.getxScA(), trafoType.getxScB(), trafoType.getxScC(),
          trafoType.getdV()
        },
        trafoType);
  }

  /**
   * Validates a measurement unit if: <br>
   * - it is not null <br>
   * - its node is not nul
   *
   * @param measurementUnit Measurement unit to validate
   */
  public static void checkMeasurementUnit(MeasurementUnitInput measurementUnit) {
    checkNonNull(measurementUnit, "a measurement unit");
    if (measurementUnit.getNode() == null)
      throw new InvalidEntityException("node is null", measurementUnit);
  }

  /**
   * Validates a measurement unit if: <br>
   * - it is not null <br>
   * - its node is not nul
   *
   * @param switchInput Switch to validate
   */
  public static void checkSwitch(SwitchInput switchInput) {
    checkNonNull(switchInput, "a switch");
    checkConnector(switchInput);
    if (switchInput.getNodeA().getVoltLvl() != switchInput.getNodeB().getVoltLvl())
      throw new InvalidEntityException(
          "the switch {} connects to different voltage levels", switchInput);
    /* Remark: Connecting two different "subnets" is fine, because as of our definition regarding a switchgear in
     * "upstream" direction of a transformer, all the nodes, that hare within the switch chain, belong to the lower
     * grid, whilst the "real" upper node is within the upper grid */
  }

  /**
   * Goes through the provided quantities and reports those, that have negative value via synoptic
   * {@link UnsafeEntityException}
   *
   * @param quantities Array of quantities to check
   * @param entity Unique entity holding the malformed quantities
   */
  private static void detectNegativeQuantities(Quantity<?>[] quantities, UniqueEntity entity) {
    Predicate<Quantity<?>> predicate = quantity -> quantity.getValue().doubleValue() < 0;
    detectMalformedQuantities(
        quantities, entity, predicate, "The following quantities have to be zero or positive");
  }

  /**
   * Goes through the provided quantities and reports those, that are zero or have negative value
   * via synoptic {@link UnsafeEntityException}
   *
   * @param quantities Array of quantities to check
   * @param entity Unique entity holding the malformed quantities
   */
  private static void detectZeroOrNegativeQuantities(
      Quantity<?>[] quantities, UniqueEntity entity) {
    Predicate<Quantity<?>> predicate = quantity -> quantity.getValue().doubleValue() <= 0;
    detectMalformedQuantities(
        quantities, entity, predicate, "The following quantities have to be positive");
  }

  /**
   * Goes through the provided quantities and reports those, that do fulfill the given predicate via
   * synoptic {@link UnsafeEntityException}
   *
   * @param quantities Array of quantities to check
   * @param entity Unique entity holding the malformed quantities
   * @param predicate Predicate to detect the malformed quantities
   * @param msg Message prefix to use for the exception message: [msg]: [malformedQuantities]
   */
  private static void detectMalformedQuantities(
      Quantity<?>[] quantities, UniqueEntity entity, Predicate<Quantity<?>> predicate, String msg) {
    String malformedQuantities =
        Arrays.stream(quantities)
            .filter(predicate)
            .map(Quantity::toString)
            .collect(Collectors.joining(", "));
    if (!malformedQuantities.isEmpty()) {
      throw new UnsafeEntityException(msg + ": " + malformedQuantities, entity);
    }
  }

  /**
   * Determines if the provided set only contains elements with distinct UUIDs
   *
   * @param entities the set that should be checked
   * @return true if all UUIDs of the provided entities are unique, false otherwise
   */
  public static boolean distinctUuids(Set<? extends UniqueEntity> entities) {
    return entities.stream()
            .filter(distinctByKey(UniqueEntity::getUuid))
            .collect(Collectors.toSet())
            .size()
        == entities.size();
  }

  /**
   * Predicate that can be used to filter elements based on a given Function
   *
   * @param keyExtractor the function that should be used for the filter operations
   * @param <T> the type of the returning predicate
   * @return the filter predicate that filters based on the provided function
   */
  public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(keyExtractor.apply(t));
  }

  /**
   * Checks if the provided set of unique entities only contains elements with distinct UUIDs and
   * either returns a string with duplicated UUIDs or an empty optional otherwise.
   *
   * @param entities the entities that should be checkd for UUID uniqueness
   * @return either a string wrapped in an optional with duplicate UUIDs or an empty optional
   */
  public static Optional<String> checkForDuplicateUuids(Set<UniqueEntity> entities) {
    if (distinctUuids(entities)) {
      return Optional.empty();
    }
    String duplicationsString =
        entities.stream()
            .collect(Collectors.groupingBy(UniqueEntity::getUuid, Collectors.counting()))
            .entrySet()
            .stream()
            .filter(entry -> entry.getValue() > 1)
            .map(
                entry -> {
                  String duplicateEntitiesString =
                      entities.stream()
                          .filter(entity -> entity.getUuid().equals(entry.getKey()))
                          .map(UniqueEntity::toString)
                          .collect(Collectors.joining("\n - "));

                  return entry.getKey()
                      + ": "
                      + entry.getValue()
                      + "\n - "
                      + duplicateEntitiesString;
                })
            .collect(Collectors.joining("\n\n"));

    return Optional.of(duplicationsString);
  }
}
