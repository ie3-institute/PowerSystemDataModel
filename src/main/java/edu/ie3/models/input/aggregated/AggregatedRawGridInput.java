/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.models.input.aggregated;

import edu.ie3.models.UniqueEntity;
import edu.ie3.models.input.NodeInput;
import edu.ie3.models.input.connector.LineInput;
import edu.ie3.models.input.connector.SwitchInput;
import edu.ie3.models.input.connector.Transformer2WInput;
import edu.ie3.models.input.connector.Transformer3WInput;
import edu.ie3.models.validation.ValidationTools;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/** Represents the aggregation of raw grid elements (nodes, lines, transformers, switches) */
public class AggregatedRawGridInput implements AggregatedEntities {
  /** List of nodes in this grid */
  private LinkedList<NodeInput> nodes = new LinkedList<>();
  /** List of lines in this grid */
  private LinkedList<LineInput> lines = new LinkedList<>();
  /** List of two winding transformers in this grid */
  private LinkedList<Transformer2WInput> transformer2Ws = new LinkedList<>();
  /** List of three winding in this grid */
  private LinkedList<Transformer3WInput> transformer3Ws = new LinkedList<>();
  /** List of switches in this grid */
  private LinkedList<SwitchInput> switches = new LinkedList<>();

  @Override
  public void add(UniqueEntity entity) {
    if (entity instanceof NodeInput) add((NodeInput) entity);
    else if (entity instanceof LineInput) add((LineInput) entity);
    else if (entity instanceof Transformer2WInput) add((Transformer2WInput) entity);
    else if (entity instanceof Transformer3WInput) add((Transformer3WInput) entity);
    else if (entity instanceof SwitchInput) add((SwitchInput) entity);
    else
      throw new IllegalArgumentException(
          "Entity type is unknown, cannot add entity [" + entity + "]");
  }

  @Override
  public List<UniqueEntity> allEntitiesAsList() {
    List<UniqueEntity> allEntities = new LinkedList<>();
    allEntities.addAll(nodes);
    allEntities.addAll(lines);
    allEntities.addAll(transformer2Ws);
    allEntities.addAll(transformer3Ws);
    allEntities.addAll(switches);
    return Collections.unmodifiableList(allEntities);
  }

  @Override
  public boolean areValuesValid() {
    for (NodeInput node : nodes) {
      if (!ValidationTools.checkNode(node)) return false;
    }
    for (LineInput line : lines) {
      if (!ValidationTools.checkLine(line)) return false;
    }
    for (Transformer2WInput transformer2W : transformer2Ws) {
      if (!ValidationTools.checkTransformer2W(transformer2W)) return false;
    }
    for (Transformer3WInput transformer3W : transformer3Ws) {
      if (!ValidationTools.checkTransformer3W(transformer3W)) return false;
    }
    for (SwitchInput switchInput : switches) {
      if (!ValidationTools.checkConnector(switchInput)) return false;
    }
    return true;
  }

  public void add(NodeInput entity) {
    nodes.add(entity);
  }

  public void add(LineInput entity) {
    lines.add(entity);
  }

  public void add(Transformer2WInput entity) {
    transformer2Ws.add(entity);
  }

  public void add(Transformer3WInput entity) {
    transformer3Ws.add(entity);
  }

  public void add(SwitchInput entity) {
    switches.add(entity);
  }

  /** @return unmodifiable List of all three winding transformers in this grid */
  public List<NodeInput> getNodes() {
    return Collections.unmodifiableList(nodes);
  }

  /** @return unmodifiable List of all lines in this grid */
  public List<LineInput> getLines() {
    return Collections.unmodifiableList(lines);
  }

  /** @return unmodifiable List of all two winding transformers in this grid */
  public List<Transformer2WInput> getTransformer2Ws() {
    return Collections.unmodifiableList(transformer2Ws);
  }

  /** @return unmodifiable List of all three winding transformers in this grid */
  public List<Transformer3WInput> getTransformer3Ws() {
    return Collections.unmodifiableList(transformer3Ws);
  }

  /** @return unmodifiable List of all switches in this grid */
  public List<SwitchInput> getSwitches() {
    return Collections.unmodifiableList(switches);
  }
}
