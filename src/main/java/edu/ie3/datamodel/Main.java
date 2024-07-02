/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel;

import edu.ie3.datamodel.exceptions.EntityProcessorException;
import edu.ie3.datamodel.exceptions.ProcessorProviderException;
import edu.ie3.datamodel.io.SqlUtils;
import edu.ie3.datamodel.models.input.system.type.StorageTypeInput;

public class Main {
  public static void main(String[] args) {
    try {
      System.out.println(SqlUtils.queryCreateTableUniqueEntity(StorageTypeInput.class, "public"));
    } catch (EntityProcessorException e) {
      throw new RuntimeException(e);
    } catch (ProcessorProviderException e) {
      throw new RuntimeException(e);
    }
  }
}
