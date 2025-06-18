/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.util.quantities.interfaces.Irradiance;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
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

  /** Optional: A map of ground temperatures at different depths. The key represents the depth. */
  private Map<ComparableQuantity<Length>, TemperatureValue> groundTemperatures = Map.of();
  /**
   * @param coordinate of this weather value set
   * @param solarIrradiance values for this coordinate
   * @param temperature values for this coordinate
   * @param wind values for this coordinate
   * @param groundTemperatures A map of ground temperatures at different depths
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
    this.groundTemperatures = Collections.unmodifiableMap(new HashMap<>(groundTemperatures));
  }

  /**
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
    this(
        coordinate,
        new SolarIrradianceValue(directSolarIrradiance, diffuseSolarIrradiance),
        new TemperatureValue(temperature),
        new WindValue(direction, velocity));
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

  /**
   * Returns a map of ground temperatures, with the depth as key. The map is unmodifiable. Returns
   * an empty map if no values are available.
   *
   * @return A map of ground temperatures.
   */
  public Map<ComparableQuantity<Length>, TemperatureValue> getGroundTemperatures() {
    return groundTemperatures;
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
        && groundTemperatures.equals(that.groundTemperatures);
  }

  @Override
  public int hashCode() {
    return Objects.hash(coordinate, solarIrradiance, temperature, wind, groundTemperatures);
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
        + groundTemperatures
        + ", groundTemperatures="
        + '}';
  }
}
