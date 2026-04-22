/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import static edu.ie3.datamodel.io.factory.timeseries.BdewLoadProfileFactory.BDEW1999_FIELDS;
import static edu.ie3.datamodel.io.factory.timeseries.BdewLoadProfileFactory.BDEW2025_FIELDS;
import static edu.ie3.datamodel.utils.CollectionUtils.expandSet;
import static edu.ie3.datamodel.utils.CollectionUtils.newSet;

import edu.ie3.datamodel.io.naming.timeseries.TimeSeriesMetaInformation;
import edu.ie3.datamodel.io.source.TimeSeriesMappingSource;
import edu.ie3.datamodel.models.Entity;
import edu.ie3.datamodel.models.input.*;
import edu.ie3.datamodel.models.input.connector.LineInput;
import edu.ie3.datamodel.models.input.connector.SwitchInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.models.input.system.*;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.models.input.thermal.CylindricalStorageInput;
import edu.ie3.datamodel.models.input.thermal.DomesticHotWaterStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.datamodel.models.profile.markov.MarkovLoadModel;
import edu.ie3.datamodel.models.result.CongestionResult;
import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.datamodel.models.result.connector.LineResult;
import edu.ie3.datamodel.models.result.connector.SwitchResult;
import edu.ie3.datamodel.models.result.connector.Transformer2WResult;
import edu.ie3.datamodel.models.result.connector.Transformer3WResult;
import edu.ie3.datamodel.models.result.system.*;
import edu.ie3.datamodel.models.result.thermal.CylindricalStorageResult;
import edu.ie3.datamodel.models.result.thermal.DomesticHotWaterStorageResult;
import edu.ie3.datamodel.models.result.thermal.ThermalHouseResult;
import edu.ie3.datamodel.models.value.*;
import edu.ie3.datamodel.models.value.load.BdewLoadValues;
import edu.ie3.datamodel.models.value.load.RandomLoadValues;
import java.util.*;
import java.util.stream.Stream;

/**
 * Class that contains the field namings for entity classes and some values. There are some method
 * to register and retrieve mandatory, optional and unsupported fields for entity and value classes.
 * This is used for validating sources.
 */
public final class ModelFields extends FieldNamingStrategy {

  private ModelFields() {
    throw new IllegalStateException("Utility classes cannot be instantiated");
  }

  // field stores
  private static final Map<Class<? extends Entity>, Set<String>> mandatoryFields = new HashMap<>();
  private static final Map<Class<?>, Set<String>> optionalFields = new HashMap<>();
  private static final Map<Class<?>, Set<String>> unsupportedFields = new HashMap<>();
  private static final Map<Class<? extends Value>, List<Set<String>>> valueMandatoryFields =
      new HashMap<>();
  private static final Map<Class<?>, List<Set<String>>> genericMandatoryFields = new HashMap<>();

  /**
   * Retrieves a list that contains combinations of mandatory fields for the provided class.
   *
   * @param clazz to used
   * @return either a set of fields or an empty set
   */
  public static List<Set<String>> getMandatoryFields(Class<?> clazz) {
    if (Entity.class.isAssignableFrom(clazz)) {
      return List.of(mandatoryFields.getOrDefault(clazz, Collections.emptySet()));
    } else if (Value.class.isAssignableFrom(clazz)) {
      return valueMandatoryFields.getOrDefault(clazz, Collections.emptyList());
    } else {
      return genericMandatoryFields.getOrDefault(clazz, Collections.emptyList());
    }
  }

  /**
   * Retrieves the optional fields for the provided entity class.
   *
   * @param entityClass to used
   * @return either a set of fields or an empty set
   */
  public static Set<String> getOptionalFields(Class<?> entityClass) {
    return optionalFields.getOrDefault(entityClass, Collections.emptySet());
  }

  /**
   * Retrieves the unsupported fields for the provided entity class.
   *
   * @param entityClass to used
   * @return either a set of fields or an empty set
   */
  public static Set<String> getUnsupportedFields(Class<?> entityClass) {
    return unsupportedFields.getOrDefault(entityClass, Collections.emptySet());
  }

  /**
   * Method to return all fields as a set.
   *
   * @param entityClass to use
   * @return a flattened set of all fields
   */
  public static Set<String> getAllFields(Class<?> entityClass) {
    Set<String> allFields = new HashSet<>();
    allFields.addAll(mandatoryFields.getOrDefault(entityClass, Collections.emptySet()));
    allFields.addAll(optionalFields.getOrDefault(entityClass, Collections.emptySet()));
    return allFields;
  }

