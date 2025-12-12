/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.io.source;

import static edu.ie3.datamodel.io.naming.EntityFieldNames.*;
import static tech.units.indriya.unit.Units.PERCENT;

import edu.ie3.datamodel.exceptions.ParsingException;
import edu.ie3.datamodel.exceptions.SourceException;
import edu.ie3.datamodel.exceptions.ValidationException;
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
import java.util.Set;
import java.util.UUID;
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

  public ResultEntitySource(DataSource dataSource) {
    this(dataSource, TimeUtil.withDefaults);
  }

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
    validate(
        dataSource,
        LoadResult.class,
        FixedFeedInResult.class,
        BmResult.class,
        PvResult.class,
        WecResult.class,
        EvcsResult.class,
        EmResult.class,
        ChpResult.class,
        HpResult.class,
        StorageResult.class,
        EvResult.class,
        CylindricalStorageResult.class,
        DomesticHotWaterStorageResult.class,
        ThermalHouseResult.class,
        NodeResult.class,
        SwitchResult.class,
        LineResult.class,
        Transformer2WResult.class,
        Transformer3WResult.class,
        FlexOptionsResult.class,
        CongestionResult.class);
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
    return getResultEntities(NodeResult.class, nodeResultBuildFunction(timeUtil));
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
    return getResultEntities(SwitchResult.class, switchResultBuildFunction(timeUtil));
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
    return getResultEntities(LineResult.class, lineResultBuildFunction(timeUtil));
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
    return getResultEntities(Transformer2WResult.class, transformer2WResultBuilder(timeUtil));
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
    return getResultEntities(Transformer3WResult.class, transformer3WResultBuilder(timeUtil));
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
    return getResultEntities(FlexOptionsResult.class, flexOptionsResultBuildFunction(timeUtil));
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
    return getResultEntities(LoadResult.class, loadResultBuildFunction(timeUtil));
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
    return getResultEntities(PvResult.class, pvResultBuildFunction(timeUtil));
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
    return getResultEntities(FixedFeedInResult.class, fixedFeedInResultBuildFunction(timeUtil));
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
    return getResultEntities(BmResult.class, bmResultBuildFunction(timeUtil));
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
    return getResultEntities(ChpResult.class, chpResultBuildFunction(timeUtil));
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
    return getResultEntities(WecResult.class, wecResultBuildFunction(timeUtil));
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
    return getResultEntities(StorageResult.class, storageResultBuildFunction(timeUtil));
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
    return getResultEntities(EvcsResult.class, evcsResultBuildFunction(timeUtil));
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
    return getResultEntities(EvResult.class, evResultBuildFunction(timeUtil));
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
    return getResultEntities(HpResult.class, hpResultBuildFunction(timeUtil));
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
        CylindricalStorageResult.class, cylindricalStorageResultBuilder(timeUtil));
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
    return getResultEntities(DomesticHotWaterStorageResult.class, dhwsResultBuilder(timeUtil));
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
    return getResultEntities(ThermalHouseResult.class, thermalHouseResultBuildFunction(timeUtil));
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
    return getResultEntities(EmResult.class, emResultBuildFunction(timeUtil));
  }

  /**
   * Returns a unique set of {@link CongestionResult} instances.
   *
   * @return a set of object and subgrid unique {@link CongestionResult} entities
   */
  public Set<CongestionResult> getCongestionResults() throws SourceException {
    return getResultEntities(CongestionResult.class, congestionResultBuildFunction(timeUtil));
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

  // build functions
  protected static BuildFunction<ResultEntity> buildResult(TimeUtil timeUtil) {
    return entityData ->
        entityData.flatMap(
            data -> {
              try {
                ZonedDateTime time = timeUtil.toZonedDateTime(data.getField(TIME));
                UUID inputModel = data.getUUID(INPUT_MODEL);

                return Try.Success.of(new ResultEntity(time, inputModel) {});
              } catch (Exception e) {
                return Try.Failure.of(
                    new SourceException("Could not build result model due to: ", e));
              }
            });
  }

  protected static BuildFunction<NodeResult> nodeResultBuildFunction(TimeUtil timeUtil) {
    return buildResult(timeUtil)
        .with(
            pair -> {
              EntityData data = pair.getLeft();

              return new NodeResult(
                  pair.getRight(),
                  data.getQuantity(V_MAG, StandardUnits.VOLTAGE_MAGNITUDE),
                  data.getQuantity(V_ANG, StandardUnits.VOLTAGE_ANGLE));
            });
  }

  protected static BuildFunction<FlexOptionsResult> flexOptionsResultBuildFunction(
      TimeUtil timeUtil) {
    return buildResult(timeUtil)
        .with(
            pair -> {
              EntityData data = pair.getLeft();

              return new FlexOptionsResult(
                  pair.getRight(),
                  data.getQuantity(P_REF, StandardUnits.ACTIVE_POWER_RESULT),
                  data.getQuantity(P_MIN, StandardUnits.ACTIVE_POWER_RESULT),
                  data.getQuantity(P_MAX, StandardUnits.ACTIVE_POWER_RESULT));
            });
  }

  protected static BuildFunction<SwitchResult> switchResultBuildFunction(TimeUtil timeUtil) {
    return buildResult(timeUtil)
        .with(pair -> new SwitchResult(pair.getRight(), pair.getLeft().getBoolean(CLOSED)));
  }

  protected static BuildFunction<CongestionResult> congestionResultBuildFunction(
      TimeUtil timeUtil) {
    return buildResult(timeUtil)
        .with(
            pair -> {
              EntityData data = pair.getLeft();

              String typeString = data.getField(TYPE);

              CongestionResult.InputModelType type =
                  Try.of(
                          () -> CongestionResult.InputModelType.parse(typeString),
                          ParsingException.class)
                      .transformF(SourceException::new)
                      .getOrThrow();

              return new CongestionResult(
                  pair.getRight(),
                  type,
                  data.getInt(SUBGRID),
                  data.getQuantity(VALUE, PERCENT),
                  data.getQuantity(MIN, PERCENT),
                  data.getQuantity(MAX, PERCENT));
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
                      data.getQuantity(IAMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
                      data.getQuantity(IAANG, StandardUnits.ELECTRIC_CURRENT_ANGLE),
                      data.getQuantity(IBMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
                      data.getQuantity(IBANG, StandardUnits.ELECTRIC_CURRENT_ANGLE)) {};
                },
                SourceException.class);
  }

  protected static BuildFunction<LineResult> lineResultBuildFunction(TimeUtil timeUtil) {
    return connectorResultBuilder(timeUtil).with(pair -> new LineResult(pair.getRight()));
  }

  protected static BuildFunction<TransformerResult> transformerResultBuilder(TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(connectorResultBuilder(timeUtil))
            .map(
                pair -> new TransformerResult(pair.getRight(), pair.getLeft().getInt(TAPPOS)) {},
                SourceException.class);
  }

  protected static BuildFunction<Transformer2WResult> transformer2WResultBuilder(
      TimeUtil timeUtil) {
    return transformerResultBuilder(timeUtil)
        .with(pair -> new Transformer2WResult(pair.getRight()));
  }

  protected static BuildFunction<Transformer3WResult> transformer3WResultBuilder(
      TimeUtil timeUtil) {
    return transformerResultBuilder(timeUtil)
        .with(
            pair -> {
              EntityData data = pair.getLeft();

              return new Transformer3WResult(
                  pair.getRight(),
                  data.getQuantity(ICMAG, StandardUnits.ELECTRIC_CURRENT_MAGNITUDE),
                  data.getQuantity(ICANG, StandardUnits.ELECTRIC_CURRENT_ANGLE));
            });
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
                      data.getQuantity(POWER, StandardUnits.ACTIVE_POWER_RESULT),
                      data.getQuantity(REACTIVE_POWER, StandardUnits.REACTIVE_POWER_RESULT)) {};
                },
                SourceException.class);
  }

  protected static BuildFunction<LoadResult> loadResultBuildFunction(TimeUtil timeUtil) {
    return participantResultBuilder(timeUtil).with(pair -> new LoadResult(pair.getRight()));
  }

  protected static BuildFunction<PvResult> pvResultBuildFunction(TimeUtil timeUtil) {
    return participantResultBuilder(timeUtil).with(pair -> new PvResult(pair.getRight()));
  }

  protected static BuildFunction<FixedFeedInResult> fixedFeedInResultBuildFunction(
      TimeUtil timeUtil) {
    return participantResultBuilder(timeUtil).with(pair -> new FixedFeedInResult(pair.getRight()));
  }

  protected static BuildFunction<BmResult> bmResultBuildFunction(TimeUtil timeUtil) {
    return participantResultBuilder(timeUtil).with(pair -> new BmResult(pair.getRight()));
  }

  protected static BuildFunction<WecResult> wecResultBuildFunction(TimeUtil timeUtil) {
    return participantResultBuilder(timeUtil).with(pair -> new WecResult(pair.getRight()));
  }

  protected static BuildFunction<EvcsResult> evcsResultBuildFunction(TimeUtil timeUtil) {
    return participantResultBuilder(timeUtil).with(pair -> new EvcsResult(pair.getRight()));
  }

  protected static BuildFunction<EmResult> emResultBuildFunction(TimeUtil timeUtil) {
    return participantResultBuilder(timeUtil).with(pair -> new EmResult(pair.getRight()));
  }

  protected static BuildFunction<SystemParticipantWithHeatResult> participantWithHeatResultBuilder(
      TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(participantResultBuilder(timeUtil))
            .map(
                pair -> {
                  EntityData data = pair.getLeft();

                  return new SystemParticipantWithHeatResult(
                      pair.getRight(), data.getQuantity(Q_DOT, StandardUnits.Q_DOT_RESULT)) {};
                },
                SourceException.class);
  }

  protected static BuildFunction<ChpResult> chpResultBuildFunction(TimeUtil timeUtil) {
    return participantWithHeatResultBuilder(timeUtil).with(pair -> new ChpResult(pair.getRight()));
  }

  protected static BuildFunction<HpResult> hpResultBuildFunction(TimeUtil timeUtil) {
    return participantWithHeatResultBuilder(timeUtil).with(pair -> new HpResult(pair.getRight()));
  }

  protected static BuildFunction<ElectricalEnergyStorageResult>
      electricalEnergyStorageResultBuilder(TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(participantResultBuilder(timeUtil))
            .map(
                pair ->
                    new ElectricalEnergyStorageResult(
                        pair.getRight(), pair.getLeft().getQuantity(SOC, StandardUnits.SOC)) {
                      @Override
                      public ComparableQuantity<Dimensionless> getSoc() {
                        return super.getSoc();
                      }
                    },
                SourceException.class);
  }

  protected static BuildFunction<StorageResult> storageResultBuildFunction(TimeUtil timeUtil) {
    return electricalEnergyStorageResultBuilder(timeUtil)
        .with(pair -> new StorageResult(pair.getRight()));
  }

  protected static BuildFunction<EvResult> evResultBuildFunction(TimeUtil timeUtil) {
    return electricalEnergyStorageResultBuilder(timeUtil)
        .with(pair -> new EvResult(pair.getRight()));
  }

  protected static BuildFunction<ThermalUnitResult> thermalUnitResultBuilder(TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(buildResult(timeUtil))
            .map(
                pair ->
                    new ThermalUnitResult(
                        pair.getRight(),
                        pair.getLeft().getQuantity(Q_DOT, StandardUnits.HEAT_DEMAND)) {
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

  protected static BuildFunction<ThermalHouseResult> thermalHouseResultBuildFunction(
      TimeUtil timeUtil) {
    return thermalSinkResultBuilder(timeUtil)
        .with(
            pair ->
                new ThermalHouseResult(
                    pair.getRight(),
                    pair.getLeft().getQuantity(INDOOR_TEMPERATURE, StandardUnits.TEMPERATURE)));
  }

  protected static BuildFunction<ThermalStorageResult> thermalStorageResultBuilder(
      TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(thermalUnitResultBuilder(timeUtil))
            .map(
                pair ->
                    new ThermalStorageResult(
                        pair.getRight(),
                        pair.getLeft().getQuantity(ENERGY, StandardUnits.ENERGY_RESULT)) {
                      @Override
                      public ComparableQuantity<Energy> getEnergy() {
                        return super.getEnergy();
                      }
                    },
                SourceException.class);
  }

  protected static BuildFunction<AbstractThermalStorageResult> abstractThermalStorageResultBuilder(
      TimeUtil timeUtil) {
    return entityData ->
        entityData
            .zip(thermalStorageResultBuilder(timeUtil))
            .map(
                pair ->
                    new AbstractThermalStorageResult(
                        pair.getRight(),
                        pair.getLeft().getQuantity(FILL_LEVEL, StandardUnits.FILL_LEVEL)) {},
                SourceException.class);
  }

  protected static BuildFunction<CylindricalStorageResult> cylindricalStorageResultBuilder(
      TimeUtil timeUtil) {
    return abstractThermalStorageResultBuilder(timeUtil)
        .with(pair -> new CylindricalStorageResult(pair.getRight()));
  }

  protected static BuildFunction<DomesticHotWaterStorageResult> dhwsResultBuilder(
      TimeUtil timeUtil) {
    return abstractThermalStorageResultBuilder(timeUtil)
        .with(pair -> new DomesticHotWaterStorageResult(pair.getRight()));
  }
}
