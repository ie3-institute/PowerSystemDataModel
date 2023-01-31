/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.GraphicSourceException;
import edu.ie3.datamodel.exceptions.RawInputDataException;
import edu.ie3.datamodel.io.factory.FactoryData;
import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputEntityData;
import edu.ie3.datamodel.io.factory.input.graphics.LineGraphicInputFactory;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputEntityData;
import edu.ie3.datamodel.io.factory.input.graphics.NodeGraphicInputFactory;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.GraphicSource;
import edu.ie3.datamodel.io.source.RawGridSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.container.GraphicElements;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.utils.options.Try;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of the {@link GraphicSource} interface to read {@link NodeGraphicInput} and {@link
 * LineGraphicInput} entities from .csv files
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

  /** {@inheritDoc} */
  @Override
  public GraphicElements getGraphicElements() throws RawInputDataException {

    // read all needed entities
    /// start with types and operators
    Set<OperatorInput> operators = typeSource.getOperators();
    Set<LineTypeInput> lineTypes = typeSource.getLineTypes();

    Set<NodeInput> nodes = rawGridSource.getNodes(operators);
    Set<LineInput> lines = rawGridSource.getLines(nodes, lineTypes, operators);

    Try<Set<NodeGraphicInput>, RawInputDataException> nodeGraphics =
        Try.scanForExceptions(
            buildNodeGraphicEntityData(nodes)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(nodeGraphicInputFactory::get)
                .collect(Collectors.toSet()),
            NodeGraphicInput.class);

    Try<Set<LineGraphicInput>, RawInputDataException> lineGraphics =
        Try.scanForExceptions(
            buildLineGraphicEntityData(lines)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(lineGraphicInputFactory::get)
                .collect(Collectors.toSet()),
            LineGraphicInput.class);

    List<RawInputDataException> exceptions = new ArrayList<>();

    if (nodeGraphics.isFailure()) {
      exceptions.add(nodeGraphics.getException());
    }
    if (lineGraphics.isFailure()) {
      exceptions.add(lineGraphics.getException());
    }

    if (exceptions.size() > 0) {
      throw new GraphicSourceException(exceptions.size() + "error(s) occurred.", exceptions);
    } else {
      // if everything is fine, return a GraphicElements instance
      return new GraphicElements(nodeGraphics.getData(), lineGraphics.getData());
    }
  }
  /** {@inheritDoc} */
  @Override
  public Set<NodeGraphicInput> getNodeGraphicInput() throws RawInputDataException {
    return getNodeGraphicInput(rawGridSource.getNodes(typeSource.getOperators()));
  }

  /**
   * {@inheritDoc}
   *
   * <p>If the set of {@link NodeInput} entities is not exhaustive for all available {@link
   * NodeGraphicInput} entities or if an error during the building process occurs, all entities that
   * has been able to be built are returned and the not-built ones are ignored (= filtered out).
   */
  @Override
  public Set<NodeGraphicInput> getNodeGraphicInput(Set<NodeInput> nodes)
      throws RawInputDataException {
    return Try.scanForExceptions(
            buildNodeGraphicEntityData(nodes)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(nodeGraphicInputFactory::get)
                .collect(Collectors.toSet()),
            NodeGraphicInput.class)
        .get();
  }

  /** {@inheritDoc} */
  @Override
  public Set<LineGraphicInput> getLineGraphicInput() throws RawInputDataException {
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
  @Override
  public Set<LineGraphicInput> getLineGraphicInput(Set<LineInput> lines)
      throws RawInputDataException {
    return Try.scanForExceptions(
            buildLineGraphicEntityData(lines)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(lineGraphicInputFactory::get)
                .collect(Collectors.toSet()),
            LineGraphicInput.class)
        .get();
  }

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
  private Stream<Optional<NodeGraphicInputEntityData>> buildNodeGraphicEntityData(
      Set<NodeInput> nodes) {
    return buildStreamWithFieldsToAttributesMap(NodeGraphicInput.class, connector)
        .map(mapWithRowIndex -> buildNodeGraphicEntityData(mapWithRowIndex, nodes));
  }

  private Optional<NodeGraphicInputEntityData> buildNodeGraphicEntityData(
      FactoryData.MapWithRowIndex mapWithRowIndex, Set<NodeInput> nodes) {

    // get the node of the entity
    String nodeUuid = mapWithRowIndex.fieldsToAttribute().get(NODE);
    Optional<NodeInput> node = findFirstEntityByUuid(nodeUuid, nodes);

    // if the node is not present we return an empty element and
    // log a warning
    if (node.isEmpty()) {
      logSkippingWarning(
          NodeGraphicInput.class.getSimpleName(),
          mapWithRowIndex.fieldsToAttribute().get("uuid"),
          "no id (graphic entities don't have one)",
          NODE + ": " + nodeUuid);
      return Optional.empty();
    }

    // remove fields that are passed as objects to constructor
    mapWithRowIndex.fieldsToAttribute().keySet().remove(NODE);

    return Optional.of(new NodeGraphicInputEntityData(mapWithRowIndex, node.get()));
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
  private Stream<Optional<LineGraphicInputEntityData>> buildLineGraphicEntityData(
      Set<LineInput> lines) {
    return buildStreamWithFieldsToAttributesMap(LineGraphicInput.class, connector)
        .map(mapWithRowIndex -> buildLineGraphicEntityData(mapWithRowIndex, lines));
  }

  private Optional<LineGraphicInputEntityData> buildLineGraphicEntityData(
      FactoryData.MapWithRowIndex mapWithRowIndex, Set<LineInput> lines) {

    // get the node of the entity
    String lineUuid = mapWithRowIndex.fieldsToAttribute().get("line");
    Optional<LineInput> line = findFirstEntityByUuid(lineUuid, lines);

    // if the node is not present we return an empty element and
    // log a warning
    if (line.isEmpty()) {
      logSkippingWarning(
          LineGraphicInput.class.getSimpleName(),
          mapWithRowIndex.fieldsToAttribute().get("uuid"),
          "no id (graphic entities don't have one)",
          "line: " + lineUuid);
      return Optional.empty();
    }

    // remove fields that are passed as objects to constructor
    mapWithRowIndex.fieldsToAttribute().keySet().remove("line");

    return Optional.of(new LineGraphicInputEntityData(mapWithRowIndex, line.get()));
  }
}
