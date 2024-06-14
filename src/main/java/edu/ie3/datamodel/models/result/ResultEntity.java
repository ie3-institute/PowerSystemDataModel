/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.result;

import edu.ie3.datamodel.models.Entity;
import java.time.ZonedDateTime;
import java.util.Objects;

/** Abstract class to hold all mappings common to all result models */
public abstract class ResultEntity implements Entity {

  /** date and time of the produced result */
  private ZonedDateTime time;

  /**
   * Standard constructor which includes auto generation of the resulting output models uuid.
   *
   * @param time date and time when the result is produced
   */
  protected ResultEntity(ZonedDateTime time) {
    this.time = time;
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
