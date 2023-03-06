/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.FailedValidationException;
import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.ConnectorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.container.*;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.utils.ContainerUtils;
import edu.ie3.datamodel.utils.ExceptionUtils;
import edu.ie3.datamodel.utils.options.Failure;
import edu.ie3.datamodel.utils.options.Success;
import edu.ie3.datamodel.utils.options.Try;
import java.util.*;
import java.util.stream.Stream;

public class GridContainerValidationUtils extends ValidationUtils {

  private static String duplicateUuidsString(String simpleName, Optional<String> exceptionString) {
    return "The provided entities in '"
        + simpleName
        + "' contains duplicate UUIDs. "
        + "This is not allowed!\nDuplicated uuids:\n\n"
        + exceptionString;
  }

  /** Private Constructor as this class is not meant to be instantiated */
  private GridContainerValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Checks a complete grid data container
   *
   * @param gridContainer Grid model to check
   */
  protected static void check(GridContainer gridContainer) {
    checkNonNull(gridContainer, "grid container");

    /* sanity check to ensure distinct UUIDs */
    Optional<String> exceptionString =
        checkForDuplicateUuids(new HashSet<>(gridContainer.allEntitiesAsList()));
    if (exceptionString.isPresent()) {
      throw new InvalidGridException(
          duplicateUuidsString(gridContainer.getClass().getSimpleName(), exceptionString));
    }

    Try<Void, FailedValidationException> rawGridElements =
        checkRawGridElements(gridContainer.getRawGrid());

    Try<Void, FailedValidationException> systemParticipants =
        checkSystemParticipants(
            gridContainer.getSystemParticipants(), gridContainer.getRawGrid().getNodes());
    Try<Void, FailedValidationException> graphicElements =
        checkGraphicElements(
            gridContainer.getGraphics(),
            gridContainer.getRawGrid().getNodes(),
            gridContainer.getRawGrid().getLines());
  }

  /**
   * Checks the validity of given {@link RawGridElements}. The single elements are checked as well
   * as the fact, that none of the assets is connected to a node, that is not in the set of nodes.
   *
   * @param rawGridElements Raw grid elements
   * @throws InvalidGridException If something is wrong
   */
  protected static Try<Void, FailedValidationException> checkRawGridElements(
      RawGridElements rawGridElements) {
    try {
      checkNonNull(rawGridElements, "raw grid elements");
    } catch (ValidationException e) {
      return new Failure<>(
          new FailedValidationException(
              "Validation not possible because received object {" + rawGridElements + "} was null",
              e));
    }

    List<ValidationException> exceptions = new ArrayList<>();

    /* sanity check to ensure distinct UUIDs */
    Optional<String> exceptionString =
        checkForDuplicateUuids(new HashSet<>(rawGridElements.allEntitiesAsList()));
    if (exceptionString.isPresent()) {
      exceptions.add(
          new InvalidGridException(
              duplicateUuidsString(rawGridElements.getClass().getSimpleName(), exceptionString)));
    }

    /* Checking nodes */
    Set<NodeInput> nodes = rawGridElements.getNodes();
    nodes.forEach(NodeValidationUtils::check);

    /* Checking lines */
    exceptions.addAll(
        rawGridElements.getLines().stream()
            .map(
                line -> {
                  try {
                    checkNodeAvailability(line, nodes);
                  } catch (ValidationException e) {
                    return new Failure<>(e);
                  }
                  return ConnectorValidationUtils.check(line);
                })
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList());

    /* Checking two winding transformers */
    exceptions.addAll(
        rawGridElements.getTransformer2Ws().stream()
            .map(
                transformer -> {
                  try {
                    checkNodeAvailability(transformer, nodes);
                  } catch (InvalidGridException e) {
                    return new Failure<>(e);
                  }
                  return ConnectorValidationUtils.check(transformer);
                })
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList());

    /* Checking three winding transformers */
    exceptions.addAll(
        rawGridElements.getTransformer3Ws().stream()
            .map(
                transformer -> {
                  try {
                    checkNodeAvailability(transformer, nodes);
                  } catch (ValidationException e) {
                    return new Failure<>(e);
                  }
                  return ConnectorValidationUtils.check(transformer);
                })
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList());

    /* Checking switches
     * Because of the fact, that a transformer with switch gear in "upstream" direction has it's corresponding node in
     * upper grid connected to a switch, instead of to the transformer directly: Collect all nodes at the end of the
     * upstream switch chain and add them to the set of allowed nodes */
    HashSet<NodeInput> validSwitchNodes = new HashSet<>(nodes);
    validSwitchNodes.addAll(
        Stream.of(rawGridElements.getTransformer2Ws(), rawGridElements.getTransformer2Ws())
            .flatMap(Set::stream)
            .parallel()
            .map(
                transformer ->
                    ContainerUtils.traverseAlongSwitchChain(transformer.getNodeA(), rawGridElements)
                        .getLast())
            .toList());

    exceptions.addAll(
        rawGridElements.getSwitches().stream()
            .map(
                switcher -> {
                  try {
                    checkNodeAvailability(switcher, validSwitchNodes);
                  } catch (ValidationException e) {
                    return new Failure<>(e);
                  }
                  return ConnectorValidationUtils.check(switcher);
                })
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList());

    /* Checking measurement units */
    exceptions.addAll(
        rawGridElements.getMeasurementUnits().stream()
            .map(
                measurement -> {
                  try {
                    checkNodeAvailability(measurement, nodes);
                  } catch (ValidationException e) {
                    return new Failure<>(e);
                  }
                  return MeasurementUnitValidationUtils.check(measurement);
                })
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList());

    if (exceptions.size() > 0) {
      return new Failure<>(
          new FailedValidationException(
              "Validation failed due to the following exception(s): ",
              new Throwable(ExceptionUtils.getMessages(exceptions))));
    } else {
      return Success.empty();
    }
  }

