/*
 * © 2021. TU Dortmund University,
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

  /**
   * Constructor for an asset with timely limited operation and specific operator
   *
   * @param uuid Unique identifier
   * @param id Human readable identifier
   * @param operator Operator of the asset
   * @param operationTime Operation time limitation
   */
  protected AssetInput(UUID uuid, String id, OperatorInput operator, OperationTime operationTime) {
    super(uuid);
    this.operationTime = operationTime;
    this.operator = operator;
    this.id = id;
  }

  /**
   * Constructor for an asset with timely unlimited operation and unassigned operator
   *
   * @param uuid Unique identifier
   * @param id Human readable identifier
   */
  protected AssetInput(UUID uuid, String id) {
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

  public abstract UniqueEntityBuilder copy();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AssetInput that)) return false;
    if (!super.equals(o)) return false;
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
        + "uuid="
        + getUuid()
        + ", id="
        + id
        + ", operator="
        + operator.getUuid()
        + ", operationTime="
        + operationTime
        + '}';
  }

  /**
   * Abstract class for all builder that build child entities of abstract class {@link AssetInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  protected abstract static class AssetInputCopyBuilder<T extends AssetInputCopyBuilder<T>>
      extends UniqueEntityCopyBuilder<T> {

    private String id;
    private OperatorInput operator;
    private OperationTime operationTime;

    protected AssetInputCopyBuilder(AssetInput entity) {
      super(entity);
      this.id = entity.getId();
      this.operator = entity.getOperator();
      this.operationTime = entity.getOperationTime();
    }

    public T id(String id) {
      this.id = id;
      return childInstance();
    }

    public T operator(OperatorInput operator) {
      this.operator = operator;
      return childInstance();
    }

    public T operationTime(OperationTime operationTime) {
      this.operationTime = operationTime;
      return childInstance();
    }

    protected String getId() {
      return id;
    }

    protected OperatorInput getOperator() {
      return operator;
    }

    protected OperationTime getOperationTime() {
      return operationTime;
    }

    @Override
    public abstract AssetInput build();

    @Override
    protected abstract T childInstance();
  }
}
