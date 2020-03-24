/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.graph;

import java.util.function.Supplier;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import org.jgrapht.graph.SimpleWeightedGraph;
import tec.uom.se.quantity.Quantities;

/** A distance weighted graph that uses {@link DistanceWeightedEdge}s as edge type. */
public class DistanceWeightedGraph<V> extends SimpleWeightedGraph<V, DistanceWeightedEdge> {

  private static final long serialVersionUID = -2797654003980753341L;

  public DistanceWeightedGraph(Class<? extends DistanceWeightedEdge> edgeClass) {
    super(edgeClass);
  }

  public DistanceWeightedGraph(
      Supplier<V> vertexSupplier, Supplier<DistanceWeightedEdge> edgeSupplier) {
    super(vertexSupplier, edgeSupplier);
  }

  /** Assigns a {@link Quantity} of type {@link Length} to an edge. */
  public void setWeightQuantity(DistanceWeightedEdge edge, Quantity<Length> weight) {
    double weightDouble = weight.to(DistanceWeightedEdge.DEFAULT_UNIT).getValue().doubleValue();
    super.setEdgeWeight(edge, weightDouble);
  }

  @Override
  public void setEdgeWeight(DistanceWeightedEdge edge, double weight) {
    this.setWeightQuantity(edge, Quantities.getQuantity(weight, DistanceWeightedEdge.DEFAULT_UNIT));
  }
}
