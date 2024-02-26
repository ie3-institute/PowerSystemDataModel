/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.utils.validation;

import edu.ie3.datamodel.exceptions.InvalidEntityException;
import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.utils.Try;
import java.util.ArrayList;
import java.util.List;

public class GraphicValidationUtils extends ValidationUtils {

  /** Private Constructor as this class is not meant to be instantiated */
  private GraphicValidationUtils() {
    throw new IllegalStateException("Don't try and instantiate a Utility class.");
  }

  /**
   * Validates a graphic input if:
   *
   * <ul>
   *   <li>it is not null
   *   <li>its graphic layer is not null
   * </ul>
   *
   * <p>A "distribution" method, that forwards the check request to specific implementations to
   * fulfill the checking task, based on the class of the given object.
   *
   * @param graphicInput GraphicInput to validate
   * @return a list of try objects either containing an {@link InvalidEntityException} or an empty
   *     Success
   */
  protected static List<Try<Void, InvalidEntityException>> check(GraphicInput graphicInput) {
    Try<Void, InvalidEntityException> isNull = checkNonNull(graphicInput, "a graphic input");

    if (isNull.isFailure()) {
      return List.of(isNull);
    }

    List<Try<Void, InvalidEntityException>> exceptions = new ArrayList<>();

    exceptions.add(
        Try.ofVoid(
            graphicInput.getGraphicLayer() == null,
            () ->
                new InvalidEntityException(
                    "Graphic Layer of graphic element is not defined", graphicInput)));

    // Further checks for subclasses
    if (LineGraphicInput.class.isAssignableFrom(graphicInput.getClass())) {
      exceptions.add(checkLineGraphicInput((LineGraphicInput) graphicInput));
    } else if (NodeGraphicInput.class.isAssignableFrom(graphicInput.getClass())) {
      exceptions.add(checkNodeGraphicInput((NodeGraphicInput) graphicInput));
    }

    return exceptions;
  }

  /**
   * Validates a line graphic input if:
   *
   * <ul>
   *   <li>its path is not null
   * </ul>
   *
   * @param lineGraphicInput LineGraphicInput to validate
   */
  private static Try<Void, InvalidEntityException> checkLineGraphicInput(
      LineGraphicInput lineGraphicInput) {
    return Try.ofVoid(
        lineGraphicInput.getPath() == null,
        () ->
            new InvalidEntityException(
                "Path of line graphic element is not defined", lineGraphicInput));
  }

  /**
   * Validates a node graphic input if:
   *
   * <ul>
   *   <li>its node is not null
   *   <li>its point is not null
   * </ul>
   *
   * @param nodeGraphicInput NodeGraphicInput to validate
   */
  private static Try<Void, InvalidEntityException> checkNodeGraphicInput(
      NodeGraphicInput nodeGraphicInput) {
    return Try.ofVoid(
        nodeGraphicInput.getPoint() == null,
        () -> new InvalidEntityException("Point of node graphic is not defined", nodeGraphicInput));
  }
}
