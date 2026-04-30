/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models;

import java.util.UUID;

/** Interface that indicates that the given object contains an uuid and is unique. */
public interface Uniqueness {

  UUID getUuid();
}
