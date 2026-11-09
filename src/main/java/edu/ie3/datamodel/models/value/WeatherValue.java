/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.value;

import edu.ie3.util.quantities.interfaces.Irradiance;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;

/** Describes weather as a combination of solar irradiance, temperature and wind values */
public sealed class WeatherValue implements Value {
  /** The coordinate of this weather value set */
  private final Point coordinate;

  /** Default for {@link #coordinateId}, denoting that the id of the coordinate is not known */
  public static final int UNKNOWN_COORDINATE_ID = Integer.MIN_VALUE;

  /**
   * The id of the coordinate of this weather value set, or {@link #UNKNOWN_COORDINATE_ID} if it is
   * not known.
   *
   * <p>Not part of the persisted output: {@link
   * edu.ie3.datamodel.io.processor.timeseries.TimeSeriesProcessor} explicitly ignores this field
   * when it collects the fields to persist.
   */
  private final int coordinateId;

  /** solar irradiance values for this coordinate */
  private final SolarIrradianceValue solarIrradiance;

  /** Temperature value for this coordinate */
  private final TemperatureValue temperature;

  /** Wind values for this coordinate */
  private final WindValue wind;

  /** Ground temperature value for this coordinate */
  private final GroundTemperatureValue groundTemperatureLevel1;

  /** Ground temperature value for this coordinate */
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
      Optional<GroundTemperatureValue> groundTemperatureLevel1,
      Optional<GroundTemperatureValue> groundTemperatureLevel2) {
    this(
        coordinate,
        UNKNOWN_COORDINATE_ID,
        solarIrradiance,
        temperature,
        wind,
        groundTemperatureLevel1,
        groundTemperatureLevel2);
  }

  /**
   * Same as {@link #WeatherValue(Point, SolarIrradianceValue, TemperatureValue, WindValue,
   * Optional, Optional)}, but additionally retaining the id of the coordinate.
   *
   * @param coordinate of this weather value set
   * @param coordinateId id of that coordinate ({@link #UNKNOWN_COORDINATE_ID} if unknown)
   * @param solarIrradiance values for this coordinate
   * @param temperature values for this coordinate
   * @param wind values for this coordinate
   * @param groundTemperatureLevel1 values for this coordinate (can be null)
   * @param groundTemperatureLevel2 values for this coordinate (can be null)
   */
  public WeatherValue(
      Point coordinate,
      int coordinateId,
      SolarIrradianceValue solarIrradiance,
      TemperatureValue temperature,
      WindValue wind,
      Optional<GroundTemperatureValue> groundTemperatureLevel1,
      Optional<GroundTemperatureValue> groundTemperatureLevel2) {
    this.coordinate = coordinate;
    this.coordinateId = coordinateId;
    this.solarIrradiance = solarIrradiance;
    this.temperature = temperature;
    this.wind = wind;
    this.groundTemperatureLevel1 = groundTemperatureLevel1.orElse(null);
    this.groundTemperatureLevel2 = groundTemperatureLevel2.orElse(null);
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
      Optional<ComparableQuantity<Temperature>> groundTempValOne,
      Optional<ComparableQuantity<Temperature>> groundTempValTwo) {
    this(
        coordinate,
        UNKNOWN_COORDINATE_ID,
        directSolarIrradiance,
        diffuseSolarIrradiance,
        temperature,
        direction,
        velocity,
        groundTempValOne,
        groundTempValTwo);
  }

  /**
   * Same as {@link #WeatherValue(Point, ComparableQuantity, ComparableQuantity, ComparableQuantity,
   * ComparableQuantity, ComparableQuantity, Optional, Optional)}, but additionally retaining the id
   * of the coordinate.
   *
   * @param coordinate of this weather value set
   * @param coordinateId id of that coordinate ({@link #UNKNOWN_COORDINATE_ID} if unknown)
   * @param directSolarIrradiance Direct sun irradiance for this coordinate (typically in W/m²)
   * @param diffuseSolarIrradiance Diffuse sun irradiance for this coordinate (typically in W/m²)
   * @param temperature for this coordinate (typically in K)
   * @param direction Direction, the wind comes from as an angle from north increasing clockwise
   * @param velocity Wind velocity for this coordinate (typically in m/s)
   * @param groundTempValOne Ground temperature for this coordinate (can be null)
   * @param groundTempValTwo Ground temperature for this coordinate (can be null)
   */
  public WeatherValue(
      Point coordinate,
      int coordinateId,
      ComparableQuantity<Irradiance> directSolarIrradiance,
      ComparableQuantity<Irradiance> diffuseSolarIrradiance,
      ComparableQuantity<Temperature> temperature,
      ComparableQuantity<Angle> direction,
      ComparableQuantity<Speed> velocity,
      Optional<ComparableQuantity<Temperature>> groundTempValOne,
      Optional<ComparableQuantity<Temperature>> groundTempValTwo) {
    this(
        coordinate,
        coordinateId,
        new SolarIrradianceValue(directSolarIrradiance, diffuseSolarIrradiance),
        new TemperatureValue(temperature),
        new WindValue(direction, velocity),
        Optional.ofNullable(groundTempValOne)
            .flatMap(optional -> optional.map(GroundTemperatureValue::new)),
        Optional.ofNullable(groundTempValTwo)
            .flatMap(optional -> optional.map(GroundTemperatureValue::new)));
  }

  public Point getCoordinate() {
    return coordinate;
  }

  /**
   * Returns the id of this value's coordinate, if it is known.
   *
   * @return an option on the coordinate id
   */
  public OptionalInt getCoordinateId() {
    return coordinateId == UNKNOWN_COORDINATE_ID
        ? OptionalInt.empty()
        : OptionalInt.of(coordinateId);
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
    if (o == null || getClass() != o.getClass()) return false;
    WeatherValue that = (WeatherValue) o;
    return coordinate.equals(that.coordinate)
        && coordinateId == that.coordinateId
        && solarIrradiance.equals(that.solarIrradiance)
        && temperature.equals(that.temperature)
        && wind.equals(that.wind)
        && Objects.equals(groundTemperatureLevel1, that.groundTemperatureLevel1)
        && Objects.equals(groundTemperatureLevel2, that.groundTemperatureLevel2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        coordinate,
        coordinateId,
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
        + ", coordinateId="
        + coordinateId
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
        + '}';
  }

  public static final class CosmoWeatherValue extends WeatherValue {
    private CosmoWeatherValue() {
      super(null, null, null, null, Optional.empty(), Optional.empty());
    }
  }

  public static final class IconWeatherValue extends WeatherValue {
    private IconWeatherValue() {
      super(null, null, null, null, Optional.empty(), Optional.empty());
    }
  }
}
