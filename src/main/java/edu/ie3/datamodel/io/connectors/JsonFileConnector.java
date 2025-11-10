/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.connectors;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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
   * Opens a buffered reader for the given JSON file, using UTF-8 decoding.
   *
   * @param filePath relative path without ending
   * @return buffered reader referencing the JSON file
   */
  public BufferedReader initReader(Path filePath) throws FileNotFoundException {
    InputStream inputStream = openInputStream(filePath);
    return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8), 16384);
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
