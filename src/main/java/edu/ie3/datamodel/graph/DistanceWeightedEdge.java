/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.graph;

import static tec.uom.se.unit.Units.METRE;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Length;
import org.jgrapht.graph.DefaultWeightedEdge;
import tec.uom.se.ComparableQuantity;
import tec.uom.se.quantity.Quantities;

/**
 * A default implementation for edges in a {@link DistanceWeightedGraph}. All access to the weight
 * of an edge must go through the graph interface, which is why this class doesn't expose any public
 * methods.
 */
public class DistanceWeightedEdge extends DefaultWeightedEdge {

  private static final long serialVersionUID = -1679382970341818555L;

  protected static final Unit<Length> DEFAULT_UNIT = METRE;

  private double distance;

  public DistanceWeightedEdge(ComparableQuantity<Length> weightQuantity) {
    this.distance = weightQuantity.to(DEFAULT_UNIT).getValue().doubleValue();
  }

  /** Returns the weightQuantity of the edge as {@link Quantity}. */
  protected ComparableQuantity<Length> getWeightQuantity() {
    return Quantities.getQuantity(distance, DEFAULT_UNIT);
  }
}
