/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.connector.ConnectorInput;

/** Interface that contains all known field names. */
public interface FieldNames {

  // general
  String ID = "id";
  String OPERATOR = "operator";
  String OPERATES_FROM = "operatesFrom";
  String OPERATES_UNTIL = "operatesUntil";
  String UUID = UniqueEntity.UUID_FIELD_NAME;
  String GEO_POSITION = "geoPosition";
  String NODE = "node";

  // power
  String P = "p";
  String POWER = P;
  String ACTIVE_POWER = P;
  String P_MAX = "pMax";
  String P_MIN = "pMin";
  String P_REF = "pRef";
  String P_OWN = "pOwn";
  String P_THERMAL = "pThermal";
  String P_THERMAL_MAX = "pThermalMax";
  String Q = "q";
  String Q_DOT = "qDot";
  String REACTIVE_POWER = Q;
  String S_RATED = "sRated";
  String S_RATED_A = "sRatedA";
  String S_RATED_B = "sRatedB";
  String S_RATED_C = "sRatedC";
  String S_RATED_DC = "sRatedDC";

  // node
  String SLACK = "slack";
  String SUBNET = "subnet";

  // connector asset
  String AUTO_TAP = "autoTap";
  String CLOSED = "closed";
  String LENGTH = "length";
  String NODE_A = "nodeA";
  String NODE_B = "nodeB";
  String NODE_C = "nodeC";

  /**
   * Attribute that _can_, but does not _have to_ be present for the creation of {@link
   * ConnectorInput}s.
   */
  String PARALLEL_DEVICES = "parallelDevices";

  String TAP_POS = "tapPos";

  // connector type
  String B = "b";
  String B_M = "bM";
  String D_PHI = "dPhi";
  String D_V = "dV";
  String G = "g";
  String G_M = "gM";
  String I_MAX = "iMax";
  String R = "r";
  String R_SC = "rSc";
  String R_SC_A = "rScA";
  String R_SC_B = "rScB";
  String R_SC_C = "rScC";
  String TAP_MAX = "tapMax";
  String TAP_MIN = "tapMin";
  String TAP_NEUTR = "tapNeutr";
  String TAP_SIDE = "tapSide";
  String X = "x";
  String X_SC = "xSc";
  String X_SC_A = "xScA";
  String X_SC_B = "xScB";
  String X_SC_C = "xScC";

  // efficiency
  String ETA = "eta";
  String ETH_CAPA = "ethCapa";
  String ETA_CONV = "etaConv";
  String ETA_EL = "etaEl";
  String ETH_LOSSES = "ethLosses";
  String ETA_THERMAL = "etaThermal";

  // participant
  String CHARGING_POINTS = "chargingPoints";
  String CONTROLLING_EM = "controllingEm";
  String CONTROL_STRATEGY = "controlStrategy";
  String COST_CONTROLLED = "costControlled";
  String COS_PHI_RATED = "cosPhiRated";
  String E_CONS_ANNUAL = "eConsAnnual";
  String FEED_IN_TARIFF = "feedInTariff";
  String LOCATION_TYPE = "locationType";
  String LOAD_PROFILE = "loadProfile";
  String OLM_CHARACTERISTIC = "olmCharacteristic";
  String Q_CHARACTERISTICS = "qCharacteristics";
  String TYPE = "type";
  String V2G_SUPPORT = "v2gSupport";

  // participant type
  String ACTIVE_POWER_GRADIENT = "activePowerGradient";
  String CAP_EX = "capex";
  String CP_CHARACTERISTIC = "cpCharacteristic";
  String E_CONS = "eCons";
  String E_STORAGE = "eStorage";
  String HUB_HEIGHT = "hubHeight";
  String OP_EX = "opex";
  String ROTOR_AREA = "rotorArea";

  // pv
  String ALBEDO = "albedo";
  String AZIMUTH = "azimuth";
  String ELEVATION_ANGLE = "elevationAngle";
  String KG = "kG";
  String KT = "kT";

