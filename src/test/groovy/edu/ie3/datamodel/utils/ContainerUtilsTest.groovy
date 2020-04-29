/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.datamodel.utils


import static edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils.*
import static edu.ie3.util.quantities.PowerSystemUnits.PU
import edu.ie3.datamodel.exceptions.InvalidGridException
import edu.ie3.datamodel.graph.SubGridTopologyGraph
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.Transformer3WInput
import edu.ie3.datamodel.models.input.container.GraphicElements
import edu.ie3.datamodel.models.input.container.GridContainer
import edu.ie3.datamodel.models.input.container.JointGridContainer
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.models.input.container.SubGridContainer
import edu.ie3.datamodel.models.input.container.SystemParticipants
import edu.ie3.util.TimeTools
import tec.uom.se.quantity.Quantities

import java.time.ZoneId

import edu.ie3.datamodel.models.voltagelevels.VoltageLevel
import edu.ie3.test.common.ComplexTopology
import spock.lang.Shared
import spock.lang.Specification

class ContainerUtilsTest extends Specification {
	static {
		TimeTools.initialize(ZoneId.of("UTC"), Locale.GERMANY, "yyyy-MM-dd HH:mm:ss")
	}

	@Shared
	GridContainer complexTopology = ComplexTopology.grid

	def "The container utils filter raw grid elements correctly for a given subnet"() {
		when:
		RawGridElements actual = ContainerUtils.filterForSubnet(complexTopology.rawGrid, subnet)

		then:
		actual.nodes == expectedNodes
		actual.transformer2Ws == expectedTransformers2W
		actual.transformer3Ws == expectedTransformers3W
		/* TODO: Add lines, switches etc. to testing data */

		where:
		subnet || expectedNodes               || expectedTransformers2W || expectedTransformers3W
		1      || [
			ComplexTopology.nodeA,
			ComplexTopology.nodeB,
			ComplexTopology.nodeC] as Set || [] as Set              || [
			ComplexTopology.transformerAtoBtoC] as Set
		2      || [
			ComplexTopology.nodeA,
			ComplexTopology.nodeB,
			ComplexTopology.nodeC] as Set || [] as Set              || [
			ComplexTopology.transformerAtoBtoC] as Set
		3      || [
			ComplexTopology.nodeA,
			ComplexTopology.nodeB,
			ComplexTopology.nodeC] as Set || [] as Set              || [
			ComplexTopology.transformerAtoBtoC] as Set
		4      || [
			ComplexTopology.nodeB,
			ComplexTopology.nodeD] as Set || [
			ComplexTopology.transformerBtoD] as Set                 || [] as Set
		5      || [
			ComplexTopology.nodeB,
			ComplexTopology.nodeC,
			ComplexTopology.nodeE] as Set || [
			ComplexTopology.transformerBtoE,
			ComplexTopology.transformerCtoE] as Set                 || [] as Set
		6      || [
			ComplexTopology.nodeC,
			ComplexTopology.nodeF,
			ComplexTopology.nodeG] as Set || [
			ComplexTopology.transformerCtoF,
			ComplexTopology.transformerCtoG] as Set                 || [] as Set
	}

	def "The container utils are able to derive the predominant voltage level"() {
		given:
		RawGridElements rawGrid = ContainerUtils.filterForSubnet(complexTopology.rawGrid, subnet)

		when:
		VoltageLevel actual = ContainerUtils.determinePredominantVoltLvl(rawGrid, subnet)

		then:
		actual == expected

		where:
		subnet || expected
		1      || EHV_380KV
		2      || HV
		3      || MV_20KV
		4      || MV_20KV
		5      || MV_10KV
		6      || LV
	}

