/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.factory.SimpleEntityData;
import edu.ie3.datamodel.io.factory.timeseries.TimeSeriesMappingFactory;
import edu.ie3.datamodel.models.input.InputEntity;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class TimeSeriesMappingSource implements DataSource {
  private final TimeSeriesMappingFactory mappingFactory;

  private final FunctionalDataSource dataSource;

  private final Map<UUID, UUID> mapping;

  public TimeSeriesMappingSource(FunctionalDataSource _dataSource) {
    this.dataSource = _dataSource;
    this.mappingFactory = new TimeSeriesMappingFactory();

    mapping = dataSource.getSourceData(MappingEntry.class)
            .map(
                    fieldToValues -> {
                      SimpleEntityData entityData =
                              new SimpleEntityData(fieldToValues, MappingEntry.class);
                      return mappingFactory.get(entityData);
                    })
            .flatMap(Optional::stream)
            .collect(Collectors.toMap(MappingEntry::getParticipant, MappingEntry::getTimeSeries));
  }

  /**
   * Get a mapping from model {@link UUID} to the time series {@link UUID}
   *
   * @return That mapping
   */
  Map<UUID, UUID> getMapping() { return mapping; }

  /**
   * Get a time series identifier to a given model identifier
   *
   * @param modelIdentifier Identifier of the model
   * @return An {@link Optional} to the time series identifier
   */
  public Optional<UUID> getTimeSeriesUuid(UUID modelIdentifier) {
    return Optional.ofNullable(getMapping().get(modelIdentifier));
  }


  /**
   * Get an option on the given time series meta information
   *
   * @param timeSeriesUuid Unique identifier of the time series in question
   * @return An Option onto the meta information
   * @deprecated since 3.0. Use {@link
   *     TimeSeriesMetaInformationSource#getTimeSeriesMetaInformation()} instead
   */
  @Deprecated(since = "3.0", forRemoval = true)
  Optional<edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation(UUID timeSeriesUuid) {
    return null;
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
