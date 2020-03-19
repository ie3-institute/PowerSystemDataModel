/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.graph;

import edu.ie3.datamodel.models.input.container.SubGridContainer;
import java.util.Objects;
import org.jgrapht.graph.AsUnmodifiableGraph;
import org.jgrapht.graph.SimpleDirectedGraph;

public class SubGridTopologyGraph
    extends AsUnmodifiableGraph<SubGridContainer, SubGridTopologyGraph.SubGridTopolgyEdge> {
  /**
   * Creates a new unmodifiable graph based on the specified backing graph.
   *
   * @param g the backing graph on which an unmodifiable graph is to be created.
   */
  public SubGridTopologyGraph(SimpleDirectedGraph<SubGridContainer, SubGridTopolgyEdge> g) {
    super(g);
  }

  public static class SubGridTopolgyEdge {
    private final int from;
    private final int to;

    public SubGridTopolgyEdge(int from, int to) {
      this.from = from;
      this.to = to;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      SubGridTopolgyEdge that = (SubGridTopolgyEdge) o;
      return from == that.from && to == that.to;
    }

    @Override
    public int hashCode() {
      return Objects.hash(from, to);
    }

    @Override
    public String toString() {
      return "TopologyEdge{" + "from=" + from + ", to=" + to + '}';
    }
  }
}
