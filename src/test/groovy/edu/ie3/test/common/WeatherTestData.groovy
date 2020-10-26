/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.io.source.IdCoordinateSource
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Point
import tech.units.indriya.quantity.Quantities

import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.stream.Collectors
import java.util.stream.Stream

class WeatherTestData {

	private static final class DummyIdCoordinateSource implements IdCoordinateSource {
		@Override
		Optional<Point> getCoordinate(int id) {
			switch (id) {
				case 193186: return Optional.of(GeoUtils.xyToPoint(49d, 7d))
				case 193187: return Optional.of(GeoUtils.xyToPoint(49d, 8d))
				case 193188: return Optional.of(GeoUtils.xyToPoint(50d, 7d))
			}
			return null
		}
		@Override
		Collection<Point> getCoordinates(int... ids) {
			return Stream.of(ids).map(this.&getCoordinate).filter({ c -> c != null }).collect(Collectors.toSet())
		}
		@Override
		Optional<Integer> getId(Point coordinate) {
			if (coordinate.x == 49 && coordinate.y == 7) {
				return Optional.of(193186)
			}
			if (coordinate.x == 49 && coordinate.y == 8) {
				return Optional.of(193187)
			}
			if (coordinate.x == 50 && coordinate.y == 7) {
				return Optional.of(193188)
			}
			return Optional.empty()
		}

		@Override
		Collection<Point> getAllCoordinates() {
			return [
				GeoUtils.xyToPoint(49d, 7d),
				GeoUtils.xyToPoint(49d, 8d),
				GeoUtils.xyToPoint(50d, 7d)
			]
		}
	}

	public static final IdCoordinateSource coordinateSource = new DummyIdCoordinateSource()

	public static final COORDINATE_193186 = coordinateSource.getCoordinate(193186).get()
	public static final COORDINATE_193187 = coordinateSource.getCoordinate(193187).get()
	public static final COORDINATE_193188 = coordinateSource.getCoordinate(193188).get()

	public static final ZonedDateTime TIME_15h = ZonedDateTime.of(2020, 04, 28, 15, 0, 0, 0, ZoneId.of("UTC"))
	public static final ZonedDateTime TIME_16h = ZonedDateTime.of(2020, 04, 28, 16, 0, 0, 0, ZoneId.of("UTC"))
	public static final ZonedDateTime TIME_17h = ZonedDateTime.of(2020, 04, 28, 17, 0, 0, 0, ZoneId.of("UTC"))


	public static final WeatherValue weatherVal_coordinate_193186_15h = new WeatherValue(COORDINATE_193186,
	Quantities.getQuantity(282.671997070312d, StandardUnits.IRRADIATION),
	Quantities.getQuantity(286.872985839844d, StandardUnits.IRRADIATION),
	Quantities.getQuantity(278.019012451172d, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(0d, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(1.66103506088257d, StandardUnits.WIND_VELOCITY))
	public static final WeatherValue weatherVal_coordinate_193186_16h = new WeatherValue(COORDINATE_193186,
	Quantities.getQuantity(282.672d, StandardUnits.IRRADIATION),
	Quantities.getQuantity(286.872d, StandardUnits.IRRADIATION),
	Quantities.getQuantity(278.012d, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(0d, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(1.662d, StandardUnits.WIND_VELOCITY))
	public static final WeatherValue  weatherVal_coordinate_193186_17h = new WeatherValue(COORDINATE_193186,
	Quantities.getQuantity(282.673d, StandardUnits.IRRADIATION),
	Quantities.getQuantity(286.873d, StandardUnits.IRRADIATION),
	Quantities.getQuantity(278.013d, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(0d, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(1.663d, StandardUnits.WIND_VELOCITY))

	public static final WeatherValue  weatherVal_coordinate_193187_15h = new WeatherValue(COORDINATE_193187,
	Quantities.getQuantity(283.671997070312d, StandardUnits.IRRADIATION),
	Quantities.getQuantity(287.872985839844d, StandardUnits.IRRADIATION),
	Quantities.getQuantity(279.019012451172d, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(0d, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(1.76103506088257d, StandardUnits.WIND_VELOCITY))
	public static final WeatherValue  weatherVal_coordinate_193187_16h = new WeatherValue(COORDINATE_193187,
	Quantities.getQuantity(283.672d, StandardUnits.IRRADIATION),
	Quantities.getQuantity(287.872d, StandardUnits.IRRADIATION),
	Quantities.getQuantity(279.012d, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(0d, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(1.762d, StandardUnits.WIND_VELOCITY))

	public static final WeatherValue  weatherVal_coordinate_193188_15h = new WeatherValue(COORDINATE_193188,
	Quantities.getQuantity(284.671997070312d, StandardUnits.IRRADIATION),
	Quantities.getQuantity(288.872985839844d, StandardUnits.IRRADIATION),
	Quantities.getQuantity(280.019012451172d, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(0d, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(1.86103506088257d, StandardUnits.WIND_VELOCITY))
}
