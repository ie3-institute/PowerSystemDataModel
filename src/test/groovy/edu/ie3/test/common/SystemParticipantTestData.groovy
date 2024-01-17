/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
 */
package edu.ie3.test.common

import static edu.ie3.datamodel.models.StandardUnits.*

import edu.ie3.datamodel.models.OperationTime
import edu.ie3.datamodel.models.input.NodeInput
import edu.ie3.datamodel.models.input.OperatorInput
import edu.ie3.datamodel.models.input.container.SystemParticipants
import edu.ie3.datamodel.models.input.system.*
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiFixed
import edu.ie3.datamodel.models.input.system.characteristic.CosPhiP
import edu.ie3.datamodel.models.input.system.characteristic.QV
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput
import edu.ie3.datamodel.models.input.system.type.*
import edu.ie3.datamodel.models.input.system.type.chargingpoint.ChargingPointTypeUtils
import edu.ie3.datamodel.models.input.system.type.evcslocation.EvcsLocationType
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput
import edu.ie3.datamodel.models.profile.BdewStandardLoadProfile
import edu.ie3.datamodel.models.profile.StandardLoadProfile
import edu.ie3.util.TimeUtil
import edu.ie3.util.quantities.interfaces.*
import tech.units.indriya.ComparableQuantity
import tech.units.indriya.quantity.Quantities

import javax.measure.quantity.*

class SystemParticipantTestData {

  // general participant data
  static final OperationTime operationTime = OperationTime.builder()
  .withStart(TimeUtil.withDefaults.toZonedDateTime("2020-03-24 15:11:31"))
  .withEnd(TimeUtil.withDefaults.toZonedDateTime("2020-03-25 15:11:31")).build()
  static final OperatorInput operator = new OperatorInput(
  UUID.fromString("8f9682df-0744-4b58-a122-f0dc730f6510"), "TestOperator")
  public static final NodeInput participantNode = GridTestData.nodeA

  // general type data
  static final CosPhiFixed cosPhiFixed = new CosPhiFixed("cosPhiFixed:{(0.0,0.95)}")
  private static final CosPhiP cosPhiP = new CosPhiP("cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}")
  private static final QV qV = new QV("qV:{(0.9,-0.3),(0.95,0.0),(1.05,0.0),(1.1,0.3)}")
  public static final String cosPhiFixedSerialized = "cosPhiFixed:{(0.0,0.95)}"
  public static final String cosPhiPSerialized = "cosPhiP:{(0.0,1.0),(0.9,1.0),(1.2,-0.3)}"
  public static final String qVSerialized = "qV:{(0.9,-0.3),(0.95,0.0),(1.05,0.0),(1.1,0.3)}"
  static final ComparableQuantity<Power> sRated = Quantities.getQuantity(25d, ACTIVE_POWER_IN)
  static final double cosPhiRated = 0.95
  private static final UUID typeUuid = UUID.fromString("5ebd8f7e-dedb-4017-bb86-6373c4b68eb8")
  private static final ComparableQuantity<Currency> capex = Quantities.getQuantity(100d, CAPEX)
  private static final ComparableQuantity<EnergyPrice> opex = Quantities.getQuantity(50d, ENERGY_PRICE)
  static final ComparableQuantity<Dimensionless> etaConv = Quantities.getQuantity(98d, EFFICIENCY)


  // FixedFeedInput
  public static final FixedFeedInInput fixedFeedInInput = new FixedFeedInInput(
  UUID.fromString("717af017-cc69-406f-b452-e022d7fb516a"),
  "test_fixedFeedInInput",
  operator,
  operationTime,
  participantNode,
  cosPhiFixed,
  sRated,
  cosPhiRated
  )

  // PV
  static final double albedo = 0.20000000298023224d
  static final ComparableQuantity<Angle> azimuth = Quantities.getQuantity(-8.926613807678223d, AZIMUTH)
  static final ComparableQuantity<Angle> elevationAngle = Quantities.getQuantity(41.01871871948242d, SOLAR_ELEVATION_ANGLE)
  static double kT = 1d
  static double kG = 0.8999999761581421d
  public static final PvInput pvInput = new PvInput(
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
  cosPhiRated
  )


