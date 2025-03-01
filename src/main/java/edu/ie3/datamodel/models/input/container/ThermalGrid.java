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
import java.util.stream.Collectors;

/**
 * Container object to denote a fully connected thermal "grid". As there are currently no branch
 * elements, a grid always only consists of one {@link ThermalBusInput} and all its connected {@link
 * edu.ie3.datamodel.models.input.thermal.ThermalUnitInput}s
 */
public record ThermalGrid(
    ThermalBusInput bus,
    Set<ThermalHouseInput> houses,
    Set<ThermalStorageInput> heatStorages,
    Set<ThermalStorageInput> domesticHotWaterStorages)
    implements InputContainer<ThermalInput> {
  public ThermalGrid(
      ThermalBusInput bus,
      Collection<ThermalHouseInput> houses,
      Collection<ThermalStorageInput> heatStorages,
      Collection<ThermalStorageInput> domesticHotWaterStorages) {
    this(
        bus,
        new HashSet<>(houses),
        new HashSet<>(heatStorages),
        new HashSet<>(domesticHotWaterStorages));
  }

  @Override
  public List<ThermalInput> allEntitiesAsList() {
    List<ThermalInput> ret =
        new ArrayList<>(houses.size() + heatStorages.size() + domesticHotWaterStorages.size() + 1);
    ret.add(bus);
    ret.addAll(houses);
    ret.addAll(heatStorages);
    ret.addAll(domesticHotWaterStorages);
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
        + ", #heatStorages="
        + heatStorages.size()
        + ", #domesticHotWaterStorages="
        + domesticHotWaterStorages.size()
        + '}';
  }

  /**
   * A builder pattern based approach to create copies of {@link ThermalGrid} containers with
   * altered field values. For detailed field descriptions refer to java docs of {@link ThermalGrid}
   *
   * @version 3.1
   * @since 14.02.23
   */
  public static class ThermalGridCopyBuilder implements InputContainerCopyBuilder<ThermalInput> {
    private ThermalBusInput bus;
    private Set<ThermalHouseInput> houses;
    private Set<ThermalStorageInput> heatStorages;
    private Set<ThermalStorageInput> domesticHotWaterStorages;

    /**
     * Constructor for {@link ThermalGridCopyBuilder}
     *
     * @param thermalGrid instance of {@link ThermalGrid}
     */
    protected ThermalGridCopyBuilder(ThermalGrid thermalGrid) {
      this.bus = thermalGrid.bus();
      this.houses = thermalGrid.houses();
      this.heatStorages = thermalGrid.heatStorages();
      this.domesticHotWaterStorages = thermalGrid.domesticHotWaterStorages();
    }

    /**
     * Method to alter {@link ThermalBusInput}
     *
     * @param bus altered thermal bus
     * @return this instance of {@link ThermalGridCopyBuilder}
     */
    public ThermalGridCopyBuilder bus(ThermalBusInput bus) {
      this.bus = bus;
      return this;
    }

    /**
     * Method to alter {@link ThermalHouseInput}
     *
     * @param houses altered thermal houses
     * @return this instance of {@link ThermalGridCopyBuilder}
     */
    public ThermalGridCopyBuilder houses(Set<ThermalHouseInput> houses) {
      this.houses = houses;
      return this;
    }

    /**
     * Method to alter {@link ThermalStorageInput}
     *
     * @param heatStorages altered thermal storages
     * @return this instance of {@link ThermalGridCopyBuilder}
     */
    public ThermalGridCopyBuilder heatStorages(Set<ThermalStorageInput> heatStorages) {
      this.heatStorages = heatStorages;
      return this;
    }

    /**
     * Method to alter {@link ThermalStorageInput}
     *
     * @param domesticHotWaterStorages altered thermal storages
     * @return this instance of {@link ThermalGridCopyBuilder}
     */
    public ThermalGridCopyBuilder domesticHotWaterStorages(
        Set<ThermalStorageInput> domesticHotWaterStorages) {
      this.domesticHotWaterStorages = domesticHotWaterStorages;
      return this;
    }

    public ThermalGridCopyBuilder scale(Double factor) {
      houses(
          houses.stream()
              .map(house -> house.copy().scale(factor).build())
              .collect(Collectors.toSet()));
      heatStorages(
          heatStorages.stream()
              .map(storage -> storage.copy().scale(factor).build())
              .collect(Collectors.toSet()));
      domesticHotWaterStorages(
          domesticHotWaterStorages.stream()
              .map(storage -> storage.copy().scale(factor).build())
              .collect(Collectors.toSet()));
      return this;
    }

    @Override
    public ThermalGrid build() {
      return new ThermalGrid(bus, houses, heatStorages, domesticHotWaterStorages);
    }
  }
}
