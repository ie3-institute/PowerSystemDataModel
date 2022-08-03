/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.exceptions.ParsingException
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.connector.LineInput
import edu.ie3.datamodel.models.input.connector.Transformer2WInput
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput
import edu.ie3.datamodel.models.input.container.GraphicElements
import edu.ie3.datamodel.models.input.container.JointGridContainer
import edu.ie3.datamodel.models.input.container.RawGridElements
import edu.ie3.datamodel.models.input.container.SystemParticipants
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.StorageInput
import edu.ie3.datamodel.models.input.system.characteristic.OlmCharacteristicInput
import edu.ie3.datamodel.models.voltagelevels.GermanVoltageLevelUtils
import edu.ie3.datamodel.utils.GridAndGeoUtils
import edu.ie3.util.quantities.PowerSystemUnits
import org.locationtech.jts.geom.Point
import org.locationtech.jts.io.ParseException
import org.locationtech.jts.io.geojson.GeoJsonReader
import tech.units.indriya.quantity.Quantities

import javax.measure.MetricPrefix
import java.util.stream.Collectors

import static edu.ie3.util.quantities.PowerSystemUnits.*

/**
 * //ToDo: Class Description
 *
 * @version 0.1* @since 08.06.20
 */
class SampleJointGrid extends SystemParticipantTestData {

	static JointGridContainer grid() throws ParseException, ParsingException {

		RawGridElements rawGridElements = jointSampleRawGridElements()

		return new JointGridContainer(
				"sampleGrid",
				rawGridElements,
				systemParticipants(rawGridElements),
				new GraphicElements(Collections.emptySet()))
	}

	private static SystemParticipants systemParticipants(RawGridElements rawGridElements)
	throws ParsingException {

		// set the participant node to nodeA
		NodeInput participantNode =
				rawGridElements.getNodes().stream()
				.filter({ node -> node.getId().equalsIgnoreCase("nodeA") })
				.collect(Collectors.toList())
				.get(0)


		// general participant data
		final PvInput pvInput =
				new PvInput(
				UUID.fromString("d56f15b7-8293-4b98-b5bd-58f6273ce229"),
			"test_pvInput",
				operator,
				operationTime,
				participantNode,
				cosPhiFixed,
				albedo,
				azimuth,
				etaConv,
				elevationAngle,
				kG,
				kT,
		false,
				sRated,
				cosPhiRated)

		// Load
		final LoadInput loadInput =
				new LoadInput(
				UUID.fromString("eaf77f7e-9001-479f-94ca-7fb657766f5f"),
				"test_loadInput",
						operator,
						operationTime,
				participantNode,
						cosPhiFixed,
						standardLoadProfile,
				false,
						eConsAnnual,
						sRated,
						cosPhiRated)

		final LoadInput loadInput1 =
				new LoadInput(
				UUID.fromString("eaf77f7e-9001-479f-94ca-7fb657766f6f"),
				"test_loadInput1",
						operator,
						operationTime,
				participantNode,
						cosPhiFixed,
						standardLoadProfile,
				false,
						eConsAnnual,
						sRated,
						cosPhiRated)

		// Storage
		final StorageInput storageInput =
				new StorageInput(
				UUID.fromString("06b58276-8350-40fb-86c0-2414aa4a0452"),
				"test_storageInput",
						operator,
						operationTime,
				participantNode,
						cosPhiFixed,
				storageTypeInput)

		return new SystemParticipants(
				Collections.emptySet(),
				Collections.emptySet(),
				Collections.emptySet(),
				Collections.emptySet(),
				Collections.emptySet(),
				Collections.emptySet(),
				new HashSet<>(Arrays.asList(loadInput, loadInput1)),
				Collections.singleton(pvInput),
				Collections.singleton(storageInput),
				Collections.emptySet(),
		Collections.emptySet())
	}

	private static RawGridElements jointSampleRawGridElements() throws ParseException {

		return new RawGridElements(
				new HashSet<>(Arrays.asList(nodeA, nodeB, nodeC, nodeD, nodeE, nodeF, nodeG)),
				new HashSet<>(Arrays.asList(lineAB, lineAC, lineBC, lineDE, lineDF, lineEF)),
				new HashSet<>(Arrays.asList(transformerDtoA, transformerGtoD)),
				Collections.emptySet(),
				Collections.emptySet(),
				Collections.emptySet())
	}

	private static final GeoJsonReader geoJsonReader = new GeoJsonReader()

	// LV
	public static final NodeInput nodeA =
	new NodeInput(
	UUID.fromString("4ca90220-74c2-4369-9afa-a18bf068840d"),
	"nodeA",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	Quantities.getQuantity(1, PU),
	false,
	(Point) geoJsonReader.read(
	"{ \"type\": \"Point\", \"coordinates\": [6.592276813887139, 49.37770599548332] }"),
	GermanVoltageLevelUtils.LV,
	1)

