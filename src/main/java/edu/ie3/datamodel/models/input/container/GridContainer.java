/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import com.google.common.graph.ImmutableGraph;
import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.utils.ContainerUtils;
import edu.ie3.datamodel.utils.ValidationUtils;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GridContainer implements InputContainer {
  private static Logger logger = LoggerFactory.getLogger(GridContainer.class);

  /** Name of this grid */
  protected final String gridName;
  /** Accumulated raw grid elements (lines, nodes, transformers, switches) */
  protected final RawGridElements rawGrid;
  /** Accumulated system participant elements */
  protected final SystemParticipants systemParticipants;
  /** Accumulated graphic data entities (node graphics, line graphics) */
  protected final GraphicElements graphics;
  /** A graph describing the subnet dependencies */
  private final ImmutableGraph<SubGridContainer> subnetDependencyGraph;

  public GridContainer(
      String gridName,
      RawGridElements rawGrid,
      SystemParticipants systemParticipants,
      GraphicElements graphics) {
    this.gridName = gridName;

    this.rawGrid = rawGrid;
    if (!this.rawGrid.validate())
      throw new InvalidGridException(
          "You provided NULL as raw grid data for "
              + gridName
              + ". It has at least have to have nodes.");

    this.systemParticipants = systemParticipants;
    if (!ValidationUtils.checkSystemParticipants(this.systemParticipants, this.rawGrid.getNodes()))
      logger.warn(
          "You provided NULL as system participants for {}, which doesn't make much sense...",
          gridName);

    this.graphics = graphics;
    if (!this.graphics.validate())
      logger.debug("No graphic information provided for {}.", gridName);

    /* Build sub grid dependency */
    this.subnetDependencyGraph =
        ContainerUtils.buildSubGridTopology(
            this.gridName, this.rawGrid, this.systemParticipants, this.graphics);
  }

  @Override
  public List<UniqueEntity> allEntitiesAsList() {
    List<UniqueEntity> allEntities = new LinkedList<>();
    allEntities.addAll(rawGrid.allEntitiesAsList());
    allEntities.addAll(systemParticipants.allEntitiesAsList());
    allEntities.addAll(graphics.allEntitiesAsList());
    return Collections.unmodifiableList(allEntities);
  }

  /**
   * @return true, as we are positive people and believe in what we do. Just kidding. Checks are
   *     made during initialisation.
   */
  @Override
  public boolean validate() {
    return true;
  }

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

  public ImmutableGraph<SubGridContainer> getSubnetDependencyGraph() {
    return subnetDependencyGraph;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GridContainer that = (GridContainer) o;
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
}
