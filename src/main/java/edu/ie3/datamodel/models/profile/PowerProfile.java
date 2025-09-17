/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.profile;

import java.io.Serializable;

/** Interface defining a power profile. */
public interface PowerProfile extends Serializable {

  /**
   * @return The identifying String
   */
  String getKey();
}
