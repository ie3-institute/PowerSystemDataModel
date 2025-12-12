/*
 * © 2025. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.models.input.connector.ConnectorInput;

/** Final class that contains all entity field names. */
public final class EntityFieldNames {

  // general fields

  /** UUID field. */
  public static final String UUID_FIELD_NAME = "uuid";

  /** ID field. */
  public static final String ID = "id";

  /** Operator field */
  public static final String OPERATOR = "operator";

  /** Operation start time field. */
  public static final String OPERATES_FROM = "operatesFrom";

  /** Operation end time field. */
  public static final String OPERATES_UNTIL = "operatesUntil";

  /** Controlling em field. */
  public static final String CONTROLLING_EM = "controllingEm";

  /** Connecting node field. */
  public static final String NODE = "node";

  /** Type field. */
  public static final String TYPE = "type";

  /** Geo position field. */
  public static final String GEO_POSITION = "geoPosition";

  // connector fields

  /** Connecting node on port A. field */
  public static final String NODE_A = "nodeA";

  /** Connecting node on port B field. */
  public static final String NODE_B = "nodeB";

  /** Connecting node on port C field. */
  public static final String NODE_C = "nodeC";

  /** Transformer tap position field. */
  public static final String TAP_POS = "tapPos";

  /** Transformer auto tap field. */
  public static final String AUTO_TAP = "autoTap";

  /**
   * Parallel devices field.
   *
   * <p>Attribute that _can_, but does not _have to_ be present for the creation of {@link
   * ConnectorInput}s.
   */
  public static final String PARALLEL_DEVICES = "parallelDevices";

  /** Switch closed field. */
  public static final String CLOSED = "closed";

  /** Line susceptance field. */
  public static final String B = "b";

  /** Line conductance field. */
  public static final String G = "g";

  /** Line resistance field. */
  public static final String R = "r";

  /** Line reactance field. */
  public static final String X = "x";

  /** Maximal current field. */
  public static final String I_MAX = "iMax";

  /** Length field. */
  public static final String LENGTH = "length";

  /** Phase-to-ground conductance field. */
  public static final String G_M = "gM";

  /** Phase-to-ground susceptance field. */
  public static final String B_M = "bM";

  /** Voltage magnitude on tap change field. */
  public static final String D_V = "dV";

  /** Voltage angle on tap change field. */
  public static final String D_PHI = "dPhi";

  /** Neutral tap position field. */
  public static final String TAP_NEUTR = "tapNeutr";

  public static final String TAP_MIN = "tapMin";

  public static final String TAP_MAX = "tapMax";

  public static final String R_SC = "rSc";

  public static final String X_SC = "xSc";

  public static final String TAP_SIDE = "tapSide";

  public static final String R_SC_A = "rScA";

  public static final String R_SC_B = "rScB";

  public static final String R_SC_C = "rScC";

  public static final String X_SC_A = "xScA";

  public static final String X_SC_B = "xScB";

  public static final String X_SC_C = "xScC";

  public static final String IAMAG = "iAMag";

  public static final String IAANG = "iAAng";

  public static final String IBMAG = "iBMag";

  public static final String IBANG = "iBAng";

  public static final String ICMAG = "iCMag";

  public static final String ICANG = "iCAng";

  public static final String TAPPOS = "tapPos";

  // power fields

  /** Active power field. */
  public static final String POWER = "p";

  public static final String P_REF = "pRef";

  public static final String P_MIN = "pMin";

  public static final String P_MAX = "pMax";

  /** Active power gradient field. */
  public static final String ACTIVE_POWER_GRADIENT = "activePowerGradient";

  /** Thermal power field. */
  public static final String P_THERMAL = "pThermal";

  /** Internal power consumption field. */
  public static final String P_OWN = "pOwn";

  /** Reactive power field. */
  public static final String REACTIVE_POWER = "q";

  public static final String S_RATED = "sRated";

  public static final String S_RATED_DC = "sRatedDC";

  public static final String S_RATED_A = "sRatedA";

  public static final String S_RATED_B = "sRatedB";

  public static final String S_RATED_C = "sRatedC";

  // voltage fields

  /** Voltage level field. */
  public static final String VOLT_LVL = "voltLvl";

