/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import edu.ie3.datamodel.models.Operable;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.util.TimeUtil;
import java.util.Objects;
import java.util.SequencedMap;
import java.util.UUID;

/** Describes a grid asset under the assumption that every asset could be operable. */
public abstract class AssetInput extends UniqueInputEntity implements Operable {
  /** Name or ID of the asset. */
  private final String id;

  /** The operator of this asset. */
  private final OperatorInput operator;

  /** Time for which the entity is operated. */
  private final OperationTime operationTime;

  /**
   * @param uuid Unique identifier for an entity.
   * @param id Name or ID of the asset.
   */
  protected AssetInput(UUID uuid, String id) {
    super(uuid);
    this.id = id;
    this.operator = OperatorInput.NO_OPERATOR_ASSIGNED;
    this.operationTime = OperationTime.notLimited();
  }

  /**
   * @param uuid Unique identifier for an entity.
   * @param id Name or ID of the asset.
   * @param operator The operator of this asset.
   * @param operationTime Time for which the entity is operated.
   */
  protected AssetInput(UUID uuid, String id, OperatorInput operator, OperationTime operationTime) {
    super(uuid);
    this.id = id;
    this.operator = operator;
    this.operationTime = operationTime;
  }

  public String getId() {
    return id;
  }

  public OperatorInput getOperator() {
    return operator;
  }

  public OperationTime getOperationTime() {
    return operationTime;
  }

  @Override
  public SequencedMap<String, String> toMap() {
    SequencedMap<String, String> map = super.toMap();
    map.put("id", id);
    map.put("operator", operator.getUuid().toString());
    map.put(
        "operatesFrom",
        operationTime.getStartDate().map(TimeUtil.withDefaults::toString).orElse(""));
    map.put(
        "operatesUntil",
        operationTime.getEndDate().map(TimeUtil.withDefaults::toString).orElse(""));
    return map;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof AssetInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(id, that.id)
        && Objects.equals(operator, that.operator)
        && Objects.equals(operationTime, that.operationTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), id, operator, operationTime);
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
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public abstract AssetInputCopyBuilder<?> copy();

  public abstract static class AssetInputCopyBuilder<B extends AssetInputCopyBuilder<B>>
      extends UniqueInputEntityCopyBuilder<B> {
    private String id;

    private OperatorInput operator;

    private OperationTime operationTime;

    protected AssetInputCopyBuilder(AssetInput entity) {
      super(entity);
      this.id = entity.id;
      this.operator = entity.operator;
      this.operationTime = entity.operationTime;
    }

    public B id(String id) {
      this.id = id;
      return thisInstance();
    }

    protected String getId() {
      return id;
    }

    public B operator(OperatorInput operator) {
      this.operator = operator;
      return thisInstance();
    }

    protected OperatorInput getOperator() {
      return operator;
    }

    public B operationTime(OperationTime operationTime) {
      this.operationTime = operationTime;
      return thisInstance();
    }

    protected OperationTime getOperationTime() {
      return operationTime;
    }

    @Override
    public abstract AssetInput build();

    @Override
    protected abstract B thisInstance();
  }
}
