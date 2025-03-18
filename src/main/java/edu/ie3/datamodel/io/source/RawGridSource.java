/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.*;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.utils.Try;
import java.util.*;
import java.util.stream.Stream;

/**
 * Implementation that provides the capability to build entities held by {@link RawGridElements} as
 * well as the {@link RawGridElements} container from different data sources e.g. .csv files or
 * databases.
 *
 * @version 0.1
 * @since 08.04.20
 */
public class RawGridSource extends AssetEntitySource {

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
    super(dataSource);
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
  public void validate() throws ValidationException {
    Try.scanStream(
            Stream.of(
                validate(NodeInput.class, dataSource, nodeInputFactory),
                validate(LineInput.class, dataSource, lineInputFactory),
                validate(Transformer2WInput.class, dataSource, transformer2WInputFactory),
                validate(Transformer3WInput.class, dataSource, transformer3WInputFactory),
                validate(SwitchInput.class, dataSource, switchInputFactory),
                validate(MeasurementUnitInput.class, dataSource, measurementUnitInputFactory)),
            "Validation")
        .transformF(FailedValidationException::new)
        .getOrThrow();
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
   * @return a valid, complete {@link RawGridElements}
   * @throws SourceException on error
   */
  public RawGridElements getGridData() throws SourceException {
    /* read all needed entities start with the types and operators */
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    Map<UUID, LineTypeInput> lineTypes = typeSource.getLineTypes();

    /* assets */
    Map<UUID, NodeInput> nodes = getNodes(operators);
    Map<UUID, LineInput> lines = getLines(operators, nodes, lineTypes);

    return getGridData(operators, nodes, lines);
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
   * <p>In contrast to {@link #getGridData()}, this method provides the ability to pass in already
   * existing input objects that this method depends on. Doing so, already loaded operators, nodes
   * and lines can be recycled to improve performance and prevent unnecessary loading operations.
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param lines a map of UUID to object- and uuid-unique {@link LineInput} entities
   * @return a valid, complete {@link RawGridElements}
   * @throws SourceException on error
   */
  public RawGridElements getGridData(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes, Map<UUID, LineInput> lines)
      throws SourceException {
    /* read all needed entities start with the types and operators */
    Map<UUID, Transformer2WTypeInput> transformer2WTypeInputs = typeSource.getTransformer2WTypes();
    Map<UUID, Transformer3WTypeInput> transformer3WTypeInputs = typeSource.getTransformer3WTypes();

    /* assets */
    Try<Set<Transformer2WInput>, SourceException> transformer2WInputs =
        Try.of(
            () -> get2WTransformers(operators, nodes, transformer2WTypeInputs),
            SourceException.class);
    Try<Set<Transformer3WInput>, SourceException> transformer3WInputs =
        Try.of(
            () -> get3WTransformers(operators, nodes, transformer3WTypeInputs),
            SourceException.class);
    Try<Set<SwitchInput>, SourceException> switches =
        Try.of(() -> getSwitches(operators, nodes), SourceException.class);
    Try<Set<MeasurementUnitInput>, SourceException> measurementUnits =
        Try.of(() -> getMeasurementUnits(operators, nodes), SourceException.class);

    List<SourceException> exceptions =
        Try.getExceptions(
            List.of(transformer2WInputs, transformer3WInputs, switches, measurementUnits));

    if (!exceptions.isEmpty()) {
      throw new RawGridException(
          exceptions.size() + " error(s) occurred while initializing raw grid. ", exceptions);
    } else {
      /* build and return the grid if it is not empty */
      // getOrThrow should not throw an exception in this context, because all exception are
      // filtered and thrown before
      return new RawGridElements(
          new HashSet<>(nodes.values()),
          new HashSet<>(lines.values()),
          transformer2WInputs.getOrThrow(),
          transformer3WInputs.getOrThrow(),
          switches.getOrThrow(),
          measurementUnits.getOrThrow());
    }
  }

  /**
   * Returns a unique set of {@link NodeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link NodeInput} which has to be checked manually,
   * as {@link NodeInput#equals(Object)} is NOT restricted on the uuid of {@link NodeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link NodeInput} entities
   */
  public Map<UUID, NodeInput> getNodes() throws SourceException {
    return getNodes(typeSource.getOperators());
  }

  /**
   * Returns a unique set of {@link NodeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link NodeInput} which has to be checked manually,
   * as {@link NodeInput#equals(Object)} is NOT restricted on the uuid of {@link NodeInput}.
   *
   * <p>In contrast to {@link #getNodes} this method provides the ability to pass in an already
   * existing set of {@link OperatorInput} entities, the {@link NodeInput} instances depend on.
   * Doing so, already loaded nodes can be recycled to improve performance and prevent unnecessary
   * loading operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @return a map of UUID to object- and uuid-unique {@link NodeInput} entities
   */
  public Map<UUID, NodeInput> getNodes(Map<UUID, OperatorInput> operators) throws SourceException {
    return getEntities(
            NodeInput.class,
            dataSource,
            nodeInputFactory,
            data -> assetEnricher.apply(data, operators))
        .collect(toMap());
  }

  /**
   * Returns a unique set of {@link LineInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link LineInput} which has to be checked manually,
   * as {@link LineInput#equals(Object)} is NOT restricted on the uuid of {@link LineInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link LineInput} entities
   */
  public Map<UUID, LineInput> getLines() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    return getLines(operators, getNodes(operators), typeSource.getLineTypes());
  }

  /**
   * Returns a unique set of {@link LineInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link LineInput} which has to be checked manually,
   * as {@link LineInput#equals(Object)} is NOT restricted on the uuid of {@link LineInput}.
   *
   * <p>In contrast to {@link #getNodes} this method provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link LineTypeInput} and {@link OperatorInput} entities,
   * the {@link LineInput} instances depend on. Doing so, already loaded nodes, line types and
   * operators can be recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param lineTypeInputs a map of UUID to object- and uuid-unique {@link LineTypeInput} entities
   * @return a map of UUID to object- and uuid-unique {@link LineInput} entities
   */
  public Map<UUID, LineInput> getLines(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, LineTypeInput> lineTypeInputs)
      throws SourceException {
    return getTypedConnectorEntities(
            LineInput.class, dataSource, lineInputFactory, operators, nodes, lineTypeInputs)
        .collect(toMap());
  }

  /**
   * Returns a unique set of {@link Transformer2WInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link Transformer2WInput} which has to be checked
   * manually, as {@link Transformer2WInput#equals(Object)} is NOT restricted on the uuid of {@link
   * Transformer2WInput}.
   *
   * @return a set of object- and uuid-unique {@link Transformer2WInput} entities
   */
  public Set<Transformer2WInput> get2WTransformers() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    return get2WTransformers(operators, getNodes(operators), typeSource.getTransformer2WTypes());
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
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param transformer2WTypes a map of UUID to object- and uuid-unique {@link
   *     Transformer2WTypeInput} entities
   * @return a set of object- and uuid-unique {@link Transformer2WInput} entities
   */
  public Set<Transformer2WInput> get2WTransformers(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, Transformer2WTypeInput> transformer2WTypes)
      throws SourceException {
    return getTypedConnectorEntities(
            Transformer2WInput.class,
            dataSource,
            transformer2WInputFactory,
            operators,
            nodes,
            transformer2WTypes)
        .collect(toSet());
  }

  /**
   * Returns a unique set of {@link Transformer3WInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link Transformer3WInput} which has to be checked
   * manually, as {@link Transformer3WInput#equals(Object)} is NOT restricted on the uuid of {@link
   * Transformer3WInput}.
   *
   * @return a set of object- and uuid-unique {@link Transformer3WInput} entities
   */
  public Set<Transformer3WInput> get3WTransformers() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    return get3WTransformers(operators, getNodes(operators), typeSource.getTransformer3WTypes());
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
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @param transformer3WTypes a map of UUID to object- and uuid-unique {@link
   *     Transformer3WTypeInput} entities
   * @return a set of object- and uuid-unique {@link Transformer3WInput} entities
   */
  public Set<Transformer3WInput> get3WTransformers(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, Transformer3WTypeInput> transformer3WTypes)
      throws SourceException {
    WrappedFunction<EntityData, Transformer3WInputEntityData> builder =
        data ->
            connectorEnricher
                .andThen(
                    biEnrich(
                        "nodeC",
                        nodes,
                        TYPE,
                        transformer3WTypes,
                        Transformer3WInputEntityData::new))
                .apply(data, operators, nodes);

    return getEntities(Transformer3WInput.class, dataSource, transformer3WInputFactory, builder)
        .collect(toSet());
  }

  /**
   * Returns a unique set of {@link SwitchInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link SwitchInput} which has to be checked
   * manually, as {@link SwitchInput#equals(Object)} is NOT restricted on the uuid of {@link
   * SwitchInput}.
   *
   * @return a set of object- and uuid-unique {@link SwitchInput} entities
   */
  public Set<SwitchInput> getSwitches() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    return getSwitches(operators, getNodes(operators));
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
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @return a set of object- and uuid-unique {@link SwitchInput} entities
   */
  public Set<SwitchInput> getSwitches(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes) throws SourceException {
    return getEntities(
            SwitchInput.class,
            dataSource,
            switchInputFactory,
            data -> connectorEnricher.apply(data, operators, nodes))
        .collect(toSet());
  }

  /**
   * Returns a unique set of {@link MeasurementUnitInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link MeasurementUnitInput} which has to be checked
   * manually, as {@link MeasurementUnitInput#equals(Object)} is NOT restricted on the uuid of
   * {@link MeasurementUnitInput}.
   *
   * @return a set of object- and uuid-unique {@link MeasurementUnitInput} entities
   */
  public Set<MeasurementUnitInput> getMeasurementUnits() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    return getMeasurementUnits(operators, getNodes(operators));
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
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @param nodes a map of UUID to object- and uuid-unique {@link NodeInput} entities
   * @return a set of object- and uuid-unique {@link MeasurementUnitInput} entities
   */
  public Set<MeasurementUnitInput> getMeasurementUnits(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes) throws SourceException {
    return getEntities(
            MeasurementUnitInput.class,
            dataSource,
            measurementUnitInputFactory,
            data -> nodeAssetEnricher.apply(data, operators, nodes))
        .collect(toSet());
  }
}
