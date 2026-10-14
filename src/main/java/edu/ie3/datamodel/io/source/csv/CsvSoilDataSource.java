/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.SoilDataSource;
import edu.ie3.datamodel.models.Entity;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Stream;

/** CSV source for soil layers and soil types. */
public class CsvSoilDataSource extends SoilDataSource {
  private final CsvDataSource dataSource;

  /**
   * Constructor for a CSV source of soil data.
   *
   * @param csvSeparator the CSV field separator
   * @param directoryPath the base directory of the input data
   * @param fileNamingStrategy the naming strategy used to locate the files
   */
  public CsvSoilDataSource(
      String csvSeparator, Path directoryPath, FileNamingStrategy fileNamingStrategy) {
    this.dataSource = new CsvDataSource(csvSeparator, directoryPath, fileNamingStrategy);
  }

  @Override
  protected Stream<Map<String, String>> getSourceData(Class<? extends Entity> entityClass)
      throws SourceException {
    return dataSource.getSourceData(entityClass);
  }
}
