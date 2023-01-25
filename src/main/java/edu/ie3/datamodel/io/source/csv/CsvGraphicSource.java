/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.GraphicSource;
import edu.ie3.datamodel.io.source.RawGridSource;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;

/**
 * Implementation of the {@link GraphicSource} interface to read {@link NodeGraphicInput} and {@link
 * LineGraphicInput} entities from .csv files
 *
 * @version 0.1
 * @since 08.04.20
 */
public class CsvGraphicSource extends GraphicSource {
  public CsvGraphicSource(
      String csvSep,
      String folderPath,
      FileNamingStrategy fileNamingStrategy,
      TypeSource typeSource,
      RawGridSource rawGridSource) {
    super(typeSource, rawGridSource, new CsvDataSource(csvSep, folderPath, fileNamingStrategy));
  }
}
