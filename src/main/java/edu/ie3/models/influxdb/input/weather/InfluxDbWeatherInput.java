/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.influxdb.input.weather;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.models.influxdb.InfluxDbEntity;
import edu.ie3.models.value.TimeBasedValue;
import edu.ie3.models.value.WeatherValues;
import edu.ie3.util.quantities.PowerSystemUnits;
import edu.ie3.util.quantities.interfaces.PowerDensity;
import edu.ie3.utils.CoordinateTools;
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

  @Column(name = "lat", tag=true)
  String lat; // MIA gewählt, da kein querverweis auf koordinaten möglich ist + String wegen tag

  @Column(name = "lon", tag=true)
  String lon;

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
      Double lat,
      Double lon,
      Double diffusstrahlung,
      Double direkstrahlung,
      Double temperatur,
      Double windrichtung,
      Double windgeschwindigkeit) {
    super(time);
    this.lat = lat.toString();
    this.lon = lon.toString();
    this.diffusstrahlung = diffusstrahlung;
    this.direktstrahlung = direkstrahlung;
    this.temperatur = temperatur;
    this.windrichtung = windrichtung;
    this.windgeschwindigkeit = windgeschwindigkeit;
  }

  public InfluxDbWeatherInput(TimeBasedValue<WeatherValues> weather) {
    this.time = weather.getTime().toInstant();
    WeatherValues values = weather.getValue();
    this.lat = String.valueOf(values.getCoordinate().getX());
    this.lon = String.valueOf(values.getCoordinate().getY());
    Quantity<PowerDensity> diffuseRadiation =
        weather.getValue().getRadiation().getDiffuseRadiation();
    this.diffusstrahlung = diffuseRadiation.getValue().doubleValue();
    Quantity<PowerDensity> directRadiation = weather.getValue().getRadiation().getDirectRadiation();
    this.direktstrahlung = directRadiation.getValue().doubleValue();
    Quantity<Temperature> temperature = values.getTemperature().get();
    this.temperatur = temperature.getValue().doubleValue();
    Quantity<Angle> direction = values.getWind().getDirection();
    this.windrichtung = direction.getValue().doubleValue();
    Quantity<Speed> velocity = values.getWind().getVelocity();
    this.windgeschwindigkeit = velocity.getValue().doubleValue();
  }

  public TimeBasedValue<WeatherValues> toTimeBasedWeatherValues() {
    ZonedDateTime dateTime = time.atZone(ZoneId.of("UTC"));
    Point geometry = CoordinateTools.xyCoordToPoint(Double.parseDouble(lat), Double.parseDouble(lon));
    Quantity<PowerDensity> diffuseRadiation =
        Quantities.getQuantity(diffusstrahlung, PowerSystemUnits.WATT_PER_SQUAREMETRE);
    Quantity<PowerDensity> directRadiation =
        Quantities.getQuantity(direktstrahlung, PowerSystemUnits.WATT_PER_SQUAREMETRE);
    Quantity<Temperature> temperature = Quantities.getQuantity(temperatur, PowerSystemUnits.KELVIN);
    Quantity<Angle> direction = Quantities.getQuantity(windrichtung, PowerSystemUnits.RADIAN);
    Quantity<Speed> velocity =
        Quantities.getQuantity(windgeschwindigkeit, PowerSystemUnits.METRE_PER_SECOND);
    return new TimeBasedValue<>(
        dateTime,
        new WeatherValues(
            geometry, diffuseRadiation, directRadiation, temperature, direction, velocity));
  }
}
