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
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.container.*;
import edu.ie3.datamodel.models.input.system.ExtendedSystemParticipantInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.Failure;
import edu.ie3.datamodel.utils.Try.Success;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
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
    exceptions.addAll(checkEnergyManagementUnits(gridContainer.getEmUnits()));
    exceptions.addAll(checkSoilData(gridContainer.getSoilData()));

    if (gridContainer instanceof SubGridContainer subGridContainer) {
      exceptions.add(ConnectorValidationUtils.checkConnectivity(subGridContainer));
    }

    return exceptions;
  }

  /**
   * Checks the validity of the given {@link SoilData}. This covers the per-entity checks for soil
   * types and soil layers as well as the check that soil layers sharing the same geometry neither
   * overlap nor leave gaps within their depth ranges.
   *
   * @param soilData the soil data to check
   * @return a list of try objects either containing an {@link ValidationException} or an empty
   *     Success
   */
  protected static List<Try<Void, ? extends ValidationException>> checkSoilData(SoilData soilData) {
    Try<Void, InvalidEntityException> isNull = checkNonNull(soilData, "soil data");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    return List.of(
        SoilValidationUtils.validateAll(
            new ArrayList<>(soilData.getSoilLayers()), new ArrayList<>(soilData.getSoilTypes())));
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
    List<Try<Void, ? extends ValidationException>> exceptions =
        new ArrayList<>(
            Try.ofVoids(
                DuplicateEntitiesException.class,
                () -> checkAssetUniqueness(rawGridElements.getNodes()),
                () -> checkAssetUniqueness(rawGridElements.getLines()),
                () -> checkAssetUniqueness(rawGridElements.getSwitches()),
                () -> checkAssetUniqueness(rawGridElements.getTransformer2Ws()),
                () -> checkAssetUniqueness(rawGridElements.getTransformer3Ws()),
                () -> checkAssetUniqueness(rawGridElements.getMeasurementUnits())));

    /* Checking nodes */
    Set<NodeInput> nodes = rawGridElements.getNodes();
    nodes.forEach(node -> exceptions.addAll(NodeValidationUtils.check(node)));

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

    /* Checking switches */
    rawGridElements
        .getSwitches()
        .forEach(
            switcher -> {
              exceptions.add(checkNodeAvailability(switcher, nodes));
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

    /* Checking cable deployments */
    Map<UUID, LineInput> linesByUuid =
        rawGridElements.getLines().stream().collect(Collectors.toMap(LineInput::getUuid, l -> l));
    rawGridElements
        .getCableDeploymentsByLine()
        .forEach(
            (lineUuid, deployments) -> {
              LineInput line = linesByUuid.get(lineUuid);

              for (CableDeploymentInput deployment : deployments) {
                if (line == null) {
                  exceptions.add(
                      Try.ofVoid(
                          true,
                          () ->
                              new InvalidEntityException(
                                  "Cable deployment references unknown line with uuid " + lineUuid,
                                  deployment)));
                } else {
                  exceptions.addAll(
                      ConnectorValidationUtils.checkCableDeployment(deployment, line));
                }
              }
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
        .filter(SwitchInput::isClosed)
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
   * Checks the validity and uniqueness of each and every system participant without checking if
   * they are connected to a known node.
   *
   * @param systemParticipants The system participants
   * @return a list of try objects either containing an {@link ValidationException} or an empty
   *     Success
   */
  protected static List<Try<Void, ? extends ValidationException>> checkSystemParticipants(
      SystemParticipants systemParticipants) {
    Try<Void, InvalidEntityException> isNull =
        checkNonNull(systemParticipants, "system participants");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();
    participantsByType(systemParticipants)
        .forEach(participants -> exceptions.addAll(checkSystemParticipants(participants)));

    return exceptions;
  }

  /**
   * Checks the validity and uniqueness of each and every system participant. Moreover, it checks,
   * if the systems are connected to a node that is not in the provided set
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

    List<Try<Void, ? extends ValidationException>> exceptions =
        new ArrayList<>(checkSystemParticipants(systemParticipants));

    participantsByType(systemParticipants).stream()
        .flatMap(Set::stream)
        .filter(ExtendedSystemParticipantInput.class::isInstance)
        .forEach(participant -> exceptions.add(checkNodeAvailability(participant, nodes)));

    return exceptions;
  }

  /**
   * Checks the validity and uniqueness of specific system participants.
   *
   * @param participants a set of specific system participants
   * @return a list of try objects either containing an {@link ValidationException} or an empty
   *     Success
   */
  protected static List<Try<Void, ? extends ValidationException>> checkSystemParticipants(
      Set<? extends SystemParticipantInput> participants) {
    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();
    exceptions.add(
        Try.ofVoid(() -> checkAssetUniqueness(participants), DuplicateEntitiesException.class));

    participants.forEach(
        participant -> exceptions.addAll(SystemParticipantValidationUtils.check(participant)));

    return exceptions;
  }

  /**
   * Returns the sets of system participants that are validated.
   *
   * @param systemParticipants The system participants
   * @return a list with one set of system participants per type
   */
  private static List<Set<? extends SystemParticipantInput>> participantsByType(
      SystemParticipants systemParticipants) {
    return List.of(
        systemParticipants.getAirConditions(),
        systemParticipants.getBmPlants(),
        systemParticipants.getChpPlants(),
        systemParticipants.getEvcs(),
        systemParticipants.getEvs(),
        systemParticipants.getFixedFeedIns(),
        systemParticipants.getHeatPumps(),
        systemParticipants.getLoads(),
        systemParticipants.getPvPlants(),
        systemParticipants.getStorages(),
        systemParticipants.getWecPlants());
  }

  /**
   * Checks the validity and uniqueness of each and every energy management unit.
   *
   * @param emUnits The energy management units
   * @return a list of try objects either containing an {@link ValidationException} or an empty
   *     Success
   */
  protected static List<Try<Void, ? extends ValidationException>> checkEnergyManagementUnits(
      EnergyManagementUnits emUnits) {
    Try<Void, InvalidEntityException> isNull = checkNonNull(emUnits, "energy management units");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    /* sanity check to ensure uniqueness */
    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();
    exceptions.add(
        Try.ofVoid(
            () -> checkAssetUniqueness(emUnits.getEmUnits()), DuplicateEntitiesException.class));

    emUnits
        .getEmUnits()
        .forEach(emUnit -> exceptions.addAll(EnergyManagementValidationUtils.check(emUnit)));

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

    switch (input) {
      case Transformer3WInput transformer ->
          available =
              !nodes.containsAll(
                  Arrays.asList(
                      transformer.getNodeA(), transformer.getNodeB(), transformer.getNodeC()));
      case ConnectorInput connector ->
          available = !nodes.containsAll(Arrays.asList(connector.getNodeA(), connector.getNodeB()));
      case ExtendedSystemParticipantInput participant ->
          available = !nodes.contains(participant.getNode());
      case MeasurementUnitInput measurementUnit ->
          available = !nodes.contains(measurementUnit.getNode());
      default -> {
        return Failure.ofVoid(
            new InvalidGridException(
                "Checking the node availability of"
                    + input.getClass().getSimpleName()
                    + " is not implemented."));
      }
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
}
