/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.io.IoUtil;
import edu.ie3.datamodel.io.naming.timeseries.IndividualTimeSeriesMetaInformation;
import edu.ie3.datamodel.io.naming.timeseries.LoadProfileMetaInformation;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileTimeSeries;
import edu.ie3.datamodel.models.value.Value;
import edu.ie3.datamodel.utils.FileUtils;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A naming strategy, that combines an {@link EntityPersistenceNamingStrategy} for naming entities
 * and a {@link FileHierarchy} for a folder structure.
 */
public class FileNamingStrategy {

  private static final Logger logger = LoggerFactory.getLogger(FileNamingStrategy.class);

  private final EntityPersistenceNamingStrategy entityPersistenceNamingStrategy;
  private final FileHierarchy fileHierarchy;

  /**
   * Constructor for building the file naming strategy.
   *
   * @param entityPersistenceNamingStrategy entity naming strategy
   * @param fileHierarchy directory hierarchy
   */
  public FileNamingStrategy(
      EntityPersistenceNamingStrategy entityPersistenceNamingStrategy,
      FileHierarchy fileHierarchy) {
    this.entityPersistenceNamingStrategy = entityPersistenceNamingStrategy;
    this.fileHierarchy = fileHierarchy;
  }

  /**
   * Constructor for building the file naming strategy. Since no directory hierarchy is provided, a
   * flat directory hierarchy is used.
   *
   * @param entityPersistenceNamingStrategy entity naming strategy
   */
  public FileNamingStrategy(EntityPersistenceNamingStrategy entityPersistenceNamingStrategy) {
    this(entityPersistenceNamingStrategy, new FlatDirectoryHierarchy());
  }

  /**
   * Constructor for building the file naming strategy. Since no entity naming strategy is provided,
   * the entity naming strategy is used. Since no directory hierarchy is provided, a flat directory
   * hierarchy is used.
   */
  public FileNamingStrategy() {
    this(new EntityPersistenceNamingStrategy(), new FlatDirectoryHierarchy());
  }

  /**
   * Get the full path to the file with regard to some (not explicitly specified) base directory.
   * The path does NOT start or end with any of the known file separators or file extension.
   *
   * @param cls Targeted class of the given file
   * @return An optional sub path to the actual file
   */
  public Optional<Path> getFilePath(Class<? extends Entity> cls) {
    return FileUtils.of(getEntityName(cls), getDirectoryPath(cls));
  }

  /**
   * Get the full path to the file with regard to some (not explicitly specified) base directory.
   * The path does NOT start or end with any of the known file separators or file extension.
   *
   * @param <T> Type of the time series
   * @param <E> Type of the entry in the time series
   * @param <V> Type of the value, that is carried by the time series entry
   * @param timeSeries Time series to derive naming information from
   * @return An optional sub path to the actual file
   */
  public <
          T extends TimeSeries<E, V, R>,
          E extends TimeSeriesEntry<V>,
          V extends Value,
          R extends Value>
      Optional<Path> getFilePath(T timeSeries) {
    return FileUtils.of(
        entityPersistenceNamingStrategy.getEntityName(timeSeries), getDirectoryPath(timeSeries));
  }

  /**
   * Compose a full file path from directory name and file name. Additionally perform some checks,
   * like if the file name itself actually is available
   *
   * @param fileName File name
   * @param subDirectories Sub directory path
   * @return Concatenation of sub directory structure and file name
   * @deprecated replaced with {@link FileUtils#of(String, Optional)}
   */
  @Deprecated(since = "3.0", forRemoval = true)
  private Optional<Path> getFilePath(String fileName, Optional<Path> subDirectories) {
    if (fileName.isEmpty()) return Optional.empty();
    return subDirectories
        .map(path -> path.resolve(fileName))
        .or(() -> Optional.of(Path.of(fileName)));
  }

  /**
   * Returns the sub directory structure with regard to some (not explicitly specified) base
   * directory. The path does NOT start or end with any of the known file separators.
   *
   * @param cls Targeted class of the given file
   * @return An optional sub directory path
   */
  public Optional<Path> getDirectoryPath(Class<? extends Entity> cls) {
    Optional<Path> maybeDirectoryName = fileHierarchy.getSubDirectory(cls);
    if (maybeDirectoryName.isEmpty()) {
      logger.debug("Cannot determine directory name for class '{}'.", cls);
      return Optional.empty();
    } else {
      /* Make sure, the directory path does not start or end with file separator and in between the separator is harmonized */
      return maybeDirectoryName.map(IoUtil::harmonizeFileSeparator);
    }
  }

  /**
   * Returns the sub directory structure with regard to some (not explicitly specified) base
   * directory. The path does NOT start or end with any of the known file separators.
   *
   * @param <T> Type of the time series
   * @param <E> Type of the entry in the time series
   * @param <V> Type of the value, that is carried by the time series entry
   * @param timeSeries Time series to derive naming information from
   * @return An optional sub directory path
   */
  public <
          T extends TimeSeries<E, V, R>,
          E extends TimeSeriesEntry<V>,
          V extends Value,
          R extends Value>
      Optional<Path> getDirectoryPath(T timeSeries) {
    Optional<Path> maybeDirectoryName = fileHierarchy.getSubDirectory(timeSeries.getClass());
    if (maybeDirectoryName.isEmpty()) {
      logger.debug("Cannot determine directory name for time series '{}'.", timeSeries);
      return Optional.empty();
    } else {
      /* Make sure, the directory path does not start or end with file separator and in between the separator is harmonized */
      return maybeDirectoryName.map(IoUtil::harmonizeFileSeparator);
    }
  }

