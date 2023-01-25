/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.RawGridElements;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Interface that provides the capability to build entities that are hold by a {@link
 * RawGridElements} as well as the {@link RawGridElements} container as well from different data
 * sources e.g. .csv files or databases.
 *
 * @version 0.1
 * @since 08.04.20
 */
public class RawGridSource implements DataSource {
  //general fields
  private TypeSource typeSource;

  private FunctionalDataSource dataSource;

  //factories
  private final NodeInputFactory nodeInputFactory;
  private final LineInputFactory lineInputFactory;
  private final Transformer2WInputFactory transformer2WInputFactory;
  private final Transformer3WInputFactory transformer3WInputFactory;
  private final SwitchInputFactory switchInputFactory;
  private final MeasurementUnitInputFactory measurementUnitInputFactory;

  public RawGridSource(TypeSource _typeSource, FunctionalDataSource _dataSource) {
    this.typeSource = _typeSource;
    this.dataSource = _dataSource;

    // init factories
    this.nodeInputFactory = new NodeInputFactory();
    this.lineInputFactory = new LineInputFactory();
    this.transformer2WInputFactory = new Transformer2WInputFactory();
    this.transformer3WInputFactory = new Transformer3WInputFactory();
    this.switchInputFactory = new SwitchInputFactory();
    this.measurementUnitInputFactory = new MeasurementUnitInputFactory();
  }

  /**
   * Should return either a consistent instance of {@link RawGridElements} wrapped in {@link
   * Optional} or an empty {@link Optional}. The decision to use {@link Optional} instead of
   * returning the {@link RawGridElements} instance directly is motivated by the fact, that a {@link
   * RawGridElements} is a container instance that depends on several other entities. Without being
   * complete, it is useless for further processing.
   *
   * <p>Hence, whenever at least one entity {@link RawGridElements} depends on cannot be provided,
   * {@link Optional#empty()} should be returned and extensive logging should provide enough
   * information to debug the error and fix the persistent data that has been failed to processed.
   *
   * <p>Furthermore, it is expected, that the specific implementation of this method ensures not
   * only the completeness of the resulting {@link RawGridElements} instance, but also its validity
   * e.g. in the sense that not duplicate UUIDs exist within all entities contained in the returning
   * instance.
   *
   * @return either a valid, complete {@link RawGridElements} optional or {@link Optional#empty()}
   */
  public Optional<RawGridElements> getGridData() {
    /* read all needed entities start with the types and operators */
    Set<OperatorInput> operators = typeSource.getOperators();
    Set<LineTypeInput> lineTypes = typeSource.getLineTypes();
    Set<Transformer2WTypeInput> transformer2WTypeInputs = typeSource.getTransformer2WTypes();
    Set<Transformer3WTypeInput> transformer3WTypeInputs = typeSource.getTransformer3WTypes();

    /* assets */
    Set<NodeInput> nodes = getNodes(operators);

    /* start with the entities needed for a RawGridElement as we want to return a working grid, keep an eye on empty
     * optionals which is equal to elements that have been unable to be built e.g. due to missing elements they depend
     * on
     */
    ConcurrentHashMap<Class<? extends UniqueEntity>, LongAdder> nonBuildEntities =
            new ConcurrentHashMap<>();

    Set<LineInput> lineInputs = dataSource.buildTypedEntities(LineInput.class, lineInputFactory, nodes, operators, lineTypes, nonBuildEntities);
    Set<Transformer2WInput> transformer2WInputs = dataSource.buildTypedEntities(
            Transformer2WInput.class,
            transformer2WInputFactory,
            nodes,
            operators,
            transformer2WTypeInputs,
            nonBuildEntities);
    Set<Transformer3WInput> transformer3WInputs = dataSource.buildTransformer3WEntities(transformer3WInputFactory, nodes, transformer3WTypeInputs, operators);
    Set<SwitchInput> switches = dataSource.buildUntypedConnectorInputEntities(SwitchInput.class, switchInputFactory, nodes, operators, nonBuildEntities);
    Set<MeasurementUnitInput> measurementUnits = dataSource.buildNodeAssetEntities(MeasurementUnitInput.class, measurementUnitInputFactory, nodes, operators, nonBuildEntities);

    /* if we found non-build elements return an empty optional and log the problems */
    if (!nonBuildEntities.isEmpty()) {
      nonBuildEntities.forEach(dataSource::printInvalidElementInformation);
      return Optional.empty();
    }

    //build the grid
    RawGridElements gridElements =
            new RawGridElements(
                    nodes,
                    lineInputs,
                    transformer2WInputs,
                    transformer3WInputs,
                    switches,
                    measurementUnits);

    //return the grid if it is not empty
    return gridElements.allEntitiesAsList().isEmpty()
            ? Optional.empty()
            : Optional.of(gridElements);
  }

