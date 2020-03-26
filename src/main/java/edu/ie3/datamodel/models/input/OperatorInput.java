/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import java.util.Objects;
import java.util.UUID;

/** Describes an operator, that operates assets */
public class OperatorInput extends InputEntity {

  public static final OperatorInput NO_OPERATOR_ASSIGNED =
      new OperatorInput(UUID.randomUUID(), "NO_OPERATOR_ASSIGNED");

  /** The name of this operator */
  private final String id;

  /** @param id of this operator */
  public OperatorInput(UUID uuid, String id) {
    super(uuid);
    this.id = id;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    OperatorInput that = (OperatorInput) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id);
  }

  @Override
  public String toString() {
    return "OperatorInput{" + "id='" + id + '\'' + '}';
  }
}
