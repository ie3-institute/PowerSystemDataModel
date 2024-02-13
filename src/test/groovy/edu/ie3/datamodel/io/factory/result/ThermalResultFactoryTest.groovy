/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.result

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult
import edu.ie3.datamodel.models.result.thermal.ThermalUnitResult
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class ThermalResultFactoryTest extends Specification implements FactoryTestHelper {

  def "A ThermalResultFactory should contain all expected classes for parsing"() {
    given:
    def resultFactory = new ThermalResultFactory()
    def expectedClasses = [
      ThermalHouseResult,
      CylindricalStorageResult
    ]

    expect:
    resultFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A ThermalResultFactory should parse a CylindricalStorageResult correctly"() {
    given: "a thermal result factory and model data"
    def resultFactory = new ThermalResultFactory()
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44+01:00",
      "inputModel": "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
      "qDot"      : "2",
      "energy"    : "3",
      "fillLevel" : "20"
    ]
    when:
    Try<? extends ThermalUnitResult, FactoryException> result = resultFactory.get(new EntityData(parameter, CylindricalStorageResult))

    then:
    result.success
    result.data.get().getClass() == CylindricalStorageResult
    ((CylindricalStorageResult) result.data.get()).with {
      assert time == TIME_UTIL.toZonedDateTime(parameter.get("time"))
      assert inputModel == UUID.fromString(parameter.get("inputModel"))
      assert qDot == Quantities.getQuantity(Double.parseDouble(parameter.get("qDot")), StandardUnits.HEAT_DEMAND)
      assert energy == Quantities.getQuantity(Double.parseDouble(parameter.get("energy")), StandardUnits.ENERGY_RESULT)
      assert fillLevel == Quantities.getQuantity(Double.parseDouble(parameter.get("fillLevel")), StandardUnits.FILL_LEVEL)
    }
  }

  def "A ThermalResultFactory should parse a ThermalHouseResult correctly"() {
    given: "a thermal result factory and model data"
    def resultFactory = new ThermalResultFactory()
    HashMap<String, String> parameter = [
      "time"             : "2020-01-30T17:26:44+01:00",
      "inputModel"       : "91ec3bcf-1897-4d38-af67-0bf7c9fa73c7",
      "qDot"             : "2",
      "indoorTemperature": "21"
    ]
    when:
    Try<? extends ThermalUnitResult, FactoryException> result = resultFactory.get(new EntityData(parameter, ThermalHouseResult))

    then:
    result.success
    result.data.get().getClass() == ThermalHouseResult
    ((ThermalHouseResult) result.data.get()).with {
      assert time == TIME_UTIL.toZonedDateTime(parameter.get("time"))
      assert inputModel == UUID.fromString(parameter.get("inputModel"))
      assert qDot == Quantities.getQuantity(Double.parseDouble(parameter.get("qDot")), StandardUnits.HEAT_DEMAND)
      assert indoorTemperature == Quantities.getQuantity(Double.parseDouble(parameter.get("indoorTemperature")), StandardUnits.TEMPERATURE)
    }
  }
}
