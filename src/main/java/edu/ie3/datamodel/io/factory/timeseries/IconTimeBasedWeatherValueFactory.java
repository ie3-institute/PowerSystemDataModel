/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.*;
import edu.ie3.util.quantities.PowerSystemUnits;
import java.time.ZonedDateTime;
import java.util.Optional;
import javax.measure.quantity.Angle;
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

  public IconTimeBasedWeatherValueFactory() {
    super();
  }

  @Override
  protected TimeBasedValue<WeatherValue> buildModel(TimeBasedWeatherValueData data) {
    Point coordinate = data.getCoordinate();
    ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(TIME));

    SolarIrradianceValue solarIrradianceValue =
        new SolarIrradianceValue(
            data.getQuantity(ICON_DIRECT_IRRADIANCE, PowerSystemUnits.WATT_PER_SQUAREMETRE),
            data.getQuantity(ICON_DIFFUSE_IRRADIANCE, PowerSystemUnits.WATT_PER_SQUAREMETRE));
    TemperatureValue temperatureValue =
        new TemperatureValue(
            data.getQuantity(ICON_TEMPERATURE, Units.KELVIN).to(StandardUnits.TEMPERATURE));
    WindValue windValue = getWindValue(data);
    Optional<ComparableQuantity<Temperature>> groundTemperatureLevel1 =
        data.getQuantityOptional(ICON_GROUND_TEMPERATURE_LEVEL_1, Units.KELVIN)
            .map(quantity -> quantity.to(StandardUnits.TEMPERATURE));
    Optional<ComparableQuantity<Temperature>> groundTemperatureLevel2 =
        data.getQuantityOptional(ICON_GROUND_TEMPERATURE_LEVEL_2, Units.KELVIN)
            .map(quantity -> quantity.to(StandardUnits.TEMPERATURE));
    WeatherValue weatherValue =
        new WeatherValue(
            coordinate,
            solarIrradianceValue,
            temperatureValue,
            windValue,
            groundTemperatureLevel1.map(GroundTemperatureValue::new),
            groundTemperatureLevel2.map(GroundTemperatureValue::new));
    return new TimeBasedValue<>(time, weatherValue);
  }

  /**
   * Determines the wind direction and velocity. In ICON both values are given in three-dimensional
   * Cartesian coordinates.
   *
   * <p><b>For the direction:</b><br>
   * Here, the upward component is neglected. 0° or 0 rad are defined to point northwards. The angle
   * increases clockwise. Please note, that the wind direction is the direction, the wind
   * <b>comes</b> from and not goes to. We choose to use the wind velocity calculations at 131 m
   * above ground, as this is a height that pretty good matches the common hub height of today's
   * onshore wind generators, that are commonly connected to the voltage levels of interest.
   *
   * <p><b>For the velocity:</b><br>
   * Here, the upward component is neglected. We choose to use the wind velocity calculations at 131
   * m above ground, as this is a height that pretty good matches the common hub height of today's
   * onshore wind generators, that are commonly connected to the voltage levels of interest.
   *
   * @param data Collective information to convert
   * @return The wind value.
   */
  private static WindValue getWindValue(TimeBasedWeatherValueData data) {
    /* Get the three-dimensional parts of the wind velocity vector in Cartesian coordinates */
    double u =
        data.getDouble(
            ICON_WIND_VELOCITY_U); // Wind component from west to east (parallel to latitudes)
    double v =
        data.getDouble(
            ICON_WIND_VELOCITY_V); // Wind component from south to north (parallel to longitudes)

    double angle = Math.toDegrees(Math.atan2(-u, -v));
    ComparableQuantity<Angle> windAngle =
        Quantities.getQuantity(angle < 0 ? angle + 360d : angle, PowerSystemUnits.DEGREE_GEOM)
            .to(StandardUnits.WIND_DIRECTION);

    double velocity = Math.sqrt(Math.pow(u, 2) + Math.pow(v, 2));
    ComparableQuantity<Speed> windVelocity =
        Quantities.getQuantity(velocity, Units.METRE_PER_SECOND).to(StandardUnits.WIND_VELOCITY);

    return new WindValue(windAngle, windVelocity);
  }
}
