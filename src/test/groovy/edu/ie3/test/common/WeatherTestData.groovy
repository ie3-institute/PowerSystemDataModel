/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.io.source.CoordinateSource
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Point
import tec.uom.se.quantity.Quantities

import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.stream.Collectors
import java.util.stream.IntStream
import java.util.stream.Stream

class WeatherTestData {

	private static final class DummyCoordinateSource implements CoordinateSource {
		@Override
		Point getCoordinate(Integer id) {
			switch (id) {
				case 193186: return GeoUtils.xyToPoint(49d, 7d)
				case 193187: return GeoUtils.xyToPoint(49d, 8d)
				case 193188: return GeoUtils.xyToPoint(50d, 7d)
			}
			return null
		}
		@Override
		Collection<Point> getCoordinates(Integer... ids) {
			return Stream.of(ids).map(this.&getCoordinate).filter({ c -> c != null }).collect(Collectors.toSet())
		}
		@Override
		Collection<Point> getCoordinatesBetween(Integer fromId, Integer toId) {
			getCoordinates(IntStream.rangeClosed(fromId, toId).toArray())
		}
		@Override
		Integer getId(Point coordinate) {
			if(coordinate.x == 49 && coordinate.y == 7) return 193186
			if(coordinate.x == 49 && coordinate.y == 8) return 193187
			if(coordinate.x == 50 && coordinate.y == 7) return 193188
			return null
		}
	}

	public static final CoordinateSource coordinateSource = new DummyCoordinateSource();

	public static final coordinate_193186 = coordinateSource.getCoordinate(193186)
	public static final coordinate_193187 = coordinateSource.getCoordinate(193187)
	public static final coordinate_193188 = coordinateSource.getCoordinate(193188)

	public static final ZonedDateTime time_15h = ZonedDateTime.of(2020, 04, 28, 15, 0, 0, 0, ZoneId.of("UTC"))
	public static final ZonedDateTime time_16h = ZonedDateTime.of(2020, 04, 28, 16, 0, 0, 0, ZoneId.of("UTC"))
	public static final ZonedDateTime time_17h = ZonedDateTime.of(2020, 04, 28, 17, 0, 0, 0, ZoneId.of("UTC"))


	public static final WeatherValue weatherVal_coordinate_193186_15h = new WeatherValue(coordinate_193186,
	Quantities.getQuantity(282.671997070312, StandardUnits.IRRADIATION),
	Quantities.getQuantity(286.872985839844, StandardUnits.IRRADIATION),
	Quantities.getQuantity(278.019012451172, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(0, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(1.66103506088257, StandardUnits.WIND_VELOCITY))
	public static final WeatherValue weatherVal_coordinate_193186_16h = new WeatherValue(coordinate_193186,
	Quantities.getQuantity(282.672, StandardUnits.IRRADIATION),
	Quantities.getQuantity(286.872, StandardUnits.IRRADIATION),
	Quantities.getQuantity(278.012, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(0, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(1.662, StandardUnits.WIND_VELOCITY))
	public static final WeatherValue  weatherVal_coordinate_193186_17h = new WeatherValue(coordinate_193186,
	Quantities.getQuantity(282.673, StandardUnits.IRRADIATION),
	Quantities.getQuantity(286.873, StandardUnits.IRRADIATION),
	Quantities.getQuantity(278.013, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(0, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(1.663, StandardUnits.WIND_VELOCITY))

	public static final WeatherValue  weatherVal_coordinate_193187_15h = new WeatherValue(coordinate_193187,
	Quantities.getQuantity(283.671997070312, StandardUnits.IRRADIATION),
	Quantities.getQuantity(287.872985839844, StandardUnits.IRRADIATION),
	Quantities.getQuantity(279.019012451172, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(0, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(1.76103506088257, StandardUnits.WIND_VELOCITY))
	public static final WeatherValue  weatherVal_coordinate_193187_16h = new WeatherValue(coordinate_193187,
	Quantities.getQuantity(283.672, StandardUnits.IRRADIATION),
	Quantities.getQuantity(287.872, StandardUnits.IRRADIATION),
	Quantities.getQuantity(279.012, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(0, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(1.762, StandardUnits.WIND_VELOCITY))

	public static final WeatherValue  weatherVal_coordinate_193188_15h = new WeatherValue(coordinate_193188,
	Quantities.getQuantity(284.671997070312, StandardUnits.IRRADIATION),
	Quantities.getQuantity(288.872985839844, StandardUnits.IRRADIATION),
	Quantities.getQuantity(280.019012451172, StandardUnits.TEMPERATURE),
	Quantities.getQuantity(0, StandardUnits.WIND_DIRECTION),
	Quantities.getQuantity(1.86103506088257, StandardUnits.WIND_VELOCITY))
}
