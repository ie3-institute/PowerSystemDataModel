/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.graph;

import edu.ie3.datamodel.models.input.NodeInput;
import edu.ie3.datamodel.models.input.connector.Transformer2WInput;
import edu.ie3.datamodel.models.input.connector.Transformer3WInput;
import edu.ie3.datamodel.models.input.connector.TransformerInput;
import edu.ie3.datamodel.models.input.container.SubGridContainer;
import java.util.Objects;

/**
 * Defines gates between {@link SubGridContainer}s and serves as edge definition for {@link
 * SubGridTopologyGraph}
 */
public class SubGridGate {
  private final TransformerInput link;
  private final NodeInput superiorNode;
  private final NodeInput inferiorNode;

  /**
   * Creates a sub grid gate from two winding transformer.
   *
   * @param transformer2w Two winding transformer to create gate for
   */
  public SubGridGate(Transformer2WInput transformer2w) {
    this.link = transformer2w;
    this.superiorNode = transformer2w.getNodeA();
    this.inferiorNode = transformer2w.getNodeB();
  }

  /**
   * Creates a sub grid gate from three winding transformer. Define, which of the two superior sub
   * grids should be taken by the inferior port flag.
   *
   * @param transformer3W Three winding transformer to create gate for
   * @param inferiorPort Choose, which 1-to-1-gate should be created
   */
  public SubGridGate(Transformer3WInput transformer3W, ConnectorPort inferiorPort) {
    this.link = transformer3W;
    this.superiorNode = transformer3W.getNodeA();
    switch (inferiorPort) {
      case B:
        this.inferiorNode = transformer3W.getNodeB();
        break;
      case C:
        this.inferiorNode = transformer3W.getNodeC();
        break;
      default:
        throw new IllegalArgumentException(
            "Only port "
                + ConnectorPort.B
                + " or "
                + ConnectorPort.C
                + " can be "
                + "chosen as inferior port.");
    }
  }

  public TransformerInput getLink() {
    return link;
  }

  public NodeInput getSuperiorNode() {
    return superiorNode;
  }

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
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    SubGridGate that = (SubGridGate) o;
    return link.equals(that.link)
        && superiorNode.equals(that.superiorNode)
        && inferiorNode.equals(that.inferiorNode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(link, superiorNode, inferiorNode);
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
