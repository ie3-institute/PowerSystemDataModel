/*
 * © 2020. TU Dortmund University,
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
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.container.GridContainer;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.input.container.SystemParticipants;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.utils.ContainerUtils;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GridContainerValidationUtils extends ValidationUtils {

  /**
   * Checks a complete grid data container
   *
   * @param gridContainer Grid model to check
   */
  public static void check(GridContainer gridContainer) {
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
  public static void checkRawGridElements(RawGridElements rawGridElements) {
    checkNonNull(rawGridElements, "raw grid elements");

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
              MeasurementUnitValidationUtils.check(
                  measurement);
              // TODO NSteffan: Bezug aus MeasurementUnitValidationUtils, da bei
              //  Aufteilung der ValidationUtils notwendig geworden; anders lösen?
            });
  }

  /**
   * Checks the validity of each and every system participant. Moreover, it checks, if the systems
   * are connected to an node that is not in the provided set
   *
   * @param systemParticipants The system participants
   * @param nodes Set of already known nodes
   */
  public static void checkSystemParticipants(
      SystemParticipants systemParticipants, Set<NodeInput> nodes) {
    checkNonNull(systemParticipants, "system participants");

    systemParticipants.getBmPlants().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getChpPlants().forEach(entity -> checkNodeAvailability(entity, nodes));

    /* TODO: Electric vehicle charging systems are currently only dummy implementation. if this has changed, the whole
     *   method can be aggregated */

    systemParticipants.getFixedFeedIns().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getHeatPumps().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getLoads().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getPvPlants().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getStorages().forEach(entity -> checkNodeAvailability(entity, nodes));

    systemParticipants.getWecPlants().forEach(entity -> checkNodeAvailability(entity, nodes));
  }

  /**
   * Checks the given graphic elements for validity
   *
   * @param graphicElements Elements to check
   * @param nodes Already known and checked nodes
   * @param lines Already known and checked lines
   */
  public static void checkGraphicElements(
      GraphicElements graphicElements, Set<NodeInput> nodes, Set<LineInput> lines) {
    checkNonNull(graphicElements, "graphic elements");

    graphicElements
        .getNodeGraphics()
        .forEach(
            graphic -> {
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
    if (!nodes.contains(connector.getNodeA()) || !nodes.contains(connector.getNodeB()))
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
    if (!nodes.contains(transformer.getNodeA())
        || !nodes.contains(transformer.getNodeB())
        || !nodes.contains(transformer.getNodeC())) throw getMissingNodeException(transformer);
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
            + " is connected to a node, that is not in the set of nodes.");
  }
}
