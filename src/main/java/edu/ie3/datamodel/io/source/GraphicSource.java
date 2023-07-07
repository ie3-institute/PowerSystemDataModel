/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.GraphicSourceException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputEntityData;
import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputFactory;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputEntityData;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputFactory;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation that provides the capability to build entities of type {@link GraphicInput} from
 * different data sources e.g. .csv files or databases
 *
 * @version 0.1
 * @since 08.04.20
 */
public class GraphicSource extends EntitySource {
  // general fields
  private final TypeSource typeSource;
  private final RawGridSource rawGridSource;

  // factories
  private final LineGraphicInputFactory lineGraphicInputFactory;
  private final NodeGraphicInputFactory nodeGraphicInputFactory;

  public GraphicSource(TypeSource typeSource, RawGridSource rawGridSource, DataSource dataSource) {
    this.typeSource = typeSource;
    this.rawGridSource = rawGridSource;
    this.dataSource = dataSource;

    this.lineGraphicInputFactory = new LineGraphicInputFactory();
    this.nodeGraphicInputFactory = new NodeGraphicInputFactory();
  }

  /** Returns the graphic elements of the grid or throws a {@link SourceException} */
  public GraphicElements getGraphicElements() throws SourceException {

    // read all needed entities
    /// start with types and operators
    Set<OperatorInput> operators = typeSource.getOperators();
    Set<LineTypeInput> lineTypes = typeSource.getLineTypes();

    Set<NodeInput> nodes = rawGridSource.getNodes(operators);
    Set<LineInput> lines = rawGridSource.getLines(nodes, lineTypes, operators);

    Try<Set<NodeGraphicInput>, SourceException> nodeGraphics =
        Try.of(() -> getNodeGraphicInput(nodes), SourceException.class);
    Try<Set<LineGraphicInput>, SourceException> lineGraphics =
        Try.of(() -> getLineGraphicInput(lines), SourceException.class);

    List<SourceException> exceptions = Try.getExceptions(List.of(nodeGraphics, lineGraphics));

    if (!exceptions.isEmpty()) {
      throw new GraphicSourceException(
          exceptions.size() + "error(s) occurred while initializing graphic elements. ",
          exceptions);
    } else {
      // if everything is fine, return a GraphicElements instance
      // getOrThrow should not throw an exception in this context, because all exception are
      // filtered and thrown before
      return new GraphicElements(nodeGraphics.getOrThrow(), lineGraphics.getOrThrow());
    }
  }

  /**
   * If the set of {@link NodeInput} entities is not exhaustive for all available {@link
   * NodeGraphicInput} entities or if an error during the building process occurs a {@link
   * SourceException} is thrown, else all entities that has been able to be built are returned.
   */
  public Set<NodeGraphicInput> getNodeGraphicInput() throws SourceException {
    return getNodeGraphicInput(rawGridSource.getNodes(typeSource.getOperators()));
  }

  public Set<NodeGraphicInput> getNodeGraphicInput(Set<NodeInput> nodes) throws SourceException {
    return Try.scanCollection(
            buildNodeGraphicEntityData(nodes)
                .map(nodeGraphicInputFactory::get)
                .collect(Collectors.toSet()),
            NodeGraphicInput.class)
        .transformF(SourceException::new)
        .getOrThrow();
  }

  /**
   * If the set of {@link LineInput} entities is not exhaustive for all available {@link
   * LineGraphicInput} entities or if an error during the building process occurs a {@link
   * SourceException} is thrown, else all entities that has been able to be built are returned.
   */
  public Set<LineGraphicInput> getLineGraphicInput() throws SourceException {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getLineGraphicInput(
        rawGridSource.getLines(
            rawGridSource.getNodes(operators), typeSource.getLineTypes(), operators));
  }

