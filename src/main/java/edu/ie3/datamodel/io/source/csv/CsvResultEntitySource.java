/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.ResultEntitySource;
import edu.ie3.datamodel.models.result.ResultEntity;

/**
 * Source that provides the capability to build entities of type {@link ResultEntity} container from
 * .csv files.
 *
 * <p>This source is <b>not buffered</b> which means each call on a getter method always tries to
 * read all data is necessary to return the requested objects in a hierarchical cascading way.
 *
 * <p>The resulting sets are always unique on object <b>and</b> UUID base (with distinct UUIDs).
 *
 * @version 0.1
 * @since 22 June 2021
 */
public class CsvResultEntitySource extends ResultEntitySource {

  public CsvResultEntitySource(
      String csvSep, String folderPath, FileNamingStrategy fileNamingStrategy) {
    super(new CsvDataSource(csvSep, folderPath, fileNamingStrategy));
  }

  public CsvResultEntitySource(
      String csvSep, String folderPath, FileNamingStrategy fileNamingStrategy, String dtfPattern) {
    super(new CsvDataSource(csvSep, folderPath, fileNamingStrategy), dtfPattern);
  }

}
