/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.graphics;

import edu.ie3.datamodel.io.extractor.Nodes;
import edu.ie3.datamodel.models.input.NodeInput;
import java.util.*;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

/** Describes the graphic data belonging to a {@link NodeInput} */
public class NodeGraphicInput extends GraphicInput implements Nodes {
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

  @Override
  public String toString() {
    return "NodeGraphicInput{" + "node=" + node + ", point=" + point + '}';
  }

  @Override
  public List<NodeInput> getNodes() {
    return Collections.singletonList(node);
  }
}
