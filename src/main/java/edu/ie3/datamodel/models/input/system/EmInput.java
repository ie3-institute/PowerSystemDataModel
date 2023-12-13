/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.models.ControlStrategy;
import edu.ie3.datamodel.models.EmControlStrategy;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class EmInput extends AssetInput {

  /** Reference via UUID to all SystemParticipantInputs connected to this model */
  private final UUID[] connectedAssets;

  /** Reference to the control strategy to be used for this model */
  private final ControlStrategy controlStrategy;
  /**
   * Constructor for an operated energy management system
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime time for which the entity is operated
   * @param connectedAssets array of all connected assets
   * @param controlStrategy control strategy used for this model
   */
  public EmInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      UUID[] connectedAssets,
      ControlStrategy controlStrategy) {
    super(uuid, id, operator, operationTime);
    this.connectedAssets = connectedAssets;
    this.controlStrategy = controlStrategy;
  }

  /**
   * Constructor for an operated energy management system
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime time for which the entity is operated
   * @param connectedAssets array of all connected assets
   * @param emControlStrategy {@link edu.ie3.datamodel.models.EmControlStrategy} control strategy
   *     key
   */
  public EmInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      UUID[] connectedAssets,
      String emControlStrategy) {
    super(uuid, id, operator, operationTime);
    this.connectedAssets = connectedAssets;
    this.controlStrategy = EmControlStrategy.get(emControlStrategy);
  }

  /**
   * Constructor for an operated energy management system
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param connectedAssets array of all connected assets
   * @param controlStrategy control strategy used for this model
   */
  public EmInput(UUID uuid, String id, UUID[] connectedAssets, ControlStrategy controlStrategy) {
    super(uuid, id);
    this.connectedAssets = connectedAssets;
    this.controlStrategy = controlStrategy;
  }

  /**
   * Constructor for an operated energy management system
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param connectedAssets array of all connected assets
   * @param emControlStrategy {@link edu.ie3.datamodel.models.EmControlStrategy} control strategy
   *     key
   */
  public EmInput(UUID uuid, String id, UUID[] connectedAssets, String emControlStrategy) {
    super(uuid, id);
    this.connectedAssets = connectedAssets;
    this.controlStrategy = EmControlStrategy.get(emControlStrategy);
  }

  public UUID[] getConnectedAssets() {
    return connectedAssets;
  }

  public ControlStrategy getControlStrategy() {
    return controlStrategy;
  }

  @Override
  public EmInputCopyBuilder copy() {
    return new EmInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EmInput emInput)) return false;
    if (!super.equals(o)) return false;
    return Arrays.equals(connectedAssets, emInput.connectedAssets)
        && controlStrategy == emInput.controlStrategy;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), Arrays.hashCode(connectedAssets), controlStrategy);
  }

  @Override
  public String toString() {
    return "EmInput{"
        + "uuid="
        + getUuid()
        + ", id='"
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", connectedAssets="
        + Arrays.toString(connectedAssets)
        + ", controlStrategy="
        + controlStrategy
        + '}';
  }

  public static class EmInputCopyBuilder extends AssetInputCopyBuilder<EmInputCopyBuilder> {

    private UUID[] connectedAssets;

    private ControlStrategy controlStrategy;

    protected EmInputCopyBuilder(EmInput entity) {
      super(entity);
      this.connectedAssets = entity.getConnectedAssets();
      this.controlStrategy = entity.getControlStrategy();
    }

    public EmInputCopyBuilder connectedAssets(UUID[] connectedAssets) {
      this.connectedAssets = connectedAssets;
      return this;
    }

    public EmInputCopyBuilder controlStrategy(ControlStrategy controlStrategy) {
      this.controlStrategy = controlStrategy;
      return this;
    }

    @Override
    public EmInput build() {
      return new EmInput(
          getUuid(), getId(), getOperator(), getOperationTime(), connectedAssets, controlStrategy);
    }

    @Override
    protected EmInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
