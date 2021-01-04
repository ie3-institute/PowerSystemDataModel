/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.timeseries.mapping;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.InputEntity;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.value.Value;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TimeSeriesMapping {
  private final Map<UUID, IndividualTimeSeries<Value>> mapping;

  public TimeSeriesMapping(Map<UUID, IndividualTimeSeries<Value>> mapping) {
    this.mapping = mapping;
  }

  /**
   * Builds the mapping from given entries (e.g. from a file) and available time series. If a
   * referred time series is not available, an {@link IllegalArgumentException} is thrown.
   *
   * @param entries Collection of mapping entries
   * @param timeSeries Available time series
   */
  public TimeSeriesMapping(
      Collection<Entry> entries, Collection<IndividualTimeSeries<Value>> timeSeries) {
    /* Map time series from their uuid to themselves */
    Map<UUID, IndividualTimeSeries<Value>> timeSeriesMap =
        timeSeries.stream().collect(Collectors.toMap(UniqueEntity::getUuid, Function.identity()));

    /* Map from participant UUID to time series */
    mapping =
        entries.stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.participant,
                    entry -> {
                      UUID tsUuid = entry.timeSeries;
                      if (timeSeriesMap.containsKey(tsUuid)) return timeSeriesMap.get(tsUuid);
                      else
                        throw new IllegalArgumentException(
                            "Cannot find referenced time series with uuid '" + tsUuid + "'.");
                    }));
  }

  /**
   * Try to get a matching time series for the given participant uuid.
   *
   * @param participantUuid UUID of the questioned participant
   * @return Optional time series, if it is available, empty Optional otherwise
   */
  public Optional<IndividualTimeSeries<Value>> get(UUID participantUuid) {
    return Optional.ofNullable(mapping.get(participantUuid));
  }

  /**
   * Builds the mapping entries from the given mapping
   *
   * @return A List of {@link Entry}s
   */
  public List<Entry> buildEntries() {
    return mapping
        .entrySet()
        .parallelStream()
        .map(
            mapEntry ->
                new Entry(UUID.randomUUID(), mapEntry.getKey(), mapEntry.getValue().getUuid()))
        .collect(Collectors.toList());
  }

  /**
   * Model class to denote a single mapping between a participant (represented by it's UUID) and the
   * corresponding time series (represented with a UUID as well).
   */
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
          + "} ";
    }
  }
}
