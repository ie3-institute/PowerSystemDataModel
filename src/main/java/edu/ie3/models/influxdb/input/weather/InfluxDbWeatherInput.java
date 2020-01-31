/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.influxdb.input.weather;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.dataconnection.source.CsvCoordinateSource;
import edu.ie3.models.StandardUnits;
import edu.ie3.models.influxdb.InfluxDbEntity;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.quantities.interfaces.Irradiation;
import org.influxdb.annotation.Column;
import org.influxdb.annotation.Measurement;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Measurement(name = "weather")
public class InfluxDbWeatherInput extends InfluxDbEntity<TimeBasedValue<WeatherValues>> {

  @Column(name = "koordinatenid", tag=true)
  String koordinatenId; // MIA gewählt, da kein querverweis auf koordinaten möglich ist + String wegen tag

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

  public InfluxDbWeatherInput(TimeBasedValue<WeatherValues> weather) {
    this.time = weather.getTime().toInstant();
    WeatherValues values = weather.getValue();
    this.koordinatenId = CsvCoordinateSource.getId(values.getCoordinate()).toString();
    Quantity<Irradiation> diffuseIrradiation =
        weather.getValue().getIrradiation().getDiffuseIrradiation();
    this.diffusstrahlung = diffuseIrradiation.getValue().doubleValue();
    Quantity<Irradiation> directIrradiation = weather.getValue().getIrradiation().getDirectIrradiation();
    this.direktstrahlung = directIrradiation.getValue().doubleValue();
    Quantity<Temperature> temperature = values.getTemperature().getTemperature();
    this.temperatur = temperature.getValue().doubleValue();
    Quantity<Angle> direction = values.getWind().getDirection();
    this.windrichtung = direction.getValue().doubleValue();
    Quantity<Speed> velocity = values.getWind().getVelocity();
    this.windgeschwindigkeit = velocity.getValue().doubleValue();
  }

  public TimeBasedValue<WeatherValues> toTimeBasedWeatherValues() {
    ZonedDateTime dateTime = time.atZone(ZoneId.of("UTC"));
    Point geometry = CsvCoordinateSource.getCoordinate(Integer.parseInt(koordinatenId));
    Quantity<Irradiation> diffuseIrradiation =
        Quantities.getQuantity(diffusstrahlung, StandardUnits.IRRADIATION);
    Quantity<Irradiation> directIrradiation =
        Quantities.getQuantity(direktstrahlung, StandardUnits.IRRADIATION);
    Quantity<Temperature> temperature = Quantities.getQuantity(temperatur, StandardUnits.TEMPERATURE);
    Quantity<Angle> direction = Quantities.getQuantity(windrichtung, StandardUnits.WIND_DIRECTION);
    Quantity<Speed> velocity =
        Quantities.getQuantity(windgeschwindigkeit, StandardUnits.WIND_VELOCITY);
    return new TimeBasedValue<>(
        dateTime,
        new WeatherValues(
            geometry, diffuseIrradiation, directIrradiation, temperature, direction, velocity));
  }
}
