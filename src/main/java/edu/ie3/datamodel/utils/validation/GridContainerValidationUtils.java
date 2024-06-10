/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import static edu.ie3.datamodel.utils.validation.UniquenessValidationUtils.checkAssetUniqueness;
import static edu.ie3.datamodel.utils.validation.UniquenessValidationUtils.checkUniqueEntities;

import edu.ie3.datamodel.exceptions.DuplicateEntitiesException;
import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.ConnectorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.container.*;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.utils.ContainerUtils;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Failure;
import edu.ie3.datamodel.utils.Try.Success;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class GridContainerValidationUtils extends ValidationUtils {

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
    Try<Void, InvalidEntityException> isNull = checkNonNull(gridContainer, "grid container");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    /* sanity check to ensure uniqueness */
    exceptions.add(
        Try.ofVoid(
            () -> checkUniqueEntities(gridContainer.allEntitiesAsList()),
            DuplicateEntitiesException.class));

    exceptions.addAll(checkRawGridElements(gridContainer.getRawGrid()));
    exceptions.addAll(
        checkSystemParticipants(
            gridContainer.getSystemParticipants(), gridContainer.getRawGrid().getNodes()));
    exceptions.addAll(
        checkGraphicElements(
            gridContainer.getGraphics(),
            gridContainer.getRawGrid().getNodes(),
            gridContainer.getRawGrid().getLines()));

    if (gridContainer instanceof SubGridContainer subGridContainer) {
      exceptions.add(ConnectorValidationUtils.checkConnectivity(subGridContainer));
    }

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
    Try<Void, InvalidEntityException> isNull = checkNonNull(rawGridElements, "raw grid elements");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    /* sanity check to ensure uniqueness */
    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();
    exceptions.add(
        Try.ofVoid(
            () -> checkAssetUniqueness(rawGridElements.allEntitiesAsList()),
            DuplicateEntitiesException.class));

    /* Checking nodes */
    Set<NodeInput> nodes = rawGridElements.getNodes();
    nodes.forEach(NodeValidationUtils::check);

    /* Checking lines */
    rawGridElements
        .getLines()
        .forEach(
            line -> {
              exceptions.add(checkNodeAvailability(line, nodes));
              exceptions.addAll(ConnectorValidationUtils.check(line));
            });

    /* Checking two winding transformers */
    rawGridElements
        .getTransformer2Ws()
        .forEach(
            transformer -> {
              exceptions.add(checkNodeAvailability(transformer, nodes));
              exceptions.addAll(ConnectorValidationUtils.check(transformer));
            });

    /* Checking three winding transformers */
    rawGridElements
        .getTransformer3Ws()
        .forEach(
            transformer -> {
              exceptions.add(checkNodeAvailability(transformer, nodes));
              exceptions.addAll(ConnectorValidationUtils.check(transformer));
            });

    /* Checking switches
     * Because of the fact, that a transformer with switch gear in "upstream" direction has its corresponding node in
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
              exceptions.add(checkNodeAvailability(switcher, validSwitchNodes));
              exceptions.addAll(ConnectorValidationUtils.check(switcher));
            });

    /* Checking measurement units */
    rawGridElements
        .getMeasurementUnits()
        .forEach(
            measurement -> {
              exceptions.add(checkNodeAvailability(measurement, nodes));
              exceptions.add(MeasurementUnitValidationUtils.check(measurement));
            });

    exceptions.addAll(checkConnectivity(rawGridElements));

    return exceptions;
  }

  /**
   * Checks the connectivity of the given grid for all defined {@link OperationTime}s. If every
   * {@link AssetInput} is set to {@link OperationTime#notLimited()}, the connectivity is only
   * checked once.
   *
   * @param rawGridElements to check
   * @return a try
   */
  protected static List<Try<Void, InvalidGridException>> checkConnectivity(
      RawGridElements rawGridElements) {
    Set<ZonedDateTime> times =
        rawGridElements.allEntitiesAsList().stream()
            .map(AssetInput::getOperationTime)
            .filter(OperationTime::isLimited)
            .map(OperationTime::getOperationLimit)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(interval -> Set.of(interval.getLower(), interval.getUpper()))
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());

    if (times.isEmpty()) {
      return List.of(checkConnectivity(rawGridElements, Optional.empty()));
    } else {
      return times.stream()
          .sorted()
          .map(time -> checkConnectivity(rawGridElements, Optional.of(time)))
          .toList();
    }
  }

  /**
   * Checks if the given {@link RawGridElements} from a connected grid.
   *
   * @param rawGridElements to check
   * @param time for operation filtering
   * @return a try
   */
  protected static Try<Void, InvalidGridException> checkConnectivity(
      RawGridElements rawGridElements, Optional<ZonedDateTime> time) {

    Predicate<AssetInput> isInOperation =
        assetInput -> time.map(assetInput::inOperationOn).orElse(true);

    // build graph
    Graph<UUID, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

    rawGridElements.getNodes().stream()
        .filter(isInOperation)
        .forEach(node -> graph.addVertex(node.getUuid()));
    rawGridElements.getLines().stream()
        .filter(isInOperation)
        .forEach(
            connector ->
                graph.addEdge(connector.getNodeA().getUuid(), connector.getNodeB().getUuid()));
    rawGridElements.getTransformer2Ws().stream()
        .filter(isInOperation)
        .forEach(
            connector ->
                graph.addEdge(connector.getNodeA().getUuid(), connector.getNodeB().getUuid()));
    rawGridElements.getTransformer3Ws().stream()
        .filter(isInOperation)
        .forEach(
            connector ->
                graph.addEdge(connector.getNodeA().getUuid(), connector.getNodeB().getUuid()));
    rawGridElements.getSwitches().stream()
        .filter(isInOperation)
        .forEach(
            connector ->
                graph.addEdge(connector.getNodeA().getUuid(), connector.getNodeB().getUuid()));

    ConnectivityInspector<UUID, DefaultEdge> inspector = new ConnectivityInspector<>(graph);

    if (inspector.isConnected()) {
      return Success.empty();
    } else {
      List<Set<UUID>> sets = inspector.connectedSets();

      List<UUID> unconnected =
          sets.stream()
              .max(Comparator.comparing(Set::size))
              .map(set -> graph.vertexSet().stream().filter(v -> !set.contains(v)).toList())
              .orElse(List.of());

      String message = "The grid contains unconnected elements";

      if (time.isPresent()) {
        message += " for time " + time.get();
      }

      return Failure.of(new InvalidGridException(message + ": " + unconnected));
    }
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
    Try<Void, InvalidEntityException> isNull =
        checkNonNull(systemParticipants, "system participants");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    /* sanity check to ensure uniqueness */
    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();
    exceptions.add(
        Try.ofVoid(
            () -> checkAssetUniqueness(systemParticipants.allEntitiesAsList()),
            DuplicateEntitiesException.class));

    exceptions.addAll(checkSystemParticipants(systemParticipants.getBmPlants(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getChpPlants(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getEvcs(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getFixedFeedIns(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getHeatPumps(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getLoads(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getPvPlants(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getStorages(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getWecPlants(), nodes));

    return exceptions;
  }

  /**
   * Checks the validity of specific system participant. Moreover, it checks, if the systems are
   * connected to a node that is not in the provided set
   *
   * @param participants a set of specific system participants
   * @param nodes Set of already known nodes
   * @return a list of try objects either containing an {@link ValidationException} or an empty
   *     Success
   */
  protected static List<Try<Void, ? extends ValidationException>> checkSystemParticipants(
      Set<? extends SystemParticipantInput> participants, Set<NodeInput> nodes) {
    return participants.stream()
        .map(
            entity -> {
              List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

              exceptions.add(checkNodeAvailability(entity, nodes));
              exceptions.addAll(SystemParticipantValidationUtils.check(entity));

              return exceptions;
            })
        .flatMap(List::stream)
        .toList();
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
  protected static List<Try<Void, ? extends ValidationException>> checkGraphicElements(
      GraphicElements graphicElements, Set<NodeInput> nodes, Set<LineInput> lines) {
    Try<Void, InvalidEntityException> isNull = checkNonNull(graphicElements, "graphic elements");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    /* sanity check to ensure uniqueness */
    exceptions.add(
        Try.ofVoid(
            () -> checkUniqueEntities(graphicElements.allEntitiesAsList()),
            DuplicateEntitiesException.class));

    graphicElements
        .getNodeGraphics()
        .forEach(
            graphic -> {
              exceptions.addAll(GraphicValidationUtils.check(graphic));
              exceptions.add(
                  Try.ofVoid(
                      !nodes.contains(graphic.getNode()),
                      () ->
                          buildGraphicExceptionMessage(
                              graphic, "node", graphic.getNode().getUuid())));
            });

    graphicElements
        .getLineGraphics()
        .forEach(
            graphic -> {
              exceptions.addAll(GraphicValidationUtils.check(graphic));
              exceptions.add(
                  Try.ofVoid(
                      !lines.contains(graphic.getLine()),
                      () ->
                          buildGraphicExceptionMessage(
                              graphic, "line", graphic.getLine().getUuid())));
            });

    return exceptions;
  }

  /**
   * Checks if the node(s) of the given {@link AssetInput} are in the collection of provided already
   * determined nodes.
   *
   * @param input asset to examine
   * @param nodes permissible, already known nodes
   * @return either an {@link InvalidGridException} wrapped in a {@link Failure} or an empty {@link
   *     Success}
   */
  private static Try<Void, InvalidGridException> checkNodeAvailability(
      AssetInput input, Collection<NodeInput> nodes) {
    boolean available;

    if (input instanceof Transformer3WInput transformer) {
      available =
          !nodes.containsAll(
              Arrays.asList(
                  transformer.getNodeA(), transformer.getNodeB(), transformer.getNodeC()));
    } else if (input instanceof ConnectorInput connector) {
      available = !nodes.containsAll(Arrays.asList(connector.getNodeA(), connector.getNodeB()));
    } else if (input instanceof SystemParticipantInput participant) {
      available = !nodes.contains(participant.getNode());
    } else if (input instanceof MeasurementUnitInput measurementUnit) {
      available = !nodes.contains(measurementUnit.getNode());
    } else {
      return Failure.ofVoid(
          new InvalidGridException(
              "Checking the node availability of"
                  + input.getClass().getSimpleName()
                  + " is not implemented."));
    }

    return Try.ofVoid(
        available,
        () ->
            new InvalidGridException(
                input.getClass().getSimpleName()
                    + " "
                    + input
                    + " is connected to a node that is not in the set of nodes."));
  }

  /**
   * Creates a {@link InvalidEntityException} for graphic inputs.
   *
   * @param graphic input
   * @param type of the graphic
   * @param asset uuid of the referred asset
   * @return a {@link Failure}
   */
  private static InvalidEntityException buildGraphicExceptionMessage(
      GraphicInput graphic, String type, UUID asset) {
    return new InvalidEntityException(
        "The "
            + type
            + " graphic with uuid '"
            + graphic.getUuid()
            + "' refers to "
            + type
            + " with uuid '"
            + asset
            + "', that is not a,ong the provided ones.",
        graphic);
  }
}
