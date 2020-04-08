/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputEntityData;
import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputFactory;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputEntityData;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputFactory;
import edu.ie3.datamodel.io.source.GraphicSource;
import edu.ie3.datamodel.io.source.RawGridSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 08.04.20
 */
public class CsvGraphicSource extends CsvDataSource implements GraphicSource {

  // general fields
  private final TypeSource typeSource;
  private final RawGridSource rawGridSource;

  // factories
  private final LineGraphicInputFactory lineGraphicInputFactory;
  private final NodeGraphicInputFactory nodeGraphicInputFactory;

  public CsvGraphicSource(
      String csvSep,
      String folderPath,
      FileNamingStrategy fileNamingStrategy,
      TypeSource typeSource,
      RawGridSource rawGridSource) {
    super(csvSep, folderPath, fileNamingStrategy);
    this.typeSource = typeSource;
    this.rawGridSource = rawGridSource;

    // init factories
    this.lineGraphicInputFactory = new LineGraphicInputFactory();
    this.nodeGraphicInputFactory = new NodeGraphicInputFactory();
  }

  @Override
  public Optional<GraphicElements> getGraphicElements() {

    // read all needed entities
    /// start with types and operators
    Collection<OperatorInput> operators = typeSource.getOperators();
    Collection<LineTypeInput> lineTypes = typeSource.getLineTypes();

    Set<NodeInput> nodes =
        checkForUuidDuplicates(NodeInput.class, rawGridSource.getNodes(operators));
    Set<LineInput> lines =
        checkForUuidDuplicates(
            LineInput.class, rawGridSource.getLines(nodes, lineTypes, operators));

    // start with the entities needed for a GraphicElements entity
    /// as we want to return a working grid, keep an eye on empty optionals
    ConcurrentHashMap<Class<? extends UniqueEntity>, LongAdder> invalidElementsCounter =
        new ConcurrentHashMap<>();

    Set<NodeGraphicInput> nodeGraphics =
        checkForUuidDuplicates(
            NodeGraphicInput.class,
            buildNodeGraphicEntityData(nodes)
                .map(dataOpt -> dataOpt.flatMap(nodeGraphicInputFactory::getEntity))
                .filter(isPresentCollectIfNot(NodeGraphicInput.class, invalidElementsCounter))
                .map(Optional::get)
                .collect(Collectors.toSet()));

    Set<LineGraphicInput> lineGraphics =
        checkForUuidDuplicates(
            LineGraphicInput.class,
            buildLineGraphicEntityData(lines)
                .map(dataOpt -> dataOpt.flatMap(lineGraphicInputFactory::getEntity))
                .filter(isPresentCollectIfNot(LineGraphicInput.class, invalidElementsCounter))
                .map(Optional::get)
                .collect(Collectors.toSet()));

    // if we found invalid elements return an empty optional and log the problems
    if (!invalidElementsCounter.isEmpty()) {
      invalidElementsCounter.forEach(this::printInvalidElementInformation);
      return Optional.empty();
    }

    // if everything is fine, return a GraphicElements instance
    return Optional.of(new GraphicElements(nodeGraphics, lineGraphics));
  }

  @Override
  public Collection<NodeGraphicInput> getNodeGraphicInput() {
    return getNodeGraphicInput(rawGridSource.getNodes(typeSource.getOperators()));
  }

  @Override
  public Collection<NodeGraphicInput> getNodeGraphicInput(Collection<NodeInput> nodes) {
    return filterEmptyOptionals(
            buildNodeGraphicEntityData(nodes)
                .map(dataOpt -> dataOpt.flatMap(nodeGraphicInputFactory::getEntity)))
        .collect(Collectors.toSet());
  }

  @Override
  public Collection<LineGraphicInput> getLineGraphicInput() {
    Collection<OperatorInput> operators = typeSource.getOperators();
    return getLineGraphicInput(
        rawGridSource.getLines(
            rawGridSource.getNodes(operators), typeSource.getLineTypes(), operators));
  }

  @Override
  public Collection<LineGraphicInput> getLineGraphicInput(Collection<LineInput> lines) {

    return filterEmptyOptionals(
            buildLineGraphicEntityData(lines)
                .map(dataOpt -> dataOpt.flatMap(lineGraphicInputFactory::getEntity)))
        .collect(Collectors.toSet());
  }

  private Stream<Optional<NodeGraphicInputEntityData>> buildNodeGraphicEntityData(
      Collection<NodeInput> nodes) {

    return buildStreamWithFieldsToAttributesMap(NodeGraphicInput.class, connector)
        .map(
            fieldsToAttributes -> {

              // get the node of the entity
              String nodeUuid = fieldsToAttributes.get(NODE);
              Optional<NodeInput> node = findFirstEntityByUuid(nodeUuid, nodes);

              // if the node is not present we return an empty element and
              // log a warning
              if (!node.isPresent()) {
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
            });
  }

  private Stream<Optional<LineGraphicInputEntityData>> buildLineGraphicEntityData(
      Collection<LineInput> lines) {

    return buildStreamWithFieldsToAttributesMap(LineGraphicInput.class, connector)
        .map(
            fieldsToAttributes -> {

              // get the node of the entity
              String lineUuid = fieldsToAttributes.get("line");
              Optional<LineInput> line = findFirstEntityByUuid(lineUuid, lines);

              // if the node is not present we return an empty element and
              // log a warning
              if (!line.isPresent()) {
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
            });
  }
}
