/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.exceptions.ValidationException;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;

public class GraphicValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private GraphicValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a graphic input if: <br>
   * - it is not null <br>
   * - its graphic layer is not null <br>
   *
   * <p>A "distribution" method, that forwards the check request to specific implementations to
   * fulfill the checking task, based on the class of the given object. If an unknown class is
   * handed in, a {@link ValidationException} is thrown.
   *
   * @param graphicInput GraphicInput to validate
   */
  public static void check(GraphicInput graphicInput) {
    checkNonNull(graphicInput, "a graphic input");
    if (graphicInput.getGraphicLayer() == null)
      throw new InvalidEntityException(
          "Graphic Layer of graphic element is not defined", graphicInput);

    // Further checks for subclasses
    if (LineGraphicInput.class.isAssignableFrom(graphicInput.getClass()))
      checkLineGraphicInput((LineGraphicInput) graphicInput);
    if (NodeGraphicInput.class.isAssignableFrom(graphicInput.getClass()))
      checkNodeGraphicInput((NodeGraphicInput) graphicInput);
  }

  /**
   * Validates a line graphic input if: <br>
   * - its path is not null
   *
   * @param lineGraphicInput LineGraphicInput to validate
   */
  protected static void checkLineGraphicInput(LineGraphicInput lineGraphicInput) {
    if (lineGraphicInput.getPath() == null)
      throw new InvalidEntityException(
          "Path of line graphic element is not defined", lineGraphicInput);
  }

  /**
   * Validates a node graphic input if: <br>
   * - its node is not null <br>
   * - its point is not null
   *
   * @param nodeGraphicInput NodeGraphicInput to validate
   */
  protected static void checkNodeGraphicInput(NodeGraphicInput nodeGraphicInput) {
    if (nodeGraphicInput.getPoint() == null)
      throw new InvalidEntityException("Point of node graphic is not defined", nodeGraphicInput);
  }
}
