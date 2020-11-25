/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import org.locationtech.jts.geom.LineString
import tech.units.indriya.ComparableQuantity

import javax.measure.quantity.Angle
import javax.measure.quantity.Dimensionless
import javax.measure.quantity.ElectricConductance
import javax.measure.quantity.ElectricPotential
import javax.measure.quantity.ElectricResistance
import javax.measure.quantity.Power

import static edu.ie3.datamodel.models.StandardUnits.*
import static edu.ie3.util.quantities.PowerSystemUnits.*

import edu.ie3.datamodel.exceptions.InvalidEntityException
import edu.ie3.test.common.GridTestData
import edu.ie3.util.geo.GeoUtils
import org.locationtech.jts.geom.Coordinate
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class ConnectorValidationUtilsTest extends Specification {

	def "Smoke Test: Correct line throws no exception"() {
		given:
		def line = GridTestData.lineFtoG

		when:
		ValidationUtils.check(line)

		then:
		noExceptionThrown()
	}

	def "ConnectorValidationUtils.checkLine() recognizes all potential errors for a line"() {
		when:
		ConnectorValidationUtils.check(invalidLine)

		then:
		Exception ex = thrown()
		ex.class == expectedException.class
		ex.message == expectedException.message

		where:
		invalidLine                                                                                                                                                                                   || expectedException
		GridTestData.lineFtoG.copy().nodeA(GridTestData.nodeG).build()                                                                                                                                || new InvalidEntityException("Line connects the same node", invalidLine)
		GridTestData.lineFtoG.copy().nodeA(GridTestData.nodeF.copy().subnet(5).build()).build()                                                                                                       || new InvalidEntityException("Line connects different subnets", invalidLine)
		GridTestData.lineFtoG.copy().nodeA(GridTestData.nodeF.copy().voltLvl(GermanVoltageLevelUtils.MV_10KV).build()).build()                                                                        || new InvalidEntityException("Line connects different voltage levels", invalidLine)
		GridTestData.lineFtoG.copy().length(Quantities.getQuantity(0d, METRE)).build()                                                                                                                || new InvalidEntityException("Line has a zero or negative length", invalidLine)
		GridTestData.lineFtoG.copy().nodeA(GridTestData.nodeF.copy().geoPosition(GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(10, 10))).build()).build()                              || new InvalidEntityException("Coordinates of start and end point do not match coordinates of connected nodes", invalidLine)
		GridTestData.lineFtoG.copy().nodeB(GridTestData.nodeG.copy().geoPosition(GeoUtils.DEFAULT_GEOMETRY_FACTORY.createPoint(new Coordinate(10, 10))).build()).build()                              || new InvalidEntityException("Coordinates of start and end point do not match coordinates of connected nodes", invalidLine)
		new LineInput(
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
				) 																																															  || new InvalidEntityException("Line length does not equal calculated distances between points building the line", invalidLine)
		GridTestData.lineFtoG.copy().olmCharacteristic(null).build()                                                                                                                                  || new InvalidEntityException("Characteristic for overhead line monitoring of the line is null", invalidLine)
	}

	def "Smoke Test: Correct line type throws no exception"() {
		given:
		def lineType = GridTestData.lineTypeInputCtoD

		when:
		ValidationUtils.check(lineType)

		then:
		noExceptionThrown()
	}

	// No tests for "ConnectorValidationUtils.checkLineType recognizes all potential errors for a line type"

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
		ConnectorValidationUtils.check(invalidTransformer2W)

		then:
		Exception ex = thrown()
		ex.class == expectedException.class
		ex.message == expectedException.message

		where:
		invalidTransformer2W                                                                                                     || expectedException
		GridTestData.transformerBtoD.copy().tapPos(100).build()                                                                  || new InvalidEntityException("Tap position of transformer is outside of bounds", invalidTransformer2W)
		GridTestData.transformerBtoD.copy().nodeB(GridTestData.nodeD.copy().voltLvl(GermanVoltageLevelUtils.HV).build()).build() || new InvalidEntityException("Transformer connects nodes of the same voltage level", invalidTransformer2W)
		GridTestData.transformerBtoD.copy().nodeB(GridTestData.nodeD.copy().subnet(2).build()).build()                           || new InvalidEntityException("Transformer connects nodes in the same subnet", invalidTransformer2W)
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
	private static UUID uuid = UUID.fromString("202069a7-bcf8-422c-837c-273575220c8a")
	private static String id = "HS-MS_1"
	private static ComparableQuantity<ElectricResistance> rSc = Quantities.getQuantity(45.375d, IMPEDANCE)
	private static ComparableQuantity<ElectricResistance> xSc = Quantities.getQuantity(102.759d, IMPEDANCE)
	private static ComparableQuantity<Power> sRated = Quantities.getQuantity(20000d, ACTIVE_POWER_IN)
	private static ComparableQuantity<ElectricPotential> vRatedA = Quantities.getQuantity(110d, RATED_VOLTAGE_MAGNITUDE)
	private static ComparableQuantity<ElectricPotential> vRatedB = Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE)
	private static ComparableQuantity<ElectricConductance> gM = Quantities.getQuantity(0d, ADMITTANCE)
	private static ComparableQuantity<ElectricConductance> bM = Quantities.getQuantity(0d, ADMITTANCE)
	private static ComparableQuantity<Dimensionless> dV = Quantities.getQuantity(1.5d, DV_TAP)
	private static ComparableQuantity<Angle> dPhi = Quantities.getQuantity(0d, DPHI_TAP)
	private static boolean tapSide = false
	private static int tapNeutr = 0
	private static int tapMin = -10
	private static int tapMax = 10

	def "ConnectorValidationUtils.checkTransformer2WType recognizes all potential errors for a transformer2W type"() {
		when:
		ConnectorValidationUtils.check(invalidTransformer2WType)

		then:
		Exception ex = thrown()
		ex.class == expectedException.class
		ex.message == expectedException.message

		where:
		invalidTransformer2WType || expectedException
		new Transformer2WTypeInput(uuid, id, rSc, xSc, sRated, vRatedA, vRatedB, gM, bM, Quantities.getQuantity(0d, DV_TAP), dPhi, tapSide, tapNeutr, tapMin, tapMax) || new InvalidEntityException("Voltage magnitude increase per tap position must be between 0% and 100%", invalidTransformer2WType)
		new Transformer2WTypeInput(uuid, id, rSc, xSc, sRated, vRatedA, vRatedB, gM, bM, dV, dPhi, tapSide, tapNeutr, 100, tapMax) || new InvalidEntityException("Minimum tap position must be lower than maximum tap position", invalidTransformer2WType)
		new Transformer2WTypeInput(uuid, id, rSc, xSc, sRated, vRatedA, vRatedB, gM, bM, dV, dPhi, tapSide, 100, tapMin,tapMax) || new InvalidEntityException("Neutral tap position must be between minimum and maximum tap position", invalidTransformer2WType)
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
		ConnectorValidationUtils.check(invalidTransformer3W)

		then:
		Exception ex = thrown()
		ex.class == expectedException.class
		ex.message == expectedException.message

		where:
		invalidTransformer3W             	                                                                                        || expectedException
		GridTestData.transformerAtoBtoC.copy().tapPos(100).build()                                                                  || new InvalidEntityException("Tap position of transformer is outside of bounds", invalidTransformer3W)
		GridTestData.transformerAtoBtoC.copy().nodeA(GridTestData.nodeA.copy().voltLvl(GermanVoltageLevelUtils.HV).build()).build() || new InvalidEntityException("Transformer connects nodes of the same voltage level", invalidTransformer3W)
		GridTestData.transformerAtoBtoC.copy().nodeA(GridTestData.nodeA.copy().subnet(2).build()).build()                           || new InvalidEntityException("Transformer connects nodes in the same subnet", invalidTransformer3W)
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
	// UUID uuid
	// String id
	private static ComparableQuantity<Power> sRatedA = Quantities.getQuantity(120000d, ACTIVE_POWER_IN)
	private static ComparableQuantity<Power> sRatedB = Quantities.getQuantity(60000d, ACTIVE_POWER_IN)
	private static ComparableQuantity<Power> sRatedC = Quantities.getQuantity(40000d, ACTIVE_POWER_IN)
	// ComparableQuantity<ElectricPotential> vRatedA = Quantities.getQuantity(380d, RATED_VOLTAGE_MAGNITUDE)
	// ComparableQuantity<ElectricPotential> vRatedB = Quantities.getQuantity(110d, RATED_VOLTAGE_MAGNITUDE)
	private static ComparableQuantity<ElectricPotential> vRatedC = Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE)
	private static ComparableQuantity<ElectricResistance> rScA = Quantities.getQuantity(0.3d, IMPEDANCE)
	private static ComparableQuantity<ElectricResistance> rScB = Quantities.getQuantity(0.025d, IMPEDANCE)
	private static ComparableQuantity<ElectricResistance> rScC = Quantities.getQuantity(0.0080d, IMPEDANCE)
	private static ComparableQuantity<ElectricResistance> xScA = Quantities.getQuantity(1d, IMPEDANCE)
	private static ComparableQuantity<ElectricResistance> xScB = Quantities.getQuantity(0.08d, IMPEDANCE)
	private static ComparableQuantity<ElectricResistance> xScC = Quantities.getQuantity(0.003d, IMPEDANCE)
	// ComparableQuantity<ElectricConductance> gM = Quantities.getQuantity(40000d, ADMITTANCE)
	// ComparableQuantity<ElectricConductance> bM = Quantities.getQuantity(1000d, ADMITTANCE)
	// ComparableQuantity<Dimensionless> dV = Quantities.getQuantity(1.5d, DV_TAP)
	// ComparableQuantity<Angle> dPhi = Quantities.getQuantity(0d, DPHI_TAP)
	// int tapNeutr = 0
	// int tapMin = -10
	// int tapMax = 10

	def "ConnectorValidationUtils.checkTransformer3WType recognizes all potential errors for a transformer3W type"() {
		when:
		ConnectorValidationUtils.check(invalidTransformer3WType)

		then:
		Exception ex = thrown()
		ex.class == expectedException.class
		ex.message == expectedException.message

		where:
		invalidTransformer3WType || expectedException
		new Transformer3WTypeInput(uuid, id, sRatedA, sRatedB, sRatedC, vRatedA, vRatedB, vRatedC, rScA, rScB, rScC, xScA, xScB, xScC, gM, bM, Quantities.getQuantity(0d, DV_TAP), dPhi, tapNeutr, tapMin, tapMax) || new InvalidEntityException("Voltage magnitude increase per tap position must be between 0% and 100%", invalidTransformer3WType)
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
		ConnectorValidationUtils.check(invalidSwitch)

		then:
		Exception ex = thrown()
		ex.class == expectedException.class
		ex.message == expectedException.message

		where:
		invalidSwitch           || expectedException
		GridTestData.switchAtoB || new InvalidEntityException("Switch connects two different voltage levels", invalidSwitch)
	}

}