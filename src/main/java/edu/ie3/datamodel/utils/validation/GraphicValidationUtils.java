/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.models.input.graphics.GraphicInput;

public class GraphicValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private GraphicValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }


  public static void check(GraphicInput graphicInput) {
    checkNonNull(graphicInput, "a measurement unit");

  }

}
