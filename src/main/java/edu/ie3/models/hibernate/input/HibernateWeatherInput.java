/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.hibernate.input;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.dataconnection.source.csv.CsvCoordinateSource;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.quantities.interfaces.Irradiation;
import java.io.Serializable;
import java.time.ZonedDateTime;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import javax.persistence.*;
import tec.uom.se.quantity.Quantities;

@NamedQueries({
  @NamedQuery(
      name = "HibernateWeatherInput.WeatherInInterval",
      query = "SELECT w FROM weather w WHERE datum BETWEEN ?1 AND ?2"),
  @NamedQuery(
      name = "HibernateWeatherInput.WeatherWithCoordinateInInterval",
      query = "SELECT w FROM weather w WHERE koordinatenid = ?1 AND datum BETWEEN ?2 AND ?3"),
  @NamedQuery(
      name = "HibernateWeatherInput.WeatherWithCoordinateAndDate",
      query = "SELECT w FROM weather w WHERE koordinatenid = ?1 AND datum = ?2"),
  @NamedQuery(
      name = "HibernateWeatherInput.WeatherWithMultipleCoordinatesInInterval",
      query = "SELECT w FROM weather w WHERE koordinatenid IN ?1 AND datum BETWEEN ?2 AND ?3")
})
@Entity(name = "weather")
@IdClass(WeatherKey.class)
public class HibernateWeatherInput implements Serializable {

  @Id
  @Column(name = "datum", nullable = false)
  private ZonedDateTime date;

  @Id
  @Column(name = "koordinatenid")
  private Integer coordinate;

  @Column(name = "diffusstrahlung")
  Double diffusstrahlung;

  @Column(name = "direktstrahlung")
  Double direktstrahlung;

  @Column(name = "temperatur")
  Double temperatur;

  @Column(name = "windrichtung")
  Double windrichtung;

  @Column(name = "windgeschwindigkeit")
  Double windgeschwindigkeit;

  public HibernateWeatherInput() {}

  public HibernateWeatherInput(
      ZonedDateTime date,
      Integer koordinatenId,
      Double diffusstrahlung,
      Double direkstrahlung,
      Double temperatur,
      Double windrichtung,
      Double windgeschwindigkeit) {
    this.date = date;
    this.coordinate = koordinatenId;
    this.diffusstrahlung = diffusstrahlung;
    this.direktstrahlung = direkstrahlung;
    this.temperatur = temperatur;
    this.windrichtung = windrichtung;
    this.windgeschwindigkeit = windgeschwindigkeit;
  }

  public HibernateWeatherInput(TimeBasedValue<WeatherValues> weather) {
    this.date = weather.getTime();
    WeatherValues values = weather.getValue();
    this.coordinate = CsvCoordinateSource.getId(values.getCoordinate());
    Quantity<Irradiation> diffuseIrradiation =
        weather.getValue().getIrradiation().getDiffuseIrradiation();
    this.diffusstrahlung = diffuseIrradiation.getValue().doubleValue();
    Quantity<Irradiation> directIrradiation =
        weather.getValue().getIrradiation().getDirectIrradiation();
    this.direktstrahlung = directIrradiation.getValue().doubleValue();
    Quantity<Temperature> temperature = values.getTemperature().getTemperature();
    this.temperatur = temperature.getValue().doubleValue();
    Quantity<Angle> direction = values.getWind().getDirection();
    this.windrichtung = direction.getValue().doubleValue();
    Quantity<Speed> velocity = values.getWind().getVelocity();
    this.windgeschwindigkeit = velocity.getValue().doubleValue();
  }

  public TimeBasedValue<WeatherValues> toTimeBasedWeatherValues() {
    Point geometry = CsvCoordinateSource.getCoordinate(coordinate);
    Quantity<Irradiation> diffuseIrradiation =
        Quantities.getQuantity(diffusstrahlung, StandardUnits.IRRADIATION);
    Quantity<Irradiation> directIrradiation =
        Quantities.getQuantity(direktstrahlung, StandardUnits.IRRADIATION);
    Quantity<Temperature> temperature =
        Quantities.getQuantity(temperatur, StandardUnits.TEMPERATURE);
    Quantity<Angle> direction = Quantities.getQuantity(windrichtung, StandardUnits.WIND_DIRECTION);
    Quantity<Speed> velocity =
        Quantities.getQuantity(windgeschwindigkeit, StandardUnits.WIND_VELOCITY);
    return new TimeBasedValue<>(
        date,
        new WeatherValues(
            geometry, diffuseIrradiation, directIrradiation, temperature, direction, velocity));
  }
}
