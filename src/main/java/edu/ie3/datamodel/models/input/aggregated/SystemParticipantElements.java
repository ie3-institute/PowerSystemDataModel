/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.aggregated;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.EvcsInput;
import edu.ie3.datamodel.models.input.system.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents the aggregation of system participant elements (BM plants, CHP plants, EVCS, fixed
 * feed ins, heat pumps, loads, PV plants, storages, WECs)
 */
public class SystemParticipantElements implements AggregatedEntities {
  private final List<BmInput> bmPlants = new LinkedList<>();
  private final List<ChpInput> chpPlants = new LinkedList<>();
  private final List<EvcsInput> evCS = new LinkedList<>();
  private final List<FixedFeedInInput> fixedFeedIns = new LinkedList<>();
  private final List<HpInput> heatPumps = new LinkedList<>();
  private final List<LoadInput> loads = new LinkedList<>();
  private final List<PvInput> pvPlants = new LinkedList<>();
  private final List<StorageInput> storages = new LinkedList<>();
  private final List<WecInput> wecPlants = new LinkedList<>();

  @Override
  public void add(UniqueEntity entity) {
    if (entity instanceof BmInput) add((BmInput) entity);
    else if (entity instanceof ChpInput) add((ChpInput) entity);
    else if (entity instanceof EvcsInput) add((EvcsInput) entity);
    else if (entity instanceof FixedFeedInInput) add((FixedFeedInInput) entity);
    else if (entity instanceof HpInput) add((HpInput) entity);
    else if (entity instanceof LoadInput) add((LoadInput) entity);
    else if (entity instanceof PvInput) add((PvInput) entity);
    else if (entity instanceof StorageInput) add((StorageInput) entity);
    else if (entity instanceof WecInput) add((WecInput) entity);
    else
      throw new IllegalArgumentException(
          "Entity type is unknown, cannot add entity [" + entity + "]");
  }

  @Override
  public List<UniqueEntity> allEntitiesAsList() {
    List<UniqueEntity> allEntities = new LinkedList<>();
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
  public boolean areValuesValid() {
    return true; // no check defined in ValidationTools, so noe need for unnecessary instanceofs
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

  /** @return unmodifiable List of all biomass plants in this grid */
  public List<BmInput> getBmPlants() {
    return Collections.unmodifiableList(bmPlants);
  }
  /** @return unmodifiable List of all CHP plants in this grid */
  public List<ChpInput> getChpPlants() {
    return Collections.unmodifiableList(chpPlants);
  }
  /** @return unmodifiable List of all ev charging stations in this grid */
  public List<EvcsInput> getEvCS() {
    return Collections.unmodifiableList(evCS);
  }
  /** @return unmodifiable List of all fixed feed in in this grid */
  public List<FixedFeedInInput> getFixedFeedIns() {
    return Collections.unmodifiableList(fixedFeedIns);
  }
  /** @return unmodifiable List of all heat pumps in this grid */
  public List<HpInput> getHeatPumps() {
    return Collections.unmodifiableList(heatPumps);
  }
  /** @return unmodifiable List of all loads in this grid */
  public List<LoadInput> getLoads() {
    return Collections.unmodifiableList(loads);
  }
  /** @return unmodifiable List of all PV plants in this grid */
  public List<PvInput> getPvPlants() {
    return Collections.unmodifiableList(pvPlants);
  }
  /** @return unmodifiable List of all storages in this grid */
  public List<StorageInput> getStorages() {
    return Collections.unmodifiableList(storages);
  }
  /** @return unmodifiable List of all WECs in this grid */
  public List<WecInput> getWecPlants() {
    return Collections.unmodifiableList(wecPlants);
  }

  @Override
  public String toString() {
    return "SystemParticipantElements{" +
            "bmPlants=" + bmPlants +
            ", chpPlants=" + chpPlants +
            ", evCS=" + evCS +
            ", fixedFeedIns=" + fixedFeedIns +
            ", heatPumps=" + heatPumps +
            ", loads=" + loads +
            ", pvPlants=" + pvPlants +
            ", storages=" + storages +
            ", wecPlants=" + wecPlants +
            '}';
  }
}
