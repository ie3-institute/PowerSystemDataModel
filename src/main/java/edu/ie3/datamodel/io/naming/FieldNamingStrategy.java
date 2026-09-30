/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.AssetInput;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.IdCoordinateInput;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.UniqueInputEntity;
import edu.ie3.datamodel.models.input.connector.ConnectorInput;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.TransformerInput;
import edu.ie3.datamodel.models.input.connector.type.CableTypeInput;
import edu.ie3.datamodel.models.input.connector.type.ConductorInput;
import edu.ie3.datamodel.models.input.connector.type.LayerInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.ScreenLayerInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.system.AcInput;
import edu.ie3.datamodel.models.input.system.BmInput;
import edu.ie3.datamodel.models.input.system.ChpInput;
import edu.ie3.datamodel.models.input.system.EvInput;
import edu.ie3.datamodel.models.input.system.EvcsInput;
import edu.ie3.datamodel.models.input.system.FixedFeedInInput;
import edu.ie3.datamodel.models.input.system.HpInput;
import edu.ie3.datamodel.models.input.system.LoadInput;
import edu.ie3.datamodel.models.input.system.PvInput;
import edu.ie3.datamodel.models.input.system.StorageInput;
import edu.ie3.datamodel.models.input.system.SystemParticipantInput;
import edu.ie3.datamodel.models.input.system.WecInput;
import edu.ie3.datamodel.models.input.system.type.AcTypeInput;
import edu.ie3.datamodel.models.input.system.type.BmTypeInput;
import edu.ie3.datamodel.models.input.system.type.ChpTypeInput;
import edu.ie3.datamodel.models.input.system.type.EvTypeInput;
import edu.ie3.datamodel.models.input.system.type.HpTypeInput;
import edu.ie3.datamodel.models.input.system.type.StorageTypeInput;
import edu.ie3.datamodel.models.input.system.type.SystemParticipantTypeInput;
import edu.ie3.datamodel.models.input.system.type.WecTypeInput;
import edu.ie3.datamodel.models.input.thermal.AbstractStorageInput;
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput;
import edu.ie3.datamodel.models.input.thermal.DomesticHotWaterStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.datamodel.models.input.thermal.ThermalInput;
import edu.ie3.datamodel.models.input.thermal.ThermalSinkInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalUnitInput;
import edu.ie3.datamodel.models.result.CongestionResult;
import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.result.connector.ConnectorResult;
import edu.ie3.datamodel.models.result.connector.LineResult;
import edu.ie3.datamodel.models.result.connector.SwitchResult;
import edu.ie3.datamodel.models.result.connector.Transformer2WResult;
import edu.ie3.datamodel.models.result.connector.Transformer3WResult;
import edu.ie3.datamodel.models.result.connector.TransformerResult;
import edu.ie3.datamodel.models.result.system.AcResult;
import edu.ie3.datamodel.models.result.system.BmResult;
import edu.ie3.datamodel.models.result.system.ChpResult;
import edu.ie3.datamodel.models.result.system.ElectricalEnergyStorageResult;
import edu.ie3.datamodel.models.result.system.EmResult;
import edu.ie3.datamodel.models.result.system.EnergyBoundariesFlexOptionsResult;
import edu.ie3.datamodel.models.result.system.EvResult;
import edu.ie3.datamodel.models.result.system.EvcsResult;
import edu.ie3.datamodel.models.result.system.FixedFeedInResult;
import edu.ie3.datamodel.models.result.system.FlexOptionsResult;
import edu.ie3.datamodel.models.result.system.HpResult;
import edu.ie3.datamodel.models.result.system.LoadResult;
import edu.ie3.datamodel.models.result.system.PowerLimitFlexOptionsResult;
import edu.ie3.datamodel.models.result.system.PvResult;
import edu.ie3.datamodel.models.result.system.StorageResult;
import edu.ie3.datamodel.models.result.system.SystemParticipantResult;
import edu.ie3.datamodel.models.result.system.SystemParticipantWithHeatResult;
import edu.ie3.datamodel.models.result.system.WecResult;
import edu.ie3.datamodel.models.result.thermal.AbstractThermalStorageResult;
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult;
import edu.ie3.datamodel.models.result.thermal.DomesticHotWaterStorageResult;
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult;
import edu.ie3.datamodel.models.result.thermal.ThermalSinkResult;
import edu.ie3.datamodel.models.result.thermal.ThermalStorageResult;
import edu.ie3.datamodel.models.result.thermal.ThermalUnitResult;
import edu.ie3.datamodel.utils.CollectionUtils;