  // WEC
  private static final WecCharacteristicInput wecCharacteristic = new WecCharacteristicInput("cP:{(10.00,0.05),(15.00,0.10),(20.00,0.20)}")
  private static final ComparableQuantity<Area> rotorArea = Quantities.getQuantity(20, ROTOR_AREA)
  private static final ComparableQuantity<Length> hubHeight = Quantities.getQuantity(200, HUB_HEIGHT)
  public static final WecTypeInput wecType = new WecTypeInput(
  typeUuid,
  "test_wecType",
  capex,
  opex,
  sRated,
  cosPhiRated,
  wecCharacteristic,
  etaConv,
  rotorArea,
  hubHeight
  )

  public static final WecInput wecInput = new WecInput(
  UUID.fromString("ee7e2e37-a5ad-4def-a832-26a317567ca1"),
  "test_wecInput",
  operator,
  operationTime,
  participantNode,
  cosPhiP,
  wecType,
  false
  )

  // CHP
  private static final ComparableQuantity<Dimensionless> etaEl = Quantities.getQuantity(19, EFFICIENCY)
  private static final ComparableQuantity<Dimensionless> etaThermal = Quantities.getQuantity(76, EFFICIENCY)
  private static final ComparableQuantity<Power> pOwn = Quantities.getQuantity(0, ACTIVE_POWER_IN)
  private static final ComparableQuantity<Power> pThermal = Quantities.getQuantity(9, ACTIVE_POWER_IN)
  public static final ChpTypeInput chpTypeInput = new ChpTypeInput(
  typeUuid,
  "test_chpType",
  capex,
  opex,
  etaEl,
  etaThermal,
  sRated,
  cosPhiRated,
  pThermal,
  pOwn
  )

  public static final ThermalBusInput thermalBus = new ThermalBusInput(
  UUID.fromString("0d95d7f2-49fb-4d49-8636-383a5220384e"),
  "test_thermalBusInput",
  operator,
  operationTime
  )
  public static final ComparableQuantity<Volume> storageVolumeLvl = Quantities.getQuantity(1.039154027, VOLUME)
  public static final ComparableQuantity<Volume> storageVolumeLvlMin = Quantities.getQuantity(0.3, VOLUME)
  public static final ComparableQuantity<Temperature> inletTemp = Quantities.getQuantity(110, TEMPERATURE)
  public static final ComparableQuantity<Temperature> returnTemp = Quantities.getQuantity(80, TEMPERATURE)
  public static final ComparableQuantity<SpecificHeatCapacity> c = Quantities.getQuantity(
  1, SPECIFIC_HEAT_CAPACITY)
  public static final ThermalStorageInput thermalStorage = new CylindricalStorageInput(
  UUID.fromString("8851813b-3a7d-4fee-874b-4df9d724e4b3"),
  "test_cylindricThermalStorage",
  GridTestData.profBroccoli,
  OperationTime.notLimited(),
  thermalBus,
  storageVolumeLvl,
  storageVolumeLvlMin,
  inletTemp,
  returnTemp,
  c
  )

  public static final ChpInput chpInput = new ChpInput(
  UUID.fromString("9981b4d7-5a8e-4909-9602-e2e7ef4fca5c"),
  "test_chpInput",
  operator,
  operationTime,
  participantNode,
  thermalBus,
  cosPhiFixed,
  chpTypeInput,
  thermalStorage,
  false
  )

  // BM
  private static final ComparableQuantity<DimensionlessRate> loadGradient = Quantities.getQuantity(25, ACTIVE_POWER_GRADIENT)
  public static final BmTypeInput bmTypeInput = new BmTypeInput(
  typeUuid,
  "test_bmTypeInput",
  capex,
  opex,
  loadGradient,
  sRated,
  cosPhiRated,
  etaConv
  )

