/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.value.WeatherValue
import tech.units.indriya.quantity.Quantities

import java.time.ZoneId
import java.time.ZonedDateTime

class IconWeatherTestData extends WeatherTestData {
	public static final ZonedDateTime TIME_15H = ZonedDateTime.of(2019, 8, 1, 15, 0, 0, 0, ZoneId.of("UTC"))
	public static final ZonedDateTime TIME_16H = ZonedDateTime.of(2019, 8, 1, 16, 0, 0, 0, ZoneId.of("UTC"))
	public static final ZonedDateTime TIME_17H = ZonedDateTime.of(2019, 8, 1, 17, 0, 0, 0, ZoneId.of("UTC"))

	public static final WeatherValue WEATHER_VALUE_67775_15H = new WeatherValue(
	COORDINATE_67775,
	Quantities.getQuantity(356.2648859375, StandardUnits.SOLAR_IRRADIANCE),
	Quantities.getQuantity(228.021339757131, StandardUnits.SOLAR_IRRADIANCE),
	Quantities.getQuantity(24.4741992659816, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(270.45278309919627, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(3.76601470961371, StandardUnits.WIND_VELOCITY)
	)

	public static final WeatherValue WEATHER_VALUE_67775_16H = new WeatherValue(
	COORDINATE_67775,
	Quantities.getQuantity(204.38963365625, StandardUnits.SOLAR_IRRADIANCE),
	Quantities.getQuantity(200.46049098038043, StandardUnits.SOLAR_IRRADIANCE),
	Quantities.getQuantity(24.1700023473353, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(278.144331776102, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(4.05744164637287, StandardUnits.WIND_VELOCITY)
	)

	public static final WeatherValue WEATHER_VALUE_67775_17H = new WeatherValue(
	COORDINATE_67775,
	Quantities.getQuantity(175.039569078125, StandardUnits.SOLAR_IRRADIANCE),
	Quantities.getQuantity(180.73429610400223, StandardUnits.SOLAR_IRRADIANCE),
	Quantities.getQuantity(23.6787403584074, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(286.891007103442, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(3.81526300455393, StandardUnits.WIND_VELOCITY)
	)

	public static final WeatherValue WEATHER_VALUE_67776_15H = new WeatherValue(
	COORDINATE_67776,
	Quantities.getQuantity(333.0547140625, StandardUnits.SOLAR_IRRADIANCE),
	Quantities.getQuantity(245.24079037841295, StandardUnits.SOLAR_IRRADIANCE),
	Quantities.getQuantity(22.365335568404, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(245.604554131632, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(4.39390441381814, StandardUnits.WIND_VELOCITY)
	)

	public static final WeatherValue WEATHER_VALUE_67776_16H = new WeatherValue(
	COORDINATE_67776,
	Quantities.getQuantity(091.70939132297, StandardUnits.SOLAR_IRRADIANCE),
	Quantities.getQuantity(241.641483540946, StandardUnits.SOLAR_IRRADIANCE),
	Quantities.getQuantity(20.305111314491, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(252.810224701109, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(3.44242472583919, StandardUnits.WIND_VELOCITY)
	)
}
