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
 * Represents the accumulation of system participant elements (BM plants, CHP plants, EVCS, fixed
 * feed ins, heat pumps, loads, PV plants, storages, WECs)
 */
public class SystemParticipants implements InputContainer<SystemParticipantInput> {
  private final Set<BmInput> bmPlants;
  private final Set<ChpInput> chpPlants;
  private final Set<EvcsInput> evCS;
  private final Set<EvInput> evs;
  private final Set<FixedFeedInInput> fixedFeedIns;
  private final Set<HpInput> heatPumps;
  private final Set<LoadInput> loads;
  private final Set<PvInput> pvPlants;
  private final Set<StorageInput> storages;
  private final Set<WecInput> wecPlants;
  private final Set<EmInput> emSystems;

  public SystemParticipants(
      Set<BmInput> bmPlants,
      Set<ChpInput> chpPlants,
      Set<EvcsInput> evCS,
      Set<EvInput> evs,
      Set<FixedFeedInInput> fixedFeedIns,
      Set<HpInput> heatPumps,
      Set<LoadInput> loads,
      Set<PvInput> pvPlants,
      Set<StorageInput> storages,
      Set<WecInput> wecPlants,
      Set<EmInput> emSystems) {
    this.bmPlants = bmPlants;
    this.chpPlants = chpPlants;
    this.evCS = evCS;
    this.evs = evs;
    this.fixedFeedIns = fixedFeedIns;
    this.heatPumps = heatPumps;
    this.loads = loads;
    this.pvPlants = pvPlants;
    this.storages = storages;
    this.wecPlants = wecPlants;
    this.emSystems = emSystems;
  }

  /**
   * Combine different already existing containers
   *
   * @param systemParticipants already existing containers
   */
  public SystemParticipants(Collection<SystemParticipants> systemParticipants) {
    this.bmPlants =
        systemParticipants.stream()
            .flatMap(participants -> participants.bmPlants.stream())
            .collect(Collectors.toSet());
    this.chpPlants =
        systemParticipants.stream()
            .flatMap(participants -> participants.chpPlants.stream())
            .collect(Collectors.toSet());
    this.evCS =
        systemParticipants.stream()
            .flatMap(participants -> participants.evCS.stream())
            .collect(Collectors.toSet());
    this.evs =
        systemParticipants.stream()
            .flatMap(participants -> participants.evs.stream())
            .collect(Collectors.toSet());
    this.fixedFeedIns =
        systemParticipants.stream()
            .flatMap(participants -> participants.fixedFeedIns.stream())
            .collect(Collectors.toSet());
    this.heatPumps =
        systemParticipants.stream()
            .flatMap(participants -> participants.heatPumps.stream())
            .collect(Collectors.toSet());
    this.loads =
        systemParticipants.stream()
            .flatMap(participants -> participants.loads.stream())
            .collect(Collectors.toSet());
    this.pvPlants =
        systemParticipants.stream()
            .flatMap(participants -> participants.pvPlants.stream())
            .collect(Collectors.toSet());
    this.storages =
        systemParticipants.stream()
            .flatMap(participants -> participants.storages.stream())
            .collect(Collectors.toSet());
    this.wecPlants =
        systemParticipants.stream()
            .flatMap(participants -> participants.wecPlants.stream())
            .collect(Collectors.toSet());
    this.emSystems =
        systemParticipants.stream()
            .flatMap(participants -> participants.emSystems.stream())
            .collect(Collectors.toSet());
  }

  /**
   * Create an instance based on a list of {@link SystemParticipantInput} entities
   *
   * @param systemParticipants list of system participants this container instance should be created
   *     from
   */
  public SystemParticipants(List<SystemParticipantInput> systemParticipants) {

    /* init sets */
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
    this.evCS =
        systemParticipants.parallelStream()
            .filter(EvcsInput.class::isInstance)
            .map(EvcsInput.class::cast)
            .collect(Collectors.toSet());
    this.evs =
        systemParticipants.parallelStream()
            .filter(EvInput.class::isInstance)
            .map(EvInput.class::cast)
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
    this.emSystems =
        systemParticipants.parallelStream()
            .filter(EmInput.class::isInstance)
            .map(EmInput.class::cast)
            .collect(Collectors.toSet());
  }

