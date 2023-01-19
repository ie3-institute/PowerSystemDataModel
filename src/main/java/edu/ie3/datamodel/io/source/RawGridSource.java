/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.container.RawGridElements;
import edu.ie3.datamodel.models.input.system.type.EvTypeInput;

import javax.xml.crypto.Data;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Interface that provides the capability to build entities that are hold by a {@link
 * RawGridElements} as well as the {@link RawGridElements} container as well from different data
 * sources e.g. .csv files or databases.
 *
 * @version 0.1
 * @since 08.04.20
 */
public class RawGridSource {

  TypeSource typeSource;

  public RawGridSource(TypeSource _typeSource) {
    this.typeSource = _typeSource;
  }

  /**
   * Should return either a consistent instance of {@link RawGridElements} wrapped in {@link
   * Optional} or an empty {@link Optional}. The decision to use {@link Optional} instead of
   * returning the {@link RawGridElements} instance directly is motivated by the fact, that a {@link
   * RawGridElements} is a container instance that depends on several other entities. Without being
   * complete, it is useless for further processing.
   *
   * <p>Hence, whenever at least one entity {@link RawGridElements} depends on cannot be provided,
   * {@link Optional#empty()} should be returned and extensive logging should provide enough
   * information to debug the error and fix the persistent data that has been failed to processed.
   *
   * <p>Furthermore, it is expected, that the specific implementation of this method ensures not
   * only the completeness of the resulting {@link RawGridElements} instance, but also its validity
   * e.g. in the sense that not duplicate UUIDs exist within all entities contained in the returning
   * instance.
   *
   * @return either a valid, complete {@link RawGridElements} optional or {@link Optional#empty()}
   */
  public Optional<RawGridElements> getGridData() { return null; }

  /**
   * Returns a unique set of {@link NodeInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link NodeInput} which has to be checked manually,
   * as {@link NodeInput#equals(Object)} is NOT restricted on the uuid of {@link NodeInput}.
   *
   * @return a set of object and uuid unique {@link NodeInput} entities
   */
  public Set<NodeInput> getNodes() {
    return getNodes(typeSource.getOperators());
  }

