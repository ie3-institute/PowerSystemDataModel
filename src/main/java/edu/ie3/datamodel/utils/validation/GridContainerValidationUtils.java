/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.exceptions.UnsafeEntityException;
import edu.ie3.datamodel.exceptions.ValidationException;
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
import edu.ie3.datamodel.utils.Try.*;
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
    Try<Void, InvalidEntityException> isNull = checkNonNull(gridContainer, "grid container");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    /* sanity check to ensure distinct UUIDs */
    Optional<String> exceptionString =
        checkForDuplicateUuids(new HashSet<>(gridContainer.allEntitiesAsList()));
    exceptions.add(
        Try.ofVoid(
            exceptionString.isPresent(),
            () ->
                new InvalidGridException(
                    duplicateUuidsString(
                        gridContainer.getClass().getSimpleName(), exceptionString))));

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

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    /* sanity check to ensure distinct UUIDs */
    Optional<String> exceptionString =
        checkForDuplicateUuids(new HashSet<>(rawGridElements.allEntitiesAsList()));
    exceptions.add(
        Try.ofVoid(
            exceptionString.isPresent(),
            () ->
                new InvalidGridException(
                    duplicateUuidsString(
                        rawGridElements.getClass().getSimpleName(), exceptionString))));

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

    exceptions.addAll(checkRawGridTypeIds(rawGridElements));

    return exceptions;
  }

  /**
   * Checks the validity of type ids of every entity.
   *
   * @param rawGridElements the raw grid elements
   * @return a list of try objects either containing an {@link UnsafeEntityException} or an empty
   *     Success
   */
  protected static List<Try<Void, UnsafeEntityException>> checkRawGridTypeIds(
      RawGridElements rawGridElements) {
    List<Try<Void, UnsafeEntityException>> exceptions = new ArrayList<>();
    exceptions.addAll(ValidationUtils.checkIds(rawGridElements.getNodes()));
    exceptions.addAll(ValidationUtils.checkIds(rawGridElements.getLines()));
    exceptions.addAll(ValidationUtils.checkIds(rawGridElements.getTransformer2Ws()));
    exceptions.addAll(ValidationUtils.checkIds(rawGridElements.getTransformer3Ws()));
    exceptions.addAll(ValidationUtils.checkIds(rawGridElements.getSwitches()));
    exceptions.addAll(ValidationUtils.checkIds(rawGridElements.getMeasurementUnits()));

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
    Try<Void, InvalidEntityException> isNull =
        checkNonNull(systemParticipants, "system participants");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    // sanity check for distinct uuids
    Optional<String> exceptionString =
        ValidationUtils.checkForDuplicateUuids(
            new HashSet<>(systemParticipants.allEntitiesAsList()));
    exceptions.add(
        Try.ofVoid(
            exceptionString.isPresent(),
            () ->
                new InvalidGridException(
                    duplicateUuidsString(
                        systemParticipants.getClass().getSimpleName(), exceptionString))));

    exceptions.addAll(checkSystemParticipants(systemParticipants.getBmPlants(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getChpPlants(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getEvCS(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getFixedFeedIns(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getHeatPumps(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getLoads(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getPvPlants(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getStorages(), nodes));
    exceptions.addAll(checkSystemParticipants(systemParticipants.getWecPlants(), nodes));
    exceptions.addAll(checkSystemParticipantsTypeIds(systemParticipants));

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
   * Checks the validity of type ids of every entity.
   *
   * @param systemParticipants the system participants
   * @return a list of try objects either containing an {@link UnsafeEntityException} or an empty
   *     Success
   */
  protected static List<Try<Void, UnsafeEntityException>> checkSystemParticipantsTypeIds(
      SystemParticipants systemParticipants) {
    List<Try<Void, UnsafeEntityException>> exceptions = new ArrayList<>();
    exceptions.addAll(ValidationUtils.checkIds(systemParticipants.getBmPlants()));
    exceptions.addAll(ValidationUtils.checkIds(systemParticipants.getChpPlants()));
    exceptions.addAll(ValidationUtils.checkIds(systemParticipants.getEvCS()));
    exceptions.addAll(ValidationUtils.checkIds(systemParticipants.getEvs()));
    exceptions.addAll(ValidationUtils.checkIds(systemParticipants.getFixedFeedIns()));
    exceptions.addAll(ValidationUtils.checkIds(systemParticipants.getHeatPumps()));
    exceptions.addAll(ValidationUtils.checkIds(systemParticipants.getLoads()));
    exceptions.addAll(ValidationUtils.checkIds(systemParticipants.getPvPlants()));
    exceptions.addAll(ValidationUtils.checkIds(systemParticipants.getStorages()));
    exceptions.addAll(ValidationUtils.checkIds(systemParticipants.getWecPlants()));
    exceptions.addAll(ValidationUtils.checkIds(systemParticipants.getEmSystems()));

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
  protected static List<Try<Void, ? extends ValidationException>> checkGraphicElements(
      GraphicElements graphicElements, Set<NodeInput> nodes, Set<LineInput> lines) {
    Try<Void, InvalidEntityException> isNull = checkNonNull(graphicElements, "graphic elements");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    List<Try<Void, ? extends ValidationException>> exceptions = new ArrayList<>();

    // sanity check for distinct uuids
    Optional<String> exceptionString =
        checkForDuplicateUuids(new HashSet<>(graphicElements.allEntitiesAsList()));
    exceptions.add(
        Try.ofVoid(
            exceptionString.isPresent(),
            () ->
                new InvalidGridException(
                    duplicateUuidsString(
                        graphicElements.getClass().getSimpleName(), exceptionString))));

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
