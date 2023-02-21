/*
 * © 2023. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.exceptions;

import java.util.List;

public class GraphicSourceException extends RuntimeException {
  public GraphicSourceException(String message, List<SourceException> exceptions) {
    super(message + " " + addMessages(exceptions), exceptions.get(0));
  }

  private static String addMessages(List<SourceException> exceptions) {
    SourceException firstInList = exceptions.remove(0);
    return exceptions.stream()
        .map(Throwable::getMessage)
        .reduce(firstInList.getMessage(), (a, b) -> a + ", " + b);
  }
}
