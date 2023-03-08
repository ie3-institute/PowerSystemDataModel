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
import edu.ie3.datamodel.utils.ExceptionUtils;
import edu.ie3.datamodel.utils.options.Failure;
import edu.ie3.datamodel.utils.options.Success;
import edu.ie3.datamodel.utils.options.Try;
import java.util.List;
import java.util.stream.Stream;

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
   * fulfill the checking task, based on the class of the given object.
   *
   * @param graphicInput GraphicInput to validate
   * @return a try object either containing an {@link InvalidEntityException} or an empty Success
   */
  protected static Try<Void, InvalidEntityException> check(GraphicInput graphicInput) {
    try {
      checkNonNull(graphicInput, "a graphic input");
    } catch (InvalidEntityException e) {
      return new Failure<>(
          new InvalidEntityException(
              "Validation not possible because received object {" + graphicInput + "} was null",
              e));
    }

    Try<Void, InvalidEntityException> layer;

    if (graphicInput.getGraphicLayer() == null) {
      layer =
          new Failure<>(
              new InvalidEntityException(
                  "Graphic Layer of graphic element is not defined", graphicInput));
    } else {
      layer = Success.empty();
    }

    Try<Void, InvalidEntityException> graphic;

    // Further checks for subclasses
    if (LineGraphicInput.class.isAssignableFrom(graphicInput.getClass())) {
      graphic = Try.apply(() -> checkLineGraphicInput((LineGraphicInput) graphicInput));
    } else if (NodeGraphicInput.class.isAssignableFrom(graphicInput.getClass())) {
      graphic = Try.apply(() -> checkNodeGraphicInput((NodeGraphicInput) graphicInput));
    } else {
      graphic = Success.empty();
    }

    List<InvalidEntityException> exceptions =
        Stream.of(layer, graphic).filter(Try::isFailure).map(Try::getException).toList();

    if (exceptions.size() > 0) {
      return new Failure<>(
          new InvalidEntityException(
              "Validation failed due to the following exception(s): ",
              new Throwable(ExceptionUtils.getMessages(exceptions))));
    } else {
      return Success.empty();
    }
  }

  /**
   * Validates a line graphic input if: <br>
   * - its path is not null
   *
   * @param lineGraphicInput LineGraphicInput to validate
   */
  private static void checkLineGraphicInput(LineGraphicInput lineGraphicInput) {
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
  private static void checkNodeGraphicInput(NodeGraphicInput nodeGraphicInput) {
    if (nodeGraphicInput.getPoint() == null)
      throw new InvalidEntityException("Point of node graphic is not defined", nodeGraphicInput);
  }
}
