/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source.csv;

import edu.ie3.datamodel.io.naming.FileNamingStrategy;
import edu.ie3.datamodel.io.source.TypeSource;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.type.*;

/**
 * Source that provides the capability to build entities of type {@link SystemParticipantTypeInput}
 * and {@link OperatorInput} from .csv files
 *
 * @version 0.1
 * @since 05.04.20
 */
public class CsvTypeSource extends TypeSource {
  public CsvTypeSource(
      String csvSep, String typeFolderPath, FileNamingStrategy fileNamingStrategy
  ) {
    super(new CsvDataSource(csvSep, typeFolderPath, fileNamingStrategy));
  }
}
