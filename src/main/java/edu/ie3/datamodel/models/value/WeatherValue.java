/*
 * © 2026. TU Dortmund University,
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

/** Describes weather as a combination of solar irradiance, temperature and wind values. */
public class WeatherValue implements Value {
  /** The coordinate of this weather value set. */
  private final Point coordinate;

  /** Solar irradiance values for this coordinate. */
  private final SolarIrradianceValue solarIrradiance;

  /** Temperature value for this coordinate. */
  private final TemperatureValue temperature;

  /** Wind values for this coordinate. */
  private final WindValue wind;

  /** Ground temperature value for this coordinate. */
  private final GroundTemperatureValue groundTemperatureLevel1;

  /** Ground temperature value for this coordinate. */
  private final GroundTemperatureValue groundTemperatureLevel2;

  /**
   * @param coordinate of this weather value set
   * @param solarIrradiance values for this coordinate
   * @param temperature values for this coordinate
   * @param wind values for this coordinate
   * @param groundTemperatureLevel1 values for this coordinate (can be null)
   * @param groundTemperatureLevel2 values for this coordinate (can be null)
   */
  public WeatherValue(
      Point coordinate,
      SolarIrradianceValue solarIrradiance,
      TemperatureValue temperature,
      WindValue wind,
      GroundTemperatureValue groundTemperatureLevel1,
      GroundTemperatureValue groundTemperatureLevel2) {
    this.coordinate = coordinate;
    this.solarIrradiance = solarIrradiance;
    this.temperature = temperature;
    this.wind = wind;
    this.groundTemperatureLevel1 = groundTemperatureLevel1;
    this.groundTemperatureLevel2 = groundTemperatureLevel2;
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
   * @param groundTempValOne Ground temperature for this coordinate (typically in K, can be null)
   * @param groundTempValTwo Ground temperature for this coordinate (typically in K, can be null)
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
    this.coordinate = coordinate;
    this.solarIrradiance = new SolarIrradianceValue(directSolarIrradiance, diffuseSolarIrradiance);
    this.temperature = new TemperatureValue(temperature);
    this.wind = new WindValue(direction, velocity);
    this.groundTemperatureLevel1 = new GroundTemperatureValue(groundTempValOne);
    this.groundTemperatureLevel2 = new GroundTemperatureValue(groundTempValTwo);
  }

  /**
   * @param coordinate of this weather value set
   * @param solarIrradiance values for this coordinate
   * @param temperature values for this coordinate
   * @param wind values for this coordinate
   */
  public WeatherValue(
      Point coordinate,
      SolarIrradianceValue solarIrradiance,
      TemperatureValue temperature,
      WindValue wind) {
    this.coordinate = coordinate;
    this.solarIrradiance = solarIrradiance;
    this.temperature = temperature;
    this.wind = wind;
    this.groundTemperatureLevel1 = null;
    this.groundTemperatureLevel2 = null;
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
   */
  public WeatherValue(
      Point coordinate,
      ComparableQuantity<Irradiance> directSolarIrradiance,
      ComparableQuantity<Irradiance> diffuseSolarIrradiance,
      ComparableQuantity<Temperature> temperature,
      ComparableQuantity<Angle> direction,
      ComparableQuantity<Speed> velocity) {
    this.coordinate = coordinate;
    this.solarIrradiance = new SolarIrradianceValue(directSolarIrradiance, diffuseSolarIrradiance);
    this.temperature = new TemperatureValue(temperature);
    this.wind = new WindValue(direction, velocity);
    this.groundTemperatureLevel1 = null;
    this.groundTemperatureLevel2 = null;
  }

  private WeatherValue() {
    this.coordinate = null;
    this.solarIrradiance = null;
    this.temperature = null;
    this.wind = null;
    this.groundTemperatureLevel1 = null;
    this.groundTemperatureLevel2 = null;
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

  public Optional<GroundTemperatureValue> getGroundTemperatureLevel1() {
    return Optional.ofNullable(groundTemperatureLevel1);
  }

  public Optional<GroundTemperatureValue> getGroundTemperatureLevel2() {
    return Optional.ofNullable(groundTemperatureLevel2);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof WeatherValue that)) return false;
    return Objects.equals(coordinate, that.coordinate)
        && Objects.equals(solarIrradiance, that.solarIrradiance)
        && Objects.equals(temperature, that.temperature)
        && Objects.equals(wind, that.wind)
        && Objects.equals(groundTemperatureLevel1, that.groundTemperatureLevel1)
        && Objects.equals(groundTemperatureLevel2, that.groundTemperatureLevel2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        coordinate,
        solarIrradiance,
        temperature,
        wind,
        groundTemperatureLevel1,
        groundTemperatureLevel2);
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
        + ", groundTemperatureLevel1="
        + groundTemperatureLevel1
        + ", groundTemperatureLevel2="
        + groundTemperatureLevel2
        + "}";
  }

  public static final class CosmoWeatherValue extends WeatherValue {}

  public static final class IconWeatherValue extends WeatherValue {}
}
