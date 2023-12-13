/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Objects;
import java.util.UUID;

public class EmInput extends AssetInput {

  /** Reference to the control strategy to be used for this model */
  private final String controlStrategy;

  /**
   * Constructor for an operated energy management system
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime time for which the entity is operated
   * @param emControlStrategy the control strategy
   */
  public EmInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      String emControlStrategy) {
    super(uuid, id, operator, operationTime);
    this.controlStrategy = emControlStrategy;
  }

  /**
   * Constructor for an operated energy management system
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param emControlStrategy the control strategy
   */
  public EmInput(UUID uuid, String id, String emControlStrategy) {
    super(uuid, id);
    this.controlStrategy = emControlStrategy;
  }

  public String getControlStrategy() {
    return controlStrategy;
  }

  @Override
  public EmInputCopyBuilder copy() {
    return new EmInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EmInput emInput)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(controlStrategy, emInput.controlStrategy);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), controlStrategy);
  }

  @Override
  public String toString() {
    return "EmInput{"
        + "uuid="
        + getUuid()
        + ", id='"
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", controlStrategy="
        + getControlStrategy()
        + '}';
  }

  public static class EmInputCopyBuilder extends AssetInputCopyBuilder<EmInputCopyBuilder> {

    private String controlStrategy;

    protected EmInputCopyBuilder(EmInput entity) {
      super(entity);
      this.controlStrategy = entity.getControlStrategy();
    }

    public EmInputCopyBuilder controlStrategy(String controlStrategy) {
      this.controlStrategy = controlStrategy;
      return this;
    }

    @Override
    public EmInput build() {
      return new EmInput(getUuid(), getId(), getOperator(), getOperationTime(), controlStrategy);
    }

    @Override
    protected EmInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