  /**
   * Method to register mandatory and optional fields for a given entity class.
   *
   * <p>NOTE: This method will only add fields, if no fields are registered yet!
   *
   * @param entityClass for which fields should be registered
   * @param mandatoryFields the mandatory fields to register
   * @param optionalFields the optional fields to register
   */
  public static void register(
      Class<? extends Entity> entityClass,
      Set<String> mandatoryFields,
      Set<String> optionalFields) {
    ModelFields.mandatoryFields.putIfAbsent(entityClass, mandatoryFields);
    ModelFields.optionalFields.putIfAbsent(entityClass, optionalFields);
  }

  /**
   * Method to register mandatory fields for a given entity class.
   *
   * <p>NOTE: This method will only add fields, if no fields are registered yet!
   *
   * @param entityClass for which fields should be registered
   * @param mandatoryFields the mandatory fields to register
   */
  public static void register(Class<? extends Entity> entityClass, Set<String> mandatoryFields) {
    ModelFields.mandatoryFields.putIfAbsent(entityClass, mandatoryFields);
  }

  @SafeVarargs
  public static void registerValue(
      Class<? extends Value> entityClass, Set<String>... mandatoryFields) {
    ModelFields.valueMandatoryFields.putIfAbsent(entityClass, List.of(mandatoryFields));
  }

  /**
   * Method to register mandatory fields for a class that is neither an {@link Entity} nor a {@link
   * Value}.
   *
   * <p>NOTE: This method will only add fields, if no fields are registered yet!
   *
   * @param clazz for which fields should be registered
   * @param mandatoryFields the mandatory field combinations to register
   */
  @SafeVarargs
  public static void registerGeneric(Class<?> clazz, Set<String>... mandatoryFields) {
    ModelFields.genericMandatoryFields.putIfAbsent(clazz, List.of(mandatoryFields));
  }

  /**
   * Method to register mandatory fields for a given entity class.
   *
   * <p>NOTE: This method will only add fields, if no fields are registered yet!
   *
   * @param entityClass for which fields should be registered
   * @param baseSet the base mandatory fields to register
   * @param additionalFields additional fields to register
   */
  private static void registerMandatory(
      Class<? extends Entity> entityClass, Set<String> baseSet, String... additionalFields) {
    register(entityClass, expandSet(baseSet, additionalFields));
  }

  /**
   * Method to register optional fields for a given entity class.
   *
   * <p>NOTE: This method will only add fields, if no fields are registered yet!
   *
   * @param entityClass for which fields should be registered
   * @param optionalFields the optional fields to register
   */
  public static void registerOptional(Class<?> entityClass, Set<String> optionalFields) {
    ModelFields.optionalFields.putIfAbsent(entityClass, optionalFields);
  }

  /**
   * Method to register unsupported fields for a given entity class.
   *
   * <p>NOTE: This method will only add fields, if no fields are registered yet!
   *
   * @param entityClass for which fields should be registered
   * @param unsupportedFields the unsupported fields to register
   */
  public static void registerUnsupported(Class<?> entityClass, Set<String> unsupportedFields) {
    ModelFields.unsupportedFields.putIfAbsent(entityClass, unsupportedFields);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
  // code for registering the fields for known assets

  // basic asset fields
  private static final Set<String> assetFields = newSet(UUID, ID);
  private static final Set<String> assetOptionalFields =
      newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL);

  static {
    // registering all known fields
    registerTypeFields();
    registerGridAssetFields();
    registerParticipantFields();
    registerThermalFields();
    registerGraphicFields();
    registerResultFields();
    registerTimeSeriesRelatedFields();
    registerValueFields();
    registerWeatherValueFields();

    // registering em fields
    registerMandatory(EmInput.class, assetFields, CONTROLLING_EM, CONTROL_STRATEGY);
    registerOptional(EmInput.class, assetOptionalFields);

    // markov load model fields
    registerGeneric(
        MarkovLoadModel.class,
        newSet(
            MARKOV_SCHEMA,
            MARKOV_GENERATED_AT,
            MARKOV_GENERATOR,
            MARKOV_TIME_MODEL,
            MARKOV_VALUE_MODEL,
            MARKOV_PARAMETERS,
            MARKOV_DATA,
            MARKOV_GENERATOR_NAME,
            MARKOV_GENERATOR_VERSION,
            MARKOV_BUCKET_COUNT,
            MARKOV_SAMPLING_INTERVAL,
            MARKOV_TIMEZONE,
            MARKOV_DISCRETIZATION_STATES,
            MARKOV_DISCRETIZATION_THRESHOLDS,
            MARKOV_MAX_POWER_VALUE,
            MARKOV_MAX_POWER_UNIT,
            MARKOV_MIN_POWER_VALUE,
            MARKOV_MIN_POWER_UNIT,
            MARKOV_TRANSITION_VALUES,
            MARKOV_GMM_BUCKETS));
  }

