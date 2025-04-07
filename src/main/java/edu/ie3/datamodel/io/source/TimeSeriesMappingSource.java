/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.*;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.timeseries.TimeSeriesMappingFactory;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.utils.Try;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This interface describes basic function to handle mapping between models and their respective
 * time series
 */
public abstract class TimeSeriesMappingSource extends EntitySource {

  protected final TimeSeriesMappingFactory mappingFactory;

  protected TimeSeriesMappingSource() {
    this.mappingFactory = new TimeSeriesMappingFactory();
  }

  @Override
  public void validate() throws ValidationException {
    validate(MappingEntry.class, this::getSourceFields, mappingFactory);
  }

  /**
   * Get a mapping from model {@link UUID} to the time series {@link UUID}
   *
   * @return That mapping
   */
  public Map<UUID, UUID> getMapping() throws SourceException {
    Collection<Try<MappingEntry, FactoryException>> tries =
        getMappingSourceData().map(this::createMappingEntry).toList();

    return Try.scanCollection(tries, MappingEntry.class)
        .transform(
            s ->
                s.stream()
                    .collect(Collectors.toMap(MappingEntry::getAsset, MappingEntry::getTimeSeries)),
            SourceException::new)
        .getOrThrow();
  }

  /**
   * Get a time series identifier to a given model identifier
   *
   * @param modelIdentifier Identifier of the model
   * @return An {@link Optional} to the time series identifier
   */
  public Optional<UUID> getTimeSeriesUuid(UUID modelIdentifier) throws SourceException {
    return Optional.ofNullable(getMapping().get(modelIdentifier));
  }

  /**
   * Extract a stream of maps from the database for the mapping
   *
   * @return Stream of maps
   */
  public abstract Stream<Map<String, String>> getMappingSourceData() throws SourceException;

  /** Returns the option for fields found in the source */
  public abstract Optional<Set<String>> getSourceFields() throws SourceException;

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  private Try<MappingEntry, FactoryException> createMappingEntry(
      Map<String, String> fieldToValues) {
    EntityData entityData = new EntityData(fieldToValues, MappingEntry.class);
    return mappingFactory.get(entityData);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /** Class to represent one entry within the asset to time series mapping */
  public static class MappingEntry implements InputEntity {
    private final UUID asset;
    private final UUID timeSeries;

    public MappingEntry(UUID asset, UUID timeSeries) {
      this.asset = asset;
      this.timeSeries = timeSeries;
    }

    /** Returns the {@link UUID} of the {@link edu.ie3.datamodel.models.input.AssetInput}. */
    public UUID getAsset() {
      return asset;
    }

    /** Returns the {@link UUID} of the {@link TimeSeries}. */
    public UUID getTimeSeries() {
      return timeSeries;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof MappingEntry that)) return false;
      return asset.equals(that.asset) && timeSeries.equals(that.timeSeries);
    }

    @Override
    public int hashCode() {
      return Objects.hash(asset, timeSeries);
    }

    @Override
    public String toString() {
      return "MappingEntry{" + "asset=" + asset + ", timeSeries=" + timeSeries + '}';
    }
  }
}
