/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.value;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.util.quantities.interfaces.Irradiation;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import java.util.Objects;

/** Describes weather as a combination of irradiation, temperature and wind values */
public class WeatherValues implements Value {
  /** The coordinate of this weather value set */
  private Point coordinate;
  /** irradiation values for this coordinate */
  private IrradiationValue irradiation;
  /** Temperature value for this coordinate */
  private TemperatureValue temperature;
  /** Wind values for this coordinate */
  private WindValue wind;

  /**
   * @param coordinate of this weather value set
   * @param irradiation values for this coordinate
   * @param temperature values for this coordinate
   * @param wind values for this coordinate
   */
  public WeatherValues(
      Point coordinate,
      IrradiationValue irradiation,
      TemperatureValue temperature,
      WindValue wind) {
    this.coordinate = coordinate;
    this.irradiation = irradiation;
    this.temperature = temperature;
    this.wind = wind;
  }

  /**
   * @param coordinate of this weather value set
   * @param directirradiation Direct sun irradiation for this coordinate (typically in W/m²)
   * @param diffuseirradiation Diffuse sun irradiation for this coordinate (typically in W/m²)
   * @param temperature for this coordinate (typically in K)
   * @param direction Wind direction as an angle from north for this coordinate (typically in rad)
   * @param velocity Wind velocity for this coordinate (typically in m/s)
   */
  public WeatherValues(
      Point coordinate,
      Quantity<Irradiation> directirradiation,
      Quantity<Irradiation> diffuseirradiation,
      Quantity<Temperature> temperature,
      Quantity<Angle> direction,
      Quantity<Speed> velocity) {
    this(
        coordinate,
        new IrradiationValue(directirradiation, diffuseirradiation),
        new TemperatureValue(temperature),
        new WindValue(direction, velocity));
  }

  public Point getCoordinate() {
    return coordinate;
  }

  public void setCoordinate(Point coordinate) {
    this.coordinate = coordinate;
  }

  public IrradiationValue getIrradiation() {
    return irradiation;
  }

  public void setIrradiation(IrradiationValue irradiation) {
    this.irradiation = irradiation;
  }

  public TemperatureValue getTemperature() {
    return temperature;
  }

  public void setTemperature(TemperatureValue temperature) {
    this.temperature = temperature;
  }

  public WindValue getWind() {
    return wind;
  }

  public void setWind(WindValue wind) {
    this.wind = wind;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof WeatherValues)) return false;
    WeatherValues that = (WeatherValues) o;
    return Objects.equals(coordinate, that.coordinate) &&
            irradiation.equals(that.irradiation) &&
            temperature.equals(that.temperature) &&
            wind.equals(that.wind);
  }

  @Override
  public int hashCode() {
    return Objects.hash(coordinate, irradiation, temperature, wind);
  }
}
