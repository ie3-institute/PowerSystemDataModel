/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.file.FileLoadProfileMetaInformation;
import edu.ie3.datamodel.io.file.FileTimeSeriesParserFactory;
import edu.ie3.datamodel.io.file.FileType;
import edu.ie3.datamodel.io.file.TimeSeriesMetaInformationParser;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import edu.ie3.datamodel.io.source.TimeSeriesMetaInformationSource;
import edu.ie3.datamodel.utils.TimeSeriesUtils;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class CsvTimeSeriesMetaInformationSource extends TimeSeriesMetaInformationSource {

  private final Map<UUID, IndividualTimeSeriesMetaInformation> timeSeriesMetaInformation;

  public CsvTimeSeriesMetaInformationSource(
      Path path, FileType fileType, FileNamingStrategy fileNamingStrategy, String csvSeparator) {
    this(path, fileType, fileNamingStrategy, csvSeparator, new FileTimeSeriesParserFactory());
  }

  CsvTimeSeriesMetaInformationSource(
      Path path,
      FileType fileType,
      FileNamingStrategy fileNamingStrategy,
      String csvSeparator,
      FileTimeSeriesParserFactory parserFactory) {
    String effectiveSeparator = csvSeparator == null ? "," : csvSeparator;
    TimeSeriesMetaInformationParser parser =
        parserFactory.metaInformationParser(path, fileType, fileNamingStrategy, effectiveSeparator);

    try {
      ColumnScheme[] acceptedSchemes =
          TimeSeriesUtils.getAcceptedColumnSchemes().toArray(new ColumnScheme[0]);
      this.timeSeriesMetaInformation =
          Collections.unmodifiableMap(
              parser.parseIndividualTimeSeriesMetaInformation(acceptedSchemes));

      Map<String, FileLoadProfileMetaInformation> parsedLoadProfiles =
          parser.parseLoadProfileMetaInformation();
      this.loadProfileMetaInformation =
          Collections.unmodifiableMap(
              parsedLoadProfiles.entrySet().stream()
                  .collect(
                      Collectors.toMap(
                          Map.Entry::getKey,
                          entry -> (LoadProfileMetaInformation) entry.getValue())));
    } catch (SourceException e) {
      throw new IllegalStateException(
          "Unable to initialize time series meta information source for " + fileType + '.', e);
    }
  }

  @Override
  public Map<UUID, IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation() {
    return timeSeriesMetaInformation;
  }

  @Override
  public Optional<IndividualTimeSeriesMetaInformation> getTimeSeriesMetaInformation(
      UUID timeSeriesUuid) {
    return Optional.ofNullable(timeSeriesMetaInformation.get(timeSeriesUuid));
  }
}
