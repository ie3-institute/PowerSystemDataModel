/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.io.processor

import edu.ie3.datamodel.exceptions.EntityProcessorException
import edu.ie3.datamodel.models.input.connector.type.CableMaterial
import edu.ie3.datamodel.models.input.connector.type.LayerInput
import edu.ie3.datamodel.utils.Try
import edu.ie3.util.exceptions.QuantityException
import edu.ie3.util.quantities.PowerSystemUnits
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

import javax.measure.Quantity

class ProcessorListCaseTest extends Specification {

  static class LayerEntity {
    List<LayerInput> getLayers() {
      return [
        new LayerInput(
        UUID.fromString("00000000-0000-0000-0000-000000000001"),
        "Main insulation",
        CableMaterial.XLPE,
        Quantities.getQuantity(22.5, PowerSystemUnits.MILLIMETRE),
        Quantities.getQuantity(27.0, PowerSystemUnits.MILLIMETRE),
        Quantities.getQuantity(3.5, PowerSystemUnits.KELVIN_METRE_PER_WATT),
        Quantities.getQuantity(2.4, PowerSystemUnits.JOULE_PER_CUBIC_METRE_KELVIN),
        null)
      ]
    }

    List<String> getNames() {
      return ["a", "b"]
    }
  }

  static class TestProcessor extends Processor<LayerEntity> {
    TestProcessor() {
      super(LayerEntity)
    }

    @Override
    String[] getHeaderElements() {
      return [] as String[]
    }

    @Override
    List<Class<? extends LayerEntity>> getEligibleEntityClasses() {
      return [LayerEntity]
    }

    @Override
    Try<String, QuantityException> handleProcessorSpecificQuantity(Quantity<?> quantity, String fieldName) {
      return Try.Success.of("0")
    }
  }

  def "A Processor serializes a List of LayerInput as a JSON array"() {
    given:
    def processor = new TestProcessor()
    def entity = new LayerEntity()
    def getter = new GetterMethod(LayerEntity.getMethod("getLayers"))

    when:
    def result = processor.processMethodResult(getter.invoke(entity), getter, "layers")

    then:
    def mapper = tools.jackson.databind.json.JsonMapper.builder().build()
    def node = mapper.readTree(result)
    node.isArray()
    node.get(0).get("name").asString() == "Main insulation"
    node.get(0).get("uuid").asString() == "00000000-0000-0000-0000-000000000001"
  }

  def "A Processor throws an EntityProcessorException for a List that is not a list of LayerInput"() {
    given:
    def processor = new TestProcessor()
    def entity = new LayerEntity()
    def getter = new GetterMethod(LayerEntity.getMethod("getNames"))

    when:
    processor.processMethodResult(getter.invoke(entity), getter, "names")

    then:
    EntityProcessorException ex = thrown()
    ex.message.contains("Only lists of LayerInput are supported")
  }
}
