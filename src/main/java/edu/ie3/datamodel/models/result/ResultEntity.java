/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result;

import edu.ie3.datamodel.models.Entity;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/** Abstract class to hold all mappings common to all result models. */
public abstract class ResultEntity implements Entity {
  /** Date and time of the produced result. */
  private ZonedDateTime time;

  /** Uuid of the input model that produces the result. */
  private UUID inputModel;

  /**
   * Standard constructor for a result entity.
   *
   * @param time Date and time when the result is produced.
   * @param inputModel The UUID of the input model that produces the result.
   */
  protected ResultEntity(ZonedDateTime time, UUID inputModel) {
    this.time = time;
    this.inputModel = inputModel;
  }

  public ZonedDateTime getTime() {
    return time;
  }

  public UUID getInputModel() {
    return inputModel;
  }

  public void setTime(ZonedDateTime time) {
    this.time = time;
  }

  public void setInputModel(UUID inputModel) {
    this.inputModel = inputModel;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ResultEntity that)) return false;
    return Objects.equals(time, that.time) && Objects.equals(inputModel, that.inputModel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(time, inputModel);
  }

  @Override
  public String toString() {
    return "ResultEntity{" + "time=" + time + ", inputModel=" + inputModel + "}";
  }
}
