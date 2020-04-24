/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.input;

import edu.ie3.datamodel.io.factory.EntityFactory;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * Abstract factory class that can be extended in order for creating {@link AssetInput} entities
 * with {@link AssetInputEntityData} data objects.
 *
 * @param <T> Type of entity that this factory can create. Must be a subclass of {@link AssetInput}
 * @param <D> Type of data class that is required for entity creation
 * @since 19.02.20
 */
public abstract class AssetInputEntityFactory<T extends AssetInput, D extends AssetInputEntityData>
    extends EntityFactory<T, D> {

  private static final String UUID = "uuid";
  private static final String OPERATES_FROM = "operatesfrom";
  private static final String OPERATES_UNTIL = "operatesuntil";
  private static final String ID = "id";

  public AssetInputEntityFactory(Class<? extends T>... allowedClasses) {
    super(allowedClasses);
  }

  /**
   * Returns list of sets of attribute names that the entity requires to be built.
   *
   * <p>The mandatory attributes required to create an {@link AssetInput} are enhanced with custom
   * attribute names that each subclass factory determines in {@link #getAdditionalFields()}.
   *
   * @param data EntityData (or subclass) containing the data
   * @return list of possible attribute sets
   */
  @Override
  protected List<Set<String>> getFields(D data) {
    Set<String> constructorParamsMin = newSet(UUID, ID);
    Set<String> constructorParamsFrom = expandSet(constructorParamsMin, OPERATES_FROM);
    Set<String> constructorParamsUntil = expandSet(constructorParamsMin, OPERATES_UNTIL);
    Set<String> constructorParamsBoth = expandSet(constructorParamsFrom, OPERATES_UNTIL);

    final String[] additionalFields = getAdditionalFields();

    constructorParamsMin = expandSet(constructorParamsMin, additionalFields);
    constructorParamsFrom = expandSet(constructorParamsFrom, additionalFields);
    constructorParamsUntil = expandSet(constructorParamsUntil, additionalFields);
    constructorParamsBoth = expandSet(constructorParamsBoth, additionalFields);
    return Arrays.asList(
        constructorParamsMin, constructorParamsFrom, constructorParamsUntil, constructorParamsBoth);
  }

  /**
   * Returns fields other than the required fields of {@link SystemParticipantInput} that have to be
   * present.
   *
   * @return Array of field names, can be empty but not null
   */
  protected abstract String[] getAdditionalFields();

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
