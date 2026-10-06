/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.io.factory.UniqueEntityFactory;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * Abstract factory class that can be extended in order for creating {@link AssetInput} entities.
 *
 * @param <T> Type of entity that this factory can create. Must be a subclass of {@link AssetInput}
 * @since 19.02.20
 */
public abstract class AssetInputEntityFactory<T extends AssetInput>
    extends UniqueEntityFactory<T, T> {

  private final Map<UUID, OperatorInput> operators;

  @SafeVarargs
  protected AssetInputEntityFactory(
      Map<UUID, OperatorInput> operators, Class<? extends T>... allowedClasses) {
    super(allowedClasses);
    this.operators = operators;
  }

  @Override
  protected T buildModel(Map<String, String> data) {
    UUID uuid = getUUID(data, UUID);
    String id = getField(data, ID);
    OperatorInput operator =
        getEntity(data, OPERATOR, operators, OperatorInput.NO_OPERATOR_ASSIGNED);
    OperationTime operationTime = buildOperationTime(data);

    return buildModel(data, uuid, id, operator, operationTime);
  }

  /**
   * Creates asset input entity with given parameters
   *
   * @param data entity data
   * @param uuid UUID of the input entity
   * @param id ID
   * @param operator Operator of the asset
   * @param operationTime time in which the entity is operated
   * @return newly created asset object
   */
  protected abstract T buildModel(
      Map<String, String> data,
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime);

  /**
   * Creates an {@link OperationTime} from the entity data from attributes OPERATES_FROM and
   * OPERATES_UNTIL. Both or one of these can be empty or non-existing.
   *
   * @param data entity data to take the dates from
   * @return Operation time object
   */
  private OperationTime buildOperationTime(Map<String, String> data) {
    final String from = getFieldOptional(data, OPERATES_FROM).orElse(null);
    final String until = getFieldOptional(data, OPERATES_UNTIL).orElse(null);

    OperationTime.OperationTimeBuilder builder = new OperationTime.OperationTimeBuilder();
    if (from != null && !from.trim().isEmpty()) builder.withStart(ZonedDateTime.parse(from));
    if (until != null && !until.trim().isEmpty()) builder.withEnd(ZonedDateTime.parse(until));

    return builder.build();
  }
}