  /**
   * Returns the pattern to identify individual time series in this instance of the file naming
   * strategy considering the {@link EntityPersistenceNamingStrategy} and {@link FileHierarchy}.
   *
   * @return An individual time series pattern
   */
  public Pattern getIndividualTimeSeriesPattern() {
    Optional<Path> subDirectory = fileHierarchy.getSubDirectory(IndividualTimeSeries.class);

    if (subDirectory.isEmpty()) {
      return entityPersistenceNamingStrategy.getIndividualTimeSeriesPattern();
    } else {
      /* Build the pattern by joining the subdirectory with the file name pattern, harmonizing file separators and
       * finally escaping them */
      String joined =
          FilenameUtils.concat(
              subDirectory.get().toString(),
              entityPersistenceNamingStrategy.getIndividualTimeSeriesPattern().pattern());
      String harmonized = IoUtil.harmonizeFileSeparator(joined);
      String escaped = harmonized.replace("\\", "\\\\");

      return Pattern.compile(escaped);
    }
  }

  /**
   * Returns the pattern to identify load profile time series in this instance of the file naming
   * strategy considering the {@link EntityPersistenceNamingStrategy} and {@link FileHierarchy}.
   *
   * @return A load profile time series pattern
   */
  public Pattern getLoadProfileTimeSeriesPattern() {
    Optional<Path> subDirectory = fileHierarchy.getSubDirectory(LoadProfileTimeSeries.class);

    if (subDirectory.isEmpty()) {
      return entityPersistenceNamingStrategy.getLoadProfileTimeSeriesPattern();
    } else {
      /* Build the pattern by joining the sub directory with the file name pattern, harmonizing file separators and
       * finally escaping them */
      String joined =
          FilenameUtils.concat(
              subDirectory.get().toString(),
              entityPersistenceNamingStrategy.getLoadProfileTimeSeriesPattern().pattern());
      String harmonized = IoUtil.harmonizeFileSeparator(joined);
      String escaped = harmonized.replace("\\", "\\\\");

      return Pattern.compile(escaped);
    }
  }

  /**
   * Extracts meta information from a file name, of a time series.
   *
   * @param path Path to the file
   * @return The meeting meta information
   */
  public TimeSeriesMetaInformation timeSeriesMetaInformation(Path path) {
    /* Extract file name from possibly fully qualified path */
    Path fileName = path.getFileName();
    if (fileName == null)
      throw new IllegalArgumentException("Unable to extract file name from path '" + path + "'.");
    return timeSeriesMetaInformation(fileName.toString());
  }

  /**
   * Extracts meta information from a file name, of a time series. Here, a file name <u>without</u>
   * leading path has to be provided
   *
   * @param fileName File name
   * @return The meeting meta information
   */
  public TimeSeriesMetaInformation timeSeriesMetaInformation(String fileName) {
    /* Remove the file ending (ending limited to 255 chars, which is the max file name allowed in NTFS and ext4) */
    String withoutEnding = removeFileNameEnding(fileName);

    if (getIndividualTimeSeriesPattern().matcher(withoutEnding).matches())
      return entityPersistenceNamingStrategy.individualTimesSeriesMetaInformation(withoutEnding);
    else if (getLoadProfileTimeSeriesPattern().matcher(withoutEnding).matches())
      return entityPersistenceNamingStrategy.loadProfileTimesSeriesMetaInformation(withoutEnding);
    else
      throw new IllegalArgumentException(
          "Unknown format of '" + fileName + "'. Cannot extract meta information.");
  }

  public IndividualTimeSeriesMetaInformation individualTimeSeriesMetaInformation(String fileName) {
    return entityPersistenceNamingStrategy.individualTimesSeriesMetaInformation(
        removeFileNameEnding(fileName));
  }

  public LoadProfileMetaInformation loadProfileTimeSeriesMetaInformation(String fileName) {
    return entityPersistenceNamingStrategy.loadProfileTimesSeriesMetaInformation(
        removeFileNameEnding(fileName));
  }

  public static String removeFileNameEnding(String fileName) {
    return fileName.replaceAll("(?:\\.[^.\\\\/\\s]{1,255}){1,2}$", "");
  }

  public static Path removeFileNameEnding(Path filename) {
    return Path.of(removeFileNameEnding(filename.toString()));
  }

  /**
   * Get the entity name for coordinates
   *
   * @return the entity name string
   */
  public String getIdCoordinateEntityName() {
    return entityPersistenceNamingStrategy.getIdCoordinateEntityName();
  }

  /**
   * Returns the name of the entity, that should be used for persistence.
   *
   * @param cls Targeted class of the given file
   * @return The name of the entity
   */
  public Optional<String> getEntityName(Class<? extends Entity> cls) {
    return entityPersistenceNamingStrategy.getEntityName(cls);
  }

  /**
   * Builds a file name (and only the file name without any directories and extension) of the given
   * information.
   *
   * @param <T> Type of the time series
   * @param <E> Type of the entry in the time series
   * @param <V> Type of the value, that is carried by the time series entry
   * @param timeSeries Time series to derive naming information from
   * @return A file name for this particular time series
   */
  public <
          T extends TimeSeries<E, V, R>,
          E extends TimeSeriesEntry<V>,
          V extends Value,
          R extends Value>
      Optional<String> getEntityName(T timeSeries) {
    return entityPersistenceNamingStrategy.getEntityName(timeSeries);
  }
}
