/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import com.google.common.graph.ImmutableGraph;
import edu.ie3.datamodel.exceptions.InvalidGridException;
import edu.ie3.datamodel.utils.ContainerUtils;
import java.util.Objects;

/** Model class to hold input models for more than one galvanically separated subnet */
public class JointGridContainer extends GridContainer {
  /** A graph describing the subnet dependencies */
  private final ImmutableGraph<SubGridContainer> subGridTopologyGraph;

  public JointGridContainer(
      String gridName,
      RawGridElements rawGrid,
      SystemParticipants systemParticipants,
      GraphicElements graphics) {
    super(gridName, rawGrid, systemParticipants, graphics);

    /* Build sub grid dependency */
    this.subGridTopologyGraph =
        ContainerUtils.buildSubGridTopology(
            this.gridName, this.rawGrid, this.systemParticipants, this.graphics);
    checkSubGridDependencyGraph(subGridTopologyGraph);
  }

  public JointGridContainer(
      String gridName,
      RawGridElements rawGrid,
      SystemParticipants systemParticipants,
      GraphicElements graphics,
      ImmutableGraph<SubGridContainer> subGridTopologyGraph) {
    super(gridName, rawGrid, systemParticipants, graphics);
    this.subGridTopologyGraph = subGridTopologyGraph;
    checkSubGridDependencyGraph(this.subGridTopologyGraph);
  }

  /**
   * Checks, if the sub grid dependency graph has only one node.
   *
   * @param subnetDependencyGraph The graph to check
   * @return true
   */
  private boolean checkSubGridDependencyGraph(
      ImmutableGraph<SubGridContainer> subnetDependencyGraph) {
    if (subnetDependencyGraph.nodes().size() == 1)
      throw new InvalidGridException(
          "This joint grid model only contains one single grid. Consider using SubGridContainer.");
    return true;
  }

  /**
   * @return true, as we are positive people and believe in what we do. Just kidding. Checks are
   *     made during initialisation.
   */
  @Override
  public boolean validate() {
    return true;
  }

  public ImmutableGraph<SubGridContainer> getSubGridTopologyGraph() {
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
