/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import edu.ie3.datamodel.exceptions.ConnectorException;
import edu.ie3.datamodel.io.IoUtil;
import edu.ie3.datamodel.io.csv.*;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.TimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provides the connector (here: buffered writer) for specific files to be used by a {@link
 * edu.ie3.datamodel.io.sink.CsvFileSink}
 *
 * @version 0.1
 * @since 19.03.20
 */
public class CsvFileConnector implements DataConnector {
  private static final Logger log = LoggerFactory.getLogger(CsvFileConnector.class);

  private final Map<Class<? extends UniqueEntity>, BufferedCsvWriter> entityWriters =
      new HashMap<>();
  private final Map<UUID, BufferedCsvWriter> timeSeriesWriters = new HashMap<>();

  private final FileNamingStrategy fileNamingStrategy;
  private final Path baseDirectory;

  private static final String FILE_ENDING = ".csv";

  public CsvFileConnector(Path baseDirectory, FileNamingStrategy fileNamingStrategy) {
    this.baseDirectory = baseDirectory;
    this.fileNamingStrategy = fileNamingStrategy;
  }

  public synchronized BufferedCsvWriter getOrInitWriter(
      Class<? extends UniqueEntity> clz, String[] headerElements, String csvSep)
      throws ConnectorException {
    /* Try to the the right writer */
    BufferedCsvWriter predefinedWriter = entityWriters.get(clz);
    if (predefinedWriter != null) return predefinedWriter;

    /* If it is not available, build and register one */
    try {
      CsvFileDefinition fileDefinition = buildFileDefinition(clz, headerElements, csvSep);
      BufferedCsvWriter newWriter = initWriter(baseDirectory, fileDefinition);

      entityWriters.put(clz, newWriter);
      return newWriter;
    } catch (ConnectorException | IOException e) {
      throw new ConnectorException(
          "Can neither find suitable writer nor build the correct one in CsvFileConnector.", e);
    }
  }

