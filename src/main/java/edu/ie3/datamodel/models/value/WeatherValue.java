/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import com.vividsolutions.jts.geom.Point;
import edu.ie3.util.quantities.interfaces.Irradiation;
import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;

/** Describes weather as a combination of irradiation, temperature and wind values */
public class WeatherValue implements Value {
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
  public WeatherValue(
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
  public WeatherValue(
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

  public IrradiationValue getirradiation() {
    return irradiation;
  }

  public void setirradiation(IrradiationValue irradiation) {
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
  public String toString() {
    return "WeatherValue{"
        + "coordinate="
        + coordinate
        + ", irradiation="
        + irradiation
        + ", temperature="
        + temperature
        + ", wind="
        + wind
        + '}';
  }
}
