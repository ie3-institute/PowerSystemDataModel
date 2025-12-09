/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.models.input.OperatorInput.NO_OPERATOR_ASSIGNED;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.ConnectorInput;
import edu.ie3.datamodel.utils.Try;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Class that provides all functionalities to build asset entities */
public abstract class AssetEntitySource extends EntitySource {

  protected static final Logger log = LoggerFactory.getLogger(AssetEntitySource.class);

  protected final DataSource dataSource;

  // enriching functions
  protected static BuildFunction<AssetInput> assetBuilder(Map<UUID, OperatorInput> operators) {
    return entityData ->
        entityData
            .zip(uniqueEntityBuilder)
            .map(
                pair -> {
                  EntityData data = pair.getLeft();
                  OperatorInput operatorInput =
                      extractWithDefault(
                          data, AssetInput.OPERATOR, operators, NO_OPERATOR_ASSIGNED);

                  String from = data.getFieldOptional(AssetInput.OPERATES_FROM).orElse(null);
                  String until = data.getFieldOptional(AssetInput.OPERATES_UNTIL).orElse(null);

                  OperationTime.OperationTimeBuilder builder =
                      new OperationTime.OperationTimeBuilder();
                  if (from != null && !from.trim().isEmpty())
                    builder.withStart(ZonedDateTime.parse(from));
                  if (until != null && !until.trim().isEmpty())
                    builder.withEnd(ZonedDateTime.parse(until));

                  OperationTime time = builder.build();

                  return new AssetInput(
                      pair.getRight(), data.getField(AssetInput.ID), operatorInput, time) {
                    @Override
                    public AssetInputCopyBuilder<?> copy() {
                      return null;
                    }
                  };
                },
                SourceException.class);
  }

  protected static BuildFunction<ConnectorInput> connectorBuilder(
      Map<UUID, OperatorInput> operators, Map<UUID, NodeInput> nodes) {
    return entityData ->
        entityData
            .zip(assetBuilder(operators))
            .map(
                pair -> {
                  EntityData data = pair.getLeft();
                  NodeInput nodeA = extractFunction(data, ConnectorInput.NODE_A, nodes);
                  NodeInput nodeB = extractFunction(data, ConnectorInput.NODE_B, nodes);

                  int parallelDevices =
                      Try.of(
                              () -> data.getInt(ConnectorInput.PARALLEL_DEVICES),
                              SourceException.class)
                          .convert(Function.identity(), f -> 1);

                  return new ConnectorInput(pair.getRight(), nodeA, nodeB, parallelDevices) {
                    @Override
                    public ConnectorInputCopyBuilder<?> copy() {
                      return null;
                    }
                  };
                },
                SourceException.class);
  }

  protected AssetEntitySource(DataSource dataSource) {
    this.dataSource = dataSource;
  }
}
