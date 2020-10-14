/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.graph;

import edu.ie3.datamodel.models.input.NodeInput;
import java.util.function.Supplier;
import javax.measure.Quantity;
import javax.measure.quantity.Length;
import org.jgrapht.graph.SimpleWeightedGraph;
import tech.units.indriya.ComparableQuantity;

/** A distance weighted graph that uses {@link DistanceWeightedEdge}s as edge type. */
public class DistanceWeightedGraph extends SimpleWeightedGraph<NodeInput, DistanceWeightedEdge> {

  private static final long serialVersionUID = -2797654003980753341L;

  public DistanceWeightedGraph() {
    super(DistanceWeightedEdge.class);
  }

  public DistanceWeightedGraph(
      Supplier<NodeInput> vertexSupplier, Supplier<DistanceWeightedEdge> edgeSupplier) {
    super(vertexSupplier, edgeSupplier);
  }

  /**
   * Assigns a {@link Quantity} of type {@link Length} to an instance of edge {@link
   * DistanceWeightedEdge}
   *
   * @param edge edge whose weight should be altered
   * @param weight the weight of the {@link DistanceWeightedEdge}
   */
  public void setEdgeWeight(DistanceWeightedEdge edge, ComparableQuantity<Length> weight) {
    double weightDouble =
        weight.to(DistanceWeightedEdge.DEFAULT_DISTANCE_UNIT).getValue().doubleValue();
    super.setEdgeWeight(edge, weightDouble);
  }

  /**
   * The only purpose for overriding this method is to provide a better indication of the unit that
   * is expected to be passed in. It is highly advised to use the {@link
   * #setEdgeWeight(DistanceWeightedEdge, ComparableQuantity)} for safety purposes that the provided
   * edge weight is correct.
   *
   * @param edge the edge whose weight should be altered
   * @param distanceInMeters the weight of the {@link DistanceWeightedEdge} in meters
   */
  @Override
  public final void setEdgeWeight(DistanceWeightedEdge edge, double distanceInMeters) {
    super.setEdgeWeight(edge, distanceInMeters);
  }
}
