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
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/** Dummy class to represent a constant feed in regardless of its type. */
public class FixedFeedInInput extends SystemParticipantInput {
  /** Rated apparent power (typically in kVA). */
  private final ComparableQuantity<Power> sRated;

  /** Rated power factor. */
  private final double cosPhiRated;

  /**
   * Constructor for an operated feed in.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param sRated Rated apparent power
   * @param cosPhiRated Power factor
   */
  public FixedFeedInInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.sRated = sRated;
    this.cosPhiRated = cosPhiRated;
  }

  /**
   * Constructor for an operated feed in.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime Time for which the entity is operated
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param sRated Rated apparent power
   * @param cosPhiRated Power factor
   * @param additionalInformation That were provided by the source
   */
  public FixedFeedInInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      ComparableQuantity<Power> sRated,
      double cosPhiRated,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime, node, qCharacteristics, controllingEm);
    this.sRated = sRated;
    this.cosPhiRated = cosPhiRated;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated feed in.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param node the asset is connected to
   * @param qCharacteristics Description of a reactive power characteristic
   * @param controllingEm The {@link EmInput} controlling this system participant. Null, if not
   *     applicable.
   * @param sRated Rated apparent power
   * @param cosPhiRated Power factor
   */
  public FixedFeedInInput(
      UUID uuid,
      String id,
      NodeInput node,
      ReactivePowerCharacteristic qCharacteristics,
      EmInput controllingEm,
      ComparableQuantity<Power> sRated,
      double cosPhiRated) {
    super(uuid, id, node, qCharacteristics, controllingEm);
    this.sRated = sRated;
    this.cosPhiRated = cosPhiRated;
  }

  public ComparableQuantity<Power> getsRated() {
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
    if (!(o instanceof FixedFeedInInput that)) return false;
    if (!super.equals(o)) return false;
    return sRated.equals(that.sRated) && cosPhiRated == that.cosPhiRated;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), sRated, cosPhiRated);
  }

  @Override
  public String toString() {
    return "FixedFeedInInput{"
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
        + ", sRated="
        + sRated
        + ", cosPhiRated="
        + cosPhiRated
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public FixedFeedInInputCopyBuilder copy() {
    return new FixedFeedInInputCopyBuilder(this);
  }

  public static class FixedFeedInInputCopyBuilder
      extends SystemParticipantInputCopyBuilder<FixedFeedInInputCopyBuilder> {
    private ComparableQuantity<Power> sRated;

    private double cosPhiRated;

    protected FixedFeedInInputCopyBuilder(FixedFeedInInput entity) {
      super(entity);
      this.sRated = entity.sRated;
      this.cosPhiRated = entity.cosPhiRated;
    }

    public FixedFeedInInputCopyBuilder sRated(ComparableQuantity<Power> sRated) {
      this.sRated = sRated;
      return thisInstance();
    }

    protected ComparableQuantity<Power> getsRated() {
      return sRated;
    }

    public FixedFeedInInputCopyBuilder cosPhiRated(double cosPhiRated) {
      this.cosPhiRated = cosPhiRated;
      return thisInstance();
    }

    protected double getCosPhiRated() {
      return cosPhiRated;
    }

    @Override
    public FixedFeedInInputCopyBuilder scale(double factor) {
      sRated(sRated.multiply(factor));
      return thisInstance();
    }

    @Override
    public FixedFeedInInput build() {
      return new FixedFeedInInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          getNode(),
          getqCharacteristics(),
          getControllingEm(),
          sRated,
          cosPhiRated,
          getAdditionalInformation());
    }

    @Override
    protected FixedFeedInInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
