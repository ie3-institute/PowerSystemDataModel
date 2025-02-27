/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import static edu.ie3.datamodel.models.StandardUnits.*
import static tech.units.indriya.unit.Units.METRE

import edu.ie3.datamodel.exceptions.InvalidEntityException
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.datamodel.utils.Try
import edu.ie3.test.common.GridTestData
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Coordinate
import org.locationtech.jts.geom.LineString
import spock.lang.Specification
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.*

class ConnectorValidationUtilsTest extends Specification {

  def "Instantiating a ConnectorValidationUtil leads to an exception"() {
    when:
    new ConnectorValidationUtils()

    then:
    def e = thrown(IllegalStateException)
    e.message == "Don't try and instantiate a Utility class."
  }

  def "Smoke Test: Correct line throws no exception"() {
    given:
    def line = GridTestData.lineFtoG

    when:
    ValidationUtils.check(line)

    then:
    noExceptionThrown()
  }

  static testCoordinate = GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(10, 10))
  static invalidLineLengthNotMatchingCoordinateDistances = new LineInput(
  UUID.fromString("92ec3bcf-1777-4d38-af67-0bf8c9fa73c7"),
  "test_line_FtoG",
  GridTestData.profBroccoli,
  GridTestData.defaultOperationTime,
  GridTestData.nodeF.copy().geoPosition(GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(7.4116482, 51.4843281))).build(),
  GridTestData.nodeG,
  2,
  GridTestData.lineTypeInputCtoD,
  Quantities.getQuantity(0.003d, LINE_LENGTH),
  GridTestData.geoJsonReader.read("{ \"type\": \"LineString\", \"coordinates\": [[7.4116482, 51.4843281], [3.4116482, 10.4843281], [7.4116482, 51.4843281]]}") as LineString,
  OlmCharacteristicInput.CONSTANT_CHARACTERISTIC
  )

  def "A ConnectorInput needs at least one parallel device"() {
    when:
    def actual = ConnectorValidationUtils.lessThanOneParallelDevice(invalidConnector)

    then:
    actual.failure
    actual.exception.get().class == InvalidEntityException
    actual.exception.get().message.contains(expectedMessage)

    where:
    invalidConnector                                                  || expectedMessage
    GridTestData.lineFtoG.copy().parallelDevices(0).build()           || "LineInput needs to have at least one parallel device"
    GridTestData.lineCtoD.copy().parallelDevices(-1).build()          || "LineInput needs to have at least one parallel device"
    GridTestData.transformerBtoE.copy().parallelDevices(0).build()    || "Transformer2WInput needs to have at least one parallel device"
    GridTestData.transformerAtoBtoC.copy().parallelDevices(0).build() || "Transformer3WInput needs to have at least one parallel device"
  }

  def "ConnectorValidationUtils.checkLine() recognizes all potential errors for a line"() {
    when:
    List<Try<Void, InvalidEntityException>> exceptions = ConnectorValidationUtils.check(invalidLine).stream().filter { it -> it.failure }.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception.get()
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidLine                                                                                                              || expectedSize || expectedException
    GridTestData.lineFtoG.copy().nodeA(GridTestData.nodeG).build()                                                           || 1            || new InvalidEntityException("LineInput connects the same node, but shouldn't", invalidLine)
    GridTestData.lineFtoG.copy().nodeA(GridTestData.nodeF.copy().subnet(5).build()).build()                                  || 1            || new InvalidEntityException("LineInput connects different subnets, but shouldn't", invalidLine)
    GridTestData.lineFtoG.copy().nodeA(GridTestData.nodeF.copy().voltLvl(GermanVoltageLevelUtils.MV_10KV).build()).build()   || 1            || new InvalidEntityException("LineInput connects different voltage levels, but shouldn't", invalidLine)
    GridTestData.lineFtoG.copy().length(Quantities.getQuantity(0d, METRE)).build()                                           || 1            || new InvalidEntityException("The following quantities have to be positive: 0 km", invalidLine)
  }

  def "Smoke Test: Correct line type throws no exception"() {
    given:
    def lineType = GridTestData.lineTypeInputCtoD

    when:
    ValidationUtils.check(lineType)

    then:
    noExceptionThrown()
  }

  // No tests for "ConnectorValidationUtils.checkLineType recognizes all potential errors for a line type", as nothing needs to be tested there

  def "Smoke Test: Correct transformer2W throws no exception"() {
    given:
    def transformer2W = GridTestData.transformerBtoD

    when:
    ValidationUtils.check(transformer2W)

    then:
    noExceptionThrown()
  }

  def "ConnectorValidationUtils.checkTransformer2W recognizes all potential errors for a transformer2W"() {
    when:
    List<Try<Void, InvalidEntityException>> exceptions = ConnectorValidationUtils.check(invalidTransformer2W).stream().filter { it -> it.failure }.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception.get()
    ex.class == expectedException.class
    ex.message == expectedException.message

    where:
    invalidTransformer2W                                                                                                     		|| expectedSize || expectedException
    GridTestData.transformerBtoD.copy().tapPos(100).build()                                                                  		|| 1            || new InvalidEntityException("Tap position of Transformer2WInput is outside of bounds", invalidTransformer2W)
    GridTestData.transformerBtoD.copy().nodeB(GridTestData.nodeD.copy().voltLvl(GermanVoltageLevelUtils.HV).build()).build() 		|| 2            || new InvalidEntityException("Transformer2WInput connects the same voltage level, but shouldn't", invalidTransformer2W)
    GridTestData.transformerBtoD.copy().nodeB(GridTestData.nodeD.copy().subnet(2).build()).build()                           		|| 1            || new InvalidEntityException("Transformer2WInput connects the same subnet, but shouldn't", invalidTransformer2W)
    GridTestData.transformerBtoD.copy().nodeB(GridTestData.nodeD.copy().voltLvl(GermanVoltageLevelUtils.MV_30KV).build()).build() 	|| 1            || new InvalidEntityException("Rated voltages of Transformer2WInput do not equal voltage levels at the nodes", invalidTransformer2W)
  }

  def "Smoke Test: Correct transformer2W type throws no exception"() {
    given:
    def transformer2WType = GridTestData.transformerTypeBtoD

    when:
    ValidationUtils.check(transformer2WType)

    then:
    noExceptionThrown()
  }

  // Data for valid transformer2WType
  private static final UUID uuid = UUID.fromString("202069a7-bcf8-422c-837c-273575220c8a")
  private static final String id = "HS-MS_1"
  private static final ComparableQuantity<ElectricResistance> rSc = Quantities.getQuantity(45.375d, RESISTANCE)
  private static final ComparableQuantity<ElectricResistance> xSc = Quantities.getQuantity(102.759d, REACTANCE)
  private static final ComparableQuantity<Power> sRated = Quantities.getQuantity(20000d, ACTIVE_POWER_IN)
  private static final ComparableQuantity<ElectricPotential> vRatedA = Quantities.getQuantity(110d, RATED_VOLTAGE_MAGNITUDE)
  private static final ComparableQuantity<ElectricPotential> vRatedB = Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE)
  private static final ComparableQuantity<ElectricConductance> gM = Quantities.getQuantity(0d, CONDUCTANCE)
  private static final ComparableQuantity<ElectricConductance> bM = Quantities.getQuantity(0d, SUSCEPTANCE)
  private static final ComparableQuantity<Dimensionless> dV = Quantities.getQuantity(1.5d, DV_TAP)
  private static final ComparableQuantity<Angle> dPhi = Quantities.getQuantity(0d, DPHI_TAP)
  private static final boolean tapSide = false
  private static final int tapNeutr = 0
  private static final int tapMin = -10
  private static final int tapMax = 10

  def "ConnectorValidationUtils.checkTransformer2WType recognizes all potential errors for a transformer2W type"() {
    when:
    ConnectorValidationUtils.check(invalidTransformer2WType)

    then:
    Exception ex = thrown()
    ex.message.contains(expectedException.message)

    where:
    invalidTransformer2WType || expectedException
    new Transformer2WTypeInput(uuid, id, rSc, xSc, sRated, vRatedA, vRatedB, gM, bM, Quantities.getQuantity(-1d, DV_TAP), dPhi, tapSide, tapNeutr, tapMin, tapMax) || new InvalidEntityException("Voltage magnitude increase per tap position must be between 0% and 100%", invalidTransformer2WType)
    new Transformer2WTypeInput(uuid, id, rSc, xSc, sRated, vRatedA, vRatedB, gM, bM, dV, dPhi, tapSide, tapNeutr, 100, tapMax) || new InvalidEntityException("Minimum tap position must be lower than maximum tap position", invalidTransformer2WType)
    new Transformer2WTypeInput(uuid, id, rSc, xSc, sRated, vRatedA, vRatedB, gM, bM, dV, dPhi, tapSide, 100, tapMin, tapMax) || new InvalidEntityException("Neutral tap position must be between minimum and maximum tap position", invalidTransformer2WType)
  }

  def "Smoke Test: Correct transformer3W throws no exception"() {
    given:
    def transformer3W = GridTestData.transformerAtoBtoC

    when:
    ValidationUtils.check(transformer3W)

    then:
    noExceptionThrown()
  }

  def "ConnectorValidationUtils.checkTransformer3W recognizes all potential errors for a transformer3W"() {
    when:
    List<Try<Void, InvalidEntityException>> exceptions = ConnectorValidationUtils.check(invalidTransformer3W).stream().filter { it -> it.failure }.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception.get()
    ex.message == expectedException.message

    where:
    invalidTransformer3W             	                                                                                        		|| expectedSize || expectedException
    GridTestData.transformerAtoBtoC.copy().tapPos(100).build()                                                                  		|| 1            || new InvalidEntityException("Tap position of Transformer3WInput is outside of bounds", invalidTransformer3W)
    GridTestData.transformerAtoBtoC.copy().nodeA(GridTestData.nodeA.copy().voltLvl(GermanVoltageLevelUtils.HV).build()).build() 		|| 2            || new InvalidEntityException("Transformer connects nodes of the same voltage level", invalidTransformer3W)
    GridTestData.transformerAtoBtoC.copy().nodeA(GridTestData.nodeA.copy().subnet(2).build()).build()                           		|| 1            || new InvalidEntityException("Transformer connects nodes in the same subnet", invalidTransformer3W)
    GridTestData.transformerAtoBtoC.copy().nodeC(GridTestData.nodeC.copy().voltLvl(GermanVoltageLevelUtils.MV_30KV).build()).build() 	|| 1            || new InvalidEntityException("Rated voltages of Transformer3WInput do not equal voltage levels at the nodes", invalidTransformer3W)
  }

  def "Smoke Test: Correct transformer3W type throws no exception"() {
    given:
    def transformer3WType = GridTestData.transformerTypeAtoBtoC

    when:
    ValidationUtils.check(transformer3WType)

    then:
    noExceptionThrown()
  }

  // Data for valid transformer3WType (partly already defined above for 2W)
  private static final ComparableQuantity<Power> sRatedA = Quantities.getQuantity(120000d, ACTIVE_POWER_IN)
  private static final ComparableQuantity<Power> sRatedB = Quantities.getQuantity(60000d, ACTIVE_POWER_IN)
  private static final ComparableQuantity<Power> sRatedC = Quantities.getQuantity(40000d, ACTIVE_POWER_IN)
  private static final ComparableQuantity<ElectricPotential> vRatedC = Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE)
  private static final ComparableQuantity<ElectricResistance> rScA = Quantities.getQuantity(0.3d, RESISTANCE)
  private static final ComparableQuantity<ElectricResistance> rScB = Quantities.getQuantity(0.025d, RESISTANCE)
  private static final ComparableQuantity<ElectricResistance> rScC = Quantities.getQuantity(0.0080d, RESISTANCE)
  private static final ComparableQuantity<ElectricResistance> xScA = Quantities.getQuantity(1d, REACTANCE)
  private static final ComparableQuantity<ElectricResistance> xScB = Quantities.getQuantity(0.08d, REACTANCE)
  private static final ComparableQuantity<ElectricResistance> xScC = Quantities.getQuantity(0.003d, REACTANCE)

  def "ConnectorValidationUtils.checkTransformer3WType recognizes all potential errors for a transformer3W type"() {
    when:
    ConnectorValidationUtils.check(invalidTransformer3WType)

    then:
    Exception ex = thrown()
    ex.message.contains(expectedException.message)

    where:
    invalidTransformer3WType || expectedException
    new Transformer3WTypeInput(uuid, id, sRatedA, sRatedB, sRatedC, vRatedA, vRatedB, vRatedC, rScA, rScB, rScC, xScA, xScB, xScC, gM, bM, Quantities.getQuantity(-1d, DV_TAP), dPhi, tapNeutr, tapMin, tapMax) || new InvalidEntityException("Voltage magnitude increase per tap position must be between 0% and 100%", invalidTransformer3WType)
    new Transformer3WTypeInput(uuid, id, sRatedA, sRatedB, sRatedC, vRatedA, vRatedB, vRatedC, rScA, rScB, rScC, xScA, xScB, xScC, gM, bM, dV, dPhi, tapNeutr, 100, tapMax) || new InvalidEntityException("Minimum tap position must be lower than maximum tap position", invalidTransformer3WType)
    new Transformer3WTypeInput(uuid, id, sRatedA, sRatedB, sRatedC, vRatedA, vRatedB, vRatedC, rScA, rScB, rScC, xScA, xScB, xScC, gM, bM, dV, dPhi, 100, tapMin, tapMax) || new InvalidEntityException("Neutral tap position must be between minimum and maximum tap position", invalidTransformer3WType)
  }

  def "Smoke Test: Correct switch throws no exception"() {
    given:
    def switchAtoB = GridTestData.switchAtoB.copy().nodeA(GridTestData.nodeA.copy().voltLvl(GermanVoltageLevelUtils.HV).build()).build()

    when:
    ValidationUtils.check(switchAtoB)

    then:
    noExceptionThrown()
  }

  def "ConnectorValidationUtils.checkSwitch recognizes all potential errors for a switch"() {
    when:
    List<Try<Void, InvalidEntityException>> exceptions = ConnectorValidationUtils.check(invalidSwitch).stream().filter { it -> it.failure }.toList()

    then:
    exceptions.size() == expectedSize
    Exception ex = exceptions.get(0).exception.get()
    ex.message == expectedException.message

    where:
    invalidSwitch           || expectedSize || expectedException
    GridTestData.switchAtoB || 1            || new InvalidEntityException("Switch connects two different voltage levels", invalidSwitch)
  }
}