public class FieldNamingStrategy extends FieldNamingStrategyAdditions {
  public static final String ACTIVE_POWER_GRADIENT = "activePowerGradient";

  public static final String ALBEDO = "albedo";

  public static final String AREA = "area";

  public static final String ARMOR = "armor";

  public static final String AUTO_TAP = "autoTap";

  public static final String AZIMUTH = "azimuth";

  public static final String B = "b";

  public static final String B_M = "bM";

  public static final String C = "c";

  public static final String CAPEX = "capex";

  public static final String CHARGING_POINTS = "chargingPoints";

  public static final String CIRCULATING_LOSS_FACTOR = "circulatingLossFactor";

  public static final String CLOSED = "closed";

  public static final String CONDUCTOR = "conductor";

  public static final String CONTROL_STRATEGY = "controlStrategy";

  public static final String CONTROLLING_EM = "controllingEm";

  public static final String CORE_NUMBER = "coreNumber";

  public static final String COS_PHI_RATED = "cosPhiRated";

  public static final String COST_CONTROLLED = "costControlled";

  public static final String CP_CHARACTERISTIC = "cpCharacteristic";

  public static final String CROSS_SECTION = "crossSection";

  public static final String D_PHI = "dPhi";

  public static final String D_V = "dV";

  public static final String DIAMETER = "diameter";

  public static final String E_CONS = "eCons";

  public static final String E_CONS_ANNUAL = "eConsAnnual";

  public static final String E_MAX = "eMax";

  public static final String E_MIN = "eMin";

  public static final String E_STATE = "eState";

  public static final String E_STORAGE = "eStorage";

  public static final String EDDY_CURRENT_LOSS_FACTOR = "eddyCurrentLossFactor";

  public static final String ELECTRICAL_CAPACITANCE = "electricalCapacitance";

  public static final String ELECTRICAL_RESISTIVITY = "electricalResistivity";

  public static final String ELEVATION_ANGLE = "elevationAngle";

  public static final String ENERGY = "energy";

  public static final String ETA = "eta";

  public static final String ETA_CONV = "etaConv";

  public static final String ETA_EL = "etaEl";

  public static final String ETA_THERMAL = "etaThermal";

  public static final String ETH_CAPA = "ethCapa";

  public static final String ETH_LOSSES = "ethLosses";

  public static final String FEED_IN_TARIFF = "feedInTariff";

  public static final String FILL_LEVEL = "fillLevel";

  public static final String FILLER = "filler";

  public static final String FREQUENCY = "frequency";

  public static final String G = "g";

  public static final String G_M = "gM";

  public static final String GEO_POSITION = "geoPosition";

  public static final String HOUSING_TYPE = "housingType";

  public static final String HUB_HEIGHT = "hubHeight";

  public static final String I_A_ANG = "iAAng";

  public static final String I_A_MAG = "iAMag";

  public static final String I_B_ANG = "iBAng";

  public static final String I_B_MAG = "iBMag";

  public static final String I_C_ANG = "iCAng";

  public static final String I_C_MAG = "iCMag";

  public static final String I_MAX = "iMax";

  public static final String ID = "id";

  public static final String INDOOR_TEMPERATURE = "indoorTemperature";

  public static final String INLET_TEMP = "inletTemp";

  public static final String INNER_DIAMETER = "innerDiameter";

  public static final String INPUT_MODEL = "inputModel";

  public static final String IS_COMPACTED = "isCompacted";

  public static final String ISOLATION = "isolation";

  public static final String JACK = "jack";

  public static final String K_G = "kG";

  public static final String K_T = "kT";

  public static final String LENGTH = "length";

  public static final String LENGTH_OF_LAY = "lengthOfLay";

  public static final String LIMIT_TEMPERATURE = "limitTemperature";

