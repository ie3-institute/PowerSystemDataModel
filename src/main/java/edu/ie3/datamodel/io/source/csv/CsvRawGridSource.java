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
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// TODO use Sets to prevent duplicates!

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
  private final Transformer2WInputFactory transformer2WInputFactory;
  private final LineInputFactory lineInputFactory;

  // todo dangerous if csvSep != ; because of the json strings -> find a way to parse that stuff
  //  anyway

  // field names
  private static final String OPERATOR_FIELD = "operator";
  //    private static final String NODE_A = "nodeA";
  //    private static final String NODE_B = "nodeB";

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
    transformer2WInputFactory = new Transformer2WInputFactory();
    lineInputFactory = new LineInputFactory();
  }

  @Override
  public RawGridElements getGridData() {
    return null; // todo
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
                        getOrDefaultOperator(operators, fieldsToAttributes.get(OPERATOR_FIELD));

                    // remove fields that are passed as objects to constructor
                    fieldsToAttributes
                        .keySet()
                        .removeAll(new HashSet<>(Collections.singletonList(OPERATOR_FIELD)));

                    // build the asset data
                    AssetInputEntityData data =
                        new AssetInputEntityData(fieldsToAttributes, entityClass, nodeOperator);

                    // build the model
                    return nodeInputFactory.getEntity(data);
                  })
              .filter(Optional::isPresent)
              .map(Optional::get)
              .collect(Collectors.toSet());

    }
    // todo test for this!
    catch (IOException e) {
      e.printStackTrace(); // todo
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
              .map(
                  csvRow -> {
                    final Map<String, String> fieldsToAttributes =
                        buildFieldsToAttributes(csvRow, headline);

                    // get the transformer nodes
                    Optional<NodeInput> nodeA =
                        findNodeByUuid(fieldsToAttributes.get("nodeA"), nodes);
                    Optional<NodeInput> nodeB =
                        findNodeByUuid(fieldsToAttributes.get("nodeB"), nodes);

                    // get the transformer type
                    Optional<Transformer2WTypeInput> transformerType =
                        findTypeByUuid(fieldsToAttributes.get("type"), transformer2WTypes);

                    // if nodeA, nodeB or the type are not present we return an empty element and
                    // log a warning
                    Optional<Transformer2WInput> trafoOpt;
                    if (!nodeA.isPresent() || !nodeB.isPresent() || !transformerType.isPresent()) {
                      trafoOpt = Optional.empty();
                      log.warn(
                          "Skipping transformer with uuid '{}' and id '{}'. Not all required entities found!"
                              + "Missing elements: {}",
                          fieldsToAttributes.get("uuid"),
                          fieldsToAttributes.get("id"),
                          (nodeA.isPresent() ? "" : "\nnode_a: " + fieldsToAttributes.get("node_a"))
                              .concat(
                                  nodeB.isPresent()
                                      ? ""
                                      : "\nnode_b: " + fieldsToAttributes.get("node_b"))
                              .concat(
                                  transformerType.isPresent()
                                      ? ""
                                      : "\ntype: " + fieldsToAttributes.get("type")));

                    } else {

                      // remove fields that are passed as objects to constructor
                      fieldsToAttributes
                          .keySet()
                          .removeAll(
                              new HashSet<>(
                                  Arrays.asList(OPERATOR_FIELD, "nodeA", "nodeB", "type")));

                      // build the asset data
                      Transformer2WInputEntityData data =
                          new Transformer2WInputEntityData(
                              fieldsToAttributes,
                              entityClass,
                              getOrDefaultOperator(
                                  operators, fieldsToAttributes.get(OPERATOR_FIELD)),
                              nodeA.get(),
                              nodeB.get(),
                              transformerType.get());
                      // build the model
                      trafoOpt = transformer2WInputFactory.getEntity(data);
                    }

                    return trafoOpt;
                  })
              .collect(Collectors.toSet());

    } catch (IOException e) {
      e.printStackTrace(); // todo
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
                    Optional<NodeInput> nodeA =
                        findNodeByUuid(fieldsToAttributes.get("nodeA"), nodes);
                    Optional<NodeInput> nodeB =
                        findNodeByUuid(fieldsToAttributes.get("nodeB"), nodes);

                    // get the line type
                    Optional<LineTypeInput> lineType =
                        findTypeByUuid(fieldsToAttributes.get("type"), lineTypeInputs);

                    // if nodeA, nodeB or the type are not present we return an empty element and
                    // log a warning
                    Optional<LineInput> lineOpt;
                    if (!nodeA.isPresent() || !nodeB.isPresent() || !lineType.isPresent()) {
                      lineOpt = Optional.empty();
                      log.warn(
                          "Skipping line with uuid '{}' and id '{}'. Not all required entities found!"
                              + "Missing elements: {}",
                          fieldsToAttributes.get("uuid"),
                          fieldsToAttributes.get("id"),
                          (nodeA.isPresent() ? "" : "\nnode_a: " + fieldsToAttributes.get("node_a"))
                              .concat(
                                  nodeB.isPresent()
                                      ? ""
                                      : "\nnode_b: " + fieldsToAttributes.get("node_b"))
                              .concat(
                                  lineType.isPresent()
                                      ? ""
                                      : "\ntype: " + fieldsToAttributes.get("type")));

                    } else {

                      // remove fields that are passed as objects to constructor
                      fieldsToAttributes
                          .keySet()
                          .removeAll(
                              new HashSet<>(
                                  Arrays.asList(OPERATOR_FIELD, "nodeA", "nodeB", "type")));

                      // build the asset data
                      LineInputEntityData data =
                          new LineInputEntityData(
                              fieldsToAttributes,
                              entityClass,
                              getOrDefaultOperator(
                                  operators, fieldsToAttributes.get(OPERATOR_FIELD)),
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
      e.printStackTrace(); // todo
    }

    return resultingAssets;
  }
}
