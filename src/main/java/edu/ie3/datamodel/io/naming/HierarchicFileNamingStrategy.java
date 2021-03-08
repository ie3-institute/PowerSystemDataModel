/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.io.csv.DefaultDirectoryHierarchy;
import edu.ie3.datamodel.io.csv.FileHierarchy;
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

/**
 * A file naming strategy, that takes hierarchic order of sub folders into account. For the standard
 * structure that can be found in the documentation {@link DefaultDirectoryHierarchy} can be used
 */
public class HierarchicFileNamingStrategy extends EntityPersistenceNamingStrategy {
  private static final String FILE_SEPARATOR_REGEX = "[\\\\/]";
  private static final String FILE_SEPARATOR_REPLACEMENT =
      File.separator.equals("\\") ? "\\\\" : "/";

  private final FileHierarchy hierarchy;

  public HierarchicFileNamingStrategy(String prefix, String suffix, FileHierarchy hierarchy) {
    super(prefix, suffix);
    this.hierarchy = hierarchy;
  }

  public HierarchicFileNamingStrategy(FileHierarchy hierarchy) {
    this.hierarchy = hierarchy;
  }

  public HierarchicFileNamingStrategy(String prefix, FileHierarchy hierarchy) {
    super(prefix);
    this.hierarchy = hierarchy;
  }

  @Override
  public Pattern getIndividualTimeSeriesPattern() {
    String subDirectory = hierarchy.getSubDirectory(IndividualTimeSeries.class).orElse("");
    return subDirectory.isEmpty()
        ? super.getIndividualTimeSeriesPattern()
        : Pattern.compile(
            FilenameUtils.concat(subDirectory, super.getIndividualTimeSeriesPattern().pattern()));
  }

  @Override
  public Pattern getLoadProfileTimeSeriesPattern() {
    String subDirectory = hierarchy.getSubDirectory(LoadProfileInput.class).orElse("");
    return subDirectory.isEmpty()
        ? super.getLoadProfileTimeSeriesPattern()
        : Pattern.compile(
            FilenameUtils.concat(subDirectory, super.getLoadProfileTimeSeriesPattern().pattern()));
  }

  /**
   * Returns the sub directory structure with regard to some (not explicitly specified) base
   * directory. The path does NOT start or end with any of the known file separators.
   *
   * @param cls Targeted class of the given file
   * @return An optional sub directory path
   */
  @Override
  public Optional<String> getDirectoryPath(Class<? extends UniqueEntity> cls) {
    Optional<String> maybeDirectoryName = hierarchy.getSubDirectory(cls);
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

  @Override
  public <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      Optional<String> getDirectoryPath(T timeSeries) {
    Optional<String> maybeDirectoryName = hierarchy.getSubDirectory(timeSeries.getClass());
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
