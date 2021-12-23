/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.io.IoUtil;
import edu.ie3.datamodel.io.csv.FileNameMetaInformation;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput;
import edu.ie3.datamodel.models.value.Value;
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
  public Optional<String> getFilePath(Class<? extends UniqueEntity> cls) {
    // do not adapt orElseGet, see https://www.baeldung.com/java-optional-or-else-vs-or-else-get for
    // details
    return getFilePath(getEntityName(cls).orElse(""), getDirectoryPath(cls).orElse(""));
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
  public <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      Optional<String> getFilePath(T timeSeries) {
    // do not adapt orElseGet, see https://www.baeldung.com/java-optional-or-else-vs-or-else-get for
    // details
    return getFilePath(
        entityPersistenceNamingStrategy.getEntityName(timeSeries).orElse(""),
        getDirectoryPath(timeSeries).orElse(""));
  }

  /**
   * Compose a full file path from directory name and file name. Additionally perform some checks,
   * like if the file name itself actually is available
   *
   * @param fileName File name
   * @param subDirectories Sub directory path
   * @return Concatenation of sub directory structure and file name
   */
  private Optional<String> getFilePath(String fileName, String subDirectories) {
    if (fileName.isEmpty()) return Optional.empty();
    if (!subDirectories.isEmpty())
      return Optional.of(FilenameUtils.concat(subDirectories, fileName));
    else return Optional.of(fileName);
  }

  /**
   * Returns the sub directory structure with regard to some (not explicitly specified) base
   * directory. The path does NOT start or end with any of the known file separators.
   *
   * @param cls Targeted class of the given file
   * @return An optional sub directory path
   */
  public Optional<String> getDirectoryPath(Class<? extends UniqueEntity> cls) {
    Optional<String> maybeDirectoryName = fileHierarchy.getSubDirectory(cls);
    if (!maybeDirectoryName.isPresent()) {
      logger.debug("Cannot determine directory name for class '{}'.", cls);
      return Optional.empty();
    } else {
      /* Make sure, the directory path does not start or end with file separator and in between the separator is harmonized */
      return Optional.of(
          IoUtil.harmonizeFileSeparator(
              maybeDirectoryName
                  .get()
                  .replaceFirst("^" + IoUtil.FILE_SEPARATOR_REGEX, "")
                  .replaceAll(IoUtil.FILE_SEPARATOR_REGEX + "$", "")));
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
  public <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      Optional<String> getDirectoryPath(T timeSeries) {
    Optional<String> maybeDirectoryName = fileHierarchy.getSubDirectory(timeSeries.getClass());
    if (!maybeDirectoryName.isPresent()) {
      logger.debug("Cannot determine directory name for time series '{}'.", timeSeries);
      return Optional.empty();
    } else {
      /* Make sure, the directory path does not start or end with file separator and in between the separator is harmonized */
      return Optional.of(
          IoUtil.harmonizeFileSeparator(
              maybeDirectoryName
                  .get()
                  .replaceFirst("^" + IoUtil.FILE_SEPARATOR_REGEX, "")
                  .replaceAll(IoUtil.FILE_SEPARATOR_REGEX + "$", "")));
    }
  }

  /**
   * Returns the pattern to identify individual time series in this instance of the file naming
   * strategy considering the {@link EntityPersistenceNamingStrategy} and {@link FileHierarchy}.
   *
   * @return An individual time series pattern
   */
  public Pattern getIndividualTimeSeriesPattern() {
    String subDirectory = fileHierarchy.getSubDirectory(IndividualTimeSeries.class).orElse("");

    if (subDirectory.isEmpty()) {
      return entityPersistenceNamingStrategy.getIndividualTimeSeriesPattern();
    } else {
      /* Build the pattern by joining the subdirectory with the file name pattern, harmonizing file separators and
       * finally escaping them */
      String joined =
          FilenameUtils.concat(
              subDirectory,
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
    String subDirectory = fileHierarchy.getSubDirectory(LoadProfileInput.class).orElse("");

    if (subDirectory.isEmpty()) {
      return entityPersistenceNamingStrategy.getLoadProfileTimeSeriesPattern();
    } else {
      /* Build the pattern by joining the sub directory with the file name pattern, harmonizing file separators and
       * finally escaping them */
      String joined =
          FilenameUtils.concat(
              subDirectory,
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
  public FileNameMetaInformation extractTimeSeriesMetaInformation(Path path) {
    /* Extract file name from possibly fully qualified path */
    Path fileName = path.getFileName();
    if (fileName == null)
      throw new IllegalArgumentException("Unable to extract file name from path '" + path + "'.");
    return extractTimeSeriesMetaInformation(fileName.toString());
  }

  /**
   * Extracts meta information from a file name, of a time series. Here, a file name <u>without</u>
   * leading path has to be provided
   *
   * @param fileName File name
   * @return The meeting meta information
   */
  public FileNameMetaInformation extractTimeSeriesMetaInformation(String fileName) {
    /* Remove the file ending (ending limited to 255 chars, which is the max file name allowed in NTFS and ext4) */
    String withoutEnding = fileName.replaceAll("(?:\\.[^\\\\/\\s]{1,255}){1,2}$", "");

    if (getIndividualTimeSeriesPattern().matcher(withoutEnding).matches())
      return entityPersistenceNamingStrategy.extractIndividualTimesSeriesMetaInformation(
          withoutEnding);
    else if (getLoadProfileTimeSeriesPattern().matcher(withoutEnding).matches())
      return entityPersistenceNamingStrategy.extractLoadProfileTimesSeriesMetaInformation(
          withoutEnding);
    else
      throw new IllegalArgumentException(
          "Unknown format of '" + fileName + "'. Cannot extract meta information.");
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
   * Get the full path to the id coordinate file with regard to some (not explicitly specified) base
   * directory. The path does NOT start or end with any of the known file separators or file
   * extension.
   *
   * @return An optional sub path to the id coordinate file
   */
  public Optional<String> getIdCoordinateFilePath() {
    // do not adapt orElseGet, see https://www.baeldung.com/java-optional-or-else-vs-or-else-get for
    // details
    return getFilePath(getIdCoordinateEntityName(), fileHierarchy.getBaseDirectory().orElse(""));
  }

  /**
   * Returns the name of the entity, that should be used for persistence.
   *
   * @param cls Targeted class of the given file
   * @return The name of the entity
   */
  public Optional<String> getEntityName(Class<? extends UniqueEntity> cls) {
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
  public <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      Optional<String> getEntityName(T timeSeries) {
    return entityPersistenceNamingStrategy.getEntityName(timeSeries);
  }
}
