/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import edu.ie3.datamodel.utils.ValidationUtils;
import java.util.*;
import java.util.stream.Collectors;

/** Represents the accumulation of graphic data elements (node graphics, line graphics) */
public class GraphicElements implements InputContainer {

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

    // sanity check for distinct uuids
    ValidationUtils.checkForDuplicateUuids("GraphicElements", this.allEntitiesAsList());
  }

  @Override
  public List<UniqueEntity> allEntitiesAsList() {
    List<UniqueEntity> allEntities = new LinkedList<>();
    allEntities.addAll(nodeGraphics);
    allEntities.addAll(lineGraphics);
    return Collections.unmodifiableList(allEntities);
  }

  @Override
  public void validate() {
    throw new UnsupportedOperationException(
        "Graphic elements cannot be validated without raw grid elements.");
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
