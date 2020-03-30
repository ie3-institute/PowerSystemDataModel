/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.SwitchInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.Transformer3WInput
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.util.TimeTools
import edu.ie3.util.quantities.PowerSystemUnits
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import org.locationtech.jts.io.geojson.GeoJsonReader
import tec.uom.se.quantity.Quantities
import tec.uom.se.unit.MetricPrefix
import tec.uom.se.unit.Units

import static edu.ie3.util.quantities.PowerSystemUnits.DEGREE_GEOM
import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLT
import static edu.ie3.util.quantities.PowerSystemUnits.KILOVOLTAMPERE
import static edu.ie3.util.quantities.PowerSystemUnits.PU
import static tec.uom.se.unit.Units.OHM
import static tec.uom.se.unit.Units.PERCENT
import static tec.uom.se.unit.Units.SIEMENS

/**
 * This class contains a collection of different model instances that can be used for testing purposes.
 * Please note that these entities do NOT necessarily form a valid grid. For valid topologies please refer
 * to {@link ComplexTopology}
 */
class GridTestData {

	private static final GeoJsonReader geoJsonReader = new GeoJsonReader()

	private static final Transformer2WTypeInput transformerTypeBtoD = new Transformer2WTypeInput(
	UUID.randomUUID(),
	"HS-MS_1",
	Quantities.getQuantity(45.375, OHM),
	Quantities.getQuantity(102.759, OHM),
	Quantities.getQuantity(20000d, KILOVOLTAMPERE),
	Quantities.getQuantity(110d, KILOVOLT),
	Quantities.getQuantity(20d, KILOVOLT),
	Quantities.getQuantity(0d, MetricPrefix.MICRO(SIEMENS)),
	Quantities.getQuantity(0d, MetricPrefix.MICRO(SIEMENS)),
	Quantities.getQuantity(1.5, PERCENT),
	Quantities.getQuantity(0d, DEGREE_GEOM),
	false,
	0,
	-10,
	10
	)
	private static final Transformer2WTypeInput transformerTypeBtoE = new Transformer2WTypeInput(
	UUID.randomUUID(),
	"transformer_type_gedfi89fc7c895076ff25ec6d3b2e7ab9a1b24b37f73ecf30f895005d766a8d8d2774aa",
	Quantities.getQuantity(0d, OHM),
	Quantities.getQuantity(51.72750115394592, OHM),
	Quantities.getQuantity(40000d, KILOVOLTAMPERE),
	Quantities.getQuantity(110d, KILOVOLT),
	Quantities.getQuantity(10d, KILOVOLT),
	Quantities.getQuantity(0d, MetricPrefix.MICRO(SIEMENS)),
	Quantities.getQuantity(0d, MetricPrefix.MICRO(SIEMENS)),
	Quantities.getQuantity(1.777780055999756, PERCENT),
	Quantities.getQuantity(0d, DEGREE_GEOM),
	false,
	10,
	1,
	19
	)
	private static final Transformer2WTypeInput transformerTypeCtoE = new Transformer2WTypeInput(
	UUID.randomUUID(),
	"no_shunt_elements_mv-mv",
	Quantities.getQuantity(1.5, OHM),
	Quantities.getQuantity(15.5, OHM),
	Quantities.getQuantity(250d, KILOVOLTAMPERE),
	Quantities.getQuantity(20d, KILOVOLT),
	Quantities.getQuantity(10d, KILOVOLT),
	Quantities.getQuantity(0d, MetricPrefix.MICRO(SIEMENS)),
	Quantities.getQuantity(0d, MetricPrefix.MICRO(SIEMENS)),
	Quantities.getQuantity(1.5, PERCENT),
	Quantities.getQuantity(0d, DEGREE_GEOM),
	false,
	0,
	-5,
	5
	)
	private static final Transformer2WTypeInput transformerTypeCtoX = new Transformer2WTypeInput(
	UUID.fromString("08559390-d7c0-4427-a2dc-97ba312ae0ac"),
	"MS-NS_1",
	Quantities.getQuantity(10.078, OHM),
	Quantities.getQuantity(23.312, OHM),
	Quantities.getQuantity(630d, KILOVOLTAMPERE),
	Quantities.getQuantity(20d, KILOVOLT),
	Quantities.getQuantity(0.4, KILOVOLT),
	Quantities.getQuantity(0d, MetricPrefix.MICRO(SIEMENS)),
	Quantities.getQuantity(0d, MetricPrefix.MICRO(SIEMENS)),
	Quantities.getQuantity(0.5, PERCENT),
	Quantities.getQuantity(0d, DEGREE_GEOM),
	false,
	0,
	-10,
	10
	)

