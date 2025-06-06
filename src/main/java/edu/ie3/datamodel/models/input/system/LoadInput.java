/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.models.*;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.profile.LoadProfile;
import edu.ie3.datamodel.models.profile.StandardLoadProfile;
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries;
import edu.ie3.datamodel.models.timeseries.repetitive.RepetitiveTimeSeries;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a load */
public class LoadInput extends SystemParticipantInput {
  /**
   * Reference to a load profile to use for the model. If you intend to assign specific values,
   * create an {@link IndividualTimeSeries} or {@link RepetitiveTimeSeries} and assign it via an
   * external mapping (e.g. by providing a global time series for a specific load profile) to this
   * model
   */
  private final LoadProfile loadProfile;
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
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param loadProfile Load profile to use for this model
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
      EmInput em,
      LoadProfile loadProfile,
      ComparableQuantity<Energy> eConsAnnual,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, em);
    this.loadProfile = loadProfile;
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
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param loadProfileKey Load profile key corresponding to {@link
   *     edu.ie3.datamodel.models.profile.BdewStandardLoadProfile} or {@link
   *     edu.ie3.datamodel.models.profile.NbwTemperatureDependantLoadProfile}
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
      EmInput em,
      String loadProfileKey,
      ComparableQuantity<Energy> eConsAnnual,
      ComparableQuantity<Power> sRated,
      double cosPhiRated)
      throws ParsingException {

    this(
        uuid,
        id,
        operator,
        operationTime,
        node,
        qCharacteristics,
        em,
        LoadProfile.parse(loadProfileKey),
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
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param loadProfile Standard load profile to use for this model
   * @param eConsAnnual Annually consumed energy (typically in kWh)
   * @param sRated Rated apparent power (in kVA)
   * @param cosPhiRated Rated power factor
   */
  public LoadInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      LoadProfile loadProfile,
      ComparableQuantity<Energy> eConsAnnual,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id, node, qCharacteristics, em);
    this.loadProfile = loadProfile;
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
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param loadProfileKey load profile key corresponding to {@link
   *     edu.ie3.datamodel.models.profile.BdewStandardLoadProfile} or {@link
   *     edu.ie3.datamodel.models.profile.NbwTemperatureDependantLoadProfile}
   * @param eConsAnnual Annually consumed energy (typically in kWh)
   * @param sRated Rated apparent power (in kVA)
   * @param cosPhiRated Rated power factor
   */
  public LoadInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      String loadProfileKey,
      ComparableQuantity<Energy> eConsAnnual,
      ComparableQuantity<Power> sRated,
      double cosPhiRated)
      throws ParsingException {
    this(
        uuid,
        id,
        node,
        qCharacteristics,
        em,
        LoadProfile.parse(loadProfileKey),
        eConsAnnual,
        sRated,
        cosPhiRated);
  }

  public LoadProfile getLoadProfile() {
    return loadProfile;
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
    if (!(o instanceof LoadInput loadInput)) return false;
    if (!super.equals(o)) return false;
    return Double.compare(loadInput.cosPhiRated, cosPhiRated) == 0
        && eConsAnnual.equals(loadInput.eConsAnnual)
        && loadProfile.equals(loadInput.loadProfile)
        && sRated.equals(loadInput.sRated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), eConsAnnual, sRated, cosPhiRated);
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
        + "', em="
        + getControllingEm()
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

    private LoadProfile loadProfile;
    private ComparableQuantity<Energy> eConsAnnual;
    private ComparableQuantity<Power> sRated;
    private double cosPhiRated;

    private LoadInputCopyBuilder(LoadInput entity) {
      super(entity);
      this.loadProfile = entity.getLoadProfile();
      this.eConsAnnual = entity.geteConsAnnual();
      this.sRated = entity.getsRated();
      this.cosPhiRated = entity.getCosPhiRated();
    }

    public LoadInputCopyBuilder loadprofile(StandardLoadProfile standardLoadProfile) {
      this.loadProfile = standardLoadProfile;
      return thisInstance();
    }

    public LoadInputCopyBuilder eConsAnnual(ComparableQuantity<Energy> eConsAnnual) {
      this.eConsAnnual = eConsAnnual;
      return thisInstance();
    }

    public LoadInputCopyBuilder sRated(ComparableQuantity<Power> sRated) {
      this.sRated = sRated;
      return thisInstance();
    }

    public LoadInputCopyBuilder cosPhiRated(double cosPhiRated) {
      this.cosPhiRated = cosPhiRated;
      return thisInstance();
    }

    @Override
    public LoadInputCopyBuilder scale(Double factor) {
      eConsAnnual(eConsAnnual.multiply(factor));
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
          getqCharacteristics(),
          getEm(),
          loadProfile,
          eConsAnnual,
          sRated,
          cosPhiRated);
    }

    @Override
    protected LoadInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
