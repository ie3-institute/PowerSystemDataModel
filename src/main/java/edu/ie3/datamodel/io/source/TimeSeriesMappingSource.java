/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.models.input.InputEntity;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * This interface describes basic function to handle mapping between models and their respective
 * time series
 */
public interface TimeSeriesMappingSource extends DataSource {
  /**
   * Get a mapping from model {@link UUID} to the time series {@link UUID}
   *
   * @return That mapping
   */
  Map<UUID, UUID> getMapping();

  /**
   * Get a time series identifier to a given model identifier
   *
   * @param modelIdentifier Identifier of the model
   * @return An {@link Optional} to the time series identifier
   */
  default Optional<UUID> getTimeSeriesUuid(UUID modelIdentifier) {
    return Optional.ofNullable(getMapping().get(modelIdentifier));
  }

  /**
   * Get an option on the given time series meta information
   *
   * @param timeSeriesUuid Unique identifier of the time series in question
   * @return An Option onto the meta information
   * @deprecated since 3.0. Use {@link #timeSeriesMetaInformation(java.util.UUID)} instead
   */
  @Deprecated
  Optional<edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation>
      getTimeSeriesMetaInformation(UUID timeSeriesUuid);

  /**
   * Get an option on the given time series meta information
   *
   * @param timeSeriesUuid Unique identifier of the time series in question
   * @return An Option onto the meta information
   */
  Optional<IndividualTimeSeriesMetaInformation> timeSeriesMetaInformation(UUID timeSeriesUuid);

  /** Class to represent one entry within the participant to time series mapping */
  class MappingEntry extends InputEntity {
    private final UUID participant;
    private final UUID timeSeries;

    public MappingEntry(UUID uuid, UUID participant, UUID timeSeries) {
      super(uuid);
      this.participant = participant;
      this.timeSeries = timeSeries;
    }

    public UUID getParticipant() {
      return participant;
    }

    public UUID getTimeSeries() {
      return timeSeries;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof MappingEntry)) return false;
      if (!super.equals(o)) return false;
      MappingEntry that = (MappingEntry) o;
      return participant.equals(that.participant) && timeSeries.equals(that.timeSeries);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), participant, timeSeries);
    }

    @Override
    public String toString() {
      return "MappingEntry{"
          + "uuid="
          + getUuid()
          + ", participant="
          + participant
          + ", timeSeries="
          + timeSeries
          + '}';
    }
  }
}
