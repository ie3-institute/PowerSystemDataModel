/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.input.EvInput;
import edu.ie3.datamodel.models.input.UniqueInputEntity;
import java.util.*;

public abstract class GridContainer implements InputContainer<UniqueInputEntity> {
  /** Name of this grid */
  protected final String gridName;

  /** Accumulated raw grid elements (lines, nodes, transformers, switches) */
  protected final RawGridElements rawGrid;

  /** Accumulated system participant elements */
  protected final SystemParticipants systemParticipants;

  /** Accumulated energy management units */
  protected final EnergyManagementUnits emUnits;

  // ** Accumulated electric vehicles */
  protected final Set<EvInput> evs;

  protected GridContainer(
      String gridName,
      RawGridElements rawGrid,
      SystemParticipants systemParticipants,
      EnergyManagementUnits emUnits,
      Set<EvInput> evs) {
    this.gridName = gridName;

    this.rawGrid = rawGrid;
    this.systemParticipants = systemParticipants;
    this.emUnits = emUnits;
    this.evs = evs;
  }

  @Override
  public List<UniqueInputEntity> allEntitiesAsList() {
    List<UniqueInputEntity> allEntities = new LinkedList<>();
    allEntities.addAll(rawGrid.allEntitiesAsList());
    rawGrid.getCableDeploymentsByLine().values().stream()
        .flatMap(java.util.Collection::stream)
        .forEach(allEntities::add);
    allEntities.addAll(systemParticipants.allEntitiesAsList());
    allEntities.addAll(emUnits.allEntitiesAsList());
    allEntities.addAll(evs);
    return Collections.unmodifiableList(allEntities);
  }

  /**
   * @return true, as we are positive people and believe in what we do. Just kidding. Checks are
   *     made during initialization.
   */
  public String getGridName() {
    return gridName;
  }

  public RawGridElements getRawGrid() {
    return rawGrid;
  }

  public SystemParticipants getSystemParticipants() {
    return systemParticipants;
  }

  public EnergyManagementUnits getEmUnits() {
    return emUnits;
  }

  public Set<EvInput> getEvs() {
    return Collections.unmodifiableSet(evs);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GridContainer that)) return false;
    return gridName.equals(that.gridName)
        && rawGrid.equals(that.rawGrid)
        && systemParticipants.equals(that.systemParticipants)
        && emUnits.equals(that.emUnits)
        && evs.equals(that.evs);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gridName, rawGrid, systemParticipants, emUnits, evs);
  }

  @Override
  public String toString() {
    return "GridContainer{" + "gridName='" + gridName + '\'' + '}';
  }

  /**
   * Abstract class for all builder that build child containers of abstract class {@link
   * GridContainer}
   *
   * @version 3.1
   * @since 14.02.23
   */
  protected abstract static class GridContainerCopyBuilder<B extends GridContainerCopyBuilder<B>>
      extends InputContainerCopyBuilder<UniqueInputEntity> {
    private String gridName;
    private RawGridElements rawGrid;
    private SystemParticipants systemParticipants;
    private EnergyManagementUnits emUnits;
    private Set<EvInput> evs;

    /**
     * Constructor for {@link GridContainerCopyBuilder}.
     *
     * @param gridContainer instance of {@link GridContainerCopyBuilder}
     */
    protected GridContainerCopyBuilder(GridContainer gridContainer) {
      this.gridName = gridContainer.getGridName();
      this.rawGrid = gridContainer.getRawGrid();
      this.systemParticipants = gridContainer.getSystemParticipants();
      this.emUnits = gridContainer.getEmUnits();
      this.evs = gridContainer.getEvs();
    }

    /** Returns grid name */
    protected String getGridName() {
      return gridName;
    }

    /** Returns {@link RawGridElements}. */
    protected RawGridElements getRawGrid() {
      return rawGrid;
    }

    /** Returns {@link SystemParticipants} */
    protected SystemParticipants getSystemParticipants() {
      return systemParticipants;
    }

    /** Returns {@link EnergyManagementUnits} */
    public EnergyManagementUnits getEmUnits() {
      return emUnits;
    }

    /** Returns {@link EvInput} set */
    public Set<EvInput> getEvs() {
      return evs;
    }

    /**
     * Method to alter the grid name.
     *
     * @param gridName altered grid name
     * @return this instance of {@link GridContainerCopyBuilder}
     */
    public B gridName(String gridName) {
      this.gridName = gridName;
      return thisInstance();
    }

    /**
     * Method to alter the {@link RawGridElements}
     *
     * @param rawGrid altered raw grid
     * @return this instance of {@link GridContainerCopyBuilder}
     */
    public B rawGrid(RawGridElements rawGrid) {
      this.rawGrid = rawGrid;
      return thisInstance();
    }

    /**
     * Method to alter the {@link SystemParticipants}.
     *
     * @param systemParticipants altered systemParticipants
     * @return this instance of {@link GridContainerCopyBuilder}
     */
    public B systemParticipants(SystemParticipants systemParticipants) {
      this.systemParticipants = systemParticipants;
      return thisInstance();
    }

    /**
     * Method to alter the {@link EnergyManagementUnits}s.
     *
     * @param emUnits altered em units
     * @return this instance of {@link GridContainerCopyBuilder}
     */
    public B emUnits(EnergyManagementUnits emUnits) {
      this.emUnits = emUnits;
      return thisInstance();
    }

    /** Returns the current instance of builder with the correct subclass type */
    protected abstract B thisInstance();
  }
}
