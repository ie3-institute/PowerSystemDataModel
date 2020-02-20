/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.connector;

import edu.ie3.models.OperationTime;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.OperatorInput;
import edu.ie3.models.input.connector.type.Transformer2WTypeInput;
import java.util.Objects;
import java.util.UUID;

/**
 * Describes a two winding transformer, that is connected to two {@link
 * edu.ie3.models.input.NodeInput}s
 */
public class Transformer2WInput extends TransformerInput {
  /** Type of this 2W transformer, containing default values for transformers of this kind */
  private final Transformer2WTypeInput type;

  /**
   * Constructor for an operated two winding transformer
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
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
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput nodeA,
      NodeInput nodeB,
      int parallelDevices,
      Transformer2WTypeInput type,
      int tapPos,
      boolean autoTap) {
    super(uuid, operationTime, operator, id, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.type = type;
  }

  /**
   * Constructor for a non-operated two winding transformer
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
      boolean autoTap) {
    super(uuid, id, nodeA, nodeB, parallelDevices, tapPos, autoTap);
    this.type = type;
  }

  public Transformer2WTypeInput getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    Transformer2WInput that = (Transformer2WInput) o;
    return type.equals(that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type);
  }
}
