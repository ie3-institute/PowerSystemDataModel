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
import edu.ie3.utils.ValidationUtils;

import java.util.*;

/** Represents the aggregation of raw grid elements (nodes, lines, transformers, switches) */
public class AggregatedRawGridInput implements AggregatedEntities {
  /** Set of nodes in this grid */
  private final Set<NodeInput> nodes = new HashSet<>();
  /** Set of lines in this grid */
  private final Set<LineInput> lines = new HashSet<>();
  /** Set of two winding transformers in this grid */
  private final Set<Transformer2WInput> transformer2Ws = new HashSet<>();
  /** Set of three winding in this grid */
  private final Set<Transformer3WInput> transformer3Ws = new HashSet<>();
  /** Set of switches in this grid */
  private final Set<SwitchInput> switches = new HashSet<>();

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
      if (!ValidationUtils.checkNode(node)) return false;
    }
    for (LineInput line : lines) {
      if (!ValidationUtils.checkLine(line)) return false;
    }
    for (Transformer2WInput transformer2W : transformer2Ws) {
      if (!ValidationUtils.checkTransformer2W(transformer2W)) return false;
    }
    for (Transformer3WInput transformer3W : transformer3Ws) {
      if (!ValidationUtils.checkTransformer3W(transformer3W)) return false;
    }
    for (SwitchInput switchInput : switches) {
      if (!ValidationUtils.checkConnector(switchInput)) return false;
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

  /** @return unmodifiable Set of all three winding transformers in this grid */
  public Set<NodeInput> getNodes() {
    return Collections.unmodifiableSet(nodes);
  }

  /** @return unmodifiable Set of all lines in this grid */
  public Set<LineInput> getLines() {
    return Collections.unmodifiableSet(lines);
  }

  /** @return unmodifiable Set of all two winding transformers in this grid */
  public Set<Transformer2WInput> getTransformer2Ws() {
    return Collections.unmodifiableSet(transformer2Ws);
  }

  /** @return unmodifiable Set of all three winding transformers in this grid */
  public Set<Transformer3WInput> getTransformer3Ws() {
    return Collections.unmodifiableSet(transformer3Ws);
  }

  /** @return unmodifiable Set of all switches in this grid */
  public Set<SwitchInput> getSwitches() {
    return Collections.unmodifiableSet(switches);
  }
}
