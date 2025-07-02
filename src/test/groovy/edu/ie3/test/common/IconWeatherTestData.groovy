/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.value.TemperatureValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.util.TimeUtil
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units

import java.time.ZonedDateTime
import java.util.Collections

class IconWeatherTestData extends WeatherTestData {
  public static final ZonedDateTime TIME_15H = TimeUtil.withDefaults.toZonedDateTime("2019-08-01T15:00:00+00:00")
  public static final ZonedDateTime TIME_16H = TimeUtil.withDefaults.toZonedDateTime("2019-08-01T16:00:00+00:00")
  public static final ZonedDateTime TIME_17H = TimeUtil.withDefaults.toZonedDateTime("2019-08-01T17:00:00+00:00")

  public static final WeatherValue WEATHER_VALUE_67775_15H = new WeatherValue(
  COORDINATE_67775,
  Quantities.getQuantity(356.2648859375, StandardUnits.SOLAR_IRRADIANCE),
  Quantities.getQuantity(228.021339757131, StandardUnits.SOLAR_IRRADIANCE),
  Quantities.getQuantity(24.4741992659816, StandardUnits.TEMPERATURE),
  Quantities.getQuantity(270.45278309919627, StandardUnits.WIND_DIRECTION),
  Quantities.getQuantity(3.76601470961371, StandardUnits.WIND_VELOCITY),
  [
    (Quantities.getQuantity(0, Units.METRE)): new TemperatureValue(Quantities.getQuantity(27.5132065668998, Units.CELSIUS))
  ]
  )

  public static final WeatherValue WEATHER_VALUE_67775_16H = new WeatherValue(
  COORDINATE_67775,
  Quantities.getQuantity(204.38963365625, StandardUnits.SOLAR_IRRADIANCE),
  Quantities.getQuantity(200.46049098038043, StandardUnits.SOLAR_IRRADIANCE),
  Quantities.getQuantity(24.1700023473353, StandardUnits.TEMPERATURE),
  Quantities.getQuantity(278.144331776102, StandardUnits.WIND_DIRECTION),
  Quantities.getQuantity(4.05744164637287, StandardUnits.WIND_VELOCITY),
  [
    (Quantities.getQuantity(0, Units.METRE)): new TemperatureValue(Quantities.getQuantity(25.6947737622156, Units.CELSIUS))
  ]
  )

  public static final WeatherValue WEATHER_VALUE_67775_17H = new WeatherValue(
  COORDINATE_67775,
  Quantities.getQuantity(175.039569078125, StandardUnits.SOLAR_IRRADIANCE),
  Quantities.getQuantity(180.73429610400223, StandardUnits.SOLAR_IRRADIANCE),
  Quantities.getQuantity(23.6787403584074, StandardUnits.TEMPERATURE),
  Quantities.getQuantity(286.891007103442, StandardUnits.WIND_DIRECTION),
  Quantities.getQuantity(3.81526300455393, StandardUnits.WIND_VELOCITY),
  [
    (Quantities.getQuantity(0, Units.METRE)): new TemperatureValue(Quantities.getQuantity(24.5096017457568, Units.CELSIUS))
  ]
  )

  public static final WeatherValue WEATHER_VALUE_67776_15H = new WeatherValue(
  COORDINATE_67776,
  Quantities.getQuantity(333.0547140625, StandardUnits.SOLAR_IRRADIANCE),
  Quantities.getQuantity(245.24079037841295, StandardUnits.SOLAR_IRRADIANCE),
  Quantities.getQuantity(22.365335568404, StandardUnits.TEMPERATURE),
  Quantities.getQuantity(245.604554131632, StandardUnits.WIND_DIRECTION),
  Quantities.getQuantity(4.39390441381814, StandardUnits.WIND_VELOCITY),
  [
    (Quantities.getQuantity(0, Units.METRE)): new TemperatureValue(Quantities.getQuantity(24.28684351873816, Units.CELSIUS))
  ]
  )

  public static final WeatherValue WEATHER_VALUE_67776_16H = new WeatherValue(
  COORDINATE_67776,
  Quantities.getQuantity(91.70939132297, StandardUnits.SOLAR_IRRADIANCE),
  Quantities.getQuantity(241.641483540946, StandardUnits.SOLAR_IRRADIANCE),
  Quantities.getQuantity(20.305111314491, StandardUnits.TEMPERATURE),
  Quantities.getQuantity(252.810224701109, StandardUnits.WIND_DIRECTION),
  Quantities.getQuantity(3.44242472583919, StandardUnits.WIND_VELOCITY),
  [
    (Quantities.getQuantity(0, Units.METRE)): new TemperatureValue(Quantities.getQuantity(21.8376832274387, Units.CELSIUS))
  ]
  )
}