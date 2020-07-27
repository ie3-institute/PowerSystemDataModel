/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointType;
import java.util.Objects;
import java.util.UUID;

public class EvcsInput extends SystemParticipantInput {

  /** type of all installed charging points */
  private final ChargingPointType type;

  /** no of installed charging points */
  private final int chargingPoints;

  /** Rated power factor */
  private final double cosPhiRated;

  public EvcsInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      ChargingPointType type,
      int chargingPoints,
      double cosPhiRated) {
    super(uuid, id, operator, operationTime, node, qCharacteristics);
    this.type = type;
    this.chargingPoints = chargingPoints;
    this.cosPhiRated = cosPhiRated;
  }

  public EvcsInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      ChargingPointType type,
      double cosPhiRated) {
    this(uuid, id, operator, operationTime, node, qCharacteristics, type, 1, cosPhiRated);
  }

  public EvcsInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      ChargingPointType type,
      int chargingPoints,
      double cosPhiRated) {
    super(uuid, id, node, qCharacteristics);
    this.type = type;
    this.chargingPoints = chargingPoints;
    this.cosPhiRated = cosPhiRated;
  }

  public EvcsInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      ChargingPointType type,
      double cosPhiRated) {
    this(uuid, id, node, qCharacteristics, type, 1, cosPhiRated);
  }

  public ChargingPointType getType() {
    return type;
  }

  public int getChargingPoints() {
    return chargingPoints;
  }

  public double getCosPhiRated() {
    return cosPhiRated;
  }

  @Override
  public EvcsInputCopyBuilder copy() {
    return new EvcsInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    EvcsInput evcsInput = (EvcsInput) o;
    return chargingPoints == evcsInput.chargingPoints
        && Double.compare(evcsInput.cosPhiRated, cosPhiRated) == 0
        && type.equals(evcsInput.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, chargingPoints, cosPhiRated);
  }

  @Override
  public String toString() {
    return "EvcsInput{"
        + "id='"
        + getId()
        + '\''
        + ", uuid="
        + getUuid()
        + ", type="
        + type
        + ", chargingPoints="
        + chargingPoints
        + ", cosPhiRated="
        + cosPhiRated
        + ", node="
        + getNode()
        + "} "
        + super.toString();
  }

  public static class EvcsInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<EvcsInputCopyBuilder> {

    private ChargingPointType type;
    private int chargingPoints;
    private double cosPhiRated;

    public EvcsInputCopyBuilder(EvcsInput entity) {
      super(entity);
      this.type = entity.type;
      this.chargingPoints = entity.chargingPoints;
      this.cosPhiRated = entity.cosPhiRated;
    }

    public EvcsInputCopyBuilder type(ChargingPointType type) {
      this.type = type;
      return this;
    }

    public EvcsInputCopyBuilder chargingPoints(int noChargingPoints) {
      this.chargingPoints = noChargingPoints;
      return this;
    }

    public EvcsInputCopyBuilder cosPhiRated(double cosPhiRated) {
      this.cosPhiRated = cosPhiRated;
      return this;
    }

    @Override
    public EvcsInput build() {
      return new EvcsInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNode(),
          getqCharacteristics(),
          type,
          chargingPoints,
          cosPhiRated);
    }

    @Override
    protected EvcsInputCopyBuilder childInstance() {
      return this;
    }
  }
}