  /**
   * Method for expanding the mandatory fields for entities.
   *
   * <p>This overrides the mandatory fields and should only be accessible from this place in order
   * to prevent errors.
   *
   * @param entityClass for which fields should be registered
   * @param additionalFields new fields that should be added
   */
  private static void addMandatory(
      Class<? extends Entity> entityClass, String... additionalFields) {
    Set<String> oldSet =
        ModelFields.mandatoryFields.getOrDefault(entityClass, Collections.emptySet());
    ModelFields.mandatoryFields.put(entityClass, expandSet(oldSet, additionalFields));
  }

  /** Method for registering all asset type fields. */
  private static void registerTypeFields() {
    register(OperatorInput.class, assetFields);

    registerMandatory(LineTypeInput.class, assetFields, B, G, R, X, I_MAX, V_RATED);

    Stream.of(Transformer2WTypeInput.class, Transformer3WTypeInput.class)
        .forEach(
            c ->
                registerMandatory(
                    c,
                    assetFields,
                    V_RATED_A,
                    V_RATED_B,
                    G_M,
                    B_M,
                    D_V,
                    D_PHI,
                    TAP_NEUTR,
                    TAP_MIN,
                    TAP_MAX));

    addMandatory(Transformer2WTypeInput.class, R_SC, X_SC, S_RATED, TAP_SIDE);

    addMandatory(
        Transformer3WTypeInput.class,
        S_RATED_A,
        S_RATED_B,
        S_RATED_C,
        V_RATED_C,
        R_SC_A,
        R_SC_B,
        R_SC_C,
        X_SC_A,
        X_SC_B,
        X_SC_C);

    Stream.of(
            BmTypeInput.class,
            ChpTypeInput.class,
            EvTypeInput.class,
            AcTypeInput.class,
            HpTypeInput.class,
            StorageTypeInput.class,
            WecTypeInput.class)
        .forEach(c -> registerMandatory(c, assetFields, CAP_EX, OP_EX, S_RATED, COS_PHI_RATED));

    addMandatory(BmTypeInput.class, ACTIVE_POWER_GRADIENT, ETA_CONV);

    addMandatory(ChpTypeInput.class, ETA_EL, ETA_THERMAL, P_THERMAL, P_OWN);

    addMandatory(EvTypeInput.class, E_STORAGE, E_CONS, S_RATED_DC);

    Stream.of(AcTypeInput.class, HpTypeInput.class).forEach(c -> addMandatory(c, P_THERMAL));

    addMandatory(StorageTypeInput.class, E_STORAGE, P_MAX, ACTIVE_POWER_GRADIENT, ETA);

    addMandatory(WecTypeInput.class, CP_CHARACTERISTIC, ETA_CONV, ROTOR_AREA, HUB_HEIGHT);
  }

  /** Method for registering all grid asset fields. */
  private static void registerGridAssetFields() {

    addMandatory(NodeInput.class, V_TARGET, V_RATED, SLACK, GEO_POSITION, VOLT_LVL, SUBNET);

    addMandatory(SwitchInput.class, NODE_A, NODE_B, CLOSED);

    addMandatory(LineInput.class, LENGTH, GEO_POSITION, OLM_CHARACTERISTIC);

    Stream.of(LineInput.class, Transformer2WInput.class, Transformer3WInput.class)
        .forEach(c -> addMandatory(c, NODE_A, NODE_B, PARALLEL_DEVICES, TYPE));

    addMandatory(Transformer2WInput.class, AUTO_TAP, TAP_POS);
    addMandatory(Transformer3WInput.class, AUTO_TAP, TAP_POS, NODE_C);

    addMandatory(MeasurementUnitInput.class, NODE, V_MAG, V_ANG, P, Q);

    // adding unsupported fields
    ModelFields.unsupportedFields.put(SwitchInput.class, newSet(PARALLEL_DEVICES));
  }