  /**
   * Returns a set of {@link NodeInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * NodeInput} which has to be checked manually, as {@link NodeInput#equals(Object)} is NOT
   * restricted on the uuid of {@link NodeInput}.
   *
   * <p>In contrast to {@link #getNodes} this interface provides the ability to pass in an already
   * existing set of {@link OperatorInput} entities, the {@link NodeInput} instances depend on.
   * Doing so, already loaded nodes can be recycled to improve performance and prevent unnecessary
   * loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @return a set of object and uuid unique {@link NodeInput} entities
   */
  public Set<NodeInput> getNodes(Set<OperatorInput> operators) {
    return buildEntities(Transformer2WTypeInput.class, RawGridSourceFactories.getNodeInputFactory());
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
  public Set<LineInput> getLines() {
    return buildEntities(Transformer2WTypeInput.class, RawGridSourceFactories.getTransformer2WTypeInputFactory());
  }

  /**
   * Returns a set of {@link LineInput} instances. This set has to be unique in the sense of object
   * uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided {@link
   * LineInput} which has to be checked manually, as {@link LineInput#equals(Object)} is NOT
   * restricted on the uuid of {@link LineInput}.
   *
   * <p>In contrast to {@link #getNodes} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link LineTypeInput} and {@link OperatorInput} entities,
   * the {@link LineInput} instances depend on. Doing so, already loaded nodes, line types and
   * operators can be recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @param lineTypeInputs a set of object and uuid unique {@link LineTypeInput} entities
   * @return a set of object and uuid unique {@link LineInput} entities
   */
  public Set<LineInput> getLines(
      Set<NodeInput> nodes, Set<LineTypeInput> lineTypeInputs, Set<OperatorInput> operators) {
    return buildEntities(Transformer2WTypeInput.class, RawGridSourceFactories.getTransformer2WTypeInputFactory());
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
  public Set<Transformer2WInput> get2WTransformers() {
    return buildEntities(Transformer2WTypeInput.class, RawGridSourceFactories.getTransformer2WTypeInputFactory());
  }

  /**
   * Returns a set of {@link Transformer2WInput} instances. This set has to be unique in the sense
   * of object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link Transformer2WInput} which has to be checked manually, as {@link
   * Transformer2WInput#equals(Object)} is NOT restricted on the uuid of {@link Transformer2WInput}.
   *
   * <p>In contrast to {@link #getNodes()} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link Transformer2WTypeInput} and {@link OperatorInput}
   * entities, the {@link Transformer2WInput} instances depend on. Doing so, already loaded nodes,
   * line types and operators can be recycled to improve performance and prevent unnecessary loading
   * operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
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
      Set<OperatorInput> operators) {
    return buildEntities(Transformer2WTypeInput.class, RawGridSourceFactories.getTransformer2WTypeInputFactory());
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
  public Set<Transformer3WInput> get3WTransformers() {
    return buildEntities(Transformer2WTypeInput.class, RawGridSourceFactories.getTransformer2WTypeInputFactory());
  }

  /**
   * Returns a set of {@link Transformer3WInput} instances. This set has to be unique in the sense
   * of object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link Transformer3WInput} which has to be checked manually, as {@link
   * Transformer3WInput#equals(Object)} is NOT restricted on the uuid of {@link Transformer3WInput}.
   *
   * <p>In contrast to {@link #getNodes()} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput}, {@link Transformer3WTypeInput} and {@link OperatorInput}
   * entities, the {@link Transformer3WInput} instances depend on. Doing so, already loaded nodes,
   * line types and operators can be recycled to improve performance and prevent unnecessary loading
   * operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
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
      Set<OperatorInput> operators) {
    return buildEntities(Transformer2WTypeInput.class, RawGridSourceFactories.getTransformer2WTypeInputFactory());
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
  public Set<SwitchInput> getSwitches() {
    return buildEntities(Transformer2WTypeInput.class, RawGridSourceFactories.getTransformer2WTypeInputFactory());
  }

  /**
   * Returns a set of {@link SwitchInput} instances. This set has to be unique in the sense of
   * object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link SwitchInput} which has to be checked manually, as {@link SwitchInput#equals(Object)} is
   * NOT restricted on the uuid of {@link SwitchInput}.
   *
   * <p>In contrast to {@link #getNodes()} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link SwitchInput}
   * instances depend on. Doing so, already loaded nodes, line types and operators can be recycled
   * to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link SwitchInput} entities
   */
  public Set<SwitchInput> getSwitches(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return buildEntities(Transformer2WTypeInput.class, RawGridSourceFactories.getTransformer2WTypeInputFactory());
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
  public Set<MeasurementUnitInput> getMeasurementUnits() {
    return buildEntities(Transformer2WTypeInput.class, RawGridSourceFactories.getTransformer2WTypeInputFactory());
  }

  /**
   * Returns a set of {@link MeasurementUnitInput} instances. This set has to be unique in the sense
   * of object uniqueness but also in the sense of {@link java.util.UUID} uniqueness of the provided
   * {@link MeasurementUnitInput} which has to be checked manually, as {@link
   * MeasurementUnitInput#equals(Object)} is NOT restricted on the uuid of {@link
   * MeasurementUnitInput}.
   *
   * <p>In contrast to {@link #getNodes()} this interface provides the ability to pass in an already
   * existing set of {@link NodeInput} and {@link OperatorInput} entities, the {@link
   * MeasurementUnitInput} instances depend on. Doing so, already loaded nodes, line types and
   * operators can be recycled to improve performance and prevent unnecessary loading operations.
   *
   * <p>If something fails during the creation process it's up to the concrete implementation of an
   * empty set or a set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @param nodes a set of object and uuid unique {@link NodeInput} entities
   * @return a set of object and uuid unique {@link MeasurementUnitInput} entities
   */
  public Set<MeasurementUnitInput> getMeasurementUnits(Set<NodeInput> nodes, Set<OperatorInput> operators) {
    return buildEntities(EvTypeInput.class, RawGridSourceFactories.getSystemParticipantTypeInputFactory());
  }

  //public abstract <T extends InputEntity> Stream<Map<String, String>> getSourceData(Class<T> entityClass);

  public <T extends InputEntity> Set<T> buildEntities(
          Class<T> entityClass,
          EntityFactory<? extends InputEntity, SimpleEntityData> factory
  ) {
    return getSourceData(entityClass)
            .map(
                    fieldsToAttributes -> {
                      SimpleEntityData data = new SimpleEntityData(fieldsToAttributes, entityClass);
                      return (Optional<T>) factory.get(data);
                    })
            .flatMap(Optional::stream)
            .collect(Collectors.toSet());
  }

}
