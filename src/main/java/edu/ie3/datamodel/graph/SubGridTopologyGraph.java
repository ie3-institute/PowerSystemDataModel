/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.graph;

import edu.ie3.datamodel.models.input.container.SubGridContainer;
import org.jgrapht.graph.AsUnmodifiableGraph;
import org.jgrapht.graph.DirectedMultigraph;

/**
 * A wrapper class to define a graph for representing the structure of galvanically seperated sub
 * grid models.
 */
public class SubGridTopologyGraph extends AsUnmodifiableGraph<SubGridContainer, SubGridGate> {
  /**
   * Creates a new unmodifiable graph based on the specified backing graph.
   *
   * @param g the backing graph on which an unmodifiable graph is to be created.
   */
  public SubGridTopologyGraph(DirectedMultigraph<SubGridContainer, SubGridGate> g) {
    super(g);
  }
}
