/*
 * © 2021. TU Dortmund University,
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
import java.util.Objects;
import java.util.UUID;

/** Describes a Wind Energy Converter */
public class WecInput extends SystemParticipantInput implements HasType {

  /** Type of this WEC, containing default values for WEC assets of this kind */
  private final WecTypeInput type;
  /** Is this asset market oriented? */
  private final boolean marketReaction;

  /**
   * Constructor for an operated wind energy converter
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type of this WEC
   * @param marketReaction Is this asset market oriented?
   */
  public WecInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      WecTypeInput type,
      boolean marketReaction) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, em);
    this.type = type;
    this.marketReaction = marketReaction;
  }

  /**
   * Constructor for an operated, always on wind energy converter
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type of this WEC
   * @param marketReaction Is this asset market oriented?
   */
  public WecInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      WecTypeInput type,
      boolean marketReaction) {
    super(uuid, id, node, qCharacteristics, em);
    this.type = type;
    this.marketReaction = marketReaction;
  }

  public boolean isMarketReaction() {
    return marketReaction;
  }

  @Override
  public WecTypeInput getType() {
    return type;
  }

  public WecInputCopyBuilder copy() {
    return new WecInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof WecInput wecInput)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(type, wecInput.type)
        && Objects.equals(marketReaction, wecInput.marketReaction);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, marketReaction);
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
        + ", qCharacteristics='"
        + getqCharacteristics()
        + "', em="
        + getControllingEm()
        + ", type="
        + type.getUuid()
        + ", marketReaction="
        + marketReaction
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link WecInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link WecInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class WecInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<WecInputCopyBuilder> {

    private boolean marketReaction;
    private WecTypeInput type;

    private WecInputCopyBuilder(WecInput entity) {
      super(entity);
      this.type = entity.getType();
      this.marketReaction = entity.isMarketReaction();
    }

    @Override
    public WecInputCopyBuilder scale(Double factor) {
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
          getEm(),
          type,
          marketReaction);
    }

    public WecInputCopyBuilder type(WecTypeInput type) {
      this.type = type;
      return thisInstance();
    }

    public WecInputCopyBuilder marketReaction(boolean marketReaction) {
      this.marketReaction = marketReaction;
      return thisInstance();
    }

    @Override
    protected WecInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
