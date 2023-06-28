/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.RawGridException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.utils.Try;
import java.util.*;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation that provides the capability to build entities that are hold by a {@link
 * RawGridElements} as well as the {@link RawGridElements} container as well from different data
 * sources e.g. .csv files or databases.
 *
 * @version 0.1
 * @since 08.04.20
 */
public class RawGridSource extends EntitySource {

  // field names
  protected static final String NODE_A = "nodeA";
  protected static final String NODE_B = "nodeB";
  protected static final String TYPE = "type";

  // general fields
  private final TypeSource typeSource;

  // factories
  private final NodeInputFactory nodeInputFactory;
  private final LineInputFactory lineInputFactory;
  private final Transformer2WInputFactory transformer2WInputFactory;
  private final Transformer3WInputFactory transformer3WInputFactory;
  private final SwitchInputFactory switchInputFactory;
  private final MeasurementUnitInputFactory measurementUnitInputFactory;

  public RawGridSource(TypeSource typeSource, DataSource dataSource) {
    this.typeSource = typeSource;
    this.dataSource = dataSource;

    // init factories
    this.nodeInputFactory = new NodeInputFactory();
    this.lineInputFactory = new LineInputFactory();
    this.transformer2WInputFactory = new Transformer2WInputFactory();
    this.transformer3WInputFactory = new Transformer3WInputFactory();
    this.switchInputFactory = new SwitchInputFactory();
    this.measurementUnitInputFactory = new MeasurementUnitInputFactory();
  }

