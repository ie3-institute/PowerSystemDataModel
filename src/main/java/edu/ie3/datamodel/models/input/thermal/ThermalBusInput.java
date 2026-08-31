/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/** A thermal bus, to which different {@link ThermalUnitInput} units may be connected. */
public class ThermalBusInput extends ThermalInput {
  /**
   * Constructor for an operated thermal bus.
   *
   * @param uuid Unique identifier of a certain thermal bus
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   */
  public ThermalBusInput(
      UUID uuid, String id, OperatorInput operator, OperationTime operationTime) {
    super(uuid, id, operator, operationTime);
  }

  /**
   * Constructor for an operated thermal bus.
   *
   * @param uuid Unique identifier of a certain thermal bus
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param additionalInformation Of the input
   */
  public ThermalBusInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime);
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated, always on thermal bus.
   *
   * @param uuid Unique identifier of a certain thermal bus
   * @param id of the asset
   */
  public ThermalBusInput(UUID uuid, String id) {
    super(uuid, id);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ThermalBusInput that)) return false;
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode());
  }

  @Override
  public String toString() {
    return "ThermalBusInput{"
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
  public ThermalBusInputCopyBuilder copy() {
    return new ThermalBusInputCopyBuilder(this);
  }

  public static class ThermalBusInputCopyBuilder
      extends ThermalInputCopyBuilder<ThermalBusInputCopyBuilder> {
    protected ThermalBusInputCopyBuilder(ThermalBusInput entity) {
      super(entity);
    }

    @Override
    public ThermalBusInput build() {
      return new ThermalBusInput(
          getUuid(), getId(), getOperator(), getOperationTime(), getAdditionalInformation());
    }

    @Override
    protected ThermalBusInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
