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

public class WeatherValue implements Value {
  private final Point coordinate;
  private final ComparableQuantity<Irradiance> diffSolar;
  private final ComparableQuantity<Irradiance> directSolar;
  private final ComparableQuantity<Temperature> temperature;
  private final ComparableQuantity<Angle> windDir;
  private final ComparableQuantity<Speed> windVel;
  private final Map<ComparableQuantity<Length>, TemperatureValue> groundTemperatures;

  /**
   * @param coordinate of this weather value set
   * @param directSolar Direct solar irradiance
   * @param diffSolar Diffuse solar irradiance
   * @param temperature Temperature in 2m height
   * @param windDir Wind direction
   * @param windVel Wind velocity
   * @param groundTemperatures A map of ground temperatures at different depths
   */
  public WeatherValue(
      Point coordinate,
      ComparableQuantity<Irradiance> directSolar,
      ComparableQuantity<Irradiance> diffSolar,
      ComparableQuantity<Temperature> temperature,
      ComparableQuantity<Angle> windDir,
      ComparableQuantity<Speed> windVel,
      Map<ComparableQuantity<Length>, TemperatureValue> groundTemperatures) {
    this.coordinate = coordinate;
    this.directSolar = directSolar;
    this.diffSolar = diffSolar;
    this.temperature = temperature;
    this.windDir = windDir;
    this.windVel = windVel;
    this.groundTemperatures = Collections.unmodifiableMap(new HashMap<>(groundTemperatures));
  }

  public Point getCoordinate() {
    return coordinate;
  }

  public ComparableQuantity<Irradiance> getDiffSolar() {
    return diffSolar;
  }

  public ComparableQuantity<Irradiance> getDirectSolar() {
    return directSolar;
  }

  public ComparableQuantity<Temperature> getTemperature() {
    return temperature;
  }

  public ComparableQuantity<Angle> getWindDir() {
    return windDir;
  }

  public ComparableQuantity<Speed> getWindVel() {
    return windVel;
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
        && diffSolar.equals(that.diffSolar)
        && directSolar.equals(that.directSolar)
        && temperature.equals(that.temperature)
        && windDir.equals(that.windDir)
        && windVel.equals(that.windVel)
        && groundTemperatures.equals(that.groundTemperatures);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        coordinate, diffSolar, directSolar, temperature, windDir, windVel, groundTemperatures);
  }

  @Override
  public String toString() {
    return "WeatherValue{"
        + "coordinate="
        + coordinate
        + ", diffSolar="
        + diffSolar
        + ", directSolar="
        + directSolar
        + ", temperature="
        + temperature
        + ", windDir="
        + windDir
        + ", windVel="
        + windVel
        + ", groundTemperatures="
        + groundTemperatures
        + '}';
  }
}
