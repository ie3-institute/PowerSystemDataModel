/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.io.naming.EntityFieldNames.*;

import edu.ie3.datamodel.exceptions.*;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.*;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput;
import edu.ie3.datamodel.utils.GridAndGeoUtils;
import edu.ie3.datamodel.utils.Try;
import java.util.*;

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

  public RawGridSource(TypeSource typeSource, DataSource dataSource) {
    super(dataSource);
    this.typeSource = typeSource;
  }

  @Override
  public void validate() throws ValidationException {
    validate(
        dataSource,
        NodeInput.class,
        LineInput.class,
        Transformer2WInput.class,
        Transformer3WInput.class,
        SwitchInput.class,
        MeasurementUnitInput.class);
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
        Try.getExceptions(transformer2WInputs, transformer3WInputs, switches, measurementUnits);

    if (!exceptions.isEmpty()) {
      throw new RawGridException(
          "Exception(s) occurred in "
              + exceptions.size()
              + " input file(s) while initializing raw grid.",
          exceptions);
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
    return getEntities(NodeInput.class, dataSource, nodeBuildFunction(operators)).collect(toMap());
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
    return getEntities(
            LineInput.class, dataSource, lineBuildFunction(operators, nodes, lineTypeInputs))
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
    return getEntities(
            Transformer2WInput.class,
            dataSource,
            transformer2WBuildFunction(operators, nodes, transformer2WTypes))
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
    return getEntities(
            Transformer3WInput.class,
            dataSource,
            transformer3WBuildFunction(operators, nodes, transformer3WTypes))
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
    return getEntities(SwitchInput.class, dataSource, switchBuildFunction(operators, nodes))
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
            MeasurementUnitInput.class, dataSource, measurementBuildFunction(operators, nodes))
        .collect(toSet());
  }

  // build functions
  protected static BuildFunction<TransformerInput> transformerBuilder(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes) {
    return entityData ->
        entityData
            .zip(connectorBuilder(operators, nodes))
            .map(
                pair -> {
                  EntityData data = pair.getLeft();

                  return new TransformerInput(
                      pair.getRight(), data.getInt(TAP_POS), data.getBoolean(AUTO_TAP)) {
                    @Override
                    public TransformerInputCopyBuilder<? extends TransformerInputCopyBuilder<?>>
                        copy() {
                      return null;
                    }
                  };
                },
                SourceException.class);
  }

  protected static BuildFunction<NodeInput> nodeBuildFunction(Map<UUID, OperatorInput> operators) {
    return assetBuilder(operators)
        .with(
            pair -> {
              EntityData data = pair.getLeft();

              return new NodeInput(
                  pair.getRight(),
                  data.getQuantity(V_TARGET, StandardUnits.TARGET_VOLTAGE_MAGNITUDE),
                  data.getBoolean(SLACK),
                  data.getPoint(GEO_POSITION).orElse(NodeInput.DEFAULT_GEO_POSITION),
                  data.getVoltageLvl(VOLT_LVL.toLowerCase(), V_RATED.toLowerCase()),
                  data.getInt(SUBNET));
            });
  }

  protected static BuildFunction<LineInput> lineBuildFunction(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, LineTypeInput> types) {
    return connectorBuilder(operators, nodes)
        .with(
            pair -> {
              EntityData data = pair.getLeft();
              ConnectorInput connectorInput = pair.getRight();

              String olmString = data.getField(OLM_CHARACTERISTIC);
              OlmCharacteristicInput olmCharacteristic;

              try {
                olmCharacteristic =
                    !olmString.isEmpty()
                        ? new OlmCharacteristicInput(olmString)
                        : OlmCharacteristicInput.CONSTANT_CHARACTERISTIC;
              } catch (ParsingException e) {
                throw new FactoryException(
                    "Cannot parse the following overhead line monitoring characteristic: '"
                        + olmString
                        + "'",
                    e);
              }

              return new LineInput(
                  connectorInput,
                  extractFunction(data, TYPE, types),
                  data.getQuantity(LENGTH, StandardUnits.LINE_LENGTH),
                  data.getLineString(GEO_POSITION)
                      .orElse(
                          GridAndGeoUtils.buildSafeLineStringBetweenNodes(
                              connectorInput.getNodeA(), connectorInput.getNodeB())),
                  olmCharacteristic);
            });
  }

  protected static BuildFunction<Transformer2WInput> transformer2WBuildFunction(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, Transformer2WTypeInput> types) {
    return transformerBuilder(operators, nodes)
        .with(
            pair -> {
              try {
                return new Transformer2WInput(
                    pair.getRight(), extractFunction(pair.getLeft(), TYPE, types));
              } catch (IllegalArgumentException e) {
                throw new SourceException(e);
              }
            });
  }

  protected static BuildFunction<Transformer3WInput> transformer3WBuildFunction(
      Map<UUID, OperatorInput> operators,
      Map<UUID, NodeInput> nodes,
      Map<UUID, Transformer3WTypeInput> types) {
    return transformerBuilder(operators, nodes)
        .with(
            pair -> {
              EntityData data = pair.getLeft();

              try {
                return new Transformer3WInput(
                    pair.getRight(),
                    extractFunction(data, NODE_C, nodes),
                    extractFunction(data, TYPE, types));
              } catch (IllegalArgumentException e) {
                throw new SourceException(e);
              }
            });
  }

  protected static BuildFunction<SwitchInput> switchBuildFunction(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes) {
    return connectorBuilder(operators, nodes)
        .with(pair -> new SwitchInput(pair.getRight(), pair.getLeft().getBoolean(CLOSED)));
  }

  protected static BuildFunction<MeasurementUnitInput> measurementBuildFunction(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes) {
    return assetBuilder(operators)
        .with(
            pair -> {
              EntityData data = pair.getLeft();

              return new MeasurementUnitInput(
                  pair.getRight(),
                  extractFunction(data, NODE, nodes),
                  data.getBoolean(V_MAG),
                  data.getBoolean(V_ANG),
                  data.getBoolean(POWER),
                  data.getBoolean(REACTIVE_POWER));
            });
  }
}
