/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import edu.ie3.datamodel.exceptions.ConnectorException;
import edu.ie3.datamodel.io.csv.*;
import edu.ie3.datamodel.io.csv.timeseries.ColumnScheme;
import edu.ie3.datamodel.io.csv.timeseries.IndividualTimeSeriesMetaInformation;
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
import org.apache.commons.io.FilenameUtils;
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
  // ATTENTION: Do not finalize. It's meant for lazy evaluation.
  private Map<UUID, CsvIndividualTimeSeriesMetaInformation> individualTimeSeriesMetaInformation;
  private final FileNamingStrategy fileNamingStrategy;
  private final String baseDirectoryName;

  private static final String FILE_ENDING = ".csv";
  private static final String FILE_SEPARATOR_REGEX = "[\\\\/]";
  private static final String FILE_SEPARATOR_REPLACEMENT =
      File.separator.equals("\\") ? "\\\\" : "/";

  public CsvFileConnector(String baseDirectoryName, FileNamingStrategy fileNamingStrategy) {
    this.baseDirectoryName = baseDirectoryName;
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
      BufferedCsvWriter newWriter = initWriter(baseDirectoryName, fileDefinition);

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
      BufferedCsvWriter newWriter = initWriter(baseDirectoryName, fileDefinition);

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
  private BufferedCsvWriter initWriter(String baseDirectory, CsvFileDefinition fileDefinition)
      throws ConnectorException, IOException {
    /* Join the full DIRECTORY path (excluding file name) */
    String baseDirectoryHarmonized =
        baseDirectory.replaceAll(FILE_SEPARATOR_REGEX, FILE_SEPARATOR_REPLACEMENT);
    String fullDirectoryPath =
        FilenameUtils.concat(baseDirectoryHarmonized, fileDefinition.getDirectoryPath());
    String fullPath = FilenameUtils.concat(baseDirectoryHarmonized, fileDefinition.getFilePath());

    /* Create missing directories */
    File directories = new File(fullDirectoryPath);
    if (directories.isFile())
      throw new ConnectorException("Directory '" + directories + "' already exists and is a file!");
    if (!directories.exists() && !directories.mkdirs())
      throw new IOException("Unable to create directory tree '" + directories.toString() + "'");

    File pathFile = new File(fullPath);
    boolean append = pathFile.exists();
    BufferedCsvWriter writer =
        new BufferedCsvWriter(
            fullPath, fileDefinition.getHeadLineElements(), fileDefinition.getCsvSep(), append);
    if (!append) {
      writer.writeFileHeader();
    } else {
      log.warn(
          "File '{}' already exist. Will append new content WITHOUT new header! Full path: {}",
          fileDefinition.getFileName(),
          pathFile.getAbsolutePath());
    }
    return writer;
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
    String filePath = null;
    try {
      filePath =
          fileNamingStrategy
              .getFilePath(clz)
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
    return initReader(filePath);
  }

  /**
   * Initializes a file reader for the given file name. Use {@link
   * CsvFileConnector#initReader(Class)} for files that actually correspond to concrete entities.
   *
   * @param filePath sub directory tree starting from base folder, including file name
   * @return the reader that contains information about the file to be read in
   * @throws FileNotFoundException if no file with the provided file name can be found
   */
  public BufferedReader initReader(String filePath) throws FileNotFoundException {
    File fullPath = new File(baseDirectoryName + File.separator + filePath + FILE_ENDING);
    return new BufferedReader(
        new InputStreamReader(new FileInputStream(fullPath), StandardCharsets.UTF_8), 16384);
  }

  /**
   * Get time series meta information for a given uuid.
   *
   * <p>This method lazily evaluates the mapping from <i>all</i> time series files to their meta
   * information.
   *
   * @param timeSeriesUuid The time series in question
   * @return An option on the queried information
   */
  public Optional<IndividualTimeSeriesMetaInformation> getIndividualTimeSeriesMetaInformation(
      UUID timeSeriesUuid) {
    if (Objects.isNull(individualTimeSeriesMetaInformation))
      individualTimeSeriesMetaInformation = buildIndividualTimeSeriesMetaInformation();

    return Optional.ofNullable(individualTimeSeriesMetaInformation.get(timeSeriesUuid));
  }

  /**
   * This method creates a map from time series uuid to it's meta information.
   *
   * @return Mapping from time series uuid to it's meta information.
   */
  private Map<UUID, CsvIndividualTimeSeriesMetaInformation>
      buildIndividualTimeSeriesMetaInformation() {
    return getIndividualTimeSeriesFilePaths().parallelStream()
        .map(
            filePath -> {
              /* Extract meta information from file path and enhance it with the file path itself */
              String filePathWithoutEnding = removeFileEnding(filePath);
              IndividualTimeSeriesMetaInformation metaInformation =
                  (IndividualTimeSeriesMetaInformation)
                      fileNamingStrategy.extractTimeSeriesMetaInformation(filePathWithoutEnding);
              return new CsvIndividualTimeSeriesMetaInformation(
                  metaInformation, filePathWithoutEnding);
            })
        .collect(Collectors.toMap(FileNameMetaInformation::getUuid, v -> v));
  }

  /**
   * Initialises the readers for time series with the specified column schemes. They are given back
   * grouped by the column scheme in order to allow for accounting the different content types.
   *
   * @param columnSchemes the column schemes to initialize readers for. If no scheme is given, all
   *     possible readers will be initialized.
   * @return A mapping from column type to respective readers
   * @deprecated Don't use {@link TimeSeriesReadingData}, as it contains a reader, that might not be
   *     closed
   */
  @Deprecated
  public Map<ColumnScheme, Set<TimeSeriesReadingData>> initTimeSeriesReader(
      ColumnScheme... columnSchemes) {
    return getIndividualTimeSeriesFilePaths().parallelStream()
        .map(
            pathString -> {
              String filePathWithoutEnding = removeFileEnding(pathString);
              return buildReadingData(filePathWithoutEnding, columnSchemes);
            })
        .filter(Optional::isPresent)
        .map(Optional::get)
        .collect(Collectors.groupingBy(TimeSeriesReadingData::getColumnScheme, Collectors.toSet()));
  }

  /**
   * Initialises a reader to get grip on the file that contains mapping information between
   * coordinate id and actual coordinate
   *
   * @return A {@link BufferedReader}
   * @throws FileNotFoundException If the file is not present
   */
  public BufferedReader initIdCoordinateReader() throws FileNotFoundException {
    String filePath = fileNamingStrategy.getIdCoordinateFileName();
    return initReader(filePath);
  }

  /**
   * Returns a set of relative paths strings to time series files, with respect to the base folder
   * path
   *
   * @return A set of relative paths to time series files, with respect to the base folder path
   */
  private Set<String> getIndividualTimeSeriesFilePaths() {
    Path baseDirectoryPath = Paths.get(baseDirectoryName);
    try (Stream<Path> pathStream = Files.walk(baseDirectoryPath)) {
      return pathStream
          .map(baseDirectoryPath::relativize)
          .filter(
              path -> {
                String withoutEnding = removeFileEnding(path.toString());
                return fileNamingStrategy
                    .getIndividualTimeSeriesPattern()
                    .matcher(withoutEnding)
                    .matches();
              })
          .map(Path::toString)
          .collect(Collectors.toSet());
    } catch (IOException e) {
      log.error("Unable to determine time series files readers for time series.", e);
      return Collections.emptySet();
    }
  }

  /**
   * Compose the needed information for reading in a single time series. If the file points to a
   * non-individual time series or a time series of a column scheme other than the specified ones,
   * or the initialisation of the reader does not work, an empty {@link Optional} is given back
   *
   * @param filePathString String describing the path to the time series file
   * @param columnSchemes the allowed column schemes. If no scheme is specified, all schemes are
   *     allowed.
   * @return An {@link Optional} to {@link TimeSeriesReadingData}
   * @deprecated Don't use {@link TimeSeriesReadingData}, as it contains a reader, that might not be
   *     closed
   */
  @Deprecated
  private Optional<TimeSeriesReadingData> buildReadingData(
      String filePathString, ColumnScheme... columnSchemes) {
    try {
      FileNameMetaInformation metaInformation =
          fileNamingStrategy.extractTimeSeriesMetaInformation(filePathString);
      if (!IndividualTimeSeriesMetaInformation.class.isAssignableFrom(metaInformation.getClass())) {
        log.error(
            "The time series file '{}' does not represent an individual time series.",
            filePathString);
        return Optional.empty();
      }

      IndividualTimeSeriesMetaInformation individualMetaInformation =
          (IndividualTimeSeriesMetaInformation) metaInformation;

      // If no column schemes are specified, we will include all. If there a specified schemes, we
      // check if the file's column scheme matches any of them
      if (columnSchemes != null
          && columnSchemes.length > 0
          && Stream.of(columnSchemes)
              .noneMatch(scheme -> scheme.equals(individualMetaInformation.getColumnScheme()))) {
        log.warn(
            "The column scheme of the time series file {} does not match any of the specified column schemes ({}), so it will not be processed.",
            filePathString,
            columnSchemes);
        return Optional.empty();
      }

      BufferedReader reader = initReader(filePathString);
      return Optional.of(
          new TimeSeriesReadingData(
              individualMetaInformation.getUuid(),
              individualMetaInformation.getColumnScheme(),
              reader));
    } catch (FileNotFoundException e) {
      log.error("Cannot init the writer for time series file path '{}'.", filePathString, e);
      return Optional.empty();
    } catch (IllegalArgumentException e) {
      log.error(
          "Error during extraction of meta information from file name '{}'.", filePathString, e);
      return Optional.empty();
    }
  }

  /**
   * Removes the file ending from input string
   *
   * @param input String to manipulate
   * @return input without possible ending
   */
  private String removeFileEnding(String input) {
    return input.replaceAll(FILE_ENDING + "$", "");
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
    String directoryPath = fileNamingStrategy.getDirectoryPath(timeSeries).orElse("");
    String fileName =
        fileNamingStrategy
            .getFileName(timeSeries)
            .orElseThrow(
                () ->
                    new ConnectorException(
                        "Cannot determine the file name for time series '" + timeSeries + "'."));
    return new CsvFileDefinition(fileName, directoryPath, headLineElements, csvSep);
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
    String directoryPath = fileNamingStrategy.getDirectoryPath(clz).orElse("");
    String fileName =
        fileNamingStrategy
            .getFileName(clz)
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

  /**
   * Class to bundle all information, that are necessary to read a single time series
   *
   * @deprecated Use the {@link CsvIndividualTimeSeriesMetaInformation} and build reader on demand
   */
  @Deprecated
  public static class TimeSeriesReadingData {
    private final UUID uuid;
    private final ColumnScheme columnScheme;
    private final BufferedReader reader;

    public TimeSeriesReadingData(UUID uuid, ColumnScheme columnScheme, BufferedReader reader) {
      this.uuid = uuid;
      this.columnScheme = columnScheme;
      this.reader = reader;
    }

    public UUID getUuid() {
      return uuid;
    }

    public ColumnScheme getColumnScheme() {
      return columnScheme;
    }

    public BufferedReader getReader() {
      return reader;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof TimeSeriesReadingData)) return false;
      TimeSeriesReadingData that = (TimeSeriesReadingData) o;
      return uuid.equals(that.uuid)
          && columnScheme == that.columnScheme
          && reader.equals(that.reader);
    }

    @Override
    public int hashCode() {
      return Objects.hash(uuid, columnScheme, reader);
    }

    @Override
    public String toString() {
      return "TimeSeriesReadingData{"
          + "uuid="
          + uuid
          + ", columnScheme="
          + columnScheme
          + ", reader="
          + reader
          + '}';
    }
  }

  /** Enhancing the {@link IndividualTimeSeriesMetaInformation} with the full path to csv file */
  public static class CsvIndividualTimeSeriesMetaInformation
      extends IndividualTimeSeriesMetaInformation {
    private final String fullFilePath;

    public CsvIndividualTimeSeriesMetaInformation(
        UUID uuid, ColumnScheme columnScheme, String fullFilePath) {
      super(uuid, columnScheme);
      this.fullFilePath = fullFilePath;
    }

    public CsvIndividualTimeSeriesMetaInformation(
        IndividualTimeSeriesMetaInformation metaInformation, String fullFilePath) {
      this(metaInformation.getUuid(), metaInformation.getColumnScheme(), fullFilePath);
    }

    public String getFullFilePath() {
      return fullFilePath;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof CsvIndividualTimeSeriesMetaInformation)) return false;
      if (!super.equals(o)) return false;
      CsvIndividualTimeSeriesMetaInformation that = (CsvIndividualTimeSeriesMetaInformation) o;
      return fullFilePath.equals(that.fullFilePath);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), fullFilePath);
    }

    @Override
    public String toString() {
      return "CsvIndividualTimeSeriesMetaInformation{"
          + "uuid="
          + getUuid()
          + ", columnScheme="
          + getColumnScheme()
          + ", fullFilePath='"
          + fullFilePath
          + '\''
          + '}';
    }
  }
}
