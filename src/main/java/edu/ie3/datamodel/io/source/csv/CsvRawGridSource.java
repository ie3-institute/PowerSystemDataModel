/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.RawGridException;
import edu.ie3.datamodel.exceptions.RawInputDataException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.FactoryData;
import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.RawGridSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.utils.options.Try;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Source that provides the capability to build entities that are hold by a {@link RawGridElements}
 * as well as the {@link RawGridElements} container from .csv files.
 *
 * <p>This source is <b>not buffered</b> which means each call on a getter method always tries to
 * read all data is necessary to return the requested objects in a hierarchical cascading way.
 *
 * <p>If performance is an issue, it is recommended to read the data cascading starting with reading
 * nodes and then using the getters with arguments to avoid reading the same data multiple times.
 *
 * <p>The resulting sets are always unique on object <b>and</b> UUID base (with distinct UUIDs).
 *
 * @version 0.1
 * @since 03.04.20
 */
public class CsvRawGridSource extends CsvDataSource implements RawGridSource {

  // general fields
  private final TypeSource typeSource;

  // factories
  private final NodeInputFactory nodeInputFactory;
  private final LineInputFactory lineInputFactory;
  private final Transformer2WInputFactory transformer2WInputFactory;
  private final Transformer3WInputFactory transformer3WInputFactory;
  private final SwitchInputFactory switchInputFactory;
  private final MeasurementUnitInputFactory measurementUnitInputFactory;

  public CsvRawGridSource(
      String csvSep,
      String gridFolderPath,
      FileNamingStrategy fileNamingStrategy,
      TypeSource typeSource) {
    super(csvSep, gridFolderPath, fileNamingStrategy);
    this.typeSource = typeSource;

    // init factories
    this.nodeInputFactory = new NodeInputFactory();
    this.lineInputFactory = new LineInputFactory();
    this.transformer2WInputFactory = new Transformer2WInputFactory();
    this.transformer3WInputFactory = new Transformer3WInputFactory();
    this.switchInputFactory = new SwitchInputFactory();
    this.measurementUnitInputFactory = new MeasurementUnitInputFactory();
  }

  /** {@inheritDoc} */
  @Override
  public RawGridElements getGridData() throws SourceException {

    /* read all needed entities start with the types and operators */
    Set<OperatorInput> operators = typeSource.getOperators();
    Set<LineTypeInput> lineTypes = typeSource.getLineTypes();
    Set<Transformer2WTypeInput> transformer2WTypeInputs = typeSource.getTransformer2WTypes();
    Set<Transformer3WTypeInput> transformer3WTypeInputs = typeSource.getTransformer3WTypes();

    /* assets */
    Set<NodeInput> nodes = getNodes(operators);

    Try<Set<LineInput>, RawInputDataException> lineInputs =
        Try.apply(() -> getLines(nodes, lineTypes, operators), RawInputDataException.class);
    Try<Set<Transformer2WInput>, RawInputDataException> transformer2WInputs =
        Try.apply(
            () -> get2WTransformers(nodes, transformer2WTypeInputs, operators),
            RawInputDataException.class);
    Try<Set<Transformer3WInput>, RawInputDataException> transformer3WInputs =
        Try.apply(
            () -> get3WTransformers(nodes, transformer3WTypeInputs, operators),
            RawInputDataException.class);
    Try<Set<SwitchInput>, RawInputDataException> switches =
        Try.apply(() -> getSwitches(nodes, operators), RawInputDataException.class);
    Try<Set<MeasurementUnitInput>, RawInputDataException> measurementUnits =
        Try.apply(() -> getMeasurementUnits(nodes, operators), RawInputDataException.class);

    List<RawInputDataException> exceptions =
        Stream.of(lineInputs, transformer2WInputs, transformer3WInputs, switches, measurementUnits)
            .filter(Try::isFailure)
            .map(Try::getException)
            .toList();

    if (exceptions.size() > 0) {
      throw new RawGridException(
          exceptions.size() + " error(s) occurred while initializing raw grid. ", exceptions);
    } else {
      /* build and return the grid if it is not empty */
      return new RawGridElements(
          nodes,
          lineInputs.getData(),
          transformer2WInputs.getData(),
          transformer3WInputs.getData(),
          switches.getData(),
          measurementUnits.getData());
    }
  }

  /** {@inheritDoc} */
  @Override
  public Set<NodeInput> getNodes() throws SourceException {
    return getNodes(typeSource.getOperators());
  }

  /**
   * {@inheritDoc}
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<NodeInput> getNodes(Set<OperatorInput> operators) throws SourceException {
    return Try.scanForExceptions(
            assetInputEntityDataStream(NodeInput.class, operators)
                .map(nodeInputFactory::get)
                .collect(Collectors.toSet()),
            NodeInput.class)
        .get();
  }

  /** {@inheritDoc} */
  @Override
  public Set<LineInput> getLines() throws SourceException {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getLines(getNodes(operators), typeSource.getLineTypes(), operators);
  }

