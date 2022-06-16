/*
 * © 2022. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.input.thermal.ThermalBusInput;
import edu.ie3.datamodel.models.input.thermal.ThermalHouseInput;
import edu.ie3.datamodel.models.input.thermal.ThermalInput;
import edu.ie3.datamodel.models.input.thermal.ThermalStorageInput;
import java.util.*;

/**
 * Container object to denote a fully connected thermal "grid". As there are currently no branch
 * elements, a grid always only consists of one {@link ThermalBusInput} and all its connected {@link
 * edu.ie3.datamodel.models.input.thermal.ThermalUnitInput}s
 */
public class ThermalGrid implements InputContainer<ThermalInput> {
  private final ThermalBusInput bus;
  private final Set<ThermalHouseInput> houses;
  private final Set<ThermalStorageInput> storages;

  public ThermalGrid(
      ThermalBusInput bus,
      Collection<ThermalHouseInput> houses,
      Collection<ThermalStorageInput> storages) {
    this.bus = bus;
    this.houses = new HashSet<>(houses);
    this.storages = new HashSet<>(storages);
  }

  public ThermalBusInput getBus() {
    return bus;
  }

  public Set<ThermalHouseInput> getHouses() {
    return houses;
  }

  public Set<ThermalStorageInput> getStorages() {
    return storages;
  }

  @Override
  public List<ThermalInput> allEntitiesAsList() {
    return null;
  }

  @Override
  public String toString() {
    return "ThermalGrid{"
        + "bus="
        + bus
        + ", #houses="
        + houses.size()
        + ", #storages="
        + storages.size()
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    ThermalGrid that = (ThermalGrid) o;
    return bus.equals(that.bus) && houses.equals(that.houses) && storages.equals(that.storages);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bus, houses, storages);
  }
}
