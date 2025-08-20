/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.thermal;

import edu.ie3.datamodel.io.extractor.HasThermalBus;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Objects;
import java.util.UUID;

/** Abstract class for grouping all common properties to thermal models. */
public abstract class ThermalUnitInput extends ThermalInput implements HasThermalBus {
  /** The thermal bus, a thermal unit is connected to. */
  private final ThermalBusInput thermalBus;

  /**
   * Instantiates a new Thermal unit input.
   *
   * @param uuid Unique identifier of a certain thermal input
   * @param id Identifier of the thermal unit
   * @param thermalBus thermal bus, a thermal unit is connected to
   */
  ThermalUnitInput(UUID uuid, String id, ThermalBusInput thermalBus) {
    super(uuid, id);
    this.thermalBus = thermalBus;
  }

  /**
   * Instantiates a new Thermal unit input.
   *
   * @param uuid Unique identifier of a certain thermal input
   * @param id Identifier of the thermal unit
   * @param operator operator of the asset
   * @param operationTime operation time of the asset
   * @param thermalBus thermal bus, a thermal unit is connected to
   */
  ThermalUnitInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      ThermalBusInput thermalBus) {
    super(uuid, id, operator, operationTime);
    this.thermalBus = thermalBus;
  }

  @Override
  public ThermalBusInput getThermalBus() {
    return thermalBus;
  }

  @Override
  public abstract ThermalUnitInputCopyBuilder<?> copy();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ThermalUnitInput that)) return false;
    if (!super.equals(o)) return false;
    return thermalBus.equals(that.thermalBus);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), thermalBus);
  }

  @Override
  public String toString() {
    return "ThermalUnitInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", bus="
        + thermalBus.getUuid()
        + '}';
  }

  /**
   * Abstract class for all builders that build child entities of abstract class {@link
   * ThermalUnitInput}*
   *
   * @param <B> Type parameter representing the builder type extending from
   *     ThermalUnitInputCopyBuilder.
   */
  public abstract static class ThermalUnitInputCopyBuilder<B extends ThermalUnitInputCopyBuilder<B>>
      extends AssetInputCopyBuilder<B> {

    private ThermalBusInput thermalBus;

    /**
     * Instantiates a new Thermal unit input copy builder.
     *
     * @param entity the entity
     */
    protected ThermalUnitInputCopyBuilder(ThermalUnitInput entity) {
      super(entity);
      this.thermalBus = entity.getThermalBus();
    }

    /**
     * Thermal bus b.
     *
     * @param thermalBus the thermal bus
     * @return the b
     */
    public B thermalBus(ThermalBusInput thermalBus) {
      this.thermalBus = thermalBus;
      return thisInstance();
    }

    /**
     * Gets thermal bus.
     *
     * @return the thermal bus
     */
    protected ThermalBusInput getThermalBus() {
      return thermalBus;
    }

    /**
     * Scales properties by given factor.
     *
     * @param factor Scaling factor
     * @return A copy builder with scaled relevant properties
     */
    public abstract B scale(Double factor);

    @Override
    public abstract ThermalUnitInput build();

    @Override
    protected abstract B thisInstance();
  }
}
