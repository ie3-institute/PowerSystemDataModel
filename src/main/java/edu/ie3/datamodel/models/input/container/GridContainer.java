/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

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

  protected abstract static class GridContainerCopyBuilder<T extends GridContainerCopyBuilder<T>>
      extends InputContainerCopyBuilder<InputEntity, GridContainer> {
    private String gridName;
    private RawGridElements rawGrid;
    private SystemParticipants systemParticipants;
    private GraphicElements graphics;

    protected GridContainerCopyBuilder(GridContainer container) {
      super(container);
      this.gridName = container.getGridName();
      this.rawGrid = container.getRawGrid();
      this.systemParticipants = container.getSystemParticipants();
      this.graphics = container.getGraphics();
    }

    protected String getGridName() {
      return gridName;
    }

    protected RawGridElements getRawGrid() {
      return rawGrid;
    }

    protected SystemParticipants getSystemParticipants() {
      return systemParticipants;
    }

    protected GraphicElements getGraphics() {
      return graphics;
    }

    public T gridName(String gridName) {
      this.gridName = gridName;
      return childInstance();
    }

    public T rawGrid(RawGridElements rawGrid) {
      this.rawGrid = rawGrid;
      return childInstance();
    }

    public T systemParticipants(SystemParticipants systemParticipants) {
      this.systemParticipants = systemParticipants;
      return childInstance();
    }

    public T graphics(GraphicElements graphics) {
      this.graphics = graphics;
      return childInstance();
    }

    @Override
    protected abstract T childInstance();

    @Override
    abstract GridContainer build();
  }
}