  /**
   * {@inheritDoc}
   *
   * <p>If one of the sets of {@link NodeInput} or {@link LineTypeInput} entities is not exhaustive
   * for all available {@link LineInput} entities (e.g. a {@link NodeInput} or {@link LineTypeInput}
   * entity is missing) or if an error during the building process occurs, the entity that misses
   * something will be skipped (which can be seen as a filtering functionality) but all entities
   * that are able to be built will be returned anyway and the elements that couldn't have been
   * built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<LineInput> getLines(
      Set<NodeInput> nodes, Set<LineTypeInput> lineTypeInputs, Set<OperatorInput> operators)
      throws SourceException {
    return Try.scanForExceptions(
            typedEntityStream(LineInput.class, lineInputFactory, nodes, operators, lineTypeInputs)
                .collect(Collectors.toSet()),
            LineInput.class)
        .get();
  }

  /** {@inheritDoc} */
  @Override
  public Set<Transformer2WInput> get2WTransformers() throws SourceException {
    Set<OperatorInput> operators = typeSource.getOperators();
    return get2WTransformers(getNodes(operators), typeSource.getTransformer2WTypes(), operators);
  }

  /**
   * {@inheritDoc}
   *
   * <p>If one of the sets of {@link NodeInput} or {@link Transformer2WTypeInput} entities is not
   * exhaustive for all available {@link Transformer2WInput} entities (e.g. a {@link NodeInput} or
   * {@link Transformer2WTypeInput} entity is missing) or if an error during the building process
   * occurs, the entity that misses something will be skipped (which can be seen as a filtering
   * functionality) but all entities that are able to be built will be returned anyway and the
   * elements that couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<Transformer2WInput> get2WTransformers(
      Set<NodeInput> nodes,
      Set<Transformer2WTypeInput> transformer2WTypes,
      Set<OperatorInput> operators)
      throws SourceException {
    return Try.scanForExceptions(
            typedEntityStream(
                    Transformer2WInput.class,
                    transformer2WInputFactory,
                    nodes,
                    operators,
                    transformer2WTypes)
                .collect(Collectors.toSet()),
            Transformer2WInput.class)
        .get();
  }

  /** {@inheritDoc} */
  @Override
  public Set<Transformer3WInput> get3WTransformers() throws SourceException {
    Set<OperatorInput> operators = typeSource.getOperators();
    return get3WTransformers(getNodes(operators), typeSource.getTransformer3WTypes(), operators);
  }

  /**
   * {@inheritDoc}
   *
   * <p>If one of the sets of {@link NodeInput} or {@link Transformer3WTypeInput} entities is not
   * exhaustive for all available {@link Transformer3WInput} entities (e.g. a {@link NodeInput} or
   * {@link Transformer3WTypeInput} entity is missing) or if an error during the building process
   * occurs, the entity that misses something will be skipped (which can be seen as a filtering
   * functionality) but all entities that are able to be built will be returned anyway and the
   * elements that couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<Transformer3WInput> get3WTransformers(
      Set<NodeInput> nodes,
      Set<Transformer3WTypeInput> transformer3WTypeInputs,
      Set<OperatorInput> operators)
      throws SourceException {
    return Try.scanForExceptions(
            transformer3WEntityStream(nodes, transformer3WTypeInputs, operators)
                .collect(Collectors.toSet()),
            Transformer3WInput.class)
        .get();
  }

  private Stream<Try<Transformer3WInput, FactoryException>> transformer3WEntityStream(
      Set<NodeInput> nodes,
      Set<Transformer3WTypeInput> transformer3WTypeInputs,
      Set<OperatorInput> operators) {

    return buildTransformer3WEntityData(
            buildTypedConnectorEntityData(
                buildUntypedConnectorInputEntityData(
                    assetInputEntityDataStream(Transformer3WInput.class, operators), nodes),
                transformer3WTypeInputs),
            nodes)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(transformer3WInputFactory::get);
  }

  /** {@inheritDoc} */
  @Override
  public Set<SwitchInput> getSwitches() throws SourceException {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getSwitches(getNodes(operators), operators);
  }

  /**
   * {@inheritDoc}
   *
   * <p>If one of the sets of {@link NodeInput} entities is not exhaustive for all available {@link
   * SwitchInput} entities (e.g. a {@link NodeInput} entity is missing) or if an error during the
   * building process occurs, the entity that misses something will be skipped (which can be seen as
   * a filtering functionality) but all entities that are able to be built will be returned anyway
   * and the elements that couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<SwitchInput> getSwitches(Set<NodeInput> nodes, Set<OperatorInput> operators)
      throws SourceException {
    return Try.scanForExceptions(
            untypedConnectorInputEntityStream(
                    SwitchInput.class, switchInputFactory, nodes, operators)
                .collect(Collectors.toSet()),
            SwitchInput.class)
        .get();
  }

  private <T extends ConnectorInput>
      Stream<Try<T, FactoryException>> untypedConnectorInputEntityStream(
          Class<T> entityClass,
          EntityFactory<T, ConnectorInputEntityData> factory,
          Set<NodeInput> nodes,
          Set<OperatorInput> operators) {

    return buildUntypedConnectorInputEntityData(
            assetInputEntityDataStream(entityClass, operators), nodes)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(factory::get);
  }

  /** {@inheritDoc} */
  @Override
  public Set<MeasurementUnitInput> getMeasurementUnits() throws SourceException {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getMeasurementUnits(getNodes(operators), operators);
  }

