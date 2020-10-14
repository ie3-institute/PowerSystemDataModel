/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.csv;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.timeseries.TimeSeries;
import edu.ie3.datamodel.models.timeseries.TimeSeriesEntry;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.LoadProfileInput;
import edu.ie3.datamodel.models.value.Value;
import java.util.Optional;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;

/**
 * A file naming strategy, that takes hierarchic order of sub folders into account. For the standard
 * structure that can be found in the documentation {@link DefaultInputHierarchy} can be used
 */
public class HierarchicFileNamingStrategy extends FileNamingStrategy {
  private static final String ALL_POSSIBLE_FILE_SEPARATORS_AT_START = "^[\\\\/]";

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
   * Determines the filename for a certain class by composing the hierarchic sub directory structure
   * provided by {@link #hierarchy} with the actual file name given by {@link FileNamingStrategy}.
   * If one of both components cannot be determined, an empty {@link Optional} is returned.
   *
   * @param cls The class to define the file name for.
   * @return The file name including the hierarchic sub directory structure
   */
  @Override
  public Optional<String> getFileName(Class<? extends UniqueEntity> cls) {
    /* Get the file name */
    Optional<String> maybeFilename = super.getFileName(cls);
    String fileName;
    if (!maybeFilename.isPresent()) {
      logger.debug("Cannot determine file name for class '{}'.", cls);
      return Optional.empty();
    } else {
      /* Make sure, that the file name does not start with a file separator */
      fileName = maybeFilename.get().replaceFirst(ALL_POSSIBLE_FILE_SEPARATORS_AT_START, "");
    }

    /* Get the directory name */
    Optional<String> maybeDirectoryName = hierarchy.getSubDirectory(cls);
    String directoryName;
    if (!maybeDirectoryName.isPresent()) {
      logger.debug("Cannot determine directory name for class '{}'.", cls);
      return Optional.empty();
    } else {
      /* Make sure, that the directory name does not start with a file separator */
      directoryName =
          maybeDirectoryName
              .get()
              .replaceFirst(ALL_POSSIBLE_FILE_SEPARATORS_AT_START, "")
              .replaceAll("[\\\\/]", "/");
    }

    /* Put everything together and return it */
    String fullName = FilenameUtils.concat(directoryName, fileName);

    return Optional.of(fullName);
  }

  @Override
  public <T extends TimeSeries<E, V>, E extends TimeSeriesEntry<V>, V extends Value>
      Optional<String> getFileName(T timeSeries) {
    /* Get the file name */
    Optional<String> maybeFilename = super.getFileName(timeSeries);
    String fileName;
    if (!maybeFilename.isPresent()) {
      logger.debug("Cannot determine file name for time series '{}'.", timeSeries);
      return Optional.empty();
    } else {
      /* Make sure, that the file name does not start with a file separator */
      fileName = maybeFilename.get().replaceFirst(ALL_POSSIBLE_FILE_SEPARATORS_AT_START, "");
    }

    /* Get the directory name */
    Optional<String> maybeDirectoryName = hierarchy.getSubDirectory(timeSeries.getClass());
    String directoryName;
    if (!maybeDirectoryName.isPresent()) {
      logger.debug("Cannot determine directory name for class '{}'.", timeSeries.getClass());
      return Optional.empty();
    } else {
      /* Make sure, that the directory name does not start with a file separator */
      directoryName =
          maybeDirectoryName
              .get()
              .replaceFirst(ALL_POSSIBLE_FILE_SEPARATORS_AT_START, "")
              .replaceAll("[\\\\/]", "/");
    }

    /* Put everything together and return it */
    String fullName = FilenameUtils.concat(directoryName, fileName);

    return Optional.of(fullName);
  }
}
