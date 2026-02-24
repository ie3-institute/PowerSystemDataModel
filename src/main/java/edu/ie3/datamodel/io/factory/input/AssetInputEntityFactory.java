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
import java.util.UUID;

/**
 * Abstract factory class that can be extended in order for creating {@link AssetInput} entities
 * with {@link AssetInputEntityData} data objects.
 *
 * @param <T> Type of entity that this factory can create. Must be a subclass of {@link AssetInput}
 * @param <D> Type of data class that is required for entity creation
 * @since 19.02.20
 */
public abstract class AssetInputEntityFactory<T extends AssetInput, D extends AssetInputEntityData>
    extends UniqueEntityFactory<T, D> {

  @SafeVarargs
  protected AssetInputEntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }

  @Override
  protected T buildModel(D data) {
    UUID uuid = data.getUUID(UUID);
    String id = data.getField(ID);
    OperatorInput operator = data.getOperatorInput();
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
      D data, UUID uuid, String id, OperatorInput operator, OperationTime operationTime);

  /**
   * Creates an {@link OperationTime} from the entity data from attributes OPERATES_FROM and
   * OPERATES_UNTIL. Both or one of these can be empty or non-existing.
   *
   * @param data entity data to take the dates from
   * @return Operation time object
   */
  private static OperationTime buildOperationTime(AssetInputEntityData data) {
    final String from = data.getFieldOptional(OPERATES_FROM).orElse(null);
    final String until = data.getFieldOptional(OPERATES_UNTIL).orElse(null);

    OperationTime.OperationTimeBuilder builder = new OperationTime.OperationTimeBuilder();
    if (from != null && !from.trim().isEmpty()) builder.withStart(ZonedDateTime.parse(from));
    if (until != null && !until.trim().isEmpty()) builder.withEnd(ZonedDateTime.parse(until));

    return builder.build();
  }
}
