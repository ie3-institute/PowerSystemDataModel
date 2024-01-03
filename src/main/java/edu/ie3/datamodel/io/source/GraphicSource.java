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
    super(dataSource);
    this.typeSource = typeSource;
    this.rawGridSource = rawGridSource;

    this.lineGraphicInputFactory = new LineGraphicInputFactory();
    this.nodeGraphicInputFactory = new NodeGraphicInputFactory();
  }

  /** Returns the graphic elements of the grid or throws a {@link SourceException} */
  public GraphicElements getGraphicElements() throws SourceException {

    // read all needed entities
    /// start with types and operators
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, LineTypeInput> lineTypes = typeSource.getLineTypes();

    Map<UUID, NodeInput> nodes = rawGridSource.getNodes(operators);
    Map<UUID, LineInput> lines = rawGridSource.getLines(operators, nodes, lineTypes);

    return getGraphicElements(nodes, lines);
  }

  /**
   * Returns the graphic elements of the grid or throws a {@link SourceException}.
   *
   * <p>This constructor reuses some basic input data to improve performance.
   *
   * @param nodes All nodes of the grid in a map UUID -> node
   * @param lines All lines of the grid in a map UUID -> line
   */
  public GraphicElements getGraphicElements(Map<UUID, NodeInput> nodes, Map<UUID, LineInput> lines)
      throws SourceException {
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
   * SourceException} is thrown, else all entities that have been able to be built, are returned.
   */
  public Set<NodeGraphicInput> getNodeGraphicInput() throws SourceException {
    return getNodeGraphicInput(rawGridSource.getNodes(typeSource.getOperators()));
  }

  public Set<NodeGraphicInput> getNodeGraphicInput(Map<UUID, NodeInput> nodes)
      throws SourceException {
    return unpackSet(
        buildNodeGraphicEntityData(nodes).map(nodeGraphicInputFactory::get),
        NodeGraphicInput.class);
  }

  /**
   * If the set of {@link LineInput} entities is not exhaustive for all available {@link
   * LineGraphicInput} entities or if an error during the building process occurs a {@link
   * SourceException} is thrown, else all entities that have been able to be built are returned.
   */
  public Set<LineGraphicInput> getLineGraphicInput() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    return getLineGraphicInput(
        rawGridSource.getLines(
            operators, rawGridSource.getNodes(operators), typeSource.getLineTypes()));
  }

  public Set<LineGraphicInput> getLineGraphicInput(Map<UUID, LineInput> lines)
      throws SourceException {
    return unpackSet(
        buildLineGraphicEntityData(lines).map(lineGraphicInputFactory::get),
        LineGraphicInput.class);
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
      Map<UUID, NodeInput> nodes) {
    return buildEntityData(NodeGraphicInput.class)
        .map(
            entityDataTry ->
                entityDataTry.flatMap(
                    entityData ->
                        enrichEntityData(
                            entityData, NODE, nodes, NodeGraphicInputEntityData::new)));
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
      Map<UUID, LineInput> lines) {
    return buildEntityData(LineGraphicInput.class)
        .map(
            entityDataTry ->
                entityDataTry.flatMap(
                    entityData ->
                        enrichEntityData(
                            entityData, "line", lines, LineGraphicInputEntityData::new)));
  }
}
