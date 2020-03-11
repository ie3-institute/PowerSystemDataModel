/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.datamodel.models.input.container;

import edu.ie3.datamodel.models.UniqueEntity;
import edu.ie3.datamodel.models.input.graphics.LineGraphicInput;
import edu.ie3.datamodel.models.input.graphics.NodeGraphicInput;
import java.util.*;

/** Represents the accumulation of graphic data elements (node graphics, line graphics) */
public class GraphicElements implements InputContainer {

  private final Set<NodeGraphicInput> nodeGraphics;
  private final Set<LineGraphicInput> lineGraphics;

  public GraphicElements(Set<NodeGraphicInput> nodeGraphics, Set<LineGraphicInput> lineGraphics) {
    this.nodeGraphics = nodeGraphics;
    this.lineGraphics = lineGraphics;
  }

  @Override
  public List<UniqueEntity> allEntitiesAsList() {
    List<UniqueEntity> allEntities = new LinkedList<>();
    allEntities.addAll(nodeGraphics);
    allEntities.addAll(lineGraphics);
    return Collections.unmodifiableList(allEntities);
  }

  @Override
  public boolean areValuesValid() {
    return true; // no check defined in ValidationTools, so no need for unnecessary instanceofs
  }

  /** @return unmodifiable Set of all node graphic data for this grid */
  public Set<NodeGraphicInput> getNodeGraphics() {
    return Collections.unmodifiableSet(nodeGraphics);
  }

  /** @return unmodifiable Set of all line graphic data for this grid */
  public Set<LineGraphicInput> getLineGraphics() {
    return Collections.unmodifiableSet(lineGraphics);
  }
}