  @Override
  public final List<SystemParticipantInput> allEntitiesAsList() {
    List<SystemParticipantInput> allEntities = new ArrayList<>();
    allEntities.addAll(bmPlants);
    allEntities.addAll(chpPlants);
    allEntities.addAll(evCS);
    allEntities.addAll(evs);
    allEntities.addAll(fixedFeedIns);
    allEntities.addAll(heatPumps);
    allEntities.addAll(loads);
    allEntities.addAll(pvPlants);
    allEntities.addAll(storages);
    allEntities.addAll(wecPlants);
    allEntities.addAll(emSystems);
    return Collections.unmodifiableList(allEntities);
  }

  public SystemParticipantsCopyBuilder copy() {
    return new SystemParticipantsCopyBuilder(this);
  }

  /** @return unmodifiable Set of all biomass plants in this grid */
  public Set<BmInput> getBmPlants() {
    return Collections.unmodifiableSet(bmPlants);
  }

  /** @return unmodifiable Set of all CHP plants in this grid */
  public Set<ChpInput> getChpPlants() {
    return Collections.unmodifiableSet(chpPlants);
  }

  /** @return unmodifiable Set of all ev charging stations in this grid */
  public Set<EvcsInput> getEvCS() {
    return Collections.unmodifiableSet(evCS);
  }

  /** @return unmodifiable Set of all electric vehicles in this grid */
  public Set<EvInput> getEvs() {
    return evs;
  }

  /** @return unmodifiable Set of all fixed feed in this grid */
  public Set<FixedFeedInInput> getFixedFeedIns() {
    return Collections.unmodifiableSet(fixedFeedIns);
  }

  /** @return unmodifiable Set of all heat pumps in this grid */
  public Set<HpInput> getHeatPumps() {
    return Collections.unmodifiableSet(heatPumps);
  }

  /** @return unmodifiable Set of all loads in this grid */
  public Set<LoadInput> getLoads() {
    return Collections.unmodifiableSet(loads);
  }

  /** @return unmodifiable Set of all PV plants in this grid */
  public Set<PvInput> getPvPlants() {
    return Collections.unmodifiableSet(pvPlants);
  }

  /** @return unmodifiable Set of all storages in this grid */
  public Set<StorageInput> getStorages() {
    return Collections.unmodifiableSet(storages);
  }

  /** @return unmodifiable Set of all WECs in this grid */
  public Set<WecInput> getWecPlants() {
    return Collections.unmodifiableSet(wecPlants);
  }

