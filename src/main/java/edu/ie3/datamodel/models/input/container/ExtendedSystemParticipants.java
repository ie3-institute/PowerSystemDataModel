/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.input.system.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the accumulation of grid-connected system participant elements (BM plants, CHP plants,
 * EVCS, fixed feed ins, heat pumps, air conditions, loads, PV plants, storages, WECs).
 *
 * @since 1.0
 */
public class ExtendedSystemParticipants implements InputContainer<SystemParticipantInput> {
  private final Set<BmInput> bmPlants;
  private final Set<ChpInput> chpPlants;
  private final Set<EvcsInput> evcs;
  private final Set<FixedFeedInInput> fixedFeedIns;
  private final Set<HpInput> heatPumps;
  private final Set<AcInput> airConditions;
  private final Set<LoadInput> loads;
  private final Set<PvInput> pvPlants;
  private final Set<StorageInput> storages;
  private final Set<WecInput> wecPlants;

  public ExtendedSystemParticipants(
      Set<BmInput> bmPlants,
      Set<ChpInput> chpPlants,
      Set<EvcsInput> evcs,
      Set<FixedFeedInInput> fixedFeedIns,
      Set<HpInput> heatPumps,
      Set<AcInput> airConditions,
      Set<LoadInput> loads,
      Set<PvInput> pvPlants,
      Set<StorageInput> storages,
      Set<WecInput> wecPlants) {
    this.bmPlants = bmPlants;
    this.chpPlants = chpPlants;
    this.evcs = evcs;
    this.fixedFeedIns = fixedFeedIns;
    this.heatPumps = heatPumps;
    this.airConditions = airConditions;
    this.loads = loads;
    this.pvPlants = pvPlants;
    this.storages = storages;
    this.wecPlants = wecPlants;
  }

  /**
   * Combine different already existing containers.
   *
   * @param extendedSystemParticipants already existing containers
   */
  public ExtendedSystemParticipants(
      Collection<ExtendedSystemParticipants> extendedSystemParticipants) {
    this.bmPlants =
        extendedSystemParticipants.stream()
            .flatMap(p -> p.bmPlants.stream())
            .collect(Collectors.toSet());
    this.chpPlants =
        extendedSystemParticipants.stream()
            .flatMap(p -> p.chpPlants.stream())
            .collect(Collectors.toSet());
    this.evcs =
        extendedSystemParticipants.stream()
            .flatMap(p -> p.evcs.stream())
            .collect(Collectors.toSet());
    this.fixedFeedIns =
        extendedSystemParticipants.stream()
            .flatMap(p -> p.fixedFeedIns.stream())
            .collect(Collectors.toSet());
    this.heatPumps =
        extendedSystemParticipants.stream()
            .flatMap(p -> p.heatPumps.stream())
            .collect(Collectors.toSet());
    this.airConditions =
        extendedSystemParticipants.stream()
            .flatMap(p -> p.airConditions.stream())
            .collect(Collectors.toSet());
    this.loads =
        extendedSystemParticipants.stream()
            .flatMap(p -> p.loads.stream())
            .collect(Collectors.toSet());
    this.pvPlants =
        extendedSystemParticipants.stream()
            .flatMap(p -> p.pvPlants.stream())
            .collect(Collectors.toSet());
    this.storages =
        extendedSystemParticipants.stream()
            .flatMap(p -> p.storages.stream())
            .collect(Collectors.toSet());
    this.wecPlants =
        extendedSystemParticipants.stream()
            .flatMap(p -> p.wecPlants.stream())
            .collect(Collectors.toSet());
  }

