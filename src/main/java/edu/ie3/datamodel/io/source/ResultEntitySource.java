/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.models.result.system.ElectricalEnergyStorageResult.electricalEnergyStorageFields;
import static edu.ie3.datamodel.models.result.system.SystemParticipantResult.participantFields;
import static edu.ie3.datamodel.models.result.system.SystemParticipantWithHeatResult.participantWithHeatFields;
import static edu.ie3.datamodel.models.result.thermal.AbstractThermalStorageResult.abstractThermalStorageFields;
import static tech.units.indriya.unit.Units.PERCENT;

import edu.ie3.datamodel.exceptions.*;
import edu.ie3.datamodel.io.factory.EntityData;
import edu.ie3.datamodel.models.StandardUnits;
import edu.ie3.datamodel.models.result.CongestionResult;
import edu.ie3.datamodel.models.result.NodeResult;
import edu.ie3.datamodel.models.result.ResultEntity;
import edu.ie3.datamodel.models.result.connector.*;
import edu.ie3.datamodel.models.result.system.*;
import edu.ie3.datamodel.models.result.thermal.*;
import edu.ie3.datamodel.utils.Try;
import edu.ie3.util.TimeUtil;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Power;
import tech.units.indriya.ComparableQuantity;

/**
 * Interface that provides the capability to build entities of type {@link ResultEntity} container
 * from .csv files.
 *
 * @version 0.1
 * @since 22 June 2021
 */
public class ResultEntitySource extends EntitySource {

  private final DataSource dataSource;
  protected final TimeUtil timeUtil;

  public ResultEntitySource(DataSource dataSource, TimeUtil timeUtil) {
    this.dataSource = dataSource;
    this.timeUtil = timeUtil;
  }

  public ResultEntitySource(DataSource dataSource, DateTimeFormatter dateTimeFormatter) {
    this.dataSource = dataSource;
    this.timeUtil = new TimeUtil(dateTimeFormatter);
  }

  @Override
  public void validate() throws ValidationException {
    List<Try<Void, ValidationException>> results =
        new ArrayList<>(
            Stream.of(
                    LoadResult.class,
                    FixedFeedInResult.class,
                    BmResult.class,
                    PvResult.class,
                    WecResult.class,
                    EvcsResult.class,
                    EmResult.class)
                .map(c -> validate(c, dataSource, new SourceValidator<>(participantFields())))
                .toList());

    Stream.of(ChpResult.class, HpResult.class)
        .map(c -> validate(c, dataSource, new SourceValidator<>(participantWithHeatFields())))
        .forEach(results::add);

    Stream.of(StorageResult.class, EvResult.class)
        .map(c -> validate(c, dataSource, new SourceValidator<>(electricalEnergyStorageFields())))
        .forEach(results::add);

    Stream.of(CylindricalStorageResult.class, DomesticHotWaterStorageResult.class)
        .map(c -> validate(c, dataSource, new SourceValidator<>(abstractThermalStorageFields())))
        .forEach(results::add);

    Stream.of(
            validate(
                ThermalHouseResult.class,
                dataSource,
                new SourceValidator<>(ThermalHouseResult.getFields())),
            validate(
                SwitchResult.class, dataSource, new SourceValidator<>(SwitchResult.getFields())),
            validate(NodeResult.class, dataSource, new SourceValidator<>(NodeResult.getFields())),
            validate(LineResult.class, dataSource, new SourceValidator<>(LineResult.getFields())),
            validate(
                Transformer2WResult.class,
                dataSource,
                new SourceValidator<>(Transformer2WResult.getFields())),
            validate(
                Transformer3WResult.class,
                dataSource,
                new SourceValidator<>(Transformer3WResult.getFields())),
            validate(
                FlexOptionsResult.class,
                dataSource,
                new SourceValidator<>(FlexOptionsResult.getFields())),
            validate(
                CongestionResult.class,
                dataSource,
                new SourceValidator<>(CongestionResult.getFields())))
        .forEach(results::add);

    Try.scanCollection(results, Void.class, FailedValidationException::new).getOrThrow();
  }

