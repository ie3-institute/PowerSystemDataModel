/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input;

import edu.ie3.models.Operable;
import edu.ie3.util.interval.ClosedInterval;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Describes a grid asset under the assumption that every asset could be operable */
public abstract class AssetInput extends InputEntity implements Operable {
  /** Empty, if the asset is not operated, or the operation period interval else */
  private Optional<ClosedInterval<ZonedDateTime>> operationInterval;
  /** The operator of this asset */
  private OperatorInput operator;
  /** Name or ID of the asset */
  private String id;

  /**
   * Constructor for an operated asset. An empty Optional indicates a non-operated asset. <br>
   * Please use {@link AssetInput#AssetInput(UUID, Optional, Optional, OperatorInput, String)} if
   * either start or end date are unknown.
   */
  public AssetInput(
      UUID uuid,
      Optional<ClosedInterval<ZonedDateTime>> operationInterval,
      OperatorInput operator,
      String id) {
    super(uuid);
    this.operationInterval = operationInterval;
    this.operator = operator;
    this.id = id;
  }

  /**
   * Constructor for an operated asset, where either start or end date may be unknown. If both dates
   * are empty, the constructor creates an empty Optional for the operationInterval, meaning that
   * the asset is non-operated.<br>
   * If one of the dates is empty, it will respectively be replaced by a min or max date.
   */
  public AssetInput(
      UUID uuid,
      Optional<ZonedDateTime> operatesFrom,
      Optional<ZonedDateTime> operatesUntil,
      OperatorInput operator,
      String id) {
    super(uuid);
    if (operatesFrom.isPresent() || operatesUntil.isPresent()) {
      ZonedDateTime from =
          operatesFrom.orElse(ZonedDateTime.of(LocalDateTime.MIN, ZoneId.of("UTC")));
      ZonedDateTime until =
          operatesFrom.orElse(ZonedDateTime.of(LocalDateTime.MAX, ZoneId.of("UTC")));
      operationInterval = Optional.of(new ClosedInterval<>(from, until));
    } else operationInterval = Optional.empty();
    this.operator = operator;
    this.id = id;
  }

  /** Constructor for a non-operated asset */
  public AssetInput(UUID uuid, String id) {
    this(uuid, Optional.empty(), null, id);
  }

  /** @return Empty if asset is non-operated, operationInterval else */
  @Override
  public Optional<ClosedInterval<ZonedDateTime>> getOperationInterval() {
    return operationInterval;
  }

  @Override
  public void setOperationInterval(Optional<ClosedInterval<ZonedDateTime>> operationInterval) {
    this.operationInterval = operationInterval;
  }

  @Override
  public void setOperationInterval(ClosedInterval<ZonedDateTime> operationInterval) {
    this.operationInterval = Optional.ofNullable(operationInterval);
  }

  /**
   * @return true if asset is non-operated or operated and the given date is in the operation
   *     period, else false
   */
  @Override
  public boolean inInterval(ZonedDateTime date) {
    if (!operationInterval.isPresent()) return true;
    return operationInterval.filter(i -> i.includes(date)).isPresent();
  }

  @Override
  public void setOperator(OperatorInput operator) {
    this.operator = operator;
  }

  @Override
  public OperatorInput getOperator() {
    return operator;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    AssetInput that = (AssetInput) o;
    return Objects.equals(operationInterval, that.operationInterval)
        && Objects.equals(operator, that.operator)
        && Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), operationInterval, operator, id);
  }
}