	def "The container utils throw an exception, when there is an ambiguous voltage level in the grid"() {
		given:
		RawGridElements rawGrid = ContainerUtils.filterForSubnet(complexTopology.rawGrid, 4)

		NodeInput corruptNode = new NodeInput(
				UUID.randomUUID(), "node_e", OperatorInput.NO_OPERATOR_ASSIGNED,
				OperationTime.notLimited()
				,
				Quantities.getQuantity(1d, PU),
				false,
				null,
				MV_10KV,
				4)

		Set<NodeInput> corruptNodes = [corruptNode] as Set
		corruptNodes.addAll(rawGrid.nodes)

		RawGridElements dut = new RawGridElements(corruptNodes, rawGrid.lines, rawGrid.transformer2Ws,
				rawGrid.transformer3Ws, rawGrid.switches, rawGrid.measurementUnits)

		when:
		ContainerUtils.determinePredominantVoltLvl(dut, 4)

		then:
		InvalidGridException ex = thrown()
		ex.message == "There are 2 voltage levels apparent, although only one is expected. Following voltage levels" +
				" are present: CommonVoltageLevel{id='Mittelspannung', nominalVoltage=10.0 kV, synonymousIds=" +
				"[Mittelspannung, ms, ms_10kv, mv, mv_10kV], voltageRange=Interval [10.0 kV, 20.0 kV)}, " +
				"CommonVoltageLevel{id='Mittelspannung', nominalVoltage=20.0 kV, synonymousIds=[Mittelspannung, ms, " +
				"ms_20kv, mv, mv_20kV], voltageRange=Interval [20.0 kV, 30.0 kV)}"
	}

	def "The container util determines the set of subnet number correctly"() {
		expect:
		ContainerUtils.determineSubnetNumbers(ComplexTopology.grid.rawGrid.nodes) == [1, 2, 3, 4, 5, 6] as Set
	}

	def "The container util builds the sub grid containers correctly"() {
		given:
		String gridName = ComplexTopology.grid.gridName
		Set<Integer> subNetNumbers = ContainerUtils.determineSubnetNumbers(ComplexTopology.grid.rawGrid.nodes)
		RawGridElements rawGrid = ComplexTopology.grid.rawGrid
		SystemParticipants systemParticipants = ComplexTopology.grid.systemParticipants
		GraphicElements graphics = ComplexTopology.grid.graphics

		HashMap<Integer, SubGridContainer> expectedSubGrids = ComplexTopology.expectedSubGrids

		when:
		HashMap<Integer, SubGridContainer> actual = ContainerUtils.buildSubGridContainers(
				gridName,
				subNetNumbers,
				rawGrid,
				systemParticipants,
				graphics)

		then:
		actual.size() == 6
		for (Map.Entry<Integer, SubGridContainer> entry : actual) {
			int subnetNo = entry.key
			SubGridContainer actualSubGrid = entry.value
			SubGridContainer expectedSubGrid = expectedSubGrids.get(subnetNo)

			assert actualSubGrid == expectedSubGrid
		}
	}

