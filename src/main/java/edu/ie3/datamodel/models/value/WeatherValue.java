/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.util.quantities.interfaces.Irradiance;
import java.util.Objects;
import java.util.Optional;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;

/** Describes weather as a combination of solar irradiance, temperature and wind values */
public class WeatherValue implements Value {
  /** The coordinate of this weather value set */
  private final Point coordinate;

  /** solar irradiance values for this coordinate */
  private final SolarIrradianceValue solarIrradiance;

  /** Temperature value for this coordinate */
  private final TemperatureValue temperature;

  /** Wind values for this coordinate */
  private final WindValue wind;

  /** Ground temperature value at 0cm depth for this coordinate */
  private final GroundTemperatureValue groundTemperatureValueOne;

  /** Ground temperature value at 80cm depth for this coordinate */
  private final GroundTemperatureValue groundTemperatureValueTwo;

  /**
   * @param coordinate of this weather value set
   * @param solarIrradiance values for this coordinate
   * @param temperature values for this coordinate
   * @param wind values for this coordinate
   * @param groundTemperatureValueOne values for this coordinate (can be null)
   * @param groundTemperatureValueTwo values for this coordinate (can be null)
   */
  public WeatherValue(
      Point coordinate,
      SolarIrradianceValue solarIrradiance,
      TemperatureValue temperature,
      WindValue wind,
      GroundTemperatureValue groundTemperatureValueOne,
      GroundTemperatureValue groundTemperatureValueTwo) {
    this.coordinate = coordinate;
    this.solarIrradiance = solarIrradiance;
    this.temperature = temperature;
    this.wind = wind;
    this.groundTemperatureValueOne = groundTemperatureValueOne;
    this.groundTemperatureValueTwo = groundTemperatureValueTwo;
  }

  /**
   * Constructor with all parameters as quantities.
   *
   * @param coordinate of this weather value set
   * @param directSolarIrradiance Direct sun irradiance for this coordinate (typically in W/m²)
   * @param diffuseSolarIrradiance Diffuse sun irradiance for this coordinate (typically in W/m²)
   * @param temperature for this coordinate (typically in K)
   * @param direction Direction, the wind comes from as an angle from north increasing clockwise
   *     (typically in rad)
   * @param velocity Wind velocity for this coordinate (typically in m/s)
   * @param groundTempValOne Ground temperature at 0cm for this coordinate (typically in K, can be
   *     null)
   * @param groundTempValTwo Ground temperature at 80cm for this coordinate (typically in K, can be
   *     null)
   */
  public WeatherValue(
      Point coordinate,
      ComparableQuantity<Irradiance> directSolarIrradiance,
      ComparableQuantity<Irradiance> diffuseSolarIrradiance,
      ComparableQuantity<Temperature> temperature,
      ComparableQuantity<Angle> direction,
      ComparableQuantity<Speed> velocity,
      ComparableQuantity<Temperature> groundTempValOne,
      ComparableQuantity<Temperature> groundTempValTwo) {
    this(
        coordinate,
        new SolarIrradianceValue(directSolarIrradiance, diffuseSolarIrradiance),
        new TemperatureValue(temperature),
        new WindValue(direction, velocity),
        groundTempValOne == null ? null : new GroundTemperatureValue(groundTempValOne),
        groundTempValTwo == null ? null : new GroundTemperatureValue(groundTempValTwo));
  }

  public Point getCoordinate() {
    return coordinate;
  }

  public SolarIrradianceValue getSolarIrradiance() {
    return solarIrradiance;
  }

  public TemperatureValue getTemperature() {
    return temperature;
  }

  public WindValue getWind() {
    return wind;
  }

  public Optional<GroundTemperatureValue> getGroundTemperatureValueOne() {
    return Optional.ofNullable(groundTemperatureValueOne);
  }

  public Optional<GroundTemperatureValue> getGroundTemperatureValueTwo() {
    return Optional.ofNullable(groundTemperatureValueTwo);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    WeatherValue that = (WeatherValue) o;
    return coordinate.equals(that.coordinate)
        && solarIrradiance.equals(that.solarIrradiance)
        && temperature.equals(that.temperature)
        && wind.equals(that.wind)
        && Objects.equals(groundTemperatureValueOne, that.groundTemperatureValueOne)
        && Objects.equals(groundTemperatureValueTwo, that.groundTemperatureValueTwo);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        coordinate,
        solarIrradiance,
        temperature,
        wind,
        groundTemperatureValueOne,
        groundTemperatureValueTwo);
  }

  @Override
  public String toString() {
    return "WeatherValue{"
        + "coordinate="
        + coordinate
        + ", solarIrradiance="
        + solarIrradiance
        + ", temperature="
        + temperature
        + ", wind="
        + wind
        + ", groundTemperatureValueOne="
        + groundTemperatureValueOne
        + ", groundTemperatureValueTwo="
        + groundTemperatureValueOne
        + '}';
  }
}
