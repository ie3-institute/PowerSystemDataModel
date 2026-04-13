/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.system;

import edu.ie3.datamodel.io.extractor.HasType;
import edu.ie3.datamodel.models.OperationTime;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.system.characteristic.ReactivePowerCharacteristic;
import edu.ie3.datamodel.models.input.system.type.BmTypeInput;
import edu.ie3.util.quantities.interfaces.EnergyPrice;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Describes a biomass plant */
public class BmInput extends SystemParticipantInput implements HasType {
  /** Type of this BM plant, containing default values for BM plants of this kind */
  private final BmTypeInput type;

  /**
   * Does this plant increase the output power if the revenues exceed the energy generation costs?
   */
  private final boolean costControlled;

  /** Granted feed in tariff (typically in €/MWh) */
  private final ComparableQuantity<EnergyPrice> feedInTariff;

  /**
   * Constructor for an operated biomass plant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type of BM
   * @param costControlled Does this plant increase the output power if the revenues exceed the
   *     energy generation costs?
   * @param feedInTariff Granted feed in tariff (typically in €/MWh)
   */
  public BmInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      BmTypeInput type,
      boolean costControlled,
      ComparableQuantity<EnergyPrice> feedInTariff) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, em);
    this.type = type;
    this.costControlled = costControlled;
    this.feedInTariff = feedInTariff.to(StandardUnits.ENERGY_PRICE);
  }

  /**
   * Constructor for an operated biomass plant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type of BM
   * @param costControlled Does this plant increase the output power if the revenues exceed the
   *     energy generation costs?
   * @param feedInTariff Granted feed in tariff (typically in €/MWh)
   * @param additionalInformation That were provided by the source
   */
  public BmInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      BmTypeInput type,
      boolean costControlled,
      ComparableQuantity<EnergyPrice> feedInTariff,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, em);
    this.type = type;
    this.costControlled = costControlled;
    this.feedInTariff = feedInTariff.to(StandardUnits.ENERGY_PRICE);
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated, always on biomass plant
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param em The {@link EmInput} controlling this system participant. Null, if not applicable.
   * @param type of BM
   * @param costControlled Does this plant increase the output power if the revenues exceed the
   *     energy generation costs?
   * @param feedInTariff Granted feed in tariff (typically in €/MWh)
   */
  public BmInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput em,
      BmTypeInput type,
      boolean costControlled,
      ComparableQuantity<EnergyPrice> feedInTariff) {
    super(uuid, id, node, qCharacteristics, em);
    this.type = type;
    this.costControlled = costControlled;
    this.feedInTariff = feedInTariff.to(StandardUnits.ENERGY_PRICE);
  }

  @Override
  public BmTypeInput getType() {
    return type;
  }

  public boolean isCostControlled() {
    return costControlled;
  }

  public ComparableQuantity<EnergyPrice> getFeedInTariff() {
    return feedInTariff;
  }

  @Override
  public ComparableQuantity<Power> sRated() {
    return this.type.getsRated();
  }

  public BmInputCopyBuilder copy() {
    return new BmInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof BmInput bmInput)) return false;
    if (!super.equals(o)) return false;
    return costControlled == bmInput.costControlled
        && type.equals(bmInput.type)
        && feedInTariff.equals(bmInput.feedInTariff);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), type, costControlled, feedInTariff);
  }

  @Override
  public String toString() {
    return "BmInput{"
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
        + type.getUuid()
        + ", costControlled="
        + costControlled
        + ", feedInTariff="
        + feedInTariff
        + ", additionalInformation="
        + getAdditionalInformation()
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link BmInput} entities with altered
   * field values. For detailed field descriptions refer to java docs of {@link BmInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class BmInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<BmInputCopyBuilder> {

    private BmTypeInput type;
    private boolean costControlled;
    private ComparableQuantity<EnergyPrice> feedInTariff;

    private BmInputCopyBuilder(BmInput entity) {
      super(entity);
      this.type = entity.getType();
      this.costControlled = entity.isCostControlled();
      this.feedInTariff = entity.getFeedInTariff();
    }

    public BmInputCopyBuilder type(BmTypeInput type) {
      this.type = type;
      return thisInstance();
    }

    public BmInputCopyBuilder costControlled(boolean costControlled) {
      this.costControlled = costControlled;
      return thisInstance();
    }

    public BmInputCopyBuilder feedInTariff(ComparableQuantity<EnergyPrice> feedInTariff) {
      this.feedInTariff = feedInTariff;
      return thisInstance();
    }

    @Override
    public BmInputCopyBuilder scale(Double factor) {
      this.type = this.type.copy().scale(factor).build();
      return thisInstance();
    }

    @Override
    public BmInput build() {
      return new BmInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNode(),
          getqCharacteristics(),
          getEm(),
          type,
          costControlled,
          feedInTariff);
    }

    @Override
    protected BmInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
