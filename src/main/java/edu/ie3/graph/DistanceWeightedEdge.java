/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.graph;

import static tec.uom.se.unit.Units.METRE;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import tec.uom.se.quantity.Quantities;

/**
 * A default implementation for edges in a {@link DistanceWeightedGraph}. All access to the weight
 * of an edge must go through the graph interface, which is why this class doesn't expose any public
 * methods.
 *
 * @author Mahr
 * @since 10.08.2018
 */
public class DistanceWeightedEdge extends DefaultWeightedEdge {

  // TODO: Quantities-package replaceable with internal package?

  private static final long serialVersionUID = -1679382970341818555L;

  public static final Unit<Length> DEFAULT_UNIT = METRE;

  private double weight = SimpleWeightedGraph.DEFAULT_EDGE_WEIGHT;

  public DistanceWeightedEdge() {}

  public DistanceWeightedEdge(Quantity<Length> weightQuantity) {
    this.weight = weightQuantity.to(DEFAULT_UNIT).getValue().doubleValue();
  }

  /**
   * Returns the weightQuantity of the edge as {@link Quantity}
   *
   * @return The weight as a {@link Quantity}
   */
  protected Quantity<Length> getWeightQuantity() {
    return Quantities.getQuantity(weight, DEFAULT_UNIT);
  }
}