  public static final String LOAD_PROFILE = "loadProfile";

  public static final String LOCATION_TYPE = "locationType";

  public static final String LOWER_TEMPERATURE_LIMIT = "lowerTemperatureLimit";

  public static final String MATERIAL = "material";

  public static final String MAX = "max";

  public static final String MIN = "min";

  public static final String NODE = "node";

  public static final String NODE_A = "nodeA";

  public static final String NODE_B = "nodeB";

  public static final String NODE_C = "nodeC";

  public static final String NODE_INTERNAL = "nodeInternal";

  public static final String NUMBER_OF_INHABITANTS = "numberOfInhabitants";

  public static final String OLM_CHARACTERISTIC = "olmCharacteristic";

  public static final String OPERATES_FROM = "operatesFrom";

  public static final String OPERATES_UNTIL = "operatesUntil";

  public static final String OPERATOR = "operator";

  public static final String OPEX = "opex";

  public static final String OUTER_DIAMETER = "outerDiameter";

  public static final String P = "p";

  public static final String P_MAX = "pMax";

  public static final String P_MIN = "pMin";

  public static final String P_OWN = "pOwn";

  public static final String P_REF = "pRef";

  public static final String P_THERMAL = "pThermal";

  public static final String P_THERMAL_MAX = "pThermalMax";

  public static final String PARALLEL_DEVICES = "parallelDevices";

  public static final String POINT = "point";

  public static final String PROXIMITY_EFFECT_COEFFICIENT = "proximityEffectCoefficient";

  public static final String Q = "q";

  public static final String Q_CHARACTERISTICS = "qCharacteristics";

  public static final String Q_DOT = "qDot";

  public static final String R = "r";

  public static final String R_SC = "rSc";

  public static final String R_SC_A = "rScA";

  public static final String R_SC_B = "rScB";

  public static final String R_SC_C = "rScC";

  public static final String RETURN_TEMP = "returnTemp";

  public static final String ROTOR_AREA = "rotorArea";

  public static final String S_RATED = "sRated";

  public static final String S_RATED_A = "sRatedA";

  public static final String S_RATED_B = "sRatedB";

  public static final String S_RATED_C = "sRatedC";

  public static final String S_RATED_DC = "sRatedDC";

  public static final String SCREEN = "screen";

  public static final String SKIN_EFFECT_COEFFICIENT = "skinEffectCoefficient";

  public static final String SLACK = "slack";

  public static final String SOC = "soc";

  public static final String STORAGE_VOLUME_LVL = "storageVolumeLvl";

  public static final String SUBGRID = "subgrid";

  public static final String SUBNET = "subnet";

  public static final String TAN_DELTA = "tanDelta";

  public static final String TAP_MAX = "tapMax";

  public static final String TAP_MIN = "tapMin";

  public static final String TAP_NEUTR = "tapNeutr";

  public static final String TAP_POS = "tapPos";

  public static final String TAP_SIDE = "tapSide";

  public static final String TARGET_TEMPERATURE = "targetTemperature";

  public static final String THERMAL_BUS = "thermalBus";

  public static final String THERMAL_CAPACITANCE = "thermalCapacitance";

  public static final String THERMAL_RESISTIVITY = "thermalResistivity";

  public static final String THERMAL_STORAGE = "thermalStorage";

  public static final String TIME = "time";

  public static final String TYPE = "type";

  public static final String UPPER_TEMPERATURE_LIMIT = "upperTemperatureLimit";

  public static final String UUID = "uuid";

  public static final String V2G_SUPPORT = "v2gSupport";

  public static final String V_ANG = "vAng";

  public static final String V_MAG = "vMag";

  public static final String V_RATED = "vRated";

  public static final String V_RATED_A = "vRatedA";

  public static final String V_RATED_B = "vRatedB";

  public static final String V_RATED_C = "vRatedC";

  public static final String V_TARGET = "vTarget";

  public static final String VALUE = "value";

  public static final String VOLT_LVL = "voltLvl";

  public static final String WIRE_DIAMETER = "wireDiameter";

  public static final String WIRES_NUMBER = "wiresNumber";

  public static final String X = "x";

  public static final String X_SC = "xSc";

  public static final String X_SC_A = "xScA";

