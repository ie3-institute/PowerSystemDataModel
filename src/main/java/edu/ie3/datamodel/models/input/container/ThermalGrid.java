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
public record ThermalGrid(
    ThermalBusInput bus, Set<ThermalHouseInput> houses, Set<ThermalStorageInput> storages)
    implements InputContainer<ThermalInput> {
  public ThermalGrid(
      ThermalBusInput bus,
      Collection<ThermalHouseInput> houses,
      Collection<ThermalStorageInput> storages) {
    this(bus, new HashSet<>(houses), new HashSet<>(storages));
  }

  @Override
  public List<ThermalInput> allEntitiesAsList() {
    List<ThermalInput> ret = new ArrayList<>(houses.size() + storages.size() + 1);
    ret.add(bus);
    ret.addAll(houses);
    ret.addAll(storages);
    return ret;
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
}