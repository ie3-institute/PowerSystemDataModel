/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.graphics;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import edu.ie3.models.input.NodeInput;
import java.util.Objects;
import java.util.UUID;

/** Describes the graphic data belonging to a {@link NodeInput} */
public class NodeGraphicInput extends GraphicInput {
  /** The NodeInput to this graphic data */
  NodeInput node;
  /** The geometric point of this node */
  Point point;

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

  public void setNode(NodeInput node) {
    this.node = node;
  }

  public Point getPoint() {
    return point;
  }

  public void setPoint(Point point) {
    this.point = point;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    NodeGraphicInput that = (NodeGraphicInput) o;
    return Objects.equals(node, that.node) && Objects.equals(point, that.point);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), node, point);
  }
}
