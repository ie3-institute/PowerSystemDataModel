/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.io.factory.input.participant.EmInputFactory.PARENT_EM;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.io.factory.input.AssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.EmAssetInputEntityData;
import edu.ie3.datamodel.io.factory.input.participant.EmInputFactory;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.utils.Try;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnergyManagementSource extends EntitySource {

  private final TypeSource typeSource;

  private static final EmInputFactory emInputFactory = new EmInputFactory();

  public EnergyManagementSource(TypeSource typeSource, DataSource dataSource) {
    super(dataSource);
    this.typeSource = typeSource;
  }

  @Override
  public void validate() throws ValidationException {
    validate(EmInput.class, emInputFactory).getOrThrow();
  }

  /**
   * Returns a unique set of {@link EmInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EmInput} which has to be checked manually, as
   * {@link EmInput#equals(Object)} is NOT restricted on the UUID of {@link EmInput}.
   *
   * @return a map of UUID to {@link EmInput} entities
   */
  public Map<UUID, EmInput> getEmUnits() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    return getEmUnits(operators);
  }

  /**
   * This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EmInput} which has to be checked manually, as
   * {@link EmInput#equals(Object)} is NOT restricted on the UUID of {@link EmInput}.
   *
   * <p>In contrast to {@link #getEmUnits()} this method provides the ability to pass in an already
   * existing set of {@link OperatorInput} entities, the {@link EmInput} instances depend on. Doing
   * so, already loaded nodes can be recycled to improve performance and prevent unnecessary loading
   * operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   * @return a map of UUID to {@link EmInput} entities
   */
  public Map<UUID, EmInput> getEmUnits(Map<UUID, OperatorInput> operators) throws SourceException {
    return createEmInputs(buildAssetInputEntityData(EmInput.class, operators));
  }

  /**
   * Since each EM can itself be controlled by another EM, it does not suffice to link {@link
   * EmInput}s via {@link EntitySource#optionallyEnrichEntityData} as we do for system participants
   * in {@link SystemParticipantSource}. Instead, we use a recursive approach, starting with EMs at
   * root level (which are not EM-controlled themselves).
   *
   * @param assetEntityDataStream the data stream of {@link AssetInputEntityData} {@link Try}
   *     objects
   * @return a map of UUID to {@link EmInput} entities
   */
  private static Map<UUID, EmInput> createEmInputs(
      Stream<Try<AssetInputEntityData, SourceException>> assetEntityDataStream)
      throws SourceException {

    // Split stream by failures and EMs that are themselves EM-controlled on one side, and EMs at
    // root position (that have not failed so far) on the other side, which do not have parents per
    // definition.
    Map<Boolean, List<Try<AssetInputEntityData, SourceException>>> split =
        assetEntityDataStream.collect(
            Collectors.partitioningBy(
                dataTry ->
                    dataTry
                        .map(
                            data ->
                                data.containsKey(PARENT_EM) && !data.getField(PARENT_EM).isBlank())
                        .getOrElse(() -> true)));

    List<Try<AssetInputEntityData, SourceException>> rootEmsEntityData = split.get(false);
    List<Try<AssetInputEntityData, SourceException>> others = split.get(true);

    // at the start, this is only root ems
    Map<UUID, EmInput> allEms =
        unpackMap(
            rootEmsEntityData.stream()
                .parallel()
                .map(
                    entityDataTry ->
                        entityDataTry.map(
                            entityData -> new EmAssetInputEntityData(entityData, null)))
                .map(emInputFactory::get),
            EmInput.class);

    if (!others.isEmpty()) {
      // there's more EM levels beyond root level. Build them recursively
      Stream<AssetDataAndValidParentUuid> othersWithParentUuid =
          // We try to keep the Tries as long as possible so that as many failures as possible can
          // be reported. At this point however, we need to "unpack" (and throw, if applicable),
          // because without valid parent EM UUID, we cannot proceed.
          unpack(
              others.stream()
                  .map(
                      dataTry ->
                          dataTry.flatMap(
                              data -> {
                                // we already filtered out those entities that do not have a parent,
                                // so the field should exist
                                String uuidString = data.getField(PARENT_EM);
                                return Try.of(
                                        () -> UUID.fromString(uuidString),
                                        IllegalArgumentException.class)
                                    .transformF(
                                        iae ->
                                            new SourceException(
                                                String.format(
                                                    "Exception while trying to parse UUID of field \"%s\" with value \"%s\"",
                                                    PARENT_EM, uuidString),
                                                iae))
                                    // failed UUID parses are filtered out at this point. We save
                                    // the parsed UUID with the asset data
                                    .map(
                                        parentUuid ->
                                            new AssetDataAndValidParentUuid(data, parentUuid));
                              })),
              AssetDataAndValidParentUuid.class);

      allEms.putAll(createHierarchicalEmInputs(othersWithParentUuid, allEms));
    }

    return allEms;
  }

  private static Map<UUID, EmInput> createHierarchicalEmInputs(
      Stream<AssetDataAndValidParentUuid> assetEntityDataStream, Map<UUID, EmInput> lastLevelEms)
      throws SourceException {

    // Split stream by assets whose parent is already built (which can be built at this level), and
    // those whose parents are not built yet (which have to be built at some lower recursion level
    // or not at all)
    Map<Boolean, List<AssetDataAndValidParentUuid>> split =
        assetEntityDataStream.collect(
            Collectors.partitioningBy(data -> lastLevelEms.containsKey(data.parentEm)));

    List<AssetDataAndValidParentUuid> toBeBuiltAtThisLevel = split.get(true);
    List<AssetDataAndValidParentUuid> toBeBuiltAtNextLevel = split.get(false);

    if (toBeBuiltAtThisLevel.isEmpty()) {
      // Since we only start a new recursion step if the asset data stream is not empty, there have
      // to be EMs to be built at next level. This does not work if there's no EMs at the current
      // recursion level.
      throw new SourceException(
          "EMs " + toBeBuiltAtNextLevel + " were assigned a parent EM that does not exist.");
    } else {
      // New EMs can be built at this level
      Map<UUID, EmInput> newEms =
          unpackMap(
              toBeBuiltAtThisLevel.stream()
                  .map(
                      data -> {
                        // exists because we checked above
                        EmInput parentEm = lastLevelEms.get(data.parentEm);
                        return emInputFactory.get(
                            new EmAssetInputEntityData(data.entityData, parentEm));
                      }),
              EmInput.class);

      if (!toBeBuiltAtNextLevel.isEmpty()) {
        // If there's more EMs left to build, the new EMs have to function as parents there
        newEms.putAll(createHierarchicalEmInputs(toBeBuiltAtNextLevel.stream(), newEms));
      }
      return newEms;
    }
  }

  /**
   * Helper data record that holds an {@link AssetInputEntityData} and the UUID successfully parsed
   * from {@link EmInputFactory#PARENT_EM} field
   */
  private record AssetDataAndValidParentUuid(AssetInputEntityData entityData, UUID parentEm) {}
}