  /** Method for registering all participant fields. */
  private static void registerParticipantFields() {
    Stream.of(
            AcInput.class,
            HpInput.class,
            BmInput.class,
            ChpInput.class,
            EvcsInput.class,
            EvInput.class,
            FixedFeedInInput.class,
            LoadInput.class,
            PvInput.class,
            StorageInput.class,
            WecInput.class)
        .forEach(
            c -> {
              registerMandatory(c, assetFields, NODE, Q_CHARACTERISTICS, CONTROLLING_EM);
              registerOptional(c, assetOptionalFields);
            });

    Stream.of(AcInput.class, HpInput.class).forEach(c -> addMandatory(c, TYPE, THERMAL_BUS));

    addMandatory(BmInput.class, TYPE, COST_CONTROLLED, FEED_IN_TARIFF);

    addMandatory(ChpInput.class, TYPE, THERMAL_BUS, THERMAL_STORAGE);

    addMandatory(EvcsInput.class, TYPE, CHARGING_POINTS, COS_PHI_RATED, LOCATION_TYPE, V2G_SUPPORT);

    addMandatory(EvInput.class, TYPE, Q_CHARACTERISTICS);

    addMandatory(FixedFeedInInput.class, S_RATED, COS_PHI_RATED);

    addMandatory(LoadInput.class, LOAD_PROFILE, E_CONS_ANNUAL, S_RATED, COS_PHI_RATED);

    addMandatory(
        PvInput.class, ALBEDO, AZIMUTH, ETA_CONV, ELEVATION_ANGLE, KG, KT, S_RATED, COS_PHI_RATED);

    Stream.of(StorageInput.class, WecInput.class).forEach(c -> addMandatory(c, TYPE));
  }

  /** Method for registering all thermal fields. */
  private static void registerThermalFields() {
    Stream.of(
            ThermalBusInput.class,
            ThermalHouseInput.class,
            CylindricalStorageInput.class,
            DomesticHotWaterStorageInput.class)
        .forEach(
            c -> {
              register(c, assetFields);
              registerOptional(c, assetOptionalFields);
            });
    addMandatory(
        ThermalHouseInput.class,
        ETH_LOSSES,
        ETH_CAPA,
        TARGET_TEMPERATURE,
        UPPER_TEMPERATURE_LIMIT,
        LOWER_TEMPERATURE_LIMIT,
        HOUSING_TYPE,
        NUMBER_INHABITANTS,
        THERMAL_BUS);

    Stream.of(CylindricalStorageInput.class, DomesticHotWaterStorageInput.class)
        .forEach(
            c ->
                addMandatory(
                    c, STORAGE_VOLUME_LVL, INLET_TEMP, RETURN_TEMP, C, P_THERMAL_MAX, THERMAL_BUS));
  }

  /** Method for registering all graphic fields. */
  private static void registerGraphicFields() {
    Set<String> graphicBase = newSet(UUID, GRAPHIC_LAYER, PATH_LINE_STRING);
    registerMandatory(NodeGraphicInput.class, graphicBase, POINT, NODE);
    registerMandatory(LineGraphicInput.class, graphicBase, LINE);
  }

  /** Method for registering all result fields. */
  private static void registerResultFields() {
    Set<String> result = newSet(TIME, INPUT_MODEL);

    // grid results
    Set<String> connectorResultBase = expandSet(result, IAMAG, IAANG, IBMAG, IBANG);

    registerMandatory(CongestionResult.class, result, TYPE, VALUE, SUBGRID, MIN, MAX);

    register(LineResult.class, connectorResultBase);

    registerMandatory(Transformer2WResult.class, connectorResultBase, TAPPOS);

    registerMandatory(Transformer3WResult.class, connectorResultBase, ICMAG, ICANG, TAPPOS);

    registerMandatory(PowerLimitFlexOptionsResult.class, result, P_REF, P_MIN, P_MAX);

    registerMandatory(EnergyBoundariesFlexOptionsResult.class, result, E_MIN, E_MAX, P_MIN, P_MAX);

    registerMandatory(NodeResult.class, result, V_MAG, V_ANG);

    registerMandatory(SwitchResult.class, result, CLOSED);

    // participant results
    Set<String> participant = expandSet(result, POWER, REACTIVE_POWER);

    Stream.of(EvResult.class, StorageResult.class)
        .forEach(r -> registerMandatory(r, participant, SOC));

    Stream.of(ChpResult.class, HpResult.class)
        .forEach(r -> registerMandatory(r, participant, Q_DOT));

    Stream.of(
            BmResult.class,
            EmResult.class,
            EvcsResult.class,
            FixedFeedInResult.class,
            LoadResult.class,
            PvResult.class,
            WecResult.class)
        .forEach(r -> register(r, participant));

    // thermal
    Set<String> thermal = expandSet(result, Q_DOT);

    registerMandatory(ThermalHouseResult.class, thermal, INDOOR_TEMPERATURE);

    Stream.of(CylindricalStorageResult.class, DomesticHotWaterStorageResult.class)
        .forEach(r -> registerMandatory(r, thermal, ENERGY, FILL_LEVEL));
  }