  public Set<LineGraphicInput> getLineGraphicInput(Set<LineInput> lines) throws SourceException {
    return Try.scanCollection(
            buildLineGraphicEntityData(lines)
                .map(lineGraphicInputFactory::get)
                .collect(Collectors.toSet()),
            LineGraphicInput.class)
        .transformF(SourceException::new)
        .getOrThrow();
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  // build EntityData

  /**
   * Builds a stream of {@link NodeGraphicInputEntityData} instances that can be consumed by a
   * {@link NodeGraphicInputFactory} to build instances of {@link NodeGraphicInput} entities. This
   * method depends on corresponding instances of {@link NodeInput} entities that are represented by
   * a corresponding {@link NodeGraphicInput} entity. The determination of matching {@link
   * NodeInput} and {@link NodeGraphicInput} entities is carried out by the UUID of the {@link
   * NodeInput} entity. Hence it is crucial to only pass over collections that are pre-checked for
   * the uniqueness of the UUIDs of the nodes they contain. No further sanity checks are included in
   * this method. If no UUID of a {@link NodeInput} entity can be found for a {@link
   * NodeGraphicInputEntityData} instance, a {@link Failure} is included in the stream and warning
   * is logged.
   *
   * @param nodes a set of nodes with unique uuids
   * @return a stream of tries of {@link NodeGraphicInput} entities
   */
  protected Stream<Try<NodeGraphicInputEntityData, SourceException>> buildNodeGraphicEntityData(
      Set<NodeInput> nodes) {
    return dataSource
        .getSourceData(NodeGraphicInput.class)
        .map(fieldsToAttributes -> buildNodeGraphicEntityData(fieldsToAttributes, nodes));
  }

  protected Try<NodeGraphicInputEntityData, SourceException> buildNodeGraphicEntityData(
      Map<String, String> fieldsToAttributes, Set<NodeInput> nodes) {

    // get the node of the entity
    String nodeUuid = fieldsToAttributes.get(NODE);
    Optional<NodeInput> node = findFirstEntityByUuid(nodeUuid, nodes);

    // if the node is not present we return a failure
    // log a warning
    if (node.isEmpty()) {
      String logMessage =
          logSkippingWarning(
              NodeGraphicInput.class.getSimpleName(),
              fieldsToAttributes.get("uuid"),
              "no id (graphic entities don't have one)",
              NODE + ": " + nodeUuid);
      return new Failure<>(new SourceException("Failure due to: " + logMessage));
    }

    // remove fields that are passed as objects to constructor
    fieldsToAttributes.keySet().remove(NODE);

    return new Success<>(new NodeGraphicInputEntityData(fieldsToAttributes, node.get()));
  }

  /**
   * Builds a stream of {@link LineGraphicInputEntityData} instances that can be consumed by a
   * {@link LineGraphicInputFactory} to build instances of {@link LineGraphicInput} entities. This
   * method depends on corresponding instances of {@link LineInput} entities that are represented by
   * a corresponding {@link LineGraphicInput} entity. The determination of matching {@link
   * LineInput} and {@link LineGraphicInput} entities is carried out by the UUID of the {@link
   * LineInput} entity. Hence it is crucial to only pass over collections that are pre-checked for
   * the uniqueness of the UUIDs of the nodes they contain. No further sanity checks are included in
   * this method. If no UUID of a {@link LineInput} entity can be found for a {@link
   * LineGraphicInputEntityData} instance, a {@link Failure} is included in the stream and warning
   * is logged.
   *
   * @param lines a set of lines with unique uuids
   * @return a stream of tries of {@link LineGraphicInput} entities
   */
  protected Stream<Try<LineGraphicInputEntityData, SourceException>> buildLineGraphicEntityData(
      Set<LineInput> lines) {
    return dataSource
        .getSourceData(LineGraphicInput.class)
        .map(fieldsToAttributes -> buildLineGraphicEntityData(fieldsToAttributes, lines));
  }

  protected Try<LineGraphicInputEntityData, SourceException> buildLineGraphicEntityData(
      Map<String, String> fieldsToAttributes, Set<LineInput> lines) {

    // get the node of the entity
    String lineUuid = fieldsToAttributes.get("line");
    Optional<LineInput> line = findFirstEntityByUuid(lineUuid, lines);

    // if the node is not present we return an empty element and
    // log a warning
    if (line.isEmpty()) {
      String logMessage =
          logSkippingWarning(
              LineGraphicInput.class.getSimpleName(),
              fieldsToAttributes.get("uuid"),
              "no id (graphic entities don't have one)",
              "line: " + lineUuid);
      return new Failure<>(new SourceException("Failure due to: " + logMessage));
    }

    // remove fields that are passed as objects to constructor
    fieldsToAttributes.keySet().remove("line");

    return new Success<>(new LineGraphicInputEntityData(fieldsToAttributes, line.get()));
  }
}
