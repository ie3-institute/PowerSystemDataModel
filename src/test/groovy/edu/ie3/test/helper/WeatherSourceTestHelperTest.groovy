/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.helper

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.timeseries.individual.IndividualTimeSeries
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.GroundTemperatureValue
import edu.ie3.datamodel.models.value.SolarIrradianceValue
import edu.ie3.datamodel.models.value.TemperatureValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.datamodel.models.value.WindValue
import edu.ie3.util.TimeUtil
import edu.ie3.util.geo.GeoUtils
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZonedDateTime

class WeatherSourceTestHelperTest extends Specification implements WeatherSourceTestHelper {

  @Shared
  ZonedDateTime time = TimeUtil.withDefaults.toZonedDateTime("2020-04-28T15:00:00+00:00")

  @Shared
  def coordinate = GeoUtils.buildPoint(7.4116482, 51.4843281)

  @Shared
  WeatherValue baseWeather

  def setup() {
    def directIrr = Quantities.getQuantity(100d, StandardUnits.SOLAR_IRRADIANCE)
    def diffuseIrr = Quantities.getQuantity(50d, StandardUnits.SOLAR_IRRADIANCE)
    def solar = new SolarIrradianceValue(directIrr, diffuseIrr)

    def tempVal = new TemperatureValue(Quantities.getQuantity(20d, StandardUnits.TEMPERATURE))

    def windDir = Quantities.getQuantity(1.5d, StandardUnits.WIND_DIRECTION)
    def windVel = Quantities.getQuantity(5d, StandardUnits.WIND_VELOCITY)
    def wind = new WindValue(windDir, windVel)

    def ground1 = new GroundTemperatureValue(Quantities.getQuantity(10d, StandardUnits.TEMPERATURE))
    def ground2 = new GroundTemperatureValue(Quantities.getQuantity(12d, StandardUnits.TEMPERATURE))

    baseWeather = new WeatherValue(
        coordinate,
        solar,
        tempVal,
        wind,
        Optional.of(ground1),
        Optional.of(ground2)
        )
  }

  def "The WeatherSourceTestHelper compares two identical single TimeBasedValues correctly"() {
    given:
    def tbv1 = new TimeBasedValue(time, baseWeather)
    def tbv2 = new TimeBasedValue(time, baseWeather)

    expect:
    equalsIgnoreUUID(tbv1, tbv2)
  }

  def "The WeatherSourceTestHelper fails on single values with different timestamps"() {
    given:
    def tbv1 = new TimeBasedValue(time, baseWeather)
    def tbv2 = new TimeBasedValue(time.plusHours(1), baseWeather)

    expect:
    !equalsIgnoreUUID(tbv1, tbv2)
  }

  def "The WeatherSourceTestHelper respects the tolerance of 1E-10 for mandatory values"() {
    given:
    def tbv1 = new TimeBasedValue(time, baseWeather)

    def createWeatherWithIrr = { double directVal ->
      new WeatherValue(
          coordinate,
          new SolarIrradianceValue(
          Quantities.getQuantity(directVal, StandardUnits.SOLAR_IRRADIANCE),
          baseWeather.solarIrradiance.diffuseIrradiance.get()
          ),
          baseWeather.temperature,
          baseWeather.wind,
          baseWeather.groundTemperatureLevel1,
          baseWeather.groundTemperatureLevel2
          )
    }

    def weatherWithinTolerance = createWeatherWithIrr(100d + 0.5E-10)
    def weatherOutsideTolerance = createWeatherWithIrr(100d + 2E-10)

    def tbvWithin = new TimeBasedValue(time, weatherWithinTolerance)
    def tbvOutside = new TimeBasedValue(time, weatherOutsideTolerance)

    expect:
    equalsIgnoreUUID(tbv1, tbvWithin)
    !equalsIgnoreUUID(tbv1, tbvOutside)
  }

  def "The WeatherSourceTestHelper detects differences in optional ground temperatures"() {
    given:
    def tbv1 = new TimeBasedValue(time, baseWeather)

    def groundDiff = new GroundTemperatureValue(Quantities.getQuantity(99d, StandardUnits.TEMPERATURE))
    def weatherDiffGround = new WeatherValue(
        coordinate,
        baseWeather.solarIrradiance,
        baseWeather.temperature,
        baseWeather.wind,
        Optional.of(groundDiff),
        baseWeather.groundTemperatureLevel2
        )

    def weatherMissingGround = new WeatherValue(
        coordinate,
        baseWeather.solarIrradiance,
        baseWeather.temperature,
        baseWeather.wind,
        Optional.empty(),
        baseWeather.groundTemperatureLevel2
        )

    def tbvDiff = new TimeBasedValue(time, weatherDiffGround)
    def tbvMissing = new TimeBasedValue(time, weatherMissingGround)

    expect:
    !equalsIgnoreUUID(tbv1, tbvDiff)
    !equalsIgnoreUUID(tbv1, tbvMissing)
  }

  def "The WeatherSourceTestHelper compares Collections correctly"() {
    given:
    def tbv1 = new TimeBasedValue(time, baseWeather)
    def tbv2 = new TimeBasedValue(time.plusHours(1), baseWeather)

    def listA = [tbv1, tbv2]
    def listB = [tbv1, tbv2]
    def listMixedOrder = [tbv2, tbv1]
    def listShort = [tbv1]
    def listDiffContent = [
      tbv1,
      new TimeBasedValue(time.plusHours(2), baseWeather)
    ]

    expect:
    equalsIgnoreUUID(listA, listB)
    equalsIgnoreUUID(listA, listMixedOrder)
    !equalsIgnoreUUID(listA, listShort)
    !equalsIgnoreUUID(listA, listDiffContent)
  }

  def "The WeatherSourceTestHelper handles null Collections gracefully"() {
    expect:
    equalsIgnoreUUID((Collection)null, (Collection)null)
    !equalsIgnoreUUID([], null)
    !equalsIgnoreUUID(null, [])
  }

  def "The WeatherSourceTestHelper compares IndividualTimeSeries correctly"() {
    given:
    def tbv = new TimeBasedValue(time, baseWeather)
    def ts1 = new IndividualTimeSeries(UUID.randomUUID(), [tbv] as Set)
    def ts2 = new IndividualTimeSeries(UUID.randomUUID(), [tbv] as Set)
    def tsEmpty = new IndividualTimeSeries(UUID.randomUUID(), [] as Set)

    expect:
    equalsIgnoreUUID(ts1, ts2)
    !equalsIgnoreUUID(ts1, tsEmpty)
  }
}