/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputEntityData;
import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputFactory;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputEntityData;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputFactory;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Interface that provides the capability to build entities of type {@link
 * edu.ie3.datamodel.models.input.graphics.GraphicInput} from different data sources e.g. .csv files
 * or databases
 *
 * @version 0.1
 * @since 08.04.20
 */
public class GraphicSource extends EntitySource implements DataSource {
  // general fields
  TypeSource typeSource;
  RawGridSource rawGridSource;
  FunctionalDataSource dataSource;

  // factories
  private final LineGraphicInputFactory lineGraphicInputFactory;
  private final NodeGraphicInputFactory nodeGraphicInputFactory;

  public GraphicSource(
          TypeSource typeSource,
          RawGridSource rawGridSource,
          FunctionalDataSource dataSource
  ) {
    this.typeSource = typeSource;
    this.rawGridSource = rawGridSource;
    this.dataSource = dataSource;

    this.lineGraphicInputFactory = new LineGraphicInputFactory();
    this.nodeGraphicInputFactory = new NodeGraphicInputFactory();
  }

  /** {@inheritDoc} */
  public Optional<GraphicElements> getGraphicElements() {

    // read all needed entities
    /// start with types and operators
    Set<OperatorInput> operators = typeSource.getOperators();
    Set<LineTypeInput> lineTypes = typeSource.getLineTypes();

    Set<NodeInput> nodes = rawGridSource.getNodes(operators);
    Set<LineInput> lines = rawGridSource.getLines(nodes, lineTypes, operators);

    // start with the entities needed for a GraphicElements entity
    /// as we want to return a working grid, keep an eye on empty optionals
    ConcurrentHashMap<Class<? extends UniqueEntity>, LongAdder> nonBuildEntities =
            new ConcurrentHashMap<>();

    Set<NodeGraphicInput> nodeGraphics =
            buildNodeGraphicEntityData(nodes)
                    .map(dataOpt -> dataOpt.flatMap(nodeGraphicInputFactory::get))
                    .filter(isPresentCollectIfNot(NodeGraphicInput.class, nonBuildEntities))
                    .map(Optional::get)
                    .collect(Collectors.toSet());

    Set<LineGraphicInput> lineGraphics =
            buildLineGraphicEntityData(lines)
                    .map(dataOpt -> dataOpt.flatMap(lineGraphicInputFactory::get))
                    .filter(isPresentCollectIfNot(LineGraphicInput.class, nonBuildEntities))
                    .map(Optional::get)
                    .collect(Collectors.toSet());

    // if we found invalid elements return an empty optional and log the problems
    if (!nonBuildEntities.isEmpty()) {
      nonBuildEntities.forEach(this::printInvalidElementInformation);
      return Optional.empty();
    }

    // if everything is fine, return a GraphicElements instance
    return Optional.of(new GraphicElements(nodeGraphics, lineGraphics));
  }

  /** {@inheritDoc} */
  public Set<NodeGraphicInput> getNodeGraphicInput() {
    return getNodeGraphicInput(rawGridSource.getNodes(typeSource.getOperators()));
  }

  /**
   * {@inheritDoc}
   *
   * <p>If the set of {@link NodeInput} entities is not exhaustive for all available {@link
   * NodeGraphicInput} entities or if an error during the building process occurs, all entities that
   * has been able to be built are returned and the not-built ones are ignored (= filtered out).
   */
  public Set<NodeGraphicInput> getNodeGraphicInput(Set<NodeInput> nodes) {
    return buildNodeGraphicEntityData(nodes)
            .map(dataOpt -> dataOpt.flatMap(nodeGraphicInputFactory::get))
            .flatMap(Optional::stream)
            .collect(Collectors.toSet());
  }

