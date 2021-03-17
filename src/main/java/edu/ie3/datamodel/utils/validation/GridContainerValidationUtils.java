/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.ConnectorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.container.*;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.utils.ContainerUtils;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GridContainerValidationUtils extends ValidationUtils {

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
    Optional<String> exceptionString =
        checkForDuplicateUuids(new HashSet<>(gridContainer.allEntitiesAsList()));
    if (exceptionString.isPresent()) {
      throw new InvalidGridException(
          "The provided entities in '"
              + gridContainer.getClass().getSimpleName()
              + "' contains duplicate UUIDs. "
              + "This is not allowed!\nDuplicated uuids:\n\n"
              + exceptionString);
    }

    checkRawGridElements(gridContainer.getRawGrid());
    checkSystemParticipants(
        gridContainer.getSystemParticipants(), gridContainer.getRawGrid().getNodes());
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
  protected static void checkRawGridElements(RawGridElements rawGridElements) {
    checkNonNull(rawGridElements, "raw grid elements");

    /* sanity check to ensure distinct UUIDs */
    Optional<String> exceptionString =
        checkForDuplicateUuids(new HashSet<>(rawGridElements.allEntitiesAsList()));
    if (exceptionString.isPresent()) {
      throw new InvalidGridException(
          "The provided entities in '"
              + rawGridElements.getClass().getSimpleName()
              + "' contains duplicate UUIDs. "
              + "This is not allowed!\nDuplicated uuids:\n\n"
              + exceptionString);
    }

    /* Checking nodes */
    Set<NodeInput> nodes = rawGridElements.getNodes();
    nodes.forEach(NodeValidationUtils::check);

    /* Checking lines */
    rawGridElements
        .getLines()
        .forEach(
            line -> {
              checkNodeAvailability(line, nodes);
              ConnectorValidationUtils.check(line);
            });

    /* Checking two winding transformers */
    rawGridElements
        .getTransformer2Ws()
        .forEach(
            transformer -> {
              checkNodeAvailability(transformer, nodes);
              ConnectorValidationUtils.check(transformer);
            });

    /* Checking three winding transformers */
    rawGridElements
        .getTransformer3Ws()
        .forEach(
            transformer -> {
              checkNodeAvailability(transformer, nodes);
              ConnectorValidationUtils.check(transformer);
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
            .collect(Collectors.toList()));

    rawGridElements
        .getSwitches()
        .forEach(
            switcher -> {
              checkNodeAvailability(switcher, validSwitchNodes);
              ConnectorValidationUtils.check(switcher);
            });

    /* Checking measurement units */
    rawGridElements
        .getMeasurementUnits()
        .forEach(
            measurement -> {
              checkNodeAvailability(measurement, nodes);
              MeasurementUnitValidationUtils.check(measurement);
            });
  }

  /**
   * Checks the validity of each and every system participant. Moreover, it checks, if the systems
   * are connected to a node that is not in the provided set
   *
   * @param systemParticipants The system participants
   * @param nodes Set of already known nodes
   */
  protected static void checkSystemParticipants(
      SystemParticipants systemParticipants, Set<NodeInput> nodes) {
    checkNonNull(systemParticipants, "system participants");

    // sanity check for distinct uuids
    Optional<String> exceptionString =
        ValidationUtils.checkForDuplicateUuids(
            new HashSet<>(systemParticipants.allEntitiesAsList()));
    if (exceptionString.isPresent()) {
      throw new InvalidGridException(
          "The provided entities in '"
              + systemParticipants.getClass().getSimpleName()
              + "' contains duplicate UUIDs. "
              + "This is not allowed!\nDuplicated uuids:\n\n"
              + exceptionString);
    }

    systemParticipants
        .getBmPlants()
        .forEach(
            entity -> {
              checkNodeAvailability(entity, nodes);
              SystemParticipantValidationUtils.check(entity);
            });

    systemParticipants
        .getChpPlants()
        .forEach(
            entity -> {
              checkNodeAvailability(entity, nodes);
              SystemParticipantValidationUtils.check(entity);
            });

    /* TODO: Electric vehicle charging systems are currently only dummy implementation. if this has changed, the whole
     *   method can be aggregated */

    systemParticipants
        .getFixedFeedIns()
        .forEach(
            entity -> {
              checkNodeAvailability(entity, nodes);
              SystemParticipantValidationUtils.check(entity);
            });

    systemParticipants
        .getHeatPumps()
        .forEach(
            entity -> {
              checkNodeAvailability(entity, nodes);
              SystemParticipantValidationUtils.check(entity);
            });

    systemParticipants
        .getLoads()
        .forEach(
            entity -> {
              checkNodeAvailability(entity, nodes);
              SystemParticipantValidationUtils.check(entity);
            });

    systemParticipants
        .getPvPlants()
        .forEach(
            entity -> {
              checkNodeAvailability(entity, nodes);
              SystemParticipantValidationUtils.check(entity);
            });

    systemParticipants
        .getStorages()
        .forEach(
            entity -> {
              checkNodeAvailability(entity, nodes);
              SystemParticipantValidationUtils.check(entity);
            });

    systemParticipants
        .getWecPlants()
        .forEach(
            entity -> {
              checkNodeAvailability(entity, nodes);
              SystemParticipantValidationUtils.check(entity);
            });
  }

  /**
   * Checks the given graphic elements for validity
   *
   * @param graphicElements Elements to check
   * @param nodes Already known and checked nodes
   * @param lines Already known and checked lines
   */
  protected static void checkGraphicElements(
      GraphicElements graphicElements, Set<NodeInput> nodes, Set<LineInput> lines) {
    checkNonNull(graphicElements, "graphic elements");

    // sanity check for distinct uuids
    Optional<String> exceptionString =
        checkForDuplicateUuids(new HashSet<>(graphicElements.allEntitiesAsList()));
    if (exceptionString.isPresent()) {
      throw new InvalidGridException(
          "The provided entities in '"
              + graphicElements.getClass().getSimpleName()
              + "' contains duplicate UUIDs. "
              + "This is not allowed!\nDuplicated uuids:\n\n"
              + exceptionString);
    }

    graphicElements
        .getNodeGraphics()
        .forEach(
            graphic -> {
              GraphicValidationUtils.check(graphic);
              if (!nodes.contains(graphic.getNode()))
                throw new InvalidEntityException(
                    "The node graphic with uuid '"
                        + graphic.getUuid()
                        + "' refers to node with uuid '"
                        + graphic.getNode().getUuid()
                        + "', that is not among the provided ones.",
                    graphic);
            });

    graphicElements
        .getLineGraphics()
        .forEach(
            graphic -> {
              GraphicValidationUtils.check(graphic);
              if (!lines.contains(graphic.getLine()))
                throw new InvalidEntityException(
                    "The line graphic with uuid '"
                        + graphic.getUuid()
                        + "' refers to line with uuid '"
                        + graphic.getLine().getUuid()
                        + "', that is not among the provided ones.",
                    graphic);
            });
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
