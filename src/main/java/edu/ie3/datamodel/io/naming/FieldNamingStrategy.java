/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.connector.ConnectorInput;

/** Class that contains all known field names. */
public class FieldNamingStrategy {

  // general
  public static final String ID = "id";
  public static final String OPERATOR = "operator";
  public static final String OPERATES_FROM = "operatesFrom";
  public static final String OPERATES_UNTIL = "operatesUntil";
  public static final String UUID = UniqueEntity.UUID_FIELD_NAME;
  public static final String GEO_POSITION = "geoPosition";
  public static final String NODE = "node";

  // power
  public static final String P = "p";
  public static final String POWER = P;
  public static final String ACTIVE_POWER = P;
  public static final String P_MAX = "pMax";
  public static final String P_MIN = "pMin";
  public static final String P_REF = "pRef";
  public static final String P_OWN = "pOwn";
  public static final String P_THERMAL = "pThermal";
  public static final String P_THERMAL_MAX = "pThermalMax";
  public static final String Q = "q";
  public static final String Q_DOT = "qDot";
  public static final String REACTIVE_POWER = Q;
  public static final String S_RATED = "sRated";
  public static final String S_RATED_A = "sRatedA";
  public static final String S_RATED_B = "sRatedB";
  public static final String S_RATED_C = "sRatedC";
  public static final String S_RATED_DC = "sRatedDC";

  // energy
  public static final String E_MAX = "eMax";
  public static final String E_MIN = "eMin";

  // node
  public static final String SLACK = "slack";
  public static final String SUBNET = "subnet";

  // connector asset
  public static final String AUTO_TAP = "autoTap";
  public static final String CLOSED = "closed";
  public static final String LENGTH = "length";
  public static final String NODE_A = "nodeA";
  public static final String NODE_B = "nodeB";
  public static final String NODE_C = "nodeC";

  /**
   * Attribute that _can_, but does not _have to_ be present for the creation of {@link
   * ConnectorInput}s.
   */
  public static final String PARALLEL_DEVICES = "parallelDevices";

  public static final String TAP_POS = "tapPos";

  // connector type
  public static final String B = "b";
  public static final String B_M = "bM";
  public static final String D_PHI = "dPhi";
  public static final String D_V = "dV";
  public static final String G = "g";
  public static final String G_M = "gM";
  public static final String I_MAX = "iMax";
  public static final String R = "r";
  public static final String R_SC = "rSc";
  public static final String R_SC_A = "rScA";
  public static final String R_SC_B = "rScB";
  public static final String R_SC_C = "rScC";
  public static final String TAP_MAX = "tapMax";
  public static final String TAP_MIN = "tapMin";
  public static final String TAP_NEUTR = "tapNeutr";
  public static final String TAP_SIDE = "tapSide";
  public static final String X = "x";
  public static final String X_SC = "xSc";
  public static final String X_SC_A = "xScA";
  public static final String X_SC_B = "xScB";
  public static final String X_SC_C = "xScC";

  // efficiency
  public static final String ETA = "eta";
  public static final String ETA_CONV = "etaConv";
  public static final String ETA_EL = "etaEl";
  public static final String ETA_THERMAL = "etaThermal";

  // participant
  public static final String CHARGING_POINTS = "chargingPoints";
  public static final String CONTROLLING_EM = "controllingEm";
  public static final String CONTROL_STRATEGY = "controlStrategy";
  public static final String COST_CONTROLLED = "costControlled";
  public static final String COS_PHI_RATED = "cosPhiRated";
  public static final String E_CONS_ANNUAL = "eConsAnnual";
  public static final String FEED_IN_TARIFF = "feedInTariff";
  public static final String LOCATION_TYPE = "locationType";
  public static final String LOAD_PROFILE = "loadProfile";
  public static final String OLM_CHARACTERISTIC = "olmCharacteristic";
  public static final String Q_CHARACTERISTICS = "qCharacteristics";
  public static final String TYPE = "type";
  public static final String V2G_SUPPORT = "v2gSupport";

  // participant type
  public static final String ACTIVE_POWER_GRADIENT = "activePowerGradient";
  public static final String CAP_EX = "capex";
  public static final String CP_CHARACTERISTIC = "cpCharacteristic";
  public static final String E_CONS = "eCons";
  public static final String E_STORAGE = "eStorage";
  public static final String HUB_HEIGHT = "hubHeight";
  public static final String OP_EX = "opex";
  public static final String ROTOR_AREA = "rotorArea";

  // pv
  public static final String ALBEDO = "albedo";
  public static final String AZIMUTH = "azimuth";
  public static final String ELEVATION_ANGLE = "elevationAngle";
  public static final String KG = "kG";
  public static final String KT = "kT";

