/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.models.value

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Coordinate
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class WeatherValueTest extends Specification {

  @Shared
  def coordinate = GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(7.4116482, 51.4843281))

  private static WeatherValue weatherValue(coordinate, int coordinateId) {
    return new WeatherValue(
        coordinate,
        coordinateId,
        new SolarIrradianceValue(
            Quantities.getQuantity(5d, StandardUnits.SOLAR_IRRADIANCE),
            Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE)),
        new TemperatureValue(Quantities.getQuantity(5d, StandardUnits.TEMPERATURE)),
        new WindValue(
            Quantities.getQuantity(5d, StandardUnits.WIND_DIRECTION),
            Quantities.getQuantity(10d, StandardUnits.WIND_VELOCITY)),
        Optional.empty(),
        Optional.empty())
  }

  def "A WeatherValue without a coordinate id hands back an empty option"() {
    given:
    def withoutId = new WeatherValue(
        coordinate,
        new SolarIrradianceValue(
            Quantities.getQuantity(5d, StandardUnits.SOLAR_IRRADIANCE),
            Quantities.getQuantity(10d, StandardUnits.SOLAR_IRRADIANCE)),
        new TemperatureValue(Quantities.getQuantity(5d, StandardUnits.TEMPERATURE)),
        new WindValue(
            Quantities.getQuantity(5d, StandardUnits.WIND_DIRECTION),
            Quantities.getQuantity(10d, StandardUnits.WIND_VELOCITY)),
        Optional.empty(),
        Optional.empty())

    expect:
    withoutId.coordinateId == OptionalInt.empty()
    weatherValue(coordinate, 193186).coordinateId == OptionalInt.of(193186)
    weatherValue(coordinate, WeatherValue.UNKNOWN_COORDINATE_ID).coordinateId == OptionalInt.empty()
  }

  def "The coordinate id of a WeatherValue takes part in equals and hashCode"() {
    given:
    def first = weatherValue(coordinate, 193186)
    def second = weatherValue(coordinate, 193186)
    def other = weatherValue(coordinate, 193187)
    def unknown = weatherValue(coordinate, WeatherValue.UNKNOWN_COORDINATE_ID)

    expect:
    first == second
    first.hashCode() == second.hashCode()

    first != other
    first != unknown
  }
}
