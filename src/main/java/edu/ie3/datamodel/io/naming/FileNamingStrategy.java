/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.io.csv.FileNameMetaInformation;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput;
import edu.ie3.datamodel.models.value.Value;
import java.io.File;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A naming strategy, that combines an {@link EntityNamingStrategy} for naming entities and a {@link
 * DirectoryHierarchy} for a folder structure.
 */
public class FileNamingStrategy {

  protected static final Logger logger = LogManager.getLogger(FileNamingStrategy.class);

  private static final String FILE_SEPARATOR_REGEX = "[\\\\/]";
  private static final String FILE_SEPARATOR_REPLACEMENT =
      File.separator.equals("\\") ? "\\\\" : "/";

  private final EntityNamingStrategy entityNamingStrategy;
  private final DirectoryHierarchy directoryHierarchy;

  /**
   * Constructor for building the file naming strategy.
   *
   * @param entityNamingStrategy entity naming strategy
   * @param directoryHierarchy directory hierarchy
   */
  public FileNamingStrategy(
      EntityNamingStrategy entityNamingStrategy, DirectoryHierarchy directoryHierarchy) {
    this.entityNamingStrategy = entityNamingStrategy;
    this.directoryHierarchy = directoryHierarchy;
  }

  /**
   * Constructor for building the file naming strategy. Since no directory hierarchy is provided, a
   * flat directory hierarchy is used.
   *
   * @param entityNamingStrategy entity naming strategy
   */
  public FileNamingStrategy(EntityNamingStrategy entityNamingStrategy) {
    this.entityNamingStrategy = entityNamingStrategy;
    this.directoryHierarchy = new FlatDirectoryHierarchy();
  }

  /**
   * Constructor for building the file naming strategy. Since no entity naming strategy is provided,
   * the entity naming strategy is used. Since no directory hierarchy is provided, a flat directory
   * hierarchy is used.
   */
  public FileNamingStrategy() {
    this.entityNamingStrategy = new EntityNamingStrategy();
    this.directoryHierarchy = new FlatDirectoryHierarchy();
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
    return getFilePath(
        entityNamingStrategy.getEntityName(cls).orElseGet(() -> ""),
        getDirectoryPath(cls).orElseGet(() -> ""));
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
        entityNamingStrategy.getEntityName(timeSeries).orElseGet(() -> ""),
        getDirectoryPath(timeSeries).orElseGet(() -> ""));
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
    Optional<String> maybeDirectoryName = directoryHierarchy.getSubDirectory(cls);
    String directoryPath;
    if (!maybeDirectoryName.isPresent()) {
      logger.debug("Cannot determine directory name for class '{}'.", cls);
      return Optional.empty();
    } else {
      /* Make sure, the directory path does not start or end with file separator and in between the separator is harmonized */
      directoryPath =
          maybeDirectoryName
              .get()
              .replaceFirst("^" + FILE_SEPARATOR_REGEX, "")
              .replaceAll(FILE_SEPARATOR_REGEX + "$", "")
              .replaceAll(FILE_SEPARATOR_REGEX, FILE_SEPARATOR_REPLACEMENT);
      return Optional.of(directoryPath);
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
    Optional<String> maybeDirectoryName = directoryHierarchy.getSubDirectory(timeSeries.getClass());
    String directoryPath;
    if (!maybeDirectoryName.isPresent()) {
      logger.debug("Cannot determine directory name for time series '{}'.", timeSeries);
      return Optional.empty();
    } else {
      /* Make sure, the directory path does not start or end with file separator and in between the separator is harmonized */
      directoryPath =
          maybeDirectoryName
              .get()
              .replaceFirst("^" + FILE_SEPARATOR_REGEX, "")
              .replaceAll(FILE_SEPARATOR_REGEX + "$", "")
              .replaceAll(FILE_SEPARATOR_REGEX, FILE_SEPARATOR_REPLACEMENT);
      return Optional.of(directoryPath);
    }
  }

  public Pattern getIndividualTimeSeriesPattern() {
    String subDirectory = directoryHierarchy.getSubDirectory(IndividualTimeSeries.class).orElse("");
    return subDirectory.isEmpty()
        ? entityNamingStrategy.getIndividualTimeSeriesPattern()
        : Pattern.compile(
            FilenameUtils.concat(
                subDirectory, entityNamingStrategy.getIndividualTimeSeriesPattern().pattern()));
  }

  public Pattern getLoadProfileTimeSeriesPattern() {
    String subDirectory = directoryHierarchy.getSubDirectory(LoadProfileInput.class).orElse("");
    return subDirectory.isEmpty()
        ? entityNamingStrategy.getLoadProfileTimeSeriesPattern()
        : Pattern.compile(
            FilenameUtils.concat(
                subDirectory, entityNamingStrategy.getLoadProfileTimeSeriesPattern().pattern()));
  }

  /**
   * Extracts meta information from a file name, of a time series. Here, a file name <u>without</u>
   * leading path has to be provided
   *
   * @param fileName File name
   * @return The meeting meta information
   */
  public FileNameMetaInformation extractTimeSeriesMetaInformation(String fileName) {
    return entityNamingStrategy.extractTimeSeriesMetaInformation(fileName);
  }

  /**
   * Get the entity name for coordinates
   *
   * @return the entity name string
   */
  public String getIdCoordinateEntityName() {
    return entityNamingStrategy.getIdCoordinateEntityName();
  }

  /**
   * Returns the name of the entity, that should be used for persistence.
   *
   * @param cls Targeted class of the given file
   * @return The name of the entity
   */
  public Optional<String> getEntityName(Class<? extends UniqueEntity> cls) {
    return entityNamingStrategy.getEntityName(cls);
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
    return entityNamingStrategy.getEntityName(timeSeries);
  }
}
