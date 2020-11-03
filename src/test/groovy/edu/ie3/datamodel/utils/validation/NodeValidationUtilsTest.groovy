/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils.validation

import edu.ie3.datamodel.exceptions.UnsafeEntityException

import static edu.ie3.util.quantities.PowerSystemUnits.PU

import edu.ie3.datamodel.exceptions.InvalidEntityException
import edu.ie3.datamodel.exceptions.ValidationException
import edu.ie3.test.common.GridTestData
import spock.lang.Specification
import tech.units.indriya.quantity.Quantities

class NodeValidationUtilsTest extends Specification {

	def "The check method in ValidationUtils delegates the check to NodeValidationUtils for a node"() {
		given:
		def node = GridTestData.nodeA

		when:
		ValidationUtils.check(node)

		then:
		1 * NodeValidationUtils.check(node)
		// TODO NSteffan: Why is the method invoked 0 times?
	}

	def "Smoke Test: Correct node throws no exception"() {
		given:
		def node = GridTestData.nodeA

		when:
		ValidationUtils.check(node)

		then:
		noExceptionThrown()
	}

	def "The check method recognizes an invalid voltage level"() {
		given:
		def node = GridTestData.nodeA.copy().voltLvl(null).build()

		when:
		NodeValidationUtils.check(node)
		then:
		ValidationException ex = thrown()
		ex.message == "Expected a voltage level, but got nothing. :-("
	}
	// TODO NSteffan: Check unvalid voltLvl (not null), check nominal voltage = null oder < 0

	def "The check method recognizes a null or invalid target voltage"() {
		given:
		// def nullNode = GridTestData.nodeA.copy().vTarget(null).build()
		def invalidNodeTooLow = GridTestData.nodeA.copy().vTarget(Quantities.getQuantity(0d, PU)).build()
		def invalidNodeTooHigh = GridTestData.nodeA.copy().vTarget(Quantities.getQuantity(2.1d, PU)).build()

		/*
		 when:
		 NodeValidationUtils.check(nullNode)
		 then:
		 NullPointerException ex = thrown()
		 ex.message == "Entity is invalid because of: Target voltage (p.u.) is null [NodeInput{uuid=47d29df0-ba2d-4d23-8e75-c82229c5c758, id='node_b', operator=28c9f622-210c-4d4d-806d-d338e29009c0, operationTime=OperationTime{startDate=null, endDate=null, isLimited=false}, vTarget=0 PU, slack=false, geoPosition=POINT (7.4116482 51.4843281), voltLvl=CommonVoltageLevel{id='Hochspannung', nominalVoltage=110 kV, synonymousIds=[Hochspannung, hs, hv], voltageRange=Interval [110 kV, 220 kV)}, subnet=2}]"
		 */
		// TODO NSteffan: vTarget can't be null, causes NullPointerException in builder -> not necessary to check?

		when:
		NodeValidationUtils.check(invalidNodeTooLow)
		then:
		InvalidEntityException exTooLow = thrown()
		exTooLow.message == "Entity is invalid because of: Target voltage (p.u.) is not a positive value [NodeInput{uuid=4ca90220-74c2-4369-9afa-a18bf068840d, id='node_a', operator=f15105c4-a2de-4ab8-a621-4bc98e372d92, operationTime=OperationTime{startDate=2020-03-24T15:11:31Z[UTC], endDate=2020-03-25T15:11:31Z[UTC], isLimited=true}, vTarget=0 PU, slack=true, geoPosition=POINT (7.411111 51.492528), voltLvl=CommonVoltageLevel{id='Höchstspannung', nominalVoltage=380 kV, synonymousIds=[Höchstspannung, ehv, ehv_380kv, hoes, hoes_380kv], voltageRange=Interval [380 kV, 560 kV)}, subnet=1}]"

		when:
		NodeValidationUtils.check(invalidNodeTooHigh)
		then:
		UnsafeEntityException exTooHigh = thrown()
		exTooHigh.message == "Entity may be unsafe because of: Target voltage (p.u.) might be too high [NodeInput{uuid=4ca90220-74c2-4369-9afa-a18bf068840d, id='node_a', operator=f15105c4-a2de-4ab8-a621-4bc98e372d92, operationTime=OperationTime{startDate=2020-03-24T15:11:31Z[UTC], endDate=2020-03-25T15:11:31Z[UTC], isLimited=true}, vTarget=2.1 PU, slack=true, geoPosition=POINT (7.411111 51.492528), voltLvl=CommonVoltageLevel{id='Höchstspannung', nominalVoltage=380 kV, synonymousIds=[Höchstspannung, ehv, ehv_380kv, hoes, hoes_380kv], voltageRange=Interval [380 kV, 560 kV)}, subnet=1}]"
	}

