/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.RawGridSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.container.RawGridElements;

/**
 * Source that provides the capability to build entities that are hold by a {@link RawGridElements}
 * as well as the {@link RawGridElements} container from .csv files.
 *
 * <p>This source is <b>not buffered</b> which means each call on a getter method always tries to
 * read all data is necessary to return the requested objects in a hierarchical cascading way.
 *
 * <p>If performance is an issue, it is recommended to read the data cascading starting with reading
 * nodes and then using the getters with arguments to avoid reading the same data multiple times.
 *
 * <p>The resulting sets are always unique on object <b>and</b> UUID base (with distinct UUIDs).
 *
 * @version 0.1
 * @since 03.04.20
 */
public class CsvRawGridSource extends RawGridSource {

  public CsvRawGridSource(
          String csvSep,
          String gridFolderPath,
          FileNamingStrategy fileNamingStrategy,
          TypeSource typeSource
  ) {
    super(typeSource, new CsvDataSource(csvSep, gridFolderPath, fileNamingStrategy));
  }

}