  /** Voltage target field. */
  public static final String V_TARGET = "vTarget";

  /** Rated voltage field. */
  public static final String V_RATED = "vRated";

  /** Voltage magnitude field. */
  public static final String V_MAG = "vMag";

  /** Voltage angle field. */
  public static final String V_ANG = "vAng";

  /** Rated voltage at port A field. */
  public static final String V_RATED_A = "vRatedA";

  /** Rated voltage at port B field. */
  public static final String V_RATED_B = "vRatedB";

  /** Rated voltage at port C field. */
  public static final String V_RATED_C = "vRatedC";

  // participant fields

  /** Olm characteristic field. */
  public static final String OLM_CHARACTERISTIC = "olmCharacteristic";

  /** Eta conv field. */
  public static final String ETA_CONV = "etaConv";

  /** Eta electrical field. */
  public static final String ETA_EL = "etaEl";

  /** Eta thermal field. */
  public static final String ETA_THERMAL = "etaThermal";

  public static final String E_STORAGE = "eStorage";

  public static final String E_CONS = "eCons";

  public static final String ETA = "eta";

  public static final String CAP_EX = "capex";

  public static final String OP_EX = "opex";

  public static final String COS_PHI_RATED = "cosPhiRated";

  public static final String CP_CHARACTERISTIC = "cpCharacteristic";

  public static final String ROTOR_AREA = "rotorArea";

  public static final String HUB_HEIGHT = "hubHeight";

  public static final String MARKET_REACTION = "marketReaction";

  public static final String COST_CONTROLLED = "costControlled";

  public static final String FEED_IN_TARIFF = "feedInTariff";

  public static final String THERMAL_BUS = "thermalBus";

  public static final String THERMAL_STORAGE = "thermalStorage";

  public static final String CHARGING_POINTS = "chargingPoints";

  public static final String LOCATION_TYPE = "locationType";

  public static final String V2G_SUPPORT = "v2gSupport";

  public static final String LOAD_PROFILE = "loadProfile";

  public static final String E_CONS_ANNUAL = "eConsAnnual";

  public static final String ALBEDO = "albedo";

  public static final String AZIMUTH = "azimuth";

  public static final String ELEVATION_ANGLE = "elevationAngle";

  public static final String KG = "kG";

  public static final String KT = "kT";

  public static final String Q_CHARACTERISTICS = "qCharacteristics";

  public static final String SOC = "soc";

  public static final String ENERGY = "energy";

  // thermal fields

  public static final String STORAGE_VOLUME_LVL = "storageVolumeLvl";

  public static final String INLET_TEMP = "inletTemp";

  public static final String RETURN_TEMP = "returnTemp";

  public static final String C = "c";

  public static final String P_THERMAL_MAX = "pThermalMax";

  public static final String ETH_LOSSES = "ethLosses";

  public static final String ETH_CAPA = "ethCapa";

  public static final String TARGET_TEMPERATURE = "targetTemperature";

  public static final String UPPER_TEMPERATURE_LIMIT = "upperTemperatureLimit";

  public static final String LOWER_TEMPERATURE_LIMIT = "lowerTemperatureLimit";

  public static final String Q_DOT = "qDot";

  public static final String FILL_LEVEL = "fillLevel";

  public static final String INDOOR_TEMPERATURE = "indoorTemperature";

  // other fields

  /** Control strategy field. */
  public static final String CONTROL_STRATEGY = "controlStrategy";

  /** Slack node field. */
  public static final String SLACK = "slack";

  /** Subgrid field. */
  public static final String SUBNET = "subnet";

  public static final String SUBGRID = "subgrid";

  public static final String HOUSING_TYPE = "housingType";

  public static final String NUMBER_INHABITANTS = "numberInhabitants";

  public static final String VALUE = "value";

  public static final String MIN = "min";

  public static final String MAX = "max";

  public static final String TIME = "time";

  public static final String INPUT_MODEL = "inputModel";

  // graphic fields

  /** Graphic layer field. */
  public static final String GRAPHIC_LAYER = "graphicLayer";

  /** Path field. */
  public static final String PATH_LINE_STRING = "path";

  /** Line field. */
  public static final String LINE = "line";

  /** Point field. */
  public static final String POINT = "point";
}
