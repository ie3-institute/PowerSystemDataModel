/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.typeinput

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import edu.ie3.datamodel.io.factory.SimpleEntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import spock.lang.Specification

class LineTypeInputFactoryTest extends Specification implements FactoryTestHelper {

  def "A LineTypeInputFactory should contain exactly the expected class for parsing"() {
    given:
    def typeInputFactory = new LineTypeInputFactory()
    def expectedClasses = [LineTypeInput]

    expect:
    typeInputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A LineTypeInputFactory should parse a valid LineTypeInput correctly"() {
    given: "a system participant input type factory and model data"
    def typeInputFactory = new LineTypeInputFactory()
    Map<String, String> parameter = [
      "uuid":     "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":       "blablub",
      "b":        "3",
      "g":        "4",
      "r":        "5",
      "x":        "6",
      "imax":     "7",
      "vrated":   "8"
    ]
    def typeInputClass = LineTypeInput

    when:
    Try<LineTypeInput, FactoryException> typeInput = typeInputFactory.get(new SimpleEntityData(parameter, typeInputClass))

    then:
    typeInput.success
    typeInput.data.get().getClass() == typeInputClass
    typeInput.data.get().with {
      assert uuid == UUID.fromString(parameter["uuid"])
      assert id == parameter["id"]
      assert b == getQuant(parameter["b"], StandardUnits.SUSCEPTANCE_PER_LENGTH)
      assert g == getQuant(parameter["g"], StandardUnits.CONDUCTANCE_PER_LENGTH)
      assert r == getQuant(parameter["r"], StandardUnits.RESISTANCE_PER_LENGTH)
      assert x == getQuant(parameter["x"], StandardUnits.REACTANCE_PER_LENGTH)
      assert iMax == getQuant(parameter["imax"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
      assert vRated == getQuant(parameter["vrated"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
    }
  }
}
