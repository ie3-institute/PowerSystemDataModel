/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import edu.ie3.datamodel.models.BdewLoadProfile
import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.StandardLoadProfile
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.system.BmInput
import edu.ie3.datamodel.models.input.system.ChpInput
import edu.ie3.datamodel.models.input.system.EvInput
import edu.ie3.datamodel.models.input.system.FixedFeedInInput
import edu.ie3.datamodel.models.input.system.HpInput
import edu.ie3.datamodel.models.input.system.LoadInput
import edu.ie3.datamodel.models.input.system.PvInput
import edu.ie3.datamodel.models.input.system.StorageInput
import edu.ie3.datamodel.models.input.system.WecInput
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiP
import edu.ie3.datamodel.models.input.system.characteristic.QV
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput
import edu.ie3.datamodel.models.input.system.type.BmTypeInput
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput
import edu.ie3.datamodel.models.input.system.type.EvTypeInput
import edu.ie3.datamodel.models.input.system.type.HpTypeInput
import edu.ie3.datamodel.models.input.system.type.StorageTypeInput
import edu.ie3.datamodel.models.input.system.type.WecTypeInput
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput
import edu.ie3.util.TimeUtil
import edu.ie3.util.quantities.interfaces.Currency
import edu.ie3.util.quantities.interfaces.DimensionlessRate
import edu.ie3.util.quantities.interfaces.EnergyPrice
import edu.ie3.util.quantities.interfaces.SpecificEnergy
import edu.ie3.util.quantities.interfaces.SpecificHeatCapacity
import tec.uom.se.quantity.Quantities

import javax.measure.Quantity
import javax.measure.quantity.Angle
import javax.measure.quantity.Area
import javax.measure.quantity.Dimensionless
import javax.measure.quantity.Energy
import javax.measure.quantity.Length
import javax.measure.quantity.Power
import javax.measure.quantity.Temperature
import javax.measure.quantity.Time
import javax.measure.quantity.Volume
import java.time.ZoneId

import static edu.ie3.util.quantities.PowerSystemUnits.*


class SystemParticipantTestData {

	// general participant data
	private static final OperationTime operationTime = OperationTime.builder()
	.withStart(TimeUtil.withDefaults.toZonedDateTime("2020-03-24 15:11:31"))
	.withEnd(TimeUtil.withDefaults.toZonedDateTime("2020-03-25 15:11:31")).build()
	private static final OperatorInput operator = new OperatorInput(
	UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "TestOperator")
	private static final NodeInput participantNode = GridTestData.nodeA

	// general type data
	private static final CosPhiFixed cosPhiFixed = new CosPhiFixed("cosPhiFixed:{(0.0,0.95)}")
	private static final CosPhiP cosPhiP = new CosPhiP("cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}")
	private static final QV qV = new QV("qV:{(0.9,-0.3),(0.95,0.0),(1.05,0.0),(1.1,0.3)}")
	public static final String cosPhiFixedDeSerialized = "cosPhiFixed:{(0.00,0.95)}"
	public static final String cosPhiPDeSerialized = "cosPhiP:{(0.00,1.00),(0.90,1.00),(1.20,-0.30)}"
	public static final String qVDeSerialized = "qV:{(0.90,-0.30),(0.95,0.00),(1.05,0.00),(1.10,0.30)}"
	private static final Quantity<Power> sRated = Quantities.getQuantity(25d, KILOVOLTAMPERE)
	private static final double cosPhiRated = 0.95
	private static final UUID typeUuid = UUID.fromString("5ebd8f7e-dedb-4017-bb86-6373c4b68eb8")
	private static final Quantity<Currency> capex = Quantities.getQuantity(100d, EURO)
	private static final Quantity<EnergyPrice> opex = Quantities.getQuantity(50d, EURO_PER_MEGAWATTHOUR)
	private static final Quantity<Dimensionless> etaConv = Quantities.getQuantity(98d, PERCENT)


	// FixedFeedInput
	public static final FixedFeedInInput fixedFeedInInput = new FixedFeedInInput(UUID.fromString("717af017-cc69-406f-b452-e022d7fb516a"), "test_fixedFeedInInput", operator,
	operationTime, participantNode, cosPhiFixed,
	sRated, cosPhiRated)

	// PV
	private static final double albedo = 0.20000000298023224
	private static final Quantity<Angle> azimuth = Quantities.getQuantity(-8.926613807678223, DEGREE_GEOM)
	private static final Quantity<Angle> height = Quantities.getQuantity(41.01871871948242, DEGREE_GEOM)
	private static double kT = 1
	private static double kG = 0.8999999761581421
	public static final PvInput pvInput = new PvInput(UUID.fromString("d56f15b7-8293-4b98-b5bd-58f6273ce229"), "test_pvInput", operator, operationTime,
	participantNode, cosPhiFixed, albedo, azimuth,
	etaConv, height, kG, kT, false, sRated, cosPhiRated)


	// WEC
	private static final WecCharacteristicInput wecCharacteristic = new WecCharacteristicInput("cP:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}")
	private static final Quantity<Area> rotorArea = Quantities.getQuantity(20, SQUARE_METRE)
	private static final Quantity<Length> hubHeight = Quantities.getQuantity(200, METRE)
	public static final WecTypeInput wecType = new WecTypeInput(typeUuid, "test_wecType", capex, opex,
	cosPhiRated, wecCharacteristic, etaConv, sRated, rotorArea, hubHeight)

	public static final WecInput wecInput = new WecInput(UUID.fromString("ee7e2e37-a5ad-4def-a832-26a317567ca1"), "test_wecInput", operator,
	operationTime, participantNode, cosPhiP,
	wecType, false)

