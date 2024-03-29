/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.graph;

import edu.ie3.datamodel.models.input.NodeInput;
import java.util.function.Supplier;
import javax.measure.Quantity;
import javax.measure.quantity.ElectricResistance;
import org.jgrapht.graph.SimpleWeightedGraph;
import tech.units.indriya.ComparableQuantity;

/** An impedance weighted graph that uses {@link ImpedanceWeightedEdge}s as edge type. */
public class ImpedanceWeightedGraph extends SimpleWeightedGraph<NodeInput, ImpedanceWeightedEdge> {

  private static final long serialVersionUID = -2797654003980753342L;

  public ImpedanceWeightedGraph() {
    super(ImpedanceWeightedEdge.class);
  }

  public ImpedanceWeightedGraph(
      Supplier<NodeInput> vertexSupplier, Supplier<ImpedanceWeightedEdge> edgeSupplier) {
    super(vertexSupplier, edgeSupplier);
  }

  /**
   * Assigns a {@link Quantity} of type {@link ElectricResistance} to an instance of edge {@link
   * ImpedanceWeightedEdge}
   *
   * @param edge edge whose weight should be altered
   * @param weight the weight of the {@link ImpedanceWeightedEdge}
   */
  public void setEdgeWeightQuantity(
      ImpedanceWeightedEdge edge, ComparableQuantity<ElectricResistance> weight) {
    double weightDouble =
        weight.to(ImpedanceWeightedEdge.DEFAULT_IMPEDANCE_UNIT).getValue().doubleValue();
    super.setEdgeWeight(edge, weightDouble);
  }
}
