/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a system participant. */
public abstract class SystemParticipantInput extends AssetInput {

  /** Description of a reactive power characteristic. */
  private final ReactivePowerCharacteristic qCharacteristics;

  /**
   * Constructor for an operated system participant.
   *
   * @param uuid UUID of the input entity
   * @param id ID of the asset
   * @param operator operator of the asset
   * @param operationTime time for which the entity is operated
   * @param qCharacteristics description of a reactive power characteristic
   */
  protected SystemParticipantInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      ReactivePowerCharacteristic qCharacteristics) {
    super(uuid, id, operator, operationTime);
    this.qCharacteristics = qCharacteristics;
  }

  /**
   * Constructor for an operated, always-on system participant.
   *
   * @param uuid UUID of the input entity
   * @param id ID of the asset
   * @param qCharacteristics description of a reactive power characteristic
   */
  protected SystemParticipantInput(
      UUID uuid, String id, ReactivePowerCharacteristic qCharacteristics) {
    super(uuid, id);
    this.qCharacteristics = qCharacteristics;
  }

  public abstract ComparableQuantity<Power> sRated();

  public ReactivePowerCharacteristic getqCharacteristics() {
    return qCharacteristics;
  }

  @Override
  public abstract SystemParticipantInputCopyBuilder<?> copy();

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SystemParticipantInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(qCharacteristics, that.qCharacteristics);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), qCharacteristics);
  }

  @Override
  public String toString() {
    return "SystemParticipantInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", qCharacteristics='"
        + qCharacteristics
        + "'}";
  }

  /**
   * Base copy builder for subclasses of {@link SystemParticipantInput}.
   *
   * @param <B> concrete builder type
   */
  public abstract static class SystemParticipantInputCopyBuilder<
          B extends SystemParticipantInputCopyBuilder<B>>
      extends AssetInputCopyBuilder<B> {

    private ReactivePowerCharacteristic qCharacteristics;

    protected SystemParticipantInputCopyBuilder(SystemParticipantInput entity) {
      super(entity);
      this.qCharacteristics = entity.getqCharacteristics();
    }

    public B qCharacteristics(ReactivePowerCharacteristic qCharacteristics) {
      this.qCharacteristics = qCharacteristics;
      return thisInstance();
    }

    protected ReactivePowerCharacteristic getqCharacteristics() {
      return qCharacteristics;
    }

    /**
     * Scales relevant power-related properties.
     *
     * @param factor scaling factor
     * @return this builder
     */
    public abstract B scale(Double factor);

    @Override
    public abstract SystemParticipantInput build();

    @Override
    protected abstract B thisInstance();
  }
}
