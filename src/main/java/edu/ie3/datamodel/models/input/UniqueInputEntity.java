/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import edu.ie3.datamodel.models.UniqueEntity;
import java.util.UUID;

/** Functionless class to describe that all subclasses are unique input classes */
public abstract class UniqueInputEntity extends UniqueEntity implements InputEntity {

  protected UniqueInputEntity(UUID uuid) {
    super(uuid);
  }
}
