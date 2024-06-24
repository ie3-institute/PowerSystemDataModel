/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.models.input.OperatorInput.NO_OPERATOR_ASSIGNED;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.input.*;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.ConnectorInput;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Class that provides all functionalities to build asset entities */
public abstract class AssetEntitySource extends EntitySource {

  protected static final Logger log = LoggerFactory.getLogger(AssetEntitySource.class);

  protected final DataSource dataSource;

  // field names
  protected static final String OPERATOR = "operator";
  protected static final String NODE = "node";
  protected static final String NODE_A = "nodeA";
  protected static final String NODE_B = "nodeB";
  protected static final String TYPE = "type";

  // enriching functions
  protected static final EnrichFunction<EntityData, OperatorInput, AssetInputEntityData>
      assetEnricher =
          (data, operators) ->
              enrichWithDefault(
                      OPERATOR, operators, NO_OPERATOR_ASSIGNED, AssetInputEntityData::new)
                  .apply(data);

  protected static final BiEnrichFunction<
          EntityData, OperatorInput, NodeInput, NodeAssetInputEntityData>
      nodeAssetEnricher =
          (data, operators, nodes) ->
              assetEnricher
                  .andThen(enrich(NODE, nodes, NodeAssetInputEntityData::new))
                  .apply(data, operators);

  protected static final BiEnrichFunction<
          EntityData, OperatorInput, NodeInput, ConnectorInputEntityData>
      connectorEnricher =
          (data, operators, nodes) ->
              assetEnricher
                  .andThen(biEnrich(NODE_A, nodes, NODE_B, nodes, ConnectorInputEntityData::new))
                  .apply(data, operators);

  protected AssetEntitySource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=

  /**
   * Method to build typed connector entities.
   *
   * @param entityClass class of the entity
   * @param dataSource source for the data
   * @param factory to build the entity
   * @param operators map: uuid to {@link OperatorInput}
   * @param nodes map: uuid to {@link NodeInput}
   * @param types map: uuid to {@link AssetTypeInput}
   * @return a stream of {@link ConnectorInput}s
   * @param <E> type of connector input
   * @param <T> type of asset types
   * @throws SourceException if an error happens during reading
   */
  protected static <E extends ConnectorInput, T extends AssetTypeInput>
      Stream<E> getTypedConnectorEntities(
          Class<E> entityClass,
          DataSource dataSource,
          ConnectorInputEntityFactory<E, TypedConnectorInputEntityData<T>> factory,
          Map<UUID, OperatorInput> operators,
          Map<UUID, NodeInput> nodes,
          Map<UUID, T> types)
          throws SourceException {
    return getEntities(
        entityClass,
        dataSource,
        factory,
        data -> connectorEnricher.andThen(enrichConnector(types)).apply(data, operators, nodes));
  }

  /**
   * Builds a function for enriching {@link ConnectorInputEntityData} with types.
   *
   * @param types all known types
   * @return a typed entity data
   * @param <T> type of types
   */
  private static <T extends AssetTypeInput, D extends ConnectorInputEntityData>
      WrappedFunction<D, TypedConnectorInputEntityData<T>> enrichConnector(Map<UUID, T> types) {
    BiFunction<D, T, TypedConnectorInputEntityData<T>> typeEnricher =
        TypedConnectorInputEntityData::new;
    return entityData -> enrich(TYPE, types, typeEnricher).apply(entityData);
  }
}
