/*
 * © 2021. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.input.graphics.GraphicInput;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import java.util.*;
import java.util.stream.Collectors;

/** Represents the accumulation of graphic data elements (node graphics, line graphics) */
public class GraphicElements implements InputContainer<GraphicInput> {

  private final Set<NodeGraphicInput> nodeGraphics;
  private final Set<LineGraphicInput> lineGraphics;

  public GraphicElements(Set<NodeGraphicInput> nodeGraphics, Set<LineGraphicInput> lineGraphics) {
    this.nodeGraphics = nodeGraphics;
    this.lineGraphics = lineGraphics;
  }

  /**
   * Combine different already existing containers
   *
   * @param graphicElements Already existing containers
   */
  public GraphicElements(Collection<GraphicElements> graphicElements) {
    this.nodeGraphics =
        graphicElements.stream()
            .flatMap(graphics -> graphics.nodeGraphics.stream())
            .collect(Collectors.toSet());
    this.lineGraphics =
        graphicElements.stream()
            .flatMap(graphics -> graphics.lineGraphics.stream())
            .collect(Collectors.toSet());
  }

  /**
   * Create an instance based on a list of {@link GraphicInput} entities that are included in {@link
   * GraphicElements}
   *
   * @param graphics list of grid elements this container instance should created from
   */
  public GraphicElements(List<GraphicInput> graphics) {

    /* init sets */
    this.nodeGraphics =
        graphics.parallelStream()
            .filter(graphic -> graphic instanceof NodeGraphicInput)
            .map(graphic -> (NodeGraphicInput) graphic)
            .collect(Collectors.toSet());
    this.lineGraphics =
        graphics.parallelStream()
            .filter(graphic -> graphic instanceof LineGraphicInput)
            .map(graphic -> (LineGraphicInput) graphic)
            .collect(Collectors.toSet());
  }

  @Override
  public final List<GraphicInput> allEntitiesAsList() {
    List<GraphicInput> allEntities = new LinkedList<>();
    allEntities.addAll(nodeGraphics);
    allEntities.addAll(lineGraphics);
    return Collections.unmodifiableList(allEntities);
  }

  /** @return unmodifiable Set of all node graphic data for this grid */
  public Set<NodeGraphicInput> getNodeGraphics() {
    return Collections.unmodifiableSet(nodeGraphics);
  }

  /** @return unmodifiable Set of all line graphic data for this grid */
  public Set<LineGraphicInput> getLineGraphics() {
    return Collections.unmodifiableSet(lineGraphics);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    GraphicElements that = (GraphicElements) o;
    return nodeGraphics.equals(that.nodeGraphics) && lineGraphics.equals(that.lineGraphics);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nodeGraphics, lineGraphics);
  }
}
