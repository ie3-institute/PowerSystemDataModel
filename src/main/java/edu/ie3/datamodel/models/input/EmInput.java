/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import edu.ie3.datamodel.io.extractor.HasEm;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.UniqueEntity;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class EmInput extends AssetInput implements HasEm {

  /** Reference to the control strategy to be used for this model */
  private final String controlStrategy;

  /**
   * Optional UUID of the parent {@link EmInput} that is controlling this em unit. If null, this em
   * unit is not em-controlled.
   */
  private final EmInput parentEm;

  /**
   * Constructor for an operated energy management system
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime time for which the entity is operated
   * @param emControlStrategy the control strategy
   * @param parentEm The {@link EmInput} controlling this em unit. Null, if not applicable.
   */
  public EmInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      String emControlStrategy,
      EmInput parentEm) {
    super(uuid, id, operator, operationTime);
    this.controlStrategy = emControlStrategy;
    this.parentEm = parentEm;
  }

  /**
   * Constructor for an operated energy management system
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param emControlStrategy the control strategy
   * @param parentEm The {@link EmInput} controlling this em unit. Null, if not applicable.
   */
  public EmInput(UUID uuid, String id, String emControlStrategy, EmInput parentEm) {
    super(uuid, id);
    this.controlStrategy = emControlStrategy;
    this.parentEm = parentEm;
  }

  public String getControlStrategy() {
    return controlStrategy;
  }

  public Optional<EmInput> getParentEm() {
    return Optional.ofNullable(parentEm);
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
    return Objects.equals(controlStrategy, emInput.controlStrategy)
        && Objects.equals(parentEm, emInput.parentEm);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), controlStrategy, parentEm);
  }

  @Override
  public String toString() {
    return "EmInput{"
        + "uuid="
        + getUuid()
        + ", id='"
        + getId()
        + "', operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", controlStrategy="
        + getControlStrategy()
        + ", parentEm="
        + getParentEm().map(UniqueEntity::getUuid).map(UUID::toString).orElse("")
        + "}";
  }

  @Override
  public Optional<EmInput> getControllingEm() {
    return Optional.ofNullable(parentEm);
  }

  public static class EmInputCopyBuilder extends AssetInputCopyBuilder<EmInputCopyBuilder> {

    private String controlStrategy;

    private EmInput parentEm;

    protected EmInputCopyBuilder(EmInput entity) {
      super(entity);
      this.controlStrategy = entity.getControlStrategy();
      this.parentEm = entity.parentEm;
    }

    public EmInputCopyBuilder controlStrategy(String controlStrategy) {
      this.controlStrategy = controlStrategy;
      return this;
    }

    public EmInputCopyBuilder parentEm(EmInput parentEm) {
      this.parentEm = parentEm;
      return thisInstance();
    }

    @Override
    public EmInput build() {
      return new EmInput(
          getUuid(), getId(), getOperator(), getOperationTime(), controlStrategy, parentEm);
    }

    @Override
    protected EmInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
