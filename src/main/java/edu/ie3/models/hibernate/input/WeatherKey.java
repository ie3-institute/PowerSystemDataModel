/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.hibernate.input;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

public class WeatherKey implements Serializable {

  private Integer coordinate;
  private ZonedDateTime date;

  public WeatherKey(Integer coordinate, ZonedDateTime date) {
    this.coordinate = coordinate;
    this.date = date;
  }

  public WeatherKey() {}

  public Integer getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(Integer coordinate) {
    this.coordinate = coordinate;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public void setDate(ZonedDateTime date) {
    this.date = date;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WeatherKey that = (WeatherKey) o;
    return Objects.equals(coordinate, that.coordinate) && Objects.equals(date, that.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(coordinate, date);
  }
}
