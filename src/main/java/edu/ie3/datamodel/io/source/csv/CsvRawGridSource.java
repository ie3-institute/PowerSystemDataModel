/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.io.source.RawGridSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
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
 * //ToDo: Class Description Nothing is buffered -> for performance one might consider reading
 * nodes, operators etc. first and then passing in all required collections, otherwise reading is
 * done in a hierarchical cascading way to get all elements needed TODO description needs hint that
 * Set does NOT mean uuid uniqueness
 *
 * <p>// todo performance improvements in all sources to make as as less possible recursive stream
 * calls on files
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

  @Override
  public Optional<RawGridElements> getGridData() {

    // read all needed entities
    /// start with the types and operators
    Set<OperatorInput> operators = typeSource.getOperators();
    Set<LineTypeInput> lineTypes = typeSource.getLineTypes();
    Set<Transformer2WTypeInput> transformer2WTypeInputs = typeSource.getTransformer2WTypes();
    Set<Transformer3WTypeInput> transformer3WTypeInputs = typeSource.getTransformer3WTypes();

    /// assets
    Set<NodeInput> nodes = getNodes(operators);

    // start with the entities needed for a RawGridElement
    /// as we want to return a working grid, keep an eye on empty optionals which is equal to
    // elements that
    /// have been unable to be built e.g. due to missing elements they depend on
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

    // if we found non-build elements return an empty optional and log the problems
    if (!nonBuildEntities.isEmpty()) {
      nonBuildEntities.forEach(this::printInvalidElementInformation);
      return Optional.empty();
    }

    // build the grid
    RawGridElements gridElements =
        new RawGridElements(
            nodes,
            lineInputs,
            transformer2WInputs,
            transformer3WInputs,
            switches,
            measurementUnits);

    // return the grid if it is not empty
    return gridElements.allEntitiesAsList().isEmpty()
        ? Optional.empty()
        : Optional.of(gridElements);
  }

  @Override
  public Set<NodeInput> getNodes() {
    return getNodes(typeSource.getOperators());
  }

  @Override
  public Set<NodeInput> getNodes(Collection<OperatorInput> operators) {
    return filterEmptyOptionals(
            assetInputEntityDataStream(NodeInput.class, operators).map(nodeInputFactory::getEntity))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<LineInput> getLines() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getLines(getNodes(operators), typeSource.getLineTypes(), operators);
  }

  @Override
  public Set<LineInput> getLines(
      Set<NodeInput> nodes, Set<LineTypeInput> lineTypeInputs, Set<OperatorInput> operators) {
    return filterEmptyOptionals(
            typedEntityStream(LineInput.class, lineInputFactory, nodes, operators, lineTypeInputs))
        .collect(Collectors.toSet());
  }

  @Override
  public Set<Transformer2WInput> get2WTransformers() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return get2WTransformers(getNodes(operators), typeSource.getTransformer2WTypes(), operators);
  }

  @Override
  public Set<Transformer2WInput> get2WTransformers(
      Set<NodeInput> nodes,
      Set<Transformer2WTypeInput> transformer2WTypes,
      Set<OperatorInput> operators) {
    return filterEmptyOptionals(
            typedEntityStream(
                Transformer2WInput.class,
                transformer2WInputFactory,
                nodes,
                operators,
                transformer2WTypes))
        .collect(Collectors.toSet());
  }

  private <T extends AssetInput, A extends AssetTypeInput> Stream<Optional<T>> typedEntityStream(
      Class<T> entityClass,
      EntityFactory<T, TypedConnectorInputEntityData<A>> factory,
      Collection<NodeInput> nodes,
      Collection<OperatorInput> operators,
      Collection<A> types) {

    return buildTypedConnectorEntityData(
            buildUntypedConnectorInputEntityData(
                assetInputEntityDataStream(entityClass, operators), nodes),
            types)
        .map(dataOpt -> dataOpt.flatMap(factory::getEntity));
  }

  @Override
  public Set<Transformer3WInput> get3WTransformers() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return get3WTransformers(getNodes(operators), typeSource.getTransformer3WTypes(), operators);
  }

  @Override
  public Set<Transformer3WInput> get3WTransformers(
      Set<NodeInput> nodes,
      Set<Transformer3WTypeInput> transformer3WTypeInputs,
      Set<OperatorInput> operators) {

    return filterEmptyOptionals(
            transformer3WEntityStream(nodes, transformer3WTypeInputs, operators))
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
        .map(dataOpt -> dataOpt.flatMap(transformer3WInputFactory::getEntity));
  }

  @Override
  public Set<SwitchInput> getSwitches() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getSwitches(getNodes(operators), operators);
  }

  @Override
  public Set<SwitchInput> getSwitches(Set<NodeInput> nodes, Set<OperatorInput> operators) {

    return filterEmptyOptionals(
            untypedConnectorInputEntityStream(
                SwitchInput.class, switchInputFactory, nodes, operators))
        .collect(Collectors.toSet());
  }

  private <T extends AssetInput> Stream<Optional<T>> untypedConnectorInputEntityStream(
      Class<T> entityClass,
      EntityFactory<T, ConnectorInputEntityData> factory,
      Set<NodeInput> nodes,
      Set<OperatorInput> operators) {

    return buildUntypedConnectorInputEntityData(
            assetInputEntityDataStream(entityClass, operators), nodes)
        .map(dataOpt -> dataOpt.flatMap(factory::getEntity));
  }

  @Override
  public Set<MeasurementUnitInput> getMeasurementUnits() {
    Set<OperatorInput> operators = typeSource.getOperators();
    return getMeasurementUnits(getNodes(operators), operators);
  }

  @Override
  public Set<MeasurementUnitInput> getMeasurementUnits(
      Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return filterEmptyOptionals(
            nodeAssetEntityStream(
                MeasurementUnitInput.class, measurementUnitInputFactory, nodes, operators))
        .collect(Collectors.toSet());
  }

  private Stream<Optional<ConnectorInputEntityData>> buildUntypedConnectorInputEntityData(
      Stream<AssetInputEntityData> assetInputEntityDataStream, Collection<NodeInput> nodes) {
    return assetInputEntityDataStream
        .parallel()
        .map(
            assetInputEntityData -> {

              // get the raw data
              Map<String, String> fieldsToAttributes = assetInputEntityData.getFieldsToValues();

              // get the two connector nodes
              String nodeAUuid = fieldsToAttributes.get(NODE_A);
              String nodeBUuid = fieldsToAttributes.get(NODE_B);
              Optional<NodeInput> nodeA = findFirstEntityByUuid(nodeAUuid, nodes);
              Optional<NodeInput> nodeB = findFirstEntityByUuid(nodeBUuid, nodes);

              // if nodeA or nodeB are not present we return an empty element and log a
              // warning
              if (!nodeA.isPresent() || !nodeB.isPresent()) {
                String debugString =
                    Stream.of(
                            new AbstractMap.SimpleEntry<>(nodeA, NODE_A + ": " + nodeAUuid),
                            new AbstractMap.SimpleEntry<>(nodeB, NODE_B + ": " + nodeBUuid))
                        .filter(entry -> !entry.getKey().isPresent())
                        .map(AbstractMap.SimpleEntry::getValue)
                        .collect(Collectors.joining("\n"));

                logSkippingWarning(
                    assetInputEntityData.getEntityClass().getSimpleName(),
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
                      assetInputEntityData.getEntityClass(),
                      assetInputEntityData.getOperatorInput(),
                      nodeA.get(),
                      nodeB.get()));
            });
  }

  private <T extends AssetTypeInput>
      Stream<Optional<TypedConnectorInputEntityData<T>>> buildTypedConnectorEntityData(
          Stream<Optional<ConnectorInputEntityData>> noTypeConnectorEntityDataStream,
          Collection<T> types) {
    return noTypeConnectorEntityDataStream
        .parallel()
        .map(
            noTypeEntityDataOpt ->
                noTypeEntityDataOpt.flatMap(
                    noTypeEntityData ->
                        getType(
                                types,
                                noTypeEntityData.getFieldsToValues(),
                                noTypeEntityData.getClass())
                            .map( // if the optional is present, transform and return to the data,
                                // otherwise return an empty optional
                                assetType -> {
                                  Map<String, String> fieldsToAttributes =
                                      noTypeEntityData.getFieldsToValues();

                                  // remove fields that are passed as objects to constructor
                                  fieldsToAttributes.keySet().remove(TYPE);

                                  // build result object
                                  return new TypedConnectorInputEntityData<>(
                                      fieldsToAttributes,
                                      noTypeEntityData.getEntityClass(),
                                      noTypeEntityData.getOperatorInput(),
                                      noTypeEntityData.getNodeA(),
                                      noTypeEntityData.getNodeB(),
                                      assetType);
                                })));
  }

  private Stream<Optional<Transformer3WInputEntityData>> buildTransformer3WEntityData(
      Stream<Optional<TypedConnectorInputEntityData<Transformer3WTypeInput>>>
          typedConnectorEntityDataStream,
      Collection<NodeInput> nodes) {
    return typedConnectorEntityDataStream
        .parallel()
        .map(
            typedEntityDataOpt ->
                typedEntityDataOpt.flatMap(
                    typeEntityData -> {

                      // get the raw data
                      Map<String, String> fieldsToAttributes = typeEntityData.getFieldsToValues();

                      // get nodeC of the transformer
                      String nodeCUuid = fieldsToAttributes.get("nodeC");
                      Optional<NodeInput> nodeC = findFirstEntityByUuid(nodeCUuid, nodes);

                      // if nodeC is not present we return an empty element and
                      // log a warning
                      if (!nodeC.isPresent()) {
                        logSkippingWarning(
                            typeEntityData.getEntityClass().getSimpleName(),
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
                              typeEntityData.getEntityClass(),
                              typeEntityData.getOperatorInput(),
                              typeEntityData.getNodeA(),
                              typeEntityData.getNodeB(),
                              nodeC.get(),
                              typeEntityData.getType()));
                    }));
  }
}
