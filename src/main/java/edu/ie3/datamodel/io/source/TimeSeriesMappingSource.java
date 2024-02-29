/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.io.factory.timeseries.TimeSeriesMappingFactory;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.datamodel.utils.Try.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This interface describes basic function to handle mapping between models and their respective
 * time series
 */
public abstract class TimeSeriesMappingSource {

  protected final TimeSeriesMappingFactory mappingFactory;

  protected TimeSeriesMappingSource() {
    this.mappingFactory = new TimeSeriesMappingFactory();
  }

  /**
   * Get a mapping from model {@link UUID} to the time series {@link UUID}
   *
   * @return That mapping
   */
  public Map<UUID, UUID> getMapping() throws SourceException {
    return getMappingSourceData()
        .map(this::createMappingEntry)
        .filter(Try::isSuccess)
        .map(t -> (Success<MappingEntry, FactoryException>) t)
        .map(Success::get)
        .collect(Collectors.toMap(MappingEntry::participant, MappingEntry::timeSeries));
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

  /** Class to represent one entry within the participant to time series mapping */
  public record MappingEntry(UUID participant, UUID timeSeries) implements InputEntity {

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof MappingEntry that)) return false;
      return participant.equals(that.participant) && timeSeries.equals(that.timeSeries);
    }

    @Override
    public int hashCode() {
      return Objects.hash(participant, timeSeries);
    }

    @Override
    public String toString() {
      return "MappingEntry{" + "participant=" + participant + ", timeSeries=" + timeSeries + '}';
    }
  }
}
