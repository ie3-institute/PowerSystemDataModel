/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.io.factory.input.participant.EmInputFactory.PARENT_EM;

import edu.ie3.datamodel.exceptions.SourceException;
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

  private final EmInputFactory emInputFactory;

  public EnergyManagementSource(TypeSource typeSource, DataSource dataSource) {
    super(dataSource);
    this.typeSource = typeSource;

    this.emInputFactory = new EmInputFactory();
  }

  /**
   * Returns a unique set of {@link EmInput} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EmInput} which has to be checked manually, as
   * {@link EmInput#equals(Object)} is NOT restricted on the uuid of {@link EmInput}.
   *
   * @return a map of uuid to {@link EmInput} entities
   */
  public Map<UUID, EmInput> getEmUnits() throws SourceException {
    Map<UUID, OperatorInput> operators = typeSource.getOperators();
    return getEmUnits(operators);
  }

  /**
   * This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EmInput} which has to be checked manually, as
   * {@link EmInput#equals(Object)} is NOT restricted on the uuid of {@link EmInput}.
   *
   * <p>In contrast to {@link #getEmUnits()} this method provides the ability to pass in an already
   * existing set of {@link OperatorInput} entities, the {@link EmInput} instances depend on. Doing
   * so, already loaded nodes can be recycled to improve performance and prevent unnecessary loading
   * operations.
   *
   * <p>If something fails during the creation process a {@link SourceException} is thrown, else a
   * set with all entities that has been able to be build is returned.
   *
   * @param operators a set of object and uuid unique {@link OperatorInput} that should be used for
   *     the returning instances
   * @return a map of uuid to {@link EmInput} entities
   */
  public Map<UUID, EmInput> getEmUnits(Map<UUID, OperatorInput> operators) throws SourceException {
    return buildHierarchicalEmInputs(operators);
  }

  private Map<UUID, EmInput> buildHierarchicalEmInputs(Map<UUID, OperatorInput> operators)
      throws SourceException {
    Stream<Try<AssetInputEntityData, SourceException>> assetEntityDataStream =
        buildAssetInputEntityData(EmInput.class, operators);

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
      // there's more levels beyond EMs at root level. Build them recursively
      Stream<AssetDataAndValidParentUuid> othersWithParentUuid =
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

      allEms.putAll(buildHierarchicalEmInputs(othersWithParentUuid, allEms));
    }

    return allEms;
  }

  private Map<UUID, EmInput> buildHierarchicalEmInputs(
      Stream<AssetDataAndValidParentUuid> assetEntityDataStream, Map<UUID, EmInput> builtEms)
      throws SourceException {

    // Split stream by assets whose parent is already built (which can be built at this level), and
    // those whose parents are not built yet (which have to be built at some lower recursion level
    // or not at all)
    Map<Boolean, List<AssetDataAndValidParentUuid>> split =
        assetEntityDataStream.collect(
            Collectors.partitioningBy(data -> builtEms.containsKey(data.parentEm)));

    List<AssetDataAndValidParentUuid> toBeBuiltAtThisLevel = split.get(true);
    List<AssetDataAndValidParentUuid> others = split.get(false);

    if (toBeBuiltAtThisLevel.isEmpty()) {
      // Since we only start a new recursion step if the asset data stream is not empty,
      // we can conclude at this point that from all asset data at this recursion level,
      // no new EMs can be built - thus, parents must be missing
      throw new SourceException(
          "EMs " + others + " were assigned a parent EM that does not exist.");
    } else {
      // New EMs can be built at this level
      Map<UUID, EmInput> newEms =
          unpackMap(
              toBeBuiltAtThisLevel.stream()
                  .map(
                      data ->
                          emInputFactory.get(
                              new EmAssetInputEntityData(
                                  data.entityData, builtEms.get(data.parentEm)))),
              EmInput.class);

      // This also means that if there's more EMs left to build, the new EMs might function as
      // parents there
      if (!others.isEmpty()) {
        newEms.putAll(buildHierarchicalEmInputs(others.stream(), newEms));
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