	// CHP
	private static final Quantity<Dimensionless> etaEl = Quantities.getQuantity(19, PERCENT)
	private static final Quantity<Dimensionless> etaThermal = Quantities.getQuantity(76, PERCENT)
	private static final Quantity<Power> pOwn = Quantities.getQuantity(0, KILOWATT)
	private static final Quantity<Power> pThermal = Quantities.getQuantity(9, KILOWATT)
	public static final ChpTypeInput chpTypeInput = new ChpTypeInput(typeUuid, "test_chpType", capex, opex,
	etaEl, etaThermal, sRated, cosPhiRated, pThermal, pOwn)

	public static final ThermalBusInput thermalBus = new ThermalBusInput(UUID.fromString("0d95d7f2-49fb-4d49-8636-383a5220384e"), "test_thermalBusInput", operator, operationTime
	)
	private static final Quantity<Volume> storageVolumeLvl = Quantities.getQuantity(1.039154027, CUBIC_METRE)
	private static final Quantity<Volume> storageVolumeLvlMin = Quantities.getQuantity(0.3, CUBIC_METRE)
	private static final Quantity<Temperature> inletTemp = Quantities.getQuantity(110, CELSIUS)
	private static final Quantity<Temperature> returnTemp = Quantities.getQuantity(80, CELSIUS)
	private static final Quantity<SpecificHeatCapacity> c = Quantities.getQuantity(
	1, KILOWATTHOUR_PER_KELVIN_TIMES_CUBICMETRE)
	private static final ThermalStorageInput thermalStorage = new CylindricalStorageInput(UUID.fromString("8851813b-3a7d-4fee-874b-4df9d724e4b3"),
	"test_cylindricThermalStorage", thermalBus, storageVolumeLvl, storageVolumeLvlMin,
	inletTemp, returnTemp, c)

	public static final ChpInput chpInput = new ChpInput(UUID.fromString("9981b4d7-5a8e-4909-9602-e2e7ef4fca5c"), "test_chpInput", operator, operationTime,
	participantNode, thermalBus, cosPhiFixed, chpTypeInput, thermalStorage, false)


	// BM
	private static final Quantity<DimensionlessRate> loadGradient = Quantities.getQuantity(25, PERCENT_PER_HOUR)
	public static final BmTypeInput bmTypeInput = new BmTypeInput(typeUuid, "test_bmTypeInput", capex, opex,
	loadGradient, sRated, cosPhiRated, etaConv)

	private static final Quantity<EnergyPrice> feedInTarif = Quantities.getQuantity(10, EURO_PER_MEGAWATTHOUR)
	public static final BmInput bmInput = new BmInput(UUID.fromString("d06e5bb7-a3c7-4749-bdd1-4581ff2f6f4d"), "test_bmInput", operator, operationTime,
	participantNode, qV, bmTypeInput, false, false, feedInTarif)

	// EV
	private static final Quantity<Energy> eStorage = Quantities.getQuantity(100, KILOWATTHOUR)
	private static final Quantity<SpecificEnergy> eCons = Quantities.getQuantity(5, KILOWATTHOUR_PER_KILOMETRE)
	public static final EvTypeInput evTypeInput = new EvTypeInput(typeUuid, "test_evTypeInput", capex, opex,
	eStorage, eCons, sRated, cosPhiRated)
	public static final EvInput evInput = new EvInput(UUID.fromString("a17be20f-c7a7-471d-8ffe-015487c9d022"), "test_evInput", operator, operationTime,
	participantNode, cosPhiFixed, evTypeInput)

	// Load
	private static final Quantity<Energy> eConsAnnual = Quantities.getQuantity(4000, KILOWATTHOUR)
	private static final StandardLoadProfile standardLoadProfile = BdewLoadProfile.H0
	public static final LoadInput loadInput = new LoadInput(UUID.fromString("eaf77f7e-9001-479f-94ca-7fb657766f5f"), "test_loadInput", operator, operationTime,
	participantNode, cosPhiFixed, standardLoadProfile, false, eConsAnnual, sRated, cosPhiRated)

	// Storage
	private static final Quantity<Power> pMax = Quantities.getQuantity(15, KILOWATT)
	private static final Quantity<Dimensionless> eta = Quantities.getQuantity(95, PERCENT)
	private static final Quantity<Dimensionless> dod = Quantities.getQuantity(10, PERCENT)
	private static final Quantity<DimensionlessRate> cpRate = Quantities.getQuantity(1, PU_PER_HOUR)
	private static final Quantity<Time> lifeTime = Quantities.getQuantity(20, YEAR)
	private static final int lifeCycle = 100
	public static final StorageTypeInput storageTypeInput = new StorageTypeInput(typeUuid, "test_storageTypeInput",
	capex, opex, eStorage, sRated, cosPhiRated, pMax, cpRate, eta, dod, lifeTime, lifeCycle)
	public static final StorageInput storageInput = new StorageInput(UUID.fromString("06b58276-8350-40fb-86c0-2414aa4a0452"), "test_storageInput", operator, operationTime
	, participantNode, cosPhiFixed, storageTypeInput, "market")

	// HP
	public static final HpTypeInput hpTypeInput = new HpTypeInput(typeUuid, "test_hpTypeInput", capex, opex,
	sRated, cosPhiRated, pThermal)

	public static final HpInput hpInput = new HpInput(UUID.fromString("798028b5-caff-4da7-bcd9-1750fdd8742b"), "test_hpInput", operator, operationTime,
	participantNode, thermalBus, cosPhiFixed, hpTypeInput)


	public static allParticipants = [
		fixedFeedInInput,
		pvInput,
		loadInput,
		bmInput,
		storageInput,
		wecInput,
		evInput,
		chpInput,
		hpInput
	]

}