  /**
   * Returns a unique set of {@link NodeResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link NodeResult} which has to be checked manually,
   * as {@link NodeResult#equals(Object)} is NOT restricted by the uuid of {@link NodeResult}.
   *
   * @return a set of object and uuid unique {@link NodeResult} entities
   */
  public Set<NodeResult> getNodeResults() throws SourceException {
    BuildFunction<NodeResult> buildFunction =
        buildResult(timeUtil)
            .with(
                pair -> {
                  EntityData data = pair.getLeft();

                  return new NodeResult(
                      pair.getRight(),
                      data.getQuantity(NodeResult.VMAG, StandardUnits.VOLTAGE_MAGNITUDE),
                      data.getQuantity(NodeResult.VANG, StandardUnits.VOLTAGE_ANGLE));
                });

    return getResultEntities(NodeResult.class, buildFunction);
  }

  /**
   * Returns a unique set of {@link SwitchResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link SwitchResult} which has to be checked
   * manually, as {@link SwitchResult#equals(Object)} is NOT restricted by the uuid of {@link
   * SwitchResult}.
   *
   * @return a set of object and uuid unique {@link SwitchResult} entities
   */
  public Set<SwitchResult> getSwitchResults() throws SourceException {
    return getResultEntities(
        SwitchResult.class,
        buildResult(timeUtil)
            .with(
                pair ->
                    new SwitchResult(
                        pair.getRight(), pair.getLeft().getBoolean(SwitchResult.CLOSED))));
  }

