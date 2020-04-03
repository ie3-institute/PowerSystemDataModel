/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import edu.ie3.datamodel.exceptions.ConnectorException;
import edu.ie3.datamodel.io.CsvFileDefinition;
import edu.ie3.datamodel.io.FileNamingStrategy;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.util.io.FileIOUtils;
import java.io.BufferedWriter;
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

  private final Map<Class<? extends UniqueEntity>, BufferedWriter> entityWriters = new HashMap<>();
  private final Map<CsvFileDefinition, BufferedWriter> timeSeriesWriters = new HashMap<>();
  private final FileNamingStrategy fileNamingStrategy;
  private final String baseFolderName;

  private static final String FILE_ENDING = ".csv";

  public CsvFileConnector(String baseFolderName, FileNamingStrategy fileNamingStrategy) {
    this.baseFolderName = baseFolderName;
    this.fileNamingStrategy = fileNamingStrategy;
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

  public BufferedWriter initWriter(
      Class<? extends UniqueEntity> clz, String[] headerElements, String csvSep)
      throws ConnectorException, IOException {
    return initWriter(baseFolderName, clz, fileNamingStrategy, headerElements, csvSep);
  }

  public Optional<BufferedWriter> getWriter(Class<? extends UniqueEntity> clz) {
    return Optional.ofNullable(entityWriters.get(clz));
  }

  public BufferedWriter getOrInitWriter(
      Class<? extends UniqueEntity> clz, String[] headerElements, String csvSep) {

    return getWriter(clz)
        .orElseGet(
            () -> {
              BufferedWriter newWriter = null;
              try {
                newWriter = initWriter(clz, headerElements, csvSep);
              } catch (ConnectorException | IOException e) {
                log.error("Error while initiating writer in CsvFileConnector.", e);
              }

              entityWriters.put(clz, newWriter);
              return newWriter;
            });
  }

  public <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      BufferedWriter getOrInitWriter(T timeSeries, String[] headerElements, String csvSep)
          throws ConnectorException {
    try {
      CsvFileDefinition fileDefinition = buildFileDefinition(timeSeries, headerElements, csvSep);

      return Optional.ofNullable(timeSeriesWriters.get(fileDefinition))
          .orElseGet(
              () -> {
                BufferedWriter newWriter = null;
                try {
                  newWriter = initWriter(fileDefinition);
                } catch (ConnectorException | IOException e) {
                  log.error("Error while initiating writer in CsvFileConnector.", e);
                }

                timeSeriesWriters.put(fileDefinition, newWriter);
                return newWriter;
              });
    } catch (ConnectorException e) {
      throw new ConnectorException(
          "Error during look up of writer, caused by wrong reference definition.", e);
    }
  }

  private BufferedWriter initWriter(
      String baseFolderName,
      Class<? extends UniqueEntity> clz,
      FileNamingStrategy fileNamingStrategy,
      String[] headerElements,
      String csvSep)
      throws ConnectorException, IOException {
    File basePathDir = new File(baseFolderName);
    if (basePathDir.isFile())
      throw new ConnectorException(
          "Base path dir '" + baseFolderName + "' already exists and is a file!");
    if (!basePathDir.exists()) basePathDir.mkdirs();

    String fileName =
        fileNamingStrategy
            .getFileName(clz)
            .orElseThrow(
                () ->
                    new ConnectorException(
                        "Cannot determine the file name for provided class '"
                            + clz.getSimpleName()
                            + "'."));
    String fullPath = baseFolderName + File.separator + fileName + FILE_ENDING;

    BufferedWriter writer = FileIOUtils.getBufferedWriterUTF8(fullPath);

    // write header
    writeFileHeader(writer, headerElements, csvSep);

    return writer;
  }

  private BufferedWriter initWriter(CsvFileDefinition fileDefinition)
      throws ConnectorException, IOException {
    File basePathDir = new File(baseFolderName);
    if (basePathDir.isFile())
      throw new ConnectorException(
          "Base path dir '" + baseFolderName + "' already exists and is a file!");
    if (!basePathDir.exists()) basePathDir.mkdirs();

    String fullPath = baseFolderName + File.separator + fileDefinition.getFilePath();

    BufferedWriter writer = FileIOUtils.getBufferedWriterUTF8(fullPath);

    // write header
    writeFileHeader(writer, fileDefinition.getHeadLineElements(), fileDefinition.getCsvSep());

    return writer;
  }

  private void writeFileHeader(BufferedWriter writer, final String[] headerElements, String csvSep)
      throws IOException {
    for (int i = 0; i < headerElements.length; i++) {
      String attribute = headerElements[i];
      writer.append("\"").append(attribute).append("\""); // adds " to headline
      if (i + 1 < headerElements.length) {
        writer.append(csvSep);
      } else {
        writer.append("\n");
      }
    }
    writer.flush();
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
}
