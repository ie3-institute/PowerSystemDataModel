/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.system;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.system.type.WecTypeInput;
import java.util.Objects;
import java.util.UUID;

/** Describes a Wind Energy Converter */
public class WecInput extends SystemParticipantInput {

  /** Type of this WEC, containing default values for WEC assets of this kind */
  private WecTypeInput type;
  /** Is this asset market oriented? */
  private boolean marketReaction;
  /**
   * Constructor for an operated wind energy converter
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics
   * @param type of this WEC
   * @param marketReaction Is this asset market oriented?
   */
  public WecInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput node,
      String qCharacteristics,
      WecTypeInput type,
      boolean marketReaction) {
    super(uuid, operationTime, operator, id, node, qCharacteristics);
    this.type = type;
    this.marketReaction = marketReaction;
  }

  /**
   * Constructor for a non-operated wind energy converter
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics
   * @param type of this WEC
   * @param marketReaction Is this asset market oriented?
   */
  public WecInput(
      UUID uuid,
      String id,
      NodeInput node,
      String qCharacteristics,
      double cosphi,
      WecTypeInput type,
      boolean marketReaction) {
    super(uuid, id, node, qCharacteristics);
    this.type = type;
    this.marketReaction = marketReaction;
  }

  public WecTypeInput getType() {
    return type;
  }

  public void setType(WecTypeInput type) {
    this.type = type;
  }

  public boolean getMarketReaction() {
    return marketReaction;
  }

  public void setMarketReaction(boolean marketReaction) {
    this.marketReaction = marketReaction;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    WecInput wecInput = (WecInput) o;
    return Objects.equals(type, wecInput.type)
        && Objects.equals(marketReaction, wecInput.marketReaction);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, marketReaction);
  }
}
