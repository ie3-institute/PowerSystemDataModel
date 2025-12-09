/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import edu.ie3.datamodel.exceptions.*;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.input.AssetTypeInput;
import edu.ie3.datamodel.models.input.OperatorInput;
import edu.ie3.datamodel.models.input.connector.type.LineTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer2WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.Transformer3WTypeInput;
import edu.ie3.datamodel.models.input.connector.type.TransformerTypeInput;
import edu.ie3.datamodel.models.input.system.characteristic.WecCharacteristicInput;
import edu.ie3.datamodel.models.input.system.type.*;
import edu.ie3.datamodel.utils.Try;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Interface that provides the capability to build entities of type {@link
 * SystemParticipantTypeInput} and {@link OperatorInput} from different data sources e.g. .csv files
 * or databases
 *
 * @version 0.1
 * @since 08.04.20
 */
public class TypeSource extends EntitySource {
  protected final DataSource dataSource;

  public TypeSource(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void validate() throws ValidationException {
    List<Try<Void, ValidationException>> result = new ArrayList<>();

    Stream.of(
            validate(EvTypeInput.class, dataSource, new SourceValidator<>(EvTypeInput.getFields())),
            validate(HpTypeInput.class, dataSource, new SourceValidator<>(HpTypeInput.getFields())),
            validate(BmTypeInput.class, dataSource, new SourceValidator<>(BmTypeInput.getFields())),
            validate(
                WecTypeInput.class, dataSource, new SourceValidator<>(WecTypeInput.getFields())),
            validate(
                ChpTypeInput.class, dataSource, new SourceValidator<>(ChpTypeInput.getFields())),
            validate(
                StorageTypeInput.class,
                dataSource,
                new SourceValidator<>(StorageTypeInput.getFields())),
            validate(
                OperatorInput.class, dataSource, new SourceValidator<>(OperatorInput.getFields())),
            validate(
                LineTypeInput.class, dataSource, new SourceValidator<>(LineTypeInput.getFields())),
            validate(
                Transformer2WTypeInput.class,
                dataSource,
                new SourceValidator<>(Transformer2WTypeInput.getFields())),
            validate(
                Transformer3WTypeInput.class,
                dataSource,
                new SourceValidator<>(Transformer3WTypeInput.getFields())))
        .forEach(result::add);

    Try.scanCollection(result, Void.class, FailedValidationException::new).getOrThrow();
  }

  /**
   * Returns a set of {@link Transformer2WTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link Transformer2WTypeInput} which has to be checked
   * manually, as {@link Transformer2WTypeInput#equals(Object)} is NOT restricted on the uuid of
   * {@link Transformer2WTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link Transformer2WTypeInput} entities
   */
  public Map<UUID, Transformer2WTypeInput> getTransformer2WTypes() throws SourceException {
    return getEntities(Transformer2WTypeInput.class, dataSource, transformer2WTypeBuildFunction)
        .collect(toMap());
  }

  /**
   * Returns a set of {@link OperatorInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link OperatorInput} which has to be checked manually, as
   * {@link OperatorInput#equals(Object)} is NOT restricted on the uuid of {@link OperatorInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link OperatorInput} entities
   */
  public Map<UUID, OperatorInput> getOperators() throws SourceException {
    return getEntities(OperatorInput.class, dataSource, operatorBuildFunction).collect(toMap());
  }

  /**
   * Returns a set of {@link LineTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link LineTypeInput} which has to be checked manually, as
   * {@link LineTypeInput#equals(Object)} is NOT restricted on the uuid of {@link LineTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link LineTypeInput} entities
   */
  public Map<UUID, LineTypeInput> getLineTypes() throws SourceException {
    return getEntities(LineTypeInput.class, dataSource, lineTypeBuildFunction).collect(toMap());
  }

  /**
   * Returns a set of {@link Transformer3WTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link Transformer3WTypeInput} which has to be checked
   * manually, as {@link Transformer3WTypeInput#equals(Object)} is NOT restricted on the uuid of
   * {@link Transformer3WTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link Transformer3WTypeInput} entities
   */
  public Map<UUID, Transformer3WTypeInput> getTransformer3WTypes() throws SourceException {
    return getEntities(Transformer3WTypeInput.class, dataSource, transformer3WTypeBuildFunction)
        .collect(toMap());
  }

  /**
   * Returns a set of {@link BmTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link BmTypeInput} which has to be checked manually, as
   * {@link BmTypeInput#equals(Object)} is NOT restricted on the uuid of {@link BmTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link BmTypeInput} entities
   */
  public Map<UUID, BmTypeInput> getBmTypes() throws SourceException {
    return getEntities(BmTypeInput.class, dataSource, bmTypeBuildFunction).collect(toMap());
  }

  /**
   * Returns a set of {@link ChpTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link ChpTypeInput} which has to be checked manually, as
   * {@link ChpTypeInput#equals(Object)} is NOT restricted on the uuid of {@link ChpTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link ChpTypeInput} entities
   */
  public Map<UUID, ChpTypeInput> getChpTypes() throws SourceException {
    return getEntities(ChpTypeInput.class, dataSource, chpTypeBuildFunction).collect(toMap());
  }

  /**
   * Returns a set of {@link HpTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link HpTypeInput} which has to be checked manually, as
   * {@link HpTypeInput#equals(Object)} is NOT restricted on the uuid of {@link HpTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link HpTypeInput} entities
   */
  public Map<UUID, HpTypeInput> getHpTypes() throws SourceException {
    return getEntities(HpTypeInput.class, dataSource, hpTypeBuildFunction).collect(toMap());
  }

  /**
   * Returns a set of {@link StorageTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link StorageTypeInput} which has to be checked manually, as
   * {@link StorageTypeInput#equals(Object)} is NOT restricted on the uuid of {@link
   * StorageTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link StorageTypeInput} entities
   */
  public Map<UUID, StorageTypeInput> getStorageTypes() throws SourceException {
    return getEntities(StorageTypeInput.class, dataSource, storageBuildFunction).collect(toMap());
  }

  /**
   * Returns a set of {@link WecTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link WecTypeInput} which has to be checked manually, as
   * {@link WecTypeInput#equals(Object)} is NOT restricted on the uuid of {@link WecTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link WecTypeInput} entities
   */
  public Map<UUID, WecTypeInput> getWecTypes() throws SourceException {
    return getEntities(WecTypeInput.class, dataSource, wecTypeBuildFunction).collect(toMap());
  }

  /**
   * Returns a set of {@link EvTypeInput} instances within a map by UUID.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * UUID} uniqueness of the provided {@link EvTypeInput} which has to be checked manually, as
   * {@link EvTypeInput#equals(Object)} is NOT restricted on the uuid of {@link EvTypeInput}.
   *
   * @return a map of UUID to object- and uuid-unique {@link EvTypeInput} entities
   */
  public Map<UUID, EvTypeInput> getEvTypes() throws SourceException {
    return getEntities(EvTypeInput.class, dataSource, evTypeBuildFunction).collect(toMap());
  }

  // building functions
  protected static final BuildFunction<AssetTypeInput> assetTypeBuilder =
      entityData ->
          entityData
              .zip(uniqueEntityBuilder)
              .map(
                  pair ->
                      new AssetTypeInput(
                          pair.getRight(), pair.getLeft().getField(AssetTypeInput.ID)) {
                        @Override
                        public AssetTypeInputCopyBuilder<?> copy() {
                          return null;
                        }
                      },
                  SourceException.class);

  protected static BuildFunction<OperatorInput> operatorBuildFunction =
      uniqueEntityBuilder.with(
          pair -> new OperatorInput(pair.getRight(), pair.getLeft().getField(OperatorInput.ID)));

  protected static BuildFunction<LineTypeInput> lineTypeBuildFunction =
      uniqueEntityBuilder.with(
          pair -> {
            EntityData data = pair.getLeft();

            return new LineTypeInput(
                pair.getRight().getUuid(),
                data.getField(LineTypeInput.ID),
                data.getQuantity(LineTypeInput.B, StandardUnits.SUSCEPTANCE_PER_LENGTH),
                data.getQuantity(LineTypeInput.G, StandardUnits.CONDUCTANCE_PER_LENGTH),
                data.getQuantity(LineTypeInput.R, StandardUnits.RESISTANCE_PER_LENGTH),
                data.getQuantity(LineTypeInput.X, StandardUnits.REACTANCE_PER_LENGTH),
                data.getQuantity(LineTypeInput.I_MAX, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
                data.getQuantity(LineTypeInput.V_RATED, StandardUnits.RATED_VOLTAGE_MAGNITUDE));
          });

  protected static BuildFunction<TransformerTypeInput> transformerTypeBuilder =
      entityData ->
          entityData
              .zip(assetTypeBuilder)
              .map(
                  pair -> {
                    EntityData data = pair.getLeft();
                    return new TransformerTypeInput(
                        pair.getRight(),
                        data.getQuantity(TransformerTypeInput.G_M, StandardUnits.CONDUCTANCE),
                        data.getQuantity(TransformerTypeInput.B_M, StandardUnits.SUSCEPTANCE),
                        data.getQuantity(TransformerTypeInput.D_V, StandardUnits.DV_TAP),
                        data.getQuantity(TransformerTypeInput.D_PHI, StandardUnits.DPHI_TAP),
                        data.getInt(TransformerTypeInput.TAP_NEUTR),
                        data.getInt(TransformerTypeInput.TAP_MIN),
                        data.getInt(TransformerTypeInput.TAP_MAX)) {
                      @Override
                      public AssetTypeInputCopyBuilder<?> copy() {
                        return null;
                      }
                    };
                  },
                  SourceException.class);

  protected static BuildFunction<Transformer2WTypeInput> transformer2WTypeBuildFunction =
      transformerTypeBuilder.with(
          pair -> {
            EntityData data = pair.getLeft();

            return new Transformer2WTypeInput(
                pair.getRight(),
                data.getQuantity(Transformer2WTypeInput.R_SC, StandardUnits.RESISTANCE),
                data.getQuantity(Transformer2WTypeInput.X_SC, StandardUnits.RESISTANCE),
                data.getQuantity(Transformer2WTypeInput.S_RATED, StandardUnits.S_RATED),
                data.getQuantity(
                    Transformer2WTypeInput.V_RATED_A, StandardUnits.RATED_VOLTAGE_MAGNITUDE),
                data.getQuantity(
                    Transformer2WTypeInput.V_RATED_B, StandardUnits.RATED_VOLTAGE_MAGNITUDE),
                data.getBoolean(Transformer2WTypeInput.TAP_SIDE));
          });

  protected static BuildFunction<Transformer3WTypeInput> transformer3WTypeBuildFunction =
      transformerTypeBuilder.with(
          pair -> {
            EntityData data = pair.getLeft();

            return new Transformer3WTypeInput(
                pair.getRight(),
                data.getQuantity(Transformer3WTypeInput.S_RATED_A, StandardUnits.S_RATED),
                data.getQuantity(Transformer3WTypeInput.S_RATED_B, StandardUnits.S_RATED),
                data.getQuantity(Transformer3WTypeInput.S_RATED_C, StandardUnits.S_RATED),
                data.getQuantity(
                    Transformer3WTypeInput.V_RATED_A, StandardUnits.RATED_VOLTAGE_MAGNITUDE),
                data.getQuantity(
                    Transformer3WTypeInput.V_RATED_B, StandardUnits.RATED_VOLTAGE_MAGNITUDE),
                data.getQuantity(
                    Transformer3WTypeInput.V_RATED_C, StandardUnits.RATED_VOLTAGE_MAGNITUDE),
                data.getQuantity(Transformer3WTypeInput.R_SC_A, StandardUnits.RESISTANCE),
                data.getQuantity(Transformer3WTypeInput.R_SC_B, StandardUnits.RESISTANCE),
                data.getQuantity(Transformer3WTypeInput.R_SC_C, StandardUnits.RESISTANCE),
                data.getQuantity(Transformer3WTypeInput.X_SC_A, StandardUnits.RESISTANCE),
                data.getQuantity(Transformer3WTypeInput.X_SC_B, StandardUnits.RESISTANCE),
                data.getQuantity(Transformer3WTypeInput.X_SC_C, StandardUnits.RESISTANCE));
          });

  protected static BuildFunction<SystemParticipantTypeInput> systemParticipantTypeBuilder =
      entityData ->
          entityData
              .zip(assetTypeBuilder)
              .map(
                  pair -> {
                    EntityData data = pair.getLeft();
                    return new SystemParticipantTypeInput(
                        pair.getRight(),
                        data.getQuantity(SystemParticipantTypeInput.CAP_EX, StandardUnits.CAPEX),
                        data.getQuantity(
                            SystemParticipantTypeInput.OP_EX, StandardUnits.ENERGY_PRICE),
                        data.getQuantity(SystemParticipantTypeInput.S_RATED, StandardUnits.S_RATED),
                        data.getDouble(SystemParticipantTypeInput.COS_PHI_RATED)) {
                      @Override
                      public SystemParticipantTypeInputCopyBuilder<?> copy() {
                        return null;
                      }
                    };
                  },
                  SourceException.class);

  protected static BuildFunction<BmTypeInput> bmTypeBuildFunction =
      systemParticipantTypeBuilder.with(
          pair -> {
            EntityData data = pair.getLeft();

            return new BmTypeInput(
                pair.getRight(),
                data.getQuantity(
                    BmTypeInput.ACTIVE_POWER_GRADIENT, StandardUnits.ACTIVE_POWER_GRADIENT),
                data.getQuantity(BmTypeInput.ETA_CONV, StandardUnits.EFFICIENCY));
          });

  protected static BuildFunction<ChpTypeInput> chpTypeBuildFunction =
      systemParticipantTypeBuilder.with(
          pair -> {
            EntityData data = pair.getLeft();

            return new ChpTypeInput(
                pair.getRight(),
                data.getQuantity(ChpTypeInput.ETA_EL, StandardUnits.EFFICIENCY),
                data.getQuantity(ChpTypeInput.ETA_THERMAL, StandardUnits.EFFICIENCY),
                data.getQuantity(ChpTypeInput.P_THERMAL, StandardUnits.ACTIVE_POWER_IN),
                data.getQuantity(ChpTypeInput.P_OWN, StandardUnits.ACTIVE_POWER_IN));
          });

  protected static BuildFunction<HpTypeInput> hpTypeBuildFunction =
      systemParticipantTypeBuilder.with(
          pair ->
              new HpTypeInput(
                  pair.getRight(),
                  pair.getLeft()
                      .getQuantity(HpTypeInput.P_THERMAL, StandardUnits.ACTIVE_POWER_IN)));

  protected static BuildFunction<StorageTypeInput> storageBuildFunction =
      systemParticipantTypeBuilder.with(
          pair -> {
            EntityData data = pair.getLeft();

            return new StorageTypeInput(
                pair.getRight(),
                data.getQuantity(StorageTypeInput.E_STORAGE, StandardUnits.ENERGY_IN),
                data.getQuantity(StorageTypeInput.P_MAX, StandardUnits.ACTIVE_POWER_IN),
                data.getQuantity(
                    StorageTypeInput.ACTIVE_POWER_GRADIENT, StandardUnits.ACTIVE_POWER_GRADIENT),
                data.getQuantity(StorageTypeInput.ETA, StandardUnits.EFFICIENCY));
          });

  protected static BuildFunction<WecTypeInput> wecTypeBuildFunction =
      systemParticipantTypeBuilder.with(
          pair -> {
            EntityData data = pair.getLeft();

            WecCharacteristicInput cpCharacteristic;
            String CP_CHARACTERISTIC = WecTypeInput.CP_CHARACTERISTIC;

            try {
              cpCharacteristic = new WecCharacteristicInput(data.getField(CP_CHARACTERISTIC));
            } catch (ParsingException ex) {
              throw new FactoryException(
                  "Cannot parse the following Betz characteristic: '"
                      + data.getField(CP_CHARACTERISTIC)
                      + "'",
                  ex);
            }

            return new WecTypeInput(
                pair.getRight(),
                cpCharacteristic,
                data.getQuantity(WecTypeInput.ETA_CONV, StandardUnits.EFFICIENCY),
                data.getQuantity(WecTypeInput.ROTOR_AREA, StandardUnits.ROTOR_AREA),
                data.getQuantity(WecTypeInput.HUB_HEIGHT, StandardUnits.HUB_HEIGHT));
          });

  protected static BuildFunction<EvTypeInput> evTypeBuildFunction =
      systemParticipantTypeBuilder.with(
          pair -> {
            EntityData data = pair.getLeft();
            return new EvTypeInput(
                pair.getRight(),
                data.getQuantity(EvTypeInput.E_STORAGE, StandardUnits.ENERGY_IN),
                data.getQuantity(EvTypeInput.E_CONS, StandardUnits.ENERGY_PER_DISTANCE),
                data.getQuantity(EvTypeInput.S_RATED_DC, StandardUnits.ACTIVE_POWER_IN));
          });
}
