/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointType;
import edu.ie3.datamodel.models.input.system.type.evcslocation.EvcsLocationType;
import java.util.Objects;
import java.util.UUID;

public class EvcsInput extends SystemParticipantInput {

  /** type of all installed charging points */
  private final ChargingPointType type;

  /** no of installed charging points */
  private final int chargingPoints;

  /** Rated power factor */
  private final double cosPhiRated;

  /** Evcs location type */
  private final EvcsLocationType locationType;

  /**
   * @param uuid Unique identifier
   * @param id Human readable identifier
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param type type of the charging points available to this charging station
   * @param chargingPoints number of charging points available at this charging station
   * @param cosPhiRated rated cos phi
   * @param locationType the location type
   */
  public EvcsInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      ChargingPointType type,
      int chargingPoints,
      double cosPhiRated,
      EvcsLocationType locationType) {
    super(uuid, id, operator, operationTime, node, qCharacteristics);
    this.type = type;
    this.chargingPoints = chargingPoints;
    this.cosPhiRated = cosPhiRated;
    this.locationType = locationType;
  }

  /**
   * @param uuid Unique identifier
   * @param id Human readable identifier
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param type type of the charging points available to this charging station
   * @param cosPhiRated rated cos phi
   * @param locationType the location type
   */
  public EvcsInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      ChargingPointType type,
      double cosPhiRated,
      EvcsLocationType locationType) {
    this(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        type,
        1,
        cosPhiRated,
        locationType);
  }
  /**
   * @param uuid Unique identifier
   * @param id Human readable identifier
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param type type of the charging points available to this charging station
   * @param chargingPoints number of charging points available at this charging station
   * @param cosPhiRated rated cos phi
   * @param locationType the location type
   */
  public EvcsInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      ChargingPointType type,
      int chargingPoints,
      double cosPhiRated,
      EvcsLocationType locationType) {
    super(uuid, id, node, qCharacteristics);
    this.type = type;
    this.chargingPoints = chargingPoints;
    this.cosPhiRated = cosPhiRated;
    this.locationType = locationType;
  }

  /**
   * @param uuid Unique identifier
   * @param id Human readable identifier
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param type type of the charging points available to this charging station
   * @param cosPhiRated rated cos phi
   * @param locationType the location type
   */
  public EvcsInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      ChargingPointType type,
      double cosPhiRated,
      EvcsLocationType locationType) {
    this(uuid, id, node, qCharacteristics, type, 1, cosPhiRated, locationType);
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

  public EvcsLocationType getLocationType() {
    return locationType;
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
        && type.equals(evcsInput.type)
        && locationType.equals(evcsInput.locationType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, chargingPoints, cosPhiRated, locationType);
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
        + ", locationType="
        + locationType
        + ", node="
        + getNode()
        + "} "
        + super.toString();
  }

  /**
   * A builder pattern based approach to create copies of {@link EvcsInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link EvcsInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class EvcsInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<EvcsInputCopyBuilder> {

    private ChargingPointType type;
    private int chargingPoints;
    private double cosPhiRated;
    private EvcsLocationType locationType;

    public EvcsInputCopyBuilder(EvcsInput entity) {
      super(entity);
      this.type = entity.type;
      this.chargingPoints = entity.chargingPoints;
      this.cosPhiRated = entity.cosPhiRated;
      this.locationType = entity.locationType;
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

    public EvcsInputCopyBuilder locationType(EvcsLocationType locationType) {
      this.locationType = locationType;
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
          cosPhiRated,
          locationType);
    }

    @Override
    protected EvcsInputCopyBuilder childInstance() {
      return this;
    }
  }
}
