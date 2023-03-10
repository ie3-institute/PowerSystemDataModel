/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

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
import edu.ie3.datamodel.utils.options.Failure;
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
   * @return a list of try objects either containing an {@link ValidationException} or an empty
   *     Success
   */
  protected static List<Try<Void, ? extends ValidationException>> check(
      GridContainer gridContainer) {
    try {
      checkNonNull(gridContainer, "grid container");
    } catch (InvalidEntityException e) {
      return List.of(
          new Failure<>(
              new InvalidEntityException(
                  "Validation not possible because received object {"
                      + gridContainer
                      + "} was null",
                  e)));
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    /* sanity check to ensure distinct UUIDs */
    Optional<String> exceptionString =
        checkForDuplicateUuids(new HashSet<>(gridContainer.allEntitiesAsList()));
    if (exceptionString.isPresent()) {
      exceptions.add(
          new Failure<>(
              new InvalidGridException(
                  duplicateUuidsString(
                      gridContainer.getClass().getSimpleName(), exceptionString))));
    }

    exceptions.addAll(checkRawGridElements(gridContainer.getRawGrid()));
    exceptions.addAll(
        checkSystemParticipants(
            gridContainer.getSystemParticipants(), gridContainer.getRawGrid().getNodes()));
    exceptions.addAll(
        checkGraphicElements(
            gridContainer.getGraphics(),
            gridContainer.getRawGrid().getNodes(),
            gridContainer.getRawGrid().getLines()));

    return exceptions;
  }

  /**
   * Checks the validity of given {@link RawGridElements}. The single elements are checked as well
   * as the fact, that none of the assets is connected to a node, that is not in the set of nodes.
   *
   * @param rawGridElements Raw grid elements
   * @return a list of try objects either containing an {@link ValidationException} or an empty
   *     Success
   */
  protected static List<Try<Void, ? extends ValidationException>> checkRawGridElements(
      RawGridElements rawGridElements) {
    try {
      checkNonNull(rawGridElements, "raw grid elements");
    } catch (InvalidEntityException e) {
      return List.of(
          new Failure<>(
              new InvalidEntityException(
                  "Validation not possible because received object {"
                      + rawGridElements
                      + "} was null",
                  e)));
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    /* sanity check to ensure distinct UUIDs */
    Optional<String> exceptionString =
        checkForDuplicateUuids(new HashSet<>(rawGridElements.allEntitiesAsList()));
    if (exceptionString.isPresent()) {
      exceptions.add(
          new Failure<>(
              new InvalidGridException(
                  duplicateUuidsString(
                      rawGridElements.getClass().getSimpleName(), exceptionString))));
    }

    /* Checking nodes */
    Set<NodeInput> nodes = rawGridElements.getNodes();
    nodes.forEach(NodeValidationUtils::check);

    /* Checking lines */
    rawGridElements
        .getLines()
        .forEach(
            line -> {
              try {
                checkNodeAvailability(line, nodes);
              } catch (InvalidGridException e) {
                exceptions.add(new Failure<>(e));
              }
              exceptions.addAll(ConnectorValidationUtils.check(line));
            });

    /* Checking two winding transformers */
    rawGridElements
        .getTransformer2Ws()
        .forEach(
            transformer -> {
              try {
                checkNodeAvailability(transformer, nodes);
              } catch (InvalidGridException e) {
                exceptions.add(new Failure<>(e));
              }
              exceptions.addAll(ConnectorValidationUtils.check(transformer));
            });

    /* Checking three winding transformers */
    rawGridElements
        .getTransformer3Ws()
        .forEach(
            transformer -> {
              try {
                checkNodeAvailability(transformer, nodes);
              } catch (ValidationException e) {
                exceptions.add(new Failure<>(e));
              }
              exceptions.addAll(ConnectorValidationUtils.check(transformer));
            });

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

    rawGridElements
        .getSwitches()
        .forEach(
            switcher -> {
              try {
                checkNodeAvailability(switcher, validSwitchNodes);
              } catch (ValidationException e) {
                exceptions.add(new Failure<>(e));
              }
              exceptions.addAll(ConnectorValidationUtils.check(switcher));
            });

    /* Checking measurement units */
    rawGridElements
        .getMeasurementUnits()
        .forEach(
            measurement -> {
              try {
                checkNodeAvailability(measurement, nodes);
              } catch (ValidationException e) {
                exceptions.add(new Failure<>(e));
              }
              exceptions.add(MeasurementUnitValidationUtils.check(measurement));
            });

    return exceptions;
  }

  /**
   * Checks the validity of each and every system participant. Moreover, it checks, if the systems
   * are connected to a node that is not in the provided set
   *
   * @param systemParticipants The system participants
   * @param nodes Set of already known nodes
   * @return a list of try objects either containing an {@link ValidationException} or an empty
   *     Success
   */
  protected static List<Try<Void, ? extends ValidationException>> checkSystemParticipants(
      SystemParticipants systemParticipants, Set<NodeInput> nodes) {
    try {
      checkNonNull(systemParticipants, "system participants");
    } catch (InvalidEntityException e) {
      return List.of(
          new Failure<>(
              new InvalidEntityException(
                  "Validation not possible because received object {"
                      + systemParticipants
                      + "} was null",
                  e)));
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    // sanity check for distinct uuids
    Optional<String> exceptionString =
        ValidationUtils.checkForDuplicateUuids(
            new HashSet<>(systemParticipants.allEntitiesAsList()));
    if (exceptionString.isPresent()) {
      exceptions.add(
          new Failure<>(
              new InvalidGridException(
                  duplicateUuidsString(
                      systemParticipants.getClass().getSimpleName(), exceptionString))));
    }

    systemParticipants
        .getBmPlants()
        .forEach(
            entity -> {
              try {
                checkNodeAvailability(entity, nodes);
              } catch (InvalidGridException e) {
                exceptions.add(new Failure<>(e));
              }
              exceptions.addAll(SystemParticipantValidationUtils.check(entity));
            });

    systemParticipants
        .getChpPlants()
        .forEach(
            entity -> {
              try {
                checkNodeAvailability(entity, nodes);
              } catch (InvalidGridException e) {
                exceptions.add(new Failure<>(e));
              }
              exceptions.addAll(SystemParticipantValidationUtils.check(entity));
            });

    /* TODO: Electric vehicle charging systems are currently only dummy implementation. if this has changed, the whole
     *   method can be aggregated */

    systemParticipants
        .getFixedFeedIns()
        .forEach(
            entity -> {
              try {
                checkNodeAvailability(entity, nodes);
              } catch (InvalidGridException e) {
                exceptions.add(new Failure<>(e));
              }
              exceptions.addAll(SystemParticipantValidationUtils.check(entity));
            });

    systemParticipants
        .getHeatPumps()
        .forEach(
            entity -> {
              try {
                checkNodeAvailability(entity, nodes);
              } catch (InvalidGridException e) {
                exceptions.add(new Failure<>(e));
              }
              exceptions.addAll(SystemParticipantValidationUtils.check(entity));
            });

    systemParticipants
        .getLoads()
        .forEach(
            entity -> {
              try {
                checkNodeAvailability(entity, nodes);
              } catch (InvalidGridException e) {
                exceptions.add(new Failure<>(e));
              }
              exceptions.addAll(SystemParticipantValidationUtils.check(entity));
            });

    systemParticipants
        .getPvPlants()
        .forEach(
            entity -> {
              try {
                checkNodeAvailability(entity, nodes);
              } catch (InvalidGridException e) {
                exceptions.add(new Failure<>(e));
              }
              exceptions.addAll(SystemParticipantValidationUtils.check(entity));
            });

    systemParticipants
        .getStorages()
        .forEach(
            entity -> {
              try {
                checkNodeAvailability(entity, nodes);
              } catch (InvalidGridException e) {
                exceptions.add(new Failure<>(e));
              }
              exceptions.addAll(SystemParticipantValidationUtils.check(entity));
            });

    systemParticipants
        .getWecPlants()
        .forEach(
            entity -> {
              try {
                checkNodeAvailability(entity, nodes);
              } catch (InvalidGridException e) {
                exceptions.add(new Failure<>(e));
              }
              exceptions.addAll(SystemParticipantValidationUtils.check(entity));
            });

    return exceptions;
  }

  /**
   * Checks the given graphic elements for validity
   *
   * @param graphicElements Elements to check
   * @param nodes Already known and checked nodes
   * @param lines Already known and checked lines
   * @return a list of try objects either containing an {@link ValidationException} or an empty
   *     Success
   */
  protected static List<Try<Void, ValidationException>> checkGraphicElements(
      GraphicElements graphicElements, Set<NodeInput> nodes, Set<LineInput> lines) {
    try {
      checkNonNull(graphicElements, "graphic elements");
    } catch (InvalidEntityException e) {
      return List.of(
          new Failure<>(
              new InvalidEntityException(
                  "Validation not possible because received object {"
                      + graphicElements
                      + "} was null",
                  e)));
    }

    List<Try<Void, ValidationException>> exceptions = new ArrayList<>();

    // sanity check for distinct uuids
    Optional<String> exceptionString =
        checkForDuplicateUuids(new HashSet<>(graphicElements.allEntitiesAsList()));
    if (exceptionString.isPresent()) {
      exceptions.add(
          new Failure<>(
              new InvalidGridException(
                  duplicateUuidsString(
                      graphicElements.getClass().getSimpleName(), exceptionString))));
    }

    graphicElements
        .getNodeGraphics()
        .forEach(
            graphic -> {
              try {
                GraphicValidationUtils.check(graphic);
              } catch (InvalidEntityException e) {
                exceptions.add(new Failure<>(e));
              }
              if (!nodes.contains(graphic.getNode())) {
                exceptions.add(
                    new Failure<>(
                        new InvalidEntityException(
                            "The node graphic with uuid '"
                                + graphic.getUuid()
                                + "' refers to node with uuid '"
                                + graphic.getNode().getUuid()
                                + "', that is not among the provided ones.",
                            graphic)));
              }
            });

    graphicElements
        .getLineGraphics()
        .forEach(
            graphic -> {
              try {
                GraphicValidationUtils.check(graphic);
              } catch (InvalidEntityException e) {
                exceptions.add(new Failure<>(e));
              }
              if (!lines.contains(graphic.getLine())) {
                exceptions.add(
                    new Failure<>(
                        new InvalidEntityException(
                            "The line graphic with uuid '"
                                + graphic.getUuid()
                                + "' refers to line with uuid '"
                                + graphic.getLine().getUuid()
                                + "', that is not among the provided ones.",
                            graphic)));
              }
            });

    return exceptions;
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
