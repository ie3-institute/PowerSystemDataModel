/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.timeseries.TimeSeriesMappingFactory;
import edu.ie3.datamodel.models.input.InputEntity;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * This interface describes basic function to handle mapping between models and their respective
 * time series
 */
public abstract class TimeSeriesMappingSource implements DataSource {

  protected final TimeSeriesMappingFactory mappingFactory;

  public TimeSeriesMappingSource() {
    this.mappingFactory = new TimeSeriesMappingFactory();
  }

  /**
   * Get a mapping from model {@link UUID} to the time series {@link UUID}
   *
   * @return That mapping
   */
  public abstract Map<UUID, UUID> getMapping();

  /**
   * Get a time series identifier to a given model identifier
   *
   * @param modelIdentifier Identifier of the model
   * @return An {@link Optional} to the time series identifier
   */
  public Optional<UUID> getTimeSeriesUuid(UUID modelIdentifier) {
    return Optional.ofNullable(getMapping().get(modelIdentifier));
  }

  /** Class to represent one entry within the participant to time series mapping */
  public static class MappingEntry extends InputEntity {
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
      if (!(o instanceof MappingEntry that)) return false;
      if (!super.equals(o)) return false;
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
