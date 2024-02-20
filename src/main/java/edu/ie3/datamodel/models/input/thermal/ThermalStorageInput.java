/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.UUID;

/** Common properties to all thermal storage devices */
public abstract class ThermalStorageInput extends ThermalUnitInput {
  /**
   * @param uuid Unique identifier of a certain thermal storage input model
   * @param id Identifier of the thermal unit
   * @param bus Thermal bus, a thermal unit is connected to
   */
  ThermalStorageInput(UUID uuid, String id, ThermalBusInput bus) {
    super(uuid, id, bus);
  }

  /**
   * @param uuid Unique identifier of a certain thermal storage input model
   * @param id Identifier of the thermal unit
   * @param operator operator of the asset
   * @param operationTime operation time of the asset
   * @param bus Thermal bus, a thermal unit is connected to
   */
  ThermalStorageInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      ThermalBusInput bus) {
    super(uuid, id, operator, operationTime, bus);
  }

  @Override
  public abstract ThermalStorageInputCopyBuilder<?> copy();

  /**
   * Abstract class for all builders that build child entities of abstract class {@link
   * ThermalStorageInput}
   */
  public abstract static class ThermalStorageInputCopyBuilder<
          B extends ThermalStorageInputCopyBuilder<B>>
      extends ThermalUnitInputCopyBuilder<B> {

    protected ThermalStorageInputCopyBuilder(ThermalStorageInput entity) {
      super(entity);
    }

    @Override
    public abstract ThermalStorageInput build();
  }
}