  /**
   * Returns a unique set of {@link NodeInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link NodeInput} which has to be checked manually,
   * as {@link NodeInput#equals(Object)} is NOT restricted on the uuid of {@link NodeInput}.
   *
   * @return a set of object and uuid unique {@link NodeInput} entities
   */
  public Set<NodeInput> getNodes() {
    return getNodes(typeSource.getOperators());
  }

  /**
   * Returns a set of {@link NodeInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * NodeInput} which has to be checked manually, as {@link NodeInput#equals(Object)} is NOT
   * restricted on the uuid of {@link NodeInput}.
   *
   * <p>In contrast to {@link #getNodes} this interface provides the ability to pass in an already
   * existing set of {@link OperatorInput} entities, the {@link NodeInput} instances depend on.
   * Doing so, already loaded nodes can be recycled to improve performance and prevent unnecessary
   * loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @return a set of object and uuid unique {@link NodeInput} entities
   */
  public Set<NodeInput> getNodes(Set<OperatorInput> operators) {
    return dataSource.buildAssetInputEntities(NodeInput.class, nodeInputFactory, operators);
  }

  /**
   * Returns a unique set of {@link LineInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link LineInput} which has to be checked manually,
   * as {@link LineInput#equals(Object)} is NOT restricted on the uuid of {@link LineInput}.
   *
   * @return a set of object and uuid unique {@link LineInput} entities
   */
  public Set<LineInput> getLines() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getLines(getNodes(operators), typeSource.getLineTypes(), operators);
  }

  /**
   * Returns a set of {@link LineInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * LineInput} which has to be checked manually, as {@link LineInput#equals(Object)} is NOT
   * restricted on the uuid of {@link LineInput}.
   *
   * <p>In contrast to {@link #getNodes} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link LineTypeInput} and {@link OperatorInput} entities,
   * the {@link LineInput} instances depend on. Doing so, already loaded nodes, line types and
   * operators can be recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param lineTypeInputs a set of object and uuid unique {@link LineTypeInput} entities
   * @return a set of object and uuid unique {@link LineInput} entities
   */
  public Set<LineInput> getLines(
      Set<NodeInput> nodes, Set<LineTypeInput> lineTypeInputs, Set<OperatorInput> operators) {
    return dataSource.buildTypedEntities(LineInput.class, lineInputFactory, nodes, operators, lineTypeInputs);
  }

  /**
   * Returns a unique set of {@link Transformer2WInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link Transformer2WInput} which has to be checked
   * manually, as {@link Transformer2WInput#equals(Object)} is NOT restricted on the uuid of {@link
   * Transformer2WInput}.
   *
   * @return a set of object and uuid unique {@link Transformer2WInput} entities
   */
  public Set<Transformer2WInput> get2WTransformers() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return get2WTransformers(getNodes(operators), typeSource.getTransformer2WTypes(), operators);
  }

  /**
   * Returns a set of {@link Transformer2WInput} instances. This set has to be unique in the sense
   * of object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link Transformer2WInput} which has to be checked manually, as {@link
   * Transformer2WInput#equals(Object)} is NOT restricted on the uuid of {@link Transformer2WInput}.
   *
   * <p>In contrast to {@link #getNodes()} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link Transformer2WTypeInput} and {@link OperatorInput}
   * entities, the {@link Transformer2WInput} instances depend on. Doing so, already loaded nodes,
   * line types and operators can be recycled to improve performance and prevent unnecessary loading
   * operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param transformer2WTypes a set of object and uuid unique {@link Transformer2WTypeInput}
   *     entities
   * @return a set of object and uuid unique {@link Transformer2WInput} entities
   */
  public Set<Transformer2WInput> get2WTransformers(
      Set<NodeInput> nodes,
      Set<Transformer2WTypeInput> transformer2WTypes,
      Set<OperatorInput> operators) {
    return dataSource.buildTypedEntities(
            Transformer2WInput.class,
            transformer2WInputFactory,
            nodes,
            operators,
            transformer2WTypes);
  }

  /**
   * Returns a unique set of {@link Transformer3WInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link Transformer3WInput} which has to be checked
   * manually, as {@link Transformer3WInput#equals(Object)} is NOT restricted on the uuid of {@link
   * Transformer3WInput}.
   *
   * @return a set of object and uuid unique {@link Transformer3WInput} entities
   */
  public Set<Transformer3WInput> get3WTransformers() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return get3WTransformers(getNodes(operators), typeSource.getTransformer3WTypes(), operators);
  }

  /**
   * Returns a set of {@link Transformer3WInput} instances. This set has to be unique in the sense
   * of object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link Transformer3WInput} which has to be checked manually, as {@link
   * Transformer3WInput#equals(Object)} is NOT restricted on the uuid of {@link Transformer3WInput}.
   *
   * <p>In contrast to {@link #getNodes()} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link Transformer3WTypeInput} and {@link OperatorInput}
   * entities, the {@link Transformer3WInput} instances depend on. Doing so, already loaded nodes,
   * line types and operators can be recycled to improve performance and prevent unnecessary loading
   * operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param transformer3WTypeInputs a set of object and uuid unique {@link Transformer3WTypeInput}
   *     entities
   * @return a set of object and uuid unique {@link Transformer3WInput} entities
   */
  public Set<Transformer3WInput> get3WTransformers(
      Set<NodeInput> nodes,
      Set<Transformer3WTypeInput> transformer3WTypeInputs,
      Set<OperatorInput> operators) {
    return dataSource.buildTransformer3WEntities(transformer3WInputFactory, nodes, transformer3WTypeInputs, operators);
  }

  /**
   * Returns a unique set of {@link SwitchInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link SwitchInput} which has to be checked
   * manually, as {@link SwitchInput#equals(Object)} is NOT restricted on the uuid of {@link
   * SwitchInput}.
   *
   * @return a set of object and uuid unique {@link SwitchInput} entities
   */
  public Set<SwitchInput> getSwitches() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getSwitches(getNodes(operators), operators);
  }

  /**
   * Returns a set of {@link SwitchInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link SwitchInput} which has to be checked manually, as {@link SwitchInput#equals(Object)} is
   * NOT restricted on the uuid of {@link SwitchInput}.
   *
   * <p>In contrast to {@link #getNodes()} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link SwitchInput}
   * instances depend on. Doing so, already loaded nodes, line types and operators can be recycled
   * to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link SwitchInput} entities
   */
  public Set<SwitchInput> getSwitches(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return dataSource.buildUntypedConnectorInputEntities(SwitchInput.class, switchInputFactory, nodes, operators);
  }

  /**
   * Returns a unique set of {@link MeasurementUnitInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link MeasurementUnitInput} which has to be checked
   * manually, as {@link MeasurementUnitInput#equals(Object)} is NOT restricted on the uuid of
   * {@link MeasurementUnitInput}.
   *
   * @return a set of object and uuid unique {@link MeasurementUnitInput} entities
   */
  public Set<MeasurementUnitInput> getMeasurementUnits() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getMeasurementUnits(getNodes(operators), operators);
  }

  /**
   * Returns a set of {@link MeasurementUnitInput} instances. This set has to be unique in the sense
   * of object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link MeasurementUnitInput} which has to be checked manually, as {@link
   * MeasurementUnitInput#equals(Object)} is NOT restricted on the uuid of {@link
   * MeasurementUnitInput}.
   *
   * <p>In contrast to {@link #getNodes()} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link
   * MeasurementUnitInput} instances depend on. Doing so, already loaded nodes, line types and
   * operators can be recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link MeasurementUnitInput} entities
   */
  public Set<MeasurementUnitInput> getMeasurementUnits(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return dataSource.buildNodeAssetEntities(MeasurementUnitInput.class, measurementUnitInputFactory, nodes, operators);
  }



  //-------------------------------------
  protected Stream<Optional<ConnectorInputEntityData>> buildUntypedConnectorInputEntityData(
          Stream<AssetInputEntityData> assetInputEntityDataStream, Collection<NodeInput> nodes) {
    return assetInputEntityDataStream
            .parallel()
            .map(
                    assetInputEntityData ->
                            buildUntypedConnectorInputEntityData(assetInputEntityData, nodes));
  }


  protected Optional<ConnectorInputEntityData> buildUntypedConnectorInputEntityData(
          AssetInputEntityData assetInputEntityData, Collection<NodeInput> nodes) {
    return dataSource.buildUntypedConnectorInputEntityData(assetInputEntityData, nodes);
  }

  protected <T extends AssetTypeInput> TypedConnectorInputEntityData<T> addTypeToEntityData(
          ConnectorInputEntityData untypedEntityData, T assetType) {
    return dataSource.addTypeToEntityData(untypedEntityData, assetType);
  }

  protected <T extends AssetTypeInput> Optional<TypedConnectorInputEntityData<T>> findAndAddType(
          ConnectorInputEntityData untypedEntityData, Collection<T> availableTypes) {
    return dataSource.findAndAddType(untypedEntityData, availableTypes);
  }

  protected <T extends AssetTypeInput>
  Stream<Optional<TypedConnectorInputEntityData<T>>> buildTypedConnectorEntityData(
          Stream<Optional<ConnectorInputEntityData>> noTypeConnectorEntityDataStream,
          Collection<T> availableTypes) {
    return dataSource.buildTypedConnectorEntityData(noTypeConnectorEntityDataStream, availableTypes);
  }

  protected Optional<Transformer3WInputEntityData> addThirdNode(
          TypedConnectorInputEntityData<Transformer3WTypeInput> typeEntityData,
          Collection<NodeInput> nodes) {
    return dataSource.addThirdNode(typeEntityData, nodes);
  }

  protected Stream<Optional<Transformer3WInputEntityData>> buildTransformer3WEntityData(
          Stream<Optional<TypedConnectorInputEntityData<Transformer3WTypeInput>>> typedConnectorEntityDataStream,
          Collection<NodeInput> nodes) {
    return dataSource.buildTransformer3WEntityData(typedConnectorEntityDataStream, nodes);
  }
}
