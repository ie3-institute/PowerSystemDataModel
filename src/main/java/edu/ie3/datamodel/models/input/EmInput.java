/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input;

import edu.ie3.datamodel.io.extractor.HasEm;
import edu.ie3.datamodel.models.OperationTime;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Describes the type of {@link edu.ie3.datamodel.models.input.AssetInput}. */
public class EmInput extends AssetInput implements HasEm {
  /** Reference to the control strategy to be used for this model. */
  private final String controlStrategy;

  /**
   * Optional UUID of the parent {@link EmInput} that is controlling this em unit. If null, this em
   * unit is not em-controlled.
   */
  private final EmInput controllingEm;

  /**
   * Constructor for an operated energy management system.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime time for which the entity is operated
   * @param controlStrategy the control strategy
   * @param controllingEm The {@link EmInput} controlling this em unit. Null, if not applicable.
   */
  public EmInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      String controlStrategy,
      EmInput controllingEm) {
    super(uuid, id, operator, operationTime);
    this.controlStrategy = controlStrategy;
    this.controllingEm = controllingEm;
  }

  /**
   * Constructor for an operated energy management system.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param operator of the asset
   * @param operationTime time for which the entity is operated
   * @param controlStrategy the control strategy
   * @param controllingEm The {@link EmInput} controlling this em unit. Null, if not applicable.
   * @param additionalInformation That were provided by the source
   */
  public EmInput(
      UUID uuid,
      String id,
      OperatorInput operator,
      OperationTime operationTime,
      String controlStrategy,
      EmInput controllingEm,
      Map<String, String> additionalInformation) {
    super(uuid, id, operator, operationTime);
    this.controlStrategy = controlStrategy;
    this.controllingEm = controllingEm;
    setAdditionalInformation(additionalInformation);
  }

  /**
   * Constructor for an operated energy management system.
   *
   * @param uuid of the input entity
   * @param id of the asset
   * @param controlStrategy the control strategy
   * @param controllingEm The {@link EmInput} controlling this em unit. Null, if not applicable.
   */
  public EmInput(UUID uuid, String id, String controlStrategy, EmInput controllingEm) {
    super(uuid, id);
    this.controlStrategy = controlStrategy;
    this.controllingEm = controllingEm;
  }

  public String getControlStrategy() {
    return controlStrategy;
  }

  public Optional<EmInput> getControllingEm() {
    return Optional.ofNullable(controllingEm);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EmInput that)) return false;
    if (!super.equals(o)) return false;
    return Objects.equals(controlStrategy, that.controlStrategy)
        && Objects.equals(controllingEm, that.controllingEm);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), controlStrategy, controllingEm);
  }

  @Override
  public String toString() {
    return "EmInput{"
        + "uuid="
        + getUuid()
        + ", id="
        + getId()
        + ", operator="
        + getOperator().getUuid()
        + ", operationTime="
        + getOperationTime()
        + ", controlStrategy="
        + controlStrategy
        + ", controllingEm="
        + getControllingEm().map(e -> e.getUuid().toString()).orElse("")
        + ", additionalInformation="
        + getAdditionalInformation()
        + "}";
  }

  @Override
  public EmInputCopyBuilder copy() {
    return new EmInputCopyBuilder(this);
  }

  public static class EmInputCopyBuilder extends AssetInputCopyBuilder<EmInputCopyBuilder> {
    private String controlStrategy;

    private EmInput controllingEm;

    protected EmInputCopyBuilder(EmInput entity) {
      super(entity);
      this.controlStrategy = entity.controlStrategy;
      this.controllingEm = entity.controllingEm;
    }

    public EmInputCopyBuilder controlStrategy(String controlStrategy) {
      this.controlStrategy = controlStrategy;
      return thisInstance();
    }

    protected String getControlStrategy() {
      return controlStrategy;
    }

    public EmInputCopyBuilder controllingEm(EmInput controllingEm) {
      this.controllingEm = controllingEm;
      return thisInstance();
    }

    protected EmInput getControllingEm() {
      return controllingEm;
    }

    @Override
    public EmInput build() {
      return new EmInput(
          getUuid(),
          getId(),
          getOperator(),
          getOperationTime(),
          controlStrategy,
          controllingEm,
          getAdditionalInformation());
    }

    @Override
    protected EmInputCopyBuilder thisInstance() {
      return this;
    }
  }
}
