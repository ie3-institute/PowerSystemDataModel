/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result;

import edu.ie3.datamodel.exceptions.ParsingException;

public enum InputModelType {
  NODE("node"),
  LINE("line"),
  TRANSFORMER_2W("transformer_2w"),
  TRANSFORMER_3W("transformer_3w");

  public final String type;

  InputModelType(String type) {
    this.type = type;
  }

  public static InputModelType parse(String inputModelType) throws ParsingException {
    return switch (inputModelType) {
      case "node" -> NODE;
      case "line" -> LINE;
      case "transformer_2w" -> TRANSFORMER_2W;
      case "transformer_3w" -> TRANSFORMER_3W;
      default ->
          throw new ParsingException("InputModelType '" + inputModelType + "' cannot be parsed!");
    };
  }
}