	def "The check method recognizes an invalid target voltage"() {
		given:
		def invalidNodeTooLow = GridTestData.nodeA.copy().vTarget(Quantities.getQuantity(0d, PU)).build()
		def invalidNodeTooHigh = GridTestData.nodeA.copy().vTarget(Quantities.getQuantity(2.1d, PU)).build()

		when:
		NodeValidationUtils.check(invalidNodeTooLow)
		then:
		InvalidEntityException exTooLow = thrown()
		exTooLow.message == "Entity is invalid because of: Target voltage (p.u.) is not a positive value [...]"
		when:
		NodeValidationUtils.check(invalidNodeTooHigh)
		then:
		UnsafeEntityException exTooHigh = thrown()
		exTooHigh.message == "Entity may be unsafe because of: Target voltage (p.u.) might be too high [...]"
	}

	// TODO: New version from Johannes(adapted) -> ex == expectedException tests false
	def "The check method recognizes an invalid target voltage NEW"() {
		when:
		NodeValidationUtils.check(invalidNode)

		then:
		Exception ex = thrown()
		ex.class == expectedException.class
		ex.message == expectedException.message

		where:
		invalidNode                                                                 || expectedException
		GridTestData.nodeA.copy().vTarget(Quantities.getQuantity(0d, PU)).build()   || new InvalidEntityException("Target voltage (p.u.) is not a positive value", invalidNode)
		GridTestData.nodeA.copy().vTarget(Quantities.getQuantity(2.1d, PU)).build() || new UnsafeEntityException("Target voltage (p.u.) might be too high", invalidNode)

	}


	/*
	 def "The check method recognizes an null or invalid target voltage2"() {
	 given:
	 def node = GridTestData.nodeA.copy().vTarget(input).build()
	 when:
	 NodeValidationUtils.check(node)
	 then:
	 InvalidEntityException ex = thrown()
	 ex.message == text
	 where:
	 input | text
	 null  | "Entity is invalid because of: Target voltage (p.u.) is null [NodeInput{uuid=47d29df0-ba2d-4d23-8e75-c82229c5c758, id='node_b', operator=28c9f622-210c-4d4d-806d-d338e29009c0, operationTime=OperationTime{startDate=null, endDate=null, isLimited=false}, vTarget=0 PU, slack=false, geoPosition=POINT (7.4116482 51.4843281), voltLvl=CommonVoltageLevel{id='Hochspannung', nominalVoltage=110 kV, synonymousIds=[Hochspannung, hs, hv], voltageRange=Interval [110 kV, 220 kV)}, subnet=2}]"
	 Quantities.getQuantity(0d, PU) | "Entity is invalid because of: Target voltage (p.u.) is not a positive value [NodeInput{uuid=47d29df0-ba2d-4d23-8e75-c82229c5c758, id='node_b', operator=28c9f622-210c-4d4d-806d-d338e29009c0, operationTime=OperationTime{startDate=null, endDate=null, isLimited=false}, vTarget=0 PU, slack=false, geoPosition=POINT (7.4116482 51.4843281), voltLvl=CommonVoltageLevel{id='Hochspannung', nominalVoltage=110 kV, synonymousIds=[Hochspannung, hs, hv], voltageRange=Interval [110 kV, 220 kV)}, subnet=2}]"
	 }
	 */


	def "The check method recognizes an invalid subnet"() {
		given:
		def correctNode = GridTestData.nodeA
		def errorNode = correctNode.copy().subnet(0).build()

		when:
		NodeValidationUtils.check(errorNode)
		then:
		InvalidEntityException ex = thrown()
	}

	def "The check method recognizes an invalid geoPosition"() {
		given:
		def correctNode = GridTestData.nodeA
		def errorNode = correctNode.copy().geoPosition(null).build()

		when:
		NodeValidationUtils.check(errorNode)
		then:
		InvalidEntityException ex = thrown()
	}

	/*
	 def "The check method recognizes an invalid geoPosition2"() {
	 given:
	 def node = GridTestData.nodeB.copy().geoPosition(input).build()
	 def testNode = node.copy().geoPosition(input).build()
	 when:
	 NodeValidationUtils.check(testNode)
	 then:
	 thrown(ex)
	 where:
	 input || ex
	 null  || InvalidEntityException
	 node.getGeoPosition()  || null
	 }
	 */
}
