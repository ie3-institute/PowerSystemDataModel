/*
 * © 2024. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/** Abstract class to hold all mappings common to all input result models */
public abstract class ModelResultEntity extends ResultEntity {

  /** uuid of the input model that produces the result */
  private UUID inputModel;

  /**
   * Standard constructor which includes auto generation of the resulting output models uuid.
   *
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   */
  protected ModelResultEntity(ZonedDateTime time, UUID inputModel) {
    super(time);
    this.inputModel = inputModel;
  }

  public UUID getInputModel() {
    return inputModel;
  }

  public void setInputModel(UUID inputID) {
    inputModel = inputID;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ModelResultEntity that = (ModelResultEntity) o;
    return getTime().equals(that.getTime()) && inputModel.equals(that.inputModel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), getTime(), inputModel);
  }

  @Override
  public String toString() {
    return "InputResultEntity{time=" + getTime() + ", inputModel=" + inputModel + '}';
  }
}
