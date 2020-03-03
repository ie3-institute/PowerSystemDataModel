/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.io.factory.input;

import edu.ie3.exceptions.FactoryException;
import edu.ie3.io.factory.EntityData;
import edu.ie3.io.factory.EntityFactory;
import edu.ie3.models.OperationTime;
import edu.ie3.models.input.AssetInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.SystemParticipantInput;
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
   * In addition to the checks performed by {@link EntityFactory#validateParameters(EntityData,
   * Set[])}, integrity of operational data is checked here.
   *
   * @param data the entity containing at least the entity class as well a mapping of the provided
   *     field name strings to its value (e.g. a headline of a csv -> column values)
   * @param fieldSets a set containing all available constructor combinations as field names
   * @return the index of the set in the fieldSets array that fits the provided entity data
   */
  @Override
  protected int validateParameters(D data, Set<String>... fieldSets) {
    if ((data.containsKey(OPERATES_FROM)
        || data.containsKey(OPERATES_UNTIL)
        || data.hasOperatorInput())) {

      if ((data.containsKey(OPERATES_FROM) || data.containsKey(OPERATES_UNTIL))
          && !data.hasOperatorInput())
        throw new FactoryException(
            "Operation time (fields '"
                + OPERATES_FROM
                + "' and '"
                + OPERATES_UNTIL
                + "') are passed, but operator input is not.");

      if (data.hasOperatorInput()
          && (!data.containsKey(OPERATES_FROM) || !data.containsKey(OPERATES_UNTIL)))
        throw new FactoryException(
            "Operator input is passed, but operation time (fields '"
                + OPERATES_FROM
                + "' and '"
                + OPERATES_UNTIL
                + "') is not.");
    }

    return super.validateParameters(data, fieldSets);
  }

  @Override
  protected List<Set<String>> getFields(D data) {
    Set<String> minConstructorParams = newSet(UUID, ID);
    Set<String> optConstructorParams =
        expandSet(minConstructorParams, OPERATES_FROM, OPERATES_UNTIL);

    final String[] additionalFields = getAdditionalFields();

    minConstructorParams = expandSet(minConstructorParams, additionalFields);
    optConstructorParams = expandSet(optConstructorParams, additionalFields);
    return Arrays.asList(minConstructorParams, optConstructorParams);
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
    Optional<OperatorInput> operatorInput = data.getOperatorInput();
    Optional<OperationTime> operationTime = buildOperationTime(data);

    final boolean isOperational = operatorInput.isPresent() && operationTime.isPresent();

    return isOperational
        ? buildModel(data, uuid, id, operatorInput.get(), operationTime.get())
        : buildModel(data, uuid, id);
  }

  /**
   * Creates operated asset entity with given parameters
   *
   * @param data entity data
   * @param uuid UUID of the input entity
   * @param id ID
   * @param operatorInput Operator of the asset
   * @param operationTime time in which the entity is operated
   * @return newly created asset object
   */
  protected abstract T buildModel(
      D data, UUID uuid, String id, OperatorInput operatorInput, OperationTime operationTime);

  /**
   * Creates non-operated asset entity with given parameters
   *
   * @param data entity data
   * @param uuid UUID of the input entity
   * @param id ID
   * @return newly created asset object
   */
  protected abstract T buildModel(D data, UUID uuid, String id);

  /**
   * Creates an {@link OperationTime} from the entity data iff the required attributes OPERATES_FROM
   * and OPERATES_UNTIL are present. Both or one of these can be empty, which results in an
   * unlimited operation interval. If at least one of the attributes is missing, an empty Optional
   * is returned.
   *
   * @param data entity data to take the dates from
   * @return Operation time object if attributes are present, empty Optional otherwise.
   */
  private static Optional<OperationTime> buildOperationTime(AssetInputEntityData data) {
    if (!data.containsKey(OPERATES_FROM) || !data.containsKey(OPERATES_UNTIL))
      return Optional.empty();

    final String from = data.getField(OPERATES_FROM);
    final String until = data.getField(OPERATES_UNTIL);

    OperationTime.OperationTimeBuilder builder = new OperationTime.OperationTimeBuilder();
    if (!from.trim().isEmpty()) builder.withStart(ZonedDateTime.parse(from));
    if (!until.trim().isEmpty()) builder.withEnd(ZonedDateTime.parse(until));

    return Optional.of(builder.build());
  }
}
