/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.MeasurementUnitInput
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
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.util.TimeUtil
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import org.locationtech.jts.io.geojson.GeoJsonReader
import tech.units.indriya.quantity.Quantities

import static edu.ie3.datamodel.models.StandardUnits.*
import static edu.ie3.util.quantities.PowerSystemUnits.*

/**
 * This class contains a collection of different model instances that can be used for testing purposes.
 * Please note that these entities do NOT necessarily form a valid grid. For valid topologies please refer
 * to {@link ComplexTopology}
 */
class GridTestData {

	private static final GeoJsonReader geoJsonReader = new GeoJsonReader()

	public static final OperationTime defaultOperationTime = OperationTime.builder().
	withStart(TimeUtil.withDefaults.toZonedDateTime("2020-03-24 15:11:31")).
	withEnd(TimeUtil.withDefaults.toZonedDateTime("2020-03-25 15:11:31")).build()

	public static final OperatorInput profBroccoli = new OperatorInput(
	UUID.fromString("f15105c4-a2de-4ab8-a621-4bc98e372d92"),
	"Univ.-Prof. Dr. rer. hort. Klaus-Dieter Brokkoli"
	)

	public static final Transformer2WTypeInput transformerTypeBtoD = new Transformer2WTypeInput(
	UUID.fromString("202069a7-bcf8-422c-837c-273575220c8a"),
	"HS-MS_1",
	Quantities.getQuantity(45.375d, IMPEDANCE),
	Quantities.getQuantity(102.759d, IMPEDANCE),
	Quantities.getQuantity(20000d, ACTIVE_POWER_IN),
	Quantities.getQuantity(110d, RATED_VOLTAGE_MAGNITUDE),
	Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE),
	Quantities.getQuantity(0d, ADMITTANCE),
	Quantities.getQuantity(0d, ADMITTANCE),
	Quantities.getQuantity(1.5d, DV_TAP),
	Quantities.getQuantity(0d, DPHI_TAP),
	false,
	0,
	-10,
	10
	)
	private static final Transformer2WTypeInput transformerTypeBtoE = new Transformer2WTypeInput(
	UUID.fromString("ac30443b-29e7-4635-b399-1062cfb3ffda"),
	"transformer_type_gedfi89fc7c895076ff25ec6d3b2e7ab9a1b24b37f73ecf30f895005d766a8d8d2774aa",
	Quantities.getQuantity(0d, IMPEDANCE),
	Quantities.getQuantity(51.72750115394592d, IMPEDANCE),
	Quantities.getQuantity(40000d, ACTIVE_POWER_IN),
	Quantities.getQuantity(110d, RATED_VOLTAGE_MAGNITUDE),
	Quantities.getQuantity(10d, RATED_VOLTAGE_MAGNITUDE),
	Quantities.getQuantity(0d, ADMITTANCE),
	Quantities.getQuantity(0d, ADMITTANCE),
	Quantities.getQuantity(1.777780055999756d, DV_TAP),
	Quantities.getQuantity(0d, DPHI_TAP),
	false,
	10,
	1,
	19
	)

	private static final Transformer2WTypeInput transformerTypeCtoE = new Transformer2WTypeInput(
	UUID.fromString("8441dd78-c528-4e63-830d-52d341131432"),
	"no_shunt_elements_mv-mv",
	Quantities.getQuantity(1.5d, IMPEDANCE),
	Quantities.getQuantity(15.5d, IMPEDANCE),
	Quantities.getQuantity(250d, ACTIVE_POWER_IN),
	Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE),
	Quantities.getQuantity(10d, RATED_VOLTAGE_MAGNITUDE),
	Quantities.getQuantity(0d, ADMITTANCE),
	Quantities.getQuantity(0d, ADMITTANCE),
	Quantities.getQuantity(1.5d, DV_TAP),
	Quantities.getQuantity(0d, DPHI_TAP),
	false,
	0,
	-5,
	5
	)

	private static final Transformer2WTypeInput transformerTypeCtoX = new Transformer2WTypeInput(
	UUID.fromString("08559390-d7c0-4427-a2dc-97ba312ae0ac"),
	"MS-NS_1",
	Quantities.getQuantity(10.078d, IMPEDANCE),
	Quantities.getQuantity(23.312d, IMPEDANCE),
	Quantities.getQuantity(630d, ACTIVE_POWER_IN),
	Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE),
	Quantities.getQuantity(0.4d, RATED_VOLTAGE_MAGNITUDE),
	Quantities.getQuantity(0d, ADMITTANCE),
	Quantities.getQuantity(0d, ADMITTANCE),
	Quantities.getQuantity(0.5d, DV_TAP),
	Quantities.getQuantity(0d, DPHI_TAP),
	false,
	0,
	-10,
	10
	)

	public static final Transformer3WTypeInput transformerTypeAtoBtoC = new Transformer3WTypeInput(
	UUID.fromString("5b0ee546-21fb-4a7f-a801-5dbd3d7bb356"),
	"HöS-HS-MS_1",
	Quantities.getQuantity(120000d, ACTIVE_POWER_IN),
	Quantities.getQuantity(60000d, ACTIVE_POWER_IN),
	Quantities.getQuantity(40000d, ACTIVE_POWER_IN),
	Quantities.getQuantity(380d, RATED_VOLTAGE_MAGNITUDE),
	Quantities.getQuantity(110d, RATED_VOLTAGE_MAGNITUDE),
	Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE),
	Quantities.getQuantity(0.3d, IMPEDANCE),
	Quantities.getQuantity(0.025d, IMPEDANCE),
	Quantities.getQuantity(0.0008d, IMPEDANCE),
	Quantities.getQuantity(1d, IMPEDANCE),
	Quantities.getQuantity(0.08d, IMPEDANCE),
	Quantities.getQuantity(0.003d, IMPEDANCE),
	Quantities.getQuantity(40000d, ADMITTANCE),
	Quantities.getQuantity(1000d, ADMITTANCE),
	Quantities.getQuantity(1.5d, DV_TAP),
	Quantities.getQuantity(0d, DPHI_TAP),
	0,
	-10,
	10
	)

	public static final NodeInput nodeA = new NodeInput(
	UUID.fromString("4ca90220-74c2-4369-9afa-a18bf068840d"),
	"node_a",
	profBroccoli,
	defaultOperationTime,
	Quantities.getQuantity(1d, TARGET_VOLTAGE_MAGNITUDE),
	true,
	geoJsonReader.read("{ \"type\": \"Point\", \"coordinates\": [7.411111, 51.492528] }") as Point,
	GermanVoltageLevelUtils.EHV_380KV,
	1)

	public static final NodeInput nodeB = new NodeInput(
	UUID.fromString("47d29df0-ba2d-4d23-8e75-c82229c5c758"), "node_b", OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	Quantities.getQuantity(1d, TARGET_VOLTAGE_MAGNITUDE),
	false,
	NodeInput.DEFAULT_GEO_POSITION,
	GermanVoltageLevelUtils.HV,
	2)

	public static final NodeInput nodeC = new NodeInput(
	UUID.fromString("bd837a25-58f3-44ac-aa90-c6b6e3cd91b2"), "node_c", OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	Quantities.getQuantity(1d, TARGET_VOLTAGE_MAGNITUDE),
	false,
	NodeInput.DEFAULT_GEO_POSITION,
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
	UUID.fromString("6e0980e0-10f2-4e18-862b-eb2b7c90509b"), "node_d", OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	Quantities.getQuantity(1d, TARGET_VOLTAGE_MAGNITUDE),
	false,
	NodeInput.DEFAULT_GEO_POSITION,
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
	UUID.fromString("98a3e7fa-c456-455b-a5ea-bb19e7cbeb63"),
	"node_e",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	Quantities.getQuantity(1d, TARGET_VOLTAGE_MAGNITUDE),
	false,
	NodeInput.DEFAULT_GEO_POSITION,
	GermanVoltageLevelUtils.MV_10KV,
	5)

	public static final NodeInput nodeF = new NodeInput(
	UUID.fromString("9e37ce48-9650-44ec-b888-c2fd182aff01"),
	"node_f",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	Quantities.getQuantity(1d, TARGET_VOLTAGE_MAGNITUDE),
	false,
	NodeInput.DEFAULT_GEO_POSITION,
	GermanVoltageLevelUtils.LV,
	6)

	public static final NodeInput nodeG = new NodeInput(
	UUID.fromString("aaa74c1a-d07e-4615-99a5-e991f1d81cc4"),
	"node_g",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	Quantities.getQuantity(1d, TARGET_VOLTAGE_MAGNITUDE),
	false,
	NodeInput.DEFAULT_GEO_POSITION,
	GermanVoltageLevelUtils.LV,
	6)

	public static final Transformer2WInput transformerBtoD = new Transformer2WInput(
	UUID.fromString("58247de7-e297-4d9b-a5e4-b662c058c655"),
	"2w_single_test",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	nodeB,
	nodeD,
	1,
	transformerTypeBtoD,
	0,
	true
	)

	public static final Transformer2WInput transformerBtoE = new Transformer2WInput(
	UUID.fromString("8542bfa5-dc34-4367-b549-e9f515e6cced"),
	"2w_v_1",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	nodeB,
	nodeE,
	1,
	transformerTypeBtoE,
	0,
	true
	)

	public static final Transformer2WInput transformerCtoE = new Transformer2WInput(
	UUID.fromString("0c03391d-47e1-49b3-9c9c-1616258e78a7"),
	"2w_v_2",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	nodeC,
	nodeE,
	1,
	transformerTypeCtoE,
	0,
	true
	)

	public static final Transformer2WInput transformerCtoF = new Transformer2WInput(
	UUID.fromString("26a3583e-8e62-40b7-ba4c-092f6fd5a70d"),
	"2w_parallel_1", OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	nodeC,
	nodeF,
	1,
	transformerTypeCtoX,
	0,
	true
	)

	public static final Transformer2WInput transformerCtoG = new Transformer2WInput(
	UUID.fromString("5dc88077-aeb6-4711-9142-db57292640b1"), "2w_parallel_2",
	profBroccoli,
	defaultOperationTime,
	nodeC,
	nodeG,
	1,
	transformerTypeCtoX,
	0,
	true
	)

	public static final Transformer3WInput transformerAtoBtoC = new Transformer3WInput(
	UUID.fromString("cc327469-7d56-472b-a0df-edbb64f90e8f"),
	"3w_test",
	profBroccoli,
	defaultOperationTime,
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
	"test_switch_AtoB",
	profBroccoli,
	defaultOperationTime,
	nodeA,
	nodeB,
	true
	)

	public static final LineTypeInput lineTypeInputCtoD = new LineTypeInput(
	UUID.fromString("3bed3eb3-9790-4874-89b5-a5434d408088"),
	"lineType_AtoB",
	Quantities.getQuantity(0.00322d, ADMITTANCE_PER_LENGTH),
	Quantities.getQuantity(0d, ADMITTANCE_PER_LENGTH),
	Quantities.getQuantity(0.437d, OHM_PER_KILOMETRE),
	Quantities.getQuantity(0.356d, OHM_PER_KILOMETRE),
	Quantities.getQuantity(300d, ELECTRIC_CURRENT_MAGNITUDE),
	Quantities.getQuantity(20d, RATED_VOLTAGE_MAGNITUDE)

	)

	public static final LineInput lineCtoD = new LineInput(
	UUID.fromString("91ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
	"test_line_CtoD",
	profBroccoli,
	defaultOperationTime,
	nodeC,
	nodeD,
	2,
	lineTypeInputCtoD,
	Quantities.getQuantity(0.003d, LINE_LENGTH),
	geoJsonReader.read("{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}") as LineString,
	OlmCharacteristicInput.CONSTANT_CHARACTERISTIC
	)
	public static final LineGraphicInput lineGraphicCtoD = new LineGraphicInput(
	UUID.fromString("ece86139-3238-4a35-9361-457ecb4258b0"),
	"main",
	geoJsonReader.read("{ \"type\": \"LineString\", \"coordinates\": [[0, 0], [0, 10]]}") as LineString,
	lineCtoD
	)

	public static final LineInput lineAtoB = new LineInput(
	UUID.fromString("92ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
	"test_line_AtoB",
	profBroccoli,
	defaultOperationTime,
	nodeA,
	nodeB,
	2,
	lineTypeInputCtoD,
	Quantities.getQuantity(0.003d, LINE_LENGTH),
	geoJsonReader.read("{ \"type\": \"LineString\", \"coordinates\": [[7.411111, 51.492528], [7.414116, 51.484136]]}") as LineString,
	OlmCharacteristicInput.CONSTANT_CHARACTERISTIC
	)

	public static final MeasurementUnitInput measurementUnitInput = new MeasurementUnitInput(
	UUID.fromString("ce6119e3-f725-4166-b6e0-59f62e0c293d"),
	"test_measurementUnit",
	profBroccoli,
	defaultOperationTime,
	nodeG,
	true,
	true,
	true,
	true
	)
}
