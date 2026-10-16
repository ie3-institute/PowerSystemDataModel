/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.typeinput

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class Transformer3WTypeInputFactoryTest extends Specification implements FactoryTestHelper {

  def "A Transformer3WTypeInputFactory should contain exactly the expected class for parsing"() {
    given:
    def typeInputFactory = new Transformer3WTypeInputFactory()
    def expectedClasses = [Transformer3WTypeInput]

    expect:
    typeInputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A Transformer3WTypeInputFactory should parse a valid Transformer2WTypeInput correctly"() {
    given: "a system participant input type factory and model data"
    def typeInputFactory = new Transformer3WTypeInputFactory()
    Map<String, String> parameter = [
      "uuid": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id": "blablub",
      "sRatedA": "3",
      "sRatedB": "4",
      "sRatedC": "5",
      "vRatedA": "6",
      "vRatedB": "7",
      "vRatedC": "8",
      "rScA": "9",
      "rScB": "10",
      "rScC": "11",
      "xScA": "12",
      "xScB": "13",
      "xScC": "14",
      "gM": "15",
      "bM": "16",
      "dV": "17",
      "dPhi": "18",
      "tapNeutr": "19",
      "tapMin": "20",
      "tapMax": "21"
    ]

    when:
    Try<Transformer3WTypeInput, FactoryException> typeInput = typeInputFactory.get(new HashMap<>(parameter))

    then:
    typeInput.success
    typeInput.data.get().getClass() == Transformer3WTypeInput

    typeInput.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      id == parameter["id"]
      sRatedA == getQuant(parameter["sRatedA"], StandardUnits.S_RATED)
      sRatedB == getQuant(parameter["sRatedB"], StandardUnits.S_RATED)
      sRatedC == getQuant(parameter["sRatedC"], StandardUnits.S_RATED)
      vRatedA == getQuant(parameter["vRatedA"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      vRatedB == getQuant(parameter["vRatedB"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      vRatedC == getQuant(parameter["vRatedC"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      rScA == getQuant(parameter["rScA"], StandardUnits.RESISTANCE)
      rScB == getQuant(parameter["rScB"], StandardUnits.RESISTANCE)
      rScC == getQuant(parameter["rScC"], StandardUnits.RESISTANCE)
      xScA == getQuant(parameter["xScA"], StandardUnits.REACTANCE)
      xScB == getQuant(parameter["xScB"], StandardUnits.REACTANCE)
      xScC == getQuant(parameter["xScC"], StandardUnits.REACTANCE)
      gM == getQuant(parameter["gM"], StandardUnits.CONDUCTANCE)
      bM == getQuant(parameter["bM"], StandardUnits.SUSCEPTANCE)
      dV == getQuant(parameter["dV"], StandardUnits.DV_TAP)
      dPhi == getQuant(parameter["dPhi"], StandardUnits.DPHI_TAP)
      tapNeutr == Integer.parseInt(parameter["tapNeutr"])
      tapMin == Integer.parseInt(parameter["tapMin"])
      tapMax == Integer.parseInt(parameter["tapMax"])
    }
  }
}