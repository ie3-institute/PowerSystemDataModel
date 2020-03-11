/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.type.WecTypeInput;
import java.util.Objects;
import java.util.UUID;

/** Describes a Wind Energy Converter */
public class WecInput extends SystemParticipantInput {

  /** Type of this WEC, containing default values for WEC assets of this kind */
  private final WecTypeInput type;
  /** Is this asset market oriented? */
  private final boolean marketReaction;
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
      WecTypeInput type,
      boolean marketReaction) {
    super(uuid, id, node, qCharacteristics);
    this.type = type;
    this.marketReaction = marketReaction;
  }

  public boolean isMarketReaction() {
    return marketReaction;
  }

  public WecTypeInput getType() {
    return type;
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

  @Override
  public String toString() {
    return "WecInput{" +
            "type=" + type +
            ", marketReaction=" + marketReaction +
            '}';
  }
}
