/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.value.TemperatureValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.test.common.CosmoWeatherTestData
import edu.ie3.util.TimeUtil
import edu.ie3.util.quantities.PowerSystemUnits
import edu.ie3.util.quantities.QuantityUtil
import org.locationtech.jts.geom.Point
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units

import java.util.Optional

class IconTimeBasedWeatherValueFactoryTest extends Specification {
  def "A time based weather value factory for ICON column scheme determines wind velocity angle correctly"() {
    given:
    def data = new TimeBasedWeatherValueData([
      "u131m": u.toString(),
      "v131m": v.toString(),
    ], Mock(Point))
    def expected = Quantities.getQuantity(expectedValue, PowerSystemUnits.DEGREE_GEOM)

    when:
    def actual = IconTimeBasedWeatherValueFactory.getWindDirection(data)

    then:
    actual.getUnit() == StandardUnits.WIND_DIRECTION
    QuantityUtil.isEquivalentAbs(actual, expected, 1E-10.doubleValue())

    where:
    u    | v    || expectedValue
    0.0  | -5.0 || 0.0
    -5.0 | -5.0 || 45.0
    -5.0 | 0.0  || 90.0
    -5.0 | 5.0  || 135.0
    0.0  | 5.0  || 180.0
    5.0  | 5.0  || 225.0
    5.0  | 0.0  || 270.0
    5.0  | -5.0 || 315.0
  }

  def "A time based weather value factory for ICON column scheme determines wind velocity correctly"() {
    given:
    def data = new TimeBasedWeatherValueData([
      "u131m": u.toString(),
      "v131m": v.toString(),
    ], Mock(Point))
    def expected = Quantities.getQuantity(expectedValue, PowerSystemUnits.METRE_PER_SECOND)

    when:
    def actual = IconTimeBasedWeatherValueFactory.getWindVelocity(data)

    then:
    actual.getUnit() == StandardUnits.WIND_VELOCITY
    QuantityUtil.isEquivalentAbs(actual, expected, 1E-10.doubleValue())

    where:
    u    | v    | w    || expectedValue
    0.0  | -5.0 | 0.0  || 5.0
    -5.0 | -5.0 | 10.0 || 7.071067811865
    -5.0 | 0.0  | 20.0 || 5.0
    -5.0 | 5.0  | 30.0 || 7.071067811865
    0.0  | 5.0  | 40.0 || 5.0
    5.0  | 5.0  | 50.0 || 7.071067811865
    5.0  | 0.0  | 60.0 || 5.0
    5.0  | -5.0 | 70.0 || 7.071067811865
  }

  def "A time based weather value factory for ICON column scheme builds a single time based value correctly"() {
    given:
    def factory = new IconTimeBasedWeatherValueFactory()
    def coordinate = CosmoWeatherTestData.COORDINATE_67775

    def parameter = [
      "time"        : "2019-08-01T01:00:00Z",
      "aswdifdS"    : "1.8088226191406245",
      "aswdirS"     : "2.317613203124999",
      "t2m"         : "289.1179319051744",
      "tG"          : "288.4101691197649",
      "u131m"       : "2.6058700426057797",
      "v131m"       : "3.8391590569599927",
      "coordinateId": "67775"
    ]
    def data = new TimeBasedWeatherValueData(parameter, coordinate)

    when:
    def actual = factory.buildModel(data)

    then:
    actual.with {
      it.time == TimeUtil.withDefaults.toZonedDateTime("2019-08-01T01:00:00Z")
      it.value.coordinate == coordinate
      it.value.directSolar == Quantities.getQuantity(2.317613203124999, StandardUnits.SOLAR_IRRADIANCE)
      it.value.diffSolar == Quantities.getQuantity(1.8088226191406245, StandardUnits.SOLAR_IRRADIANCE)
      QuantityUtil.isEquivalentAbs(it.value.temperature, Quantities.getQuantity(289.1179319051744d, Units.KELVIN).to(StandardUnits.TEMPERATURE), 1e-6)
      QuantityUtil.isEquivalentAbs(it.value.windDir, Quantities.getQuantity(214.16711674907722, StandardUnits.WIND_DIRECTION), 1e-6)
      QuantityUtil.isEquivalentAbs(it.value.windVel, Quantities.getQuantity(4.640010877529081, StandardUnits.WIND_VELOCITY), 1e-6)

      it.value.groundTemperatures.size() == 1
      def expectedGroundTemp = new TemperatureValue(Quantities.getQuantity(288.4101691197649d, Units.KELVIN).to(StandardUnits.TEMPERATURE))
      it.value.groundTemperatures[Quantities.getQuantity(0, Units.METRE)] == expectedGroundTemp
    }
  }

  def "A time based weather value factory for ICON column scheme builds a value with all ground temperatures"() {
    given:
    def factory = new IconTimeBasedWeatherValueFactory()
    def coordinate = CosmoWeatherTestData.COORDINATE_67775

    def parameter = [
      "time"        : "2019-08-01T01:00:00Z",
      "aswdifdS"    : "1.80",
      "aswdirS"     : "2.31",
      "t2m"         : "289.11",
      "tG"          : "288.41",
      "tso100cm"    : "286.5",
      "u131m"       : "2.60",
      "v131m"       : "3.83",
      "coordinateId": "67775"
    ]
    def data = new TimeBasedWeatherValueData(parameter, coordinate)

    when:
    def actual = factory.buildModel(data)

    then:
    actual.with {
      it.value.groundTemperatures.size() == 2
      def expectedSurfaceTemp = new TemperatureValue(Quantities.getQuantity(288.41d, Units.KELVIN).to(StandardUnits.TEMPERATURE))
      def expected1mTemp = new TemperatureValue(Quantities.getQuantity(286.5d, Units.KELVIN).to(StandardUnits.TEMPERATURE))

      it.value.groundTemperatures[Quantities.getQuantity(0, Units.METRE)] == expectedSurfaceTemp
      it.value.groundTemperatures[Quantities.getQuantity(1, Units.METRE)] == expected1mTemp
    }
  }
}