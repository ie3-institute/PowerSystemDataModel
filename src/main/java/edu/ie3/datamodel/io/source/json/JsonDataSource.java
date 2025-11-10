/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.json;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.connectors.JsonFileConnector;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.file.FileDataSource;
import edu.ie3.datamodel.models.Entity;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/** Data source abstraction for JSON files. */
public class JsonDataSource extends FileDataSource {

  private final JsonFileConnector connector;

  public JsonDataSource(Path directoryPath, FileNamingStrategy fileNamingStrategy) {
    this(new JsonFileConnector(directoryPath), fileNamingStrategy);
  }

  public JsonDataSource(JsonFileConnector connector, FileNamingStrategy fileNamingStrategy) {
    super(connector.getBaseDirectory(), fileNamingStrategy);
    this.connector = connector;
  }

  /**
   * Opens a buffered reader for the provided file path.
   *
   * @param filePath relative path without ending
   * @return buffered reader
   * @throws SourceException if the file cannot be opened
   */
  public BufferedReader initReader(Path filePath) throws SourceException {
    try {
      return connector.initReader(filePath);
    } catch (FileNotFoundException e) {
      throw new SourceException("Unable to open JSON file '" + filePath + "'.", e);
    }
  }

  /**
   * Opens an input stream for the provided file path.
   *
   * @param filePath relative path without ending
   * @return input stream
   * @throws SourceException if the file cannot be opened
   */
  public InputStream initInputStream(Path filePath) throws SourceException {
    try {
      return connector.initInputStream(filePath);
    } catch (FileNotFoundException e) {
      throw new SourceException("Unable to open JSON file '" + filePath + "'.", e);
    }
  }

  /**
   * Utility method that reads the entire JSON file into memory.
   *
   * @param filePath relative path without ending
   * @return optional JSON string (empty if the file does not exist)
   * @throws SourceException if reading fails
   */
  public Optional<String> readRaw(Path filePath) throws SourceException {
    try (Reader reader = connector.initReader(filePath)) {
      return Optional.of(readAll(reader));
    } catch (FileNotFoundException e) {
      return Optional.empty();
    } catch (IOException e) {
      throw new SourceException("Unable to read JSON file '" + filePath + "'.", e);
    }
  }

  /**
   * Reads the JSON file using the provided consumer function.
   *
   * @param filePath relative path without ending
   * @param readerFunction function that consumes the reader
   * @param <T> result type
   * @return optional result (empty if file not found)
   * @throws SourceException if reading fails
   */
  public <T> Optional<T> readWith(Path filePath, Function<BufferedReader, T> readerFunction)
      throws SourceException {
    try (BufferedReader reader = connector.initReader(filePath)) {
      return Optional.ofNullable(readerFunction.apply(reader));
    } catch (FileNotFoundException e) {
      return Optional.empty();
    } catch (IOException e) {
      throw new SourceException("Unable to read JSON file '" + filePath + "'.", e);
    }
  }

  @Override
  public Optional<Set<String>> getSourceFields(Class<? extends Entity> entityClass)
      throws SourceException {
    throw unsupportedTabularAccess("getSourceFields(Class)");
  }

  @Override
  public Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass)
      throws SourceException {
    throw unsupportedTabularAccess("getSourceData(Class)");
  }

  @Override
  public Optional<Set<String>> getSourceFields(Path filePath) throws SourceException {
    throw unsupportedTabularAccess("getSourceFields(Path)");
  }

  @Override
  public Stream<Map<String, String>> getSourceData(Path filePath) throws SourceException {
    throw unsupportedTabularAccess("getSourceData(Path)");
  }

  private UnsupportedOperationException unsupportedTabularAccess(String method) {
    return new UnsupportedOperationException(
        "JsonDataSource does not support '" + method + "', as JSON sources are not tabular.");
  }

  private String readAll(Reader reader) throws IOException {
    StringBuilder builder = new StringBuilder();
    char[] buffer = new char[4096];
    int read;
    while ((read = reader.read(buffer)) != -1) {
      builder.append(buffer, 0, read);
    }
    return builder.toString();
  }
}