  /**
   * Create an instance based on a list of {@link SystemParticipantInput} entities.
   *
   * @param systemParticipants list of system participants this container instance should be created
   *     from
   */
  public ExtendedSystemParticipants(List<SystemParticipantInput> systemParticipants) {
    this.bmPlants =
        systemParticipants.parallelStream()
            .filter(BmInput.class::isInstance)
            .map(BmInput.class::cast)
            .collect(Collectors.toSet());
    this.chpPlants =
        systemParticipants.parallelStream()
            .filter(ChpInput.class::isInstance)
            .map(ChpInput.class::cast)
            .collect(Collectors.toSet());
    this.evcs =
        systemParticipants.parallelStream()
            .filter(EvcsInput.class::isInstance)
            .map(EvcsInput.class::cast)
            .collect(Collectors.toSet());
    this.fixedFeedIns =
        systemParticipants.parallelStream()
            .filter(FixedFeedInInput.class::isInstance)
            .map(FixedFeedInInput.class::cast)
            .collect(Collectors.toSet());
    this.heatPumps =
        systemParticipants.parallelStream()
            .filter(HpInput.class::isInstance)
            .map(HpInput.class::cast)
            .collect(Collectors.toSet());
    this.airConditions =
        systemParticipants.parallelStream()
            .filter(AcInput.class::isInstance)
            .map(AcInput.class::cast)
            .collect(Collectors.toSet());
    this.loads =
        systemParticipants.parallelStream()
            .filter(LoadInput.class::isInstance)
            .map(LoadInput.class::cast)
            .collect(Collectors.toSet());
    this.pvPlants =
        systemParticipants.parallelStream()
            .filter(PvInput.class::isInstance)
            .map(PvInput.class::cast)
            .collect(Collectors.toSet());
    this.storages =
        systemParticipants.parallelStream()
            .filter(StorageInput.class::isInstance)
            .map(StorageInput.class::cast)
            .collect(Collectors.toSet());
    this.wecPlants =
        systemParticipants.parallelStream()
            .filter(WecInput.class::isInstance)
            .map(WecInput.class::cast)
            .collect(Collectors.toSet());
  }

  @Override
  public final List<SystemParticipantInput> allEntitiesAsList() {
    List<SystemParticipantInput> allEntities = new ArrayList<>();
    allEntities.addAll(bmPlants);
    allEntities.addAll(chpPlants);
    allEntities.addAll(evcs);
    allEntities.addAll(fixedFeedIns);
    allEntities.addAll(heatPumps);
    allEntities.addAll(airConditions);
    allEntities.addAll(loads);
    allEntities.addAll(pvPlants);
    allEntities.addAll(storages);
    allEntities.addAll(wecPlants);
    return Collections.unmodifiableList(allEntities);
  }

  public ExtendedSystemParticipantsCopyBuilder copy() {
    return new ExtendedSystemParticipantsCopyBuilder(this);
  }

  /**
   * @return unmodifiable Set of all biomass plants in this grid
   */
  public Set<BmInput> getBmPlants() {
    return Collections.unmodifiableSet(bmPlants);
  }

  /**
   * @return unmodifiable Set of all CHP plants in this grid
   */
  public Set<ChpInput> getChpPlants() {
    return Collections.unmodifiableSet(chpPlants);
  }

  /**
   * @return unmodifiable Set of all EV charging stations in this grid
   */
  public Set<EvcsInput> getEvcs() {
    return Collections.unmodifiableSet(evcs);
  }

  /**
   * @return unmodifiable Set of all fixed feed in this grid
   */
  public Set<FixedFeedInInput> getFixedFeedIns() {
    return Collections.unmodifiableSet(fixedFeedIns);
  }

  /**
   * @return unmodifiable Set of all heat pumps in this grid
   */
  public Set<HpInput> getHeatPumps() {
    return Collections.unmodifiableSet(heatPumps);
  }

  /**
   * @return unmodifiable Set of all air conditions in this grid
   */
  public Set<AcInput> getAirConditions() {
    return Collections.unmodifiableSet(airConditions);
  }

  /**
   * @return unmodifiable Set of all loads in this grid
   */
  public Set<LoadInput> getLoads() {
    return Collections.unmodifiableSet(loads);
  }

  /**
   * @return unmodifiable Set of all PV plants in this grid
   */
  public Set<PvInput> getPvPlants() {
    return Collections.unmodifiableSet(pvPlants);
  }

  /**
   * @return unmodifiable Set of all storages in this grid
   */
  public Set<StorageInput> getStorages() {
    return Collections.unmodifiableSet(storages);
  }

