/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import edu.ie3.datamodel.models.input.thermal.ThermalUnitInput;
import java.util.*;

/**
 * Container to group together all {@link ThermalUnitInput}s
 *
 * @param houses Available houses
 * @param storages Available storage
 */
public record ThermalUnits(Set<ThermalHouseInput> houses, Set<ThermalStorageInput> storages)
    implements InputContainer<ThermalUnitInput> {
  public ThermalUnits(
      Collection<ThermalHouseInput> houses, Collection<ThermalStorageInput> storages) {
    this(new HashSet<>(houses), new HashSet<>(storages));
  }

  @Override
  public List<ThermalUnitInput> allEntitiesAsList() {
    List<ThermalUnitInput> ret = new ArrayList<>(houses.size() + storages.size());
    ret.addAll(houses);
    ret.addAll(storages);
    return ret;
  }

  @Override
  public String toString() {
    return "ThermalUnits{" + "#houses=" + houses.size() + ", #storages=" + storages.size() + '}';
  }
}
