/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.graph;

import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.ConnectorPort;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.TransformerInput;
import edu.ie3.datamodel.models.input.container.SubGridContainer;
import java.io.Serializable;

/**
 * Defines gates between {@link SubGridContainer}s and serves as edge definition for {@link
 * SubGridTopologyGraph}
 */
public record SubGridGate(TransformerInput link, NodeInput superiorNode, NodeInput inferiorNode)
    implements Serializable {
  /**
   * Creates a sub grid gate from two winding transformer.
   *
   * @param transformer Two winding transformer to create gate for
   * @return A {@link SubGridGate} with given transformer and its nodes
   */
  public static SubGridGate fromTransformer2W(Transformer2WInput transformer) {
    return new SubGridGate(transformer, transformer.getNodeA(), transformer.getNodeB());
  }

  /**
   * Creates a sub grid gate from three winding transformer. Define, which of the two superior sub
   * grids should be taken by the inferior port flag.
   *
   * @param transformer Three winding transformer to create gate for
   * @param inferiorPort Choose, which 1-to-1-gate should be created
   * @return A {@link SubGridGate} with transformer, its higher voltage node and either the medium
   *     or low voltage node
   */
  public static SubGridGate fromTransformer3W(
      Transformer3WInput transformer, ConnectorPort inferiorPort) {
    return switch (inferiorPort) {
      case B -> new SubGridGate(transformer, transformer.getNodeA(), transformer.getNodeB());
      case C -> new SubGridGate(transformer, transformer.getNodeA(), transformer.getNodeC());
      default -> throw new IllegalArgumentException(
          "Only port "
              + ConnectorPort.B
              + " or "
              + ConnectorPort.C
              + " can be "
              + "chosen as inferior port.");
    };
  }

  /** @deprecated since 3.0. Use {@link #link()} instead */
  @Deprecated(since = "3.0")
  public TransformerInput getLink() {
    return link;
  }

  /** @deprecated since 3.0. Use {@link #superiorNode()} instead */
  @Deprecated(since = "3.0")
  public NodeInput getSuperiorNode() {
    return superiorNode;
  }

  /** @deprecated since 3.0. Use {@link #inferiorNode()} instead */
  @Deprecated(since = "3.0")
  public NodeInput getInferiorNode() {
    return inferiorNode;
  }

  public int getSuperiorSubGrid() {
    return superiorNode.getSubnet();
  }

  public int getInferiorSubGrid() {
    return inferiorNode.getSubnet();
  }

  @Override
  public String toString() {
    return "SubGridTopolgyEdge{"
        + "link="
        + link.getClass().getSimpleName()
        + "("
        + link.getUuid()
        + ")"
        + ", superiorNode="
        + superiorNode.getUuid()
        + " subgrid "
        + superiorNode.getSubnet()
        + ", inferiorNode="
        + inferiorNode.getUuid()
        + " subgrid "
        + inferiorNode.getSubnet()
        + '}';
  }
}
