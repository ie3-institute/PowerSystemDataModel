/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.result

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.io.factory.EntityData
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.result.connector.ConnectorResult
import edu.ie3.datamodel.models.result.connector.LineResult
import edu.ie3.datamodel.models.result.connector.Transformer2WResult
import edu.ie3.datamodel.models.result.connector.Transformer3WResult
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import spock.lang.Specification

class ConnectorResultFactoryTest extends Specification implements FactoryTestHelper {

  def "A ConnectorResultFactory should contain all expected classes for parsing"() {
    given:
    def resultFactory = new ConnectorResultFactory()
    def expectedClasses = [
      LineResult,
      Transformer2WResult,
      Transformer3WResult
    ]

    expect:
    resultFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A ConnectorResultFactory should parse a valid result model correctly"() {
    given: "a connector result factory and model data"
    def resultFactory = new ConnectorResultFactory()
    Map<String, String> parameter = [
      "time"      : "2020-01-30T17:26:44+00:00",
      "inputModel": "91ec3bcf-1777-4d38-af67-0bf7c9fa73c7",
      "iamag"     : "1.0",
      "iaang"     : "90",
      "ibmag"     : "0.98123",
      "ibang"     : "90"
    ]

    if (modelClass == Transformer2WResult) {
      parameter["tappos"] = "3"
    }
    if (modelClass == Transformer3WResult) {
      parameter["tappos"] = "3"
      parameter["icmag"] = "1.0"
      parameter["icang"] = "90"
    }

    when:
    Try<? extends ConnectorResult, FactoryException> result = resultFactory.get(new EntityData(parameter, modelClass))

    then:
    result.success
    result.data.get().getClass() == resultingModelClass
    ((ConnectorResult) result.data.get()).with {
      assert time == TIME_UTIL.toZonedDateTime(parameter["time"])
      assert inputModel == UUID.fromString(parameter["inputModel"])
      assert iAAng == getQuant(parameter["iaang"], StandardUnits.ELECTRIC_CURRENT_ANGLE)
      assert iAMag == getQuant(parameter["iamag"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
      assert iBAng == getQuant(parameter["ibang"], StandardUnits.ELECTRIC_CURRENT_ANGLE)
      assert iBMag == getQuant(parameter["ibmag"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
    }

    if (result.data.get().getClass() == Transformer2WResult) {
      assert ((Transformer2WResult) result.data.get()).tapPos == Integer.parseInt(parameter["tappos"])
    }

    if (result.data.get().getClass() == Transformer3WResult) {
      Transformer3WResult transformer3WResult = ((Transformer3WResult) result.data.get())
      assert transformer3WResult.tapPos == Integer.parseInt(parameter["tappos"])
      assert transformer3WResult.iCAng == getQuant(parameter["icang"], StandardUnits.ELECTRIC_CURRENT_ANGLE)
      assert transformer3WResult.iCMag == getQuant(parameter["icmag"], StandardUnits.ELECTRIC_CURRENT_MAGNITUDE)
    }


    where:
    modelClass          || resultingModelClass
    LineResult          || LineResult
    Transformer2WResult || Transformer2WResult
    Transformer3WResult || Transformer3WResult
  }
}
