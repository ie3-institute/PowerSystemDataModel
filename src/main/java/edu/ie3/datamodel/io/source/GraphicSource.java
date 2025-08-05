/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FailedValidationException;
import edu.ie3.datamodel.exceptions.GraphicSourceException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Implementation that provides the capability to build entities of type {@link GraphicInput} from
 * different data sources e.g. .csv files or databases
 *
 * @version 0.1
 * @since 08.04.20
 */
public class GraphicSource extends AssetEntitySource {
  // general fields
  private final TypeSource typeSource;
  private final RawGridSource rawGridSource;

  // factories
  private final LineGraphicInputFactory lineGraphicInputFactory;
  private final NodeGraphicInputFactory nodeGraphicInputFactory;

  /**
   * Instantiates a new Graphic source.
   *
   * @param typeSource the type source
   * @param rawGridSource the raw grid source
   * @param dataSource the data source
   */
  public GraphicSource(TypeSource typeSource, RawGridSource rawGridSource, DataSource dataSource) {
    super(dataSource);
    this.typeSource = typeSource;
    this.rawGridSource = rawGridSource;

    this.lineGraphicInputFactory = new LineGraphicInputFactory();
    this.nodeGraphicInputFactory = new NodeGraphicInputFactory();
  }

  @Override
  public void validate() throws ValidationException {
    Try.scanStream(
            Stream.of(
                validate(NodeGraphicInput.class, dataSource, nodeGraphicInputFactory),
                validate(LineGraphicInput.class, dataSource, lineGraphicInputFactory)),
            "Validation",
            FailedValidationException::new)
        .getOrThrow();
  }

  /**
   * Returns the graphic elements of the grid or throws a {@link SourceException} @return the
   * graphic elements
   *
   * @throws SourceException the source exception
   */
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
   * <p>In contrast to {@link #getGraphicElements()}, this method provides the ability to pass in
   * already existing input objects that this method depends on. Doing so, already loaded nodes and
   * lines can be recycled to improve performance and prevent unnecessary loading operations.
   *
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param lines a map of UUID to object- and uuid-unique {@link LineInput} entities
   * @return the graphic elements
   * @throws SourceException the source exception
   */
  public GraphicElements getGraphicElements(Map<UUID, NodeInput> nodes, Map<UUID, LineInput> lines)
      throws SourceException {
    Try<Set<NodeGraphicInput>, SourceException> nodeGraphics =
        Try.of(() -> getNodeGraphicInput(nodes), SourceException.class);
    Try<Set<LineGraphicInput>, SourceException> lineGraphics =
        Try.of(() -> getLineGraphicInput(lines), SourceException.class);

    List<SourceException> exceptions = Try.getExceptions(nodeGraphics, lineGraphics);

    if (!exceptions.isEmpty()) {
      throw new GraphicSourceException(
          "Exception(s) occurred in "
              + exceptions.size()
              + " input file(s) while initializing graphic elements. ",
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
   * NodeGraphicInput}* entities or if an error during the building process occurs a {@link
   * SourceException}* is thrown, else all entities that have been able to be built, are returned.
   *
   * @return the node graphic input
   * @throws SourceException the source exception
   */
  public Set<NodeGraphicInput> getNodeGraphicInput() throws SourceException {
    return getNodeGraphicInput(rawGridSource.getNodes(typeSource.getOperators()));
  }

  /**
   * Gets node graphic input.
   *
   * @param nodes the nodes
   * @return the node graphic input
   * @throws SourceException the source exception
   */
  public Set<NodeGraphicInput> getNodeGraphicInput(Map<UUID, NodeInput> nodes)
      throws SourceException {
    return getEntities(
            NodeGraphicInput.class,
            dataSource,
            nodeGraphicInputFactory,
            enrich(NODE, nodes, NodeGraphicInputEntityData::new))
        .collect(toSet());
  }

  /**
   * If the set of {@link LineInput} entities is not exhaustive for all available {@link
   * LineGraphicInput}* entities or if an error during the building process occurs a {@link
   * SourceException}* is thrown, else all entities that have been able to be built are returned.
   *
   * @return the line graphic input
   * @throws SourceException the source exception
   */
  public Set<LineGraphicInput> getLineGraphicInput() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    return getLineGraphicInput(
        rawGridSource.getLines(
            operators, rawGridSource.getNodes(operators), typeSource.getLineTypes()));
  }

  /**
   * Gets line graphic input.
   *
   * @param lines the lines
   * @return the line graphic input
   * @throws SourceException the source exception
   */
  public Set<LineGraphicInput> getLineGraphicInput(Map<UUID, LineInput> lines)
      throws SourceException {
    return getEntities(
            LineGraphicInput.class,
            dataSource,
            lineGraphicInputFactory,
            enrich("line", lines, LineGraphicInputEntityData::new))
        .collect(toSet());
  }
}
