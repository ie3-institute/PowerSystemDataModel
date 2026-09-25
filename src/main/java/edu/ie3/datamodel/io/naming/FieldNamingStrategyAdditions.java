/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

public class FieldNamingStrategyAdditions {

  public static final String POWER = "p";
  public static final String ACTIVE_POWER = "p";
  public static final String REACTIVE_POWER = "q";
  public static final String HEAT_DEMAND = "heatDemand";

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
  public static final String COORDINATE_ID = "id";
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

  // markov - top-level
  public static final String MARKOV_SCHEMA = "schema";
  public static final String MARKOV_GENERATED_AT = "generatedAt";
  public static final String MARKOV_GENERATOR = "generator";
  public static final String MARKOV_TIME_MODEL = "timeModel";
  public static final String MARKOV_VALUE_MODEL = "valueModel";
  public static final String MARKOV_PARAMETERS = "parameters";
  public static final String MARKOV_DATA = "data";

  // markov - nested fields required for simulation
  public static final String MARKOV_GENERATOR_NAME = "generator.name";
  public static final String MARKOV_GENERATOR_VERSION = "generator.version";
  public static final String MARKOV_GENERATOR_CONFIG = "generator.config";
  public static final String MARKOV_BUCKET_COUNT = "timeModel.bucketCount";
  public static final String MARKOV_BUCKET_ENCODING = "timeModel.bucketEncoding";
  public static final String MARKOV_BUCKET_ENCODING_FORMULA = "timeModel.bucketEncoding.formula";
  public static final String MARKOV_SAMPLING_INTERVAL = "timeModel.samplingIntervalMinutes";
  public static final String MARKOV_TIMEZONE = "timeModel.timezone";
  public static final String MARKOV_VALUE_UNIT = "valueModel.valueUnit";
  public static final String MARKOV_NORMALIZATION = "valueModel.normalization";
  public static final String MARKOV_NORMALIZATION_METHOD = "valueModel.normalization.method";
  public static final String MARKOV_MAX_POWER = "valueModel.normalization.maxPower";
  public static final String MARKOV_DISCRETIZATION = "valueModel.discretization";
  public static final String MARKOV_DISCRETIZATION_STATES = "valueModel.discretization.states";
  public static final String MARKOV_DISCRETIZATION_THRESHOLDS =
      "valueModel.discretization.thresholdsRight";
  public static final String MARKOV_MAX_POWER_VALUE = "valueModel.normalization.maxPower.value";
  public static final String MARKOV_MAX_POWER_UNIT = "valueModel.normalization.maxPower.unit";
  public static final String MARKOV_MIN_POWER = "valueModel.normalization.minPower";
  public static final String MARKOV_MIN_POWER_VALUE = "valueModel.normalization.minPower.value";
  public static final String MARKOV_MIN_POWER_UNIT = "valueModel.normalization.minPower.unit";
  public static final String MARKOV_PARAMETERS_TRANSITIONS = "parameters.transitions";
  public static final String MARKOV_EMPTY_ROW_STRATEGY = "parameters.transitions.emptyRowStrategy";
  public static final String MARKOV_PARAMETERS_GMM = "parameters.gmm";
  public static final String MARKOV_GMM_VALUE_COLUMN = "parameters.gmm.valueCol";
  public static final String MARKOV_GMM_VERBOSE = "parameters.gmm.verbose";
  public static final String MARKOV_GMM_HEARTBEAT_SECONDS = "parameters.gmm.heartbeatSeconds";
  public static final String MARKOV_TRANSITIONS = "data.transitions";
  public static final String MARKOV_TRANSITION_DTYPE = "data.transitions.dtype";
  public static final String MARKOV_TRANSITION_ENCODING = "data.transitions.encoding";
  public static final String MARKOV_TRANSITION_SHAPE = "data.transitions.shape";
  public static final String MARKOV_TRANSITION_VALUES = "data.transitions.values";
  public static final String MARKOV_GMMS = "data.gmms";
  public static final String MARKOV_GMM_BUCKETS = "data.gmms.buckets";
  public static final String MARKOV_GMM_STATES = "data.gmms.buckets.states";
  public static final String MARKOV_GMM_WEIGHTS = "data.gmms.buckets.states.weights";
  public static final String MARKOV_GMM_MEANS = "data.gmms.buckets.states.means";
  public static final String MARKOV_GMM_VARIANCES = "data.gmms.buckets.states.variances";
}
