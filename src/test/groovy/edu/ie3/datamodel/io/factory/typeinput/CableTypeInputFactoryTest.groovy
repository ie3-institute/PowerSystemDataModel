/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.factory.typeinput

import edu.ie3.datamodel.exceptions.FactoryException
import edu.ie3.datamodel.models.StandardUnits
import edu.ie3.datamodel.models.input.connector.type.CableMaterial
import edu.ie3.datamodel.models.input.connector.type.CableTypeInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.helper.FactoryTestHelper
import edu.ie3.util.quantities.PowerSystemUnits
import edu.ie3.util.quantities.interfaces.SpecificCapacitance
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities
import tech.units.indriya.unit.ProductUnit
import tech.units.indriya.unit.Units

import javax.measure.Unit

class CableTypeInputFactoryTest extends Specification implements FactoryTestHelper {

  static final Unit<SpecificCapacitance> FARAD_PER_METRE =
  new ProductUnit<>(Units.FARAD.divide(Units.METRE))

  def "A CableTypeInputFactory should contain exactly the expected class for parsing"() {
    given:
    def typeInputFactory = new CableTypeInputFactory()
    def expectedClasses = [CableTypeInput]

    expect:
    typeInputFactory.supportedClasses == Arrays.asList(expectedClasses.toArray())
  }

  def "A CableTypeInputFactory should parse a valid CableTypeInput correctly"() {
    given: "a cable type input factory and model data"
    def typeInputFactory = new CableTypeInputFactory()
    Map<String, String> parameter = [
      "uuid": "994dcc32-d6ec-4d0f-9941-7c25be942aa6",
      "id": "test cable type input",
      "coreNumber": "1",
      "conductor": '{"id":"conductor","uuid":"fbf23859-b88f-58d5-8b54-4b9468c7ab60","material":"COPPER","crossSection":"240.0","diameter":"18.4","thermalResistivity":"0.0026","thermalCapacitance":"3.4e6","area":"240.0","isCompacted":false}',
      "isolation": '[{"id":"insulation","uuid":"b13f4943-ab7c-53eb-a9d1-e4711f8ff4ba","material":"XLPE","innerDiameter":"19.4","outerDiameter":"34.8","thermalResistivity":"3.5","thermalCapacitance":"2.4e6","area":null}]',
      "screen": '{"id":"screen","uuid":"73dde224-622d-52ec-9c58-2ea442720175","material":"COPPER","innerDiameter":"36.8","outerDiameter":"38.6","thermalResistivity":"0.0026","thermalCapacitance":"3.4e6","area":"35.62566","wiresNumber":56,"wireDiameter":"0.9","electricalResistivity":"1.7241e-8"}',
      "filler": "",
      "armor": "",
      "jack": "",
      "limitTemperature": "90.0",
      "frequency": "50.0",
      "skinEffectCoefficient": "1.0",
      "proximityEffectCoefficient": "1.0",
      "electricalCapacitance": "2.37683304e-10",
      "tanDelta": "0.004",
      "circulatingLossFactor": "0.0435122656",
      "eddyCurrentLossFactor": "0.0",
    ]

    when:
    Try<CableTypeInput, FactoryException> typeInput = typeInputFactory.get(new HashMap<>(parameter))

    then:
    typeInput.success
    typeInput.data.get().getClass() == CableTypeInput
    typeInput.data.get().with {
      uuid == UUID.fromString(parameter["uuid"])
      id == parameter["id"]
      coreNumber == 1
      conductor.id == "conductor"
      conductor.material == CableMaterial.COPPER
      conductor.crossSection.to(PowerSystemUnits.SQUARE_MILLIMETRE).value == 240.0d
      conductor.diameter.to(PowerSystemUnits.MILLIMETRE).value == 18.4d
      isolation.size() == 1
      isolation[0].id == "insulation"
      screen.isPresent()
      screen.get().id == "screen"
      screen.get().wiresNumber == 56
      filler.isEmpty()
      armor.isEmpty()
      jack.isEmpty()
      limitTemperature == Quantities.getQuantity(90.0, StandardUnits.TEMPERATURE)
      frequency == Quantities.getQuantity(50.0, Units.HERTZ)
      skinEffectCoefficient == 1.0d
      proximityEffectCoefficient == 1.0d
      electricalCapacitance ==
          Quantities.getQuantity(2.37683304e-10, FARAD_PER_METRE)
      tanDelta == 0.004d
      circulatingLossFactor == 0.0435122656d
      eddyCurrentLossFactor == 0.0d
    }
  }

  def "A CableTypeInputFactory with invalid conductor JSON fails with a FactoryException"() {
    given:
    def typeInputFactory = new CableTypeInputFactory()
    Map<String, String> parameter = [
      "uuid": "994dcc32-d6ec-4d0f-9941-7c25be942aa6",
      "id": "invalid cable type",
      "coreNumber": "1",
      "conductor": "{this is not valid json",
      "isolation": "",
      "screen": "",
      "filler": "",
      "armor": "",
      "jack": "",
      "limitTemperature": "90.0",
      "frequency": "50.0",
      "skinEffectCoefficient": "1.0",
      "proximityEffectCoefficient": "1.0",
      "electricalCapacitance": "2.37683304e-10",
      "tanDelta": "0.004",
      "circulatingLossFactor": "0.0435122656",
      "eddyCurrentLossFactor": "0.0",
    ]

    when:
    typeInputFactory.get(parameter).getOrThrow()

    then:
    FactoryException e = thrown()
    e.cause instanceof IllegalArgumentException
    e.cause.message.contains("invalid cable component JSON")
  }
}
