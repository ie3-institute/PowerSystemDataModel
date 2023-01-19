/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.RawGridSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
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
public class CsvRawGridSource extends CsvDataSource {

  // general fields
  //private final TypeSource typeSource;

  private final RawGridSource rawGridSource;

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

    this.rawGridSource = new RawGridSource(typeSource);

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

    Set<LineInput> lineInputs =
        typedEntityStream(LineInput.class, lineInputFactory, nodes, operators, lineTypes)
            .filter(isPresentCollectIfNot(LineInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<Transformer2WInput> transformer2WInputs =
        typedEntityStream(
                Transformer2WInput.class,
                transformer2WInputFactory,
                nodes,
                operators,
                transformer2WTypeInputs)
            .filter(isPresentCollectIfNot(Transformer2WInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<Transformer3WInput> transformer3WInputs =
        transformer3WEntityStream(nodes, transformer3WTypeInputs, operators)
            .filter(isPresentCollectIfNot(Transformer3WInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<SwitchInput> switches =
        untypedConnectorInputEntityStream(SwitchInput.class, switchInputFactory, nodes, operators)
            .filter(isPresentCollectIfNot(SwitchInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());
    Set<MeasurementUnitInput> measurementUnits =
        nodeAssetEntityStream(
                MeasurementUnitInput.class, measurementUnitInputFactory, nodes, operators)
            .filter(isPresentCollectIfNot(MeasurementUnitInput.class, nonBuildEntities))
            .map(Optional::get)
            .collect(Collectors.toSet());

    /* if we found non-build elements return an empty optional and log the problems */
    if (!nonBuildEntities.isEmpty()) {
      nonBuildEntities.forEach(this::printInvalidElementInformation);
      return Optional.empty();
    }

    /* build the grid */
    RawGridElements gridElements =
        new RawGridElements(
            nodes,
            lineInputs,
            transformer2WInputs,
            transformer3WInputs,
            switches,
            measurementUnits);

    /* return the grid if it is not empty */
    return gridElements.allEntitiesAsList().isEmpty()
        ? Optional.empty()
        : Optional.of(gridElements);
  }

  /** {@inheritDoc} */
  @Override
  public Set<NodeInput> getNodes() {
    return getNodes(typeSource.getOperators());
  }

  /**
   * {@inheritDoc}
   *
   * <p>If the set with {@link OperatorInput} is not exhaustive, the corresponding operator is set
   * to {@link OperatorInput#NO_OPERATOR_ASSIGNED}
   */
  @Override
  public Set<NodeInput> getNodes(Set<OperatorInput> operators) {
    return assetInputEntityDataStream(NodeInput.class, operators)
        .map(nodeInputFactory::get)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

  /** {@inheritDoc} */
  @Override
  public Set<LineInput> getLines() {
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
      Set<NodeInput> nodes, Set<LineTypeInput> lineTypeInputs, Set<OperatorInput> operators) {
    return typedEntityStream(LineInput.class, lineInputFactory, nodes, operators, lineTypeInputs)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

  /** {@inheritDoc} */
  @Override
  public Set<Transformer2WInput> get2WTransformers() {
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
      Set<OperatorInput> operators) {
    return typedEntityStream(
            Transformer2WInput.class,
            transformer2WInputFactory,
            nodes,
            operators,
            transformer2WTypes)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

  /** {@inheritDoc} */
  @Override
  public Set<Transformer3WInput> get3WTransformers() {
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
      Set<OperatorInput> operators) {
    return transformer3WEntityStream(nodes, transformer3WTypeInputs, operators)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

  private Stream<Optional<Transformer3WInput>> transformer3WEntityStream(
      Set<NodeInput> nodes,
      Set<Transformer3WTypeInput> transformer3WTypeInputs,
      Set<OperatorInput> operators) {

    return buildTransformer3WEntityData(
            buildTypedConnectorEntityData(
                buildUntypedConnectorInputEntityData(
                    assetInputEntityDataStream(Transformer3WInput.class, operators), nodes),
                transformer3WTypeInputs),
            nodes)
        .map(dataOpt -> dataOpt.flatMap(transformer3WInputFactory::get));
  }

  /** {@inheritDoc} */
  @Override
  public Set<SwitchInput> getSwitches() {
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
  public Set<SwitchInput> getSwitches(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return untypedConnectorInputEntityStream(
            SwitchInput.class, switchInputFactory, nodes, operators)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
  }

  /** {@inheritDoc} */
  @Override
  public Set<MeasurementUnitInput> getMeasurementUnits() {
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
      Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return nodeAssetEntityStream(
            MeasurementUnitInput.class, measurementUnitInputFactory, nodes, operators)
        .flatMap(Optional::stream)
        .collect(Collectors.toSet());
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
            fieldsToAttributes,
            typeEntityData.getTargetClass(),
            typeEntityData.getOperatorInput(),
            typeEntityData.getNodeA(),
            typeEntityData.getNodeB(),
            nodeC.get(),
            typeEntityData.getType()));
  }
}
