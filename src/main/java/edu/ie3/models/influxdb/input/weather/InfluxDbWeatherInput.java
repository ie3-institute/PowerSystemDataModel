/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.influxdb.input.weather;

import edu.ie3.dataconnection.source.csv.CsvCoordinateSource;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.models.influxdb.InfluxDbEntity;
import edu.ie3.util.quantities.interfaces.Irradiation;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import org.locationtech.jts.geom.Point;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Measurement(name = "weather")
public class InfluxDbWeatherInput extends InfluxDbEntity<TimeBasedValue<WeatherValue>> {

  @Column(name = "koordinatenid", tag = true)
  String
      koordinatenId; // MIA gewählt, da kein querverweis auf koordinaten möglich ist + String wegen
  // tag

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

  public InfluxDbWeatherInput() {}

  public InfluxDbWeatherInput(
      Instant time,
      String koordinatenId,
      Double diffusstrahlung,
      Double direkstrahlung,
      Double temperatur,
      Double windrichtung,
      Double windgeschwindigkeit) {
    super(time);
    this.koordinatenId = koordinatenId;
    this.diffusstrahlung = diffusstrahlung;
    this.direktstrahlung = direkstrahlung;
    this.temperatur = temperatur;
    this.windrichtung = windrichtung;
    this.windgeschwindigkeit = windgeschwindigkeit;
  }

  public InfluxDbWeatherInput(TimeBasedValue<WeatherValue> weather) {
    this.time = weather.getTime().toInstant();
    WeatherValue values = weather.getValue();
    this.koordinatenId = CsvCoordinateSource.getId(values.getCoordinate()).toString();
    ComparableQuantity<Irradiation> diffuseIrradiation =
        weather.getValue().getIrradiation().getDiffuseIrradiation();
    this.diffusstrahlung = diffuseIrradiation.getValue().doubleValue();
    ComparableQuantity<Irradiation> directIrradiation =
        weather.getValue().getIrradiation().getDirectIrradiation();
    this.direktstrahlung = directIrradiation.getValue().doubleValue();
    ComparableQuantity<Temperature> temperature = values.getTemperature().getTemperature();
    this.temperatur = temperature.getValue().doubleValue();
    ComparableQuantity<Angle> direction = values.getWind().getDirection();
    this.windrichtung = direction.getValue().doubleValue();
    ComparableQuantity<Speed> velocity = values.getWind().getVelocity();
    this.windgeschwindigkeit = velocity.getValue().doubleValue();
  }

  public TimeBasedValue<WeatherValue> toTimeBasedWeatherValue() {
    ZonedDateTime dateTime = time.atZone(ZoneId.of("UTC"));
    Point geometry = CsvCoordinateSource.getCoordinate(Integer.parseInt(koordinatenId));
    ComparableQuantity<Irradiation> diffuseIrradiation =
        Quantities.getQuantity(diffusstrahlung, StandardUnits.IRRADIATION);
    ComparableQuantity<Irradiation> directIrradiation =
        Quantities.getQuantity(direktstrahlung, StandardUnits.IRRADIATION);
    ComparableQuantity<Temperature> temperature =
        Quantities.getQuantity(temperatur, StandardUnits.TEMPERATURE);
    ComparableQuantity<Angle> direction = Quantities.getQuantity(windrichtung, StandardUnits.WIND_DIRECTION);
    ComparableQuantity<Speed> velocity =
        Quantities.getQuantity(windgeschwindigkeit, StandardUnits.WIND_VELOCITY);
    return new TimeBasedValue<>(
        dateTime,
        new WeatherValue(
            geometry, diffuseIrradiation, directIrradiation, temperature, direction, velocity));
  }
}
