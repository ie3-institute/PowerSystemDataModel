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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
}
