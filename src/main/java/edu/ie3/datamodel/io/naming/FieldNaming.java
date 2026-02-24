/*
 * © 2026. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.naming;

import static edu.ie3.datamodel.utils.CollectionUtils.expandSet;
import static edu.ie3.datamodel.utils.CollectionUtils.newSet;

import edu.ie3.datamodel.io.factory.input.SwitchInputFactory;
import edu.ie3.datamodel.models.input.EmInput;
import edu.ie3.datamodel.models.input.MeasurementUnitInput;
import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
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
import java.util.*;
import java.util.stream.Stream;

/** Class that contains the field namings for entity classes and some values. */
public final class FieldNaming implements FieldNames {

  // field stores
  private static final Map<Class<?>, List<Set<String>>> mandatoryFields = new HashMap<>();
  private static final Map<Class<?>, Set<String>> optionalFields = new HashMap<>();
  private static final Map<Class<?>, Set<String>> unsupportedFields = new HashMap<>();

  /**
   * Retrieves a list that contains combinations of mandatory fields for the provided class.
   *
   * @param entityClass to used
   * @return either a set of fields or an empty set
   */
  public static List<Set<String>> getMandatoryFields(Class<?> entityClass) {
    return mandatoryFields.getOrDefault(entityClass, Collections.emptyList());
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
   * Method to register mandatory and optional fields for a given entity class.
   *
   * <p>NOTE: This method will only add fields, if no fields are registered yet!
   *
   * @param entityClass for which fields should be registered
   * @param mandatoryFields the mandatory fields to register
   * @param optionalFields the optional fields to register
   */
  public static void register(
      Class<?> entityClass, Set<String> mandatoryFields, Set<String> optionalFields) {
    FieldNaming.mandatoryFields.putIfAbsent(entityClass, List.of(mandatoryFields));
    FieldNaming.optionalFields.putIfAbsent(entityClass, optionalFields);
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
      Class<?> entityClass, List<Set<String>> mandatoryFields, Set<String> optionalFields) {
    FieldNaming.mandatoryFields.putIfAbsent(entityClass, mandatoryFields);
    FieldNaming.optionalFields.putIfAbsent(entityClass, optionalFields);
  }

  /**
   * Method to register mandatory fields for a given entity class.
   *
   * <p>NOTE: This method will only add fields, if no fields are registered yet!
   *
   * @param entityClass for which fields should be registered
   * @param mandatoryFields the mandatory fields to register
   */
  public static void register(Class<?> entityClass, Set<String> mandatoryFields) {
    FieldNaming.mandatoryFields.putIfAbsent(entityClass, List.of(mandatoryFields));
  }

  /**
   * Method to register mandatory fields for a given entity class.
   *
   * <p>NOTE: This method will only add fields, if no fields are registered yet!
   *
   * @param entityClass for which fields should be registered
   * @param mandatoryFields the mandatory fields to register
   */
  public static void register(Class<?> entityClass, List<Set<String>> mandatoryFields) {
    FieldNaming.mandatoryFields.putIfAbsent(entityClass, mandatoryFields);
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
    FieldNaming.unsupportedFields.putIfAbsent(entityClass, unsupportedFields);
  }

  // input entities
  static {
    // basic sets
    Set<String> uniqueEntity = newSet(UUID);
    Set<String> asset = expandSet(uniqueEntity, ID);

    Set<String> assetOptional = newSet(OPERATOR, OPERATES_FROM, OPERATES_UNTIL);

    register(OperatorInput.class, asset);

    // grid assets
    Set<String> connectorBase = expandSet(asset, NODE_A, NODE_B);
    Set<String> connector = expandSet(connectorBase, PARALLEL_DEVICES);
    Set<String> transformer2W = expandSet(connector, TAP_POS, AUTO_TAP);

    register(
        NodeInput.class,
        expandSet(asset, V_TARGET, V_RATED, SLACK, GEO_POSITION, VOLT_LVL, SUBNET),
        assetOptional);

    register(
        LineInput.class,
        expandSet(connector, LENGTH, GEO_POSITION, OLM_CHARACTERISTIC, TYPE),
        assetOptional);

    register(SwitchInput.class, expandSet(connectorBase, SwitchInputFactory.CLOSED), assetOptional);

    registerUnsupported(SwitchInput.class, newSet(PARALLEL_DEVICES));

    register(Transformer2WInput.class, transformer2W, assetOptional);

    register(Transformer3WInput.class, expandSet(transformer2W, NODE_C), assetOptional);

    register(MeasurementUnitInput.class, expandSet(asset, NODE, V_MAG, V_ANG, P, Q), assetOptional);

    // participant
    Set<String> participantBase = expandSet(asset, NODE, Q_CHARACTERISTICS, CONTROLLING_EM);

    Stream.of(AcInput.class, HpInput.class)
        .forEach(c -> register(c, expandSet(participantBase, TYPE, THERMAL_BUS), assetOptional));

    register(
        BmInput.class,
        expandSet(participantBase, TYPE, COST_CONTROLLED, FEED_IN_TARIFF),
        assetOptional);

    register(
        ChpInput.class,
        expandSet(participantBase, THERMAL_BUS, TYPE, THERMAL_STORAGE),
        assetOptional);

    register(
        EvcsInput.class,
        expandSet(
            participantBase, TYPE, CHARGING_POINTS, COS_PHI_RATED, LOCATION_TYPE, V2G_SUPPORT),
        assetOptional);

    register(EvInput.class, expandSet(asset, Q_CHARACTERISTICS, TYPE), assetOptional);

    register(
        FixedFeedInInput.class, expandSet(participantBase, S_RATED, COS_PHI_RATED), assetOptional);

    register(
        LoadInput.class,
        expandSet(participantBase, LOAD_PROFILE, E_CONS_ANNUAL, S_RATED, COS_PHI_RATED),
        assetOptional);

    register(
        PvInput.class,
        expandSet(
            participantBase,
            ALBEDO,
            AZIMUTH,
            ETA_CONV,
            ELEVATION_ANGLE,
            KG,
            KT,
            S_RATED,
            COS_PHI_RATED),
        assetOptional);

    Stream.of(EvcsInput.class, StorageInput.class, WecInput.class)
        .forEach(c -> register(c, expandSet(participantBase, TYPE), assetOptional));

    // em
    register(EmInput.class, expandSet(asset, CONTROLLING_EM, CONTROL_STRATEGY), assetOptional);

    // thermal
    Set<String> thermalStorageBase =
        expandSet(
            asset, STORAGE_VOLUME_LVL, INLET_TEMP, RETURN_TEMP, C, P_THERMAL_MAX, THERMAL_BUS);

    register(ThermalBusInput.class, asset, assetOptional);

    register(
        ThermalHouseInput.class,
        expandSet(
            asset,
            ETH_LOSSES,
            ETH_CAPA,
            TARGET_TEMPERATURE,
            UPPER_TEMPERATURE_LIMIT,
            LOWER_TEMPERATURE_LIMIT,
            HOUSING_TYPE,
            NUMBER_INHABITANTS,
            THERMAL_BUS),
        assetOptional);

    Stream.of(CylindricalStorageInput.class, DomesticHotWaterStorageInput.class)
        .forEach(c -> register(c, thermalStorageBase, assetOptional));

    // graphic
    Set<String> graphicBase = expandSet(uniqueEntity, GRAPHIC_LAYER, PATH_LINE_STRING);

    register(NodeGraphicInput.class, expandSet(graphicBase, POINT, NODE));

    register(LineGraphicInput.class, expandSet(graphicBase, LINE));
  }

  // type input entities
  static {
    Set<String> assetType = newSet(UUID, ID);

    // grid assets
    Set<String> transformerBase =
        newSet(V_RATED_A, V_RATED_B, G_M, B_M, D_V, D_PHI, TAP_NEUTR, TAP_MIN, TAP_MAX);

    register(LineTypeInput.class, expandSet(assetType, B, G, R, X, I_MAX, V_RATED));

    register(
        Transformer2WTypeInput.class, expandSet(transformerBase, R_SC, X_SC, S_RATED, TAP_SIDE));

    register(
        Transformer3WTypeInput.class,
        expandSet(
            transformerBase,
            S_RATED_A,
            S_RATED_B,
            S_RATED_C,
            V_RATED_C,
            R_SC_A,
            R_SC_B,
            R_SC_C,
            X_SC_A,
            X_SC_B,
            X_SC_C));

    // participants
    Set<String> participantBase = expandSet(assetType, CAP_EX, OP_EX, S_RATED, COS_PHI_RATED);

    register(AcTypeInput.class, expandSet(participantBase, THERMAL_BUS, TYPE));

    register(BmTypeInput.class, expandSet(participantBase, ACTIVE_POWER_GRADIENT, ETA_CONV));

    register(ChpTypeInput.class, expandSet(participantBase, ETA_EL, ETA_THERMAL, P_THERMAL, P_OWN));

    register(EvTypeInput.class, expandSet(participantBase, E_STORAGE, E_CONS, S_RATED_DC));

    register(HpTypeInput.class, expandSet(participantBase, P_THERMAL));

    register(
        StorageTypeInput.class,
        expandSet(participantBase, E_STORAGE, P_MAX, ACTIVE_POWER_GRADIENT, ETA));

    register(
        WecTypeInput.class,
        expandSet(participantBase, CP_CHARACTERISTIC, ETA_CONV, ROTOR_AREA, HUB_HEIGHT));
  }

  // result entities
  static {
    Set<String> result = newSet(TIME, INPUT_MODEL);

    Set<String> connectorResultBase = expandSet(result, IAMAG, IAANG, IBMAG, IBANG);

    register(CongestionResult.class, expandSet(result, TYPE, VALUE, SUBGRID, MIN, MAX));

    register(LineResult.class, connectorResultBase);

    register(Transformer2WResult.class, expandSet(connectorResultBase, TAPPOS));

    register(Transformer3WResult.class, expandSet(connectorResultBase, ICMAG, ICANG, TAPPOS));

    register(FlexOptionsResult.class, expandSet(result, P_REF, P_MIN, P_MAX));

    register(NodeResult.class, expandSet(result, V_MAG, V_ANG));

    register(SwitchResult.class, expandSet(result, CLOSED));

    // participant results
    Set<String> participant = expandSet(result, POWER, REACTIVE_POWER);

    Stream.of(EvResult.class, StorageResult.class)
        .forEach(r -> register(r, expandSet(participant, SOC)));

    Stream.of(ChpResult.class, HpResult.class)
        .forEach(r -> register(r, expandSet(participant, Q_DOT)));

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

    register(ThermalHouseResult.class, expandSet(thermal, INDOOR_TEMPERATURE));

    Stream.of(CylindricalStorageResult.class, DomesticHotWaterStorageResult.class)
        .forEach(r -> register(r, expandSet(thermal, ENERGY, FILL_LEVEL)));
  }
}