  /**
   * Checks the validity of each and every system participant. Moreover, it checks, if the systems
   * are connected to a node that is not in the provided set
   *
   * @param systemParticipants The system participants
   * @param nodes Set of already known nodes
   */
  protected static Try<Void, FailedValidationException> checkSystemParticipants(
      SystemParticipants systemParticipants, Set<NodeInput> nodes) {
    checkNonNull(systemParticipants, "system participants");

    List<ValidationException> exceptions = new ArrayList<>();

    // sanity check for distinct uuids
    Optional<String> exceptionString =
        ValidationUtils.checkForDuplicateUuids(
            new HashSet<>(systemParticipants.allEntitiesAsList()));
    if (exceptionString.isPresent()) {
      exceptions.add(
          new InvalidGridException(
              duplicateUuidsString(
                  systemParticipants.getClass().getSimpleName(), exceptionString)));
    }

    exceptions.addAll(
        systemParticipants.getBmPlants().stream()
            .map(
                entity -> {
                  try {
                    checkNodeAvailability(entity, nodes);
                  } catch (ValidationException e) {
                    return new Failure<>(e);
                  }
                  return SystemParticipantValidationUtils.check(entity);
                })
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList());

    exceptions.addAll(
        systemParticipants.getChpPlants().stream()
            .map(
                entity -> {
                  try {
                    checkNodeAvailability(entity, nodes);
                  } catch (ValidationException e) {
                    return new Failure<>(e);
                  }
                  return SystemParticipantValidationUtils.check(entity);
                })
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList());

    /* TODO: Electric vehicle charging systems are currently only dummy implementation. if this has changed, the whole
     *   method can be aggregated */

    exceptions.addAll(
        systemParticipants.getFixedFeedIns().stream()
            .map(
                entity -> {
                  try {
                    checkNodeAvailability(entity, nodes);
                  } catch (ValidationException e) {
                    return new Failure<>(e);
                  }
                  return SystemParticipantValidationUtils.check(entity);
                })
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList());

    exceptions.addAll(
        systemParticipants.getHeatPumps().stream()
            .map(
                entity -> {
                  try {
                    checkNodeAvailability(entity, nodes);
                  } catch (ValidationException e) {
                    return new Failure<>(e);
                  }
                  return SystemParticipantValidationUtils.check(entity);
                })
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList());

    exceptions.addAll(
        systemParticipants.getLoads().stream()
            .map(
                entity -> {
                  try {
                    checkNodeAvailability(entity, nodes);
                  } catch (ValidationException e) {
                    return new Failure<>(e);
                  }
                  return SystemParticipantValidationUtils.check(entity);
                })
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList());

    exceptions.addAll(
        systemParticipants.getPvPlants().stream()
            .map(
                entity -> {
                  try {
                    checkNodeAvailability(entity, nodes);
                  } catch (ValidationException e) {
                    return new Failure<>(e);
                  }
                  return SystemParticipantValidationUtils.check(entity);
                })
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList());

    exceptions.addAll(
        systemParticipants.getStorages().stream()
            .map(
                entity -> {
                  try {
                    checkNodeAvailability(entity, nodes);
                  } catch (ValidationException e) {
                    return new Failure<>(e);
                  }
                  return SystemParticipantValidationUtils.check(entity);
                })
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList());

    exceptions.addAll(
        systemParticipants.getWecPlants().stream()
            .map(
                entity -> {
                  try {
                    checkNodeAvailability(entity, nodes);
                  } catch (ValidationException e) {
                    return new Failure<>(e);
                  }
                  return SystemParticipantValidationUtils.check(entity);
                })
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList());

    if (exceptions.size() > 0) {
      return new Failure<>(
          new FailedValidationException(
              "Validation failed due to the following exception(s): ",
              new Throwable(ExceptionUtils.getMessages(exceptions))));
    } else {
      return Success.empty();
    }
  }

