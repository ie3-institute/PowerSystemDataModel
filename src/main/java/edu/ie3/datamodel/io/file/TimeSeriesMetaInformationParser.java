/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.file;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.models.profile.LoadProfile;
import java.util.Map;
import java.util.UUID;

public interface TimeSeriesMetaInformationParser {
  Map<UUID, IndividualTimeSeriesMetaInformation> parseIndividualTimeSeriesMetaInformation(
      ColumnScheme... columnSchemes) throws SourceException;

  Map<String, FileLoadProfileMetaInformation> parseLoadProfileMetaInformation(
      LoadProfile... profiles) throws SourceException;
}
