/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputFactory;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputFactory;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Interface that provides the capability to build entities of type {@link
 * edu.ie3.datamodel.models.input.graphics.GraphicInput} from different data sources e.g. .csv files
 * or databases
 *
 * @version 0.1
 * @since 08.04.20
 */
public class GraphicSource implements DataSource {
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
  /**
   * Should return either a consistent instance of {@link GraphicElements} wrapped in {@link
   * Optional} or an empty {@link Optional}. The decision to use {@link Optional} instead of
   * returning the {@link GraphicElements} instance directly is motivated by the fact, that a {@link
   * GraphicElements} is a container instance that depends on several other entities. Without being
   * complete, it is useless for further processing. Hence, whenever at least one entity {@link
   * GraphicElements} depends on cannot be provided, {@link Optional#empty()} should be returned and
   * extensive logging should provide enough information to debug the error and fix the persistent
   * data that has been failed to processed.
   *
   * <p>Furthermore, it is expected, that the specific implementation of this method ensures not
   * only the completeness of the resulting {@link GraphicElements} instance, but also its validity
   * e.g. in the sense that not duplicate UUIDs exist within all entities contained in the returning
   * instance.
   *
   * @return either a valid, complete {@link GraphicElements} optional or {@link Optional#empty()}
   */
  public Optional<GraphicElements> getGraphicElements() {
    return null;
    /*

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
     */
  }

  /**
   * Returns a set of {@link NodeGraphicInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link NodeGraphicInput} which has to be checked manually, as {@link
   * NodeGraphicInput#equals(Object)} is NOT restricted on the uuid of {@link NodeGraphicInput}.
   *
   * @return a set of object and uuid unique {@link NodeGraphicInput} entities
   */
  public Set<NodeGraphicInput> getNodeGraphicInput() {
    return null;
    /*    return buildNodeGraphicEntityData(nodes)
        .map(dataOpt -> dataOpt.flatMap(nodeGraphicInputFactory::get))
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());

     */
  }

  /**
   * Returns a set of {@link NodeGraphicInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link NodeGraphicInput} which has to be checked manually, as {@link
   * NodeGraphicInput#equals(Object)} is NOT restricted on the uuid of {@link NodeGraphicInput}.
   *
   * <p>In contrast to {@link #getNodeGraphicInput} this interface provides the ability to pass in
   * an already existing set of {@link NodeInput} entities, the {@link NodeGraphicInput} instances
   * depend on. Doing so, already loaded nodes can be recycled to improve performance and prevent
   * unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param nodes a set of object and uuid unique nodes that should be used for the returning
   *     instances
   * @return a set of object and uuid unique {@link NodeGraphicInput} entities
   */
  public Set<NodeGraphicInput> getNodeGraphicInput(Set<NodeInput> nodes) {
    return null;
    /*
    Set<OperatorInput> operators = typeSource.getOperators();
    return getLineGraphicInput(
        rawGridSource.getLines(
            rawGridSource.getNodes(operators), typeSource.getLineTypes(), operators));

     */
  }

  /**
   * Returns a set of {@link LineGraphicInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link LineGraphicInput} which has to be checked manually, as {@link
   * LineGraphicInput#equals(Object)} is NOT restricted on the uuid of {@link LineGraphicInput}.
   *
   * @return a set of object and uuid unique {@link LineGraphicInput} entities
   */
  public Set<LineGraphicInput> getLineGraphicInput() {
    return null;
    /*
    return buildLineGraphicEntityData(lines)
            .map(dataOpt -> dataOpt.flatMap(lineGraphicInputFactory::get))
            .flatMap(Optional::stream)
            .collect(Collectors.toSet());

     */
  }

  /**
   * Returns a set of {@link LineGraphicInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link LineGraphicInput} which has to be checked manually, as {@link
   * LineGraphicInput#equals(Object)} is NOT restricted on the uuid of {@link LineGraphicInput}.
   *
   * <p>In contrast to {@link #getLineGraphicInput} this interface provides the ability to pass in
   * an already existing set of {@link LineInput} entities, the {@link LineGraphicInput} instances
   * depend on. Doing so, already loaded nodes can be recycled to improve performance and prevent
   * unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param lines a set of object and uuid unique lines that should be used for the returning
   *     instances
   * @return a set of object and uuid unique {@link LineGraphicInput} entities
   */
  public Set<LineGraphicInput> getLineGraphicInput(Set<LineInput> lines) {
    return null;
  }





}