  /**
   * Checks the given graphic elements for validity
   *
   * @param graphicElements Elements to check
   * @param nodes Already known and checked nodes
   * @param lines Already known and checked lines
   */
  protected static Try<Void, FailedValidationException> checkGraphicElements(
      GraphicElements graphicElements, Set<NodeInput> nodes, Set<LineInput> lines) {
    checkNonNull(graphicElements, "graphic elements");

    List<ValidationException> exceptions = new ArrayList<>();

    // sanity check for distinct uuids
    Optional<String> exceptionString =
        checkForDuplicateUuids(new HashSet<>(graphicElements.allEntitiesAsList()));
    if (exceptionString.isPresent()) {
      exceptions.add(
          new InvalidGridException(
              duplicateUuidsString(graphicElements.getClass().getSimpleName(), exceptionString)));
    }

    exceptions.addAll(
        (Collection<? extends ValidationException>)
            graphicElements.getNodeGraphics().stream()
                .map(
                    graphic -> {
                      try {
                        GraphicValidationUtils.check(graphic);
                      } catch (ValidationException e) {
                        return new Failure<>(e);
                      }
                      if (!nodes.contains(graphic.getNode())) {
                        return new Failure<>(
                            new InvalidEntityException(
                                "The node graphic with uuid '"
                                    + graphic.getUuid()
                                    + "' refers to node with uuid '"
                                    + graphic.getNode().getUuid()
                                    + "', that is not among the provided ones.",
                                graphic));
                      } else {
                        return Success.empty();
                      }
                    })
                .filter(Try::isFailure)
                .map(Try::getException)
                .toList());

    exceptions.addAll(
        (Collection<? extends ValidationException>)
            graphicElements.getLineGraphics().stream()
                .map(
                    graphic -> {
                      try {
                        GraphicValidationUtils.check(graphic);
                      } catch (ValidationException e) {
                        return new Failure<>(e);
                      }
                      if (!lines.contains(graphic.getLine())) {
                        return new Failure<>(
                            new InvalidEntityException(
                                "The line graphic with uuid '"
                                    + graphic.getUuid()
                                    + "' refers to line with uuid '"
                                    + graphic.getLine().getUuid()
                                    + "', that is not among the provided ones.",
                                graphic));
                      } else {
                        return Success.empty();
                      }
                    })
                .filter(Try::isFailure)
                .map(Try::getException)
                .toList());

    if (exceptions.size() > 0) {
      return new Failure<>(
          new FailedValidationException(
              "Validation failed due to the following exception(s): ",
              new Throwable(ExceptionUtils.getMessages(exceptions))));
    } else {
      return Success.empty();
    }
  }

  /**
   * Checks, if the nodes of the {@link ConnectorInput} are in the collection of provided, already
   * determined nodes
   *
   * @param connector Connector to examine
   * @param nodes Permissible, already known nodes
   */
  private static void checkNodeAvailability(ConnectorInput connector, Collection<NodeInput> nodes) {
    if (!nodes.containsAll(Arrays.asList(connector.getNodeA(), connector.getNodeB())))
      throw getMissingNodeException(connector);
  }

  /**
   * Checks, if the nodes of the {@link Transformer3WInput} are in the collection of provided,
   * already determined nodes
   *
   * @param transformer Transformer to examine
   * @param nodes Permissible, already known nodes
   */
  private static void checkNodeAvailability(
      Transformer3WInput transformer, Collection<NodeInput> nodes) {
    if (!nodes.containsAll(
        Arrays.asList(transformer.getNodeA(), transformer.getNodeB(), transformer.getNodeC())))
      throw getMissingNodeException(transformer);
  }

  /**
   * Checks, if the node of the {@link SystemParticipantInput} are in the collection of provided,
   * already determined nodes
   *
   * @param participant Connector to examine
   * @param nodes Permissible, already known nodes
   */
  private static void checkNodeAvailability(
      SystemParticipantInput participant, Collection<NodeInput> nodes) {
    if (!nodes.contains(participant.getNode())) throw getMissingNodeException(participant);
  }

  /**
   * Checks, if the node of the {@link MeasurementUnitInput} are in the collection of provided,
   * already determined nodes
   *
   * @param measurementUnit Connector to examine
   * @param nodes Permissible, already known nodes
   */
  private static void checkNodeAvailability(
      MeasurementUnitInput measurementUnit, Collection<NodeInput> nodes) {
    if (!nodes.contains(measurementUnit.getNode())) throw getMissingNodeException(measurementUnit);
  }

  /**
   * Builds an exception, that announces, that the given input is connected to a node, that is not
   * in the set of nodes provided.
   *
   * @param input Input model
   * @return Exception for a missing node
   */
  private static InvalidGridException getMissingNodeException(AssetInput input) {
    return new InvalidGridException(
        input.getClass().getSimpleName()
            + " "
            + input
            + " is connected to a node that is not in the set of nodes.");
  }
}
