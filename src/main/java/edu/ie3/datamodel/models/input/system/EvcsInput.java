/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.input.EmInput;
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

  /** Whether charging station supports vehicle to grid */
  private final boolean v2gSupport;

  /**
   * @param uuid Unique identifier
   * @param id Human readable identifier
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type type of the charging points available to this charging station
   * @param chargingPoints number of charging points available at this charging station
   * @param cosPhiRated rated cos phi
   * @param locationType the location type
   * @param v2gSupport whether charging station supports vehicle to grid
   */
  public EvcsInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      ChargingPointType type,
      int chargingPoints,
      double cosPhiRated,
      EvcsLocationType locationType,
      boolean v2gSupport) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, em);
    this.type = type;
    this.chargingPoints = chargingPoints;
    this.cosPhiRated = cosPhiRated;
    this.locationType = locationType;
    this.v2gSupport = v2gSupport;
  }

  /**
   * @param uuid Unique identifier
   * @param id Human readable identifier
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type type of the charging points available to this charging station
   * @param cosPhiRated rated cos phi
   * @param locationType the location type
   * @param v2gSupport whether charging station supports vehicle to grid
   */
  public EvcsInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      ChargingPointType type,
      double cosPhiRated,
      EvcsLocationType locationType,
      boolean v2gSupport) {
    this(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        em,
        type,
        1,
        cosPhiRated,
        locationType,
        v2gSupport);
  }
  /**
   * @param uuid Unique identifier
   * @param id Human readable identifier
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type type of the charging points available to this charging station
   * @param chargingPoints number of charging points available at this charging station
   * @param cosPhiRated rated cos phi
   * @param locationType the location type
   * @param v2gSupport whether charging station supports vehicle to grid
   */
  public EvcsInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      ChargingPointType type,
      int chargingPoints,
      double cosPhiRated,
      EvcsLocationType locationType,
      boolean v2gSupport) {
    super(uuid, id, node, qCharacteristics, em);
    this.type = type;
    this.chargingPoints = chargingPoints;
    this.cosPhiRated = cosPhiRated;
    this.locationType = locationType;
    this.v2gSupport = v2gSupport;
  }

  /**
   * @param uuid Unique identifier
   * @param id Human readable identifier
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type type of the charging points available to this charging station
   * @param cosPhiRated rated cos phi
   * @param locationType the location type
   * @param v2gSupport whether charging station supports vehicle to grid
   */
  public EvcsInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      ChargingPointType type,
      double cosPhiRated,
      EvcsLocationType locationType,
      boolean v2gSupport) {
    this(uuid, id, node, qCharacteristics, em, type, 1, cosPhiRated, locationType, v2gSupport);
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

  public boolean getV2gSupport() {
    return v2gSupport;
  }

  @Override
  public EvcsInputCopyBuilder copy() {
    return new EvcsInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EvcsInput evcsInput)) return false;
    if (!super.equals(o)) return false;
    return chargingPoints == evcsInput.chargingPoints
        && Double.compare(evcsInput.cosPhiRated, cosPhiRated) == 0
        && type.equals(evcsInput.type)
        && locationType.equals(evcsInput.locationType)
        && v2gSupport == evcsInput.v2gSupport;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, chargingPoints, cosPhiRated, locationType);
  }

  @Override
  public String toString() {
    return "EvcsInput{"
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
        + type
        + ", chargingPoints="
        + chargingPoints
        + ", cosPhiRated="
        + cosPhiRated
        + ", locationType="
        + locationType
        + ", v2gSupport="
        + getV2gSupport()
        + '}';
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
    private boolean v2gSupport;

    public EvcsInputCopyBuilder(EvcsInput entity) {
      super(entity);
      this.type = entity.type;
      this.chargingPoints = entity.chargingPoints;
      this.cosPhiRated = entity.cosPhiRated;
      this.locationType = entity.locationType;
      this.v2gSupport = entity.v2gSupport;
    }

    public EvcsInputCopyBuilder type(ChargingPointType type) {
      this.type = type;
      return thisInstance();
    }

    public EvcsInputCopyBuilder chargingPoints(int noChargingPoints) {
      this.chargingPoints = noChargingPoints;
      return thisInstance();
    }

    public EvcsInputCopyBuilder cosPhiRated(double cosPhiRated) {
      this.cosPhiRated = cosPhiRated;
      return thisInstance();
    }

    public EvcsInputCopyBuilder locationType(EvcsLocationType locationType) {
      this.locationType = locationType;
      return thisInstance();
    }

    public EvcsInputCopyBuilder v2gSupport(boolean v2gSupport) {
      this.v2gSupport = v2gSupport;
      return thisInstance();
    }

    @Override
    public EvcsInputCopyBuilder scale(Double factor) {
      type(type.copy().scale(factor).build());
      return thisInstance();
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
          getEm(),
          type,
          chargingPoints,
          cosPhiRated,
          locationType,
          v2gSupport);
    }

    @Override
    protected EvcsInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
