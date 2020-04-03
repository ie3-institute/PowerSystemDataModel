/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import edu.ie3.datamodel.models.Operable;
import edu.ie3.datamodel.models.OperationTime;
import java.util.Objects;
import java.util.UUID;

/** Describes a grid asset under the assumption that every asset could be operable */
public abstract class AssetInput extends InputEntity implements Operable {
  /** Time for which the entity is operated */
  private final OperationTime operationTime;
  /** The operator of this asset */
  private final OperatorInput operator;
  /** Name or ID of the asset */
  private final String id;

  /** Constructor for an operated asset */
  public AssetInput(UUID uuid, String id, OperatorInput operator, OperationTime operationTime) {
    super(uuid);
    this.operationTime = operationTime;
    this.operator = operator;
    this.id = id;
  }

  /** Constructor for an operated, always on asset */
  public AssetInput(UUID uuid, String id) {
    this(uuid, id, OperatorInput.NO_OPERATOR_ASSIGNED, OperationTime.notLimited());
  }

  @Override
  public OperationTime getOperationTime() {
    return operationTime;
  }

  @Override
  public OperatorInput getOperator() {
    return operator;
  }

  public String getId() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    AssetInput that = (AssetInput) o;
    return Objects.equals(operationTime, that.operationTime)
        && Objects.equals(operator, that.operator)
        && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), operationTime, operator, id);
  }

  @Override
  public String toString() {
    return "AssetInput{"
        + "operationTime="
        + operationTime
        + ", operator="
        + operator
        + ", id='"
        + id
        + '\''
        + '}';
  }
}