  /** Method for registering some time series related fields. */
  private static void registerTimeSeriesRelatedFields() {
    register(TimeSeriesMetaInformation.class, newSet(COLUMN_SCHEME, TIME_SERIES));
    register(TimeSeriesMappingSource.MappingEntry.class, newSet(ASSET, TIME_SERIES));

    register(
        IdCoordinateInput.CosmoIdCoordinateInput.class,
        newSet(TID, COORDINATE_ID, LONG_GEO, LAT_GEO, LONG_ROT, LAT_ROT));
    register(
        IdCoordinateInput.IconIdCoordinateInput.class,
        newSet(COORDINATE_ID, LAT, LONG, COORDINATE_TYPE));
    register(IdCoordinateInput.SqlIdCoordinateInput.class, newSet(COORDINATE_ID, COORDINATE));
  }

  /** Method for registering most of the value fields. */
  private static void registerValueFields() {
    // load values
    registerValue(
        BdewLoadValues.class,
        expandSet(BDEW1999_FIELDS.values(), QUARTER_HOUR),
        expandSet(BDEW2025_FIELDS.values(), QUARTER_HOUR));
    registerValue(
        RandomLoadValues.class,
        newSet(
            K_WEEKDAY,
            K_SATURDAY,
            K_SUNDAY,
            MY_WEEKDAY,
            MY_SATURDAY,
            MY_SUNDAY,
            SIGMA_WEEKDAY,
            SIGMA_SATURDAY,
            SIGMA_SUNDAY,
            QUARTER_HOUR));

    // time base value
    Set<String> timeBase = newSet(TIME);

    registerValue(EnergyPriceValue.class, expandSet(timeBase, PRICE));
    registerValue(
        HeatAndSValue.class, expandSet(timeBase, ACTIVE_POWER, REACTIVE_POWER, HEAT_DEMAND));
    registerValue(HeatAndPValue.class, expandSet(timeBase, ACTIVE_POWER, HEAT_DEMAND));
    registerValue(HeatDemandValue.class, expandSet(timeBase, HEAT_DEMAND));
    registerValue(SValue.class, expandSet(timeBase, ACTIVE_POWER, REACTIVE_POWER));
    registerValue(PValue.class, expandSet(timeBase, ACTIVE_POWER));
    registerValue(VoltageValue.class, expandSet(timeBase, V_ANG, V_MAG));
  }

  /** Method for registering all weather fields. */
  private static void registerWeatherValueFields() {
    // cosmo weather values
    Set<String> cosmoMinConstructorParams =
        newSet(
            WEATHER_COORDINATE_ID,
            COSMO_DIFFUSE_IRRADIANCE,
            COSMO_DIRECT_IRRADIANCE,
            COSMO_TEMPERATURE,
            COSMO_WIND_DIRECTION,
            COSMO_WIND_VELOCITY);

    registerValue(
        WeatherValue.CosmoWeatherValue.class,
        cosmoMinConstructorParams,
        expandSet(
            cosmoMinConstructorParams,
            COSMO_GROUND_TEMPERATURE_LEVEL_1,
            COSMO_GROUND_TEMPERATURE_LEVEL_2));

    // icon weather value

    Set<String> iconMinParameters =
        newSet(
            ICON_DIFFUSE_IRRADIANCE,
            ICON_DIRECT_IRRADIANCE,
            ICON_TEMPERATURE,
            ICON_WIND_VELOCITY_U,
            ICON_WIND_VELOCITY_V);

    registerValue(
        WeatherValue.CosmoWeatherValue.class,
        iconMinParameters,
        expandSet(
            iconMinParameters,
            "albrad",
            "asobs",
            "aswdifuS",
            "tg1",
            "tg2",
            "u10m",
            "u20m",
            "u216m",
            "u65m",
            "v10m",
            "v20m",
            "v216m",
            "v65m",
            "w131m",
            "w20m",
            "w216m",
            "w65m",
            "z0",
            "p131m",
            "p20m",
            "p65m",
            "sobsrad",
            "t131m"));
  }
}
