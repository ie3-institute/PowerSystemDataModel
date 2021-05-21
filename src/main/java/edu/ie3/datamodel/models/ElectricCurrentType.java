/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import edu.ie3.util.StringUtils;
import java.util.Optional;

/**
 * Generic electric current type implementation. Main purpose is to indicate the current type that
 * is provided by a specific asset e.g. {@link edu.ie3.datamodel.models.input.system.EvcsInput}
 *
 * @version 0.1
 * @since 25.07.20
 */
public enum ElectricCurrentType {
  AC,
  DC;

  public static Optional<ElectricCurrentType> parse(String electricCurrentId) {
    String cleanedElectricCurrentId =
        StringUtils.cleanString(electricCurrentId).replace("_", "").trim().toUpperCase();
    Optional<ElectricCurrentType> res;
    try {
      res = Optional.of(ElectricCurrentType.valueOf(cleanedElectricCurrentId));
    } catch (IllegalArgumentException | NullPointerException e) {
      res = Optional.empty();
    }
    return res;
  }
}