  // thermal
  String C = "c";
  String HOUSING_TYPE = "housingType";
  String INDOOR_TEMPERATURE = "indoorTemperature";
  String INLET_TEMP = "inletTemp";
  String LOWER_TEMPERATURE_LIMIT = "lowerTemperatureLimit";
  String NUMBER_INHABITANTS = "numberInhabitants";
  String RETURN_TEMP = "returnTemp";
  String STORAGE_VOLUME_LVL = "storageVolumeLvl";
  String TARGET_TEMPERATURE = "targetTemperature";
  String THERMAL_BUS = "thermalBus";
  String THERMAL_STORAGE = "thermalStorage";
  String UPPER_TEMPERATURE_LIMIT = "upperTemperatureLimit";

  // time series
  String ASSET = "asset";
  String COLUMN_SCHEME = "columnScheme";
  String QUARTER_HOUR = "quarterHour";
  String TIME_SERIES = "timeSeries";
  String K_WEEKDAY = "kWd";
  String K_SATURDAY = "kSa";
  String K_SUNDAY = "kSu";
  String MY_WEEKDAY = "myWd";
  String MY_SATURDAY = "mySa";
  String MY_SUNDAY = "mySu";
  String SIGMA_WEEKDAY = "sigmaWd";
  String SIGMA_SATURDAY = "sigmaSa";
  String SIGMA_SUNDAY = "sigmaSu";
  String COORDINATE = "coordinate";
  String COORDINATE_ID = ID;
  String WEATHER_COORDINATE_ID = "coordinateId";
  String COSMO_DIFFUSE_IRRADIANCE = "diffuseIrradiance";
  String COSMO_DIRECT_IRRADIANCE = "directIrradiance";
  String COSMO_TEMPERATURE = "temperature";
  String COSMO_WIND_DIRECTION = "windDirection";
  String COSMO_WIND_VELOCITY = "windVelocity";
  String COSMO_GROUND_TEMPERATURE_LEVEL_1 = "groundTemperatureLevel1";
  String COSMO_GROUND_TEMPERATURE_LEVEL_2 = "groundTemperatureLevel2";
  String TID = "tid";
  String LONG_GEO = "longGeo";
  String LAT_GEO = "latGeo";
  String LONG_ROT = "longRot";
  String LAT_ROT = "latRot";
  String ICON_DIFFUSE_IRRADIANCE = "aswdifdS";
  String ICON_DIRECT_IRRADIANCE = "aswdirS";
  String ICON_TEMPERATURE = "t2m";
  String ICON_WIND_VELOCITY_U = "u131m";
  String ICON_WIND_VELOCITY_V = "v131m";
  String ICON_GROUND_TEMPERATURE_LEVEL_1 = "tg1";
  String ICON_GROUND_TEMPERATURE_LEVEL_2 = "tg2";
  String LONG = "longitude";
  String LAT = "latitude";
  String COORDINATE_TYPE = "coordinateType";
  String PRICE = "price";
  String HEAT_DEMAND = "heatDemand";

  // voltage
  String VOLT_LVL = "voltLvl";
  String V_ANG = "vAng";
  String V_MAG = "vMag";
  String V_RATED = "vRated";
  String V_RATED_A = "vRatedA";
  String V_RATED_B = "vRatedB";
  String V_RATED_C = "vRatedC";
  String V_TARGET = "vTarget";

  // result
  String IAANG = "iAAng";
  String IAMAG = "iAMag";
  String IBANG = "iBAng";
  String IBMAG = "iBMag";
  String ICANG = "iCAng";
  String ICMAG = "iCMag";

  String INPUT_MODEL = "inputModel";
  String ENERGY = "energy";
  String FILL_LEVEL = "fillLevel";
  String MAX = "max";
  String MIN = "min";
  String SOC = "soc";
  String SUBGRID = "subgrid";
  String TAPPOS = "tapPos";
  String TIME = "time";
  String VALUE = "value";

  // graphic
  String GRAPHIC_LAYER = "graphicLayer";
  String LINE = "line";
  String PATH_LINE_STRING = "path";
  String POINT = "point";
}
