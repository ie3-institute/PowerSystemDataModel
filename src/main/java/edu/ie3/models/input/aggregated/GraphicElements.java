/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.aggregated;

import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.graphics.LineGraphicInput;
import edu.ie3.models.input.graphics.NodeGraphicInput;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** Represents the aggregation of graphic data elements (node graphics, line graphics) */
public class GraphicElements implements AggregatedEntities {

  private final LinkedList<NodeGraphicInput> nodeGraphics = new LinkedList<>();
  private final LinkedList<LineGraphicInput> lineGraphics = new LinkedList<>();

  @Override
  public void add(UniqueEntity entity) {
    if (entity instanceof NodeGraphicInput) add((NodeGraphicInput) entity);
    else if (entity instanceof LineGraphicInput) add((LineGraphicInput) entity);
    else
      throw new IllegalArgumentException(
          "Entity type is unknown, cannot add entity [" + entity + "]");
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

  public void add(NodeGraphicInput entity) {
    nodeGraphics.add(entity);
  }

  public void add(LineGraphicInput entity) {
    lineGraphics.add(entity);
  }

  /** @return unmodifiable List of all node graphic data for this grid */
  public List<NodeGraphicInput> getNodeGraphics() {
    return Collections.unmodifiableList(nodeGraphics);
  }

  /** @return unmodifiable List of all line graphic data for this grid */
  public List<LineGraphicInput> getLineGraphics() {
    return Collections.unmodifiableList(lineGraphics);
  }

}