	private static final Transformer3WTypeInput transformerTypeAtoBtoC = new Transformer3WTypeInput(
	UUID.fromString("5b0ee546-21fb-4a7f-a801-5dbd3d7bb356"),
	"HöS-HS-MS_1",
	Quantities.getQuantity(120000d, KILOVOLTAMPERE),
	Quantities.getQuantity(60000d, KILOVOLTAMPERE),
	Quantities.getQuantity(40000d, KILOVOLTAMPERE),
	Quantities.getQuantity(380d, KILOVOLT),
	Quantities.getQuantity(110d, KILOVOLT),
	Quantities.getQuantity(20d, KILOVOLT),
	Quantities.getQuantity(0.3, OHM),
	Quantities.getQuantity(0.025, OHM),
	Quantities.getQuantity(0.0008, OHM),
	Quantities.getQuantity(1d, OHM),
	Quantities.getQuantity(0.08, OHM),
	Quantities.getQuantity(0.003, OHM),
	Quantities.getQuantity(40, MetricPrefix.MICRO(SIEMENS)),
	Quantities.getQuantity(1d, MetricPrefix.MICRO(SIEMENS)),
	Quantities.getQuantity(1.5, PERCENT),
	Quantities.getQuantity(0d, DEGREE_GEOM),
	0,
	-10,
	10
	)

