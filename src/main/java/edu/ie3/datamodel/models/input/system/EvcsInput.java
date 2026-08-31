/*
 * © 2026. TU Dortmund University,
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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes an electric vehicle charging station. */
public class EvcsInput extends SystemParticipantInput {
  /** Type of all installed charging points. */
  private final ChargingPointType type;

  /** Number of installed charging points. */
  private final int chargingPoints;

  /** Rated power factor. */
  private final double cosPhiRated;

  /** Evcs location type. */
  private final EvcsLocationType locationType;

  /** Whether charging station supports vehicle to grid. */
  private final boolean v2gSupport;

  /**
   * @param uuid Unique identifier
   * @param id Human readable identifier
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
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
      EmInput controllingEm,
      ChargingPointType type,
      int chargingPoints,
      double cosPhiRated,
      EvcsLocationType locationType,
      boolean v2gSupport) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
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
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param type type of the charging points available to this charging station
   * @param chargingPoints number of charging points available at this charging station
   * @param cosPhiRated rated cos phi
   * @param locationType the location type
   * @param v2gSupport whether charging station supports vehicle to grid
   * @param additionalInformation That were provided by the source
   */
  public EvcsInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      ChargingPointType type,
      int chargingPoints,
      double cosPhiRated,
      EvcsLocationType locationType,
      boolean v2gSupport,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.type = type;
    this.chargingPoints = chargingPoints;
    this.cosPhiRated = cosPhiRated;
    this.locationType = locationType;
    this.v2gSupport = v2gSupport;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * @param uuid Unique identifier
   * @param id Human readable identifier
   * @param node that the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
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
      EmInput controllingEm,
      ChargingPointType type,
      int chargingPoints,
      double cosPhiRated,
      EvcsLocationType locationType,
      boolean v2gSupport) {
    super(uuid, id, node, qCharacteristics, controllingEm);
    this.type = type;
    this.chargingPoints = chargingPoints;
    this.cosPhiRated = cosPhiRated;
    this.locationType = locationType;
    this.v2gSupport = v2gSupport;
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

  public boolean isV2gSupport() {
    return v2gSupport;
  }

  @Override
  public ComparableQuantity<Power> sRated() {
    return this.type.getsRated();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EvcsInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(type, that.type)
        && chargingPoints == that.chargingPoints
        && cosPhiRated == that.cosPhiRated
        && Objects.equals(locationType, that.locationType)
        && v2gSupport == that.v2gSupport;
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        super.hashCode(), type, chargingPoints, cosPhiRated, locationType, v2gSupport);
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
        + ", qCharacteristics="
        + getqCharacteristics()
        + ", controllingEm="
        + getControllingEm().map(e -> e.getUuid().toString()).orElse("")
        + ", type="
        + type
        + ", chargingPoints="
        + chargingPoints
        + ", cosPhiRated="
        + cosPhiRated
        + ", locationType="
        + locationType
        + ", v2gSupport="
        + v2gSupport
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public EvcsInputCopyBuilder copy() {
    return new EvcsInputCopyBuilder(this);
  }

  public static class EvcsInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<EvcsInputCopyBuilder> {
    private ChargingPointType type;

    private int chargingPoints;

    private double cosPhiRated;

    private EvcsLocationType locationType;

    private boolean v2gSupport;

    protected EvcsInputCopyBuilder(EvcsInput entity) {
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

    protected ChargingPointType getType() {
      return type;
    }

    public EvcsInputCopyBuilder chargingPoints(int chargingPoints) {
      this.chargingPoints = chargingPoints;
      return thisInstance();
    }

    protected int getChargingPoints() {
      return chargingPoints;
    }

    public EvcsInputCopyBuilder cosPhiRated(double cosPhiRated) {
      this.cosPhiRated = cosPhiRated;
      return thisInstance();
    }

    protected double getCosPhiRated() {
      return cosPhiRated;
    }

    public EvcsInputCopyBuilder locationType(EvcsLocationType locationType) {
      this.locationType = locationType;
      return thisInstance();
    }

    protected EvcsLocationType getLocationType() {
      return locationType;
    }

    public EvcsInputCopyBuilder v2gSupport(boolean v2gSupport) {
      this.v2gSupport = v2gSupport;
      return thisInstance();
    }

    protected boolean isV2gSupport() {
      return v2gSupport;
    }

    @Override
    public EvcsInputCopyBuilder scale(double factor) {
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
          getControllingEm(),
          type,
          chargingPoints,
          cosPhiRated,
          locationType,
          v2gSupport,
          getAdditionalInformation());
    }

    @Override
    protected EvcsInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
