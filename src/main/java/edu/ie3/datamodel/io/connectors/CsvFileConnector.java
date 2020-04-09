/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import edu.ie3.datamodel.exceptions.ConnectorException;
import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.io.csv.BufferedCsvWriter;
import edu.ie3.datamodel.io.csv.CsvFileDefinition;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides the connector (here: buffered writer) for specific files to be used by a {@link
 * edu.ie3.datamodel.io.sink.CsvFileSink}
 *
 * @version 0.1
 * @since 19.03.20
 */
public class CsvFileConnector implements DataConnector {
  private static final Logger log = LogManager.getLogger(CsvFileConnector.class);

  private final Map<Class<? extends UniqueEntity>, BufferedCsvWriter> entityWriters =
      new HashMap<>();
  private final Map<UUID, BufferedCsvWriter> timeSeriesWriters = new HashMap<>();
  private final FileNamingStrategy fileNamingStrategy;
  private final String baseFolderName;

  public CsvFileConnector(String baseFolderName, FileNamingStrategy fileNamingStrategy) {
    this.baseFolderName = baseFolderName;
    this.fileNamingStrategy = fileNamingStrategy;
  }

  public BufferedCsvWriter getOrInitWriter(
      Class<? extends UniqueEntity> clz, String[] headerElements, String csvSep)
      throws ConnectorException {
    /* Try to the the right writer */
    BufferedCsvWriter predefinedWriter = entityWriters.get(clz);
    if (predefinedWriter != null) return predefinedWriter;

    /* If it is not available, build and register one */
    try {
      CsvFileDefinition fileDefinition = buildFileDefinition(clz, headerElements, csvSep);
      BufferedCsvWriter newWriter = initWriter(baseFolderName, fileDefinition);

      entityWriters.put(clz, newWriter);
      return newWriter;
    } catch (ConnectorException | IOException e) {
      throw new ConnectorException(
          "Can neither find suitable writer nor build the correct one in CsvFileConnector.", e);
    }
  }

  public <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      BufferedCsvWriter getOrInitWriter(T timeSeries, String[] headerElements, String csvSep)
          throws ConnectorException {
    /* Try to the the right writer */
    BufferedCsvWriter predefinedWriter = timeSeriesWriters.get(timeSeries.getUuid());
    if (predefinedWriter != null) return predefinedWriter;

    /* If it is not available, build and register one */
    try {
      CsvFileDefinition fileDefinition = buildFileDefinition(timeSeries, headerElements, csvSep);
      BufferedCsvWriter newWriter = initWriter(baseFolderName, fileDefinition);

      timeSeriesWriters.put(timeSeries.getUuid(), newWriter);
      return newWriter;
    } catch (ConnectorException | IOException e) {
      throw new ConnectorException(
          "Can neither find suitable writer nor build the correct one in CsvFileConnector.", e);
    }
  }

  /**
   * Initializes a writer with the given base folder and file definition
   *
   * @param baseFolderName Base folder, where the file hierarchy should start
   * @param fileDefinition Definition of the files shape
   * @return an initialized buffered writer
   * @throws ConnectorException If the base folder is a file
   * @throws IOException If the writer cannot be initialized correctly
   */
  private BufferedCsvWriter initWriter(String baseFolderName, CsvFileDefinition fileDefinition)
      throws ConnectorException, IOException {
    File basePathDir = new File(baseFolderName);
    if (basePathDir.isFile())
      throw new ConnectorException(
          "Base path dir '" + baseFolderName + "' already exists and is a file!");
    if (!basePathDir.exists()) basePathDir.mkdirs();

    return new BufferedCsvWriter(baseFolderName, fileDefinition);
  }

  /**
   * Builds a new file definition consisting of file name and head line elements
   *
   * @param timeSeries Time series to derive naming information from
   * @param headLineElements Array of head line elements
   * @param csvSep Separator for csv columns
   * @return A suitable file definition
   * @throws ConnectorException If the definition cannot be determined
   */
  private <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      CsvFileDefinition buildFileDefinition(T timeSeries, String[] headLineElements, String csvSep)
          throws ConnectorException {
    String fileName =
        fileNamingStrategy
            .getFileName(timeSeries)
            .orElseThrow(
                () ->
                    new ConnectorException(
                        "Cannot determine the file name for time series '" + timeSeries + "'."));
    return new CsvFileDefinition(fileName, headLineElements, csvSep);
  }

  /**
   * Builds a new file definition consisting of file name and head line elements
   *
   * @param clz Class that is meant to be de-serialized into this file
   * @param headLineElements Array of head line elements
   * @param csvSep Separator for csv columns
   * @return A suitable file definition
   * @throws ConnectorException If the definition cannot be determined
   */
  private CsvFileDefinition buildFileDefinition(
      Class<? extends UniqueEntity> clz, String[] headLineElements, String csvSep)
      throws ConnectorException {
    String fileName =
        fileNamingStrategy
            .getFileName(clz)
            .orElseThrow(
                () ->
                    new ConnectorException(
                        "Cannot determine the file name for class '" + clz + "'."));
    return new CsvFileDefinition(fileName, headLineElements, csvSep);
  }

  @Override
  public void shutdown() {
    Stream.of(entityWriters.values(), timeSeriesWriters.values())
        .flatMap(Collection::stream)
        .forEach(
            bufferedWriter -> {
              try {
                bufferedWriter.close();
              } catch (IOException e) {
                log.error("Error during CsvFileConnector shutdown process.", e);
              }
            });
  }
}