  public synchronized <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      BufferedCsvWriter getOrInitWriter(T timeSeries, String[] headerElements, String csvSep)
          throws ConnectorException {
    /* Try to the the right writer */
    BufferedCsvWriter predefinedWriter = timeSeriesWriters.get(timeSeries.getUuid());
    if (predefinedWriter != null) return predefinedWriter;

    /* If it is not available, build and register one */
    try {
      CsvFileDefinition fileDefinition = buildFileDefinition(timeSeries, headerElements, csvSep);
      BufferedCsvWriter newWriter = initWriter(baseDirectory, fileDefinition);

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
   * @param baseDirectory Base directory, where the file hierarchy should start
   * @param fileDefinition Definition of the files shape
   * @return an initialized buffered writer
   * @throws ConnectorException If the base folder is a file
   * @throws IOException If the writer cannot be initialized correctly
   */
  private BufferedCsvWriter initWriter(Path baseDirectory, CsvFileDefinition fileDefinition)
      throws ConnectorException, IOException {
    /* Join the full DIRECTORY path (excluding file name) */
    Path baseDirectoryHarmonized = IoUtil.harmonizeFileSeparator(baseDirectory);
    Path fullDirectoryPath = baseDirectoryHarmonized.resolve(fileDefinition.getDirectoryPath());
    Path fullPath = baseDirectoryHarmonized.resolve(fileDefinition.getFilePath());

    /* Create missing directories */
    File directories = fullDirectoryPath.toFile();
    if (directories.isFile())
      throw new ConnectorException("Directory '" + directories + "' already exists and is a file!");
    if (!directories.exists() && !directories.mkdirs())
      throw new IOException("Unable to create directory tree '" + directories + "'");

    BufferedCsvWriter writer =
        new BufferedCsvWriter(
            fullPath, fileDefinition.headLineElements(), fileDefinition.csvSep(), false);
    writer.writeFileHeader();

    return writer;
  }

  /**
   * Closes a time series writer for the time series with given {@link UUID}
   *
   * @param uuid identifier of time series, whose writer is meant to be closed
   * @throws IOException If closing of writer fails.
   */
  public synchronized void closeTimeSeriesWriter(UUID uuid) throws IOException {
    Optional<BufferedCsvWriter> maybeWriter = Optional.ofNullable(timeSeriesWriters.get(uuid));
    if (maybeWriter.isPresent()) {
      log.debug("Remove reference to time series writer for UUID '{}'.", uuid);
      timeSeriesWriters.remove(uuid);
      maybeWriter.get().close();
    } else {
      log.warn("No writer found for time series '{}'.", uuid);
    }
  }

  /**
   * Close an entity writer for the given class
   *
   * @param clz Class, that the writer is able to persist
   * @param <C> Type of class
   * @throws IOException If closing of writer fails.
   */
  public synchronized <C extends UniqueEntity> void closeEntityWriter(Class<C> clz)
      throws IOException {
    Optional<BufferedCsvWriter> maybeWriter = Optional.ofNullable(entityWriters.get(clz));
    if (maybeWriter.isPresent()) {
      log.debug("Remove reference to entity writer for class '{}'.", clz);
      entityWriters.remove(clz);
      maybeWriter.get().close();
    } else {
      log.warn("No writer found for class '{}'.", clz);
    }
  }

  /**
   * Initializes a file reader for the given class that should be read in. The expected file name is
   * determined based on {@link FileNamingStrategy} of the this {@link CsvFileConnector} instance
   *
   * @param clz the class of the entity that should be read
   * @return the reader that contains information about the file to be read in
   * @throws FileNotFoundException If the matching file cannot be found
   */
  public BufferedReader initReader(Class<? extends UniqueEntity> clz)
      throws FileNotFoundException, ConnectorException {
    Path filePath =
        fileNamingStrategy
            .getFilePath(clz)
            .orElseThrow(
                () ->
                    new ConnectorException(
                        "Cannot find a naming strategy for class '" + clz.getSimpleName() + "'."));
    return initReader(filePath);
  }

  /**
   * Initializes a file reader for the given file name. Use {@link
   * CsvFileConnector#initReader(Class)} for files that actually correspond to concrete entities.
   *
   * @param filePath path of file starting from base folder, including file name but not file
   *     extension
   * @return the reader that contains information about the file to be read in
   * @throws FileNotFoundException if no file with the provided file name can be found
   */
  public BufferedReader initReader(Path filePath) throws FileNotFoundException {
    File fullPath = baseDirectory.resolve(filePath.toString() + FILE_ENDING).toFile();
    return new BufferedReader(
        new InputStreamReader(new FileInputStream(fullPath), StandardCharsets.UTF_8), 16384);
  }

  /**
   * Receive the information for specific time series. They are given back filtered by the column
   * scheme in order to allow for accounting the different content types.
   *
   * @param columnSchemes the column schemes to initialize readers for. If no scheme is given, all
   *     possible readers will be initialized.
   * @return A mapping from column scheme to the individual time series meta information
   */
  public Map<UUID, CsvIndividualTimeSeriesMetaInformation>
      getCsvIndividualTimeSeriesMetaInformation(final ColumnScheme... columnSchemes) {
    return getIndividualTimeSeriesFilePaths().parallelStream()
        .map(
            filePath -> {
              /* Extract meta information from file path and enhance it with the file path itself */
              IndividualTimeSeriesMetaInformation metaInformation =
                  fileNamingStrategy.individualTimeSeriesMetaInformation(filePath.toString());
              return new CsvIndividualTimeSeriesMetaInformation(
                  metaInformation, FileNamingStrategy.removeFileNameEnding(filePath.getFileName()));
            })
        .filter(
            metaInformation ->
                columnSchemes == null
                    || columnSchemes.length == 0
                    || Stream.of(columnSchemes)
                        .anyMatch(scheme -> scheme.equals(metaInformation.getColumnScheme())))
        .collect(Collectors.toMap(TimeSeriesMetaInformation::getUuid, Function.identity()));
  }

  /**
   * Returns a set of relative paths strings to time series files, with respect to the base folder
   * path
   *
   * @return A set of relative paths to time series files, with respect to the base folder path
   */
  private Set<Path> getIndividualTimeSeriesFilePaths() {
    try (Stream<Path> pathStream = Files.walk(baseDirectory)) {
      return pathStream
          .map(baseDirectory::relativize)
          .filter(
              path -> {
                Path withoutEnding =
                    Path.of(FileNamingStrategy.removeFileNameEnding(path.toString()));
                return fileNamingStrategy
                    .getIndividualTimeSeriesPattern()
                    .matcher(withoutEnding.toString())
                    .matches();
              })
          .collect(Collectors.toSet());
    } catch (IOException e) {
      log.error("Unable to determine time series files readers for time series.", e);
      return Collections.emptySet();
    }
  }

  /**
   * Initialises a reader to get grip on the file that contains mapping information between
   * coordinate id and actual coordinate
   *
   * @return A {@link BufferedReader}
   * @throws FileNotFoundException If the file is not present
   */
  public BufferedReader initIdCoordinateReader() throws FileNotFoundException {
    Path filePath = Path.of(fileNamingStrategy.getIdCoordinateEntityName());
    return initReader(filePath);
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
    Path directoryPath = fileNamingStrategy.getDirectoryPath(timeSeries).orElse(Path.of(""));
    String fileName =
        fileNamingStrategy
            .getEntityName(timeSeries)
            .orElseThrow(
                () ->
                    new ConnectorException(
                        "Cannot determine the file name for time series '" + timeSeries + "'."));
    return new CsvFileDefinition(fileName, directoryPath, headLineElements, csvSep);
  }

  /**
   * Builds a new file definition consisting of file name and head line elements
   *
   * @param clz Class that is meant to be serialized into this file
   * @param headLineElements Array of head line elements
   * @param csvSep Separator for csv columns
   * @return A suitable file definition
   * @throws ConnectorException If the definition cannot be determined
   */
  private CsvFileDefinition buildFileDefinition(
      Class<? extends UniqueEntity> clz, String[] headLineElements, String csvSep)
      throws ConnectorException {
    Path directoryPath = fileNamingStrategy.getDirectoryPath(clz).orElse(Path.of(""));
    String fileName =
        fileNamingStrategy
            .getEntityName(clz)
            .orElseThrow(
                () ->
                    new ConnectorException(
                        "Cannot determine the file name for class '" + clz.getSimpleName() + "'."));
    return new CsvFileDefinition(fileName, directoryPath, headLineElements, csvSep);
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
