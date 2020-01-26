/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.connector;

import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.util.interval.ClosedInterval;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Describes a two winding transformer, that is connected to two {@link
 * edu.ie3.models.input.NodeInput}s
 */
public class Transformer2WInput extends TransformerInput {
  /** Type of this 2W transformer, containing default values for transformers of this kind */
  private Transformer2WTypeInput type;

  /**
   * @param uuid of the input entity
   * @param operationInterval Empty for a non-operated asset, Interval of operation period else
   * @param operator of the asset
   * @param id of the asset
   * @param nodeA higher voltage node
   * @param nodeB lower voltage node
   * @param parallelDevices Amount of singular transformers
   * @param type of 2W transformer
   * @param tapPos Tap position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   */
  public Transformer2WInput(
      UUID uuid,
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      Transformer2WTypeInput type,
      int tapPos,
      Boolean autoTap) {
    super(uuid, operationInterval, operator, id, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.type = type;
  }
  /**
   * If both operatesFrom and operatesUntil are Empty, it is assumed that the asset is non-operated.
   *
   * @param uuid of the input entity
   * @param operatesFrom start of operation period, will be replaced by LocalDateTime.MIN if Empty
   * @param operatesUntil end of operation period, will be replaced by LocalDateTime.MAX if Empty
   * @param operator of the asset
   * @param id of the asset
   * @param nodeA higher voltage node
   * @param nodeB lower voltage node
   * @param parallelDevices Amount of singular transformers
   * @param type of 2W transformer
   * @param tapPos Tap position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   */
  public Transformer2WInput(
      UUID uuid,
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      Transformer2WTypeInput type,
      int tapPos,
      Boolean autoTap) {
    super(
        uuid,
        operatesFrom,
        operatesUntil,
        operator,
        id,
        nodeA,
        nodeB,
        parallelDevices,
        tapPos,
        autoTap);
    this.type = type;
  }

  /**
   * Constructor for a non-operated asset
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param nodeA higher voltage node
   * @param nodeB lower voltage node
   * @param parallelDevices Amount of singular transformers
   * @param type of 2W transformer
   * @param tapPos Tap position of this transformer
   * @param autoTap True, if the tap position of the transformer is adapted automatically
   */
  public Transformer2WInput(
      UUID uuid,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      Transformer2WTypeInput type,
      int tapPos,
      Boolean autoTap) {
    super(uuid, id, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.type = type;
  }

  public Transformer2WTypeInput getType() {
    return type;
  }

  public void setType(Transformer2WTypeInput type) {
    this.type = type;
  }

  @Override
  public boolean equals(Object o) {
    if(this == o)
      return true;
    if(o == null || getClass() != o.getClass())
      return false;
    if(!super.equals(o))
      return false;
    Transformer2WInput that = (Transformer2WInput) o;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type);
  }
}
