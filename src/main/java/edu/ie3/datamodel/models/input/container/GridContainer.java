/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.models.input.InputEntity;
import java.util.*;

public abstract class GridContainer implements InputContainer<InputEntity> {
  /** Name of this grid */
  protected final String gridName;
  /** Accumulated raw grid elements (lines, nodes, transformers, switches) */
  protected final RawGridElements rawGrid;
  /** Accumulated system participant elements */
  protected final SystemParticipants systemParticipants;
  /** Accumulated graphic data entities (node graphics, line graphics) */
  protected final GraphicElements graphics;

  protected GridContainer(
      String gridName,
      RawGridElements rawGrid,
      SystemParticipants systemParticipants,
      GraphicElements graphics) {
    this.gridName = gridName;

    this.rawGrid = rawGrid;
    this.systemParticipants = systemParticipants;
    this.graphics = graphics;
  }

  @Override
  public List<InputEntity> allEntitiesAsList() {
    List<InputEntity> allEntities = new LinkedList<>();
    allEntities.addAll(rawGrid.allEntitiesAsList());
    allEntities.addAll(systemParticipants.allEntitiesAsList());
    allEntities.addAll(graphics.allEntitiesAsList());
    return Collections.unmodifiableList(allEntities);
  }

  /**
   * @return true, as we are positive people and believe in what we do. Just kidding. Checks are
   *     made during initialisation.
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

  public GraphicElements getGraphics() {
    return graphics;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof GridContainer that)) return false;
    return gridName.equals(that.gridName)
        && rawGrid.equals(that.rawGrid)
        && systemParticipants.equals(that.systemParticipants)
        && graphics.equals(that.graphics);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gridName, rawGrid, systemParticipants, graphics);
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
      implements InputContainerCopyBuilder<GridContainer> {
    private String gridName;
    private RawGridElements rawGrid;
    private SystemParticipants systemParticipants;
    private GraphicElements graphics;

    /**
     * Constructor for {@link GridContainerCopyBuilder}.
     *
     * @param gridContainer instance of {@link GridContainerCopyBuilder}
     */
    protected GridContainerCopyBuilder(GridContainer gridContainer) {
      this.gridName = gridContainer.getGridName();
      this.rawGrid = gridContainer.getRawGrid();
      this.systemParticipants = gridContainer.getSystemParticipants();
      this.graphics = gridContainer.getGraphics();
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

    /** Returns {@link GraphicElements} */
    protected GraphicElements getGraphics() {
      return graphics;
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
     * Method to alter the {@link GraphicElements}.
     *
     * @param graphics altered graphics
     * @return this instance of {@link GridContainerCopyBuilder}
     */
    public B graphics(GraphicElements graphics) {
      this.graphics = graphics;
      return thisInstance();
    }

    /** Returns the current instance of builder with the correct subclass type */
    protected abstract B thisInstance();

    @Override
    public abstract GridContainer build() throws InvalidGridException;
  }
}
