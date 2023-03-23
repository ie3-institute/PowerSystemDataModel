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
  public ThermalGridCopyBuilder copy() {
    return new ThermalGridCopyBuilder(this);
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

  /**
   * A builder pattern based approach to create copies of {@link ThermalGrid} containers with
   * altered field values. For detailed field descriptions refer to java docs of {@link ThermalGrid}
   *
   * @version 3.1
   * @since 14.02.23
   */
  public static class ThermalGridCopyBuilder
      extends InputContainerCopyBuilder<ThermalInput, ThermalGrid> {
    private ThermalBusInput bus;
    private Set<ThermalHouseInput> houses;
    private Set<ThermalStorageInput> storages;

    /**
     * Constructor for {@link ThermalGridCopyBuilder}
     *
     * @param thermalGrid instance of {@link ThermalGrid}
     */
    protected ThermalGridCopyBuilder(ThermalGrid thermalGrid) {
      super();
      this.bus = thermalGrid.bus();
      this.houses = thermalGrid.houses();
      this.storages = thermalGrid.storages();
    }

    /**
     * Method to alter {@link ThermalBusInput}
     *
     * @param bus altered thermal bus
     * @return child instance of {@link ThermalGridCopyBuilder}
     */
    public ThermalGridCopyBuilder bus(ThermalBusInput bus) {
      this.bus = bus;
      return childInstance();
    }

    /**
     * Method to alter {@link ThermalHouseInput}
     *
     * @param houses altered thermal houses
     * @return child instance of {@link ThermalGridCopyBuilder}
     */
    public ThermalGridCopyBuilder houses(Set<ThermalHouseInput> houses) {
      this.houses = houses;
      return childInstance();
    }

    /**
     * Method to alter {@link ThermalStorageInput}
     *
     * @param storages altered thermal storages
     * @return child instance of {@link ThermalGridCopyBuilder}
     */
    public ThermalGridCopyBuilder storages(Set<ThermalStorageInput> storages) {
      this.storages = storages;
      return childInstance();
    }

    @Override
    protected ThermalGridCopyBuilder childInstance() {
      return this;
    }

    @Override
    ThermalGrid build() {
      return new ThermalGrid(bus, houses, storages);
    }
  }
}
