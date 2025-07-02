/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.TemperatureValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.test.common.CosmoWeatherTestData
import edu.ie3.util.TimeUtil
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.Units

import java.util.Optional

class CosmoTimeBasedWeatherValueFactoryTest extends Specification {

  def "A CosmoTimeBasedWeatherValueFactory creates values correctly when optional ground temperatures are missing"() {
    given:
    def factory = new CosmoTimeBasedWeatherValueFactory(TimeUtil.withDefaults)
    def coordinate = CosmoWeatherTestData.COORDINATE_193186
    def time = TimeUtil.withDefaults.toZonedDateTime("2019-01-01T00:00:00Z")

    Map<String, String> parameter = [
      "time"             : TimeUtil.withDefaults.toString(time),
      "uuid"             : "980f7714-8def-479f-baae-4deed6c8d6d1",
      "coordinateId"     : "193186",
      "diffuseIrradiance": "282.671997070312",
      "directIrradiance" : "286.872985839844",
      "temperature"      : "278.019012451172",
      "windDirection"    : "0",
      "windVelocity"     : "1.66103506088257"
    ]

    def data = new TimeBasedWeatherValueData(parameter, coordinate)

    def expectedResults = new TimeBasedValue(
        time, new WeatherValue(coordinate,
        Quantities.getQuantity(286.872985839844d, StandardUnits.SOLAR_IRRADIANCE),
        Quantities.getQuantity(282.671997070312d, StandardUnits.SOLAR_IRRADIANCE),
        Quantities.getQuantity(278.019012451172d, StandardUnits.TEMPERATURE),
        Quantities.getQuantity(0d, StandardUnits.WIND_DIRECTION),
        Quantities.getQuantity(1.66103506088257d, StandardUnits.WIND_VELOCITY),
        Collections.emptyMap()))

    when:
    def model = factory.buildModel(data)

    then:
    model == expectedResults
  }

  def "A CosmoTimeBasedWeatherValueFactory creates values correctly when ground temperatures are present"() {
    given:
    def factory = new CosmoTimeBasedWeatherValueFactory(TimeUtil.withDefaults)
    def coordinate = CosmoWeatherTestData.COORDINATE_193186
    def time = TimeUtil.withDefaults.toZonedDateTime("2019-01-01T00:00:00Z")

    Map<String, String> parameter = [
      "time"                    : TimeUtil.withDefaults.toString(time),
      "uuid"                    : "980f7714-8def-479f-baae-4deed6c8d6d1",
      "coordinateId"            : "193186",
      "diffuseIrradiance"       : "282.671997070312",
      "directIrradiance"        : "286.872985839844",
      "temperature"             : "278.019012451172",
      "windDirection"           : "0",
      "windVelocity"            : "1.66103506088257",
      "groundTemperatureSurface": "275.5",
      "groundTemperature1m"     : "279.0"
    ]

    def data = new TimeBasedWeatherValueData(parameter, coordinate)

    def expectedGroundTemps = [
      (Quantities.getQuantity(0, Units.METRE)): new TemperatureValue(Quantities.getQuantity(275.5d, StandardUnits.TEMPERATURE)),
      (Quantities.getQuantity(1, Units.METRE)): new TemperatureValue(Quantities.getQuantity(279.0d, StandardUnits.TEMPERATURE))
    ]

    def expectedResults = new TimeBasedValue(
        time, new WeatherValue(coordinate,
        Quantities.getQuantity(286.872985839844d, StandardUnits.SOLAR_IRRADIANCE),
        Quantities.getQuantity(282.671997070312d, StandardUnits.SOLAR_IRRADIANCE),
        Quantities.getQuantity(278.019012451172d, StandardUnits.TEMPERATURE),
        Quantities.getQuantity(0d, StandardUnits.WIND_DIRECTION),
        Quantities.getQuantity(1.66103506088257d, StandardUnits.WIND_VELOCITY),
        expectedGroundTemps))

    when:
    def model = factory.buildModel(data)

    then:
    model == expectedResults
  }
}