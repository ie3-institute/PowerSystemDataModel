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
import edu.ie3.datamodel.models.profile.PowerProfileKey;
import edu.ie3.datamodel.utils.QuantityUtils;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a load. */
public class LoadInput extends SystemParticipantInput {
  /**
   * Reference to a load profile to use for the model. If you intend to assign specific values,
   * create an timeSeries and assign it via an external mapping (e.g. by providing a global time
   * series for a specific load profile) to this model.
   */
  private final PowerProfileKey loadProfile;

  /** Annually consumed energy (typically in kWh). */
  private final ComparableQuantity<Energy> eConsAnnual;

  /** Active Power (typically in kVA). */
  private final ComparableQuantity<Power> sRated;

  /** Rated power factor. */
  private final double cosPhiRated;

  /**
   * Constructor for an operated load.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param loadProfile Key of the load profile to use for this model
   * @param eConsAnnual Annually consumed energy (typically in kWh)
   * @param sRated Rated apparent power (in kVA)
   * @param cosPhiRated Rated power factor
   */
  public LoadInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      PowerProfileKey loadProfile,
      ComparableQuantity<Energy> eConsAnnual,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.loadProfile = loadProfile;
    this.eConsAnnual = eConsAnnual;
    this.sRated = sRated;
    this.cosPhiRated = cosPhiRated;
  }

  /**
   * Constructor for an operated load.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param loadProfile Key of the load profile to use for this model
   * @param eConsAnnual Annually consumed energy (typically in kWh)
   * @param sRated Rated apparent power (in kVA)
   * @param cosPhiRated Rated power factor
   * @param additionalInformation That were provided by the source
   */
  public LoadInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      PowerProfileKey loadProfile,
      ComparableQuantity<Energy> eConsAnnual,
      ComparableQuantity<Power> sRated,
      double cosPhiRated,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.loadProfile = loadProfile;
    this.eConsAnnual = eConsAnnual;
    this.sRated = sRated;
    this.cosPhiRated = cosPhiRated;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated load.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param loadProfile Key of the load profile to use for this model
   * @param eConsAnnual Annually consumed energy (typically in kWh)
   * @param sRated Rated apparent power (in kVA)
   * @param cosPhiRated Rated power factor
   */
  public LoadInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      PowerProfileKey loadProfile,
      ComparableQuantity<Energy> eConsAnnual,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id, node, qCharacteristics, controllingEm);
    this.loadProfile = loadProfile;
    this.eConsAnnual = eConsAnnual;
    this.sRated = sRated;
    this.cosPhiRated = cosPhiRated;
  }

  public PowerProfileKey getLoadProfile() {
    return loadProfile;
  }

  public ComparableQuantity<Energy> getEConsAnnual() {
    return eConsAnnual;
  }

  public ComparableQuantity<Power> getSRated() {
    return sRated;
  }

  public double getCosPhiRated() {
    return cosPhiRated;
  }

  @Override
  public ComparableQuantity<Power> sRated() {
    return sRated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof LoadInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(loadProfile, that.loadProfile)
        && QuantityUtils.equals(eConsAnnual, that.eConsAnnual)
        && QuantityUtils.equals(sRated, that.sRated)
        && cosPhiRated == that.cosPhiRated;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), loadProfile, eConsAnnual, sRated, cosPhiRated);
  }

  @Override
  public String toString() {
    return "LoadInput{"
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
        + getQCharacteristics()
        + ", controllingEm="
        + getControllingEm().map(e -> e.getUuid().toString()).orElse("")
        + ", loadProfile="
        + loadProfile
        + ", eConsAnnual="
        + eConsAnnual
        + ", sRated="
        + sRated
        + ", cosPhiRated="
        + cosPhiRated
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public LoadInputCopyBuilder copy() {
    return new LoadInputCopyBuilder(this);
  }

  public static class LoadInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<LoadInputCopyBuilder> {
    private PowerProfileKey loadProfile;

    private ComparableQuantity<Energy> eConsAnnual;

    private ComparableQuantity<Power> sRated;

    private double cosPhiRated;

    protected LoadInputCopyBuilder(LoadInput entity) {
      super(entity);
      this.loadProfile = entity.loadProfile;
      this.eConsAnnual = entity.eConsAnnual;
      this.sRated = entity.sRated;
      this.cosPhiRated = entity.cosPhiRated;
    }

    public LoadInputCopyBuilder loadProfile(PowerProfileKey loadProfile) {
      this.loadProfile = loadProfile;
      return thisInstance();
    }

    protected PowerProfileKey getLoadProfile() {
      return loadProfile;
    }

    public LoadInputCopyBuilder eConsAnnual(ComparableQuantity<Energy> eConsAnnual) {
      this.eConsAnnual = eConsAnnual;
      return thisInstance();
    }

    protected ComparableQuantity<Energy> getEConsAnnual() {
      return eConsAnnual;
    }

    public LoadInputCopyBuilder sRated(ComparableQuantity<Power> sRated) {
      this.sRated = sRated;
      return thisInstance();
    }

    protected ComparableQuantity<Power> getSRated() {
      return sRated;
    }

    public LoadInputCopyBuilder cosPhiRated(double cosPhiRated) {
      this.cosPhiRated = cosPhiRated;
      return thisInstance();
    }

    protected double getCosPhiRated() {
      return cosPhiRated;
    }

    @Override
    public LoadInputCopyBuilder scale(double factor) {
      return eConsAnnual(eConsAnnual.multiply(factor));
      sRated(sRated.multiply(factor));
      return thisInstance();
    }

    @Override
    public LoadInput build() {
      return new LoadInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNode(),
          getQCharacteristics(),
          getControllingEm(),
          loadProfile,
          eConsAnnual,
          sRated,
          cosPhiRated,
          getAdditionalInformation());
    }

    @Override
    protected LoadInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