	def "The container util returns copy of provided subgrids with slack nodes marked as expected"() {
		given:
		String gridName = ComplexTopology.grid.gridName
		Set<Integer> subNetNumbers = ContainerUtils.determineSubnetNumbers(ComplexTopology.grid.rawGrid.nodes)
		RawGridElements rawGridInput= ComplexTopology.grid.rawGrid
		SystemParticipants systemParticipantsInput = ComplexTopology.grid.systemParticipants
		GraphicElements graphicsInput = ComplexTopology.grid.graphics

		HashMap<Integer, SubGridContainer> unmodifiedSubGrids = ComplexTopology.expectedSubGrids

		HashMap<Integer, SubGridContainer> subgrids = ContainerUtils.buildSubGridContainers(
				gridName,
				subNetNumbers,
				rawGridInput,
				systemParticipantsInput,
				graphicsInput)

		when:
		def computableSubgrids = subgrids.collectEntries {[(it.key): ContainerUtils.withTrafoNodeAsSlack(it.value)]} as HashMap<Integer, SubGridContainer>

		then:
		computableSubgrids.size() == 6
		computableSubgrids.each {
			SubGridContainer computableSubGrid = it.value
			SubGridContainer unmodifiedSubGrid = unmodifiedSubGrids.get(it.key)

			computableSubGrid.with {
				assert subnet == unmodifiedSubGrid.subnet
				assert predominantVoltageLevel == unmodifiedSubGrid.predominantVoltageLevel

				// 2 winding transformer hv nodes must be marked as slacks
				rawGrid.transformer2Ws.each {
					def trafo2w = it
					trafo2w.with {
						assert nodeA.slack
					}
				}

				// all adapted trafo2w nodes must be part of the nodes set
				assert rawGrid.nodes.containsAll(rawGrid.transformer2Ws.collect{it.nodeA})

				// 3 winding transformer slack nodes must be mapped correctly
				rawGrid.transformer3Ws.each {
					def trafo3w = it
					if(trafo3w.nodeA.subnet == subnet) {
						// subnet 1 is highest grid in test set + trafo 3w -> nodeA must be slack
						assert subnet == 1 ? trafo3w.nodeA.slack : !trafo3w.nodeA.slack
						assert !trafo3w.nodeInternal.slack
						assert rawGrid.nodes.contains(trafo3w.nodeInternal)
					} else {
						assert trafo3w.nodeInternal.slack
						assert !trafo3w.nodeA.slack
						assert !trafo3w.nodeB.slack
						assert !trafo3w.nodeC.slack
						assert rawGrid.nodes.contains(trafo3w.nodeInternal)
					}
				}

				assert systemParticipants == unmodifiedSubGrid.systemParticipants

			}
		}

	}

	def "The container util builds the correct sub grid dependency graph"() {
		given:
		String gridName = ComplexTopology.grid.gridName
		Set<Integer> subNetNumbers = ContainerUtils.determineSubnetNumbers(ComplexTopology.grid.rawGrid.nodes)
		RawGridElements rawGrid = ComplexTopology.grid.rawGrid
		SystemParticipants systemParticipants = ComplexTopology.grid.systemParticipants
		GraphicElements graphics = ComplexTopology.grid.graphics
		Map<Integer, SubGridContainer> subgrids = ContainerUtils.buildSubGridContainers(
				gridName,
				subNetNumbers,
				rawGrid,
				systemParticipants,
				graphics)
		Set<Transformer2WInput> transformer2ws = ComplexTopology.grid.rawGrid.transformer2Ws
		Set<Transformer3WInput> transformer3ws = ComplexTopology.grid.rawGrid.transformer3Ws
		SubGridTopologyGraph expectedSubGridTopology = ComplexTopology.expectedSubGridTopology

		when:
		SubGridTopologyGraph actual = ContainerUtils.buildSubGridTopologyGraph(
				subgrids,
				transformer2ws,
				transformer3ws)

		then:
		actual == expectedSubGridTopology
	}

	def "The container util builds the correct assembly of sub grids from basic information"() {
		given:
		String gridName = ComplexTopology.gridName
		RawGridElements rawGrid = ComplexTopology.grid.rawGrid
		SystemParticipants systemParticpants = ComplexTopology.grid.systemParticipants
		GraphicElements graphics = ComplexTopology.grid.graphics
		SubGridTopologyGraph expectedSubGridTopology = ComplexTopology.expectedSubGridTopology

		when:
		SubGridTopologyGraph actual = ContainerUtils.buildSubGridTopologyGraph(
				gridName,
				rawGrid,
				systemParticpants,
				graphics)

		then:
		actual == expectedSubGridTopology
	}

	def "The container utils build a joint model correctly from sub grids"() {
		given:
		Collection<SubGridContainer> subGridContainers = ComplexTopology.expectedSubGrids.values()
		JointGridContainer expected = ComplexTopology.grid

		when:
		JointGridContainer actual = ContainerUtils.combineToJointGrid(subGridContainers)

		then:
		actual == expected
	}

	/* TODO: Extend testing data so that,
	 *   - filtering of system participants can be tested
	 *   - filtering of graphic elements can be tested */
}
