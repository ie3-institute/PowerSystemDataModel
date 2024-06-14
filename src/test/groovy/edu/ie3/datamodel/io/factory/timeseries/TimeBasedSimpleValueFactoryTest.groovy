/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.timeseries

import static edu.ie3.datamodel.io.factory.timeseries.TimeBasedSimpleValueFactory.*

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.timeseries.individual.TimeBasedValue
import edu.ie3.datamodel.models.value.*
import edu.ie3.util.TimeUtil
import spock.lang.Shared
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import java.time.ZoneId

class TimeBasedSimpleValueFactoryTest extends Specification {
  @Shared
  TimeUtil defaultTimeUtil
  def zonedDateTime = TimeUtil.withDefaults.toZonedDateTime("2019-01-01T00:00:00Z")

  def setupSpec() {
    defaultTimeUtil = TimeUtil.withDefaults
  }

  def "The simple time based value factory provides correct fields"() {
    given:
    def factory = new TimeBasedSimpleValueFactory(valueClass)
    def data = Mock(SimpleTimeBasedValueData)
    data.targetClass >> valueClass

    expect:
    factory.getFields(data.targetClass) == expectedFields

    where:
    valueClass       || expectedFields
    EnergyPriceValue || [
      [
        TIME,
        PRICE
      ] as Set
    ]
    SValue           || [
      [
        TIME,
        ACTIVE_POWER,
        REACTIVE_POWER
      ] as Set
    ]
    PValue || [
      [
        TIME,
        ACTIVE_POWER
      ] as Set
    ]
    HeatAndSValue || [
      [
        TIME,
        ACTIVE_POWER,
        REACTIVE_POWER,
        HEAT_DEMAND
      ] as Set
    ]
    HeatAndPValue || [
      [
        TIME,
        ACTIVE_POWER,
        HEAT_DEMAND
      ] as Set
    ]
    HeatDemandValue || [
      [
        TIME,
        HEAT_DEMAND
      ] as Set
    ]
  }

  def "The simple time based value factory throws a FactoryException upon request of fields, if a class is not supported"() {
    given:
    def factory = new TimeBasedSimpleValueFactory(EnergyPriceValue)
    def data = Mock(SimpleTimeBasedValueData)
    data.targetClass >> NodeInput

    when:
    factory.getFields(data.targetClass)

    then:
    def e = thrown(FactoryException)
    e.message == "The given factory cannot handle target class '" + NodeInput + "'."
  }

  def "The simple time based value factory builds correct energy price value"() {
    given:
    def factory = new TimeBasedSimpleValueFactory(EnergyPriceValue)
    def time = zonedDateTime
    def data = new SimpleTimeBasedValueData([
      "uuid": "78ca078a-e6e9-4972-a58d-b2cadbc2df2c",
      "time": defaultTimeUtil.toString(time),
      "price": "52.4"
    ], EnergyPriceValue)
    def expected = new TimeBasedValue(
        time,
        new EnergyPriceValue(Quantities.getQuantity(52.4, StandardUnits.ENERGY_PRICE))
        )

    expect:
    factory.buildModel(data) == expected
  }

  def "The simple time based value factory builds correct heat and apparent power value"() {
    given:
    def factory = new TimeBasedSimpleValueFactory(HeatAndSValue)
    def time = zonedDateTime
    def data = new SimpleTimeBasedValueData([
      "uuid": "78ca078a-e6e9-4972-a58d-b2cadbc2df2c",
      "time": defaultTimeUtil.toString(time),
      "p": "500.0",
      "q": "165.0",
      "heatDemand": "8.0"
    ], HeatAndSValue)
    def expected = new TimeBasedValue(
        time,
        new HeatAndSValue(Quantities.getQuantity(500.0, StandardUnits.ACTIVE_POWER_IN), Quantities.getQuantity(165.0, StandardUnits.REACTIVE_POWER_IN), Quantities.getQuantity(8.0, StandardUnits.HEAT_DEMAND))
        )

    expect:
    factory.buildModel(data) == expected
  }

  def "The simple time based value factory builds correct heat and active power value"() {
    given:
    def factory = new TimeBasedSimpleValueFactory(HeatAndPValue)
    def time = zonedDateTime
    def data = new SimpleTimeBasedValueData([
      "uuid": "78ca078a-e6e9-4972-a58d-b2cadbc2df2c",
      "time": defaultTimeUtil.toString(time),
      "p": "500.0",
      "heatDemand": "8.0"
    ], HeatAndPValue)
    def expected = new TimeBasedValue(
        time,
        new HeatAndPValue(Quantities.getQuantity(500.0, StandardUnits.ACTIVE_POWER_IN), Quantities.getQuantity(8.0, StandardUnits.HEAT_DEMAND))
        )

    expect:
    factory.buildModel(data) == expected
  }

  def "The simple time based value factory builds correct heat demand value"() {
    given:
    def factory = new TimeBasedSimpleValueFactory(HeatDemandValue)
    def time = zonedDateTime
    def data = new SimpleTimeBasedValueData([
      "uuid": "78ca078a-e6e9-4972-a58d-b2cadbc2df2c",
      "time": defaultTimeUtil.toString(time),
      "heatDemand": "8.0"
    ], HeatDemandValue)
    def expected = new TimeBasedValue(
        time,
        new HeatDemandValue(Quantities.getQuantity(8.0, StandardUnits.HEAT_DEMAND))
        )

    expect:
    factory.buildModel(data) == expected
  }

  def "The simple time based value factory builds correct apparent power value"() {
    given:
    def factory = new TimeBasedSimpleValueFactory(SValue)
    def time = zonedDateTime
    def data = new SimpleTimeBasedValueData([
      "uuid": "78ca078a-e6e9-4972-a58d-b2cadbc2df2c",
      "time": defaultTimeUtil.toString(time),
      "p": "500.0",
      "q": "165.0"
    ], SValue)
    def expected = new TimeBasedValue(
        time,
        new SValue(Quantities.getQuantity(500.0, StandardUnits.ACTIVE_POWER_IN), Quantities.getQuantity(165.0, StandardUnits.REACTIVE_POWER_IN))
        )

    expect:
    factory.buildModel(data) == expected
  }

  def "The simple time based value factory builds correct active power value"() {
    given:
    def factory = new TimeBasedSimpleValueFactory(PValue)
    def time = zonedDateTime
    def data = new SimpleTimeBasedValueData([
      "uuid": "78ca078a-e6e9-4972-a58d-b2cadbc2df2c",
      "time": defaultTimeUtil.toString(time),
      "p": "500.0"
    ], PValue)
    def expected = new TimeBasedValue(
        time,
        new PValue(Quantities.getQuantity(500.0, StandardUnits.ACTIVE_POWER_IN))
        )

    expect:
    factory.buildModel(data) == expected
  }

  def "The simple time based value factory throws a FactoryException upon build request, if a class is not supported"() {
    given:
    def factory = new TimeBasedSimpleValueFactory(EnergyPriceValue)
    def time = zonedDateTime
    def data = new SimpleTimeBasedValueData([
      "uuid": "78ca078a-e6e9-4972-a58d-b2cadbc2df2c",
      "time": defaultTimeUtil.toString(time)
    ], NodeInput)

    when:
    factory.buildModel(data)

    then:
    def e = thrown(FactoryException)
    e.message == "The given factory cannot handle target class '" + NodeInput + "'."
  }
}
