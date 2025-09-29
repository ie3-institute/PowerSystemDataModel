/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Function;

/** Base connector for file-based sources and sinks. */
public abstract class FileConnector implements DataConnector {

  private final Path baseDirectory;
  private final Optional<Function<String, InputStream>> customInputStream;

  protected FileConnector(Path baseDirectory) {
    this(baseDirectory, null);
  }

  protected FileConnector(Path baseDirectory, Function<String, InputStream> inputStreamSupplier) {
    this.baseDirectory = baseDirectory;
    this.customInputStream = Optional.ofNullable(inputStreamSupplier);
  }

  /** Returns the base directory backing this connector. */
  public Path getBaseDirectory() {
    return baseDirectory;
  }

  /**
   * Open an {@link InputStream} to the given file path (without file ending) relative to the base
   * directory.
   */
  protected InputStream openInputStream(Path filePath) throws FileNotFoundException {
    Path fullPath = resolveFilePath(filePath);
    if (customInputStream.isPresent()) {
      return customInputStream.get().apply(fullPath.toString());
    }
    return new FileInputStream(fullPath.toFile());
  }

  /** Resolve the path including the file ending relative to the base directory. */
  protected Path resolveFilePath(Path filePath) {
    String relativePath = filePath.toString();
    if (!relativePath.endsWith(getFileEnding())) {
      relativePath = relativePath + getFileEnding();
    }
    return baseDirectory.resolve(relativePath);
  }

  /** Returns the file ending (including the dot) handled by this connector. */
  protected abstract String getFileEnding();

  @Override
  public void shutdown() {
  }
}
