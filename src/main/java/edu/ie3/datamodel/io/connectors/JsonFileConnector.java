/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.function.Function;

/** Connector for JSON-based sources and sinks. */
public class JsonFileConnector extends FileConnector {
  private static final String FILE_ENDING = ".json";

  public JsonFileConnector(Path baseDirectory) {
    super(baseDirectory);
  }

  public JsonFileConnector(Path baseDirectory, Function<String, InputStream> customInputStream) {
    super(baseDirectory, customInputStream);
  }

  /**
   * Opens an input stream for the given JSON file.
   *
   * @param filePath relative path without ending
   * @return input stream for the file
   */
  public InputStream initInputStream(Path filePath) throws FileNotFoundException {
    return openInputStream(filePath);
  }

  @Override
  protected String getFileEnding() {
    return FILE_ENDING;
  }
}
