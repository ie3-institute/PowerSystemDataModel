package edu.ie3.datamodel.io;

import edu.ie3.datamodel.io.csv.DefaultDirectoryHierarchy;
import edu.ie3.datamodel.io.csv.DirectoryNamingStrategy;
import edu.ie3.datamodel.io.csv.FlatDirectoryHierarchy;
import edu.ie3.datamodel.io.naming.EntityPersistenceNamingStrategy;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.value.Value;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.Optional;

public class FileNamingStrategy {

  protected static final Logger logger =
          LogManager.getLogger(FileNamingStrategy.class);

  private static final String FILE_SEPARATOR_REGEX = "[\\\\/]";
  private static final String FILE_SEPARATOR_REPLACEMENT =
          File.separator.equals("\\") ? "\\\\" : "/";

  private final EntityPersistenceNamingStrategy entityPersistenceNamingStrategy;
  private final DirectoryNamingStrategy directoryNamingStrategy;
  private final String fileExtension;


  public FileNamingStrategy(EntityPersistenceNamingStrategy entityPersistenceNamingStrategy, DirectoryNamingStrategy directoryNamingStrategy, String fileExtension) {
    this.entityPersistenceNamingStrategy = entityPersistenceNamingStrategy;
    this.directoryNamingStrategy = directoryNamingStrategy;
    this.fileExtension = fileExtension;
  }

  public FileNamingStrategy(EntityPersistenceNamingStrategy entityPersistenceNamingStrategy) {
    this.entityPersistenceNamingStrategy = entityPersistenceNamingStrategy;
    this.directoryNamingStrategy = new FlatDirectoryHierarchy();
    this.fileExtension = ".csv";
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
            entityPersistenceNamingStrategy.getEntityName(cls).orElseGet(() -> ""), getDirectoryPath(cls).orElseGet(() -> ""));
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
            entityPersistenceNamingStrategy.getEntityName(timeSeries).orElseGet(() -> ""),
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
    Optional<String> maybeDirectoryName = directoryNamingStrategy.getSubDirectory(cls);
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

  public <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
  Optional<String> getDirectoryPath(T timeSeries) {
    Optional<String> maybeDirectoryName = directoryNamingStrategy.getSubDirectory(timeSeries.getClass());
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

}
