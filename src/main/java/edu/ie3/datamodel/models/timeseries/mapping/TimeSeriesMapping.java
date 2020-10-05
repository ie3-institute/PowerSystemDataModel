/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.mapping;

import edu.ie3.datamodel.models.input.InputEntity;
import java.util.Objects;
import java.util.UUID;

/**
 * Model class to denote a single mapping between a participant (represented by it's UUID) and the
 * corresponding time series (represented with a UUID as well).
 */
public class TimeSeriesMapping {
  public static class Entry extends InputEntity {

    private final UUID participant;

    private final UUID timeSeries;

    public Entry(UUID uuid, UUID participant, UUID timeSeries) {
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
      if (!(o instanceof TimeSeriesMapping.Entry)) return false;
      if (!super.equals(o)) return false;
      TimeSeriesMapping.Entry that = (TimeSeriesMapping.Entry) o;
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
          + "} "
          + super.toString();
    }
  }
}