  /**
   * Should return either a consistent instance of {@link RawGridElements} or throw a {@link
   * SourceException}. The decision to throw a {@link SourceException} instead of returning the
   * incomplete {@link RawGridElements} instance is motivated by the fact, that a {@link
   * RawGridElements} is a container instance that depends on several other entities. Without being
   * complete, it is useless for further processing.
   *
   * <p>Hence, whenever at least one entity {@link RawGridElements} depends on cannot be provided,
   * {@link SourceException} should be thrown. The thrown exception should provide enough
   * information to debug the error and fix the persistent data that has been failed to processed.
   *
   * <p>Furthermore, it is expected, that the specific implementation of this method ensures not
   * only the completeness of the resulting {@link RawGridElements} instance, but also its validity
   * e.g. in the sense that not duplicate UUIDs exist within all entities contained in the returning
   * instance.
   *
   * @return either a valid, complete {@link RawGridElements} or throws a {@link SourceException}
   */
  public RawGridElements getGridData() throws RawGridException, SourceException {
    /* read all needed entities start with the types and operators */
    Set<OperatorInput> operators = typeSource.getOperators();
    Set<LineTypeInput> lineTypes = typeSource.getLineTypes();
    Set<Transformer2WTypeInput> transformer2WTypeInputs = typeSource.getTransformer2WTypes();
    Set<Transformer3WTypeInput> transformer3WTypeInputs = typeSource.getTransformer3WTypes();

    /* assets */
    Set<NodeInput> nodes = getNodes(operators);
    Try<Set<LineInput>> lineInputs = Try.of(() -> getLines(nodes, lineTypes, operators));
    Try<Set<Transformer2WInput>> transformer2WInputs =
        Try.of(() -> get2WTransformers(nodes, transformer2WTypeInputs, operators));
    Try<Set<Transformer3WInput>> transformer3WInputs =
        Try.of(() -> get3WTransformers(nodes, transformer3WTypeInputs, operators));
    Try<Set<SwitchInput>> switches = Try.of(() -> getSwitches(nodes, operators));
    Try<Set<MeasurementUnitInput>> measurementUnits =
        Try.of(() -> getMeasurementUnits(nodes, operators));

    List<SourceException> exceptions =
        (List<SourceException>)
            Try.getExceptions(
                List.of(
                    lineInputs,
                    transformer2WInputs,
                    transformer3WInputs,
                    switches,
                    measurementUnits));

    if (!exceptions.isEmpty()) {
      throw new RawGridException(
          exceptions.size() + " error(s) occurred while initializing raw grid. ", exceptions);
    } else {
      /* build and return the grid if it is not empty */
      // getOrThrow should not throw an exception in this context, because all exception are
      // filtered and thrown before
      return new RawGridElements(
          nodes,
          lineInputs.getOrThrow(),
          transformer2WInputs.getOrThrow(),
          transformer3WInputs.getOrThrow(),
          switches.getOrThrow(),
          measurementUnits.getOrThrow());
    }
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
  public Set<NodeInput> getNodes() throws SourceException {
    return getNodes(typeSource.getOperators());
  }

  /**
   * Returns a set of {@link NodeInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * NodeInput} which has to be checked manually, as {@link NodeInput#equals(Object)} is NOT
   * restricted on the uuid of {@link NodeInput}.
   *
   * <p>In contrast to {@link #getNodes} this method provides the ability to pass in an already
   * existing set of {@link OperatorInput} entities, the {@link NodeInput} instances depend on.
   * Doing so, already loaded nodes can be recycled to improve performance and prevent unnecessary
   * loading operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @return a set of object and uuid unique {@link NodeInput} entities
   */
  public Set<NodeInput> getNodes(Set<OperatorInput> operators) throws SourceException {
    return Try.scanCollection(
            assetInputEntityDataStream(NodeInput.class, operators)
                .map(nodeInputFactory::get)
                .collect(Collectors.toSet()),
            NodeInput.class)
        .getOrThrow(SourceException.class);
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
  public Set<LineInput> getLines() throws SourceException {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getLines(getNodes(operators), typeSource.getLineTypes(), operators);
  }

  /**
   * Returns a set of {@link LineInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * LineInput} which has to be checked manually, as {@link LineInput#equals(Object)} is NOT
   * restricted on the uuid of {@link LineInput}.
   *
   * <p>In contrast to {@link #getNodes} this method provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link LineTypeInput} and {@link OperatorInput} entities,
   * the {@link LineInput} instances depend on. Doing so, already loaded nodes, line types and
   * operators can be recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param lineTypeInputs a set of object and uuid unique {@link LineTypeInput} entities
   * @return a set of object and uuid unique {@link LineInput} entities
   */
  public Set<LineInput> getLines(
      Set<NodeInput> nodes, Set<LineTypeInput> lineTypeInputs, Set<OperatorInput> operators)
      throws SourceException {
    return Try.scanCollection(
            typedEntityStream(LineInput.class, lineInputFactory, nodes, operators, lineTypeInputs)
                .collect(Collectors.toSet()),
            LineInput.class)
        .getOrThrow(SourceException.class);
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
  public Set<Transformer2WInput> get2WTransformers() throws SourceException {
    Set<OperatorInput> operators = typeSource.getOperators();
    return get2WTransformers(getNodes(operators), typeSource.getTransformer2WTypes(), operators);
  }

  /**
   * Returns a set of {@link Transformer2WInput} instances. This set has to be unique in the sense
   * of object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link Transformer2WInput} which has to be checked manually, as {@link
   * Transformer2WInput#equals(Object)} is NOT restricted on the uuid of {@link Transformer2WInput}.
   *
   * <p>In contrast to {@link #getNodes()} this method provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link Transformer2WTypeInput} and {@link OperatorInput}
   * entities, the {@link Transformer2WInput} instances depend on. Doing so, already loaded nodes,
   * line types and operators can be recycled to improve performance and prevent unnecessary loading
   * operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
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
      Set<OperatorInput> operators)
      throws SourceException {
    return Try.scanCollection(
            typedEntityStream(
                    Transformer2WInput.class,
                    transformer2WInputFactory,
                    nodes,
                    operators,
                    transformer2WTypes)
                .collect(Collectors.toSet()),
            Transformer2WInput.class)
        .getOrThrow(SourceException.class);
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
  public Set<Transformer3WInput> get3WTransformers() throws SourceException {
    Set<OperatorInput> operators = typeSource.getOperators();
    return get3WTransformers(getNodes(operators), typeSource.getTransformer3WTypes(), operators);
  }

  /**
   * Returns a set of {@link Transformer3WInput} instances. This set has to be unique in the sense
   * of object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link Transformer3WInput} which has to be checked manually, as {@link
   * Transformer3WInput#equals(Object)} is NOT restricted on the uuid of {@link Transformer3WInput}.
   *
   * <p>In contrast to {@link #getNodes()} this method provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link Transformer3WTypeInput} and {@link OperatorInput}
   * entities, the {@link Transformer3WInput} instances depend on. Doing so, already loaded nodes,
   * line types and operators can be recycled to improve performance and prevent unnecessary loading
   * operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
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
      Set<OperatorInput> operators)
      throws SourceException {
    return Try.scanCollection(
            buildTransformer3WEntities(
                transformer3WInputFactory, nodes, transformer3WTypeInputs, operators),
            Transformer3WInput.class)
        .getOrThrow(SourceException.class);
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
  public Set<SwitchInput> getSwitches() throws SourceException {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getSwitches(getNodes(operators), operators);
  }

  /**
   * Returns a set of {@link SwitchInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link SwitchInput} which has to be checked manually, as {@link SwitchInput#equals(Object)} is
   * NOT restricted on the uuid of {@link SwitchInput}.
   *
   * <p>In contrast to {@link #getNodes()} this method provides the ability to pass in an already
   * existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link SwitchInput}
   * instances depend on. Doing so, already loaded nodes, line types and operators can be recycled
   * to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link SwitchInput} entities
   */
  public Set<SwitchInput> getSwitches(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return buildUntypedConnectorInputEntities(
        SwitchInput.class, switchInputFactory, nodes, operators);
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
  public Set<MeasurementUnitInput> getMeasurementUnits() throws SourceException {
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
   * <p>In contrast to {@link #getNodes()} this method provides the ability to pass in an already
   * existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link
   * MeasurementUnitInput} instances depend on. Doing so, already loaded nodes, line types and
   * operators can be recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link MeasurementUnitInput} entities
   */
  public Set<MeasurementUnitInput> getMeasurementUnits(
      Set<NodeInput> nodes, Set<OperatorInput> operators) throws SourceException {
    return Try.scanCollection(
            buildNodeAssetEntities(
                MeasurementUnitInput.class, measurementUnitInputFactory, nodes, operators),
            MeasurementUnitInput.class)
        .getOrThrow(SourceException.class);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
  public <T extends AssetInput> Set<Try<T>> buildNodeInputEntities(
      Class<T> entityClass,
      EntityFactory<T, AssetInputEntityData> factory,
      Collection<OperatorInput> operators) {
    return assetInputEntityDataStream(entityClass, operators)
        .map(factory::get)
        .collect(Collectors.toSet());
  }

  public <T extends ConnectorInput> Set<T> buildUntypedConnectorInputEntities(
      Class<T> entityClass,
      EntityFactory<T, ConnectorInputEntityData> factory,
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators) {
    return untypedConnectorInputEntityStream(entityClass, factory, nodes, operators)
        .collect(Collectors.toSet());
  }

  public Set<Try<Transformer3WInput>> buildTransformer3WEntities(
      Transformer3WInputFactory transformer3WInputFactory,
      Collection<NodeInput> nodes,
      Collection<Transformer3WTypeInput> transformer3WTypeInputs,
      Collection<OperatorInput> operators) {
    return buildTransformer3WEntityData(
            buildTypedConnectorEntityData(
                buildUntypedConnectorInputEntityData(
                    assetInputEntityDataStream(Transformer3WInput.class, operators), nodes),
                transformer3WTypeInputs),
            nodes)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(transformer3WInputFactory::get)
        .collect(Collectors.toSet());
  }

  public <T extends ConnectorInput, A extends AssetTypeInput> Set<Try<T>> buildTypedEntities(
      Class<T> entityClass,
      EntityFactory<T, TypedConnectorInputEntityData<A>> factory,
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<A> types) {
    return typedEntityStream(entityClass, factory, nodes, operators, types)
        .collect(Collectors.toSet());
  }

  /**
   * Enriches the given untyped entity data with the equivalent asset type. If this is not possible,
   * an empty Optional is returned
   *
   * @param noTypeConnectorEntityDataStream Stream of untyped entity data
   * @param availableTypes Yet available asset types
   * @param <T> Type of the asset type
   * @return Stream of option to enhanced data
   */
  protected <T extends AssetTypeInput>
      Stream<Optional<TypedConnectorInputEntityData<T>>> buildTypedConnectorEntityData(
          Stream<Optional<ConnectorInputEntityData>> noTypeConnectorEntityDataStream,
          Collection<T> availableTypes) {
    return noTypeConnectorEntityDataStream
        .parallel()
        .map(
            noTypeEntityDataOpt ->
                noTypeEntityDataOpt.flatMap(
                    noTypeEntityData -> findAndAddType(noTypeEntityData, availableTypes)));
  }

  /**
   * Converts a stream of {@link AssetInputEntityData} in connection with a collection of known
   * {@link NodeInput}s to a stream of {@link ConnectorInputEntityData}.
   *
   * @param assetInputEntityDataStream Input stream of {@link AssetInputEntityData}
   * @param nodes A collection of known nodes
   * @return A stream on option to matching {@link ConnectorInputEntityData}
   */
  protected Stream<Optional<ConnectorInputEntityData>> buildUntypedConnectorInputEntityData(
      Stream<AssetInputEntityData> assetInputEntityDataStream, Collection<NodeInput> nodes) {
    return assetInputEntityDataStream
        .parallel()
        .map(
            assetInputEntityData ->
                buildUntypedConnectorInputEntityData(assetInputEntityData, nodes));
  }

  /**
   * Converts a single given {@link AssetInputEntityData} in connection with a collection of known
   * {@link NodeInput}s to {@link ConnectorInputEntityData}. If this is not possible, an empty
   * option is given back.
   *
   * @param assetInputEntityData Input entity data to convert
   * @param nodes A collection of known nodes
   * @return An option to matching {@link ConnectorInputEntityData}
   */
  protected Optional<ConnectorInputEntityData> buildUntypedConnectorInputEntityData(
      AssetInputEntityData assetInputEntityData, Collection<NodeInput> nodes) {
    // get the raw data
    Map<String, String> fieldsToAttributes = assetInputEntityData.getFieldsToValues();

    // get the two connector nodes
    String nodeAUuid = fieldsToAttributes.get(NODE_A);
    String nodeBUuid = fieldsToAttributes.get(NODE_B);
    Optional<NodeInput> nodeA = findFirstEntityByUuid(nodeAUuid, nodes);
    Optional<NodeInput> nodeB = findFirstEntityByUuid(nodeBUuid, nodes);

    // if nodeA or nodeB are not present we return an empty element and log a
    // warning
    if (nodeA.isEmpty() || nodeB.isEmpty()) {
      String debugString =
          Stream.of(
                  new AbstractMap.SimpleEntry<>(nodeA, NODE_A + ": " + nodeAUuid),
                  new AbstractMap.SimpleEntry<>(nodeB, NODE_B + ": " + nodeBUuid))
              .filter(entry -> entry.getKey().isEmpty())
              .map(AbstractMap.SimpleEntry::getValue)
              .collect(Collectors.joining("\n"));

      logSkippingWarning(
          assetInputEntityData.getTargetClass().getSimpleName(),
          fieldsToAttributes.get("uuid"),
          fieldsToAttributes.get("id"),
          debugString);
      return Optional.empty();
    }

    // remove fields that are passed as objects to constructor
    fieldsToAttributes.keySet().removeAll(new HashSet<>(Arrays.asList(NODE_A, NODE_B)));

    return Optional.of(
        new ConnectorInputEntityData(
            fieldsToAttributes,
            assetInputEntityData.getTargetClass(),
            assetInputEntityData.getOperatorInput(),
            nodeA.get(),
            nodeB.get()));
  }

  private <T extends ConnectorInput, A extends AssetTypeInput> Stream<Try<T>> typedEntityStream(
      Class<T> entityClass,
      EntityFactory<T, TypedConnectorInputEntityData<A>> factory,
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<A> types) {
    return buildTypedConnectorEntityData(
            buildUntypedConnectorInputEntityData(
                assetInputEntityDataStream(entityClass, operators), nodes),
            types)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(factory::get);
  }

  public <T extends ConnectorInput> Stream<T> untypedConnectorInputEntityStream(
      Class<T> entityClass,
      EntityFactory<T, ConnectorInputEntityData> factory,
      Set<NodeInput> nodes,
      Set<OperatorInput> operators) {
    return buildUntypedConnectorInputEntityData(
            assetInputEntityDataStream(entityClass, operators), nodes)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(factory::get)
        .map(Try::getOrThrow);
  }

  private <T extends ConnectorInput> Stream<T> untypedConnectorInputEntityStream(
      Class<T> entityClass,
      EntityFactory<T, ConnectorInputEntityData> factory,
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators) {
    return untypedConnectorInputEntityStream(
        entityClass, factory, new HashSet<>(nodes), new HashSet<>(operators));
  }

  /**
   * Enriches the Stream of options on {@link Transformer3WInputEntityData} with the information of
   * the internal node
   *
   * @param typedConnectorEntityDataStream Stream of already typed input entity data
   * @param nodes Yet available nodes
   * @return A stream of options on enriched data
   */
  protected Stream<Optional<Transformer3WInputEntityData>> buildTransformer3WEntityData(
      Stream<Optional<TypedConnectorInputEntityData<Transformer3WTypeInput>>>
          typedConnectorEntityDataStream,
      Collection<NodeInput> nodes) {
    return typedConnectorEntityDataStream
        .parallel()
        .map(
            typedEntityDataOpt ->
                typedEntityDataOpt.flatMap(typeEntityData -> addThirdNode(typeEntityData, nodes)));
  }

  /**
   * Enriches the third node to the already typed entity data of a three winding transformer. If no
   * matching node can be found, return an empty Optional.
   *
   * @param typeEntityData Already typed entity data
   * @param nodes Yet available nodes
   * @return An option to the enriched data
   */
  protected Optional<Transformer3WInputEntityData> addThirdNode(
      TypedConnectorInputEntityData<Transformer3WTypeInput> typeEntityData,
      Collection<NodeInput> nodes) {

    // get the raw data
    Map<String, String> fieldsToAttributes = typeEntityData.getFieldsToValues();

    // get nodeC of the transformer
    String nodeCUuid = fieldsToAttributes.get("nodeC");
    Optional<NodeInput> nodeC = findFirstEntityByUuid(nodeCUuid, nodes);

    // if nodeC is not present we return an empty element and
    // log a warning
    if (nodeC.isEmpty()) {
      logSkippingWarning(
          typeEntityData.getTargetClass().getSimpleName(),
          fieldsToAttributes.get("uuid"),
          fieldsToAttributes.get("id"),
          "nodeC: " + nodeCUuid);
      return Optional.empty();
    }

    // remove fields that are passed as objects to constructor
    fieldsToAttributes.keySet().remove("nodeC");

    return Optional.of(
        new Transformer3WInputEntityData(
            fieldsToAttributes,
            typeEntityData.getTargetClass(),
            typeEntityData.getOperatorInput(),
            typeEntityData.getNodeA(),
            typeEntityData.getNodeB(),
            nodeC.get(),
            typeEntityData.getType()));
  }
}
