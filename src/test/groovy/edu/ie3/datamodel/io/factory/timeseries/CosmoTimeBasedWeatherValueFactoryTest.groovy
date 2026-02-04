/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.WeatherValue
import edu.ie3.test.common.CosmoWeatherTestData
import edu.ie3.util.TimeUtil
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities
def
class CosmoTimeBasedWeatherValueFactoryTest extends Specification {

  def "A PsdmTimeBasedWeatherValueFactory should throw an Exception if a required field is empty"() {
    given:
    def factory = new CosmoTimeBasedWeatherValueFactory()
    def coordinate = CosmoWeatherTestData.COORDINATE_193186

    Map<String, String> parameter = [
      "time"                    : "2019-01-01T00:00:00Z",
      "diffuseIrradiance"       : "282.671997070312",
      "directIrradiance"        : "286.872985839844",
      "temperature"             : "",
      "windDirection"           : "0",
      "windVelocity"            : "1.66103506088257",
      "groundTemperatureLevel1" : "",
      "groundTemperatureLevel2" : ""
    ]

    def data = new TimeBasedWeatherValueData(parameter, coordinate)

    when:
    factory.buildModel(data)

    then:
    def exception = thrown(FactoryException)
    exception.message.toLowerCase().contains("temperature")
  }


  def "A PsdmTimeBasedWeatherValueFactory should be able to create time series values"() {
    given:
    def factory = new CosmoTimeBasedWeatherValueFactory()
    def coordinate = CosmoWeatherTestData.COORDINATE_193186
    def time = TimeUtil.withDefaults.toZonedDateTime("2019-01-01T00:00:00Z")

    Map<String, String> parameter = [
      "time"                   : TimeUtil.withDefaults.toString(time),
      "diffuseIrradiance"      : "282.671997070312",
      "directIrradiance"       : "286.872985839844",
      "temperature"            : "278.019012451172",
      "windDirection"          : "0",
      "windVelocity"           : "1.66103506088257",
      "groundTemperatureLevel1": "278.019012451172",
      "groundTemperatureLevel2": ""
    ]

    def data = new TimeBasedWeatherValueData(parameter, coordinate)

    def expectedResults = new TimeBasedValue(
        time, new WeatherValue(coordinate,
        Quantities.getQuantity(286.872985839844d, StandardUnits.SOLAR_IRRADIANCE),
        Quantities.getQuantity(282.671997070312d, StandardUnits.SOLAR_IRRADIANCE),
        Quantities.getQuantity(278.019012451172d, StandardUnits.TEMPERATURE),
        Quantities.getQuantity(0d, StandardUnits.WIND_DIRECTION),
        Quantities.getQuantity(1.66103506088257d, StandardUnits.WIND_VELOCITY),
        Optional.of(Quantities.getQuantity(278.019012451172d, StandardUnits.TEMPERATURE)),
        Optional.empty()))

    when:
    def model = factory.buildModel(data)

    then:
    Objects.equals(model,expectedResults)
  }

  def "A PsdmTimeBasedWeatherValueFactory should throw FactoryException if required field is missing"() {
    given:
    def factory = new CosmoTimeBasedWeatherValueFactory()
    def coordinate = CosmoWeatherTestData.COORDINATE_193186
    def time = TimeUtil.withDefaults.toZonedDateTime("2019-01-01T00:00:00Z")

    // Missing 'directIrradiance' field
    Map<String, String> parameter = [
      "time"             : TimeUtil.withDefaults.toString(time),
      "diffuseIrradiance": "182.671997070312",
      "temperature"      : "278.019012451172",
      "windDirection"    : "50",
      "windVelocity"     : "1.66103506088257"
    ]

    def data = new TimeBasedWeatherValueData(parameter, coordinate)

    when:
    factory.buildModel(data)

    then:
    thrown(FactoryException)
  }

  def "Smoke Test: This PsdmTimeBasedWeatherValueFactory should fail since expected results doesn't match input"() {
    given:
    def factory = new CosmoTimeBasedWeatherValueFactory()
    def coordinate = CosmoWeatherTestData.COORDINATE_193186
    def time = TimeUtil.withDefaults.toZonedDateTime("2019-01-01T00:00:00Z")

    Map<String, String> parameter = [
      "time"             : TimeUtil.withDefaults.toString(time),
      "diffuseIrradiance": "1.0",
      "directIrradiance" : "2.0",
      "temperature"      : "3.0",
      "windDirection"    : "4",
      "windVelocity"     : "5.0"
    ]

    def data = new TimeBasedWeatherValueData(parameter, coordinate)

    def expectedResults = new TimeBasedValue(
        time, new WeatherValue(coordinate,
        Quantities.getQuantity(5.0, StandardUnits.SOLAR_IRRADIANCE),
        Quantities.getQuantity(4.0, StandardUnits.SOLAR_IRRADIANCE),
        Quantities.getQuantity(3.0, StandardUnits.TEMPERATURE),
        Quantities.getQuantity(2d, StandardUnits.WIND_DIRECTION),
        Quantities.getQuantity(1.0, StandardUnits.WIND_VELOCITY),
        Optional.empty(),
        Optional.empty()))

    when:
    def model = factory.buildModel(data)

    then:
    !Objects.equals(model,expectedResults)
  }
}
