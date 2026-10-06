/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.typeinput

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class Transformer2WTypeInputFactoryTest extends Specification implements FactoryTestHelper {

  def "A Transformer2WTypeInputFactory should contain exactly the expected class for parsing"() {
    given:
    def typeInputFactory = new Transformer2WTypeInputFactory()
    def expectedClasses = [Transformer2WTypeInput]

    expect:
    typeInputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A Transformer2WTypeInputFactory should parse a valid Transformer2WTypeInput correctly"() {
    given: "a system participant input type factory and model data"
    def typeInputFactory = new Transformer2WTypeInputFactory()
    Map<String, String> parameter = [
      "uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id": "blablub",
      "rSc": "3",
      "xSc": "4",
      "sRated": "5",
      "vRatedA": "6",
      "vRatedB": "7",
      "gM": "8",
      "bM": "9",
      "dV": "10",
      "dPhi": "11",
      "tapSide": "1",
      "tapNeutr": "12",
      "tapMin": "13",
      "tapMax": "14"
    ]

    when:
    Try<Transformer2WTypeInput, FactoryException> typeInput = typeInputFactory.get(new HashMap<>(parameter))

    then:
    typeInput.success
    typeInput.data.get().getClass() == Transformer2WTypeInput

    typeInput.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      id == parameter["id"]
      rSc == getQuant(parameter["rSc"], StandardUnits.RESISTANCE)
      xSc == getQuant(parameter["xSc"], StandardUnits.REACTANCE)
      sRated == getQuant(parameter["sRated"], StandardUnits.S_RATED)
      vRatedA == getQuant(parameter["vRatedA"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      vRatedB == getQuant(parameter["vRatedB"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      gM == getQuant(parameter["gM"], StandardUnits.CONDUCTANCE)
      bM == getQuant(parameter["bM"], StandardUnits.SUSCEPTANCE)
      dV == getQuant(parameter["dV"], StandardUnits.DV_TAP)
      dPhi == getQuant(parameter["dPhi"], StandardUnits.DPHI_TAP)
      tapSide == (parameter["tapSide"].trim() == "1") || parameter["tapSide"].trim() == "true"
      tapNeutr == Integer.parseInt(parameter["tapNeutr"])
      tapMin == Integer.parseInt(parameter["tapMin"])
      tapMax == Integer.parseInt(parameter["tapMax"])
    }
  }
}
