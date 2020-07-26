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

/**
 * //ToDo: Class Description
 *
 * @version 0.1
 * @since 25.07.20
 */
public class CsInput extends SystemParticipantInput {

  private final ChargingPointType type;
  private final int noChargingPoints;

  /** Rated power factor */
  private final double cosPhiRated;

  public CsInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      ChargingPointType type,
      int noChargingPoints,
      double cosPhiRated) {
    super(uuid, id, operator, operationTime, node, qCharacteristics);
    this.type = type;
    this.noChargingPoints = noChargingPoints;
    this.cosPhiRated = cosPhiRated;
  }

  public CsInput(
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

  public CsInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      ChargingPointType type,
      int noChargingPoints,
      double cosPhiRated) {
    super(uuid, id, node, qCharacteristics);
    this.type = type;
    this.noChargingPoints = noChargingPoints;
    this.cosPhiRated = cosPhiRated;
  }

  public CsInput(
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

  public int getNoChargingPoints() {
    return noChargingPoints;
  }

  public double getCosPhiRated() {
    return cosPhiRated;
  }

  @Override
  public CsInputCopyBuilder copy() {
    return new CsInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    CsInput csInput = (CsInput) o;
    return noChargingPoints == csInput.noChargingPoints
        && Double.compare(csInput.cosPhiRated, cosPhiRated) == 0
        && type.equals(csInput.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, noChargingPoints, cosPhiRated);
  }

  @Override
  public String toString() {
    return "CsInput{"
        + ", id='"
        + getId()
        + '\''
        + ", uuid="
        + getUuid()
        + "type="
        + type
        + ", noOfConnectionPoints="
        + noChargingPoints
        + ", cosPhiRated="
        + cosPhiRated
        + "} "
        + super.toString();
  }

  public static class CsInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<CsInputCopyBuilder> {

    private ChargingPointType type;
    private int noChargingPoints;
    private double cosPhiRated;

    public CsInputCopyBuilder(CsInput entity) {
      super(entity);
      this.type = entity.type;
      this.noChargingPoints = entity.noChargingPoints;
      this.cosPhiRated = entity.cosPhiRated;
    }

    public CsInputCopyBuilder type(ChargingPointType type) {
      this.type = type;
      return this;
    }

    public CsInputCopyBuilder noChargingPoints(int noChargingPoints) {
      this.noChargingPoints = noChargingPoints;
      return this;
    }

    public CsInputCopyBuilder cosPhiRated(double cosPhiRated) {
      this.cosPhiRated = cosPhiRated;
      return this;
    }

    @Override
    public CsInput build() {
      return new CsInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNode(),
          getqCharacteristics(),
          type,
          noChargingPoints,
          cosPhiRated);
    }

    @Override
    protected CsInputCopyBuilder childInstance() {
      return this;
    }
  }
}
