/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileTimeSeriesMetaInformation;
import edu.ie3.datamodel.models.profile.LoadProfile;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Source for all available time series with their {@link UUID} and {@link ColumnScheme} */
public interface TimeSeriesMetaInformationSource {

  /**
   * Get a mapping from time series {@link UUID} to its meta information {@link
   * IndividualTimeSeriesMetaInformation}
   *
   * @return that mapping
   */
  Map<UUID, IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation();

  /**
   * Get an option on the given time series meta information
   *
   * @param timeSeriesUuid Unique identifier of the time series in question
   * @return An Option on the meta information
   */
  Optional<IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation(UUID timeSeriesUuid);

  /**
   * Gat a mapping from load profile to {@link LoadProfileTimeSeriesMetaInformation}.
   *
   * @return that mapping
   */
  Map<String, LoadProfileTimeSeriesMetaInformation> getLoadProfileMetaInformation();

  /**
   * Get an option on the given time series meta information
   *
   * @param loadProfile load profile of the time series in question
   * @return An Option on the meta information
   */
  Optional<LoadProfileTimeSeriesMetaInformation> getLoadProfileMetaInformation(
      LoadProfile loadProfile);
}