  /** {@inheritDoc} */
  public Set<LineGraphicInput> getLineGraphicInput() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getLineGraphicInput(
            rawGridSource.getLines(
                    rawGridSource.getNodes(operators), typeSource.getLineTypes(), operators));
  }

  /**
   * {@inheritDoc}
   *
   * <p>If the set of {@link LineInput} entities is not exhaustive for all available {@link
   * LineGraphicInput} entities or if an error during the building process occurs, all entities that
   * has been able to be built are returned and the not-built ones are ignored (= filtered out).
   */
  public Set<LineGraphicInput> getLineGraphicInput(Set<LineInput> lines) {
    return buildLineGraphicEntityData(lines)
            .map(dataOpt -> dataOpt.flatMap(lineGraphicInputFactory::get))
            .flatMap(Optional::stream)
            .collect(Collectors.toSet());
  }

  // -=-=-=-=-=-

  /**
   * Builds a stream of {@link NodeGraphicInputEntityData} instances that can be consumed by a
   * {@link NodeGraphicInputFactory} to build instances of {@link NodeGraphicInput} entities. This
   * method depends on corresponding instances of {@link NodeInput} entities that are represented by
   * a corresponding {@link NodeGraphicInput} entity. The determination of matching {@link
   * NodeInput} and {@link NodeGraphicInput} entities is carried out by the UUID of the {@link
   * NodeInput} entity. Hence it is crucial to only pass over collections that are pre-checked for
   * the uniqueness of the UUIDs of the nodes they contain. No further sanity checks are included in
   * this method. If no UUID of a {@link NodeInput} entity can be found for a {@link
   * NodeGraphicInputEntityData} instance, an empty optional is included in the stream and warning
   * is logged.
   *
   * @param nodes a set of nodes with unique uuids
   * @return a stream of optional {@link NodeGraphicInput} entities
   */
  protected Stream<Optional<NodeGraphicInputEntityData>> buildNodeGraphicEntityData(
          Set<NodeInput> nodes) {
    return dataSource.getSourceData(NodeGraphicInput.class)
            .map(fieldsToAttributes -> buildNodeGraphicEntityData(fieldsToAttributes, nodes));
  }

  protected Optional<NodeGraphicInputEntityData> buildNodeGraphicEntityData(
          Map<String, String> fieldsToAttributes, Set<NodeInput> nodes) {

    // get the node of the entity
    String nodeUuid = fieldsToAttributes.get(NODE);
    Optional<NodeInput> node = findFirstEntityByUuid(nodeUuid, nodes);

    // if the node is not present we return an empty element and
    // log a warning
    if (node.isEmpty()) {
      logSkippingWarning(
              NodeGraphicInput.class.getSimpleName(),
              fieldsToAttributes.get("uuid"),
              "no id (graphic entities don't have one)",
              NODE + ": " + nodeUuid);
      return Optional.empty();
    }

    // remove fields that are passed as objects to constructor
    fieldsToAttributes.keySet().remove(NODE);

    return Optional.of(new NodeGraphicInputEntityData(fieldsToAttributes, node.get()));
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
   * LineGraphicInputEntityData} instance, an empty optional is included in the stream and warning
   * is logged.
   *
   * @param lines a set of lines with unique uuids
   * @return a stream of optional {@link LineGraphicInput} entities
   */
  protected Stream<Optional<LineGraphicInputEntityData>> buildLineGraphicEntityData(
          Set<LineInput> lines) {
    return dataSource.getSourceData(LineGraphicInput.class)
            .map(fieldsToAttributes -> buildLineGraphicEntityData(fieldsToAttributes, lines));
  }

  protected Optional<LineGraphicInputEntityData> buildLineGraphicEntityData(
          Map<String, String> fieldsToAttributes, Set<LineInput> lines) {

    // get the node of the entity
    String lineUuid = fieldsToAttributes.get("line");
    Optional<LineInput> line = findFirstEntityByUuid(lineUuid, lines);

    // if the node is not present we return an empty element and
    // log a warning
    if (line.isEmpty()) {
      logSkippingWarning(
              LineGraphicInput.class.getSimpleName(),
              fieldsToAttributes.get("uuid"),
              "no id (graphic entities don't have one)",
              "line: " + lineUuid);
      return Optional.empty();
    }

    // remove fields that are passed as objects to constructor
    fieldsToAttributes.keySet().remove("line");

    return Optional.of(new LineGraphicInputEntityData(fieldsToAttributes, line.get()));
  }



}
