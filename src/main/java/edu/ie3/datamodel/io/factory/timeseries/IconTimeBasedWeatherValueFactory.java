/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.factory.timeseries;

import static edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory.*;

import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue;
import edu.ie3.datamodel.models.value.WeatherValue;
import edu.ie3.util.TimeUtil;
import edu.ie3.util.quantities.PowerSystemUnits;
import edu.ie3.util.quantities.interfaces.Irradiation;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Speed;
import javax.measure.quantity.Temperature;
import org.locationtech.jts.geom.Point;
import tech.units.indriya.ComparableQuantity;
import tech.units.indriya.quantity.Quantities;

/**
 * Factory implementation of {@link TimeBasedWeatherValueFactory}, that is able to handle field to
 * value mapping in the column scheme, ie<sup>3</sup> uses to store it's data from German Federal
 * Weather Service's ICON-EU model
 */
public class IconTimeBasedWeatherValueFactory extends TimeBasedWeatherValueFactory {
  /* Redefine the column names to meet the icon specifications */
  private static final String COORDINATE = "coordinate";
  private static final String TIME = "datum";
  private static final String DIFFUSE_IRRADIATION = "aswdifd_s";
  private static final String DIRECT_IRRADIATION = "aswdir_s";
  private static final String TEMPERATURE = "t_2m";

  public IconTimeBasedWeatherValueFactory(TimeUtil timeUtil) {
    super(timeUtil);
  }

  public IconTimeBasedWeatherValueFactory(String timePattern) {
    super(timePattern);
  }

  public IconTimeBasedWeatherValueFactory() {
    super(new TimeUtil(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss"));
  }

  @Override
  public String getCoordinateIdFieldString() {
    return COORDINATE;
  }

  @Override
  protected TimeBasedValue<WeatherValue> buildModel(TimeBasedWeatherValueData data) {
    Point coordinate = data.getCoordinate();
    java.util.UUID uuid = java.util.UUID.randomUUID();
    ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(TIME));
    ComparableQuantity<Irradiation> directIrradiation =
        data.getQuantity(DIRECT_IRRADIATION, PowerSystemUnits.WATT_PER_SQUAREMETRE)
            .to(StandardUnits.IRRADIATION);
    ComparableQuantity<Irradiation> diffuseIrradiation =
        data.getQuantity(DIFFUSE_IRRADIATION, PowerSystemUnits.WATT_PER_SQUAREMETRE)
            .to(StandardUnits.IRRADIATION);
    ComparableQuantity<Temperature> temperature =
        data.getQuantity(TEMPERATURE, PowerSystemUnits.KELVIN).to(StandardUnits.TEMPERATURE);
    ComparableQuantity<Angle> windDirection = getWindDirection(data);
    ComparableQuantity<Speed> windVelocity = getWindVelocity(data);
    WeatherValue weatherValue =
        new WeatherValue(
            coordinate,
            directIrradiation,
            diffuseIrradiation,
            temperature,
            windDirection,
            windVelocity);
    return new TimeBasedValue<>(uuid, time, weatherValue);
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
    double u = data.getDouble("u_131m"); // Wind component from west to east (parallel to latitudes)
    double v =
        data.getDouble("v_131m"); // Wind component from south to north (parallel to longitudes)

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
    double u = data.getDouble("u_131m");
    double v = data.getDouble("v_131m");

    double velocity = Math.sqrt(Math.pow(u, 2) + Math.pow(v, 2));
    return Quantities.getQuantity(velocity, PowerSystemUnits.METRE_PER_SECOND)
        .to(StandardUnits.WIND_VELOCITY);
  }
}
