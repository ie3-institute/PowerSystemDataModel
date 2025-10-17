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
  private final GroundTemperatureValue groundTemperature0cm;

  /** Ground temperature value at 80cm depth for this coordinate */
  private final GroundTemperatureValue groundTemperature80cm;

  /**
   * @param coordinate of this weather value set
   * @param solarIrradiance values for this coordinate
   * @param temperature values for this coordinate
   * @param wind values for this coordinate
   * @param groundTemperature0cm values for this coordinate (can be null)
   * @param groundTemperature80cm values for this coordinate (can be null)
   */
  public WeatherValue(
      Point coordinate,
      SolarIrradianceValue solarIrradiance,
      TemperatureValue temperature,
      WindValue wind,
      GroundTemperatureValue groundTemperature0cm,
      GroundTemperatureValue groundTemperature80cm) {
    this.coordinate = coordinate;
    this.solarIrradiance = solarIrradiance;
    this.temperature = temperature;
    this.wind = wind;
    this.groundTemperature0cm = groundTemperature0cm;
    this.groundTemperature80cm = groundTemperature80cm;
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
   * @param groundTemp0cm Ground temperature at 0cm for this coordinate (typically in K, can be
   *     null)
   * @param groundTemp80cm Ground temperature at 80cm for this coordinate (typically in K, can be
   *     null)
   */
  public WeatherValue(
      Point coordinate,
      ComparableQuantity<Irradiance> directSolarIrradiance,
      ComparableQuantity<Irradiance> diffuseSolarIrradiance,
      ComparableQuantity<Temperature> temperature,
      ComparableQuantity<Angle> direction,
      ComparableQuantity<Speed> velocity,
      ComparableQuantity<Temperature> groundTemp0cm,
      ComparableQuantity<Temperature> groundTemp80cm) {
    this(
        coordinate,
        new SolarIrradianceValue(directSolarIrradiance, diffuseSolarIrradiance),
        new TemperatureValue(temperature),
        new WindValue(direction, velocity),
        groundTemp0cm == null ? null : new GroundTemperatureValue(groundTemp0cm),
        groundTemp80cm == null ? null : new GroundTemperatureValue(groundTemp80cm));
  }

  /**
   * @deprecated Use the constructor that includes ground temperatures instead
   */
  @Deprecated(since = "3.0", forRemoval = true)
  public WeatherValue(
      Point coordinate,
      SolarIrradianceValue solarIrradiance,
      TemperatureValue temperature,
      WindValue wind) {
    this(coordinate, solarIrradiance, temperature, wind, null, null);
  }

  /**
   * @deprecated Use the constructor that includes ground temperatures instead
   */
  @Deprecated(since = "3.0", forRemoval = true)
  public WeatherValue(
      Point coordinate,
      ComparableQuantity<Irradiance> directSolarIrradiance,
      ComparableQuantity<Irradiance> diffuseSolarIrradiance,
      ComparableQuantity<Temperature> temperature,
      ComparableQuantity<Angle> direction,
      ComparableQuantity<Speed> velocity) {
    this(
        coordinate,
        directSolarIrradiance,
        diffuseSolarIrradiance,
        temperature,
        direction,
        velocity,
        null,
        null);
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

  public Optional<GroundTemperatureValue> getGroundTemperature0cm() {
    return Optional.ofNullable(groundTemperature0cm);
  }

  public Optional<GroundTemperatureValue> getGroundTemperature80cm() {
    return Optional.ofNullable(groundTemperature80cm);
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
        && Objects.equals(groundTemperature0cm, that.groundTemperature0cm)
        && Objects.equals(groundTemperature80cm, that.groundTemperature80cm);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        coordinate,
        solarIrradiance,
        temperature,
        wind,
        groundTemperature0cm,
        groundTemperature80cm);
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
        + ", groundTemperature0cm="
        + groundTemperature0cm
        + ", groundTemperature80cm="
        + groundTemperature80cm
        + '}';
  }
}
