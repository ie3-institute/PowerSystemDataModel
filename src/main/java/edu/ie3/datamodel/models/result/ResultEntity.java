/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result;

import edu.ie3.datamodel.models.Entity;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/** Abstract class to hold all mappings common to all result models */
public abstract class ResultEntity implements Entity {

  /** date and time of the produced result */
  private ZonedDateTime time;

  /** uuid of the input model that produces the result */
  private UUID inputModel;

  /**
   * Standard constructor which includes auto generation of the resulting output models uuid.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   */
  protected ResultEntity(ZonedDateTime time, UUID inputModel) {
    this.time = time;
    this.inputModel = inputModel;
  }

  /**
   * Gets input model.
   *
   * @return the input model
   */
  public UUID getInputModel() {
    return inputModel;
  }

  /**
   * Sets input model.
   *
   * @param inputID the input id
   */
  public void setInputModel(UUID inputID) {
    inputModel = inputID;
  }

  /**
   * Gets time.
   *
   * @return the time
   */
  public ZonedDateTime getTime() {
    return time;
  }

  /**
   * Sets time.
   *
   * @param time the time
   */
  public void setTime(ZonedDateTime time) {
    this.time = time;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ResultEntity that = (ResultEntity) o;
    return time.equals(that.time);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), time);
  }

  @Override
  public String toString() {
    return "ResultEntity{time=" + time + '}';
  }
}
