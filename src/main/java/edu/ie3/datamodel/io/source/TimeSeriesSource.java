/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.value.Value;
import java.util.Optional;
import java.util.UUID;

/**
 * The interface definition of a source, that is able to provide one specific time series for one
 * model
 */
public interface TimeSeriesSource extends DataSource {
  /**
   * Get the time series for the model denoted by its UUID
   *
   * @param modelUuid Unique identifier of the model in question
   * @return An option onto an {@link IndividualTimeSeries} for the questioned model
   */
  default Optional<IndividualTimeSeries<? extends Value>> getTimeSeriesForModel(UUID modelUuid) {
    return getTimeSeriesUuid(modelUuid)
        .flatMap(this::getTimeSeriesMetaInformation)
        .flatMap(this::getTimeSeries);
  }

  /**
   * Determine the unique identifier of the time series in question. Typically, a {@link
   * TimeSeriesMappingSource} should be useful.
   *
   * <p>Please note, that this method could be implemented here, but we cannot transfer this class
   * to an abstract class, as e.g. {@link edu.ie3.datamodel.io.source.csv.CsvTimeSeriesSource} has
   * to inherit from {@link edu.ie3.datamodel.io.source.csv.CsvDataSource} as well and in Java,
   * double inheritance is not permitted.
   *
   * @param modelUuid Unique identifier of the model
   * @return The unique identifier of the time series
   */
  Optional<UUID> getTimeSeriesUuid(UUID modelUuid);

  /**
   * Get an option on the given time series meta information
   *
   * @param timeSeriesUuid Unique identifier of the time series in question
   * @return An Option onto the meta information
   */
  Optional<IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation(UUID timeSeriesUuid);

  /**
   * Obtain time series by provided meta information
   *
   * @param metaInformation Meta information of the time series to obtain
   * @return the time series
   */
  Optional<IndividualTimeSeries<? extends Value>> getTimeSeries(
      IndividualTimeSeriesMetaInformation metaInformation);
}
