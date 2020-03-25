/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.EvcsInput;
import edu.ie3.datamodel.models.input.system.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Represents the accumulation of system participant elements (BM plants, CHP plants, EVCS, fixed
 * feed ins, heat pumps, loads, PV plants, storages, WECs)
 */
public class SystemParticipants implements InputContainer {
  private final Set<BmInput> bmPlants;
  private final Set<ChpInput> chpPlants;
  private final Set<EvcsInput> evCS;
  private final Set<FixedFeedInInput> fixedFeedIns;
  private final Set<HpInput> heatPumps;
  private final Set<LoadInput> loads;
  private final Set<PvInput> pvPlants;
  private final Set<StorageInput> storages;
  private final Set<WecInput> wecPlants;

  public SystemParticipants(
      Set<BmInput> bmPlants,
      Set<ChpInput> chpPlants,
      Set<EvcsInput> evCS,
      Set<FixedFeedInInput> fixedFeedIns,
      Set<HpInput> heatPumps,
      Set<LoadInput> loads,
      Set<PvInput> pvPlants,
      Set<StorageInput> storages,
      Set<WecInput> wecPlants) {
    this.bmPlants = bmPlants;
    this.chpPlants = chpPlants;
    this.evCS = evCS;
    this.fixedFeedIns = fixedFeedIns;
    this.heatPumps = heatPumps;
    this.loads = loads;
    this.pvPlants = pvPlants;
    this.storages = storages;
    this.wecPlants = wecPlants;
  }

  /**
   * Combine different already existing containers
   *
   * @param systemParticipants Already existing containers
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
  }

  @Override
  public List<UniqueEntity> allEntitiesAsList() {
    List<UniqueEntity> allEntities = new ArrayList<>();
    allEntities.addAll(bmPlants);
    allEntities.addAll(chpPlants);
    allEntities.addAll(evCS);
    allEntities.addAll(fixedFeedIns);
    allEntities.addAll(heatPumps);
    allEntities.addAll(loads);
    allEntities.addAll(pvPlants);
    allEntities.addAll(storages);
    allEntities.addAll(wecPlants);
    return Collections.unmodifiableList(allEntities);
  }

  @Override
  public void validate() {
    throw new UnsupportedOperationException(
        "Currently there are no tests for system participants in ValidationUtils.");
  }

  public void add(BmInput bm) {
    bmPlants.add(bm);
  }

  public void add(ChpInput chp) {
    chpPlants.add(chp);
  }

  public void add(EvcsInput evcsInput) {
    evCS.add(evcsInput);
  }

  public void add(FixedFeedInInput fixedFeedIn) {
    fixedFeedIns.add(fixedFeedIn);
  }

  public void add(HpInput hp) {
    heatPumps.add(hp);
  }

  public void add(LoadInput load) {
    loads.add(load);
  }

  public void add(PvInput pv) {
    pvPlants.add(pv);
  }

  public void add(StorageInput storage) {
    this.storages.add(storage);
  }

  public void add(WecInput wec) {
    wecPlants.add(wec);
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
  /** @return unmodifiable Set of all fixed feed in in this grid */
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SystemParticipants that = (SystemParticipants) o;
    return bmPlants.equals(that.bmPlants)
        && chpPlants.equals(that.chpPlants)
        && evCS.equals(that.evCS)
        && fixedFeedIns.equals(that.fixedFeedIns)
        && heatPumps.equals(that.heatPumps)
        && loads.equals(that.loads)
        && pvPlants.equals(that.pvPlants)
        && storages.equals(that.storages)
        && wecPlants.equals(that.wecPlants);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        bmPlants, chpPlants, evCS, fixedFeedIns, heatPumps, loads, pvPlants, storages, wecPlants);
  }
}