  /**
   * Returns a unique set of {@link LineResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link LineResult} which has to be checked manually,
   * as {@link LineResult#equals(Object)} is NOT restricted by the uuid of {@link LineResult}.
   *
   * @return a set of object and uuid unique {@link LineResult} entities
   */
  public Set<LineResult> getLineResults() throws SourceException {
    return getResultEntities(
        LineResult.class,
        connectorResultBuilder(timeUtil).with(pair -> new LineResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link Transformer2WResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link Transformer2WResult} which has to be checked
   * manually, as {@link Transformer2WResult#equals(Object)} is NOT restricted by the uuid of {@link
   * Transformer2WResult}.
   *
   * @return a set of object and uuid unique {@link Transformer2WResult} entities
   */
  public Set<Transformer2WResult> getTransformer2WResultResults() throws SourceException {
    return getResultEntities(
        Transformer2WResult.class,
        transformerResultBuilder(timeUtil).with(pair -> new Transformer2WResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link Transformer3WResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link Transformer3WResult} which has to be checked
   * manually, as {@link Transformer3WResult#equals(Object)} is NOT restricted by the uuid of {@link
   * Transformer3WResult}.
   *
   * @return a set of object and uuid unique {@link Transformer3WResult} entities
   */
  public Set<Transformer3WResult> getTransformer3WResultResults() throws SourceException {
    BuildFunction<Transformer3WResult> buildFunction =
        transformerResultBuilder(timeUtil)
            .with(
                pair -> {
                  EntityData data = pair.getLeft();

                  return new Transformer3WResult(
                      pair.getRight(),
                      data.getQuantity(
                          Transformer3WResult.ICMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
                      data.getQuantity(
                          Transformer3WResult.ICANG, StandardUnits.ELECTRIC_CURRENT_ANGLE));
                });

    return getResultEntities(Transformer3WResult.class, buildFunction);
  }

  /**
   * Returns a unique set of {@link FlexOptionsResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link FlexOptionsResult} which has to be checked
   * manually, as {@link FlexOptionsResult#equals(Object)} is NOT restricted by the uuid of {@link
   * FlexOptionsResult}.
   *
   * @return a set of object and uuid unique {@link FlexOptionsResult} entities
   */
  public Set<FlexOptionsResult> getFlexOptionsResults() throws SourceException {
    BuildFunction<FlexOptionsResult> buildFunction =
        buildResult(timeUtil)
            .with(
                pair -> {
                  EntityData data = pair.getLeft();

                  return new FlexOptionsResult(
                      pair.getRight(),
                      data.getQuantity(FlexOptionsResult.P_REF, StandardUnits.ACTIVE_POWER_RESULT),
                      data.getQuantity(FlexOptionsResult.P_MIN, StandardUnits.ACTIVE_POWER_RESULT),
                      data.getQuantity(FlexOptionsResult.P_MAX, StandardUnits.ACTIVE_POWER_RESULT));
                });

    return getResultEntities(FlexOptionsResult.class, buildFunction);
  }

  /**
   * Returns a unique set of {@link LoadResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link LoadResult} which has to be checked manually,
   * as {@link LoadResult#equals(Object)} is NOT restricted by the uuid of {@link LoadResult}.
   *
   * @return a set of object and uuid unique {@link LoadResult} entities
   */
  public Set<LoadResult> getLoadResults() throws SourceException {
    return getResultEntities(
        LoadResult.class,
        participantResultBuilder(timeUtil).with(pair -> new LoadResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link PvResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link PvResult} which has to be checked manually,
   * as {@link PvResult#equals(Object)} is NOT restricted by the uuid of {@link PvResult}.
   *
   * @return a set of object and uuid unique {@link PvResult} entities
   */
  public Set<PvResult> getPvResults() throws SourceException {
    return getResultEntities(
        PvResult.class,
        participantResultBuilder(timeUtil).with(pair -> new PvResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link FixedFeedInResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link FixedFeedInResult} which has to be checked
   * manually, as {@link FixedFeedInResult#equals(Object)} is NOT restricted by the uuid of {@link
   * FixedFeedInResult}.
   *
   * @return a set of object and uuid unique {@link FixedFeedInResult} entities
   */
  public Set<FixedFeedInResult> getFixedFeedInResults() throws SourceException {
    return getResultEntities(
        FixedFeedInResult.class,
        participantResultBuilder(timeUtil).with(pair -> new FixedFeedInResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link BmResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link BmResult} which has to be checked manually,
   * as {@link BmResult#equals(Object)} is NOT restricted by the uuid of {@link BmResult}.
   *
   * @return a set of object and uuid unique {@link BmResult} entities
   */
  public Set<BmResult> getBmResults() throws SourceException {
    return getResultEntities(
        BmResult.class,
        participantResultBuilder(timeUtil).with(pair -> new BmResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link ChpResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ChpResult} which has to be checked manually,
   * as {@link ChpResult#equals(Object)} is NOT restricted by the uuid of {@link ChpResult}.
   *
   * @return a set of object and uuid unique {@link ChpResult} entities
   */
  public Set<ChpResult> getChpResults() throws SourceException {
    return getResultEntities(
        ChpResult.class,
        participantWithHeatResultBuilder(timeUtil).with(pair -> new ChpResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link WecResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link WecResult} which has to be checked manually,
   * as {@link WecResult#equals(Object)} is NOT restricted by the uuid of {@link WecResult}.
   *
   * @return a set of object and uuid unique {@link WecResult} entities
   */
  public Set<WecResult> getWecResults() throws SourceException {
    return getResultEntities(
        WecResult.class,
        participantResultBuilder(timeUtil).with(pair -> new WecResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link StorageResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link StorageResult} which has to be checked
   * manually, as {@link StorageResult#equals(Object)} is NOT restricted by the uuid of {@link
   * StorageResult}.
   *
   * @return a set of object and uuid unique {@link StorageResult} entities
   */
  public Set<StorageResult> getStorageResults() throws SourceException {
    return getResultEntities(
        StorageResult.class,
        electricalEnergyStorageResultBuilder(timeUtil)
            .with(pair -> new StorageResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link EvcsResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EvcsResult} which has to be checked manually,
   * as {@link EvcsResult#equals(Object)} is NOT restricted by the uuid of {@link EvcsResult}.
   *
   * @return a set of object and uuid unique {@link EvcsResult} entities
   */
  public Set<EvcsResult> getEvcsResults() throws SourceException {
    return getResultEntities(
        EvcsResult.class,
        participantResultBuilder(timeUtil).with(pair -> new EvcsResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link EvResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EvResult} which has to be checked manually,
   * as {@link EvResult#equals(Object)} is NOT restricted by the uuid of {@link EvResult}.
   *
   * @return a set of object and uuid unique {@link EvResult} entities
   */
  public Set<EvResult> getEvResults() throws SourceException {
    return getResultEntities(
        EvResult.class,
        electricalEnergyStorageResultBuilder(timeUtil).with(pair -> new EvResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link HpResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link HpResult} which has to be checked manually,
   * as {@link HpResult#equals(Object)} is NOT restricted by the uuid of {@link HpResult}.
   *
   * @return a set of object and uuid unique {@link HpResult} entities
   */
  public Set<HpResult> getHpResults() throws SourceException {
    return getResultEntities(
        HpResult.class,
        participantWithHeatResultBuilder(timeUtil).with(pair -> new HpResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link CylindricalStorageResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link CylindricalStorageResult} which has to be
   * checked manually, as {@link CylindricalStorageResult#equals(Object)} is NOT restricted by the
   * uuid of {@link CylindricalStorageResult}.
   *
   * @return a set of object and uuid unique {@link CylindricalStorageResult} entities
   */
  public Set<CylindricalStorageResult> getCylindricalStorageResult() throws SourceException {
    return getResultEntities(
        CylindricalStorageResult.class,
        abstractThermalStorageResultBuilder(timeUtil)
            .with(pair -> new CylindricalStorageResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link DomesticHotWaterStorageResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link DomesticHotWaterStorageResult} which has to
   * be checked manually, as {@link DomesticHotWaterStorageResult#equals(Object)} is NOT restricted
   * by the uuid of {@link DomesticHotWaterStorageResult}.
   *
   * @return a set of object and uuid unique {@link DomesticHotWaterStorageResult} entities
   */
  public Set<DomesticHotWaterStorageResult> getDomesticHotWaterStorageResult()
      throws SourceException {
    return getResultEntities(
        DomesticHotWaterStorageResult.class,
        abstractThermalStorageResultBuilder(timeUtil)
            .with(pair -> new DomesticHotWaterStorageResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link ThermalHouseResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link ThermalHouseResult} which has to be checked
   * manually, as {@link ThermalHouseResult#equals(Object)} is NOT restricted by the uuid of {@link
   * ThermalHouseResult}.
   *
   * @return a set of object and uuid unique {@link ThermalHouseResult} entities
   */
  public Set<ThermalHouseResult> getThermalHouseResults() throws SourceException {
    return getResultEntities(
        ThermalHouseResult.class,
        thermalSinkResultBuilder(timeUtil)
            .with(
                pair ->
                    new ThermalHouseResult(
                        pair.getRight(),
                        pair.getLeft()
                            .getQuantity(
                                ThermalHouseResult.INDOOR_TEMPERATURE,
                                StandardUnits.TEMPERATURE))));
  }

  /**
   * Returns a unique set of {@link EmResult} instances.
   *
   * <p>This set has to be unique in the sense of object uniqueness but also in the sense of {@link
   * java.util.UUID} uniqueness of the provided {@link EmResult} which has to be checked manually,
   * as {@link EmResult#equals(Object)} is NOT restricted by the uuid of {@link EmResult}.
   *
   * @return a set of object and uuid unique {@link EmResult} entities
   */
  public Set<EmResult> getEmResults() throws SourceException {
    return getResultEntities(
        EmResult.class,
        participantResultBuilder(timeUtil).with(pair -> new EmResult(pair.getRight())));
  }

  /**
   * Returns a unique set of {@link CongestionResult} instances.
   *
   * @return a set of object and subgrid unique {@link CongestionResult} entities
   */
  public Set<CongestionResult> getCongestionResults() throws SourceException {
    BuildFunction<CongestionResult> buildFunction =
        buildResult(timeUtil)
            .with(
                pair -> {
                  EntityData data = pair.getLeft();

                  String typeString = data.getField(CongestionResult.TYPE);

                  CongestionResult.InputModelType type =
                      Try.of(
                              () -> CongestionResult.InputModelType.parse(typeString),
                              ParsingException.class)
                          .transformF(SourceException::new)
                          .getOrThrow();

                  return new CongestionResult(
                      pair.getRight(),
                      type,
                      data.getInt(CongestionResult.SUBGRID),
                      data.getQuantity(CongestionResult.VALUE, PERCENT),
                      data.getQuantity(CongestionResult.MIN, PERCENT),
                      data.getQuantity(CongestionResult.MAX, PERCENT));
                });

    return getResultEntities(CongestionResult.class, buildFunction);
  }

  // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

  /**
   * Build and cast entities to the correct type, since result factories outputs result entities of
   * some general type.
   *
   * @param entityClass that should be build
   * @return a set of entities
   * @param <T> type of entity
   */
  private <T extends ResultEntity> Set<T> getResultEntities(
      Class<T> entityClass, BuildFunction<T> buildFunction) throws SourceException {
    return getEntities(entityClass, dataSource, buildFunction).collect(toSet());
  }

  protected static BuildFunction<ResultEntity> buildResult(TimeUtil timeUtil) {
    return entityData ->
        entityData.flatMap(
            data -> {
              try {
                ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(ResultEntity.TIME));
                UUID inputModel = data.getUUID(ResultEntity.INPUT_MODEL);

                return Try.Success.of(new ResultEntity(time, inputModel) {});
              } catch (Exception e) {
                return Try.Failure.of(
                    new SourceException("Could not build result model due to: ", e));
              }
            });
  }

  protected static BuildFunction<ConnectorResult> connectorResultBuilder(TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(buildResult(timeUtil))
            .map(
                pair -> {
                  EntityData data = pair.getLeft();

                  return new ConnectorResult(
                      pair.getRight(),
                      data.getQuantity(
                          ConnectorResult.IAMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
                      data.getQuantity(ConnectorResult.IAANG, StandardUnits.ELECTRIC_CURRENT_ANGLE),
                      data.getQuantity(
                          ConnectorResult.IBMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
                      data.getQuantity(
                          ConnectorResult.IBANG, StandardUnits.ELECTRIC_CURRENT_ANGLE)) {};
                },
                SourceException.class);
  }

  protected static BuildFunction<TransformerResult> transformerResultBuilder(TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(connectorResultBuilder(timeUtil))
            .map(
                pair ->
                    new TransformerResult(
                        pair.getRight(), pair.getLeft().getInt(TransformerResult.TAPPOS)) {},
                SourceException.class);
  }

  protected static BuildFunction<SystemParticipantResult> participantResultBuilder(
      TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(buildResult(timeUtil))
            .map(
                pair -> {
                  EntityData data = pair.getLeft();

                  return new SystemParticipantResult(
                      pair.getRight(),
                      data.getQuantity(
                          SystemParticipantResult.POWER, StandardUnits.ACTIVE_POWER_RESULT),
                      data.getQuantity(
                          SystemParticipantResult.REACTIVE_POWER,
                          StandardUnits.REACTIVE_POWER_RESULT)) {};
                },
                SourceException.class);
  }

  protected static BuildFunction<SystemParticipantWithHeatResult> participantWithHeatResultBuilder(
      TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(participantWithHeatResultBuilder(timeUtil))
            .map(
                pair -> {
                  EntityData data = pair.getLeft();

                  return new SystemParticipantWithHeatResult(
                      pair.getRight(),
                      data.getQuantity(
                          SystemParticipantWithHeatResult.Q_DOT, StandardUnits.Q_DOT_RESULT)) {};
                },
                SourceException.class);
  }

  protected static BuildFunction<ElectricalEnergyStorageResult>
      electricalEnergyStorageResultBuilder(TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(participantResultBuilder(timeUtil))
            .map(
                pair ->
                    new ElectricalEnergyStorageResult(
                        pair.getRight(),
                        pair.getLeft()
                            .getQuantity(ElectricalEnergyStorageResult.SOC, StandardUnits.SOC)) {
                      @Override
                      public ComparableQuantity<Dimensionless> getSoc() {
                        return super.getSoc();
                      }
                    },
                SourceException.class);
  }

  protected static BuildFunction<ThermalUnitResult> thermalUnitResultBuilder(TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(buildResult(timeUtil))
            .map(
                pair ->
                    new ThermalUnitResult(
                        pair.getRight(),
                        pair.getLeft()
                            .getQuantity(ThermalUnitResult.Q_DOT, StandardUnits.HEAT_DEMAND)) {
                      @Override
                      public ComparableQuantity<Power> getqDot() {
                        return super.getqDot();
                      }
                    },
                SourceException.class);
  }

  protected static BuildFunction<ThermalSinkResult> thermalSinkResultBuilder(TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(thermalUnitResultBuilder(timeUtil))
            .map(
                pair ->
                    new ThermalSinkResult(pair.getRight()) {
                      @Override
                      public ComparableQuantity<Power> getqDot() {
                        return super.getqDot();
                      }
                    },
                SourceException.class);
  }

  protected BuildFunction<ThermalStorageResult> thermalStorageResultBuilder(TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(thermalUnitResultBuilder(timeUtil))
            .map(
                pair ->
                    new ThermalStorageResult(
                        pair.getRight(),
                        pair.getLeft()
                            .getQuantity(
                                ThermalStorageResult.ENERGY, StandardUnits.ENERGY_RESULT)) {
                      @Override
                      public ComparableQuantity<Energy> getEnergy() {
                        return super.getEnergy();
                      }
                    },
                SourceException.class);
  }

  protected BuildFunction<AbstractThermalStorageResult> abstractThermalStorageResultBuilder(
      TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(thermalStorageResultBuilder(timeUtil))
            .map(
                pair ->
                    new AbstractThermalStorageResult(
                        pair.getRight(),
                        pair.getLeft()
                            .getQuantity(
                                AbstractThermalStorageResult.FILL_LEVEL,
                                StandardUnits.FILL_LEVEL)) {},
                SourceException.class);
  }
}
