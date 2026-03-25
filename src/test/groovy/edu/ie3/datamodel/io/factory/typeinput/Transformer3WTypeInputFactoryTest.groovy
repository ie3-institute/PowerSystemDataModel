/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.typeinput

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.EntityData
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
      "uuid":	    "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "id":   	"blablub",
      "srateda":	"3",
      "sratedb":	"4",
      "sratedc":	"5",
      "vrateda":	"6",
      "vratedb":	"7",
      "vratedc":	"8",
      "rsca":	    "9",
      "rscb":	    "10",
      "rscc":	    "11",
      "xsca":	    "12",
      "xscb":	    "13",
      "xscc":	    "14",
      "gm":	    "15",
      "bm":	    "16",
      "dv":   	"17",
      "dphi":	    "18",
      "tapneutr":	"19",
      "tapmin":	"20",
      "tapmax":	"21"
    ]
    def typeInputClass = Transformer3WTypeInput

    when:
    Try<Transformer3WTypeInput, FactoryException> typeInput = typeInputFactory.get(new EntityData(parameter, typeInputClass))

    then:
    typeInput.success
    typeInput.data.get().getClass() == typeInputClass

    typeInput.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      id == parameter["id"]
      sRatedA == getQuant(parameter["srateda"], StandardUnits.S_RATED)
      sRatedB == getQuant(parameter["sratedb"], StandardUnits.S_RATED)
      sRatedC == getQuant(parameter["sratedc"], StandardUnits.S_RATED)
      vRatedA == getQuant(parameter["vrateda"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      vRatedB == getQuant(parameter["vratedb"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      vRatedC == getQuant(parameter["vratedc"], StandardUnits.RATED_VOLTAGE_MAGNITUDE)
      rScA == getQuant(parameter["rsca"], StandardUnits.RESISTANCE)
      rScB == getQuant(parameter["rscb"], StandardUnits.RESISTANCE)
      rScC == getQuant(parameter["rscc"], StandardUnits.RESISTANCE)
      xScA == getQuant(parameter["xsca"], StandardUnits.REACTANCE)
      xScB == getQuant(parameter["xscb"], StandardUnits.REACTANCE)
      xScC == getQuant(parameter["xscc"], StandardUnits.REACTANCE)
      gM == getQuant(parameter["gm"], StandardUnits.CONDUCTANCE)
      bM == getQuant(parameter["bm"], StandardUnits.SUSCEPTANCE)
      dV == getQuant(parameter["dv"], StandardUnits.DV_TAP)
      dPhi == getQuant(parameter["dphi"], StandardUnits.DPHI_TAP)
      tapNeutr == Integer.parseInt(parameter["tapneutr"])
      tapMin == Integer.parseInt(parameter["tapmin"])
      tapMax == Integer.parseInt(parameter["tapmax"])
    }
  }
}