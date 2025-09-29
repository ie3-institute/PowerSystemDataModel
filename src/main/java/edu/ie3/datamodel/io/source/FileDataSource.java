/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.utils.Try;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FileDataSource implements DataSource {

  protected final Logger log = LoggerFactory.getLogger(getClass());
  private final Path baseDirectory;
  private final FileNamingStrategy fileNamingStrategy;

  protected FileDataSource(Path directoryPath, FileNamingStrategy fileNamingStrategy) {
    this.baseDirectory = Objects.requireNonNull(directoryPath, "directoryPath");
    this.fileNamingStrategy = Objects.requireNonNull(fileNamingStrategy, "fileNamingStrategy");
  }

  @Override
  public Optional<Set<String>> getSourceFields(Class<? extends Entity> entityClass)
      throws SourceException {
    return getSourceFields(getFilePath(entityClass).getOrThrow());
  }

  public abstract Optional<Set<String>> getSourceFields(Path filePath) throws SourceException;

  @Override
  public Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass)
      throws SourceException {
    return buildStreamWithFieldsToAttributesMap(entityClass, true).getOrThrow();
  }

  public abstract Stream<Map<String, String>> getSourceData(Path filePath) throws SourceException;

  public FileNamingStrategy getNamingStrategy() {
    return fileNamingStrategy;
  }

  protected Path getBaseDirectory() {
    return baseDirectory;
  }

  protected Try<Stream<Map<String, String>>, SourceException> buildStreamWithFieldsToAttributesMap(
      Class<? extends Entity> entityClass, boolean allowFileNotExisting) {
    return getFilePath(entityClass)
        .flatMap(path -> buildStreamWithFieldsToAttributesMap(path, allowFileNotExisting));
  }

  protected abstract Try<Stream<Map<String, String>>, SourceException>
      buildStreamWithFieldsToAttributesMap(Path filePath, boolean allowFileNotExisting);

  protected Try<Path, SourceException> getFilePath(Class<? extends Entity> entityClass) {
    return Try.from(
        fileNamingStrategy.getFilePath(entityClass),
        () ->
            new SourceException(
                "Cannot find a naming strategy for class '" + entityClass.getSimpleName() + "'."));
  }

  protected Set<Path> getTimeSeriesFilePaths(Pattern pattern) {
    Path baseDirectory = getBaseDirectory();
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
}
