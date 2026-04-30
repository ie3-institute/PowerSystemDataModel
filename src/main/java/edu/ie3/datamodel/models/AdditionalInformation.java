/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import java.util.Map;

/** Interface that describes that additional information may be present. */
public interface AdditionalInformation {

  /**
   * Returns a map: string to string containing all additional information that were provided by the
   * source.
   */
  Map<String, String> getAdditionalInformation();
}
