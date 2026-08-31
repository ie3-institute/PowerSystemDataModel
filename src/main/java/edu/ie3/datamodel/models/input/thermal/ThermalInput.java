/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Objects;
import java.util.UUID;

/** Abstract class as a common super class of all thermal input models. */
public abstract class ThermalInput extends AssetInput {
  /**
   * Constructor for a thermal input model.
   *
   * @param uuid Unique identifier
   * @param id Human readable identifier
   * @param operator Reference to the operator
   * @param operationTime Time frame, within the asset is in operation
   */
  protected ThermalInput(
      UUID uuid, String id, OperatorInput operator, OperationTime operationTime) {
    super(uuid, id, operator, operationTime);
  }

  /**
   * Constructor for a thermal input model.
   *
   * @param uuid Unique identifier
   * @param id Human readable identifier
   */
  protected ThermalInput(UUID uuid, String id) {
    super(uuid, id);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ThermalInput that)) return false;
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String toString() {
    return "ThermalInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public abstract ThermalInputCopyBuilder<?> copy();

  public abstract static class ThermalInputCopyBuilder<B extends ThermalInputCopyBuilder<B>>
      extends AssetInputCopyBuilder<B> {
    protected ThermalInputCopyBuilder(ThermalInput entity) {
      super(entity);
    }

    @Override
    public abstract ThermalInput build();

    @Override
    protected abstract B thisInstance();
  }
}
