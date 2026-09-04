/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.WecTypeInput;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a Wind Energy Converter. */
public class WecInput extends SystemParticipantInput implements HasType {
  /** Type of this WEC, containing default values for WEC assets of this kind. */
  private final WecTypeInput type;

  /**
   * Constructor for an operated wind energy converter.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param type of this WEC
   */
  public WecInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      WecTypeInput type) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.type = type;
  }

  /**
   * Constructor for an operated wind energy converter.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param type of this WEC
   * @param additionalInformation That were provided by the source
   */
  public WecInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      WecTypeInput type,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.type = type;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated wind energy converter.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param type of this WEC
   */
  public WecInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      WecTypeInput type) {
    super(uuid, id, node, qCharacteristics, controllingEm);
    this.type = type;
  }

  public WecTypeInput getType() {
    return type;
  }

  @Override
  public ComparableQuantity<Power> sRated() {
    return this.type.getsRated();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof WecInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type);
  }

  @Override
  public String toString() {
    return "WecInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", node="
        + getNode().getUuid()
        + ", qCharacteristics="
        + getqCharacteristics()
        + ", controllingEm="
        + getControllingEm().map(e -> e.getUuid().toString()).orElse("")
        + ", type="
        + type.getUuid()
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public WecInputCopyBuilder copy() {
    return new WecInputCopyBuilder(this);
  }

  public static class WecInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<WecInputCopyBuilder> {
    private WecTypeInput type;

    protected WecInputCopyBuilder(WecInput entity) {
      super(entity);
      this.type = entity.type;
    }

    public WecInputCopyBuilder type(WecTypeInput type) {
      this.type = type;
      return thisInstance();
    }

    protected WecTypeInput getType() {
      return type;
    }

    @Override
    public WecInputCopyBuilder scale(double factor) {
      type(type.copy().scale(factor).build());
      return thisInstance();
    }

    @Override
    public WecInput build() {
      return new WecInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNode(),
          getqCharacteristics(),
          getControllingEm(),
          type,
          getAdditionalInformation());
    }

    @Override
    protected WecInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
