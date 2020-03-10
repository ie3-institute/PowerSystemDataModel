/*
 * © 2020. TU Dortmund University,
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
  private ZonedDateTime timestamp;
  /** uuid of the input model that produces the result */
  private UUID inputModel;

  /**
   * Standard constructor which includes auto generation of the resulting output models uuid.
   *
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   */
  public ResultEntity(ZonedDateTime timestamp, UUID inputModel) {
    super();
    this.timestamp = timestamp;
    this.inputModel = inputModel;
  }

  /**
   * Standard constructor without uuid generation.
   *
   * @param uuid uuid of this result entity, for automatic uuid generation use primary constructor
   *     above
   * @param timestamp date and time when the result is produced
   * @param inputModel uuid of the input model that produces the result
   */
  public ResultEntity(UUID uuid, ZonedDateTime timestamp, UUID inputModel) {
    super(uuid);
    this.timestamp = timestamp;
    this.inputModel = inputModel;
  }

  public UUID getInputModel() {
    return inputModel;
  }

  public void setInputModel(UUID inputID) {
    inputModel = inputID;
  }

  public ZonedDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(ZonedDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ResultEntity that = (ResultEntity) o;
    return timestamp.equals(that.timestamp) && inputModel.equals(that.inputModel);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), timestamp, inputModel);
  }
}