  /**
   * @return unmodifiable Set of all WECs in this grid
   */
  public Set<WecInput> getWecPlants() {
    return Collections.unmodifiableSet(wecPlants);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ExtendedSystemParticipants that)) return false;
    return Objects.equals(bmPlants, that.bmPlants)
        && Objects.equals(chpPlants, that.chpPlants)
        && Objects.equals(evcs, that.evcs)
        && Objects.equals(fixedFeedIns, that.fixedFeedIns)
        && Objects.equals(heatPumps, that.heatPumps)
        && Objects.equals(airConditions, that.airConditions)
        && Objects.equals(loads, that.loads)
        && Objects.equals(pvPlants, that.pvPlants)
        && Objects.equals(storages, that.storages)
        && Objects.equals(wecPlants, that.wecPlants);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        bmPlants,
        chpPlants,
        evcs,
        fixedFeedIns,
        heatPumps,
        airConditions,
        loads,
        pvPlants,
        storages,
        wecPlants);
  }

  /**
   * A builder pattern based approach to create copies of {@link ExtendedSystemParticipants}
   * containers with altered field values.
   *
   * @since 1.0
   */
  public static class ExtendedSystemParticipantsCopyBuilder
      extends InputContainerCopyBuilder<SystemParticipantInput> {
    private Set<BmInput> bmPlants;
    private Set<ChpInput> chpPlants;
    private Set<EvcsInput> evcs;
    private Set<FixedFeedInInput> fixedFeedIns;
    private Set<HpInput> heatPumps;
    private Set<AcInput> airConditions;
    private Set<LoadInput> loads;
    private Set<PvInput> pvPlants;
    private Set<StorageInput> storages;
    private Set<WecInput> wecPlants;

    protected ExtendedSystemParticipantsCopyBuilder(
        ExtendedSystemParticipants extendedSystemParticipants) {
      this.bmPlants = extendedSystemParticipants.bmPlants;
      this.chpPlants = extendedSystemParticipants.chpPlants;
      this.evcs = extendedSystemParticipants.evcs;
      this.fixedFeedIns = extendedSystemParticipants.fixedFeedIns;
      this.heatPumps = extendedSystemParticipants.heatPumps;
      this.airConditions = extendedSystemParticipants.airConditions;
      this.loads = extendedSystemParticipants.loads;
      this.pvPlants = extendedSystemParticipants.pvPlants;
      this.storages = extendedSystemParticipants.storages;
      this.wecPlants = extendedSystemParticipants.wecPlants;
    }

    public ExtendedSystemParticipantsCopyBuilder bmPlants(Set<BmInput> bmPlants) {
      this.bmPlants = bmPlants;
      return thisInstance();
    }

    public ExtendedSystemParticipantsCopyBuilder chpPlants(Set<ChpInput> chpPlants) {
      this.chpPlants = chpPlants;
      return thisInstance();
    }

    public ExtendedSystemParticipantsCopyBuilder evcs(Set<EvcsInput> evcs) {
      this.evcs = evcs;
      return thisInstance();
    }

    public ExtendedSystemParticipantsCopyBuilder fixedFeedIns(Set<FixedFeedInInput> fixedFeedIns) {
      this.fixedFeedIns = fixedFeedIns;
      return thisInstance();
    }

    public ExtendedSystemParticipantsCopyBuilder heatPumps(Set<HpInput> heatPumps) {
      this.heatPumps = heatPumps;
      return thisInstance();
    }

    public ExtendedSystemParticipantsCopyBuilder airConditions(Set<AcInput> airConditions) {
      this.airConditions = airConditions;
      return thisInstance();
    }

    public ExtendedSystemParticipantsCopyBuilder loads(Set<LoadInput> loads) {
      this.loads = loads;
      return thisInstance();
    }

    public ExtendedSystemParticipantsCopyBuilder pvPlants(Set<PvInput> pvPlants) {
      this.pvPlants = pvPlants;
      return thisInstance();
    }

    public ExtendedSystemParticipantsCopyBuilder storages(Set<StorageInput> storages) {
      this.storages = storages;
      return thisInstance();
    }

    public ExtendedSystemParticipantsCopyBuilder wecPlants(Set<WecInput> wecPlants) {
      this.wecPlants = wecPlants;
      return thisInstance();
    }

    @Override
    protected ExtendedSystemParticipantsCopyBuilder thisInstance() {
      return this;
    }

    @Override
    public ExtendedSystemParticipants build() {
      return new ExtendedSystemParticipants(
          bmPlants,
          chpPlants,
          evcs,
          fixedFeedIns,
          heatPumps,
          airConditions,
          loads,
          pvPlants,
          storages,
          wecPlants);
    }
  }
}
