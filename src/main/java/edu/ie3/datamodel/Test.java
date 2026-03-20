/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel;

import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.io.source.TypeSource;

public class Test {

  public static void main(String[] args) throws SourceException {

    TypeSource.getStandardLineTypes();
    TypeSource.getStandardTransformer2WTypes();
    TypeSource.getStandardTransformer3WTypes();
  }
}
