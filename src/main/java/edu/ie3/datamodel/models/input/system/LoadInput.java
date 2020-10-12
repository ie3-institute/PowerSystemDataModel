/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.models.BdewLoadProfile;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardLoadProfile;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.RepetitiveTimeSeries;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tec.uom.se.ComparableQuantity;

/** Describes a load */
public class LoadInput extends SystemParticipantInput {
  /**
   * Reference to a standard load profile to use for the model. If you intend to assign specific
   * values, create an {@link IndividualTimeSeries} or {@link RepetitiveTimeSeries} and assign it
   * via an external mapping (e.g. by providing a global time series for a specific load profile) to
   * this model
   */
  private final StandardLoadProfile standardLoadProfile;
  /** True, if demand side management is activated for this load */
  private final boolean dsm;
  /** Annually consumed energy (typically in kWh) */
  private final ComparableQuantity<Energy> eConsAnnual;
  /** Active Power (typically in kVA) */
  private final ComparableQuantity<Power> sRated;
  /** Rated power factor */
  private final double cosPhiRated;

  /**
   * Constructor for an operated load
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param standardLoadProfile Standard load profile to use for this model
   * @param dsm True, if demand side management is activated for this load
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
      StandardLoadProfile standardLoadProfile,
      boolean dsm,
      ComparableQuantity<Energy> eConsAnnual,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id, operator, operationTime, node, qCharacteristics);
    this.standardLoadProfile = standardLoadProfile;
    this.dsm = dsm;
    this.eConsAnnual = eConsAnnual.to(StandardUnits.ENERGY_IN);
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.cosPhiRated = cosPhiRated;
  }

  /**
   * Constructor for an operated load
   *
   * @param uuid of the input entity
   * @param operationTime Time for which the entity is operated
   * @param operator of the asset
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param bdewStandardLoadProfile {@link edu.ie3.datamodel.models.BdewLoadProfile} load profile
   *     key
   * @param dsm True, if demand side management is activated for this load
   * @param eConsAnnual Annually consumed energy (typically in kWh)
   * @param sRated Rated apparent power (in kVA)
   * @param cosPhiRated Rated power factor
   */
  public LoadInput(
      UUID uuid,
      OperationTime operationTime,
      OperatorInput operator,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      String bdewStandardLoadProfile,
      boolean dsm,
      ComparableQuantity<Energy> eConsAnnual,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    this(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        BdewLoadProfile.get(bdewStandardLoadProfile),
        dsm,
        eConsAnnual,
        sRated,
        cosPhiRated);
  }

  /**
   * Constructor for an operated, always on load
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param standardLoadProfile Standard load profile to use for this model
   * @param dsm True, if demand side management is activated for this load
   * @param eConsAnnual Annually consumed energy (typically in kWh)
   * @param sRated Rated apparent power (in kVA)
   * @param cosPhiRated Rated power factor
   */
  public LoadInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      StandardLoadProfile standardLoadProfile,
      boolean dsm,
      ComparableQuantity<Energy> eConsAnnual,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id, node, qCharacteristics);
    this.standardLoadProfile = standardLoadProfile;
    this.dsm = dsm;
    this.eConsAnnual = eConsAnnual.to(StandardUnits.ENERGY_IN);
    this.sRated = sRated.to(StandardUnits.S_RATED);
    this.cosPhiRated = cosPhiRated;
  }

  /**
   * Constructor for an operated, always on load
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param bdewStandardLoadProfile {@link edu.ie3.datamodel.models.BdewLoadProfile} load profile
   *     key
   * @param dsm True, if demand side management is activated for this load
   * @param eConsAnnual Annually consumed energy (typically in kWh)
   * @param sRated Rated apparent power (in kVA)
   * @param cosPhiRated Rated power factor
   */
  public LoadInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      String bdewStandardLoadProfile,
      boolean dsm,
      ComparableQuantity<Energy> eConsAnnual,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    this(
        uuid,
        id,
        node,
        qCharacteristics,
        BdewLoadProfile.get(bdewStandardLoadProfile),
        dsm,
        eConsAnnual,
        sRated,
        cosPhiRated);
  }

  public StandardLoadProfile getStandardLoadProfile() {
    return standardLoadProfile;
  }

  public boolean isDsm() {
    return dsm;
  }

  public ComparableQuantity<Energy> geteConsAnnual() {
    return eConsAnnual;
  }

  public ComparableQuantity<Power> getsRated() {
    return sRated;
  }

  public double getCosPhiRated() {
    return cosPhiRated;
  }

  public LoadInputCopyBuilder copy() {
    return new LoadInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    LoadInput loadInput = (LoadInput) o;
    return dsm == loadInput.dsm
        && Double.compare(loadInput.cosPhiRated, cosPhiRated) == 0
        && eConsAnnual.equals(loadInput.eConsAnnual)
        && sRated.equals(loadInput.sRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), dsm, eConsAnnual, sRated, cosPhiRated);
  }

  @Override
  public String toString() {
    return "LoadInput{"
        + "uuid="
        + getUuid()
        + ", id='"
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", node="
        + getNode().getUuid()
        + ", qCharacteristics='"
        + getqCharacteristics()
        + '\''
        + ", dsm="
        + dsm
        + ", eConsAnnual="
        + eConsAnnual
        + ", sRated="
        + sRated
        + ", cosphiRated="
        + cosPhiRated
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link LoadInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link LoadInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class LoadInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<LoadInputCopyBuilder> {

    private StandardLoadProfile standardLoadProfile;
    private boolean dsm;
    private ComparableQuantity<Energy> eConsAnnual;
    private ComparableQuantity<Power> sRated;
    private double cosPhiRated;

    private LoadInputCopyBuilder(LoadInput entity) {
      super(entity);
      this.standardLoadProfile = entity.getStandardLoadProfile();
      this.dsm = entity.isDsm();
      this.eConsAnnual = entity.geteConsAnnual();
      this.sRated = entity.getsRated();
      this.cosPhiRated = entity.getCosPhiRated();
    }

    public LoadInputCopyBuilder standardLoadProfile(StandardLoadProfile standardLoadProfile) {
      this.standardLoadProfile = standardLoadProfile;
      return this;
    }

    public LoadInputCopyBuilder dsm(boolean dsm) {
      this.dsm = dsm;
      return this;
    }

    public LoadInputCopyBuilder eConsAnnual(ComparableQuantity<Energy> eConsAnnual) {
      this.eConsAnnual = eConsAnnual;
      return this;
    }

    public LoadInputCopyBuilder sRated(ComparableQuantity<Power> sRated) {
      this.sRated = sRated;
      return this;
    }

    public LoadInputCopyBuilder cosPhiRated(double cosPhiRated) {
      this.cosPhiRated = cosPhiRated;
      return this;
    }

    @Override
    public LoadInput build() {
      return new LoadInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNode(),
          getqCharacteristics(),
          standardLoadProfile,
          dsm,
          eConsAnnual,
          sRated,
          cosPhiRated);
    }

    @Override
    protected LoadInputCopyBuilder childInstance() {
      return this;
    }
  }
}