  private static final ComparableQuantity<EnergyPrice> feedInTarif = Quantities.getQuantity(10, ENERGY_PRICE)
  public static final BmInput bmInput = new BmInput(
  UUID.fromString("d06e5bb7-a3c7-4749-bdd1-4581ff2f6f4d"),
  "test_bmInput",
  operator,
  operationTime,
  participantNode,
  qV,
  bmTypeInput,
  false,
  false,
  feedInTarif
  )

  // EV
  private static final ComparableQuantity<Energy> eStorage = Quantities.getQuantity(100, ENERGY_IN)
  private static final ComparableQuantity<SpecificEnergy> eCons = Quantities.getQuantity(5, ENERGY_PER_DISTANCE)
  public static final EvTypeInput evTypeInput = new EvTypeInput(
  typeUuid,
  "test_evTypeInput",
  capex,
  opex,
  eStorage,
  eCons,
  sRated,
  cosPhiRated)
  public static final EvInput evInput = new EvInput(
  UUID.fromString("a17be20f-c7a7-471d-8ffe-015487c9d022"),
  "test_evInput",
  operator,
  operationTime,
  participantNode,
  cosPhiFixed,
  evTypeInput
  )

  // Load
  protected static final ComparableQuantity<Energy> eConsAnnual = Quantities.getQuantity(4000, ENERGY_IN)
  protected static final StandardLoadProfile standardLoadProfile = BdewStandardLoadProfile.H0
  public static final LoadInput loadInput = new LoadInput(
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
  cosPhiRated
  )

  // Storage
  private static final ComparableQuantity<Power> pMax = Quantities.getQuantity(15, ACTIVE_POWER_IN)
  private static final ComparableQuantity<Dimensionless> eta = Quantities.getQuantity(95, EFFICIENCY)
  private static final ComparableQuantity<Dimensionless> dod = Quantities.getQuantity(10, EFFICIENCY)
  private static final ComparableQuantity<DimensionlessRate> cpRate = Quantities.getQuantity(100, ACTIVE_POWER_GRADIENT)
  private static final ComparableQuantity<Time> lifeTime = Quantities.getQuantity(175316.4, LIFE_TIME)
  private static final int lifeCycle = 100
  public static final StorageTypeInput storageTypeInput = new StorageTypeInput(
  typeUuid,
  "test_storageTypeInput",
  capex,
  opex,
  eStorage,
  sRated,
  cosPhiRated,
  pMax,
  cpRate,
  eta,
  dod,
  lifeTime,
  lifeCycle
  )
  public static final StorageInput storageInput = new StorageInput(
  UUID.fromString("06b58276-8350-40fb-86c0-2414aa4a0452"),
  "test_storageInput",
  operator,
  operationTime,
  participantNode,
  cosPhiFixed,
  storageTypeInput
  )

  // HP
  public static final HpTypeInput hpTypeInput = new HpTypeInput(
  typeUuid,
  "test_hpTypeInput",
  capex,
  opex,
  sRated,
  cosPhiRated,
  pThermal
  )

  public static final HpInput hpInput = new HpInput(
  UUID.fromString("798028b5-caff-4da7-bcd9-1750fdd8742b"),
  "test_hpInput",
  operator,
  operationTime,
  participantNode,
  thermalBus,
  cosPhiFixed,
  hpTypeInput
  )

  // charging station
  public static final boolean v2gSupport = false
  public static final evcsInput = new EvcsInput(
  UUID.fromString("798028b5-caff-4da7-bcd9-1750fdd8742c"),
  "test_csInput",
  operator,
  operationTime,
  participantNode,
  cosPhiFixed,
  ChargingPointTypeUtils.HouseholdSocket,
  4,
  cosPhiRated,
  EvcsLocationType.HOME,
  v2gSupport
  )

  public static SystemParticipants emptySystemParticipants =
  new SystemParticipants(
  [] as Set,
  [] as Set,
  [] as Set,
  [] as Set,
  [] as Set,
  [] as Set,
  [] as Set,
  [] as Set,
  [] as Set,
  [] as Set)
}
