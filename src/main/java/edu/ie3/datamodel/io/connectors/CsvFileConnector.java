/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import edu.ie3.datamodel.exceptions.ConnectorException;
import edu.ie3.datamodel.io.csv.BufferedCsvWriter;
import edu.ie3.datamodel.io.csv.CsvFileDefinition;
import edu.ie3.datamodel.io.csv.FileNamingStrategy;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
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

  private static final String FILE_ENDING = ".csv";

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
   * @param baseFolder Base folder, where the file hierarchy should start
   * @param fileDefinition Definition of the files shape
   * @return an initialized buffered writer
   * @throws ConnectorException If the base folder is a file
   * @throws IOException If the writer cannot be initialized correctly
   */
  private BufferedCsvWriter initWriter(String baseFolder, CsvFileDefinition fileDefinition)
      throws ConnectorException, IOException {
    File basePathDir = new File(baseFolder);
    if (basePathDir.isFile())
      throw new ConnectorException(
          "Base path dir '" + baseFolder + "' already exists and is a file!");
    if (!basePathDir.exists()) basePathDir.mkdirs();

    String fullPathToFile = baseFolder + File.separator + fileDefinition.getFilePath();

    File pathFile = new File(fullPathToFile);
    if (!pathFile.exists()) {
      return new BufferedCsvWriter(baseFolder, fileDefinition, true, false);
    }
    log.warn(
        "File '{}.csv' already exist. Will append new content WITHOUT new header! Full path: {}",
        fileDefinition.getFileName(),
        pathFile.getAbsolutePath());
    return new BufferedCsvWriter(baseFolder, fileDefinition, false, true);
  }

  /**
   * Initializes a file reader for the given class that should be read in. The expected file name is
   * determined based on {@link FileNamingStrategy} of the this {@link CsvFileConnector} instance
   *
   * @param clz the class of the entity that should be read
   * @return the reader that contains information about the file to be read in
   * @throws FileNotFoundException If the matching file cannot be found
   */
  public BufferedReader initReader(Class<? extends UniqueEntity> clz) throws FileNotFoundException {

    BufferedReader newReader;

    String fileName = null;
    try {
      fileName =
          fileNamingStrategy
              .getFileName(clz)
              .orElseThrow(
                  () ->
                      new ConnectorException(
                          "Cannot find a naming strategy for class '"
                              + clz.getSimpleName()
                              + "'."));
    } catch (ConnectorException e) {
      log.error(
          "Cannot get reader for entity '{}' as no file naming strategy for this file exists. Exception:{}",
          clz::getSimpleName,
          () -> e);
    }
    newReader = initReader(fileName);

    return newReader;
  }

  /**
   * Initializes a file reader for the given file name. Use {@link
   * CsvFileConnector#initReader(Class)} for files that actually correspond to concrete entities.
   *
   * @param fileName the name of the file that should be read
   * @return the reader that contains information about the file to be read in
   * @throws FileNotFoundException if no file with the provided file name can be found
   */
  public BufferedReader initReader(String fileName) throws FileNotFoundException {
    BufferedReader newReader;
    File filePath = new File(baseFolderName + File.separator + fileName + FILE_ENDING);
    newReader =
        new BufferedReader(
            new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8), 16384);
    return newReader;
  }

  /*
   * TODO: ~~~ Treating time series ~~~
   */

  /**
   * Returns a set of relative paths strings to time series files, with respect to the base folder
   * path
   *
   * @return A set of relative paths to time series files, with respect to the base folder path
   * @throws IOException If the files cannot be walked
   */
  private Set<String> getIndividualTimeSeriesFilePaths() throws IOException {
    Path baseFolderPath = Paths.get(baseFolderName);
    try (Stream<Path> pathStream = Files.walk(baseFolderPath)) {
      return pathStream
          .map(baseFolderPath::relativize)
          .filter(
              path -> {
                String withoutEnding = path.toString().replaceAll("(.*)" + FILE_ENDING + "$", "$1");
                return fileNamingStrategy
                    .getIndividualTimeSeriesPattern()
                    .matcher(withoutEnding)
                    .matches();
              })
          .map(Path::toString)
          .collect(Collectors.toSet());
    }
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
