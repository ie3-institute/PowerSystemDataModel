/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.graph.SubGridTopologyGraph;
import edu.ie3.datamodel.utils.ContainerUtils;
import java.util.Objects;

/** Model class to hold input models for more than one galvanically separated subnet */
public class JointGridContainer extends GridContainer {
  /** A graph describing the subnet dependencies */
  private final SubGridTopologyGraph subGridTopologyGraph;

  public JointGridContainer(
      String gridName,
      RawGridElements rawGrid,
      SystemParticipants systemParticipants,
      GraphicElements graphics) {
    super(gridName, rawGrid, systemParticipants, graphics);

    /* Build sub grid dependency */
    this.subGridTopologyGraph =
        ContainerUtils.buildSubGridTopologyGraph(
            this.gridName, this.rawGrid, this.systemParticipants, this.graphics);
    checkSubGridDependencyGraph(subGridTopologyGraph);
  }

  public JointGridContainer(
      String gridName,
      RawGridElements rawGrid,
      SystemParticipants systemParticipants,
      GraphicElements graphics,
      SubGridTopologyGraph subGridTopologyGraph) {
    super(gridName, rawGrid, systemParticipants, graphics);
    this.subGridTopologyGraph = subGridTopologyGraph;
    checkSubGridDependencyGraph(this.subGridTopologyGraph);
  }

  /**
   * Checks, if the sub grid dependency graph has only one node.
   *
   * @param subGridTopologyGraph The graph to check
   */
  private void checkSubGridDependencyGraph(SubGridTopologyGraph subGridTopologyGraph) {
    if (subGridTopologyGraph.vertexSet().size() == 1)
      throw new InvalidGridException(
          "This joint grid model only contains one single grid. Consider using SubGridContainer.");
  }

  public SubGridTopologyGraph getSubGridTopologyGraph() {
    return subGridTopologyGraph;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JointGridContainer that = (JointGridContainer) o;
    return subGridTopologyGraph.equals(that.subGridTopologyGraph);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), subGridTopologyGraph);
  }

  @Override
  public String toString() {
    return "JointGridContainer{" + "gridName='" + gridName + '\'' + '}';
  }
}