  // thermal
  public static final String C = "c";
  public static final String ETH_CAPA = "ethCapa";
  public static final String ETH_LOSSES = "ethLosses";
  public static final String HOUSING_TYPE = "housingType";
  public static final String HEAT_DEMAND = "heatDemand";
  public static final String INDOOR_TEMPERATURE = "indoorTemperature";
  public static final String INLET_TEMP = "inletTemp";
  public static final String LOWER_TEMPERATURE_LIMIT = "lowerTemperatureLimit";
  public static final String NUMBER_INHABITANTS = "numberInhabitants";
  public static final String RETURN_TEMP = "returnTemp";
  public static final String STORAGE_VOLUME_LVL = "storageVolumeLvl";
  public static final String TARGET_TEMPERATURE = "targetTemperature";
  public static final String THERMAL_BUS = "thermalBus";
  public static final String THERMAL_STORAGE = "thermalStorage";
  public static final String UPPER_TEMPERATURE_LIMIT = "upperTemperatureLimit";

  // time series
  public static final String ASSET = "asset";
  public static final String COLUMN_SCHEME = "columnScheme";
  public static final String QUARTER_HOUR = "quarterHour";
  public static final String TIME_SERIES = "timeSeries";
  public static final String K_WEEKDAY = "kWd";
  public static final String K_SATURDAY = "kSa";
  public static final String K_SUNDAY = "kSu";
  public static final String MY_WEEKDAY = "myWd";
  public static final String MY_SATURDAY = "mySa";
  public static final String MY_SUNDAY = "mySu";
  public static final String SIGMA_WEEKDAY = "sigmaWd";
  public static final String SIGMA_SATURDAY = "sigmaSa";
  public static final String SIGMA_SUNDAY = "sigmaSu";
  public static final String COORDINATE = "coordinate";
  public static final String COORDINATE_ID = ID;
  public static final String WEATHER_COORDINATE_ID = "coordinateId";
  public static final String COSMO_DIFFUSE_IRRADIANCE = "diffuseIrradiance";
  public static final String COSMO_DIRECT_IRRADIANCE = "directIrradiance";
  public static final String COSMO_TEMPERATURE = "temperature";
  public static final String COSMO_WIND_DIRECTION = "windDirection";
  public static final String COSMO_WIND_VELOCITY = "windVelocity";
  public static final String COSMO_GROUND_TEMPERATURE_LEVEL_1 = "groundTemperatureLevel1";
  public static final String COSMO_GROUND_TEMPERATURE_LEVEL_2 = "groundTemperatureLevel2";
  public static final String TID = "tid";
  public static final String LONG_GEO = "longGeo";
  public static final String LAT_GEO = "latGeo";
  public static final String LONG_ROT = "longRot";
  public static final String LAT_ROT = "latRot";
  public static final String ICON_DIFFUSE_IRRADIANCE = "aswdifdS";
  public static final String ICON_DIRECT_IRRADIANCE = "aswdirS";
  public static final String ICON_TEMPERATURE = "t2m";
  public static final String ICON_WIND_VELOCITY_U = "u131m";
  public static final String ICON_WIND_VELOCITY_V = "v131m";
  public static final String ICON_GROUND_TEMPERATURE_LEVEL_1 = "tg1";
  public static final String ICON_GROUND_TEMPERATURE_LEVEL_2 = "tg2";
  public static final String LONG = "longitude";
  public static final String LAT = "latitude";
  public static final String COORDINATE_TYPE = "coordinateType";
  public static final String PRICE = "price";

  // voltage
  public static final String VOLT_LVL = "voltLvl";
  public static final String V_ANG = "vAng";
  public static final String V_MAG = "vMag";
  public static final String V_RATED = "vRated";
  public static final String V_RATED_A = "vRatedA";
  public static final String V_RATED_B = "vRatedB";
  public static final String V_RATED_C = "vRatedC";
  public static final String V_TARGET = "vTarget";

  // result
  public static final String IAANG = "iAAng";
  public static final String IAMAG = "iAMag";
  public static final String IBANG = "iBAng";
  public static final String IBMAG = "iBMag";
  public static final String ICANG = "iCAng";
  public static final String ICMAG = "iCMag";

  public static final String INPUT_MODEL = "inputModel";
  public static final String ENERGY = "energy";
  public static final String FILL_LEVEL = "fillLevel";
  public static final String MAX = "max";
  public static final String MIN = "min";
  public static final String SOC = "soc";
  public static final String SUBGRID = "subgrid";
  public static final String TAPPOS = "tapPos";
  public static final String TIME = "time";
  public static final String VALUE = "value";

  // graphic
  public static final String GRAPHIC_LAYER = "graphicLayer";
  public static final String LINE = "line";
  public static final String PATH_LINE_STRING = "path";
  public static final String POINT = "point";
}