	public static final NodeInput nodeB =
	new NodeInput(
	UUID.fromString("47d29df0-ba2d-4d23-8e75-c82229c5c758"),
	"nodeB",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	Quantities.getQuantity(1, PU),
	false,
	(Point) geoJsonReader.read(
	"{ \"type\": \"Point\", \"coordinates\": [6.593358228545043, 49.377139554965595] }"),
	GermanVoltageLevelUtils.LV,
	1)

	public static final NodeInput nodeC =
	new NodeInput(
	UUID.fromString("bd837a25-58f3-44ac-aa90-c6b6e3cd91b2"),
	"nodeC",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	Quantities.getQuantity(1, PU),
	false,
	(Point) geoJsonReader.read(
	"{ \"type\": \"Point\", \"coordinates\": [6.592850044965246, 49.37684839141148] }"),
	GermanVoltageLevelUtils.LV,
	1)

	public static final LineTypeInput lv_lineType =
	new LineTypeInput(
	UUID.fromString("3bed3eb3-9790-4874-89b5-a5434d408088"),
	"lineType_AtoB",
	Quantities.getQuantity(191.636993408203, SIEMENS_PER_KILOMETRE),
	Quantities.getQuantity(0, SIEMENS_PER_KILOMETRE),
	Quantities.getQuantity(0.253899991512299, OHM_PER_KILOMETRE),
	Quantities.getQuantity(0.0691149979829788, OHM_PER_KILOMETRE),
	Quantities.getQuantity(265, AMPERE),
	Quantities.getQuantity(0.4, KILOVOLT))

