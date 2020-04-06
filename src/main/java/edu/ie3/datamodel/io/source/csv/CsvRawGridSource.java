/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.connectors.CsvFileConnector;
import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.io.source.RawGridSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * //ToDo: Class Description Nothing is buffered -> for performance one might consider reading
 * nodes, operators etc. first and then passing in all required collections, otherwise reading is
 * done in a hierarchical cascading way to get all elements needed
 *
 * @version 0.1
 * @since 03.04.20
 */
public class CsvRawGridSource extends CsvDataSource implements RawGridSource {

  private static final Logger log = LogManager.getLogger(CsvRawGridSource.class);

  // general fields
  private final CsvFileConnector connector;
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
    super(csvSep);
    this.connector = new CsvFileConnector(gridFolderPath, fileNamingStrategy);
    this.typeSource = typeSource;

    // init factories
    nodeInputFactory = new NodeInputFactory();
    lineInputFactory = new LineInputFactory();
    transformer2WInputFactory = new Transformer2WInputFactory();
    transformer3WInputFactory = new Transformer3WInputFactory();
    switchInputFactory = new SwitchInputFactory();
    measurementUnitInputFactory = new MeasurementUnitInputFactory();
  }

  @Override
  public Optional<RawGridElements> getGridData() {

    // read all needed entities
    /// start with the types and operators
    Collection<OperatorInput> operators = typeSource.getOperators();
    Collection<LineTypeInput> lineTypes = typeSource.getLineTypes();
    Collection<Transformer2WTypeInput> transformer2WTypeInputs = typeSource.getTransformer2WTypes();
    Collection<Transformer3WTypeInput> transformer3WTypeInputs = typeSource.getTransformer3WTypes();

    /// assets incl. filter of unique entities + warning if duplicate uuids got filtered out
    Set<NodeInput> nodes = checkForUuidDuplicates(NodeInput.class, readNodes(operators));

    List<Optional<LineInput>> invalidLines = new CopyOnWriteArrayList<>();
    List<Optional<Transformer2WInput>> invalidTrafo2Ws = new CopyOnWriteArrayList<>();
    List<Optional<Transformer3WInput>> invalidTrafo3Ws = new CopyOnWriteArrayList<>();
    List<Optional<SwitchInput>> invalidSwitches = new CopyOnWriteArrayList<>();
    List<Optional<MeasurementUnitInput>> invalidMeasurementUnits = new CopyOnWriteArrayList<>();

    Set<LineInput> lineInputs =
        checkForUuidDuplicates(
            LineInput.class,
            readLines(nodes, lineTypes, operators).stream()
                .filter(isPresentWithInvalidList(invalidLines))
                .map(Optional::get)
                .collect(Collectors.toSet()));
    Set<Transformer2WInput> transformer2WInputs =
        checkForUuidDuplicates(
            Transformer2WInput.class,
            read2WTransformers(nodes, transformer2WTypeInputs, operators).stream()
                .filter(isPresentWithInvalidList(invalidTrafo2Ws))
                .map(Optional::get)
                .collect(Collectors.toSet()));
    Set<Transformer3WInput> transformer3WInputs =
        checkForUuidDuplicates(
            Transformer3WInput.class,
            read3WTransformers(nodes, transformer3WTypeInputs, operators).stream()
                .filter(isPresentWithInvalidList(invalidTrafo3Ws))
                .map(Optional::get)
                .collect(Collectors.toSet()));
    Set<SwitchInput> switches =
        checkForUuidDuplicates(
            SwitchInput.class,
            readSwitches(nodes, operators).stream()
                .filter(isPresentWithInvalidList(invalidSwitches))
                .map(Optional::get)
                .collect(Collectors.toSet()));
    Set<MeasurementUnitInput> measurementUnits =
        checkForUuidDuplicates(
            MeasurementUnitInput.class,
            readMeasurementUnits(nodes, operators).stream()
                .filter(isPresentWithInvalidList(invalidMeasurementUnits))
                .map(Optional::get)
                .collect(Collectors.toSet()));

    // check if we have invalid elements and if yes, log information
    boolean invalidExists =
        Stream.of(
                new AbstractMap.SimpleEntry<>(LineInput.class, invalidLines),
                new AbstractMap.SimpleEntry<>(Transformer2WInput.class, invalidTrafo2Ws),
                new AbstractMap.SimpleEntry<>(Transformer3WInput.class, invalidTrafo3Ws),
                new AbstractMap.SimpleEntry<>(SwitchInput.class, invalidSwitches),
                new AbstractMap.SimpleEntry<>(MeasurementUnitInput.class, invalidMeasurementUnits))
            .filter(entry -> !entry.getValue().isEmpty())
            .map(
                entry -> {
                  printInvalidElementInformation(entry.getKey(), entry.getValue());
                  return Optional.empty();
                })
            .anyMatch(x -> true);

    // if we found invalid elements return an empty optional
    if (invalidExists) {
      return Optional.empty();
    }

    // if everything is fine, return a grid
    return Optional.of(
        new RawGridElements(
            nodes,
            lineInputs,
            transformer2WInputs,
            transformer3WInputs,
            switches,
            measurementUnits));
  }

  @Override
  public Collection<NodeInput> getNodes() {
    return readNodes(typeSource.getOperators());
  }

  @Override
  public Collection<NodeInput> getNodes(Collection<OperatorInput> operators) {
    return readNodes(operators);
  }

  @Override
  public Collection<LineInput> getLines() {
    return filterEmptyOptionals(
        readLines(getNodes(), typeSource.getLineTypes(), typeSource.getOperators()));
  }

  @Override
  public Collection<LineInput> getLines(
      Collection<NodeInput> nodes,
      Collection<LineTypeInput> lineTypeInputs,
      Collection<OperatorInput> operators) {
    return filterEmptyOptionals(readLines(nodes, lineTypeInputs, operators));
  }

  @Override
  public Collection<Transformer2WInput> get2WTransformers() {
    return filterEmptyOptionals(
        read2WTransformers(
            getNodes(), typeSource.getTransformer2WTypes(), typeSource.getOperators()));
  }

  @Override
  public Collection<Transformer2WInput> get2WTransformers(
      Collection<NodeInput> nodes,
      Collection<Transformer2WTypeInput> transformer2WTypes,
      Collection<OperatorInput> operators) {
    return filterEmptyOptionals(read2WTransformers(nodes, transformer2WTypes, operators));
  }

  @Override
  public Collection<Transformer3WInput> get3WTransformers() {
    return filterEmptyOptionals(
        read3WTransformers(
            getNodes(), typeSource.getTransformer3WTypes(), typeSource.getOperators()));
  }

  @Override
  public Collection<Transformer3WInput> get3WTransformers(
      Collection<NodeInput> nodes,
      Collection<Transformer3WTypeInput> transformer3WTypeInputs,
      Collection<OperatorInput> operators) {
    return filterEmptyOptionals(read3WTransformers(nodes, transformer3WTypeInputs, operators));
  }

  @Override
  public Collection<SwitchInput> getSwitches() {
    return filterEmptyOptionals(readSwitches(getNodes(), typeSource.getOperators()));
  }

  @Override
  public Collection<SwitchInput> getSwitches(
      Collection<NodeInput> nodes, Collection<OperatorInput> operators) {
    return filterEmptyOptionals(readSwitches(nodes, operators));
  }

  @Override
  public Collection<MeasurementUnitInput> getMeasurementUnits() {
    return filterEmptyOptionals(readMeasurementUnits(getNodes(), typeSource.getOperators()));
  }

  @Override
  public Collection<MeasurementUnitInput> getMeasurementUnits(
      Collection<NodeInput> nodes, Collection<OperatorInput> operators) {
    return filterEmptyOptionals(readMeasurementUnits(nodes, operators));
  }

  private Collection<NodeInput> readNodes(Collection<OperatorInput> operators) {
    Set<NodeInput> resultingAssets = new HashSet<>();
    final Class<NodeInput> entityClass = NodeInput.class;

    try (BufferedReader reader = connector.getReader(entityClass)) {

      final String[] headline = readHeadline(reader);
      resultingAssets =
          reader
              .lines()
              .parallel()
              .map(
                  csvRow -> {
                    Map<String, String> fieldsToAttributes =
                        buildFieldsToAttributes(csvRow, headline);

                    // get the operator
                    OperatorInput nodeOperator =
                        getOrDefaultOperator(operators, fieldsToAttributes.get(OPERATOR));

                    // remove fields that are passed as objects to constructor
                    fieldsToAttributes
                        .keySet()
                        .removeAll(new HashSet<>(Collections.singletonList(OPERATOR)));

                    // build the asset data
                    AssetInputEntityData data =
                        new AssetInputEntityData(fieldsToAttributes, entityClass, nodeOperator);

                    // build the model
                    return nodeInputFactory.getEntity(data);
                  })
              .filter(Optional::isPresent)
              .map(Optional::get)
              .collect(Collectors.toSet());

    } catch (IOException e) {
      logIOExceptionFromConnector(NodeInput.class, e);
    }

    return resultingAssets;
  }

  private Collection<Optional<LineInput>> readLines(
      Collection<NodeInput> nodes,
      Collection<LineTypeInput> lineTypeInputs,
      Collection<OperatorInput> operators) {
    Set<Optional<LineInput>> resultingAssets = new HashSet<>();

    final Class<LineInput> entityClass = LineInput.class;

    try (BufferedReader reader = connector.getReader(entityClass)) {
      String[] headline = readHeadline(reader);

      resultingAssets =
          reader
              .lines()
              .parallel()
              .map(csvRow -> buildFieldsToAttributes(csvRow, headline))
              .map(
                  fieldsToAttributes -> {

                    // get the line nodes
                    String nodeBUuid = fieldsToAttributes.get(NODE_B);
                    Optional<NodeInput> nodeA =
                        findNodeByUuid(fieldsToAttributes.get(NODE_A), nodes);
                    Optional<NodeInput> nodeB = findNodeByUuid(nodeBUuid, nodes);

                    // get the line type
                    String typeUuid = fieldsToAttributes.get("type");
                    Optional<LineTypeInput> lineType = findTypeByUuid(typeUuid, lineTypeInputs);

                    // if nodeA, nodeB or the type are not present we return an empty element and
                    // log a warning
                    Optional<LineInput> lineOpt;
                    if (!nodeA.isPresent() || !nodeB.isPresent() || !lineType.isPresent()) {
                      lineOpt = Optional.empty();

                      String debugString =
                          Stream.of(
                                  new AbstractMap.SimpleEntry<>(
                                      nodeA, NODE_A + ": " + fieldsToAttributes.get(NODE_A)),
                                  new AbstractMap.SimpleEntry<>(nodeB, NODE_B + ": " + nodeBUuid),
                                  new AbstractMap.SimpleEntry<>(lineType, TYPE + ": " + typeUuid))
                              .filter(entry -> !entry.getKey().isPresent())
                              .map(AbstractMap.SimpleEntry::getValue)
                              .collect(Collectors.joining("\n"));

                      logSkippingWarning(
                          "line",
                          fieldsToAttributes.get("uuid"),
                          fieldsToAttributes.get("id"),
                          debugString);

                    } else {

                      // remove fields that are passed as objects to constructor
                      fieldsToAttributes
                          .keySet()
                          .removeAll(
                              new HashSet<>(Arrays.asList(OPERATOR, NODE_A, NODE_B, "type")));

                      // build the asset data
                      LineInputEntityData data =
                          new LineInputEntityData(
                              fieldsToAttributes,
                              entityClass,
                              getOrDefaultOperator(operators, fieldsToAttributes.get(OPERATOR)),
                              nodeA.get(),
                              nodeB.get(),
                              lineType.get());
                      // build the model
                      lineOpt = lineInputFactory.getEntity(data);
                    }

                    return lineOpt;
                  })
              .collect(Collectors.toSet());

    } catch (IOException e) {
      logIOExceptionFromConnector(LineInput.class, e);
    }

    return resultingAssets;
  }

  private Collection<Optional<Transformer2WInput>> read2WTransformers(
      Collection<NodeInput> nodes,
      Collection<Transformer2WTypeInput> transformer2WTypes,
      Collection<OperatorInput> operators) {
    Set<Optional<Transformer2WInput>> resultingAssets = new HashSet<>();

    final Class<Transformer2WInput> entityClass = Transformer2WInput.class;

    try (BufferedReader reader = connector.getReader(entityClass)) {
      String[] headline = readHeadline(reader);

      resultingAssets =
          reader
              .lines()
              .parallel()
              .map(csvRow -> buildFieldsToAttributes(csvRow, headline))
              .map(
                  fieldsToAttributes -> {

                    // get the transformer nodes
                    String nodeAUuid = fieldsToAttributes.get(NODE_A);
                    String nodeBUuid = fieldsToAttributes.get(NODE_B);
                    Optional<NodeInput> nodeA = findNodeByUuid(nodeAUuid, nodes);
                    Optional<NodeInput> nodeB = findNodeByUuid(nodeBUuid, nodes);

                    // get the transformer type
                    String typeUuid = fieldsToAttributes.get("type");
                    Optional<Transformer2WTypeInput> transformerType =
                        findTypeByUuid(typeUuid, transformer2WTypes);

                    // if nodeA, nodeB or the type are not present we return an empty element and
                    // log a warning
                    Optional<Transformer2WInput> trafo2WOpt;
                    if (!nodeA.isPresent() || !nodeB.isPresent() || !transformerType.isPresent()) {
                      trafo2WOpt = Optional.empty();

                      String debugString =
                          Stream.of(
                                  new AbstractMap.SimpleEntry<>(nodeA, NODE_A + ": " + nodeAUuid),
                                  new AbstractMap.SimpleEntry<>(nodeB, NODE_B + ": " + nodeBUuid),
                                  new AbstractMap.SimpleEntry<>(
                                      transformerType, TYPE + ": " + typeUuid))
                              .filter(entry -> !entry.getKey().isPresent())
                              .map(AbstractMap.SimpleEntry::getValue)
                              .collect(Collectors.joining("\n"));

                      logSkippingWarning(
                          "2 winding transformer",
                          fieldsToAttributes.get("uuid"),
                          fieldsToAttributes.get("id"),
                          debugString);

                    } else {

                      // remove fields that are passed as objects to constructor
                      fieldsToAttributes
                          .keySet()
                          .removeAll(
                              new HashSet<>(Arrays.asList(OPERATOR, NODE_A, NODE_B, "type")));

                      // build the asset data
                      Transformer2WInputEntityData data =
                          new Transformer2WInputEntityData(
                              fieldsToAttributes,
                              entityClass,
                              getOrDefaultOperator(operators, fieldsToAttributes.get(OPERATOR)),
                              nodeA.get(),
                              nodeB.get(),
                              transformerType.get());
                      // build the model
                      trafo2WOpt = transformer2WInputFactory.getEntity(data);
                    }

                    return trafo2WOpt;
                  })
              .collect(Collectors.toSet());

    } catch (IOException e) {
      logIOExceptionFromConnector(Transformer2WInput.class, e);
    }

    return resultingAssets;
  }

  private Collection<Optional<Transformer3WInput>> read3WTransformers(
      Collection<NodeInput> nodes,
      Collection<Transformer3WTypeInput> transformer3WTypes,
      Collection<OperatorInput> operators) {
    Set<Optional<Transformer3WInput>> resultingAssets = new HashSet<>();

    final Class<Transformer3WInput> entityClass = Transformer3WInput.class;

    try (BufferedReader reader = connector.getReader(entityClass)) {
      String[] headline = readHeadline(reader);

      resultingAssets =
          reader
              .lines()
              .parallel()
              .map(csvRow -> buildFieldsToAttributes(csvRow, headline))
              .map(
                  fieldsToAttributes -> {

                    // get the transformer nodes
                    String nodeBUuid = fieldsToAttributes.get(NODE_B);
                    String nodeCUuid = fieldsToAttributes.get("nodeC");
                    Optional<NodeInput> nodeA =
                        findNodeByUuid(fieldsToAttributes.get(NODE_A), nodes);
                    Optional<NodeInput> nodeB = findNodeByUuid(nodeBUuid, nodes);
                    Optional<NodeInput> nodeC = findNodeByUuid(nodeCUuid, nodes);

                    // get the transformer type
                    String typeUuid = fieldsToAttributes.get("type");
                    Optional<Transformer3WTypeInput> transformerType =
                        findTypeByUuid(typeUuid, transformer3WTypes);

                    // if nodeA, nodeB or the type are not present we return an empty element and
                    // log a warning
                    Optional<Transformer3WInput> trafo3WOpt;
                    if (!nodeA.isPresent()
                        || !nodeB.isPresent()
                        || !nodeC.isPresent()
                        || !transformerType.isPresent()) {
                      trafo3WOpt = Optional.empty();

                      String debugString =
                          Stream.of(
                                  new AbstractMap.SimpleEntry<>(
                                      nodeA, NODE_A + ": " + fieldsToAttributes.get(NODE_A)),
                                  new AbstractMap.SimpleEntry<>(nodeB, NODE_B + ": " + nodeBUuid),
                                  new AbstractMap.SimpleEntry<>(nodeC, "node_c: " + nodeCUuid),
                                  new AbstractMap.SimpleEntry<>(
                                      transformerType, TYPE + ": " + typeUuid))
                              .filter(entry -> !entry.getKey().isPresent())
                              .map(AbstractMap.SimpleEntry::getValue)
                              .collect(Collectors.joining("\n"));

                      logSkippingWarning(
                          "3 winding transformer",
                          fieldsToAttributes.get("uuid"),
                          fieldsToAttributes.get("id"),
                          debugString);

                    } else {

                      // remove fields that are passed as objects to constructor
                      fieldsToAttributes
                          .keySet()
                          .removeAll(
                              new HashSet<>(
                                  Arrays.asList(OPERATOR, NODE_A, NODE_B, "nodeC", "type")));

                      // build the asset data
                      Transformer3WInputEntityData data =
                          new Transformer3WInputEntityData(
                              fieldsToAttributes,
                              entityClass,
                              getOrDefaultOperator(operators, fieldsToAttributes.get(OPERATOR)),
                              nodeA.get(),
                              nodeB.get(),
                              nodeC.get(),
                              transformerType.get());
                      // build the model
                      trafo3WOpt = transformer3WInputFactory.getEntity(data);
                    }

                    return trafo3WOpt;
                  })
              .collect(Collectors.toSet());

    } catch (IOException e) {
      logIOExceptionFromConnector(Transformer3WInput.class, e);
    }

    return resultingAssets;
  }

  private Collection<Optional<SwitchInput>> readSwitches(
      Collection<NodeInput> nodes, Collection<OperatorInput> operators) {
    Set<Optional<SwitchInput>> resultingAssets = new HashSet<>();

    final Class<SwitchInput> entityClass = SwitchInput.class;

    try (BufferedReader reader = connector.getReader(entityClass)) {
      String[] headline = readHeadline(reader);

      resultingAssets =
          reader
              .lines()
              .parallel()
              .map(csvRow -> buildFieldsToAttributes(csvRow, headline))
              .map(
                  fieldsToAttributes -> {

                    // get the switch nodes
                    String nodeAUuid = fieldsToAttributes.get(NODE_A);
                    String nodeBUuid = fieldsToAttributes.get(NODE_B);
                    Optional<NodeInput> nodeA = findNodeByUuid(nodeAUuid, nodes);
                    Optional<NodeInput> nodeB = findNodeByUuid(nodeBUuid, nodes);

                    // if nodeA or nodeB are not present we return an empty element and log a
                    // warning
                    Optional<SwitchInput> switchOpt;
                    if (!nodeA.isPresent() || !nodeB.isPresent()) {
                      switchOpt = Optional.empty();

                      String debugString =
                          Stream.of(
                                  new AbstractMap.SimpleEntry<>(nodeA, NODE_A + ": " + nodeAUuid),
                                  new AbstractMap.SimpleEntry<>(nodeB, NODE_B + ": " + nodeBUuid))
                              .filter(entry -> !entry.getKey().isPresent())
                              .map(AbstractMap.SimpleEntry::getValue)
                              .collect(Collectors.joining("\n"));

                      logSkippingWarning(
                          "switch",
                          fieldsToAttributes.get("uuid"),
                          fieldsToAttributes.get("id"),
                          debugString);

                    } else {

                      // remove fields that are passed as objects to constructor
                      fieldsToAttributes
                          .keySet()
                          .removeAll(new HashSet<>(Arrays.asList(OPERATOR, NODE_A, NODE_B)));

                      // build the asset data
                      ConnectorInputEntityData data =
                          new ConnectorInputEntityData(
                              fieldsToAttributes,
                              entityClass,
                              getOrDefaultOperator(operators, fieldsToAttributes.get(OPERATOR)),
                              nodeA.get(),
                              nodeB.get());
                      // build the model
                      switchOpt = switchInputFactory.getEntity(data);
                    }

                    return switchOpt;
                  })
              .collect(Collectors.toSet());

    } catch (IOException e) {
      logIOExceptionFromConnector(SwitchInput.class, e);
    }

    return resultingAssets;
  }

  private Collection<Optional<MeasurementUnitInput>> readMeasurementUnits(
      Collection<NodeInput> nodes, Collection<OperatorInput> operators) {

    Set<Optional<MeasurementUnitInput>> resultingAssets = new HashSet<>();

    final Class<MeasurementUnitInput> entityClass = MeasurementUnitInput.class;

    try (BufferedReader reader = connector.getReader(entityClass)) {
      String[] headline = readHeadline(reader);

      resultingAssets =
          reader
              .lines()
              .parallel()
              .map(csvRow -> buildFieldsToAttributes(csvRow, headline))
              .map(
                  fieldsToAttributes -> {

                    // get the measurement unit node
                    String nodeUuid = fieldsToAttributes.get("node");
                    Optional<NodeInput> node = findNodeByUuid(nodeUuid, nodes);

                    // if nodeA or nodeB are not present we return an empty element and log a
                    // warning
                    Optional<MeasurementUnitInput> measurementUnitOpt;
                    if (!node.isPresent()) {
                      measurementUnitOpt = Optional.empty();

                      String debugString =
                          Stream.of(new AbstractMap.SimpleEntry<>(node, "node: " + nodeUuid))
                              .filter(entry -> !entry.getKey().isPresent())
                              .map(AbstractMap.SimpleEntry::getValue)
                              .collect(Collectors.joining("\n"));

                      logSkippingWarning(
                          "measurement unit",
                          fieldsToAttributes.get("uuid"),
                          fieldsToAttributes.get("id"),
                          debugString);

                    } else {

                      // remove fields that are passed as objects to constructor
                      fieldsToAttributes
                          .keySet()
                          .removeAll(new HashSet<>(Arrays.asList(OPERATOR, "node")));

                      // build the asset data
                      MeasurementUnitInputEntityData data =
                          new MeasurementUnitInputEntityData(
                              fieldsToAttributes,
                              entityClass,
                              getOrDefaultOperator(operators, fieldsToAttributes.get(OPERATOR)),
                              node.get());
                      // build the model
                      measurementUnitOpt = measurementUnitInputFactory.getEntity(data);
                    }

                    return measurementUnitOpt;
                  })
              .collect(Collectors.toSet());

    } catch (IOException e) {
      logIOExceptionFromConnector(MeasurementUnitInput.class, e);
    }

    return resultingAssets;
  }
}