  /**
   * {@inheritDoc}
   *
   * <p>If one of the sets of {@link NodeInput} entities is not exhaustive for all available {@link
   * MeasurementUnitInput} entities (e.g. a {@link NodeInput} entity is missing) or if an error
   * during the building process occurs, the entity that misses something will be skipped (which can
   * be seen as a filtering functionality) but all entities that are able to be built will be
   * returned anyway and the elements that couldn't have been built are logged.
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<MeasurementUnitInput> getMeasurementUnits(
      Set<NodeInput> nodes, Set<OperatorInput> operators) throws SourceException {
    return Try.scanForExceptions(
            nodeAssetEntityStream(
                    MeasurementUnitInput.class, measurementUnitInputFactory, nodes, operators)
                .collect(Collectors.toSet()),
            MeasurementUnitInput.class)
        .get();
  }

  private <T extends ConnectorInput, A extends AssetTypeInput>
      Stream<Try<T, FactoryException>> typedEntityStream(
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

  /**
   * Converts a stream of {@link AssetInputEntityData} in connection with a collection of known
   * {@link NodeInput}s to a stream of {@link ConnectorInputEntityData}.
   *
   * @param assetInputEntityDataStream Input stream of {@link AssetInputEntityData}
   * @param nodes A collection of known nodes
   * @return A stream on option to matching {@link ConnectorInputEntityData}
   */
  private Stream<Optional<ConnectorInputEntityData>> buildUntypedConnectorInputEntityData(
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
  private Optional<ConnectorInputEntityData> buildUntypedConnectorInputEntityData(
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
            new FactoryData.MapWithRowIndex(assetInputEntityData.getRowIndex(), fieldsToAttributes),
            assetInputEntityData.getTargetClass(),
            assetInputEntityData.getOperatorInput(),
            nodeA.get(),
            nodeB.get()));
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
  private <T extends AssetTypeInput>
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
   * Finds the required asset type and if present, adds it to the untyped entity data
   *
   * @param untypedEntityData Untyped entity data to enrich
   * @param availableTypes Yet available asset types
   * @param <T> Type of the asset type
   * @return Option to enhanced data
   */
  private <T extends AssetTypeInput> Optional<TypedConnectorInputEntityData<T>> findAndAddType(
      ConnectorInputEntityData untypedEntityData, Collection<T> availableTypes) {
    Optional<T> assetTypeOption =
        getAssetType(
            availableTypes,
            untypedEntityData.getFieldsToValues(),
            untypedEntityData.getClass().getSimpleName());
    return assetTypeOption.map(assetType -> addTypeToEntityData(untypedEntityData, assetType));
  }

  /**
   * Enriches the given, untyped entity data with the provided asset type
   *
   * @param untypedEntityData Untyped entity data to enrich
   * @param assetType Asset type to add
   * @param <T> Type of the asset type
   * @return The enriched entity data
   */
  private <T extends AssetTypeInput> TypedConnectorInputEntityData<T> addTypeToEntityData(
      ConnectorInputEntityData untypedEntityData, T assetType) {
    Map<String, String> fieldsToAttributes = untypedEntityData.getFieldsToValues();

    // remove fields that are passed as objects to constructor
    fieldsToAttributes.keySet().remove(TYPE);

    // build result object
    return new TypedConnectorInputEntityData<>(
        new FactoryData.MapWithRowIndex(untypedEntityData.getRowIndex(), fieldsToAttributes),
        untypedEntityData.getTargetClass(),
        untypedEntityData.getOperatorInput(),
        untypedEntityData.getNodeA(),
        untypedEntityData.getNodeB(),
        assetType);
  }

  /**
   * Enriches the Stream of options on {@link Transformer3WInputEntityData} with the information of
   * the internal node
   *
   * @param typedConnectorEntityDataStream Stream of already typed input entity data
   * @param nodes Yet available nodes
   * @return A stream of options on enriched data
   */
  private Stream<Optional<Transformer3WInputEntityData>> buildTransformer3WEntityData(
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
  private Optional<Transformer3WInputEntityData> addThirdNode(
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
            new FactoryData.MapWithRowIndex(typeEntityData.getRowIndex(), fieldsToAttributes),
            typeEntityData.getTargetClass(),
            typeEntityData.getOperatorInput(),
            typeEntityData.getNodeA(),
            typeEntityData.getNodeB(),
            nodeC.get(),
            typeEntityData.getType()));
  }
}
