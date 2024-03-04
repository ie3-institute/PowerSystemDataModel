/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import edu.ie3.datamodel.exceptions.ConnectorException;
import edu.ie3.datamodel.exceptions.FileException;
import edu.ie3.datamodel.io.IoUtil;
import edu.ie3.datamodel.io.csv.BufferedCsvWriter;
import edu.ie3.datamodel.io.csv.CsvFileDefinition;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.utils.Try.TrySupplier;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;
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

  private final Map<Class<? extends Entity>, BufferedCsvWriter> entityWriters = new HashMap<>();
  private final Map<UUID, BufferedCsvWriter> timeSeriesWriters = new HashMap<>();
  private final Path baseDirectory;
  private static final String FILE_ENDING = ".csv";

  public CsvFileConnector(Path baseDirectory) {
    this.baseDirectory = baseDirectory;
  }

  /** Returns the base directory of this connector. */
  public Path getBaseDirectory() {
    return baseDirectory;
  }

  public synchronized BufferedCsvWriter getOrInitWriter(
      Class<? extends Entity> clz, TrySupplier<CsvFileDefinition, FileException> supplier)
      throws ConnectorException {
    /* Try to the right writer */
    BufferedCsvWriter predefinedWriter = entityWriters.get(clz);
    if (predefinedWriter != null) return predefinedWriter;

    /* If it is not available, build and register one */
    try {
      BufferedCsvWriter newWriter = initWriter(baseDirectory, supplier.get());

      entityWriters.put(clz, newWriter);
      return newWriter;
    } catch (ConnectorException | FileException | IOException e) {
      throw new ConnectorException(
          "Can neither find suitable writer nor build the correct one in CsvFileConnector.", e);
    }
  }

  public synchronized <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      BufferedCsvWriter getOrInitWriter(
          T timeSeries, TrySupplier<CsvFileDefinition, FileException> supplier)
          throws ConnectorException {
    /* Try to the right writer */
    BufferedCsvWriter predefinedWriter = timeSeriesWriters.get(timeSeries.getUuid());
    if (predefinedWriter != null) return predefinedWriter;

    /* If it is not available, build and register one */
    try {
      BufferedCsvWriter newWriter = initWriter(baseDirectory, supplier.get());

      timeSeriesWriters.put(timeSeries.getUuid(), newWriter);
      return newWriter;
    } catch (ConnectorException | FileException | IOException e) {
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
  public synchronized <C extends Entity> void closeEntityWriter(Class<C> clz) throws IOException {
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
   * Initializes a file reader for the given file name.
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