	public static final NodeInput nodeA = new NodeInput(
	UUID.fromString("5dc88077-aeb6-4711-9142-db57292640b1"),
	OperationTime.builder().withStart(TimeTools.toZonedDateTime("2020-03-24 15:11:31")).withEnd(TimeTools.toZonedDateTime("2020-03-25 15:11:31")).build(),
	new OperatorInput(UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "TestOperator"),
	"node_a",
	Quantities.getQuantity(1d, PU),
	true,
	geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] }") as Point,
	GermanVoltageLevelUtils.EHV_380KV,
	1)
	public static final NodeInput nodeB = new NodeInput(
	UUID.fromString("47d29df0-ba2d-4d23-8e75-c82229c5c758"),
	OperationTime.notLimited(),
	OperatorInput.NO_OPERATOR_ASSIGNED,
	"node_b",
	Quantities.getQuantity(1d, PU),
	false,
	null,
	GermanVoltageLevelUtils.HV,
	2)
	public static final NodeInput nodeC = new NodeInput(
	UUID.fromString("bd837a25-58f3-44ac-aa90-c6b6e3cd91b2"),
	OperationTime.notLimited(),
	OperatorInput.NO_OPERATOR_ASSIGNED,
	"node_c",
	Quantities.getQuantity(1d, PU),
	false,
	null,
	GermanVoltageLevelUtils.MV_20KV,
	3)
	public static final NodeGraphicInput nodeGraphicC = new NodeGraphicInput(
	UUID.fromString("09aec636-791b-45aa-b981-b14edf171c4c"),
	"main",
	null,
	nodeC,
	geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [0, 10] }") as Point
	)
	public static final NodeInput nodeD = new NodeInput(
	UUID.fromString("6e0980e0-10f2-4e18-862b-eb2b7c90509b"),
	OperationTime.notLimited(),
	OperatorInput.NO_OPERATOR_ASSIGNED,
	"node_d",
	Quantities.getQuantity(1d, PU),
	false,
	null,
	GermanVoltageLevelUtils.MV_20KV,
	4)
	public static final NodeGraphicInput nodeGraphicD = new NodeGraphicInput(
	UUID.fromString("9ecad435-bd16-4797-a732-762c09d4af25"),
	"main",
	geoJsonReader.read("{ \"type\": \"LineString\", \"coordinates\": [[-1, 0], [1, 0]]}") as LineString,
	nodeD,
	null
	)
	public static final NodeInput nodeE = new NodeInput(
	UUID.randomUUID(),
	OperationTime.notLimited(),
	OperatorInput.NO_OPERATOR_ASSIGNED,
	"node_e",
	Quantities.getQuantity(1d, PU),
	false,
	null,
	GermanVoltageLevelUtils.MV_10KV,
	5)
	public static final NodeInput nodeF = new NodeInput(
	UUID.fromString("aaa74c1a-d07e-4615-99a5-e991f1d81cc4"),
	OperationTime.notLimited(),
	OperatorInput.NO_OPERATOR_ASSIGNED,
	"node_f",
	Quantities.getQuantity(1d, PU),
	false,
	null,
	GermanVoltageLevelUtils.LV,
	6)
	public static final NodeInput nodeG = new NodeInput(
	UUID.fromString("aaa74c1a-d07e-4615-99a5-e991f1d81cc4"),
	OperationTime.notLimited(),
	OperatorInput.NO_OPERATOR_ASSIGNED,
	"node_g",
	Quantities.getQuantity(1d, PU),
	false,
	null,
	GermanVoltageLevelUtils.LV,
	6)

	public static final Transformer2WInput transformerBtoD = new Transformer2WInput(
	UUID.randomUUID(),
	OperationTime.notLimited(),
	OperatorInput.NO_OPERATOR_ASSIGNED,
	"2w_single_test",
	nodeB,
	nodeD,
	1,
	transformerTypeBtoD,
	0,
	true
	)
	public static final Transformer2WInput transformerBtoE = new Transformer2WInput(
	UUID.randomUUID(),
	OperationTime.notLimited(),
	OperatorInput.NO_OPERATOR_ASSIGNED,
	"2w_v_1",
	nodeB,
	nodeE,
	1,
	transformerTypeBtoE,
	0,
	true
	)
	public static final Transformer2WInput transformerCtoE = new Transformer2WInput(
	UUID.randomUUID(),
	OperationTime.notLimited(),
	OperatorInput.NO_OPERATOR_ASSIGNED,
	"2w_v_2",
	nodeC,
	nodeE,
	1,
	transformerTypeCtoE,
	0,
	true
	)
	public static final Transformer2WInput transformerCtoF = new Transformer2WInput(
	UUID.randomUUID(),
	OperationTime.notLimited(),
	OperatorInput.NO_OPERATOR_ASSIGNED,
	"2w_parallel_1",
	nodeC,
	nodeF,
	1,
	transformerTypeCtoX,
	0,
	true
	)
	public static final Transformer2WInput transformerCtoG = new Transformer2WInput(
	UUID.fromString("5dc88077-aeb6-4711-9142-db57292640b1"),
	OperationTime.builder().withStart(TimeTools.toZonedDateTime("2020-03-24 15:11:31")).withEnd(TimeTools.toZonedDateTime("2020-03-25 15:11:31")).build(),
	new OperatorInput(UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "TestOperator"),
	"2w_parallel_2",
	nodeC,
	nodeG,
	1,
	transformerTypeCtoX,
	0,
	true
	)

	public static Transformer3WInput transformerAtoBtoC = new Transformer3WInput(
	UUID.fromString("5dc88077-aeb6-4711-9142-db57292640b1"),
	OperationTime.builder().withStart(TimeTools.toZonedDateTime("2020-03-24 15:11:31")).withEnd(TimeTools.toZonedDateTime("2020-03-25 15:11:31")).build(),
	new OperatorInput(UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "TestOperator"),
	"3w_test",
	nodeA,
	nodeB,
	nodeC,
	1,
	transformerTypeAtoBtoC,
	0,
	true
	)


	public static final SwitchInput switchAtoB = new SwitchInput(
	UUID.fromString("5dc88077-aeb6-4711-9142-db57287640b1"),
	OperationTime.builder().withStart(TimeTools.toZonedDateTime("2020-03-24 15:11:31")).withEnd(TimeTools.toZonedDateTime("2020-03-25 15:11:31")).build(),
	new OperatorInput(UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "TestOperator"),
	"test_switch_AtoB",
	nodeA,
	nodeB,
	true
	)

	private static final LineTypeInput lineTypeInputCtoD = new LineTypeInput(
	UUID.fromString("3bed3eb3-9790-4874-89b5-a5434d408088"),
	"lineType_AtoB",
	Quantities.getQuantity(0.00000000322, PowerSystemUnits.SIEMENS_PER_KILOMETRE),
	Quantities.getQuantity(0, PowerSystemUnits.SIEMENS_PER_KILOMETRE),
	Quantities.getQuantity(0.437, PowerSystemUnits.OHM_PER_KILOMETRE),
	Quantities.getQuantity(0.356, PowerSystemUnits.OHM_PER_KILOMETRE),
	Quantities.getQuantity(300,  PowerSystemUnits.AMPERE),
	Quantities.getQuantity(20, KILOVOLT)

	)

	public static final LineInput lineCtoD = new LineInput(
	UUID.fromString("91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
	OperationTime.builder().withStart(TimeTools.toZonedDateTime("2020-03-24 15:11:31")).withEnd(TimeTools.toZonedDateTime("2020-03-25 15:11:31")).build(),
	new OperatorInput(UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "TestOperator"),
	"test_line_AtoB",
	nodeC, nodeD,
	2,
	lineTypeInputCtoD,
	Quantities.getQuantity(3, Units.METRE),
	geoJsonReader.read("{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}") as LineString,
	Optional.of("olm")
	)
	public static final LineGraphicInput lineGraphicCtoD = new LineGraphicInput(
	UUID.fromString("ece86139-3238-4a35-9361-457ecb4258b0"),
	"main",
	geoJsonReader.read("{ \"type\": \"LineString\", \"coordinates\": [[0, 0], [0, 10]]}") as LineString,
	lineCtoD
	)
}
