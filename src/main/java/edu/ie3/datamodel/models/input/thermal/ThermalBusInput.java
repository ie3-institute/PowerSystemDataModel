/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.UUID;

/** A thermal bus, to which different {@link ThermalUnitInput} units may be connected */
public class ThermalBusInput extends ThermalInput {
  /**
   * Constructor for an operated thermal bus
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
   * Constructor for an operated, always on thermal bus
   *
   * @param uuid Unique identifier of a certain thermal bus
   * @param id of the asset
   */
  public ThermalBusInput(UUID uuid, String id) {
    super(uuid, id);
  }

  @Override
  public ThermalBusInputCopyBuilder copy() {
    return new ThermalBusInputCopyBuilder(this);
  }

  /**
   * A builder pattern based approach to create copies of {@link ThermalBusInput} entities with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * ThermalBusInput}
   */
  public static class ThermalBusInputCopyBuilder
      extends AssetInput.AssetInputCopyBuilder<ThermalBusInputCopyBuilder> {

    private ThermalBusInputCopyBuilder(ThermalBusInput entity) {
      super(entity);
    }

    @Override
    public ThermalBusInput build() {
      return new ThermalBusInput(getUuid(), getId(), getOperator(), getOperationTime());
    }

    @Override
    protected ThermalBusInputCopyBuilder childInstance() {
      return this;
    }
  }
}
