/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.graphics;

import edu.ie3.datamodel.io.extractor.HasNodes;
import edu.ie3.datamodel.models.input.NodeInput;
import java.util.*;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

/** Describes the graphic data belonging to a {@link NodeInput} */
public class NodeGraphicInput extends GraphicInput implements HasNodes {
  /** The NodeInput to this graphic data */
  private final NodeInput node;
  /** The geometric point of this node */
  private final Point point;

  /**
   * @param uuid of the input entity
   * @param graphicLayer Description of the graphic layer, this graphic is located on
   * @param path A graphic representation as path
   * @param node The NodeInput to this graphic data
   * @param point The geometric point of this node
   */
  public NodeGraphicInput(
      UUID uuid, String graphicLayer, LineString path, NodeInput node, Point point) {
    super(uuid, graphicLayer, path);
    this.node = node;
    this.point = point;
  }

  public NodeInput getNode() {
    return node;
  }

  public Point getPoint() {
    return point;
  }

  public NodeGraphicInputCopyBuilder copy() {
    return new NodeGraphicInputCopyBuilder(this);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    NodeGraphicInput that = (NodeGraphicInput) o;
    return node.equals(that.node) && point.equals(that.point);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), node, point);
  }

  @Override
  public String toString() {
    return "NodeGraphicInput{" + "node=" + node + ", point=" + point + '}';
  }

  @Override
  public List<NodeInput> allNodes() {
    return Collections.singletonList(node);
  }

  /**
   * A builder pattern based approach to create copies of {@link NodeGraphicInput} entities with
   * altered field values. For detailed field descriptions refer to java docs of {@link
   * NodeGraphicInput}
   *
   * @version 0.1
   * @since 05.06.20
   */
  public static class NodeGraphicInputCopyBuilder
      extends GraphicInputCopyBuilder<NodeGraphicInputCopyBuilder> {

    private Point point;
    private NodeInput node;

    private NodeGraphicInputCopyBuilder(NodeGraphicInput entity) {
      super(entity);
      this.node = entity.getNode();
      this.point = entity.getPoint();
    }

    public NodeGraphicInputCopyBuilder point(Point point) {
      this.point = point;
      return this;
    }

    public NodeGraphicInputCopyBuilder node(NodeInput node) {
      this.node = node;
      return this;
    }

    @Override
    protected NodeGraphicInputCopyBuilder childInstance() {
      return this;
    }

    @Override
    public NodeGraphicInput build() {
      return new NodeGraphicInput(getUuid(), getGraphicLayer(), getPath(), node, point);
    }
  }
}
