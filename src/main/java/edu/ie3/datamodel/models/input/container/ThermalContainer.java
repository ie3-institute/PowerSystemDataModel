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

public class ThermalContainer implements InputContainer<ThermalUnitInput> {

  private final Set<ThermalHouseInput> houses;

  private final Set<ThermalStorageInput> storages;

  /**
   * Container to group together all {@link ThermalUnitInput}s
   *
   * @param houses Available houses
   * @param storages Available storage
   */
  public ThermalContainer(
      Collection<ThermalHouseInput> houses, Collection<ThermalStorageInput> storages) {
    this.houses = new HashSet<>(houses);
    this.storages = new HashSet<>(storages);
  }

  public Set<ThermalHouseInput> getHouses() {
    return houses;
  }

  public Set<ThermalStorageInput> getStorages() {
    return storages;
  }

  @Override
  public List<ThermalUnitInput> allEntitiesAsList() {
    List<ThermalUnitInput> ret = new ArrayList<>(houses.size() + storages.size());
    ret.addAll(houses);
    ret.addAll(storages);
    return ret;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ThermalContainer that = (ThermalContainer) o;
    return houses.equals(that.houses) && storages.equals(that.storages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(houses, storages);
  }
}