  public static final String X_SC_B = "xScB";

  public static final String X_SC_C = "xScC";

  public static void registerFields() {
    ModelFields.register(
        CylindricalStorageInput.class,
        CollectionUtils.newSet(
            UUID, ID, THERMAL_BUS, STORAGE_VOLUME_LVL, INLET_TEMP, RETURN_TEMP, C, P_THERMAL_MAX),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        SystemParticipantWithHeatResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q, Q_DOT),
        CollectionUtils.newSet());
    ModelFields.register(
        EnergyBoundariesFlexOptionsResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, P_MIN, P_MAX, E_STATE, E_MIN, E_MAX),
        CollectionUtils.newSet());
    ModelFields.register(
        CongestionResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, SUBGRID, TYPE, VALUE, MIN, MAX),
        CollectionUtils.newSet());
    ModelFields.register(
        CylindricalStorageResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, Q_DOT, ENERGY, FILL_LEVEL),
        CollectionUtils.newSet());
    ModelFields.register(
        ThermalUnitResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, Q_DOT),
        CollectionUtils.newSet());
    ModelFields.register(
        BmInput.class,
        CollectionUtils.newSet(
            UUID,
            ID,
            NODE,
            Q_CHARACTERISTICS,
            CONTROLLING_EM,
            TYPE,
            COST_CONTROLLED,
            FEED_IN_TARIFF),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        DomesticHotWaterStorageResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, Q_DOT, ENERGY, FILL_LEVEL),
        CollectionUtils.newSet());
    ModelFields.register(
        Transformer2WTypeInput.class,
        CollectionUtils.newSet(
            UUID, ID, R_SC, X_SC, S_RATED, V_RATED_A, V_RATED_B, G_M, B_M, D_V, D_PHI, TAP_SIDE,
            TAP_NEUTR, TAP_MIN, TAP_MAX),
        CollectionUtils.newSet());
    ModelFields.register(
        ScreenLayerInput.class,
        CollectionUtils.newSet(
            UUID,
            ID,
            MATERIAL,
            INNER_DIAMETER,
            OUTER_DIAMETER,
            THERMAL_RESISTIVITY,
            THERMAL_CAPACITANCE,
            WIRES_NUMBER,
            WIRE_DIAMETER,
            ELECTRICAL_RESISTIVITY),
        CollectionUtils.newSet(AREA, LENGTH_OF_LAY));
    ModelFields.register(
        DomesticHotWaterStorageInput.class,
        CollectionUtils.newSet(
            UUID, ID, THERMAL_BUS, STORAGE_VOLUME_LVL, INLET_TEMP, RETURN_TEMP, C, P_THERMAL_MAX),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        ConnectorResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, I_A_MAG, I_A_ANG, I_B_MAG, I_B_ANG),
        CollectionUtils.newSet());
    ModelFields.register(
        HpInput.class,
        CollectionUtils.newSet(
            UUID, ID, NODE, Q_CHARACTERISTICS, CONTROLLING_EM, TYPE, THERMAL_BUS),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        WecInput.class,
        CollectionUtils.newSet(UUID, ID, NODE, Q_CHARACTERISTICS, CONTROLLING_EM, TYPE),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        AcResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q, Q_DOT),
        CollectionUtils.newSet());
    ModelFields.register(
        ConnectorInput.class,
        CollectionUtils.newSet(UUID, ID, NODE_A, NODE_B, PARALLEL_DEVICES),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        ThermalSinkResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, Q_DOT),
        CollectionUtils.newSet());
    ModelFields.register(
        TransformerResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, I_A_MAG, I_A_ANG, I_B_MAG, I_B_ANG, TAP_POS),
        CollectionUtils.newSet());
    ModelFields.register(
        StorageTypeInput.class,
        CollectionUtils.newSet(
            UUID,
            ID,
            CAPEX,
            OPEX,
            S_RATED,
            COS_PHI_RATED,
            E_STORAGE,
            P_MAX,
            ACTIVE_POWER_GRADIENT,
            ETA),
        CollectionUtils.newSet());
    ModelFields.register(
        EmInput.class,
        CollectionUtils.newSet(UUID, ID, CONTROL_STRATEGY, CONTROLLING_EM),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        HpResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q, Q_DOT),
        CollectionUtils.newSet());
    ModelFields.register(
        AcTypeInput.class,
        CollectionUtils.newSet(UUID, ID, CAPEX, OPEX, S_RATED, COS_PHI_RATED, P_THERMAL),
        CollectionUtils.newSet());
    ModelFields.register(
        ThermalSinkInput.class,
        CollectionUtils.newSet(UUID, ID, THERMAL_BUS),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        PowerLimitFlexOptionsResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, P_MIN, P_MAX, P_REF),
        CollectionUtils.newSet());
    ModelFields.register(
        ConductorInput.class,
        CollectionUtils.newSet(
            UUID,
            ID,
            MATERIAL,
            CROSS_SECTION,
            DIAMETER,
            IS_COMPACTED,
            THERMAL_RESISTIVITY,
            THERMAL_CAPACITANCE),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL, AREA));
    ModelFields.register(
        LoadResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q),
        CollectionUtils.newSet());
    ModelFields.register(
        ThermalHouseResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, Q_DOT, INDOOR_TEMPERATURE),
        CollectionUtils.newSet());
    ModelFields.register(
        BmResult.class, CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q), CollectionUtils.newSet());
    ModelFields.register(
        MeasurementUnitInput.class,
        CollectionUtils.newSet(UUID, ID, NODE, V_MAG, V_ANG, P, Q),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        ChpInput.class,
        CollectionUtils.newSet(
            UUID, ID, NODE, Q_CHARACTERISTICS, CONTROLLING_EM, THERMAL_BUS, TYPE, THERMAL_STORAGE),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        NodeInput.class,
        CollectionUtils.newSet(UUID, ID, V_TARGET, SLACK, GEO_POSITION, VOLT_LVL, V_RATED, SUBNET),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        AcInput.class,
        CollectionUtils.newSet(
            UUID, ID, NODE, Q_CHARACTERISTICS, CONTROLLING_EM, TYPE, THERMAL_BUS),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        Transformer2WInput.class,
        CollectionUtils.newSet(UUID, ID, NODE_A, NODE_B, PARALLEL_DEVICES, TAP_POS, AUTO_TAP, TYPE),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        ResultEntity.class, CollectionUtils.newSet(TIME, INPUT_MODEL), CollectionUtils.newSet());
    ModelFields.register(
        FlexOptionsResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, P_MIN, P_MAX),
        CollectionUtils.newSet());
    ModelFields.register(
        LineInput.class,
        CollectionUtils.newSet(
            UUID,
            ID,
            NODE_A,
            NODE_B,
            PARALLEL_DEVICES,
            TYPE,
            LENGTH,
            GEO_POSITION,
            OLM_CHARACTERISTIC),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        ElectricalEnergyStorageResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q, SOC),
        CollectionUtils.newSet());
    ModelFields.register(
        StorageInput.class,
        CollectionUtils.newSet(UUID, ID, NODE, Q_CHARACTERISTICS, CONTROLLING_EM, TYPE),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        StorageResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q, SOC),
        CollectionUtils.newSet());
    ModelFields.register(
        PvInput.class,
        CollectionUtils.newSet(
            UUID,
            ID,
            NODE,
            Q_CHARACTERISTICS,
            CONTROLLING_EM,
            ALBEDO,
            AZIMUTH,
            ETA_CONV,
            ELEVATION_ANGLE,
            K_G,
            K_T,
            S_RATED,
            COS_PHI_RATED),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        SystemParticipantTypeInput.class,
        CollectionUtils.newSet(UUID, ID, CAPEX, OPEX, S_RATED, COS_PHI_RATED),
        CollectionUtils.newSet());
    ModelFields.register(
        CableTypeInput.class,
        CollectionUtils.newSet(
            UUID,
            ID,
            CORE_NUMBER,
            CONDUCTOR,
            ISOLATION,
            FILLER,
            ARMOR,
            JACK,
            LIMIT_TEMPERATURE,
            FREQUENCY,
            SKIN_EFFECT_COEFFICIENT,
            PROXIMITY_EFFECT_COEFFICIENT,
            ELECTRICAL_CAPACITANCE,
            TAN_DELTA,
            CIRCULATING_LOSS_FACTOR,
            EDDY_CURRENT_LOSS_FACTOR),
        CollectionUtils.newSet(SCREEN));
    ModelFields.register(
        ThermalHouseInput.class,
        CollectionUtils.newSet(
            UUID,
            ID,
            THERMAL_BUS,
            ETH_LOSSES,
            ETH_CAPA,
            TARGET_TEMPERATURE,
            UPPER_TEMPERATURE_LIMIT,
            LOWER_TEMPERATURE_LIMIT,
            HOUSING_TYPE,
            NUMBER_OF_INHABITANTS),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        OperatorInput.class, CollectionUtils.newSet(UUID, ID), CollectionUtils.newSet());
    ModelFields.register(
        ChpResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q, Q_DOT),
        CollectionUtils.newSet());
    ModelFields.register(
        FixedFeedInInput.class,
        CollectionUtils.newSet(
            UUID, ID, NODE, Q_CHARACTERISTICS, CONTROLLING_EM, S_RATED, COS_PHI_RATED),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        SwitchResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, CLOSED),
        CollectionUtils.newSet());
    ModelFields.register(
        SwitchInput.class,
        CollectionUtils.newSet(UUID, ID, NODE_A, NODE_B, PARALLEL_DEVICES, CLOSED),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        HpTypeInput.class,
        CollectionUtils.newSet(UUID, ID, CAPEX, OPEX, S_RATED, COS_PHI_RATED, P_THERMAL),
        CollectionUtils.newSet());
    ModelFields.register(
        TransformerInput.class,
        CollectionUtils.newSet(UUID, ID, NODE_A, NODE_B, PARALLEL_DEVICES, TAP_POS, AUTO_TAP),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        EvResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q, SOC),
        CollectionUtils.newSet());
    ModelFields.register(
        AssetInput.class,
        CollectionUtils.newSet(UUID, ID),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        Transformer3WTypeInput.class,
        CollectionUtils.newSet(
            UUID, ID, S_RATED_A, S_RATED_B, S_RATED_C, V_RATED_A, V_RATED_B, V_RATED_C, R_SC_A,
            R_SC_B, R_SC_C, X_SC_A, X_SC_B, X_SC_C, G_M, B_M, D_V, D_PHI, TAP_NEUTR, TAP_MIN,
            TAP_MAX),
        CollectionUtils.newSet());
    ModelFields.register(
        SystemParticipantInput.class,
        CollectionUtils.newSet(UUID, ID, NODE, Q_CHARACTERISTICS, CONTROLLING_EM),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        LineTypeInput.class,
        CollectionUtils.newSet(UUID, ID, B, G, R, X, I_MAX, V_RATED),
        CollectionUtils.newSet());
    ModelFields.register(
        ChpTypeInput.class,
        CollectionUtils.newSet(
            UUID, ID, CAPEX, OPEX, S_RATED, COS_PHI_RATED, ETA_EL, ETA_THERMAL, P_THERMAL, P_OWN),
        CollectionUtils.newSet());
    ModelFields.register(
        PvResult.class, CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q), CollectionUtils.newSet());
    ModelFields.register(
        ThermalStorageInput.class,
        CollectionUtils.newSet(UUID, ID, THERMAL_BUS),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        WecTypeInput.class,
        CollectionUtils.newSet(
            UUID,
            ID,
            CAPEX,
            OPEX,
            S_RATED,
            COS_PHI_RATED,
            CP_CHARACTERISTIC,
            ETA_CONV,
            ROTOR_AREA,
            HUB_HEIGHT),
        CollectionUtils.newSet());
    ModelFields.register(
        ThermalInput.class,
        CollectionUtils.newSet(UUID, ID),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        NodeResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, V_MAG, V_ANG),
        CollectionUtils.newSet());
    ModelFields.register(
        Transformer3WInput.class,
        CollectionUtils.newSet(
            UUID,
            ID,
            NODE_A,
            NODE_B,
            PARALLEL_DEVICES,
            TAP_POS,
            AUTO_TAP,
            TYPE,
            NODE_C,
            NODE_INTERNAL),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        LayerInput.class,
        CollectionUtils.newSet(
            UUID,
            ID,
            MATERIAL,
            INNER_DIAMETER,
            OUTER_DIAMETER,
            THERMAL_RESISTIVITY,
            THERMAL_CAPACITANCE),
        CollectionUtils.newSet(AREA));
    ModelFields.register(
        Transformer2WResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, I_A_MAG, I_A_ANG, I_B_MAG, I_B_ANG, TAP_POS),
        CollectionUtils.newSet());
    ModelFields.register(
        Transformer3WResult.class,
        CollectionUtils.newSet(
            TIME, INPUT_MODEL, I_A_MAG, I_A_ANG, I_B_MAG, I_B_ANG, TAP_POS, I_C_MAG, I_C_ANG),
        CollectionUtils.newSet());
    ModelFields.register(
        EvInput.class,
        CollectionUtils.newSet(UUID, ID, NODE, Q_CHARACTERISTICS, CONTROLLING_EM, TYPE),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        ThermalUnitInput.class,
        CollectionUtils.newSet(UUID, ID, THERMAL_BUS),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        AbstractThermalStorageResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, Q_DOT, ENERGY, FILL_LEVEL),
        CollectionUtils.newSet());
    ModelFields.register(
        UniqueEntity.class, CollectionUtils.newSet(UUID), CollectionUtils.newSet());
    ModelFields.register(
        WecResult.class, CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q), CollectionUtils.newSet());
    ModelFields.register(
        LoadInput.class,
        CollectionUtils.newSet(
            UUID,
            ID,
            NODE,
            Q_CHARACTERISTICS,
            CONTROLLING_EM,
            LOAD_PROFILE,
            E_CONS_ANNUAL,
            S_RATED,
            COS_PHI_RATED),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        AssetTypeInput.class, CollectionUtils.newSet(UUID, ID), CollectionUtils.newSet());
    ModelFields.register(
        BmTypeInput.class,
        CollectionUtils.newSet(
            UUID, ID, CAPEX, OPEX, S_RATED, COS_PHI_RATED, ACTIVE_POWER_GRADIENT, ETA_CONV),
        CollectionUtils.newSet());
    ModelFields.register(
        EmResult.class, CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q), CollectionUtils.newSet());
    ModelFields.register(
        AbstractStorageInput.class,
        CollectionUtils.newSet(
            UUID, ID, THERMAL_BUS, STORAGE_VOLUME_LVL, INLET_TEMP, RETURN_TEMP, C, P_THERMAL_MAX),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        SystemParticipantResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q),
        CollectionUtils.newSet());
    ModelFields.register(
        EvcsInput.class,
        CollectionUtils.newSet(
            UUID,
            ID,
            NODE,
            Q_CHARACTERISTICS,
            CONTROLLING_EM,
            TYPE,
            CHARGING_POINTS,
            COS_PHI_RATED,
            LOCATION_TYPE,
            V2G_SUPPORT),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        EvTypeInput.class,
        CollectionUtils.newSet(
            UUID, ID, CAPEX, OPEX, S_RATED, COS_PHI_RATED, E_STORAGE, E_CONS, S_RATED_DC),
        CollectionUtils.newSet());
    ModelFields.register(
        ThermalBusInput.class,
        CollectionUtils.newSet(UUID, ID),
        CollectionUtils.newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL));
    ModelFields.register(
        EvcsResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q),
        CollectionUtils.newSet());
    ModelFields.register(
        IdCoordinateInput.class, CollectionUtils.newSet(ID, POINT), CollectionUtils.newSet());
    ModelFields.register(
        LineResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, I_A_MAG, I_A_ANG, I_B_MAG, I_B_ANG),
        CollectionUtils.newSet());
    ModelFields.register(
        UniqueInputEntity.class, CollectionUtils.newSet(UUID), CollectionUtils.newSet());
    ModelFields.register(
        FixedFeedInResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, P, Q),
        CollectionUtils.newSet());
    ModelFields.register(
        ThermalStorageResult.class,
        CollectionUtils.newSet(TIME, INPUT_MODEL, Q_DOT, ENERGY),
        CollectionUtils.newSet());
  }
}
