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
public abstract class AssetInput extends UniqueInputEntity implements Operable {
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

  /**
   * Gets id.
   *
   * @return the id
   */
  public String getId() {
    return id;
  }

  /**
   * Copy asset input copy builder.
   *
   * @return the asset input copy builder
   */
  public abstract AssetInputCopyBuilder<?> copy();

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
   * @param <B> the type parameter
   * @version 0.1
   * @since 05.06.20
   */
  public abstract static class AssetInputCopyBuilder<B extends AssetInputCopyBuilder<B>>
      extends UniqueEntityCopyBuilder<B> {

    private String id;
    private OperatorInput operator;
    private OperationTime operationTime;

    /**
     * Instantiates a new Asset input copy builder.
     *
     * @param entity the entity
     */
    protected AssetInputCopyBuilder(AssetInput entity) {
      super(entity);
      this.id = entity.getId();
      this.operator = entity.getOperator();
      this.operationTime = entity.getOperationTime();
    }

    /**
     * Id b.
     *
     * @param id the id
     * @return the b
     */
    public B id(String id) {
      this.id = id;
      return thisInstance();
    }

    /**
     * Operator b.
     *
     * @param operator the operator
     * @return the b
     */
    public B operator(OperatorInput operator) {
      this.operator = operator;
      return thisInstance();
    }

    /**
     * Operation time b.
     *
     * @param operationTime the operation time
     * @return the b
     */
    public B operationTime(OperationTime operationTime) {
      this.operationTime = operationTime;
      return thisInstance();
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    protected String getId() {
      return id;
    }

    /**
     * Gets operator.
     *
     * @return the operator
     */
    protected OperatorInput getOperator() {
      return operator;
    }

    /**
     * Gets operation time.
     *
     * @return the operation time
     */
    protected OperationTime getOperationTime() {
      return operationTime;
    }

    @Override
    public abstract AssetInput build();

    @Override
    protected abstract B thisInstance();
  }
}
