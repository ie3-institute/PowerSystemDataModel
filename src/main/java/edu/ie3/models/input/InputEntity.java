/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input;

import edu.ie3.models.UniqueEntity;
import java.util.UUID;

/** Functionless class to describe that all subclasses are input classes */
public abstract class InputEntity extends UniqueEntity {

  public InputEntity(UUID uuid) {
    super(uuid);
  }

  @Override
  public String toString() {
    return "InputEntity{} " + super.toString();
  }



}
