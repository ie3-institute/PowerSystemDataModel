/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.file;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.file.FileType;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.naming.timeseries.*;
import edu.ie3.datamodel.io.source.DataSource;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.utils.Try;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileDataSource implements DataSource {

  protected final Logger log = LoggerFactory.getLogger(getClass());
  protected final Path baseDirectory;
  protected final FileNamingStrategy fileNamingStrategy;

  protected FileDataSource(Path directoryPath, FileNamingStrategy fileNamingStrategy) {
    this.baseDirectory = Objects.requireNonNull(directoryPath, "directoryPath");
    this.fileNamingStrategy = Objects.requireNonNull(fileNamingStrategy, "fileNamingStrategy");
  }

  public abstract Optional<Set<String>> getSourceFields(Path filePath) throws SourceException;

  public abstract Stream<Map<String, String>> getSourceData(Path filePath) throws SourceException;

  public FileNamingStrategy getNamingStrategy() {
    return fileNamingStrategy;
  }

  protected Try<Path, SourceException> getFilePath(Class<? extends Entity> entityClass) {
    return Try.from(
        fileNamingStrategy.getFilePath(entityClass),
        () ->
            new SourceException(
                "Cannot find a naming strategy for class '" + entityClass.getSimpleName() + "'."));
  }

  protected Set<Path> getTimeSeriesFilePaths(Pattern pattern) {
    try (Stream<Path> pathStream = Files.walk(baseDirectory)) {
      return pathStream
          .map(baseDirectory::relativize)
          .filter(
              path -> {
                Path withoutEnding =
                    Path.of(FileNamingStrategy.removeFileNameEnding(path.toString()));
                return pattern.matcher(withoutEnding.toString()).matches();
              })
          .collect(Collectors.toSet());
    } catch (IOException e) {
      log.error("Unable to determine time series files readers for time series.", e);
      return Collections.emptySet();
    }
  }

  public Stream<FileIndividualTimeSeriesMetaInformation> getIndividualTimeSeriesMetaInformation(
      final ColumnScheme... columnSchemes) {
    return getTimeSeriesFilePaths(fileNamingStrategy.getIndividualTimeSeriesPattern())
        .parallelStream()
        .map(filePath -> resolveFileInformation(filePath, "individual time series"))
        .flatMap(Optional::stream)
        .map(
            fileMeta -> {
              IndividualTimeSeriesMetaInformation metaInformation =
                  fileNamingStrategy.individualTimeSeriesMetaInformation(
                      fileMeta.filePath().toString());
              return new FileIndividualTimeSeriesMetaInformation(
                  metaInformation, fileMeta.pathWithoutEnding(), fileMeta.fileType());
            })
        .filter(
            metaInformation ->
                columnSchemes == null
                    || columnSchemes.length == 0
                    || Stream.of(columnSchemes)
                        .anyMatch(scheme -> scheme.equals(metaInformation.getColumnScheme())));
  }

  /**
   * Receive the information for specific load profile time series. They are given back mapped to
   * their uuid.
   *
   * @return A mapping from profile to the load profile time series meta information
   */
  public Stream<FileLoadProfileMetaInformation> getLoadProfileMetaInformation(
      LoadProfile... profiles) {
    return getTimeSeriesFilePaths(fileNamingStrategy.getLoadProfileTimeSeriesPattern())
        .parallelStream()
        .map(filePath -> resolveFileInformation(filePath, "load profile"))
        .flatMap(Optional::stream)
        .map(
            fileMeta -> {
              LoadProfileMetaInformation metaInformation =
                  fileNamingStrategy.loadProfileTimeSeriesMetaInformation(
                      fileMeta.filePath().toString());
              return new FileLoadProfileMetaInformation(
                  metaInformation, fileMeta.pathWithoutEnding(), fileMeta.fileType());
            })
        .filter(
            metaInformation ->
                profiles == null
                    || profiles.length == 0
                    || Stream.of(profiles)
                        .anyMatch(profile -> metaInformation.getProfileKey().equals(profile)));
  }

  private Optional<FileMetaDetails> resolveFileInformation(Path filePath, String metaType) {
    String fileName = filePath.getFileName().toString();
    try {
      FileType fileType = FileType.getFileType(fileName);
      Path pathWithoutEnding = Path.of(FileNamingStrategy.removeFileNameEnding(fileName));
      return Optional.of(new FileMetaDetails(filePath, pathWithoutEnding, fileType));
    } catch (ParsingException e) {
      log.warn("Unable to load {} meta data for {}", metaType, fileName, e);
      return Optional.empty();
    }
  }

  private record FileMetaDetails(Path filePath, Path pathWithoutEnding, FileType fileType) {}
}
