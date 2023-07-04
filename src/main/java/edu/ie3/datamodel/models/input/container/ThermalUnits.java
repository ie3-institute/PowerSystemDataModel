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
  public ThermalUnitsCopyBuilder copy() {
    return new ThermalUnitsCopyBuilder(this);
  }

  @Override
  public String toString() {
    return "ThermalUnits{" + "#houses=" + houses.size() + ", #storages=" + storages.size() + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link ThermalUnits} containers with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * ThermalUnits}
   *
   * @version 3.1
   * @since 14.02.23
   */
  public static class ThermalUnitsCopyBuilder
      extends InputContainerCopyBuilder<ThermalUnitInput, ThermalUnits> {
    private Set<ThermalHouseInput> houses;
    private Set<ThermalStorageInput> storages;

    /**
     * Constructor for {@link ThermalUnitsCopyBuilder}
     *
     * @param thermalUnits instance of {@link ThermalUnits}
     */
    protected ThermalUnitsCopyBuilder(ThermalUnits thermalUnits) {
      super();
      this.houses = thermalUnits.houses();
      this.storages = thermalUnits.storages();
    }

    /**
     * Method to alter {@link ThermalHouseInput}
     *
     * @param houses altered thermal houses
     * @return child instance of {@link ThermalUnitsCopyBuilder}
     */
    public ThermalUnitsCopyBuilder houses(Set<ThermalHouseInput> houses) {
      this.houses = houses;
      return childInstance();
    }

    /**
     * Method to alter {@link ThermalStorageInput}
     *
     * @param storages altered thermal storages
     * @return child instance of {@link ThermalUnitsCopyBuilder}
     */
    public ThermalUnitsCopyBuilder storages(Set<ThermalStorageInput> storages) {
      this.storages = storages;
      return childInstance();
    }

    @Override
    protected ThermalUnitsCopyBuilder childInstance() {
      return this;
    }

    @Override
    ThermalUnits build() {
      return new ThermalUnits(houses, storages);
    }
  }
}
