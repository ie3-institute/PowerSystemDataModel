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
  private final ImmutableGraph<SubGridContainer> subnetDependencyGraph;

  public JointGridContainer(
      String gridName,
      RawGridElements rawGrid,
      SystemParticipants systemParticipants,
      GraphicElements graphics) {
    super(gridName, rawGrid, systemParticipants, graphics);

    /* Build sub grid dependency */
    this.subnetDependencyGraph =
        ContainerUtils.buildSubGridTopology(
            this.gridName, this.rawGrid, this.systemParticipants, this.graphics);

    if (subnetDependencyGraph.nodes().size() == 1)
      throw new InvalidGridException(
          "This joint grid model only contains one single grid. Consider using SubGridContainer.");
  }

  /**
   * @return true, as we are positive people and believe in what we do. Just kidding. Checks are
   *     made during initialisation.
   */
  @Override
  public boolean validate() {
    return true;
  }

  public ImmutableGraph<SubGridContainer> getSubnetDependencyGraph() {
    return subnetDependencyGraph;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JointGridContainer that = (JointGridContainer) o;
    return subnetDependencyGraph.equals(that.subnetDependencyGraph);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), subnetDependencyGraph);
  }

  @Override
  public String toString() {
    return "JointGridContainer{" + "gridName='" + gridName + '\'' + '}';
  }
}