  public Set<EmInput> getEmSystems() {
    return Collections.unmodifiableSet(emSystems);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof SystemParticipants that)) return false;
    return Objects.equals(bmPlants, that.bmPlants)
        && Objects.equals(chpPlants, that.chpPlants)
        && Objects.equals(evCS, that.evCS)
        && Objects.equals(evs, that.evs)
        && Objects.equals(fixedFeedIns, that.fixedFeedIns)
        && Objects.equals(heatPumps, that.heatPumps)
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
        evCS,
        evs,
        fixedFeedIns,
        heatPumps,
        loads,
        pvPlants,
        storages,
        wecPlants);
  }

  /**
   * A builder pattern based approach to create copies of {@link SystemParticipants} containers with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * SystemParticipants}
   *
   * @version 3.1
   * @since 14.02.23
   */
  public static class SystemParticipantsCopyBuilder
      implements InputContainerCopyBuilder<SystemParticipantInput> {
    private Set<BmInput> bmPlants;
    private Set<ChpInput> chpPlants;
    private Set<EvcsInput> evCS;
    private Set<EvInput> evs;
    private Set<FixedFeedInInput> fixedFeedIns;
    private Set<HpInput> heatPumps;
    private Set<LoadInput> loads;
    private Set<PvInput> pvPlants;
    private Set<StorageInput> storages;
    private Set<WecInput> wecPlants;
    private Set<EmInput> emSystems;

    /**
     * Constructor for {@link SystemParticipantsCopyBuilder}
     *
     * @param systemParticipants instance of {@link SystemParticipants}
     */
    protected SystemParticipantsCopyBuilder(SystemParticipants systemParticipants) {
      this.bmPlants = systemParticipants.bmPlants;
      this.chpPlants = systemParticipants.chpPlants;
      this.evCS = systemParticipants.evCS;
      this.evs = systemParticipants.evs;
      this.fixedFeedIns = systemParticipants.fixedFeedIns;
      this.heatPumps = systemParticipants.heatPumps;
      this.loads = systemParticipants.loads;
      this.pvPlants = systemParticipants.pvPlants;
      this.storages = systemParticipants.storages;
      this.wecPlants = systemParticipants.wecPlants;
      this.emSystems = systemParticipants.emSystems;
    }

    /**
     * Method to alter {@link BmInput}
     *
     * @param bmPlants set of altered biomass plants
     * @return this instance of {@link SystemParticipantsCopyBuilder}
     */
    public SystemParticipantsCopyBuilder bmPlants(Set<BmInput> bmPlants) {
      this.bmPlants = bmPlants;
      return this;
    }

    /**
     * Method to alter {@link ChpInput}
     *
     * @param chpPlants set of altered combined heat and power plants
     * @return this instance of {@link SystemParticipantsCopyBuilder}
     */
    public SystemParticipantsCopyBuilder chpPlants(Set<ChpInput> chpPlants) {
      this.chpPlants = chpPlants;
      return this;
    }

    /**
     * Method to alter {@link EvcsInput}
     *
     * @param evCS set of altered biomass electric vehicle charging stations
     * @return this instance of {@link SystemParticipantsCopyBuilder}
     */
    public SystemParticipantsCopyBuilder evCS(Set<EvcsInput> evCS) {
      this.evCS = evCS;
      return this;
    }

    /**
     * Method to alter {@link EvInput}
     *
     * @param evs set of altered electric vehicles
     * @return this instance of {@link SystemParticipantsCopyBuilder}
     */
    public SystemParticipantsCopyBuilder evs(Set<EvInput> evs) {
      this.evs = evs;
      return this;
    }

    /**
     * Method to alter {@link FixedFeedInInput}
     *
     * @param fixedFeedIns set of altered fixed feed in facilities
     * @return this instance of {@link SystemParticipantsCopyBuilder}
     */
    public SystemParticipantsCopyBuilder fixedFeedIn(Set<FixedFeedInInput> fixedFeedIns) {
      this.fixedFeedIns = fixedFeedIns;
      return this;
    }

    /**
     * Method to alter {@link HpInput}
     *
     * @param heatPumps set of altered heat pumps
     * @return this instance of {@link SystemParticipantsCopyBuilder}
     */
    public SystemParticipantsCopyBuilder heatPumps(Set<HpInput> heatPumps) {
      this.heatPumps = heatPumps;
      return this;
    }

    /**
     * Method to alter {@link LoadInput}
     *
     * @param loads set of altered loads
     * @return this instance of {@link SystemParticipantsCopyBuilder}
     */
    public SystemParticipantsCopyBuilder loads(Set<LoadInput> loads) {
      this.loads = loads;
      return this;
    }

    /**
     * Method to alter {@link PvInput}
     *
     * @param pvPlants set of altered photovoltaic power plants
     * @return this instance of {@link SystemParticipantsCopyBuilder}
     */
    public SystemParticipantsCopyBuilder pvPlants(Set<PvInput> pvPlants) {
      this.pvPlants = pvPlants;
      return this;
    }

    /**
     * Method to alter {@link StorageInput}
     *
     * @param storages set of altered electric energy storages
     * @return this instance of {@link SystemParticipantsCopyBuilder}
     */
    public SystemParticipantsCopyBuilder storages(Set<StorageInput> storages) {
      this.storages = storages;
      return this;
    }

    /**
     * Method to alter {@link WecInput}
     *
     * @param wecPlants set of altered wind energy converters
     * @return this instance of {@link SystemParticipantsCopyBuilder}
     */
    public SystemParticipantsCopyBuilder wecPlants(Set<WecInput> wecPlants) {
      this.wecPlants = wecPlants;
      return this;
    }

    /**
     * Method to alter {@link EmInput}
     *
     * @param emSystems set of altered energy management systems
     * @return this instance of {@link SystemParticipantsCopyBuilder}
     */
    public SystemParticipantsCopyBuilder emSystems(Set<EmInput> emSystems) {
      this.emSystems = emSystems;
      return this;
    }

    @Override
    public SystemParticipants build() {
      return new SystemParticipants(
          bmPlants,
          chpPlants,
          evCS,
          evs,
          fixedFeedIns,
          heatPumps,
          loads,
          pvPlants,
          storages,
          wecPlants,
          emSystems);
    }
  }
}
