/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.input

import static edu.ie3.datamodel.models.StandardUnits.TEMPERATURE
import static edu.ie3.util.quantities.PowerSystemUnits.KELVIN_METRE_PER_WATT
import static edu.ie3.util.quantities.PowerSystemUnits.KILOWATTHOUR_PER_KELVIN_TIMES_CUBICMETRE

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.FieldMap
import edu.ie3.datamodel.models.input.SoilType
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class SoilTypeInputFactoryTest extends Specification implements FactoryTestHelper {
  def "A SoilTypeInputFactory should contain exactly the expected class for parsing"() {
    given:
    def inputFactory = new SoilTypeInputFactory()
    def expectedClasses = [SoilType]

    expect:
    inputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A SoilTypeInputFactory should parse a valid SoilType correctly"() {
    given:
    def inputFactory = new SoilTypeInputFactory()
    Map<String, String> parameter = [
      "uuid"                          : "32b43a78-7721-431d-b1c2-56975a123670",
      "id"                            : "loam",
      "thermalResistivityWet"         : "0.3",
      "thermalResistivityDry"         : "0.4",
      "volumetricHeatCapacity"        : "0.0015",
      "criticalTemperatureDifference" : "15"
    ]
    def inputClass = SoilType

    when:
    Try<SoilType, FactoryException> result =
        inputFactory.get(new FieldMap(parameter))

    then:
    result.success
    result.data.get().getClass() == inputClass
    result.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      id == "loam"
      thermalResistivityWet == getQuant(parameter["thermalResistivityWet"], KELVIN_METRE_PER_WATT)
      thermalResistivityDry == getQuant(parameter["thermalResistivityDry"], KELVIN_METRE_PER_WATT)
      volumetricHeatCapacity == getQuant(parameter["volumetricHeatCapacity"], KILOWATTHOUR_PER_KELVIN_TIMES_CUBICMETRE)
      criticalTemperatureDifference == getQuant(parameter["criticalTemperatureDifference"], TEMPERATURE)
    }
  }

  def "A SoilTypeInputFactory throws FactoryException when a required field is missing"() {
    given:
    def inputFactory = new SoilTypeInputFactory()
    Map<String, String> parameter = [
      "uuid"                          : "32b43a78-7721-431d-b1c2-56975a123670",
      "id"                            : "loam",
      "thermalResistivityWet"         : "0.3",
      "thermalResistivityDry"         : "0.4",
      "volumetricHeatCapacity"        : "0.0015",
      // criticalTemperatureDifference intentionally missing
    ]

    when:
    Try<SoilType, FactoryException> result =
        inputFactory.get(new FieldMap(parameter))

    then:
    result.failure
    result.exception.get() instanceof FactoryException
  }

  def "A SoilTypeInputFactory throws FactoryException for invalid uuid"() {
    given:
    def inputFactory = new SoilTypeInputFactory()
    Map<String, String> parameter = [
      "uuid"                          : "not-a-valid-uuid",
      "id"                            : "loam",
      "thermalResistivityWet"         : "0.3",
      "thermalResistivityDry"         : "0.4",
      "volumetricHeatCapacity"        : "0.0015",
      "criticalTemperatureDifference" : "15"
    ]

    when:
    Try<SoilType, FactoryException> result =
        inputFactory.get(new FieldMap(parameter))

    then:
    result.failure
    result.exception.get() instanceof FactoryException
  }

  def "A SoilTypeInputFactory stores additional information"() {
    given:
    def inputFactory = new SoilTypeInputFactory()
    Map<String, String> parameter = [
      "uuid"                          : "32b43a78-7721-431d-b1c2-56975a123670",
      "id"                            : "loam",
      "thermalResistivityWet"         : "0.3",
      "thermalResistivityDry"         : "0.4",
      "volumetricHeatCapacity"          : "0.0015",
      "criticalTemperatureDifference" : "15",
      "extraField"                    : "extraValue"
    ]

    when:
    Try<SoilType, FactoryException> result =
        inputFactory.get(new FieldMap(parameter))

    then:
    result.success
    result.data.get().additionalInformation["extraField"] == "extraValue"
  }
}
