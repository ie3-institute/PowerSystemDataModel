/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result;

import edu.ie3.datamodel.models.UniqueEntity;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/** Abstract class to hold all mappings common to all result models */
public abstract class ResultEntity extends UniqueEntity {

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
    super();
    this.time = time;
    this.inputModel = inputModel;
  }

  /**
   * Standard constructor without uuid generation.
   *
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param time date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   */
  protected ResultEntity(UUID uuid, ZonedDateTime time, UUID inputModel) {
    super(uuid);
    this.time = time;
    this.inputModel = inputModel;
  }

  public UUID getInputModel() {
    return inputModel;
  }

  public void setInputModel(UUID inputID) {
    inputModel = inputID;
  }

  public ZonedDateTime getTime() {
    return time;
  }

  public void setTime(ZonedDateTime time) {
    this.time = time;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ResultEntity that = (ResultEntity) o;
    return time.equals(that.time) && inputModel.equals(that.inputModel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), time, inputModel);
  }

  @Override
  public String toString() {
    return "ResultEntity{"
        + "uuid="
        + getUuid()
        + ", time="
        + time
        + ", inputModel="
        + inputModel
        + '}';
  }
}
