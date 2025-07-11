/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.exceptions.FactoryException;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.TemperatureValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.quantities.PowerSystemUnits;
import edu.ie3.util.quantities.interfaces.Irradiance;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;
import tech.units.indriya.unit.Units;

/**
 * Factory implementation of {@link TimeBasedWeatherValueFactory}, that is able to handle field to
 * value mapping in the column scheme, ie<sup>3</sup> uses to store its data from German Federal
 * Weather Service's ICON-EU model
 */
public class IconTimeBasedWeatherValueFactory extends TimeBasedWeatherValueFactory {
  /* Redefine the column names to meet the icon specifications */
  private static final String DIFFUSE_IRRADIANCE = "aswdifdS";
  private static final String DIRECT_IRRADIANCE = "aswdirS";
  private static final String TEMPERATURE = "t2m";
  private static final String WIND_VELOCITY_U = "u131m";
  private static final String WIND_VELOCITY_V = "v131m";
  private static final String GROUND_TEMP_SURFACE = "tG";
  private static final String SOIL_TEMP_100CM = "tso100cm";

  public IconTimeBasedWeatherValueFactory() {
    super();
  }

  public IconTimeBasedWeatherValueFactory(DateTimeFormatter dateTimeFormatter) {
    super(dateTimeFormatter);
  }

  @Override
  protected List<Set<String>> getFields(Class<?> entityClass) {
    Set<String> minParameters =
        newSet(
            DIFFUSE_IRRADIANCE, DIRECT_IRRADIANCE, TEMPERATURE, WIND_VELOCITY_U, WIND_VELOCITY_V);

    Set<String> minParametersWithGroundTemp = new HashSet<>(minParameters);
    minParametersWithGroundTemp.add(GROUND_TEMP_SURFACE);
    minParametersWithGroundTemp.add(SOIL_TEMP_100CM);

    Set<String> allParameters =
        expandSet(
            minParameters,
            "albrad",
            "asobs",
            "aswdifuS",
            "tG",
            GROUND_TEMP_SURFACE,
            SOIL_TEMP_100CM,
            "u10m",
            "u20m",
            "u216m",
            "u65m",
            "v10m",
            "v20m",
            "v216m",
            "v65m",
            "w131m",
            "w20m",
            "w216m",
            "w65m",
            "z0",
            "p131m",
            "p20m",
            "p65m",
            "sobsrad",
            "t131m");

    return Arrays.asList(minParameters, minParametersWithGroundTemp, allParameters);
  }

  @Override
  protected TimeBasedValue<WeatherValue> buildModel(TimeBasedWeatherValueData data) {
    Point coordinate = data.getCoordinate();
    ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(TIME));
    ComparableQuantity<Irradiance> directIrradiance =
        data.getQuantity(DIRECT_IRRADIANCE, PowerSystemUnits.WATT_PER_SQUAREMETRE);
    ComparableQuantity<Irradiance> diffuseIrradiance =
        data.getQuantity(DIFFUSE_IRRADIANCE, PowerSystemUnits.WATT_PER_SQUAREMETRE);
    ComparableQuantity<Temperature> temperature =
        data.getQuantity(TEMPERATURE, Units.KELVIN).to(StandardUnits.TEMPERATURE);
    ComparableQuantity<Angle> windDirection = getWindDirection(data);
    ComparableQuantity<Speed> windVelocity = getWindVelocity(data);

    Map<ComparableQuantity<Length>, TemperatureValue> groundTemperatures = new HashMap<>();

    try {
      TemperatureValue tempValue =
          new TemperatureValue(
              data.getQuantity(GROUND_TEMP_SURFACE, Units.KELVIN).to(StandardUnits.TEMPERATURE));
      groundTemperatures.put(Quantities.getQuantity(0, Units.METRE), tempValue);
    } catch (FactoryException ignored) {
    }

    try {
      TemperatureValue tempValue =
          new TemperatureValue(
              data.getQuantity(SOIL_TEMP_100CM, Units.KELVIN).to(StandardUnits.TEMPERATURE));
      groundTemperatures.put(Quantities.getQuantity(1, Units.METRE), tempValue);
    } catch (FactoryException ignored) {
    }

    WeatherValue weatherValue =
        new WeatherValue(
            coordinate,
            directIrradiance,
            diffuseIrradiance,
            temperature,
            windDirection,
            windVelocity,
            groundTemperatures);

    return new TimeBasedValue<>(time, weatherValue);
  }

  /**
   * Determines the wind direction. In ICON the wind velocity is given in three dimensional
   * Cartesian coordinates. Here, the upward component is neglected. 0° or 0 rad are defined to
   * point northwards. The angle increases clockwise. Please note, that the wind direction is the
   * direction, the wind <b>comes</b> from and not goes to. We choose to use the wind velocity
   * calculations at 131 m above ground, as this is a height that pretty good matches the common hub
   * height of today's onshore wind generators, that are commonly connected to the voltage levels of
   * interest.
   *
   * @param data Collective information to convert
   * @return A {@link ComparableQuantity} of type {@link Speed}, that is converted to {@link
   *     StandardUnits#WIND_VELOCITY}
   */
  private static ComparableQuantity<Angle> getWindDirection(TimeBasedWeatherValueData data) {
    /* Get the three dimensional parts of the wind velocity vector in cartesian coordinates */
    double u =
        data.getDouble(WIND_VELOCITY_U); // Wind component from west to east (parallel to latitudes)
    double v =
        data.getDouble(
            WIND_VELOCITY_V); // Wind component from south to north (parallel to longitudes)

    double angle = Math.toDegrees(Math.atan2(-u, -v));
    return Quantities.getQuantity(angle < 0 ? angle + 360d : angle, PowerSystemUnits.DEGREE_GEOM)
        .to(StandardUnits.WIND_DIRECTION);
  }

  /**
   * Determines the wind velocity. In ICON the wind velocity is given in three dimensional Cartesian
   * coordinates. Here, the upward component is neglected. We choose to use the wind velocity
   * calculations at 131 m above ground, as this is a height that pretty good matches the common hub
   * height of today's onshore wind generators, that are commonly connected to the voltage levels of
   * interest.
   *
   * @param data Collective information to convert
   * @return A {@link ComparableQuantity} of type {@link Speed}, that is converted to {@link
   *     StandardUnits#WIND_VELOCITY}
   */
  private static ComparableQuantity<Speed> getWindVelocity(TimeBasedWeatherValueData data) {
    /* Get the three dimensional parts of the wind velocity vector in cartesian coordinates */
    double u = data.getDouble(WIND_VELOCITY_U);
    double v = data.getDouble(WIND_VELOCITY_V);

    double velocity = Math.sqrt(Math.pow(u, 2) + Math.pow(v, 2));
    return Quantities.getQuantity(velocity, Units.METRE_PER_SECOND).to(StandardUnits.WIND_VELOCITY);
  }
}