	public static final LineInput lineAB =
	new LineInput(
	UUID.fromString("92ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
	"lineAtoB",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	nodeA,
	nodeB,
	1,
	lv_lineType,
	GridAndGeoUtils.distanceBetweenNodes(nodeA, nodeB),
	GridAndGeoUtils.buildSafeLineStringBetweenNodes(nodeA, nodeB),
	OlmCharacteristicInput.CONSTANT_CHARACTERISTIC)

	public static final LineInput lineAC =
	new LineInput(
	UUID.fromString("93ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
	"lineAtoC",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	nodeA,
	nodeC,
	1,
	lv_lineType,
	GridAndGeoUtils.distanceBetweenNodes(nodeA, nodeC),
	GridAndGeoUtils.buildSafeLineStringBetweenNodes(nodeA, nodeC),
	OlmCharacteristicInput.CONSTANT_CHARACTERISTIC)

	public static final LineInput lineBC =
	new LineInput(
	UUID.fromString("94ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
	"lineBtoC",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	nodeB,
	nodeC,
	1,
	lv_lineType,
	GridAndGeoUtils.distanceBetweenNodes(nodeB, nodeC),
	GridAndGeoUtils.buildSafeLineStringBetweenNodes(nodeB, nodeC),
	OlmCharacteristicInput.CONSTANT_CHARACTERISTIC)

	// MV
	public static final NodeInput nodeD =
	new NodeInput(
	UUID.fromString("09aec636-791b-45aa-b981-b14edf171c4c"),
	"nodeD",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	Quantities.getQuantity(1, PowerSystemUnits.PU),
	false,
	(Point) geoJsonReader.read(
	"{ \"type\": \"Point\", \"coordinates\": [6.592276813887139, 49.37770599548332] }"),
	GermanVoltageLevelUtils.MV_10KV,
	2)

	public static final NodeInput nodeE =
	new NodeInput(
	UUID.fromString("10aec636-791b-45aa-b981-b14edf171c4c"),
	"nodeE",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	Quantities.getQuantity(1, PU),
	false,
	(Point) geoJsonReader.read(
	"{ \"type\": \"Point\", \"coordinates\": [6.572286813887139, 49.39770699548332] }"),
	GermanVoltageLevelUtils.MV_10KV,
	2)

	public static final NodeInput nodeF =
	new NodeInput(
	UUID.fromString("11aec636-791b-45aa-b981-b14edf171c4c"),
	"nodeF",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	Quantities.getQuantity(1, PU),
	false,
	(Point) geoJsonReader.read(
	"{ \"type\": \"Point\", \"coordinates\": [6.572286813887139, 49.38770799548332] }"),
	GermanVoltageLevelUtils.MV_10KV,
	2)

	// HV
	public static final NodeInput nodeG =
	new NodeInput(
	UUID.fromString("11aec637-791b-45aa-b981-b14edf171c4c"),
	"nodeG",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	Quantities.getQuantity(1, PU),
	false,
	(Point) geoJsonReader.read(
	"{ \"type\": \"Point\", \"coordinates\": [6.592276813887139, 49.37770599548332] }"),
	GermanVoltageLevelUtils.HV,
	4)

	public static final LineTypeInput mv_lineType =
	new LineTypeInput(
	UUID.fromString("4bed3eb3-9790-4874-89b5-a5434d408088"),
	"lineType_AtoB",
	Quantities.getQuantity(191.636993408203, SIEMENS_PER_KILOMETRE),
	Quantities.getQuantity(0, SIEMENS_PER_KILOMETRE),
	Quantities.getQuantity(0.207000002264977, OHM_PER_KILOMETRE),
	Quantities.getQuantity(0.0691149979829788, OHM_PER_KILOMETRE),
	Quantities.getQuantity(300, AMPERE),
	Quantities.getQuantity(10, KILOVOLT))

	public static final LineInput lineDE =
	new LineInput(
	UUID.fromString("99ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
	"lineDtoE",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	nodeD,
	nodeE,
	1,
	mv_lineType,
	GridAndGeoUtils.distanceBetweenNodes(nodeD, nodeE),
	GridAndGeoUtils.buildSafeLineStringBetweenNodes(nodeD, nodeE),
	OlmCharacteristicInput.CONSTANT_CHARACTERISTIC)

	public static final LineInput lineEF =
	new LineInput(
	UUID.fromString("99fc3bcf-1777-4d38-af67-0bf7c9fa73c7"),
	"lineEtoF",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	nodeE,
	nodeF,
	1,
	mv_lineType,
	GridAndGeoUtils.distanceBetweenNodes(nodeE, nodeF),
	GridAndGeoUtils.buildSafeLineStringBetweenNodes(nodeE, nodeF),
	OlmCharacteristicInput.CONSTANT_CHARACTERISTIC)

	public static final LineInput lineDF =
	new LineInput(
	UUID.fromString("60ec3bcf-1777-4d38-af67-0bf7c9fa73c7"),
	"lineDtoF",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	nodeD,
	nodeF,
	1,
	mv_lineType,
	GridAndGeoUtils.distanceBetweenNodes(nodeD, nodeF),
	GridAndGeoUtils.buildSafeLineStringBetweenNodes(nodeD, nodeF),
	OlmCharacteristicInput.CONSTANT_CHARACTERISTIC)

	// transformers
	public static final Transformer2WTypeInput transformerType_LV_MV_10KV =
	new Transformer2WTypeInput(
	UUID.fromString("08559390-d7c0-4427-a2dc-97ba312ae0ac"),
	"MS-NS_1",
	Quantities.getQuantity(10.078, OHM),
	Quantities.getQuantity(23.312, OHM),
	Quantities.getQuantity(630d, KILOVOLTAMPERE),
	Quantities.getQuantity(10d, KILOVOLT),
	Quantities.getQuantity(0.4, KILOVOLT),
	Quantities.getQuantity(0d, MetricPrefix.MICRO(SIEMENS)),
	Quantities.getQuantity(0d, MetricPrefix.MICRO(SIEMENS)),
	Quantities.getQuantity(0.5, PERCENT),
	Quantities.getQuantity(0d, DEGREE_GEOM),
	false,
	0,
	-10,
	10)

	public static final Transformer2WInput transformerDtoA =
	new Transformer2WInput(
	UUID.fromString("58247de7-e297-4d9b-a5e4-b662c058c655"),
	"transformerAtoD",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	nodeD,
	nodeA,
	1,
	transformerType_LV_MV_10KV,
	0,
	false)

	public static final Transformer2WTypeInput transformerType_MV_HV_110KV =
	new Transformer2WTypeInput(
	UUID.fromString("08559390-d7c0-4427-a2dc-97ba312ae0ac"),
	"MS-NS_1",
	Quantities.getQuantity(10.078, OHM),
	Quantities.getQuantity(23.312, OHM),
	Quantities.getQuantity(800d, KILOVOLTAMPERE),
	Quantities.getQuantity(110d, KILOVOLT),
	Quantities.getQuantity(10d, KILOVOLT),
	Quantities.getQuantity(0d, MetricPrefix.MICRO(SIEMENS)),
	Quantities.getQuantity(0d, MetricPrefix.MICRO(SIEMENS)),
	Quantities.getQuantity(0.5, PERCENT),
	Quantities.getQuantity(0d, DEGREE_GEOM),
	false,
	0,
	-10,
	10)

	public static final Transformer2WInput transformerGtoD =
	new Transformer2WInput(
	UUID.fromString("58257de7-e297-4d9b-a5e4-b662c058c655"),
	"transformerGtoD",
	OperatorInput.NO_OPERATOR_ASSIGNED,
	OperationTime.notLimited(),
	nodeG,
	nodeD,
	1,
	transformerType_MV_HV_110KV,
	0,
	false)

}
